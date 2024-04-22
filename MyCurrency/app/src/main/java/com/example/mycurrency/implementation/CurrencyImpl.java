package com.example.mycurrency.implementation;


import android.content.Context;

import com.example.mycurrency.model.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


public class CurrencyImpl implements Currency {
    Context context;
    String conversion_value;

    public CurrencyImpl(Context context) {
        this.context = context;
    }

    // Arrotondamento cifra
    public double round(double value, int precision) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(precision, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public List<String> getValues(String valueList, String regex) {

       String[] lines = valueList.split("[{},:]+");
       List<String> cleanedList = new ArrayList<String>(lines.length);

        for (String line : lines) {
            if (line.matches(regex)) {
                cleanedList.add(line);
            }
        }
        return cleanedList;
    }

    // Estrazione tassi di cambio e calcolo valore convertito
    @Override
    public String getConversionRate(String responseAPI, Double amount_to_convert) {
        String regexNumber = ".*\\d.*";
        List<String> ratesList = getValues(responseAPI, regexNumber);

        Double convert_from = Double.valueOf(ratesList.get(0));
        Double convert_to = Double.valueOf(ratesList.get(1));
        conversion_value = String.valueOf(round(((convert_to / convert_from) * amount_to_convert), 2));

        return conversion_value;
    }

    // Salvataggio valute preferite
    public void saveCurrency(String convert_from, String convert_to){
        StorageCurrencyImpl storageCurrency = new StorageCurrencyImpl(context, "Favorite_Currencies");
        storageCurrency.writeData(convert_from, convert_to);
    }

    // Recupero valute preferite
    public List<String> getSavedCurr(){
        String regexChar = ".*[A-Z].*";
        StorageCurrencyImpl storageCurrency = new StorageCurrencyImpl(context, "Favorite_Currencies");

        return getValues(String.valueOf(storageCurrency.readData()), regexChar);
    }
}



