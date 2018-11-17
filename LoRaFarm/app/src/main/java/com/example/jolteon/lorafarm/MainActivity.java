package com.example.jolteon.lorafarm;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

    String serverIP="220.122.182.173";
    String operation="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerApp();
    }

    public void registerApp(){
        String url=makeUrl("register");
        final String refreshedToken= FirebaseInstanceId.getInstance().getToken();


        StringRequest request=new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        error.printStackTrace();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams(){
                String mobile=null;
                TelephonyManager telephonyManager=(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if(telephonyManager.getLine1Number()!=null){
                    mobile=telephonyManager.getLine1Number();
                }

                Map<String, String> params=new HashMap<>();
                params.put("mobile", mobile);
                params.put("registrationId", refreshedToken);

                return params;
            }
        };

        request.setShouldCache(false);
        Volley.newRequestQueue(this).add(request);
    }
    public void controlButtonClicked(View v){

        String url=makeUrl("controlArduino");


        StringRequest request=new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        error.printStackTrace();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params=new HashMap<>();
                params.put("op", operation);

                return params;
            }
        };

        request.setShouldCache(false);
        Volley.newRequestQueue(this).add(request);
    }

    public void mOnClick(View v){

        /*
        StringRequest request=new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        try{
                            JSONArray jarray=new JSONArray(response);

                            for(int i=0; i<jarray.length(); i++){
                                JSONObject jObject=jarray.getJSONObject(i);
                                String date=jObject.getString("date");
                                int temperature=jObject.getInt("temperature");
                                int humidity=jObject.getInt("humidity");

                            }

                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                },
        new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                error.printStackTrace();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params=new HashMap<>();

                return params;
            }
        };


        request.setShouldCache(false);
        Volley.newRequestQueue(this).add(request);
        */
    }

    public String makeUrl(String str){
        return "http://"+serverIP+":3000/"+str;
    }

}
