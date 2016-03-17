package org.darkstorm.minecraft.gui.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.darkstorm.minecraft.gui.listener.ComponentListener;
import org.darkstorm.minecraft.gui.theme.ComponentUI;
import org.darkstorm.minecraft.gui.theme.Theme;

public abstract class AbstractComponent implements Component
{
	private Container parent = null;
	private Theme theme;
	
	protected Rectangle area = new Rectangle(0, 0, 0, 0);
	protected ComponentUI ui;
	protected Color foreground, background;
	protected boolean enabled = true, visible = true;
	
	private List<ComponentListener> listeners =
		new CopyOnWriteArrayList<ComponentListener>();
	
	@Override
	public void render()
	{
		if(ui == null)
			return;
		ui.render(this);
	}
	
	@Override
	public void update()
	{
		if(ui == null)
			return;
		ui.handleUpdate(this);
	}
	
	protected ComponentUI getUI()
	{
		return theme.getUIForComponent(this);
	}
	
	@Override
	public void onMousePress(int x, int y, int button)
	{
		if(ui != null)
			for(Rectangle area : ui.getInteractableRegions(this))
				if(area.contains(x, y))
				{
					ui.handleInteraction(this, new Point(x, y), button);
					break;
				}
	}
	
	@Override
	public void onMouseRelease(int x, int y, int button)
	{}
	
	@Override
	public Theme getTheme()
	{
		return theme;
	}
	
	@Override
	public void setTheme(Theme theme)
	{
		Theme oldTheme = this.theme;
		this.theme = theme;
		if(theme == null)
		{
			ui = null;
			foreground = null;
			background = null;
			return;
		}
		
		ui = getUI();
		boolean changeArea;
		if(oldTheme != null)
		{
			Dimension defaultSize =
				oldTheme.getUIForComponent(this).getDefaultSize(this);
			changeArea =
				area.width == defaultSize.width
					&& area.height == defaultSize.height;
		}else
			changeArea = area.equals(new Rectangle(0, 0, 0, 0));
		if(changeArea)
		{
			Dimension defaultSize = ui.getDefaultSize(this);
			area =
				new Rectangle(area.x, area.y, defaultSize.width,
					defaultSize.height);
		}
		foreground = ui.getDefaultForegroundColor(this);
		background = ui.getDefaultBackgroundColor(this);
	}
	
	@Override
	public int getX()
	{
		return area.x;
	}
	
	@Override
	public int getY()
	{
		return area.y;
	}
	
	@Override
	public int getWidth()
	{
		return area.width;
	}
	
	@Override
	public int getHeight()
	{
		return area.height;
	}
	
	@Override
	public void setX(int x)
	{
		area.x = x;
	}
	
	@Override
	public void setY(int y)
	{
		area.y = y;
	}
	
	@Override
	public void setWidth(int width)
	{
		area.width = width;
	}
	
	@Override
	public void setHeight(int height)
	{
		area.height = height;
	}
	
	@Override
	public Color getBackgroundColor()
	{
		return background;
	}
	
	@Override
	public Color getForegroundColor()
	{
		return foreground;
	}
	
	@Override
	public void setBackgroundColor(Color color)
	{
		background = color;
	}
	
	@Override
	public void setForegroundColor(Color color)
	{
		foreground = color;
	}
	
	@Override
	public Point getLocation()
	{
		return area.getLocation();
	}
	
	@Override
	public Dimension getSize()
	{
		return area.getSize();
	}
	
	@Override
	public Rectangle getArea()
	{
		return area;
	}
	
	@Override
	public Container getParent()
	{
		return parent;
	}
	
	@Override
	public void setParent(Container parent)
	{
		if(!parent.hasChild(this) || this.parent != null
			&& this.parent.hasChild(this))
			throw new IllegalArgumentException();
		this.parent = parent;
	}
	
	@Override
	public void resize()
	{
		Dimension defaultDimension = ui.getDefaultSize(this);
		setWidth(defaultDimension.width);
		setHeight(defaultDimension.height);
	}
	
	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		if(parent != null && !parent.isEnabled())
			this.enabled = false;
		else
			this.enabled = enabled;
	}
	
	@Override
	public boolean isVisible()
	{
		return visible;
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		if(parent != null && !parent.isVisible())
			this.visible = false;
		else
			this.visible = visible;
	}
	
	protected void addListener(ComponentListener listener)
	{
		listeners.add(listener);
	}
	
	protected void removeListener(ComponentListener listener)
	{
		listeners.remove(listener);
	}
	
	protected ComponentListener[] getListeners()
	{
		return listeners.toArray(new ComponentListener[listeners.size()]);
	}
}
