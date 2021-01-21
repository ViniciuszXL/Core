package br.com.vinicius.core.global.utilitaries.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import br.com.vinicius.core.global.utilitaries.color.ColorUtil;
import br.com.vinicius.core.global.utilitaries.lang.Language;
import net.minecraft.server.v1_8_R3.NBTTagByte;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagInt;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.NBTTagShort;
import net.minecraft.server.v1_8_R3.NBTTagString;

public class ItemBuilder {

	private net.minecraft.server.v1_8_R3.ItemStack base;
	private NBTTagCompound tag;

	private boolean glow = false;
	private boolean hideEnchants = false;
	private boolean unbreakable = false;

	private List<ItemFlag> itemFlagList = new ArrayList<ItemFlag>();

	private ItemBuilder(ItemStack base) {
		this.base = CraftItemStack.asNMSCopy(base);
		this.tag = this.base == null || this.base.getTag() == null ? new NBTTagCompound() : this.base.getTag();
	}

	public static ItemBuilder create(ItemStack itemStack) {
		return new ItemBuilder(itemStack);
	}

	public static ItemBuilder create(Material material) {
		return new ItemBuilder(new ItemStack(material));
	}

	public ItemBuilder name(String name) {
		NBTTagCompound displayTag = tag.getCompound("display");
		if (name == null) {
			displayTag.remove("Name");
		} else {
			displayTag.set("Name", new NBTTagString(name));
			tag.set("display", displayTag);
		}
		
		return this;
	}

	public ItemBuilder lore(String lore) {
		NBTTagCompound displayTag = tag.getCompound("display");
		NBTTagList loreList = displayTag.getList("Lore", 8);
		if (!displayTag.hasKeyOfType("Lore", 9))
			displayTag.set("Lore", loreList);
		
		loreList.add(new NBTTagString(lore));
		return this;
	}

	public ItemBuilder lore(String... lore) {
		return lore(Arrays.asList(lore));
	}

	public ItemBuilder lore(List<String> lore) {
		NBTTagCompound displayTag = tag.getCompound("display");
		NBTTagList loreList = displayTag.getList("Lore", 8);
		if (!displayTag.hasKeyOfType("Lore", 9))
			displayTag.set("Lore", loreList);

		for (String loreEntry : lore)
			loreList.add(new NBTTagString(loreEntry));
		return this;
	}

	public ItemBuilder lore(int index, String lore) {
		NBTTagCompound displayTag = tag.getCompound("display");
		NBTTagList loreList = displayTag.getList("Lore", 8);
		if (!displayTag.hasKeyOfType("Lore", 9))
			displayTag.set("Lore", loreList);

		loreList.a(index, new NBTTagString(lore));
		return this;
	}

	public ItemBuilder noLore() {
		NBTTagCompound displayTag = tag.getCompound("display");
		displayTag.remove("Lore");
		return this;
	}

	@SuppressWarnings("deprecation")
	public ItemBuilder enchantment(Enchantment enchantment, int level) {
		NBTTagList enchs = tag.getList("ench", 10);
		if (enchantment != null) {
			NBTTagCompound enchTag = new NBTTagCompound();
			enchTag.set("id", new NBTTagShort((short) enchantment.getId()));
			enchTag.set("lvl", new NBTTagShort((short) level));
			enchs.add(enchTag);
		} else {
			enchs = new NBTTagList();
		}

		tag.set("ench", enchs);
		return this;
	}

	@SuppressWarnings("deprecation")
	public ItemBuilder effect(PotionEffect effect) {
		NBTTagList effs = tag.getList("CustomPotionEffects", 10);
		if (effect != null) {
			NBTTagCompound effTag = new NBTTagCompound();
			effTag.set("Id", new NBTTagByte((byte) effect.getType().getId()));
			effTag.set("Amplifier", new NBTTagByte((byte) effect.getAmplifier()));
			effTag.set("Duration", new NBTTagInt(effect.getDuration()));
			effTag.set("Ambient", new NBTTagByte(effect.isAmbient() ? (byte) 1 : (byte) 0));
			effs.add(effTag);
		} else {
			effs = new NBTTagList();
		}

		tag.set("CustomPotionEffects", effs);
		return this;
	}

	public ItemBuilder noAttributes() {
		tag.set("AttributeModifiers", new NBTTagList());
		return this;
	}

	public ItemBuilder noEffects() {
		tag.set("CustomPotionEffects", new NBTTagList());
		return this;
	}

	public ItemBuilder unbreakable() {
		tag.set("Unbreakable", new NBTTagByte((byte) 1));
		this.unbreakable = true;
		return this;
	}
	
	public ItemBuilder addItemFlag(ItemFlag... itemFlags) {
		for (ItemFlag itemFlag : itemFlags) {
			if (!this.itemFlagList.contains(itemFlag))
				this.itemFlagList.add(itemFlag);
		}
		
		return this;
	}

	public ItemBuilder addItemFlag(ItemFlag itemFlag) {
		if (!this.itemFlagList.contains(itemFlag))
			this.itemFlagList.add(itemFlag);
		return this;
	}

	public ItemBuilder randomID() {
		tag.setString("random-id", UUID.randomUUID().toString());
		return this;
	}

	public ItemBuilder glow() {
		this.glow = true;
		return this;
	}

	public ItemBuilder hideEnchants() {
		this.hideEnchants = true;
		return this;
	}

	public ItemStack getItemStack() {
		if (base == null)
			return new ItemStack(Material.AIR);

		ItemStack stack = CraftItemStack.asBukkitCopy(base);
		stack = stack.clone();
		return stack;
	}

	public ItemStack build() {
		if (base == null)
			return new ItemStack(Material.AIR);

		base.setTag(tag);
		ItemStack stack = CraftItemStack.asBukkitCopy(base);
		stack = stack.clone();
		ItemMeta meta = stack.getItemMeta();

		this.addGlow(meta);
		this.addItemFlags(meta);
		this.addHideEnchants(stack, meta);

		stack.setItemMeta(meta);
		return stack;
	}

	private void addGlow(ItemMeta meta) {
		if (!glow)
			return;

		meta.addEnchant(Enchantment.ARROW_INFINITE, -1, true);
		meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
	}

	private void addHideEnchants(ItemStack stack, ItemMeta meta) {
		if (!hideEnchants)
			return;

		if (unbreakable)
			meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE });
		else
			meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });

		List<String> newLore = new ArrayList<String>();
		for (Entry<Enchantment, Integer> enchants : stack.getEnchantments().entrySet()) {
			Enchantment enchant = enchants.getKey();
			int level = enchants.getValue();

			newLore.add(ColorUtil.GRAY + Language.nameEnchant(enchant) + " " + level);
		}

		if (unbreakable)
			newLore.add(ColorUtil.GRAY + "Inquebrável");

		if (meta.hasLore()) {
			newLore.add(" ");
			for (String s : meta.getLore())
				newLore.add(s);
		}

		meta.setLore(newLore);
	}

	private void addItemFlags(ItemMeta itemMeta) {
		for (ItemFlag itemFlag : this.itemFlagList)
			itemMeta.addItemFlags(itemFlag);
	}
}
