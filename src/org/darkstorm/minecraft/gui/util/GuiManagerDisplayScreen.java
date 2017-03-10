package org.darkstorm.minecraft.gui.util;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Rectangle;
import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.darkstorm.minecraft.gui.GuiManager;
import org.darkstorm.minecraft.gui.component.Component;
import org.darkstorm.minecraft.gui.component.Frame;

public class GuiManagerDisplayScreen extends GuiScreen
{
	private final GuiManager guiManager;
	
	public GuiManagerDisplayScreen(GuiManager guiManager)
	{
		this.guiManager = guiManager;
	}
	
	@Override
	public void initGui()
	{
		buttonList.add(new GuiButton(0, 2, height - 20, 150, 16,
			"Switch to the new GUI"));
		buttonList
			.add(new GuiButton(1, 156, height - 20, 150, 16, "Learn More"));
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
	
	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException
	{
		super.mouseClicked(x, y, button);
		for(Frame frame : guiManager.getFrames())
		{
			if(!frame.isVisible())
				continue;
			if(!frame.isMinimized() && !frame.getArea().contains(x, y))
				for(Component component : frame.getChildren())
					for(Rectangle area : component.getTheme()
						.getUIForComponent(component)
						.getInteractableRegions(component))
						if(area.contains(x - frame.getX() - component.getX(), y
							- frame.getY() - component.getY()))
						{
							frame.onMousePress(x - frame.getX(),
								y - frame.getY(), button);
							guiManager.bringForward(frame);
							return;
						}
		}
		for(Frame frame : guiManager.getFrames())
		{
			if(!frame.isVisible())
				continue;
			if(!frame.isMinimized() && frame.getArea().contains(x, y))
			{
				frame.onMousePress(x - frame.getX(), y - frame.getY(), button);
				guiManager.bringForward(frame);
				break;
			}else if(frame.isMinimized())
				for(Rectangle area : frame.getTheme().getUIForComponent(frame)
					.getInteractableRegions(frame))
					if(area.contains(x - frame.getX(), y - frame.getY()))
					{
						frame.onMousePress(x - frame.getX(), y - frame.getY(),
							button);
						guiManager.bringForward(frame);
						return;
					}
		}
	}
	
	@Override
	public void mouseReleased(int x, int y, int button)
	{
		super.mouseReleased(x, y, button);
		for(Frame frame : guiManager.getFrames())
		{
			if(!frame.isVisible())
				continue;
			if(!frame.isMinimized() && !frame.getArea().contains(x, y))
				for(Component component : frame.getChildren())
					for(Rectangle area : component.getTheme()
						.getUIForComponent(component)
						.getInteractableRegions(component))
						if(area.contains(x - frame.getX() - component.getX(), y
							- frame.getY() - component.getY()))
						{
							frame.onMouseRelease(x - frame.getX(),
								y - frame.getY(), button);
							guiManager.bringForward(frame);
							return;
						}
		}
		for(Frame frame : guiManager.getFrames())
		{
			if(!frame.isVisible())
				continue;
			if(!frame.isMinimized() && frame.getArea().contains(x, y))
			{
				frame
					.onMouseRelease(x - frame.getX(), y - frame.getY(), button);
				guiManager.bringForward(frame);
				break;
			}else if(frame.isMinimized())
				for(Rectangle area : frame.getTheme().getUIForComponent(frame)
					.getInteractableRegions(frame))
					if(area.contains(x - frame.getX(), y - frame.getY()))
					{
						frame.onMouseRelease(x - frame.getX(),
							y - frame.getY(), button);
						guiManager.bringForward(frame);
						return;
					}
		}
	}
	
	@Override
	public void drawScreen(int par2, int par3, float par4)
	{
		guiManager.render();
		
		glDisable(GL_CULL_FACE);
		glDisable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		
		glColor4f(0.25F, 0.25F, 0.25F, 0.5F);
		glBegin(GL_QUADS);
		{
			glVertex2i(2, height - 42);
			glVertex2i(308, height - 42);
			glVertex2i(308, height - 2);
			glVertex2i(2, height - 2);
		}
		glEnd();
		RenderUtil.boxShadow(2, height - 42, 308, height - 2);
		glEnable(GL_CULL_FACE);
		glEnable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);
		drawString(fontRendererObj,
			"§lNotice:§r You are currently viewing the old GUI.", 4,
			height - 36, 0xffffff);
		super.drawScreen(par2, par3, par4);
	}
}
