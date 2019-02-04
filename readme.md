<p align="center"><img src="docs/emk_logo.png" alt="EMK Logo" height="100"></p>

---

The Equilinox Mod Kit is an **open source modding API** for the indie game [Equilinox](https://www.equilinox.com) which was developed 
single-handedly by Karl Wimble 
([ThinMatrix](https://equilinox.com/presskit/index.php)). It is part of the Equilinox Mod Kit Project aiming to bring simple and straightforward 
modding support to Equilinox.

If you simply want to add modding support to Equilinox and are not interested in creating mods the Equilinox Mod Kit is the wrong project for you. 
Head over
 to the [Equilinox Mod Loader](https://github.com/EquilinoxModKitProject/Equilinox-Mod-Loader) project for detailed instructions on adding modding 
 support and installing mods.

- [Downloads](https://github.com/EquilinoxModKitProject/Equilinox-Mod-Kit/releases)
- [Wiki](https://github.com/EquilinoxModKitProject/Equilinox-Mod-Kit/wiki)
- [Website](https://equilinoxmodkitproject.github.io)
- [Discord](https://discord.gg/sv5rxQz)

*The Equilinox Mod Kit and Mod Loader currently are in public beta and rough edges are to be expected.*

---

#### Making a mod
Making a mod is as simple as adding the Equilinox Mod Kit as a dependency to your project and creating a class extending `EquilinoxMod`.
```java
public class Mod extends EquilinoxMod {
	@Override
	public void preInit( PreInitializer pInit ) {
	}
	
	@Override
	public void init( Initializer initializer ) {
	}
}
```
All your mod is missing now is the `@ModInfo` annotation.
```java
@ModInfo(
	id = "example.mod",
	name = "Mod",
	version = "0.0.1",
	author = "Example",
	description = "",
	thumbnail = ""
)
```
For more detailed tutorials visit the [Wiki](https://github.com/EquilinoxModKitProject/Equilinox-Mod-Kit/wiki) and for examples take a look at our 
[Example Mods](https://github.com/EquilinoxModKitProject/Example-Mods) repository.

---

#### Contributing
As an open source community project, EMKP is grateful for every helping hand. It can be as simple as 
[reporting a bug](https://github.com/EquilinoxModKitProject/Equilinox-Mod-Kit/issues/new?template=bug_report.md) or 
[requesting a new feature](https://github.com/EquilinoxModKitProject/Equilinox-Mod-Kit/issues/new?template=feature_request.md) that you think is 
crucial for the Equilinox Mod Kit.<br>
If you are more into contributing code read into our 
[Contributing Guidelines](https://github.com/EquilinoxModKitProject/Equilinox-Mod-Kit/blob/master/contributing.md).
