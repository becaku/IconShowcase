/*
 * Copyright (c) 2016 Jahir Fiquitiva
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Special thanks to the project contributors and collaborators
 * 	https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.iconshowcase.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import jahirfiquitiva.iconshowcase.R;
import timber.log.Timber;

/**
 * With a little help from Aidan Follestad (afollestad)
 */
public class Utils {

    public static String getAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // this should never happen
            return "Unknown";
        }
    }

    public static String getAppPackageName(Context context) {
        return context.getPackageName();
    }

    public static boolean hasNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        final PackageManager pm = context.getPackageManager();
        boolean installed;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    public static Snackbar snackbar(@NonNull Context context, @NonNull View view, @NonNull CharSequence text, int duration) {
        Snackbar snackbar = Snackbar.make(view, text, duration);
        snackbar.getView().setBackgroundColor(ThemeUtils.darkOrLight(context, R.color.snackbar_dark, R.color.snackbar_light));
        return snackbar;
    }

    public static void showSimpleSnackbar(Context context, View location, String text) {
        snackbar(context, location, text, Snackbar.LENGTH_SHORT).show();
    }

    public static void openLink(Context context, String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @SuppressWarnings("ResourceAsColor")
    public static void openLinkInChromeCustomTab(Context context, String link) {
        final CustomTabsClient[] mClient = new CustomTabsClient[1];
        final CustomTabsSession[] mCustomTabsSession = new CustomTabsSession[1];
        CustomTabsServiceConnection mCustomTabsServiceConnection;
        CustomTabsIntent customTabsIntent;

        mCustomTabsServiceConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                mClient[0] = customTabsClient;
                mClient[0].warmup(0L);
                mCustomTabsSession[0] = mClient[0].newSession(null);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mClient[0] = null;
            }
        };

        CustomTabsClient.bindCustomTabsService(context, "com.android.chrome", mCustomTabsServiceConnection);
        customTabsIntent = new CustomTabsIntent.Builder(mCustomTabsSession[0])
                .setToolbarColor(ThemeUtils.darkTheme ?
                        ContextCompat.getColor(context, R.color.dark_theme_primary) :
                        ContextCompat.getColor(context, R.color.light_theme_primary))
                .setShowTitle(true)
                .build();

        try {
            customTabsIntent.launchUrl((Activity) context, Uri.parse(link));
        } catch (Exception ex) {
            openLink(context, link);
        }
    }

    public static void showLog(Context context, String s) {
        if (context != null) {
            if (context.getResources().getBoolean(R.bool.debugging)) {
                String tag = "IconShowcase + " + context.getResources().getString(R.string.app_name);
                Log.d(tag, s);
            }
        }
    }

    public static void showLog(Context context, boolean muzei, String s) {
        if (context != null) {
            if (context.getResources().getBoolean(R.bool.debugging) && muzei) {
                Log.d(context.getResources().getString(R.string.app_name) + " Muzei", s);
            }
        }
    }

    public static String getStringFromResources(Context context, int id) {
        return context.getResources().getString(id);
    }

    public static String makeTextReadable(String name) {

        String partialConvertedText = name.replaceAll("_", " ");
        String[] text = partialConvertedText.split("\\s+");
        StringBuilder sb = new StringBuilder();
        if (text[0].length() > 0) {
            sb.append(Character.toUpperCase(text[0].charAt(0))).append(text[0].subSequence(1, text[0].length()).toString().toLowerCase());
            for (int i = 1; i < text.length; i++) {
                sb.append(" ");
                sb.append(capitalizeText(text[i]));
            }
        }
        return sb.toString();

    }

    public static String capitalizeText(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    public static void sendEmailWithDeviceInfo(Context context) {
        StringBuilder emailBuilder = new StringBuilder();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + context.getResources().getString(R.string.email_id)));
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.email_subject));
        emailBuilder.append("\n \n \nOS Version: ").append(System.getProperty("os.version")).append("(").append(Build.VERSION.INCREMENTAL).append(")");
        emailBuilder.append("\nOS API Level: ").append(Build.VERSION.SDK_INT);
        emailBuilder.append("\nDevice: ").append(Build.DEVICE);
        emailBuilder.append("\nManufacturer: ").append(Build.MANUFACTURER);
        emailBuilder.append("\nModel (and Product): ").append(Build.MODEL).append(" (").append(Build.PRODUCT).append(")");
        PackageInfo appInfo = null;
        try {
            appInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert appInfo != null;
        emailBuilder.append("\nApp Version Name: ").append(appInfo.versionName);
        emailBuilder.append("\nApp Version Code: ").append(appInfo.versionCode);
        intent.putExtra(Intent.EXTRA_TEXT, emailBuilder.toString());
        context.startActivity(Intent.createChooser(intent, (context.getResources().getString(R.string.send_title))));
    }

    @SuppressWarnings("ResourceAsColor")
    public static void setupCollapsingToolbarTextColors(Context context,
                                                        CollapsingToolbarLayout collapsingToolbarLayout) {
        int iconsColor = ThemeUtils.darkTheme ?
                ContextCompat.getColor(context, R.color.toolbar_text_dark) :
                ContextCompat.getColor(context, R.color.toolbar_text_light);
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(context, android.R.color.transparent));
        collapsingToolbarLayout.setCollapsedTitleTextColor(iconsColor);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @SuppressWarnings("ConstantConditions")
    public static Bitmap getWidgetPreview(@NonNull Bitmap bitmap, @ColorInt int colorToReplace) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int minX = width;
        int minY = height;
        int maxX = -1;
        int maxY = -1;

        Bitmap newBitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());
        int pixel;

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int index = y * width + x;
                pixel = pixels[index];
                if (pixel == colorToReplace) {
                    pixels[index] = android.graphics.Color.TRANSPARENT;
                }
                if (pixels[index] != android.graphics.Color.TRANSPARENT) {
                    if (x < minX)
                        minX = x;
                    if (x > maxX)
                        maxX = x;
                    if (y < minY)
                        minY = y;
                    if (y > maxY)
                        maxY = y;
                }
            }
        }

        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return Bitmap.createBitmap(newBitmap, minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
    }

    public static int convertMinutesToMillis(int minute) {
        return minute * 60 * 1000;
    }

    public static int convertMillisToMinutes(int millis) {
        return millis / 60 / 1000;
    }

    public static int getIconResId(Resources r, String p, String name) {
        int res = r.getIdentifier(name, "drawable", p);
        if (res != 0) {
            return res;
        } else {
            return 0;
        }
    }

    @SuppressLint("DefaultLocale")
    public static int canRequestXApps(Context context, int numOfMinutes, Preferences mPrefs) {

        Calendar c = Calendar.getInstance();

        int requestsLeft = mPrefs.getRequestsLeft();

        if (requestsLeft > 0) {
            return requestsLeft;
        } else {
            boolean hasHappenedTheTime = timeHappened(numOfMinutes, mPrefs, c);
            if (!hasHappenedTheTime) {
                return -2;
            } else {
                mPrefs.resetRequestsLeft(context);
                return mPrefs.getRequestsLeft(context);
            }
        }

    }

    @SuppressLint("DefaultLocale")
    public static void saveCurrentTimeOfRequest(Preferences mPrefs, Calendar c) {
        String time = String.format("%02d", c.get(Calendar.HOUR_OF_DAY)) + ":" +
                String.format("%02d", c.get(Calendar.MINUTE));
        String day = String.format("%02d", c.get(Calendar.DAY_OF_YEAR));
        mPrefs.setRequestHour(time);
        mPrefs.setRequestDay(Integer.valueOf(day));
        mPrefs.setRequestsCreated(true);
    }

    @SuppressLint("DefaultLocale")
    private static boolean timeHappened(int numOfMinutes, Preferences mPrefs, Calendar c) {
        float hours = (numOfMinutes + 1) / 60.0f;
        float hoursToDays = hours / 24.0f;

        String time = mPrefs.getRequestHour();
        int dayNum = mPrefs.getRequestDay();

        if (numOfMinutes <= 0) {
            return true;
        } else {
            if (!(time.equals("null"))) {

                String currentTime = String.format("%02d", c.get(Calendar.HOUR_OF_DAY)) + ":" +
                        String.format("%02d", c.get(Calendar.MINUTE));
                String currentDay = String.format("%02d", c.get(Calendar.DAY_OF_YEAR));

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                Date startDate = null;
                try {
                    startDate = simpleDateFormat.parse(time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date endDate = null;
                try {
                    endDate = simpleDateFormat.parse(currentTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                long difference = (endDate != null ? endDate.getTime() : 0) - (startDate != null ? startDate.getTime() : 0);
                if (difference < 0) {
                    Date dateMax = null;
                    try {
                        dateMax = simpleDateFormat.parse("24:00");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Date dateMin = null;
                    try {
                        dateMin = simpleDateFormat.parse("00:00");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    difference = ((dateMax != null ? dateMax.getTime() : 0) - startDate.getTime()) + (endDate.getTime() - (dateMin != null ? dateMin.getTime() : 0));
                }
                int days = Integer.valueOf(currentDay) - dayNum;
                int hoursHappened = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
                int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hoursHappened)) / (1000 * 60);

                if (days >= hoursToDays) {
                    return true;
                } else {
                    return hoursHappened >= hours || min >= numOfMinutes;
                }
            } else {
                return true;
            }
        }
    }

    @SuppressLint("DefaultLocale")
    public static int getSecondsLeftToEnableRequest(Context context,
                                                    int numOfMinutes, Preferences mPrefs) {

        int secondsHappened = 0;

        Calendar c = Calendar.getInstance();

        String time;
        int dayNum;

        if (mPrefs.getRequestsCreated()) {
            time = mPrefs.getRequestHour();
            dayNum = mPrefs.getRequestDay();

            String currentTime = String.format("%02d", c.get(Calendar.HOUR_OF_DAY)) + ":" +
                    String.format("%02d", c.get(Calendar.MINUTE));
            String currentDay = String.format("%02d", c.get(Calendar.DAY_OF_YEAR));

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Date startDate = null;
            try {
                startDate = simpleDateFormat.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date endDate = null;
            try {
                endDate = simpleDateFormat.parse(currentTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long difference = (endDate != null ? endDate.getTime() : 0) - (startDate != null ? startDate.getTime() : 0);
            if (difference < 0) {
                Date dateMax = null;
                try {
                    dateMax = simpleDateFormat.parse("24:00");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date dateMin = null;
                try {
                    dateMin = simpleDateFormat.parse("00:00");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                difference = ((dateMax != null ? dateMax.getTime() : 0) - startDate.getTime()) + (endDate.getTime() - (dateMin != null ? dateMin.getTime() : 0));
            }
            int days = Integer.valueOf(currentDay) - dayNum;
            int hoursHappened = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));

            int minutes = (int) (difference - (1000 * 60 * 60 * 24 * days) -
                    (1000 * 60 * 60 * hoursHappened)) / (1000 * 60);

            secondsHappened = (int) (minutes * 60.0f);

        }

        int secondsLeft = (numOfMinutes * 60) - secondsHappened;

        if (secondsHappened < 0 || numOfMinutes <= 0) {
            mPrefs.resetRequestsLeft(context);
            secondsLeft = 0;
        }

        return secondsLeft;

    }

    public static String getTimeName(Context context, int minutes) {
        String text;
        if (minutes > 40320) {
            text = Utils.getStringFromResources(context, R.string.months).toLowerCase();
        } else if (minutes > 10080) {
            text = Utils.getStringFromResources(context, R.string.weeks).toLowerCase();
        } else if (minutes > 1440) {
            text = Utils.getStringFromResources(context, R.string.days).toLowerCase();
        } else if (minutes > 60) {
            text = Utils.getStringFromResources(context, R.string.hours).toLowerCase();
        } else {
            text = Utils.getStringFromResources(context, R.string.minutes).toLowerCase();
        }
        return text;
    }

    public static String getTimeNameInSeconds(Context context, int secs) {
        String text;
        if (secs > (40320 * 60)) {
            text = Utils.getStringFromResources(context, R.string.months).toLowerCase();
        } else if (secs > (10080 * 60)) {
            text = Utils.getStringFromResources(context, R.string.weeks).toLowerCase();
        } else if (secs > (1440 * 60)) {
            text = Utils.getStringFromResources(context, R.string.days).toLowerCase();
        } else if (secs > (60 * 60)) {
            text = Utils.getStringFromResources(context, R.string.hours).toLowerCase();
        } else if (secs > 60) {
            text = Utils.getStringFromResources(context, R.string.minutes).toLowerCase();
        } else {
            text = Utils.getStringFromResources(context, R.string.seconds).toLowerCase();
        }
        return text;
    }

    public static float getExactMinutes(int minutes, boolean withSeconds) {
        float time;
        if (minutes > 40320) {
            time = minutes / 40320.0f;
        } else if (minutes > 10080) {
            time = minutes / 10080.0f;
        } else if (minutes > 1440) {
            time = minutes / 1440.0f;
        } else if (minutes > 60) {
            time = minutes / 60.0f;
        } else {
            if (withSeconds) {
                time = minutes / 60.0f;
            } else {
                time = minutes;
            }
        }
        return time;
    }

    public static long getNotifsUpdateIntervalInMillis(int interval) {
        long millisInAnHour = 60 * 60 * 1000;
        int hours;
        switch (interval) {
            case 1:
                hours = 1;
                break;
            case 2:
                hours = 6;
                break;
            case 3:
                hours = 12;
                break;
            case 4:
                hours = 24;
                break;
            case 5:
                hours = 48;
                break;
            case 6:
                hours = 96;
                break;
            case 7:
                hours = 168;
                break;
            default:
                hours = 24;
                break;
        }

        return hours * millisInAnHour;

    }

    public static Drawable getVectorDrawable(@NonNull Context context, @DrawableRes int drawable) {
        Drawable vectorDrawable;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                vectorDrawable = ContextCompat.getDrawable(context, drawable);
            } else {
                vectorDrawable = VectorDrawableCompat.create(context.getResources(), drawable, null);
                if (vectorDrawable != null) {
                    vectorDrawable = DrawableCompat.wrap(vectorDrawable);
                }
            }
        } catch (Resources.NotFoundException ex) {
            vectorDrawable = ContextCompat.getDrawable(context, R.drawable.iconshowcase_logo);
        }
        return vectorDrawable != null ? vectorDrawable : null;
    }

    /**
     * Methods for tasks
     */

    public static void copyFiles(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[2048];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        out.flush();
    }

    public static String getFilenameWithoutExtension(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static int clean(File file) {
        if (!file.exists()) return 0;
        int count = 0;
        if (file.isDirectory()) {
            File[] folderContent = file.listFiles();
            if (folderContent != null && folderContent.length > 0) {
                for (File fileInFolder : folderContent) {
                    count += clean(fileInFolder);
                }
            }
        }
        file.delete();
        return count;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static int getNavigationBarHeight(Activity activity) {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = activity.getResources().getBoolean(R.bool.isTablet) ? metrics.heightPixels :
                    activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ?
                            metrics.widthPixels : metrics.heightPixels;
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = activity.getResources().getBoolean(R.bool.isTablet) ? metrics.heightPixels :
                    activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ?
                            metrics.widthPixels : metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

    public static void sendFirebaseNotification(Context context, Class mainActivity,
                                                Map<String, String> data, String title, String content) {
        Intent intent = new Intent(context, mainActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (data != null) {
            if (data.size() > 0) {
                for (int i = 0; i < data.size(); i++) {
                    String[] dataValue = data.toString().replace("{", "").replace("}", "").split(",")[i].split("=");
                    Timber.d("Key: " + dataValue[0] + " Value: " + dataValue[1]);
                    intent.putExtra(dataValue[0], dataValue[1]);
                }
            }
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title)
                .setTicker(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setOngoing(false)
                .setColor(ThemeUtils.darkTheme ?
                        ContextCompat.getColor(context, jahirfiquitiva.iconshowcase.R.color.dark_theme_accent) :
                        ContextCompat.getColor(context, jahirfiquitiva.iconshowcase.R.color.light_theme_accent))
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}