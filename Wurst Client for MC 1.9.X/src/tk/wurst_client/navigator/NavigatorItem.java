/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.navigator;

import java.util.ArrayList;

import tk.wurst_client.navigator.settings.NavigatorSetting;

public interface NavigatorItem
{
	public String getName();
	
	public String getType();
	
	public String getDescription();
	
	public boolean isEnabled();
	
	public boolean isBlocked();
	
	public String getTags();
	
	public ArrayList<NavigatorSetting> getSettings();
	
	public ArrayList<PossibleKeybind> getPossibleKeybinds();
	
	public String getPrimaryAction();
	
	public void doPrimaryAction();
	
	public String getTutorialPage();
	
	public NavigatorItem[] getSeeAlso();
}
