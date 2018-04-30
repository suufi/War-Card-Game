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

public class Server {

	private static final int portNumber = 9009;
	
	private GUI gui;

	ServerSocket serverSocket = null;
	boolean listeningSocket = true;
	int playerCount = 0;
	ArrayList<PlayerThread> playerThreads = new ArrayList<>();
	int currentPlayerIndex;
	
	ArrayList<PlayableCard> cardsInPlay = new ArrayList<>();
	
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
		if (listeningSocket == true) {
			listeningSocket = false;
			playerCount = 0;
			
			for (PlayerThread player : playerThreads) {
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(player.getSocket().getOutputStream()));
				bw.write("serverStop");
				bw.newLine();
				bw.flush();
			}
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
				
				// Divide the cards evenly among the players
				ArrayList<String> cards = deck.drawCards(52 / playerCount);
				
				// Deal each card in cards by writing to the BufferedWriter
				for (String card : cards) {
					gui.log("dealing " + card + " to " + player.getPlayerName());
					bw.write("deal " + card);
					bw.newLine();
				}
				
				// Send "dealt" to the client to signal a GUI update
				bw.write("dealt");
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
		cardsInPlay.add(card);
		
		if (cardsInPlay.size() == 2) {
			if (card.isStronger(cardsInPlay.get(0))) {
				// TODO: add the cards to the player's deck
			}
		}
	}
	
	public void turnNext() throws IOException {
		// If the currentPlayerIndex is less than the playerCount, increment the currentPlayerIndex; otherwise, reset it back to 0 so we don't go over
		if (currentPlayerIndex < playerCount) {
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
}
