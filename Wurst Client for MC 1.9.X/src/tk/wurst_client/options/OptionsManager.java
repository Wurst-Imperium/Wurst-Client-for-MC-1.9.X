/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.options;

import java.security.SecureRandom;

import tk.wurst_client.WurstClient;

public class OptionsManager
{
	public boolean autoReconnect = false;
	public boolean cleanupFailed = true;
	public boolean cleanupOutdated = true;
	public boolean cleanupRename = true;
	public boolean cleanupUnknown = true;
	public boolean cleanupGriefMe = false;
	public boolean forceOPDontWait = false;
	public boolean middleClickFriends = true;
	public boolean spamFont = false;
	public boolean wurstNews = true;
	
	public int modListMode = 0;
	public int fastbreakMode = 0;
	public int forceOPDelay = 1000;
	public int ghostHandID = 54;
	public int searchID = 116;
	public int serverFinderThreads = 128;
	public int spamDelay = 1000;
	public int throwAmount = 16;
	
	public String forceOPList = WurstClient.INSTANCE.files.wurstDir.getPath();
	public String lastLaunchedVersion = "0";
	
	public OptionsManager.GoogleAnalytics google_analytics =
		new OptionsManager.GoogleAnalytics();
	
	public class GoogleAnalytics
	{
		public boolean enabled = true;
		public int id = new SecureRandom().nextInt() & 0x7FFFFFFF;
		public long first_launch = System.currentTimeMillis() / 1000L;
		public long last_launch = System.currentTimeMillis() / 1000L;
		public int launches = 0;
	}
	
	public OptionsManager.Zoom zoom = new OptionsManager.Zoom();
	
	public class Zoom
	{
		public int keybind = 43;
		public float level = 2.8F;
		public boolean scroll = true;
	}
}
