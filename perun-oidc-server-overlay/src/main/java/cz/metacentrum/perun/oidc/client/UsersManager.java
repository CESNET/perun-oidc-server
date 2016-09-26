package cz.metacentrum.perun.oidc.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
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
        } catch (IOException e) {
            throw new IllegalStateException("IO Error while getting user from perun", e);
        }

    }

    @Override
    protected String getManagerName() {
        return "usersManager";
    }
}
