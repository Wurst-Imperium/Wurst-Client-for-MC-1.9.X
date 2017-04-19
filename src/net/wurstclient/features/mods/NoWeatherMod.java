package net.wurstclient.features.mods;

import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@Mod.Info(
	description = "Allows you to alter the client-side weather, time and moon phase.",
	name = "NoWeather")
@Mod.Bypasses
public final class NoWeatherMod extends Mod
{
	public CheckboxSetting disableRain =
		new CheckboxSetting("Disable Rain", true);
	
	public CheckboxSetting changeTime =
		new CheckboxSetting("Change World Time", false)
		{
			@Override
			public void update()
			{
				time.setDisabled(!isChecked());
			}
		};
	public SliderSetting time =
		new SliderSetting("Time", 6000, 0, 23900, 100, ValueDisplay.INTEGER);
	
	public CheckboxSetting changeMoonPhase =
		new CheckboxSetting("Change Moon Phase", false)
		{
			@Override
			public void update()
			{
				moonPhase.setDisabled(!isChecked());
			}
		};
	public SliderSetting moonPhase =
		new SliderSetting("Moon Phase", 0, 0, 7, 1, ValueDisplay.INTEGER);
	
	@Override
	public void initSettings()
	{
		settings.add(disableRain);
		settings.add(changeTime);
		settings.add(time);
		settings.add(changeMoonPhase);
		settings.add(moonPhase);
	}
}
