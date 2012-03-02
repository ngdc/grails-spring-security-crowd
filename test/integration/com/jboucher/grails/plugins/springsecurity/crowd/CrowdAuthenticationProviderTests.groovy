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

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
import org.springframework.security.core.userdetails.UsernameNotFoundException


/**
 * Integration tests for CrowdAuthenticationProviderTests.
 *
 * @author <a href="mailto:jordan@jboucher.com">Jordan Boucher</a>
 * @since  2011.12
 */
class CrowdAuthenticationProviderTests extends GroovyTestCase {

  def crowdAuthProvider //DI

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

  void testAuth_badUsername() {
    def username = 'badUsername'
    def token = new UsernamePasswordAuthenticationToken( username, "pw" )
    
    String message = shouldFail(UsernameNotFoundException) {
      crowdAuthProvider.authenticate( token )
    }
    //println message
    assertTrue message.contains(username)
    assertTrue message.contains('does not exist')
  }

  void testAuth_goodUsername_badPassword() {
    def username = 'xfred'
    def token = new UsernamePasswordAuthenticationToken( username, "bad" )
    
    String message = shouldFail(AuthenticationException) {
      crowdAuthProvider.authenticate( token )
    }
    //println message
    assertTrue message.contains(username)
    assertTrue message.contains('failed to authenticate')
  }

  void testAuth_goodUsername_goodPassword() {
    def username = 'xfred'
    def token = new UsernamePasswordAuthenticationToken( username, "ff!" )
    def result = crowdAuthProvider.authenticate( token )

    assertNotNull result
    //println result
    //println result.class.name
    assertTrue result instanceof CrowdAuthenticationToken
    assertTrue result.isAuthenticated()
    assertNull result.credentials
    assertNotNull result.principal
    //println result.principal
    //println result.principal.class.name
    assertTrue result.principal instanceof String
    assertEquals username, result.principal
    assertNotNull result.details
    //println result.details
    //println result.details.class.name
    assertTrue result.details instanceof CrowdUserDetails
    assertEquals username, result.details.username
    assertEquals 'Fred', result.details.firstName
    assertEquals 'Flintstone', result.details.lastName
  }

  void testAuth_goodUsername_goodPassword_badConnection() {
    def username = 'xfred'
    def token = new UsernamePasswordAuthenticationToken( username, "ff!" )
    //TODO: "tamper" with the CrowdApi instance, so it has bad connection settings
    def result = crowdAuthProvider.authenticate( token )

    assertNotNull result
    assertTrue result instanceof CrowdAuthenticationToken
  }

  void testSupports_badToken() {
    def result = crowdAuthProvider.supports( org.springframework.security.authentication.TestingAuthenticationToken.class )

    assertFalse result
  }

  void testSupports_goodToken() {
    def result = crowdAuthProvider.supports( UsernamePasswordAuthenticationToken.class )

    assertTrue result
  }
}


