package com.tlatla.extimer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final static String TOKEN = "@";
    private final String FILE_NAME = "timer.list";
    private RecyclerView recyclerView;
    private Adapter adapter;
    private ArrayList<String> titleList = new ArrayList<>();    //리사이클러뷰 출력용 (제목)
    private ArrayList<String> allList;  //파일 저장용 (제목+시간)
    private ArrayList<String> timeList = new ArrayList<>(); //시간 예쁘게 저장용

    private TextView closeBtn;
    private Button newBtn;
    private Button addTimerBtn;
    private Button saveBtn;
    private LinearLayout add_layout;
    private TextView title_editText;
    private LinearLayout time_layout;
    private ArrayList<EditText> editTexts;
    private SQLdbHelper helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //뷰 세팅
        recyclerView = findViewById(R.id.recyclerview);
        add_layout = findViewById(R.id.add_layout);
        closeBtn = findViewById(R.id.closeBtn);
        newBtn = findViewById(R.id.newBtn);
        addTimerBtn = findViewById(R.id.addTimerBtn);
        saveBtn = findViewById(R.id.saveBtn);
        title_editText = findViewById(R.id.title_editText);
        time_layout = findViewById(R.id.time_layout);
        add_layout.setVisibility(View.GONE);

        //SQLite & ArrayList 세팅
        helper = new SQLdbHelper(this);
        allList = helper.loadDataList();
        for (int i = 0; i < allList.size(); i++) {
            String s_time = "";
            String [] splits = allList.get(i).split(TOKEN);
            titleList.add(splits[0]);
            for (int j=1; j<splits.length-1; j=j+2){
                s_time = s_time + splits[j] +"("+splits[j+1]+"초) - ";
            }
            s_time = s_time.substring(0, s_time.length()-3);
            timeList.add(s_time);
        }

        //리사이클러뷰 세팅
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(titleList, timeList, this);
        recyclerView.setAdapter(adapter);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.newBtn:
                        editTexts = new ArrayList<>();
                        time_layout.removeAllViews();
                        title_editText.setText("");
                        add_layout.setVisibility(View.VISIBLE);
                        newBtn.setVisibility(View.GONE);
                        break;
                    case R.id.closeBtn:
                        add_layout.setVisibility(View.GONE);
                        newBtn.setVisibility(View.VISIBLE);
                        break;
                    case R.id.addTimerBtn:
                        timerView();
                        break;
                    case R.id.saveBtn:
                        //제목을 가져옴
                        Boolean blank = true;
                        String title = String.valueOf(title_editText.getText());
                        String line = "";   String line2 = "";
                        if (title == null || title.equals("")) {
                            Toast.makeText(MainActivity.this, "제목을 입력해주세요", Toast.LENGTH_LONG).show();
                        } else {
                            line =  line + title + TOKEN;
                            //단계와 시간을 가져옴
                            for (EditText et : editTexts) {
                                if (et.getText().toString() == null || et.getText().toString().equals("")) {
                                    Toast.makeText(MainActivity.this, "빈칸이 있어요", Toast.LENGTH_LONG).show();
                                    blank = true;
                                    break;
                                } else {
                                    line = line + et.getText().toString() + TOKEN;
                                    blank = false;
                                }
                            }
                            if (!blank) {
                                String [] splits = line.split(TOKEN);
                                for (int j=1; j<splits.length-1; j=j+2){
                                    line2 = line2 + splits[j] +"("+splits[j+1]+"초) - ";
                                }
                                line2 = line2.substring(0, line2.length()-3);
                                helper.insertData(line);
                                allList.add(line);  //
                                titleList.add(title);
                                timeList.add(line2);
                                adapter.notifyDataSetChanged();
                                add_layout.setVisibility(View.GONE);
                                newBtn.setVisibility(View.VISIBLE);
                                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(editTexts.get(editTexts.size() - 1).getWindowToken(), 0);
                            }
                        }
                }
            }
        };
        newBtn.setOnClickListener(onClickListener);
        addTimerBtn.setOnClickListener(onClickListener);
        saveBtn.setOnClickListener(onClickListener);
        closeBtn.setOnClickListener(onClickListener);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Adapter.Holder holder, View view, int position) {
                Log.d("아이템 클릭함", position + "");
                String item = adapter.getItem(position);
                int clickedIndex = 0;
                for (String title : titleList) {
                    if (title.equals(item)) break;
                    clickedIndex++;
                }
                SharedPreferences pref = getSharedPreferences("Pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("timer", allList.get(clickedIndex));
                editor.commit();
                Intent intent = new Intent(MainActivity.this, TimerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            @Override
            public boolean onLongItemClick(Adapter.Holder holder, View view, int position) {
                Log.d("아이템 길게 클릭함", position + "");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("삭제하시겠습니까?");
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        helper.deleteData(allList.get(position));
                        allList.remove(position);
                        titleList.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
                });
                builder.show();
                return true;
            }
        });
    }

    //타이머 추가
    public void timerView(){
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.addView(newEditText("단계", InputType.TYPE_CLASS_TEXT));
        layout.addView(newEditText("타이머 시간", InputType.TYPE_CLASS_NUMBER));
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        time_layout.addView(layout);
    }

    public EditText newEditText(String hint, int type){
        EditText myText = new EditText(this);
        myText.setTextColor(Color.BLACK);
        myText.setTextSize(18);
        myText.setTypeface(ResourcesCompat.getFont(this, R.font.app_font));
        myText.setHint(hint);
        myText.setHintTextColor(Color.LTGRAY);
        myText.setInputType(type);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        myText.setLayoutParams(lp);
        editTexts.add(myText);
        return myText;
    }
}
