package org.darkstorm.minecraft.gui.component.basic;

import org.darkstorm.minecraft.gui.component.AbstractComponent;
import org.darkstorm.minecraft.gui.component.Label;

public class BasicLabel extends AbstractComponent implements Label
{
	protected String text;
	protected TextAlignment horizontalAlignment = TextAlignment.LEFT,
		verticalAlignment = TextAlignment.CENTER;
	
	public BasicLabel()
	{}
	
	public BasicLabel(String text)
	{
		this.text = text;
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
	public TextAlignment getHorizontalAlignment()
	{
		return horizontalAlignment;
	}
	
	@Override
	public TextAlignment getVerticalAlignment()
	{
		return verticalAlignment;
	}
	
	@Override
	public void setHorizontalAlignment(TextAlignment alignment)
	{
		horizontalAlignment = alignment;
	}
	
	@Override
	public void setVerticalAlignment(TextAlignment alignment)
	{
		verticalAlignment = alignment;
	}
}
