package com.keda.test;

import java.io.IOException;
import java.io.InputStream;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;
import javax.obex.ServerRequestHandler;
import javax.obex.SessionNotifier;

public class OBEXPutServer {
	static final String serverUUID = "11111111111111111111111111111123";
	
	public static void main(String[] args) throws IOException {
		LocalDevice.getLocalDevice().setDiscoverable(DiscoveryAgent.GIAC);
		SessionNotifier serviceConnection = (SessionNotifier)Connector.open("btgoep://localhost:"
				+serverUUID+";name=ObexExample");
		int count=0;
		while(count<2){
			RequestHandler handler = new RequestHandler();
			serviceConnection.acceptAndOpen(handler);
			System.out.println("Received OBEX connection "+(++count));
		}
	}
	
	private static class RequestHandler extends ServerRequestHandler{

		@Override
		public int onPut(Operation op) {
			try{
				HeaderSet hs = op.getReceivedHeaders();
				String name = (String)hs.getHeader(HeaderSet.NAME);
				if(name!=null){
					System.out.println("put name:"+name);
				}
				InputStream is = op.openInputStream();
				StringBuffer buf = new StringBuffer();
				int data;
				while((data=is.read())!=-1){
					buf.append((char)data);
				}
				System.out.println("got:"+buf.toString());
				op.close();
				return ResponseCodes.OBEX_HTTP_OK;
			}catch(IOException e){
				e.printStackTrace();
				return ResponseCodes.OBEX_HTTP_UNAVAILABLE;
			}
		}
		
	}
}
