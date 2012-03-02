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

import com.atlassian.crowd.integration.rest.entity.UserEntity

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails


/**
 * Provides an Atlassian Crowd based implementation of the {@link UserDetails} interface,
 * as well as the {@link CrowdUserDetails} extensions.
 *
 * @author <a href="mailto:jordan@jboucher.com">Jordan Boucher</a>
 * @since  2011.12
 */
class CrowdUser
extends User
implements CrowdUserDetails {

  static private boolean testFlag( Boolean flag, boolean defaultValue ) {
    (flag != null) ? flag : defaultValue
  }

  CrowdUser( UserEntity entity,
             Collection<? extends GrantedAuthority> authorities,
             Map flags = [:] ) {
    super( entity.name, 'notStoredForSecurityReason!',
           testFlag( flags.enabled, true ),
           testFlag( flags.accountNonExpired, true ),
           testFlag( flags.credentialsNonExpired, true ),
           testFlag( flags.accountNonLocked, true ),
           authorities )

    this.entity = entity
  }

  private final entity
  
  String getEmailAddress() {
    entity.emailAddress
  }

  String getFirstName() {
    entity.firstName
  }

  String getLastName() {
    entity.lastName
  }

  String getDisplayName() {
    entity.displayName
  }
}
