package net.laurus.nettyfix;

import static net.laurus.nettyfix.utils.Logger.logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.laurus.nettyfix.utils.Logger;

@Mod(modid = NettyPatch.MODID, name = NettyPatch.NAME, version = NettyPatch.VERSION, dependencies = "")
public class NettyPatch {	
	
	public static final String MODID = "NettyPatch";
	public static final String NAME = "NettyPatch";
	public static final String VERSION = "0.2";
	
	@Instance(MODID)
	public static NettyPatch instance;	
	
	@EventHandler
	public synchronized void preInit(final FMLPreInitializationEvent e) {
		logger = e.getModLog();
    	Logger.INFO("Loading "+NAME+" - v"+VERSION);
	}
	
}
