/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.client.Minecraft;
import tk.wurst_client.WurstClient;
import tk.wurst_client.gui.error.GuiError;
import tk.wurst_client.navigator.NavigatorItem;
import tk.wurst_client.navigator.PossibleKeybind;
import tk.wurst_client.navigator.settings.NavigatorSetting;

public class Mod implements NavigatorItem
{
	private final String name = getClass().getAnnotation(Info.class).name();
	private final String description = getClass().getAnnotation(Info.class)
		.description();
	private final Category category = getClass().getAnnotation(Info.class)
		.category();
	private final String tags = getClass().getAnnotation(Info.class).tags();
	private final String tutorial = getClass().getAnnotation(Info.class)
		.tutorial();
	private boolean enabled;
	private boolean blocked;
	private boolean active;
	protected ArrayList<NavigatorSetting> settings = new ArrayList<>();
	private long currentMS = 0L;
	protected long lastMS = -1L;
	
	protected static final WurstClient wurst = WurstClient.INSTANCE;
	protected static final Minecraft mc = Minecraft.getMinecraft();
	
	public enum Category
	{
		AUTOBUILD,
		BLOCKS,
		CHAT,
		COMBAT,
		EXPLOITS,
		FUN,
		HIDDEN,
		RENDER,
		MISC,
		MOVEMENT;
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Info
	{
		String name();
		
		String description();
		
		Category category();
		
		boolean noCheatCompatible() default true;
		
		String tags() default "";
		
		String tutorial() default "";
	}
	
	@Override
	public final String getName()
	{
		return name;
	}
	
	@Override
	public final String getType()
	{
		return "Mod";
	}
	
	public String getRenderName()
	{
		return name;
	}
	
	@Override
	public final String getDescription()
	{
		return description;
	}
	
	@Override
	public final String getTags()
	{
		return tags;
	}
	
	@Override
	public final ArrayList<NavigatorSetting> getSettings()
	{
		return settings;
	}
	
	@Override
	public final ArrayList<PossibleKeybind> getPossibleKeybinds()
	{
		// mod keybinds
		String dotT = ".t " + name.toLowerCase();
		ArrayList<PossibleKeybind> possibleKeybinds =
			new ArrayList<>(Arrays.asList(new PossibleKeybind(dotT, "Toggle "
				+ name), new PossibleKeybind(dotT + " on", "Enable " + name),
				new PossibleKeybind(dotT + " off", "Disable " + name)));
		
		// settings keybinds
		for(NavigatorSetting setting : settings)
			possibleKeybinds.addAll(setting.getPossibleKeybinds(name));
		
		return possibleKeybinds;
	}
	
	@Override
	public final String getPrimaryAction()
	{
		return enabled ? "Disable" : "Enable";
	}
	
	@Override
	public final void doPrimaryAction()
	{
		toggle();
	}
	
	@Override
	public final String getTutorialPage()
	{
		return tutorial;
	}
	
	@Override
	public NavigatorItem[] getSeeAlso()
	{
		return new NavigatorItem[0];
	}
	
	public final Category getCategory()
	{
		return category;
	}
	
	@Override
	public final boolean isEnabled()
	{
		return enabled;
	}
	
	public final boolean isActive()
	{
		return active;
	}
	
	public final void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
		active = enabled && !blocked;
		if(blocked && enabled)
			return;
		try
		{
			onToggle();
		}catch(Exception e)
		{
			Minecraft.getMinecraft().displayGuiScreen(
				new GuiError(e, this, "toggling", "Mod was toggled "
					+ (enabled ? "on" : "off") + "."));
		}
		if(enabled)
			try
			{
				onEnable();
			}catch(Exception e)
			{
				Minecraft.getMinecraft().displayGuiScreen(
					new GuiError(e, this, "enabling", ""));
			}
		else
			try
			{
				onDisable();
			}catch(Exception e)
			{
				Minecraft.getMinecraft().displayGuiScreen(
					new GuiError(e, this, "disabling", ""));
			}
		if(!WurstClient.INSTANCE.files.isModBlacklisted(this))
			WurstClient.INSTANCE.files.saveMods();
	}
	
	public final void enableOnStartup()
	{
		enabled = true;
		active = enabled && !blocked;
		try
		{
			onToggle();
		}catch(Exception e)
		{
			Minecraft.getMinecraft().displayGuiScreen(
				new GuiError(e, this, "toggling", "Mod was toggled "
					+ (enabled ? "on" : "off") + "."));
		}
		try
		{
			onEnable();
		}catch(Exception e)
		{
			Minecraft.getMinecraft().displayGuiScreen(
				new GuiError(e, this, "enabling", ""));
		}
	}
	
	public final void toggle()
	{
		setEnabled(!isEnabled());
	}
	
	@Override
	public boolean isBlocked()
	{
		return blocked;
	}
	
	public void setBlocked(boolean blocked)
	{
		this.blocked = blocked;
		active = enabled && !blocked;
		if(enabled)
		{
			try
			{
				onToggle();
			}catch(Exception e)
			{
				Minecraft.getMinecraft().displayGuiScreen(
					new GuiError(e, this, "toggling", "Mod was toggled "
						+ (blocked ? "off" : "on") + "."));
			}
			try
			{
				if(blocked)
					onDisable();
				else
					onEnable();
			}catch(Exception e)
			{
				Minecraft.getMinecraft().displayGuiScreen(
					new GuiError(e, this, blocked ? "disabling" : "enabling",
						""));
			}
		}
	}
	
	public final void noCheatMessage()
	{
		WurstClient.INSTANCE.chat.warning(name + " cannot bypass NoCheat+.");
	}
	
	public final void updateMS()
	{
		currentMS = System.currentTimeMillis();
	}
	
	public final void updateLastMS()
	{
		lastMS = System.currentTimeMillis();
	}
	
	public final boolean hasTimePassedM(long MS)
	{
		return currentMS >= lastMS + MS;
	}
	
	public final boolean hasTimePassedS(float speed)
	{
		return currentMS >= lastMS + (long)(1000 / speed);
	}
	
	public void onToggle()
	{}
	
	public void onEnable()
	{}
	
	public void onDisable()
	{}
	
	public void initSettings()
	{}
}
