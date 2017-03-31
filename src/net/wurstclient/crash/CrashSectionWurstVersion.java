/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.crash;

import java.util.concurrent.Callable;

import net.wurstclient.WurstClient;

public class CrashSectionWurstVersion implements Callable
{
	@Override
	public String call()
	{
		return WurstClient.VERSION + " (latest: "
			+ (WurstClient.INSTANCE.updater.getLatestVersion() == null
				? "unknown" : WurstClient.INSTANCE.updater.getLatestVersion())
			+ ")";
	}
}
