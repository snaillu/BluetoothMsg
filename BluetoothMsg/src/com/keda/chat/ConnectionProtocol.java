package com.keda.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;

/**
 * A class that handles connections to remote chatters and the protocol used in
 * messaging.
 * 
 */
public class ConnectionProtocol {

	/**
	 * An interface for UI event handler.
	 */
	static public interface EventHandler {
		public void chatMessage(String userName, String msg);

		public void chatLeave(String userName);

		public void chatEnter(String userName);

		public void setProtocol(ConnectionProtocol protocol);
	}

	class RemoteChatter {
		Connection conn;

		DataOutputStream out;

		DataInputStream in;

		String userName;

		String btAddress;

		RemoteChatter(String btAddress, Connection conn, DataInputStream in,
				DataOutputStream out, String uName) {
			this.btAddress = btAddress;
			this.conn = conn;
			this.in = in;
			this.out = out;
			this.userName = uName;
		}

		/**
		 * A method that starts the message reading thread which contains the
		 * main chat loop. When the main loop starts the remote chatter enters
		 * in chat. Messages are read from the connection while it's open. When
		 * the connection closes chatter leaves from the chat.
		 */
		void startMessageReadingThread() {
			new Thread() {
				public void run() {
					ui.chatEnter(userName);
					while (true) { // infinite loop to read messages.
						try {
							ui.chatMessage(userName, readString(in));
						} catch (IOException e) {
							ui.chatLeave(userName);
							close();
							break;
						}
					}
				}
			}.start();
		}

		void close() {
			synchronized (btAddress2remoteChatters) {
				Log.log("Close chat connection.");
				try {
					conn.close();
				} catch (IOException e) {
				}
				btAddress2remoteChatters.remove(btAddress);
			}
		}
	}

	protected Hashtable btAddress2remoteChatters = new Hashtable();

	protected EventHandler ui;

	protected String userName;

	public ConnectionProtocol(EventHandler ui) {
		this.ui = ui;
		ui.setProtocol(this);
	}

	public void handleServerConnection(final StreamConnection connection) {
		// A server connections shall be handled immediately. Hence, a new
		// thread is started to do the actual protocol handling.
		new Thread() {
			public void run() {
				DataInputStream in;
				try { // to read client's bluetooth address
					in = connection.openDataInputStream();
					Log.log("Read bt address.");
					String btAddress = readString(in);
					Log.log("bt addres is: " + btAddress);
					handleConnection(connection, btAddress, in, connection
							.openDataOutputStream());
				} catch (Exception e) {
					Log
							.log(
									"ConnectionProtocol.handleServerConnection-Exception",
									e);
				}
			}
		}.start();
	}

	public void handleClientConnection(StreamConnection connection,
			String btAddress) {
		DataOutputStream out;
		try { // to write my bluetooth address to server
			out = connection.openDataOutputStream();
			Log.log("Write bt address.");
			writeString(out, LocalDevice.getLocalDevice().getBluetoothAddress());

			handleConnection(connection, btAddress, connection
					.openDataInputStream(), out);
		} catch (Exception e) {
			Log.log("ConnectionProtocol.handleClientConnection-Exception", e);
			try {
				connection.close();
			} catch (IOException e1) {
			}
		}
	}

	protected void handleConnection(StreamConnection connection,
			String btAddress, DataInputStream in, DataOutputStream out)
			throws Exception {

		btAddress = btAddress.toLowerCase();

		// The synchronization is very important here. Otherwise there might be
		// two connection between devices A and B if both create a connection to
		// each other at the same time.
		synchronized (btAddress2remoteChatters) {
			if (!btAddress2remoteChatters.containsKey(btAddress)) {
				Log.log("Write user name.");
				writeString(out, userName);
				Log.log("Read user name.");
				String userName = readString(in);
				Log.log("userName: " + userName);
				RemoteChatter chatConn = new RemoteChatter(btAddress,
						connection, in, out, userName);
				btAddress2remoteChatters.put(btAddress, chatConn);
				Log.log("Start message read thread");
				chatConn.startMessageReadingThread();
			} else {
				connection.close();
			}
		}
	}

	/**
	 * @return true if connection is made already or the address is local
	 *         Bluetooth address.
	 */
	public boolean hasConnection(String bluetoothAddress) {
		try {
			return btAddress2remoteChatters.containsKey(bluetoothAddress
					.toLowerCase())
					|| bluetoothAddress.equalsIgnoreCase(LocalDevice
							.getLocalDevice().getBluetoothAddress());
		} catch (BluetoothStateException e) {
			Log.log("ConnectionProtocol.hasConnection()", e);
			return true;
		}
	}

	public void setUserName(String userName) {
		this.userName = userName;
		ui.chatEnter(userName);
	}

	/**
	 * Broadcasts given message to other chatters and then sends the message to
	 * user's own chat UI.
	 */
	public void broadcastMessage(String msg) {
		Log.log("Broadcast message: " + msg);
		for (Enumeration e = btAddress2remoteChatters.elements(); e
				.hasMoreElements();) {
			RemoteChatter chat = (RemoteChatter) e.nextElement();
			try {
				writeString(chat.out, msg);
			} catch (IOException e1) {
				chat.close();
			}
		}

		ui.chatMessage(userName, msg);
	}

	/*-
	 * 
	 *  ----  Util methods ----
	 */

	protected void writeString(DataOutputStream out, String str)
			throws IOException {
		out.writeUTF(str);
		out.flush();
	}

	protected String readString(DataInputStream in) throws IOException {
		return in.readUTF();
	}

}
