package com.keda.msg;

import com.keda.chat.ConnectionProtocol;

public class ConsoleUI implements ConnectionProtocol.EventHandler {

	private ConnectionProtocol protocol;
	
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
