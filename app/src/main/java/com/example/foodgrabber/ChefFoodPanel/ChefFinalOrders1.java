package com.example.foodgrabber.ChefFoodPanel;

public class ChefFinalOrders1 {

    private String Address,Latitude_Lagitude,GrandTotalPrice,MobileNumber,Name,RandomUID,Status;

    public ChefFinalOrders1(String address,String latitude_Lagitude, String grandTotalPrice, String mobileNumber, String name, String randomUID, String status) {
        Address = address;
        Latitude_Lagitude = latitude_Lagitude;
        GrandTotalPrice = grandTotalPrice;
        MobileNumber = mobileNumber;
        Name = name;
        RandomUID = randomUID;
        Status = status;
    }

    public ChefFinalOrders1()
    {

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
}
