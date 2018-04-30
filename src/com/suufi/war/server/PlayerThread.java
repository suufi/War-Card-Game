package com.suufi.war.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class PlayerThread extends Thread {

	private Socket socket = null;
	private GUI gui;
	private Server server;
	private String playerName = "";

	/**
	 * Constructor for a PlayerThread
	 * @param socket - the player's socket
	 * @param gui - the server GUI
	 */
	public PlayerThread(Socket socket, GUI gui, Server server) {

		super("MiniServer");
		this.socket = socket;
		this.gui = gui;
		this.server = server;
	}

	/**
	 * What to execute when the Thread is run
	 */
	public void run() {
		// Receive message from the client
		try {
			
			// Create a BufferedReader to read what the client is sending
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String data;
			
			// While what the client sent exists
			while ((data = br.readLine()) != null) {
				
				// Check if name was set
				if (data.contains("name")) {
					
					// Split the message so the playerName can be read
					String[] parts = data.split(" ");
					this.playerName = parts[1];
					
				}
				
				if (data.contains("play")) {
					String[] parts = data.split(" ");

					server.playCard(socket, parts[1]);
				}
				
				// Log it to the server
				gui.log("Message from " + playerName + ": " + data);
			}
		} catch (IOException error) {
			System.out.println(error);
		}
	}
	
	/**
	 * Returns the player's name
	 * 
	 * @return the player's name
	 */
	public String getPlayerName() {
		return playerName;
	}
	
	/**
	 * Returns the player's socket so it can be used
	 * 
	 * @return the player's socket
	 */
	public Socket getSocket() {
		return socket;
	}
}