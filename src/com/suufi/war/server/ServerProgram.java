package com.suufi.war.server;

import java.io.IOException;

public class ServerProgram {

	public static void main(String[] args) {

		/*
		 * ServerSocket serverSocket = null; boolean listeningSocket = true; int
		 * playerCount = 0;
		 * 
		 * try { //starting server
		 * System.out.println("Server starting at port number: "+portNumber);
		 * serverSocket = new ServerSocket(portNumber);
		 * 
		 * //client connecting System.out.println("Waiting for clients to connect"); }
		 * catch (Exception e) { e.printStackTrace(); }
		 * 
		 * // while the socket is listening while (listeningSocket) { if (playerCount <
		 * 4) { // accept all incoming socket connections as a clientSocket Socket
		 * clientSocket = serverSocket.accept();
		 * System.out.println("Client has connected.");
		 * 
		 * // create a "MiniServer" off of the clientSocket and start it MiniServer mini
		 * = new MiniServer(clientSocket); mini.start();
		 * 
		 * // send message to the client BufferedWriter bw = new BufferedWriter(new
		 * OutputStreamWriter(clientSocket.getOutputStream()));
		 * bw.write("Welcome to BS! The game needs 4 players to start."); bw.newLine();
		 * bw.flush();
		 * 
		 * playerCount++; } else { // accept all incoming socket connections as a
		 * clientSocket Socket clientSocket = serverSocket.accept();
		 * System.out.println("Extraneous client has connected.");
		 * 
		 * // tell client only 4 per server BufferedWriter bw = new BufferedWriter(new
		 * OutputStreamWriter(clientSocket.getOutputStream()));
		 * bw.write("Welcome to BS! The game only permits 4 people per server. Sorry!");
		 * bw.newLine(); bw.flush();
		 * 
		 * clientSocket.close(); } }
		 * 
		 * serverSocket.close();
		 */

		try {
			new GUI();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}