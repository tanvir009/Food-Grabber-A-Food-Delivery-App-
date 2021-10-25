package com.example.foodgrabber.DeliveryFoodPanel;

public class DeliveryShipOrders1 {

    private String Address, Latitude_Lagitude, ChefId, ChefName, GrandTotalPrice, MobileNumber, Name, RandomUID, Status, UserId;

    public DeliveryShipOrders1(String address,String latitude_Lagitude, String chefId, String chefName, String grandTotalPrice, String mobileNumber, String name, String randomUID, String status, String userId) {
        Address = address;
        Latitude_Lagitude = latitude_Lagitude;
        ChefId = chefId;
        ChefName = chefName;
        GrandTotalPrice = grandTotalPrice;
        MobileNumber = mobileNumber;
        Name = name;
        RandomUID = randomUID;
        Status = status;
        UserId = userId;
    }

    public DeliveryShipOrders1() {

    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getLatitude_Lagitude() {
        return Latitude_Lagitude;
    }

    public void setLatitude_Lagitude(String latitude_Lagitude) {Latitude_Lagitude= latitude_Lagitude ;}

    public String getChefId() {
        return ChefId;
    }

    public void setChefId(String chefId) {
        ChefId = chefId;
    }

    public String getChefName() {
        return ChefName;
    }

    public void setChefName(String chefName) {
        ChefName = chefName;
    }

    public String getGrandTotalPrice() {
        return GrandTotalPrice;
    }

    public void setGrandTotalPrice(String grandTotalPrice) {
        GrandTotalPrice = grandTotalPrice;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getRandomUID() {
        return RandomUID;
    }

    public void setRandomUID(String randomUID) {
        RandomUID = randomUID;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}
