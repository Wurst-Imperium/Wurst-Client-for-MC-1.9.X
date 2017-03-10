package org.darkstorm.minecraft.gui.component.basic;

import org.darkstorm.minecraft.gui.component.AbstractComponent;
import org.darkstorm.minecraft.gui.component.Button;
import org.darkstorm.minecraft.gui.component.ButtonGroup;
import org.darkstorm.minecraft.gui.listener.ButtonListener;
import org.darkstorm.minecraft.gui.listener.ComponentListener;

import tk.wurst_client.mods.Mod;

public class BasicButton extends AbstractComponent implements Button
{
	protected String text = "";
	protected ButtonGroup group;
	private String description;
	private Mod mod;
	
	public BasicButton()
	{}
	
	public BasicButton(String text, String description)
	{
		this.text = text;
		this.description = description;
	}
	
	public BasicButton(Mod mod)
	{
		text = mod.getName();
		if(mod.getDescription().isEmpty())
			description = "Error! This is a bug. Please report it.";
		else
			description = mod.getDescription();
		this.mod = mod;
	}
	
	@Override
	public String getText()
	{
		return text;
	}
	
	@Override
	public void setText(String text)
	{
		this.text = text;
	}
	
	@Override
	public String getDescription()
	{
		return description;
	}
	
	@Override
	public void press()
	{
		for(ComponentListener listener : getListeners())
			((ButtonListener)listener).onButtonPress(this);
	}
	
	@Override
	public void addButtonListener(ButtonListener listener)
	{
		addListener(listener);
	}
	
	@Override
	public void removeButtonListener(ButtonListener listener)
	{
		removeListener(listener);
	}
	
	@Override
	public ButtonGroup getGroup()
	{
		return group;
	}
	
	@Override
	public void setGroup(ButtonGroup group)
	{
		this.group = group;
	}
	
	@Override
	public Mod getMod()
	{
		return mod;
	}
}
