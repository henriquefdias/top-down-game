package com.hfdias.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.hfdias.entities.BulletShoot;
import com.hfdias.entities.Enemy;
import com.hfdias.entities.Entity;
import com.hfdias.entities.Player;
import com.hfdias.graficos.Spritesheet;
import com.hfdias.graficos.UI;
import com.hfdias.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	public static final int WIDTH = 240;
	public static final int HEIGHT = 160;
	public static int SCALE = 3;

	private int CUR_LEVEL = 1, MAX_LEVEL = 2;
	private BufferedImage image;

	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<BulletShoot> bullets;
	public static Spritesheet spritesheet;

	public static World world;

	public static Player player;

	public static Random rand;

	public UI ui;

	// public InputStream stream =
	// ClassLoader.getSystemClassLoader().getResourceAsStream("pixelfont.ttf");
	// public Font newFont;

	public static String gameState = "MENU";
	private boolean showMessageGameOver = true;
	private int framesGameOver = 0;
	private boolean restartGame = false;

	public Menu menu;

	public int[] pixels;
	public BufferedImage lightmap;
	public int[] lightMapPixels;
	public static int[] minimapaPixels;
	
	public static BufferedImage minimapa;

	public boolean saveGame = false;

	public int mx, my;

	public Game() {
		rand = new Random();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		initFrame();
		// Inicializando objetos
		ui = new UI();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		try {
			lightmap = ImageIO.read(getClass().getResource("/lightmap.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		lightMapPixels = new int[lightmap.getWidth() * lightmap.getHeight()];
		lightmap.getRGB(0, 0, lightmap.getWidth(), lightmap.getHeight(), lightMapPixels, 0, lightmap.getWidth());
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<BulletShoot>();
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0, 0, 16, 16, spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		world = new World("/level1.png");
		minimapa = new BufferedImage(World.WIDTH, World.HEIGHT, BufferedImage.TYPE_INT_RGB);
		minimapaPixels = ((DataBufferInt) minimapa.getRaster().getDataBuffer()).getData();
		
		/*
		 * try { newFont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(70f);
		 * } catch (FontFormatException e) { e.printStackTrace(); } catch (IOException
		 * e) { e.printStackTrace(); }
		 */

		menu = new Menu();
	}

	public void initFrame() {
		frame = new JFrame("Nelda");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}

	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void tick() {
		if (gameState == "NORMAL") {
			if (this.saveGame) {
				this.saveGame = false;
				String[] opt1 = { "level", "vida" };
				int[] opt2 = { this.CUR_LEVEL, (int) player.life };
				Menu.saveGame(opt1, opt2, 10);
				System.out.println("Jogo salvo!");
			}
			restartGame = false;
			for (int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.tick();
			}

			for (int i = 0; i < bullets.size(); i++) {
				bullets.get(i).tick();
			}

			if (enemies.size() == 0) {
				CUR_LEVEL++;
				if (CUR_LEVEL > MAX_LEVEL) {
					CUR_LEVEL = 1;
				}
				String newWorld = "level" + CUR_LEVEL + ".png";
				World.restartGame(newWorld);
			}
		} else if (gameState == "GAME_OVER") {
			framesGameOver++;
			if (framesGameOver == 30) {
				framesGameOver = 0;
				if (showMessageGameOver) {
					showMessageGameOver = false;
				} else {
					showMessageGameOver = true;
				}
			}

			if (restartGame) {
				restartGame = false;
				gameState = "NORMAL";
				CUR_LEVEL = 1;
				String newWorld = "level" + CUR_LEVEL + ".png";
				World.restartGame(newWorld);
			}
		} else if (gameState == "MENU") {
			player.updateCamera();
			menu.tick();
		}

	}

	/*
	 * Na função abaixo, ele preenche primeiro os pixels verticalmente, e depois vai
	 * partindo para o lado.
	 */
	/*
	 * public void drawRectangleExample(int xoff, int yoff) { for (int xx = 0; xx <
	 * 32; xx++) { for(int yy = 0; yy < 32; yy++) { int xOff = xx + xoff; int yOff =
	 * yy + yoff; if(xOff < 0 || yOff < 0 || xOff >= WIDTH || yOff >= HEIGHT)
	 * continue; pixels[xOff + (yOff*WIDTH)] = 0xff0000; } } }
	 */

	public void applyLight() {
		/*
		for (int xx = 0; xx < Game.WIDTH; xx++) {
			for (int yy = 0; yy < Game.HEIGHT; yy++) {
				if(lightMapPixels[xx + (yy * Game.WIDTH)] == 0xffffffff) {
					pixels[xx + (yy * Game.WIDTH)] = 0;
				}
			}
		}
		*/
		
	}

	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, WIDTH, HEIGHT); // Limpa a tela a cada renderização

		/* Renderização do jogo */

//		Graphics2D g2 = (Graphics2D) g;
		world.render(g);
		Collections.sort(entities, Entity.nodeSorter);
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).render(g);
		}
		applyLight();
		ui.render(g);
		/***/
		g.dispose();
		g = bs.getDrawGraphics();
		// drawRectangleExample(xx, yy);
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		g.setFont(new Font("arial", Font.BOLD, 20));
		g.setColor(Color.white);
		g.drawString("Ammo : " + player.ammo, 600, 40);
		if (gameState == "GAME_OVER") {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0, 0, 0, 100));
			g2.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
			g.setFont(new Font("arial", Font.BOLD, 36));
			g.setColor(Color.white);
			g.drawString("Game Over", ((WIDTH * SCALE) >> 1) - 90, (HEIGHT * SCALE) >> 1);
			g.setFont(new Font("arial", Font.BOLD, 26));
			if (showMessageGameOver)
				g.drawString("> Pressione Enter para reiniciar <", ((WIDTH * SCALE) >> 1) - 200,
						((HEIGHT * SCALE) >> 1) + 50);
		} else if (gameState == "MENU") {
			menu.render(g);
		}
		World.renderMiniMap();
		g.drawImage(minimapa, 600, 50, World.WIDTH * 5, World.HEIGHT * 5, null);
		
		/*
		 * Graphics2D g2 = (Graphics2D) g; double angleMouse = Math.atan2(200 + 25 - my,
		 * 200 + 25 - mx); g2.rotate(angleMouse, 200+25, 200+25); g.setColor(Color.red);
		 * g.fillRect(200, 200, 50, 50);
		 * 
		 * g.setFont(newFont); g.setColor(Color.red); g.drawString("Testando", 90, 90);
		 */
		bs.show();
	}

	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}

	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		while (isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if (delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}

			if (System.currentTimeMillis() - timer >= 1000) {
				System.out.println("FPS: " + frames);
				frames = 0;
				timer += 1000;
			}
		}
		stop();
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			player.jump = true;
		}

		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}

		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			player.up = true;

			if (gameState == "MENU") {
				menu.up = true;
			}

		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;

			if (gameState == "MENU") {
				menu.down = true;
			}

		}

		if (e.getKeyCode() == KeyEvent.VK_J) {
			player.shoot = true;
		}

		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			restartGame = true;
			if (gameState == "MENU") {
				menu.enter = true;
			}
		}

		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			gameState = "MENU";
			menu.pause = true;
		}

		if (e.getKeyCode() == KeyEvent.VK_F5) {
			if (gameState == "NORMAL")
				this.saveGame = true;
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
		}

		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		player.mouseShoot = true;
		player.mx = (e.getX() / 3);
		player.my = (e.getY() / 3);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.mx = e.getX();
		this.my = e.getY();
	}

}
