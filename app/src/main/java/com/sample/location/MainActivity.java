package com.sample.location;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;



public class MainActivity extends AppCompatActivity {
    EditText title;
    Button btn_save, btn_view;
    String save_location;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();

        Intent intent = getIntent();
        final double latitude = intent.getDoubleExtra("latitude", 0);
        final double longitude = intent.getDoubleExtra("longitude", 0);

        TextView location = findViewById(R.id.location);
        location.setText("위도=" + latitude + ", 경도=" + longitude);

        title = (EditText) findViewById(R.id.txt_location);

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_location = title.getText().toString();
                dataInsert(save_location, latitude, longitude);
            }
        });
    }

    public void dataInsert(String title, double latitude, double longitude) {
        new Thread(){
            public void run(){
                try{
                    URL url = new URL("http://10.0.2.2/insert.php");
                    HttpURLConnection http;
                    http = (HttpURLConnection)url.openConnection();

                    http.setDefaultUseCaches(false);
                    http.setDoInput(true);
                    http.setRequestMethod("POST");
                    http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

                    StringBuffer buffer = new StringBuffer();
                    buffer.append("title").append("=").append("latitude").append("_").append("longitude");

                    OutputStreamWriter osw = new OutputStreamWriter(http.getOutputStream(), "utf8");
                    osw.write(buffer.toString());
                    osw.flush();

                    InputStreamReader isr = new InputStreamReader(http.getInputStream(), "utf8");
                    BufferedReader reader = new BufferedReader(isr);
                    String str;
                    while((str=reader.readLine()) != null){
                        Log.e("서버측에서 받은 것", str);
                    }

                } catch (Exception e){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "입력 실패", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }.start();
    }
}
