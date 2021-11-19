package net.laurus.nettyfix.asm;

import net.laurus.nettyfix.asm.transformer.ClassTransformer_Netty_Bootstrap;
import net.minecraft.launchwrapper.IClassTransformer;

public class NP_CORE_Handler implements IClassTransformer {

	public byte[] transform(String name, String transformedName, byte[] basicClass) {

		// Patch Poppet Shelf to enable/disable chunk loading
		if (transformedName.equals("io.netty.bootstrap.Bootstrap")) {
			return new ClassTransformer_Netty_Bootstrap(transformedName, basicClass).getWriter().toByteArray();
		}

		return basicClass;
	}



}
