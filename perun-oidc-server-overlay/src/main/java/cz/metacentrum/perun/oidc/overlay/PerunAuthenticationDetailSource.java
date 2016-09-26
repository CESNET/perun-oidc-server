package cz.metacentrum.perun.oidc.overlay;

import cz.metacentrum.perun.oidc.client.PerunPrincipal;
import cz.metacentrum.perun.oidc.client.PerunUtils;
import org.springframework.security.authentication.AuthenticationDetailsSource;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class PerunAuthenticationDetailSource implements AuthenticationDetailsSource<HttpServletRequest, PerunWebAuthenticationDetails> {

	@Override
	public PerunWebAuthenticationDetails buildDetails(HttpServletRequest context) {

		PerunPrincipal pp = PerunUtils.parsePrincipal(context);

		Map<String, Serializable> additionalInfo = new HashMap<>();
		additionalInfo.put("extSourceName", pp.getExtSourceName());
		additionalInfo.put("userExtSourceLogin", pp.getUserExtSourceLogin());
		additionalInfo.put("extSourceType", pp.getExtSourceType());
		additionalInfo.put("extSourceLoa", String.valueOf(pp.getExtSourceLoa()));

		PerunWebAuthenticationDetails details = new PerunWebAuthenticationDetails(context, additionalInfo);

		return details;
	}

}
