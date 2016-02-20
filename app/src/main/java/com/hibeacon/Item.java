package com.hibeacon;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alper Tekin - alpertekin.com on 17.2.2016.
 */
public class Item implements Parcelable {

    String text;
    String imageUrl;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
        dest.writeString(this.imageUrl);
    }

    public Item() {
    }

    protected Item(Parcel in) {
        this.text = in.readString();
        this.imageUrl = in.readString();
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
