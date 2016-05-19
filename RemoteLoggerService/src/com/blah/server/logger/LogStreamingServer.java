package com.blah.server.logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LogStreamingServer implements Runnable {
	
	private int port;
	private ServerSocket serverSocket;
	private ScheduledExecutorService pool= Executors.newScheduledThreadPool(100);

	public LogStreamingServer(int port) {
		this.port=port;
		try {
			serverSocket= new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while(true){
				Socket clientSocket= serverSocket.accept();
				pool.scheduleAtFixedRate(new ServerStreamHandler(clientSocket), 1,1, TimeUnit.SECONDS);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] a){
		new LogStreamingServer(2121).run();
	}
	
	

}