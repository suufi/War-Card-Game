package com.suufi.war.client;

import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class Client {
	public JPanel selCardPanel = new JPanel();
	public ArrayList<ClickableCard> selCard = new ArrayList<ClickableCard>();
	public ArrayList<ClickableCard> hand = new ArrayList<ClickableCard>();
	public JPanel p = new JPanel(new FlowLayout());
	private JScrollPane scrollPane;
	public JPanel bottomPanel = new JPanel();
	public JPanel masterBox = new JPanel();
	private ClientConnection connection;
	private JFrame frame;
	private JPanel infoTower = new JPanel();
	private JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JButton submitButton = new JButton("submit");
	private JLabel handSize;
	private JLabel pileSize;
	private String username;
	private JTextArea serverMessages = new JTextArea();

	public static void main(String[] args) {
		try {
			// Attempt to create a ClientConnection after prompting user for an IP address
			ClientConnection connection = new ClientConnection(
					JOptionPane.showInputDialog("Please enter an IP address")
			);

			// ClientConnection connection = new ClientConnection("localhost");
		} catch (IOException e) {
			// Throw back error in a dialog
			JOptionPane.showMessageDialog(null, e.toString());
			throw new RuntimeException(e);
		}
	}

	public Client(ClientConnection cc) throws IOException {
		connection = cc;
		
		// Create a new JFrame that is the parent window of everything
		frame = new JFrame();
		frame.setResizable(true);
		frame.setTitle("BS Client");
		frame.setIconImage(ImageIO.read(Client.class.getResource("war-logo@4x.png")));

		// Set the container's layout to BoxLayout
		masterBox.setLayout(new BoxLayout(masterBox, BoxLayout.PAGE_AXIS));
		
		// Re-implement
		// JPanel p = new JPanel(new FlowLayout());
		
		// Create a JScrollPane that will hold the cards
		scrollPane = new JScrollPane(p);
		scrollPane.setPreferredSize(new Dimension(500, 125));

		// midPanel contains selCardPanel and submit button
		JPanel midPanel = new JPanel(new FlowLayout());
		midPanel.setBackground(Color.gray);

		/// topPanel contains the info box both left and right, timer, score, and card
		/// back visual
		infoTower.setLayout(new BoxLayout(infoTower, BoxLayout.PAGE_AXIS));
		handSize = new JLabel("hand size");
		pileSize = new JLabel("pile size");
		infoTower.add(handSize);
		infoTower.add(pileSize);

		JLabel title = new JLabel(" WELCOME TO BS!");
		serverMessages.setBounds(0, 0, 100, 100);
		serverMessages.setColumns(44);
		serverMessages.setRows(8);
		serverMessages.setEditable(false);

		JScrollPane scrollPane2 = new JScrollPane(serverMessages);
		scrollPane2.setAlignmentX(0);
		scrollPane2.setAlignmentY(0);

		// topPanel.add(infoTower);
		// topPanel.add(title);
		topPanel.add(scrollPane);
		topPanel.setPreferredSize(new Dimension(100, 150));
		topPanel.setBackground(Color.lightGray);
		// JPanel clock;

		// JPanel selCardPanel = new JPanel();
		selCardPanel.setLayout(new FlowLayout()); // panel for the selected cards
		selCardPanel.setBackground(Color.darkGray);
		selCardPanel.setPreferredSize(new Dimension(350, 110));
		selCardPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		midPanel.add(selCardPanel);
		// submitButton.setActionCommand("enable");
		submitButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// Execute when button is pressed
				System.out.println("You clicked the button");
				System.out.println("sending selCards: " + selCard);
				// connection.submit(selCard);
				submitButton.setEnabled(false);
			}
		});
		submitButton.setEnabled(false);
		midPanel.add(submitButton);

		JButton bsButton = new JButton("Call BS");
		bsButton.setActionCommand("enable");
		bsButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// Execute when button is pressed
				System.out.println("You clicked the BS button");
				connection.sendBS();

			}
		});
		midPanel.add(bsButton);

		// JPanel bottomPanel = new JPanel(); the container for scrollPane for selected cards
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.PAGE_AXIS));
		bottomPanel.add(midPanel);
		bottomPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		bottomPanel.add(scrollPane);
		bottomPanel.add(scrollPane2);
		
		masterBox.add(topPanel);
		masterBox.add(bottomPanel);
		frame.getContentPane().add(masterBox);

		frame.pack();
		frame.setVisible(true);
		setName(JOptionPane.showInputDialog("Please enter a username"));
	}

	public boolean addToSelCards(ClickableCard cardToAdd) {
		if (selCard.size() < 4 && selCard.size() >= 0) {
			selCard.add(cardToAdd);
			hand.remove(cardToAdd);

			p.remove(cardToAdd);
			selCardPanel.add(cardToAdd);

			bottomPanel.validate();
			bottomPanel.repaint();
			System.out.println("selCard size " + selCard.size());
			return false;
		} else {
			System.out.println("you cannot select more than 4 cards");
			return true;
		}

	}

	public boolean addToHand(ClickableCard cardToAdd) {
		placeCard(cardToAdd);
		selCard.remove(cardToAdd);

		displayCards();
		selCardPanel.remove(cardToAdd);

		bottomPanel.validate();
		bottomPanel.repaint();
		System.out.println("hand size: " + hand.size());
		return true;

	}

	public void initHand(String cardName) {
		String cardID = cardName;
		ClickableCard handCard = new ClickableCard(this, cardID,
				new ImageIcon(Client.class.getResource("/Cards/" + cardID)), "scrollpane");
		if (hand.size() == 0) {
			hand.add(handCard);
		} else {
			int before = hand.size();
			for (int i = 0; i < hand.size(); i++) {
				if (hand.get(i).getCardValue() >= handCard.getCardValue()) {
					hand.add(i, handCard);
					i = hand.size() + 10;
				}
			}
			if (before == hand.size())
				hand.add(handCard);
		}

	}

	// returns the cards in sorted order to hand 
	public void placeCard(ClickableCard card) { 
		String cardID = card.getID();
		// ClickableCard handCard = new ClickableCard(this, cardID, new
		// ImageIcon(Bsdisplay.class.getResource(cardID)), "scrollpane");
		if (hand.size() == 0) {
			hand.add(card);
		} else {
			int before = hand.size();
			for (int i = 0; i < hand.size(); i++) {
				if (hand.get(i).getCardValue() >= card.getCardValue()) {
					hand.add(i, card);
					i = hand.size() + 10;
				}
			}
			if (before == hand.size())
				hand.add(card);
		}

	}

	// shows all cards after dealt is called
	public void displayCards() 
	{
		bottomPanel.remove(scrollPane);
		scrollPane = new JScrollPane(p);
		bottomPanel.add(scrollPane);
		for (int i = 0; i < hand.size(); i++) {
			p.add(hand.get(i));
		}
		bottomPanel.validate();
		bottomPanel.repaint();

	}

	public JButton getSubmitButton() {
		return submitButton;
	}

	public void clearSubmitted() {
		selCard = new ArrayList<ClickableCard>();
		selCardPanel.removeAll();
		selCardPanel.validate();
		selCardPanel.repaint();

	}

	public void setName(String name) {
		username = name;
		infoTower.add(new JLabel("You are: " + username));
		infoTower.add(new JLabel("----------------")); // supposed to be a spacer
		topPanel.validate();
		topPanel.repaint();
		connection.setUsername(name);
	}

	public void showDialog(String text) {
		JOptionPane.showMessageDialog(frame, text);
	}

	public void updateHandSize() {
		infoTower.remove(handSize);
		handSize = new JLabel("Your Hand: " + hand.size());
		infoTower.add(handSize);
		topPanel.validate();
		topPanel.repaint();
	}

	public void updatePileSize(String pile) {
		infoTower.remove(pileSize);
		pileSize = new JLabel("Num Cards in Pile: " + pile);
		infoTower.add(pileSize);
		topPanel.validate();
		topPanel.repaint();
	}

	public static String load(String fileName) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			// ArrayList<String> lines = new ArrayList<String>();
			return in.readLine();
		}
		/**
		 * while (line != null) { lines.add(line); line = in.readLine(); } in.close();
		 * String[] array = new String[lines.size()]; for (int i = 0; i < array.length;
		 * i++) array[i] = lines.get(i); return array; }
		 */
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void log(String message) {
		serverMessages.append("[SERVER] " + message + "\n");
	}

}