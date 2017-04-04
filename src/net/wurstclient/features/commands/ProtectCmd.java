/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.minecraft.entity.Entity;
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.EntityUtils.TargetSettings;

@Cmd.Info(
	description = "Toggles Protect or makes it protect a specific entity.",
	name = "protect",
	syntax = {"[<entity>]"},
	help = "Commands/protect")
public final class ProtectCmd extends Cmd
{
	private TargetSettings targetSettings = new TargetSettings()
	{
		@Override
		public boolean targetFriends()
		{
			return true;
		}
		
		@Override
		public boolean targetBehindWalls()
		{
			return true;
		};
	};
	
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(args.length > 1)
			syntaxError();
		if(args.length == 0)
			wurst.mods.protectMod.toggle();
		else
		{
			if(wurst.mods.protectMod.isEnabled())
				wurst.mods.protectMod.setEnabled(false);
			Entity entity =
				EntityUtils.getEntityWithName(args[0], targetSettings);
			if(entity == null)
				error("Entity \"" + args[0] + "\" could not be found.");
			wurst.mods.protectMod.setEnabled(true);
			wurst.mods.protectMod.setFriend(entity);
		}
	}
}
