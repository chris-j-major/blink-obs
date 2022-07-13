# Blink1 - OBS connector
This project provides a simple application that allows
a [`blink(1)`](https://blink1.thingm.com/) to show the status 
of [OBS](https://obsproject.com/).

To achieve this, it uses a websocket connection to OBS to monitor
the current state, and updates the `blink(1)` based on what's happening.

## Usage
1. Install OBS, and the [Remote-control OBS Studio from WebSockets 4.9.1](https://obsproject.com/forum/resources/obs-websocket-remote-control-obs-studio-from-websockets.466/)
extension
2. Plug in an `blink(1)`
3. Run the program (optionally specifying configuration via a json file
referenced on the command line)

When in use a "SystemTray" icon will be shown indicating the current state
of the light. This will fucntion even if no `blink(1)` is avalaible.

## Build from Source
This project uses gradle to build and run.

### build
```bash
./gradlew build shadowJar 
```
will produce a jar file in `build/libs/blink-obs-1.0-SNAPSHOT-all.jar`

## Configuration
To customise the configuration create a json file and specify the path
as the first argument when starting the jar file:
```bash
java -jar build/libs/blink-obs-1.0-SNAPSHOT-all.jar config.json
```
and example can be found in `test.json` in the root.

Example configuration file:
```json
{
  "obs": {
    "obsWsUrl": "ws://localhost:4444",
    "obsWsPassword": "password",
    "debug": false
  },
  "light": {
    "id": "",
    "connectAny": true
  },
  "scenes": {
    "known": [
      {
          "regex": "Song",
          "lights": "#ff0000:50,#000000:150"
      },
      {
          "regex": "Camera",
          "lights": "#ffffff"
      }
    ],
    "shouldBeRecording": false,
    "shouldBeStreaming": false
  }
}
```
### `obs` settings control connection to OBS
* `obsWsUrl` - the url to the web socket from OBS - only needs to be
changed if OBS is elsewhere on the network.
* `osbWsPassword` - the password used to connect to OBS
* `debug` - when true provides extra logging about OBS connection
### `light` settings control connections to the `\`blink(1)`
* `connectAny` - 'true' indicates that it should connect to any `blink(1)`
device, which is the default and sensible when only one is used.
* `id` - specifies the ID of the `blink(1)` to use - allows the programme
to be run multiple times with different configs to control different lights.
### `scenes` settings control how the OBS state is represented
* `shouldBeRecording` - indicates that BS should be recording.
* `shouldBeStreaming` - indicates that BS should be streaming.
* `known` - indicates scenes that are known, each option defined a
`regex`(Regular Expression) that should match the scene name, and
a `lights` string indicting the Color/Pattern to display.
The following settings take strings indicting the Color/Pattern to display:
* `unknownScene`  when none of the `known` patterns match the scene.
* `noConnection` when not connected to OBS.
* `notStreamingNotRecording` when OBS isn't streaming or recording (but should be)
* `notStreaming` when OBS isn't streaming (but should be)
* `notRecording` when OBS isn't recording (but should be)

### defining colors or patterns
Light displays are described as a string, either with a static color or an
animated patten.

Colors can be specified with a Hex RGB string: `#ff0000` would indicate
full red, and `#88ff00` would indicate orange.

Patterns contain one or more blocks separated by `,` characters, each block
has at least a color, and then following sections separated by `:` specifying
the duration (in ms, defaults to 100ms), and which LED to use (only available
on some devices, default to 'all'). For example `#ff0000:50,#000000:150` will
flash red for 50ms, and then black for 150ms - making a brief flash.

## 