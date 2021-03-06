package com.keda.msg;

import com.keda.utils.ServerKiller;


public class Chat {
	public Chat(String id){
		ConnectionsProtocol protocol = new ConnectionsProtocol(id);
		
		new ServerThread(protocol).start();
		new ServerKiller().start();
	}
	
	public static void main(String[] args) {
		if(args!=null && args.length>0){
			new Chat(args[0]);
		}else{
			System.out.println("params error.");
		}
		
	}
}
