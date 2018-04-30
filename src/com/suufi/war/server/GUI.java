package com.suufi.war.server;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;

import javax.imageio.ImageIO;
import javax.swing.*;

public class GUI {

	private Server server;

	private JFrame frame;
	public JPanel masterBox = new JPanel();
	public JPanel bottomPanel = new JPanel();
	private JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

	private JTextArea serverMessages = new JTextArea();
	private JButton serverButton = new JButton();
	private JButton startGameButton = new JButton("Start Game");

	ServerSocket serverSocket = null;
	boolean listeningSocket = true;
	int playerCount = 0;

	public GUI() throws IOException {
		frame = new JFrame();
		// frame.setResizable(false);
		frame.setTitle("War Server");
		frame.setIconImage(ImageIO.read(GUI.class.getResource("war-server-logo@4x.png")));

		serverMessages.setBounds(0, 0, 100, 100);
		serverMessages.setColumns(44);
		serverMessages.setRows(9);
		serverMessages.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(serverMessages);

		topPanel.setPreferredSize(new Dimension(500, 150));
		topPanel.setBackground(Color.lightGray);

		serverButton.setText("Start");
		serverButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (serverButton.getText().equals("Start")) {
					Thread thread = new Thread(() -> {
						try {
							startServer();
						} catch (IOException e1) {
							log(e1.toString());
						}
					});
					thread.start();
					serverButton.setText("Stop");
					startGameButton.setEnabled(true);
				} else {
					try {
						stopServer();
					} catch (IOException e1) {
						log(e1.toString());
					}
					serverButton.setText("Start");
				}
			}
		});

		startGameButton.setEnabled(false);
		startGameButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					startGame();
					startGameButton.setEnabled(false);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});

		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.PAGE_AXIS));
		bottomPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		bottomPanel.add(scrollPane);
		topPanel.add(serverButton);
		topPanel.add(startGameButton);

		masterBox.add(topPanel);
		masterBox.add(bottomPanel);
		frame.getContentPane().add(masterBox);

		frame.setBounds(0, 0, 1100, 200);
		frame.setVisible(true);

		server = new Server(this);
	}

	private void startServer() throws IOException {
		log("Server starting");
		server.start();
	}

	private void startGame() throws IOException {
		log("Starting the game…");
		server.startGame();
	}

	private void stopServer() throws IOException {
		log("Stopping server...");
		server.stop();
	}

	void log(String message) {
		serverMessages.append(message + "\n");
	}
}
