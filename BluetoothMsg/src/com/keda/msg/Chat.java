package com.keda.msg;


public class Chat {
	
	public Chat(){
		ConnectionsProtocol protocol = new ConnectionsProtocol();
		
		new ServerThread(protocol).start();
		//new DiscoveryThread(protocol).start();
	}
	
	public static void main(String[] args) {
		new Chat();
	}
}
