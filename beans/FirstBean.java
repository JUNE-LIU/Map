package com.zhongqihong.beans;

public class FirstBean {
	private String resultcode;
	public String getResultcode() {
		return resultcode;
	}
	public void setResultcode(String resultcode) {
		this.resultcode = resultcode;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getError_code() {
		return error_code;
	}
	public void setError_code(String error_code) {
		this.error_code = error_code;
	}
	public SecondBean getResult() {
		return result;
	}
	public void setResult(SecondBean result) {
		this.result = result;
	}
	private String reason;
	private String error_code;
	private SecondBean result;
}
