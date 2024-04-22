package com.example.mycurrency;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.mycurrency.fragment.DatePickerFragment;
import com.example.mycurrency.implementation.CurrencyImpl;
import com.example.mycurrency.requestManager.HttpRequestManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private MutableLiveData<String> httpResponseLiveData;
    private TextView convert_from_dropdown_menu, convert_to_dropdown_menu, conversion_rate, picked_date;
    private EditText amount_to_convert_value;
    private ArrayList<String> arraylist;
    private Dialog from_dialog, to_dialog;
    private String convert_from_value, convert_to_value, url, dateAPI;
    private final String[] currency = {"BGN", "BRL", "CAD", "CHF", "CNY", "CZK", "DKK", "EUR", "GBP", "HKD", "HRK", "HUF", "IDR", "ILS", "INR", "ISK", "JPY", "KRW", "MXN", "MYR", "NOK", "NZD", "PHP", "PLN", "RON", "RUB", "SEK", "SGD", "THB", "TRY", "USD", "ZAR"};
    private static final String BASE_URL = "https://api.freecurrencyapi.com/v1/latest?apikey=fca_live_Jlpw0XDBWdaGgc5nMmviVBlSqaMkSDDZWNl80RcH&currencies=";
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        handleDataManagement();
    }

    private void initUI() {
        // Inizializza gli elementi dell'interfaccia utente

        // Trova e assegna le view ai componenti UI
        convert_from_dropdown_menu = findViewById(R.id.convert_from_dropdown_menu);
        convert_to_dropdown_menu = findViewById(R.id.convert_to_dropdown_menu);
        arraylist = new ArrayList<>();
        Collections.addAll(arraylist, currency);
        conversion_rate = findViewById(R.id.conversion_rate);
        picked_date = findViewById(R.id.picked_date_text);

        // Trova i pulsanti nell'interfaccia utente
        Button date = findViewById(R.id.pick_date);
        Button conversion = findViewById(R.id.conversion);
        Button save = findViewById(R.id.save);
        Button set_saved = findViewById(R.id.set_saved);
        Button exit = findViewById(R.id.exit);

        builder = new AlertDialog.Builder(this);
        amount_to_convert_value = findViewById(R.id.edit_amount_to_convert_value);

        // Selezione della valuta di conversione "da"
        convert_from_dropdown_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from_dialog = new Dialog(MainActivity.this);
                from_dialog.setContentView(R.layout.from_spinner);
                Objects.requireNonNull(from_dialog.getWindow()).setLayout(650, 800);
                from_dialog.show();

                EditText edittext = from_dialog.findViewById(R.id.edit_text);
                ListView listview = from_dialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arraylist);
                listview.setAdapter(adapter);

                edittext.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        convert_from_dropdown_menu.setText(adapter.getItem(position));
                        from_dialog.dismiss();
                        convert_from_value = adapter.getItem(position);
                    }
                });
            }
        });

        // Selezione della valuta di conversione "a"
        convert_to_dropdown_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                to_dialog = new Dialog(MainActivity.this);
                to_dialog.setContentView(R.layout.to_spinner);
                Objects.requireNonNull(to_dialog.getWindow()).setLayout(650, 800);
                to_dialog.show();

                EditText edittext = to_dialog.findViewById(R.id.edit_text);
                ListView listview = to_dialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arraylist);
                listview.setAdapter(adapter);

                edittext.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        convert_to_dropdown_menu.setText(adapter.getItem(position));
                        to_dialog.dismiss();
                        convert_to_value = adapter.getItem(position);
                    }
                });
            }
        });

        // Selezione della data
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "pickDate");
            }
        });

        // Invio richiesta HTTP per convertire la valuta
        conversion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(picked_date.getText().toString().isEmpty()) {
                    url = BASE_URL+convert_from_value+"%2C"+convert_to_value;

                } else {
                    dateAPI = picked_date.getText().toString().replaceAll("/", "-") + "T09%3A58%3A31.818Z";
                    url = BASE_URL+convert_from_value+"%2C"+convert_to_value+"&date_from="+dateAPI+"&date_to="+dateAPI;
                }

                sendHttpRequest(url);
            }
        });

        // Salva preferito
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle("Attenzione")
                        .setMessage("Salvare le valute selezionate come preferite?")
                        .setCancelable(true)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                CurrencyImpl favoriteCurrency = new CurrencyImpl(getApplicationContext());
                                favoriteCurrency.saveCurrency(convert_from_value, convert_to_value);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
            }
        });

        // Carica il preferito
        set_saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrencyImpl favoriteCurrency = new CurrencyImpl(getApplicationContext());
                List<String> favoriteList = favoriteCurrency.getSavedCurr();

                convert_from_dropdown_menu.setText(favoriteList.get(0));
                convert_to_dropdown_menu.setText(favoriteList.get(1));
                convert_from_value = (String) convert_from_dropdown_menu.getText();
                convert_to_value = (String) convert_to_dropdown_menu.getText();
            }
        });

        // Chiusura applicazione
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle("Attenzione")
                        .setMessage("Vuoi chiudere l'applicazione?")
                        .setCancelable(true)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Close_MainActivity();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
            }
        });
    }

   private void handleDataManagement(){

        CurrencyImpl currencyManager = new CurrencyImpl(getApplicationContext());
        //Init Mutable Live Data
        httpResponseLiveData = new MutableLiveData<>();

        // Observer per gestire le risposte HTTP
        httpResponseLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String newHttpResponse) {
                Log.d(TAG, "New HTTP Response Received ! "+newHttpResponse);

                // Aggiorna la UI con il tasso di conversione
                Double amount_to_convert_value = Double.valueOf(MainActivity.this.amount_to_convert_value.getText().toString());
                conversion_rate.setText(currencyManager.getConversionRate(newHttpResponse, amount_to_convert_value));
            }
        });
    }

    private void sendHttpRequest(String url){
        // Invia una richiesta HTTP utilizzando il gestore delle richieste
        HttpRequestManager.scheduleHttpRequest(url, httpResponseLiveData);
    }

    public void Close_MainActivity() {
        // Chiude l'activity e termina l'applicazione
        MainActivity.this.finish();
        System.exit(0);
    }
}

