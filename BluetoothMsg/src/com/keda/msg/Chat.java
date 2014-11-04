package com.keda.msg;


public class Chat {
	
	public Chat(String id){
		ConnectionsProtocol protocol = new ConnectionsProtocol();
		
		new ServerThread(protocol).start();
		//new DiscoveryThread(protocol).start();
	}
	
	public static void main(String[] args) {
		if(args!=null && args.length>0){
			new Chat(args[0]);
		}else{
			System.out.println("params error.");
		}
		
	}
}
