package com.hfdias.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.hfdias.main.Game;
import com.hfdias.world.Camera;
import com.hfdias.world.World;

public class BulletShoot extends Entity{
	
	private double dx, dy;
	private double spd = 4;
	
	private int life = 80, curLife = 0;
	
	public BulletShoot(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);
		this.dx = dx;
		this.dy = dy;
	}

	public void tick() {
		if(World.isFreeDynamic((int)(x+(dx*spd)), (int)(y+(dy*spd)), 3, 3)) {
			x+=dx * spd;
			y+=dy * spd;
		} else {
			Game.bullets.remove(this);
			World.generateParticles(100, (int)x, (int)y);
			System.out.println("Bala removida.");
			return;
		}
		curLife++;
		if(curLife == life) {
			Game.bullets.remove(this);
			World.generateParticles(100, (int)x, (int)y);
			return;
		}
	}
	
	public void render(Graphics g) {
		g.setColor(Color.yellow);
		g.fillOval(this.getX() - Camera.x, this.getY() - Camera.y, width, height);
	}
}
