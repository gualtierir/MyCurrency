package com.example.mycurrency.requestManager;

import androidx.lifecycle.MutableLiveData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequestManager {
    public static void scheduleHttpRequest(String url, MutableLiveData<String> httpResponseLiveData) {
        try {
            if(httpResponseLiveData != null){
                final Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            // Connessione
                            URL urlObj = new URL(url);
                            HttpURLConnection connection = (HttpURLConnection)urlObj.openConnection();
                            connection.connect();

                            // Lettura
                            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            String ratesList = reader.readLine();

                            // Aggiornamento
                            httpResponseLiveData.postValue(ratesList);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
