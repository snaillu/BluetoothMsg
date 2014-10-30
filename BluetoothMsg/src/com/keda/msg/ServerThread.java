package com.keda.msg;

import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnectionNotifier;

/**
 * A class that acts as a server to wait for new Bluetooth connections.
 */
public class ServerThread extends Thread {

	private ConnectionProtocol protocol;

	public ServerThread(ConnectionProtocol protocol) {
		this.protocol = protocol;
	}

	public void run() {
		try {
			LocalDevice device = LocalDevice.getLocalDevice();
			device.setDiscoverable(BluetoothSettings.DISCOVERY_MODE);
			String url = "btspp://localhost:" + BluetoothSettings.UUID
					+ ";name=ChatApplication";

			Log.log("Create server by uri: " + url);
			StreamConnectionNotifier notifier = (StreamConnectionNotifier) Connector
					.open(url);

			while (true) { // infinite loop to accept connections.
				Log.log("Waiting for connection...");
				protocol.handleServerConnection(new WorkaroundStreamConnection(notifier.acceptAndOpen()));
			}
		} catch (Exception e) {
			Log.log("ServerThread-Exception", e);
		}
	}

}
