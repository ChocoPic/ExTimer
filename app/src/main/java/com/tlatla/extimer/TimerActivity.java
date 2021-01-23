package com.tlatla.extimer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class TimerActivity extends AppCompatActivity {

    private TextView textView;
    private TextView stageText;
    private TextView titleText;
    private TextView timeText;
    private Button startBtn;
    private Button listBtn;

    private SharedPreferences pref;

    private ArrayList<String> STAGE = new ArrayList<>();
    private ArrayList<Integer> TIME = new ArrayList<>();
    private int COUNT;
    private String TITLE;
    private MyTimer myTimer;

    private SoundPool soundPool;
    private int sound1;

    int time;
    int i = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        sound1 = soundPool.load(this, R.raw.bbb, 1);
        //sound2 = soundPool.load(this, R.raw.bbb, 0);

        titleText = findViewById(R.id.titleText);
        stageText = findViewById(R.id.countText);
        textView = findViewById(R.id.textView);
        timeText = findViewById(R.id.timeText);
        startBtn = findViewById(R.id.startBtn);
        listBtn = findViewById(R.id.listBtn);

        pref = getSharedPreferences("Pref", MODE_PRIVATE);
        initData();

        titleText.setText(TITLE);
        stageText.setText(STAGE.get(0));
        timeText.setText(TIME.get(0) + "초");
        textView.setVisibility(View.INVISIBLE);

        View.OnClickListener btnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.startBtn:
                        i = 0;
                        time = TIME.get(i);
                        stageText.setText(STAGE.get(i));
                        timeText.setText(time + "초");
                        if (myTimer!=null) myTimer.cancel();
                        myTimer = new MyTimer(getTotalTime()*1000, 1000);
                        myTimer.start();
                        break;
                    case R.id.listBtn:
                        myTimer.cancel();
                        Intent intent = new Intent(TimerActivity.this, MainActivity.class);
                        startActivity(intent);
                }
            }
        };
        startBtn.setOnClickListener(btnClick);
        listBtn.setOnClickListener(btnClick);
    }

    public int getTotalTime(){
        int sum=0;
        for(int i=0; i<TIME.size(); i++){
            sum += TIME.get(i);
        }
        return sum+TIME.size();
    }

    public class MyTimer extends CountDownTimer{
        public MyTimer(long millis, long countDownInterval){
            super(millis, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            time--;
            timeText.setText((time+1) + "초");
            if ((time==-1) && (i<(TIME.size()-1))){
                soundPool.play(sound1, 1f,1f,0,0,1f);
                i++;
                time = TIME.get(i);
                stageText.setText(STAGE.get(i));
            }
        }

        @Override
        public void onFinish() {
            //soundPool.play(sound1, 1f,1f,0,0,1f);
            timeText.setText("끝!");
        }
    }

    public void initData(){
        String lastPicked_data = pref.getString("timer", "");
        String [] splits = lastPicked_data.split(",");
        TITLE = splits[0];
        COUNT = (splits.length)-1;
        for (int i=1; i<splits.length;i++){
            try{
                if (i%2==0)
                    TIME.add(Integer.parseInt(splits[i]));
                else if (i%2==1)
                    STAGE.add(splits[i]);
            }catch (Exception e){
                e.printStackTrace();
                }
            }
    }
}