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

import org.springframework.security.authentication.Authentication
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.User


/**
 * Provides an Atlassian Crowd based {@link Authentication} token.
 *
 * @author <a href="mailto:jordan@jboucher.com">Jordan Boucher</a>
 * @since  2011.12
 */
class CrowdAuthenticationToken
extends UsernamePasswordAuthenticationToken
implements Authentication {

  CrowdAuthenticationToken( CrowdUser user, Object details = null ) {
    super( user.username, 'notStoredForSecurityReasons!', user.authorities ) 

    setDetails( (details) ?: user )
    this.user = user

    eraseCredentials() // this token goes into the session, so don't keep secrets around
  }

  final CrowdUser user
}


