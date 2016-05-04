/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import java.util.UUID;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.utils.EntityUtils;

@Info(category = Category.RENDER,
	description = "Allows you to see the world as someone else.\n"
		+ "Use the .rv command to make it target a specific entity.",
	name = "RemoteView",
	tags = "remote view",
	tutorial = "Mods/RemoteView")
public class RemoteViewMod extends Mod implements UpdateListener
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
			wurst.chat.message("There is no nearby entity.");
			setEnabled(false);
			return;
		}
		oldX = mc.thePlayer.posX;
		oldY = mc.thePlayer.posY;
		oldZ = mc.thePlayer.posZ;
		oldYaw = mc.thePlayer.rotationYaw;
		oldPitch = mc.thePlayer.rotationPitch;
		mc.thePlayer.noClip = true;
		if(otherID == null)
			otherID = EntityUtils.getClosestEntityRaw(false).getUniqueID();
		otherView = EntityUtils.searchEntityByIdRaw(otherID);
		wasInvisible = otherView.isInvisibleToPlayer(mc.thePlayer);
		EntityOtherPlayerMP fakePlayer =
			new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
		fakePlayer.clonePlayer(mc.thePlayer, true);
		fakePlayer.copyLocationAndAnglesFrom(mc.thePlayer);
		fakePlayer.rotationYawHead = mc.thePlayer.rotationYawHead;
		mc.theWorld.addEntityToWorld(-69, fakePlayer);
		wurst.chat.message("Now viewing " + otherView.getName() + ".");
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
			wurst.chat.error("Entity not found.");
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
		newView = mc.thePlayer;
		otherView = EntityUtils.searchEntityByIdRaw(otherID);
		newView.copyLocationAndAnglesFrom(otherView);
		mc.thePlayer.motionX = 0;
		mc.thePlayer.motionY = 0;
		mc.thePlayer.motionZ = 0;
		mc.thePlayer = newView;
		otherView.setInvisible(true);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		if(otherView != null)
		{
			wurst.chat
				.message("No longer viewing " + otherView.getName() + ".");
			otherView.setInvisible(wasInvisible);
			mc.thePlayer.noClip = false;
			mc.thePlayer.setPositionAndRotation(oldX, oldY, oldZ, oldYaw,
				oldPitch);
			mc.theWorld.removeEntityFromWorld(-69);
		}
		newView = null;
		otherView = null;
		otherID = null;
	}
}
