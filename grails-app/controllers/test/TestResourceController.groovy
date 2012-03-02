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
package test


class TestResourceController {

  def springSecurityService //DI
  def userDetailsService //DI

  def crowdClientProperties

  /**
   * Exercise the metaprogrammed conroller methods and display a little helpful information
   * on the index page for any test controller.
   */
  def index = {
    //metaprogrammed method by s2-crowd plugin:
    //def auth = SCH.context?.authentication
    //metaprogrammed method by s2-core plugin:
    //def principal = auth?.principal

    render """
    <pre>
    isLoggedIn = ${isLoggedIn()}
    principal = ${principal}
    authentication = {authentication}
    user = ${authenticatedUser}

    baseURL = ${crowdClientProperties.baseURL}
    applicationAuthenticationURL = ${crowdClientProperties.applicationAuthenticationURL}
    applicationName = ${crowdClientProperties.applicationName}
    applicationPassword = ${crowdClientProperties.applicationPassword}
    </pre>
    """
  }

  /**
   * Exercise the {@code CrowdUserDetailsService} via this simple interface.
   * <p>
   * 
   * Run the following command in the s2-crowd plugin project.
   * <pre>
   *   grails run-app
   * </pre>
   *
   * Then hit the following link - just change the username to access other users:
   * <pre>
   *   http://localhost:8080/spring-security-crowd/testResource/userDetails?username=xfred
   * </pre>
   */
  def userDetails = {
    def username = (params.username) ?: 'xfred'
    def user = userDetailsService.loadUserByUsername( username )

    render """
    <pre>
    user class = ${user.class}
    user toString = ${user}

    user username = ${user.username}
    user emailAddress = ${user.emailAddress}
    user displayName = ${user.displayName}
    user firstName = ${user.firstName}
    user lastName = ${user.lastName}
    </pre>
    """
  }
}

