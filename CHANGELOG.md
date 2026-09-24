# 1.0.9
- Micro-optimization for the Poppy of Demeter, now checks for nearby blocks once per second if the poppy is idling
- Fixed a case where the Aphrodite's Lyre keeps playing the music after a slot change while using it

# 1.0.8
- Changed the modid from olympus to olympusmythology to avoid clashing with a private library with the same id (crashing on load with mods like tempad)
- Things should be migrated automatically, but existing generated structures may fail (it's better to do this now before the mod becomes more popular)
- Sorry for the inconvenient, I tried my best. Remember to save your world before uploading

# 1.0.7
- Fixed a case where the Hades Invisibility status effect screen filter wouldn't compute transparency when using Iris Shaders

# 1.0.6
- Moved armor rendering call, potentially fixing wrong transforms when using external mods that tweak bone mutations (like the resourcepack Fresh Animations: Player Extension)

# 1.0.5
- Added configurability to the Celestial Parthenon structure spawning, such as permitted biomes, dimensions, % of spawning, structure separation, etc.
- Fixed a case where the Spear of Ares hand transforms could potentially conflict with other mods

# 1.0.4
- Added extra config options to enable/disable item abilities at will
- Decreased min default spear of ares ground ability height from 3 to 2.5
- Persephone's Cup now empties by default when preventing a mortal blow

# 1.0.3
- Increased Harpy melee attack range

# 1.0.2
- Fixed Poppy of Demeter item name using a wrong translation key

# 1.0.1
- Added missing tooltip to the Parthenon Key item
- Added 3 additional config entries to configure the Parthenon Spawner harpy spawn count

# 1.0.0
- Added a config entry to modify the Hermes' Sandals max airborne jump amount
- No longer requiring geckolib to launch the mod
- Increased Artemis's Bow durability to 2000
- Added better combat compat for the spear of ares
- Fixed harpy projectile using a corrupted geo model
- Fixed a case where the ribbon trail of some projectiles wasn't properly centered
- Fixed harpy feathers spawning in a wrong position when dashing