package com.zhongqihong.beans;

import java.io.Serializable;

public class PriceInfo implements Serializable {

	private String latitude;
	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getPrice_92() {
		return price_92;
	}

	public void setPrice_92(String price_92) {
		this.price_92 = price_92;
	}

	public String getPrice_95() {
		return price_95;
	}

	public void setPrice_95(String price_95) {
		this.price_95 = price_95;
	}

	public String getPrice_97() {
		return price_97;
	}

	public void setPrice_97(String price_97) {
		this.price_97 = price_97;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	private String longitude;
	private String price_92;
	private String price_95;
	private String price_97;
	private String name;
	private String address;

	public PriceInfo(String string, String string2, String string3,
			String string4, String string5, String name, String address) {
		this.latitude = string;
		this.longitude = string2;
		this.price_92 = string3;
		this.price_95 = string4;
		this.price_97 = string5;
		this.name = name;
		this.address = address;

	}
}
