package cz.metacentrum.perun.oidc.client;

import com.google.gson.JsonParser;
import org.mitre.openid.connect.model.DefaultUserInfo;
import org.mitre.openid.connect.model.UserInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class OidcManager extends Manager {

    private static OidcManager manager;

    private OidcManager() {
    }

    public static synchronized OidcManager getInstance() {
        if (manager == null) {
            manager = new OidcManager();
        }
        return manager;
    }

    public UserInfo getSpecificUserinfo(Integer userId) {

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("user", userId);
            JsonParser parser = new JsonParser();
            return DefaultUserInfo.fromJson(parser.parse(get("getSpecificUserinfo", params)).getAsJsonObject());
        } catch (IOException e) {
            throw new IllegalStateException("IO Error while getting userinfo from perun for user id " + userId, e);
        }

    }

    @Override
    protected String getManagerName() {
        return "oidcManager";
    }
}
