package com.zhongqihong.mymap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.zhongqihong.beans.*;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.google.gson.Gson;

public class PriceInfoActivity extends Activity implements OnClickListener {
	private MapView myMapView;
	private BaiduMap myBaiduMap;
	public static final int num = 0;
	private Button bt1;
	private TextView tv;
	private String fff;
	private String ffff;
	private String fffff;
	private String eee;
	private String name;
	private String address;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case num:
				String response = (String) msg.obj;
				Gson gson = new Gson();
				 FirstBean firstbean = gson.fromJson(response,
						FirstBean.class);
				 
				 SecondBean secondbean=firstbean.getResult();
				 
				 List<thirdBean> list1=secondbean.getData();
				 
				 for (int i = 0; i < list1.size(); i++) {
					 ffff=list1.get(i).getLat();
					 fffff=list1.get(i).getLon();
					 
					 
					 tv.append(fffff);
					 tv.append(ffff);
					 tv.append(list1.get(i).getName());
					 tv.append(list1.get(i).getAddress());
					 
					 List<gastprice> list2=(List<gastprice>)list1.get(i).getGastprice();
					 for (int j = 0; j < list2.size(); j++) {
						 fff=list2.get(j).getPrice();
						 tv.append(fff+"\n");
					}
					
				}
				 
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.priceinfo);
		bt1 = (Button) findViewById(R.id.button1);
		tv = (TextView) findViewById(R.id.textView1);
		bt1.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.button1) {
			sendRequest();
		}
	}

	private void sendRequest() {
		// TODO Auto-generated method stub
		// 开启线程来发起网络请求
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection connection = null;
				try {
					String apiAddress = "http://apis.juhe.cn/oil/local?"
							+ "key=8be5535b85c72d0f8866fb93573fe9ed"
							+ "&lon=116.403119" + "&lat=39.916042"
							+ "&format=2" + "&r=5000";
					URL url = new URL(apiAddress);

					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(5 * 1000);
					connection.setReadTimeout(5 * 1000);

					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;

					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					Message msg = new Message();
					msg.obj = response.toString();
					msg.what = num;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}
}