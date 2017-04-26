/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.special_features;

import net.wurstclient.features.Feature;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Spf;

@SearchTags({"Force OP", "Session Stealer", "Account Stealer"})
@Spf.Info(help = "Special_Features/Force_OP_(Session_Stealer)")
public final class SessionStealerSpf extends Spf
{
	public SessionStealerSpf()
	{
		super("SessionStealer",
			"Allows you to temporarily steal the Minecraft account of another player. This can either be\n"
				+ "used to hack into the account of a server admin or as an alternative to alt accounts. Unlike\n"
				+ "alt accounts, however, session stealing does not allow you to change the skin or the\n"
				+ "password of the account.");
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.special.bookHackSpf, wurst.mods.forceOpMod};
	}
}
