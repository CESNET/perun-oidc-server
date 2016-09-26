package cz.metacentrum.perun.oidc.overlay;

import cz.metacentrum.perun.oidc.client.PerunPrincipal;
import cz.metacentrum.perun.oidc.client.PerunUser;
import cz.metacentrum.perun.oidc.client.PerunUtils;
import cz.metacentrum.perun.oidc.client.UsersManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class PerunAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest httpServletRequest) {
		PerunPrincipal pp = PerunUtils.parsePrincipal(httpServletRequest);

		PerunUser user = UsersManager.getInstance().getUserByExtSourceNameAndExtLogin(pp.getExtSourceName(), pp.getUserExtSourceLogin());

		return user.getId();
	}

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest httpServletRequest) {
		return "N/A";
	}

}
