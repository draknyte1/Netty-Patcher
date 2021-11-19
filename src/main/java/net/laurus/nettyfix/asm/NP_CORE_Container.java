package net.laurus.nettyfix.asm;

import java.util.Arrays;
import java.util.List;

import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.versioning.ArtifactVersion;

public class NP_CORE_Container extends DummyModContainer {

	public NP_CORE_Container() {

		super(new ModMetadata());
		ModMetadata meta = getMetadata();
		meta.modId = NP_CORE.MODID;
		meta.name = NP_CORE.NAME;
		meta.version = NP_CORE.VERSION;
		meta.credits = "Roll Credits ...";
		meta.authorList = Arrays.asList("LunarLaurus");
		meta.description = "Makes Netty a bit safer.";
		meta.url = "";
		meta.updateUrl = "";
		meta.screenshots = new String[0];
		meta.logoFile = "";
		meta.dependencies = (List<ArtifactVersion>) NP_CORE.DEPENDENCIES;

	}
	
	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(this);
		return true;
	}
	
}