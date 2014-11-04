package com.keda.msg;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;

public class ConnectionsProtocol {
	class RemoteMsg{
		Connection conn;
		DataInputStream in;
		
		public RemoteMsg(Connection conn,DataInputStream in){
			this.conn = conn;
			this.in = in;
		}
		
		void startMessageReadingThread(){
			new Thread(){
				public void run(){
					while(true){
						try{
							MsgOperation operation = MsgOperation.getInstance();
							operation.handleMsg(readString(in));
						}catch(IOException e){
							close();
							break;
						}
					}
				}
			}.start();
		}
		
		void close(){
			try{
				conn.close();
			}catch(IOException e){
			}
		}
	}
	
	public void handleServerConnection(final StreamConnection connection){
		new Thread(){
			public void run(){
				DataInputStream in;
				try{
					in = connection.openDataInputStream();
					handleConnection(connection, in);
				}catch(Exception e){
					System.out.println("handleServerConnection exception:"+e.getMessage());
				}
			}
		}.start();
	}
	
	protected void handleConnection(StreamConnection connection,final DataInputStream in) throws IOException{
		RemoteMsg msg = new RemoteMsg(connection,in);
		msg.startMessageReadingThread();
	}
	
	protected void writeString(DataOutputStream out,String str) throws IOException{
		out.writeUTF(str);
		out.flush();
	}
	
	protected String readString(DataInputStream in) throws IOException{
		return in.readUTF();
	}
}
