package com.tlatla.extimer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class TimerActivity extends AppCompatActivity {

    private final static String TOKEN = "@";
    private TextView stageText;
    private TextView titleText;
    private TextView timeText;
    private Button startBtn;
    private Button listBtn;
    private Switch DL_switch;
    private LinearLayout title_layout;
    private ImageView img;

    private SharedPreferences pref;

    private ArrayList<String> STAGE = new ArrayList<>();
    private ArrayList<Integer> TIME = new ArrayList<>();
    private String TITLE;
    private MyTimer myTimer;

    Uri note;
    Ringtone ringtone;
    SoundPool soundPool;
    int sound;

    private int time = 0;
    private int SET = 0;

    private LinearLayout ll;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        soundPool = new SoundPool(1, AudioManager.STREAM_ALARM , 0);
        sound = soundPool.load(this, R.raw.bbb, 1);
        note = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(),note);

        titleText = findViewById(R.id.titleText);
        stageText = findViewById(R.id.countText);
        timeText = findViewById(R.id.timeText);
        startBtn = findViewById(R.id.startBtn);
        listBtn = findViewById(R.id.listBtn);
        DL_switch = findViewById(R.id.switch1);
        ll = findViewById(R.id.backgroud_layout);
        title_layout = findViewById(R.id.title_layout);
        img = findViewById(R.id.imageView2);

        pref = getSharedPreferences("Pref", MODE_PRIVATE);
        String value = pref.getString("timer", "");
        System.out.println("출력 " + value);
        String [] splits = value.split(TOKEN);
        TIME.clear(); STAGE.clear();
        TITLE = splits[0];
        for (int i=1; i<splits.length-1; i=i+2){
            STAGE.add(splits[i]);
            TIME.add(Integer.parseInt(splits[i+1]));
        }
        System.out.println("출력 " + TIME.size() + STAGE.size());
        SET = 0;
        time = TIME.get(SET);
        titleText.setText(TITLE);
        stageText.setText(STAGE.get(SET));
        timeText.setText(time + "초");


        View.OnClickListener btnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.startBtn:
                        SET = 0;
                        time = TIME.get(SET);
                        titleText.setText(TITLE);
                        stageText.setText(STAGE.get(SET));
                        timeText.setText( time +"초");
                        if (myTimer!=null) myTimer.cancel();
                        myTimer = new MyTimer(getTotalTime()*1000, 1000);
                        myTimer.start();
                        startBtn.setBackgroundColor(Color.parseColor("#D77F6E"));
                        startBtn.setText("재시작");
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

        DL_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //다크모드
                    changeBarColor("#366185");
                    title_layout.setBackgroundColor(Color.parseColor("#366185"));
                    ll.setBackgroundColor(Color.parseColor("#2F3942"));
                    timeText.setTextColor(Color.parseColor("#EAEAEA"));
                    stageText.setTextColor(Color.parseColor("#818181"));
                    titleText.setTextColor(Color.parseColor("#C1C1C1"));
                    img.setImageResource(R.drawable.light_white);
                }else {
                    //라이트모드
                    changeBarColor("#6EBEC1");
                    title_layout.setBackgroundColor(Color.parseColor("#6EBEC1"));
                    ll.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    timeText.setTextColor(Color.parseColor("#000000"));
                    stageText.setTextColor(Color.parseColor("#C1C1C1"));
                    titleText.setTextColor(Color.parseColor("#000000"));
                    img.setImageResource(R.drawable.light_black);
                }
            }
        });
    }

    public int getTotalTime(){
        int sum=0;
        for(int i=0; i<TIME.size(); i++){
            sum += TIME.get(i);
        }
        return sum+TIME.size()-1;
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
                soundPool.play(sound, 1f, 1f, 1,0,1f);
                SET++;
                time = TIME.get(SET);
                stageText.setText(STAGE.get(SET));
            }
        }

        @Override
        public void onFinish() {
            ringtone.play();
            timeText.setText("끝!");
            startBtn.setBackgroundColor(Color.parseColor("#EDC779"));
            startBtn.setText("시작");
        }
    }
    public void changeBarColor(String color){
        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (view!=null){
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor(color));
            }
        }
    }
}