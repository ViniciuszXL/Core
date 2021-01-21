package br.com.vinicius.core.global.utilitaries.lang.convert;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemEntry {
    private Material material;
    private int metadata;

    public ItemEntry(Material material, int meta) {
        this.material = material;
        this.metadata = meta;
    }

    public ItemEntry(Material material) {
        this(material, 0);
    }

    public ItemEntry(ItemStack itemStack) {
        this.material = itemStack.getType();
        this.metadata = itemStack.getDurability();
    }

    public Material getMaterial() {
        return material;
    }
    
    public int getMetadata() {
        return metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemEntry)) return false;

        ItemEntry itemEntry = (ItemEntry) o;

        return metadata == itemEntry.metadata && material == itemEntry.material;
    }

    @Override
    public int hashCode() {
        int result = material.hashCode();
        result = 31 * result + metadata;
        return result;
    }

    @Override
    public String toString() {
        return this.material.toString() + " " + this.metadata;
    }
}
