package com.example.mycurrency.model;

public interface StorageCurrency {
    //Function that reads data stored on the device
    public String readData();
    //Function that writes data to the device
    public void writeData(String convert_from, String convert_to);
}
