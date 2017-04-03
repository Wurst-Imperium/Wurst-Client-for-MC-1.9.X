/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.special_features;

import net.wurstclient.utils.MiscUtils;

@Spf.Info(description = "Opens the changelog in your browser.",
	name = "Changelog",
	tags = "change log,new features,wurst update")
public final class ChangelogSpf extends Spf
{
	@Override
	public String getPrimaryAction()
	{
		return "View Changelog";
	}
	
	@Override
	public void doPrimaryAction()
	{
		MiscUtils.openLink("https://www.wurstclient.net/changelog/");
	}
}
