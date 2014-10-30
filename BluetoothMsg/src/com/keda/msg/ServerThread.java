package com.keda.msg;

import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnectionNotifier;

public class ServerThread extends Thread {
	private ConnectionProtocol protocol;
	public ServerThread(ConnectionProtocol protocol){
		this.protocol = protocol;
	}
	@Override
	public void run() {
		try{
			LocalDevice device = LocalDevice.getLocalDevice();
			device.setDiscoverable(BluetoothSettings.DISCOVERY_MODE);
			String url = "btspp://localhost:"+BluetoothSettings.UUID+";name=ChatApp";
			
			System.out.println("Create server by url:"+url);
			StreamConnectionNotifier notifier = (StreamConnectionNotifier)Connector.open(url);
			while(true){
				System.out.println("waiting for connetion...");
				protocol.handleServerConnection(new WorkaroundStreamConnection(notifier.acceptAndOpen()));
			}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("ServerThread Exception:"+e.getMessage());
		}
	}
	
	
}
