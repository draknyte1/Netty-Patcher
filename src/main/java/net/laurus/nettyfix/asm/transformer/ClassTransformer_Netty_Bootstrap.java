package net.laurus.nettyfix.asm.transformer;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import net.laurus.nettyfix.utils.Logger;

public class ClassTransformer_Netty_Bootstrap {

	private final ClassReader reader;
	private final ClassWriter writer;

	public ClassTransformer_Netty_Bootstrap(String transformedName, byte[] basicClass) {
		ClassReader aTempReader = null;
		ClassWriter aTempWriter = null;
		aTempReader = new ClassReader(basicClass);
		aTempWriter = new ClassWriter(aTempReader, ClassWriter.COMPUTE_FRAMES);
		reader = aTempReader;
		writer = aTempWriter;
		init(transformedName);
	}

	protected final void init(String transformedName) {
		Fix_CheckAddress aFix = new Fix_CheckAddress(writer);
		reader.accept(aFix, 0);
		boolean aFoundMethod = aFix.found;
		if (aFoundMethod) {
			log("Transforming "+transformedName);
			injectMethod(writer);		
			log("Found method \"checkAddress\", your version of Netty *IS* susceptible to MC-108343, this has been patched.");	
		}	
		else {
			//Logger.ASM("Unable to find method \"checkAddress\", your version of Netty is not susceptible to MC-108343.");
		}
		if (aFoundMethod) {
			boolean isValid = false;
			if (reader != null && writer != null) {
				isValid = true;
			}
			log("Valid? " + isValid + ".");	
		}
	}	

	public boolean injectMethod(ClassWriter cw) {
		boolean b = inject_checkAddress();
		if (!b) {
			log("Patching failed.");
			return false;
		}
		log("Patching Success.");	
		return true;
	}

	private boolean inject_checkAddress() {
		MethodVisitor mv;
		mv = getWriter().visitMethod(0, "checkAddress", "(Ljava/net/SocketAddress;)Lio/netty/channel/ChannelFuture;", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(173, l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKESTATIC, "net/laurus/nettyfix/utils/NettyUtils", "checkAddress", "(Lio/netty/bootstrap/Bootstrap;Ljava/net/SocketAddress;)Lio/netty/channel/ChannelFuture;", false);
		mv.visitInsn(ARETURN);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable("this", "Lio/netty/bootstrap/Bootstrap;", null, l0, l1, 0);
		mv.visitLocalVariable("remoteAddress", "Ljava/net/SocketAddress;", null, l0, l1, 1);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
		return true;
	}

	public final ClassReader getReader() {
		return reader;
	}

	public final ClassWriter getWriter() {
		return writer;
	}

	public String getTransformerName() {
		return "Netty-Bootstrap";
	}

	protected void log(String s) {
		Logger.ASM(getTransformerName() + "  | " + s);
	}

	public class Fix_CheckAddress extends ClassVisitor {

		public boolean found = false;

		public Fix_CheckAddress(ClassVisitor cv) {
			super(ASM5, cv);
			this.cv = cv;
		}


		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			MethodVisitor methodVisitor;
			boolean foundMethod = false;		
			if (name.equals("checkAddress")) {
				if (desc.equals("(Ljava/net/SocketAddress;)Lio/netty/channel/ChannelFuture;")) {
					foundMethod = true;					
				}
			}
			if (foundMethod) {
				methodVisitor = null;
				log("Found method " + name + ", removing. "+" Found matching desc: "+desc);
				this.found = true;
			} 
			else {
				methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
			}
			return methodVisitor;
		}

	}

}
