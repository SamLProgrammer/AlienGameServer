package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Player;

public class Connection {

	private Socket socket;
	private DataOutputStream outputStream;
	private DataInputStream inputStream;
	private boolean connectionStatus;
	private Observer observer;
	private boolean ready;
	private Player player;
	
	public Connection(Socket socket, Observer connectionListener) throws IOException {
		connectionStatus = true;
		this.socket = socket;
		this.observer = connectionListener;
		initStreams();
		Logger.getGlobal().log(Level.INFO, 
				"Nuevo cliente." + socket.getInetAddress().getHostAddress());
		initComunication();
	}
	
	private void initStreams() throws IOException {
		inputStream = new DataInputStream(socket.getInputStream());
		outputStream = new DataOutputStream(socket.getOutputStream());
	}
	
	private void initComunication() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(connectionStatus) {
					try {
						if(inputStream.available() > 0) {
							String trace = inputStream.readUTF();
							switch (Requests.valueOf(trace)) {
							case ASK_FOR_JOIN:
								addPlayer();
								break;
							case READY:
								ready = true;
								break;
							case MOVE_UP:
								moveUp();
								break;
							case MOVE_DOWN:
								moveDown();
								break;
							case MOVE_LEFT:
								moveLeft();
								break;
							case MOVE_RIGHT:
								moveRight();
								break;
							case SHOT:
								addShot();
								break;
							case ROTATION:
								setRotationToPlayer();
								break;
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					} 
				}
			}
		}).start();
	}
	
	private void addShot() {
		observer.addBullet(player, readInt(), readInt());
	}
	
	private void setRotationToPlayer() {
		player.setDegrees(readDouble());
		player.setInversed(readBoolean());
	}
	
	public void addPlayer() {
		player = observer.addPlayer(readUTF());
	}
	
	private void moveUp() {
		player.moveUp();
	}
	
	private void moveLeft() {
		player.moveLeft();
	}
	
	private void moveRight() {
		player.moveRight();
	}
	
	private void moveDown() {
		player.moveDown();
	}
	
	//============================================= RESPONSES ============================================
	
	public void sendTokens(String playersListAsJson) {
		writeUTF(Responses.SERVER_TO_CLIENT_TOKENS.toString());
		writeUTF(playersListAsJson);
	}
	
	public void sendAliens(String aliensListAsAsJson) {
			writeUTF(Responses.SERVER_TO_CLIENT_ALIENS.toString());
			writeUTF(aliensListAsAsJson);
	}
	
	
	public void sendBullets(String bulletsListAsJson) {
		writeUTF(Responses.SERVER_TO_CLIENT_BULLETS.toString());
		writeUTF(bulletsListAsJson);
	}

	public void gameStartedNotify() {
			writeUTF(Responses.GAME_STARTED.toString());
	}
	
	public void initExplosion() {
		writeUTF(Responses.INIT_EXPLOSION.toString());
	}
	
	public void notifyEndGame() {
		writeUTF(Responses.GAME_ENDED.toString());
		
	}
	
	public void sendHitsOnStation(byte hitsOnStationNumber) {
		writeUTF(Responses.HITS_ON_STATION.toString());
		writeByte(hitsOnStationNumber);
	}
	
	//============================================== UTILITIES ============================================
	
	private void writeUTF(String string) {
		try {
			outputStream.writeUTF(string);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeByte(byte number) {
		try {
			outputStream.writeByte(number);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String readUTF() {
		String string = "";
		try {
			string = inputStream.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return string;
	}
	
	private int readInt() {
		int number = 0;
		try {
			number = inputStream.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return number;
	}
	
	private double readDouble() {
		double number = 0;
		try {
			number = inputStream.readDouble();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return number;
	}
	
	private boolean readBoolean() {
		boolean flag = false;
		try {
			flag = inputStream.readBoolean();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
}