package org.darkstorm.minecraft.gui.component;

import org.darkstorm.minecraft.gui.listener.ButtonListener;

import tk.wurst_client.mods.Mod;

public interface Button extends Component, TextComponent
{
	public void press();
	
	public void addButtonListener(ButtonListener listener);
	
	public void removeButtonListener(ButtonListener listener);
	
	public ButtonGroup getGroup();
	
	public void setGroup(ButtonGroup group);
	
	public String getDescription();
	
	public Mod getMod();
}
