package net.laurus.nettyfix.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.DefaultChannelPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.EmptyArrays;

public class NettyUtils {

	private static final Joiner DOT_JOINER = Joiner.on('.');	  
	private static final Splitter DOT_SPLITTER = Splitter.on('.');
	static final Set<String> BLOCKED_SERVERS = Sets.newHashSet();

	public static ChannelFuture checkAddress(Bootstrap boostrap, SocketAddress remoteAddress) {
		if (remoteAddress instanceof InetSocketAddress) {
			InetAddress address = ((InetSocketAddress)remoteAddress).getAddress();
			if (address != null && (BlockedServers.isBlockedServer(address.getHostAddress()) || BlockedServers.isBlockedServer(address.getHostName()))) {
				Channel channel = ReflectionUtils.invokeNonBool(boostrap, ReflectionUtils.getMethod(boostrap.getClass(), "channelFactory", new Class[] {}), new Object[] {});
				channel.unsafe().closeForcibly();
				SocketException cause = new SocketException("Network is unreachable");
				cause.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
				return (ChannelFuture)(new DefaultChannelPromise(channel, (EventExecutor)GlobalEventExecutor.INSTANCE)).setFailure(cause);
			} 
		} 
		return null;
	}

	public boolean isBlockedServer(String server) {
		if (server == null || server.isEmpty())
			return false; 
		while (server.charAt(server.length() - 1) == '.')
			server = server.substring(0, server.length() - 1); 
		if (isBlockedServerHostName(server))
			return true; 
		List<String> strings = Lists.newArrayList(DOT_SPLITTER.split(server));
		boolean isIp = (strings.size() == 4);
		if (isIp)
			for (String string : strings) {
				try {
					int part = Integer.parseInt(string);
					if (part >= 0 && part <= 255)
						continue; 
				} catch (NumberFormatException ignored) {}
				isIp = false;
			}  
		if (!isIp && isBlockedServerHostName("*." + server))
			return true; 
		while (strings.size() > 1) {
			strings.remove(isIp ? (strings.size() - 1) : 0);
			String starredPart = isIp ? (DOT_JOINER.join(strings) + ".*") : ("*." + DOT_JOINER.join(strings));
			if (isBlockedServerHostName(starredPart))
				return true; 
		} 
		return false;
	}

	private boolean isBlockedServerHostName(String server) {
		return BLOCKED_SERVERS.contains(Hashing.sha1().hashBytes(server.toLowerCase().getBytes(Charset.forName("ISO-8859-1"))).toString());
	}

	public static class BlockedServers {

		static final Set<String> BLOCKED_SERVERS = Sets.newHashSet();	  
		private static final String SRV_PREFIX = "_minecraft._tcp.";	  
		private static final Joiner DOT_JOINER = Joiner.on('.');	  
		private static final Splitter DOT_SPLITTER = Splitter.on('.');	  
		private static final Charset HASH_CHARSET = Charsets.ISO_8859_1;

		static {
			try {
				BLOCKED_SERVERS.addAll(IOUtils.readLines((new URL("https://sessionserver.mojang.com/blockedservers")).openConnection().getInputStream(), HASH_CHARSET));
			} catch (IOException iOException) {}
		}

		public static boolean isBlockedServer(String server) {
			if (server == null || server.isEmpty())
				return false; 
			if (server.startsWith(SRV_PREFIX))
				server = server.substring(SRV_PREFIX.length()); 
			while (server.charAt(server.length() - 1) == '.')
				server = server.substring(0, server.length() - 1); 
			if (isBlockedServerHostName(server))
				return true; 
			List<String> parts = Lists.newArrayList(DOT_SPLITTER.split(server));
			boolean isIp = isIp(parts);
			if (!isIp && isBlockedServerHostName("*." + server))
				return true; 
			while (parts.size() > 1) {
				parts.remove(isIp ? (parts.size() - 1) : 0);
				String starredPart = isIp ? (DOT_JOINER.join(parts) + ".*") : ("*." + DOT_JOINER.join(parts));
				if (isBlockedServerHostName(starredPart))
					return true; 
			} 
			return false;
		}

		private static boolean isIp(List<String> address) {
			if (address.size() != 4)
				return false; 
			for (String s : address) {
				try {
					int part = Integer.parseInt(s);
					if (part < 0 || part > 255)
						return false; 
				} catch (NumberFormatException ignored) {
					return false;
				} 
			} 
			return true;
		}

		private static boolean isBlockedServerHostName(String server) {
			return BLOCKED_SERVERS.contains(Hashing.sha1().hashBytes(server.toLowerCase().getBytes(HASH_CHARSET)).toString());
		}
	}


}
