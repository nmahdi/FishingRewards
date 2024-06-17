package com.fishingrewards.rewards;

import org.bukkit.attribute.Attribute;

public enum RewardAttribute {
    MaxHealth("health", "GENERIC_MAX_HEALTH"),
    FollowRange("follow-range", "GENERIC_FOLLOW_RANGE"),
    KnockbackResistance("knockback-resistance", "GENERIC_KNOCKBACK_RESISTANCE"),
    MovementSpeed("speed", "GENERIC_MOVEMENT_SPEED"),
    FlyingSpeed("flying-speed", "GENERIC_FLYING_SPEED"),
    AttackDamage("damage", "GENERIC_ATTACK_DAMAGE"),
    AttackSpeed("attack-speed", "GENERIC_ATTACK_SPEED"),
    Armor("armor", "GENERIC_ARMOR"),
    ArmorToughness("armor-toughness", "GENERIC_ARMOR_TOUGHNESS"),
    Luck("luck", "GENERIC_LUCK"),
    MaxAbsorption("absorption", "GENERIC_MAX_ABSORPTION"),
    //Horse
    HorseJump("horse-jump", "HORSE_JUMP_STRENGTH"),
    //Zombie
    ZombieSpawnReinforcements("zombie-reinforcement", "ZOMBIE_SPAWN_REINFORCEMENTS");

    private final String configID;
    private final Attribute attribute;

    RewardAttribute(String configID, String attribute){
        this.configID = configID;
        this.attribute = Attribute.valueOf(attribute);
    }

    public String getConfigID() {
        return configID;
    }

    public Attribute getAttribute() {
        return attribute;
    }
}
