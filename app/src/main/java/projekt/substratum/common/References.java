/*
 * Copyright (c) 2016-2017 Projekt Substratum
 * This file is part of Substratum.
 *
 * Substratum is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Substratum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Substratum.  If not, see <http://www.gnu.org/licenses/>.
 */

package projekt.substratum.common;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.omnirom.substratum.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.security.auth.x500.X500Principal;

import projekt.substratum.activities.launch.ThemeLaunchActivity;
import projekt.substratum.common.platform.ThemeInterfacerService;
import projekt.substratum.services.system.InterfacerAuthorizationReceiver;
import projekt.substratum.util.files.IOUtils;
import projekt.substratum.util.readers.ReadVariantPrioritizedColor;

public class References {

    public static final Boolean ENABLE_AOPT_OUTPUT = false; // WARNING, DEVELOPERS - BREAKS COMPILE
    public static final Boolean ENABLE_PACKAGE_LOGGING = true; // Show time/date/place of install
    public static final Boolean ENABLE_DIRECT_ASSETS_LOGGING = false; // Self explanatory
    public static final Boolean BYPASS_ALL_VERSION_CHECKS = false; // For developer previews only!
    public static final Boolean BYPASS_SUBSTRATUM_BUILDER_DELETION = false; // Do not delete cache?
    @SuppressWarnings("WeakerAccess")
    // These are specific log tags for different classes
    public static final String SUBSTRATUM_BUILDER = "SubstratumBuilder";
    public static final String SUBSTRATUM_LOG = "SubstratumLogger";
    public static final String INTERFACER_PACKAGE = "projekt.interfacer";
    public static final String INTERFACER_SERVICE = INTERFACER_PACKAGE + ".services.JobService";
    // Specific intent for receiving completion status on backend
    public static final String INTERFACER_BINDED = INTERFACER_PACKAGE + ".INITIALIZE";
    public static final String STATUS_CHANGED = INTERFACER_PACKAGE + ".STATUS_CHANGED";
    // App intents to send
    public static final String KEY_RETRIEVAL = "Substratum.KeyRetrieval";
    public static final String TEMPLATE_THEME_MODE = "projekt.substratum.THEME";
    public static final String TEMPLATE_GET_KEYS = "projekt.substratum.GET_KEYS";
    // Delays for Icon Pack Handling
    public static final int MAIN_WINDOW_REFRESH_DELAY = 2000;
    public static final int FIRST_WINDOW_REFRESH_DELAY = 1000;
    public static final int SECOND_WINDOW_REFRESH_DELAY = 3000;
    // These strings control the current filter for themes
    public static final String metadataName = "Substratum_Name";
    public static final String metadataAuthor = "Substratum_Author";
    public static final String metadataEmail = "Substratum_Email";
    public static final String metadataLegacy = "Substratum_Legacy";
    public static final String metadataEncryption = "Substratum_Encryption";
    public static final String metadataEncryptionValue = "onCompileVerify";
    public static final String metadataWallpapers = "Substratum_Wallpapers";
    public static final String metadataOverlayDevice = "Substratum_Device";
    public static final String metadataOverlayParent = "Substratum_Parent";
    public static final String metadataOverlayTarget = "Substratum_Target";
    public static final String metadataOverlayType1a = "Substratum_Type1a";
    public static final String metadataOverlayType1b = "Substratum_Type1b";
    public static final String metadataOverlayType1c = "Substratum_Type1c";
    public static final String metadataOverlayType2 = "Substratum_Type2";
    public static final String metadataOverlayType3 = "Substratum_Type3";
    public static final String metadataOverlayVersion = "Substratum_Version";
    public static final String metadataIconPackParent = "Substratum_IconPack";
    public static final String metadataOverlayVersionOmni = "Substratum_Version_Omni";

    public static final String overlaysFragment = "overlays";
    public static final String wallpaperFragment = "wallpapers";
    // These strings control the directories that Substratum uses
    public static final String EXTERNAL_STORAGE_CACHE = "/.substratum/";
    public static final String SUBSTRATUM_BUILDER_CACHE = "/SubstratumBuilder/";
    // Notification Channel
    public static final String DEFAULT_NOTIFICATION_CHANNEL_ID = "default";
    public static final String ONGOING_NOTIFICATION_CHANNEL_ID = "ongoing";
    private static final String TEMPLATE_RECEIVE_KEYS = "projekt.substratum.RECEIVE_KEYS";
    private static final String SUBSTRATUM_THEME = "projekt.substratum.THEME";
    private static final String SUBSTRATUM_LAUNCHER_CLASS = ".SubstratumLauncher";
    private static final String SUBSTRATUM_LAUNCHER_CLASS_PATH =
            "substratum.theme.template.SubstratumLauncher";
    // Metadata used in theme templates to denote specific parts of a theme
    private static final String metadataVersion = "Substratum_Plugin";
    private static final String metadataThemeReady = "Substratum_ThemeReady";
    private static final String resourceChangelog = "ThemeChangelog";
    // This string controls the hero image name
    private static final String heroImageResourceName = "heroimage";
    // This int controls the notification identifier
    public static int firebase_notification_id = 24862486;
    public static int notification_id = 2486;
    public static int notification_id_upgrade = 248600;
    // This int controls the delay for window refreshes to occur
    public static int REFRESH_WINDOW_DELAY = 500;
    // This int controls the default priority level for legacy overlays
    public static int DEFAULT_PRIORITY = 50;
    // These strings control package names for system apps
    public static String settingsPackageName = "com.android.settings";
    public static String settingsSubstratumDrawableName = "ic_settings_substratum";
    // These values control the dynamic certification of substratum
    private static Boolean uncertified = null;
    private static int hashValue;

    public static void registerBroadcastReceivers(Context context) {
        try {
            IntentFilter interfacerAuthorize = new IntentFilter(
                    INTERFACER_PACKAGE + ".CALLER_AUTHORIZED");
            context.getApplicationContext().registerReceiver(
                    new InterfacerAuthorizationReceiver(), interfacerAuthorize);

            Log.d(SUBSTRATUM_LOG,
                    "Successfully registered broadcast receivers for Substratum functionality!");
        } catch (Exception e) {
            Log.e(SUBSTRATUM_LOG,
                    "Failed to register broadcast receivers for Substratum functionality...");
        }
    }

    public static int getDeviceEncryptionStatus(Context context) {
        // 0: ENCRYPTION_STATUS_UNSUPPORTED
        // 1: ENCRYPTION_STATUS_INACTIVE
        // 2: ENCRYPTION_STATUS_ACTIVATING
        // 3: ENCRYPTION_STATUS_ACTIVE_DEFAULT_KEY
        // 4: ENCRYPTION_STATUS_ACTIVE
        // 5: ENCRYPTION_STATUS_ACTIVE_PER_USER
        int status = DevicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED;
        final DevicePolicyManager dpm = (DevicePolicyManager)
                context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (dpm != null) status = dpm.getStorageEncryptionStatus();
        return status;
    }

    // This method is used to place the Substratum Rescue archives if they are not present
    public static void injectRescueArchives(Context context) {
        File storageDirectory = new File(Environment.getExternalStorageDirectory(), "/substratum/");
        if (!storageDirectory.exists() && !storageDirectory.mkdirs()) {
            Log.e(SUBSTRATUM_LOG, "Unable to create storage directory");
        }
        File rescueFile = new File(
                Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "substratum" +
                        File.separator + "SubstratumRescue.zip");
        File rescueFileLegacy = new File(
                Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "substratum" +
                        File.separator + "SubstratumRescue_Legacy.zip");
        if (!rescueFile.isFile()) {
            copyRescueFile(context, "rescue.dat",
                    Environment.getExternalStorageDirectory().getAbsolutePath() +
                            File.separator + "substratum" +
                            File.separator + "SubstratumRescue.zip");
        }
        if (!rescueFileLegacy.isFile()) {
            copyRescueFile(context, "rescue_legacy.dat",
                    Environment.getExternalStorageDirectory().getAbsolutePath() +
                            File.separator + "substratum" +
                            File.separator + "SubstratumRescue_Legacy.zip");
        }
    }

    private static void copyRescueFile(Context context, String sourceFileName, String
            destFileName) {
        AssetManager assetManager = context.getAssets();

        File destFile = new File(destFileName);
        File destParentDir = destFile.getParentFile();
        if (!destParentDir.exists() && !destParentDir.mkdir()) {
            Log.e(SUBSTRATUM_LOG,
                    "Unable to create directories for rescue archive dumps.");
        }

        try (
                InputStream in = assetManager.open(sourceFileName);
                OutputStream out = new FileOutputStream(destFile)
        ) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method is used to determine whether there the system is initiated with OMS
    public static Boolean checkOMS(@NonNull Context context) {
        //noinspection ConstantConditions
        if (context == null) return true; // Safe to assume that window refreshes only on OMS
        if (!BYPASS_ALL_VERSION_CHECKS) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            if (!prefs.contains("oms_state")) {
                setAndCheckOMS(context);
            }
            return prefs.getBoolean("oms_state", false);
        } else {
            return false;
        }
    }

    public static void setAndCheckOMS(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().remove("oms_state").apply();
        try {
            boolean foundOms = false;
            if (checkThemeInterfacer(context)) {
                foundOms = true;
            }
            if (foundOms ) {
                prefs.edit().putBoolean("oms_state", true).apply();
                prefs.edit().putInt("oms_version", 7).apply();
                Log.d(SUBSTRATUM_LOG, "Initializing Substratum with the seventh " +
                        "iteration of the Overlay Manager Service...");
            } else {
                prefs.edit().putBoolean("oms_state", false).apply();
                prefs.edit().putInt("oms_version", 0).apply();
                Log.d(SUBSTRATUM_LOG, "Initializing Substratum with the second " +
                        "iteration of the Resource Runtime Overlay system...");
            }
        } catch (Exception e) {
            prefs.edit().putBoolean("oms_state", false).apply();
            prefs.edit().putInt("oms_version", 0).apply();
            Log.d(SUBSTRATUM_LOG, "Initializing Substratum with the second " +
                    "iteration of the Resource Runtime Overlay system...");
        }
    }

    public static boolean checkSubstratumFeature(Context context) {
        // Using lowercase because that's how we defined it in our permissions xml
        return context.getPackageManager().hasSystemFeature(SUBSTRATUM_THEME.toLowerCase());
    }

    // This method is used to determine whether there the system was dirty flashed / upgraded
    public static Boolean checkROMVersion(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!prefs.contains("build_date")) {
            setROMVersion(context, false);
        }
        return prefs.getInt("build_date", 0) ==
                Integer.parseInt(getProp("ro.build.date.utc"));
    }

    public static void setROMVersion(Context context, boolean force) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!prefs.contains("build_date") || force) {
            prefs.edit().putInt("build_date",
                    Integer.parseInt(getProp("ro.build.date.utc")))
                    .apply();
        }
    }

    // This method is used to obtain the device ID of the current device (set up)
    @SuppressLint("HardwareIds")
    public static String getDeviceID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    // This method is used to check whether a build.prop value is found
    public static String getProp(String propName) {
        Process p = null;
        String result = "";
        try {
            p = new ProcessBuilder("/system/bin/getprop", propName)
                    .redirectErrorStream(true).start();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream())
            )) {
                String line;
                while ((line = br.readLine()) != null) {
                    result = line;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
        return result;
    }

    // This method is used to check whether a build.prop value is found
    public static StringBuilder getBuildProp() {
        Process p = null;
        StringBuilder result = new StringBuilder();
        try {
            p = new ProcessBuilder("/system/bin/getprop").redirectErrorStream(true).start();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream())
            )) {
                String line;
                while ((line = br.readLine()) != null) {
                    result.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
        return result;
    }

    // This method checks whether there is any network available for Wallpapers
    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // This method checks whether fonts is supported by the system
    public static boolean isFontsSupported() {
        try {
            Class<?> cls = Class.forName("android.graphics.Typeface");
            cls.getDeclaredMethod("getSystemFontDirLocation");
            cls.getDeclaredMethod("getThemeFontConfigLocation");
            cls.getDeclaredMethod("getThemeFontDirLocation");
            Log.d(References.SUBSTRATUM_LOG, "This system fully supports font hotswapping.");
            return true;
        } catch (Exception ex) {
            // Suppress Fonts
        }
        return false;
    }

    // This string array contains all the SystemUI acceptable overlay packs
    public static Boolean allowedSystemUIOverlay(String current) {
        return Arrays.asList(Resources.ALLOWED_SYSTEMUI_ELEMENTS).contains(current);
    }

    // This string array contains all the Settings acceptable overlay packs
    public static Boolean allowedSettingsOverlay(String current) {
        return Arrays.asList(Resources.ALLOWED_SETTINGS_ELEMENTS).contains(current);
    }

    // This string array contains all blacklisted app for theme
    public static Boolean allowedAppOverlay(String targetValue) {
        return !Arrays.asList(Resources.BLACKLIST_THEME_TARGET_APPS).contains(targetValue);
    }

    // This string array contains all the legacy allowed folders
    public static Boolean checkIconPackNotAllowed(String targetValue) {
        return Arrays.asList(Resources.BLACKLIST_STUDIO_TARGET_APPS).contains(targetValue);
    }

    // This method determines whether a specified package is installed
    public static boolean isPackageInstalled(Context context, String package_name) {
        return isPackageInstalled(context, package_name, true);
    }

    // This method checks if a ComponentInfo is valid
    public static boolean isIntentValid(Context context, Intent intent) {
        List<ResolveInfo> list =
                context.getPackageManager().queryIntentActivities(
                        intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    // This method determines whether a specified package is installed (enabled OR disabled)
    public static boolean isPackageInstalled(
            Context context,
            String package_name,
            boolean enabled) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(package_name, 0);
            PackageManager pm = context.getPackageManager();
            pm.getPackageInfo(package_name, PackageManager.GET_ACTIVITIES);
            if (enabled) return ai.enabled;
            // if package doesn't exist, an Exception will be thrown, so return true in every case
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // This method determines the installed directory of the overlay for legacy mode
    public static String getInstalledDirectory(Context context, String package_name) {
        PackageManager pm = context.getPackageManager();
        for (ApplicationInfo app : pm.getInstalledApplications(0)) {
            if (app.packageName.equals(package_name)) {
                // The way this works is that Android will traverse within the SYMLINK and not the
                // actual directory. e.g.:
                // rm -r /vendor/overlay/com.android.systemui.navbars.Mono.apk (ON NEXUS FILTER)
                return app.sourceDir;
            }
        }
        return null;
    }

    // Check if a service is running from our app
    public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // This method validates the resources by their name in a specific package
    public static Boolean validateResource(Context mContext, String package_Name,
                                           String resource_name, String resource_type) {
        try {
            Context context = mContext.createPackageContext(package_Name, 0);
            android.content.res.Resources resources = context.getResources();
            int drawablePointer = resources.getIdentifier(
                    resource_name, // Drawable name explicitly defined
                    resource_type, // Declared icon is a drawable, indeed.
                    package_Name); // Icon pack package name
            return drawablePointer != 0;
        } catch (Exception e) {
            return false;
        }
    }

    // This method converts a vector drawable into a bitmap object
    public static Bitmap getBitmapFromVector(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    // This method obtains the application icon for a specified package
    public static Drawable grabAppIcon(Context context, String package_name) {
        try {
            Drawable icon;
            if (allowedSystemUIOverlay(package_name)) {
                icon = context.getPackageManager().getApplicationIcon("com.android.systemui");
            } else if (allowedSettingsOverlay(package_name)) {
                icon = context.getPackageManager().getApplicationIcon("com.android.settings");
            } else {
                icon = context.getPackageManager().getApplicationIcon(package_name);
            }
            return icon;
        } catch (Exception e) {
            // Suppress warning
        }
        return context.getDrawable(R.drawable.default_overlay_icon);
    }

    // This method obtains the overlay parent icon for specified package, returns self package icon
    // if not found
    public static Drawable grabOverlayParentIcon(Context context, String package_name) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    package_name, PackageManager.GET_META_DATA);
            if (appInfo.metaData != null &&
                    appInfo.metaData.getString(metadataOverlayParent) != null) {
                return grabAppIcon(context, appInfo.metaData.getString(metadataOverlayParent));
            }
        } catch (Exception e) {
            // Suppress warning
        }
        return grabAppIcon(context, package_name);
    }

    public static List<ResolveInfo> getThemes(Context context) {
        // Scavenge through the packages on the device with specific substratum metadata in
        // their manifest
        PackageManager packageManager = context.getPackageManager();
        return packageManager.queryIntentActivities(new Intent(SUBSTRATUM_THEME),
                PackageManager.GET_META_DATA);
    }

    public static ArrayList<String> getThemesArray(Context context) {
        ArrayList<String> returnArray = new ArrayList<>();
        List<ResolveInfo> themesResolveInfo = getThemes(context);
        for (int i = 0; i < themesResolveInfo.size(); i++) {
            returnArray.add(themesResolveInfo.get(i).activityInfo.packageName);
        }
        return returnArray;
    }

    @SuppressWarnings("unchecked")
    public static HashMap getIconState(Context mContext, @NonNull String packageName) {
        /*
          Returns a HashMap in a specific order, of which the key would be the activityName
          that is most likely a perfect match in what icon we want to be overlaying. A check should
          be made to ensure this specific activity is the one being overlaid.

          The object will be an ArrayList of icon directories where the icon occurs inside the
          to-be-themed target. For example "res/mipmap-xxxhdpi/ic_launcher.png".
         */
        Process process = null;
        DataOutputStream outputStream = null;
        BufferedReader reader = null;
        try {
            ApplicationInfo ai =
                    mContext.getPackageManager().getApplicationInfo(packageName, 0);
            process = Runtime.getRuntime().exec(mContext.getFilesDir().getAbsolutePath() +
                    "/aopt d badging " + ai.sourceDir);

            outputStream = new DataOutputStream(process.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            HashMap hashMap = new HashMap<>();
            ArrayList<String> iconArray = new ArrayList();
            Boolean has_passed_icons = false;

            ArrayList<String> lines = new ArrayList<>();
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith("application-icon")) {
                    String appIcon = lines.get(i).split(":")[1];
                    appIcon = appIcon.replace("'", "");
                    appIcon = appIcon.replace("-v4", "");
                    if (!iconArray.contains(appIcon)) {
                        // Do not contain duplicates in AOPT report, such as 65534-65535
                        iconArray.add(appIcon);
                        has_passed_icons = true;
                    }
                } else if (lines.get(i).startsWith("launchable-activity") && !has_passed_icons) {
                    String appIcon = lines.get(i);
                    appIcon = appIcon.substring(appIcon.lastIndexOf("=") + 1);
                    appIcon = appIcon.replace("'", ""); // Strip the quotes
                    appIcon = appIcon.replace("-v4", ""); // Make it to a non-API dependency
                    if (!iconArray.contains(appIcon)) {
                        iconArray.add(appIcon);
                        has_passed_icons = true;
                    }
                }
            }
            if (has_passed_icons) {
                hashMap.put(packageName, iconArray);
                // Once we reach this point, we have concluded the map assignation
                return hashMap;
            }

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.writeBytes("exit\n");
                    outputStream.flush();
                    outputStream.close();
                }

                if (reader != null) {
                    reader.close();
                }

                if (process != null) {
                    process.destroy();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static String getPackageIconName(Context mContext, @NonNull String packageName) {
        /*
          Returns the name of the icon in the package

          The object will be an ArrayList of icon directories where the icon occurs inside the
          to-be-themed target. For example "res/mipmap-xxxhdpi/ic_launcher.png".
         */
        Process process = null;
        DataOutputStream outputStream = null;
        BufferedReader reader = null;
        try {
            ApplicationInfo ai =
                    mContext.getPackageManager().getApplicationInfo(packageName, 0);
            process = Runtime.getRuntime().exec(
                    mContext.getFilesDir().getAbsolutePath() + "/aopt d badging " + ai.sourceDir);

            outputStream = new DataOutputStream(process.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String s;
            while ((s = reader.readLine()) != null) {
                if (s.contains("application-icon")) {
                    String appIcon = s.split(":")[1];
                    appIcon = appIcon.substring(1, appIcon.length() - 1).replace("-v4", "");
                    return appIcon.split("/")[2].substring(0, appIcon.split("/")[2].length() - 4);
                }
            }
            process.waitFor();
        } catch (Exception e) {
            // At this point we could simply show that there is no app icon in the package
            // e.g. DocumentsUI
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.writeBytes("exit\n");
                    outputStream.flush();
                    outputStream.close();
                }

                if (reader != null) {
                    reader.close();
                }

                if (process != null) {
                    process.destroy();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "ic_launcher";
    }

    // Run shell command and return a StringBuilder of the output
    @SuppressWarnings("SameParameterValue")
    public static StringBuilder runShellCommand(String input) {
        try {
            Process shell = Runtime.getRuntime().exec(input);
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(shell.getInputStream()));

            StringBuilder returnString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                returnString.append(line).append("\n");
            }
            return returnString;
        } catch (Exception e) {
            // Suppress warning
        }
        return null;
    }

    // PackageName Crawling Methods
    public static String grabAppVersion(Context mContext, String package_name) {
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(package_name, 0);
            return pInfo.versionName;
        } catch (Exception e) {
            // Suppress warning
        }
        return null;
    }

    public static int grabAppVersionCode(Context mContext, String packageName) {
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(packageName, 0);
            return pInfo.versionCode;
        } catch (Exception e) {
            // Suppress warning
        }
        return 0;
    }

    public static String grabThemeVersion(Context mContext, String package_name) {
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(package_name, 0);
            return pInfo.versionName + " (" + pInfo.versionCode + ")";
        } catch (PackageManager.NameNotFoundException e) {
            // Suppress warning
        }
        return null;
    }

    public static String grabThemeAPIs(Context mContext, String package_name) {
        try {
            ApplicationInfo appInfo = mContext.getPackageManager().getApplicationInfo(
                    package_name, PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                try {
                    if (appInfo.minSdkVersion == appInfo.targetSdkVersion) {
                        int target = appInfo.targetSdkVersion;
                        if (target == 23) {
                            return mContext.getString(R.string.api_23);
                        } else if (target == 24) {
                            return mContext.getString(R.string.api_24);
                        } else if (target == 25) {
                            return mContext.getString(R.string.api_25);
                        } else if (target == 26) {
                            return mContext.getString(R.string.api_26);
                        }
                    } else {
                        String minSdk = "";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            int min = appInfo.minSdkVersion;
                            if (min == 21) {
                                minSdk = mContext.getString(R.string.api_21);
                            } else if (min == 22) {
                                minSdk = mContext.getString(R.string.api_22);
                            } else if (min == 23) {
                                minSdk = mContext.getString(R.string.api_23);
                            } else if (min == 24) {
                                minSdk = mContext.getString(R.string.api_24);
                            } else if (min == 25) {
                                minSdk = mContext.getString(R.string.api_25);
                            } else if (min == 26) {
                                minSdk = mContext.getString(R.string.api_26);
                            }
                        } else {
                            // At this point, it is under API24 (API warning) thus we'll do an
                            // educated guess here.
                            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                                minSdk = mContext.getString(R.string.api_21);
                            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) {
                                minSdk = mContext.getString(R.string.api_22);
                            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                                minSdk = mContext.getString(R.string.api_23);
                            }
                        }
                        String targetSdk = "";
                        int target = appInfo.targetSdkVersion;
                        if (target == 23) {
                            targetSdk = mContext.getString(R.string.api_23);
                        } else if (target == 24) {
                            targetSdk = mContext.getString(R.string.api_24);
                        } else if (target == 25) {
                            targetSdk = mContext.getString(R.string.api_25);
                        } else if (target == 26) {
                            targetSdk = mContext.getString(R.string.api_26);
                        }
                        return minSdk + " - " + targetSdk;
                    }
                } catch (NoSuchFieldError noSuchFieldError) {
                    // The device is API 23 if it throws a NoSuchFieldError
                    if (appInfo.targetSdkVersion == 23) {
                        return mContext.getString(R.string.api_23);
                    } else {
                        String targetAPI = "";
                        int target = appInfo.targetSdkVersion;
                        if (target == 24) {
                            targetAPI = mContext.getString(R.string.api_24);
                        } else if (target == 25) {
                            targetAPI = mContext.getString(R.string.api_25);
                        } else if (target == 26) {
                            targetAPI = mContext.getString(R.string.api_26);
                        }
                        return mContext.getString(R.string.api_23) + " - " + targetAPI;
                    }
                }
            }
        } catch (Exception e) {
            // Suppress warning
        }
        return null;
    }

    // Grab specified metadats
    public static String getOverlayMetadata(
            Context mContext,
            String package_name,
            String metadata) {
        try {
            ApplicationInfo appInfo = mContext.getPackageManager().getApplicationInfo(
                    package_name, PackageManager.GET_META_DATA);
            if (appInfo.metaData != null && appInfo.metaData.getString(metadata) != null) {
                return appInfo.metaData.getString(metadata);
            }
        } catch (Exception e) {
            // Suppress warning
        }
        return null;
    }

    // Grab specified metadats
    public static Boolean getOverlayMetadata(
            Context mContext,
            String package_name,
            String metadata,
            Boolean defaultValue) {
        try {
            ApplicationInfo appInfo = mContext.getPackageManager().getApplicationInfo(
                    package_name, PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                return appInfo.metaData.getBoolean(metadata);
            }
        } catch (Exception e) {
            // Suppress warning
        }
        return defaultValue;
    }

    // Grab specified metadats
    public static int getOverlaySubstratumVersion(
            Context mContext,
            String package_name,
            String metadata) {
        try {
            ApplicationInfo appInfo = mContext.getPackageManager().getApplicationInfo(
                    package_name, PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                return appInfo.metaData.getInt(metadata);
            }
        } catch (Exception e) {
            // Suppress warning
        }
        return 0;
    }

    // Grab any resource from any package
    private static int getResource(Context mContext,
                                   String package_name,
                                   String resourceName,
                                   String type) {
        try {
            android.content.res.Resources res =
                    mContext.getPackageManager().getResourcesForApplication(package_name);
            return res.getIdentifier(
                    package_name + ":" + type + "/" + resourceName,
                    type,
                    package_name);
        } catch (Exception e) {
            // Suppress warning
        }
        return 0;
    }

    // Grab Color Resource
    public static int grabColorResource(Context mContext, String package_name, String colorName) {
        return getResource(mContext, package_name, colorName, "color");
    }

    // Grab Theme Changelog
    public static String[] grabThemeChangelog(Context mContext, String package_name) {
        try {
            android.content.res.Resources res =
                    mContext.getPackageManager().getResourcesForApplication(package_name);
            int array_position = getResource(mContext, package_name, resourceChangelog, "array");
            return res.getStringArray(array_position);
        } catch (Exception e) {
            // Suppress warning
        }
        return null;
    }

    // Grab Theme Hero Image
    public static Drawable grabPackageHeroImage(Context mContext, String package_name) {
        android.content.res.Resources res;
        Drawable hero = mContext.getDrawable(android.R.color.transparent); // Initialize to be clear
        try {
            res = mContext.getPackageManager().getResourcesForApplication(package_name);
            int resourceId = res.getIdentifier(
                    package_name + ":drawable/" + heroImageResourceName, null, null);
            if (0 != resourceId) {
                hero = mContext.getPackageManager().getDrawable(package_name, resourceId, null);
            }
            return hero;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hero;
    }

    // Grab Overlay Target Package Name (Human Readable)
    public static String grabPackageName(Context mContext, String package_name) {
        PackageManager pm = mContext.getPackageManager();
        ApplicationInfo ai;
        try {
            switch (package_name) {
                case "com.android.systemui.navbars":
                    return mContext.getString(R.string.systemui_navigation);
                case "com.android.systemui.headers":
                    return mContext.getString(R.string.systemui_headers);
                case "com.android.systemui.tiles":
                    return mContext.getString(R.string.systemui_qs_tiles);
                case "com.android.systemui.statusbars":
                    return mContext.getString(R.string.systemui_statusbar);
                case "com.android.settings.icons":
                    return mContext.getString(R.string.settings_icons);
            }
            ai = pm.getApplicationInfo(package_name, 0);
        } catch (Exception e) {
            ai = null;
        }
        return (String) (ai != null ? pm.getApplicationLabel(ai) : null);
    }

    // Grab Theme Ready Metadata
    public static String grabThemeReadyVisibility(Context mContext, String package_name) {
        return getOverlayMetadata(mContext, package_name, metadataThemeReady);
    }

    // Grab Theme Plugin Metadata
    public static String grabPackageTemplateVersion(Context mContext, String package_name) {
        String template_version = getOverlayMetadata(mContext, package_name, metadataVersion);
        if (template_version != null) {
            return mContext.getString(R.string.plugin_template) + ": " + template_version;
        }
        return null;
    }

    // Grab Overlay Parent
    public static String grabOverlayParent(Context mContext, String package_name) {
        return getOverlayMetadata(mContext, package_name, metadataOverlayParent);
    }

    // Grab Overlay Target
    public static String grabOverlayTarget(Context mContext, String package_name) {
        return getOverlayMetadata(mContext, package_name, metadataOverlayTarget);
    }

    @SuppressLint("PackageManagerGetSignatures")
    private static Signature[] getSelfSignature(Context context) {
        Signature[] sigs = new Signature[0];
        try {
            sigs = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_SIGNATURES).signatures;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return sigs;
    }

    static int hashPassthrough(Context context) {
        if (hashValue != 0) {
            return hashValue;
        }
        try {
            @SuppressLint("PackageManagerGetSignatures")
            Signature[] sigs = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_SIGNATURES).signatures;
            for (Signature sig : sigs) {
                if (sig != null) {
                    hashValue = sig.hashCode();
                    return hashValue;
                }
            }
        } catch (PackageManager.NameNotFoundException nnfe) {
            nnfe.printStackTrace();
        }
        return 0;
    }

    // Check usage permissions
    public static boolean checkUsagePermissions(Context mContext) {
        try {
            PackageManager packageManager = mContext.getPackageManager();
            ApplicationInfo applicationInfo =
                    packageManager.getApplicationInfo(mContext.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager)
                    mContext.getSystemService(Context.APP_OPS_SERVICE);
            assert appOpsManager != null;
            int mode = appOpsManager.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid,
                    applicationInfo.packageName);
            return mode == AppOpsManager.MODE_ALLOWED;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    // Check if user application or not
    public static boolean isUserApp(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            int mask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
            return (ai.flags & mask) == 0;
        } catch (PackageManager.NameNotFoundException e) {
            // Suppress warning
        }
        return false;
    }

    static void sendLocalizedKeyMessage(Context context,
                                        byte[] encryption_key,
                                        byte[] iv_encrypt_key) {
        Log.d("KeyRetrieval",
                "The system has completed the handshake for keys retrieval " +
                        "and is now passing it to the activity...");
        Intent intent = new Intent(KEY_RETRIEVAL);
        intent.putExtra("encryption_key", encryption_key);
        intent.putExtra("iv_encrypt_key", iv_encrypt_key);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void sendRefreshMessage(Context context) {
        Log.d("ThemeFragmentRefresher",
                "A theme has been modified, sending update signal to refresh the list!");
        Intent intent = new Intent("ThemeFragment.REFRESH");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    // Launch intent for a theme
    public static void launchTheme(Context mContext,
                                   String package_name,
                                   String theme_mode) {
        Intent theme_intent = themeIntent(
                mContext,
                package_name,
                theme_mode,
                TEMPLATE_THEME_MODE);
        mContext.startActivity(theme_intent);
    }

    // Key return of a theme
    public static void grabThemeKeys(Context mContext, String package_name) {
        Intent theme_intent = themeIntent(
                mContext,
                package_name,
                null,
                TEMPLATE_GET_KEYS);
        try {
            mContext.startActivity(theme_intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startKeyRetrievalReceiver(Context context) {
        try {
            IntentFilter intentGetKeys = new IntentFilter(TEMPLATE_RECEIVE_KEYS);
            context.getApplicationContext().registerReceiver(
                    new KeyRetriever(), intentGetKeys);

            Log.d(SUBSTRATUM_LOG,
                    "Successfully registered key retrieval receiver!");
        } catch (Exception e) {
            Log.e(SUBSTRATUM_LOG,
                    "Failed to register key retrieval receiver...");
        }
    }

    public static Intent themeIntent(Context mContext,
                                     String package_name,
                                     String theme_mode,
                                     String actionIntent) {
        boolean should_debug = false;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (should_debug) Log.d("ThemeLauncher", "Creating new intent...");
        Intent intentActivity;
        if (actionIntent.equals(TEMPLATE_GET_KEYS)) {
            intentActivity = new Intent();
        } else {
            intentActivity = new Intent(mContext, ThemeLaunchActivity.class);
        }
        intentActivity.putExtra("package_name", package_name);
        if (should_debug) Log.d("ThemeLauncher", "Assigning action to intent...");
        intentActivity.setAction(actionIntent);
        if (should_debug) Log.d("ThemeLauncher", "Assigning package name to intent...");
        intentActivity.setPackage(package_name);
        intentActivity.putExtra("calling_package_name", mContext.getPackageName());
        if (should_debug) Log.d("ThemeLauncher", "Checking for theme system type...");
        intentActivity.putExtra("oms_check", !checkOMS(mContext));
        intentActivity.putExtra("theme_mode", theme_mode);
        intentActivity.putExtra("notification", false);
        if (should_debug) Log.d("ThemeLauncher", "Obtaining APK signature hash...");
        intentActivity.putExtra("hash_passthrough", hashPassthrough(mContext));
        if (should_debug) Log.d("ThemeLauncher", "Checking for certification...");
        intentActivity.putExtra("certified", prefs.getBoolean("complexion", true));
        if (should_debug) Log.d("ThemeLauncher", "Starting Activity...");
        return intentActivity;
    }

    // Begin check if device is running on the latest theme interface
    public static boolean checkThemeInterfacer(Context context) {
        if (context == null) { // If activity has already been destroyed context instance will be
            // null
            Log.e(SUBSTRATUM_LOG, "activity has been destroyed, cannot check if interfacer is " +
                    "used");
            return false;
        }
        return getThemeInterfacerPackage(context) != null;
    }

    public static boolean isBinderInterfacer(Context context) {
        PackageInfo packageInfo = getThemeInterfacerPackage(context);
        return packageInfo != null && packageInfo.versionCode >= 60;
    }

    // Obtain a live sample of the metadata in an app
    private static boolean getMetaData(Context context, String trigger) {
        List<ApplicationInfo> list =
                context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).packageName.startsWith(trigger)) {
                return true;
            }
        }
        return false;
    }

    // Obtain a live sample of the content providers in an app
    private static boolean getProviders(Context context, String trigger) {
        List<PackageInfo> list =
                context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).packageName.startsWith(trigger)) {
                return true;
            }
        }
        return false;
    }

    // Obtain a live sample of the intents in an app
    private static boolean getIntents(Context context, String trigger) {
        ArrayList<Intent> intentArray = new ArrayList<>();
        intentArray.add(new Intent(Intent.ACTION_BOOT_COMPLETED));
        intentArray.add(new Intent(Intent.ACTION_PACKAGE_ADDED));
        intentArray.add(new Intent(Intent.ACTION_PACKAGE_CHANGED));
        intentArray.add(new Intent(Intent.ACTION_PACKAGE_REPLACED));
        intentArray.add(new Intent(Intent.ACTION_PACKAGE_REMOVED));
        intentArray.add(new Intent(Intent.ACTION_MEDIA_SCANNER_FINISHED));
        intentArray.add(new Intent(Intent.ACTION_MEDIA_SCANNER_STARTED));
        intentArray.add(new Intent(Intent.ACTION_MEDIA_MOUNTED));
        intentArray.add(new Intent(Intent.ACTION_MEDIA_REMOVED));
        for (Intent intent : intentArray) {
            List<ResolveInfo> activities =
                    context.getPackageManager().queryBroadcastReceivers(intent, 0);
            for (ResolveInfo resolveInfo : activities) {
                ActivityInfo activityInfo = resolveInfo.activityInfo;
                if (activityInfo != null && activityInfo.name.startsWith(trigger)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static PackageInfo getThemeInterfacerPackage(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(INTERFACER_PACKAGE, 0);
        } catch (Exception e) {
            // Theme Interfacer was not installed
        }
        return null;
    }

    public static boolean needsRecreate(Context context, ArrayList<String> list) {
        for (String o : list) {
            if (o.equals("android") || o.equals("org.omnirom.substratum")) {
                return false;
            }
        }
        return checkOMS(context);
    }

    public static void uninstallPackage(Context context, String packageName) {
        if (checkThemeInterfacer(context)) {
            ArrayList<String> list = new ArrayList<>();
            list.add(packageName);
            ThemeInterfacerService.uninstallOverlays(context, list, false);
        }
    }

    // This method checks whether these are legitimate packages for Substratum
    @SuppressWarnings("unchecked")
    public static HashMap<String, String[]> getSubstratumPackages(Context context,
                                                                  String package_name,
                                                                  HashMap packages,
                                                                  String home_type,
                                                                  Boolean old_algorithm,
                                                                  String search_filter) {
        // This algorithm was used during 490 and below and runs at a speed where the number of
        // overlay packages installed would affect the theme reload time. We are keeping this to
        // retain the old filter to show pre-6.0.0 themes.
        if (old_algorithm) try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    package_name, PackageManager.GET_META_DATA);
            Context otherContext = context.createPackageContext(package_name, 0);
            AssetManager am = otherContext.getAssets();
            if (appInfo.metaData != null) {
                boolean can_continue = true;
                if (appInfo.metaData.getString(metadataName) != null &&
                        appInfo.metaData.getString(metadataAuthor) != null) {
                    if (search_filter != null && search_filter.length() > 0) {
                        String name = appInfo.metaData.getString(metadataName) + " " +
                                appInfo.metaData.getString(metadataAuthor);
                        can_continue = name.toLowerCase()
                                .contains(search_filter.toLowerCase());
                    }
                }
                if (!checkOMS(context)) {
                    if (!appInfo.metaData.getBoolean(metadataLegacy, true)) {
                        can_continue = false;
                    }
                }
                if (can_continue) {
                    if (appInfo.metaData.getString(metadataName) != null) {
                        if (appInfo.metaData.getString(metadataAuthor) != null) {
                            if (home_type.equals("wallpapers")) {
                                if (appInfo.metaData.getString(metadataWallpapers) != null) {
                                    String[] data = {appInfo.metaData.getString
                                            (metadataAuthor), package_name};
                                    packages.put(appInfo.metaData.getString(metadataName), data);
                                }
                            } else if (home_type.length() == 0) {
                                String[] data = {appInfo.metaData.getString
                                        (metadataAuthor), package_name};
                                packages.put(appInfo.metaData.getString(metadataName), data);
                            } else {
                                try {
                                    String[] stringArray = am.list("");
                                    if (Arrays.asList(stringArray).contains(home_type)) {
                                        String[] data = {appInfo.metaData.getString
                                                (metadataAuthor), package_name};
                                        packages.put(appInfo.metaData.getString
                                                (metadataName), data);
                                    }
                                } catch (Exception e) {
                                    Log.e(SUBSTRATUM_LOG,
                                            "Unable to find package identifier");
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Exception
        }
        else {
            // This algorithm was used during 499 and above runs at a speed where the number of
            // overlay packages installed DOES NOT affect the theme reload time.
            try {
                List<ResolveInfo> listOfThemes = getThemes(context);
                for (ResolveInfo ri : listOfThemes) {
                    String packageName = ri.activityInfo.packageName;
                    ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                            packageName, PackageManager.GET_META_DATA);

                    Boolean can_continue = true;
                    if (appInfo.metaData.getString(metadataName) != null &&
                            appInfo.metaData.getString(metadataAuthor) != null) {
                        if (search_filter != null && search_filter.length() > 0) {
                            String name = appInfo.metaData.getString(metadataName) +
                                    " " + appInfo.metaData.getString(metadataAuthor);
                            if (!name.toLowerCase().contains(
                                    search_filter.toLowerCase())) {
                                can_continue = false;
                            }
                        }
                    }

                    if (can_continue) {
                        Context otherContext = context.createPackageContext(packageName, 0);
                        AssetManager am = otherContext.getAssets();
                        if (home_type.equals(wallpaperFragment)) {
                            if (appInfo.metaData.getString(metadataWallpapers) != null) {
                                String[] data = {appInfo.metaData.getString
                                        (metadataAuthor),
                                        packageName};
                                packages.put(appInfo.metaData.getString(
                                        metadataName), data);
                            }
                        } else {
                            if (home_type.length() == 0) {
                                String[] data = {appInfo.metaData.getString
                                        (metadataAuthor), packageName};
                                packages.put(appInfo.metaData.getString(metadataName), data);
                            } else {
                                try {
                                    String[] stringArray = am.list("");
                                    if (Arrays.asList(stringArray).contains(home_type)) {
                                        String[] data = {appInfo.metaData.getString
                                                (metadataAuthor), packageName};
                                        packages.put(appInfo.metaData.getString
                                                (metadataName), data);
                                    }
                                } catch (Exception e) {
                                    Log.e(SUBSTRATUM_LOG,
                                            "Unable to find package identifier");
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Suppress warning
            }
        }
        return packages;
    }

    @SuppressWarnings("deprecation")
    public static String parseTime(Context context, int hour, int minute) {
        Locale locale;
        String parse;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }

        if (android.text.format.DateFormat.is24HourFormat(context)) {
            parse = String.format(locale, "%02d:%02d", hour, minute);
        } else {
            String AM_PM = hour <= 12 ? "AM" : "PM";
            hour = hour <= 12 ? hour : hour - 12;
            parse = String.format(locale, "%d:%02d " + AM_PM, hour, minute);
        }
        return parse;
    }

    // This method parses a specific overlay resource file (.xml) and returns the specified value
    public static String getOverlayResource(InputStream overlay) {
        String hex = null;

        // Try to clone the InputStream (WARNING: Might be an ugly hek)
        byte[] byteArray;
        try {
            byteArray = IOUtils.toByteArray(overlay);
        } catch (IOException e) {
            Log.e(SUBSTRATUM_LOG, "Unable to clone InputStream");
            return null;
        }

        try (
                InputStream clone1 = new ByteArrayInputStream(byteArray);
                InputStream clone2 = new ByteArrayInputStream(byteArray)
        ) {
            // Find the name of the top most color in the file first.
            String resource_name = new ReadVariantPrioritizedColor(clone1).run();

            if (resource_name != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(clone2))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.contains("\"" + resource_name + "\"")) {
                            String[] split = line.substring(line.lastIndexOf("\">") + 2).split("<");
                            hex = split[0];
                            if (hex.startsWith("?")) hex = "#00000000";
                        }
                    }
                } catch (IOException ioe) {
                    Log.e(SUBSTRATUM_LOG, "Unable to find " + resource_name + " in this overlay!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hex;
    }

    public static boolean isPackageDebuggable(Context context, String packageName) {
        X500Principal DEBUG_1 = new X500Principal("C=US,O=Android,CN=Android Debug");
        X500Principal DEBUG_2 = new X500Principal("CN=Android Debug,O=Android,C=US");
        boolean debuggable = false;

        try {
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo pinfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            Signature signatures[] = pinfo.signatures;
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            for (Signature signature : signatures) {
                ByteArrayInputStream stream = new ByteArrayInputStream(signature.toByteArray());
                X509Certificate cert = (X509Certificate) cf.generateCertificate(stream);
                debuggable = cert.getSubjectX500Principal().equals(DEBUG_1) ||
                        cert.getSubjectX500Principal().equals(DEBUG_2);
                if (debuggable) break;
            }
        } catch (PackageManager.NameNotFoundException | CertificateException e) {
            //cacheable variable will remain false
        }
        return debuggable;
    }

    // Save a text file from LogChar
    public static void writeLogCharFile(String packageName, String data) {
        try {
            Calendar c = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String formattedDate = df.format(c.getTime());

            File logcharFolder = new File(Environment.getExternalStorageDirectory() +
                    File.separator + "substratum" + File.separator + "LogChar Reports");
            if (!logcharFolder.exists() && logcharFolder.mkdirs()) {
                Log.d("LogChar Utility", "Created LogChar directory!");
            }

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
                    new File(logcharFolder.getAbsolutePath() + File.separator +
                            packageName + "_" + formattedDate + ".txt")));
            bufferedWriter.write(data);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class KeyRetriever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            sendLocalizedKeyMessage(
                    context,
                    intent.getByteArrayExtra("encryption_key"),
                    intent.getByteArrayExtra("iv_encrypt_key"));
        }
    }

    public static int grabOverlayOmniVersion(Context context, String package_name) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    package_name, PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                return appInfo.metaData.getInt(metadataOverlayVersionOmni);
            }
        } catch (Exception e) {
            // Suppress warning
        }
        return 0;
    }
}
