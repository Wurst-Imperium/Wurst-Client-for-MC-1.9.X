package net.wurstclient.compatibility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;

public final class WMinecraft
{
	public static final String VERSION = "1.9";
	public static final boolean REALMS = false;
	public static final boolean COOLDOWN = true;
	
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
