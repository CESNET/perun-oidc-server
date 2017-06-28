package cz.metacentrum.perun.oidc.client;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class PerunUtils {

	private static final String EXTSOURCE_IDP = "cz.metacentrum.perun.core.impl.ExtSourceIdp";
	private static final String EXTSOURCE_X509 = "cz.metacentrum.perun.core.impl.ExtSourceX509";
	private static final String EXTSOURCE_NAME_LOCAL = "LOCAL";

	private static final String PROPERTIES_FILE = "/etc/perun/perun-oidc-server.properties";
	private static final String SCOPES_FILE = "/etc/perun/perun-oidc-scopes.properties";
	private static final String SHIB_IDENTITY_PROVIDER = "Shib-Identity-Provider";
	private static final String SSL_CLIENT_VERIFY = "SSL_CLIENT_VERIFY";
	private static final String SUCCESS = "SUCCESS";
	private static final String EXTSOURCE = "EXTSOURCE";
	private static final String EXTSOURCETYPE = "EXTSOURCETYPE";
	private static final String EXTSOURCELOA = "EXTSOURCELOA";
	private static final String ENV_REMOTE_USER = "ENV_REMOTE_USER";
	private static final String SSL_CLIENT_I_DN = "SSL_CLIENT_I_DN";
	private static final String SSL_CLIENT_S_DN = "SSL_CLIENT_S_DN";

	/**
	 * Gets particular property from oidc-properties.properties file.
	 *
	 * @param propertyName name of the property
	 * @param required     if requested property is required. If it is and it is not defined, exception is thrown.
	 *                     if it is false, null is returned.
	 * @return value of the property, null if it is not defined
	 */
	public static String getProperty(String propertyName, boolean required) {
		if (propertyName == null) {
			throw new IllegalArgumentException("Requested property name is null");
		}

		// Load properties file with configuration
		Properties properties = new Properties();
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(PROPERTIES_FILE))) {
			properties.load(bis);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Cannot find " + PROPERTIES_FILE + " file", e);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read " + PROPERTIES_FILE + " file", e);
		}

		String property = properties.getProperty(propertyName);
		if (property == null && required) {
			throw new RuntimeException("Property " + propertyName + " cannot be found in the configuration file");
		}
		return property;
	}

	public static Set<String> getScopes() {
		// Load scopes file
		Properties properties = new Properties();
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(SCOPES_FILE))) {
			properties.load(bis);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Cannot find " + SCOPES_FILE + " file", e);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read " + SCOPES_FILE + " file", e);
		}

		Set<String> scopes = new HashSet<>();
		for (Object scope : properties.keySet()) {
			scopes.add(scope.toString());
		}
		return scopes;
	}

	public static Set<String> getClaims() {
		// Load scopes file
		Properties properties = new Properties();
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(SCOPES_FILE))) {
			properties.load(bis);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Cannot find " + SCOPES_FILE + " file", e);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read " + SCOPES_FILE + " file", e);
		}

		ObjectMapper mapper = new ObjectMapper();
		Set<String> claimsList = new HashSet<>();
		for (Object value : properties.values()) {
			try {
				ObjectNode claims = (ObjectNode) mapper.readTree(value.toString());
				Iterator<String> iter = claims.getFieldNames();
				while (iter.hasNext()) {
					claimsList.add(iter.next());
				}
			} catch (IOException e) {
				throw new IllegalArgumentException("Config file " + SCOPES_FILE + " is wrongly configured. Values have to be valid JSON objects.", e);
			}
		}
		return claimsList;
	}

	public static String getProperty(String propertyName) {
		return getProperty(propertyName, true);
	}

	private static String getStringAttribute(HttpServletRequest req, String attributeName) {
		return (String) req.getAttribute(attributeName);
	}

	public static PerunPrincipal parsePrincipal(HttpServletRequest req) {

		String extLogin = null;
		String extSourceName = null;
		String extSourceLoaString = null;
		String extSourceType = null;
		int extSourceLoa;

		// If we have header Shib-Identity-Provider, then the user uses identity federation to authenticate
		String shibIdentityProvider = getStringAttribute(req,SHIB_IDENTITY_PROVIDER);
		String remoteUser = req.getRemoteUser();

		if (isNotEmpty(shibIdentityProvider)) {
			extSourceName =  shibIdentityProvider;
			extSourceType = EXTSOURCE_IDP;
			extSourceLoaString = getStringAttribute(req, "loa");
			if(isEmpty(extSourceLoaString)) extSourceLoaString = "2";
			if (isNotEmpty(remoteUser)) {
				extLogin = remoteUser;
			}
		}

		// EXT_SOURCE was defined in Apache configuration (e.g. Kerberos or Local)
		else if (req.getAttribute(EXTSOURCE) != null) {
			extSourceName = getStringAttribute(req, EXTSOURCE);
			extSourceType = getStringAttribute(req, EXTSOURCETYPE);
			extSourceLoaString = getStringAttribute(req, EXTSOURCELOA);
			if (isNotEmpty(remoteUser)) {
				extLogin = remoteUser;
			} else {
				String env_remote_user = getStringAttribute(req, ENV_REMOTE_USER);
				if (isNotEmpty(env_remote_user)) {
					extLogin = env_remote_user;
				} else if (extSourceName.equals(EXTSOURCE_NAME_LOCAL)) {
					/* LOCAL EXTSOURCE */
					// If ExtSource is LOCAL then generate REMOTE_USER name on the fly
					extLogin = Long.toString(System.currentTimeMillis());
				}
			}
		}

		// X509 cert was used
		// Cert must be last since Apache asks for certificate everytime and fills cert properties even when Kerberos is in place.
		else if (Objects.equals(getStringAttribute(req,SSL_CLIENT_VERIFY), SUCCESS)) {
			extSourceName = getStringAttribute(req, SSL_CLIENT_I_DN);
			extLogin = getStringAttribute(req, SSL_CLIENT_S_DN);
			extSourceType = EXTSOURCE_X509;
			extSourceLoaString = getStringAttribute(req, EXTSOURCELOA);
		}

		if (extLogin == null || extSourceName == null) {
			throw new IllegalStateException("ExtSource name or userExtSourceLogin is null. " +
					"extSourceName: " + extSourceName + ", " +
					"extLogin: " + extLogin + ", "
					);
		}

		// extSourceLoa must be number, if any specified then set to 0
		if (isEmpty(extSourceLoaString)) {
			extSourceLoa = 0;
		} else {
			try {
				extSourceLoa = Integer.parseInt(extSourceLoaString);
			} catch (NumberFormatException ex) {
				extSourceLoa = 0;
			}
		}

		return new PerunPrincipal(extSourceName, extLogin, extSourceLoa, extSourceType);

	}


	static boolean isWrapperType(Class<?> clazz) {
		Set<Class<?>> ret = new HashSet<>();
		ret.add(Boolean.class);
		ret.add(Character.class);
		ret.add(Byte.class);
		ret.add(Short.class);
		ret.add(Integer.class);
		ret.add(Long.class);
		ret.add(Float.class);
		ret.add(Double.class);
		ret.add(Void.class);
		return ret.contains(clazz);
	}
}
