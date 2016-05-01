/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.gui.alts;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import tk.wurst_client.WurstClient;
import tk.wurst_client.alts.Alt;
import tk.wurst_client.alts.LoginManager;

public class GuiAltEdit extends AltEditorScreen
{
	private Alt editedAlt;
	
	public GuiAltEdit(GuiScreen par1GuiScreen, Alt editedAlt)
	{
		super(par1GuiScreen);
		this.editedAlt = editedAlt;
	}
	
	@Override
	protected String getDoneButtonText()
	{
		return "Save";
	}
	
	@Override
	protected String getEmailBoxText()
	{
		return editedAlt.getEmail();
	}
	
	@Override
	protected String getPasswordBoxText()
	{
		return editedAlt.getPassword();
	}
	
	@Override
	protected void onDoneButtonClick(GuiButton button)
	{// Save
		if(passwordBox.getText().length() == 0)
		{
			// Cracked
			displayText = "";
			GuiAltList.alts.set(GuiAltList.alts.indexOf(editedAlt), new Alt(
				emailBox.getText(), null, null, editedAlt.isStarred()));
			
		}else
		{
			// Premium
			displayText =
				LoginManager.login(emailBox.getText(), passwordBox.getText());
			if(displayText.equals(""))
				GuiAltList.alts.set(GuiAltList.alts.indexOf(editedAlt),
					new Alt(emailBox.getText(), passwordBox.getText(),
						mc.session.getUsername(), editedAlt.isStarred()));
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
		return "/alt-manager/edit";
	}
	
	@Override
	protected String getTitle()
	{
		return "Edit this Alt";
	}
}
