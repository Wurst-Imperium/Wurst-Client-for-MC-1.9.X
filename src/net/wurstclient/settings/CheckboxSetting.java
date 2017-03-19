/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.settings;

import java.util.ArrayList;

import com.google.gson.JsonObject;

import net.wurstclient.WurstClient;
import net.wurstclient.navigator.PossibleKeybind;
import net.wurstclient.navigator.gui.NavigatorFeatureScreen;

public class CheckboxSetting implements Setting
{
	private final String name;
	private boolean checked;
	private boolean locked;
	private boolean lockChecked;
	private int y;
	
	public CheckboxSetting(String name, boolean checked)
	{
		this.name = name;
		this.checked = checked;
	}
	
	@Override
	public final String getName()
	{
		return name;
	}
	
	@Override
	public final void addToFeatureScreen(NavigatorFeatureScreen featureScreen)
	{
		featureScreen.addText("\n\n");
		y = 60 + featureScreen.getTextHeight() - 8;
		update();
		
		featureScreen.addCheckbox(this);
	}
	
	@Override
	public ArrayList<PossibleKeybind> getPossibleKeybinds(String featureName)
	{
		ArrayList<PossibleKeybind> possibleKeybinds = new ArrayList<>();
		String fullName = featureName + " " + name;
		String command = ".setcheckbox " + featureName.toLowerCase() + " "
			+ name.toLowerCase().replace(" ", "_") + " ";
		
		possibleKeybinds
			.add(new PossibleKeybind(command + "toggle", "Toggle " + fullName));
		possibleKeybinds
			.add(new PossibleKeybind(command + "on", "Enable " + fullName));
		possibleKeybinds
			.add(new PossibleKeybind(command + "off", "Disable " + fullName));
		
		return possibleKeybinds;
	}
	
	public final boolean isChecked()
	{
		return locked ? lockChecked : checked;
	}
	
	public final void setChecked(boolean checked)
	{
		if(!locked)
		{
			this.checked = checked;
			update();
			WurstClient.INSTANCE.files.saveNavigatorData();
		}
	}
	
	public final void toggle()
	{
		setChecked(!isChecked());
	}
	
	public final void lock(boolean lockChecked)
	{
		this.lockChecked = lockChecked;
		locked = true;
		update();
	}
	
	public final void unlock()
	{
		locked = false;
		update();
	}
	
	public final boolean isLocked()
	{
		return locked;
	}
	
	public final int getY()
	{
		return y;
	}
	
	@Override
	public final void save(JsonObject json)
	{
		json.addProperty(name, checked);
	}
	
	@Override
	public final void load(JsonObject json)
	{
		checked = json.get(name).getAsBoolean();
	}
	
	@Override
	public void update()
	{
		
	}
}
