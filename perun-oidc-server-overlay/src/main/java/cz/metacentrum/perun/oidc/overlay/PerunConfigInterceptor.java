/**
 * Copyright 2016 The MITRE Corporation
 * and the MIT Internet Trust Consortium
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 *
 */
package cz.metacentrum.perun.oidc.overlay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * Injects our own configuration bean into the request context.
 * This allows JSPs and the like to call "perunConfig.instanceLogoUrl" among others.
 *
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class PerunConfigInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private PerunConfigurationBean perunConfig;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		request.setAttribute("perunConfig", perunConfig);
		return true;
	}

}
