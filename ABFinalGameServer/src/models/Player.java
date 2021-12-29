package models;

public class Player {
	
	private String nick;
	private double x;
	private double y;
	private transient double speed;
	private transient double size;
	private double degrees;
	private boolean inversed;
	
	public Player(String nick, int x, int y, double size,  double speed) {
		this.x = x;
		this.y = y;
		this.nick = nick;
		this.size = size;
		this.speed = speed;
	}
	
	public void moveUp() {
			y -= speed;
	}
	
	public void moveDown() {
			y += speed;
	}
	
	public void moveLeft() {
			x -= speed;
	}
	
	public void moveRight() {
			x += speed;
	}
	
	//============================================ GETTERS && SETTERS =========================================
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getSize() {
		return size;
	}
	
	public String getNick() {
		return nick;
	}
	
	public void setDegrees(double degrees) {
		this.degrees = degrees;
	}
	
	public void setInversed(boolean inversed) {
		this.inversed = inversed;
	}
}
