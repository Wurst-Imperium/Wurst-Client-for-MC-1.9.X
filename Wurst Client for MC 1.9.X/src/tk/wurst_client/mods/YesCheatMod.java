/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import java.util.HashSet;

import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.settings.ModeSetting;

@Info(category = Category.MISC,
	description = "Makes other mods bypass AntiCheat plugins or blocks them if\n"
		+ "they can't.",
	name = "YesCheat+",
	tags = "YesCheatPlus, NoCheat+, NoCheatPlus, AntiMAC, yes cheat plus, no cheat plus, anti mac, ncp bypasses",
	help = "Mods/YesCheat")
public class YesCheatMod extends Mod
{
	private final HashSet<Mod> blockedMods = new HashSet<Mod>();
	private int bypassLevel = 2;
	
	@Override
	public void initSettings()
	{
		settings.add(new ModeSetting("Bypass Level", BypassLevel.getNames(),
			bypassLevel)
		{
			@Override
			public void update()
			{
				bypassLevel = getSelected();
				
				if(isActive())
					onDisable();
				blockedMods.clear();
				if(isActive())
					onEnable();
			}
		});
	}
	
	@Override
	public void onEnable()
	{
		// TODO: Remove this
		if(wurst.mods.antiMacMod.isEnabled())
			wurst.mods.antiMacMod.setEnabled(false);
		
		if(blockedMods.isEmpty())
			for(Mod mod : wurst.mods.getAllMods())
				if(!getBypassLevel().doesBypass(mod.getBypasses()))
					blockedMods.add(mod);
		
		for(Mod mod : blockedMods)
			mod.setBlocked(true);
	}
	
	@Override
	public void onDisable()
	{
		for(Mod mod : blockedMods)
			mod.setBlocked(false);
	}
	
	public BypassLevel getBypassLevel()
	{
		return BypassLevel.values()[bypassLevel];
	}
	
	private interface BypassTest
	{
		public boolean doesBypass(Bypasses b);
	}
	
	public enum BypassLevel
	{
		MINEPLEX_ANTICHEAT("Mineplex AntiCheat", (b) -> b.mineplexAntiCheat()),
		ANTICHEAT("AntiCheat", (b) -> b.antiCheat()),
		OLDER_NCP("Older NoCheat+", (b) -> b.olderNCP()),
		LATEST_NCP("Latest NoCheat+", (b) -> b.latestNCP()),
		GHOST_MODE("Ghost Mode", (b) -> b.ghostMode());
		
		private final String name;
		private final BypassTest test;
		
		private BypassLevel(String name, BypassTest test)
		{
			this.name = name;
			this.test = test;
		}
		
		public static String[] getNames()
		{
			String[] names = new String[values().length];
			for(int i = 0; i < names.length; i++)
				names[i] = values()[i].name;
			return names;
		}
		
		public boolean doesBypass(Bypasses bypasses)
		{
			return test.doesBypass(bypasses);
		}
	}
}
