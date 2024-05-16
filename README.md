# Luafy
Adding the Lua Scripting Language to Minecraft datapacks, entirely server sided.

## What?
Lua is a easy, simple to use and easy to learn scripting language used by many games to add custom content to games. Luafy is a mod that adds that to Minecraft datapacks, entirely server sided.

For more information on the mod and how to use, please read the Documentation.

## Disclaimer
Lua is a powerful language, and in its current state there is NO sandboxing in the Luafy Lua Engine. Please excercise common internet safety, and do not run any datapacks containing Lua code that you do not trust. I am not responsible for any content this mod allows to run.

### What now? (plans)
- Add performant & easy to use APIs for adding server-side features to the game. (eg. `set_motion` that works on all entities including players)
- Abstract the API from Lua to allow for other languages! (see [#1](https://github.com/diamonddevv/luafy/issues/1) & [#2](https://github.com/diamonddevv/luafy/issues/2))
- Research an integration with [Patbox/polymer](https://github.com/Patbox/polymer)! (see [#3](https://github.com/diamonddevv/luafy/issues/3))
- Create an optional client mod!

### Why? (Yapping)
<details>
  <summary>Click to expand</summary>
  I've always loved the amount of extensibility datapacks give to Minecraft through the use of Functions, but I never really liked mcfunctions that much. Where in recent updates to the game they have become more and more powerful, I've always hoped for (since my own days of making datapacks before I learned to mod Minecraft) some form of complete scripting. Recently, within personal and unrelated projects I have been fiddling about with Lua, a programming language that is powerful, easy to learn and easy to integrate. Lua is commonly used within games such as Roblox or Garry's Mod to add player-generated content (including in the case of why I have been using it; I have spent the last few months working with Lua in C# using a library called Moonsharp. Like c'mon.. I just wanna make a game, but I gotta spend months hacking in file management). I figured that it would be a truly worthwhile candidate to put in Minecraft. In fact, I'm not the first to this idea: the popular mod Figura has used Lua to add support for scripting their avatar system, and Luafy actually makes use of the same libary to add Lua (Thank you FiguraMC for maintaining LuaJ!). Instead of waiting for Mojang to add a Scripting language to Minecraft other than MCFUNCTION, I did what Thanos suggested, and did it myself.
</details>

