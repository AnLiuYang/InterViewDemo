package com.yang.interviewdemo.data;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/31.
 */
public class StoreData implements Serializable{

    private static final long serialVersionUID = 1L;

    int id;
    String ID;
    String name;
    String businessCenter;
    String foodType;
    String distance;
    String consume;
    float totalScore;
    int likeCount;
    String picUrl;
    String groupInfo;

    public int getid (){
        return id;
    }

    public void setid(int id) {
        this.id = id;
    }

    public String getId() {
        return ID;
    }

    public void setId(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Float getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(float totalScore) {
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
