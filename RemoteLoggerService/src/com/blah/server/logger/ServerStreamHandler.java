package com.blah.server.logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.blah.client.logger.ClientPayLoad;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServerStreamHandler implements Runnable {
	private String fileName;
	private volatile  InputStream clientIs;
	
	private static final String SERVER_LOG_DIR = "C:\\serverlogs";
	private static final int MAX_LIMIT = Integer.MAX_VALUE;
	
	private volatile int offsetOfActualData;
	private volatile FileOutputStream fos;
	private ConcurrentHashMap<String,Integer> fileNameVsLastReadOffset= new ConcurrentHashMap<>();

	public ServerStreamHandler(Socket socket) {
		try {
			ClientPayLoad payload = extractPayloadMetaData(socket);
			fileName = payload.getFileName();
			
			File file = new File(SERVER_LOG_DIR + File.separatorChar + fileName);
			
			if (!file.exists()) {
				file.createNewFile();
				fos= new FileOutputStream(file);
				
				passLastWriteOffset(socket);
			}

		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void passLastWriteOffset(Socket socket) throws IOException {
		DataOutputStream dos= new DataOutputStream(socket.getOutputStream());
		int offset= fileNameVsLastReadOffset.getOrDefault(fileName, offsetOfActualData);
		dos.writeInt(offset);
	}

	private ClientPayLoad extractPayloadMetaData(Socket socket)
			throws IOException, JsonParseException, JsonMappingException {
		clientIs= socket.getInputStream();
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		int jsonLength = dis.readInt();
		byte[] bytesToRead = new byte[jsonLength];
		offsetOfActualData=jsonLength;
		dis.read(bytesToRead, 0, jsonLength);
		String jsonAsString = new String(bytesToRead);
		ObjectMapper mapper = new ObjectMapper();

		ClientPayLoad payload = mapper.readValue(jsonAsString,
				ClientPayLoad.class);
		return payload;
	}

	@Override
	public void run()  {
		try {
			if(offsetOfActualData > MAX_LIMIT){
				fos.close();
				return;
			}
			byte[] bytesRead= new byte[1000];
			int length= clientIs.read(bytesRead, 0, bytesRead.length);
			if(length > -1){
				appendToFile(bytesRead,length);
				fileNameVsLastReadOffset.put(fileName, offsetOfActualData);
				offsetOfActualData+=length;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void appendToFile(byte[] bytesRead, int length) {
		try {
			fos.write(bytesRead, 0, length);
			fos.flush();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}

}
