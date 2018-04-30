package com.suufi.war.client;

import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class ClickableCard extends JLabel implements MouseListener {

	private String ID;
	private Client client;
	private boolean isInScrollPane;

	public ClickableCard(Client client, String ID, ImageIcon image, String panel) {
		super(image);
		this.ID = ID;
		this.client = client;

		addMouseListener(this);

		if (panel.equals("scrollpane"))
			this.isInScrollPane = true;
	}

	public int getCardValue() {
		return Integer.parseInt(ID.substring(0, ID.length() - 5));
	}

	public String getID() {
		return ID;
	}

	public void mouseClicked(MouseEvent e) {
		// System.out.println("Clicked "+ID);
		if (isInScrollPane == true) {
			isInScrollPane = client.addToSelCards(this);
			// System.out.println("put in selCard");
		} else {

			isInScrollPane = client.addToHand(this);
			// System.out.println("put back in hand");
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public String toString() {
		return ID + ":" + hashCode();
	}

	public String getSuit() {
		return ID.substring(ID.length() - 5, ID.length() - 4);
	}

}