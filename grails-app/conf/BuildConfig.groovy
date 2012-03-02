grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
  // inherit Grails' default dependencies
  inherits("global") {
    // uncomment to disable ehcache
    // excludes 'ehcache'
  }
  log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
  repositories {
    grailsPlugins()
    grailsHome()
    grailsCentral()

    mavenLocal()
    mavenRepo "https://maven.atlassian.com/content/groups/public/"
    mavenCentral()
  }

  dependencies {
    runtime( 'org.grails.plugins:spring-security-core:1.2.7' )

    //fyi: http://docs.atlassian.com/atlassian-crowd/2.4.0/
    runtime( 'com.atlassian.crowd:crowd-integration-client-rest:2.4.0' ) {
      transitive = true

      //fyi: there is a warning when resolving dependencies - a transitive dependency
      // from crowd-integration-common is not found.  See below ref, which seems to be
      // searching for a "atlassian-plugin" rather than a "jar".
      // com.atlassian.security#atlassian-cookie-tools;2.3!atlassian-cookie-tools.atlassian-plugin
      // However, the code appears to work OK without this particular library being present.
      excludes "atlassian-cookie-tools"
    }
  }
}
