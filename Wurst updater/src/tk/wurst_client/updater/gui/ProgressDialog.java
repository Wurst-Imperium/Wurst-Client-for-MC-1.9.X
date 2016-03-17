/*
 * Copyright © 2014 - 2015 | Alexander01998 | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.updater.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class ProgressDialog extends JDialog
{
	private JLabel lblProgress;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		try
		{
			ProgressDialog dialog = new ProgressDialog();
			dialog.setVisible(true);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Create the dialog.
	 */
	public ProgressDialog()
	{
		setUndecorated(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setModal(true);
		setResizable(false);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(screen.width - 450, 0, 450, 200);
		getContentPane().setLayout(
			new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		Component glue = Box.createGlue();
		getContentPane().add(glue);
		
		JLabel lblImage = new JLabel("");
		lblImage.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblImage.setHorizontalAlignment(SwingConstants.CENTER);
		lblImage
			.setIcon(new ImageIcon(
				ProgressDialog.class
					.getResource("/tk/wurst_client/updater/resources/wurst_253x64.png")));
		getContentPane().add(lblImage);
		{
			lblProgress =
				new JLabel("<html>\r\n<center>\r\n<h1>Updating...</h1>");
			lblProgress.setAlignmentX(Component.CENTER_ALIGNMENT);
			lblProgress.setFont(new Font("Verdana", Font.PLAIN, 16));
			getContentPane().add(lblProgress);
			lblProgress.setHorizontalAlignment(SwingConstants.CENTER);
		}
		
		Component glue_1 = Box.createGlue();
		getContentPane().add(glue_1);
	}
	
	public void updateProgress(String line1, String line2)
	{
		lblProgress.setText("<html><center><h1>" + line1 + "</h1><p>" + line2
			+ "</p>");
	}
}
