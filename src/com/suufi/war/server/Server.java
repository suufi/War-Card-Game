package com.suufi.war.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Server {

	private static final int portNumber = 9009;
	
	private GUI gui;

	ServerSocket serverSocket = null;
	boolean listeningSocket = true;
	int playerCount = 0;
	ArrayList<PlayerThread> playerThreads = new ArrayList<>();
	int currentPlayerIndex;
	
	ArrayList<PlayableCard> cardsInPlay = new ArrayList<>();
	ArrayList<PlayableCard> warCards = new ArrayList<>();
	
	/**
	 * Constructor for the Server class
	 * 
	 * @param gui the server GUI
	 */
	public Server(GUI gui) {
		this.gui = gui;
	}

	/**
	 * Starts a socket by creating a new one or enabling listeningSocket
	 * which allows 
	 * 
	 * @throws IOException
	 */
	public void start() throws IOException {
		
		// Check if we already have created a serverSocket
		if (serverSocket == null) {
			
			// Try to start a server on portNumber
			try {
				gui.log("Server starting on port " + portNumber);
				
				// Create a new ServerSocket and assign to serverSocket 
				serverSocket = new ServerSocket(portNumber);
				
				gui.log("Server is now live @ " + InetAddress.getLocalHost().getHostAddress() + ":"
						+ serverSocket.getLocalPort());
				gui.log("Waiting for clients to connect");
				
			} catch (Exception e) {
				gui.log(e.toString());
			}
			
			while (listeningSocket) {
				
				// Accept clientSockets while players < 2
				if (playerCount < 2) {
					
					// accept all incoming socket connections as a clientSocket
					Socket clientSocket = serverSocket.accept();
					gui.log("Client has connected.");

					// create a "MiniServer" off of the clientSocket and start it
					PlayerThread playerThread = new PlayerThread(clientSocket, gui, this);
					playerThread.start();

					// send message to the client
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
					bw.write("Welcome to War! The game needs 2 players to start.");
					bw.newLine();
					bw.flush();

					// increment playerCount and add the clientSocket to playerSockets
					playerCount++;
					playerThreads.add(playerThread);
					
				} else {
					
					// Accept all incoming socket connections as a clientSocket
					Socket clientSocket = serverSocket.accept();
					gui.log("Extraneous client has connected.");

					// Tell client only 2 per server
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
					bw.write("Welcome to War! The game only permits 2 people per server. Sorry!");
					bw.newLine();
					bw.flush();

					// Kick them out
					clientSocket.close();
				}
			}
			
		} else {
			
			// enable the socket once again (it was disabled previously)
			listeningSocket = true;
			gui.log("Server started");
			
		}

	}

	/**
	 * Stops the running server by setting listeningSocket to false
	 * 
	 * @throws IOException
	 */
	public void stop() throws IOException {
		// if the ServerSocket is still listening, tell it to stop and kick all players out
		if (listeningSocket == true) {
			listeningSocket = false;
			playerCount = 0;
			
			// kick all players out
			for (PlayerThread player : playerThreads) {
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(player.getSocket().getOutputStream()));
				bw.write("serverStop");
				bw.newLine();
				bw.flush();
			}
			
			// clear playerThreads ArrayList
			playerThreads.clear();
		} else {
			gui.log("Can't stop what's not running!");
		}
	}

	/**
	 * Starts a game if there are 2 players
	 * 
	 * @throws IOException
	 */
	public void startGame() throws IOException {
		
		// Check player count
		if (playerCount == 2) {

			// Create a new deck and initialize it with cards
			Deck deck = new Deck();
			deck.initialize();
			
			// For each playerSocket connected
			for (PlayerThread player : playerThreads) {
				
				// Initialize a BufferedWriter to that player
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(player.getSocket().getOutputStream()));
				
				System.out.println("DECK SIZE: " + deck.size());

				// Divide the cards evenly among the players
				ArrayList<String> cards = new ArrayList<>();
				cards.addAll(deck.drawCards(52 / playerCount));
				
				System.out.println("PLAYER COUNT: " + playerCount);
				
				// Deal each card in cards by writing to the BufferedWriter
				for (String card : cards) {
					gui.log("dealing " + card + " to " + player.getPlayerName());
					bw.write("deal " + card);
					bw.newLine();
					
					// give player card that is dealt
					player.giveCard(card);
					
					try {
						TimeUnit.MILLISECONDS.sleep(58);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				// Send "dealt" to the client to signal a GUI update
				bw.write("dealt");
				bw.newLine();
				bw.flush();
				
				// Start the Timer on all player GUIs
				bw.write("start");
				bw.newLine();
				bw.flush();
			}
						
			// Get a random number that is 0 or 1; this will be a reference to the index of the starting player
			Random random = new Random();
			int startingPlayerIndex = random.nextInt(2);
			
			// Reorder the playerThreads so that the starting player is first
			Collections.swap(playerThreads, 0, startingPlayerIndex);
			currentPlayerIndex = 0;
			
			// Tell the client that was just swapped that it is their turn
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(playerThreads.get(0).getSocket().getOutputStream()));
			bw.write("turn");
			bw.newLine();
			bw.flush();
			
		} else {
			gui.log("You need 2 players to play War.");
			throw new IOException("NotEnoughPlayers");
		}
	}
	
	public void playCard(Socket playerSocket, PlayableCard card) throws IOException {
		
		// add card to cardsInPlay
		System.out.println("adding card: " + card.toString());
		cardsInPlay.add(card);
		
		// set bwOpponent to opposite of whoever played the card
		BufferedWriter bwOpponent; 
		
		if (playerSocket.equals(playerThreads.get(0).getSocket())) {
			bwOpponent = new BufferedWriter(new OutputStreamWriter(playerThreads.get(1).getSocket().getOutputStream()));
		} else {
			bwOpponent = new BufferedWriter(new OutputStreamWriter(playerThreads.get(0).getSocket().getOutputStream()));
		}
		
		// tell them that opponent played a card
		bwOpponent.write("oppPlayedCard");
		bwOpponent.newLine();
		bwOpponent.flush();
		
		// check if two cards are in play
		if (cardsInPlay.size() == 2) {

			// tell both players the card that was played by their opponent
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(cardsInPlay.get(0).getPlayerSocket().getOutputStream()));
			bw.write("oppPlayed " + card.toString());
			bw.newLine();
			bw.flush();
			
			bw = new BufferedWriter(new OutputStreamWriter(playerSocket.getOutputStream()));
			bw.write("oppPlayed " + cardsInPlay.get(0));
			bw.newLine();
			bw.flush();
			
			// pause for 2 seconds (add some suspense)
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// check if the card that was played is stronger, if it was give all cardsInPlay to player that just played card
			if (card.isStronger(cardsInPlay.get(0))) {
								
				BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(playerSocket.getOutputStream()));
				
				for (PlayableCard cardWon : cardsInPlay) {
					bw2.write("won " + cardWon.toString());
					bw2.newLine();
					bwOpponent.write("lost " + cardWon.toString());
					bwOpponent.newLine();
					bwOpponent.flush();
				}
				
				cardsInPlay.clear();
				
				bw2.flush();				
				
			// does the opposite of above
			} else if (cardsInPlay.get(0).isStronger(card)) {
				
				
				BufferedWriter playerBW = new BufferedWriter(new OutputStreamWriter(playerSocket.getOutputStream()));
				
				try {
					TimeUnit.SECONDS.sleep(3);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(cardsInPlay.get(0).getPlayerSocket().getOutputStream()));
				
				for (PlayableCard cardWon : cardsInPlay) {
					bw2.write("won " + cardWon.toString());
					bw2.newLine();
					playerBW.write("lost " + cardWon.toString());
					playerBW.newLine();
					playerBW.flush();
				}
				
				cardsInPlay.clear();
				
				bw2.flush();
				
			// it is war at the point
			} else {
					
					// For each playerSocket connected
					for (PlayerThread player : playerThreads) {
						
						// Initialize a BufferedWriter to that player
						BufferedWriter playerBW = new BufferedWriter(new OutputStreamWriter(player.getSocket().getOutputStream()));
						
						// tell player it is war and no one wins
						playerBW.write("war");
						playerBW.newLine();
						playerBW.flush();
						
					}
					
					// clear cardsInPlay because no one gets them
					cardsInPlay.clear();
					
					// pause for 4 seconds
					try {
						TimeUnit.SECONDS.sleep(4);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
			}
			
			// check if player0 ran out of cards, if they did, tell player 1 they won; kicks all players out then
			if (playerThreads.get(0).getCards().size() == 0) {
				BufferedWriter playerBW = new BufferedWriter(new OutputStreamWriter(playerThreads.get(0).getSocket().getOutputStream()));
				
				playerBW.write("LOSER");
				playerBW.newLine();
				playerBW.flush();
				
				playerBW = new BufferedWriter(new OutputStreamWriter(playerThreads.get(1).getSocket().getOutputStream()));
				playerBW.write("WINNER");
				playerBW.newLine();
				playerBW.flush();
				
				playerCount = 0;
				playerThreads.clear();
			}
			
			// check if player1 ran out of cards, if they did, tell player 2 they won; kicks all players out then
			if (playerThreads.get(1).getCards().size() == 0) {
				BufferedWriter playerBW = new BufferedWriter(new OutputStreamWriter(playerThreads.get(0).getSocket().getOutputStream()));
				
				playerBW.write("WINNER");
				playerBW.newLine();
				playerBW.flush();
				
				playerBW = new BufferedWriter(new OutputStreamWriter(playerThreads.get(1).getSocket().getOutputStream()));
				playerBW.write("LOSER");
				playerBW.newLine();
				playerBW.flush();
				
				playerCount = 0;
				playerThreads.clear();
			}

			// if both players still have cards to play, run a another round
			if (playerThreads.get(0).getCards().size() > 0 && playerThreads.get(1).getCards().size() > 0) {				
				newRound();
			}
			
			
		} else {
			// there has only been 1 card played so run turnNext() for another card from next player
			
			turnNext();
		}
	}
	
	/**
	 * Tells the next player that it is their turn
	 * @throws IOException
	 */
	public void turnNext() throws IOException {
		// If the currentPlayerIndex is less than the playerCount - 1, 
		// increment the currentPlayerIndex; otherwise, reset it back to 0 
		// so we don't go over
		if (currentPlayerIndex < playerCount - 1) {
			currentPlayerIndex++;
		} else {
			currentPlayerIndex = 0;
		}
		
		// Tell the current client that it is their turn
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(playerThreads.get(currentPlayerIndex).getSocket().getOutputStream()));
		bw.write("turn");
		bw.newLine();
		bw.flush();		
		
	}
	
	/**
	 * Runs another round by telling each player newRound() and make the next player play
	 * @throws IOException
	 */
	public void newRound() throws IOException {
		
		// For each playerSocket connected
		for (PlayerThread player : playerThreads) {
			
			// Initialize a BufferedWriter to that player
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(player.getSocket().getOutputStream()));
		
			// Tell them newRound
			bw.write("newRound");
			bw.newLine();
			bw.flush();
			
		}
		
		// Pass on the playing player status to next player
		turnNext();
	}
	
	/**
	 * Stops the game and kicks all players out.
	 * @throws IOException
	 */
	public void endGame() throws IOException {
		playerCount = 0;
		
		// tell player that server was stopped and kick them
		for (PlayerThread player : playerThreads) {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(player.getSocket().getOutputStream()));
			bw.write("serverStop");
			bw.newLine();
			bw.flush();
			
		}

		// clear playerThreads
		playerThreads.clear();
	}
	
	/**
	 * Allow player to forfeit. If this method is called, all players are kicked and they are left to figure who chickened out.
	 * @throws IOException
	 */
	public void forfeit() throws IOException {
		for (PlayerThread player : playerThreads) {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(player.getSocket().getOutputStream()));
			bw.write("forfeit");
			bw.newLine();
			bw.flush();
			
		}
		
		playerThreads.clear();
		playerCount = 0;
	}
	
}
