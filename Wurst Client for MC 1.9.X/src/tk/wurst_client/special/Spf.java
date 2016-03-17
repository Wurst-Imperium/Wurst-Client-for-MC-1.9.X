/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.special;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import tk.wurst_client.WurstClient;
import tk.wurst_client.navigator.NavigatorItem;
import tk.wurst_client.navigator.PossibleKeybind;
import tk.wurst_client.navigator.settings.NavigatorSetting;

public class Spf implements NavigatorItem
{
	private final String name = getClass().getAnnotation(Info.class).name();
	private final String description = getClass().getAnnotation(Info.class)
		.description();
	private final String tags = getClass().getAnnotation(Info.class).tags();
	private final String tutorial = getClass().getAnnotation(Info.class)
		.tutorial();
	protected ArrayList<NavigatorSetting> settings = new ArrayList<>();
	
	protected static final WurstClient wurst = WurstClient.INSTANCE;
	protected static final Minecraft mc = Minecraft.getMinecraft();
	
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Info
	{
		String name();
		
		String description();
		
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
		return "Special Feature";
	}
	
	@Override
	public String getDescription()
	{
		return description;
	}
	
	@Override
	public boolean isEnabled()
	{
		return false;
	}
	
	@Override
	public boolean isBlocked()
	{
		return false;
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
	public ArrayList<PossibleKeybind> getPossibleKeybinds()
	{
		ArrayList<PossibleKeybind> possibleKeybinds = new ArrayList<>();
		
		// settings keybinds
		for(NavigatorSetting setting : settings)
			possibleKeybinds.addAll(setting.getPossibleKeybinds(name));
		
		return possibleKeybinds;
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "";
	}
	
	@Override
	public void doPrimaryAction()
	{	
		
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
}
