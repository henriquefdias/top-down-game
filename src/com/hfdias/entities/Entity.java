package com.hfdias.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.hfdias.main.Game;
import com.hfdias.world.Camera;

public class Entity {

	public static BufferedImage LIFEPACK_EN = Game.spritesheet.getSprite(6 * 16, 0, 16, 16);
	public static BufferedImage WEAPON_EN = Game.spritesheet.getSprite(7 * 16, 0, 16, 16);
	public static BufferedImage BULLET_EN = Game.spritesheet.getSprite(6 * 16, 16, 16, 16);
	public static BufferedImage ENEMY_EN = Game.spritesheet.getSprite(7 * 16, 16, 16, 16);
	public static BufferedImage ENEMY_FEEDBACK = Game.spritesheet.getSprite(7 * 16, 32, 16, 16);
	public static BufferedImage GUN_RIGHT = Game.spritesheet.getSprite(8 * 16, 0, 16, 16);
	public static BufferedImage GUN_LEFT = Game.spritesheet.getSprite(9 * 16, 0, 16, 16);
	public static BufferedImage GUN_RIGHT_DAMAGED = Game.spritesheet.getSprite(9 * 16, 16, 16, 16);
	public static BufferedImage GUN_LEFT_DAMAGED = Game.spritesheet.getSprite(9 * 16, 32, 16, 16);

	protected double x;
	protected double y;
	protected double z;
	protected int width;
	protected int height;

	private int maskx, masky, mwidth, mheight;

	private BufferedImage sprite;

	public Entity(int x, int y, int width, int height, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;

		this.maskx = 0;
		this.masky = 0;
		this.mwidth = width;
		this.mheight = height;
	}

	public void setMask(int maskx, int masky, int mwidth, int mheight) {
		this.maskx = maskx;
		this.masky = masky;
		this.mwidth = mwidth;
		this.mheight = mheight;
	}

	public void setX(int newX) {
		this.x = newX;
	}

	public void setY(int newY) {
		this.y = newY;
	}

	public void setWidth(int newWidth) {
		this.width = newWidth;
	}

	public void setHeight(int newHeight) {
		this.height = newHeight;
	}

	public int getX() {
		return (int) this.x;
	}

	public int getY() {
		return (int) this.y;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public void tick() {

	}
	
	public double calculateDistance(int x1, int y1, int x2, int y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	public static boolean isColliding(Entity e1, Entity e2) {
		Rectangle e1Mask = new Rectangle(e1.getX() + e1.maskx, e1.getY() + e1.masky, e1.mwidth, e1.mheight);
		Rectangle e2Mask = new Rectangle(e2.getX() + e2.maskx, e2.getY() + e2.masky, e2.mwidth, e2.mheight);
		if (e1Mask.intersects(e2Mask) && e1.z == e2.z) {
			return true;
		}
		return false;
	}

	public void render(Graphics g) {
		g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
		// g.setColor(Color.blue);
		// g.fillRect(this.getX() + maskx - Camera.x, this.getY() + masky - Camera.y,
		// mwidth, mheight);
	}
}
