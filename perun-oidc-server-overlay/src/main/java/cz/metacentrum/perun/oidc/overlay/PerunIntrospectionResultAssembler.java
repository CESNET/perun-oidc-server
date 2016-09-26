/*******************************************************************************
 * Copyright 2016 The MITRE Corporation
 *   and the MIT Internet Trust Consortium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package cz.metacentrum.perun.oidc.overlay;

import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.oauth2.model.OAuth2RefreshTokenEntity;
import org.mitre.oauth2.service.IntrospectionResultAssembler;
import org.mitre.oauth2.service.impl.DefaultIntrospectionResultAssembler;
import org.mitre.openid.connect.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newLinkedHashMap;

/**
 * Default implementation of the {@link IntrospectionResultAssembler} interface.
 */
public class PerunIntrospectionResultAssembler extends DefaultIntrospectionResultAssembler {

	@Override
	public Map<String, Object> assembleFrom(OAuth2AccessTokenEntity accessToken, UserInfo userInfo, Set<String> authScopes) {
		Map<String, Object> result = super.assembleFrom(accessToken, userInfo, authScopes);

		result.putAll(accessToken.getAuthenticationHolder().getExtensions());

		return result;
	}

	@Override
	public Map<String, Object> assembleFrom(OAuth2RefreshTokenEntity refreshToken, UserInfo userInfo, Set<String> authScopes) {
		Map<String, Object> result = super.assembleFrom(refreshToken, userInfo, authScopes);

		result.putAll(refreshToken.getAuthenticationHolder().getExtensions());

		return result;
	}
}
