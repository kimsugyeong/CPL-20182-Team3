package com.example.jolteon.lorafarm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    static String serverIP = "220.122.183.15";
    String operation = "";
    LinearLayout buttonLayout;
    TextView mostRecent;
    GradientTextView temp_text;

    TextView hum_text;
    Button buttonSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerApp();

        mostRecent = (TextView) findViewById(R.id.most_recent);
        temp_text = (GradientTextView) findViewById(R.id.temp_text);
        hum_text = (TextView) findViewById(R.id.hum_text);
        buttonLayout = (LinearLayout) findViewById(R.id.button_layout);
        buttonSelected = (Button) findViewById(R.id.button_t5);
        buttonSelected.setSelected(true);

        setRecentDate();

        findViewById(R.id.calendar_btn).setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), DataHistory.class);
                        startActivity(intent);
                    }
                }
        );

    }

    @Override
    protected void onResume() {
        super.onResume();

        setRecentDate();
    }

    public void setRecentDate() {

        String url = makeUrl("getRecentDate");

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jarray = new JSONArray(response);

                            for (int i = 0; i < jarray.length(); i++) {
                                JSONObject jObject = jarray.getJSONObject(i);
                                String date = jObject.getString("date");
                                int temperature = jObject.getInt("temperature");
                                int humidity = jObject.getInt("humidity");

                                mostRecent.setText(date);

                                temp_text.setText(Integer.toString(temperature) + "˚C");

                                hum_text.setText(Integer.toString(humidity) + " %");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                return params;
            }
        };

        request.setShouldCache(false);
        Volley.newRequestQueue(this).add(request);

    }

    public void registerApp() {
        String url = makeUrl("register");
        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();


        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                String mobile = null;
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager.getLine1Number() != null) {
                    mobile = telephonyManager.getLine1Number();
                }

                Map<String, String> params = new HashMap<>();
                params.put("mobile", mobile);
                params.put("registrationId", refreshedToken);

                return params;
            }
        };

        request.setShouldCache(false);
        Volley.newRequestQueue(this).add(request);
    }

    public void controlButtonClicked(View v) {

        String url = makeUrl("controlArduino");

        operation = buttonSelected.getText().toString().replaceAll("\\D", "");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("op", operation);

                return params;
            }
        };

        request.setShouldCache(false);
        Volley.newRequestQueue(this).add(request);

        Toast.makeText(this, operation + "초 동안 밸브가 열립니다.", Toast.LENGTH_SHORT).show();
    }

    public void timeButtonClicked(View v) {

        for (int i = 0; i < buttonLayout.getChildCount(); i++) {
            buttonLayout.getChildAt(i).setSelected(false);
        }

        v.setSelected(true);
        buttonSelected = (Button) v;
    }

    public static String makeUrl(String str) {
        return "http://" + serverIP + ":3000/" + str;
    }

}
