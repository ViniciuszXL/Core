package br.com.vinicius.core.global.utilitaries.lang.convert;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;

public enum EnumEnchantment {

    PROTECTION_ENVIRONMENTAL(Enchantment.PROTECTION_ENVIRONMENTAL, "enchantment.protect.all"),
    PROTECTION_FIRE(Enchantment.PROTECTION_FIRE, "enchantment.protect.fire"),
    PROTECTION_FALL(Enchantment.PROTECTION_FALL, "enchantment.protect.fall"),
    PROTECTION_EXPLOSIONS(Enchantment.PROTECTION_EXPLOSIONS, "enchantment.protect.explosion"),
    PROTECTION_PROJECTILE(Enchantment.PROTECTION_PROJECTILE, "enchantment.protect.projectile"),
    OXYGEN(Enchantment.OXYGEN, "enchantment.oxygen"),
    WATER_WORKER(Enchantment.WATER_WORKER, "enchantment.waterWorker"),
    THORNS(Enchantment.THORNS, "enchantment.thorns"),
    DAMAGE_ALL(Enchantment.DAMAGE_ALL, "enchantment.damage.all"),
    DAMAGE_UNDEAD(Enchantment.DAMAGE_UNDEAD, "enchantment.damage.undead"),
    DAMAGE_ARTHROPODS(Enchantment.DAMAGE_ARTHROPODS, "enchantment.damage.arthropods"),
    KNOCKBACK(Enchantment.KNOCKBACK, "enchantment.knockback"),
    FIRE_ASPECT(Enchantment.FIRE_ASPECT, "enchantment.fire"),
    LOOT_BONUS_MOBS(Enchantment.LOOT_BONUS_MOBS, "enchantment.lootBonus"),
    DIG_SPEED(Enchantment.DIG_SPEED, "enchantment.digging"),
    SILK_TOUCH(Enchantment.SILK_TOUCH, "enchantment.untouching"),
    DURABILITY(Enchantment.DURABILITY, "enchantment.durability"),
    LOOT_BONUS_BLOCKS(Enchantment.LOOT_BONUS_BLOCKS, "enchantment.lootBonusDigger"),
    ARROW_DAMAGE(Enchantment.ARROW_DAMAGE, "enchantment.arrowDamage"),
    ARROW_KNOCKBACK(Enchantment.ARROW_KNOCKBACK, "enchantment.arrowKnockback"),
    ARROW_FIRE(Enchantment.ARROW_FIRE, "enchantment.arrowFire"),
    ARROW_INFINITE(Enchantment.ARROW_INFINITE, "enchantment.arrowInfinite"),
    LUCK(Enchantment.LUCK, "enchantment.lootBonusFishing"),
    LURE(Enchantment.LURE, "enchantment.fishingSpeed"),
    BREAKER(null, "enchantment.breaker"),
    EXPLOSION(null, "enchantment.explosion");

    private static final Map<Enchantment, EnumEnchantment> lookup = new HashMap<Enchantment, EnumEnchantment>();

    static {
        for (EnumEnchantment enchantment : EnumSet.allOf(EnumEnchantment.class))
            lookup.put(enchantment.enchantment, enchantment);
    }

    private Enchantment enchantment;
    private String unlocalizedName;

    /**
     * Create an index of enchantments.
     */
    EnumEnchantment(Enchantment enchantment, String unlocalizedName) {
        this.enchantment = enchantment;
        this.unlocalizedName = unlocalizedName;
    }

    /**
     * Get the index of an enchantment based on {@link EnumEnchantment}.
     *
     * @param enchantment The enchantment.
     * @return The index of the item.
     */
    public static EnumEnchantment get(Enchantment enchantment) {
        return lookup.get(enchantment);
    }

    /**
     * @return The {@link Enchantment} of the enchantment.
     */
    public Enchantment getEnchantment() {
        return enchantment;
    }

    /**
     * @return The unlocalized name of the enchantment.
     */
    public String getUnlocalizedName() {
        return unlocalizedName;
    }
}
