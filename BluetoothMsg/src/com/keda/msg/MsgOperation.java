package com.keda.msg;

public class MsgOperation {
	private static MsgOperation m_instance = null;
	
	private MsgOperation(){}
	
	synchronized public static MsgOperation getInstance(){
		if(m_instance==null){
			m_instance = new MsgOperation();
		}
		
		return m_instance;
	}
	
	
	public static void handleMsg(String msg){
		if(msg.isEmpty())
			return;
		
		System.out.println("current msg is: "+msg);
	}
	
}
