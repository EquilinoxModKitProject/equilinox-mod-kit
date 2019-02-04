## Contributing Guidelines
If you want to support the Equilinox Mod Kit Project please do so at any time by 
[reporting a bug](https://github.com/EquilinoxModKitProject/Equilinox-Mod-Kit/issues/new?template=bug_report.md) or 
[requesting a new feature](https://github.com/EquilinoxModKitProject/Equilinox-Mod-Kit/issues/new?template=feature_request.md) or 
[proposing a 
change](https://github.com/EquilinoxModKitProject/Equilinox-Mod-Kit/issues/new?template=feature_request.md).<br>
If you want to directly contribute to the code source read through the guidelines in this document and head over to our 
[Discord Server](https://discord.gg/sv5rxQz) to coordinate with other contributors.

---

#### Reporting a bug
You can report a bug by using [this](https://github.com/EquilinoxModKitProject/Equilinox-Mod-Kit/issues/new?template=bug_report.md) issue template.<br>
In the report describe the problem you encountered, when it happened and what you were expecting to happen. Add code or screenshots to help us 
understand your problem and in which context it occurred. Finally, add information about your development environment like the version of the 
Equilinox Mod Kit you were using, the version of Equilinox you used to test your mod and any other information you think could help us solve your 
problem.

---

#### Requesting a feature or code change
You can requeste a new feature or propose a change by using 
[this](https://github.com/EquilinoxModKitProject/Equilinox-Mod-Kit/issues/new?template=feature_request.md) issue template.<br>
In the report describe what you would like to see added or changed and how you would do that. Feel free to give code examples or add screenshots to 
help us visualize your idea.

---

#### Contributing code
If you are a contributor already or you want to understand how the Equilinox Mod Kit contribution system works this is the right place to start.

##### Becoming a contributor
Head over to our [Discord Server](https://discord.gg/sv5rxQz) and ask an administrator to assign you the role of 'Contributor' that gives you access 
to special text and voice channels to coordinate with other contributors.<br>
As a contributor you are asked to take part in discussing new concepts regarding the Equilinox Mod Kit and Mod Loader. You also get access to the 
GitHub projects and potentially the status of 'Moderator' on the EMKP website.

##### Rules as a contributor
As to anything in life, there are rules to uphold order. Being a contributor to the Equilinox Mod Kit Project means you are supposed to be able to 
help people that have problems with any of EMKP's projects and show activity on the Discord server.<br>
Before pushing changed code to one of the repository's `contribute` branch (that also is the public beta branch so make sure your push doesn't cause 
too many problems) you are asked to inform other contributors about your changes. Your contributions should be well documented - no empty or 
meaningless commit name or description - and follow either the default Java formatting style or the custom formatting described in this document.<br>
Constantly not following these rules - knowingly or unknowingly - might get your access to the repositories and your Discord role revoked.

##### Formatting
You can either follow these formatting rules or adhere to the default Java convention. All files will be converted to the custom EMKP formatting 
when a `contribute` branch gets merged with `main` to indicate a new public release.

###### Java files
- Naming of classes, fields and methods should follow Java convention. Files should be indented with tabs (tab size: 4).
- Lines longer than 200 characters should be wrapped for readability. Continual indent is 2 tab characters. Javadoc should be wrapped after 150 
characters.
- After package declaration, imports and in between classes two lines should be left free. First and last element of a class should have two free 
lines before/after. Between fields, constructors and methods two lines should be left free.
- Fields, constructors and methods should be ordered from `public` to `private`. `static` fields and methods come before other fields/methods.
- Before braces, there should be no space. Parameters inside braces should be distanced from the braces by one space. Parameters are only separated 
by a comma - no space.
 - Short if-statements or loops should not be kept on one line.

You can download a formatting preset for IntelliJ IDEA [here](https://mega.nz/#!yFkgQS5I!hKL5Rq9KaJljP3gPWRRhv2DWwf6c5Rhd1IQFNL4c4-w).

###### Other files
- Lines longer than 150 characters should be wrapped for readability.<br>
