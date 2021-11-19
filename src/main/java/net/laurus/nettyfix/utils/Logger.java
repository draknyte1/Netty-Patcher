package net.laurus.nettyfix.utils;

public class Logger {

	public static org.apache.logging.log4j.Logger logger;

	public static void ASM(String text) {
		log("[ASM] "+text);		
	}

	public static void REFLECTION(String text) {
		log("[Reflection] "+text);		
	}

	public static void INFO(String text) {
		log(text);		
	}

	public static void WARNING(String text) {
		logger.warn(text);
	}

	public static void ERROR(String text) {
		logger.fatal(text);
	}

	private static void log(String s) {
		logger.info(s);
	}

}
