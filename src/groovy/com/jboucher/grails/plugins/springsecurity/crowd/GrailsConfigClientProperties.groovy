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

import com.atlassian.crowd.embedded.api.PasswordCredential
import com.atlassian.crowd.model.authentication.ApplicationAuthenticationContext
import com.atlassian.crowd.model.authentication.ValidationFactor
import com.atlassian.crowd.service.client.ClientProperties

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils


/**
 * Provides a Grails configuration based implementation of the Atlassian Crowd
 * {@link ClientProperties} interface.
 *
 * @author <a href="mailto:jordan@jboucher.com">Jordan Boucher</a>
 * @since  2011.12
 */
class GrailsConfigClientProperties
implements ClientProperties {

  static final crowdConfigList = [
      "applicationName",
      "applicationPassword",
      "applicationAuthenticationURL",
      "baseURL" ]


  GrailsConfigClientProperties() {}

  @Lazy
  def crowdConfig = CrowdConfigHolder.config?.crowd

  def getCrowdConfig( String name ) {
    def result = null

    if (crowdConfigList.contains( name )) {
      result = crowdConfig[name]

      if (result == null ) {
        //try foo/bar/baz
        if (result == null ) {
          //try foo.bar.baz
        }
      }
    }
    result
  }

  @Lazy
  def securityConfig = SpringSecurityUtils.securityConfig?.crowd

  def getSecurityConfig( String name ) {
    def result = securityConfig[name]

    if (result == null ) {
      //try foo/bar/baz
      if (result == null ) {
        //try foo.bar.baz
      }
    }
    result
  }

  def getConfig( String name ) {
    def result = getCrowdConfig( name )

    if (result == null) {
      result = getSecurityConfig( name )
    }

    result
  }

  //ClientProperties Interface: all getters are dynamic
  def propertyMissing( String name ) {
    def result = getConfig( name )
    
    result
  }

  //ClientProperties Interface
  ApplicationAuthenticationContext getApplicationAuthenticationContext() {
    ApplicationAuthenticationContext result = new ApplicationAuthenticationContext()
    ValidationFactor[] factors =  null

    result.name = applicationName
    result.credential = new PasswordCredential( applicationPassword )
    result.validationFactors = factors

    result
  }

  String getApplicationAuthenticationURL() {
    getConfig( "applicationAuthenticationURL" )
  }

  String getApplicationName() {
    getConfig( "applicationName" )
  }

  String getApplicationPassword() {
    getConfig( "applicationPassword" )
  }

  String getBaseURL() {
    getConfig( "baseURL" )
  }

  String getCookieTokenKey() {
    getConfig( "cookieTokenKey" )
  }

  String getHttpMaxConnections() {
    getConfig( "httpMaxConnections" )
  }

  String getHttpProxyHost() {
    getConfig( "httpProxyHost" )
  }

  String getHttpProxyPassword() {
    getConfig( "httpProxyPassword" )
  }

  String getHttpProxyPort() {
    getConfig( "httpProxyPort" )
  }

  String getHttpProxyUsername() {
    getConfig( "httpProxyUsername" )
  }

  String getHttpTimeout() {
    getConfig( "httpTimeout" )
  }

  String getSessionLastValidation() {
    getConfig( "sessionLastValidation" )
  }

  String getSessionTokenKey() {
    getConfig( "sessionTokenKey" )
  }

  long getSessionValidationInterval() {
    long result = 5
    def value = getConfig( "sessionValiationInterval" )

    if (value) {
      result = Long.valueOf( value )
    }

    result
  }

  String getSocketTimeout() {
    getConfig( "socketTimeout" )
  }

  //ClientProperties Interface
  void updateProperties( Properties p ) {
    throw new UnsupportedOperationException( 
      "Crowd implementation does not allow updating properties via this interface!" )
  }
}

