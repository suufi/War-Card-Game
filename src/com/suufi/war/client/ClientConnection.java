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
				
				
				String[] data = line.split(" ");

				if (!data[0].equals("deal"))
					clientGUI.log(line);
				
				if (data[0].equals("deal")) {
					data = data[1].split("(?<=\\d)(?=\\D)");
					
					String cardId = data[0] + data[1] + ".png";
					// clientGUI.log(cardId);
					
					hand.addCard(data[0] + data[1]);
					// clientGUI.initHand(cardId);
					
					updateHandSizes();
										
				} else if (data[0].equals("dealt")) {
					
					oppHandSize = 26;
					updateHandSizes();
					
				} else if (data[0].equals("start")) {
					
					clientGUI.startTimer();
					
				} else if (data[0].equals("turn")) {
					
					clientGUI.enableTurn();
				
				} else if (data[0].equals("war")) {

					clientGUI.startWar();
					
				} else if (data[0].equals("oppPlayed")) {
						
					clientGUI.putCard(data[1], Side.LEFT);
					// oppHandSize--;
					
				} else if (data[0].equals("oppPlayedCard")) {
					
					oppHandSize--;
					updateHandSizes();
	
		        } else if (data[0].equals("newRound")) { 
		            
		            clientGUI.resetView();
		            hand.shuffle(); 
		            
				} else if (data[0].equals("won")) {
					
					data = data[1].split("(?<=\\d)(?=\\D)");
					
					String cardId = data[0] + data[1] + ".png";
					
					hand.addCard(data[0] + data[1]);
					
					// oppHandSize--;
					updateHandSizes();
					
				} else if (data[0].equals("lost")) {
					
					oppHandSize++;
					updateHandSizes();
					
				} else if (data[0].equals("serverStop")) {
					
					clientGUI.showDialog("The server has been stopped and you were kicked.");
					System.exit(0);
					
				} else if (data[0].equals("forfeit")) {
					
					clientGUI.showDialog("Someone knew they weren't going to win this. They forfeit.");
					System.exit(0);
				}
				
			}
		} catch(IOException error) {
			System.out.println(error);
		}
	}
	
	public boolean checkIfTurn() {
		return isTurn;
	}

	public void setUsername(String username) {
	    out.println("name " + username);
	}
	
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
	
	public void playWarCards() {
		if (hand.size() > 3) {			
			ArrayList<String> cards = hand.drawTop(3);
			System.out.println(cards.toString());
			
			ArrayList<String> warCard = hand.drawTop(1);
			
			for (String card : cards) {
				clientGUI.putWarCard("back", Side.RIGHT);
			}
			
			clientGUI.putWarCard(warCard.get(0), Side.RIGHT);
		}
	}
	
	public void clearPlayCards() {
		clientGUI.resetView();
	}
	
	public void updateHandSizes() {
		clientGUI.updateHandSize(hand.size(), false);
		clientGUI.updateHandSize(oppHandSize, true);
	}
	
	public void updateHandSize() {
		clientGUI.updateHandSize(hand.size(), false);
	}
	
	public void updateOpponentHandSize(int amount) {
		clientGUI.updateHandSize(amount, true);
	}
	
	public void shuffleHand() {
		hand.shuffle();
	}
	
	public void forfeit() {
		out.println("forfeit");
	}
}
