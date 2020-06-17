package com.example.customcalender_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.customcalender_test.data.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ShowDiaryDetailActivity extends AppCompatActivity {

    private DatabaseReference rootRefer = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference conditionRef = rootRefer.child("addbook");

    private final static SimpleDateFormat dateFormat
            = new SimpleDateFormat("YYYY-MM-dd", Locale.getDefault());

    private Event mOriginalEvent;

    private String dateKey;

    private Calendar mCalendar;
    private String mTitle;
    private int mColor;
    private String mID; // 책 별 아이디
    private String mAuthor;
    private String mDiary; // 다이어리 텍스트
    private boolean isDiary; // 다이어리 저장되어 있으면 true, 저장되어 있지 않으면 false
    private String mPage; // 읽은 페이지 수

    private TextView mTitleView;
    private TextView mAuthorView;
    private TextView mDateView;
    private EditText mDiaryView;
    private TextView mPageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSelectedEvent();
        initializeUI();
    }

    private void getSelectedEvent() {
        mOriginalEvent=(Event)getIntent().getSerializableExtra("diary");

        mCalendar = mOriginalEvent.getDate();
        mColor = mOriginalEvent.getColor();
        mTitle = mOriginalEvent.getTitle();
        isDiary = mOriginalEvent.getisDiary();
        mAuthor = mOriginalEvent.getmAuthor();
        mDiary = mOriginalEvent.getDiaryText();
        mPage = mOriginalEvent.getmPage();
        mID = mOriginalEvent.getID();

        dateKey= dateFormat.format(mCalendar.getTime());

    }

    private void initializeUI() {  // 아이템 선택시 나오는 화면.
        setContentView(R.layout.activity_show_diary);

        mTitleView = findViewById(R.id.showD_title);
        mTitleView.setText(mTitle);

        mAuthorView = findViewById(R.id.showD_author);
        mAuthorView.setText(mAuthor);

        mDateView = findViewById(R.id.showD_date);
        mDateView.setText(dateFormat.format(mCalendar.getTime()));

        mDiaryView = findViewById(R.id.showD_diarytext);
        if(isDiary){
            mDiaryView.setText(mDiary);
        }else{
            mDiaryView.setText("");
        }

        mPageView = findViewById(R.id.showD_page);
        mPageView.setText("~ p. " + mPage);

    }

    // 삭제, 저장시 실시간 디비에 반영
    public void deleteOnclick(View v){

        conditionRef.child(mTitle).child("record").child(dateKey).child("isdiary").setValue("0");
        conditionRef.child(mTitle).child("record").child(dateKey).child("diarytext").setValue("");

        Toast.makeText(v.getContext(), "삭제 완료", Toast.LENGTH_LONG).show();


        Intent intent = new Intent(this, DiaryListActivity.class);
        intent.putExtra("selectdate", dateKey);
        startActivity(intent);

    }

    public void saveOnclick(View v){
        String newdiary = mDiaryView.getText().toString();


        conditionRef.child(mTitle).child("record").child(dateKey).child("isdiary").setValue("1");
        conditionRef.child(mTitle).child("record").child(dateKey).child("diarytext").setValue(newdiary);

        Toast.makeText(v.getContext(), "저장 완료", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, DiaryListActivity.class);
        intent.putExtra("selectdate", dateKey);
        startActivity(intent);

    }



}
