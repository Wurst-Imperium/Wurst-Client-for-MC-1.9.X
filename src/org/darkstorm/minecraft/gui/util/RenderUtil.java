package org.darkstorm.minecraft.gui.util;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL13.GL_SAMPLE_ALPHA_TO_COVERAGE;

import java.awt.Color;
import java.awt.Point;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class RenderUtil
{
	private static final Color outline = new Color(0, 0, 0, 128);
	private static final Color shadow1 = new Color(32, 32, 32, 192);
	private static final Color shadow2 = new Color(0, 0, 0, 0);
	
	public static void scissorBox(int x, int y, int xend, int yend)
	{
		int width = xend - x;
		int height = yend - y;
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		int factor = sr.getScaleFactor();
		int bottomY = Minecraft.getMinecraft().currentScreen.height - yend;
		glScissor(x * factor, bottomY * factor, width * factor,
			height * factor);
	}
	
	public static void setupLineSmooth()
	{
		glEnable(GL_BLEND);
		glDisable(GL_LIGHTING);
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_LINE_SMOOTH);
		glDisable(GL_TEXTURE_2D);
		glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_MULTISAMPLE);
		glEnable(GL_SAMPLE_ALPHA_TO_COVERAGE);
		glShadeModel(GL_SMOOTH);
	}
	
	public static void drawLine(double startX, double startY, double startZ,
		double endX, double endY, double endZ, float thickness)
	{
		glPushMatrix();
		setupLineSmooth();
		glLineWidth(thickness);
		glBegin(GL_LINES);
		glVertex3d(startX, startY, startZ);
		glVertex3d(endX, endY, endZ);
		glEnd();
		glDisable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);
		glDisable(GL_LINE_SMOOTH);
		glEnable(GL_LIGHTING);
		glEnable(GL_DEPTH_TEST);
		glDisable(GL_MULTISAMPLE);
		glDisable(GL_SAMPLE_ALPHA_TO_COVERAGE);
		glPopMatrix();
	}
	
	public void drawTexturedModalRect(int x, int y, int textureX, int textureY,
		int width, int height)
	{
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(x + 0, y + height, 0)
			.tex((textureX + 0) * f, (textureY + height) * f1).endVertex();
		vertexbuffer.pos(x + width, y + height, 0)
			.tex((textureX + width) * f, (textureY + height) * f1).endVertex();
		vertexbuffer.pos(x + width, y + 0, 0)
			.tex((textureX + width) * f, (textureY + 0) * f1).endVertex();
		vertexbuffer.pos(x + 0, y + 0, 0)
			.tex((textureX + 0) * f, (textureY + 0) * f1).endVertex();
		tessellator.draw();
	}
	
	public static void drawTexturedModalRect(int textureId, int posX, int posY,
		int width, int height)
	{
		double halfHeight = height / 2;
		double halfWidth = width / 2;
		
		glDisable(GL_CULL_FACE);
		glBindTexture(GL_TEXTURE_2D, textureId);
		glPushMatrix();
		glTranslated(posX + halfWidth, posY + halfHeight, 0);
		glScalef(width, height, 0.0f);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glColor4f(1F, 1F, 1F, 1f);
		glBegin(GL_TRIANGLES);
		glNormal3f(0, 0, 1);
		glTexCoord2f(1, 1);
		glVertex2d(1, 1);
		glTexCoord2f(0, 1);
		glVertex2d(-1, 1);
		glTexCoord2f(0, 0);
		glVertex2d(-1, -1);
		glTexCoord2f(0, 0);
		glVertex2d(-1, -1);
		glTexCoord2f(1, 0);
		glVertex2d(1, -1);
		glTexCoord2f(1, 1);
		glVertex2d(1, 1);
		glEnd();
		
		glDisable(GL_BLEND);
		glBindTexture(GL_TEXTURE_2D, 0);
		glPopMatrix();
	}
	
	public static int interpolateColor(int rgba1, int rgba2, float percent)
	{
		int r1 = rgba1 & 0xFF, g1 = rgba1 >> 8 & 0xFF, b1 = rgba1 >> 16 & 0xFF,
			a1 = rgba1 >> 24 & 0xFF;
		int r2 = rgba2 & 0xFF, g2 = rgba2 >> 8 & 0xFF, b2 = rgba2 >> 16 & 0xFF,
			a2 = rgba2 >> 24 & 0xFF;
		
		int r = (int)(r1 < r2 ? r1 + (r2 - r1) * percent
			: r2 + (r1 - r2) * percent);
		int g = (int)(g1 < g2 ? g1 + (g2 - g1) * percent
			: g2 + (g1 - g2) * percent);
		int b = (int)(b1 < b2 ? b1 + (b2 - b1) * percent
			: b2 + (b1 - b2) * percent);
		int a = (int)(a1 < a2 ? a1 + (a2 - a1) * percent
			: a2 + (a1 - a2) * percent);
		
		return r | g << 8 | b << 16 | a << 24;
	}
	
	public static void setColor(Color c)
	{
		glColor4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f,
			c.getAlpha() / 255f);
	}
	
	public static Color toColor(int rgba)
	{
		int r = rgba & 0xFF, g = rgba >> 8 & 0xFF, b = rgba >> 16 & 0xFF,
			a = rgba >> 24 & 0xFF;
		return new Color(r, g, b, a);
	}
	
	public static int toRGBA(Color c)
	{
		return c.getRed() | c.getGreen() << 8 | c.getBlue() << 16
			| c.getAlpha() << 24;
	}
	
	public static void setColor(int rgba)
	{
		int r = rgba & 0xFF, g = rgba >> 8 & 0xFF, b = rgba >> 16 & 0xFF,
			a = rgba >> 24 & 0xFF;
		glColor4b((byte)r, (byte)g, (byte)b, (byte)a);
	}
	
	public static Point calculateMouseLocation()
	{
		Minecraft minecraft = Minecraft.getMinecraft();
		int scale = minecraft.gameSettings.guiScale;
		if(scale == 0)
			scale = 1000;
		int scaleFactor = 0;
		while(scaleFactor < scale
			&& minecraft.displayWidth / (scaleFactor + 1) >= 320
			&& minecraft.displayHeight / (scaleFactor + 1) >= 240)
			scaleFactor++;
		return new Point(Mouse.getX() / scaleFactor,
			minecraft.displayHeight / scaleFactor - Mouse.getY() / scaleFactor
				- 1);
	}
	
	public static void boxShadow(double x1, double y1, double x2, double y2)
	{
		// outline positions
		double xi1 = x1 - 0.1;
		double xi2 = x2 + 0.1;
		double yi1 = y1 - 0.1;
		double yi2 = y2 + 0.1;
		
		// outline
		RenderUtil.setColor(outline);
		glLineWidth(1F);
		glBegin(GL_LINE_LOOP);
		{
			glVertex2d(xi1, yi1);
			glVertex2d(xi2, yi1);
			glVertex2d(xi2, yi2);
			glVertex2d(xi1, yi2);
		}
		glEnd();
		
		// shadow positions
		xi1 -= 0.9;
		xi2 += 0.9;
		yi1 -= 0.9;
		yi2 += 0.9;
		
		// top left
		glBegin(GL_POLYGON);
		{
			RenderUtil.setColor(shadow1);
			glVertex2d(x1, y1);
			glVertex2d(x2, y1);
			RenderUtil.setColor(shadow2);
			glVertex2d(xi2, yi1);
			glVertex2d(xi1, yi1);
			glVertex2d(xi1, yi2);
			RenderUtil.setColor(shadow1);
			glVertex2d(x1, y2);
		}
		glEnd();
		
		// bottom right
		glBegin(GL_POLYGON);
		{
			glVertex2d(x2, y2);
			glVertex2d(x2, y1);
			RenderUtil.setColor(shadow2);
			glVertex2d(xi2, yi1);
			glVertex2d(xi2, yi2);
			glVertex2d(xi1, yi2);
			RenderUtil.setColor(shadow1);
			glVertex2d(x1, y2);
		}
		glEnd();
	}
	
	public static void invertedBoxShadow(double x1, double y1, double x2,
		double y2)
	{
		// outline positions
		double xi1 = x1 + 0.1;
		double xi2 = x2 - 0.1;
		double yi1 = y1 + 0.1;
		double yi2 = y2 - 0.1;
		
		// outline
		RenderUtil.setColor(outline);
		glLineWidth(1F);
		glBegin(GL_LINE_LOOP);
		{
			glVertex2d(xi1, yi1);
			glVertex2d(xi2, yi1);
			glVertex2d(xi2, yi2);
			glVertex2d(xi1, yi2);
		}
		glEnd();
		
		// shadow positions
		xi1 += 0.9;
		xi2 -= 0.9;
		yi1 += 0.9;
		yi2 -= 0.9;
		
		// top left
		glBegin(GL_POLYGON);
		{
			RenderUtil.setColor(shadow1);
			glVertex2d(x1, y1);
			glVertex2d(x2, y1);
			RenderUtil.setColor(shadow2);
			glVertex2d(xi2, yi1);
			glVertex2d(xi1, yi1);
			glVertex2d(xi1, yi2);
			RenderUtil.setColor(shadow1);
			glVertex2d(x1, y2);
		}
		glEnd();
		
		// bottom right
		glBegin(GL_POLYGON);
		{
			glVertex2d(x2, y2);
			glVertex2d(x2, y1);
			RenderUtil.setColor(shadow2);
			glVertex2d(xi2, yi1);
			glVertex2d(xi2, yi2);
			glVertex2d(xi1, yi2);
			RenderUtil.setColor(shadow1);
			glVertex2d(x1, y2);
		}
		glEnd();
	}
	
	public static void downShadow(double x1, double y1, double x2, double y2)
	{
		// outline
		double yi1 = y1 + 0.1;
		RenderUtil.setColor(outline);
		glLineWidth(1F);
		glBegin(GL_LINES);
		{
			glVertex2d(x1, yi1);
			glVertex2d(x2, yi1);
		}
		glEnd();
		
		// shadow
		glBegin(GL_POLYGON);
		{
			RenderUtil.setColor(shadow1);
			glVertex2d(x1, y1);
			glVertex2d(x2, y1);
			RenderUtil.setColor(shadow2);
			glVertex2d(x2, y2);
			glVertex2d(x1, y2);
		}
		glEnd();
	}
}
