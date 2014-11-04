package com.keda.msg;

import com.keda.utils.DbHelper;

public class MsgOperation {
	private static MsgOperation m_instance = null;
	private static String id;
	
	private MsgOperation(){}
	
	synchronized public static MsgOperation getInstance(){
		if(m_instance==null){
			m_instance = new MsgOperation();
		}
		
		return m_instance;
	}
	
	
	public static void handleMsg(String msg){
		if(msg.isEmpty())
			return;
		
		System.out.println("current msg is: "+msg);
	}
	public static void setId(String idInfo){
		id = idInfo;
	}
	/**
	 * 
	 * @param info 信息包格式为：GPS:longitude:latitude:address
	 * @return 保存信息是否成功
	 */
	public static boolean insertMsgInfo(String info){
		if(!info.startsWith("GPS")){
			return false;
		}
		
		String[] locInfo = info.split(":");
		if(locInfo.length!=4){
			return false;
		}
		
		String sql = "update channelinfo set longitude='"+locInfo[1]+"',latitude='"+locInfo[2]+"',address='"+locInfo[3]+"' where id="+id;
		System.out.println("Sql="+sql);
		
		return DbHelper.execSql(sql);
	}
	
}
