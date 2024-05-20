package com.fishingrewards.rewards;

import org.bukkit.attribute.Attribute;

public enum RewardAttribute {
    MaxHealth("health", Attribute.GENERIC_MAX_HEALTH),
    FollowRange("follow-range", Attribute.GENERIC_FOLLOW_RANGE),
    KnockbackResistance("knockback-resistance", Attribute.GENERIC_KNOCKBACK_RESISTANCE),
    MovementSpeed("speed", Attribute.GENERIC_MOVEMENT_SPEED),
    FlyingSpeed("flying-speed", Attribute.GENERIC_FLYING_SPEED),
    AttackDamage("damage", Attribute.GENERIC_ATTACK_DAMAGE),
    AttackSpeed("attack-speed", Attribute.GENERIC_ATTACK_SPEED),
    Armor("armor", Attribute.GENERIC_ARMOR),
    ArmorToughness("armor-toughness", Attribute.GENERIC_ARMOR_TOUGHNESS),
    Luck("luck", Attribute.GENERIC_LUCK),
    MaxAbsorption("absorption", Attribute.GENERIC_MAX_ABSORPTION),
    //Horse
    HorseJump("horse-jump", Attribute.HORSE_JUMP_STRENGTH),
    //Zombie
    ZombieSpawnReinforcements("zombie-reinforcement", Attribute.ZOMBIE_SPAWN_REINFORCEMENTS);

    private String configID;
    private Attribute attribute;

    RewardAttribute(String configID, Attribute attribute){
        this.configID = configID;
        this.attribute = attribute;
    }

    public String getConfigID() {
        return configID;
    }

    public Attribute getAttribute() {
        return attribute;
    }
}
