package jahirfiquitiva.iconshowcase.config;

import android.content.Context;
import android.content.res.Resources;
import android.preference.Preference;
import android.support.annotation.ArrayRes;
import android.support.annotation.BoolRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import jahirfiquitiva.iconshowcase.BuildConfig;
import jahirfiquitiva.iconshowcase.R;

/**
 * Created by Allan Wang on 2016-08-19.
 * <p/>
 * With reference to Polar
 * https://github.com/afollestad/polar-dashboard/blob/master/app/src/main/java/com/afollestad/polar/config/Config.java
 */
public class Config implements IConfig {

    private Config(@Nullable Context context) {
        mR = null;
        mContext = context;
        if (context != null)
            mR = context.getResources();
    }

    private static Config mConfig;
    private Context mContext;
    private Resources mR;

    public static void init(@NonNull Context context) {
        mConfig = new Config(context);
    }

    public static void setContext(Context context) {
        if (mConfig != null) {
            mConfig.mContext = context;
            if (context != null)
                mConfig.mR = context.getResources();
        }
    }

    private void destroy() {
        mContext = null;
        mR = null;
    }

    //TODO deinit Config when Showcase is destroyed
    public static void deinit() {
        if (mConfig != null) {
            mConfig.destroy();
            mConfig = null;
        }
    }

    @NonNull
    public static IConfig get() {
        if (mConfig == null)
            return new Config(null); // shouldn't ever happen, but avoid crashes
        return mConfig;
    }

    @NonNull
    public static IConfig get(@NonNull Context context) {
        if (mConfig == null)
            return new Config(context);
        return mConfig;
    }
    
    // Getters

    private Preference prefs() {
        return new Preference(mContext);
    }

    @Override
    public boolean bool(@BoolRes int id) {
        return mR != null && mR.getBoolean(id);
    }

    @Override
    @Nullable
    public String string(@StringRes int id) {
        if (mR == null) return null;
        return mR.getString(id);
    }

    @Override
    @Nullable
    public String[] stringArray(@ArrayRes int id) {
        if (mR == null) return null;
        return mR.getStringArray(id);
    }

    @Override
    public int integer(@IntegerRes int id) {
        if (mR == null) return 0;
        return mR.getInteger(id);
    }

    @Override
    public boolean hasString(@StringRes int id) {
        String s = string(id);
        return (s != null && !s.isEmpty());
    }

    @Override
    public boolean hasArray(@ArrayRes int id) {
        String[] s = stringArray(id);
        return (s != null && s.length != 0);
    }

    @Override
    public boolean allowDebugging() {
        return BuildConfig.DEBUG || mR == null || mR.getBoolean(R.bool.debugging);
    }

    @Override
    public int appTheme() {
        return integer(R.integer.app_theme);
    }

    @Override
    public boolean hasDonations() {
        return hasGoogleDonations() || hasPaypal();
    }

    @Override
    public boolean hasGoogleDonations() { //Also check donation key from java
        return hasArray(R.array.google_donations_catalog) && hasArray(R.array.consumable_google_donation_items) && hasArray(R.array.nonconsumable_google_donation_items);
    }

    @Override
    public boolean hasPaypal() {
        return hasString(R.string.paypal_user);
    }

    @NonNull
    @Override
    public String getPaypalCurrency() {
        String s = string(R.string.paypal_currency_code);
        if (s == null || s.length() != 3) return "USD"; //TODO log currency issue
        return s;
    }

    @Override
    public boolean devOptions() {
        return bool(R.bool.dev_options);
    }

    @Override
    public boolean shuffleToolbarIcons() {
        return bool(R.bool.shuffle_toolbar_icons);
    }

    @Override
    public boolean userWallpaperInToolbar() {
        return bool(R.bool.enable_user_wallpaper_in_toolbar);
    }

    @Override
    public boolean hidePackInfo() {
        return bool(R.bool.hide_pack_info);
    }

    @Override
    public int getIconResId(String iconName) {
        if (mContext == null) return 0;
        return mR.getIdentifier(iconName, "drawable", mContext.getPackageName());
    }
}