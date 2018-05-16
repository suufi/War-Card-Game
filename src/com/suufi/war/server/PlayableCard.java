package com.suufi.war.server;

import java.net.Socket;

public class PlayableCard {
	
	Socket playerSocket;
	String card;
	
	int value;
	
	/**
	 * Creates a PlayableCard
	 * @param card - card value formatted as "6d"
	 * @param playerSocket - the Socket of the player that played this card
	 */
	public PlayableCard(String card, Socket playerSocket) {
		this.playerSocket = playerSocket;
		this.card = card;
		this.value = Integer.parseInt(card.split("(?<=\\d)(?=\\D)")[0]);
	}
	
	/**
	 * Returns if this card equals another card
	 * @param card - card to compare against
	 * @return true or false depending on their equality
	 */
	public boolean equals(PlayableCard card) {
		if (value == card.value) return true;
		
		return false;
	}
	
	/**
	 * Returns true if this card isStronger than another card
	 * @param card - card to compare against
	 * @return true or false depending on power
	 */
	public boolean isStronger(PlayableCard card) {
		// ace always wins over everything else
		if (value == 1 && card.value != 1) return true;
		
		if (value > card.value) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns the card formatted as a String (basically the card itself)
	 */
	public String toString() {
		return this.card;
	}
	
	/**
	 * Returns the Socket of the player that played this card
	 * @return the socket of the player
	 */
	public Socket getPlayerSocket() {
		return this.playerSocket;
	}
}
