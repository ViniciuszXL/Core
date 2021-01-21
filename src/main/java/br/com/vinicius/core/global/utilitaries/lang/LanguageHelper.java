package br.com.vinicius.core.global.utilitaries.lang;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.vinicius.core.global.utilitaries.lang.convert.*;

public class LanguageHelper {

	public static String getItemDisplayName(ItemStack item, String locale) {
		if (item.hasItemMeta() && item.getItemMeta().hasDisplayName())
			return item.getItemMeta().getDisplayName();
		else
			return getItemName(item, locale);
	}

	public static String getItemDisplayName(ItemStack item, Player player) {
		return getItemDisplayName(item, "pt-br");
	}

	public static String getItemName(ItemStack item, String locale) {
		if (item.getType() == Material.MONSTER_EGG)
			return EnumEntity.getSpawnEggName(item, locale);
		else if (item.getType() == Material.SKULL_ITEM && item.getDurability() == 3) // is player's skull
			return EnumItem.getPlayerSkullName(item, locale);

		return translateToLocal(getItemUnlocalizedName(item), locale);
	}

	public static String getItemName(ItemStack item, Player player) {
		return getItemName(item, "pt-br");
	}

	public static String getItemUnlocalizedName(ItemStack item) {
		EnumItem enumItem = EnumItem.get(new ItemEntry(item));
		return enumItem != null ? enumItem.getUnlocalizedName() : item.getType().toString();
	}

	public static String getEntityUnlocalizedName(Entity entity) {
		EnumEntity enumEntity = EnumEntity.get(entity.getType());
		return enumEntity != null ? enumEntity.getUnlocalizedName() : entity.getType().toString();
	}

	public static String getEntityUnlocalizedName(EntityType entityType) {
		EnumEntity enumEntity = EnumEntity.get(entityType);
		return enumEntity != null ? enumEntity.getUnlocalizedName() : entityType.toString();
	}

	public static String getEntityDisplayName(Entity entity, String locale) {
		return ((LivingEntity) entity).getCustomName() != null ? ((LivingEntity) entity).getCustomName()
				: getEntityName(entity, locale);
	}

	public static String getEntityDisplayName(Entity entity, Player player) {
		return getEntityDisplayName(entity, "pt-br");
	}

	public static String getEntityName(Entity entity, String locale) {
		return translateToLocal(getEntityUnlocalizedName(entity), locale);
	}

	public static String getEntityName(Entity entity, Player player) {
		return getEntityName(entity, "pt-br");
	}

	public static String getEntityName(EntityType entityType, String locale) {
		return translateToLocal(getEntityUnlocalizedName(entityType), locale);
	}

	public static String getEntityName(EntityType entityType, Player player) {
		return getEntityName(entityType, "pt-br");
	}

	public static String getEnchantmentLevelUnlocalizedName(int level) {
		EnumEnchantmentLevel enumEnchLevel = EnumEnchantmentLevel.get(level);
		return enumEnchLevel != null ? enumEnchLevel.getUnlocalizedName() : Integer.toString(level);
	}

	public static String getEnchantmentLevelName(int level, Player player) {
		return translateToLocal(getEnchantmentLevelUnlocalizedName(level), "pt-br");
	}

	public static String getEnchantmentLevelName(int level, String locale) {
		return translateToLocal(getEnchantmentLevelUnlocalizedName(level), locale);
	}

	public static String getEnchantmentUnlocalizedName(Enchantment enchantment) {
		EnumEnchantment enumEnch = EnumEnchantment.get(enchantment);
		return (enumEnch != null ? enumEnch.getUnlocalizedName() : enchantment.getName());
	}

	public static String getEnchantmentName(Enchantment enchantment, Player player) {
		return getEnchantmentName(enchantment, "pt-br");
	}

	public static String getEnchantmentName(Enchantment enchantment, String locale) {
		return translateToLocal(getEnchantmentUnlocalizedName(enchantment), locale);
	}

	public static String getEnchantmentDisplayName(Enchantment enchantment, int level, Player player) {
		return getEnchantmentDisplayName(enchantment, level, "pt-br");
	}

	public static String getEnchantmentDisplayName(Enchantment enchantment, int level, String locale) {
		String name = getEnchantmentName(enchantment, locale);
		String enchLevel = getEnchantmentLevelName(level, locale);
		return name + (enchLevel.length() > 0 ? " " + enchLevel : "");
	}

	public static String getEnchantmentDisplayName(Map.Entry<Enchantment, Integer> entry, String locale) {
		return getEnchantmentDisplayName(entry.getKey(), entry.getValue(), locale);
	}

	public static String getEnchantmentDisplayName(Map.Entry<Enchantment, Integer> entry, Player player) {
		return getEnchantmentDisplayName(entry.getKey(), entry.getValue(), player);
	}

	public static String translateToLocal(String unlocalizedName, String locale) {
		String result = EnumLang.get(locale.toLowerCase()).getMap().get(unlocalizedName);
		if (result != null)
			return result;
		else
			result = EnumLang.PT_BR.getMap().get(unlocalizedName);

		return result == null ? unlocalizedName : result;
	}
}
