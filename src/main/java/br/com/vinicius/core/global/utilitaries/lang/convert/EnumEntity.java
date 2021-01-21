package br.com.vinicius.core.global.utilitaries.lang.convert;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import br.com.vinicius.core.global.utilitaries.lang.LanguageHelper;

public enum EnumEntity {

    ITEM(EntityType.DROPPED_ITEM, "entity.Item.name"),
    EXPERIENCE_ORB(EntityType.EXPERIENCE_ORB, "entity.XPOrb.name"),
    SMALL_FIREBALL(EntityType.SMALL_FIREBALL, "entity.SmallFireball.name"),
    FIREBALL(EntityType.FIREBALL, "entity.Fireball.name"),
    POTION(EntityType.SPLASH_POTION, "entity.ThrownPotion.name"),
    ARROW(EntityType.ARROW, "entity.Arrow.name"),
    SNOWBALL(EntityType.SNOWBALL, "entity.Snowball.name"),
    PAINTING(EntityType.PAINTING, "entity.Painting.name"),
    CREEPER(EntityType.CREEPER, "entity.Creeper.name"),
    SKELETON(EntityType.SKELETON, "entity.Skeleton.name"),
    SPIDER(EntityType.SPIDER, "entity.Spider.name"),
    GIANT(EntityType.GIANT, "entity.Giant.name"),
    ZOMBIE(EntityType.ZOMBIE, "entity.Zombie.name"),
    SLIME(EntityType.SLIME, "entity.Slime.name"),
    GHAST(EntityType.GHAST, "entity.Ghast.name"),
    RABBIT(EntityType.RABBIT, "entity.Rabbit.name"),
    ZOMBIE_PIGMAN(EntityType.PIG_ZOMBIE, "entity.PigZombie.name"),
    ENDERMAN(EntityType.ENDERMAN, "entity.Enderman.name"),
    SILVERFISH(EntityType.SILVERFISH, "entity.Silverfish.name"),
    CAVE_SPIDER(EntityType.CAVE_SPIDER, "entity.CaveSpider.name"),
    BLAZE(EntityType.BLAZE, "entity.Blaze.name"),
    MAGMA_CUBE(EntityType.MAGMA_CUBE, "entity.LavaSlime.name"),
    MOOSHROOM(EntityType.MUSHROOM_COW, "entity.MushroomCow.name"),
    VILLAGER(EntityType.VILLAGER, "entity.Villager.name"),
    IRON_GOLEM(EntityType.IRON_GOLEM, "entity.VillagerGolem.name"),
    SNOW_GOLEM(EntityType.SNOWMAN, "entity.SnowMan.name"),
    ENDER_DRAGON(EntityType.ENDER_DRAGON, "entity.EnderDragon.name"),
    WITHER(EntityType.WITHER, "entity.WitherBoss.name"),
    WITCH(EntityType.WITCH, "entity.Witch.name"),
    PIG(EntityType.PIG, "entity.Pig.name"),
    SHEEP(EntityType.SHEEP, "entity.Sheep.name"),
    COW(EntityType.COW, "entity.Cow.name"),
    CHICKEN(EntityType.CHICKEN, "entity.Chicken.name"),
    SQUID(EntityType.SQUID, "entity.Squid.name"),
    WOLF(EntityType.WOLF, "entity.Wolf.name"),
    OCELOT(EntityType.OCELOT, "entity.Ozelot.name"),
    BAT(EntityType.BAT, "entity.Bat.name"),
    HORSE(EntityType.HORSE, "entity.Horse.name"),
    BLOCK_OF_TNT(EntityType.PRIMED_TNT, "entity.PrimedTnt.name"),
    FALLING_BLOCK(EntityType.FALLING_BLOCK, "entity.FallingSand.name"),
    MINECART(EntityType.MINECART, "entity.Minecart.name"),
    MINECART_WITH_HOPPER(EntityType.MINECART_HOPPER, "entity.MinecartHopper.name"),
    MINECART_WITH_CHEST(EntityType.MINECART_CHEST, "entity.MinecartChest.name"),
    BOAT(EntityType.BOAT, "entity.Boat.name");

    private static final Map<EntityType, EnumEntity> lookup = new HashMap<EntityType, EnumEntity>();

    static {
        for (EnumEntity entity : EnumSet.allOf(EnumEntity.class))
            lookup.put(entity.getType(), entity);
    }

    private EntityType type;
    private String unlocalizedName;

    EnumEntity(EntityType type, String unlocalizedName) {
        this.type = type;
        this.unlocalizedName = unlocalizedName;
    }

    public static EnumEntity get(EntityType entityType) {
        return lookup.get(entityType);
    }

    public static String getSpawnEggName(ItemStack egg, String locale) {
        EntityType type = null;
        
        try {
            type = getEntityType(egg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        EnumEntity entity = get(type);
        return entity != null ?
                LanguageHelper.translateToLocal("item.monsterPlacer.name", locale) + " "
                        + LanguageHelper.translateToLocal(entity.getUnlocalizedName(), locale)
                : LanguageHelper.translateToLocal("item.monsterPlacer.name", locale);
    }
    
    @SuppressWarnings("deprecation")
	public static EntityType getEntityType(ItemStack egg) {
    	return EntityType.fromId(egg.getDurability());
    }

    public String getUnlocalizedName() {
        return unlocalizedName;
    }

    public EntityType getType() {
        return type;
    }

}
