package com.blah.client.logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Callable;

public class StreamingHandler implements Runnable {

	
	private static final int MAX_OFFSET = Integer.MAX_VALUE;
	private final Socket serverSocket;
	private volatile int lastOffset;
	private volatile  FileInputStream fis;
	
	public StreamingHandler(String fileName, Socket serverSocket, int lastOffset){
		this.serverSocket=serverSocket;
		this.lastOffset=lastOffset;
		try {
			this.fis= new FileInputStream(LogManager.getLogLocation()+File.separatorChar+fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void run()  {
		try {
			if(lastOffset > MAX_OFFSET){
				fis.close();
				return ;
			}
			byte[] bytesRead= new byte[1000];
			int totalRead= fis.read(bytesRead, 0, bytesRead.length);
			if(totalRead !=-1){
				//Write to Socket
				serverSocket.getOutputStream().write(bytesRead);
				lastOffset+=totalRead;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

}