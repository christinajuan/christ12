package com.example.user.christ12;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private UIHandler uiHandler;
    private ImageView imageView;
    private Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uiHandler = new UIHandler();
        textView = (TextView)findViewById(R.id.tv);
        imageView = (ImageView)findViewById(R.id.img);
    }
    // UDP Sender
    public void test1(View v){

        new Thread(){
            @Override
            public void run() {
                byte[] buf = "Hello, Brad".getBytes();
                try {
                    DatagramSocket socket = new DatagramSocket();
                    DatagramPacket packet = new DatagramPacket(buf, buf.length,
                            InetAddress.getByName("10.0.3.2"), 8888);
                    socket.send(packet);
                    socket.close();
                    Log.v("brad", "UDP Send OK");
                }catch (Exception e){
                    Log.v("brad", e.toString());
                }
            }
        }.start();
    }
    public void test2(View v){
        new Thread(){
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(InetAddress.getByName("10.0.3.2"), 9999);
                    socket.close();
                    Log.v("brad", "TCP Client OK");
                }catch (Exception e){
                    Log.v("brad", e.toString());
                }
            }
        }.start();
    }

    public void test3(View v){
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL("http://www.iii.org.tw/");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(conn.getInputStream()));
                    String line; StringBuilder sb = new StringBuilder();
                    while ( (line = reader.readLine()) != null){
                        sb.append(line + "\n");
                    }
                    reader.close();

                    Message mesg = new Message();
                    Bundle data = new Bundle();
                    data.putCharSequence("data", sb);
                    mesg.setData(data);
                    mesg.what = 0;
                    uiHandler.sendMessage(mesg);


                } catch (Exception e) {
                    Log.v("brad", e.toString());
                }

            }
        }.start();
    }

    public void test4(View v){
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL("http://img.ltn.com.tw/Upload/ent/page/800/2016/05/01/1682406_2.jpg");
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.connect();
                    Bitmap bmp = BitmapFactory.decodeStream(conn.getInputStream());
                    Message mesg = new Message();
                    Bundle data = new Bundle();
                    data.putParcelable("data", bmp);
                    mesg.setData(data);
                    mesg.what = 1;
                    uiHandler.sendMessage(mesg);

                    //imageView.setImageBitmap(bmp);

                }catch (Exception e){
                    Log.v("brad",e.toString());
                }
            }
        }.start();
    }
    public void test5(View v){
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL(
                            "http://img.ltn.com.tw/Upload/ent/page/800/2016/04/18/1668523_1.jpg");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();

                    bmp = BitmapFactory.decodeStream(conn.getInputStream());
                    uiHandler.sendEmptyMessage(2);

                }catch (Exception e){
                    Log.v("brad", e.toString());
                }
            }
        }.start();
    }
    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    textView.setText(msg.getData().getCharSequence("data"));
                    break;
                case 1:
                    imageView.setImageBitmap((Bitmap)msg.getData().getParcelable("data"));
                    break;
                case 2:
                    imageView.setImageBitmap(bmp);
                    break;
            }
        }
    }
}
