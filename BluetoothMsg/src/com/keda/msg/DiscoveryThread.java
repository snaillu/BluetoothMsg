package com.keda.msg;

import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.keda.msg.ConnectionProtocol;
import com.keda.msg.BluetoothSettings;

public class DiscoveryThread extends Thread implements DiscoveryListener {

	private Object waitMutex = new Object();
	private ConnectionProtocol protocol;
	private Vector foundDevices = new Vector();
	private RemoteDevice currentDevice;
	private boolean serviceSearchInError;
	
	public DiscoveryThread(ConnectionProtocol protocol) {
		this.protocol = protocol;
	}
	
	
	@Override
	public void run() {
		try{
			while(true){
				foundDevices.removeAllElements();
				DiscoveryAgent agent = LocalDevice.getLocalDevice().getDiscoveryAgent();
				
				synchronized(waitMutex){
					System.out.println("Start inquiry method to found devices.");
					agent.startInquiry(BluetoothSettings.DISCOVERY_MODE, this);
					waitMutex.wait();
				}
				
				UUID uuids[] = new UUID[]{BluetoothSettings.UUID};
				for(int i=0;i<foundDevices.size();i++){
					serviceSearchInError = false;
					for(int t=0;t<BluetoothSettings.MAX_TRY_COUNT_TO_SEARCH_SERVICE;t++){
						synchronized(waitMutex){
							currentDevice = (RemoteDevice)foundDevices.elementAt(i);
							System.out.println("Try to search The Serial Port Profile(SPP) service from "
									+deviceString(currentDevice));
							agent.searchServices(null, uuids, currentDevice, this);
							waitMutex.wait();
						}
						if(!serviceSearchInError)
							break;
						else
							Thread.sleep(2*1000);
					}
				}
				Thread.sleep(BluetoothSettings.SLEEP_TIME_BEFORE_NEW_DISCOVERY*1000);
			}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("DiscorveryThread-Exception");
		}
	}

	@Override
	public void deviceDiscovered(RemoteDevice dev, DeviceClass clazz) {
		System.out.println("Device found: "+deviceString(dev));
		if(!protocol.hasConnection(dev.getBluetoothAddress()))
			foundDevices.addElement(dev);
		
	}

	@Override
	public void inquiryCompleted(int arg0) {
		synchronized(waitMutex){
			waitMutex.notify();
		}
		
	}

	@Override
	public void serviceSearchCompleted(int transId, int respCode) {
		serviceSearchInError = false;
		String msg = null;
		switch(respCode){
		case SERVICE_SEARCH_COMPLETED:
			msg = "the service search completed normally";
			break;
		case SERVICE_SEARCH_TERMINATED:
			msg = "the service search request was cancelled by a call to DiscoveryAgent.cancelServiceSearch()";
			break;
		case SERVICE_SEARCH_ERROR:
			msg = "an error occurred while processing the request";
			serviceSearchInError = true;
			break;
		case SERVICE_SEARCH_NO_RECORDS:
			msg = "no records were found during the service search";
			break;
		case SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
			msg = "the device specified in the search request could not be reached or the local device could not establish a connection to the remote device";
			break;
		}
		System.out.println("Service search completed - "+msg);
		synchronized(waitMutex){
			waitMutex.notify();
		}
	}

	@Override
	public void servicesDiscovered(int transId, ServiceRecord[] records) {
		System.out.println("Service discovered.");
		for(int i=0;i<records.length;i++){
			if(records[i]==null)
				continue;
			String url = records[i].getConnectionURL(
					BluetoothSettings.SECURITY_OPTIONS, 
					BluetoothSettings.MUST_BE_MASTER);
			System.out.println("Connecting to url: "+url);
			
			try{
				StreamConnection stream = (StreamConnection)Connector.open(url);
				stream = new WorkaroundStreamConnection(stream);
				protocol.handleClientConnection(stream, currentDevice.getBluetoothAddress());
				break;
			}catch(IOException e){
				System.out.println("DiscoveryThread-Exception "+e.getMessage());
			}
		}
		
	}
	/**
	 * 获取远程设备的设备名称
	 * @param dev
	 * @return
	 */
	private String deviceString(RemoteDevice dev){
		String result = null;
		
		try{
			result = dev.getFriendlyName(false);
		}catch(IOException e){
			result = "none";
		}
		
		result +=" - " + dev.getBluetoothAddress();
		
		return result;
	}

}
