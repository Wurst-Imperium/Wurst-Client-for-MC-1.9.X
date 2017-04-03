/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.EntityUtils;

@Mod.Info(
	description = "Allows you to see the world as someone else.\n"
		+ "Use the .rv command to make it target a specific entity.",
	name = "RemoteView",
	tags = "remote view",
	help = "Mods/RemoteView")
@Mod.Bypasses
@Mod.DontSaveState
public final class RemoteViewMod extends Mod implements UpdateListener
{
	private EntityPlayerSP newView = null;
	private double oldX;
	private double oldY;
	private double oldZ;
	private float oldYaw;
	private float oldPitch;
	private EntityLivingBase otherView = null;
	private static UUID otherID = null;
	private boolean wasInvisible;
	
	@Override
	public void onEnable()
	{
		if(EntityUtils.getClosestEntityRaw(false) == null)
		{
			ChatUtils.message("There is no nearby entity.");
			setEnabled(false);
			return;
		}
		oldX = WMinecraft.getPlayer().posX;
		oldY = WMinecraft.getPlayer().posY;
		oldZ = WMinecraft.getPlayer().posZ;
		oldYaw = WMinecraft.getPlayer().rotationYaw;
		oldPitch = WMinecraft.getPlayer().rotationPitch;
		WMinecraft.getPlayer().noClip = true;
		if(otherID == null)
			otherID = EntityUtils.getClosestEntityRaw(false).getUniqueID();
		otherView = EntityUtils.searchEntityByIdRaw(otherID);
		wasInvisible = otherView.isInvisibleToPlayer(WMinecraft.getPlayer());
		EntityOtherPlayerMP fakePlayer = new EntityOtherPlayerMP(
			WMinecraft.getWorld(), WMinecraft.getPlayer().getGameProfile());
		fakePlayer.clonePlayer(WMinecraft.getPlayer(), true);
		fakePlayer.copyLocationAndAnglesFrom(WMinecraft.getPlayer());
		fakePlayer.rotationYawHead = WMinecraft.getPlayer().rotationYawHead;
		WMinecraft.getWorld().addEntityToWorld(-69, fakePlayer);
		ChatUtils.message("Now viewing " + otherView.getName() + ".");
		wurst.events.add(UpdateListener.class, this);
	}
	
	public static void onEnabledByCommand(String viewName)
	{
		try
		{
			if(otherID == null && !viewName.equals(""))
				otherID =
					EntityUtils.searchEntityByNameRaw(viewName).getUniqueID();
			wurst.mods.remoteViewMod.toggle();
		}catch(NullPointerException e)
		{
			ChatUtils.error("Entity not found.");
		}
	}
	
	@Override
	public void onUpdate()
	{
		if(EntityUtils.searchEntityByIdRaw(otherID) == null)
		{
			setEnabled(false);
			return;
		}
		newView = WMinecraft.getPlayer();
		otherView = EntityUtils.searchEntityByIdRaw(otherID);
		newView.copyLocationAndAnglesFrom(otherView);
		WMinecraft.getPlayer().motionX = 0;
		WMinecraft.getPlayer().motionY = 0;
		WMinecraft.getPlayer().motionZ = 0;
		Minecraft.getMinecraft().thePlayer = newView;
		otherView.setInvisible(true);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		if(otherView != null)
		{
			ChatUtils.message("No longer viewing " + otherView.getName() + ".");
			otherView.setInvisible(wasInvisible);
			WMinecraft.getPlayer().noClip = false;
			WMinecraft.getPlayer().setPositionAndRotation(oldX, oldY, oldZ,
				oldYaw, oldPitch);
			WMinecraft.getWorld().removeEntityFromWorld(-69);
		}
		newView = null;
		otherView = null;
		otherID = null;
	}
}
