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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class TimerActivity extends AppCompatActivity {

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

    private SoundPool soundPool1, soundPool2;
    private int sound1, sound2;

    int time;
    int SET = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        soundPool1 = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundPool2 = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        sound1 = soundPool1.load(this, R.raw.bbb, 1);
        sound2 = soundPool2.load(this, R.raw.beeep, 1);

        titleText = findViewById(R.id.titleText);
        stageText = findViewById(R.id.countText);
        timeText = findViewById(R.id.timeText);
        startBtn = findViewById(R.id.startBtn);
        listBtn = findViewById(R.id.listBtn);

        pref = getSharedPreferences("Pref", MODE_PRIVATE);

        initData();

        View.OnClickListener btnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.startBtn:
                        SET = 0;
                        time = TIME.get(SET);
                        stageText.setText(STAGE.get(SET));
                        timeText.setText(time + "초");
                        if (myTimer!=null) myTimer.cancel();
                        myTimer = new MyTimer(getTotalTime()*1000, 1000);
                        myTimer.start();
                        break;
                    case R.id.listBtn:
                        if (myTimer!=null) myTimer.cancel();
                        Intent intent = new Intent(TimerActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
            if ((time==-1) && (SET<(TIME.size()-1))){
                soundPool1.play(sound1, 1f,1f,1,0,1f);
                SET++;
                time = TIME.get(SET);
                stageText.setText(STAGE.get(SET));
            }
        }

        @Override
        public void onFinish() {
            soundPool2.play(sound2, 1f,1f,1,0,1.5f);
            timeText.setText("끝!");
        }
    }

    public void initData(){
        STAGE.clear();  TIME.clear();   TITLE = "";

        String lastPicked_data = pref.getString("timer", "");
        if (lastPicked_data.equals("")){
            Toast.makeText(this,"타이머 로드 에러", Toast.LENGTH_LONG).show();
            Log.d("에러","타이머 로드 에러");
        }else {
            String [] splits = lastPicked_data.split(",");
            COUNT = (splits.length)-1;
            int num = 0;
            for (String str : splits){
                if (num==0){
                    TITLE = str;
                }
                else if ((num%2)==1){
                    STAGE.add(str);
                }
                else if ((num%2)==0) {
                    TIME.add(Integer.parseInt(str));
                }
                num++;
            }

            for (int i=0; i<TIME.size(); i++){
                System.out.println("***" + TIME.get(i) + " " + STAGE.get(i));
            }

            titleText.setText(TITLE);
            SET = 0;
            time = TIME.get(SET);
            timeText.setText(time + "초");
            stageText.setText(STAGE.get(SET));
        }
    }
}