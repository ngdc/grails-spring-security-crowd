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

import com.atlassian.crowd.exception.CrowdException
import com.atlassian.crowd.integration.rest.entity.UserEntity
//import com.atlassian.crowd.integration.rest.service.RestCrowdClient
import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory
import com.atlassian.crowd.model.authentication.UserAuthenticationContext
import com.atlassian.crowd.model.authentication.ValidationFactor
import com.atlassian.crowd.service.client.ClientProperties
import com.atlassian.crowd.service.client.CrowdClient

//import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails

import org.springframework.security.core.AuthenticationException
import org.springframework.security.authentication.AccountStatusException


/**
 * Provides the implementation for any interactions with the Crowd API.
 * <p>
 * A design goal of this S2 Crowd plugin is to encapsulate all dependencies on the
 * Crowd integration library within this class.
 *
 * @author <a href="mailto:jordan@jboucher.com">Jordan Boucher</a>
 * @since  2011.12
 */
class CrowdIntegrator {

  @Lazy
  static CrowdIntegrator instance = {
    def result = new CrowdIntegrator()
    def props = new GrailsConfigClientProperties()
    def factory = new RestCrowdClientFactory()

    result.client = factory.newInstance(
        props.baseURL,
        props.applicationName,
        props.applicationPassword )
    result
  }()


  CrowdIntegrator( ClientProperties clientProperties ) {
    this.clientProperties = clientProperties
  }

  CrowdIntegrator() {
    this( new GrailsConfigClientProperties() )
  }

  @Lazy
  def securityConfig = SpringSecurityUtils.securityConfig

  @Delegate
  ClientProperties clientProperties
  
  CrowdClient client

  void test() {
    client.testConnection()
  }

  void requestUsernames( String email ) {
    client.requestUsernames( email )
  }

  void requestPasswordReset( String username ) {
    client.requestPasswordReset( username )
  }

  private final Map<String,GrantedAuthority> roleCache = new HashMap<String,GrantedAuthority>()

  GrantedAuthority getRole( String groupName ) {
    GrantedAuthority result = roleCache[ groupName ]

    if (!result) {
      result = new GrantedAuthorityImpl( groupName ) 
      roleCache[ groupName ] = result
    }
    result
  }

  Collection<GrantedAuthority> getRoles( List<String> groupNames ) {
    Collection<GrantedAuthority> result = new ArrayList<GrantedAuthority>()

    if (securityConfig.crowd.roles.usePlainOldGroupName) {
      groupNames?.each { name ->
        result.add getRole( name )
      }
    }
    if (securityConfig.crowd.roles.usePrefixedGroupName) {
      String prefix = (securityConfig.crowd.roles.prefix) ?: "ROLE_"

      groupNames?.each { name ->
        name = "${prefix}${name}"
        result.add getRole( name )
      }
    }

    result
  }

  Collection<GrantedAuthority> getRoles( String username ) {
    List<String> groupNames = getGroupNames( username )

    getRoles( groupNames )
  }

  List<String> getGroupNames( String username ) {
    List<String> result = client.getNamesOfGroupsForUser( username, 0, 100 )
    //TODO: handle large scale, where our limit of 100 may be exceded

    if (securityConfig.crowd.groups.supportNested) {
      def nested = client.getNamesOfGroupsForNestedUser( username, 0, 100 )

      result << nested
    }
    result
  }
  
  Authentication authenticate( String username, String password, Object details = null )
  throws AuthenticationException {
    Authentication result

    try {
      UserEntity entity = client.authenticateUser( username, password )
      CrowdUserDetails user = toUserDetails( entity )

      result = new CrowdAuthenticationToken( user, details )
    }
    catch (CrowdException e) {
      throwSpringException( e )
    }

    result
  }

  Authentication authenticate( Authentication token )
  throws AuthenticationException {
    authenticate( token.principal, token.credentials, token.details )
  }

  CrowdUserDetails getUserDetails( String username ) {
    CrowdUserDetails result

    try {
      UserEntity entity = client.getUserWithAttributes( username )
      //client.getUser( username )

      result = toUserDetails( entity )
    }
    catch (CrowdException e) {
      throwSpringException( e )
    }

    result
  }

  CrowdUserDetails getUserDetailsForToken( String token ) {
    CrowdUserDetails result

    try {
      UserEntity entity = client.findUserFromSSOToken( token )
      //                    throws InvalidTokenException,
      //                           ApplicationPermissionException,
      //                           InvalidAuthenticationException,
      //                           OperationFailedException
      //client.getUser( username )

      result = toUserDetails( entity )
    }
    catch (CrowdException e) {
      throwSpringException( e )
    }

    result
  }

  Authentication getAuthenticationForToken( String token ) {
    CrowdUserDetails user = getUserDetailsForToken( token )

    new CrowdAuthenticationToken( user, null )
  }

  Authentication authenticateSsoToken( String token )
  throws AuthenticationException {
    Authentication result

    try {
      validateSsoToken( token )
      CrowdUserDetails user = getUserDetailsForToken( token )

      result = new CrowdAuthenticationToken( user, null )
    }
    catch (CrowdException e) {
      throwSpringException( e )
    }
    /*
    UserAuthenticationContext context = new UserAuthenticationContext()

    context.application = applicationName
    context.name = ""
    context.validationFactors

    try {
      String token = client.authenticateSSOUserWithoutValidatingPassword( context )
      
      //UserEntity entity = client.( token )
      //CrowdUserDetails user = toUserDetails( entity )

      result = new CrowdAuthenticationToken( user, details )
    }
    catch (CrowdException e) {
      throwSpringException( e )
    }
    */

    result
  }

  // called from a Crowd?
  void validateSsoToken( String token, def request ) {
    try {
      List<ValidationFactor> validationFactors = new ArrayList<ValidationFactor>()
      validationFactors << new ValidationFactor( ValidationFactor.NAME, applicationName )
      validationFactors << new ValidationFactor( ValidationFactor.REMOTE_ADDRESS, value )
      client.validateSSOAuthentication( token, validationFactors )
      //                         throws OperationFailedException,
      //                                InvalidAuthenticationException,
      //                                ApplicationPermissionException,
      //                                InvalidTokenException
    }
    catch (CrowdException e) {
      //throwSpringException( e )
      log.error ""
    }
  }

  // called from a CrowdLogoutHandler
  void invalidateSsoToken( String token ) {
    try {
      client.invalidateSSOToken( token )
      //                  throws ApplicationPermissionException,
      //                         InvalidAuthenticationException,
      //                         OperationFailedException
    }
    catch (CrowdException e) {
      //throwSpringException( e )
      log.error ""
    }
  }

  private CrowdUserDetails toUserDetails( UserEntity entity ) {
    Collection<GrantedAuthority> roles = getRoles( entity.name )
    //TODO: if entity does not have attributes to set these flags, perhaps we need to call 
    // the getUserWithAttributes to obtain them?  Not sure how essential these flags are...
    Map flags = [
        enabled:true,
        accountNonExpired:true,
        credentialsNonExpired:true,
        accountNonLocked:true,
    ]
    CrowdUserDetails result = new CrowdUser( entity, roles, flags )

    result
  }

  private throwSpringException( CrowdException e ) {
    def m = "CrowdIntegrator: ${e.message}"

    //TODO: do the actual mapping here eventually
    switch (e.class) {
    case com.atlassian.crowd.exception.UserNotFoundException:
      throw new org.springframework.security.core.userdetails.UsernameNotFoundException( m, e )

    case com.atlassian.crowd.exception.InvalidAuthenticationException:
      throw new org.springframework.security.authentication.BadCredentialsException( m, e )

    case com.atlassian.crowd.exception.ExpiredCredentialException:
      throw new org.springframework.security.authentication.CredentialsExpiredException( m, e )

    case com.atlassian.crowd.exception.InactiveAccountException:
      //TODO: does (inactive == disabled || inactive == locked)
      // throw new org.springframework.security.authentication.DisabledException( m, e )
      // throw new org.springframework.security.authentication.LockedException( m, e )

    case com.atlassian.crowd.exception.ApplicationPermissionException:
      // 

    case com.atlassian.crowd.exception.OperationFailedException:
      //fyi: there are several subclasses of this guy...
      // DirectoryInstantiationException, InvalidCrowdServiceException, 
      // OperationNotSupportedException, UnsupportedCrowdApiException
      
    case java.net.UnknownHostException:
      //fyi: this happens when the baseURL for the Crowd service is invalid

    default:
      throw new RuntimeException( m, e )
    }
  }

  @Override
  String toString() {
    "${getApplicationName()}:${getApplicationPassword()}@${getBaseURL()}"
  }
}

