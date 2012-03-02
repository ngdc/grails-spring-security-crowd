/*
 * Copyright 2010-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jboucher.grails.plugins.springsecurity.crowd

import com.atlassian.crowd.integration.http.CrowdHttpAuthenticator

import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter

import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * Provides an Atlassian Crowd based implementation of the {@link AuthenticationFilter} interface.
 *
 * @author <a href="mailto:jordan@jboucher.com">Jordan Boucher</a>
 * @since  2011.12
 */
class CrowdSsoFilter
extends AbstractAuthenticationProcessingFilter {

  CrowdHttpAuthenticator authenticator //DI
  CrowdIntegrator integrator //DI

  CrowdSsoFilter( String defaultFilterProcessesUrl ) {
    super( defaultFilterProcessesUrl )
  }

  // Filter interface
  Authentication attemptAuthentication( HttpServletRequest request, HttpServletResponse response )
  throws AuthenticationException, IOException, ServletException {
    
    if (authenticator.isAuthenticated( request, response )) {
      String token = authenticator.getToken( request )
      CrowdAuthenticationToken auth = integrator.getAuthenticationForToken( token )

      successfulAuthentication( request, response, auth )
    }
  }
}

