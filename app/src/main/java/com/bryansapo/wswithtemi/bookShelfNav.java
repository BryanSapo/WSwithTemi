package com.bryansapo.wswithtemi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener;

import java.util.List;

public class bookShelfNav extends AppCompatActivity implements OnGoToLocationStatusChangedListener {
    TextView tv_bookName,tv_bookShelf;
    Button btn_ok,btn_cancel;
    int index=-1;
    Robot robot;
    List<String> locations;
    String[] bookShelf,bookName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        robot=Robot.getInstance();
        getSupportActionBar().hide();
        setContentView(R.layout.activity_books);
        Intent intent = getIntent();
        String[] bookShelf=intent.getStringArrayExtra("bookShelf");
        String[] bookName=intent.getStringArrayExtra("bookName");
        locations = robot.getLocations();


        tv_bookName=(TextView) findViewById(R.id.bookName);
        tv_bookShelf=(TextView) findViewById(R.id.bookShelf);
        btn_ok=(Button)findViewById(R.id.key_ok);
        btn_cancel=(Button)findViewById(R.id.key_cancel);

        tv_bookName.setText(bookName[0]);
        tv_bookShelf.setText(bookShelf[0]);

        for(int i=0;i<bookShelf.length;i++) {
            bookShelf[i]=bookShelf[i].toLowerCase();
            System.out.println(bookShelf[i]);
        }
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index+=1;
                if(index<bookShelf.length){
                    if(locations.contains(bookShelf[index])){
                        tv_bookName.setText(bookName[index]);
                        tv_bookShelf.setText(bookShelf[index]);
                        Toast.makeText(getApplicationContext(),"即將前往"+bookName[index],Toast.LENGTH_SHORT).show();
                        robot.goTo(bookShelf[index]);
                    }else{
                        Toast.makeText(getApplicationContext(),"沒有這本書的資訊!!!",Toast.LENGTH_SHORT).show();
                    }



                }else{
                    Toast.makeText(getApplicationContext(),"帶位取書已完畢!!即將返回前頁!!",Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        robot.addOnGoToLocationStatusChangedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        robot.removeOnGoToLocationStatusChangedListener(this);
    }

    @Override
    public void onGoToLocationStatusChanged(@NonNull String s, @NonNull String s1, int i, @NonNull String s2) {
        if(s1=="abort"||s1=="standby"){
//            if(index<bookShelf.length){
//                robot.goTo(bookShelf[index]);
//                tv_bookName.setText(bookName[index]);
//                tv_bookShelf.setText(bookShelf[index]);
//            }
        }else if(s1=="complete"){

            if(index<bookShelf.length){
                tv_bookName.setText(bookName[index]);
                tv_bookShelf.setText(bookShelf[index]);
            }
        }
        /*
        else if(s1=="abort"){
            tv_bookName.setText(bookName[index]);
            tv_bookShelf.setText(bookShelf[index]);
        }
        */
    }
}
