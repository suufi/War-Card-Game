package com.suufi.war.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class ClientGUI {

	private JFrame frame;
	private JPanel topPanel;
	private JPanel midPanel;
	private JPanel bottomPanel;
	private JButton btnShuffle;
	private JButton btnForfeit;
	private JButton playerHand;
	private JLabel lblTimer;
	private JLabel lblHandSize;
	private JLabel lblOppHandSize;
	private JLabel lblWar;
	private JTextArea serverMessages;
	
	private ClientConnection connection;
	private JPanel opponentPanel;
	
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
		frame.setBounds(100, 100, 688, 511);
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
		
		lblWar = new JLabel("War! Play 3 cards and draw your top card.");
		lblWar.setBounds(225, 5, 259, 16);
		midPanel.add(lblWar);
		
		JPanel playerPanel = new JPanel();
		playerPanel.setBounds(356, 33, 331, 147);
		midPanel.add(playerPanel);
		
		opponentPanel = new JPanel();
		opponentPanel.setBounds(0, 33, 357, 147);
		midPanel.add(opponentPanel);
		
		bottomPanel = new JPanel();
		bottomPanel.setBounds(0, 320, 725, 168);
		masterBox.add(bottomPanel);
		
		playerHand = new JButton("");
		playerHand.setIcon(new ImageIcon(ClientGUI.class.getResource("/Cards/back.png")));
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
}
