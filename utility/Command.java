package utility;

import java.io.Serializable;
public enum Command implements Serializable {
	/*************************************************************************
	 * Cette classe du package utility sert à uniformiser la gestion 
	 * des commandes sur le reseau par le serveur et ses clients
	 *************************************************************************/
	
	SUBMITNAME("SUBMITNAME", 1),
	UPDATEUSERS("UPDATEUSERS", 200),
	UPDATEUSER("UPDATEUSER", 201), 
	UPDATEUSERINFO("UPDATEUSERINFO", 202), 
	UPDATEUSERNAME("UPDATEUSERNAME", 203),
	MESSAGE("MESSAGE", 3),
	PASSWORD("PASSWORD", 4),
	BROADCOAST("BROADCOAST", 5),
	LOGIN("LOGIN", 6),
	NAMEACCEPTED("NAMEACCEPTED",7),
	UNKNOW("UNKNOW",0);

	private int code=0;
	private String type="UNKNOW";
	
	Command(String t, int i){
		this.type=t;
		this.code=i;
	}
	
	public int getcode() {
		return code;
	}
	
	public static Command getCommand(int i) {
		for(Command c:values()) {
			if(i==c.code)return c;
		}
	return UNKNOW;
	}
	
}