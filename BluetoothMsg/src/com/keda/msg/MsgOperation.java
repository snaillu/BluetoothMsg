package com.keda.msg;

import com.keda.utils.DbHelper;

public class MsgOperation {
	private static MsgOperation m_instance = null;
	
	private MsgOperation(){}
	
	synchronized public static MsgOperation getInstance(){
		if(m_instance==null){
			m_instance = new MsgOperation();
		}
		
		return m_instance;
	}
	
	
	public static void handleMsg(String msg,String id){
		if(msg.isEmpty())
			return;
		
		System.out.println("current msg is: "+msg +" id="+id);
		boolean result = insertMsgInfo(msg,id);
		if(result){
			System.out.println("Kill the BluetoothMsg Server.");
			System.exit(0);
		}
	}

	/**
	 * 
	 * @param info 信息包格式为：GPS:longitude:latitude:address
	 * @return 保存信息是否成功
	 */
	public static boolean insertMsgInfo(String info,String id){
		if(!info.startsWith("GPS")){
			return false;
		}
		
		String[] locInfo = info.split(":");
		if(locInfo.length!=4){
			System.out.println("GPS params error can't insert.");
			return false;
		}
		String[] idInfo = id.split(":");
		if(idInfo.length!=2){
			System.out.println("id params error can't insert.");
			return false;
		}
		
		String sql = "update channelinfo set longitude='"+locInfo[1]+"',latitude='"+locInfo[2]+"',address='"+locInfo[3].trim()+"' where dvrid="+idInfo[0] +" and channelid="+idInfo[1];
		System.out.println("Sql="+sql);
		
		return DbHelper.execSql(sql);
	}
	
}
