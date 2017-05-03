package net.wurstclient.compatibility;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;

public final class WMinecraft
{
	public static final String VERSION = "1.9";
	public static final String DISPLAY_VERSION = "1.9";
	public static final boolean REALMS = false;
	public static final boolean COOLDOWN = true;
	
	public static final NavigableMap<Integer, String> PROTOCOLS;
	static
	{
		TreeMap<Integer, String> protocols = new TreeMap<>();
		protocols.put(107, "1.9");
		protocols.put(108, "1.9.1");
		protocols.put(109, "1.9.2");
		protocols.put(110, "1.9.3/4");
		PROTOCOLS = Collections.unmodifiableNavigableMap(protocols);
	}
	
	private static final Minecraft mc = Minecraft.getMinecraft();
	
	public static EntityPlayerSP getPlayer()
	{
		return mc.thePlayer;
	}
	
	public static WorldClient getWorld()
	{
		return mc.theWorld;
	}
	
	public static NetHandlerPlayClient getConnection()
	{
		return getPlayer().sendQueue;
	}
	
	public static boolean isRunningOnMac()
	{
		return Minecraft.isRunningOnMac;
	}
}
