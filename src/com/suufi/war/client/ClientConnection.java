package com.suufi.war.client;
import java.io.*;
import java.net.*;
import java.util.*;

public class ClientConnection extends Thread {
	
	private Socket socket;
	private BufferedReader in; // what comes from the server
	private PrintWriter out;   // what is sent to the server
	private ClientGUI clientGUI;
	private boolean isTurn;
	
	private Deck hand = new Deck();
	
	public ClientConnection(String host) throws IOException {
		this.socket = new Socket(host, 9009);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		clientGUI = new ClientGUI(this);
	    start();
	}
	
	public void run() {
		try {
			while (true) {
				String line = in.readLine();
				clientGUI.log(line);
				
				String[] data = line.split(" ");
				
				if (data[0].equals("deal")) {
					data = data[1].split("(?<=\\d)(?=\\D)");
					
					System.out.println(data[0]);
					System.out.println(data[1]);
					
					String cardId = data[0] + data[1] + ".gif";
					clientGUI.log(cardId);
					
					hand.addCard(data[0] + data[1]);
					// clientGUI.initHand(cardId);
				} else if (data[0].equals("dealt")) {
					clientGUI.updateHandSize(hand.size(), false);
					clientGUI.updateHandSize(26, true);
				} else if (data[0].equals("turn")) {
					clientGUI.enableTurn();
				} else if (data[0].equals("serverStop")) {
					clientGUI.showDialog("The server has been stopped and you were kicked.");
					System.exit(0);
				}
			}
		} catch(IOException error) {
			System.out.print(error);
		}
	}
	
	public boolean checkIfTurn() {
		return isTurn;
	}
	
	public void sendBS() {
		out.println("bsbs");
	}

	public void setUsername(String username) {
	    out.println("name " + username);
	}
	
	public void submit(ClickableCard card) {
		
	}
	
	/*
	 * public void submit(ArrayList<ClickableCard> submittedCards) {
	 *
	    String totalList = "";
	    for(int i = 0; i<submittedCards.size(); i++)
	    {
	      totalList = totalList + submittedCards.get(i).getCardValue() + " " + submittedCards.get(i).getSuit()+ " ";
	    }
	    client.updateHandSize();
	    client.log(totalList);
	    out.println("play " + totalList);
	    client.clearSubmitted();
	}
	*/
}
