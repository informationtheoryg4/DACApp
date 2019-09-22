package com.example.progettoit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SettingsActivity extends AppCompatActivity {
    ActionBar actionBar;
    Switch wiFiSwitch, lteSwitch;
    RadioButton concatButton, convolButton, deflateButton, lz4Button;
    WifiManager wifiManager;
    ConnectivityManager connectivityManager;
    private boolean wiFi, lte, concat, deflate, convol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF3F51B5")));
        //wiFi = getIntent().getBooleanExtra("wiFi", wiFi);  --> potrebbe servire
        lte = getIntent().getBooleanExtra("lte", lte);  //da gestire ancora se 4g già attivo
        deflate = getIntent().getBooleanExtra("deflate", deflate);
        concat = getIntent().getBooleanExtra("concat", concat);
        convol = getIntent().getBooleanExtra("convol", convol);
        wifiManager  = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService((Context.CONNECTIVITY_SERVICE));

        wiFi = wifiManager.isWifiEnabled();
        lte = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        //wiFi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

        wiFiSwitch = (Switch) findViewById(R.id.wi_fi);
        lteSwitch = (Switch) findViewById(R.id.lte);
        lteSwitch.setVisibility(View.INVISIBLE);
        concatButton = findViewById(R.id.concatenated_codes);
        convolButton = findViewById(R.id.convolutional_codes);
        deflateButton = findViewById(R.id.deflate);
        lz4Button = findViewById(R.id.lz4);
        wiFiSwitch.setChecked(wiFi);
        lteSwitch.setChecked(lte);
        deflateButton.setChecked(deflate);
        lz4Button.setChecked(!deflate);
        concatButton.setChecked(concat);
        convolButton.setChecked(convol);

        wiFiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                wiFi = isChecked;
                wifiManager.setWifiEnabled(isChecked);
                String toast = wiFi?"enabled":"disabled";
                Toast.makeText(getApplicationContext(), "Wi-Fi "+toast+".", Toast.LENGTH_SHORT).show();
            }

        });

        lteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lte = isChecked;
                setMobileDataEnabled(getBaseContext(), lte);
                String toast = lte?"enabled":"disabled";
                Toast.makeText(getApplicationContext(), "4G "+toast+".", Toast.LENGTH_SHORT).show();
            }

        });

        deflateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(deflateButton.isChecked()) {
                    deflateButton.setChecked(true);
                    deflate = true;
                    lz4Button.setChecked(false);
                    Toast.makeText(getApplicationContext(), "Deflate selected.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        lz4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lz4Button.isChecked()) {
                    lz4Button.setChecked(true);
                    deflate = false;
                    deflateButton.setChecked(false);
                    Toast.makeText(getApplicationContext(), "LZ4 Selected.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        concatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!concat) {
                    concatButton.setChecked(true);
                    concat = true;
                    convol = false;
                    convolButton.setChecked(false);
                    Toast.makeText(getApplicationContext(), "Concatenated Coding selected.", Toast.LENGTH_SHORT).show();
                }else{
                    concatButton.setChecked(false);
                    concat = false;
                    Toast.makeText(getApplicationContext(), "Concatenated Coding unselected.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        convolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!convol) {
                    convolButton.setChecked(true);
                    convol = true;
                    concat = false;
                    concatButton.setChecked(false);
                    Toast.makeText(getApplicationContext(), "Convolutional Coding selected.", Toast.LENGTH_SHORT).show();
                }else{
                    convolButton.setChecked(false);
                    convol = false;
                    Toast.makeText(getApplicationContext(), "Convolutional Coding unselected.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent data = new Intent();
        data.putExtra("wiFi", wiFi);
        data.putExtra("lte", lte);
        data.putExtra("deflate", deflate);
        data.putExtra("concat", concat);
        data.putExtra("convol", convol);
        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        finish();
    }

    private void setMobileDataEnabled(Context context, boolean enabled) {
        final ConnectivityManager conman =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            final Class conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class iConnectivityManagerClass = Class.forName(
                    iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass
                    .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
