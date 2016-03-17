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

import org.darkstorm.minecraft.gui.component.Component;
import org.darkstorm.minecraft.gui.component.Frame;
import org.darkstorm.minecraft.gui.layout.Constraint;
import org.darkstorm.minecraft.gui.theme.AbstractComponentUI;
import org.darkstorm.minecraft.gui.util.GuiManagerDisplayScreen;
import org.darkstorm.minecraft.gui.util.RenderUtil;

import tk.wurst_client.WurstClient;

public class WurstFrameUI extends AbstractComponentUI<Frame>
{
	private final WurstTheme theme;
	
	WurstFrameUI(WurstTheme theme)
	{
		super(Frame.class);
		this.theme = theme;
		
		foreground = Color.WHITE;
		background = new Color(64, 64, 64, 128);
	}
	
	@Override
	protected void renderComponent(Frame component)
	{
		translateComponent(component, false);
		
		// area & font height
		int fontHeight = theme.getFontRenderer().FONT_HEIGHT;
		Rectangle area = new Rectangle(component.getArea());
		if(component.isMinimized())
			area.height = fontHeight + 4;
		
		// mouse location
		Point mouse = RenderUtil.calculateMouseLocation();
		Component parent = component;
		while(parent != null)
		{
			mouse.x -= parent.getX();
			mouse.y -= parent.getY();
			parent = parent.getParent();
		}
		
		// GL settings
		glEnable(GL_BLEND);
		glDisable(GL_CULL_FACE);
		glDisable(GL_TEXTURE_2D);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glShadeModel(GL_SMOOTH);
		
		// title bar background
		glColor4f(0.03125f, 0.03125f, 0.03125f, 0.5f);
		glBegin(GL_QUADS);
		{
			glVertex2d(0, 0);
			glVertex2d(area.width, 0);
			glVertex2d(area.width, fontHeight + 4);
			glVertex2d(0, fontHeight + 4);
		}
		glEnd();
		
		// frame background
		RenderUtil.setColor(background);
		glBegin(GL_QUADS);
		{
			glVertex2d(0, fontHeight + 4);
			glVertex2d(area.width, fontHeight + 4);
			glVertex2d(area.width, area.height);
			glVertex2d(0, area.height);
		}
		glEnd();
		RenderUtil.boxShadow(0, 0, area.width, area.height);
		
		// title bar icons
		int offset = component.getWidth() - 2;
		boolean[] checks =
			new boolean[]{component.isClosable(), component.isPinnable(),
				component.isMinimizable()};
		boolean[] overlays =
			new boolean[]{component.isClosable(), !component.isPinned(),
				component.isMinimized()};
		for(int i = 0; i < checks.length; i++)
		{
			if(!checks[i])
				continue;
			boolean hovering =
				mouse.x >= offset - fontHeight
					&& mouse.x <= offset
					&& mouse.y >= 2
					&& mouse.y <= fontHeight + 2
					&& Minecraft.getMinecraft().currentScreen instanceof GuiManagerDisplayScreen;
			
			// colors
			Color green = new Color(0f, 1f, 0f, hovering ? 0.5f : 0.375f);
			Color red = new Color(1f, 0f, 0f, hovering ? 0.5f : 0.375f);
			Color silver = new Color(1f, 1f, 1f, hovering ? 0.5f : 0.375f);
			Color shadow =
				new Color(0.125f, 0.125f, 0.125f, hovering ? 0.75f : 0.5f);
			
			// icon background
			glColor4f(0f, 0f, 0f, 0.25f);
			glBegin(GL_QUADS);
			{
				glVertex2d(offset - fontHeight, 2);
				glVertex2d(offset, 2);
				glVertex2d(offset, fontHeight + 2);
				glVertex2d(offset - fontHeight, fontHeight + 2);
			}
			glEnd();
			RenderUtil
				.boxShadow(offset - fontHeight, 2, offset, fontHeight + 2);
			
			// pin button
			if(i == 1 && overlays[i])
			{
				// if not pinned
				
				// knob
				RenderUtil.setColor(green);
				glBegin(GL_QUADS);
				{
					glVertex2d(offset - fontHeight / 3, 2);
					glVertex2d(offset, fontHeight / 3 + 2);
					glVertex2d(offset - fontHeight / 3, fontHeight / 3 * 2 + 2);
					glVertex2d(offset - fontHeight / 3 * 2, fontHeight / 3 + 2);
				}
				glEnd();
				glBegin(GL_QUADS);
				{
					glVertex2d(offset - fontHeight / 3 * 2 - 1,
						fontHeight / 3 + 1);
					glVertex2d(offset - fontHeight / 3 + 1,
						fontHeight / 3 * 2 + 3);
					glVertex2d(offset - fontHeight / 3, fontHeight / 3 * 2 + 4);
					glVertex2d(offset - fontHeight / 3 * 2 - 2,
						fontHeight / 3 + 2);
				}
				glEnd();
				
				// needle
				RenderUtil.setColor(silver);
				glBegin(GL_TRIANGLES);
				{
					glVertex2d(offset - fontHeight / 3 * 2, fontHeight / 3 + 4);
					glVertex2d(offset - fontHeight / 3 - 2,
						fontHeight / 3 * 2 + 2);
					glVertex2d(offset - fontHeight + 1.5, fontHeight + 0.5);
				}
				glEnd();
				
				// shadow
				glLineWidth(1f);
				RenderUtil.setColor(shadow);
				glBegin(GL_LINE_LOOP);
				{
					glVertex2d(offset - fontHeight / 3, 2);
					glVertex2d(offset, fontHeight / 3 + 2);
					glVertex2d(offset - fontHeight / 3, fontHeight / 3 * 2 + 2);
					glVertex2d(offset - fontHeight / 3 * 2, fontHeight / 3 + 2);
				}
				glEnd();
				glBegin(GL_LINE_LOOP);
				{
					glVertex2d(offset - fontHeight / 3 * 2 - 1,
						fontHeight / 3 + 1);
					glVertex2d(offset - fontHeight / 3 + 1,
						fontHeight / 3 * 2 + 3);
					glVertex2d(offset - fontHeight / 3, fontHeight / 3 * 2 + 4);
					glVertex2d(offset - fontHeight / 3 * 2 - 2,
						fontHeight / 3 + 2);
				}
				glEnd();
				glBegin(GL_LINE_LOOP);
				{
					glVertex2d(offset - fontHeight / 3 * 2, fontHeight / 3 + 4);
					glVertex2d(offset - fontHeight / 3 - 2,
						fontHeight / 3 * 2 + 2);
					glVertex2d(offset - fontHeight + 1.5, fontHeight + 0.5);
				}
				glEnd();
			}else if(i == 1)
			{
				// if pinned
				
				// knob
				RenderUtil.setColor(red);
				glBegin(GL_QUADS);
				{
					glVertex2d(offset - fontHeight / 3 * 2 - 1.5,
						fontHeight / 3 + 1);
					glVertex2d(offset - fontHeight / 3 + 0.5,
						fontHeight / 3 + 1);
					glVertex2d(offset - fontHeight / 3 + 0.5,
						fontHeight / 3 * 2 + 3);
					glVertex2d(offset - fontHeight / 3 * 2 - 1.5,
						fontHeight / 3 * 2 + 3);
				}
				glEnd();
				glBegin(GL_QUADS);
				{
					glVertex2d(offset - fontHeight / 3 * 2 - 2.5,
						fontHeight / 3 * 2 + 3);
					glVertex2d(offset - fontHeight / 3 + 1.5,
						fontHeight / 3 * 2 + 3);
					glVertex2d(offset - fontHeight / 3 + 1.5,
						fontHeight / 3 * 2 + 4.5);
					glVertex2d(offset - fontHeight / 3 * 2 - 2.5,
						fontHeight / 3 * 2 + 4.5);
				}
				glEnd();
				
				// needle
				RenderUtil.setColor(silver);
				glBegin(GL_QUADS);
				{
					glVertex2d(offset - fontHeight / 3 * 2,
						fontHeight / 3 * 2 + 4.5);
					glVertex2d(offset - fontHeight / 3 * 2 + 1.5,
						fontHeight / 3 * 2 + 4.5);
					glVertex2d(offset - fontHeight / 3 * 2 + 1.5,
						fontHeight + 2);
					glVertex2d(offset - fontHeight / 3 * 2, fontHeight + 2);
				}
				glEnd();
				
				// shadow
				glLineWidth(1f);
				RenderUtil.setColor(shadow);
				glBegin(GL_LINE_LOOP);
				{
					glVertex2d(offset - fontHeight / 3 * 2 - 1.5,
						fontHeight / 3 + 1);
					glVertex2d(offset - fontHeight / 3 + 0.5,
						fontHeight / 3 + 1);
					glVertex2d(offset - fontHeight / 3 + 0.5,
						fontHeight / 3 * 2 + 3);
					glVertex2d(offset - fontHeight / 3 * 2 - 1.5,
						fontHeight / 3 * 2 + 3);
				}
				glEnd();
				glBegin(GL_LINE_LOOP);
				{
					glVertex2d(offset - fontHeight / 3 * 2 - 2.5,
						fontHeight / 3 * 2 + 3);
					glVertex2d(offset - fontHeight / 3 + 1.5,
						fontHeight / 3 * 2 + 3);
					glVertex2d(offset - fontHeight / 3 + 1.5,
						fontHeight / 3 * 2 + 4.5);
					glVertex2d(offset - fontHeight / 3 * 2 - 2.5,
						fontHeight / 3 * 2 + 4.5);
				}
				glEnd();
				glBegin(GL_LINE_LOOP);
				{
					glVertex2d(offset - fontHeight / 3 * 2,
						fontHeight / 3 * 2 + 4.5);
					glVertex2d(offset - fontHeight / 3 * 2 + 1.5,
						fontHeight / 3 * 2 + 4.5);
					glVertex2d(offset - fontHeight / 3 * 2 + 1.5,
						fontHeight + 2);
					glVertex2d(offset - fontHeight / 3 * 2, fontHeight + 2);
				}
				glEnd();
			}
			
			// minimize button
			if(i == 2 && overlays[i])
			{
				// if minimized
				
				// arrow
				RenderUtil.setColor(green);
				glBegin(GL_TRIANGLES);
				{
					glVertex2d(offset - fontHeight + 1, 4.5);
					glVertex2d(offset - 1, 4.5);
					glVertex2d(offset - fontHeight / 2, fontHeight - 0.5);
				}
				glEnd();
				
				// shadow
				glLineWidth(1f);
				RenderUtil.setColor(shadow);
				glBegin(GL_LINE_LOOP);
				{
					glVertex2d(offset - fontHeight + 1, 4.5);
					glVertex2d(offset - 1, 4.5);
					glVertex2d(offset - fontHeight / 2, fontHeight - 0.5);
				}
				glEnd();
			}else if(i == 2)
			{
				// if not minimized
				
				// arrow
				RenderUtil.setColor(red);
				glBegin(GL_TRIANGLES);
				{
					glVertex2d(offset - fontHeight + 1, fontHeight - 1);
					glVertex2d(offset - 1, fontHeight - 1);
					glVertex2d(offset - fontHeight / 2, 4);
				}
				glEnd();
				
				// shadow
				glLineWidth(1f);
				RenderUtil.setColor(shadow);
				glBegin(GL_LINE_LOOP);
				{
					glVertex2d(offset - fontHeight + 1, fontHeight - 1);
					glVertex2d(offset - 1, fontHeight - 1);
					glVertex2d(offset - fontHeight / 2, 4);
				}
				glEnd();
			}
			
			// close button
			if(i == 0)
			{
				// cross
				RenderUtil.setColor(red);
				glBegin(GL_QUADS);
				{
					glVertex2d(offset - fontHeight + 2, 5);
					glVertex2d(offset - fontHeight + 3, 4);
					glVertex2d(offset - 2, fontHeight - 1);
					glVertex2d(offset - 3, fontHeight);
				}
				glEnd();
				glBegin(GL_QUADS);
				{
					glVertex2d(offset - 2, 5);
					glVertex2d(offset - 3, 4);
					glVertex2d(offset - fontHeight / 2, fontHeight / 2 + 1);
					glVertex2d(offset - fontHeight / 2 + 1, fontHeight / 2 + 2);
				}
				glEnd();
				glBegin(GL_QUADS);
				{
					glVertex2d(offset - fontHeight / 2, fontHeight / 2 + 3);
					glVertex2d(offset - fontHeight / 2 - 1, fontHeight / 2 + 2);
					glVertex2d(offset - fontHeight + 2, fontHeight - 1);
					glVertex2d(offset - fontHeight + 3, fontHeight);
				}
				glEnd();
				
				// shadow
				glLineWidth(1f);
				RenderUtil.setColor(shadow);
				glBegin(GL_LINE_LOOP);
				{
					glVertex2d(offset - fontHeight + 2, 5);
					glVertex2d(offset - fontHeight + 3, 4);
					glVertex2d(offset - fontHeight / 2, fontHeight / 2 + 1);
					glVertex2d(offset - 3, 4);
					glVertex2d(offset - 2, 5);
					glVertex2d(offset - fontHeight / 2 + 1, fontHeight / 2 + 2);
					glVertex2d(offset - 2, fontHeight - 1);
					glVertex2d(offset - 3, fontHeight);
					glVertex2d(offset - fontHeight / 2, fontHeight / 2 + 3);
					glVertex2d(offset - fontHeight + 3, fontHeight);
					glVertex2d(offset - fontHeight + 2, fontHeight - 1);
					glVertex2d(offset - fontHeight / 2 - 1, fontHeight / 2 + 2);
				}
				glEnd();
			}
			
			offset -= fontHeight + 2;
		}
		
		// title bar
		if(!component.isMinimized())
			RenderUtil
				.downShadow(0, fontHeight + 4, area.width, fontHeight + 5);
		glEnable(GL_TEXTURE_2D);
		theme.getFontRenderer().drawStringWithShadow(component.getTitle(), 2,
			1, RenderUtil.toRGBA(component.getForegroundColor()));
		
		glEnable(GL_CULL_FACE);
		glDisable(GL_BLEND);
		translateComponent(component, true);
	}
	
	@Override
	protected Rectangle getContainerChildRenderArea(Frame container)
	{
		Rectangle area = new Rectangle(container.getArea());
		area.x = 2;
		area.y = theme.getFontRenderer().FONT_HEIGHT + 6;
		area.width -= 4;
		area.height -= theme.getFontRenderer().FONT_HEIGHT + 8;
		return area;
	}
	
	@Override
	protected Dimension getDefaultComponentSize(Frame component)
	{
		Component[] children = component.getChildren();
		Rectangle[] areas = new Rectangle[children.length];
		Constraint[][] constraints = new Constraint[children.length][];
		for(int i = 0; i < children.length; i++)
		{
			Component child = children[i];
			Dimension size =
				child.getTheme().getUIForComponent(child).getDefaultSize(child);
			areas[i] = new Rectangle(0, 0, size.width, size.height);
			constraints[i] = component.getConstraints(child);
		}
		Dimension size =
			component.getLayoutManager().getOptimalPositionedSize(areas,
				constraints);
		size.width += 4;
		size.height += theme.getFontRenderer().FONT_HEIGHT + 8;
		return size;
	}
	
	@Override
	protected Rectangle[] getInteractableComponentRegions(Frame component)
	{
		return new Rectangle[]{new Rectangle(0, 0, component.getWidth(),
			theme.getFontRenderer().FONT_HEIGHT + 4)};
	}
	
	@Override
	protected void handleComponentInteraction(Frame component, Point location,
		int button)
	{
		if(button != 0)
			return;
		int offset = component.getWidth() - 2;
		int textHeight = theme.getFontRenderer().FONT_HEIGHT;
		if(component.isClosable())
		{
			if(location.x >= offset - textHeight && location.x <= offset
				&& location.y >= 2 && location.y <= textHeight + 2)
			{
				component.close();
				return;
			}
			offset -= textHeight + 2;
		}
		if(component.isPinnable())
		{
			if(location.x >= offset - textHeight && location.x <= offset
				&& location.y >= 2 && location.y <= textHeight + 2)
			{
				component.setPinned(!component.isPinned());
				WurstClient.INSTANCE.files.saveGUI(WurstClient.INSTANCE.gui
					.getFrames());
				return;
			}
			offset -= textHeight + 2;
		}
		if(component.isMinimizable())
		{
			if(location.x >= offset - textHeight && location.x <= offset
				&& location.y >= 2 && location.y <= textHeight + 2)
			{
				component.setMinimized(!component.isMinimized());
				WurstClient.INSTANCE.files.saveGUI(WurstClient.INSTANCE.gui
					.getFrames());
				return;
			}
			offset -= textHeight + 2;
		}
		if(location.x >= 0 && location.x <= offset && location.y >= 0
			&& location.y <= textHeight + 4)
		{
			component.setDragging(true);
			return;
		}
	}
}
