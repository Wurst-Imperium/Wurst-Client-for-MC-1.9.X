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

import org.darkstorm.minecraft.gui.component.ComboBox;
import org.darkstorm.minecraft.gui.component.Component;
import org.darkstorm.minecraft.gui.theme.AbstractComponentUI;
import org.darkstorm.minecraft.gui.util.GuiManagerDisplayScreen;
import org.darkstorm.minecraft.gui.util.RenderUtil;
import org.lwjgl.input.Mouse;

import tk.wurst_client.WurstClient;

public class WurstComboBoxUI extends AbstractComponentUI<ComboBox>
{
	private final WurstTheme theme;
	
	WurstComboBoxUI(WurstTheme theme)
	{
		super(ComboBox.class);
		this.theme = theme;
		
		foreground = Color.WHITE;
		background = new Color(64, 64, 64, 128);
	}
	
	@Override
	protected void renderComponent(ComboBox component)
	{
		translateComponent(component, false);
		
		// area
		Rectangle area = component.getArea();
		
		// max width
		int maxWidth = 0;
		for(String element : component.getElements())
			maxWidth =
				Math.max(maxWidth,
					theme.getFontRenderer().getStringWidth(element));
		
		// extended height
		int extendedHeight = 0;
		if(component.isSelected())
		{
			String[] elements = component.getElements();
			for(int i = 0; i < elements.length - 1; i++)
				extendedHeight += theme.getFontRenderer().FONT_HEIGHT + 2;
			extendedHeight += 2;
		}
		
		// mouse location
		Point mouse = RenderUtil.calculateMouseLocation();
		Component parent = component.getParent();
		while(parent != null)
		{
			mouse.x -= parent.getX();
			mouse.y -= parent.getY();
			parent = parent.getParent();
		}
		
		// hovering
		boolean hovering =
			mouse.x >= area.x
				&& mouse.x <= area.x + area.width
				&& mouse.y >= area.y + 1
				&& mouse.y <= area.y + area.height - 1
				&& Minecraft.getMinecraft().currentScreen instanceof GuiManagerDisplayScreen;
		
		// GL settings
		glEnable(GL_BLEND);
		glDisable(GL_CULL_FACE);
		glDisable(GL_TEXTURE_2D);
		
		// shadow
		RenderUtil.boxShadow(0, 1, area.width, area.height - 1);
		
		// background
		RenderUtil.setColor(component.getBackgroundColor());
		glBegin(GL_QUADS);
		{
			glVertex2d(0, area.height);
			glVertex2d(area.width, area.height);
			glVertex2d(area.width, area.height + extendedHeight);
			glVertex2d(0, area.height + extendedHeight);
		}
		glEnd();
		
		// extension shadow
		if(extendedHeight > 0)
			RenderUtil.boxShadow(0, area.height, area.width, area.height
				+ extendedHeight);
		
		glColor4f(0.0f, 0.0f, 0.0f, Mouse.isButtonDown(0) ? 0.5f : 0.3f);
		if(area.contains(mouse))
		{
			// hover overlay
			glBegin(GL_QUADS);
			{
				glVertex2d(0, 1);
				glVertex2d(area.width, 1);
				glVertex2d(area.width, area.height - 1);
				glVertex2d(0, area.height - 1);
			}
			glEnd();
		}else if(component.isSelected() && mouse.x >= area.x
			&& mouse.x <= area.x + area.width)
		{
			// item hover overlay
			int offset = component.getHeight();
			String[] elements = component.getElements();
			for(int i = 0; i < elements.length; i++)
			{
				if(i == component.getSelectedIndex())
					continue;
				int height = theme.getFontRenderer().FONT_HEIGHT + 2;
				if((component.getSelectedIndex() == 0 ? i == 1 : i == 0)
					|| (component.getSelectedIndex() == elements.length - 1
						? i == elements.length - 2 : i == elements.length - 1))
					height++;
				if(mouse.y >= area.y + offset
					&& mouse.y <= area.y + offset + height)
				{
					glBegin(GL_QUADS);
					{
						glVertex2d(0, offset);
						glVertex2d(0, offset + height);
						glVertex2d(area.width, offset + height);
						glVertex2d(area.width, offset);
					}
					glEnd();
					break;
				}
				offset += height;
			}
		}
		
		// item separator
		if(component.isSelected())
		{
			glColor4f(0.125f, 0.125f, 0.125f, 0.5f);
			int offset2 = component.getHeight();
			String[] elements = component.getElements();
			for(int i = 0; i < elements.length; i++)
			{
				if(i == component.getSelectedIndex())
					continue;
				int height = theme.getFontRenderer().FONT_HEIGHT + 2;
				if((component.getSelectedIndex() == 0 ? i == 1 : i == 0)
					|| (component.getSelectedIndex() == elements.length - 1
						? i == elements.length - 2 : i == elements.length - 1))
					height++;
				if(i != 0)
				{
					glBegin(GL_LINES);
					{
						glVertex2d(0, offset2);
						glVertex2d(area.width, offset2);
					}
					glEnd();
				}
				offset2 += height;
			}
		}
		
		int height = theme.getFontRenderer().FONT_HEIGHT + 4;
		
		// arrow
		glBegin(GL_TRIANGLES);
		{
			if(component.isSelected())
			{
				glColor4f(1f, 0f, 0f, hovering ? 0.5f : 0.375f);
				glVertex2d(maxWidth + 5 + height / 2d, height / 3d);
				glVertex2d(maxWidth + 3.5 + height / 3d, 2d * height / 3d);
				glVertex2d(maxWidth + 6.5 + 2d * height / 3d, 2d * height / 3d);
			}else
			{
				glColor4f(0f, 1f, 0f, hovering ? 0.5f : 0.375f);
				glVertex2d(maxWidth + 3.5 + height / 3d, height / 3d);
				glVertex2d(maxWidth + 6.5 + 2d * height / 3d, height / 3d);
				glVertex2d(maxWidth + 5 + height / 2d, 2d * height / 3d);
			}
		}
		glEnd();
		
		// arrow shadow
		glLineWidth(1f);
		glColor4f(0.125f, 0.125f, 0.125f, hovering ? 0.75f : 0.5f);
		glBegin(GL_LINE_LOOP);
		{
			if(component.isSelected())
			{
				glVertex2d(maxWidth + 5 + height / 2d, height / 3d);
				glVertex2d(maxWidth + 3.5 + height / 3d, 2d * height / 3d);
				glVertex2d(maxWidth + 6.5 + 2d * height / 3d, 2d * height / 3d);
			}else
			{
				glVertex2d(maxWidth + 3.5 + height / 3d, height / 3d);
				glVertex2d(maxWidth + 6.5 + 2d * height / 3d, height / 3d);
				glVertex2d(maxWidth + 5 + height / 2d, 2d * height / 3d);
			}
		}
		glEnd();
		
		// arrow separator
		glLineWidth(1.0f);
		glColor4f(0.125f, 0.125f, 0.125f, 0.25f);
		glBegin(GL_LINES);
		{
			glVertex2d(maxWidth + 6, 2);
			glVertex2d(maxWidth + 6, area.height - 2);
		}
		glEnd();
		
		// text
		glEnable(GL_TEXTURE_2D);
		String text = component.getSelectedElement();
		theme.getFontRenderer().drawString(text, 2,
			area.height / 2 - theme.getFontRenderer().FONT_HEIGHT / 2 - 1,
			RenderUtil.toRGBA(component.getForegroundColor()));
		
		// item text
		if(component.isSelected())
		{
			int offset = area.height + 1;
			String[] elements = component.getElements();
			for(int i = 0; i < elements.length; i++)
			{
				if(i == component.getSelectedIndex())
					continue;
				theme.getFontRenderer().drawString(
					elements[i],
					(area.width - theme.getFontRenderer().getStringWidth(
						elements[i])) / 2, offset,
					RenderUtil.toRGBA(component.getForegroundColor()));
				offset += theme.getFontRenderer().FONT_HEIGHT + 2;
			}
		}
		
		// GL resets
		glEnable(GL_CULL_FACE);
		glEnable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);
		
		translateComponent(component, true);
	}
	
	@Override
	protected Dimension getDefaultComponentSize(ComboBox component)
	{
		int maxWidth = 0;
		for(String element : component.getElements())
			maxWidth =
				Math.max(maxWidth,
					theme.getFontRenderer().getStringWidth(element));
		return new Dimension(
			maxWidth + 8 + theme.getFontRenderer().FONT_HEIGHT,
			theme.getFontRenderer().FONT_HEIGHT + 4);
	}
	
	@Override
	protected Rectangle[] getInteractableComponentRegions(ComboBox component)
	{
		int height = component.getHeight();
		if(component.isSelected())
		{
			for(int i = 0; i < component.getElements().length; i++)
				height += theme.getFontRenderer().FONT_HEIGHT + 2;
			height += 2;
		}
		return new Rectangle[]{new Rectangle(0, 0, component.getWidth(), height)};
	}
	
	@Override
	protected void handleComponentInteraction(ComboBox component,
		Point location, int button)
	{
		if(button != 0)
			return;
		if(location.x <= component.getWidth()
			&& location.y <= component.getHeight())
			component.setSelected(!component.isSelected());
		else if(location.x <= component.getWidth() && component.isSelected())
		{
			int offset = component.getHeight() + 2;
			String[] elements = component.getElements();
			for(int i = 0; i < elements.length; i++)
			{
				if(i == component.getSelectedIndex())
					continue;
				if(location.y >= offset
					&& location.y <= offset
						+ theme.getFontRenderer().FONT_HEIGHT)
				{
					component.setSelectedIndex(i);
					component.setSelected(false);
					WurstClient.INSTANCE.files.saveOptions();
					break;
				}
				offset += theme.getFontRenderer().FONT_HEIGHT + 2;
			}
		}
	}
}
