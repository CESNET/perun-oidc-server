package cz.metacentrum.perun.oidc.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@Service
public abstract class Manager {

    @Resource(name="properties")
    private Properties properties;

    protected abstract String getManagerName();

    protected String get(String method) throws IOException {
        return get(method, new HashMap<String, Object>());
    }

    protected String get(String method, Map<String, Object> params) throws IOException {

        URL resourceURL = new URL(PerunUtils.getProperty("oidc.perun.url")+getManagerName()+"/"+method+"?"+queryString(params));

        HttpURLConnection conn = (HttpURLConnection) resourceURL.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", getAuthorizationHeader());
        conn.setRequestProperty("Accept", "application/json");

        return getString(conn);

    }



    protected String post(String method, Map<String, Object> params) throws IOException {

        URL resourceURL = new URL(PerunUtils.getProperty("oidc.perun.url")+getManagerName()+"/"+method);

        HttpURLConnection conn = (HttpURLConnection) resourceURL.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", getAuthorizationHeader());
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");

        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(jsonString(params));
        wr.flush();

        return getString(conn);
    }


    private String getAuthorizationHeader() {
        String toEncode = PerunUtils.getProperty("oidc.perun.username") + ":" + PerunUtils.getProperty("oidc.perun.password");

        return "Basic " + new String(Base64.encodeBase64(toEncode.getBytes()));
    }


    private String getString(HttpURLConnection c) throws IOException {
        try {
            c.connect();

            int httpCode = c.getResponseCode();
            if (200 <= httpCode && httpCode < 300) {

                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                return sb.toString();

            } else {

                BufferedReader br = new BufferedReader(new InputStreamReader(c.getErrorStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                String response = sb.toString();

                throw new IOException("UnsupportedDataTypeException: Fail while reading error response from Perun. Response data: " + response);
            }

        } finally {
            if (c != null) {
                c.disconnect();
            }
        }
    }


    private String jsonString(Map<String, Object> params) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(params);

    }

    private String queryString(Map<String, Object> params) throws UnsupportedEncodingException, JsonProcessingException {
        if (params == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, Object> e : params.entrySet()){
            if(sb.length() > 0){
                sb.append('&');
            }
            String key = URLEncoder.encode(e.getKey(), "UTF-8");
            String value;
            if (e.getValue() instanceof String) {
                value = URLEncoder.encode(e.getValue().toString(), "UTF-8");
            } else if (PerunUtils.isWrapperType(e.getValue().getClass())) {
                value = URLEncoder.encode(e.getValue().toString(), "UTF-8");
            } else {
                ObjectMapper mapper = new ObjectMapper();
                value = URLEncoder.encode(mapper.writeValueAsString(e.getValue()));
            }

            sb.append(key).append('=').append(value);
        }
        return sb.toString();
    }

}
