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

import cz.metacentrum.perun.oidc.client.PerunUtils;

import java.util.List;


/**
 * Bean to hold configuration information that must be injected into various parts
 * of our application. We extend it because need to inject more own properties.
 *
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class PerunConfigurationBean {

	public String getInstanceLogoUrl() {
		return PerunUtils.getProperty("oidc.logoUrl", false);
	}

	public String getOidcPerunUrl() {
		return PerunUtils.getProperty("oidc.perun.url");
	}

	public List<String> getScopes() {
		return PerunUtils.getScopes();
	}

	public List<String> getClaims() {
		return PerunUtils.getClaims();
	}

}
