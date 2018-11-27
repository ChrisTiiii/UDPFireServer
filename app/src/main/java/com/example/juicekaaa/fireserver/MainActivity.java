package com.example.juicekaaa.fireserver;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.bjw.bean.ComBean;
import com.bjw.utils.FuncUtil;
import com.bjw.utils.SerialHelper;
import com.youth.banner.Banner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.SerialPortFinder;
import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {
    private static final int PORT = 12342;//接收客户端的监听端口
    //    private FireBox fireBox;
//    private static final String SHEBEI_IP = "10.101.208.101";
//    private static final String SHEBEI_PORT = "28327";
    private static final String CHUAN = "/dev/ttymxc2";
    private static final String BOTE = "9600";
    @BindView(R.id.video)
    VideoView video;
    @BindView(R.id.banner1)
    Banner banner1;
    @BindView(R.id.banner2)
    Banner banner2;

    private SerialPortFinder serialPortFinder;
    private SerialHelper serialHelper;
    private List<Integer> bannerList = new ArrayList();


    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x211) {
                String order = msg.getData().getString("order");
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
        ButterKnife.bind(this);
        new UdpReceiveThread(PORT, handler).start();

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

    //静态初始化数据
    void initData() {
        bannerList = new ArrayList<>();
        bannerList.add(R.drawable.banner_1);
        bannerList.add(R.drawable.banner_2);
        bannerList.add(R.drawable.banner_3);
        bannerList.add(R.drawable.banner_4);
        //加载图片
        banner1.setImages(bannerList).setImageLoader(new GlideImageLoader()).start();
        banner2.setImages(bannerList).setImageLoader(new GlideImageLoader()).start();
        setVideo();
    }


    /**
     * 设置视频参数
     */
    private void setVideo() {
        MediaController mediaController = new MediaController(this);
        mediaController.setVisibility(View.GONE);//隐藏进度条
        video.setMediaController(mediaController);
        video.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sanleng));
        video.start();
        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
            }
        });
    }

    private void initView() {
        initData();
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
