package com.suufi.war.client;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ClientConnection extends Thread {
	
	private Socket socket;
	private BufferedReader in; // what comes from the server
	private PrintWriter out;   // what is sent to the server
	private ClientGUI clientGUI;
	private boolean isTurn;
	private int oppHandSize;
	
	private Deck hand = new Deck(); // the client's deck size
	
	/**
	 * Constructor for new ClientConnection
	 * @param host - the IP address of the server
	 * @throws IOException
	 */
	public ClientConnection(String host) throws IOException {
		this.socket = new Socket(host, 9009);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		clientGUI = new ClientGUI(this);
	    start();
	}
	
	/**
	 * What to do for each clientconnection (player)
	 */
	public void run() {
		try {
			while (true) {
				
				// parse what the server sent
				String line = in.readLine();
				
				// split the string into segments by space
				String[] data = line.split(" ");

				// avoid logging dealt cards
				if (!data[0].equals("deal"))
					clientGUI.log(line);
				
				// check if the server sent a deal card, if it did regex parse the card and add it to the hand
				if (data[0].equals("deal")) {
					data = data[1].split("(?<=\\d)(?=\\D)");
					
					String cardId = data[0] + data[1] + ".png";
					
					hand.addCard(data[0] + data[1]);
					
					updateHandSizes();
										
				// when a deck is evenly split, each player in a 2 player game should have 26 cards
				} else if (data[0].equals("dealt")) {
					
					// update oppHandSize and refresh client gui
					oppHandSize = 26;
					updateHandSizes();
					
				// start the time if the server sends "start"
				} else if (data[0].equals("start")) {
					
					clientGUI.startTimer();
					
				// enable the draw card button if the server sends "turn"
				} else if (data[0].equals("turn")) {
					
					clientGUI.enableTurn();
				
				// show the startWar() label if it is was
				} else if (data[0].equals("war")) {

					clientGUI.startWar();
					
				// display to client what the opponent just played
				} else if (data[0].equals("oppPlayed")) {
						
					clientGUI.putCard(data[1], Side.LEFT);
					// oppHandSize--;
					
				// decrement oppHandSize if server sends "oppPlayedCard"
				} else if (data[0].equals("oppPlayedCard")) {
					
					oppHandSize--;
					updateHandSizes();
	
				// reset view if it is a new round 
		        } else if (data[0].equals("newRound")) { 
		            
		            clientGUI.resetView();
		            
		        // add whatever card was won to the player's hand
				} else if (data[0].equals("won")) {
					
					data = data[1].split("(?<=\\d)(?=\\D)");
					
					String cardId = data[0] + data[1] + ".png";
					
					hand.addCard(data[0] + data[1]);
					
					// oppHandSize--;
					updateHandSizes();
					
				// increment oppHandSize if a card was lost
				} else if (data[0].equals("lost")) {
					
					oppHandSize++;
					updateHandSizes();
					
				// kick player out if the server is stopped and tell them
				} else if (data[0].equals("serverStop")) {
					
					clientGUI.showDialog("The server has been stopped and you were kicked.");
					System.exit(0);
				
				// kick player out and tell they that they won if server sends back "WINNER"
				} else if (data[0].equals("WINNER")) {
					
					clientGUI.showDialog("GUESS WHO WON? YOU! CONGRATULATIONS!");
					System.exit(0);
				
				// kick player out and tell them that they lost if server sends back "LOSER"
				} else if (data[0].equals("LOSER")) {
					
					clientGUI.showDialog("Guess who's taking an L home. You sadly.");
					System.exit(0);
					
				// kick player out and have them figure out who "forfeit"ed
				} else if (data[0].equals("forfeit")) {
					
					clientGUI.showDialog("Someone knew they weren't going to win this. They forfeit.");
					System.exit(0);
				}
				
			}
		} catch(IOException error) {
			System.out.println(error);
		}
	}
	
	/**
	 * checks if it is the player's turn
	 * @return true or false depending on if it is the players turn
	 */
	public boolean checkIfTurn() {
		return isTurn;
	}

	/**
	 * Sets the clientconnection's username on the server side
	 * @param username - the username to send to the server
	 */
	public void setUsername(String username) {
	    out.println("name " + username);
	}
	
	/**
	 * Draws the top card from hand and places it in the GUI then
	 * it waits 1 second then sends it to the server where it is processed
	 * then it updates handSize for local client
	 */
	public void playCard() {
		ArrayList<String> card = hand.drawTop(1);
		System.out.println(card.toString());
		String cardValue = card.get(0);
		
		clientGUI.putCard(cardValue, Side.RIGHT);
		
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		out.println("play " + cardValue);
		updateHandSize();
		
	}
	
	/**
	 * Resets the view by clearing cards that were just played
	 */
	public void clearPlayCards() {
		clientGUI.resetView();
	}
	
	/**
	 * Updates all HandSizes for client
	 */
	public void updateHandSizes() {
		clientGUI.updateHandSize(hand.size(), false);
		clientGUI.updateHandSize(oppHandSize, true);
	}
	
	/**
	 * Updates clientHandSize only on the GUI
	 */
	public void updateHandSize() {
		clientGUI.updateHandSize(hand.size(), false);
	}
	
	/**
	 * Sets the opponent's hand size to a certain number
	 * @param amount - opponent's hand size
	 */
	public void updateOpponentHandSize(int amount) {
		clientGUI.updateHandSize(amount, true);
	}
	
	/**
	 * Shuffles the client's hand
	 */
	public void shuffleHand() {
		hand.shuffle();
	}
	
	/**
	 * Sends forfeit to the server if method is called
	 */
	public void forfeit() {
		out.println("forfeit");
	}
}
