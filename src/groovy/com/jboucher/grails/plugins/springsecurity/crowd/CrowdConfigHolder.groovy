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

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.ConfigurationHolder


/**
 * Provides a configuration holder pattern for the {@code CrowdConfig.groovy} file.
 *
 * @author <a href="mailto:jordan@jboucher.com">Jordan Boucher</a>
 * @since  2011.12
 */
class CrowdConfigHolder {

  static final NAME = 'CrowdConfig'

  @Lazy
  static ConfigObject config = {
    GroovyClassLoader classLoader = new GroovyClassLoader( CrowdConfigHolder.classLoader )
    ConfigSlurper slurper = new ConfigSlurper( grails.util.Environment.current.name )
    ConfigObject result

    try {
      result = slurper.parse( classLoader.loadClass( CrowdConfigHolder.NAME ) )
    }
    catch (ignored) {
      println "ERROR: ${CrowdConfigHolder.NAME} not found in application"
    }

    ConfigurationHolder.config.merge( result )

    return result
  }()
}

