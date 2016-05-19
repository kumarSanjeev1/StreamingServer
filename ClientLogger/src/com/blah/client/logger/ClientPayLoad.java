package com.blah.client.logger;

public class ClientPayLoad {

	private String fileName;
	private String serviceName;
	private String hostName;

	public ClientPayLoad() {

	}

	public ClientPayLoad(String fileName, String serviceName, String hostName) {
		this.fileName = fileName;
		this.serviceName = serviceName;
		this.hostName = hostName;
	}

	

	public String getFileName() {
		return fileName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getHostName() {
		return hostName;
	}

}
