package com.suufi.war.server;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
	ArrayList<String> deck;
	
	/**
	 * Assign an empty ArrayList to deck
	 */
	public Deck() {
		deck = new ArrayList<>();
	}
	
	/**
	 * Populate deck with cards
	 * Each card is formatted as a String that looks like "1s" for Ace of Spades
	 */
	public void initialize() {
		for (int suit = 1; suit < 5; suit++) {
			for (int val = 1; val < 14; val++) {
				String suitLetter = null;
				if (suit == 1) suitLetter = "c";
				if (suit == 2) suitLetter = "d";
				if (suit == 3) suitLetter = "h";
				if (suit == 4) suitLetter = "s";
				
				deck.add(val + suitLetter);
			}
		}
	}
	
	/**
	 * Shuffles the deck
	 */
	public void shuffle() {
		Collections.shuffle(deck);
	}
	
	/**
	 * Returns a specific card from the deck by index
	 * 
	 * @param i - index
	 * @return the card at that index
	 */
	public String getCard(int i) {
		String card = deck.get(i);
	
		return card;
	}

	/**
	 * Returns the whole deck
	 * 
	 * @return the deck
	 */
	public ArrayList<String> getCards() {
		return this.deck;
	}
	
	/**
	 * Returns the size of the whole deck
	 * 
	 * @return the size of the deck
	 */
	public int size() {
		return deck.size();
	}
	
	/**
	 * Removes a random card from the deck and returns it
	 * 
	 * @return a random card
	 */
	public String drawCard() {
		shuffle();
		
		int deckIndex = (int) (Math.random() * size());
		String card = deck.get(deckIndex);
		deck.remove(deckIndex);
		
		return card;
	}
	
	/**
	 * Removes a specific card from the deck based off the card, if it exists, and returns it
	 * 
	 * @param targetCard - a card (String)
	 * @return the card that was drawn or null if it did not exist
	 */
	public String drawCard(String targetCard) {
		boolean hasCard = deck.contains(targetCard);
		
		if (hasCard == true) {
			for (String card : deck) {
				if (card.equals(targetCard)) {
					deck.remove(card);
					return card;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Removes a specific card from the deck based off index and returns it if it exists
	 * 
	 * @param index - index of the card to draw
	 * @return the card that was drawn
	 */
	public String drawCard(int index) {
		String card = deck.get(index);
		deck.remove(card);
		return card;
	}
	
	/**
	 * Removes a number of random cards from the deck and returns them
	 * 
	 * @param quantity - number of cards to draw
	 * @return the cards drawn
	 */
	public ArrayList<String> drawCards(int quantity) {
		ArrayList<String> drawnCards = new ArrayList<String>();
		
		for (int i = 0; i < quantity; i++) {
			String card = drawCard();
			deck.remove(card);
			drawnCards.add(card);
		}
		
		return drawnCards;
	}
	
	/**
	 * Removes the given <b>cards</b> from the deck and returns them if they exist
	 * 
	 * @param cards - an ArrayList containing the cards that will be drawn
	 * @return the cards drawn
	 */
	public ArrayList<String> drawCards(ArrayList<String> cards) {
		ArrayList<String> drawingCards = new ArrayList<String>();
		
		for (String card : deck) {
			if (cards.contains(card)) {
				deck.remove(card);
				drawingCards.add(card);
			}
		}
		
		return drawingCards;
	}
	
	/**
	 * Removes a certain number of cards from the top of the deck and returns them
	 * 
	 * @param quantity - number of cards to draw
	 * @return the cards drawn
	 */
	public ArrayList<String> drawTop(int quantity) {
		ArrayList<String> drawingCards = new ArrayList<String>();
		
		for (int i = 0; i < quantity; i++) {
			drawingCards.add(deck.get(i));
			deck.remove(i);
		}
		
		return drawingCards;
		
	}
	
	/**
	 * Add a card back to the deck
	 * 
	 * @param card the card to return back to the deck
	 */
	public void addCard(String card) {
		deck.add(card);
	}

	/**
	 * Add a group of cards back to the deck
	 * 
	 * @param cards the cards to return back to the deck
	 */
	public void addCards(ArrayList<String> cards) {
		deck.addAll(cards);
	}
	
	/**
	 * Remove a card from the deck
	 * 
	 * @param card - card to remove
	 */
	public void removeCard(String card) {
		deck.remove(card);
	}
	
	/**
	 * Remove a group of cards from the deck
	 * 
	 * @param cards - cards to remove
	 */
	public void removeCards(ArrayList<String> cards) {
		deck.removeAll(cards);
	}
	
	/**
	 * Returns a boolean that checks if the deck contains a card
	 * 
	 * @param card - card to check for
	 * @return a boolean telling whether the deck contains a card
	 */
	public boolean contains(String card) {
		if (deck.contains(card)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns a boolean that checks if a group of cards are all in the deck
	 * 
	 * @param cards - cards to check for
	 * @return a boolean telling if all the cards are in the deck
	 */
	public boolean contains(ArrayList<String> cards) {
		if (deck.containsAll(cards)) {
			return true;
		} else {
			return false;
		}
	}
	
}