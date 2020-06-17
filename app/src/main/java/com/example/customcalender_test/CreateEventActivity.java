package com.example.customcalender_test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.customcalender_test.data.Event;
import com.example.customcalender_test.utils.ColorUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// 다이어리 저장 - 파이어 베이스에 추가.

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CreateEventActivity extends AppCompatActivity {

    private DatabaseReference rootRefer = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference conditionRef = rootRefer.child("addbook");

    public static final int ACTION_DELETE = 1;
    public static final int ACTION_EDIT = 2;
    public static final int ACTION_CREATE = 3;

    private static final String INTENT_EXTRA_ACTION = "intent_extra_action";
    private static final String INTENT_EXTRA_EVENT = "intent_extra_event";
    private static final String INTENT_EXTRA_CALENDAR = "intent_extra_calendar";

    private static final int SET_DATE_AND_TIME_REQUEST_CODE = 200;

    private final static SimpleDateFormat dateFormat
            = new SimpleDateFormat("YYYY-MM-dd", Locale.getDefault());

    private Event mOriginalEvent;

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

    public static Intent makeIntent(Context context, @NonNull Event event) {
        return new Intent(context, CreateEventActivity.class).putExtra(INTENT_EXTRA_EVENT, event);
    }

    public static Event extractEventFromIntent(Intent intent) {
        return intent.getParcelableExtra(INTENT_EXTRA_EVENT);
    }

    public static int extractActionFromIntent(Intent intent) {
        return intent.getIntExtra(INTENT_EXTRA_ACTION, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setResult(RESULT_CANCELED);

        extractDataFromIntentAndInitialize();

        initializeUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete: {
                delete();
                return true;
            }
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void extractDataFromIntentAndInitialize() {
        mOriginalEvent = extractEventFromIntent(getIntent());

        mCalendar = mOriginalEvent.getDate();
        mColor = mOriginalEvent.getColor();
        mTitle = mOriginalEvent.getTitle();
        isDiary = mOriginalEvent.getisDiary();
        mAuthor = mOriginalEvent.getmAuthor();
        mDiary = mOriginalEvent.getDiaryText();
        mPage = mOriginalEvent.getmPage();

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

        // View tvSave = mHeader.findViewById(R.id.tv_save);
        // tvSave.setOnClickListener(new View.OnClickListener() {
            // @Override
           // public void onClick(View v) {
             //   save();
           // }
        //});

        // View tvCancel = mHeader.findViewById(R.id.tv_cancel);
        // tvCancel.setOnClickListener(v -> {
           // onBackPressed();
            // if (mOriginalEvent == null)
               // overridePendingTransition(R.anim.stay, R.anim.slide_out_down);
        // });


    }

    public void deleteOnclick(View v){
        findViewById(R.id.showD_title);
        mOriginalEvent.setisDiary(false);
        mOriginalEvent.setmDiary("");
        setResult(RESULT_OK, new Intent()
                .putExtra(INTENT_EXTRA_ACTION, ACTION_EDIT)
                .putExtra(INTENT_EXTRA_EVENT, mOriginalEvent));
        finish();

    }

    public void saveOnclick(View v){
        //

    }


    private void delete() {
        Log.e(getClass().getSimpleName(), "delete");

        setResult(RESULT_OK, new Intent()
                .putExtra(INTENT_EXTRA_ACTION, ACTION_DELETE)
                .putExtra(INTENT_EXTRA_EVENT, mOriginalEvent));
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_out_down);

    }

    private void save() {

        int action = mOriginalEvent != null ? ACTION_EDIT : ACTION_CREATE;
        String id = mOriginalEvent != null ? mOriginalEvent.getID() : generateID();
        String rawTitle = mTitleView.getText().toString().trim();

        setResult(RESULT_OK, new Intent()
                .putExtra(INTENT_EXTRA_ACTION, action)
                .putExtra(INTENT_EXTRA_EVENT, mOriginalEvent));
        finish();

        if (action == ACTION_CREATE)
            overridePendingTransition(R.anim.stay, R.anim.slide_out_down);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SET_DATE_AND_TIME_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //setupEditMode();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static String generateID() {
        return Long.toString(System.currentTimeMillis());
    }
}
