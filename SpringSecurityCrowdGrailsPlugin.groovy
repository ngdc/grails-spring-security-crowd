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

import com.atlassian.crowd.integration.http.CrowdHttpAuthenticator
import com.atlassian.crowd.integration.http.CrowdHttpAuthenticatorImpl
import com.atlassian.crowd.integration.http.filter.CrowdSecurityFilter
import com.atlassian.crowd.integration.http.util.CrowdHttpTokenHelper
import com.atlassian.crowd.integration.http.util.CrowdHttpTokenHelperImpl
import com.atlassian.crowd.integration.http.util.CrowdHttpValidationFactorExtractor
import com.atlassian.crowd.integration.http.util.CrowdHttpValidationFactorExtractorImpl

import com.atlassian.crowd.integration.rest.service.RestCrowdClient
import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory
import com.atlassian.crowd.integration.rest.service.factory.RestCrowdHttpAuthenticationFactory

import com.jboucher.grails.plugins.springsecurity.crowd.GrailsConfigClientProperties
import com.jboucher.grails.plugins.springsecurity.crowd.CrowdIntegrator
import com.jboucher.grails.plugins.springsecurity.crowd.CrowdAuthenticationProvider
import com.jboucher.grails.plugins.springsecurity.crowd.CrowdUserDetailsService

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

import org.springframework.security.core.context.SecurityContextHolder as SCH


class SpringSecurityCrowdGrailsPlugin {
  def version = "1.0.0"
  def grailsVersion = "1.3.7 > *"
  def dependsOn = [ springSecurityCore:'1.1 > *' ]

  // resources that are excluded from plugin packaging
  def pluginExcludes = [
    'docs/**',
    'src/docs/**',
    'grails-app/conf/CrowdConfig.groovy',
    'grails-app/controllers/test/**',
    'grails-app/views/login/**',
    "grails-app/views/error.gsp"
  ]

  def author = "Jordan Boucher"
  def authorEmail = "jordan@jboucher.com"
  def title = "Atlassian Crowd authentication support for the Spring Security plugin."
  def description = "Atlassian Crowd authentication support for the Spring Security plugin."
  def documentation = "http://grails.org/plugin/spring-security-crowd"

  def doWithSpring = {

    def conf = SpringSecurityUtils.securityConfig
    if (!conf || !conf.active) {
      return
    }

    SpringSecurityUtils.loadSecondaryConfig 'DefaultCrowdSecurityConfig'
    // have to get again after overlaying the default config settings
    conf = SpringSecurityUtils.securityConfig

    if (!conf.crowd.active) {
      return
    }

    println 'Configuring Spring Security Crowd ...'

    SpringSecurityUtils.registerProvider 'crowdAuthProvider'

    crowdClientProperties( GrailsConfigClientProperties )

    //OK: crowdClientFactory( RestCrowdClientFactory )

    //FAIL 1: crowdClient( crowdClientFactory:"newInstance", crowdClientProperties )

    //FAIL 2: crowdClient( RestCrowdClient, crowdClientProperties )
    //crowdClientFactory.newInstance( clientProperties )
    //crowdClientFactory.newInstance( baseURL, applicationName, applicationPassword )

    //OK: the REST client only seems to work when constructed with these separate property
    // values - don't know why.
    def props = new GrailsConfigClientProperties()
    def factory = new RestCrowdClientFactory()
    def crowdClient = factory.newInstance(
        props.baseURL,
        props.applicationName,
        props.applicationPassword )

    crowdHttpValidationFactorExtractor( CrowdHttpValidationFactorExtractorImpl ) { bean ->
      bean.factoryMethod = "getInstance"
    }

    crowdHttpTokenHelper( CrowdHttpTokenHelperImpl, crowdHttpValidationFactorExtractor ) { bean ->
      bean.factoryMethod = "getInstance"
    }

    //FAIL 3: crowdHttpAuthFactory( RestCrowdHttpAuthenticationFactory )
    //fyi: the above class is busted!  Rather than take a ClientProperties parameter in its
    // ctor and work correctly, it implicitly tries to load a ClientPropertiesImpl, which is
    // based on a crowd.properties resource in the classpath.  However, we do not utilize
    // this facility in this plugin, nor should we have to do it that way, as our 
    // GrailsConfigClientProperties implementation just wraps the standard Grails config settings.
    // Fail.  Sigh.

    crowdHttpAuthenticator( CrowdHttpAuthenticatorImpl,
        crowdClient, crowdClientProperties, crowdHttpTokenHelper )

    //TODO: if this filter fails, then we need to roll our own and try that
    crowdSecurityFilter( CrowdSecurityFilter,
        crowdHttpAuthenticator, crowdClientProperties )

    //  SpringSecurityUtils.registerFilter 'crowdSsoFilter', SecurityFilterPosition.OPENID_FILTER

    //crowdSsoFilter( CrowdSsoFilter, "/an/url/here" ) {
    // authenticator = crowdHttpAuthenticator
    // integrator = crowdIntegrator
    //}

    crowdIntegrator( CrowdIntegrator, crowdClientProperties ) {
      client = crowdClient
    }

    crowdAuthProvider( CrowdAuthenticationProvider, crowdIntegrator )

    userDetailsService( CrowdUserDetailsService, crowdIntegrator )

    println '... finished Spring Security Crowd'
  }

  def doWithDynamicMethods = { ctx ->

    for (controllerClass in application.controllerClasses) {
      addControllerMethods controllerClass.metaClass, ctx
    }
  }

  def onChange = { event ->
    def conf = SpringSecurityUtils.securityConfig

    if (!conf || !conf.active) {
      return
    }

    if (event.source && application.isControllerClass(event.source)) {

      if (SpringSecurityUtils.securityConfigType == 'Annotation') {
        event.ctx.objectDefinitionSource.initialize conf.controllerAnnotations.staticRules,
          event.ctx.grailsUrlMappingsHolder, application.controllerClasses
      }

      addControllerMethods application.getControllerClass(event.source.name).metaClass, event.ctx
    }
  }

  private void addControllerMethods(MetaClass mc, ctx) {

    if (!mc.respondsTo(null, 'getAuthentication')) {
      mc.getAuthentication = { -> SCH.context?.authentication }
    }

    //fyi: replace the s2-core implementation of this method, as it assumes there
    // will be a domain class and user's persisted locally, which they are not for
    // the s2-crowd plugin.
    if (mc.respondsTo(null, 'getAuthenticatedUser')) {
      mc.getAuthenticatedUser = { ->
        if (!ctx.springSecurityService.isLoggedIn()) return null

        SCH.context?.authentication?.user
      }
    }
  }
}
