package network;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import models.Game;
import models.Player;

public class GameServer {
	
	private ServerSocket serverSocket;
	private boolean serverOn;
	private ArrayList<Connection> connections;
	private Observer observer;
	private Game game;
	Timer refreshingTimer;
	
	
	public GameServer() throws IOException {
		observer = new Observer(this);
		connections = new ArrayList<>();
		game = new Game(observer);
		serverSocket = new ServerSocket(3016);
		Logger.getGlobal().log(Level.INFO, "Opened server at port: " + serverSocket.getLocalPort());
		initConnectionsThread();
	}
	
	private void initConnectionsThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				serverOn = true;
				while(serverOn) {
					try {
						Socket socket = serverSocket.accept();
						System.out.println("new Client at: " + socket.getRemoteSocketAddress().toString());
						connections.add(new Connection(socket, observer));
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	private void refreshPlayersAliens() {
		for (Connection connection : connections) {
			connection.sendAliens(game.getAliensListAsJson());
		}
	}
	
	private void warnPlayersToStart() {
		for (Connection connection : connections) {
			connection.gameStartedNotify();
		}
	}
	
	private void refreshPlayersToken() {
		for (Connection connection : connections) {
			connection.sendTokens(game.getPlayersListAsJson());
		}
	}
	
	private void refreshPlayersBullets() {
		for (Connection connection : connections) {
			connection.sendBullets(game.getBulletListAsJson());
		}
	}
	
	public void initExplosion() {
		for (Connection connection : connections) {
			connection.initExplosion();
		}
	}
	
	private void sendHitsOnStation() {
		for (Connection connection : connections) {
			connection.sendHitsOnStation(game.getHitsOnStationNumber());
		}
	}
	
	private void initRefreshingPlayersThread() {
		refreshingTimer = new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshPlayersToken();
				if(game.getAliensListSize() > 0) {
					refreshPlayersAliens();
				}
				if(game.getBulletsListSize() > 0) {
					refreshPlayersBullets();
				}
			}
		});
		refreshingTimer.start();
	}
	
	public static void main(String[] args) {
		try {
			new GameServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized Player addPlayer(String nickName) {
		Player player = game.addPlayer(nickName);
		if(game.getPlayersAmount() == 1) {
			game.initThreads();
			warnPlayersToStart();
			initRefreshingPlayersThread();
		}
		else {
			warnPlayersToStart();
			sendHitsOnStation();
		}
		
		return player; 
	}

	public void addBullet(Player player, int x, int y) {
		game.addBullet(player, x, y);
	}

	public void endGame() {
		for (Connection connection : connections) {
			connection.notifyEndGame();
		}
	}
}