package com.hfdias.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;

import com.hfdias.main.Game;
import com.hfdias.world.Camera;
import com.hfdias.world.Node;
import com.hfdias.world.Vector2i;

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
	public static BufferedImage FLOWER_EN = Game.spritesheet.getSprite(16, 16, 16, 16);

	protected double x;
	protected double y;
	protected double z;
	protected int width;
	protected int height;
	
	public int depth;

	protected List<Node> path;

	public int maskx, masky, mwidth, mheight;
	public double speed = 1;

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
	
	public static Comparator<Entity> nodeSorter = new Comparator<Entity>() {

		@Override
		public int compare(Entity n0, Entity n1) {
			if (n1.depth < n0.depth)
				return +1;
			if (n1.depth > n0.depth)
				return -1;
			return 0;
		}
	};

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

	// Método mais preciso, porém recomendado para quando há menos entities no mapa,
	// para nao sobrecarregar por conta de todas as verificações feitas
	public boolean isColliding(int xnext, int ynext) {
		Rectangle enemyCurrent = new Rectangle(xnext + maskx, ynext + masky, mwidth, mheight);
		for (int i = 0; i < Game.enemies.size(); i++) {
			Enemy e = Game.enemies.get(i);
			// Aqui valida se está colidindo, logo, se estiver comparando com si mesmo, não
			// rodar a validação, pois pode causar bug.
			if (e == this) {
				continue;
			}
			// Pega o enemy atual, que está rodando essa validação, e compara se tem
			// intersecção com os itens da lista de enemies
			Rectangle targetEnemy = new Rectangle(e.getX() + maskx, e.getY() + masky, mwidth, mheight);
			if (enemyCurrent.intersects(targetEnemy)) {
				return true;
			}
		}
		return false;
	}

	public void followPath(List<Node> path) {
		if (path != null) {
			if (path.size() > 0) {
				Vector2i target = path.get(path.size() - 1).tile;
				// xprev = x;
				// yprev = y;
				if (x < target.x * 16) {
					x+=speed;
				} else if (x > target.x * 16) {
					x-=speed;
				}

				if (y < target.y * 16) {
					y+=speed;
				} else if (y > target.y * 16) {
					y-=speed;
				}

				if (x == target.x * 16 && y == target.y * 16) {
					path.remove(path.size() - 1);
				}
			}
		}
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
