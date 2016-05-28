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

public class SliderSetting implements NavigatorSetting
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
	
	private boolean locked;
	private double lockMinimum;
	private double lockMaximum;
	
	private boolean disabled;
	
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
	public final String getName()
	{
		return name;
	}
	
	@Override
	public final void addToFeatureScreen(NavigatorFeatureScreen featureScreen)
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
	
	public final double getValue()
	{
		return locked ? Math.min(Math.max(lockMinimum, value), lockMaximum)
			: value;
	}
	
	public final float getValueF()
	{
		return (float)getValue();
	}
	
	public final void setValue(double value)
	{
		if(!disabled)
			if(locked)
				this.value =
					Math.min(Math.max(lockMinimum, value), lockMaximum);
			else
				this.value = Math.min(Math.max(minimum, value), maximum);
		
		valueString = valueDisplay.getValueString(this.value);
		percentage = (float)((this.value - minimum) / (maximum - minimum));
		x = (int)(percentage * 298) + 1;
		
		update();
	}
	
	public final void increaseValue()
	{
		setValue(getValue() + increment);
	}
	
	public final void decreaseValue()
	{
		setValue(getValue() - increment);
	}
	
	public final String getValueString()
	{
		return valueString;
	}
	
	public final double getMinimum()
	{
		return minimum;
	}
	
	public final double getMaximum()
	{
		return maximum;
	}
	
	public final double getIncrement()
	{
		return increment;
	}
	
	public final void lockToMinMax(double lockMinimum, double lockMaximum)
	{
		this.lockMinimum = lockMinimum;
		this.lockMaximum = lockMaximum;
		locked = true;
		
		double lockValue = Math.min(Math.max(lockMinimum, value), lockMaximum);
		valueString = valueDisplay.getValueString(lockValue);
		percentage = (float)((lockValue - minimum) / (maximum - minimum));
		x = (int)(percentage * 298) + 1;
		
		update();
	}
	
	public final void lockToMin(double lockMinimum)
	{
		lockToMinMax(lockMinimum, maximum);
	}
	
	public final void lockToMax(double lockMaximum)
	{
		lockToMinMax(minimum, lockMaximum);
	}
	
	public final void lockToValue(double lockValue)
	{
		lockToMinMax(lockValue, lockValue);
	}
	
	public final void unlock()
	{
		locked = false;
		setValue(value);
	}
	
	public boolean isLocked()
	{
		return locked;
	}
	
	public int getLockMinX()
	{
		return (int)((lockMinimum - minimum) / (maximum - minimum) * 298);
	}
	
	public int getLockMaxX()
	{
		return (int)((lockMaximum - minimum) / (maximum - minimum) * 298);
	}
	
	public boolean isDisabled()
	{
		return disabled;
	}
	
	public void setDisabled(boolean disabled)
	{
		this.disabled = disabled;
	}
	
	public final int getX()
	{
		return x;
	}
	
	public final int getY()
	{
		return y;
	}
	
	public final float getPercentage()
	{
		return percentage;
	}
	
	@Override
	public final void save(JsonObject json)
	{
		json.addProperty(name, getValue());
	}
	
	@Override
	public final void load(JsonObject json)
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
