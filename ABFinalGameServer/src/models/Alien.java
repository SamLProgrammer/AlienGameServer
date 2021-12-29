package models;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.Timer;

public class Alien implements Runnable {

	private boolean alive;
	private double x;
	private double y;
	private double radius;
	private transient double direction;
	private transient double screenWidth;
	private transient double screenHeight;
	private transient double vy;
	private transient double vx;
	private transient int threadSpeed;
	private transient Thread movementThread;
	private transient Timer fallTimer;
	private transient Timer zigzagTimer;
	private transient int counter;
	private transient boolean toRight;
	private boolean falling;

	public Alien(double radius, double width, double height, double xCenter, double yCenter) {
		alive = true;
		this.radius = radius;
		screenWidth = width;
		screenHeight = height;
		initStates();
		double xDistance = (xCenter - x);
		double yDistance = (yCenter - y);
		direction = Math.atan(yDistance / xDistance);
		if (xCenter < x + radius) {
			direction += Math.PI;
		} else {
			if (Math.atan(yDistance / xDistance) == -Math.PI / 2 || Math.atan(yDistance / xDistance) == Math.PI / 2) {
				direction += Math.PI;
			}
		}
		vx = 10 * Math.cos(direction);
		vy = 10 * Math.sin(direction);
		counter = 1;
		initMovementThread();
	}

	@Override
	public void run() {
		while (alive) {
			y += vy;
			x += vx;
			try {
				Thread.sleep(threadSpeed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void die() {
		falling = true;
		alive = false;
		initFallTimer();
		initZigZagTimer();
	}

	// =============================================== INITS
	// ================================================

	private void initStates() {
		threadSpeed = 30;
		vy = 10;
		vx = -15;
		int initBorder = ThreadLocalRandom.current().nextInt(0, 4);
		switch (initBorder) {
		case 0:
			x = ThreadLocalRandom.current().nextInt((int) (getSize()), (int) (screenWidth - (getSize())));
			y = 0;
			break;
		case 1:
			x = 0;
			y = ThreadLocalRandom.current().nextInt(0, (int) (screenHeight - (getSize())));
			break;
		case 2:
			x = ThreadLocalRandom.current().nextInt((int) (getSize()), (int) (screenWidth - (getSize())));
			y = screenHeight;
			break;
		case 3:
			x = screenWidth;
			y = ThreadLocalRandom.current().nextInt(0, (int) (screenHeight - (getSize())));
			break;
		}
	}

	private void initZigZagTimer() {
		int random = ThreadLocalRandom.current().nextInt(0, 2);
		if (random == 0) {
			toRight = true;
		}
		else {
			counter = 14;
		}
		zigzagTimer = new Timer(10, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (toRight) {
					counter++;
					x += 5;
					y += 2;
					if (counter >= 15) {
						toRight = false;
					}
				} else {
					counter--;
					x -= 5;
					y += 2;
					if (counter <= 0) {
						toRight = true;
					}
				}
			}
		});
		zigzagTimer.start();
	}

	private void initFallTimer() {
		y += 2;
		fallTimer = new Timer(50, new ActionListener() {
			double time = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (y >= 1080) {
					fallTimer.stop();
				}
				y += 4.9 * time * time;
				time += 0.15;
			}
		});
		fallTimer.start();
	}

	private void initMovementThread() {
		movementThread = new Thread(this);
		movementThread.start();
	}

	// =========================================== GETTERS && SETTERS
	// =========================================

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getSize() {
		return radius * 2;
	}

	public double getCenterX() {
		return x + radius;
	}

	public double getCenterY() {
		return y + radius;
	}

	public boolean isAlive() {
		return alive;
	}
}
