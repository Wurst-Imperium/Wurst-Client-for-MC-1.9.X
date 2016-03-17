/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.special;

import tk.wurst_client.navigator.NavigatorItem;

@Spf.Info(description = "Allows you to temporarily steal the Minecraft account of another player. This can either be\n"
	+ "used to hack into the account of a server admin or as an alternative to alt accounts. Unlike\n"
	+ "alt accounts, however, session stealing does not allow you to change the skin or the\n"
	+ "password of the account.",
	name = "SessionStealer",
	tags = "Force OP,Session Stealer,Account Stealer",
	tutorial = "Special_Features/Force_OP_(Session_Stealer)")
public class SessionStealerSpf extends Spf
{
	@Override
	public NavigatorItem[] getSeeAlso()
	{
		return new NavigatorItem[]{wurst.mods.opSignMod,
			wurst.special.bookHackSpf, wurst.mods.forceOpMod};
	}
}
