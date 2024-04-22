package com.example.mycurrency.implementation;

import android.content.Context;
import android.util.Log;

import com.example.mycurrency.model.StorageCurrency;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class StorageCurrencyImpl implements StorageCurrency {
    //Application context
    Context context;
    //url file position
    String fileName, data;

    public StorageCurrencyImpl(Context context, String name){
        this.fileName = name;
        this.context = context;
    }

    // Lettura dati da file con valute salvate
    @Override
    public String readData() {
        data = readFromFile(context);
        return data;
    }

    // Scrittura valute preferite su file
    @Override
    public void writeData(String convert_from, String convert_to) {
        String filename = fileName;
        FileOutputStream outputStream;
        String currencies = convert_from+","+convert_to;

        try {
            //Open the output file
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
                //Write selected data
            outputStream.write(currencies.getBytes(StandardCharsets.UTF_8));
            outputStream.close();
        } catch (Throwable e) {
            Log.d("Write file", e.toString());
        }
    }

    // Lettura file con valute salvate
    private String readFromFile(Context context) {

        StringBuilder curr = new StringBuilder();
        //Read the file in blocks of 1024
        byte[] buffer = new byte[16];

        try {
            FileInputStream fis = context.openFileInput(fileName);
            int n;
            while( (n = fis.read(buffer)) != -1){
                curr.append(new String(buffer, 0, n));
                Log.d("Read file","Saved data: "+curr );
            }
        }
        catch (FileNotFoundException e) {
            //If the file was not found I create it
            Log.d("Error", "create a new one");
            File file = new File(context.getFilesDir(), fileName);
        }
        catch(Throwable emb){
            Log.d("Error", emb.toString());
        }

        return curr.toString();
    }
}

