/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.navigator.settings;

import java.util.ArrayList;

import org.darkstorm.minecraft.gui.component.basic.BasicSlider;

import tk.wurst_client.navigator.PossibleKeybind;
import tk.wurst_client.navigator.gui.NavigatorFeatureScreen;

import com.google.gson.JsonObject;

public abstract class SliderSetting extends BasicSlider implements
	NavigatorSetting
{
	public SliderSetting()
	{
		super();
	}
	
	public SliderSetting(String text, double value, double minimum,
		double maximum, double increment, ValueDisplay display)
	{
		super(text, value, minimum, maximum, increment, display);
	}
	
	public SliderSetting(String text, double value, double minimum,
		double maximum, int increment)
	{
		super(text, value, minimum, maximum, increment);
	}
	
	public SliderSetting(String text, double value, double minimum,
		double maximum)
	{
		super(text, value, minimum, maximum);
	}
	
	public SliderSetting(String text, double value)
	{
		super(text, value);
	}
	
	public SliderSetting(String text)
	{
		super(text);
	}
	
	@Override
	public String getName()
	{
		return getText();
	}
	
	@Override
	public void addToFeatureScreen(NavigatorFeatureScreen featureScreen)
	{
		// text
		featureScreen.addText("\n" + getText() + ":\n");
		
		// slider
		featureScreen.addSlider(featureScreen.new SliderData(this,
			60 + featureScreen.getTextHeight()));
	}
	
	@Override
	public ArrayList<PossibleKeybind> getPossibleKeybinds(String featureName)
	{
		ArrayList<PossibleKeybind> possibleKeybinds = new ArrayList<>();
		String fullName = featureName + " " + getText();
		String command =
			".setslider " + featureName.toLowerCase() + " "
				+ getText().toLowerCase().replace(" ", "_") + " ";
		
		possibleKeybinds.add(new PossibleKeybind(command + "more", "Increase "
			+ fullName));
		possibleKeybinds.add(new PossibleKeybind(command + "less", "Decrease "
			+ fullName));
		
		return possibleKeybinds;
	}
	
	@Override
	public double getValue()
	{
		return super.getValue();
	}
	
	@Override
	public void setValue(double value)
	{
		super.setValue(value);
		update();
	}
	
	@Override
	public abstract void update();
	
	public void increaseValue()
	{
		setValue(getValue() + getIncrement());
	}
	
	public void decreaseValue()
	{
		setValue(getValue() - getIncrement());
	}
	
	@Override
	public void save(JsonObject json)
	{
		json.addProperty(getText(), getValue());
	}
	
	@Override
	public void load(JsonObject json)
	{
		setValue(json.get(getText()).getAsDouble());
	}
}
