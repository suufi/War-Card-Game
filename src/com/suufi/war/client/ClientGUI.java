package com.suufi.war.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class ClientGUI {

	private JFrame frame;
	private JPanel topPanel;
	private JPanel midPanel;
	private JPanel bottomPanel;
	private JPanel playerPanel;
	private JPanel opponentPanel;
	private JButton btnShuffle;
	private JButton btnForfeit;
	private JButton playerHand;
	private JLabel lblTimer;
	private JLabel lblHandSize;
	private JLabel lblOppHandSize;
	private JLabel lblWar;
	private JLabel lblCardPlayed;
	private JLabel lblOppCardPlayed;
	private JLabel lblYou;
	
	private JTextArea serverMessages;
	
	private ClientConnection connection;

	private TimeWatch watch;
	
	private boolean war;
	private int shufflesRemaining = 3;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				System.out.println(System.getProperty("os.name"));
				if (System.getProperty("os.name").startsWith("Mac OS X")) {
					System.setProperty("apple.laf.useScreenMenuBar", "true");
					System.setProperty("apple.awt.graphics.UseQuartz", "true");
					System.setProperty("com.apple.mrj.application.apple.menu.about.name", "War Client");
				}

				try {
					
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					
					new ClientConnection(JOptionPane.showInputDialog("Please enter a server IP address"));
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @wbp.parser.entryPoint
	 */
	public ClientGUI(ClientConnection cc) {
		this.connection = cc;
		
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(ClientGUI.class.getResource("/com/suufi/war/client/war-client-logo@4x.png")));
		frame.setTitle("War Client");
		frame.setBounds(100, 100, 837, 579);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel masterBox = new JPanel();
		frame.getContentPane().add(masterBox, BorderLayout.CENTER);
		masterBox.setLayout(null);
		
		JMenuBar menubar = new JMenuBar();
		menubar.setBounds(0, 0, 835, 28);
		masterBox.add(menubar);
		JMenu menu = new JMenu("Menu");
		JMenuItem size = new JMenuItem("Help");
		size.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showDialog("War (IRL) - Mohamed Suufi\n\nThe objective of the game is to win all cards.\n" + 
						"\n" + 
						"The deck is divided evenly among the players, giving each a down stack. In unison, each player reveals the \n"
						+ "top card of their deck—this is a \"battle\"—and the player with the higher card takes both of the cards played\n"
						+ "and moves them to their stack. Aces are high, and suits are ignored.\n" 
						+ "If the two cards played are of equal value, then there is a \"war\". In traditional verions of the game, you play 4\n"
						+ "additional cards where the fourth is the deciding card on who gets the card. In this version, the two cards that tied\n"
						+ "are discarded from the game.");
			}
		});
		menu.add(size);
		menubar.add(menu);
		
		topPanel = new JPanel();
		topPanel.setLocation(0, 27);
		topPanel.setBackground(Color.LIGHT_GRAY);
		masterBox.add(topPanel);
		topPanel.setLayout(null);
		topPanel.setSize(835, 141);
		
		lblTimer = new JLabel("Time Elapsed: 0:00");
		lblTimer.setBounds(509, 12, 166, 15);
		topPanel.add(lblTimer);
		
		lblHandSize = new JLabel("Your Hand Size: 0");
		lblHandSize.setBounds(509, 39, 166, 15);
		topPanel.add(lblHandSize);
		
		btnForfeit = new JButton("Forfeit");
		btnForfeit.setFont(new Font("SF Compact Text", Font.PLAIN, 13));
		btnForfeit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connection.forfeit();
			}
		});
		btnForfeit.setBounds(509, 101, 166, 29);
		topPanel.add(btnForfeit);
		
		lblOppHandSize = new JLabel("Opponent's Hand Size: 0");
		lblOppHandSize.setBounds(509, 54, 166, 15);
		topPanel.add(lblOppHandSize);
		
		btnShuffle = new JButton("Shuffle: 3");
		btnShuffle.setFont(new Font("SF Compact Text", Font.PLAIN, 13));
		btnShuffle.setBounds(509, 73, 166, 29);
		btnShuffle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (shufflesRemaining > 0) {					
					connection.shuffleHand();
					shufflesRemaining--;
					btnShuffle.setText("Shuffle: " + shufflesRemaining);
					log("Shuffled");
					
					if (shufflesRemaining == 0) {
						btnShuffle.setEnabled(false);
						btnShuffle.setText("Shuffle: 0");
					}
				} else {
					showDialog("Sorry! You ran out of shuffles.");
				}
			}
		});
		topPanel.add(btnShuffle);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(null);
		scrollPane.setBounds(17, 10, 480, 120);
		topPanel.add(scrollPane);
		
		serverMessages = new JTextArea();
		scrollPane.setViewportView(serverMessages);
		serverMessages.setText("Welcome to War!\n");
		serverMessages.setRows(5);
		serverMessages.setLineWrap(true);
		serverMessages.setForeground(Color.WHITE);
		serverMessages.setEditable(false);
		serverMessages.setColumns(30);
		serverMessages.setBackground(Color.DARK_GRAY);
		
		lblWar = new JLabel("");
		lblWar.setHorizontalAlignment(SwingConstants.LEFT);
		lblWar.setBounds(695, 12, 134, 118);
		topPanel.add(lblWar);
		
		midPanel = new JPanel();
		midPanel.setBounds(0, 170, 835, 201);
		masterBox.add(midPanel);
		midPanel.setLayout(null);
		
		playerPanel = new JPanel();
		playerPanel.setBounds(430, 33, 406, 147);
		midPanel.add(playerPanel);
		
		lblCardPlayed = new JLabel("");
		playerPanel.add(lblCardPlayed);
		
		opponentPanel = new JPanel();
		opponentPanel.setBounds(0, 33, 431, 147);
		midPanel.add(opponentPanel);
		opponentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		lblOppCardPlayed = new JLabel("");
		opponentPanel.add(lblOppCardPlayed);
		
		JLabel lblOpponent = new JLabel("Opponent");
		lblOpponent.setBounds(0, 0, 431, 35);
		midPanel.add(lblOpponent);
		lblOpponent.setBackground(Color.LIGHT_GRAY);
		lblOpponent.setHorizontalAlignment(SwingConstants.CENTER);
		
		lblYou = new JLabel("You");
		lblYou.setBounds(430, 0, 406, 35);
		midPanel.add(lblYou);
		lblYou.setHorizontalAlignment(SwingConstants.CENTER);
		lblYou.setBackground(Color.LIGHT_GRAY);
		
		bottomPanel = new JPanel();
		bottomPanel.setBounds(0, 373, 835, 184);
		masterBox.add(bottomPanel);
		
		playerHand = new JButton("");
		playerHand.setIcon(new ImageIcon(ClientGUI.class.getResource("/Cards/back.png")));
		playerHand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!war) {					
					connection.playCard();
					playerHand.setEnabled(false);
				} else {
					connection.playWarCards();
					playerHand.setEnabled(false);
				}
			}
		});
		playerHand.setEnabled(false);
		bottomPanel.add(playerHand);
		frame.setVisible(true);
		setName(JOptionPane.showInputDialog("Please enter a username"));
	}
	
	public void log(String message) {
		serverMessages.append("[SERVER] " + message + "\n");
	}
	
	public void setName(String name) {
		connection.setUsername(name);
	}
	
	public void updateHandSize(int size, boolean opponentHand) {
		if (opponentHand == true) {			
			lblOppHandSize.setText("Opponent's Hand Size: " + size);
		} else {
			lblHandSize.setText("Your Hand Size: " + size);
		}
	}
	
	public void showDialog(String text) {
		JOptionPane.showMessageDialog(frame, text);
	}
	
	public void enableTurn() {
		playerHand.setEnabled(true);
	}
	
	public void putCard(String card, Side side) {
		if (side == Side.RIGHT) {
			lblCardPlayed.setIcon(new ImageIcon(ClientGUI.class.getResource("/Cards/" + card + ".png")));
		} else {
			lblOppCardPlayed.setIcon(new ImageIcon(ClientGUI.class.getResource("/Cards/" + card + ".png")));
		}
	}
	
	public void putWarCard(String card, Side side) {
		JLabel cardLabel = new JLabel("");
		cardLabel.setIcon(new ImageIcon(ClientGUI.class.getResource("/Cards/" + card + ".png")));

		if (side == Side.RIGHT) {			
			playerPanel.add(cardLabel);
		} else {
			opponentPanel.add(cardLabel);
		}
	}

	public void resetView() {
		lblCardPlayed.setIcon(null);
		lblOppCardPlayed.setIcon(null);
		lblWar.setText("");
		war = false;
		
		for (Component element : playerPanel.getComponents()) {
			if (element != lblCardPlayed) playerPanel.remove(element);;
		}
		
		for (Component element : opponentPanel.getComponents()) {
			if (element != lblOppCardPlayed) opponentPanel.remove(element);;
		}
		
	}
	
	public ClientConnection getClientConnection() {
		return this.connection;
	}
	
	public void startTimer() {
		watch = TimeWatch.start();
		Thread thread = new Thread(() -> {
			while (true) {
				lblTimer.setText("Time Elapsed: " + watch.toString());
			}
		});
		thread.start();
	}
	
	public void startWar() {
		war = true;
		lblWar.setText("<html>It is war! No one wins. That is truth about War in reality.</html>");
	}
}
