/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.entity.Entity;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;
import net.wurstclient.features.special_features.TargetSpf;
import net.wurstclient.features.special_features.YesCheatSpf.BypassLevel;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.ColorsSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.EntityUtils.TargetSettings;
import net.wurstclient.utils.RotationUtils;

@Mod.Info(
	description = "Automatically attacks entities around you.\n"
		+ "Can be configured in various ways to attack only some entities and ignore others.",
	name = "Killaura",
	tags = "kill aura",
	help = "Mods/Killaura")
@Mod.Bypasses
public final class KillauraMod extends Mod implements UpdateListener
{
	public final CheckboxSetting useCooldown = !WMinecraft.COOLDOWN ? null
		: new CheckboxSetting("Use Attack Cooldown as Speed", true)
		{
			@Override
			public void update()
			{
				speed.setDisabled(isChecked());
			}
		};
	public final SliderSetting speed =
		new SliderSetting("Speed", 20, 0.1, 20, 0.1, ValueDisplay.DECIMAL);
	public final SliderSetting range =
		new SliderSetting("Range", 6, 1, 6, 0.05, ValueDisplay.DECIMAL);
	public final SliderSetting fov =
		new SliderSetting("FOV", 360, 30, 360, 10, ValueDisplay.DEGREES);
	public final CheckboxSetting hitThroughWalls =
		new CheckboxSetting("Hit through walls", false);
	
	public final CheckboxSetting useTarget =
		new CheckboxSetting("Use Target settings", true)
		{
			@Override
			public void update()
			{
				if(isChecked())
				{
					TargetSpf target = wurst.special.targetSpf;
					players.lock(target.players);
					animals.lock(target.animals);
					monsters.lock(target.monsters);
					golems.lock(target.golems);
					sleepingPlayers.lock(target.sleepingPlayers);
					invisiblePlayers.lock(target.invisiblePlayers);
					invisibleMobs.lock(target.invisibleMobs);
					teams.lock(target.teams);
					teamColors.lock(target.teamColors);
				}else
				{
					players.unlock();
					animals.unlock();
					monsters.unlock();
					golems.unlock();
					sleepingPlayers.unlock();
					invisiblePlayers.unlock();
					invisibleMobs.unlock();
					teams.unlock();
					teamColors.unlock();
				}
			}
		};
	public final CheckboxSetting players =
		new CheckboxSetting("Attack players", true);
	public final CheckboxSetting animals =
		new CheckboxSetting("Attack animals", true);
	public final CheckboxSetting monsters =
		new CheckboxSetting("Attack monsters", true);
	public final CheckboxSetting golems =
		new CheckboxSetting("Attack golems", true);
	
	public final CheckboxSetting sleepingPlayers =
		new CheckboxSetting("Attack sleeping players", false);
	public final CheckboxSetting invisiblePlayers =
		new CheckboxSetting("Attack invisible players", false);
	public final CheckboxSetting invisibleMobs =
		new CheckboxSetting("Attack invisible mobs", false);
	
	public final CheckboxSetting teams = new CheckboxSetting("Teams", false);
	public final ColorsSetting teamColors = new ColorsSetting("Team Colors",
		new boolean[]{true, true, true, true, true, true, true, true, true,
			true, true, true, true, true, true, true});
	
	private final TargetSettings targetSettings = new TargetSettings()
	{
		@Override
		public boolean targetBehindWalls()
		{
			return hitThroughWalls.isChecked();
		}
		
		@Override
		public float getRange()
		{
			return range.getValueF();
		}
		
		@Override
		public float getFOV()
		{
			return fov.getValueF();
		}
		
		@Override
		public boolean targetPlayers()
		{
			return players.isChecked();
		}
		
		@Override
		public boolean targetAnimals()
		{
			return animals.isChecked();
		}
		
		@Override
		public boolean targetMonsters()
		{
			return monsters.isChecked();
		}
		
		@Override
		public boolean targetGolems()
		{
			return golems.isChecked();
		}
		
		@Override
		public boolean targetSleepingPlayers()
		{
			return sleepingPlayers.isChecked();
		}
		
		@Override
		public boolean targetInvisiblePlayers()
		{
			return invisiblePlayers.isChecked();
		}
		
		@Override
		public boolean targetInvisibleMobs()
		{
			return invisibleMobs.isChecked();
		}
		
		@Override
		public boolean targetTeams()
		{
			return teams.isChecked();
		}
		
		@Override
		public boolean[] getTeamColors()
		{
			return teamColors.getSelected();
		}
	};
	
	@Override
	public void initSettings()
	{
		if(useCooldown != null)
			settings.add(useCooldown);
		
		settings.add(speed);
		settings.add(range);
		settings.add(fov);
		settings.add(hitThroughWalls);
		
		settings.add(useTarget);
		settings.add(players);
		settings.add(animals);
		settings.add(monsters);
		settings.add(golems);
		settings.add(sleepingPlayers);
		settings.add(invisiblePlayers);
		settings.add(invisibleMobs);
		settings.add(teams);
		settings.add(teamColors);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.special.targetSpf,
			wurst.mods.killauraLegitMod, wurst.mods.multiAuraMod,
			wurst.mods.clickAuraMod, wurst.mods.tpAuraMod,
			wurst.mods.triggerBotMod, wurst.mods.criticalsMod};
	}
	
	@Override
	public void onEnable()
	{
		// disable other killauras
		wurst.mods.killauraLegitMod.setEnabled(false);
		wurst.mods.multiAuraMod.setEnabled(false);
		wurst.mods.clickAuraMod.setEnabled(false);
		wurst.mods.tpAuraMod.setEnabled(false);
		wurst.mods.triggerBotMod.setEnabled(false);
		
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		// update timer
		updateMS();
		
		// check timer / cooldown
		if(useCooldown != null && useCooldown.isChecked()
			? WPlayer.getCooldown() < 1 : !hasTimePassedS(speed.getValueF()))
			return;
		
		// set entity
		Entity entity = EntityUtils.getBestEntityToAttack(targetSettings);
		if(entity == null)
			return;
		
		// prepare attack
		EntityUtils.prepareAttack();
		
		// face entity
		if(!RotationUtils.faceEntityPacket(entity))
			return;
		
		// attack entity
		EntityUtils.attackEntity(entity);
		
		// reset timer
		updateLastMS();
	}
	
	@Override
	public void onYesCheatUpdate(BypassLevel bypassLevel)
	{
		switch(bypassLevel)
		{
			default:
			case OFF:
			case MINEPLEX:
			speed.resetUsableMax();
			range.resetUsableMax();
			hitThroughWalls.unlock();
			break;
			
			case ANTICHEAT:
			case OLDER_NCP:
			case LATEST_NCP:
			speed.setUsableMax(12);
			range.setUsableMax(4.25);
			hitThroughWalls.unlock();
			break;
			
			case GHOST_MODE:
			speed.setUsableMax(12);
			range.setUsableMax(4.25);
			hitThroughWalls.lock(() -> false);
			break;
		}
	}
}
