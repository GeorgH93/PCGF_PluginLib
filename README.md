[ci]: https://ci.pcgamingfreaks.at/job/PluginLib/
[ciImg]: https://ci.pcgamingfreaks.at/job/PluginLib/badge/icon
[versionImg]: https://img.shields.io/maven-metadata/v?color=blue&label=version&metadataUrl=https%3A%2F%2Frepo.pcgamingfreaks.at%2Frepository%2Fmaven-snapshots%2Fat%2Fpcgamingfreaks%2FPluginLib%2Fmaven-metadata.xml
[coverageImg]:https://codecov.io/gh/GeorgH93/PCGF_PluginLib/branch/master/graph/badge.svg?token=EKWJRvdGny
[coverage]: https://codecov.io/gh/GeorgH93/PCGF_PluginLib
[license]: https://github.com/GeorgH93/PCGF_PluginLib/blob/master/LICENSE
[licenseImg]: https://img.shields.io/github/license/GeorgH93/PCGF_PluginLib.svg
[javadoc]: https://ci.pcgamingfreaks.at/job/PluginLib/javadoc/

# PCGF - PluginLib
A library for Bukkit, Spigot, Paper and BungeeCord, that provides commonly used functions
and provides cross version support for a lot of Minecraft features
(some of which are no longer supported or support for them has been added later by Bukkit/Spigot/Paper).

[![Build Status][ciImg]][ci] [![Coverage Status][coverageImg]][coverage] ![Version][versionImg] [![licenseImg]][license]

## Download:
[Build Server][ci]

## Features:
### Bukkit + BungeeCord:
* Config/Language file handler
    * File upgrade support (copy old values into a new file)
    * Comment support
* UUID converter
  * Supports Name -> UUID, UUID -> Name or UUID -> Name changes
  * Supports online and offline mode UUIDs
  * Batch converting and automatic batch size detection (in case Mojang decides to reduce the maximum batch size again)
  * Caching
    * If the lib is running as a Plugin the cache can be shared by all plugins using it
    * Preloads UUIDs from the Minecraft servers UUID cache
* Auto-updater
    * Supports multiple sites for checking and downloading of updates
        * spigotmc.org (checking + downloading (free and hosted on spigotmc.org))
        * dev.bukkit.org (checking + downloading)
        * GitHub (checking + downloading)
        * Jenkins build servers (checking + downloading)
        * Static URL (downloading)
* JSON message handling
* Collections of useful functions
* Option to share database connection pool between multiple plugins

### Bukkit only:
* Particles handler
* Item-Stack serializer
* Material name resolver
* Late registrable commands

## Plugins using it:
* [Marriage Master](https://www.spigotmc.org/resources/19273/) (V2.0 and newer)
* [Minepacks](https://www.spigotmc.org/resources/19286/) (V2.0 and newer)

## Requirements:
* Java 8 (or newer)
* Bukkit, Spigot, Paper, Uranium for MC 1.7 or newer or BungeeCord for MC 1.8 or newer
* (Optional) PlaceholderAPI (to use it with the provided message API)

## Adding it to your plugin:
The library can be added in two ways to your plugin.
1. Requiring it to be installed as a plugin (might be published on dev.bukkit.org and spigotmc.org at some point)
2. Shading it into your plugin (requires more RAM and some features will not work)

### Adding the library as a dependency with Maven:
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
 	<artifactId>PluginLib</artifactId>
 	<version>1.0.39.4-SNAPSHOT</version><!-- Check version shield for newest version -->
</dependency>
```

### Requiring the library to be installed as a standalone plugin:
Add `PCGF_PluginLib` as a dependency for your Bukkit/Spigot or BungeeCord plugin.
The users will have to install the library as a plugin. Download: [https://ci.pcgamingfreaks.at/job/PluginLib/][ci]

### Shading the library into your plugin with maven:
By adding the library to your plugin through shading it will require more memory (if multiple plugins using the library are installed) and some features (DB connection sharing, translation sharing) will not be available.
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
                <minimizeJar>false</minimizeJar><!-- Do not minimize or some of the utils will not work on Bukkit -->
                <artifactSet>
                    <includes>
                        <include>at.pcgamingfreaks:PluginLib</include>
                    </includes>
                </artifactSet>
                <relocations>
                    <!-- Relocate the lib to prevent conflicts with other plugins using it -->
                    <relocation>
                        <pattern>at.pcgamingfreaks</pattern>
                        <shadedPattern>your_package.libs.at.pcgamingfreaks</shadedPattern>
                    </relocation>
                </relocations>
            </configuration>
        </execution>
    </executions>
</plugin>
```
Replace `your_package` with the package of your plugin.  
**Do not shade this library into your plugin without relocation!!!**

## Building from source:
### Requirements:
* Java JDK
  * Min: 8
  * Recommended: 21
    * Acceleration for Minecraft 1.17 can only be built with Java >= 16
    * Acceleration for Minecraft 1.18 can only be built with Java >= 17
    * Acceleration for Minecraft 1.20.5 can only be built with Java >= 21
* Maven >= 3.6.3
* git

### Building:
```bash
git clone https://github.com/GeorgH93/PCGF_PluginLib
cd PCGF_PluginLib
mvn package
```

## Links:
* [Build Server ![Build Status][ciImg]][ci]
* [Code Coverage ![Coverage Status][coverageImg]][coverage]
* [Javadoc][javadoc]
* Spigot (TBA)
* Dev Bukkit (TBA)
