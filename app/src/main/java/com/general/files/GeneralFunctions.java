package com.general.files;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.app85taxi.passenger.LauncherActivity;
import com.app85taxi.passenger.MainActivity;
import com.app85taxi.passenger.R;
import com.app85taxi.passenger.VerifyInfoActivity;
import com.drawRoute.DirectionsJSONParser;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.utils.CommonUtilities;
import com.utils.ScalingUtilities;
import com.utils.Utils;
import com.view.ErrorView;
import com.view.GenerateAlertBox;
import com.view.MyProgressDialog;
import com.view.SelectableRoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneralFunctions {

    Context mContext;
    public static final int MY_PERMISSIONS_REQUEST = 51;
    public static final int MY_SETTINGS_REQUEST = 52;

    long autoLoginStartTime = 0;
    GenerateAlertBox generateAlert;
    String alertType = "";
    InternetConnection intCheck;
    private int NOTIFICATION_ID = 11;

    public GeneralFunctions(Context context) {
        this.mContext = context;
        checkForRTL();
    }

    public void checkForRTL() {
        if (mContext instanceof Activity) {
            if (!retrieveValue(CommonUtilities.LANGUAGE_IS_RTL_KEY).equals("") && retrieveValue(CommonUtilities.LANGUAGE_IS_RTL_KEY).equals(CommonUtilities.DATABASE_RTL_STR)) {
                forceRTLIfSupported((Activity) mContext);
            }
//            forceRTLIfSupported((Activity) mContext);
        }
    }

    public Typeface getDefaultFont(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/roboto_light.ttf");
    }

    public boolean isPermisionGranted() {
        int permissionCheck_storage = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck_camera = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);

        if (permissionCheck_storage == PackageManager.PERMISSION_GRANTED && permissionCheck_camera == PackageManager.PERMISSION_GRANTED) {
            return true;

        } else {
            return false;
        }
    }



    public void forceRTLIfSupported(Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            act.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    public void forceRTLIfSupported(android.support.v7.app.AlertDialog alertDialog) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            alertDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    public void forceRTLIfSupported(GenerateAlertBox generateAlert) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            generateAlert.alertDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    public String wrapHtml(Context context, String html) {
        return context.getString(isRTLmode() ? R.string.html_rtl : R.string.html, html);
    }

    public String getDateFormatedType(String date, String originalformate, String targateformate) {
        String convertdate = "";
        SimpleDateFormat original_formate = new SimpleDateFormat(originalformate);
        SimpleDateFormat date_format = new SimpleDateFormat(targateformate);

        try {
            Date datedata = original_formate.parse(date);
            convertdate = date_format.format(datedata);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertdate;

    }


    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    public void getHasKey(Context act) {
        PackageInfo info;
        try {
            info = act.getPackageManager().getPackageInfo(act.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Utils.printLog("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Utils.printLog("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Utils.printLog("no such an algorithm", e.toString());
        } catch (Exception e) {
            Utils.printLog("exception", e.toString());
        }
    }

    public void forceRTLIfSupported(Dialog alertDialog) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            alertDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    public boolean isRTLmode() {
        if (!retrieveValue(CommonUtilities.LANGUAGE_IS_RTL_KEY).equals("") && retrieveValue(CommonUtilities.LANGUAGE_IS_RTL_KEY).equals(CommonUtilities.DATABASE_RTL_STR)) {
            return true;
        }
        return false;
    }

    public String retrieveValue(String key) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String value_str = mPrefs.getString(key, "");

        return value_str;
    }

    public String retrieveLangLBl(String orig, String label) {

        if (isLanguageLabelsAvail() == true) {
            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            String languageLabels_str = mPrefs.getString(CommonUtilities.languageLabelsKey, "");

            if (getJsonValue(label, languageLabels_str).equals("")) {
                return orig;
            }

            return getJsonValue(label, languageLabels_str);
        }

        return orig;
    }

    public String generateDeviceToken() {
        if (checkPlayServices() == false) {
            return "";
        }
        // FirebaseInstanceId instanceID = FirebaseInstanceId.getInstance();
        InstanceID instanceID = InstanceID.getInstance(mContext);
        String GCMregistrationId = "";
        try {
            GCMregistrationId = instanceID.getToken(retrieveValue(CommonUtilities.APP_GCM_SENDER_ID_KEY), GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            // GCMregistrationId = FirebaseInstanceId.getInstance().getToken();
            Utils.printLog("GcmId", GCMregistrationId);

        } catch (Exception e) {
            e.printStackTrace();
            GCMregistrationId = "";
        }

        return GCMregistrationId;
    }


    public boolean checkPlayServices() {
        final GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        final int result = googleAPI.isGooglePlayServicesAvailable(mContext);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {

                ((Activity) mContext).runOnUiThread(new Runnable() {
                    public void run() {
                        googleAPI.getErrorDialog(((Activity) mContext), result,
                                Utils.PLAY_SERVICES_RESOLUTION_REQUEST).show();
                    }
                });

            }

            return false;
        }

        return true;
    }

    public static boolean checkDataAvail(String key, String response) {
        try {
            JSONObject obj_temp = new JSONObject(response);

            String action_str = obj_temp.getString(key);

            if (!action_str.equals("") && !action_str.equals("0") && action_str.equals("1")) {
                return true;
            }

        } catch (JSONException e) {
            e.printStackTrace();

            return false;
        }

        return false;
    }

    public void removeValue(String key) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.remove(key);
        editor.commit();
    }

    public void storedata(String key, String data) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(key, data);
        editor.commit();
    }

    public String addSemiColonToPrice(String value) {
        return NumberFormat.getNumberInstance(Locale.US).format(parseIntegerValue(0, value));
    }

    public void storeUserData(String memberId) {
        storedata(CommonUtilities.iMemberId_KEY, memberId);
        storedata(CommonUtilities.isUserLogIn, "1");
    }

    public String getMemberId() {
        if (isUserLoggedIn() == true) {
            return retrieveValue(CommonUtilities.iMemberId_KEY);
        } else {
            return "";
        }
    }


    public boolean isReferralSchemeEnable() {
        if (!retrieveValue(CommonUtilities.REFERRAL_SCHEME_ENABLE).equals("") && retrieveValue(CommonUtilities.REFERRAL_SCHEME_ENABLE).equalsIgnoreCase("Yes")) {
            return true;
        }
        return false;
    }

    public void logOutUser() {
        removeValue(CommonUtilities.iMemberId_KEY);
        removeValue(CommonUtilities.isUserLogIn);
        removeValue(CommonUtilities.languageLabelsKey);
        removeValue(CommonUtilities.LANGUAGE_CODE_KEY);
        removeValue(CommonUtilities.DEFAULT_CURRENCY_VALUE);
        removeValue(CommonUtilities.USER_PROFILE_JSON);

    }

    public boolean isUserLoggedIn() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String isUserLoggedIn_str = mPrefs.getString(CommonUtilities.isUserLogIn, "");

        if (!isUserLoggedIn_str.equals("") && isUserLoggedIn_str.equals("1")) {
            return true;
        }

        return false;
    }

    public String getJsonValue(String key, String response) {

        try {
            JSONObject obj_temp = new JSONObject(response);

            String value_str = obj_temp.getString(key);

            if (value_str != null && !value_str.equals("null") && !value_str.equals("")) {
                return value_str;
            }

        } catch (JSONException e) {
            e.printStackTrace();

            return "";
        }

        return "";
    }

    public boolean isLanguageLabelsAvail() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String languageLabels_str = mPrefs.getString(CommonUtilities.languageLabelsKey, null);

        if (languageLabels_str != null && !languageLabels_str.equals("")) {
            return true;
        }

        return false;
    }

    public JSONArray getJsonArray(String key, String response) {
        try {
            JSONObject obj_temp = new JSONObject(response);
            JSONArray obj_temp_arr = obj_temp.getJSONArray(key);

            return obj_temp_arr;

        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

    }

    public JSONArray getJsonArray(String response) {
        try {
            JSONArray obj_temp_arr = new JSONArray(response);

            return obj_temp_arr;

        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

    }

    public JSONObject getJsonObject(JSONArray arr, int position) {
        try {
            JSONObject obj_temp = arr.getJSONObject(position);

            return obj_temp;

        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

    }

    public JSONObject getJsonObject(String key, String response) {

        try {
            JSONObject obj_temp = new JSONObject(response);

            JSONObject value_str = obj_temp.getJSONObject(key);

            if (value_str != null && !value_str.equals("null") && !value_str.equals("")) {
                return value_str;
            }

        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

        return null;
    }

    public boolean isJSONkeyAvail(String key, String response) {
        try {
            JSONObject json_obj = new JSONObject(response);

            if (json_obj.has(key) && !json_obj.isNull(key)) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean isJSONArrKeyAvail(String key, String response) {
        try {
            JSONObject json_obj = new JSONObject(response);

            if (json_obj.optJSONArray(key) != null) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public Float parseFloatValue(float defaultValue, String strValue) {

        try {
            float value = Float.parseFloat(strValue);
            return value;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Double parseDoubleValue(double defaultValue, String strValue) {

        try {
            double value = Double.parseDouble(strValue);
            return value;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public int parseIntegerValue(int defaultValue, String strValue) {

        try {
            int value = Integer.parseInt(strValue);
            return value;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public long parseLongValue(long defaultValue, String strValue) {

        try {
            long value = Long.parseLong(strValue);
            return value;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public void sendHeartBeat() {
        mContext.sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
        mContext.sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));
    }

    public boolean isEmailValid(String email) {
        boolean isValid = false;

        // String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        String expression = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        CharSequence inputStr = email.trim();

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public void generateErrorView(ErrorView errorView, String title, String subTitle) {
        errorView.setConfig(ErrorView.Config.create()
                .title("")
                .titleColor(mContext.getResources().getColor(android.R.color.black))
                .subtitle(retrieveLangLBl("", subTitle))
                .retryText(retrieveLangLBl("Retry", "LBL_RETRY_TXT"))
                .retryTextColor(mContext.getResources().getColor(R.color.error_view_retry_btn_txt_color))
                .build());
    }

    public void showError() {
        GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
        generateAlert.setContentMessage("", retrieveLangLBl("Please try again.", "LBL_TRY_AGAIN_TXT"));
        generateAlert.setPositiveBtn(retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));
        generateAlert.showAlertBox();
    }

    public void showGeneralMessage(String title, String message) {
        try {
            GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
            generateAlert.setContentMessage(title, message);
            generateAlert.setPositiveBtn(retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));


            generateAlert.showAlertBox();

        } catch (Exception e) {
            Utils.printLog("AlertEx", e.toString());
        }
    }

    public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            final LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (locationMode != Settings.Secure.LOCATION_MODE_OFF && statusOfGPS == true) {
                return true;
            }

            return false;

        } else {
            locationProviders = Settings.Secure.getString(mContext.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

            return !TextUtils.isEmpty(locationProviders);
        }

    }


    public boolean checkLocationPermission(boolean isPermissionDialogShown) {
        int permissionCheck_fine = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheck_coarse = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionCheck_fine != PackageManager.PERMISSION_GRANTED || permissionCheck_coarse != PackageManager.PERMISSION_GRANTED) {

            if (isPermissionDialogShown == false) {
                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST);
            }


            // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            return false;
        }

        return true;
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                if (mContext instanceof Activity) {
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST);
                }
                return false;
            }
        } else {
            return true;
        }
    }

    public boolean isCameraPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {

                if (mContext instanceof Activity) {
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST);
                }

                return false;
            }
        } else {
            return true;
        }
    }

    public boolean isAllPermissionGranted(boolean openDialog) {
        int permissionCheck_fine = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheck_coarse = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_storage = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck_camera = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);

//        if (permissionCheck_fine != PackageManager.PERMISSION_GRANTED || permissionCheck_coarse != PackageManager.PERMISSION_GRANTED
//                || permissionCheck_storage != PackageManager.PERMISSION_GRANTED || permissionCheck_camera != PackageManager.PERMISSION_GRANTED) {

        if (permissionCheck_fine != PackageManager.PERMISSION_GRANTED || permissionCheck_coarse != PackageManager.PERMISSION_GRANTED) {

            if (openDialog) {
//
//                ActivityCompat.requestPermissions((Activity) mContext,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
//                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
//                        MY_PERMISSIONS_REQUEST);

                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                        },
                        MY_PERMISSIONS_REQUEST);

            }

            // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            return false;
        }

        return true;
    }

    public boolean isCameraStoragePermissionGranted() {

        int permissionCheck_storage = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck_camera = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);

        if (permissionCheck_storage != PackageManager.PERMISSION_GRANTED || permissionCheck_camera != PackageManager.PERMISSION_GRANTED) {

//
//                ActivityCompat.requestPermissions((Activity) mContext,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
//                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
//                        MY_PERMISSIONS_REQUEST);

            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    MY_PERMISSIONS_REQUEST);


            // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            return false;
        }

        return true;
    }

    public void openSettings() {
        if (mContext instanceof Activity) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", CommonUtilities.package_name, null);
            intent.setData(uri);
            ((Activity) mContext).startActivityForResult(intent, MY_SETTINGS_REQUEST);
        }
    }

    public void logOUTFrmFB() {
        LoginManager.getInstance().logOut();
    }

    public GenerateAlertBox notifyRestartApp() {
        GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
        generateAlert.setContentMessage(retrieveLangLBl("", "LBL_BTN_TRIP_CANCEL_CONFIRM_TXT"),
                retrieveLangLBl("In order to apply changes restarting app is required. Please wait.", "LBL_NOTIFY_RESTART_APP_TO_CHANGE"));
        generateAlert.setPositiveBtn(retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));
        generateAlert.showAlertBox();

        return generateAlert;
    }

    public GenerateAlertBox notifyRestartApp(String title, String contentMsg) {
        GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
        generateAlert.setContentMessage(title, contentMsg);
        generateAlert.setPositiveBtn(retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));
        generateAlert.showAlertBox();

        return generateAlert;
    }

    public void notifySessionTimeOut() {
        logOutUser();

        GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
        generateAlert.setContentMessage(retrieveLangLBl("", "LBL_BTN_TRIP_CANCEL_CONFIRM_TXT"),
                retrieveLangLBl("Your session is expired. Please login again.", "LBL_SESSION_TIME_OUT"));
        generateAlert.setPositiveBtn(retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {

                if (btn_id == 1) {
                    restartApp();
                }
            }
        });
        generateAlert.showAlertBox();

    }

    public void restartApp() {
        new StartActProcess(mContext).startAct(LauncherActivity.class);
        ((Activity) mContext).setResult(Activity.RESULT_CANCELED);
        ActivityCompat.finishAffinity((Activity) mContext);
        System.gc();

    }

    public void restartwithGetDataApp() {
        getUserData objrefresh = new getUserData(this, mContext);
        objrefresh.getData();
    }

    public void refreshMainActivity() {

        new StartActProcess(mContext).startAct(MainActivity.class);
        ((Activity) mContext).setResult(Activity.RESULT_OK);


    }


    public View getCurrentView(Activity act) {
        View view = act.findViewById(android.R.id.content);
        return view;
    }

    public void showMessage(View view, String message) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }


    public double CalculationByLocationKm(double lat1, double lon1, double lat2, double lon2) {
        int Radius = 6371;// radius of earth in Km
        double lat1_s = lat1;
        double lat2_d = lat2;
        double lon1_s = lon1;
        double lon2_d = lon2;
        double dLat = Math.toRadians(lat2_d - lat1_s);
        double dLon = Math.toRadians(lon2_d - lon1_s);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1_s))
                * Math.cos(Math.toRadians(lat2_d)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        // Log.i("Radius Value", "" + valueResult + " KM " + kmInDec
        // + " Meter " + meterInDec);

        return kmInDec;
    }

    public String getSelectedCarTypeData(String selectedCarTypeId, String jsonArrKey, String dataKey, String json) {
        JSONArray arr = getJsonArray(jsonArrKey, json);

        for (int i = 0; i < arr.length(); i++) {
            JSONObject tempObj = getJsonObject(arr, i);

            String iVehicleTypeId = getJsonValue("iVehicleTypeId", tempObj.toString());

            if (iVehicleTypeId.equals(selectedCarTypeId)) {
                String dataValue = getJsonValue(dataKey, tempObj.toString());

                return dataValue;
            }
        }

        return "";
    }

    public PolylineOptions getGoogleRouteOptions(String directionJson, int width, int color) {
        PolylineOptions lineOptions = new PolylineOptions();

        try {
            DirectionsJSONParser parser = new DirectionsJSONParser();
            List<List<HashMap<String, String>>> routes_list = parser.parse(new JSONObject(directionJson));

            ArrayList<LatLng> points = new ArrayList<LatLng>();

            if (routes_list.size() > 0) {
                // Fetching i-th route
                List<HashMap<String, String>> path = routes_list.get(0);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);

                }

                lineOptions.addAll(points);
                lineOptions.width(width);
                lineOptions.color(color);

                return lineOptions;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public void checkProfileImage(SelectableRoundedImageView userProfileImgView, String userProfileJson, String imageKey) {
        String vImgName_str = getJsonValue(imageKey, userProfileJson);

        if (vImgName_str == null || vImgName_str.equals("") || vImgName_str.equals("NONE")) {
            userProfileImgView.setImageResource(R.mipmap.ic_no_pic_user);
        } else {
            new DownloadProfileImg(mContext, userProfileImgView,
                    CommonUtilities.SERVER_URL_PHOTOS + "upload/Passenger/" + getMemberId() + "/" + vImgName_str,
                    vImgName_str).execute();
        }
    }

    public void verifyMobile(final Bundle bn, final Fragment fragment) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {
                generateAlert.closeAlertBox();

                if (btn_id == 0) {
                    return;
                }
                if (fragment == null) {
                    new StartActProcess(mContext).startActForResult(VerifyInfoActivity.class, bn, Utils.VERIFY_MOBILE_REQ_CODE);
                } else {
                    new StartActProcess(mContext).startActForResult(fragment, VerifyInfoActivity.class, Utils.VERIFY_MOBILE_REQ_CODE, bn);
                }

            }
        });
        generateAlert.setContentMessage("", retrieveLangLBl("", "LBL_VERIFY_MOBILE_CONFIRM_MSG"));
        generateAlert.setPositiveBtn(retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        generateAlert.setNegativeBtn(retrieveLangLBl("", "LBL_CANCEL_TXT"));
        generateAlert.showAlertBox();
    }

    public String decodeFile(String path, int DESIREDWIDTH, int DESIREDHEIGHT, String tempImgName) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
//            scaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT);
            int rotation = Utils.getExifRotation(path);
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.CROP);

            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                // Part 2: Scale image
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.CROP);
            } else {
//                unscaledBitmap.recycle();
//                return path;

                if (unscaledBitmap.getWidth() > unscaledBitmap.getHeight()) {
                    scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, unscaledBitmap.getHeight(), unscaledBitmap.getHeight(), ScalingUtilities.ScalingLogic.CROP);
                } else {
                    scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, unscaledBitmap.getWidth(), unscaledBitmap.getWidth(), ScalingUtilities.ScalingLogic.CROP);
                }
            }

            // Store to tmp file
            scaledBitmap = rotateBitmap(scaledBitmap, rotation);
            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/" + Utils.TempImageFolderPath);
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }

//            String s = "tmp.png";

            File f = new File(mFolder.getAbsolutePath(), tempImgName);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }

    public Bitmap convertDrawableToBitmap(Context mContext, int resId) {
        Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(),
                resId);

        return icon;
    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    public Bitmap writeTextOnDrawable(Context mContext, int drawableId, String text,boolean iswhite) {

        Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), mContext.getResources().getString(R.string.defaultFont));

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        if(iswhite)
        {
            paint.setColor(Color.WHITE);
        }
        else {
            paint.setColor(Color.BLACK);
        }
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(Utils.dipToPixels(mContext, 14));

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(bm);

        // If the text is bigger than the canvas , reduce the font size
        if (textRect.width() >= (canvas.getWidth() - 4))
            paint.setTextSize(Utils.dipToPixels(mContext, 7));

        int xPos = (canvas.getWidth() / 2) - 2; // -2 is for regulating the x
        // position offset

        // baseline to the center.
        int yPos = (int) ((canvas.getHeight() / 4) - ((paint.descent() + paint.ascent()) / 2));

        // canvas.save();

        for (String line : text.split("\n")) {
            canvas.drawText(line, xPos, yPos, paint);
            paint.setTextSize(Utils.dipToPixels(mContext, 14));
            yPos += paint.descent() - paint.ascent();
        }

        return bm;
    }


    public void autoLogin(final Activity mContext, final String tripId) {

        if (Utils.myPDialog == null) {
            Utils.myPDialog = new MyProgressDialog(mContext, true, retrieveLangLBl("Loading", "LBL_LOADING_TXT"));
            Utils.myPDialog.show();
        }
        generateAlert = new GenerateAlertBox(mContext);

        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {
                if (btn_id == 0) {
                    generateAlert.closeAlertBox();

                    if (!alertType.equals("NO_PLAY_SERVICE") && !alertType.equals("APP_UPDATE")) {
                        mContext.finish();
                    } else {
                        checkConfigurations(mContext, tripId);
                    }


                } else {
                    if (alertType.equals("NO_PLAY_SERVICE")) {

                        boolean isSuccessfulOpen = new StartActProcess(mContext).openURL("market://details?id=com.google.android.gms");
                        if (isSuccessfulOpen == false) {
                            new StartActProcess(mContext).openURL("http://play.google.com/store/apps/details?id=com.google.android.gms");
                        }

                        generateAlert.closeAlertBox();
                        checkConfigurations(mContext, tripId);

                    } else if (alertType.equals("APP_UPDATE")) {

                        boolean isSuccessfulOpen = new StartActProcess(mContext).openURL("market://details?id=" + CommonUtilities.package_name);
                        if (isSuccessfulOpen == false) {
                            new StartActProcess(mContext).openURL("http://play.google.com/store/apps/details?id=" + CommonUtilities.package_name);
                        }

                        generateAlert.closeAlertBox();
                        checkConfigurations(mContext, tripId);

                    } else if (!alertType.equals("NO_GPS")) {
                        generateAlert.closeAlertBox();
                        checkConfigurations(mContext, tripId);
                    } else {
                        new StartActProcess(mContext).
                                startActForResult(Settings.ACTION_LOCATION_SOURCE_SETTINGS, Utils.REQUEST_CODE_GPS_ON);
                    }

                }
            }
        });
        setDefaultAlertBtn();
        generateAlert.setCancelable(false);

        autoLoginStartTime = Calendar.getInstance().getTimeInMillis();

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getDetail");
        parameters.put("iUserId", getMemberId());
        parameters.put("vDeviceType", Utils.deviceType);
        parameters.put("AppVersion", Utils.getAppVersion());
        parameters.put("iTripId", tripId);
        parameters.put("UserType", CommonUtilities.app_type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(mContext, parameters);
//        exeWebServer.setLoaderConfig(mContext, true, new GeneralFunctions(mContext));
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken", new GeneralFunctions(mContext));
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(final String responseString) {

                Utils.printLog("responseString", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    String message = getJsonValue(CommonUtilities.message_str, responseString);

                    if (message.equals("SESSION_OUT")) {
                        autoLoginStartTime = 0;
                        notifySessionTimeOut();
                        Utils.runGC();
                        return;
                    }



                    if (isDataAvail == true) {
                        /*new SetUserData(generalFunc.getJsonValue(CommonUtilities.message_str, responseString), generalFunc);*/

                        if (Calendar.getInstance().getTimeInMillis() - autoLoginStartTime < 2000) {
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    new OpenMainProfile(mContext,
                                            getJsonValue(CommonUtilities.message_str, responseString), true, new GeneralFunctions(mContext), tripId).startProcess();
                                }
                            }, 2000);
                        } else {
                            new OpenMainProfile(mContext,
                                    getJsonValue(CommonUtilities.message_str, responseString), true, new GeneralFunctions(mContext), tripId).startProcess();
                        }


                    } else {
                        autoLoginStartTime = 0;
                        if (!getJsonValue("isAppUpdate", responseString).trim().equals("")
                                && getJsonValue("isAppUpdate", responseString).equals("true")) {

                            showAppUpdateDialog(retrieveLangLBl("New update is available to download. " +
                                            "Downloading the latest update, you will get latest features, improvements and bug fixes.",
                                    getJsonValue(CommonUtilities.message_str, responseString)));
                        } else {
                            showError("", retrieveLangLBl("", getJsonValue(CommonUtilities.message_str, responseString)));
                        }
                    }
                } else {
                    autoLoginStartTime = 0;
                    showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public void showError(String title, String contentMsg) {
        alertType = "ERROR";
        setDefaultAlertBtn();
        generateAlert.setContentMessage(title,
                contentMsg);

        generateAlert.showAlertBox();
    }


    public void checkConfigurations(Activity mContext, String tripId) {
        intCheck = new InternetConnection(mContext);

        int status = (GoogleApiAvailability.getInstance()).isGooglePlayServicesAvailable(mContext);

        if (status == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            showErrorOnPlayServiceDialog(retrieveLangLBl("This application requires updated google play service. " +
                    "Please install Or update it from play store", "LBL_UPDATE_PLAY_SERVICE_NOTE"));
            return;
        } else if (status != ConnectionResult.SUCCESS) {
            showErrorOnPlayServiceDialog(retrieveLangLBl("This application requires updated google play service. " +
                    "Please install Or update it from play store", "LBL_UPDATE_PLAY_SERVICE_NOTE"));
            return;
        }

        if (isAllPermissionGranted() == false) {
            showError("", retrieveLangLBl("Application requires some permission to be granted to work. Please allow it.",
                    "LBL_ALLOW_PERMISSIONS_APP"));
            return;
        }
        if (!intCheck.isNetworkConnected() && !intCheck.check_int()) {

            showNoInternetDialog();
        } else if (isLocationEnabled() == false) {
            showNoGPSDialog();
        } else {
            autoLogin(mContext, tripId);
        }

    }

    public boolean isAllPermissionGranted() {
        int permissionCheck_fine = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheck_coarse = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_storage = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck_camera = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);

        if (permissionCheck_fine != PackageManager.PERMISSION_GRANTED || permissionCheck_coarse != PackageManager.PERMISSION_GRANTED
                || permissionCheck_storage != PackageManager.PERMISSION_GRANTED || permissionCheck_camera != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST);


            // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            return false;
        }

        return true;
    }

    public void showNoInternetDialog() {
        alertType = "NO_INTERNET";
        setDefaultAlertBtn();
        generateAlert.setContentMessage("",
                retrieveLangLBl("No Internet Connection", "LBL_NO_INTERNET_TXT"));

        generateAlert.showAlertBox();

    }

    public void showNoGPSDialog() {
        alertType = "NO_GPS";
        generateAlert.setContentMessage("", retrieveLangLBl("Your GPS seems to be disabled, do you want to enable it?", "LBL_ENABLE_GPS"));

        generateAlert.resetBtn();
        generateAlert.setPositiveBtn(retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));
        generateAlert.setNegativeBtn(retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"));
        generateAlert.showAlertBox();

    }

    public void showErrorOnPlayServiceDialog(String content) {
        alertType = "NO_PLAY_SERVICE";
        generateAlert.setContentMessage("", content);

        generateAlert.resetBtn();
        generateAlert.setPositiveBtn(retrieveLangLBl("Update", "LBL_UPDATE"));
        generateAlert.setNegativeBtn(retrieveLangLBl("Retry", "LBL_RETRY_TXT"));
        generateAlert.showAlertBox();

    }

    public void setDefaultAlertBtn() {
        generateAlert.resetBtn();
        generateAlert.setPositiveBtn(retrieveLangLBl("Retry", "LBL_RETRY_TXT"));
        generateAlert.setNegativeBtn(retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"));
    }

    public void showAppUpdateDialog(String content) {
        alertType = "APP_UPDATE";
        generateAlert.setContentMessage(retrieveLangLBl("New update available", "LBL_NEW_UPDATE_AVAIL"), content);

        generateAlert.resetBtn();
        generateAlert.setPositiveBtn(retrieveLangLBl("Update", "LBL_UPDATE"));
        generateAlert.setNegativeBtn(retrieveLangLBl("Retry", "LBL_RETRY_TXT"));
        generateAlert.showAlertBox();

    }

    public void makeTextViewResizable(final TextView tv,
                                      final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0,
                            lineEndIndex - expandText.length() + 1)
                            + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(addClickablePartTextViewResizable(tv.getText()
                                    .toString(), tv, maxLine, expandText,
                            viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0,
                            lineEndIndex - expandText.length() + 1)
                            + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(tv.getText()
                                            .toString(), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(addClickablePartTextViewResizable(tv.getText().toString(), tv, lineEndIndex, expandText,
                            viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private SpannableStringBuilder addClickablePartTextViewResizable(
            final String strSpanned, final TextView tv, final int maxLine,
            final String spanableText, final boolean viewMore) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (strSpanned.contains(spanableText)) {
            ssb.setSpan(
                    new MyClickableSpan(mContext) {

                        @Override
                        public void onClick(View widget) {

                            if (viewMore) {
                                tv.setLayoutParams(tv.getLayoutParams());
                                tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                                tv.invalidate();
                                makeTextViewResizable(tv, -5, "\n- " + retrieveLangLBl("Less", "LBL_LESS_TXT"), false);
//                                tv.setTextColor(Color.BLACK);
                            } else {
                                tv.setLayoutParams(tv.getLayoutParams());
                                tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                                tv.invalidate();
                                makeTextViewResizable(tv, 5, "...\n+ " + retrieveLangLBl("View More", "LBL_VIEW_MORE_TXT"), true);
//                                tv.setTextColor(Color.BLACK);
                            }

                        }
                    }, strSpanned.indexOf(spanableText),
                    strSpanned.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }

    public String convertNumberWithRTL(String data) {
        String result = "";
        NumberFormat nf = null;
        try {

            Locale locale = mContext.getResources().getConfiguration().locale;
            nf = NumberFormat.getInstance(locale);

            if (data != null && !data.equals("")) {
                for (int i = 0; i < data.length(); i++) {

                    char c = data.charAt(i);

                    if (Character.isDigit(c)) {
                        result = result + nf.format(Integer.parseInt(String.valueOf(c)));
                        Utils.printLog("result", result);
                    } else {
                        result = result + c;

                    }

                }
            }

            Utils.printLog("result", result);
            return result;


        } catch (Exception e) {
            Utils.printLog("Exception umber ", e.toString());
        }
        return result;

    }

}
