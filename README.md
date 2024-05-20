# FishingRewards
A Minecraft 1.20 Plugin using Spigot API to customize fishing rewards.

## Features
- Two Drop Modes: Weight / Roll
- Item & Entity Reward Customization
- Item/Entity Protection
- Item Auto Clear
- Catch Conditions
- World Guard Region Support
- Dynamic Catch Chances

## Modes
- Weight: Weight: Every drop's weight/chance will get added to a list and a number will be randomly generated from 0-[Total Weight] which will decide the reward. For example, if you had 2 items in the loot table, one with 60 weight and another with 40 weight, they would have 60% & 40% drop chance respectively.


- Roll: Drops will cycle from the lowest chance to highest until the player rolls an item. (Chance should not be higher than 100). For example, if you had 3 items with 1%, 10%, 100% chance, and the player rolls a 9, the 10% item will drop, but if he rolls an 11, the 100% item will drop.

## Reward Customization
- Enchanting XP on catch
- Command execution on catch
- Player Chat Message on catch
### Items
- Custom Name/Lore
- In-depth Enchantment rolling system
- Durability
- ItemFlags
- Unbreakable Items
- Model Data
- Consumed on catch
- Attribute Modifiers
### Entities
- Name
- Attributes
- Drops
- Equipment

## Conditions
- World Guard Regions
- Biomes
- Permission
- Ingame Time
- Weather

# Planned Features
- New reward type: Money
- More Entity Customization
- NBT tag support
- More Drop Modes
- Ingame Reward Creator
