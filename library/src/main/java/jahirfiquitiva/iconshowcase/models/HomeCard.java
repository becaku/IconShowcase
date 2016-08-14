package jahirfiquitiva.iconshowcase.models;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;


public class HomeCard implements Parcelable {

    public final String title, desc;
    public String onClickLink;
    public final Drawable img;
    public final boolean imgEnabled;
    public boolean isInstalled;
    public Intent intent;

    private HomeCard(Builder builder) {
        this.title = builder.title;
        this.desc = builder.desc;
        this.img = builder.img;
        this.imgEnabled = builder.imgEnabled;
        this.onClickLink = builder.onClickLink;
        this.isInstalled = builder.isInstalled;
        this.intent = builder.intent;
    }

    public static class Builder {

        public String title, desc, onClickLink, packageName;
        public Drawable img;
        public Context context;
        public boolean imgEnabled = false, isAnApp = false, isInstalled = false;
        public Intent intent;

        public Builder() {
            this.title = "Insert title here";
            this.desc = "Insert description here";
            this.img = null;
        }

        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String desc) {
            this.desc = desc;
            return this;
        }

        public Builder icon(Drawable img) {
            this.img = img;
            this.imgEnabled = img != null;
            return this;
        }

        public Builder onClickLink(String s, boolean isAnApp, boolean isInstalled, Intent intent) {
            this.onClickLink = isAnApp ? "https://play.google.com/store/apps/details?id=" + s : s;
            this.isAnApp = isAnApp;
            if (isAnApp) {
                this.packageName = s;
                this.isInstalled = isInstalled;
                this.intent = intent;
            }
            return this;
        }

        public HomeCard build() {
            return new HomeCard(this);
        }
    }

    private HomeCard(Parcel in) { //TODO correct parcel
        title = in.readString();
        desc = in.readString();
        img = (Drawable) in.readValue(Drawable.class.getClassLoader());
        imgEnabled = in.readByte() != 0x00;
//        onClick = (Object) in.readValue(Object.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeValue(img);
        dest.writeByte((byte) (imgEnabled ? 0x01 : 0x00));
//        dest.writeValue(onClick);
    }

    @SuppressWarnings("unused")
    public static final Creator<HomeCard> CREATOR = new Creator<HomeCard>() {
        @Override
        public HomeCard createFromParcel(Parcel in) {
            return new HomeCard(in);
        }

        @Override
        public HomeCard[] newArray(int size) {
            return new HomeCard[size];
        }
    };
}