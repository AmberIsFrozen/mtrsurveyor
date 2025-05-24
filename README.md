# MTR: Surveyor Integration

This mod automatically adds stations & depots landmarks from [Minecraft Transit Railway 4](https://github.com/Minecraft-Transit-Railway/Minecraft-Transit-Railway) to the [Surveyor Map Framework](https://github.com/sisby-folk/surveyor).

This enables mods utilizing **Surveyor** (e.g. Hoofprint) to display such landmarks on the map.

![A Minecraft map displayed in Hoofprint, a minecraft map mod, along with MTR stations/depots landmarks/markers](./assets/preview.png)

## Config
The config file is located under `.minecraft/config/mtrsurveyor.json`.

## Commands
- `/mtrsurveyor reload` - Reload the config from `.minecraft/config/mtrsurveyor.json`.
- `/mtrsurveyor clear <world id>` - Clear all landmarks for the specified world (Note: Landmarks will appear again when MTR stations/depots changed, disable automatic sync in config if that's not desired)
- `/mtrsurveyor sync <world id>` - Sync & update all landmarks for the specified world

## Known-issue
Existing landmarks are not updated when a change occurs.

## License
This project is licensed under the MIT License.