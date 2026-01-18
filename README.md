# MTR: Surveyor Integration

This mod automatically adds stations & depots landmarks from [Minecraft Transit Railway 4](https://github.com/Minecraft-Transit-Railway/Minecraft-Transit-Railway) to the [Surveyor Map Framework](https://github.com/sisby-folk/surveyor).

This enables mods utilizing **Surveyor** (e.g. Hoofprint) to display such landmarks on the map.

Installable on **server-side** for global landmarks, or **client-side** for nearby landmarks, as well as custom ItemStack icons & stylized marker for [Antique Atlas 4](https://modrinth.com/mod/antique-atlas-4).

![A Minecraft map displayed in Hoofprint, a minecraft map mod, along with MTR stations/depots landmarks/markers](./assets/preview.png)

## Sinytra Connector Compatibility
![An emoji saying yes!](./assets/sinytra_yes.png)

This should work with **Sinytra Connector** on Forge/NeoForge, provided you have the following mods installed:
- Sinytra Connector
- Forgified Fabric API / FFAPI
- Minecraft Transit Railway 4.x (Forge)
- - Note: No need to run the Fabric version of MTR via connector, just use the Forge build!

## Configuring
### McQoy (Client-side)
For those who want a GUI config, you may install [McQoy](https://modrinth.com/mod/mcqoy) to provide a configuration screen with Mod Menu.  
All changes will apply immediately after saving following the GUI.

### QoMC (Server-side)
For dedicated server admins, you may install [QoMC](https://modrinth.com/mod/qomc), which will generate a config command for this mod.

By default, the command is `/mtrsurveyor_config`.  
Configuration change will apply immediately.

### Manual (TOML Editing)
The config file is located under `.minecraft/config/mtrsurveyor.toml`.  
You would have to restart your game afterwords for the changes to apply.

## Bugs/Suggestions
If you have any suggestions or bug report, don't hesitate to open an GitHub issue [here](https://github.com/AmberIsFrozen/mtrsurveyor/issues)!

## License
This project is licensed under the MIT License.