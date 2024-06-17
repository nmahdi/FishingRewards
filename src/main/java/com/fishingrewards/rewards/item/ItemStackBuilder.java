package com.fishingrewards.rewards.item;

import com.fishingrewards.PluginLogger;
import com.fishingrewards.rewards.RewardAttribute;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.profile.PlayerProfile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class ItemStackBuilder {

    private ItemStack itemStack;
    private ItemMeta meta;
    private final ArrayList<String> lore = new ArrayList<>();

    public ItemStackBuilder buildFromContainer(ItemStackContainer container, Random random){
        this.itemStack = new ItemStack(container.getMaterial());
        this.meta = itemStack.getItemMeta();
        if(container.staticAmount()){
            setAmount(container.getMinAmount());
        }else{
            setAmount(random.nextInt(container.getMinAmount(), container.getMaxAmount()+1));
        }
        if(container.hasDisplayName()) setName(container.getDisplayName());
        if(container.hasLore()) addLore(container.getLore());
        if(container.hasModelData()) setModelData(container.getModelData());

        int bonusAmount = container.getBonusEnchantmentsAmount();
        if(!container.hasBonusEnchantmentsAmount()) bonusAmount = container.getBonusEnchantments().size();

        for(EnchantmentContainer enchantmentContainer : container.getBonusEnchantments()){
            if(bonusAmount == 0) break;
            double chance = random.nextDouble(100);
            if(chance <= enchantmentContainer.getChance()){
                addEnchantment(enchantmentContainer.getEnchantment(), random.nextInt(enchantmentContainer.getMinLevel(), enchantmentContainer.getMaxLevel()+1));
                bonusAmount--;
            }
        }
        if(!container.hasBonusEnchantmentsAmount()) bonusAmount = 0;

        ArrayList<EnchantmentContainer> guaranteedEnchantments = new ArrayList<>(container.getGuaranteedEnchantments());
        int guaranteedAmount = container.getGuaranteedEnchantmentsAmount();
        if(container.overrideBonusEnchantments()) guaranteedAmount += bonusAmount;
        if(!container.hasGuaranteedEnchantmentsAmount()) guaranteedAmount = container.getGuaranteedEnchantments().size();


        int amountToRemove = guaranteedEnchantments.size()-guaranteedAmount;
        for(int i = 0; i < amountToRemove; i++){
            guaranteedEnchantments.remove(random.nextInt(guaranteedEnchantments.size()));
        }
        for(EnchantmentContainer enchantment : guaranteedEnchantments){
            addEnchantment(enchantment.getEnchantment(), random.nextInt(enchantment.getMinLevel(), enchantment.getMaxLevel()+1));
        }

        if(container.hasFlags()){
            for(ItemFlag flag : container.getFlags()) addFlag(flag);
        }
        if(container.hasDurability()){
            double percent = container.getMaterial().getMaxDurability()*(random.nextDouble(container.getMinDurability(), container.getMaxDurability()+1)/100);
            setDurability((int)percent);
        }
        if(container.isUnbreakable()){
            setUnbreakable();
        }
        for(RewardAttribute attribute : RewardAttribute.values()){
            if(container.getAttributes().containsKey(attribute.getAttribute())){
                addAttributeModifier(attribute.getAttribute(), container.getAttributes().get(attribute.getAttribute()));
            }
        }
        if(container.isSkull() && container.hasSkullURL()){
            setSkullURL(container.getSkullURL());
        }
        if(container.isPotion()){
            if(container.hasEffects()) setPotionEffects(container.getEffects());
            if(container.hasPotionColor()) setPotionColor(container.getPotionColor());
        }
        return this;
    }

    public ItemStackBuilder setMaterial(Material material){
        itemStack = new ItemStack(material);
        meta = itemStack.getItemMeta();
        return this;
    }

    public ItemStackBuilder setAmount(int amount){
        itemStack.setAmount(amount);
        return this;
    }

    public ItemStackBuilder setName(String name){
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        return this;
    }

    public ItemStackBuilder addLore(String s){
        lore.add(ChatColor.translateAlternateColorCodes('&', s));
        return this;
    }

    public ItemStackBuilder addLore(ArrayList<String> list){
        for(String s : list){
            this.lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return this;
    }

    public ItemStackBuilder spaceLore(){
        lore.add(" ");
        return this;
    }

    public ItemStackBuilder setDurability(int durability){
        if(meta instanceof Damageable){
            ((Damageable)meta).setDamage(durability);
        }
        return this;
    }

    public ItemStackBuilder setUnbreakable(){
        meta.setUnbreakable(true);
        return this;
    }

    public ItemStackBuilder addFlag(ItemFlag itemFlag){
        meta.addItemFlags(itemFlag);
        return this;
    }

    public ItemStackBuilder setModelData(int modelData){
        meta.setCustomModelData(modelData);
        return this;
    }

    public ItemStackBuilder addAttributeModifier(Attribute attribute, AttributeModifier modifier){
        meta.addAttributeModifier(attribute, modifier);
        return this;
    }

    public ItemStackBuilder setSkullURL(String url){
        SkullMeta skullMeta = (SkullMeta) meta;
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.fromString("105ca6b8-65d9-46ab-82aa-05e1fc902d67"), "fishing_reward");
        try {
            profile.getTextures().setSkin(new URL("https://textures.minecraft.net/texture/"+url));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        skullMeta.setOwnerProfile(profile);
        return this;
    }

    public ItemStackBuilder setPotionEffects(ArrayList<PotionEffect> effects){
        PotionMeta potionMeta = (PotionMeta) meta;
        for(PotionEffect effect : effects){
            potionMeta.addCustomEffect(effect, true);
        }
        return this;
    }

    public ItemStackBuilder setPotionColor(Color color){
        PotionMeta potionMeta = (PotionMeta) meta;
        potionMeta.setColor(color);
        return this;
    }

    public ItemStack build(){
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public void addEnchantment(Enchantment enchantment, int level){
        meta.addEnchant(enchantment, level, true);
    }

    public ItemStack getItemStack(){
        return itemStack;
    }

    public ItemMeta getMeta(){
        return meta;
    }

}
