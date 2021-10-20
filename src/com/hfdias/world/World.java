package com.hfdias.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;

import com.hfdias.entities.Bullet;
import com.hfdias.entities.Enemy;
import com.hfdias.entities.Entity;
import com.hfdias.entities.Flower;
import com.hfdias.entities.Lifepack;
import com.hfdias.entities.Particle;
import com.hfdias.entities.Player;
import com.hfdias.entities.Weapon;
import com.hfdias.graficos.Spritesheet;
import com.hfdias.main.Game;

public class World {

	public static Tile[] tiles;
	public static int WIDTH, HEIGHT;
	public static final int TILE_SIZE = 16;

	public World(String path) {
		/*
		Game.player.setX(0);
		Game.player.setY(0);
		WIDTH = 100;
		HEIGHT = 100;
		tiles = new Tile[WIDTH*HEIGHT];
		
		for (int xx = 0; xx < WIDTH; xx++) {
			for (int yy = 0; yy < HEIGHT; yy++) {
				tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, Tile.TILE_WALL);
			}
		}
		
		int dir = 0;
		int xx = 0, yy = 0;
		
		for (int i = 0; i < 200; i++) {
			tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.TILE_FLOOR);
			if(dir==0) {
				//direita
				if(xx < WIDTH) {
					xx++;
				}
			} else if(dir==1) {
				//esquerda
				if(xx > 0) {
					xx--;
				}
			} else if(dir==2) {
				//baixo
				if(yy < HEIGHT) {
					yy++;
				}
			} else if(dir==3) {
				//cima
				if(yy > 0) {
					yy--;
				}
			}
			
			if(Game.rand.nextInt(100) < 30) {
				dir = Game.rand.nextInt(4);
			}
			
			
		}
		*/
		// Acima está o método de geração aleatoria de mapa
		try {
			BufferedImage map = ImageIO.read(getClass().getResource(path));
			int[] pixels = new int[map.getWidth() * map.getHeight()];
			WIDTH = map.getWidth();
			HEIGHT = map.getHeight();
			tiles = new Tile[map.getWidth() * map.getHeight()];
			map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());
			for (int xx = 0; xx < map.getWidth(); xx++) {
				for (int yy = 0; yy < map.getHeight(); yy++) {
					int pixelAtual = pixels[xx + (yy * map.getWidth())];
					tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.TILE_FLOOR);
					if (pixelAtual == 0xFF000000) {
						// Floor
						tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.TILE_FLOOR);
					} else if (pixelAtual == 0xFFFFFFFF) {
						// Wall
						tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, Tile.TILE_WALL);
					} else if (pixelAtual == 0xFF4800FF) {
						// Player
						Game.player.setX(xx * 16);
						Game.player.setY(yy * 16);
					} else if (pixelAtual == 0xFFFF0000) {
						// Enemy
						Enemy en = new Enemy(xx * 16, yy * 16, 16, 16, Entity.ENEMY_EN);
						Game.entities.add(en);
						Game.enemies.add(en);
					} else if (pixelAtual == 0xFFFF6A00) {
						// Weapon
						Game.entities.add(new Weapon(xx * 16, yy * 16, 16, 16, Entity.WEAPON_EN));
					} else if (pixelAtual == 0xFFFFB27F) {
						// Life Pack
						Lifepack pack = new Lifepack(xx * 16, yy * 16, 16, 16, Entity.LIFEPACK_EN);
						pack.setMask(2, 6, 11, 10);
						Game.entities.add(pack);
					} else if (pixelAtual == 0xFFFFD800) {
						// Bullet
						Game.entities.add(new Bullet(xx * 16, yy * 16, 16, 16, Entity.BULLET_EN));
					} else if (pixelAtual == 0xFF4CFF00) {
						tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Entity.FLOWER_EN);
					}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Método de renderização de mapa via imagem pré desenhada
	}

	public static boolean isFreeDynamic(int xNext, int yNext, int width, int height) {
		int x1 = xNext / TILE_SIZE;
		int y1 = yNext / TILE_SIZE;

		int x2 = (xNext + width - 1) / TILE_SIZE;
		int y2 = yNext / TILE_SIZE;

		int x3 = xNext / TILE_SIZE;
		int y3 = (yNext + height - 1) / TILE_SIZE;

		int x4 = (xNext + width - 1) / TILE_SIZE;
		int y4 = (yNext + height - 1) / TILE_SIZE;

		if (!((tiles[x1 + (y1 * World.WIDTH)] instanceof WallTile)
				|| (tiles[x2 + (y2 * World.WIDTH)] instanceof WallTile)
				|| (tiles[x3 + (y3 * World.WIDTH)] instanceof WallTile)
				|| (tiles[x4 + (y4 * World.WIDTH)] instanceof WallTile))) {
			return true;
		}
		return false;
	}
	
	public static boolean isFree(int xNext, int yNext, int zplayer) {
		int x1 = xNext / TILE_SIZE;
		int y1 = yNext / TILE_SIZE;

		int x2 = (xNext + TILE_SIZE - 1) / TILE_SIZE;
		int y2 = yNext / TILE_SIZE;

		int x3 = xNext / TILE_SIZE;
		int y3 = (yNext + TILE_SIZE - 1) / TILE_SIZE;

		int x4 = (xNext + TILE_SIZE - 1) / TILE_SIZE;
		int y4 = (yNext + TILE_SIZE - 1) / TILE_SIZE;

		if (!((tiles[x1 + (y1 * World.WIDTH)] instanceof WallTile)
				|| (tiles[x2 + (y2 * World.WIDTH)] instanceof WallTile)
				|| (tiles[x3 + (y3 * World.WIDTH)] instanceof WallTile)
				|| (tiles[x4 + (y4 * World.WIDTH)] instanceof WallTile))) {
			return true;
		}

		if (zplayer > 0) {
			return true;
		}
		return false;
	}

	public static void restartGame(String level) {
		Game.entities.clear();
		Game.enemies.clear();
		Game.bullets.clear();
		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<Enemy>();
		Game.spritesheet = new Spritesheet("/spritesheet.png");
		Game.player = new Player(0, 0, 16, 16, Game.spritesheet.getSprite(32, 0, 16, 16));
		Game.entities.add(Game.player);
		Game.world = new World("/" + level);
		return;
	}

	public void render(Graphics g) {
		int xstart = Camera.x >> 4;
		int ystart = Camera.y >> 4;

		int xfinal = xstart + (Game.WIDTH >> 4);
		int yfinal = ystart + (Game.HEIGHT >> 4);

		for (int xx = xstart; xx <= xfinal; xx++) {
			for (int yy = ystart; yy <= yfinal; yy++) {
				if (xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT)
					continue;
				Tile tile = tiles[xx + (yy * WIDTH)];
				tile.render(g);
			}
		}
	}

	public static void renderMiniMap() {
		for (int i = 0; i < Game.minimapaPixels.length; i++) {
			Game.minimapaPixels[i] = 0;
		}
		for (int xx = 0; xx < WIDTH; xx++) {
			for (int yy = 0; yy < HEIGHT; yy++) {
				if (tiles[xx + (yy * WIDTH)] instanceof WallTile) {
					Game.minimapaPixels[xx + (yy * WIDTH)] = 0xffffff;
				}

			}
		}

		int xPlayer = Game.player.getX() / 16;
		int yPlayer = Game.player.getY() / 16;
		Game.minimapaPixels[xPlayer + (yPlayer * WIDTH)] = 0x00ffff; // posição player

	}
	
	public static void generateParticles(int amount, int x, int y) {
		for (int i = 0; i < amount; i++) {
			Game.entities.add(new Particle(x,y,1,1,null));
		}
	}

}
