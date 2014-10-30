package com.keda.msg;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

public interface BluetoothSetting {
	static public final UUID UUID = new UUID("1101", true);
	static public final int DISCOVERY_MODE = DiscoveryAgent.GIAC;
	public static final int SECURITY_OPTIONS = ServiceRecord.NOAUTHENTICATE_NOENCRYPT;
	public static final boolean MUST_BE_MASTER = false;
	public static final int MAX_TRY_COUNT_TO_SEARCH_SERVICE = 2;
	public static final int SLEEP_TIME_BEFORE_NEW_DISCOVERY = 20;
}
