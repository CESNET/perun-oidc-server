package cz.metacentrum.perun.oidc.overlay;

import com.google.gson.JsonObject;
import cz.metacentrum.perun.oidc.client.OidcManager;
import cz.metacentrum.perun.oidc.client.PerunUser;
import cz.metacentrum.perun.oidc.client.UsersManager;
import org.mitre.openid.connect.model.DefaultUserInfo;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.repository.UserInfoRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Map;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class PerunUserInfoRepository implements UserInfoRepository {

	@Override
	public UserInfo getByUsername(String s) {

		if (!s.matches("^-?\\d+$")) {
			// bug fix. Sometimes Mitre calls this method with client id. Return null if string is not integer.
			return null;
		}

		// TODO can be cached (performance)
		UserInfo ui = OidcManager.getInstance().getSpecificUserinfo(Integer.valueOf(s));

		return ui;
	}

	@Override
	public UserInfo getByEmailAddress(String s) {
		throw new UnsupportedOperationException("Cannot search user by email");
	}

}
