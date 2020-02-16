package com.app85taxi.passenger;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.GetLocationUpdates;
import com.general.files.InternetConnection;
import com.general.files.MyBackGroundService;
import com.general.files.OpenMainProfile;
import com.general.files.StartActProcess;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.anim.loader.AVLoadingIndicatorView;

import java.util.Calendar;
import java.util.HashMap;

public class LauncherActivity extends AppCompatActivity implements GenerateAlertBox.HandleAlertBtnClick, GetLocationUpdates.LastLocationListner {

    AVLoadingIndicatorView loaderView;
    InternetConnection intCheck;
    GenerateAlertBox generateAlert;
    GeneralFunctions generalFunc;

    GetLocationUpdates getLastLocation;

    Location mLastLocation;

    String alertType = "";

    long autoLoginStartTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        generalFunc = new GeneralFunctions(getActContext());
//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            if (generalFunc.retrieveValue("isnotification").equals("true")) {
//                new OpenMainProfile(getActContext(),
//                        generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON), true, generalFunc, true).startProcess();
//                generalFunc.storedata("isnotification", false + "");
//                return;
//
//            }
//
//
//        }

        intCheck = new InternetConnection(this);

        generalFunc.storedata("isInLauncher", "true");

        generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setBtnClickList(this);
        setDefaultAlertBtn();
        generateAlert.setCancelable(false);

        loaderView = (AVLoadingIndicatorView) findViewById(R.id.loaderView);
        Utils.runGC();
        checkConfigurations(true);
        new StartActProcess(getActContext()).startService(MyBackGroundService.class);
    }

    public void setDefaultAlertBtn() {
        generateAlert.resetBtn();
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("Retry", "LBL_RETRY_TXT"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"));
    }

    public void checkConfigurations(boolean isPermissionShown) {

        int status = (GoogleApiAvailability.getInstance()).isGooglePlayServicesAvailable(getActContext());

        if (status == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            showErrorOnPlayServiceDialog(generalFunc.retrieveLangLBl("This application requires updated google play service. " +
                    "Please install Or update it from play store", "LBL_UPDATE_PLAY_SERVICE_NOTE"));
            return;
        } else if (status != ConnectionResult.SUCCESS) {
            showErrorOnPlayServiceDialog(generalFunc.retrieveLangLBl("This application requires updated google play service. " +
                    "Please install Or update it from play store", "LBL_UPDATE_PLAY_SERVICE_NOTE"));
            return;
        }

        Utils.printLog("Result", "Called:" + generalFunc.isAllPermissionGranted(isPermissionShown));

        Utils.printLog("Permissions1", "::" + generalFunc.isAllPermissionGranted(isPermissionShown));
        if (generalFunc.isAllPermissionGranted(isPermissionShown) == false) {
//            showError("", generalFunc.retrieveLangLBl("Application requires some permission to be granted to work. Please allow it.",
//                    "LBL_ALLOW_PERMISSIONS_APP"));
            showNoPermission();
            return;
        }
        if (!intCheck.isNetworkConnected() && !intCheck.check_int()) {

            showNoInternetDialog();
        }
//        else if (generalFunc.isLocationEnabled() == false) {
//            showNoGPSDialog();
//        }
        else {
            if (mLastLocation == null) {
                continueProcess();
            } else if (getLastLocation == null) {
                getLastLocation = new GetLocationUpdates(getActContext(), 2);
                getLastLocation.setLastLocationListener(this);
                continueProcess();
            }
//            else
//            {
//                showNoLocationDialog();
//            }

            if (getLastLocation != null) {
                mLastLocation = getLastLocation.getLocation();
            }

        }

    }

    public void continueProcess() {

        showLoader();

        Utils.setAppLocal(getActContext());

        boolean isLanguageLabelsAvail = generalFunc.isLanguageLabelsAvail();
        Utils.printLog("isLanguageLabelsAvail", "::" + isLanguageLabelsAvail);
        Utils.printLog("isMemberAvail", "::" + generalFunc.isUserLoggedIn());
        Utils.printLog("memberId", "::" + generalFunc.getMemberId());

//        if (isLanguageLabelsAvail == false) {
//            downloadGeneralData();
//        } else {
//
//
//            if (generalFunc.isUserLoggedIn() == true) {
//                autoLogin();
//            } else {
//                closeLoader();
//
//                new Handler().postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        new StartActProcess(getActContext()).startAct(AppLoginActivity.class);
//                        ActivityCompat.finishAffinity(LauncherActivity.this);
//                    }
//                }, 2000);
//            }
//
//
//        }

        if (generalFunc.isUserLoggedIn() == true) {
            autoLogin();
        } else {
            downloadGeneralData();
        }

    }

    public void downloadGeneralData() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "generalConfigData");
        parameters.put("UserType", CommonUtilities.app_type);
        parameters.put("AppVersion", Utils.getAppVersion());
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {
                Utils.printLog("responseString", "::" + responseString);
                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    if (isDataAvail == true) {


                        generalFunc.storedata(CommonUtilities.FACEBOOK_APPID_KEY, generalFunc.getJsonValue("FACEBOOK_APP_ID", responseString));
                        generalFunc.storedata(CommonUtilities.LINK_FORGET_PASS_KEY, generalFunc.getJsonValue("LINK_FORGET_PASS_PAGE_PASSENGER", responseString));
                        generalFunc.storedata(CommonUtilities.APP_GCM_SENDER_ID_KEY, generalFunc.getJsonValue("GOOGLE_SENDER_ID", responseString));
                        generalFunc.storedata(CommonUtilities.MOBILE_VERIFICATION_ENABLE_KEY, generalFunc.getJsonValue("MOBILE_VERIFICATION_ENABLE", responseString));

                        generalFunc.storedata(CommonUtilities.CURRENCY_LIST_KEY, generalFunc.getJsonValue("LIST_CURRENCY", responseString));


                        generalFunc.storedata(CommonUtilities.GOOGLE_MAP_LANGUAGE_CODE_KEY, generalFunc.getJsonValue("vGMapLangCode", generalFunc.getJsonValue("DefaultLanguageValues", responseString)));
                        generalFunc.storedata(CommonUtilities.REFERRAL_SCHEME_ENABLE, generalFunc.getJsonValue("REFERRAL_SCHEME_ENABLE", responseString));
                        generalFunc.storedata(CommonUtilities.SITE_TYPE_KEY, generalFunc.getJsonValue("SITE_TYPE", responseString));


                        if (generalFunc.retrieveValue(CommonUtilities.LANGUAGE_CODE_KEY).equalsIgnoreCase("")) {
                            generalFunc.storedata(CommonUtilities.languageLabelsKey, generalFunc.getJsonValue("LanguageLabels", responseString));
                            generalFunc.storedata(CommonUtilities.LANGUAGE_LIST_KEY, generalFunc.getJsonValue("LIST_LANGUAGES", responseString));
                            generalFunc.storedata(CommonUtilities.LANGUAGE_IS_RTL_KEY, generalFunc.getJsonValue("eType", generalFunc.getJsonValue("DefaultLanguageValues", responseString)));
                            generalFunc.storedata(CommonUtilities.LANGUAGE_CODE_KEY, generalFunc.getJsonValue("vCode", generalFunc.getJsonValue("DefaultLanguageValues", responseString)));
                            generalFunc.storedata(CommonUtilities.DEFAULT_LANGUAGE_VALUE, generalFunc.getJsonValue("vTitle", generalFunc.getJsonValue("DefaultLanguageValues", responseString)));

                        }

                        if (generalFunc.retrieveValue(CommonUtilities.DEFAULT_CURRENCY_VALUE).equalsIgnoreCase("")) {
                            generalFunc.storedata(CommonUtilities.DEFAULT_CURRENCY_VALUE, generalFunc.getJsonValue("vName", generalFunc.getJsonValue("DefaultCurrencyValues", responseString)));
                        }
                        // GeneralFunctions.storedata(key: Utils.DEFAULT_LANGUAGE_TITLE_KEY, value: generalFunc.get("DefaultLanguageValues").get("vTitle") as AnyObject)
                        //  GeneralFunctions.storedata(key: Utils.DEFAULT_CURRENCY_TITLE_KEY, value: dataDict.getObj("DefaultCurrencyValues").get("vName") as AnyObject)

                        Utils.setAppLocal(getActContext());
                        closeLoader();

                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                new StartActProcess(getActContext()).startAct(AppLoginActivity.class);
                                ActivityCompat.finishAffinity(LauncherActivity.this);
                            }
                        }, 2000);

                    } else {
                        if (!generalFunc.getJsonValue("isAppUpdate", responseString).trim().equals("")
                                && generalFunc.getJsonValue("isAppUpdate", responseString).equals("true")) {

                            showAppUpdateDialog(generalFunc.retrieveLangLBl("New update is available to download. " +
                                            "Downloading the latest update, you will get latest features, improvements and bug fixes.",
                                    generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                        } else {
                            showError();
                        }

                    }
                } else {
                    showError();
                }

            }
        });
        exeWebServer.execute();
    }

    public void autoLogin() {
        autoLoginStartTime = Calendar.getInstance().getTimeInMillis();

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getDetail");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("vDeviceType", Utils.deviceType);
        parameters.put("AppVersion", Utils.getAppVersion());
        parameters.put("UserType", CommonUtilities.app_type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken", generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(final String responseString) {

                closeLoader();

                Utils.printLog("responseString", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    String message = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);

                    if (message.equals("SESSION_OUT")) {
                        autoLoginStartTime = 0;
                        generalFunc.notifySessionTimeOut();
                        Utils.runGC();
                        generalFunc.removeValue(CommonUtilities.LANGUAGE_CODE_KEY);
                        generalFunc.removeValue(CommonUtilities.DEFAULT_CURRENCY_VALUE);
                        return;
                    }

                    if (isDataAvail == true) {

                        generalFunc.storedata(CommonUtilities.USER_PROFILE_JSON, message);

                        if (Calendar.getInstance().getTimeInMillis() - autoLoginStartTime < 2000) {
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    new OpenMainProfile(getActContext(),
                                            generalFunc.getJsonValue(CommonUtilities.message_str, responseString), true, generalFunc).startProcess();
                                }
                            }, 2000);
                        } else {
                            new OpenMainProfile(getActContext(),
                                    generalFunc.getJsonValue(CommonUtilities.message_str, responseString), true, generalFunc).startProcess();
                        }


                    } else {
                        autoLoginStartTime = 0;
                        if (!generalFunc.getJsonValue("isAppUpdate", responseString).trim().equals("")
                                && generalFunc.getJsonValue("isAppUpdate", responseString).equals("true")) {

                            showAppUpdateDialog(generalFunc.retrieveLangLBl("New update is available to download. " +
                                            "Downloading the latest update, you will get latest features, improvements and bug fixes.",
                                    generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                        } else {

                            if (generalFunc.getJsonValue(CommonUtilities.message_str, responseString).equalsIgnoreCase("LBL_CONTACT_US_STATUS_NOTACTIVE_PASSENGER") ||
                                    generalFunc.getJsonValue(CommonUtilities.message_str, responseString).equalsIgnoreCase("LBL_ACC_DELETE_TXT")) {

                                GenerateAlertBox alertBox = generalFunc.notifyRestartApp("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                                alertBox.setCancelable(false);
                                alertBox.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                                    @Override
                                    public void handleBtnClick(int btn_id) {

                                        if (btn_id == 1) {
                                            generalFunc.logOutUser();
                                            generalFunc.restartApp();
                                        }
                                    }
                                });
                                return;
                            }
                            showError("",
                                    generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
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

    public void showLoader() {
        loaderView.setVisibility(View.VISIBLE);
    }

    public void closeLoader() {
        loaderView.setVisibility(View.GONE);
    }

    public void showError() {

        generateAlert.closeAlertBox();

        alertType = "ERROR";
        setDefaultAlertBtn();
        generateAlert.setContentMessage("",
                generalFunc.retrieveLangLBl("Please try again.", "LBL_TRY_AGAIN_TXT"));

        generateAlert.showAlertBox();
    }

    public void showError(String title, String contentMsg) {

        generateAlert.closeAlertBox();

        alertType = "ERROR";
        setDefaultAlertBtn();
        generateAlert.setContentMessage(title,
                contentMsg);

        generateAlert.showAlertBox();
    }

    public void showNoInternetDialog() {

        generateAlert.closeAlertBox();

        alertType = "NO_INTERNET";
        setDefaultAlertBtn();
        generateAlert.setContentMessage("",
                generalFunc.retrieveLangLBl("No Internet Connection", "LBL_NO_INTERNET_TXT"));

        generateAlert.showAlertBox();

    }

    public void showNoGPSDialog() {

        generateAlert.closeAlertBox();

        alertType = "NO_GPS";
        generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("Your GPS seems to be disabled, do you want to enable it?", "LBL_ENABLE_GPS"));

        generateAlert.resetBtn();
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"));
        generateAlert.showAlertBox();

    }

    public void showNoPermission() {
        generateAlert.closeAlertBox();

        alertType = "NO_PERMISSION";
        generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("Application requires some permission to be granted to work. Please allow it.",
                "LBL_ALLOW_PERMISSIONS_APP"));

        generateAlert.resetBtn();
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("Allow All", "LBL_ALLOW_ALL_TXT"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"));
        generateAlert.showAlertBox();

    }

    public void showErrorOnPlayServiceDialog(String content) {

        generateAlert.closeAlertBox();

        alertType = "NO_PLAY_SERVICE";
        generateAlert.setContentMessage("", content);

        generateAlert.resetBtn();
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("Update", "LBL_UPDATE"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("Retry", "LBL_RETRY_TXT"));
        generateAlert.showAlertBox();

    }

    public void showAppUpdateDialog(String content) {
        generateAlert.closeAlertBox();

        alertType = "APP_UPDATE";
        generateAlert.setContentMessage(generalFunc.retrieveLangLBl("New update available", "LBL_NEW_UPDATE_AVAIL"), content);

        generateAlert.resetBtn();
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("Update", "LBL_UPDATE"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("Retry", "LBL_RETRY_TXT"));
        generateAlert.showAlertBox();

    }

    public void showNoLocationDialog() {
        alertType = "NO_LOCATION";
        setDefaultAlertBtn();
        generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("No Internet Connection", "LBL_NO_LOCATION_FOUND_TXT"));

        generateAlert.showAlertBox();

    }

    public Context getActContext() {
        return LauncherActivity.this;
    }

    @Override
    public void handleBtnClick(int btn_id) {

        if (btn_id == 0) {
            generateAlert.closeAlertBox();

            if (!alertType.equals("NO_PLAY_SERVICE") && !alertType.equals("APP_UPDATE")) {
                finish();
            } else {
                checkConfigurations(false);
            }


        } else {
            if (alertType.equals("NO_PLAY_SERVICE")) {

                boolean isSuccessfulOpen = new StartActProcess(getActContext()).openURL("market://details?id=com.google.android.gms");
                if (isSuccessfulOpen == false) {
                    new StartActProcess(getActContext()).openURL("http://play.google.com/store/apps/details?id=com.google.android.gms");
                }

                generateAlert.closeAlertBox();
                checkConfigurations(false);

            } else if (alertType.equals("NO_PERMISSION")) {
//            generalFunc.openSettings();
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == false ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == false ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == false ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA) == false) {

                    generalFunc.openSettings();
                    generateAlert.closeAlertBox();
                } else if (generalFunc.isAllPermissionGranted(false) == false) {
                    generalFunc.isAllPermissionGranted(true);
                    generateAlert.closeAlertBox();
                    checkConfigurations(false);
                } else {
                    generateAlert.closeAlertBox();
                    checkConfigurations(true);
                }

            } else if (alertType.equals("APP_UPDATE")) {

                boolean isSuccessfulOpen = new StartActProcess(getActContext()).openURL("market://details?id=" + CommonUtilities.package_name);
                if (isSuccessfulOpen == false) {
                    new StartActProcess(getActContext()).openURL("http://play.google.com/store/apps/details?id=" + CommonUtilities.package_name);
                }

                generateAlert.closeAlertBox();
                checkConfigurations(false);

            } else if (!alertType.equals("NO_GPS")) {
                generateAlert.closeAlertBox();
                checkConfigurations(false);


            } else {
                new StartActProcess(getActContext()).
                        startActForResult(Settings.ACTION_LOCATION_SOURCE_SETTINGS, Utils.REQUEST_CODE_GPS_ON);
            }

        }
    }

    @Override
    public void handleLastLocationListnerCallback(Location mLastLocation) {

        this.mLastLocation = mLastLocation;
        Utils.printLog("Loc", "update:" + mLastLocation.getLatitude() + ":" + mLastLocation.getLongitude());
        checkConfigurations(false);
    }

    @Override
    public void handleLastLocationListnerNOVALUECallback(int id) {
        Utils.printLog("NO Loc", "update:");
        //showNoLocationDialog();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        if (getLastLocation != null && getLastLocation.isApiConnected()) {
            getLastLocation.startLocationUpdates();
        }

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        System.gc();

        if (getLastLocation != null && getLastLocation.isApiConnected()) {
            getLastLocation.stopLocationUpdates();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        generateAlert.closeAlertBox();
        switch (requestCode) {
            case Utils.REQUEST_CODE_GPS_ON:

                checkConfigurations(false);

                break;
            case GeneralFunctions.MY_SETTINGS_REQUEST:
                generateAlert.closeAlertBox();
                Utils.printLog("Result", "Called");
                checkConfigurations(false);

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case GeneralFunctions.MY_PERMISSIONS_REQUEST: {

                generateAlert.closeAlertBox();

                checkConfigurations(false);

                return;
            }
        }
    }
    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case GeneralFunctions.MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
//                checkConfigurations();
                return;
            }

        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        generalFunc.storedata("isInLauncher", "false");
    }
}
