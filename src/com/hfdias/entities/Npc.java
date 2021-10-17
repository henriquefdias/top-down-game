package com.hfdias.entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.hfdias.main.Game;

public class Npc extends Entity{
	
	public String[] frases = new String[2];
	
	public boolean showMessage = false;
	public boolean show = false;
	
	public int curIndexMsg = 0;
	public int fraseIndex = 0;
	public int time = 0;
	public int maxTime = 5;

	public Npc(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		frases[0] = "Mamma Mia! Fala fiote beleza";
		frases[1] = "Its-a Me Carai!";
	}
	
	@Override
	public void tick() {
		int xPlayer = Game.player.getX();
		int yPlayer = Game.player.getY();
		
		int xNpc = (int)x;
		int yNpc = (int)y;
		
		if(Math.abs(xPlayer - xNpc) < 20 && Math.abs(yPlayer - yNpc) < 20) {
			if (!show) {
				showMessage = true;
				show = true;
			}
			
		} else {
			//showMessage = false;
		}

		time++;
		if (showMessage) {
			if(time >= maxTime) {
				time = 0;
				if (curIndexMsg < frases[fraseIndex].length())
					curIndexMsg++;
				else {
					if (fraseIndex < frases.length - 1) {
						fraseIndex++;
						curIndexMsg = 0;
					}
				}
			}
		}
	}
	
	@Override
	public void render(Graphics g) {
		super.render(g);
		if(showMessage) {
			g.setColor(Color.white);
			g.fillRect(9, 9, Game.WIDTH - 18, Game.HEIGHT - 18);
			g.setColor(Color.blue);
			g.fillRect(10,  10, Game.WIDTH - 20, Game.HEIGHT - 20);
			g.setFont(new Font("Arial", Font.BOLD, 9));
			g.setColor(Color.white);
			g.drawString(frases[fraseIndex].substring(0, curIndexMsg),	(int)x, (int)y);
			
			g.drawString("> Pressione Enter para fechar <", (int)x + 10, (int)y+14);
		}
	}

}
