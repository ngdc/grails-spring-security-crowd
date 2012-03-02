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

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.User


/**
 * Provides an Atlassian Crowd based implementation of the {@link AuthenticationProvider} interface.
 *
 * @author <a href="mailto:jordan@jboucher.com">Jordan Boucher</a>
 * @since  2011.12
 */
class CrowdAuthenticationProvider
implements AuthenticationProvider {

  CrowdIntegrator integrator //DI

  CrowdAuthenticationProvider( CrowdIntegrator integrator ) {
    this.integrator = integrator
  }
  
  CrowdAuthenticationProvider() {}
  
  @Override
  Authentication authenticate( Authentication token )
  throws AuthenticationException {
    if (!supports( token.class )) {
      throw new IllegalArgumentException( "token type (${token.class.name}) not supported" )
    }
    integrator.authenticate( token )
  }
  
  @Override
  boolean supports( Class<? extends java.lang.Object> tokenClass ) {
    //TODO: either UPAuth or CrowdAuthToken?
    return UsernamePasswordAuthenticationToken.isAssignableFrom( tokenClass )
  }
}

