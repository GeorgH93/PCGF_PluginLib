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