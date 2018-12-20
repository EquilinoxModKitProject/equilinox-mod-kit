## Equilinox Mod Kit
**The Equilinox Mod Kit is an open source modding API for [Equilinox](https://www.equilinox.com).** It is part of the Equilinox Mod Kit Project that also maintains the Equilinox Mod Loader, the official EMK wiki, documentation and website, as well as other Equilinox modding related repositories here on GitHub.

*The Equilinox Mod Kit is currently in beta testing. Snapshot releases are available on [Discord](https://discord.gg/mnEUuap).*

---

#### Making a Mod
Download the latest Equilinox Mod Kit jars [here](https://github.com/EquilinoxModKit/Equilinox-Mod-Kit/releases) and add them as libraries to your project. Create a class that extends `EquilinoxMod` *(+link)* and has a valid `ModInfo` *(+link)* annotation. If your mod is dependant on other mods, specify that with the annotation `Dependency` *(+link)*. Within the `preInit` method, specify your mixin configuration file and declare events listeners. Within the `init` method, load resources like textures and sounds. When exporting your mod make sure the Equilinox Mod Kit is not included in the mod jar.
Read through the introduction series *(+link)* or look at some code examples *(link)* to learn more.

---

#### Installing a Mod
If you have the [Equilinox Mod Loader](https://github.com/EquilinoxModKit/Equilinox-Mod-Loader) installed already, it's as easy as drag and dropping a mod jar into the `mods` folder located in the Equilinox installation directory.

---

#### Contributing
If you found a [bug](https://github.com/EquilinoxModKit/Equilinox-Mod-Kit/issues/new?template=bug_report.md) or have a [feature request](https://github.com/EquilinoxModKit/Equilinox-Mod-Kit/issues/new?template=feature_request.md), feel free to create a ticket. You want to help by contributing code? Check out our [Contributing Guidelines](https://github.com/EquilinoxModKit/Equilinox-Mod-Kit/blob/master/contributing.md) for detailed instructions

---

#### Links
 * [Releases](https://github.com/EquilinoxModKit/Equilinox-Mod-Kit/releases)
 * [Wiki](https://github.com/EquilinoxModKit/Equilinox-Mod-Kit/wiki)
 * [Equilinox Mod Loader](https://github.com/EquilinoxModKit/Equilinox-Mod-Loader)
 * [Website](https://equilinoxmodkit.github.io)
 * [Discord](https://discord.gg/7emp5QA)
