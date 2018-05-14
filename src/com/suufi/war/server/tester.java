package com.suufi.war.server;

import java.util.ArrayList;

import com.suufi.war.client.Deck;

public class tester {

	
	public static void main(String[] args) {
		Deck a = new Deck();
		a.badInit();
		
		ArrayList<String> cards = a.drawCards(52 / 2);
		System.out.println(cards.toString());
		System.out.println(cards.size());
	}

}
