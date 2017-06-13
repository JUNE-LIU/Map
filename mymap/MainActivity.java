package com.zhongqihong.mymap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.zhongqihong.beans.FirstBean;
import com.zhongqihong.beans.Info;
import com.zhongqihong.beans.PriceInfo;
import com.zhongqihong.beans.SearchInfo;
import com.zhongqihong.beans.SecondBean;
import com.zhongqihong.beans.thirdBean;
import com.zhongqihong.sensor.MyOrientationListener;
import com.zhongqihong.sensor.MyOrientationListener.onOrientationListener;
import com.zhongqihong.tools.HttpUtils;

public class MainActivity extends Activity implements OnClickListener,
		OnMarkerClickListener, OnMapClickListener {
	public static final int num = 0;
	private MapView myMapView;
	private BaiduMap myBaiduMap;
	private float current;// 放大或缩小的比例系数
	// 修改默认View相关
	private View defaultBaiduMapScaleButton = null;
	private View defaultBaiduMapLogo = null;
	private View defaultBaiduMapScaleUnit = null;

	private String[] types = { "普通地图", "卫星地图", "热力地图(已关闭)" };
	private ImageView mapRoad, selectMapType, mapPanorama, map_gotogether;
	private ImageView addScale, lowScale;
	private ImageView myLoaction;
	private ImageView selectLocationMode;
	private TextView locationText;
	// 定位相关
	private LocationClient myLocationClient;
	private MyLocationListener myListener;
	private String[] LocationModeString = { "罗盘模式", "普通模式", "跟随模式",
			"3D俯视模式(已关闭)" };
	private boolean isFirstIn = true;
	double latitude, longtitude;// 经纬度
	private String locationTextString;// 定义的位置的信息
	private BitmapDescriptor myBitmapLocation;
	private MyOrientationListener myOrientationListener;
	private float myCurrentX;
	// 添加覆盖物控件
	private FirstBean firstbean;
	private SecondBean secondbean;
	private List<thirdBean> list1;
	private ImageView addMarkers;
	private BitmapDescriptor myMark;
	private BitmapDescriptor myMark2;
	private LinearLayout markLayout;
	private LinearLayout markLayout2;
	private List<Info> marks;
	/** 加油站覆盖物控件 **/
	private List<PriceInfo> maks2;
	private boolean flag = true;

	private EditText searchEdit;
	private ImageView okToSearch;
	private List<SearchInfo> searchInfoLists;

	private String uid;

	private ImageButton startGo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// setTranslucentStatus();
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		initMapView();
		changeDefaultBaiduMapView();
		initMapLocation();
		initSearchDestination();
	}

	// 搜索网络请求API得到的JSON数据
	/**
	 * 
	 * 利用子线程请求中得到的网络数据，利用Handler来更新 主线程(即UI线程)
	 * */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0x1234:
				JSONObject object = (JSONObject) msg.obj;
				// toast("json:----->"+object.toString());
				// 解析开始:然后把每一个地点信息封装到SearchInfo类中
				try {
					JSONArray array = object.getJSONArray("results");
					for (int i = 0; i < array.length(); i++) {
						JSONObject joObject = array.getJSONObject(i);
						String name = joObject.getString("name");
						JSONObject object2 = joObject.getJSONObject("location");
						double lat = object2.getDouble("lat");
						double lng = object2.getDouble("lng");
						String address = joObject.getString("address");
						String streetIds = joObject.getString("street_id");
						String uids = joObject.getString("uid");
						SearchInfo mInfo = new SearchInfo(name, lat, lng,
								address, streetIds, uids);
						searchInfoLists.add(mInfo);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				displayInDialog();
				break;
			case num:
				String response = (String) msg.obj;
				Gson gson = new Gson();
				firstbean = gson.fromJson(response, FirstBean.class);

				secondbean = firstbean.getResult();

				list1 = secondbean.getData();
				initMapMarks2();
				addOverLayer2();

				break;
			}

		}

	};

	private void initMapMarks2() {
		// TODO Auto-generated method stub
		maks2 = new ArrayList<PriceInfo>();
		for (int i = 0; i < list1.size(); i++) {
			String price_92 = null;
			String price_95 = null;
			String price_97 = null;

			price_97 = list1.get(i).getPrice().get(2).getPrice();

			int gastpricesize = list1.get(i).getGastprice().size();
			if (gastpricesize == 0) {
				price_92 = null;
				price_95 = null;

			} else if (gastpricesize == 1) {
				if (list1.get(i).getGastprice().get(0).equals("92#")) {
					price_92 = list1.get(i).getGastprice().get(0).getPrice();
					price_95 = null;

				} else if (list1.get(i).getGastprice().get(0).equals("95#")) {
					price_92 = null;
					price_95 = list1.get(i).getGastprice().get(0).getPrice();
				}
			} else if (gastpricesize == 2) {
				if (list1.get(i).getGastprice().get(0).equals("92#")
						&& list1.get(i).getGastprice().get(1).equals("95#")) {
					price_92 = list1.get(i).getGastprice().get(0).getPrice();
					price_95 = list1.get(i).getGastprice().get(1).getPrice();
				}else {
					price_92 = list1.get(i).getGastprice().get(0).getPrice();
					price_95 = list1.get(i).getGastprice().get(1).getPrice();
				}
			}

			/**
			 * if (list1.get(i).getGastprice().size()<2) { price_95 = null; }
			 * else { price_95 = list1.get(i).getGastprice().get(1).getPrice();
			 * } if (list1.get(i).getPrice().contains("E97")) { price_97 =
			 * list1.get(i).getPrice().get(3).getPrice(); } else { price_97 =
			 * null; }
			 **/

			maks2.add(new PriceInfo(list1.get(i).getLat(), list1.get(i)
					.getLon(), price_92, price_95, price_97, list1.get(i)
					.getName(), list1.get(i).getAddress()));
		}
		myBaiduMap.setOnMarkerClickListener(this);
		myBaiduMap.setOnMapClickListener(this);
	}

	/**
	 * 
	 * 显示搜索后信息的自定义列表项对话框，以及对话框点击事件的处理
	 * */
	private void displayInDialog() {
		if (searchInfoLists != null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
			builder.setIcon(R.drawable.calibration_arrow)
					.setTitle("请选择你查询到的地点")
					.setAdapter(new myDialogListAdapter(),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									final SearchInfo mInfos = searchInfoLists
											.get(which);
									uid = mInfos.getUid();
									myBaiduMap
											.setOnMarkerClickListener(new OnMarkerClickListener() {
												@Override
												public boolean onMarkerClick(
														Marker PnoramaMark) {
													Intent intent = new Intent(
															MainActivity.this,
															OtherPnoramaActivity.class);
													intent.putExtra("info",
															mInfos);
													startActivity(intent);
													return true;
												}
											});
									addPnoramaLayout(mInfos);//
								}
							}).show();
		} else {
			toast("未查询到相关地点");
		}
	}

	/**
	 * 
	 * 添加全景覆盖物，即全景的图标，迅速定位到该地点在地图上的位置
	 * */
	public void addPnoramaLayout(SearchInfo mInfos) {
		myBaiduMap.clear();
		LatLng latLng = new LatLng(mInfos.getLatitude(), mInfos.getLongtiude());
		Marker pnoramaMarker = null;
		OverlayOptions options;
		BitmapDescriptor mPnoramaIcon = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_card_streetscape_blue);
		options = new MarkerOptions().position(latLng).icon(mPnoramaIcon)
				.zIndex(6);
		pnoramaMarker = (Marker) myBaiduMap.addOverlay(options);
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
		myBaiduMap.animateMapStatus(msu);
	}

	private void initSearchDestination() {
		searchEdit = (EditText) findViewById(R.id.search_panorama);
		okToSearch = (ImageView) findViewById(R.id.ok_to_search);
		okToSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchInfoLists = new ArrayList<SearchInfo>();
				getSearchDataFromNetWork();
			}

			/**
			 * 
			 * 根据输入搜索的信息，从网络获得的JSON数据 开启一个线程去获取网络数据 getSearchDataFromNetWork
			 * */
			private void getSearchDataFromNetWork() {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							JSONObject jsonObject = HttpUtils.send(searchEdit
									.getText().toString(), null);
							Message msg = new Message();
							msg.obj = jsonObject;
							msg.what = 0x1234;
							handler.sendMessage(msg);
						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
	}

	class myDialogListAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return searchInfoLists.size();
		}

		@Override
		public Object getItem(int position) {
			return getItem(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SearchInfo mSearchInfo = searchInfoLists.get(position);
			View view = View.inflate(MainActivity.this,
					R.layout.dialog_list_item, null);
			TextView desnameTv = (TextView) view.findViewById(R.id.desname);
			TextView addressTv = (TextView) view.findViewById(R.id.address);
			desnameTv.setText(mSearchInfo.getDesname());
			addressTv.setText(mSearchInfo.getAddress());
			return view;
		}

	}

	/**
	 * 
	 * 除去百度地图上的默认控件
	 * */
	private void changeDefaultBaiduMapView() {
		changeInitialzeScaleView();// 改变默认百度地图初始加载的地图比例
		// 设置隐藏缩放和扩大的百度地图的默认的比例按钮
		for (int i = 0; i < myMapView.getChildCount(); i++) {// 遍历百度地图中的所有子View,找到这个扩大和缩放的按钮控件View，然后设置隐藏View即可
			View child = myMapView.getChildAt(i);
			if (child instanceof ZoomControls) {
				defaultBaiduMapScaleButton = child;// 该defaultBaiduMapScaleButton子View是指百度地图默认产生的放大和缩小的按钮，得到这个View
				break;
			}
		}
		defaultBaiduMapScaleButton.setVisibility(View.GONE);// 然后将该View的Visiblity设为不存在和不可见，即隐藏
		defaultBaiduMapLogo = myMapView.getChildAt(1);// 该View是指百度地图中默认的百度地图的Logo,得到这个View
		defaultBaiduMapLogo.setPadding(300, -10, 100, 100);// 设置该默认Logo
															// View的位置，因为这个该View的位置会影响下面的刻度尺单位View显示的位置
		defaultBaiduMapScaleUnit = myMapView.getChildAt(2);// 得到百度地图的默认单位刻度的View  
		defaultBaiduMapScaleUnit.setPadding(100, 0, 115, 200);// 最后设置调整百度地图的默认单位刻度View的位置
	}

	/**
	 * 修改百度地图默认开始初始化加载地图比例大小
	 * */
	private void changeInitialzeScaleView() {
		myBaiduMap = myMapView.getMap();// 改变百度地图的放大比例,让首次加载地图就开始扩大到500米的距离
		MapStatusUpdate factory = MapStatusUpdateFactory.zoomTo(15.0f);
		myBaiduMap.animateMapStatus(factory);
	}

	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		myMapView.onDestroy();
	}

	@Override
	protected void onStart() {// 当Activity调用onStart方法，开启定位以及开启方向传感器，即将定位的服务、方向传感器和Activity生命周期绑定在一起
		myBaiduMap.setMyLocationEnabled(true);// 开启允许定位
		if (!myLocationClient.isStarted()) {
			myLocationClient.start();// 将定位与Activity生命周期进行绑定,开启定位
		}
		// 开启方向传感器
		myOrientationListener.start();
		super.onStart();
	}

	@Override
	protected void onStop() {// 当Activity调用onStop方法，关闭定位以及关闭方向传感器
		myBaiduMap.setMyLocationEnabled(false);// 将定位与Activity生命周期进行绑定,关闭定位
		myLocationClient.stop();// 关闭定位
		myOrientationListener.stop();// 关闭方向传感器
		super.onStop();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		myMapView.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		myMapView.onPause();
	}

	/**
	 * 初始化地图的View
	 * */
	private void initMapView() {
		registerAllViewsId();
		registerAllViewsEvent();
	}

	private void registerAllViewsId() {
		myMapView = (MapView) findViewById(R.id.mymap_view);
		mapRoad = (ImageView) findViewById(R.id.road_condition);
		selectMapType = (ImageView) findViewById(R.id.map_type);
		addScale = (ImageView) findViewById(R.id.add_scale);
		lowScale = (ImageView) findViewById(R.id.low_scale);
		myLoaction = (ImageView) findViewById(R.id.my_location);
		locationText = (TextView) findViewById(R.id.mylocation_text);
		selectLocationMode = (ImageView) findViewById(R.id.map_location);
		addMarkers = (ImageView) findViewById(R.id.map_marker);
		map_gotogether = (ImageView) findViewById(R.id.map_gotogether);
		markLayout = (LinearLayout) findViewById(R.id.mark_layout);
		markLayout2 = (LinearLayout) findViewById(R.id.mark_layout2);
		mapPanorama = (ImageView) findViewById(R.id.map_panorama);
		startGo = (ImageButton) findViewById(R.id.start_go);
	}

	private void registerAllViewsEvent() {
		map_gotogether.setOnClickListener(this);
		mapRoad.setOnClickListener(this);
		selectMapType.setOnClickListener(this);
		addScale.setOnClickListener(this);
		lowScale.setOnClickListener(this);
		myLoaction.setOnClickListener(this);
		selectLocationMode.setOnClickListener(this);
		addMarkers.setOnClickListener(this);
		mapPanorama.setOnClickListener(this);
		startGo.setOnClickListener(this);
	}

	/**
	 * 
	 * 初始化定位功能
	 * */
	private void initMapLocation() {
		myLocationClient = new LocationClient(this);// 创建一个定位客户端对象
		myListener = new MyLocationListener();// 创建一个定位事件监听对象
		myLocationClient.registerLocationListener(myListener);// 并给该定位客户端对象注册监听事件
		// 对LocaitonClient进行一些必要的设置
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");// 设置坐标的类型
		option.setIsNeedAddress(true);// 返回当前的位置信息，如果不设置为true，默认就为false，就无法获得位置的信息
		option.setOpenGps(true);// 打开GPS
		option.setScanSpan(1000);// 表示1s中进行一次定位请求
		myLocationClient.setLocOption(option);
		useLocationOrientationListener();// 调用方向传感器
	}

	/**
	 * 
	 * 定位结合方向传感器，从而可以实时监测到X轴坐标的变化，从而就可以检测到
	 * 定位图标方向变化，只需要将这个动态变化的X轴的坐标更新myCurrentX值， 最后在MyLocationData
	 * data.driection(myCurrentX);
	 * */
	private void useLocationOrientationListener() {
		myOrientationListener = new MyOrientationListener(MainActivity.this);
		myOrientationListener
				.setMyOrientationListener(new onOrientationListener() {
					@Override
					public void onOrientationChanged(float x) {// 监听方向的改变，方向改变时，需要得到地图上方向图标的位置
						myCurrentX = x;

					}
				});
	}

	/**
	 * 
	 * 获得最新定位的位置,并且地图的中心点设置为我的位置
	 * */
	private void getMyLatestLocation(double lat, double lng) {
		LatLng latLng = new LatLng(lat, lng);// 创建一个经纬度对象，需要传入当前的经度和纬度两个整型值参数
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);// 创建一个地图最新更新的状态对象，需要传入一个最新经纬度对象
		myBaiduMap.animateMapStatus(msu);// 表示使用动画的效果传入，通过传入一个地图更新状态对象，然后利用百度地图对象来展现和还原那个地图更新状态，即此时的地图显示就为你现在的位置
	}

	/**
	 * 
	 * 获取位置信息的客户端对象的监听器类MyLocationListener
	 * */
	private class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// 得到一个MyLocationData对象，需要将BDLocation对象转换成MyLocationData对象
			MyLocationData data = new MyLocationData.Builder()
					.accuracy(location.getRadius())// 精度半径
					.direction(myCurrentX)// myCurrentX就是通过方向传感器监听得到的值来设置定位的方向
					.latitude(location.getLatitude())// 经度
					.longitude(location.getLongitude())// 纬度
					.build();
			myBaiduMap.setMyLocationData(data);
			// 配置自定义的定位图标,需要在紧接着setMyLocationData后面设置
			// 调用自定义定位图标
			changeLocationIcon();
			latitude = location.getLatitude();// 得到当前的经度
			longtitude = location.getLongitude();// 得到当前的纬度
			// toast("经度："+latitude+"     纬度:"+longtitude);
			if (isFirstIn) {// 表示用户第一次打开，就定位到用户当前位置，即此时只要将地图的中心点设置为用户此时的位置即可

				getMyLatestLocation(latitude, longtitude);// 获得最新定位的位置,并且地图的中心点设置为我的位置
				isFirstIn = false;// 表示第一次才会去定位到中心点
				locationTextString = "" + location.getAddrStr();// 这里得到地址必须需要在设置LocationOption的时候需要设置isNeedAddress为true;
				toast(locationTextString);
				locationText.setText(locationTextString);
			}
		}
	}

	/**
	 * 
	 * 自定义定位图标
	 * */
	private void changeLocationIcon() {

		myBitmapLocation = BitmapDescriptorFactory
				.fromResource(R.drawable.arrow);// 引入自己的图标
		if (isFirstIn) {// 表示第一次定位显示普通模式
			MyLocationConfiguration config = new MyLocationConfiguration(
					LocationMode.NORMAL, true, myBitmapLocation);
			myBaiduMap.setMyLocationConfigeration(config);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		/**
		 * 
		 * 是否打开实时交通
		 * */
		case R.id.road_condition:
			if (myBaiduMap.isTrafficEnabled()) {
				// 如果是开着的状态，当点击后，就会出关闭状态
				myBaiduMap.setTrafficEnabled(false);
				mapRoad.setImageResource(R.drawable.main_icon_roadcondition_off);
			} else {
				// 如果是关闭的状态，当点击后，就会处于开启的状态
				myBaiduMap.setTrafficEnabled(true);
				mapRoad.setImageResource(R.drawable.main_icon_roadcondition_on);
			}
			break;
		/**
		 * 
		 * 选择地图的类型
		 * */
		case R.id.map_type:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.icon).setTitle("请选择地图的类型")
					.setItems(types, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String select = types[which];
							if (select.equals("普通地图")) {
								myBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
							} else if (select.equals("卫星地图")) {
								myBaiduMap
										.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
							} else if (select.equals("热力地图(已关闭)")
									|| select.equals("热力地图(已打开)")) {
								if (myBaiduMap.isBaiduHeatMapEnabled()) {
									myBaiduMap.setBaiduHeatMapEnabled(false);
									Toast.makeText(MainActivity.this,
											"热力地图已关闭", 0).show();
									types[which] = "热力地图(已关闭)";
								} else {
									myBaiduMap.setBaiduHeatMapEnabled(true);
									Toast.makeText(MainActivity.this,
											"热力地图已打开", 0).show();
									types[which] = "热力地图(已打开)";
								}
							}
						}
					}).show();
			break;
		/** 附近加油站油价查询 **/
		case R.id.map_gotogether:
			sendRequest();
			// Intent intent = new Intent(MainActivity.this,
			// PriceInfoActivity.class);
			// startActivity(intent);
			break;
		case R.id.add_scale:
			/**
			 * 放大地图的比例
			 * */
			current += 0.5f;
			MapStatusUpdate factory = MapStatusUpdateFactory
					.zoomTo(15.0f + current);
			myBaiduMap.animateMapStatus(factory);
			break;
		case R.id.low_scale:
			/**
			 * 缩小地图的比例
			 * */
			current -= 0.5f;
			MapStatusUpdate factory2 = MapStatusUpdateFactory
					.zoomTo(15.0f + current);
			myBaiduMap.animateMapStatus(factory2);
			break;
		case R.id.my_location:
			// BDLocationListener
			getMyLatestLocation(latitude, longtitude);
			break;
		case R.id.map_location:
			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
			builder2.setIcon(R.drawable.track_collect_running)
					.setTitle("请选择定位的模式")
					.setItems(LocationModeString,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String mode = LocationModeString[which];
									if (mode.equals("罗盘模式")) {
										MyLocationConfiguration config = new MyLocationConfiguration(
												LocationMode.COMPASS, true,
												myBitmapLocation);
										myBaiduMap
												.setMyLocationConfigeration(config);
									} else if (mode.equals("跟随模式")) {
										MyLocationConfiguration config = new MyLocationConfiguration(
												LocationMode.FOLLOWING, true,
												myBitmapLocation);
										myBaiduMap
												.setMyLocationConfigeration(config);
									} else if (mode.equals("普通模式")) {
										MyLocationConfiguration config = new MyLocationConfiguration(
												LocationMode.NORMAL, true,
												myBitmapLocation);
										myBaiduMap
												.setMyLocationConfigeration(config);
									} else if (mode.equals("3D俯视模式(已关闭)")
											|| mode.equals("3D俯视模式(已打开)")) {
										if (mode.equals("3D俯视模式(已打开)")) {
											UiSettings mUiSettings = myBaiduMap
													.getUiSettings();
											mUiSettings.setCompassEnabled(true);
											LocationModeString[which] = "3D俯视模式(已关闭)";
											toast("3D俯视模式(已关闭)");
										} else {
											MyLocationConfiguration config = new MyLocationConfiguration(
													LocationMode.COMPASS, true,
													myBitmapLocation);
											myBaiduMap
													.setMyLocationConfigeration(config);
											MyLocationConfiguration config2 = new MyLocationConfiguration(
													LocationMode.NORMAL, true,
													myBitmapLocation);
											myBaiduMap
													.setMyLocationConfigeration(config2);
											LocationModeString[which] = "3D俯视模式(已打开)";
											toast("3D俯视模式(已打开)");
										}
									}
								}
							}).show();
			break;
		case R.id.map_marker:
			initMapMarks();
			addOverLayer();
			break;
		case R.id.map_panorama:
			Intent intent5 = new Intent(MainActivity.this,
					PanoramaActivity.class);
			intent5.putExtra("panoramaLatLng", new double[] { latitude,
					longtitude });
			startActivity(intent5);
			break;
		case R.id.start_go:
			Intent intent2 = new Intent(MainActivity.this,
					NaViPathActivity.class);
			startActivity(intent2);
			break;
		default:
			break;
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
							+ "key=8be5535b85c72d0f8866fb93573fe9ed" + "&lon="
							+ longtitude + "&lat=" + latitude + "&format=2"
							+ "&r=3000";
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
					Message msg2 = new Message();
					msg2.obj = response.toString();
					msg2.what = num;
					handler.sendMessage(msg2);
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

	/**
	 * 
	 * 添加覆盖物
	 * */
	private void addOverLayer() {
		myBaiduMap.clear();// 先清除一下图层
		LatLng latLng = null;
		Marker marker = null;
		OverlayOptions options;
		myMark = BitmapDescriptorFactory.fromResource(R.drawable.mark);// 引入自定义的覆盖物图标，将其转化成一个BitmapDescriptor对象
		// 遍历MarkInfo的List一个MarkInfo就是一个Mark
		for (int i = 0; i < marks.size(); i++) {
			// 经纬度对象
			latLng = new LatLng(marks.get(i).getLatitude(), marks.get(i)
					.getLongitude());// 需要创建一个经纬对象，通过该对象就可以定位到处于地图上的某个具体点
			// 图标
			options = new MarkerOptions().position(latLng).icon(myMark)
					.zIndex(6);
			marker = (Marker) myBaiduMap.addOverlay(options);// 将覆盖物添加到地图上
			Bundle bundle = new Bundle();// 创建一个Bundle对象将每个mark具体信息传过去，当点击该覆盖物图标的时候就会显示该覆盖物的详细信息
			bundle.putSerializable("mark", marks.get(i));
			marker.setExtraInfo(bundle);
		}
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);// 通过这个经纬度对象，地图就可以定位到该点
		myBaiduMap.animateMapStatus(msu);
	}

	private void addOverLayer2() {

		myBaiduMap.clear();// 先清除一下图层
		LatLng latLng2 = null;
		Marker marker = null;
		OverlayOptions options;
		myMark = BitmapDescriptorFactory.fromResource(R.drawable.mark);// 引入自定义的覆盖物图标，将其转化成一个BitmapDescriptor对象
		// 遍历MarkInfo的List一个MarkInfo就是一个Mark
		for (int i = 0; i < maks2.size(); i++) {
			double d = Double.parseDouble(maks2.get(i).getLatitude());
			double e = Double.parseDouble(maks2.get(i).getLongitude());
			// 经纬度对象
			latLng2 = new LatLng(d, e);// 需要创建一个经纬对象，通过该对象就可以定位到处于地图上的某个具体点
			// 图标
			options = new MarkerOptions().position(latLng2).icon(myMark)
					.zIndex(6);
			marker = (Marker) myBaiduMap.addOverlay(options);// 将覆盖物添加到地图上
			Bundle bundle = new Bundle();// 创建一个Bundle对象将每个mark具体信息传过去，当点击该覆盖物图标的时候就会显示该覆盖物的详细信息
			bundle.putSerializable("maks2", maks2.get(i));
			marker.setExtraInfo(bundle);
		}
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng2);// 通过这个经纬度对象，地图就可以定位到该点
		myBaiduMap.animateMapStatus(msu);
	}

	/**
	 * 
	 * 初始化覆盖物信息数据
	 * */
	private void initMapMarks() {
		marks = new ArrayList<Info>();
		marks.add(new Info(39.056745, 121.786368, R.drawable.pic1,
				"汉庭全季酒店大连开发区店停车场", "距离209米", 1888));
		marks.add(new Info(39.055450, 121.778912, R.drawable.pic2,
				"大连开发区商场停车场", "距离459米", 388));
		marks.add(new Info(39.061368, 121.784625, R.drawable.pic3,
				"大连开发区管委会停车场", "距离5米", 888));
		marks.add(new Info(39.047429, 121.776837, R.drawable.pic4,
				"大连经济技术开发区医院停车场", "距离10米", 188));
		myBaiduMap.setOnMarkerClickListener(this);
		myBaiduMap.setOnMapClickListener(this);
	}

	public void toast(String str) {
		Toast.makeText(MainActivity.this, str, 0).show();
	}

	/**
	 * 
	 * 覆盖物的点击事件
	 * */
	@Override
	public boolean onMarkerClick(Marker marker) {

		Bundle bundle = marker.getExtraInfo();

		if (bundle.containsKey("mark")) {
			Info MyMarker = (Info) bundle.getSerializable("mark");

			ImageView iv = (ImageView) markLayout.findViewById(R.id.mark_image);
			TextView distanceTv = (TextView) markLayout
					.findViewById(R.id.distance);
			TextView nameTv = (TextView) markLayout.findViewById(R.id.name);
			TextView zanNumsTv = (TextView) markLayout
					.findViewById(R.id.zan_nums);
			iv.setImageResource(MyMarker.getImageId());
			distanceTv.setText(MyMarker.getDistance() + "");
			nameTv.setText(MyMarker.getName());
			zanNumsTv.setText(MyMarker.getZanNum() + "");
			// 初始化一个InfoWindow
			initInfoWindow(MyMarker, marker);

			markLayout.setVisibility(View.VISIBLE);

		} else if (bundle.containsKey("maks2")) {
			PriceInfo mmMarker = (PriceInfo) bundle.getSerializable("maks2");

			TextView tv2 = (TextView) markLayout2.findViewById(R.id.oilname2);
			TextView tv3 = (TextView) markLayout2
					.findViewById(R.id.oiladdress2);
			TextView tv4 = (TextView) markLayout2.findViewById(R.id.oil92_2);
			TextView tv5 = (TextView) markLayout2.findViewById(R.id.oil95_2);

			tv2.setText(mmMarker.getName());
			tv3.setText(mmMarker.getAddress());
			tv4.setText(mmMarker.getPrice_92());
			tv5.setText(mmMarker.getPrice_95());
			initInfoWindow2(mmMarker, marker);
			markLayout2.setVisibility(View.VISIBLE);

		}
		return true;

	}

	private void initInfoWindow2(PriceInfo mmMarker, Marker marker) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		InfoWindow infoWindow;
		// InfoWindow中显示的View内容样式，显示一个TextView
		TextView infoWindowTv = new TextView(MainActivity.this);
		infoWindowTv.setBackgroundResource(R.drawable.location_tips);
		infoWindowTv.setPadding(30, 20, 30, 50);
		infoWindowTv.setText(mmMarker.getName());
		infoWindowTv.setTextColor(Color.parseColor("#FFFFFF"));

		final LatLng latLng = marker.getPosition();
		Point p = myBaiduMap.getProjection().toScreenLocation(latLng);// 将地图上的经纬度转换成屏幕中实际的点
		p.y -= 47;// 设置屏幕中点的Y轴坐标的偏移量
		LatLng ll = myBaiduMap.getProjection().fromScreenLocation(p);// 把修改后的屏幕的点有转换成地图上的经纬度对象
		/**
		 * 
		 * 实例化一个InfoWindow的对象 public InfoWindow(View view,LatLng position, int
		 * yOffset)通过传入的 view 构造一个 InfoWindow,
		 * 此时只是利用该view生成一个Bitmap绘制在地图中，监听事件由开发者实现。 参数: view - InfoWindow 展示的
		 * view position - InfoWindow 显示的地理位置 yOffset - InfoWindow Y 轴偏移量
		 * */
		infoWindow = new InfoWindow(infoWindowTv, ll, 10);
		myBaiduMap.showInfoWindow(infoWindow);// 显示InfoWindow
	}

	/**
	 * 初始化出一个InfoWindow
	 * 
	 * */
	private void initInfoWindow(Info myMarker, Marker marker) {
		// TODO Auto-generated method stub
		InfoWindow infoWindow;
		// InfoWindow中显示的View内容样式，显示一个TextView
		TextView infoWindowTv = new TextView(MainActivity.this);
		infoWindowTv.setBackgroundResource(R.drawable.location_tips);
		infoWindowTv.setPadding(30, 20, 30, 50);
		infoWindowTv.setText(myMarker.getName());
		infoWindowTv.setTextColor(Color.parseColor("#FFFFFF"));

		final LatLng latLng = marker.getPosition();
		Point p = myBaiduMap.getProjection().toScreenLocation(latLng);// 将地图上的经纬度转换成屏幕中实际的点
		p.y -= 47;// 设置屏幕中点的Y轴坐标的偏移量
		LatLng ll = myBaiduMap.getProjection().fromScreenLocation(p);// 把修改后的屏幕的点有转换成地图上的经纬度对象
		/**
		 * 
		 * 实例化一个InfoWindow的对象 public InfoWindow(View view,LatLng position, int
		 * yOffset)通过传入的 view 构造一个 InfoWindow,
		 * 此时只是利用该view生成一个Bitmap绘制在地图中，监听事件由开发者实现。 参数: view - InfoWindow 展示的
		 * view position - InfoWindow 显示的地理位置 yOffset - InfoWindow Y 轴偏移量
		 * */
		infoWindow = new InfoWindow(infoWindowTv, ll, 10);
		myBaiduMap.showInfoWindow(infoWindow);// 显示InfoWindow
	}

	/**
	 * 给整个地图添加的点击事件
	 * */
	@Override
	public void onMapClick(LatLng arg0) {// 表示点击地图其他的地方使得覆盖物的详情介绍的布局隐藏，
		// 但是点击已显示的覆盖物详情布局上，则不会消失，因为在详情布局上添加了Clickable=true

		// 由于事件的传播机制，因为点击事件首先会在覆盖物布局的父布局(map)中,由于map是可以点击的，map则会把点击事件给消费掉，
		// 如果加上Clickable=true表示点击事件由详情布局自己处理，不由map来消费
		markLayout.setVisibility(View.GONE);
		markLayout2.setVisibility(View.GONE);
		myBaiduMap.hideInfoWindow();// 隐藏InfoWindow
	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
