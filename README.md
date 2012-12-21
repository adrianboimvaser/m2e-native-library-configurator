Maven Native Library Location Configurator for m2e
==================================================

This is a m2e project configurator that reads the `java.library.path` property from the `<argLine>` maven-surefire-plugin configuration and sets the native library location property of the Maven Dependencies classpath container.

Update site: https://raw.github.com/adrianboimvaser/m2e-native-library-configurator/master/m2e-native-library-configurator-update-site

This is how you would configure maven-surefire-plugin:

    <plugin>
      <artifactId>maven-surefire-plugin</artifactId>
      <configuration>
        <argLine>-Djava.library.path="${project.build.directory}/natives"</argLine>
      </configuration>
    </plugin>

You would copy some dlls to `${project.build.directory}/natives` using the `copy`goal of maven-dependency-plugin:

    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-dependency-plugin</artifactId>
      <executions>
        <execution>
          <phase>initialize</phase>
          <goals>
            <goal>copy</goal>
          </goals>
          <configuration>
            <stripVersion>true</stripVersion>
            <artifactItems>
              <artifactItem>
                <groupId>some.group.id</groupId>
                <artifactId>some-artifact-id</artifactId>
                <version>1.0</version>
                <type>dll</type>
                <outputDirectory>${project.build.directory}/natives</outputDirectory>
                <destFileName>dllname.dll</destFileName>
              </artifactItem>
            </artifactItems>
          </configuration>
        </execution>
      </executions>
    </plugin>

... or by some other mean.
