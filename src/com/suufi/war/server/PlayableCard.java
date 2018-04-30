package com.suufi.war.server;

import com.suufi.war.client.Client;

public class PlayableCard {
	
	Client player;
	String card;
	
	int value;
	
	public PlayableCard(String card, Client player) {
		this.player = player;
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
}
