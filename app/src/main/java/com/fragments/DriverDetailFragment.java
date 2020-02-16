package com.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.app85taxi.passenger.MainActivity;
import com.app85taxi.passenger.R;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.squareup.picasso.Picasso;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.MTextView;
import com.view.SelectableRoundedImageView;
import com.view.simpleratingbar.SimpleRatingBar;

import java.util.HashMap;

public class DriverDetailFragment extends Fragment {

    int PICK_CONTACT = 2121;

    View view;
    MainActivity mainAct;
    GeneralFunctions generalFunc;

    String driverPhoneNum = "";
    DriverDetailFragment driverDetailFragment;
    String userProfileJson;

    String vDeliveryConfirmCode = "";
    LinearLayout cancelarea, contactarea;
    View contactview;
    SimpleRatingBar ratingBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.fragment_driver_detail, container, false);

        cancelarea = (LinearLayout) view.findViewById(R.id.cancelarea);
        contactarea = (LinearLayout) view.findViewById(R.id.contactarea);
        contactview = (View) view.findViewById(R.id.contactview);
        ratingBar = (SimpleRatingBar) view.findViewById(R.id.ratingBar);

//        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
//        // Filled stars
//        setRatingStarColor(stars.getDrawable(2), ContextCompat.getColor(getContext(), R.color.appThemeColor_1));
//        // Half filled stars
//        setRatingStarColor(stars.getDrawable(1), ContextCompat.getColor(getContext(), R.color.appThemeColor_1));
//        // Empty stars
//        setRatingStarColor(stars.getDrawable(0), ContextCompat.getColor(getContext(), R.color.gray));

        mainAct = (MainActivity) getActivity();
        userProfileJson = mainAct.userProfileJson;
        generalFunc = mainAct.generalFunc;

        setLabels();
        setData();

        driverDetailFragment = mainAct.getDriverDetailFragment();
        mainAct.setDriverImgView(((SelectableRoundedImageView) view.findViewById(R.id.driverImgView)));

        if (generalFunc.getJsonValue("vTripStatus", userProfileJson).equals("On Going Trip")) {
            configTripStartView(vDeliveryConfirmCode);
        }

        new CreateRoundedView(Color.parseColor("#535353"), Utils.dipToPixels(mainAct.getActContext(), 5), 2,
                mainAct.getActContext().getResources().getColor(android.R.color.transparent), (view.findViewById(R.id.numberPlateArea)));
        return view;
    }

    private void setRatingStarColor(Drawable drawable, @ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DrawableCompat.setTint(drawable, color);
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    public HashMap<String, String> getTripData() {

        HashMap<String, String> tripDataMap = (HashMap<String, String>) getArguments().getSerializable("TripData");
        return tripDataMap;
    }

    public void setLabels() {
        ((MTextView) view.findViewById(R.id.slideUpForDetailTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_SLIDE_UP_DETAIL"));
        ((MTextView) view.findViewById(R.id.contact_btn)).setText(generalFunc.retrieveLangLBl("", "LBL_CALL_TXT"));
        ((MTextView) view.findViewById(R.id.btn_share_txt)).setText(generalFunc.retrieveLangLBl("", "LBL_SHARE_BTN_TXT"));
        ((MTextView) view.findViewById(R.id.btn_cancle_trip)).setText(generalFunc.retrieveLangLBl("", "LBL_BTN_CANCEL_TRIP_TXT"));
        ((MTextView) view.findViewById(R.id.btn_message)).setText(generalFunc.retrieveLangLBl("", "LBL_MESSAGE_TXT"));
    }

    public void setData() {
        HashMap<String, String> tripDataMap = (HashMap<String, String>) getArguments().getSerializable("TripData");

        ((MTextView) view.findViewById(R.id.driver_car_model)).setText(tripDataMap.get("DriverCarModelName"));

        if (tripDataMap.get("DriverCarColour") != null && !tripDataMap.get("DriverCarColour").equals("")) {
            ((MTextView) view.findViewById(R.id.driver_car_type)).setText("(" + tripDataMap.get("DriverCarColour").toUpperCase() + ")");
        } else {

            if (!generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("UberX")) {
                ((LinearLayout) view.findViewById(R.id.driverCarDetailArea)).setVisibility(View.VISIBLE);
                ((MTextView) view.findViewById(R.id.driver_car_type)).setText("(" + tripDataMap.get("vVehicleType") + ")");
            } else {
                ((LinearLayout) view.findViewById(R.id.driverCarDetailArea)).setVisibility(View.GONE);
//            ((MTextView) view.findViewById(R.id.driver_car_type)).setText(tripDataMap.get("vVehicleType") + "-" + tripDataMap.get("iVehicleCatName"));
            }
        }

        ((MTextView) view.findViewById(R.id.driver_name)).setText(tripDataMap.get("DriverName"));
        //  ((MTextView) view.findViewById(R.id.driver_car_type)).setText("("+tripDataMap.get("vVehicleType")+")");
        ((MTextView) view.findViewById(R.id.txt_rating)).setText(tripDataMap.get("DriverRating"));
        ratingBar.setRating(generalFunc.parseFloatValue(0, tripDataMap.get("DriverRating")));
        ((MTextView) view.findViewById(R.id.driver_car_name)).setText(tripDataMap.get("DriverCarName"));
        ((MTextView) view.findViewById(R.id.driver_car_model)).setText(tripDataMap.get("DriverCarModelName"));

        ((MTextView) view.findViewById(R.id.numberPlate_txt)).setText(tripDataMap.get("DriverCarPlateNum"));

        driverPhoneNum = /*tripDataMap.get("vCode") +*/ tripDataMap.get("DriverPhone");
        vDeliveryConfirmCode = tripDataMap.get("vDeliveryConfirmCode");
        String driverImageName = tripDataMap.get("DriverImage");
        if (driverImageName == null || driverImageName.equals("") || driverImageName.equals("NONE")) {
//            ((NetworkRoundedImageView) view.findViewById(R.id.driverImgView)).setLocalImageBitmap(R.mipmap.ic_no_pic_user);
            ((SelectableRoundedImageView) view.findViewById(R.id.driverImgView)).setImageResource(R.mipmap.ic_no_pic_user);
        } else {
            String image_url = CommonUtilities.SERVER_URL_PHOTOS + "upload/Driver/" + tripDataMap.get("iDriverId") + "/" + tripDataMap.get("DriverImage");
//            ((NetworkRoundedImageView) view.findViewById(R.id.driverImgView)).setImageUrl(image_url, AppController.getInstance().getImageLoader(), R.mipmap.ic_no_pic_user);
            // new DownloadImage(image_url, ((SelectableRoundedImageView) view.findViewById(R.id.driverImgView))).execute();
            Picasso.with(mainAct.getActContext())
                    .load(image_url)
                    .placeholder(R.mipmap.ic_no_pic_user)
                    .error(R.mipmap.ic_no_pic_user)
                    .into(((SelectableRoundedImageView) view.findViewById(R.id.driverImgView)));
        }

        mainAct.registerForContextMenu(view.findViewById(R.id.contact_btn));
        (view.findViewById(R.id.contactarea)).setOnClickListener(new setOnClickList());
        (view.findViewById(R.id.sharearea)).setOnClickListener(new setOnClickList());
        (view.findViewById(R.id.cancelarea)).setOnClickListener(new setOnClickList());
        (view.findViewById(R.id.msgarea)).setOnClickListener(new setOnClickList());
    }

    public String getDriverPhone() {
        return driverPhoneNum;
    }

    public void configTripStartView(String vDeliveryConfirmCode) {

        (view.findViewById(R.id.btn_cancle_trip)).setVisibility(View.GONE);
        cancelarea.setVisibility(View.GONE);

        if (!vDeliveryConfirmCode.trim().equals("") && !generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("UberX")) {

            mainAct.setUserLocImgBtnMargin(100);
            mainAct.setPanelHeight(205);
            this.vDeliveryConfirmCode = vDeliveryConfirmCode;
            ((MTextView) view.findViewById(R.id.deliveryConfirmCodeTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_DELIVERY_CONFIRMATION_CODE_TXT") + ": " + vDeliveryConfirmCode);
            ((MTextView) view.findViewById(R.id.deliveryConfirmCodeTxt)).setVisibility(View.VISIBLE);
        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.contactarea:
                    Utils.printLog("click", "perform");
                    //  mainAct.openContextMenu(view);
                    call(driverPhoneNum);
                    break;
                case R.id.sharearea:
                    /*Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                    intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                    startActivityForResult(intent, PICK_CONTACT);*/

                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                    String link_location = "http://maps.google.com/?q=" + mainAct.userLocation.getLatitude() + "," + mainAct.userLocation.getLongitude();
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, generalFunc.retrieveLangLBl("", "LBL_SEND_STATUS_CONTENT_TXT") + " " + link_location);
                    startActivity(Intent.createChooser(sharingIntent, "Share using"));

                    break;

                case R.id.cancelarea:
                    buildWarningMessage(generalFunc.retrieveLangLBl("", "LBL_TRIP_CANCEL_TXT"),
                            generalFunc.retrieveLangLBl("", "LBL_CANCEL_TRIP_NOW"),
                            generalFunc.retrieveLangLBl("", "LBL_CONTINUE_TRIP_TXT"), true);
                    break;

                case R.id.msgarea:
                    //  new StartActProcess(mainAct.getActContext()).startAct(ContactUsActivity.class);
                    // sendMsg(driverPhoneNum);
                    mainAct.chatMsg();

                    break;
            }
        }
    }

    public void sendMsg(String phoneNumber) {
        try {
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", "" + phoneNumber);
            startActivity(smsIntent);

        } catch (Exception e) {
        }
    }

    public void call(String phoneNumber) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        } catch (Exception e) {
        }
    }

    public void buildWarningMessage(String message, String positiveBtn, String negativeBtn, final boolean isCancelTripWarning) {

        android.support.v7.app.AlertDialog alertDialog;
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mainAct.getActContext());
        builder.setTitle("");
        builder.setCancelable(false);
        builder.setMessage(message);

        builder.setPositiveButton(positiveBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (isCancelTripWarning == true) {
                    cancelTrip();
                } else {
                    //generalFunc.restartApp();
                    generalFunc.restartwithGetDataApp();
                }
            }
        });
        builder.setNegativeButton(negativeBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

//        final GenerateAlertBox generateAlert = new GenerateAlertBox(mainAct.getActContext());
//        generateAlert.setCancelable(false);
//        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
//            @Override
//            public void handleBtnClick(int btn_id) {
//                generateAlert.closeAlertBox();
//
//                if (btn_id == 1) {
//                    if (isCancelTripWarning == true) {
//                        cancelTrip();
//                    } else {
//                        generalFunc.restartApp();
//                    }
//                }
//            }
//        });
//        generateAlert.setContentMessage("", message);
//        generateAlert.setPositiveBtn(positiveBtn);
//        if (!negativeBtn.equals("")) {
//            generateAlert.setNegativeBtn(negativeBtn);
//        }
//
//        generateAlert.showAlertBox();
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

//        final Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
//        positiveButton.setTextColor(getResources().getColor(R.color.gray));
//        final Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//        negativeButton.setTextColor(getResources().getColor(R.color.black));
    }

    public void cancelTrip() {
        HashMap<String, String> tripDataMap = (HashMap<String, String>) getArguments().getSerializable("TripData");

        if (!generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("UberX"))
        {
            ((LinearLayout) view.findViewById(R.id.driverCarDetailArea)).setVisibility(View.VISIBLE);
            ((MTextView) view.findViewById(R.id.driver_car_type)).setText("(" + tripDataMap.get("vVehicleType") + ")");
        } else {
            ((LinearLayout) view.findViewById(R.id.driverCarDetailArea)).setVisibility(View.GONE);
            ((MTextView) view.findViewById(R.id.driver_car_type)).setText("(" + tripDataMap.get("vVehicleType") + "-" + tripDataMap.get("iVehicleCatName") + ")");

        }

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "cancelTrip");
        parameters.put("UserType", CommonUtilities.app_type);
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("iDriverId", tripDataMap.get("iDriverId"));
        parameters.put("iTripId", tripDataMap.get("iTripId"));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActivity(), parameters);
        exeWebServer.setLoaderConfig(mainAct.getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    if (isDataAvail == true) {
                        // generalFunc.restartApp();
                        generalFunc.restartwithGetDataApp();
//                        generalFunc.autoLogin(null,mainAct.getActContext());
                    } else {
                        buildWarningMessage(generalFunc.retrieveLangLBl("", "LBL_REQUEST_FAILED_PROCESS"),
                                generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), "", false);
                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CONTACT && data != null) {
            Uri uri = data.getData();

            if (uri != null) {
                Cursor c = null;
                try {
                    c = mainAct.getContentResolver().query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.TYPE}, null, null, null);

                    if (c != null && c.moveToFirst()) {
                        String number = c.getString(0);

                        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                        smsIntent.setType("vnd.android-dir/mms-sms");
                        smsIntent.putExtra("address", "" + number);

                        String link_location = "http://maps.google.com/?q=" + mainAct.userLocation.getLatitude() + "," + mainAct.userLocation.getLongitude();
                        smsIntent.putExtra("sms_body", generalFunc.retrieveLangLBl("", "LBL_SEND_STATUS_CONTENT_TXT") + " " + link_location);
                        startActivity(smsIntent);
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
        }
    }
}
