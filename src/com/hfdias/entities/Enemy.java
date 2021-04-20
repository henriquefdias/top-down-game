package com.hfdias.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.hfdias.main.Game;
import com.hfdias.world.AStar;
import com.hfdias.world.Camera;
import com.hfdias.world.Vector2i;

public class Enemy extends Entity {
	
	private int frames = 0, maxFrames = 6, index = 0, maxIndex = 1;

	private BufferedImage[] sprites;

	private int life = 10;

	private boolean isDamaged = false;
	private int damageFrames = 8, damageCurrent = 0;

	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		sprites = new BufferedImage[2];
		sprites[0] = Game.spritesheet.getSprite(112, 16, 16, 16);
		sprites[1] = Game.spritesheet.getSprite(112 + 16, 16, 16, 16);
	}

	public void tick() {
		depth = 0;
		// Método utilizado pra quando há muitas entities no mapa, assim o movimento
		// randomico previne que se colidam o tempo todo
		/*
		if (this.calculateDistance(this.getX(), this.getY(), Game.player.getX(), Game.player.getY()) < 75) {
			if (this.isCollidingWithPlayer() == false) {
//			if (Game.rand.nextInt(100) < 50) {
				if ((int) x < Game.player.getX() && World.isFree((int) (x + speed), this.getY(), (int) this.z)
						&& !isColliding((int) (x + speed), this.getY())) {
					x += speed;
				} else if ((int) x > Game.player.getX() && World.isFree((int) (x - speed), this.getY(), (int) this.z)
						&& !isColliding((int) (x - speed), this.getY())) {
					x -= speed;
				}

				if ((int) y < Game.player.getY() && World.isFree(this.getX(), (int) (y + speed), (int) this.z)
						&& !isColliding(this.getX(), (int) (y + speed))) {
					y += speed;
				} else if ((int) y > Game.player.getY() && World.isFree(this.getX(), (int) (y - speed), (int) this.z)
						&& !isColliding(this.getX(), (int) (y - speed))) {
					y -= speed;
				}
//			}
			} else {
				// Existe 10% de chance de causar dano ao jogador
				if (Game.rand.nextInt(100) < 21) {
//				Sound.hurtEffect.play();
					Game.player.life -= Game.rand.nextInt(3);
					Game.player.isDamaged = true;
				}
			}
		}
		*/
		
		// Algoritmo A*
		maskx = 5;
		masky = 7;
		mwidth = 7;
		mheight = 7;
		if(!isCollidingWithPlayer()) {
			if(path == null || path.size() == 0) {
				Vector2i start = new Vector2i((int)(x/16), (int)(y/16));
				Vector2i end = new Vector2i((int)(Game.player.x/16), (int)(Game.player.y/16));
				path = AStar.findPath(Game.world, start, end);
			}
		} else {
			if (Game.rand.nextInt(100) < 21) {
				Game.player.life -= Game.rand.nextInt(3);
				Game.player.isDamaged = true;
			}
		}
		if(new Random().nextInt(100) < 45)
			followPath(path);
		if(new Random().nextInt(100) < 5) {
			Vector2i start = new Vector2i((int)(x/16), (int)(y/16));
			Vector2i end = new Vector2i((int)(Game.player.x/16), (int)(Game.player.y/16));
			path = AStar.findPath(Game.world, start, end);
		}
		frames++;
		if (frames == maxFrames) {
			frames = 0;
			index++;
			if (index > maxIndex) {
				index = 0;
			}
		}

		collidingBullet();

		if (life <= 0) {
			destroySelf();
			return;
		}

		if (isDamaged) {
			damageCurrent++;
			if (damageCurrent == damageFrames) {
				damageCurrent = 0;
				isDamaged = false;
			}
		}

	}

	public void destroySelf() {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}

	public void collidingBullet() {
		for (int i = 0; i < Game.bullets.size(); i++) {
			Entity e = Game.bullets.get(i);
			if (e instanceof BulletShoot) {
				if (Entity.isColliding(this, e)) {
					isDamaged = true;
					life -= 2;
					Game.bullets.remove(i);
					return;
				}
			}
		}
	}

	public boolean isCollidingWithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskx, this.getY() + masky, mwidth, mheight);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16, 16);

		if (enemyCurrent.intersects(player) && this.z == Game.player.z) {
			return true;
		}
		return false;
	}

	public void render(Graphics g) {
		if (!isDamaged)
			g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		else
			g.drawImage(Entity.ENEMY_FEEDBACK, this.getX() - Camera.x, this.getY() - Camera.y, null);
//		g.setColor(Color.blue);
//		g.fillRect(this.getX() + maskx - Camera.x, this.getY() + masky - Camera.y, mwidth, mheight);
	}

}
