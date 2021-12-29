package models;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.Timer;
import deco.JSONManager;
import network.Observer;

public class Game {

	private Timer alienGeneratorTimer;
	private Timer crashVerifierTimer;
	private CopyOnWriteArrayList<Alien> aliensList;
	private CopyOnWriteArrayList<Bullet> bulletsList;
	private CopyOnWriteArrayList<Player> playersList;
	private Station station;
	private int alienAmount;
	private double width;
	private double height;
	private JSONManager json;
	private double bulletSize;
	private double playerSize;
	private double alienRadius;
	private double xCenter;
	private double yCenter; 
	private Observer observer;
	private byte hitsOnStation;
	
	public Game(Observer observer) {
		this.observer = observer;
		initLists();
		alienAmount = 1;
		json = new JSONManager();
		initComponents();
		station = new Station(xCenter - (3 * playerSize / 4), yCenter - (3 * playerSize / 4), 3 * playerSize / 2);
	}

	private void initLists() {
		aliensList = new CopyOnWriteArrayList<>();
		playersList = new CopyOnWriteArrayList<>();
		bulletsList = new CopyOnWriteArrayList<>();
	}

	public void initThreads() {
		initAlienGenerator(1000);
		initCrashVerifierTimer(30);
	}

	private void initComponents() {
		width = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		height = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		xCenter = width / 2;
		yCenter = height / 2;
		playerSize = (width + height) / 30;
		alienRadius = (width + height) / 80;
		bulletSize = (width + height) / 300;
	}

	public Player addPlayer(String nickName) {
		double speed = (width + height) / 150;
		int x = 0;
		int y = 0;
		do {
			x = ThreadLocalRandom.current().nextInt((int) (width / 4), (int) (3 * width / 4));
		} while ((x > xCenter - station.getSize() / 2 - playerSize/2) &&
				x < xCenter + station.getSize() / 2  + playerSize/2);
		do {
			y = ThreadLocalRandom.current().nextInt((int) (height / 5), (int) (4 * height / 5));
		} while ((y > yCenter + station.getSize() / 2) && (y > yCenter - station.getSize() / 2));
		Player player = new Player(nickName, x, y, playerSize, speed);
		playersList.add(player);
		return player;
	}

	public void addBullet(Player player, double xCursor, double yCursor) {
		double xDistance = (player.getX() + player.getSize() / 2) - xCursor;
		double yDistance = (player.getY() + player.getSize() / 2) - yCursor;
		if (xCursor < player.getX() + player.getSize() / 2) {
			bulletsList.add(new Bullet(this, bulletSize, (int)width, (int)height,  player.getX() + player.getSize() / 2,
					player.getY() + player.getSize()/2, xDistance, yDistance, true));
		} else {
			bulletsList.add(new Bullet(this, bulletSize, (int)width, (int)height, player.getX() + player.getSize() / 2,
					player.getY() + player.getSize() / 2, xDistance, yDistance, false));
		}
	}

	// =============================================== TIMERS ===============================================

	private void initAlienGenerator(int frequency) {
		alienGeneratorTimer = new Timer(frequency, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < alienAmount; i++) {
					aliensList.add(new Alien(alienRadius, width, height, xCenter, yCenter));
				}
			}
		});
		alienGeneratorTimer.start();
	}

	private void initCrashVerifierTimer(int frequency) {
		crashVerifierTimer = new Timer(frequency, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (aliensList.size() > 0) {
					verifyShootCrashing();
					verifyStationCrashed();
					verifyAlienDissapeared();
				}
			}
		});
		crashVerifierTimer.start();
	}

	// =========================================== INTERACTIONS =========================================

	private double getCrashDistance(double x1, double y1, double x2, double y2) {
		double a = Math.pow(x1 - x2, 2);
		double b = Math.pow(y1 - y2, 2);
		return Math.abs(Math.sqrt(a + b));
	}
	
	public void verifyShootCrashing() {
		for (Bullet bullet : bulletsList) {
			for (Alien alien : aliensList) {
				if((bullet.isAlive() && alien.isAlive()) && (getCrashDistance(bullet.getCenterX(), bullet.getCenterY(),
						alien.getCenterX(), alien.getCenterY()) <= (alien.getSize() + bullet.getSize()) / 2)) {
					alien.die();
					bulletsList.remove(bullet);
				} else if (bullet.isAlive() && (getCrashDistance(bullet.getCenterX(), bullet.getCenterY(), xCenter,
						yCenter) <= (station.getSize() + bullet.getSize()) / 2)) {
					bulletsList.remove(bullet);
				}
			}
		}
	}

	public void verifyAlienDissapeared() {
		for (Alien alien : aliensList) {
			if(alien.getY() >= height) {
				aliensList.remove(alien);
			}
		}
	}
	
	public void verifyStationCrashed() {
		for (Alien alien : aliensList) {
			if ((alien.isAlive()) && (getCrashDistance(xCenter, yCenter, alien.getCenterX(),
					alien.getCenterY()) <= (alien.getSize() + station.getSize()) / 2)) {
				hitsOnStation += 1;
				alien.die();
				observer.initExplosion();
				if(hitsOnStation > 21) {
					stopAll();
				}
			}
		}
	}
	
	private void stopAll() {
		alienGeneratorTimer.stop();
		crashVerifierTimer.stop();
		observer.endGame();
	}

	// =============================================== UTILITIES ============================================

	public String getAliensListAsJson() {
		return json.encodeAliensList(aliensList);
	}

	public String getPlayersListAsJson() {
		return json.encodePlayersList(playersList);
	}

	public String getBulletListAsJson() {
		return json.encodeBulletsList(bulletsList);
	}

	public int getPlayersAmount() {
		return playersList.size();
	}

	public int getBulletsListSize() {
		return bulletsList.size();
	}
	
	public int getAliensListSize() {
		return aliensList.size();
	}
	
	public byte getHitsOnStationNumber() {
		return hitsOnStation;
	}
}
