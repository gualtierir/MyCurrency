package com.example.mycurrency.model;

public interface Currency {
    public String getConversionRate(String responseAPI, Double amount_to_convert);
}
