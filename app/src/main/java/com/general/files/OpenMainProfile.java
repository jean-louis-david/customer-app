package com.general.files;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.app85taxi.passenger.AccountverificationActivity;
import com.app85taxi.passenger.MainActivity;
import com.app85taxi.passenger.RatingActivity;
import com.utils.CommonUtilities;
import com.utils.Utils;

/**
 * Created by Admin on 29-06-2016.
 */
public class OpenMainProfile {
    Context mContext;
    String responseString;
    boolean isCloseOnError;
    GeneralFunctions generalFun;
    String tripId = "";
    String eType = "";
    boolean isnotification=false;

    public OpenMainProfile(Context mContext, String responseString, boolean isCloseOnError, GeneralFunctions generalFun) {
        this.mContext = mContext;
        //  this.responseString = responseString;
        this.isCloseOnError = isCloseOnError;
        this.generalFun = generalFun;
    }

    public OpenMainProfile(Context mContext, String responseString, boolean isCloseOnError, GeneralFunctions generalFun, String tripId) {
        this.mContext = mContext;
        this.responseString = responseString;
        this.isCloseOnError = isCloseOnError;
        this.generalFun = generalFun;
        this.tripId = tripId;
    }
    public OpenMainProfile(Context mContext, String responseString, boolean isCloseOnError, GeneralFunctions generalFun, boolean isnotification) {
        this.mContext = mContext;
        this.responseString = responseString;
        this.isCloseOnError = isCloseOnError;
        this.generalFun = generalFun;
        this.tripId = tripId;
        this.isnotification=isnotification;
    }

    public void startProcess() {

        if (generalFun == null)
            return;

        generalFun.sendHeartBeat();

        responseString = generalFun.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
        setGeneralData();

        String vTripStatus = generalFun.getJsonValue("vTripStatus", responseString);
        String PaymentStatus_From_Passenger_str = "";
        String Ratings_From_Passenger_str = "";
        String vTripPaymentMode_str = "";
        String eVerified_str = "";

       // if (vTripStatus.equals("Not Active")) {
//            String Last_trip_data = generalFun.getJsonValue("Last_trip_data", responseString);
            String Last_trip_data = generalFun.getJsonValue("TripDetails", responseString);
            eType = generalFun.getJsonValue("eType", Last_trip_data);

            PaymentStatus_From_Passenger_str = generalFun.getJsonValue("PaymentStatus_From_Passenger", responseString);
            Ratings_From_Passenger_str = generalFun.getJsonValue("Ratings_From_Passenger", responseString);
            eVerified_str = generalFun.getJsonValue("eVerified", Last_trip_data);
            vTripPaymentMode_str = generalFun.getJsonValue("vTripPaymentMode", Last_trip_data);

            vTripPaymentMode_str = "Cash";// to remove paypal
            PaymentStatus_From_Passenger_str = "Approved"; // to remove paypal
      //  }

        Bundle bn = new Bundle();
        bn.putString("USER_PROFILE_JSON", responseString);

        if (generalFun.getJsonValue("vPhone", responseString).equals("") || generalFun.getJsonValue("vEmail", responseString).equals("")) {
            //open account verification screen
            if (generalFun.getMemberId() != null && !generalFun.getMemberId().equals("")) {
                if(!generalFun.getMemberId().equals("")) {
                    new StartActProcess(mContext).startActWithData(AccountverificationActivity.class, bn);
                }
                else
                {
                    generalFun.restartApp();
                }
            }
        } else if (!vTripStatus.equals("Not Active") || ((PaymentStatus_From_Passenger_str.equals("Approved")
                || vTripPaymentMode_str.equals("Cash")) && Ratings_From_Passenger_str.equals("Done")
                /*&& eVerified_str.equals("Verified")*/)) {

            if (generalFun.getJsonValue("APP_TYPE", responseString).equalsIgnoreCase("UberX")) {
//                new StartActProcess(mContext).startActWithData(UberXHomeActivity.class, bn);

            } else {
                 bn.putBoolean("isnotification",isnotification);
                new StartActProcess(mContext).startActWithData(MainActivity.class, bn);
            }
        } else {
            if (!eType.equals("")) {
                if (generalFun.getJsonValue("APP_TYPE", responseString).equalsIgnoreCase("UberX")) {
//                new StartActProcess(mContext).startActWithData(UberXHomeActivity.class, bn);

                } else if (generalFun.getJsonValue("APP_TYPE", responseString).equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX)) {
                    if (eType.equals("UberX")) {
                        new StartActProcess(mContext).startActWithData(MainActivity.class, bn);

                    } else {
                        new StartActProcess(mContext).startActWithData(RatingActivity.class, bn);
                    }

                } else {
                    new StartActProcess(mContext).startActWithData(RatingActivity.class, bn);
                }

            }

        }

        ActivityCompat.finishAffinity((Activity) mContext);
    }

    public void setGeneralData() {
        generalFun.storedata(CommonUtilities.PUBNUB_PUB_KEY, generalFun.getJsonValue("PUBNUB_PUBLISH_KEY", responseString));
        generalFun.storedata(CommonUtilities.PUBNUB_SUB_KEY, generalFun.getJsonValue("PUBNUB_SUBSCRIBE_KEY", responseString));
        generalFun.storedata(CommonUtilities.PUBNUB_SEC_KEY, generalFun.getJsonValue("PUBNUB_SECRET_KEY", responseString));
        generalFun.storedata(Utils.SESSION_ID_KEY, generalFun.getJsonValue("tSessionId", responseString));
        generalFun.storedata(Utils.DEVICE_SESSION_ID_KEY, generalFun.getJsonValue("tDeviceSessionId", responseString));

        generalFun.storedata(CommonUtilities.APP_DESTINATION_MODE, generalFun.getJsonValue("APP_DESTINATION_MODE", responseString));
        generalFun.storedata(CommonUtilities.APP_TYPE, generalFun.getJsonValue("APP_TYPE", responseString));
        generalFun.storedata(CommonUtilities.SITE_TYPE_KEY, generalFun.getJsonValue("SITE_TYPE", responseString));
        generalFun.storedata(CommonUtilities.ENABLE_TOLL_COST, generalFun.getJsonValue("ENABLE_TOLL_COST", responseString));
        generalFun.storedata(CommonUtilities.TOLL_COST_APP_ID, generalFun.getJsonValue("TOLL_COST_APP_ID", responseString));
        generalFun.storedata(CommonUtilities.TOLL_COST_APP_CODE, generalFun.getJsonValue("TOLL_COST_APP_CODE", responseString));
        generalFun.storedata(CommonUtilities.HANDICAP_ACCESSIBILITY_OPTION, generalFun.getJsonValue("HANDICAP_ACCESSIBILITY_OPTION", responseString));
        generalFun.storedata(CommonUtilities.FEMALE_RIDE_REQ_ENABLE,generalFun.getJsonValue("FEMALE_RIDE_REQ_ENABLE",responseString));
    }
}
