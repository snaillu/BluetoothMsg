package com.keda.test;

import java.util.Vector;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

public class RemoteDeviceDiscovery {
	public static final Vector devicesDiscovered = new Vector();
	
	public static void main(String[] args) throws BluetoothStateException, InterruptedException {
		final Object inquiryComplementEvent = new Object();
		devicesDiscovered.clear();
		
		DiscoveryListener listener = new DiscoveryListener(){
			@Override
			public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
				System.out.println("Device "+remoteDevice.getBluetoothAddress() +" found");
				devicesDiscovered.addElement(remoteDevice);
				try{
					System.out.println("   Name "+remoteDevice.getFriendlyName(false));
				}catch(Exception e){
					System.out.println("Exception: getFriendlyName()");
				}
			}

			@Override
			public void inquiryCompleted(int arg0) {
				System.out.println("Device Inquiry completed!");
				synchronized(inquiryComplementEvent){
					inquiryComplementEvent.notifyAll();
				}
			}

			@Override
			public void serviceSearchCompleted(int arg0, int arg1) {
			}

			@Override
			public void servicesDiscovered(int arg0, ServiceRecord[] arg1) {
			}
		};
		
		synchronized(inquiryComplementEvent){
			boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
			if(started){
				System.out.println("wait for device inquiry to complete...");
				inquiryComplementEvent.wait();
				System.out.println(devicesDiscovered.size()+"  device(s) found");
			}
		}
	}
}
