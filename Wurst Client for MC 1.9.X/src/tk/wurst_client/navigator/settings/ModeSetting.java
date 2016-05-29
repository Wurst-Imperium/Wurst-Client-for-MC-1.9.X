/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.navigator.settings;

import java.awt.Color;
import java.util.ArrayList;

import tk.wurst_client.WurstClient;
import tk.wurst_client.navigator.PossibleKeybind;
import tk.wurst_client.navigator.gui.NavigatorFeatureScreen;
import tk.wurst_client.navigator.gui.NavigatorFeatureScreen.ButtonData;

import com.google.gson.JsonObject;

public class ModeSetting implements NavigatorSetting
{
	private String name;
	private String[] modes;
	private int selected;
	
	public ModeSetting(String name, String[] modes, int selected)
	{
		this.name = name;
		this.modes = modes;
		this.selected = selected;
	}
	
	@Override
	public final String getName()
	{
		return name;
	}
	
	@Override
	public final void addToFeatureScreen(NavigatorFeatureScreen featureScreen)
	{
		// heading
		featureScreen.addText("\n" + name + ":");
		
		// buttons
		int y = 0;
		ButtonData[] buttons = new ButtonData[modes.length];
		for(int i = 0; i < modes.length; i++)
		{
			int x = featureScreen.getMiddleX();
			switch(i % 3)
			{
				case 0:
					x -= 148;
					featureScreen.addText("\n\n");
					y = 60 + featureScreen.getTextHeight() - 2;
					break;
				case 1:
					x -= 48;
					break;
				case 2:
					x += 52;
					break;
			}
			final int iFinal = i;
			ButtonData button =
				featureScreen.new ButtonData(x, y, 96, 16, modes[i],
					i == selected ? 0x00ff00 : 0x404040)
				{
					@Override
					public void press()
					{
						buttons[selected].color = new Color(0x404040);
						color = new Color(0x00ff00);
						setSelected(iFinal);
						WurstClient.INSTANCE.files.saveNavigatorData();
					}
				};
			buttons[i] = button;
			featureScreen.addButton(button);
		}
	}
	
	@Override
	public ArrayList<PossibleKeybind> getPossibleKeybinds(String featureName)
	{
		ArrayList<PossibleKeybind> possibleKeybinds = new ArrayList<>();
		String fullName = featureName + " " + name;
		String command =
			".setmode " + featureName.toLowerCase() + " "
				+ name.toLowerCase().replace(" ", "_") + " ";
		String description = "Set " + fullName + " to ";
		
		possibleKeybinds.add(new PossibleKeybind(command + "next", "Next "
			+ fullName));
		possibleKeybinds.add(new PossibleKeybind(command + "prev", "Previous "
			+ fullName));
		
		for(String mode : modes)
			possibleKeybinds.add(new PossibleKeybind(command
				+ mode.toLowerCase(), description + mode));
		
		return possibleKeybinds;
	}
	
	public final int getSelected()
	{
		return selected;
	}
	
	public final void setSelected(int selected)
	{
		this.selected = selected;
		update();
	}
	
	public final String[] getModes()
	{
		return modes;
	}
	
	public final String getSelectedMode()
	{
		return modes[selected];
	}

	public final void nextMode()
	{
		selected++;
		if(selected >= modes.length)
			selected = 0;
		update();
	}
	
	public final void prevMode()
	{
		selected--;
		if(selected <= -1)
			selected = modes.length - 1;
		update();
	}
	
	public final int indexOf(String mode)
	{
		for(int i = 0; i < modes.length; i++)
			if(modes[i].equalsIgnoreCase(mode))
				return i;
		
		return -1;
	}
	
	@Override
	public final void save(JsonObject json)
	{
		json.addProperty(name, selected);
	}
	
	@Override
	public final void load(JsonObject json)
	{
		setSelected(json.get(name).getAsInt());
	}

	@Override
	public void update()
	{
		
	}
}
