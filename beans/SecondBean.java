package com.zhongqihong.beans;

import java.util.List;

public class SecondBean {
	private List<thirdBean> data;
	public List<thirdBean> getData() {
		return data;
	}
	public void setData(List<thirdBean> data) {
		this.data = data;
	}
	public thirdBean_2 getPageinfo() {
		return pageinfo;
	}
	public void setPageinfo(thirdBean_2 pageinfo) {
		this.pageinfo = pageinfo;
	}
	private thirdBean_2 pageinfo;
	
}
