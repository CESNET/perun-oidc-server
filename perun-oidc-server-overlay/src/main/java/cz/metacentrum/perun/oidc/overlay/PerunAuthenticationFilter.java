package cz.metacentrum.perun.oidc.overlay;

import cz.metacentrum.perun.oidc.client.FacilitiesManager;
import cz.metacentrum.perun.oidc.client.PerunFacility;
import cz.metacentrum.perun.oidc.client.PerunPrincipal;
import cz.metacentrum.perun.oidc.client.PerunResource;
import cz.metacentrum.perun.oidc.client.PerunUser;
import cz.metacentrum.perun.oidc.client.PerunUtils;
import cz.metacentrum.perun.oidc.client.UsersManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 * @author Jiri Mauritz <jirmauritz at gmail.com>
 */
public class PerunAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    private final String OIDCClientAttributeName = "urn:perun:facility:attribute-def:def:OIDCClientID";

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest httpServletRequest) {
        PerunPrincipal pp = PerunUtils.parsePrincipal(httpServletRequest);

        // get user from request
        PerunUser user = UsersManager.getInstance().getUserByExtSourceNameAndExtLogin(pp.getExtSourceName(), pp.getUserExtSourceLogin());

        // get OIDCClientID value
        String OIDCClientID = PerunUtils.getClientId(httpServletRequest);

        // get priviledged resources
        List<PerunFacility> facilities = FacilitiesManager.getInstance().getFacilitiesByAttribute(OIDCClientAttributeName, OIDCClientID);
        Set<PerunResource> allowedResources = new HashSet<>();
        for (PerunFacility facility : facilities) {
            allowedResources.addAll(FacilitiesManager.getInstance().getAssignedResources(facility.getId()));
        }

        // get user resources
        Set<PerunResource> userResources = new HashSet<>(UsersManager.getInstance().getAllowedResourcesIds(user.getId()));

        // retain only allowed resources
        userResources.retainAll(allowedResources);

        // if there is no allowed user resource, the user is not allowed
        if (userResources.isEmpty()) {
            return null;
        }


        if (user == null) {
            return null;
        } else {
            return user.getId();
        }
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest httpServletRequest) {
        return "N/A";
    }

}
