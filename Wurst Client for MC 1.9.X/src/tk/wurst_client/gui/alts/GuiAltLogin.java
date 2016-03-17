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
import tk.wurst_client.alts.LoginManager;
import tk.wurst_client.gui.main.GuiWurstMainMenu;

public class GuiAltLogin extends AltEditorScreen
{
	public GuiAltLogin(GuiScreen par1GuiScreen)
	{
		super(par1GuiScreen);
	}
	
	@Override
	protected String getDoneButtonText()
	{
		return "Login";
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
		{
			LoginManager.changeCrackedName(emailBox.getText());
			displayText = "";
		}else
			displayText =
				LoginManager.login(emailBox.getText(), passwordBox.getText());
		if(displayText.equals(""))
			mc.displayGuiScreen(new GuiWurstMainMenu());
		else
			errorTimer = 8;
	}
	
	@Override
	protected String getUrl()
	{
		return "/alt-manager/direct-login";
	}
	
	@Override
	protected String getTitle()
	{
		return "Login";
	}
}
