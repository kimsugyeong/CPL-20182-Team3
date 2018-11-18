package com.example.jolteon.lorafarm;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DataHistory extends AppCompatActivity {

    Button dateSelectBtn;
    TextView yearText;
    TextView dateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        yearText=(TextView)findViewById(R.id.year_text);

        dateSelectBtn = (Button) findViewById(R.id.date_select_btn);
        dateText=(TextView)findViewById(R.id.date_text);

        final Calendar calendar = Calendar.getInstance();
        final int mYear=calendar.get(Calendar.YEAR);
        final int mMonth=calendar.get(Calendar.MONTH);
        final int mDate=calendar.get(Calendar.DAY_OF_MONTH);

        refreshHistory(mYear, mMonth, mDate);

        yearText.setText(Integer.toString(mYear)+"년");
        dateText.setText(Integer.toString(mMonth+1)+"월 "+Integer.toString(mDate));

        dateSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(DataHistory.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        yearText.setText(Integer.toString(year)+"년");
                        dateText.setText(Integer.toString(month+1)+"월 " + Integer.toString(dayOfMonth)+"일");

                       refreshHistory(year, month, dayOfMonth);
                    }
                }, mYear, mMonth, mDate);

                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dialog.show();
            }
        });
    }

    public void refreshHistory(final int year, final int month, final int day) {

        Log.d("refreshHistory", "함수 호출");
        String url=MainActivity.makeUrl("getDataHistory");


        StringRequest request=new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams(){

                Map<String, String> params=new HashMap<>();
                params.put("year", Integer.toString(year));
                params.put("month", Integer.toString(month));
                params.put("day", Integer.toString(day));

                return params;
            }
        };

        request.setShouldCache(false);
        Volley.newRequestQueue(this).add(request);
    }
}
