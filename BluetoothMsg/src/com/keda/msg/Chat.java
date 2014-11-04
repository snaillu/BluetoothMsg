package com.keda.msg;

import com.keda.msg.ConnectionProtocol;
import com.keda.msg.DiscoveryThread;

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
