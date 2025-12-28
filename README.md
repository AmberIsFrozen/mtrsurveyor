# MTR: Surveyor Integration

This mod automatically adds stations & depots landmarks from [Minecraft Transit Railway 4](https://github.com/Minecraft-Transit-Railway/Minecraft-Transit-Railway) to the [Surveyor Map Framework](https://github.com/sisby-folk/surveyor).

This enables mods utilizing **Surveyor** (e.g. Hoofprint) to display such landmarks on the map.

Required on **server-side**, installable on **client-side** icon for custom ItemStack icons, as well as stylized marker for [Antique Atlas 4](https://modrinth.com/mod/antique-atlas-4).

![A Minecraft map displayed in Hoofprint, a minecraft map mod, along with MTR stations/depots landmarks/markers](./assets/preview.png)

## Sinytra Connector Compatibility
![A fox emoji saying yes!](./assets/sinytra_yes.png)

This should work with **Sinytra Connector** on Forge/NeoForge, provided you have the following mods installed:
- Sinytra Connector
- Forgified Fabric API / FFAPI
- Minecraft Transit Railway 4.x (Forge)
- - Note: No need to run the Fabric version of MTR via connector, just use the Forge build!

## Configuring
### McQoy (Client-only)
For those who want a GUI config, this mod works with [McQoy](https://modrinth.com/mod/mcqoy) to provide a configuration screen with Mod Menu.  
All changes will apply immediately after saving following the GUI.

### In-Game Commands
You can configure and perform landmarks-related actions with in-game commands (For OP 4 only)  
Configuration change will apply immediately.

#### Available commands (Syntax: \<Required argument\>, (optional argument))

- `/mtrsurveyor config enabled (false/true)` - Query or set whether the mod should be enabled. **Note: Setting this to false would automatically remove all landmark, and vice-versa.**
- `/mtrsurveyor config addDepotLandmarks (false/true)` - Query or set whether depot landmarks should be added to the map.
- `/mtrsurveyor config addStationLandmarks (false/true)` - Query or set whether station landmarks should be added to the map.
- `/mtrsurveyor config showHiddenRoute (false/true)` - Query or set whether hidden routes in MTR should be appended to the station description.
- `/mtrsurveyor config showEmptyStation (false/true)` - Query or set whether empty stations (i.e. with no routes) should be added to the map.
- `/mtrsurveyor syncLandmarks (world id)` - Sync & update all landmarks for the specified world

### Manual (TOML Editing)
The config file is located under `.minecraft/config/mtrsurveyor.toml`.  
You would have to restart your game afterwords for the changes to apply

## Bugs/Suggestions
If you have any suggestions or bug report, don't hesitate to open an GitHub issue [here](https://github.com/AmberIsFrozen/mtrsurveyor/issues)!

## License
This project is licensed under the MIT License.