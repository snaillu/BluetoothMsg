package com.keda.msg;

import java.util.Scanner;

public class ConsoleUI implements ConnectionProtocol.EventHandler {

	private ConnectionProtocol protocol;
	
	public ConsoleUI(){
		new Thread(){
			public void run(){
				while(true){
					try{
						Scanner sc = new Scanner(System.in);
						String input = sc.next();
						if(input.equalsIgnoreCase("exit")){
							break;
						}else{
							protocol.broadcastMessage(input);
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}.start();
	};
	
	public void addMessage(String userName,String msg){
		System.out.println("msg from user:"+userName +"  msg:"+msg);
	}
	@Override
	public void chatMessage(String userName, String msg) {
		// TODO Auto-generated method stub
		addMessage(userName,msg);
	}

	@Override
	public void chatLeave(String userName) {
		// TODO Auto-generated method stub
		addMessage(null,"The user "+userName+" has leaved.");
	}

	@Override
	public void chatEnter(String userName) {
		// TODO Auto-generated method stub
		addMessage(null,"The user "+userName+" has joined.");
	}

	@Override
	public void setProtocol(ConnectionProtocol protocol) {
		// TODO Auto-generated method stub
		this.protocol = protocol;
	}

}
