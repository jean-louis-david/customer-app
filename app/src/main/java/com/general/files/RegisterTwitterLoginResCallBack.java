package com.general.files;

import android.content.Context;

import com.app85taxi.passenger.AppLoignRegisterActivity;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.MyProgressDialog;

import java.util.HashMap;

public class RegisterTwitterLoginResCallBack extends Callback<TwitterSession> {

    Context mContext;
    GeneralFunctions generalFunc;
    MyProgressDialog myPDialog;
    AppLoignRegisterActivity appLoginAct;

    public RegisterTwitterLoginResCallBack(Context mContext) {
        this.mContext = mContext;
        generalFunc = new GeneralFunctions(mContext);
        appLoginAct = (AppLoignRegisterActivity) mContext;
    }

    public void registerTwitterUser(final String email, final String fName, final String lName, final String fbId) {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "LoginWithFB");
        parameters.put("vFirstName", fName);
        parameters.put("vLastName", lName);
        parameters.put("vEmail", email);
        parameters.put("iFBId", fbId);
        parameters.put("eLoginType", "Twitter");
        parameters.put("vDeviceType", Utils.deviceType);
        parameters.put("UserType", Utils.userType);
        parameters.put("vCurrency", generalFunc.retrieveValue(CommonUtilities.DEFAULT_CURRENCY_VALUE));
        parameters.put("vLang", generalFunc.retrieveValue(CommonUtilities.LANGUAGE_CODE_KEY));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(mContext, parameters);
        exeWebServer.setLoaderConfig(mContext, true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken", generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);
                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    if (isDataAvail == true) {
                        new SetUserData(responseString, generalFunc, mContext, true);
                        generalFunc.storedata(CommonUtilities.USER_PROFILE_JSON, generalFunc.getJsonValue(CommonUtilities.message_str, responseString));
                        new OpenMainProfile(mContext,
                                generalFunc.getJsonValue(CommonUtilities.message_str, responseString), false, generalFunc).startProcess();
                    } else {
                        if (!generalFunc.getJsonValue(CommonUtilities.message_str, responseString).equals("DO_REGISTER")) {
                            generalFunc.showGeneralMessage("",
                                    generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                        } else {
                            signupUser(email, fName, lName, fbId);
                        }
                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    @Override
    public void success(Result<TwitterSession> result) {
        try {
            final TwitterSession session = result.data;

            // with your app's user model
            String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
            Utils.printLog("name", session.getUserName() + "");

            final TwitterSession twitterSession = result.data;
            TwitterAuthClient authClient = new TwitterAuthClient();
            authClient.requestEmail(twitterSession, new com.twitter.sdk.android.core.Callback<String>() {
                @Override
                public void success(Result<String> emailResult) {
                    String email = "";

                    if (emailResult.data == null) {
                        email = "";
                    } else {
                        email = emailResult.data;
                    }

                    Utils.printLog("registeremail", "" + email);
                    registerTwitterUser(email, session.getUserName(), "", session.getUserId() + "");
                }
                @Override
                public void failure(TwitterException e) {
                    Utils.printLog("twitteremail_error", e.toString());
                }
            });
            // registerTwitterUser("", session.getUserName(), "", session.getUserId() + "");
        } catch (Exception e) {
            Utils.printLog("twitter exception", e.toString());
        }
    }

    @Override
    public void failure(TwitterException exception) {
        Utils.printLog("twitter exception::", exception.toString());

    }

    public void signupUser(final String email, final String fName, final String lName, final String fbId) {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "signup");
        parameters.put("vFirstName", fName);
        parameters.put("vLastName", lName);
        parameters.put("vEmail", email);
        parameters.put("vFbId", fbId);
        parameters.put("vDeviceType", Utils.deviceType);
        parameters.put("UserType", Utils.userType);
        parameters.put("vCurrency", generalFunc.retrieveValue(CommonUtilities.DEFAULT_CURRENCY_VALUE));
        parameters.put("vLang", generalFunc.retrieveValue(CommonUtilities.LANGUAGE_CODE_KEY));
        parameters.put("eSignUpType", "Twitter");

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(mContext, parameters);
        //   exeWebServer.setLoaderConfig(mContext, true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken", generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    if (isDataAvail == true) {
                        new SetUserData(responseString, generalFunc, mContext, true);
                        generalFunc.storedata(CommonUtilities.USER_PROFILE_JSON, generalFunc.getJsonValue(CommonUtilities.message_str, responseString));
                        new OpenMainProfile(mContext,
                                generalFunc.getJsonValue(CommonUtilities.message_str, responseString), false, generalFunc).startProcess();
                    } else {

                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }
}
