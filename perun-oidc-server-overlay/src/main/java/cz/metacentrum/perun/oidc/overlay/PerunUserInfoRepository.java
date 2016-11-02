package cz.metacentrum.perun.oidc.overlay;

import cz.metacentrum.perun.oidc.client.OidcManager;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.repository.UserInfoRepository;

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
