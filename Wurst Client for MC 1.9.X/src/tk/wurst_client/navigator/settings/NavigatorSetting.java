/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.navigator.settings;

import java.util.ArrayList;

import tk.wurst_client.navigator.PossibleKeybind;
import tk.wurst_client.navigator.gui.NavigatorFeatureScreen;

import com.google.gson.JsonObject;

public interface NavigatorSetting
{
	public String getName();
	
	public void addToFeatureScreen(NavigatorFeatureScreen featureScreen);
	
	public ArrayList<PossibleKeybind> getPossibleKeybinds(String featureName);
	
	public void save(JsonObject json);
	
	public void load(JsonObject json);
	
	public void update();
}
