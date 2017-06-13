package com.zhongqihong.beans;

import java.io.Serializable;
import java.util.List;
//加上点击覆盖物的事件使得InfoWindow和底部的图文信息显示出来，
//原来底部图文信息是不显示的，当点击该覆盖物后，就会得到从上一步传来的一个Bundle对象，
//该Bundle对象实际上包括的就是当前被点击的覆盖物的Bean中所存的信息，
//然后可以通过Bean信息来更新底部图文信息UI布局（类似于ListView中的GetView方法），并给该布局设置是可见的。
//但是当我点击地图其他部分就必须隐藏该底部的图文信息，所以还得给该整个地图设置点击事件，当点击地图后，图文信息再次被隐藏。
//还有一个InfoWindow，InfoWindow就是指向当前覆盖物的介绍信息。
//所以它也需要Bean中信息，所以它也得获取Bundle对象，然后设置信息。当点击地图时它也需要被再次隐藏。
import java.io.Serializable;  

public class Info implements Serializable{  
    private double latitude;  
    private double longitude;  
    private int ImageId;  
    private String name;  
    private String distance;  
    private int zanNum;  
    public Info(double latitude, double longitude, int imageId,  
            String name, String distance, int zanNum) {  
        super();  
        this.latitude = latitude;  
        this.longitude = longitude;  
        ImageId = imageId;  
        this.name = name;  
        this.distance = distance;  
        this.zanNum = zanNum;  
    }  
    public Info() {  
        super();  
    }  
    public double getLatitude() {  
        return latitude;  
    }  
    public void setLatitude(double latitude) {  
        this.latitude = latitude;  
    }  
    public double getLongitude() {  
        return longitude;  
    }  
    public void setLongitude(double longitude) {  
        this.longitude = longitude;  
    }  
    public int getImageId() {  
        return ImageId;  
    }  
    public void setImageId(int imageId) {  
        ImageId = imageId;  
    }  
    public String getName() {  
        return name;  
    }  
    public void setName(String name) {  
        this.name = name;  
    }  
    public String getDistance() {  
        return distance;  
    }  
    public void setDistance(String distance) {  
        this.distance = distance;  
    }  
    public int getZanNum() {  
        return zanNum;  
    }  
    public void setZanNum(int zanNum) {  
        this.zanNum = zanNum;  
    }  
  
}
