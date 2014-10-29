package com.keda.test;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

public class ServicesSearch {
	static final UUID OBEX_FILE_TRANSFER = new UUID(0x1106);
	public static final Vector serviceFound = new Vector();
	
	public static void main(String[] args) throws InterruptedException, IOException {
		RemoteDeviceDiscovery.main(null);
		
		serviceFound.clear();
		
		UUID serviceUUID = OBEX_FILE_TRANSFER;
		if((args!=null) && args.length>0){
			serviceUUID = new UUID(args[0],false);
		}
		
		final Object serviceSearchCompletedEvent = new Object();
		
		DiscoveryListener listener = new DiscoveryListener(){

			@Override
			public void deviceDiscovered(RemoteDevice arg0, DeviceClass arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void inquiryCompleted(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void serviceSearchCompleted(int transID, int respCode) {
				// TODO Auto-generated method stub
				System.out.println("service search completed!");
				synchronized(serviceSearchCompletedEvent){
					serviceSearchCompletedEvent.notifyAll();
				}
			}

			@Override
			public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
				// TODO Auto-generated method stub
				for(int i=0;i<servRecord.length;i++){
					String url = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
					if(url==null){
						continue;
					}
					serviceFound.add(url);
					DataElement serviceName = servRecord[i].getAttributeValue(0x0100);
					if(serviceName!=null){
						System.out.println("service "+serviceName.getValue()+"  found "+url);
					}else{
						System.out.println("service found "+url);
					}
				}
			}};
			
			UUID[] searchUuidSet = new UUID[]{serviceUUID};
			int[] attrIDs = new int[]{
					0x0100
			};
			
			for(Enumeration en = RemoteDeviceDiscovery.devicesDiscovered.elements();en.hasMoreElements();){
				RemoteDevice btDevice = (RemoteDevice)en.nextElement();
				
				synchronized(serviceSearchCompletedEvent){
					System.out.println("search services on "+btDevice.getBluetoothAddress()+" "+btDevice.getFriendlyName(false));
					LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, searchUuidSet, btDevice, listener);
					serviceSearchCompletedEvent.wait();
				}
			}
	}
}
