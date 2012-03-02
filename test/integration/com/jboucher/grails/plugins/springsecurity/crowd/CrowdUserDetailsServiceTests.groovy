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

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
import org.springframework.security.core.userdetails.UsernameNotFoundException


/**
 * Integration tests for CrowdUserDetailsService.
 *
 * @author <a href="mailto:jordan@jboucher.com">Jordan Boucher</a>
 * @since  2011.12
 */
class CrowdUserDetailsServiceTests extends GroovyTestCase {

  static final String ROLE_ADMIN = "ROLE_test-admins"
  static final String ROLE_DEV = "ROLE_test-developers"
  static final String ROLE_USER = "ROLE_test-users"

  def userDetailsService //DI

  /**
   * {@inheritDoc}
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() {
    super.setUp()
    CH.config = new ConfigObject()
  }

  /**
   * {@inheritDoc}
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() {
    super.tearDown()
    CH.config = null
  }

  void testLoadUserByUsername_NotFound() {
    def username = 'not_a_user'
    String message = shouldFail(UsernameNotFoundException) {
      userDetailsService.loadUserByUsername username
    }

    //println message
    assertTrue message.contains(username)
    assertTrue message.contains('does not exist')
  }

  void testLoadUserByUsername_Fred() {

    String username = 'xfred'

    def details = userDetailsService.loadUserByUsername(username)
    assertNotNull details

    assertEquals username, details.username
    assertEquals 'Fred', details.firstName
    assertEquals 'Flintstone', details.lastName
    assertEquals 'Fred Flintstone', details.displayName
    assertEquals 'Fred.Flintstone@jboucher.com', details.emailAddress
    assertEquals true, details.enabled
    assertEquals([ROLE_ADMIN, ROLE_DEV, ROLE_USER], details.authorities*.authority.sort())
    /*
    assertEquals enabled, details.accountNonExpired
    assertEquals enabled, details.accountNonLocked
    assertEquals enabled, details.credentialsNonExpired
    */
  }

  void testLoadUserByUsername_Wilma() {

    String username = 'xwilma'

    def details = userDetailsService.loadUserByUsername(username)
    assertNotNull details

    assertEquals username, details.username
    assertEquals 'Wilma', details.firstName
    assertEquals 'Flintstone', details.lastName
    assertEquals 'Wilma Flintstone', details.displayName
    assertEquals 'Wilma.Flintstone@jboucher.com', details.emailAddress
    assertEquals true, details.enabled
    assertEquals([ROLE_ADMIN, ROLE_USER], details.authorities*.authority.sort())
  }

/*
  void testLoadUserByUsername_NoRoles() {

    def details = userDetailsService.loadUserByUsername( 'xslate' )

    assertEquals 1, details.authorities.size()
    assertEquals 'ROLE_NO_ROLES', details.authorities.iterator().next().authority
  }
*/

/*
  void testLoadUserByUsername_SkipRoles() {

    String loginName = 'loginName'
    String password = 'password123'
    boolean enabled = true

    def details = userDetailsService.loadUserByUsername(loginName, false)
    assertNotNull details

    assertEquals password, details.password
    assertEquals loginName, details.username
    assertEquals enabled, details.enabled
    assertEquals enabled, details.accountNonExpired
    assertEquals enabled, details.accountNonLocked
    assertEquals enabled, details.credentialsNonExpired
    assertEquals 0, details.authorities.size()
  }
*/
}

