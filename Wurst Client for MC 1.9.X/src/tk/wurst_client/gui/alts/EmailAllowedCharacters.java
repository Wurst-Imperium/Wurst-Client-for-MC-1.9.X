/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.gui.alts;

public class EmailAllowedCharacters
{
	/**
	 * Array of the special characters that are allowed in any text drawing of
	 * Minecraft.
	 */
	public static final char[] allowedCharactersArray = new char[]{'/', '\n',
		'\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"',
		':'};
	
	public static boolean isAllowedCharacter(char character)
	{
		return character >= 32 && character != 127;
	}
	
	/**
	 * Filter string by only keeping those characters for which
	 * isAllowedCharacter() returns true.
	 */
	public static String filterAllowedCharacters(String input)
	{
		StringBuilder var1 = new StringBuilder();
		char[] var2 = input.toCharArray();
		int var3 = var2.length;
		
		for(int var4 = 0; var4 < var3; ++var4)
		{
			char var5 = var2[var4];
			
			if(isAllowedCharacter(var5))
				var1.append(var5);
		}
		
		return var1.toString();
	}
}
