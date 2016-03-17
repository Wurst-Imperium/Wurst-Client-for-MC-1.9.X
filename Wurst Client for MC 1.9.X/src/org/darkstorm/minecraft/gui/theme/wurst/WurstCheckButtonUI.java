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

import net.minecraft.client.Minecraft;

import org.darkstorm.minecraft.gui.component.CheckButton;
import org.darkstorm.minecraft.gui.component.Component;
import org.darkstorm.minecraft.gui.theme.AbstractComponentUI;
import org.darkstorm.minecraft.gui.util.GuiManagerDisplayScreen;
import org.darkstorm.minecraft.gui.util.RenderUtil;
import org.lwjgl.input.Mouse;

public class WurstCheckButtonUI extends AbstractComponentUI<CheckButton>
{
	private final WurstTheme theme;
	
	WurstCheckButtonUI(WurstTheme theme)
	{
		super(CheckButton.class);
		this.theme = theme;
		
		foreground = Color.WHITE;
		background = new Color(0.125f, 0.125f, 0.125f, 0.25f);
	}
	
	@Override
	protected void renderComponent(CheckButton button)
	{
		translateComponent(button, false);
		
		// area
		Rectangle area = button.getArea();
		
		// GL settings
		glEnable(GL_BLEND);
		glDisable(GL_CULL_FACE);
		glDisable(GL_TEXTURE_2D);
		
		// background
		RenderUtil.setColor(button.getBackgroundColor());
		int size = area.height - 4;
		glBegin(GL_QUADS);
		{
			glVertex2d(2, 2);
			glVertex2d(size + 2, 2);
			glVertex2d(size + 2, size + 2);
			glVertex2d(2, size + 2);
		}
		glEnd();
		
		// border
		RenderUtil.boxShadow(2, 2, size + 2, size + 2);
		
		// mouse location
		Point mouse = RenderUtil.calculateMouseLocation();
		Component parent = button.getParent();
		while(parent != null)
		{
			mouse.x -= parent.getX();
			mouse.y -= parent.getY();
			parent = parent.getParent();
		}
		boolean hovering =
			area.contains(mouse)
				&& Minecraft.getMinecraft().currentScreen instanceof GuiManagerDisplayScreen;
		
		// check
		if(button.isSelected())
		{
			glColor4f(0f, 1f, 0f, hovering ? 0.5f : 0.375f);
			glBegin(GL_QUADS);
			{
				glVertex2d(4, size / 2 + 2);
				glVertex2d(size / 2 + 1, size - 1);
				glVertex2d(size / 2 + 1, size + 1);
				glVertex2d(3, size / 2 + 3);
				
				glVertex2d(size, 3);
				glVertex2d(size + 1, 4);
				glVertex2d(size / 2 + 1, size + 1);
				glVertex2d(size / 2 + 1, size - 1);
			}
			glEnd();
			glColor4f(0.125f, 0.125f, 0.125f, hovering ? 0.75f : 0.5f);
			glBegin(GL_LINE_LOOP);
			{
				glVertex2d(4, size / 2 + 2);
				glVertex2d(size / 2 + 1, size - 1);
				glVertex2d(size, 3);
				glVertex2d(size + 1, 4);
				
				glVertex2d(size / 2 + 1, size + 1);
				glVertex2d(3, size / 2 + 3);
				
			}
			glEnd();
		}
		
		// overlay
		if(hovering)
		{
			glColor4f(0.0f, 0.0f, 0.0f, Mouse.isButtonDown(0) ? 0.3f : 0.2f);
			glBegin(GL_QUADS);
			{
				glVertex2d(0, 0);
				glVertex2d(area.width, 0);
				glVertex2d(area.width, area.height);
				glVertex2d(0, area.height);
			}
			glEnd();
		}
		glEnable(GL_TEXTURE_2D);
		
		// text
		String text = button.getText();
		theme.getFontRenderer().drawString(text, size + 4,
			area.height / 2 - theme.getFontRenderer().FONT_HEIGHT / 2,
			RenderUtil.toRGBA(button.getForegroundColor()));
		
		glEnable(GL_CULL_FACE);
		glDisable(GL_BLEND);
		translateComponent(button, true);
	}
	
	@Override
	protected Dimension getDefaultComponentSize(CheckButton component)
	{
		return new Dimension(theme.getFontRenderer().getStringWidth(
			component.getText())
			+ theme.getFontRenderer().FONT_HEIGHT + 6,
			theme.getFontRenderer().FONT_HEIGHT + 4);
	}
	
	@Override
	protected Rectangle[] getInteractableComponentRegions(CheckButton component)
	{
		return new Rectangle[]{new Rectangle(0, 0, component.getWidth(),
			component.getHeight())};
	}
	
	@Override
	protected void handleComponentInteraction(CheckButton component,
		Point location, int button)
	{
		if(location.x <= component.getWidth()
			&& location.y <= component.getHeight() && button == 0)
			component.press();
	}
}
