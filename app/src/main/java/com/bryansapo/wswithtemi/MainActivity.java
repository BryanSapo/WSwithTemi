package com.bryansapo.wswithtemi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener;
import com.robotemi.sdk.listeners.OnLocationsUpdatedListener;
import com.robotemi.sdk.map.MapModel;
import com.robotemi.sdk.navigation.listener.OnCurrentPositionChangedListener;
import com.robotemi.sdk.navigation.listener.OnDistanceToLocationChangedListener;
import com.robotemi.sdk.navigation.model.Position;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import tech.gusavila92.websocketclient.WebSocketClient;

public class MainActivity extends AppCompatActivity implements OnLocationsUpdatedListener, OnGoToLocationStatusChangedListener, OnDistanceToLocationChangedListener, OnCurrentPositionChangedListener {
    private Robot robot;
    List<String> locations;
    String location="";
    String name="";
    //    URI uri = URI.create("ws://192.168.0.109:1107");
    //URI uri = URI.create("ws://10.0.2.2:1107/hello");//If you wnat to connect to a local device, you need to use '10.0.2.2:your_port'
    //URI uri = URI.create("ws://192.168.0.103:1107/hello");
//    URI uri = URI.create("ws://172.20.10.13:1107/hello");
    URI uri = URI.create("ws://172.20.10.7:1107/hello");
    TextView mTv_response, mTv_id,mTv_pos,mTv_face;
    EditText mEt_msg,mEt_wsConnect;
    Position position;
    Button mBtn_submit, mBtn_ReConnect,mBtn_RePostPosition,mBtn_wsConnect,mBtn_switchPage;
    Dialog dialog;
    ImageButton mBtn_eye;
    Boolean isConnect = false;
    //    URI uri;
    WebSocketClient webSocketClient = null;
    //    private WebSocketClient webSocketClient;
    JSONObject temi = new JSONObject();
    UUID uuid = UUID.randomUUID();
    String id = uuid.toString();
    //String id="temi-"+uuidAsString.substring(0,5);
    String ipAddress = "";
    float ox,oy,oyaw;
    int otiltAngel;
    //List<MapModel> locations = new ArrayList<MapModel>();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        robot=Robot.getInstance();
        ipAddress = getDeviceIpAddress();
        System.out.println("ip->"+ipAddress);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.face);
        System.out.println(locations);
        //locations.add("Sun");
        //locations.add("Earth");
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        mTv_response = (TextView) findViewById(R.id.tv_response);
        mTv_pos= (TextView)findViewById(R.id.position_text);
        mBtn_submit = (Button) findViewById(R.id.btn_submit);
        mBtn_wsConnect=(Button)findViewById(R.id.submitConnect);
        mBtn_switchPage=(Button)findViewById(R.id.SwitchPage);
        mEt_wsConnect=(EditText)findViewById(R.id.webSocketIP);
        mBtn_ReConnect = (Button) findViewById(R.id.ReConnect);
        mBtn_RePostPosition=(Button)findViewById(R.id.RePostPosition);
        mTv_face=(TextView)findViewById(R.id.textView3);
        mBtn_eye=(ImageButton)findViewById(R.id.eye_btn);
        mEt_msg = (EditText) findViewById(R.id.et_msg);
        mEt_msg.clearFocus();

        mTv_id = (TextView) findViewById(R.id.id_tv);
        mTv_id.setText(id);
        mBtn_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                TtsRequest msg=TtsRequest.create("感謝您",false);
                robot.goTo("攤位外");
            }
        });
        mBtn_wsConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createWebSocketClient(mEt_wsConnect.getText().toString());
            }
        });
        mBtn_switchPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, WebViewActivity.class);
                startActivity(intent);
            }
        });
        mBtn_RePostPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temi = new JSONObject();
                locations=robot.getLocations();
                System.out.println(locations);
                //locations
                /*if (!locations.contains(location)) {
                    locations.add(location);
                }*/

                try {
                    temi.put("id", id);
                    temi.put("type", "onMessage");
                    temi.put("ip", ipAddress);
                    temi.put("location", locations);
                    temi.put("status", "Stand by");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                webSocketClient.send(temi.toString());
            }
        });

        mBtn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locations=robot.getLocations();
                System.out.println(locations);
                //locations
                /*if (!locations.contains(location)) {
                    locations.add(location);
                }*/

                try {
                    temi.put("id", id);
                    temi.put("type", "onMessage");
                    temi.put("ip", ipAddress);
                    temi.put("location", locations);
                    temi.put("status", "Stand by");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                webSocketClient.send(temi.toString());
                Log.d("WebSocket", mEt_msg.getText().toString());
//                System.out.println(mEt_msg.getText().toString());
            }
        });
        mBtn_ReConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnect) {
                    createWebSocketClient(mEt_wsConnect.getText().toString());
                } else {
//                    mTv_response.setText("Connection is already on...");
                    Toast.makeText(getApplicationContext(), "Connection is already on...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        robot.removeOnLocationsUpdateListener(this);
        robot.removeOnGoToLocationStatusChangedListener(this);
        robot.removeOnCurrentPositionChangedListener(this);
        robot.removeOnDistanceToLocationChangedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        locations = robot.getLocations();
        System.out.println(locations);

        robot.addOnLocationsUpdatedListener(this);
        robot.addOnGoToLocationStatusChangedListener(this);
        robot.addOnCurrentPositionChangedListener(this);
        robot.addOnDistanceToLocationChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        temi = new JSONObject();
        try {
            locations = robot.getLocations();
            System.out.println(locations.getClass());
            System.out.println(locations);
            temi.put("id", id);
            temi.put("type", "onConnect");
            temi.put("ip", ipAddress);
            temi.put("location", locations);
            temi.put("status", "Stand by");

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void createWebSocketClient(String url) {

        try {
            // Connect to local host
            //uri = new URI("ws://172.20.10.13:1107/hello");
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.i("WebSocket", "Session is starting");
                try {
                    locations = robot.getLocations();
                    System.out.println(locations.getClass());
                    System.out.println(locations);
                    temi.put("id", id);
                    temi.put("type", "onConnect");
                    temi.put("ip", ipAddress);
                    temi.put("location", locations);
                    temi.put("status", "Stand by");

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                this.send(temi.toString());//建立一個JSON格式訊息,讓WEBSOCKET具有TOPIC的概念。
                isConnect = true;
            }

            @Override
            public void onTextReceived(String s) {
                Log.i("WebSocket", "接收到來自伺服器的： " + s);
                System.out.println(s.getClass());
                Boolean flag;
                try {
                    JSONObject json_obj = new JSONObject(s);
                    String command = (String) json_obj.get("command");
                    String args = (String) json_obj.get("args");

                    s="";
                    s+=command;
                    s+=" , ";
                    s+=args;
                    switch (command){
                        case "setName":
                            name = (String)json_obj.get("args");
                            System.out.println(name);
                            break;
                        case "Speak":
                            TtsRequest msg=TtsRequest.create(args,false);
                            robot.speak(msg);
                            break;
                        case "Go":
                            try {
                                System.out.println("args class-> "+args.getClass());
                                robot.goTo(args);
                                System.out.println(command+" , "+args);
                            }catch (Exception e){
                                System.out.println("[EXCEPTION]->"+e.getMessage());
                            }
                            break;
                        case "Save":
                            flag=robot.saveLocation(args);
                            if(flag){
                                Toast.makeText(getApplicationContext(), "儲存成功地點", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "儲存地點失敗", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case "Delete":
                            flag=robot.deleteLocation(args);
                            if(flag){
                                TtsRequest del_msg=TtsRequest.create("成功刪除了"+args+"位置",false);
                                robot.speak(del_msg);
                                Toast.makeText(getApplicationContext(), "刪除成功地點", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "刪除地點失敗", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case "GoPos":
                            System.out.println("From "+ox+", "+oy+", "+oyaw+", "+otiltAngel);
                            float x = Float.valueOf(args.split(";")[0]);
                            float y = Float.valueOf(args.split(";")[1]);
                            System.out.println("To   "+x+", "+y+", "+oyaw+", "+otiltAngel);
                            position = new Position(x,y,oyaw,otiltAngel);
                            robot.goToPosition(position);
                            break;
                        case "Tune":
                            System.out.println("From "+ox+", "+oy+", "+oyaw+", "+otiltAngel);
                            String target=args.split(";")[0];
                            String op = args.split(";")[1];
                            if(target.equals("x")){
                                if(op.equals("+")){
                                    position = new Position(ox+0.5f,oy,oyaw,otiltAngel);
                                }else if(op.equals("-")){
                                    position = new Position(ox-0.5f,oy,oyaw,otiltAngel);
                                }
                            }else if(target.equals("y")){
                                if(op.equals("+")){
                                    position = new Position(ox,oy+0.5f,oyaw,otiltAngel);
                                }else if(op.equals("-")){
                                    position = new Position(ox,oy-0.5f,oyaw,otiltAngel);
                                }
                            }else if(target.equals("yaw")){
                                if(op.equals("+")){
                                    position = new Position(ox,oy,oyaw+0.5f,otiltAngel);
                                }else if(op.equals("-")){
                                    position = new Position(ox,oy,oyaw-0.5f,otiltAngel);
                                }
                            }
                            //position = new Position(ox,oy,oyaw,otiltAngel);
                            System.out.println("To   "+position.getX()+", "+ position.getY()+", "+position.getYaw()+", "+otiltAngel);
                            robot.goToPosition(position);
                            break;
                        case "Reload":
                            temi=new JSONObject();
                            try {
                                locations = robot.getLocations();
                                System.out.println(locations.getClass());
                                System.out.println(locations);
                                temi.put("id", id);
                                temi.put("type", "onConnect");
                                temi.put("ip", ipAddress);
                                temi.put("location", locations);
                                temi.put("status", "Stand by");

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            webSocketClient.send(temi.toString());
                            break;
                        case "Go_queue":
                            String[] bookShelf_arr= args.split(";")[0].split(",");
                            String[] bookName_arr= args.split(";")[1].split(",");
                            for(int i=0;i<bookShelf_arr.length;i++) {
                                System.out.println(bookShelf_arr[i]);
                            }
                            Intent intent = new Intent();
                            intent.putExtra("bookShelf", bookShelf_arr);
                            intent.putExtra("bookName", bookName_arr);
                            intent.setClass(MainActivity.this, bookShelfNav.class);
                            startActivity(intent);
                            break;

                    }
                } catch (JSONException e) {
                    System.out.println(e);
                    throw new RuntimeException(e);
                }

                final String message = s;
                final String ip = ipAddress;
                mTv_response = (TextView) findViewById(R.id.tv_response);
                mTv_face=(TextView)findViewById(R.id.textView3);
//                mTv_response.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mTv_response.setText(s);
//                    }
//                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mTv_response.setText(message);
                            mTv_face.setText(name);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onBinaryReceived(byte[] data) {
            }

            @Override
            public void onPingReceived(byte[] data) {
            }

            @Override
            public void onPongReceived(byte[] data) {
            }

            @Override
            public void onException(Exception e) {
                Log.i("WebSocket", e.getMessage());
                createWebSocketClient(mEt_wsConnect.getText().toString());
            }

            @Override
            public void onCloseReceived() {
                Log.i("WebSocket", "Closed ");
                System.out.println("onCloseReceived");
                mTv_response.setText("Connection closed...");
                isConnect = false;
            }
        };

        webSocketClient.setConnectTimeout(1000000);
        webSocketClient.setReadTimeout(6000000);
        webSocketClient.enableAutomaticReconnection(500);
        webSocketClient.connect();
    }

    @Override
    public void onLocationsUpdated(@NonNull List<String> list) {
        try {
            locations=list;
            System.out.println(locations.getClass());
            temi.put("id", id);
            temi.put("type", "onConnect");
            temi.put("ip", ipAddress);
            temi.put("location", locations);
            temi.put("status", "Stand by");
            System.out.println(locations);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        webSocketClient.send(temi.toString());
    }

    @Override
    public void onGoToLocationStatusChanged(@NonNull String s, @NonNull String s1, int i, @NonNull String s2) {
        System.out.println("<[onGoToLocationStatusChanged]->"+s+" , "+s1+" , "+s2+">");
        locations=robot.getLocations();
        JSONObject temi = new JSONObject();
        try {
            temi.put("id", id);
            temi.put("type", "onConnect");
            temi.put("ip", ipAddress);
            temi.put("location", locations);
            temi.put("status", s1);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        if (s1 == "complete") {
            TtsRequest msg=TtsRequest.create("成功抵達，點擊螢幕眼睛送我回家",false);
            robot.speak(msg);
        }
        webSocketClient.send(temi.toString());

    }

    @Override
    public void onDistanceToLocationChanged(@NonNull Map<String, Float> map) {
        System.out.println(map);
        Float dis=100.0f;
        for(Map.Entry<String,Float> entry:map.entrySet()){
            if(entry.getValue()<dis){
                dis=entry.getValue();
                location=entry.getKey();
            }
        }
        System.out.println("我距離"+location+"最近");//可以用來判斷目前位置
    }
    private String getDeviceIpAddress() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ip = wifiInfo.getIpAddress();
            String ipAddress = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
            return ipAddress;
        }
        return "N/A";
    }

    @Override
    public void onCurrentPositionChanged(@NonNull Position position) {
        System.out.println(position);
        ox=position.getX();
        oy=position.getY();
        oyaw=position.getYaw();
        otiltAngel=position.getTiltAngle();
        String rtn = "X: "+ox+", Y: "+oy+", YAW: "+oyaw+", TiltAngel: "+otiltAngel;
        mTv_pos.setText(rtn);

    }
}