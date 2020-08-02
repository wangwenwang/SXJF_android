package com.kaidongyuan.app.kdydriver.bean.order;

public class User implements java.io.Serializable {
	private String USER_NAME;//用户名
	private String USER_TYPE;//用户类型 PARTY、
	private String USER_CODE;//暂时未用上？用户手机号码
	private String IDX;

	public String getUSER_NAME() {
		return USER_NAME;
	}

	public void setUSER_NAME(String USER_NAME) {
		this.USER_NAME = USER_NAME;
	}

	public String getUSER_TYPE() {
		return USER_TYPE;
	}

	public void setUSER_TYPE(String USER_TYPE) {
		this.USER_TYPE = USER_TYPE;
	}

	public String getUSER_CODE() {
		return USER_CODE;
	}

	public void setUSER_CODE(String USER_CODE) {
		this.USER_CODE = USER_CODE;
	}

	public String getIDX() {
		return IDX;
	}

	public void setIDX(String IDX) {
		this.IDX = IDX;
	}
}
