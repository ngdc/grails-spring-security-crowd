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


/**
 * Integration tests for CrowdIntegrator.
 *
 * @author <a href="mailto:jordan@jboucher.com">Jordan Boucher</a>
 * @since  2011.12
 */
class CrowdIntegratorTests extends GroovyTestCase {

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

  void testSomething() {
  }

/*OLD*-
  void testServiceUrl_default() {
    def api = new CrowdIntegrator()
    def url = api.serviceURL

    assertTrue url.startsWith( "http" )
    assertTrue url.contains( "localhost" )
    assertTrue url.endsWith( "crowd/" )
  }

  void testServiceUrl_custom() {
    def url = "http://www.google.com"
    def api = new CrowdIntegrator( serviceUrl:url )

    assertEquals url, api.serviceUrl
  }

  void testApplicationName_default() {
    def api = new CrowdIntegrator()

    assertEquals "test", api.applicationName
  }

  void testApplicationName_custom() {
    def appName = "app"
    def api = new CrowdIntegrator( applicationName:appName )

    assertEquals appName, api.applicationName
  }

  void testApplicationPassword_default() {
    def api = new CrowdIntegrator()

    assertEquals "test!", api.applicationPassword
  }

  void testApplicationPassword_custom() {
    def appPassword = "secret"
    def api = new CrowdIntegrator( appPassword:appPassword )

    assertEquals appPassword, api.applicationPassword
  }
*OLD*/
}

