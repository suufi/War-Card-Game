package com.suufi.war.server;

import java.io.IOException;

public class ServerProgram {

	public static void main(String[] args) {

		// try to create a new GUI when program is run else catch it
		
		try {
			new GUI();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}