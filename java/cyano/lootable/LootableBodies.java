package cyano.lootable;


import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import cyano.lootable.entities.EntityLootableBody;
import cyano.lootable.events.PlayerDeathEventHandler;

@Mod(modid = LootableBodies.MODID, name=LootableBodies.NAME, version = LootableBodies.VERSION)
public class LootableBodies {
    public static final String MODID = "lootablebodies";
    public static final String NAME ="DrCyano's Lootable Bodies";
    public static final String VERSION = "1.1.2";
	
    public static boolean fancyCorpses = false;
    
    @SidedProxy(clientSide="cyano.lootable.ClientProxy", serverSide="cyano.lootable.ServerProxy")
    public static Proxy proxy;
    
	
	// Mark this method for receiving an FMLEvent (in this case, it's the FMLPreInitializationEvent)
    @EventHandler public void preInit(FMLPreInitializationEvent event)
    {
        // Do stuff in pre-init phase (read config, create blocks and items, register them)
    	// load config
    	Configuration config = new Configuration(event.getSuggestedConfigurationFile());
    	config.load();
    	
    	boolean invulnderable = true;
		
    	EntityLootableBody.additionalItemDamage = config.getInt("item_damage_on_death", "options", 32, 0,1000,
				"The amount of damage suffered by damageable items when you \n"
				+ "die, to a minimum of 1 durability remaining (items will \n"
				+ "not be destroyed).");
    	EntityLootableBody.corpseHP = config.getFloat("corpse_HP", "options", 50, 1,Short.MAX_VALUE,
				"The amount of damage a corpse can suffer before being \n"
				+ "destroyed and releasing its items. \n"
				+ "Note that 10 hearts = 20 HP.");
    	fancyCorpses = config.getBoolean("use_player_skin", "options", true,
				"If true, corpses will have the skins of the player who \n"
				+ "died. If false, then skeletons will be used instead.");

    	EntityLootableBody.hurtByAll = config.getBoolean("hurt_by_all", "corpse damage", false,
				"If true, corpses will be damaged by anything that damages a player.");
    	EntityLootableBody.hurtByFire = config.getBoolean("hurt_by_fire", "corpse damage", false,
				"If true, corpses will be damaged by fire and lava.");
    	EntityLootableBody.hurtByBlast = config.getBoolean("hurt_by_explosions", "corpse damage", false,
				"If true, corpses will be damaged by creepers and TNT. \n"
				+ "If you don't want bodies to be destroyed by explosions, \n"
				+ "also disable fall damage.");
    	EntityLootableBody.hurtByFall = config.getBoolean("hurt_by_fall", "corpse damage", false,
				"If true, corpses will be damaged by falling long distances.");
    	EntityLootableBody.hurtByCactus = config.getBoolean("hurt_by_cactus", "corpse damage", false,
				"If true, corpses will be damaged by cacti.");
    	EntityLootableBody.hurtByWeapons = config.getBoolean("hurt_by_weapons", "corpse damage", false,
				"If true, corpses will be damaged by attacking it.");
    	EntityLootableBody.hurtByBlockSuffocation = config.getBoolean("hurt_by_block_suffocation", "corpse damage", false,
				"If true, corpses will be damaged by being stuck inside a block.");
    	EntityLootableBody.hurtByOther = config.getBoolean("hurt_by_other", "corpse damage", false,
				"If true, corpses will be damaged by damage sources not covered by the other options in this section.");
    	
    	
    	EntityLootableBody.invulnerable = !or(
    			EntityLootableBody.hurtByAll, 
    			EntityLootableBody.hurtByBlast, 
    			EntityLootableBody.hurtByBlockSuffocation, 
    			EntityLootableBody.hurtByCactus, 
    			EntityLootableBody.hurtByFall, 
    			EntityLootableBody.hurtByFire, 
    			EntityLootableBody.hurtByOther, 
    			EntityLootableBody.hurtByWeapons);
    	
	//	OreDictionary.initVanillaEntries()
		config.save();
		proxy.preInit(event);
    }

	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		// register entities
		registerItemRenders();
		
		registerEntity(EntityLootableBody.class);
		MinecraftForge.EVENT_BUS.register(new PlayerDeathEventHandler());
 		
		proxy.init(event);
		
		
	}
	private int entityIndex = 0;
	private void registerEntity(Class entityClass){
		String idName = MODID+"_"+entityClass.getSimpleName();
		EntityRegistry.registerGlobalEntityID(entityClass, idName, EntityRegistry.findGlobalUniqueEntityId());
 		EntityRegistry.registerModEntity(entityClass, idName, entityIndex++/*id*/, this, 64/*trackingRange*/, 10/*updateFrequency*/, true/*sendsVelocityUpdates*/);
 		
	}
    
    private void registerItemRenders() {
    	// client-side only
    	if(proxy instanceof ServerProxy) return;
    //	registerItemRender(wandGeneric,OrdinaryWand.itemName);
	}
    
    private void registerItemRender(Item i, String itemName){
    	Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(i, 0, new ModelResourceLocation(MODID+":"+itemName, "inventory"));
    }

	
	@EventHandler public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
	/*
	@EventHandler public void onServerStarting(FMLServerStartingEvent event)
	{
		// stub
	}
	*/
	
	
	private static boolean or(boolean... bools){
		for(int i = 0; i < bools.length; i++){
			if(bools[i] == true) return true;
		}
		return false;
	}
	
	private static boolean and(boolean... bools){
		for(int i = 0; i < bools.length; i++){
			if(bools[i] == false) return false;
		}
		return true;
	}
}
