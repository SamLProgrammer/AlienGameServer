package network;

import models.Player;

public class Observer {
 
	private GameServer server;
	
	public Observer(GameServer server) {
		this.server = server;
	}

	public Player addPlayer(String nick) {
		return server.addPlayer(nick);
	}

	public void addBullet(Player player, int x, int y) {
		server.addBullet(player, x, y);
	}

	public void initExplosion() {
	server.initExplosion();
	}

	public void endGame() {
		server.endGame();
	}
}
