/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * Copyright (c) 2013, DarkStorm (darkstorm@evilminecraft.net)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this
 * list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package tk.wurst_client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import org.darkstorm.minecraft.gui.AbstractGuiManager;
import org.darkstorm.minecraft.gui.component.Button;
import org.darkstorm.minecraft.gui.component.ComboBox;
import org.darkstorm.minecraft.gui.component.Component;
import org.darkstorm.minecraft.gui.component.Frame;
import org.darkstorm.minecraft.gui.component.basic.BasicButton;
import org.darkstorm.minecraft.gui.component.basic.BasicComboBox;
import org.darkstorm.minecraft.gui.component.basic.BasicFrame;
import org.darkstorm.minecraft.gui.component.basic.BasicSlider;
import org.darkstorm.minecraft.gui.layout.GridLayoutManager;
import org.darkstorm.minecraft.gui.layout.GridLayoutManager.HorizontalGridConstraint;
import org.darkstorm.minecraft.gui.listener.ButtonListener;
import org.darkstorm.minecraft.gui.listener.ComboBoxListener;
import org.darkstorm.minecraft.gui.theme.Theme;
import org.darkstorm.minecraft.gui.theme.wurst.WurstTheme;

import tk.wurst_client.WurstClient;
import tk.wurst_client.gui.target.TargetFrame;
import tk.wurst_client.mods.AutoBuildMod;
import tk.wurst_client.mods.Mod;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.navigator.settings.NavigatorSetting;

/**
 * Minecraft GUI API
 *
 * This class is not actually intended for use; rather, you should use this as a
 * template for your actual GuiManager, as the creation of frames is highly
 * implementation-specific.
 *
 * @author DarkStorm (darkstorm@evilminecraft.net)
 * @author Alexander01998
 */
public final class GuiManager extends AbstractGuiManager
{
	private class ModuleFrame extends BasicFrame
	{
		private ModuleFrame()
		{}
		
		private ModuleFrame(String title)
		{
			super(title);
		}
	}
	
	private final AtomicBoolean setup;
	private final Map<Category, ModuleFrame> categoryFrames =
		new HashMap<Category, ModuleFrame>();
	
	public GuiManager()
	{
		setup = new AtomicBoolean();
	}
	
	@Override
	public void setup()
	{
		if(!setup.compareAndSet(false, true))
			return;
		
		ModuleFrame settingsFrame = new ModuleFrame("Settings");
		settingsFrame.setTheme(theme);
		settingsFrame.setLayoutManager(new GridLayoutManager(1, 0));
		settingsFrame.setVisible(true);
		settingsFrame.setClosable(false);
		settingsFrame.setMinimized(true);
		settingsFrame.setPinnable(true);
		addFrame(settingsFrame);
		for(final Mod mod : WurstClient.INSTANCE.mods.getAllMods())
		{
			ModuleFrame frame = categoryFrames.get(mod.getCategory());
			if(frame == null)
			{
				String name = mod.getCategory().name().toLowerCase();
				if(name.equalsIgnoreCase("HIDDEN"))
					continue;
				name =
					Character.toUpperCase(name.charAt(0)) + name.substring(1);
				if(name.equalsIgnoreCase("AUTOBUILD"))
					name = "AutoBuild";// Corrects the case.
				frame = new ModuleFrame(name);
				frame.setTheme(theme);
				frame.setLayoutManager(new GridLayoutManager(1, 0));
				frame.setVisible(true);
				frame.setClosable(false);
				frame.setMinimized(true);
				frame.setPinnable(true);
				addFrame(frame);
				categoryFrames.put(mod.getCategory(), frame);
			}
			Button button = new BasicButton(mod)
			{
				@Override
				public void update()
				{
					setForegroundColor(mod.isEnabled() ? Color.BLACK
						: Color.WHITE);
					if(mod.isEnabled())
						if(mod.isBlocked())
							setBackgroundColor(new Color(255, 0, 0, 96));
						else
							setBackgroundColor(new Color(0, 255, 0, 96));
					else
						setBackgroundColor(new Color(0, 0, 0, 0));
				}
			};
			button.addButtonListener(new ButtonListener()
			{
				@Override
				public void onButtonPress(Button button)
				{
					mod.toggle();
				}
			});
			frame.add(button);
			for(NavigatorSetting setting : mod.getSettings())
				if(setting instanceof BasicSlider)
				{
					BasicSlider slider = (BasicSlider)setting;
					slider.setModNamePrefix(mod.getName());
					settingsFrame.add(slider);
				}
		}
		
		// AutoBuild
		ModuleFrame autobuild = categoryFrames.get(Category.AUTOBUILD);
		ComboBox autoBuildBox =
			new BasicComboBox(
				AutoBuildMod.names.toArray(new String[AutoBuildMod.names.size()]));
		autoBuildBox.addComboBoxListener(new ComboBoxListener()
		{
			@Override
			public void onComboBoxSelectionChanged(ComboBox comboBox)
			{
				WurstClient.INSTANCE.mods.autoBuildMod.setTemplate(comboBox
					.getSelectedIndex());
			}
		});
		autoBuildBox.setSelectedIndex(WurstClient.INSTANCE.mods.autoBuildMod
			.getTemplate());
		autobuild.add(autoBuildBox, HorizontalGridConstraint.CENTER);
		categoryFrames.remove(Category.AUTOBUILD);
		
		// Target
		addFrame(new TargetFrame());
		
		resizeComponents();
		Minecraft minecraft = Minecraft.getMinecraft();
		int offsetX = 5, offsetY = 5;
		int scale = minecraft.gameSettings.guiScale;
		if(scale == 0)
			scale = 1000;
		int scaleFactor = 0;
		while(scaleFactor < scale
			&& minecraft.displayWidth / (scaleFactor + 1) >= 320
			&& minecraft.displayHeight / (scaleFactor + 1) >= 240)
			scaleFactor++;
		for(Frame frame : getFrames())
		{
			frame.setX(offsetX);
			frame.setY(offsetY);
			Dimension frameSize = frame.getSize();
			offsetX += frameSize.width + 5;
			if(offsetX + frameSize.width + 5 > minecraft.displayWidth
				/ scaleFactor)
			{
				offsetX = 5;
				int height = 0;
				if(frame.isMinimized())
					for(Rectangle area : frame.getTheme()
						.getUIForComponent(frame).getInteractableRegions(frame))
						height = Math.max(height, area.height);
				else
					height = frameSize.height;
				offsetY += height + 5;
			}
		}
		if(WurstClient.INSTANCE.files.gui.exists())
			WurstClient.INSTANCE.files.loadGUI(getFrames());
	}
	
	@Override
	protected void resizeComponents()
	{
		Theme theme = getTheme();
		Frame[] frames = getFrames();
		Button enable = new BasicButton("Enable", "");
		Button disable = new BasicButton("Disable", "");
		Dimension enableSize =
			theme.getUIForComponent(enable).getDefaultSize(enable);
		Dimension disableSize =
			theme.getUIForComponent(disable).getDefaultSize(disable);
		int buttonWidth = Math.max(enableSize.width, disableSize.width);
		int buttonHeight = Math.max(enableSize.height, disableSize.height);
		for(Frame frame : frames)
			if(frame instanceof ModuleFrame)
				for(Component component : frame.getChildren())
					if(component instanceof Button)
					{
						component.setWidth(buttonWidth);
						component.setHeight(buttonHeight);
					}
		recalculateSizes();
	}
	
	private void recalculateSizes()
	{
		// set all frames to optimal width
		Frame[] frames = getFrames();
		for(Frame frame : frames)
		{
			Dimension defaultDimension =
				frame.getTheme().getUIForComponent(frame).getDefaultSize(frame);
			frame.setWidth(defaultDimension.width);
			frame.setHeight(defaultDimension.height);
		}
		
		// ensure enough width for the title
		for(Frame frame : frames)
		{
			FontRenderer fontRenderer = ((WurstTheme)theme).getFontRenderer();
			int minWidth =
				Math.max(fontRenderer.getStringWidth(frame.getTitle()),
					fontRenderer.getStringWidth("+++++")) + 6;
			if(frame.isMinimizable())
				minWidth += fontRenderer.FONT_HEIGHT + 2;
			if(frame.isPinnable())
				minWidth += fontRenderer.FONT_HEIGHT + 2;
			if(frame.isClosable())
				minWidth += fontRenderer.FONT_HEIGHT + 2;
			if(frame.getWidth() < minWidth)
				frame.setWidth(minWidth);
			
			// also update position & size of children
			frame.layoutChildren();
		}
	}
}
