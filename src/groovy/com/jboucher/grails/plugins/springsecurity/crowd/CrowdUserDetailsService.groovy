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

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException 


/**
 * Provides an Atlassian Crowd based implementation of the {@link UserDetailsService}.
 *
 * @author <a href="mailto:jordan@jboucher.com">Jordan Boucher</a>
 * @since  2011.12
 */
class CrowdUserDetailsService
implements UserDetailsService {

  CrowdIntegrator integrator //DI

  CrowdUserDetailsService( CrowdIntegrator integrator ) {
    this.integrator = integrator
  }
  
  CrowdUserDetailsService() {}
  
  @Override
  UserDetails loadUserByUsername( String username )
  throws UsernameNotFoundException {
    integrator.getUserDetails( username )
  }
}

