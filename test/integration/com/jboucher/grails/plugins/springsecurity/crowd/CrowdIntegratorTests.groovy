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
 * Integration tests for CrowdIntegrator.
 *
 * @author <a href="mailto:jordan@jboucher.com">Jordan Boucher</a>
 * @since  2011.12
 */
class CrowdIntegratorTests extends GroovyTestCase {

  CrowdIntegrator api

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

/*
  void test_badServiceUrl() {
    def api = new CrowdIntegrator( serviceUrl:"http://www.google.com" )
    
    String message = shouldFail(com.atlassian.crowd.exception.InvalidCrowdServiceException) {
      api.test()
    }
    assertTrue message.contains('URL does not specify a valid Crowd User Management REST service')
  }

  void test_badAppName() {
    def api = new CrowdIntegrator( appName:"notLikelyValidAppName" )
    
    //fyi: the interface does not tell us an application by the given name is not found
    String message = shouldFail(com.atlassian.crowd.exception.InvalidAuthenticationException) {
      api.test()
    }
    assertTrue message.contains('Application failed')
  }

  void test_badAppPassword() {
    def api = new CrowdIntegrator( appPassword:"notValidAppPassword" )
    
    //fyi: the interface does not tell us the password is bad
    String message = shouldFail(com.atlassian.crowd.exception.InvalidAuthenticationException) {
      api.test()
    }
    assertTrue message.contains('Application failed')
  }
*/

  void test_good() {
    def api = CrowdIntegrator.instance
    def response = api.test()

    assertNull response
  }

}

