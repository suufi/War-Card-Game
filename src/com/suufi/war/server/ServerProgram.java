package com.suufi.war.server;

import java.io.IOException;

public class ServerProgram {

	public static void main(String[] args) {

		try {
			new GUI();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}