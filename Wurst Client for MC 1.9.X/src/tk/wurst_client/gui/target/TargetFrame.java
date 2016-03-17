/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.gui.target;

import net.minecraft.client.Minecraft;

import org.darkstorm.minecraft.gui.component.Button;
import org.darkstorm.minecraft.gui.component.basic.BasicButton;
import org.darkstorm.minecraft.gui.component.basic.BasicCheckButton;
import org.darkstorm.minecraft.gui.component.basic.BasicFrame;
import org.darkstorm.minecraft.gui.layout.GridLayoutManager;
import org.darkstorm.minecraft.gui.layout.GridLayoutManager.HorizontalGridConstraint;
import org.darkstorm.minecraft.gui.listener.ButtonListener;

import tk.wurst_client.WurstClient;
import tk.wurst_client.navigator.settings.CheckboxSetting;
import tk.wurst_client.navigator.settings.NavigatorSetting;

public class TargetFrame extends BasicFrame
{
	public TargetFrame()
	{
		setTitle("Target");
		setTheme(WurstClient.INSTANCE.gui.getTheme());
		setLayoutManager(new GridLayoutManager(1, 0));
		setVisible(true);
		setClosable(false);
		setMinimized(true);
		setPinnable(true);
		
		WurstClient wurst = WurstClient.INSTANCE;
		
		for(NavigatorSetting setting : wurst.special.targetSpf.getSettings())
		{
			if(!(setting instanceof CheckboxSetting))
				continue;
			
			CheckboxSetting checkboxSetting = (CheckboxSetting)setting;
			BasicCheckButton checkbox =
				new BasicCheckButton(checkboxSetting.getName());
			checkbox.setSelected(checkboxSetting.isChecked());
			checkbox.addButtonListener(new ButtonListener()
			{
				@Override
				public void onButtonPress(Button button)
				{
					checkboxSetting.setChecked(((BasicCheckButton)button)
						.isSelected());
					wurst.files.saveNavigatorData();
				}
			});
			add(checkbox, HorizontalGridConstraint.FILL);
		}
		
		BasicButton advancedBtn = new BasicButton("Team Settings", null);
		advancedBtn.addButtonListener(new ButtonListener()
		{
			@Override
			public void onButtonPress(Button button)
			{
				Minecraft.getMinecraft()
					.displayGuiScreen(
						new GuiTeamSettings(
							Minecraft.getMinecraft().currentScreen));
			}
		});
		add(advancedBtn);
	}
}
