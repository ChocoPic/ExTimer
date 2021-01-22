package com.tlatla.extimer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private TextView countText;
    private TextView titleText;
    private TextView timeText;
    private Button startBtn;
    private Button listBtn;

    private SharedPreferences pref;

    private ArrayList<Integer> TIME = new ArrayList<>();
    private int COUNT;
    private String TITLE;
    private MyTimer myTimer;

    private SoundPool soundPool;
    private int sound;

    int time;
    int i;
    int count=1;
    int interval;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        sound = soundPool.load(this, R.raw.bbb, 1);

        titleText = findViewById(R.id.titleText);
        countText = findViewById(R.id.countText);
        textView = findViewById(R.id.textView);
        timeText = findViewById(R.id.timeText);
        startBtn = findViewById(R.id.startBtn);
        listBtn = findViewById(R.id.listBtn);

        pref = getSharedPreferences("Pref", MODE_PRIVATE);
        initData();


        COUNT = TIME.size();
        interval = (int)Math.round((COUNT+0.3)/2);

        myTimer = new MyTimer((getTotalTime()+interval)*1000, 1000);

        titleText.setText(TITLE);
        countText.setText(count +" / " + interval);
        timeText.setText(TIME.get(0) + "초");
        textView.setVisibility(View.INVISIBLE);


        View.OnClickListener btnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.startBtn:
                        count = 1;
                        i = 0;
                        time = TIME.get(i);
                        myTimer.start();
                        break;
                    case R.id.listBtn:
                        myTimer.cancel();
                        Intent intent = new Intent(MainActivity.this, TimerListActivity.class);
                        startActivity(intent);
                }
            }
        };
        startBtn.setOnClickListener(btnClick);
        listBtn.setOnClickListener(btnClick);
    }

    public int getTotalTime(){
        int sum=0;
        for(int i=0; i<COUNT; i++){
            sum += TIME.get(i);
        }
        return sum;
    }

    public class MyTimer extends CountDownTimer{
        public MyTimer(long millis, long countDownInterval){
            super(millis, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            timeText.setText(time + "초");
            time--;
            try{
                if(time==-1){
                    i++;
                    time = TIME.get(i);
                    if(i%2==0){
                        count++;
                        countText.setText(count +" / " + interval);
                    }else {
                        Log.d("세트: ", count + ", 휴식");
                    }
                    soundPool.play(sound, 1f,1f,0,0,1f);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onFinish() {
            timeText.setText("끝!");
        }
    }

    public void initData(){
        String lastPicked_data = pref.getString("timer", "");
        Log.d("**pref data",lastPicked_data);
        if(lastPicked_data.length()<0){
            TITLE = "플랭크";
            TIME.add(30);
            TIME.add(10);
            TIME.add(40);
            TIME.add(10);
            TIME.add(50);
            TIME.add(10);
            TIME.add(60);
        }else {
            String [] splits = lastPicked_data.split(",");
            TITLE = splits[0];
            for (int i=1; i<splits.length;i++){
                TIME.add(Integer.parseInt(splits[i]));
            }
        }
    }
}
