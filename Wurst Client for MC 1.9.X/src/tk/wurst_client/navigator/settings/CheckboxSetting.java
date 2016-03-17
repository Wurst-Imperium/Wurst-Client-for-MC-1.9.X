/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.navigator.settings;

import java.util.ArrayList;

import tk.wurst_client.WurstClient;
import tk.wurst_client.navigator.PossibleKeybind;
import tk.wurst_client.navigator.gui.NavigatorFeatureScreen;

import com.google.gson.JsonObject;

public class CheckboxSetting implements NavigatorSetting
{
	private String name;
	private boolean checked;
	
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
		featureScreen.addCheckbox(featureScreen.new CheckboxData(name, checked,
			60 + featureScreen.getTextHeight() - 8)
		{
			@Override
			public void toggle()
			{
				setChecked(checked);
				WurstClient.INSTANCE.files.saveNavigatorData();
			}
		});
	}
	
	@Override
	public ArrayList<PossibleKeybind> getPossibleKeybinds(String featureName)
	{
		ArrayList<PossibleKeybind> possibleKeybinds = new ArrayList<>();
		String fullName = featureName + " " + name;
		String command =
			".setcheckbox " + featureName.toLowerCase() + " "
				+ name.toLowerCase().replace(" ", "_") + " ";
		
		possibleKeybinds.add(new PossibleKeybind(command + "toggle", "Toggle "
			+ fullName));
		possibleKeybinds.add(new PossibleKeybind(command + "on", "Enable "
			+ fullName));
		possibleKeybinds.add(new PossibleKeybind(command + "off", "Disable "
			+ fullName));
		
		return possibleKeybinds;
	}
	
	public final boolean isChecked()
	{
		return checked;
	}
	
	public final void setChecked(boolean checked)
	{
		this.checked = checked;
		update();
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
