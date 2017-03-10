/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.gui.alts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import tk.wurst_client.WurstClient;
import tk.wurst_client.alts.Alt;
import tk.wurst_client.alts.LoginManager;

public class GuiAltAdd extends AltEditorScreen
{
	public GuiAltAdd(GuiScreen par1GuiScreen)
	{
		super(par1GuiScreen);
	}
	
	@Override
	protected String getDoneButtonText()
	{
		return "Add";
	}
	
	@Override
	protected String getEmailBoxText()
	{
		return Minecraft.getMinecraft().session.getUsername();
	}
	
	@Override
	protected String getPasswordBoxText()
	{
		return "";
	}
	
	@Override
	protected void onDoneButtonClick(GuiButton button)
	{
		if(passwordBox.getText().length() == 0)
		{// Cracked
			GuiAltList.alts.add(new Alt(emailBox.getText(), null, null));
			displayText = "";
		}else
		{// Premium
			displayText =
				LoginManager.login(emailBox.getText(), passwordBox.getText());
			if(displayText.equals(""))
				GuiAltList.alts.add(new Alt(emailBox.getText(), passwordBox
					.getText(), mc.session.getUsername()));
		}
		if(displayText.equals(""))
		{
			GuiAltList.sortAlts();
			WurstClient.INSTANCE.files.saveAlts();
			mc.displayGuiScreen(prevMenu);
		}else
			errorTimer = 8;
	}
	
	@Override
	protected String getUrl()
	{
		return "/alt-manager/add";
	}
	
	@Override
	protected String getTitle()
	{
		return "Add an Alt";
	}
}
