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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import javax.swing.SwingConstants;

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
				try {
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
		frame.setBounds(100, 100, 697, 526);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel masterBox = new JPanel();
		frame.getContentPane().add(masterBox, BorderLayout.CENTER);
		masterBox.setLayout(null);
		
		topPanel = new JPanel();
		topPanel.setLocation(0, 0);
		topPanel.setBackground(Color.LIGHT_GRAY);
		masterBox.add(topPanel);
		topPanel.setLayout(null);
		topPanel.setSize(687, 141);
		
		lblTimer = new JLabel("Time Elapsed: 0:00");
		lblTimer.setBounds(502, 12, 166, 15);
		topPanel.add(lblTimer);
		
		lblHandSize = new JLabel("Your Hand Size: 0");
		lblHandSize.setBounds(502, 39, 166, 15);
		topPanel.add(lblHandSize);
		
		btnForfeit = new JButton("Forfeit");
		btnForfeit.setFont(new Font("SF Compact Text", Font.PLAIN, 13));
		btnForfeit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connection.forfeit();
			}
		});
		btnForfeit.setBounds(502, 101, 166, 29);
		topPanel.add(btnForfeit);
		
		lblOppHandSize = new JLabel("Opponent's Hand Size: 0");
		lblOppHandSize.setBounds(502, 54, 166, 15);
		topPanel.add(lblOppHandSize);
		
		btnShuffle = new JButton("Shuffle: 3");
		btnShuffle.setFont(new Font("SF Compact Text", Font.PLAIN, 13));
		btnShuffle.setBounds(502, 73, 166, 29);
		btnShuffle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (shufflesRemaining > 0) {					
					connection.shuffleHand();
					shufflesRemaining--;
					btnShuffle.setText("Shuffle: " + shufflesRemaining);
					
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
		scrollPane.setBounds(10, 10, 480, 120);
		topPanel.add(scrollPane);
		
		serverMessages = new JTextArea();
		serverMessages.setText("Welcome to War!\n");
		serverMessages.setRows(5);
		serverMessages.setLineWrap(true);
		serverMessages.setForeground(Color.WHITE);
		serverMessages.setEditable(false);
		serverMessages.setColumns(30);
		serverMessages.setBackground(Color.DARK_GRAY);
		scrollPane.setViewportView(serverMessages);
		
		midPanel = new JPanel();
		midPanel.setBounds(0, 141, 725, 180);
		masterBox.add(midPanel);
		midPanel.setLayout(null);
		
		JLabel lblOpponent = new JLabel("Opponent");
		lblOpponent.setBackground(Color.LIGHT_GRAY);
		lblOpponent.setHorizontalAlignment(SwingConstants.CENTER);
		lblOpponent.setBounds(27, 11, 298, 41);
		midPanel.add(lblOpponent);
		
		lblYou = new JLabel("You");
		lblYou.setHorizontalAlignment(SwingConstants.CENTER);
		lblYou.setBackground(Color.LIGHT_GRAY);
		lblYou.setBounds(367, 11, 298, 41);
		midPanel.add(lblYou);
		
		lblWar = new JLabel("");
		lblWar.setBounds(225, 5, 259, 16);
		midPanel.add(lblWar);
		
		playerPanel = new JPanel();
		playerPanel.setBounds(356, 33, 331, 147);
		midPanel.add(playerPanel);
		
		lblCardPlayed = new JLabel("");
		playerPanel.add(lblCardPlayed);
		
		opponentPanel = new JPanel();
		opponentPanel.setBounds(0, 33, 357, 147);
		midPanel.add(opponentPanel);
		opponentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		lblOppCardPlayed = new JLabel("");
		opponentPanel.add(lblOppCardPlayed);
		
		bottomPanel = new JPanel();
		bottomPanel.setBounds(0, 320, 725, 168);
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
