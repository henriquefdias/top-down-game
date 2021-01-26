package com.hfdias.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.hfdias.graficos.Spritesheet;
import com.hfdias.main.Game;
import com.hfdias.world.Camera;
import com.hfdias.world.World;

public class Player extends Entity {

	public boolean right, up, left, down;
	public int right_dir = 0, left_dir = 1;
	public int dir = right_dir;
	public double speed = 1.4;

	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;

	private BufferedImage playerDamage;

	private boolean hasGun = false;

	public int ammo = 0;

	public boolean isDamaged = false;
	private int damageFrames = 8, damageCurrent = 0;

	public boolean shoot = false, mouseShoot = false;

	public double life = 100, maxlife = 100;
	public int mx, my;

	public boolean jump = false;

	public boolean isJumping = false;

	public int z = 0;

	public int jumpFrames = 40, jumpCur = 0;

	public boolean jumpUp = false, jumpDown = false;

	public int jumpSpd = 2;

	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);

		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		playerDamage = Game.spritesheet.getSprite(0, 16, 16, 16);
		for (int i = 0; i < 4; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i * 16), 0, 16, 16);
		}

		for (int i = 0; i < 4; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(80 - (i * 16), 16, 16, 16);
		}
	}

	public void tick() {

		if (jump) {
			if (!isJumping) {
				jump = false;
				isJumping = true;
				jumpUp = true;
			}
		}

		if (isJumping) {
			if (jumpUp) {
				jumpCur += jumpSpd;
			} else if (jumpDown) {
				jumpCur -= jumpSpd;
				if (jumpCur <= 0) {
					isJumping = false;
					jumpDown = false;
					jumpUp = false;
				}
			}
			z = jumpCur;
			if (jumpCur >= jumpFrames) {
				jumpUp = false;
				jumpDown = true;
			}
		}

		moved = false;
		if (right && World.isFree((int) (x + speed), this.getY(), (int) this.z)) {
			moved = true;
			dir = right_dir;
			x += speed;
		} else if (left && World.isFree((int) (x - speed), this.getY(), (int) this.z)) {
			moved = true;
			dir = left_dir;
			x -= speed;
		}
		if (up && World.isFree(this.getX(), (int) (y - speed), (int) this.z)) {
			moved = true;
			y -= speed;
		} else if (down && World.isFree(this.getX(), (int) (y + speed), (int) this.z)) {
			moved = true;
			y += speed;
		}
		if (moved) {
			frames++;
			if (frames == maxFrames) {
				frames = 0;
				index++;
				if (index > maxIndex) {
					index = 0;
				}
			}
		}

		checkCollisionLifePack();
		checkCollisionAmmo();
		checkCollisionGun();

		if (isDamaged) {
			this.damageCurrent++;
			if (damageCurrent == damageFrames) {
				this.damageCurrent = 0;
				isDamaged = false;
			}
		}

		if (shoot) {
			shoot = false;
			if (hasGun && ammo > 0) {
				// Criar bala e atirar
				ammo--;
				shoot = false;
				int dx = 0;
				int px = 0;
				int py = 9;
				if (dir == right_dir) {
					px = 19;
					dx = 1;
				} else {
					px = -6;
					dx = -1;
				}

				BulletShoot bullet = new BulletShoot(this.getX() + px, this.getY() + py, 3, 3, null, dx, 0);
				Game.bullets.add(bullet);
			}
		}

		if (mouseShoot) {
			mouseShoot = false;
			if (hasGun && ammo > 0) {
				// Criar bala e atirar
				double angle = 0;
				ammo--;
				int px = 0;
				int py = 9;
				if (dir == right_dir) {
					px = 19;
					angle = Math.atan2(my - (this.getY() + 8 - Camera.y), mx - (this.getX() + px - Camera.x));
				} else {
					px = -6;
					angle = Math.atan2(my - (this.getY() + 8 - Camera.y), mx - (this.getX() + px - Camera.x));
				}

				double dx = Math.cos(angle);
				double dy = Math.sin(angle);

				BulletShoot bullet = new BulletShoot(this.getX() + px, this.getY() + py, 3, 3, null, dx, dy);
				Game.bullets.add(bullet);
			}
		}

		if (life <= 0) {
			life = 0;
			Game.gameState = "GAME_OVER";
		}

		updateCamera();
	}

	public void updateCamera() {
		Camera.x = Camera.clamp(this.getX() - Game.WIDTH / 2, 0, World.WIDTH * 16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - Game.HEIGHT / 2, 0, World.HEIGHT * 16 - Game.HEIGHT);
	}

	public void checkCollisionLifePack() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if (atual instanceof Lifepack) {
				if (Entity.isColliding(this, atual)) {
					if (life < 100) {
						life += 40;
						if (life > 100) {
							life = 100;
						}
						Game.entities.remove(atual);
					}
				}
			}
		}
	}

	public void checkCollisionAmmo() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if (atual instanceof Bullet) {
				if (Entity.isColliding(this, atual)) {
					ammo += 20;
					Game.entities.remove(atual);
				}
			}
		}
	}

	public void checkCollisionGun() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if (atual instanceof Weapon) {
				if (Entity.isColliding(this, atual)) {
					hasGun = true;
					ammo += 20;
					Game.entities.remove(atual);
				}
			}
		}
	}

	public void render(Graphics g) {
		if (!isDamaged) {
			if (dir == right_dir) {
				if (moved == false) {
					g.drawImage(rightPlayer[0], this.getX() - Camera.x, this.getY() - Camera.y - z, null);
					if (hasGun) {
						// desenhar arma para direita
						g.drawImage(Entity.GUN_RIGHT, this.getX() + 8 - Camera.x, this.getY() + 2 - Camera.y - z, null);
					}
				} else {
					g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - z, null);
					if (hasGun) {
						// desenhar arma para direita
						g.drawImage(Entity.GUN_RIGHT, this.getX() + 8 - Camera.x, this.getY() + 2 - Camera.y - z, null);
					}
				}
			} else if (dir == left_dir) {
				if (moved == false) {
					g.drawImage(leftPlayer[0], this.getX() - Camera.x, this.getY() - Camera.y - z, null);
					if (hasGun) {
						// desenhar arma para esquerda
						g.drawImage(Entity.GUN_LEFT, this.getX() - 8 - Camera.x, this.getY() + 2 - Camera.y - z, null);
					}
				} else {
					g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - z, null);
					if (hasGun) {
						// desenhar arma para esquerda
						g.drawImage(Entity.GUN_LEFT, this.getX() - 8 - Camera.x, this.getY() + 2 - Camera.y - z, null);
					}
				}
			}
		} else {
			g.drawImage(playerDamage, this.getX() - Camera.x, this.getY() - Camera.y - z, null);
			if (hasGun) {
				if (dir == right_dir) {
					g.drawImage(Entity.GUN_RIGHT_DAMAGED, this.getX() + 8 - Camera.x, this.getY() + 2 - Camera.y - z,
							null);
				} else if (dir == left_dir) {
					g.drawImage(Entity.GUN_LEFT_DAMAGED, this.getX() - 8 - Camera.x, this.getY() + 2 - Camera.y - z,
							null);
				}
			}
		}
		if(isJumping) {
			g.setColor(Color.black);
			g.fillOval(this.getX() - Camera.x + 4, this.getY() - Camera.y + 10, 8, 8);
		}
	}

}
