package deco;

import java.util.concurrent.CopyOnWriteArrayList;
import com.google.gson.*;
import models.Alien;
import models.Bullet;
import models.Player;

public class JSONManager {

	public String encodePlayer(Player player) {
		String playerAsJson = "";
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		playerAsJson = gson.toJson(player);
		return playerAsJson;
	}

	public String encodeAliensList(CopyOnWriteArrayList<Alien> aliensList) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(aliensList);
	}

	public String encodeDoubleAsJson(double number) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(number);
	}

	public String decodeNickAsJson(String nickAsJson) {
		Gson gson = new Gson();
		String nick = gson.fromJson(nickAsJson, String.class);
		return nick;
	}

	public String encodeNick(String nick) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(nick);
	}

	public double decodeDouble(String jsonNumber) {
		Gson gson = new Gson();
		double number = gson.fromJson(jsonNumber, double.class);
		return number;
	}

	public String encodePlayersList(CopyOnWriteArrayList<Player> playersList) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(playersList);
	}

	public String encodeBulletsList(CopyOnWriteArrayList<Bullet> bulletsList) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(bulletsList);
	}
}