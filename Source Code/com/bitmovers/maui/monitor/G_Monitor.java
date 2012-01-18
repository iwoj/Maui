package com.bitmovers.maui.monitor;

public class G_Monitor
{
	public static final Integer MONITOR_AVERAGERESPONSETIME = new Integer (1);
	public static final Integer MONITOR_AVERAGEAPPLICATIONTIME = new Integer (2);
	public static final Integer MONITOR_CONNECTIONCOUNT = new Integer (3);
	public static final Integer MONITOR_SESSIONCOUNT = new Integer (4);
	public static final Integer MONITOR_APPLICATIONCOUNT = new Integer (5);
	public static final Integer MONITOR_THREADCOUNT = new Integer (6);
	public static final Integer MONITOR_CUSTOMMESSAGE = new Integer (7);
	
	public static final int MONITOR_SESSIONCREATED = 1;
	public static final int MONITOR_SESSIONDELETED = 2;
	public static final int MONITOR_APPLICATIONADDED = 3;
	public static final int MONITOR_APPLICATIONREMOVED = 4;
	public static final int MONITOR_APPLICATIONACTIVATED = 5;
	public static final int MONITOR_APPLICATIONDEACTIVATED = 6;
	public static final int MONITOR_STATS = 7;
	public static final int MONITOR_ERROR = 8;
	public static final int MONITOR_DETAILS = 9;
	
	public static final int MONITOR_SESSIONS = 100;
	public static final int MONITOR_APPLICATIONS = 101;

	public static final int REQUEST_SESSIONS = 10000;
	public static final int REQUEST_APPLICATIONS = 10001;
	public static final int REQUEST_STATS = 10002;
	public static final int REQUEST_SD = 1003;
	public static final int REQUEST_AD = 1004;
	public static final int REQUEST_KS = 1005;
	public static final int REQUEST_KA = 1006;
	public static final int REQUEST_LOGLEVEL = 1007;
	public static final int REQUEST_PING = 1008;
	public static final int REQUEST_KILL = 1009;
	
	public static final String METHOD_SESSIONS = "requestSessions";
	public static final String METHOD_APPLICATIONS = "requestApplications";
	public static final String METHOD_STATS = "requestStats";
	public static final String METHOD_LOGLEVEL = "requestLogLevel";
	public static final String METHOD_SD = "requestSD";
	public static final String METHOD_AD = "requestAD";
	public static final String METHOD_KS = "requestKS";
	public static final String METHOD_KA = "requestKA";
	public static final String METHOD_KILL = "requestKill";
	
	public static final int MONITOR_LOGMESSAGE = 1000;
}
	
