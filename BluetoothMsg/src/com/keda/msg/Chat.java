package com.keda.msg;

import com.keda.msg.ConnectionProtocol;
import com.keda.msg.DiscoveryThread;

public class Chat {
	
	public Chat(){
		ConsoleUI ui = new ConsoleUI();
		ConnectionProtocol protocol = new ConnectionProtocol(ui);
		
		protocol.setUserName(System.getProperty("user.name", "anonymous"));
		
		//new ServerThread(protocol).start();
		new DiscoveryThread(protocol).start();
	}
	
	public static void main(String[] args) {
		new Chat();
	}
}
