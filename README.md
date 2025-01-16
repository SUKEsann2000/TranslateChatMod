**Translate Chat mod!**
<img src="https://img.shields.io/badge/Forge%20Gradle-Java-007396.svg?logo=Java&style=for-the-badge">
---

How to use
---
1. Install .jar file for your minecraft and forge version
2. Add mods folder
3. Finish🌟 You can use this mod!

What happens when you use
---
0. Load configuration file (default: .minecraft/<YOUR_INSTANCE\>/config/translatechat.json)


You can add server configuration

## Settings

- **Fetch URL**
  - **Description**: The URL used to fetch translation data.
  - **Default**: `https://script.google.com/macros/s/AKfycbxd0Z5iavmXxdxdtn71VYftLvIBzCjmE2NuxUSZw24z-JuYjuOf-FO3B922MBW3D_Y/exec?`
  - **Example**: 
    ```plaintext
    fetchURL = "https://script.google.com/macros/s/AKfycbxd0Z5iavmXxdxdtn71VYftLvIBzCjmE2NuxUSZw24z-JuYjuOf-FO3B922MBW3D_Y/exec?"
    ```

- **Fetch Text Type**
  - **Description**: The type of text data sent for translation.
  - **Default**: `text=`
  - **Example**: 
    ```plaintext
    fetchTextType = "text="
    ```

- **Fetch Target Type**
  - **Description**: The target language type to which the text will be translated.
  - **Default**: `target=`
  - **Example**: 
    ```plaintext
    fetchTargetType = "target="
    ```

- **Debug Mode**
  - **Description**: Enables or disables debug mode for troubleshooting and logging purposes.
  - **Default**: `false`
  - **Example**: 
    ```plaintext
    debug = "true"
    ```

- **Fetch JSON Key**
  - **Description**: The key used to extract the text from the fetched JSON response.
  - **Default**: `text`
  - **Example**: 
    ```plaintext
    fetchKey = "text"
    ```

- **Player Name Start Index Of**
  - **Description**: Defines the starting index or character where the player's name begins.
  - **Default**: `>`
  - **Example**: 
    ```plaintext
    playerNameIndexOf = ">"
    ```

These settings allow for flexible configuration of the translation fetch process. Adjust the parameters to fit your specific requirements and ensure optimal performance of the translation feature.

1. Receive "ClientChatReceivedEvent"
2. Get message and clear player name (Example: "<dev\> Hello! world! → "Hello! world!)
* You can also change the setting of player name index<br>
For example (translatechat.json):
```translatechat.json
{
  "general": {
    "debug": "false",
    "playerNameIndexOf": "\u003e",
    "enable": "true",
    "fetchURL": "https://script.google.com/macros/s/AKfycbxd0Z5iavmXxdxdtn71VYftLvIBzCjmE2NuxUSZw24z-JuYjuOf-FO3B922MBW3D_Y/exec?",
    "fetchTextType": "text\u003d",
    "fetchKey": "text",
    "fetchTargetType": "target\u003d"
  },
  "mc.hypixel.net": {
    "enable": "true",
    "playerNameIndexOf": ":"
  }
}

```
When you use this configuration, your message in mc.hypixel.net will be ↓<br>
(dev: Hello world→Hello world)
<br>

3. Fetch json from server (my Google Apps Script)
4. Change message

Source installation information for modders
-------------------------------------------
This code follows the Minecraft Forge installation methodology. It will apply
some small patches to the vanilla MCP source code, giving you and it access 
to some of the data and functions you need to build a successful mod.

Note also that the patches are built against "un-renamed" MCP source code (aka
SRG Names) - this means that you will not be able to read them directly against
normal code.

Setup Process:
==============================

Step 1: Open your command-line and browse to the folder where you extracted the zip file.

Step 2: You're left with a choice.
If you prefer to use Eclipse:
1. Run the following command: `./gradlew genEclipseRuns`
2. Open Eclipse, Import > Existing Gradle Project > Select Folder 
   or run `gradlew eclipse` to generate the project.

If you prefer to use IntelliJ:
1. Open IDEA, and import project.
2. Select your build.gradle file and have it import.
3. Run the following command: `./gradlew genIntellijRuns`
4. Refresh the Gradle Project in IDEA if required.

If at any point you are missing libraries in your IDE, or you've run into problems you can 
run `gradlew --refresh-dependencies` to refresh the local cache. `gradlew clean` to reset everything 
{this does not affect your code} and then start the process again.

Mapping Names:
=============================
By default, the MDK is configured to use the official mapping names from Mojang for methods and fields 
in the Minecraft codebase. These names are covered by a specific license. All modders should be aware of this
license, if you do not agree with it you can change your mapping names to other crowdsourced names in your 
build.gradle. For the latest license text, refer to the mapping file itself, or the reference copy here:
https://github.com/MinecraftForge/MCPConfig/blob/master/Mojang.md

Additional Resources: 
=========================
Community Documentation: https://docs.minecraftforge.net/en/1.19.2/gettingstarted/
LexManos' Install Video: https://youtu.be/8VEdtQLuLO0
Forge Forums: https://forums.minecraftforge.net/
Forge Discord: https://discord.minecraftforge.net/

**Welcome your Pull Requests and Issues!!**