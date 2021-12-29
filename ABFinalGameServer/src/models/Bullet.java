package models;

public class Bullet implements Runnable{

	private double x;
	private double y;
	private double size; 
	private transient int width;
	private transient int height;
	private transient double vx; 
	private transient double vy; 
	private transient double direction; 
	private transient static final double SPEED = 40;
	private boolean alive;
	private transient Thread thread;
	private Game game;
	
	public Bullet() {
		
	}
	
	public Bullet(Game game, double size, int width, int height, double x, double y,
			double xDistance, double yDistance, boolean inversed) {
		this.game = game;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.size = size;
		direction = Math.atan(yDistance/xDistance);
		if(inversed) {
			direction += Math.PI;
		}
		if(Math.atan(yDistance/xDistance) == -Math.PI/2 || Math.atan(yDistance/xDistance) == Math.PI/2) {
			direction += Math.PI;
		}
		vx = SPEED*Math.cos(direction);
		vy = SPEED*Math.sin(direction);
		alive = true;
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		while(alive) {
			y += vy;
			x += vx;
			verifyCrashing();
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void verifyCrashing() {
		crashingUp();
		crashingDown();
		crashingLeft();
		crashingRight();
		game.verifyShootCrashing();
	}
	
	private void crashingUp() {
		if(y + size <= 0 ) {
			die();
		}
	}
	
	private void crashingDown() {
		if(y >= height) {
			die();
		}
	}
	
	private void crashingLeft() {
		if(x + size <= 0 ) {
			die();
		}
	}
	
	private void crashingRight() {
		if(x >= width ) {
			die();
		}
	}
	
	public double getSize() {
		return size;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getCenterX() {
		return x + size/2;
	}
	
	public double getCenterY() {
		return y + size/2;
	}

	public void die() {
		alive = false;
	}

	public boolean isAlive() {
		return alive;
	}
}