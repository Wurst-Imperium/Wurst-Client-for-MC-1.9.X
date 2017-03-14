/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features;

import java.util.ArrayList;

import net.wurstclient.navigator.PossibleKeybind;
import net.wurstclient.navigator.settings.NavigatorSetting;

public interface Feature
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
	
	public String getHelpPage();
	
	public Feature[] getSeeAlso();
}
