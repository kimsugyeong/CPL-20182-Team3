package com.example.jolteon.lorafarm;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DataHistory extends AppCompatActivity {

    Button dateSelectBtn;
    TextView yearText;
    TextView dateText;
    TableLayout tableLayout;

    int mYear;
    int mMonth;
    int mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        yearText = (TextView) findViewById(R.id.year_text);
        dateSelectBtn = (Button) findViewById(R.id.date_select_btn);
        dateText = (TextView) findViewById(R.id.date_text);
        tableLayout = (TableLayout) findViewById(R.id.table_layout);

        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        refreshHistory(mYear, mMonth + 1, mDay);

        yearText.setText(Integer.toString(mYear) + "년");
        dateText.setText(Integer.toString(mMonth + 1) + "월 " + Integer.toString(mDay)+"일");

        dateSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(DataHistory.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        yearText.setText(Integer.toString(year) + "년");
                        dateText.setText(Integer.toString(month + 1) + "월 " + Integer.toString(dayOfMonth) + "일");

                        mYear=year;
                        mMonth=month;
                        mDay=dayOfMonth;
                        refreshHistory(year, month + 1, dayOfMonth);
                    }
                }, mYear, mMonth, mDay);

                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dialog.show();
            }
        });
    }

    public void refreshHistory(final int year, final int month, final int day) {

        Log.d("refreshHistory", "함수 호출");
        String url = MainActivity.makeUrl("getDataHistory");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jarray = new JSONArray(response);

                            tableLayout.removeViews(1, tableLayout.getChildCount()-1);

                            for (int i = 0; i < jarray.length(); i++) {
                                JSONObject jObject = jarray.getJSONObject(i);
                                String date = jObject.getString("date");
                                int temperature = jObject.getInt("temperature");
                                int humidity = jObject.getInt("humidity");

                                TableRow tr = new TableRow(DataHistory.this);
                                tr.setLayoutParams(new TableRow.LayoutParams(
                                        TableRow.LayoutParams.MATCH_PARENT,
                                        TableRow.LayoutParams.WRAP_CONTENT
                                ));
                                tr.setGravity(Gravity.CENTER);

                                TextView d_text = new TextView(DataHistory.this);
                                TextView t_text = new TextView(DataHistory.this);
                                TextView h_text = new TextView(DataHistory.this);


                                d_text.setText(date);
                                d_text.setGravity(Gravity.CENTER);
                                t_text.setText(Integer.toString(temperature));
                                t_text.setGravity(Gravity.CENTER);
                                h_text.setText(Integer.toString(humidity));
                                h_text.setGravity(Gravity.CENTER);

                                tr.addView(d_text);
                                tr.addView(t_text);
                                tr.addView(h_text);

                                tableLayout.addView(tr, new TableLayout.LayoutParams(
                                        TableLayout.LayoutParams.WRAP_CONTENT,
                                        TableLayout.LayoutParams.WRAP_CONTENT
                                ));

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("year", Integer.toString(year));
                params.put("month", Integer.toString(month));
                params.put("day", Integer.toString(day));

                return params;
            }
        };

        request.setShouldCache(false);
        Volley.newRequestQueue(this).add(request);
    }

    private TextView rowTextView;
    public TextView makeTableRowWithText(String str){
        rowTextView.setText(str);
        rowTextView.setTextSize(15);

        return rowTextView;
    }
}
