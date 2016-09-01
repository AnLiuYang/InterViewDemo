package com.yang.interviewdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.model.Text;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.http.RequestParams;
import com.yang.interviewdemo.R;
import com.yang.interviewdemo.adapter.StoreAdapter;
import com.yang.interviewdemo.data.StoreData;
import com.yang.interviewdemo.db.MyDBHelper;
import com.yang.interviewdemo.db.StoreInfo;
import com.yang.interviewdemo.utils.DialogLoading;
import com.yang.interviewdemo.utils.MyHttpUtil;
import com.yang.interviewdemo.utils.SlideCutListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity implements LocationSource, AMapLocationListener, SlideCutListView.RemoveListener,View.OnClickListener {

    SlideCutListView list_store;
    TextView txt_getLocal;
    TextView txt_getNetwork;

    private final String STORE_URL = "http://www.alenc.xyz/interface/GetStoreList.do";
    public static final String sp_name = "SP_STORE";
    double latitude = 0;//纬度
    double longitude = 0;//经度
    String address = ""; //地理位置
    int totalPages = 1; //总页数
    int curPage = 0; //当前页数
    boolean isMore = false; //是否加载更多
    ArrayList<StoreData> store_list = new ArrayList<StoreData>();
    MyDBHelper myDB;

    final int UPADAPTER = 0; //更新adapter数据

    private AMapLocationClient mLocationClient = null; //声明AMapLocationClient类对象，定位发起端
    public AMapLocationClientOption mLocationOption = null;  //声明mLocationOption对象，定位参数
    private OnLocationChangedListener mListener = null;  //声明mListener对象，定位监听器

    Context mContext;
    private StoreAdapter mStoreAdapter;
    DialogLoading mDialogLoading;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case UPADAPTER:
                    mStoreAdapter.upData(store_list);
                    mStoreAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mStoreAdapter = new StoreAdapter(mContext);
        mDialogLoading = new DialogLoading(mContext);

        if (myDB == null){
            myDB = new MyDBHelper(getApplicationContext(), "interview", null, 1);
            StoreInfo.initDBHelper(myDB);
        }
        initView();
        location();
        getLcal();
    }

    private void initView(){
        txt_getLocal = (TextView) findViewById(R.id.txt_clean_local);
        txt_getNetwork = (TextView) findViewById(R.id.txt_get_network);

        list_store = (SlideCutListView) findViewById(R.id.slideCutListView);
        list_store.setRemoveListener(this);

        list_store.setAdapter(mStoreAdapter);
        list_store.setOnScrollListener(mScroollListener);

        txt_getLocal.setOnClickListener(this);
        txt_getNetwork.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myDB != null){
            myDB.close();
        }
        StoreInfo.closeDBHelper();
        mLocationClient.stopLocation();//停止定位
        mLocationClient.onDestroy();//销毁定位客户端。
    }

    private AbsListView.OnScrollListener mScroollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && isMore) {
                if(curPage < totalPages - 1){
                    getData(curPage + 1);
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if(firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0){
                isMore = true;
            }else{
                isMore = false;
            }
        }
    };

    //get请求数据
    public void getData(int page){
        mDialogLoading.show();
        String url = STORE_URL + "?lon=" + longitude + "&lat=" + latitude + "&pageNo=" + page;
        MyHttpUtil.getHt(url, new MyHttpUtil.setNotifyResultListener() {
            @Override
            public void postData(String result) {
                onResult(result);
            }
        });
    }

    //post请求数据
    public void postData(int page){
        mDialogLoading.show();
        RequestParams params = new RequestParams();
        params.addBodyParameter("lon", longitude + "");
        params.addBodyParameter("lat", latitude + "");
        params.addBodyParameter("pageNo", page + "");
        MyHttpUtil.postHttp(STORE_URL, params, new MyHttpUtil.setNotifyResultListener() {
            @Override
            public void postData(String result) {
                onResult(result);
            }
        });
    }

    private void onResult(String result){
        mDialogLoading.dismiss();
        if(result.equals("")){
            Toast.makeText(mContext,"加载失败",Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            JSONArray json = new JSONArray(result);
            JSONObject jdata = json.getJSONObject(1);
            if(jdata.has("detailMsg")){
                JSONObject jdetai = jdata.getJSONObject("detailMsg");
                totalPages = jdetai.getInt("totalPages");
                curPage = jdetai.getInt("curPage");
                JSONArray jstore = jdetai.getJSONArray("storeList");
                Gson gson = new Gson();
                List<StoreData> newList = gson.fromJson(jstore.toString(), new TypeToken<List<StoreData>>(){}.getType());
                for (int i = 0; i < newList.size(); i++){
                    StoreData store = newList.get(i);
                    StoreInfo info = StoreInfo.newStoreInfo(store.getName(), store.getId());
                    info.setBusinessCenter(store.getBusinessCenter());
                    info.setFoodType(store.getFoodType());
                    info.setDistance(store.getDistance());
                    info.setConsume(store.getConsume());
                    info.setTotalScore(store.getTotalScore() + "");
                    info.setLikeCount(store.getLikeCount());
                    info.setPicUrl(store.getPicUrl());
                    info.setGroupInfo(store.getGroupInfo());
                    info.save();
                }
                store_list.addAll(newList);
                mHandler.sendEmptyMessage(UPADAPTER);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void location() {
        mLocationClient = new AMapLocationClient(getApplicationContext());//初始化定位
        mLocationClient.setLocationListener(this);//设置定位回调监听
        mLocationOption = new AMapLocationClientOption();//初始化定位参数
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setNeedAddress(true);//设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setOnceLocation(false); //设置是否只定位一次,默认为false
        mLocationOption.setWifiActiveScan(true); //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setMockEnable(false);//设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setInterval(2000); //设置定位间隔,单位毫秒,默认为2000ms
        mLocationClient.setLocationOption(mLocationOption); //给定位客户端对象设置定位参数
        mLocationClient.startLocation(); //启动定位
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    //激活定位
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    //停止定位
    @Override
    public void deactivate() {
        mListener = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                latitude = aMapLocation.getLatitude();//获取纬度
                longitude = aMapLocation.getLongitude();//获取经度
                aMapLocation.getAccuracy();//获取精度信息
                address = aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                Log.i("yang", "纬度：" + aMapLocation.getLatitude() + " ，经度："  + aMapLocation.getLongitude() + " ,address: " + address);

                if (latitude != 0 && longitude != 0){
                    mLocationClient.stopLocation();
                    savedAddress();
                } else {
                    mLocationClient.startLocation();
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
                Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    //保存地理位置到本地
    public void savedAddress(){
        SharedPreferences sp = getSharedPreferences(sp_name, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("address", address);
        editor.putString("latitude", latitude + "");
        editor.putString("longitude", longitude + "");
        editor.commit();
    }

    @Override
    public void removeItem(SlideCutListView.RemoveDirection direction, int position) {
        StoreData store = store_list.get(position);
        int id = store.getid();
        StoreInfo.delete(id);

        store_list.remove(position);
        mHandler.sendEmptyMessage(UPADAPTER);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.txt_clean_local){
            store_list.clear();
            StoreInfo.deleteAll();
            mHandler.sendEmptyMessage(UPADAPTER);
        }else if(v.getId() == R.id.txt_get_network){
            postData(curPage);
        }
    }

    public void getLcal(){
        ArrayList<HashMap<String, Object>> list = StoreInfo.getAll();
        for (int i = 0; i < list.size(); i++){
            HashMap<String, Object> map = list.get(i);
            StoreData store = new StoreData();
            store.setid((int) map.get("id"));
            store.setId((String) map.get("ID"));
            store.setName((String) map.get("name"));
            store.setBusinessCenter((String) map.get("businessCenter"));
            store.setFoodType((String) map.get("foodType"));
            store.setDistance((String) map.get("distance"));
            store.setConsume((String) map.get("consume"));
            store.setTotalScore(Float.valueOf((String) map.get("totalScore")));
            store.setLikeCount((int) map.get("likeCount"));
            store.setPicUrl((String) map.get("picUrl"));
            store.setGroupInfo((String) map.get("groupInfo"));
            store_list.add(store);
        }
        mHandler.sendEmptyMessage(UPADAPTER);
    }
}
