[ci]: https://ci.pcgamingfreaks.at/job/PluginLib/
[ciImg]: https://ci.pcgamingfreaks.at/job/PluginLib/badge/icon
[versionImg]: https://img.shields.io/maven-metadata/v?color=blue&label=version&metadataUrl=https%3A%2F%2Frepo.pcgamingfreaks.at%2Frepository%2Fmaven-snapshots%2Fat%2Fpcgamingfreaks%2FPluginLib%2Fmaven-metadata.xml
[coverageImg]:https://coveralls.io/repos/github/GeorgH93/PCGF_PluginLib/badge.svg
[coverage]: https://coveralls.io/github/GeorgH93/PCGF_PluginLib
[license]: https://github.com/GeorgH93/PCGF_PluginLib/blob/master/LICENSE
[licenseImg]: https://img.shields.io/github/license/GeorgH93/PCGF_PluginLib.svg
[javadoc]: https://ci.pcgamingfreaks.at/job/PluginLib/javadoc/

# PCGF - PluginLib - UUID converter

[![Build Status][ciImg]][ci] [![Coverage Status][coverageImg]][coverage] ![Version][versionImg] [![licenseImg]][license]

## Features:
* UUID converter
  * Supports Name -> UUID, UUID -> Name or UUID -> Name changes
  * Supports online and offline mode UUIDs
  * Batch converting and automatic batch size detection (in case Mojang decides to reduce the maximum batch size again)
  * Caching
    * If the lib is running as a Plugin the cache can be shared by all plugins using it
    * Preloads UUIDs from the Minecraft servers UUID cache

## Requirements:
* Java 8 (or newer)

## Adding it to your plugin:
The library can be added in two ways to your plugin.
1. Requiring it to be installed as a plugin (will be published on dev.bukkit.org and spigotmc.org soon)
2. Shading it into your plugin (requires more RAM)

### Adding the UUID converter library as an dependency with maven:
#### Repository:
```xml
<repository>
	<id>pcgf-repo</id>
	<url>https://repo.pcgamingfreaks.at/repository/maven-everything</url>
</repository>
```

#### Dependency:
```xml
<dependency>
 	<groupId>at.pcgamingfreaks.pcgf_pluginlib</groupId>
 	<artifactId>pcgf_pluginlib-uuid</artifactId>
 	<version>1.0.29-SNAPSHOT</version><!-- Check version shield for newest version -->
 	<classifier>shade-friendly</classifier>
</dependency>
```

### Requiring the library to be installed as a standalone plugin:
Add `PCGF_PluginLib` as a dependency for your Bukkit/Spigot or BungeeCord plugin.
The users will have to install the library as a plugin. Download: [https://ci.pcgamingfreaks.at/job/PluginLib/][ci]

### Shading the library into your plugin with maven:
By adding the library to your plugin through shading it will require more memory (if multiple plugins using the library are installed).
The recommended shading settings:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.4.1</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
                <createDependencyReducedPom>false</createDependencyReducedPom>
                <minimizeJar>true</minimizeJar><!-- No need to include everything if you are only using some of the features -->
                <artifactSet>
                    <includes>
                        <include>at.pcgamingfreaks.pcgf_pluginlib:pcgf_pluginlib-uuid</include>
                    </includes>
                </artifactSet>
                <relocations>
                    <!-- Relocate the lib to prevent conflicts with other plugins using it -->
                    <relocation>
                        <pattern>at.pcgamingfreaks</pattern>
                        <shadedPattern>${project.groupId}.libs.at.pcgamingfreaks</shadedPattern>
                    </relocation>
                </relocations>
                <filters>
                    <!-- Don't copy bungee.yml, bungee_config.yml, config.yml and plugin.yml to prevent conflicts with your plugin -->
                    <filter>
                        <artifact>at.pcgamingfreaks:PluginLib</artifact>
                        <excludes>
                            <exclude>*.yml</exclude>
                        </excludes>
                    </filter>
                </filters>
            </configuration>
        </execution>
    </executions>
</plugin>
```
Replace `your_package` with the package of your plugin.  
**Do not shade this library into your plugin without relocation!!!**

## Links:
* [Build Server ![Build Status][ciImg]][ci]
* [Code Coverage ![Coverage Status][coverageImg]][coverage]
* [Javadoc][javadoc]
