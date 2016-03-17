/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.commands;

import tk.wurst_client.commands.Cmd.Info;
import tk.wurst_client.mods.Mod;

@Info(help = "Toggles a mod.", name = "t", syntax = {"<mod> [(on|off)]"})
public class TCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws Error
	{
		int mode = -1;
		if(args.length == 1)
			mode = 0;
		else if(args.length == 2 && args[1].equalsIgnoreCase("on"))
			mode = 1;
		else if(args.length == 2 && args[1].equalsIgnoreCase("off"))
			mode = 2;
		else
			syntaxError();
		Mod mod = wurst.mods.getModByName(args[0]);
		if(mod == null)
			error("Could not find mod \"" + args[0] + "\".");
		if(mode == 0)
			mod.toggle();
		else if(mode == 1 && !mod.isEnabled())
			mod.setEnabled(true);
		else if(mode == 2 && mod.isEnabled())
			mod.setEnabled(false);
	}
}
