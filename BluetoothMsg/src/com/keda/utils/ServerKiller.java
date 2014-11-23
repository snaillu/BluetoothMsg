package com.keda.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerKiller extends Thread {

	private byte[] content;
	private byte[] header;
	private int port = 60169;
	
	@Override
	public void run() {
		try {
			ServerSocket server = new ServerSocket(port);
			while(true){
				Socket connection = null;
				
				connection = server.accept();
				//OutputStream out = new BufferedOutputStream(connection.getOutputStream());
				InputStream in = new BufferedInputStream(connection.getInputStream());
				StringBuffer request = new StringBuffer();
				while(true){
					int c=in.read();
					if(c=='\r' || c=='\n' || c==-1){
						break;
					}
					request.append((char)c);
				}
				if(request.toString().indexOf("HTTP/")!=-1){
					System.out.println("request="+request.toString());
					if(request.toString().indexOf("KillTheBluetoothMsgServer")>=0){
						System.out.println("kill the jvm.");
						this.content = "success".getBytes("UTF-8");
						//sendMsg(out);
						server.close();
						System.exit(0);
					}
				}else{
					System.out.println("Not the request of HTTP, will ignore.");
				}
			}
		} catch (IOException e) {
			System.out.println("Can't start the server. May be the port is used.");
			e.printStackTrace();
		}
	}
	
	private void sendMsg(OutputStream out){
		String headContent = "HTTP/1.0 302 FOUND\r\n"+
					"Content-length: "+this.content.length+"\r\n"+
					"Content-type: text/html";
		try {
			this.header = headContent.getBytes("UTF-8");
			out.write(this.header);
			out.write(this.content);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
