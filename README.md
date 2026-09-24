# Winefox Maid Butcher

<p align="center">A Minecraft 1.20.1 Forge addon that links Touhou Little Maid, Butchercraft and GeckoLib around the winefox maid.</p>

<p align="center">
    <img src="https://img.shields.io/badge/license-MIT%20%7C%20CC%20BY--NC--SA%204.0-green" alt="License">
    <img src="https://img.shields.io/badge/Minecraft-1.20.1-blue" alt="Minecraft 1.20.1">
    <img src="https://img.shields.io/badge/Forge-47.x-orange" alt="Forge 47.x">
    <img src="https://img.shields.io/badge/version-0.10-informational" alt="version 0.10">
</p>

<hr>

## Overview

**Winefox Maid Butcher** is an unofficial linkage addon for [Touhou Little Maid](https://github.com/TartaricAcid/TouhouLittleMaid) and [Butchercraft](https://github.com/Lance5057/Butchercraft). It turns the winefox maid into both a butchering subject and a cooking ingredient: hunt her down, put the carcass on a meat hook, work through the whole processing chain, and cook the results — including the two-block **roast fox feast** rendered with GeckoLib and animated in six eating stages. On top of that the mod adds a permanent maid breeding expansion, model and voice genes, baby growth, and villager-to-maid conversion.

> **Butchering is for adults only.** Only adult winefox maids take part in butchering — baby maids are
> excluded, so killing a baby maid never yields a carcass, tail or raw winefox meat.

## Features

### Butchering

Only **adult** winefox maids are part of the butchering chain: baby maids are excluded, so a butcher
knife on a baby yields nothing — she drops no carcass, no tail and no raw winefox meat.

- Killed with a butcher knife, an **adult** winefox maid drops a carcass instead of a grave.
- The carcass can be hung on a meat hook and processed step by step: **bleeding → skinning → bone sawing → disembowelling → butcher knife cutting**.
- Outputs include winefox meat, bones, heart, head and sinew, with separate loot tables for every processing step and for bisecting / skinning routes.
- The maid can be assigned the **butcher task**: she walks up to a carcass, hangs it on a meat hook and processes it with the knife by herself.

### Winefox cuisine

- The full winefox meat line: raw / cooked meat, cubes, mince, patties, burgers, barbecue sticks, meat pot, stew, fried rice, pasta, sushi and sushi rolls.
- **Farmer's Delight integration** (soft dependency): cooking pot recipes for fried rice, meat pot and more; when Farmer's Delight is absent these recipes and blocks are skipped at load time.
- Sneak + right click with a winefox dish on a tamed maid raises her affection, and the eating animation and sound are played.

### Roast fox feast

- A two-block, bed-style feast block rendered by GeckoLib, rotating with the player's facing direction.
- Cake-style eating: right click up to **5 times** (hunger +6, saturation +4.4, 5 minutes of Nourishment each time); the model advances through stages 0–5.
- The 6th interaction takes the winefox head and returns the cutting board.
- Cooking pot recipe: 2 carrots + 2 potatoes + 1 onion + 1 winefox carcass, container is the cutting board, cooking time 230 ticks.

### Soul gear

- **Soul amulet** — channeled through an altar, doubles the holder's attributes.
- **Soul crystal sword** — area-of-effect lightning strike.
- **Spirit essence** — obtained from the grinder.

### Equipment and decoration

- Winefox fur armour (helmet, chestplate, leggings, boots) plus a **reinforced** set that upgrades itself when the wearer is struck by lightning.
- Winefox fur bed, winefox tail, and the **winefox head** block — a static 3D GeckoLib model that can be placed on the floor or on a wall.
- Soul crystal sword, soul amulet and spirit essence items with their own recipes.

### Maid breeding expansion

- Breeding is a **permanent feature, not a work mode**: any adult maid that has been fed into the courtship state looks for a partner, whatever work mode she is in.
- **Model and voice genes** with 90% inheritance / 5% global model mutation / 5% hostile mutation; genes are synced to the client for rendering and sound.
- Babies grow up over time with scaled rendering, and feeding a baby speeds up its growth.
- **Villager conversion**: villagers can be rendered as maids with the full maid voice set, including bedrocks and gecko models.

### Compatibility

- **Love & Loathing / callresponse** (soft dependency): grave creation for butchered winefox maids is cancelled; detection is done at runtime through `ModList.isLoaded`, so no hard reference is required.

## Requirements

| Mod | Version range | Mandatory |
|---|---|---|
| Forge | `[47,)` | yes |
| Minecraft | `[1.20.1,1.21)` | yes |
| [Touhou Little Maid](https://github.com/TartaricAcid/TouhouLittleMaid) | `[1.5.3,1.6)` | yes |
| [Butchercraft](https://github.com/Lance5057/Butchercraft) | `[2.4.1,3)` | yes |
| [GeckoLib](https://github.com/bernie-g/geckolib) | `[4.8.4,5)` | yes |
| [Farmer's Delight](https://github.com/vectorwing/FarmersDelight) | `[1.3.2,2)` | no (cooking recipes) |
| [Love & Loathing](https://github.com/Lance5057/LoveAndLoathing) (`callresponse`) | `[2.0.4,3)` | no (grave compat) |

## Installation

1. Install Minecraft 1.20.1 with Forge 47.x.
2. Put the mandatory dependencies above into your `mods/` folder.
3. Drop `winefox_butcher-0.10.jar` into the same `mods/` folder.
4. Launch the game. Only the client and the integrated server are supported targets; dedicated servers work as well since all mandatory dependencies are server-safe.

## Building from source

Requirements: **JDK 17** and the Gradle wrapper that ships with the repository (Gradle 7.6.4).

The build resolves three prerequisite jars from `libs/` through a `flatDir` repository, so place them there first using the `artifact-version.jar` naming convention:

```
libs/butchercraft-2.4.1.jar
libs/touhoulittlemaid-1.5.3.jar
libs/farmersdelight-1.3.2.jar
libs/geckolib-forge-1.20.1-4.8.4.jar
```

Then:

```bash
# compile only
./gradlew compileJava

# full build (reobfuscated jar lands in build/libs/)
./gradlew build

# run the development client
./gradlew runClient
```

If your machine has no network access to the Maven repositories, add `--offline` and short HTTP timeouts so ForgeGradle fails fast instead of hanging:

```bash
java -Dorg.gradle.internal.http.connectionTimeout=5000 -Dorg.gradle.internal.http.socketTimeout=5000 \
  -classpath gradle/wrapper/gradle-wrapper.jar \
  org.gradle.wrapper.GradleWrapperMain build --offline
```

## Project structure

```
.
├── build.gradle                 # ForgeGradle build script
├── gradle.properties            # mod / Forge / GeckoLib version properties
├── settings.gradle              # root project configuration
├── gradle/wrapper/              # Gradle wrapper (7.6.4)
├── libs/                        # prerequisite mod jars (flatDir dependencies, not committed)
├── LICENSE-MIT                  # license for the source code
├── LICENSE-CC                   # license for the assets
├── THIRD-PARTY-NOTICES.md       # attributions for the prerequisite projects
└── src/main/
    ├── java/com/yourname/maidfox/   # mod sources (butchering, cuisine, expansion, client renderers)
    └── resources/                   # assets (models, textures, geo, animations, lang) and data (recipes, loot tables, tags)
```

## Credits

- **OPYJJ** — current maintainer and second-hand developer of this project.
- **An anonymous friend** — co-author of the original development. The mod was created together with this friend, who prefers to stay unnamed; their contribution is gratefully acknowledged.
- This repository continues the project from the last released build: the sources were recovered by decompiling `winefox_butcher-1.0.0.jar`, remapping the SRG names through Parchment, and then cleaned up by hand. Since then the project is developed and maintained by OPYJJ.

Special thanks to the upstream projects that make this addon possible:

- **Touhou Little Maid** by TartaricAcid, Snownee, Succinum, Pajinyi, Zhi_Ban, CrystalizedSun, Foky, ZeniCrow, Paulzzh, Yuriscat, Lappland162 — models, animations, voice packs and the maid AI framework.
- **Butchercraft** by Lance5057 and contributors — the butchering framework (meat hooks, grinders, carcasses, processing steps).
- **GeckoLib** by Gecko, Eliot, AzureDoom, DerToaster, Tslat and Witixin — the animated model renderer used by the mod.
- **Farmer's Delight** by vectorwing — the cooking pot and feast block conventions.

## License

This project follows the same dual-licensing scheme as Touhou Little Maid:

| Part | License | File |
|---|---|---|
| Source code | **MIT** | [`LICENSE-MIT`](LICENSE-MIT) |
| Assets (textures, models, sounds) | **CC BY-NC-SA 4.0** | [`LICENSE-CC`](LICENSE-CC) |

In short: you may use and modify the code freely with attribution, while the assets may be shared and adapted for **non-commercial** purposes as long as you credit the project and keep the same license. Third-party attributions are listed in [`THIRD-PARTY-NOTICES.md`](THIRD-PARTY-NOTICES.md).

## Disclaimer

Winefox Maid Butcher is an **unofficial, fan-made addon**. It is not affiliated with, endorsed by, or supported by the authors of Touhou Little Maid, Butchercraft, GeckoLib or Farmer's Delight. All trademarks and assets of those projects belong to their respective owners.
