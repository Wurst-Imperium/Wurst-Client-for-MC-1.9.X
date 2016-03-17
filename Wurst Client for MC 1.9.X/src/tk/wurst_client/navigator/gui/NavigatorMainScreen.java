/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.navigator.gui;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Rectangle;
import java.util.ArrayList;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import org.darkstorm.minecraft.gui.util.RenderUtil;
import org.lwjgl.input.Keyboard;

import tk.wurst_client.WurstClient;
import tk.wurst_client.font.Fonts;
import tk.wurst_client.navigator.Navigator;
import tk.wurst_client.navigator.NavigatorItem;
import tk.wurst_client.utils.MiscUtils;

public class NavigatorMainScreen extends NavigatorScreen
{
	private static ArrayList<NavigatorItem> navigatorDisplayList =
		new ArrayList<>();
	private GuiTextField searchBar;
	private int hoveredItem = -1;
	private boolean hoveringArrow;
	private int clickTimer = -1;
	private boolean expanding = false;
	
	public NavigatorMainScreen()
	{
		hasBackground = false;
		nonScrollableArea = 0;
		
		searchBar = new GuiTextField(0, Fonts.segoe22, 0, 32, 200, 20);
		searchBar.setEnableBackgroundDrawing(false);
		searchBar.setMaxStringLength(128);
		searchBar.setFocused(true);
		
		Navigator navigator = WurstClient.INSTANCE.navigator;
		navigator.copyNavigatorList(navigatorDisplayList);
		navigator.analytics.trackPageView("/", "Navigator");
	}
	
	@Override
	protected void onResize()
	{
		searchBar.xPosition = middleX - 100;
		setContentHeight(navigatorDisplayList.size() / 3 * 20);
	}
	
	@Override
	protected void onKeyPress(char typedChar, int keyCode)
	{
		if(keyCode == 1)
			if(clickTimer == -1)
				mc.displayGuiScreen((GuiScreen)null);
		
		if(clickTimer == -1)
		{
			String oldText = searchBar.getText();
			searchBar.textboxKeyTyped(typedChar, keyCode);
			String newText = searchBar.getText();
			Navigator navigator = WurstClient.INSTANCE.navigator;
			if(newText.isEmpty())
				navigator.copyNavigatorList(navigatorDisplayList);
			else if(!newText.equals(oldText))
			{
				newText = newText.toLowerCase().trim();
				navigator.getSearchResults(navigatorDisplayList, newText);
			}
			setContentHeight(navigatorDisplayList.size() / 3 * 20);
		}
	}
	
	@Override
	protected void onMouseClick(int x, int y, int button)
	{
		if(clickTimer == -1 && hoveredItem != -1)
			if(button == 0
				&& (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || hoveringArrow)
				|| button == 2)
			{
				// arrow click, shift click, wheel click
				expanding = true;
			}else if(button == 0)
			{
				// left click
				NavigatorItem item = navigatorDisplayList.get(hoveredItem);
				if(item.getPrimaryAction().isEmpty())
					expanding = true;
				else
				{
					item.doPrimaryAction();
					WurstClient wurst = WurstClient.INSTANCE;
					wurst.navigator.addPreference(item.getName());
					wurst.files.saveNavigatorData();
				}
			}else if(button == 1)
			{
				// right click
				NavigatorItem item = navigatorDisplayList.get(hoveredItem);
				if(item.getTutorialPage().isEmpty())
					return;
				MiscUtils.openLink("https://www.wurst-client.tk/wiki/"
					+ item.getTutorialPage());
				WurstClient wurst = WurstClient.INSTANCE;
				wurst.navigator.addPreference(item.getName());
				wurst.files.saveNavigatorData();
				wurst.navigator.analytics.trackEvent("tutorial", "open",
					item.getName());
			}
	}
	
	@Override
	protected void onMouseDrag(int x, int y, int button, long timeDragged)
	{	
		
	}
	
	@Override
	protected void onMouseRelease(int x, int y, int button)
	{	
		
	}
	
	@Override
	protected void onUpdate()
	{
		searchBar.updateCursorCounter();
		
		if(expanding)
			if(clickTimer < 4)
				clickTimer++;
			else
			{
				NavigatorItem item = navigatorDisplayList.get(hoveredItem);
				mc.displayGuiScreen(new NavigatorFeatureScreen(item, this));
				
				String query = searchBar.getText();
				if(query.isEmpty())
					WurstClient.INSTANCE.navigator.analytics.trackPageView(
						"/" + item.getType() + "/" + item.getName(), item.getName());
				else
					WurstClient.INSTANCE.navigator.analytics
						.trackPageViewFromSearch(
							item.getType() + item.getName(), item.getName(),
							"/", query);
			}
		else if(!expanding && clickTimer > -1)
			clickTimer--;
		scrollbarLocked = clickTimer != -1;
	}
	
	@Override
	protected void onRender(int mouseX, int mouseY, float partialTicks)
	{
		boolean clickTimerNotRunning = clickTimer == -1;
		
		// search bar
		if(clickTimerNotRunning)
		{
			Fonts.segoe22.drawString("Search: ", middleX - 150, 32, 0xffffff);
			searchBar.drawTextBox();
			glDisable(GL_TEXTURE_2D);
		}
		
		// feature list
		int x = middleX - 50;
		if(clickTimerNotRunning)
			hoveredItem = -1;
		RenderUtil.scissorBox(0, 59, width, height - 42);
		glEnable(GL_SCISSOR_TEST);
		for(int i = Math.max(-scroll * 3 / 20 - 3, 0); i < navigatorDisplayList
			.size(); i++)
		{
			// y position
			int y = 60 + i / 3 * 20 + scroll;
			if(y < 40)
				continue;
			if(y > height - 40)
				break;
			
			// x position
			int xi = 0;
			switch(i % 3)
			{
				case 0:
					xi = x - 104;
					break;
				case 1:
					xi = x;
					break;
				case 2:
					xi = x + 104;
					break;
			}
			
			// item & area
			NavigatorItem item = navigatorDisplayList.get(i);
			Rectangle area = new Rectangle(xi, y, 100, 16);
			
			// click animation
			if(!clickTimerNotRunning)
			{
				if(i != hoveredItem)
					continue;
				
				float factor;
				if(expanding)
					if(clickTimer == 4)
						factor = 1F;
					else
						factor = (clickTimer + partialTicks) / 4F;
				else if(clickTimer == 0)
					factor = 0F;
				else
					factor = (clickTimer - partialTicks) / 4F;
				float antiFactor = 1 - factor;
				
				area.x = (int)(area.x * antiFactor + (middleX - 154) * factor);
				area.y = (int)(area.y * antiFactor + 60 * factor);
				area.width = (int)(area.width * antiFactor + 308 * factor);
				area.height =
					(int)(area.height * antiFactor + (height - 103) * factor);
				
				drawBackgroundBox(area.x, area.y, area.x + area.width, area.y
					+ area.height);
			}else
			{
				// color
				boolean hovering = area.contains(mouseX, mouseY);
				if(hovering)
					hoveredItem = i;
				if(item.isEnabled())
					if(item.isBlocked())
						glColor4f(hovering ? 1F : 0.875F, 0F, 0F, 0.5F);
					else
						glColor4f(0F, hovering ? 1F : 0.875F, 0F, 0.5F);
				else if(hovering)
					glColor4f(0.375F, 0.375F, 0.375F, 0.5F);
				else
					glColor4f(0.25F, 0.25F, 0.25F, 0.5F);
				
				// box & shadow
				drawBox(area.x, area.y, area.x + area.width, area.y
					+ area.height);
				
				// separator
				int bx1 = area.x + area.width - area.height;
				int by1 = area.y + 2;
				int by2 = by1 + area.height - 4;
				glBegin(GL_LINES);
				{
					glVertex2i(bx1, by1);
					glVertex2i(bx1, by2);
				}
				glEnd();
				
				// hovering
				if(hovering)
					hoveringArrow = mouseX >= bx1;
				
				// arrow positions
				double oneThrird = area.height / 3D;
				double twoThrirds = area.height * 2D / 3D;
				double ax1 = bx1 + oneThrird - 2D;
				double ax2 = bx1 + twoThrirds + 2D;
				double ax3 = bx1 + area.height / 2D;
				double ay1 = area.y + oneThrird;
				double ay2 = area.y + twoThrirds;
				
				// arrow
				glColor4f(0f, 1f, 0f, hovering ? 0.75f : 0.375f);
				glBegin(GL_TRIANGLES);
				{
					glVertex2d(ax1, ay1);
					glVertex2d(ax2, ay1);
					glVertex2d(ax3, ay2);
				}
				glEnd();
				
				// arrow shadow
				glLineWidth(1f);
				glColor4f(0.125f, 0.125f, 0.125f, hovering ? 0.75f : 0.5f);
				glBegin(GL_LINE_LOOP);
				{
					glVertex2d(ax1, ay1);
					glVertex2d(ax2, ay1);
					glVertex2d(ax3, ay2);
				}
				glEnd();
				
				// text
				if(clickTimerNotRunning)
				{
					String buttonText = item.getName();
					Fonts.segoe15.drawString(buttonText, area.x + 4,
						area.y + 2, 0xffffff);
					glDisable(GL_TEXTURE_2D);
				}
			}
		}
		glDisable(GL_SCISSOR_TEST);
	}
	
	public void setExpanding(boolean expanding)
	{
		this.expanding = expanding;
	}
}
