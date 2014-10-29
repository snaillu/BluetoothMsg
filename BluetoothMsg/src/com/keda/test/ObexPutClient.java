package com.keda.test;

import java.io.IOException;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;

public class ObexPutClient {
	
	public static void main(String[] args) throws InterruptedException, IOException {
		String serverURL = null;
		if((args!=null) && (args.length>0)){
			serverURL = args[0];
		}
		
		if(serverURL==null){
			String[] searchArgs = null;
			ServicesSearch.main(searchArgs);
			if(ServicesSearch.serviceFound.size()==0){
				System.out.println("OBEX service not found");
				return;
			}
			serverURL = (String)ServicesSearch.serviceFound.elementAt(0);
		}
		
		System.out.println("Connectiong to "+serverURL);
		ClientSession clientSession = (ClientSession)Connector.open(serverURL);
		HeaderSet hsConnectReply = clientSession.connect(null);
		if(hsConnectReply.getResponseCode()!=ResponseCodes.OBEX_HTTP_OK){
			System.out.println("Failed to connect");
			return;
		}
		HeaderSet hsOperation = clientSession.createHeaderSet();
		hsOperation.setHeader(HeaderSet.NAME, "Hello.txt");
		hsOperation.setHeader(HeaderSet.TYPE, "text");
		
		Operation putOperation = clientSession.put(hsOperation);
		byte data[] = "Hello world!".getBytes("iso-8859-1");
		OutputStream os = putOperation.openOutputStream();
		os.write(data);
		os.close();
		putOperation.close();
		clientSession.disconnect(null);
		clientSession.close();
	}
}
