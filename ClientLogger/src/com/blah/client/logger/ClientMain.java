package com.blah.client.logger;

public class ClientMain {
	
	public static void main(String[] a){
		new ClientLogStreamingService("localhost", 2121).run();
		
	}

}
