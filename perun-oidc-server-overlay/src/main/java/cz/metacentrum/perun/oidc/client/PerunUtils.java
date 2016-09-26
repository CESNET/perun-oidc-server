package cz.metacentrum.perun.oidc.client;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class PerunUtils {

	public static final String EXTSOURCE_IDP = "cz.metacentrum.perun.core.impl.ExtSourceIdp";
	public static final String EXTSOURCE_X509 = "cz.metacentrum.perun.core.impl.ExtSourceX509";
	public static final String EXTSOURCE_NAME_LOCAL = "LOCAL";

	private static final String PROPERTIES_FILE = "/etc/perun/perun-oidc-server.properties";

	/**
	 * Gets particular property from perun.properties file.
	 *
	 * @param propertyName name of the property
	 * @return value of the property
	 */
	public static String getProperty(String propertyName) {
		if (propertyName == null) {
			throw new IllegalArgumentException("Requested property name is null");
		}

		// Load properties file with configuration
		Properties properties = new Properties();
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(PROPERTIES_FILE))) {
			properties.load(bis);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Cannot find "+PROPERTIES_FILE+" file", e);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read "+PROPERTIES_FILE+" file", e);
		}

		String property = properties.getProperty(propertyName);
		if (property == null) {
			throw new RuntimeException("Property " + propertyName + " cannot be found in the configuration file");
		}
		return property;
	}


	public static PerunPrincipal parsePrincipal(HttpServletRequest req) {

		String extSourceLoaString = null;
		String extLogin = null;
		String extSourceName = null;
		String extSourceType = null;
		int extSourceLoa = 0;
		Map<String, String> additionalInformations = new HashMap<String, String>();

		// If we have header Shib-Identity-Provider, then the user uses identity federation to authenticate
		if (req.getHeader("Shib-Identity-Provider") != null && !req.getHeader("Shib-Identity-Provider").isEmpty()) {
			extSourceName = (String) req.getHeader("Shib-Identity-Provider");
			extSourceType = EXTSOURCE_IDP;
			if (req.getHeader("loa") != null && ! req.getHeader("loa").isEmpty()) {
				extSourceLoaString = req.getHeader("loa");
			} else {
				extSourceLoaString = "2";
			}
			// FIXME: find better place where do the operation with attributes from federation
			if (req.getHeader("eppn") != null && ! req.getHeader("eppn").isEmpty()) {
				try {
					String eppn = new String(req.getHeader("eppn").getBytes("ISO-8859-1"));

					// Remove scope from the eppn attribute
					additionalInformations.put("eppnwoscope", eppn.replaceAll("(.*)@.*", "$1"));
				} catch (UnsupportedEncodingException e) {
					//log.error("Cannot encode header eppn with value from ISO-8859-1.");
				}
			}
			if (req.getRemoteUser() != null && !req.getRemoteUser().isEmpty()) {
				extLogin = req.getRemoteUser();
			}
		}

		// EXT_SOURCE was defined in Apache configuration (e.g. Kerberos or Local)
		else if (req.getAttribute("EXTSOURCE") != null) {
			extSourceName = (String) req.getAttribute("EXTSOURCE");
			extSourceType = (String) req.getAttribute("EXTSOURCETYPE");
			extSourceLoaString = (String) req.getAttribute("EXTSOURCELOA");

			if (req.getRemoteUser() != null && !req.getRemoteUser().isEmpty()) {
				extLogin = req.getRemoteUser();
			} else if (req.getAttribute("ENV_REMOTE_USER") != null && !((String) req.getAttribute("ENV_REMOTE_USER")).isEmpty()) {
				extLogin = (String) req.getAttribute("ENV_REMOTE_USER");
			} else if (extSourceName.equals(EXTSOURCE_NAME_LOCAL)) {
				/** LOCAL EXTSOURCE **/
				// If ExtSource is LOCAL then generate REMOTE_USER name on the fly
				extLogin = Long.toString(System.currentTimeMillis());
			}
		}

		// X509 cert was used
		// Cert must be last since Apache asks for certificate everytime and fills cert properties even when Kerberos is in place.
		else if (extLogin == null && req.getAttribute("SSL_CLIENT_VERIFY") != null && ((String) req.getAttribute("SSL_CLIENT_VERIFY")).equals("SUCCESS")){
			extSourceName = (String) req.getAttribute("SSL_CLIENT_I_DN");
			extSourceType = EXTSOURCE_X509;
			extSourceLoaString = (String) req.getAttribute("EXTSOURCELOA");
			extLogin = (String) req.getAttribute("SSL_CLIENT_S_DN");

			// Store X509
			additionalInformations.put("SSL_CLIENT_S_DN", (String) req.getAttribute("SSL_CLIENT_S_DN"));
			additionalInformations.put("dn", (String) req.getAttribute("SSL_CLIENT_S_DN"));

			// Get the X.509 certificate object
			X509Certificate[] certs = (X509Certificate[]) req.getAttribute("javax.servlet.request.X509Certificate");

			// Get the emails
			if (certs != null && certs.length > 0 && certs[0] != null) {
				String emails = "";

				Collection<List<?>> altNames;
				try {
					altNames = certs[0].getSubjectAlternativeNames();
					if (altNames != null) {
						for (List<?> entry: altNames) {
							if (((Integer) entry.get(0)) == 1) {
								emails = (String) entry.get(1);
							}
						}
					}
				} catch (CertificateParsingException e) {
					//log.error("Error during parsing certificate {}", certs);
				}

				additionalInformations.put("mail", emails);

				// Get organization from the certificate
				String oRegExpPattern = "(o|O)(\\s)*=([^+,])*";
				Pattern oPattern = Pattern.compile(oRegExpPattern);
				Matcher oMatcher = oPattern.matcher(certs[0].getSubjectX500Principal().getName());
				if (oMatcher.find()) {
					String[] org = oMatcher.group().split("=");
					if (org[1] != null && !org[1].isEmpty()) {
						additionalInformations.put("o", org[1]);
					}
				}
			}
		}

		// Read all headers and store them in additionalInformation
		String headerName = "";
		for(Enumeration<String> headerNames = req.getHeaderNames(); headerNames.hasMoreElements();){
			headerName = (String)headerNames.nextElement();
			// Tomcat expects all headers are in ISO-8859-1
			try {
				additionalInformations.put(headerName, new String(req.getHeader(headerName).getBytes("ISO-8859-1")));
			} catch (UnsupportedEncodingException e) {
				//log.error("Cannot encode header {} with value from ISO-8859-1.", headerName, req.getHeader(headerName));
			}
		}

		// extSourceLoa must be number, if any specified then set to 0
		if (extSourceLoaString == null || extSourceLoaString.isEmpty()) {
			extSourceLoa = 0;
		} else {
			try {
				extSourceLoa = Integer.parseInt(extSourceLoaString);
			} catch (NumberFormatException ex) {
				extSourceLoa = 0;
			}
		}

		if (extLogin == null || extSourceName == null) {
			throw new IllegalStateException("ExtSource name or userExtSourceLogin is null. " +
					"extSourceName: "+extSourceName+", " +
					"extLogin: "+extLogin+", " +
					"extSourceLoa: "+extSourceLoa+", " +
					"extSourceType: "+extSourceType);
		}


		return new PerunPrincipal(extSourceName, extLogin, extSourceLoa, extSourceType);

	}



	public static boolean isWrapperType(Class<?> clazz)
	{
		Set<Class<?>> ret = new HashSet<Class<?>>();
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
