package com.keda.msg;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;

public class ConnectionProtocol {
	
	static public interface EventHandler{
		public void chatMessage(String userName,String msg);
		public void chatLeave(String userName);
		public void chatEnter(String userName);
		public void setProtocol(ConnectionProtocol protocol);
	}
	
	class RemoteChatter{
		Connection conn;
		DataOutputStream out;
		DataInputStream in;
		String userName;
		String btAddress;
		
		RemoteChatter(String btAddress,Connection conn,DataInputStream in,DataOutputStream out,String userName){
			this.btAddress = btAddress;
			this.conn = conn;
			this.in = in;
			this.out = out;
			this.userName = userName;
		}
		
		void startMessageReadingThread(){
			new Thread(){
				public void run(){
					ui.chatEnter(userName);
					while(true){
						try{
							ui.chatMessage(userName, readString(in));
						}catch(IOException e){
							ui.chatLeave(userName);
							close();
							break;
						}
					}
				}
			}.start();
		}
		
		void close(){
			synchronized(btAddress2RemoteChatters){
				System.out.println("Close chat connection.");
				try{
					conn.close();
				}catch(IOException e){
				}
				btAddress2RemoteChatters.remove(btAddress);
			}
		}
	}
	
	protected Hashtable btAddress2RemoteChatters = new Hashtable();
	protected EventHandler ui;
	protected String userName;
	
	public ConnectionProtocol(EventHandler ui){
		this.ui = ui;
		ui.setProtocol(this);
	}
	
	public void handleServerConnection(final StreamConnection connection){
		new Thread(){
			public void run(){
				DataInputStream in;
				try{
					in = connection.openDataInputStream();
					System.out.println("Read bt address.");
					String btAddress = readString(in);
					System.out.println("bt address is:"+btAddress);
					handleConnection(connection, btAddress, in, connection.openDataOutputStream());
				}catch(Exception e){
					System.out.println("handleServerConnection exception:"+e.getMessage());
				}
			}
		}.start();
	}
	
	public void handleClientConnection(StreamConnection connection,String btAddress){
		DataOutputStream out;
		try{
			out = connection.openDataOutputStream();
			System.out.println("Write bt address.");
			writeString(out,LocalDevice.getLocalDevice().getBluetoothAddress());
			handleConnection(connection, btAddress, connection.openDataInputStream(), out);
		}catch(Exception e){
			System.out.println("ConnectionProtocol.handleClientConnection Exception:"+e.getMessage());
			try{
				connection.close();
			}catch(IOException el){}
		}
	}
	
	protected void handleConnection(StreamConnection connection,String btAddress,DataInputStream in,DataOutputStream out) throws IOException{
		btAddress = btAddress.toLowerCase();
		synchronized(btAddress2RemoteChatters){
			if(!btAddress2RemoteChatters.containsKey(btAddress)){
				System.out.println("Write user name.");
				writeString(out,userName);
				String userName = readString(in);
				System.out.println("userName: "+userName);
				RemoteChatter chatConn = new RemoteChatter(btAddress, connection, in, out, userName);
				btAddress2RemoteChatters.put(btAddress, chatConn);
				System.out.println("Start message read thread.");
				chatConn.startMessageReadingThread();
			}else{
				connection.close();
			}
		}
	}
	
	//判断设备是否已经连接
	public boolean hasConnection(String bluetoothAddress){
		try{
			return btAddress2RemoteChatters.containsKey(bluetoothAddress.toLowerCase())||
					bluetoothAddress.equalsIgnoreCase(LocalDevice.getLocalDevice().getBluetoothAddress());
		}catch(BluetoothStateException e){
			e.printStackTrace();
			System.out.println("ConnectionProtocol hasConnection "+e.getMessage());
			return true;
		}
	}
	
	public void setUserName(String userName){
		this.userName = userName;
		ui.chatEnter(userName);
	}
	
	public void broadcastMessage(String msg){
		System.out.println("Broadcast message:"+msg);
		for(Enumeration e = btAddress2RemoteChatters.elements();e.hasMoreElements();){
			RemoteChatter chat = (RemoteChatter)e.nextElement();
			try{
				writeString(chat.out,msg);
			}catch(IOException ex){
				chat.close();
			}
		}
		ui.chatMessage(userName, msg);
	}
	
	protected void writeString(DataOutputStream out,String str) throws IOException{
		out.writeUTF(str);
		out.flush();
	}
	
	protected String readString(DataInputStream in) throws IOException{
		return in.readUTF();
	}
}
