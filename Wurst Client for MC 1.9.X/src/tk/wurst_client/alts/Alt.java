/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.alts;

public class Alt
{
	private String email;
	private String name;
	private String password;
	private boolean cracked;
	private boolean starred;
	
	public Alt(String email, String password)
	{
		this(email, password, false);
	}
	
	public Alt(String email, String password, boolean starred)
	{
		this.email = email;
		this.starred = starred;
		if(password == null || password.isEmpty())
		{
			name = email;
			this.password = null;
			cracked = true;
		}else
		{
			name = LoginManager.getName(email, password);
			this.password = password;
			cracked = false;
		}
	}
	
	public Alt(String email, String name, String password)
	{
		this(email, name, password, false);
	}
	
	public Alt(String email, String name, String password, boolean starred)
	{
		this.email = email;
		this.starred = starred;
		if(password == null || password.isEmpty())
		{
			this.name = email;
			this.password = null;
			cracked = true;
		}else
		{
			this.name = name;
			this.password = password;
			cracked = false;
		}
	}
	
	public Alt(String crackedName)
	{
		this(crackedName, false);
	}
	
	public Alt(String crackedName, boolean starred)
	{
		email = crackedName;
		name = crackedName;
		password = null;
		cracked = true;
		this.starred = starred;
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public void setEmail(String email)
	{
		this.email = email;
		if(password == null || password.isEmpty())
		{
			name = email;
			password = null;
			cracked = true;
		}else
		{
			name = LoginManager.getName(email, password);
			cracked = false;
		}
	}
	
	public String getName()
	{
		if(name != null)
			return name;
		else if(email != null)
			return email;
		else
			return "";
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getPassword()
	{
		if(password == null || password.isEmpty())
		{
			cracked = true;
			return "";
		}else
			return password;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
		if(password == null || password.isEmpty())
		{
			name = email;
			this.password = null;
			cracked = true;
		}else
		{
			name = LoginManager.getName(email, password);
			this.password = password;
			cracked = false;
		}
	}
	
	public boolean isCracked()
	{
		return cracked;
	}
	
	public void setCracked()
	{
		name = email;
		password = null;
		cracked = true;
	}
	
	public boolean isStarred()
	{
		return starred;
	}
	
	public void setStarred(boolean starred)
	{
		this.starred = starred;
	}
}
