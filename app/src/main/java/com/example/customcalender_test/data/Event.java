package com.example.customcalender_test.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

public class Event implements Parcelable {

    private String mID;
    private String mTitle;
    private String mAuthor;
    private Calendar mDate;
    private int mColor;
    private String mDiary; // 다이어리 텍스트
    private boolean isDiary; // 다이어리 저장되어 있으면 true, 저장되어 있지 않으면 false
    private String mPage; // 읽은 페이지 수


    public Event(String id, String title, Calendar date, int color, boolean checkDiary, String diary, String page) {
        mID = id;
        mTitle = title;
        mDate = date;
        mColor = color;
        mPage = page;
        isDiary = checkDiary;

        if(isDiary){
            mDiary = diary;
        }
    }

    public Event() {

    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public void setisDiary(boolean isdiary) {
        isDiary = isdiary;
    }

    public void setmDate(Calendar mDate) {
        this.mDate = mDate;
    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setmColor(int mColor) {
        this.mColor = mColor;
    }

    public void setmDiary(String diaryText) {
        this.mDiary=diaryText;
    }

    public String getID() {
        return mID;
    }

    public String getTitle() {
        return mTitle;
    }

    public Calendar getDate() {
        return mDate;
    }

    public int getColor() {
        return mColor;
    }

    public String getmPage(){return mPage;}

    public boolean getisDiary() {
        return isDiary;
    }

    public String getmAuthor() {return mAuthor; }

    public String getDiaryText(){return mDiary;}

    protected Event(Parcel in) {
        mID = in.readString();
        mTitle = in.readString();
        mAuthor = in.readString();
        mDiary = in.readString();
        mColor = in.readInt();
        mPage = in.readString();
        mDate = (Calendar) in.readSerializable();
        isDiary = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mID);
        dest.writeString(mTitle);
        dest.writeString(mAuthor);
        dest.writeString(mDiary);
        dest.writeInt(mColor);
        dest.writeString(mPage);
        dest.writeSerializable(mDate);
        dest.writeByte((byte) (isDiary ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
