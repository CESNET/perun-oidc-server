package cz.metacentrum.perun.oidc.overlay;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JWSAlgorithm;
import cz.metacentrum.perun.oidc.client.PerunUtils;
import org.mitre.discovery.util.WebfingerURLNormalizer;
import org.mitre.jwt.encryption.service.JWTEncryptionAndDecryptionService;
import org.mitre.jwt.signer.service.JWTSigningAndValidationService;
import org.mitre.oauth2.web.IntrospectionEndpoint;
import org.mitre.oauth2.web.RevocationEndpoint;
import org.mitre.openid.connect.config.ConfigurationPropertiesBean;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.service.UserInfoService;
import org.mitre.openid.connect.view.HttpCodeView;
import org.mitre.openid.connect.view.JsonEntityView;
import org.mitre.openid.connect.web.DynamicClientRegistrationEndpoint;
import org.mitre.openid.connect.web.JWKSetPublishingEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Handle OpenID Connect Discovery.
 * Overrides the default discovery endpoint from OpenID Connect Java Spring Server.
 *
 * @author Jiri Mauritz
 */
@Controller
public class DiscoveryEndpoint {

	public static final String WELL_KNOWN_URL = ".well-known";
	public static final String OPENID_CONFIGURATION_URL = WELL_KNOWN_URL + "/openid-configuration";
	public static final String WEBFINGER_URL = WELL_KNOWN_URL + "/webfinger";

	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(DiscoveryEndpoint.class);

	@Autowired
	private PerunConfigurationBean perunConfig;

	@Autowired
	private ConfigurationPropertiesBean config;

	@Autowired
	private JWTSigningAndValidationService signService;

	@Autowired
	private JWTEncryptionAndDecryptionService encService;

	@Autowired
	private UserInfoService userService;


	// used to map JWA algorithms objects to strings
	private Function<Algorithm, String> toAlgorithmName = new Function<Algorithm, String>() {
		@Override
		public String apply(Algorithm alg) {
			if (alg == null) {
				return null;
			} else {
				return alg.getName();
			}
		}
	};

	@RequestMapping(value = {"/" + WEBFINGER_URL}, produces = MediaType.APPLICATION_JSON_VALUE)
	public String webfinger(@RequestParam("resource") String resource, @RequestParam(value = "rel", required = false) String rel, Model model) {

		if (!Strings.isNullOrEmpty(rel) && !rel.equals("http://openid.net/specs/connect/1.0/issuer")) {
			logger.warn("Responding to webfinger request for non-OIDC relation: " + rel);
		}

		if (!resource.equals(config.getIssuer())) {
			// it's not the issuer directly, need to check other methods

			UriComponents resourceUri = WebfingerURLNormalizer.normalizeResource(resource);
			if (resourceUri != null
					&& resourceUri.getScheme() != null
					&& resourceUri.getScheme().equals("acct")) {
				// acct: URI (email address format)

				// check on email addresses first
				UserInfo user = userService.getByEmailAddress(resourceUri.getUserInfo() + "@" + resourceUri.getHost());

				if (user == null) {
					// user wasn't found, see if the local part of the username matches, plus our issuer host

					user = userService.getByUsername(resourceUri.getUserInfo()); // first part is the username

					if (user != null) {
						// username matched, check the host component
						UriComponents issuerComponents = UriComponentsBuilder.fromHttpUrl(config.getIssuer()).build();
						if (!Strings.nullToEmpty(issuerComponents.getHost())
								.equals(Strings.nullToEmpty(resourceUri.getHost()))) {
							logger.info("Host mismatch, expected " + issuerComponents.getHost() + " got " + resourceUri.getHost());
							model.addAttribute(HttpCodeView.CODE, HttpStatus.NOT_FOUND);
							return HttpCodeView.VIEWNAME;
						}

					} else {

						// if the user's still null, punt and say we didn't find them

						logger.info("User not found: " + resource);
						model.addAttribute(HttpCodeView.CODE, HttpStatus.NOT_FOUND);
						return HttpCodeView.VIEWNAME;
					}

				}

			} else {
				logger.info("Unknown URI format: " + resource);
				model.addAttribute(HttpCodeView.CODE, HttpStatus.NOT_FOUND);
				return HttpCodeView.VIEWNAME;
			}
		}

		// if we got here, then we're good, return ourselves
		model.addAttribute("resource", resource);
		model.addAttribute("issuer", config.getIssuer());

		return "webfingerView";
	}

	@RequestMapping("/" + OPENID_CONFIGURATION_URL)
	public String providerConfiguration(Model model) {
		String baseUrl = config.getIssuer();

		if (!baseUrl.endsWith("/")) {
			logger.warn("Configured issuer doesn't end in /, adding for discovery: " + baseUrl);
			baseUrl = baseUrl.concat("/");
		}

		signService.getAllSigningAlgsSupported();
		Lists.newArrayList(JWSAlgorithm.HS256, JWSAlgorithm.HS384, JWSAlgorithm.HS512);
		Collection<JWSAlgorithm> clientSymmetricAndAsymmetricSigningAlgs = Lists.newArrayList(JWSAlgorithm.HS256, JWSAlgorithm.HS384, JWSAlgorithm.HS512,
				JWSAlgorithm.RS256, JWSAlgorithm.RS384, JWSAlgorithm.RS512,
				JWSAlgorithm.ES256, JWSAlgorithm.ES384, JWSAlgorithm.ES512,
				JWSAlgorithm.PS256, JWSAlgorithm.PS384, JWSAlgorithm.PS512);
		Collection<Algorithm> clientSymmetricAndAsymmetricSigningAlgsWithNone = Lists.newArrayList(JWSAlgorithm.HS256, JWSAlgorithm.HS384, JWSAlgorithm.HS512,
				JWSAlgorithm.RS256, JWSAlgorithm.RS384, JWSAlgorithm.RS512,
				JWSAlgorithm.ES256, JWSAlgorithm.ES384, JWSAlgorithm.ES512,
				JWSAlgorithm.PS256, JWSAlgorithm.PS384, JWSAlgorithm.PS512,
				Algorithm.NONE);
		ArrayList<String> grantTypes = Lists.newArrayList("authorization_code", "implicit", "urn:ietf:params:oauth:grant-type:jwt-bearer", "client_credentials", "urn:ietf:params:oauth:grant_type:redelegate", "urn:ietf:params:oauth:grant-type:device_code");

		Map<String, Object> m = new HashMap<>();
		m.put("issuer", config.getIssuer());
		m.put("authorization_endpoint", baseUrl + "authorize");
		m.put("token_endpoint", baseUrl + "token");
		m.put("userinfo_endpoint", PerunUtils.getProperty("oidc.userinfo.endpoint"));
		//check_session_iframe
		//end_session_endpoint
		m.put("jwks_uri", baseUrl + JWKSetPublishingEndpoint.URL);
		m.put("registration_endpoint", baseUrl + DynamicClientRegistrationEndpoint.URL);
		m.put("scopes_supported", perunConfig.getScopes()); // these are the scopes that you can dynamically register for, which is what matters for discovery
		m.put("response_types_supported", Lists.newArrayList("code", "token")); // we don't support these yet: , "id_token", "id_token token"));
		m.put("grant_types_supported", grantTypes);
		//acr_values_supported
		m.put("subject_types_supported", Lists.newArrayList("public", "pairwise"));
		m.put("userinfo_signing_alg_values_supported", Collections2.transform(clientSymmetricAndAsymmetricSigningAlgs, toAlgorithmName));
		m.put("userinfo_encryption_alg_values_supported", Collections2.transform(encService.getAllEncryptionAlgsSupported(), toAlgorithmName));
		m.put("userinfo_encryption_enc_values_supported", Collections2.transform(encService.getAllEncryptionEncsSupported(), toAlgorithmName));
		m.put("id_token_signing_alg_values_supported", Collections2.transform(clientSymmetricAndAsymmetricSigningAlgsWithNone, toAlgorithmName));
		m.put("id_token_encryption_alg_values_supported", Collections2.transform(encService.getAllEncryptionAlgsSupported(), toAlgorithmName));
		m.put("id_token_encryption_enc_values_supported", Collections2.transform(encService.getAllEncryptionEncsSupported(), toAlgorithmName));
		m.put("request_object_signing_alg_values_supported", Collections2.transform(clientSymmetricAndAsymmetricSigningAlgs, toAlgorithmName));
		m.put("request_object_encryption_alg_values_supported", Collections2.transform(encService.getAllEncryptionAlgsSupported(), toAlgorithmName));
		m.put("request_object_encryption_enc_values_supported", Collections2.transform(encService.getAllEncryptionEncsSupported(), toAlgorithmName));
		m.put("token_endpoint_auth_methods_supported", Lists.newArrayList("client_secret_post", "client_secret_basic", "client_secret_jwt", "private_key_jwt", "none"));
		m.put("token_endpoint_auth_signing_alg_values_supported", Collections2.transform(clientSymmetricAndAsymmetricSigningAlgs, toAlgorithmName));
		//display_types_supported
		m.put("claim_types_supported", Lists.newArrayList("normal" /*, "aggregated", "distributed"*/));
		m.put("claims_supported", perunConfig.getClaims());
		//m.put("service_documentation", baseUrl + "about");
		//claims_locales_supported
		//ui_locales_supported
		m.put("claims_parameter_supported", false);
		m.put("request_parameter_supported", true);
		m.put("request_uri_parameter_supported", false);
		m.put("require_request_uri_registration", false);
		//m.put("op_policy_uri", baseUrl + "about");
		//m.put("op_tos_uri", baseUrl + "about");

		m.put("introspection_endpoint", baseUrl + IntrospectionEndpoint.URL); // token introspection endpoint for verifying tokens
		m.put("revocation_endpoint", baseUrl + RevocationEndpoint.URL); // token revocation endpoint


		model.addAttribute(JsonEntityView.ENTITY, m);

		return JsonEntityView.VIEWNAME;
	}
}
