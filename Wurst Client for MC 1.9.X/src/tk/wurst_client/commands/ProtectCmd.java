/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.commands;

import net.minecraft.entity.EntityLivingBase;
import tk.wurst_client.utils.EntityUtils;

@Cmd.Info(help = "Toggles Protect or makes it protect a specific entity.",
	name = "protect",
	syntax = {"[<entity>]"})
public class ProtectCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws Error
	{
		if(args.length > 1)
			syntaxError();
		if(args.length == 0)
			wurst.mods.protectMod.toggle();
		else
		{
			if(wurst.mods.protectMod.isEnabled())
				wurst.mods.protectMod.setEnabled(false);
			EntityLivingBase entity = EntityUtils.searchEntityByName(args[0]);
			if(entity == null)
				error("Entity \"" + args[0] + "\" could not be found.");
			wurst.mods.protectMod.setEnabled(true);
			wurst.mods.protectMod.setFriend(entity);
		}
	}
}
