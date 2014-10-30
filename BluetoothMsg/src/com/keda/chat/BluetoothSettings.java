package com.keda.chat;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

public interface BluetoothSettings {

	/**
	 * A constant UUID for Serial Port Profile.
	 */
	static public final UUID UUID = new UUID("1101", true);

	/**
	 * A constant discovery mode. In global mode no paring is needed.
	 */
	static public final int DISCOVERY_MODE = DiscoveryAgent.GIAC;

	/**
	 * @see javax.bluetooth.ServiceRecord#getConnectionURL(int requiredSecurity,
	 *      boolean mustBeMaster)
	 */
	public static final int SECURITY_OPTIONS = ServiceRecord.NOAUTHENTICATE_NOENCRYPT;

	/**
	 * @see javax.bluetooth.ServiceRecord#getConnectionURL(int requiredSecurity,
	 *      boolean mustBeMaster)
	 */
	public static final boolean MUST_BE_MASTER = false;

	/**
	 * A workaround for a bug in bluecove. BlueCove may crash if getFriendlyName
	 * method is used.
	 * 
	 * @see javax.bluetooth.RemoteDevice#getFriendlyName(boolean alwaysAsk)
	 */
	public static final boolean WORKAROUND_BLUECOVE = true;

	/**
	 * A constant number to search services from remote device. Services are
	 * searched couple of times from a device if service search returned with
	 * search error.
	 * 
	 * @see javax.bluetooth.DiscoveryListener#serviceSearchCompleted(int
	 *      transID, int respCode)
	 * @see javax.bluetooth.DiscoveryListener#SERVICE_SEARCH_ERROR
	 */
	public static final int MAX_TRY_COUNT_TO_SEARCH_SERVICE = 2;

	/**
	 * A constant number which tells how many seconds are slept between
	 * discoveries in discovery thread.
	 */
	public static final int SLEEP_TIME_BEFORE_NEW_DISCOVERY = 20;

}
