package br.com.vinicius.core.global.utilitaries.lang;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class Language {

	public static String name(Material mat) {
		return ChatColor.stripColor(LanguageHelper.getItemDisplayName(new ItemStack(mat), "pt_br"));
	}

	public static String name(ItemStack is) {
		return ChatColor.stripColor(LanguageHelper.getItemDisplayName(is, "pt_br"));
	}

	public static String nameEnchant(Enchantment enchant) {
		String name = NameEnum.convert(LanguageHelper.getEnchantmentName(enchant, "pt_br"));
		name = NameStarts.convert(name);

		return ChatColor.stripColor(name);
	}

	public enum NameEnum {

		Ã1("Ã¡", "á"), Ã2("Ãª", "ê"), Ã3("Ã£", "çã"), Ã4("Ã", "çã"), Ã5("§ão", "o");

		public String convert;
		public String converted;

		NameEnum(String convert, String converted) {
			this.convert = convert;
			this.converted = converted;
		}

		public String getConvert() {
			return this.convert;
		}

		public String getConverted() {
			return this.converted;
		}

		public static String convert(String text) {
			NameEnum name = Arrays.asList(values()).stream().filter(s -> text.contains(s.getConvert())).findFirst()
					.orElse(null);
			return name == null ? text : text.replace(name.getConvert(), name.getConverted());
		}
	}

	public enum NameStarts {

		PROTEÇAO("Proteç", "Proteção"), AFIAÇAO("Afiaç", "Afiação"), FORÇA("Forç", "Força");

		public String convert;
		public String converted;

		NameStarts(String convert, String converted) {
			this.convert = convert;
			this.converted = converted;
		}

		public String getConvert() {
			return this.convert;
		}

		public String getConverted() {
			return this.converted;
		}

		public static String convert(String text) {
			NameStarts name = Arrays.asList(values()).stream().filter(s -> text.startsWith(s.getConvert())).findFirst()
					.orElse(null);
			return name == null ? text : name.getConverted();
		}
	}
}
