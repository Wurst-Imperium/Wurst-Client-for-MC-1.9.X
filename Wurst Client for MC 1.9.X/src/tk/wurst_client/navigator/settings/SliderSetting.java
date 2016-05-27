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

public abstract class SliderSetting implements NavigatorSetting
{
	private final String name;
	private double value;
	private String valueString;
	private final double minimum;
	private final double maximum;
	private final double increment;
	private int x;
	private int y;
	private float percentage;
	private final ValueDisplay valueDisplay;

	public SliderSetting(String name, double value, double minimum,
		double maximum, double increment, ValueDisplay display)
	{
		this.name = name;
		this.value = value;
		
		this.minimum = minimum;
		this.maximum = maximum;
		this.increment = increment;
		this.valueDisplay = display;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public void addToFeatureScreen(NavigatorFeatureScreen featureScreen)
	{
		featureScreen.addText("\n" + name + ":\n");
		y = 60 + featureScreen.getTextHeight();
		setValue(value);
		
		featureScreen.addSlider(this);
	}
	
	@Override
	public ArrayList<PossibleKeybind> getPossibleKeybinds(String featureName)
	{
		ArrayList<PossibleKeybind> possibleKeybinds = new ArrayList<>();
		String fullName = featureName + " " + name;
		String command =
			".setslider " + featureName.toLowerCase() + " "
				+ name.toLowerCase().replace(" ", "_") + " ";
		
		possibleKeybinds.add(new PossibleKeybind(command + "more", "Increase "
			+ fullName));
		possibleKeybinds.add(new PossibleKeybind(command + "less", "Decrease "
			+ fullName));
		
		return possibleKeybinds;
	}
	
	public double getValue()
	{
		return value;
	}
	
	public void setValue(double value)
	{
		this.value = value;
		
		valueString = valueDisplay.getValueString(value);
		percentage = (float)((value - minimum) / (maximum - minimum));
		x = (int)(percentage * 298) + 1;
		
		update();
	}
	
	public void increaseValue()
	{
		setValue(getValue() + increment);
	}
	
	public void decreaseValue()
	{
		setValue(getValue() - increment);
	}
	
	public String getValueString()
	{
		return valueString;
	}

	public double getMinimum()
	{
		return minimum;
	}
	
	public double getMaximum()
	{
		return maximum;
	}
	
	public double getIncrement()
	{
		return increment;
	}
	
	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public float getPercentage()
	{
		return percentage;
	}

	@Override
	public void save(JsonObject json)
	{
		json.addProperty(name, getValue());
	}
	
	@Override
	public void load(JsonObject json)
	{
		value = json.get(name).getAsDouble();
	}
	
	@Override
	public void update()
	{	
		
	}
	
	public static enum ValueDisplay
	{
		DECIMAL((v) -> Double.toString(v)),
		INTEGER((v) -> Integer.toString((int)v)),
		PERCENTAGE((v) -> v * 1e6 * 100D * 1e6 / 1e12 + "%"),
		DEGREES((v) -> (int)v + "°"),
		NONE((v) -> {
			return "";
		});
		
		private ValueProcessor processor;
		
		private ValueDisplay(ValueProcessor processor)
		{
			this.processor = processor;
		}
		
		public String getValueString(double value)
		{
			return processor.getValueString(value);
		}
		
		private interface ValueProcessor
		{
			public String getValueString(double value);
		}
	}
}
