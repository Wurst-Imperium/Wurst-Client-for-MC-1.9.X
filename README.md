# Wurst Client ![downloads counter](https://drive.google.com/uc?id=0B2YeSS9tm5zLMF9NWjNZYnNqSTA)

## About

Wurst is a so-called "hacked client" for Minecraft - basically a mod that allows you to cheat. You can fly on servers, see ores through walls and much more.

## Installation

### For Users

If you just want to use Wurst, [download it](https://www.wurstclient.net/download/) from the official website and [install it](https://www.wurstclient.net/how-to-install/) on your computer.

### For Developers (Windows only)

If you want to edit Wurst and submit Pull Requests, follow these steps:

1. Fork and clone this repository.

2. Import it to Eclipse (Import... > Existing Projects into Workspace).

3. Decompile Minecraft using the [MCP](http://www.modcoderpack.com/website/releases), create a folder named `mc` next to `src` and put the source code in there.

4. Go to the [`patch` folder](/patch) and run `initialize.bat` (requires git to be installed and added to the path so that the script can use it).

5. Create a `lib` folder next to `src`, then add Minecraft's libraries to it.

All errors should have disappeared by now and you should be able to launch Wurst from the Eclipse project. If not, something is wrong.

## Contributing

Pull Requests are welcome! Just make sure that you submit a separate one for each new feature or bugfix that you make. You know, things get a bit messy otherwise...

## License

The Wurst Client is licensed under the [Mozilla Public License 2.0](/LICENSE.txt).
