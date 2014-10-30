package com.keda.msg;


public class ChatApplication  {

	/*-
	 * ===============================================
	 * Bluetooth Chat Application for Multiple Clients
	 * ===============================================
	 * 
	 * This example application is a chat application. The application
	 * forms a net of chatters via bluetooth connections. An bluetooth
	 * connection is formed to each chatter, i.e, no centralized server 
	 * exists. 
	 * 
	 * The application has two threads: client thread and server thread. 
	 * The client thread, or discovery thread to be specific, searches 
	 * for new devices. A service search is performed to each found 
	 * device. If the chat service is found a connection is constructed 
	 * to device. The server thread waits for new connections. Only one 
	 * connection is constructed to remote chatter, i.e., if chatter 
	 * A's discovery thread contacts to chatter B's server thread no 
	 * connection from B's discovery thread is made to A's server 
	 * thread. 
	 * 
	 * 
	 * Messaging Protocol
	 * ==================
	 * 
	 * The messaging protocol is simple. Every read and written string 
	 * is informative. String are encoded with 
	 *  java.io.DataOutputStream.writeUTF(java.lang.String) 
	 * and decoded with
	 *  java.io.DataInputStream.readUTF(). 
	 * 
	 * 
	 * Server Thread
	 * -------------
	 * 
	 * In server thread writing is done as follows. First written string 
	 * is chatter's user name. After this all written strings are 
	 * user's messages to other chatters.
	 * 
	 * The first read string is remote chatter's bluetooth address. 
	 * Second read string is the user name of the remote chatter. 
	 * The user name should be less than 8 chars. All other read 
	 * strings are chat messages from other chatters and should be 
	 * displayed in the user interface as messages from a specific 
	 * user.
	 * 
	 * 
	 * Client Thread
	 * -------------
	 * 
	 * In client thread first string to write is device's bluetooth address. 
	 * Then username shall be written. All writes after that are user's  
	 * chat messages.
	 * 
	 * First string to read is the username of the remote chatter. All other messages
	 * are normal chat messages from the user.
	 * 
	 * 
	 */

	public ChatApplication() {
		SwingUI ui = new SwingUI();
		ConnectionProtocol protocol = new ConnectionProtocol(ui);
		
		protocol.setUserName(System.getProperty("user.name", "anonymous"));
		
		new ServerThread(protocol).start();
		new DiscoveryThread(protocol).start();
	}


	public static void main(String[] args) {
		new ChatApplication();
		Log.log("leaving...");
	}
	
}
