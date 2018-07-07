package net.gobbob.mobends.client.gui.elements;

import net.gobbob.mobends.client.gui.GuiBendsMenu;
import net.gobbob.mobends.util.Draw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;

public class GuiIconButton
{
	public static final int WIDTH = 20;
	public static final int HEIGHT = 20;
	
	protected int x, y;
	protected int iconU, iconV, iconWidth, iconHeight;
	protected boolean hovered;
	
	public GuiIconButton(int iconU, int iconV, int iconWidth, int iconHeight)
	{
		this.x = 0;
		this.y = 0;
		this.iconU = iconU;
		this.iconV = iconV;
		this.iconWidth = iconWidth;
		this.iconHeight = iconHeight;
		this.hovered = false;
	}
	
	public void initGui(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void update(int mouseX, int mouseY)
	{
		hovered = mouseX >= x && mouseX <= x + WIDTH &&
				  mouseY >= y && mouseY <= y + HEIGHT;
	}
	
	public void display()
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiBendsMenu.ICONS_TEXTURE);
		int bgTextureY = hovered ? 64 : 44;
		Draw.texturedModalRect(x, y, 88, bgTextureY, WIDTH, HEIGHT);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		Draw.texturedModalRect(x + WIDTH/2 - this.iconWidth / 2, y + HEIGHT/2 - this.iconHeight / 2, this.iconU, this.iconV, this.iconWidth, this.iconHeight);
	}
	
	public boolean mouseClicked(int mouseX, int mouseY, int state)
	{
		return hovered;
	}
}
