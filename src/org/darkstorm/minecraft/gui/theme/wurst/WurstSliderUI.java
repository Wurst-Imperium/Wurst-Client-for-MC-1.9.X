/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.darkstorm.minecraft.gui.theme.wurst;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import net.minecraft.client.gui.FontRenderer;

import org.darkstorm.minecraft.gui.component.Container;
import org.darkstorm.minecraft.gui.component.Slider;
import org.darkstorm.minecraft.gui.theme.AbstractComponentUI;
import org.darkstorm.minecraft.gui.util.RenderUtil;
import org.lwjgl.input.Mouse;

import tk.wurst_client.WurstClient;

public class WurstSliderUI extends AbstractComponentUI<Slider>
{
	private WurstTheme theme;
	
	public WurstSliderUI(WurstTheme theme)
	{
		super(Slider.class);
		this.theme = theme;
		
		foreground = Color.WHITE;
		background = new Color(128, 128, 128, 128 + 128 / 2);
	}
	
	@Override
	protected void renderComponent(Slider component)
	{
		translateComponent(component, false);
		
		// GL settings
		glEnable(GL_BLEND);
		glDisable(GL_CULL_FACE);
		
		// area & font
		Rectangle area = component.getArea();
		int fontSize = theme.getFontRenderer().FONT_HEIGHT;
		FontRenderer fontRenderer = theme.getFontRenderer();
		
		// text
		fontRenderer.drawString(component.getTextWithModPrefix(), 0, 0,
			RenderUtil.toRGBA(component.getForegroundColor()));
		
		// value
		String content = null;
		switch(component.getValueDisplay())
		{
			case DECIMAL:
				content =
					Double
						.toString((double)(Math.round(component.getValue()
							/ component.getIncrement()) * 1000000 * (long)(component
							.getIncrement() * 1000000)) / 1000000 / 1000000);
				break;
			case INTEGER:
				content =
					String.format("%,d",
						Long.valueOf(Math.round(component.getValue())));
				break;
			case DEGREES:
				content =
					String.format("%,d°",
						Long.valueOf(Math.round(component.getValue())));
				break;
			case PERCENTAGE:
				int percent =
					(int)Math.round((component.getValue() - component
						.getMinimumValue())
						/ (component.getMaximumValue() - component
							.getMinimumValue()) * 100D);
				content = String.format("%d%%", percent);
			default:
		}
		if(content != null)
		{
			String suffix = component.getContentSuffix();
			if(suffix != null && !suffix.trim().isEmpty())
				content = content.concat(" ").concat(suffix);
			fontRenderer.drawString(content, component.getWidth()
				- fontRenderer.getStringWidth(content), 0,
				RenderUtil.toRGBA(component.getForegroundColor()));
		}
		glDisable(GL_TEXTURE_2D);
		
		// line
		glColor4f(0.03125f, 0.03125f, 0.03125f, 0.25f);
		glBegin(GL_QUADS);
		{
			glVertex2d(1, fontSize + 4);
			glVertex2d(area.width - 1, fontSize + 4);
			glVertex2d(area.width - 1, area.height - 2);
			glVertex2d(1, area.height - 2);
		}
		glEnd();
		
		// line shadow
		glLineWidth(1.0f);
		glColor4f(0.125f, 0.125f, 0.125f, 0.5f);
		glBegin(GL_LINE_LOOP);
		{
			glVertex2d(1, fontSize + 4);
			glVertex2d(area.width - 1, fontSize + 4);
			glVertex2d(area.width - 1, area.height - 2);
			glVertex2d(1, area.height - 2);
		}
		glEnd();
		
		double sliderPercentage =
			(component.getValue() - component.getMinimumValue())
				/ (component.getMaximumValue() - component.getMinimumValue());
		
		// slider
		glColor4f(0.0f + (float)sliderPercentage,
			1.0f - (float)sliderPercentage, 0.0f, 0.5f);
		glBegin(GL_QUADS);
		{
			glVertex2d((area.width - 6) * sliderPercentage - 1, fontSize + 1);
			glVertex2d((area.width - 6) * sliderPercentage + 7, fontSize + 1);
			glVertex2d((area.width - 6) * sliderPercentage + 7, area.height + 1);
			glVertex2d((area.width - 6) * sliderPercentage - 1, area.height + 1);
		}
		glEnd();
		
		// slider shadow
		RenderUtil.boxShadow((area.width - 6) * sliderPercentage - 1,
			fontSize + 1, (area.width - 6) * sliderPercentage + 7,
			area.height + 1);
		
		// GL resets
		glEnable(GL_CULL_FACE);
		glEnable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);
		
		translateComponent(component, true);
	}
	
	@Override
	protected Dimension getDefaultComponentSize(Slider component)
	{
		return new Dimension(106, 8 + theme.getFontRenderer().FONT_HEIGHT);
	}
	
	@Override
	protected Rectangle[] getInteractableComponentRegions(Slider component)
	{
		return new Rectangle[]{new Rectangle(0,
			theme.getFontRenderer().FONT_HEIGHT + 2, component.getWidth(),
			component.getHeight() - theme.getFontRenderer().FONT_HEIGHT)};
	}
	
	@Override
	protected void handleComponentInteraction(Slider component, Point location,
		int button)
	{
		if(getInteractableComponentRegions(component)[0].contains(location)
			&& button == 0)
			if(Mouse.isButtonDown(button) && !component.isValueChanging())
				component.setValueChanging(true);
			else if(!Mouse.isButtonDown(button) && component.isValueChanging())
				component.setValueChanging(false);
	}
	
	@Override
	protected void handleComponentUpdate(Slider component)
	{
		if(component.isValueChanging())
		{
			if(!Mouse.isButtonDown(0))
			{
				component.setValueChanging(false);
				WurstClient.INSTANCE.files.saveNavigatorData();
				return;
			}
			Point mouse = RenderUtil.calculateMouseLocation();
			Container parent = component.getParent();
			if(parent != null)
				mouse.translate(-parent.getX(), -parent.getY());
			double percent =
				(double)(mouse.x - 4) / (double)(component.getWidth() - 6);
			double value =
				component.getMinimumValue()
					+ percent
					* (component.getMaximumValue() - component
						.getMinimumValue());
			component.setValue(value);
		}
	}
}
