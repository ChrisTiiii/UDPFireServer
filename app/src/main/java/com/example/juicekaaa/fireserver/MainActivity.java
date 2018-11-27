package com.example.juicekaaa.fireserver;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bjw.bean.ComBean;
import com.bjw.utils.FuncUtil;
import com.bjw.utils.SerialHelper;
import com.example.juicekaaa.fireserver.firebox.FireBox;

import java.io.IOException;

import android_serialport_api.SerialPortFinder;

import static com.example.juicekaaa.fireserver.udp.EncodingConversionTools.str2HexStr;


public class MainActivity extends AppCompatActivity {
    private static TextView tv;
    private static final int PORT = 12342;//接收客户端的监听端口
    private FireBox fireBox;
    private static final String SHEBEI_IP = "10.101.208.101";
    private static final String SHEBEI_PORT = "28327";
    private static final String CHUAN = "/dev/ttymxc2";
    private static final String BOTE = "9600";

    private SerialPortFinder serialPortFinder;
    private SerialHelper serialHelper;


    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x211) {
                String order = msg.getData().getString("order");
                tv.setText(order);
                if (serialHelper.isOpen()) {
                    serialHelper.sendHex(order);
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new UdpReceiveThread(PORT, handler).start();
        tv = findViewById(R.id.tv_receive);
//        if (SHEBEI_PORT != 0) {
//            fireBox = new FireBox(getApplicationContext(), SHEBEI_IP, SHEBEI_PORT);
//            fireBox.openBox();
//        }
        initView();

    }

    public void clickbtn(View v) {
        if (serialHelper.isOpen()) {
            serialHelper.sendTxt("dsadada");
        }

    }
    private void initView() {


        serialPortFinder = new SerialPortFinder();
        serialHelper = new SerialHelper() {

            @Override
            protected void onDataReceived(final ComBean comBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(), FuncUtil.ByteArrToHex(comBean.bRec), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        try {
            serialHelper.setBaudRate(BOTE);
            serialHelper.setPort(CHUAN);
            serialHelper.open();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serialHelper.close();

    }
}
