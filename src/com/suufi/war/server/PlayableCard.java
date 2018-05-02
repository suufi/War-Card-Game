package com.suufi.war.server;

import java.net.Socket;

public class PlayableCard {
	
	Socket playerSocket;
	String card;
	
	int value;
	
	public PlayableCard(String card, Socket playerSocket) {
		this.playerSocket = playerSocket;
		this.card = card;
		this.value = Integer.parseInt(card.split("(?<=\\d)(?=\\D)")[0]);
	}
	
	public boolean equals(PlayableCard card) {
		if (value == card.value) return true;
		
		return false;
	}
	
	public boolean isStronger(PlayableCard card) {
		if (value > card.value) {
			return true;
		}
		
		return false;
	}
	
	public String toString() {
		return this.card;
	}
	
	public Socket getPlayerSocket() {
		return this.playerSocket;
	}
}
