# PlatformPacks - Geyser Extension

Send **different resource packs** to players automatically based on the device they're connecting from. Windows players get one pack, mobile players get another, Xbox players get another.

---
## 💬 Support & Community

Join our Discord server to get help with the extension, report bugs, or suggest new features!

**[Click here to join the Discord!](https://discord.gg/aUfuRZjR9S)**
---

## What it does

When a Bedrock player connects to your server through Geyser, PlatformPacks checks what device they're on and sends the resource pack you've configured for that platform.

**Supported platforms:**

| Platform | Covers |
|---|---|
| `windows` | Windows 10 / Windows 11 (Minecraft for Windows) |
| `mobile` | Android, iPhone / iPad|
| `xbox` | Xbox One, Xbox Series X\|S |
| `playstation` | PlayStation 4, PlayStation 5 |
| `nintendo` | Nintendo Switch |
| `default` | Any other device - also used as a fallback |

---

## Requirements

- Geyser **2.4.0 or newer**
- Java **17 or newer**

---

## Installation

### Step 1 - Add the JAR to Geyser

Copy `platform-packs-1.0.0.jar` into your Geyser extensions folder:

```
plugins/Geyser-Spigot/extensions/
```

> The folder name changes depending on your platform: `Geyser-Fabric`, `Geyser-BungeeCord`, `Geyser-Standalone`, etc. It's always the `extensions/` folder inside your Geyser folder.

### Step 2 - Start your server once

Boot up your server. PlatformPacks will create its config folder automatically:

```
plugins/Geyser-Spigot/extensions/platform-packs/
├── config.yml       ← edit this to set your packs
└── packs/           ← put your .mcpack files in here
```

### Step 3 - Add your resource pack files

Copy your `.mcpack` (or `.zip`) resource pack files into the `packs/` folder:

```
packs/
├── windows_hd.mcpack
├── mobile_ui.mcpack
├── xbox_controller.mcpack
├── ps_controller.mcpack
└── switch_controller.mcpack
```

### Step 4 - Edit config.yml

Open `config.yml` and enter the filename for each platform:

```yaml
packs:
  windows:
    - "windows_hd.mcpack"
  mobile:
    - "mobile_ui.mcpack"
  xbox:
    - "xbox_controller.mcpack"
  playstation:
    - "ps_controller.mcpack"
  nintendo:
    - "switch_controller.mcpack"
  default:
    - "default_pack.mcpack"

debug: false
```

**A few tips:**
- Filenames are **case-sensitive** - `Mobile_UI.mcpack` and `mobile_ui.mcpack` are different.
- You can list **multiple packs** under one platform - they will all be sent, stacking in order.
- Leave a platform out (or set it to `[]`) to send nothing to that platform.
- `default` acts as a **fallback** - if a platform has no specific entry, it gets the default packs instead.

### Step 5 - Restart your server

The extension loads its config on startup, so a full restart is needed to apply changes.

---

## Multiple packs per platform

You can send several packs to the same platform. Just list them one per line:

```yaml
packs:
  windows:
    - "base_pack.mcpack"
    - "windows_hd_addon.mcpack"
```

Packs are applied in order - the last one listed goes on top.

---

## Debug mode

If packs aren't showing up as expected, turn on debug mode to see exactly what's being detected and sent in your server console:

```yaml
debug: true
```

You'll see output like:
```
[PlatformPacks] Steve connecting | DeviceOs=WIN10 → WINDOWS
[PlatformPacks] Registered 'windows_hd.mcpack' for WINDOWS
```

Turn it off again once everything is working.

---

## Troubleshooting

| Problem | Solution |
|---|---|
| Pack isn't being sent | Double-check the filename in `config.yml` matches exactly, including capitalisation |
| `Pack file not found` in console | Make sure the file is inside the `packs/` folder |
| All players get the same pack | Enable `debug: true` and check the console - it will show what platform each player is being detected as |
| Extension doesn't appear to load | Make sure you're on Geyser 2.4.0+ and Java 17+ |
| Config changes aren't taking effect | A full server restart is required - reloading Geyser alone is not enough |

## ⚖️ License

This project is licensed under the **GNU Affero General Public License v3.0 (AGPL-3.0)**.

### What does this mean?
*   **Share Alike**: If you modify this extension, you **must** release your modified version under the same AGPL-3.0 license.
*   **Network Users**: If you run a modified version of this extension on a Minecraft server, you **must** make the source code available to your players/users.
*   **No Loophole**: Unlike standard GPL, the AGPL ensures that "private" server-side modifications must still be shared if the public can interact with them.

For the full license text, see the [LICENSE](LICENSE) file.
