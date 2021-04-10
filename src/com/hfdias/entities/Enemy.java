package com.hfdias.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.hfdias.main.Game;
import com.hfdias.main.Sound;
import com.hfdias.world.Camera;
import com.hfdias.world.World;

public class Enemy extends Entity {

	private double speed = 0.45;

	private int maskX = 5, maskY = 8, maskW = 7, maskH = 7;

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
		// Método utilizado pra quando há muitas entities no mapa, assim o movimento
		// randomico previne que se colidam o tempo todo
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
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskX, this.getY() + maskY, maskW, maskH);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16, 16);

		if (enemyCurrent.intersects(player) && this.z == Game.player.z) {
			return true;
		}
		return false;
	}

	// Método mais preciso, porém recomendado para quando há menos entities no mapa,
	// para nao sobrecarregar por conta de todas as verificações feitas
	public boolean isColliding(int xnext, int ynext) {
		Rectangle enemyCurrent = new Rectangle(xnext + maskX, ynext + maskY, maskW, maskH);
		for (int i = 0; i < Game.enemies.size(); i++) {
			Enemy e = Game.enemies.get(i);
			// Aqui valida se está colidindo, logo, se estiver comparando com si mesmo, não
			// rodar a validação, pois pode causar bug.
			if (e == this) {
				continue;
			}
			// Pega o enemy atual, que está rodando essa validação, e compara se tem
			// intersecção com os itens da lista de enemies
			Rectangle targetEnemy = new Rectangle(e.getX() + maskX, e.getY() + maskY, maskW, maskH);
			if (enemyCurrent.intersects(targetEnemy)) {
				return true;
			}
		}
		return false;
	}

	public void render(Graphics g) {
		if (!isDamaged)
			g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		else
			g.drawImage(Entity.ENEMY_FEEDBACK, this.getX() - Camera.x, this.getY() - Camera.y, null);
//		g.setColor(Color.blue);
//		g.fillRect(this.getX() + maskX - Camera.x, this.getY() + maskY - Camera.y, maskW, maskH);
	}

}
