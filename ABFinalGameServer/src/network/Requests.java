package network;

public enum Requests {

	ASK_FOR_JOIN, READY, MOVE_UP, MOVE_DOWN, MOVE_LEFT, MOVE_RIGHT, SHOT, ROTATION;
	
	public String toString() {
		return this.name();
	}
}
