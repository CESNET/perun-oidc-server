package cz.metacentrum.perun.oidc.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.activation.UnsupportedDataTypeException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class UsersManager extends Manager {

    private static UsersManager manager;

    private UsersManager() {
    }

    public static synchronized UsersManager getInstance() {
        if (manager == null) {
            manager = new UsersManager();
        }
        return manager;
    }

    public PerunUser getUserById(Integer id) {

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(get("getUserById", params), PerunUser.class);
        } catch (IOException e) {
            throw new IllegalStateException("IO Error while getting user from perun", e);
        }

    }

    public PerunUser getUserByExtSourceNameAndExtLogin(String extSourceName, String extLogin) {

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("extSourceName", extSourceName);
            params.put("extLogin", extLogin);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(get("getUserByExtSourceNameAndExtLogin", params), PerunUser.class);
        } catch (UnsupportedDataTypeException e) {
            return null;
        } catch (IOException e) {
            throw new IllegalStateException("IO Error while getting user from perun", e);
        }

    }

    public List<PerunResource> getAllowedResourcesIds(int userId) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("user", userId);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(get("getAllowedResources", params), new TypeReference<List<PerunResource>>(){});
        } catch (UnsupportedDataTypeException e) {
            return null;
        } catch (IOException e) {
            throw new IllegalStateException("IO Error while calling getAllowedResourcesIds", e);
        }
    }

    @Override
    protected String getManagerName() {
        return "usersManager";
    }
}
