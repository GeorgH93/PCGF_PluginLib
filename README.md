# Bukkit/BungeeCord PluginLib
A small library to reduce the code for some stuff commonly used in plugins.

## Features:
### Bukkit + BungeeCord:
* Config/Language file handler
    * File upgrade support (fill old values in new file)
    * Comment support
* UUID converter
* Auto-updater
    * Supports multiple sites for checking and downloading of updates
        * spigotmc.org (checking + downloading)
        * dev.bukkit.org (checking + downloading)
        * Jenkins build servers (checking + downloading)
        * Static URL (downloading)
* JSON message handling
* Collections of useful functions
* Option to share database connection pool between multiple plugins

### Bukkit only:
* Particles handler
* Item-Stack serializer
* Material name handler
* Late registerable commands

## Plugins using it:
* [Marriage Master](https://www.spigotmc.org/resources/19273/) (V2.0 and newer)
* [Minepacks](https://www.spigotmc.org/resources/19286/) (V2.0 and newer)

## Links:
* [Build Server ![Build Status](https://ci.pcgamingfreaks.at/job/PluginLib/badge/icon)](https://ci.pcgamingfreaks.at/job/PluginLib/)
* [Code Coverage ![Coverage Status](https://coveralls.io/repos/github/GeorgH93/Bukkit_Bungee_PluginLib/badge.svg)](https://coveralls.io/github/GeorgH93/Bukkit_Bungee_PluginLib)
* [Javadoc](https://ci.pcgamingfreaks.at/job/PluginLib/javadoc/)
* Spigot
* Dev Bukkit

## Adding it to your plugin
The library can be added in two ways to your plugin.
1. Requiring it to be installed as a plugin (will be published on dev.bukkit.org and spigotmc.org soon)
2. Shading it into your plugin (requires more RAM and some features will not work)

### Adding the library as an dependency with maven
#### Repository:
```xml
<repository>
	<id>pcgf-repo</id>
	<url>https://repo.pcgamingfreaks.at/repository/everything</url>
</repository>
```

#### Dependency:
```xml
<dependency>
 	<groupId>at.pcgamingfreaks</groupId>
 	<artifactId>PluginLib</artifactId>
 	<version>1.0.1-SNAPSHOT</version>
</dependency>
```

### Shading the library into your plugin
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.1.0</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
                <createDependencyReducedPom>false</createDependencyReducedPom>
                <minimizeJar>true</minimizeJar>
                <artifactSet>
                    <includes>
                        <include>at.pcgamingfreaks:PluginLib</include>
                    </includes>
                </artifactSet>
                <relocations>
                    <relocation>
                        <pattern>at.pcgamingfreaks</pattern>
                        <shadedPattern>at.pcgamingfreaks.MinePacks.libs.at.pcgamingfreaks</shadedPattern>
                    </relocation>
                </relocations>
            </configuration>
        </execution>
    </executions>
</plugin>
```
Do not shade this library into your plugin without relocation!!!