package com.yang.interviewdemo.db;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/9/1.
 */
public class StoreInfo {

    static String TAG = "yang";
    private static MyDBHelper myDB;

    private int id = 0;
    private String StoreID; //店铺ID
    private String name; //店铺地址
    private String businessCenter; //所属商圈
    private String foodType; // 美食分类
    private String distance; //距离
    private String consume; // 人均消费
    private String totalScore; // 评价分数
    private int likeCount; // 点赞人数
    private String picUrl; //图片地址
    private String groupInfo; //团购信息

    public static final String TABLE_NAME = "StoreInfo";
    final static String[] KEYS = {"id", "StoreID", "name", "businessCenter", "foodType", "distance", "consume", "totalScore", "likeCount", "picUrl" ,"groupInfo"};

    final static String[] TYPE = {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT", "TEXT NOT NULL", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "INTEGER", "TEXT", "TEXT"};

    public static void initDBHelper(MyDBHelper myDB)
    {
        StoreInfo.myDB = myDB;
    }

    public static void closeDBHelper()
    {
        if(myDB != null){
            StoreInfo.myDB.close();
            StoreInfo.myDB = null;
        }
    }

    /**
     * 初始化
     * */
    public static StoreInfo newStoreInfo(String name, String StoreID)
    {
        StoreInfo Info = new StoreInfo();
        Info.id = 0;
        Info.StoreID = StoreID;
        Info.name = name;
        Info.save();
        return Info;
    }

    /**
     * 保存信息
     * */
    public void save()
    {
        ArrayList<String> a_keys = new ArrayList<String>();
        final int count = KEYS.length - 1;
        for(int i = 0; i < count; i++)
            a_keys.add(KEYS[i + 1]);
        Object[] values = {this.StoreID, this.name, this.businessCenter, this.foodType, this.distance, this.consume,
                this.totalScore, this.likeCount, this.picUrl, this.groupInfo};

        String[] keys = new String[values.length];
        a_keys.toArray(keys);
        if(this.id == 0)
            this.id = myDB.add(TABLE_NAME, keys, values);
        else
        {
            for(int i = 0; i < count; i++)
                myDB.alterByID(TABLE_NAME, id, keys[i], values[i]);
        }
    }

    /**
     * 删除全部
     * */
    public void delete() {
        if(myDB == null)
            return;
        if(id < 1)
        {
            Log.e(TAG, "id must larger than 0");
            return;
        }
        myDB.deleteById(TABLE_NAME, id);
    }

    public static void deleteAll(){
        if(myDB == null)
            return;
        myDB.deleteAll(TABLE_NAME);
    }

    /**
     * 删除此ID的数据
     * */
    public static void delete(int id) {
        if(myDB == null)
            return;
        if(id < 1)
        {
            Log.e(TAG, "id must larger than 0");
            return;
        }
        myDB.deleteById(TABLE_NAME, id);
    }

    /**
     * 根据名字获取数据
     * */
    public static synchronized StoreInfo get(String name) {
        // TODO Auto-generated method stub
        if(myDB == null)
            return null;
        StoreInfo Info = new StoreInfo();
        Cursor res = myDB.get(TABLE_NAME, name, KEYS);
        if(res.moveToFirst())
        {
            Info.id = res.getInt(0);
            Info.StoreID = res.getString(1);
            Info.name = res.getString(2);
            Info.businessCenter = res.getString(3);
            Info.foodType = res.getString(4);
            Info.distance = res.getString(5);
            Info.consume = res.getString(6);
            Info.totalScore = res.getString(7);
            Info.likeCount = res.getInt(8);
            Info.picUrl = res.getString(9);
            Info.groupInfo = res.getString(10);
        }else
        {
            Log.e(TAG, "no such deviceInfo, maybe id is too large.");
            Info = null;
        }
        res.close();
        return Info;
    }

    //根据id获取数据
    public static synchronized StoreInfo get(int id)
    {
        if(myDB == null)
            return null;
        StoreInfo Info = new StoreInfo();
        Cursor res = myDB.get(TABLE_NAME, id, KEYS);
        if(res.moveToFirst())
        {
            Info.id = res.getInt(0);
            Info.StoreID = res.getString(1);
            Info.name = res.getString(2);
            Info.businessCenter = res.getString(3);
            Info.foodType = res.getString(4);
            Info.distance = res.getString(5);
            Info.consume = res.getString(6);
            Info.totalScore = res.getString(7);
            Info.likeCount = res.getInt(8);
            Info.picUrl = res.getString(9);
            Info.groupInfo = res.getString(10);
        }else
        {
            Log.e(TAG, "no such deviceInfo, maybe id is too large.");
            Info = null;
        }
        res.close();
        return Info;
    }
    /**
     * 获得所有设备
     * */
    public static ArrayList<String> getAllName()
    {
        if(myDB == null)
            return null;
        ArrayList<String> names_array = new ArrayList<String>();

        Cursor cur = myDB.getAll(TABLE_NAME, new String[]{"name"});

        if(cur.moveToFirst())
            do {
                names_array.add(cur.getString(0));
            }while(cur.moveToNext());
        return names_array;
    }

    /**
     * 检查是否存在
     * */
    public static boolean checkIfExist(String name)
    {
        return myDB.checkIfExist(TABLE_NAME, new String[]{"name"}, new String[]{name});
    }

    /**
     * 获取所有信息
     * */
    public static ArrayList<HashMap<String, Object>> getAll()
    {
        if(myDB == null)
            return null;
        ArrayList<HashMap<String, Object>> res = new ArrayList<HashMap<String, Object>>();
        Cursor c = myDB.getAll(TABLE_NAME, KEYS);
        if(c.moveToFirst())
            do{
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("id", c.getInt(0));
                map.put("StoreID", c.getString(1));
                map.put("name", c.getString(2));
                map.put("businessCenter", c.getString(3));
                map.put("foodType", c.getString(4));
                map.put("distance", c.getString(5));
                map.put("consume", c.getString(6));
                map.put("totalScore", c.getString(7));
                map.put("likeCount", c.getInt(8));
                map.put("picUrl", c.getString(9));
                map.put("groupInfo", c.getString(10));
                res.add(map);
            }while(c.moveToNext());
        c.close();
        return res;
    }

    @Override
    public boolean equals(Object o) {
        return ((StoreInfo)o).name.equals(this.name);
    }

    public int getId() {
        return id;

    }
    public String getName() {
        return name;
    }

    public String getStoreID() {
        return StoreID;
    }

    public String getBusinessCenter() {
        return businessCenter;
    }

    public void setBusinessCenter(String businessCenter) {
        this.businessCenter = businessCenter;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getConsume() {
        return consume;
    }

    public void setConsume(String consume) {
        this.consume = consume;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(String groupInfo) {
        this.groupInfo = groupInfo;
    }
}
