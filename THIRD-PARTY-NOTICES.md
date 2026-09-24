# Third-party notices

Winefox Maid Butcher is an unofficial addon. It **does not redistribute** any third-party code or
asset: the prerequisite mods below are only referenced as compile-time dependencies (the jars live
in the local `libs/` folder, which is not committed to this repository). All rights to those
projects remain with their respective authors.

## Touhou Little Maid

- **Used for**: the maid entity framework, the maid AI / brain extension points, the bedrock and
  gecko model pipeline, the voice pack system and the model pool used by villager conversion.
- **License**: MIT (code) / CC BY-NC-SA 4.0 (assets) — dual-licensed, as declared by the project.
- **Authors**: TartaricAcid, Snownee, Succinum, Pajinyi, Zhi_Ban, CrystalizedSun, Foky, ZeniCrow,
  Paulzzh, Yuriscat, Lappland162.
- **Credits**: Verclene, ZUN.
- **Upstream**: https://github.com/TartaricAcid/TouhouLittleMaid

## Butchercraft

- **Used for**: the butchering framework — meat hooks, carcasses, the butcher block and the
  processing step definitions that this addon hooks into.
- **License**: MIT, as declared in the mod metadata (`license = "MIT"`); the upstream repository
  additionally ships a CC BY-NC-SA 4.0 license file covering its assets.
- **Authors**: Lance5057.
- **Credits**: Alan199921, DivineAspect, Skysom, FreneticScribbler, Lanse505, Belathus, RoyalJelly,
  Robynstar.
- **Upstream**: https://github.com/Lance5057/Butchercraft

## GeckoLib

- **Used for**: the animated block and item model renderer (geo models, animations and item renderers).
- **License**: MIT — Copyright (c) 2026 GeckoLib.
- **Authors**: Gecko, Eliot, AzureDoom, DerToaster, Tslat, Witixin.
- **Upstream**: https://github.com/bernie-g/geckolib

## Farmer's Delight

- **Used for**: the cooking pot and feast block conventions, plus the optional cooking recipes of
  this addon.
- **License**: MIT — Copyright (c) 2020 vectorwing.
- **Authors**: vectorwing.
- **Upstream**: https://github.com/vectorwing/FarmersDelight

## Love & Loathing (`callresponse`)

- **Used for**: the optional grave compatibility branch (cancelling grave creation for butchered
  winefox maids). It is detected at runtime with `ModList.isLoaded`, so no code of this project
  links against it at compile time.
- **License**: as declared by its upstream project.
- **Upstream**: https://github.com/Lance5057/LoveAndLoathing

## Notes on derived assets

The textures, models and animations shipped in `src/main/resources` were authored for this addon.
They follow the visual style of the maid models from Touhou Little Maid, whose assets are licensed
CC BY-NC-SA 4.0; that license is therefore also applied to the assets of this project (see
[`LICENSE-CC`](LICENSE-CC)).
