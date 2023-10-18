# Install for Maven project

The following is a recap of the
[instructions](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-with-a-personal-access-token)
outlined by GitHub.

Add the following settings to your `~/.m2/settings.xml` file:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <activeProfiles>
    <!-- other active profiles -->
    <!-- the id 'github' must match the profile id from below -->
    <activeProfile>github</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>github</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
        </repository>
        <repository>
          <!-- must match server.id -->
          <id>github</id>
          <url>https://maven.pkg.github.com/eclipse-tractusx/SSI-agent-lib</url>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>

  <servers>
    <server>
      <id>github</id>
      <username>${env.GITHUB_PACKAGE_USERNAME}</username>
      <password>${env.GITHUB_PACKAGE_PASSWORD}</password>
    </server>
  </servers>
</settings>
```

Then add the dependency to your `pom.xml` file as usual:

```xml
<dependency>
  <groupId>org.eclipse.tractusx.ssi</groupId>
  <artifactId>cx-ssi-lib</artifactId>
  <version>0.0.17</version>
  <scope>compile</scope>
</dependency>
```

# Install for Gradle project

Add a custom maven repository to your `build.gradle`


```groovy
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/eclipse-tractusx/SSI-agent-lib")
        credentials {
            // how you retrieve the githubUserName, githubToken is up to you
            // you can use a gradle plugin to load a .env file or
            // you can use a gradle.properties file etc.
            username = "${githubUserName}"
            password = "${githubToken}"
        }
    }
}
dependencies {
  implementation 'org.eclipse.tractusx.ssi:cx-ssi-lib:0.0.17'
}
```
