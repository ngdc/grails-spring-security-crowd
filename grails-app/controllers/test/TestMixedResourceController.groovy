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

import grails.plugins.springsecurity.Secured

import org.springframework.security.core.context.SecurityContextHolder as SCH


class TestMixedResourceController
extends TestResourceController {

  def springSecurityService //DI

  /*
  def index = {
    //def auth = SCH.context?.authentication
    //meta-method by core: def principal = auth?.principal

    render """
    <pre>
    isLoggedIn = ${springSecurityService.isLoggedIn()}
    principal = ${principal}
    authentication = ${authentication}
    user = ${authenticatedUser}
    </pre>
    """
  }
  */
  // index supplied by base class

  def noRole = {
    render "noRole"
  }

  @Secured( ["ROLE_test-admins"] )
  def adminRole = {
    render "adminRole"
  }

  @Secured( ["ROLE_test-users"] )
  def userRole = {
    render "userRole"
  }

  @Secured( ["ROLE_test-admins", "ROLE_test-users"] )
  def eitherRole = {
    render "eitherRole"
  }
}
