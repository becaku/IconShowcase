package jahirfiquitiva.apps.iconshowcase.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.apps.iconshowcase.dialogs.FolderChooserDialog;
import jahirfiquitiva.apps.iconshowcase.dialogs.ISDialogs;
import jahirfiquitiva.apps.iconshowcase.fragments.base.PreferenceFragment;
import jahirfiquitiva.apps.iconshowcase.utilities.PermissionUtils;
import jahirfiquitiva.apps.iconshowcase.utilities.Preferences;
import jahirfiquitiva.apps.iconshowcase.utilities.ThemeUtils;

public class SettingsFragment extends PreferenceFragment implements PermissionUtils.OnPermissionResultListener {

    private static Preferences mPrefs;
    private static PackageManager p;
    private static ComponentName componentName;
    private static Preference WSL, data;
    private static String location, cacheSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = new Preferences(getActivity());

        mPrefs.setSettingsModified(false);

        if (mPrefs.getDownloadsFolder() != null) {
            location = mPrefs.getDownloadsFolder();
        } else {
            location = getString(R.string.walls_save_location,
                    Environment.getExternalStorageDirectory().getAbsolutePath());
        }

        cacheSize = fullCacheDataSize(getActivity().getApplicationContext());

        p = getActivity().getPackageManager();
        componentName = new ComponentName(getActivity(), ShowcaseActivity.class);

        addPreferencesFromResource(R.xml.preferences);

        PreferenceCategory uiCategory = (PreferenceCategory) findPreference("uiPreferences");
        CheckBoxPreference wallHeaderCheck = (CheckBoxPreference) findPreference("wallHeader");
        if (!ShowcaseActivity.WITH_USER_WALLPAPER_AS_TOOLBAR_HEADER) {
            uiCategory.removePreference(wallHeaderCheck);
        } else {
            wallHeaderCheck.setChecked(mPrefs.getWallpaperAsToolbarHeaderEnabled());
            wallHeaderCheck.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    mPrefs.setWallpaperAsToolbarHeaderEnabled(newValue.toString().equals("true"));
                    ShowcaseActivity.setupToolbarHeader(getActivity());
                    return true;
                }
            });
        }

        WSL = findPreference("wallsSaveLocation");
        WSL.setSummary(getResources().getString(R.string.pref_summary_wsl, location));

        // Set the preference for current selected theme
        findPreference("themes").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ShowcaseActivity.settingsDialog = ISDialogs.showThemeChooserDialog(getActivity());
                ShowcaseActivity.settingsDialog.show();
                return true;
            }
        });

        // Set the preference for colored nav bar on Lollipop
        final CheckBoxPreference coloredNavBar = (CheckBoxPreference) findPreference("coloredNavBar");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            coloredNavBar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    mPrefs.setSettingsModified(true);
                    if (newValue.toString().equals("true")) {
                        ThemeUtils.changeNavBar(getActivity(), ThemeUtils.NAVBAR_DEFAULT);
                    } else {
                        ThemeUtils.changeNavBar(getActivity(), ThemeUtils.NAVBAR_BLACK);
                    }
                    return true;
                }
            });
        } else {
            uiCategory.removePreference(coloredNavBar);
        }

        CheckBoxPreference animations = (CheckBoxPreference) findPreference("animations");
        animations.setChecked(mPrefs.getAnimationsEnabled());
        animations.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mPrefs.setAnimationsEnabled(newValue.toString().equals("true"));
                return true;
            }
        });

        data = findPreference("clearData");
        data.setSummary(getResources().getString(R.string.pref_summary_cache, cacheSize));
        data.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MaterialDialog.SingleButtonCallback positiveCallback = new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        clearApplicationDataAndCache(getActivity());
                        changeValues(getActivity());
                    }
                };
                ISDialogs.showClearCacheDialog(getActivity(), positiveCallback);
                return true;
            }
        });

        findPreference("wallsSaveLocation").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (!PermissionUtils.canAccessStorage(getContext())) {
                    PermissionUtils.requestStoragePermission(getActivity(), SettingsFragment.this);
                } else {
                    showFolderChooserDialog();
                }
                return true;
            }
        });

        final CheckBoxPreference hideIcon = (CheckBoxPreference) findPreference("launcherIcon");
        if (mPrefs.getLauncherIconShown()) {
            hideIcon.setChecked(false);
        }
        hideIcon.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.toString().equals("true")) {
                    MaterialDialog.SingleButtonCallback positive = new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                            if (mPrefs.getLauncherIconShown()) {
                                mPrefs.setIconShown(false);
                                p.setComponentEnabledSetting(componentName,
                                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                        PackageManager.DONT_KILL_APP);
                            }

                            hideIcon.setChecked(true);
                        }
                    };

                    MaterialDialog.SingleButtonCallback negative = new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                            hideIcon.setChecked(false);
                        }
                    };

                    DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (mPrefs.getLauncherIconShown()) {
                                hideIcon.setChecked(false);
                            }
                        }
                    };

                    ShowcaseActivity.settingsDialog = ISDialogs.showHideIconDialog(getActivity(), positive, negative, dismissListener);

                } else {
                    if (!mPrefs.getLauncherIconShown()) {

                        mPrefs.setIconShown(true);
                        p.setComponentEnabledSetting(componentName,
                                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                PackageManager.DONT_KILL_APP);

                    }
                }
                return true;
            }
        });

    }

    public static void changeValues(Context context) {
        if (mPrefs.getDownloadsFolder() != null) {
            location = mPrefs.getDownloadsFolder();
        } else {
            location = context.getString(R.string.walls_save_location,
                    Environment.getExternalStorageDirectory().getAbsolutePath());
        }
        WSL.setSummary(context.getResources().getString(R.string.pref_summary_wsl, location));
        cacheSize = fullCacheDataSize(context);
        data.setSummary(context.getResources().getString(R.string.pref_summary_cache, cacheSize));

    }

    private static String fullCacheDataSize(Context context) {
        String finalSize;

        long cache = 0;
        long extCache = 0;
        double finalResult, mbFinalResult;

        File[] fileList = context.getCacheDir().listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                cache += dirSize(fileList[i]);
            } else {
                cache += fileList[i].length();
            }
        }
        try {
            File[] fileExtList = context.getExternalCacheDir().listFiles();
            for (int j = 0; j < fileExtList.length; j++) {
                if (fileExtList[j].isDirectory()) {
                    extCache += dirSize(fileExtList[j]);
                } else {
                    extCache += fileExtList[j].length();
                }
            }
        } catch (NullPointerException npe) {
            Log.d("CACHE", Log.getStackTraceString(npe));
        }

        finalResult = (cache + extCache) / 1000;

        if (finalResult > 1001) {
            mbFinalResult = finalResult / 1000;
            finalSize = String.format("%.2f", mbFinalResult) + " MB";
        } else {
            finalSize = String.format("%.2f", finalResult) + " KB";
        }

        return finalSize;
    }

    private static long dirSize(File dir) {
        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    result += dirSize(fileList[i]);
                } else {
                    result += fileList[i].length();
                }
            }
            return result;
        }
        return 0;
    }

    public static void clearCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
        }
    }

    public static void clearApplicationDataAndCache(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
        clearCache(context);
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit();
        mPrefs.setEasterEggEnabled(false);
        mPrefs.setIconShown(true);
        mPrefs.setDownloadsFolder(null);
        mPrefs.setRequestsDialogDismissed(false);
        mPrefs.setApplyDialogDismissed(false);
        mPrefs.setWallsDialogDismissed(false);
        ThemeUtils.restartActivity((Activity) context);
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private void showFolderChooserDialog() {
        new FolderChooserDialog().show((AppCompatActivity) getActivity());
    }

    @Override
    public void onStoragePermissionGranted() {
        ((ShowcaseActivity) getActivity()).openFileChooser();
    }
}