/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.UpdateListener;

@Mod.Info(description = "Thousands of colors!", name = "LSD", help = "Mods/LSD")
@Mod.Bypasses
public final class LsdMod extends Mod implements UpdateListener
{
	@Override
	public void onToggle()
	{
		if(!OpenGlHelper.shadersSupported)
			mc.renderGlobal.loadRenderers();
	}
	
	@Override
	public void onEnable()
	{
		if(OpenGlHelper.shadersSupported)
			if(mc.getRenderViewEntity() instanceof EntityPlayer)
			{
				if(mc.entityRenderer.theShaderGroup != null)
					mc.entityRenderer.theShaderGroup.deleteShaderGroup();
				
				mc.entityRenderer.shaderIndex = 19;
				
				if(mc.entityRenderer.shaderIndex != EntityRenderer.shaderCount)
					mc.entityRenderer
						.loadShader(EntityRenderer.shaderResourceLocations[19]);
				else
					mc.entityRenderer.theShaderGroup = null;
			}
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(!OpenGlHelper.shadersSupported)
			WMinecraft.getPlayer().addPotionEffect(
				new PotionEffect(MobEffects.confusion, 10801220));
		mc.gameSettings.smoothCamera = isEnabled();
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		WMinecraft.getPlayer().removePotionEffect(MobEffects.confusion);
		if(mc.entityRenderer.theShaderGroup != null)
		{
			mc.entityRenderer.theShaderGroup.deleteShaderGroup();
			mc.entityRenderer.theShaderGroup = null;
		}
		mc.gameSettings.smoothCamera = false;
	}
}
