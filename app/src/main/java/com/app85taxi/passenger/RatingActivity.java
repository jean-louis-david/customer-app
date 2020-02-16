package com.app85taxi.passenger;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.ErrorView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;
import com.view.simpleratingbar.SimpleRatingBar;

import java.util.HashMap;

public class RatingActivity extends AppCompatActivity {

    String vehicleIconPath = CommonUtilities.SERVER_URL + "webimages/icons/VehicleType/";

    MTextView titleTxt;
    ImageView backImgView;

    GeneralFunctions generalFunc;

    ProgressBar loading;
    ErrorView errorView;
    MButton btn_type2;
    MTextView generalCommentTxt;
    CardView generalCommentArea;
    MaterialEditText commentBox;

    int submitBtnId;

    LinearLayout container;

    SimpleRatingBar ratingBar;
    String iTripId_str;
    LinearLayout uberXRatingLayoutArea, mainRatingArea;
    android.support.v7.app.AlertDialog giveTipAlertDialog;

    MTextView totalFareTxt;
    MTextView dateVTxt;
    MTextView promoAppliedVTxt;
    MTextView ratingHeaderTxt;
    float rating = 0;

    String tipamount = "";
    boolean isCollectTip = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        generalFunc = new GeneralFunctions(getActContext());


        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        loading = (ProgressBar) findViewById(R.id.loading);
        errorView = (ErrorView) findViewById(R.id.errorView);
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        commentBox = (MaterialEditText) findViewById(R.id.commentBox);
        generalCommentTxt = (MTextView) findViewById(R.id.generalCommentTxt);
        generalCommentArea = (CardView) findViewById(R.id.generalCommentArea);
        container = (LinearLayout) findViewById(R.id.container);
        ratingBar = (SimpleRatingBar) findViewById(R.id.ratingBar);


        uberXRatingLayoutArea = (LinearLayout) findViewById(R.id.uberXRatingLayoutArea);
        mainRatingArea = (LinearLayout) findViewById(R.id.mainRatingArea);

        totalFareTxt = (MTextView) findViewById(R.id.totalFareTxt);
        dateVTxt = (MTextView) findViewById(R.id.dateVTxt);
        promoAppliedVTxt = (MTextView) findViewById(R.id.promoAppliedVTxt);
        ratingHeaderTxt = (MTextView) findViewById(R.id.ratingHeaderTxt);

        submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);

        btn_type2.setOnClickListener(new setOnClickList());
        backImgView.setVisibility(View.GONE);
        setLabels();

        getFare();

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) titleTxt.getLayoutParams();
        params.setMargins(Utils.dipToPixels(getActContext(), 15), 0, 0, 0);
        titleTxt.setLayoutParams(params);


        commentBox.setSingleLine(false);
        commentBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        commentBox.setGravity(Gravity.TOP);
        commentBox.setHideUnderline(true);
        commentBox.setFloatingLabel(MaterialEditText.FLOATING_LABEL_NONE);
    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == submitBtnId) {
                if (ratingBar.getRating() < 1) {
                    generalFunc.showMessage(generalFunc.getCurrentView(RatingActivity.this), generalFunc.retrieveLangLBl("", "LBL_ERROR_RATING_DIALOG_TXT"));
                    return;
                }
                submitRating();
            }
        }
    }

    public Context getActContext() {
        return RatingActivity.this;
    }

    public void setLabels() {
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RATING"));
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_SUBMIT_TXT"));
        commentBox.setHint(generalFunc.retrieveLangLBl("", "LBL_WRITE_COMMENT_HINT_TXT"));
        dateVTxt.setText(generalFunc.retrieveLangLBl("", "LBL_MYTRIP_Trip_Date"));
        promoAppliedVTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DIS_APPLIED"));
        ratingHeaderTxt.setText(generalFunc.retrieveLangLBl("", "LBL_HOW_WAS_RIDE"));

        totalFareTxt.setText(generalFunc.retrieveLangLBl("Total Fare", "LBL_Total_Fare"));

    }

    public void getFare() {
        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }
        if (container.getVisibility() == View.VISIBLE) {
            container.setVisibility(View.GONE);
        }
        if (loading.getVisibility() != View.VISIBLE) {
            loading.setVisibility(View.VISIBLE);
        }

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "displayFare");
//        if (Utils.checkText(iTripId_str)) {
//            parameters.put("tripID", iTripId_str);
//        }
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("UserType", CommonUtilities.app_type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    closeLoader();
                    if (generalFunc.checkDataAvail(CommonUtilities.action_str, responseString) == true) {

                        String message = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);

                        String FormattedTripDate = generalFunc.getJsonValue("tTripRequestDateOrig", message);
                        String TotalFare = generalFunc.getJsonValue("TotalFare", message);
                        String fDiscount = generalFunc.getJsonValue("fDiscount", message);
                        String vDriverImage = generalFunc.getJsonValue("vDriverImage", message);
                        String CurrencySymbol = generalFunc.getJsonValue("CurrencySymbol", message);
                        String iVehicleTypeId = generalFunc.getJsonValue("iVehicleTypeId", message);
                        String iDriverId = generalFunc.getJsonValue("iDriverId", message);
                        String tEndLat = generalFunc.getJsonValue("tEndLat", message);
                        String tEndLong = generalFunc.getJsonValue("tEndLong", message);
                        String eCancelled = generalFunc.getJsonValue("eCancelled", message);
                        String vCancelReason = generalFunc.getJsonValue("vCancelReason", message);
                        String vCancelComment = generalFunc.getJsonValue("vCancelComment", message);
                        String vCouponCode = generalFunc.getJsonValue("vCouponCode", message);
                        String carImageLogo = generalFunc.getJsonValue("carImageLogo", message);
                        String iTripId = generalFunc.getJsonValue("iTripId", message);
                        String eType = generalFunc.getJsonValue("eType", message);
                        iTripId_str = iTripId;

                        ((MTextView) findViewById(R.id.dateTxt)).setText(generalFunc.getDateFormatedType(FormattedTripDate, Utils.OriginalDateFormate, Utils.dateFormateInList));
                        ((MTextView) findViewById(R.id.sourceAddressTxt)).setText(generalFunc.getJsonValue("tSaddress", message));
                        ((MTextView) findViewById(R.id.destAddressTxt)).setText(generalFunc.getJsonValue("tDaddress", message));
                        ((MTextView) findViewById(R.id.carType)).setText(generalFunc.getJsonValue("carTypeName", message));
                        ((MTextView) findViewById(R.id.fareTxt)).setText(CurrencySymbol + " " + generalFunc.convertNumberWithRTL(TotalFare));

                        LinearLayout towTruckdestAddrArea = (LinearLayout) findViewById(R.id.towTruckdestAddrArea);

                        if (eType.equalsIgnoreCase("UberX")) {
                            uberXRatingLayoutArea.setVisibility(View.GONE);
                            mainRatingArea.setVisibility(View.VISIBLE);

                            new CreateRoundedView(Color.parseColor("#54A626"), Utils.dipToPixels(getActContext(), 9), 0, 0, findViewById(R.id.ufxPickArea));


                            ((MTextView) findViewById(R.id.sourceAddressTxt)).setText(generalFunc.getJsonValue("tSaddress", message));
                            ((MTextView) findViewById(R.id.carType)).setText(generalFunc.getJsonValue("carTypeName", message));

                            if (generalFunc.getJsonValue("APP_DESTINATION_MODE", message).equalsIgnoreCase("Strict") || generalFunc.getJsonValue("APP_DESTINATION_MODE", message).equalsIgnoreCase("NonStrict")) {
                                Utils.printLog("ratepage", "ratepage");

                                Utils.printLog("ratepage", "ratepagetowtrucj are");
                                if (towTruckdestAddrArea.getVisibility() == View.GONE) {
                                    towTruckdestAddrArea.setVisibility(View.VISIBLE);
                                    ((MTextView) findViewById(R.id.destAddressTxt)).setText(generalFunc.getJsonValue("tDaddress", message));
                                    Utils.printLog("ratepage", "Dadd " + generalFunc.getJsonValue("tDaddress", message));
                                }
                            }

                            Utils.printLog("vLogoVehicleCategory", "vLogoVehicleCategory:" + generalFunc.getJsonValue("vLogoVehicleCategory", message));

                            setImages("", "", generalFunc.getJsonValue("vLogoVehicleCategoryPath", message), generalFunc.getJsonValue("vLogoVehicleCategory", message));

                        } else {

                            mainRatingArea.setVisibility(View.VISIBLE);
                            uberXRatingLayoutArea.setVisibility(View.GONE);

                            setImages(carImageLogo, iVehicleTypeId, "", "");

                        }

                        if (eCancelled.equals("Yes")) {
                            generalCommentTxt.setText(generalFunc.retrieveLangLBl("Trip is cancelled by driver. Reason:", "LBL_PREFIX_TRIP_CANCEL_DRIVER")
                                    + " " + vCancelReason);
                            generalCommentTxt.setVisibility(View.VISIBLE);
                            generalCommentArea.setVisibility(View.VISIBLE);
                        } else {
                            generalCommentTxt.setVisibility(View.GONE);
                            generalCommentArea.setVisibility(View.GONE);
                        }

                        if (!fDiscount.equals("") && !fDiscount.equals("0") && !fDiscount.equals("0.00")) {

                            ((MTextView) findViewById(R.id.promoAppliedTxt)).setText(CurrencySymbol + generalFunc.convertNumberWithRTL(fDiscount));

                            (findViewById(R.id.promoView)).setVisibility(View.VISIBLE);
                        } else {
                            ((MTextView) findViewById(R.id.promoAppliedTxt)).setText("--");

                        }

                        if (generalFunc.getJsonValue("ENABLE_TIP_MODULE", message).equalsIgnoreCase("Yes")) {
                            isCollectTip = true;
                            buildTipCollectMessage(generalFunc.retrieveLangLBl("", "LBL_TIP_TXT"), generalFunc.retrieveLangLBl("No,Thanks", "LBL_NO_THANKS"),
                                    generalFunc.retrieveLangLBl("Give Tip", "LBL_GIVE_TIP_TXT"));

                        } else {
                            isCollectTip = false;
//                            buildTripEndMessage(generalFunc.retrieveLangLBl("", "LBL_TRIP_FINISHED_TXT"),
//                                    generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), true);
                            //showBookingAlert(generalFunc.retrieveLangLBl("", "LBL_TRIP_FINISHED_TXT"));

                        }


                        container.setVisibility(View.VISIBLE);
                    } else {
                        generateErrorView();
                    }
                } else {
                    generateErrorView();
                }
            }
        });
        exeWebServer.execute();
    }

    public void setImages(String carImageLogo, String iVehicleTypeId, String vLogoVehicleCategoryPath, String vLogoVehicleCategory) {
        String imageName = "";
        String size = "";
        switch (getResources().getDisplayMetrics().densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                imageName = "mdpi_" + (carImageLogo.equals("") ? vLogoVehicleCategory : carImageLogo);
                size = "80";
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                imageName = "mdpi_" + (carImageLogo.equals("") ? vLogoVehicleCategory : carImageLogo);
                size = "80";
                break;
            case DisplayMetrics.DENSITY_HIGH:
                imageName = "hdpi_" + (carImageLogo.equals("") ? vLogoVehicleCategory : carImageLogo);
                size = "120";
                break;
            case DisplayMetrics.DENSITY_TV:
                imageName = "hdpi_" + (carImageLogo.equals("") ? vLogoVehicleCategory : carImageLogo);
                size = "120";
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                imageName = "xhdpi_" + (carImageLogo.equals("") ? vLogoVehicleCategory : carImageLogo);
                size = "160";
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                imageName = "xxhdpi_" + (carImageLogo.equals("") ? vLogoVehicleCategory : carImageLogo);
                size = "240";
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                imageName = "xxxhdpi_" + (carImageLogo.equals("") ? vLogoVehicleCategory : carImageLogo);
                size = "320";
                break;
        }

        Utils.printLog("imageName", ":" + imageName);

//        if (!carImageLogo.equals("")) {
//
//            Picasso.with(getActContext())
//                    .load(vehicleIconPath + iVehicleTypeId + "/android/" + imageName)
//                    .placeholder(R.mipmap.ic_car_default)
//                    .error(R.mipmap.ic_car_default)
//                    .into(((ImageView) findViewById(R.id.carImgView)));
//        } else {
//
//            Picasso.with(getActContext())
//                    .load(vLogoVehicleCategoryPath + "/android/" + imageName)
//                    .placeholder(R.mipmap.ic_no_icon)
//                    .error(R.mipmap.ic_no_icon)
//                    .into(((ImageView) findViewById(R.id.uberXcarImgView)));
//
//        }

//        new DownloadImage(
//                "https://maps.googleapis.com/maps/api/staticmap?zoom=16&size=" + size + "x" + size + "&maptype=roadmap&markers=color:blue|label:Sorce|"
//                        + endLatitude + "," + endLongitude,
//                ((SelectableRoundedImageView) findViewById(R.id.locationImgView))).execute();
    }

    public void submitRating() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "submitRating");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("tripID", iTripId_str);
        parameters.put("rating", "" + ratingBar.getRating());
        parameters.put("message", Utils.getText(commentBox));
        parameters.put("UserType", CommonUtilities.app_type);

        parameters.put("fAmount", tipamount);
        if (isCollectTip) {
            parameters.put("isCollectTip", "Yes");
        } else {
            parameters.put("isCollectTip", "No");

        }


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    if (isDataAvail == true) {

//                        if (generalFunc.getJsonValue("ENABLE_TIP_MODULE", responseString).equalsIgnoreCase("Yes")) {
//                            buildTipCollectMessage(generalFunc.retrieveLangLBl("", "LBL_TIP_TXT"), generalFunc.retrieveLangLBl("No,Thanks", "LBL_NO_THANKS"),
//                                    generalFunc.retrieveLangLBl("Give Tip", "LBL_GIVE_TIP_TXT"));
//                        } else {
////                            buildTripEndMessage(generalFunc.retrieveLangLBl("", "LBL_TRIP_FINISHED_TXT"),
////                                    generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), true);
//                            showBookingAlert(generalFunc.retrieveLangLBl("", "LBL_TRIP_FINISHED_TXT"));
//
//                        }

                        showBookingAlert(generalFunc.retrieveLangLBl("", "LBL_TRIP_FINISHED_TXT"));
                    } else {
                        generalFunc.showGeneralMessage("",
                                generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public void buildTripEndMessage(String message, String positiveBtn, final boolean isRestart) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {
                generateAlert.closeAlertBox();
                if (isRestart == true) {
                    generalFunc.restartApp();
                }
            }
        });
        generateAlert.setContentMessage("", message);
        generateAlert.setPositiveBtn(positiveBtn);
        generateAlert.showAlertBox();
    }

    public void showBookingAlert(String message) {
        android.support.v7.app.AlertDialog alertDialog;
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        builder.setTitle("");
        builder.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_booking_view, null);
        builder.setView(dialogView);

        final MTextView titleTxt = (MTextView) dialogView.findViewById(R.id.titleTxt);
        final MTextView mesasgeTxt = (MTextView) dialogView.findViewById(R.id.mesasgeTxt);


        titleTxt.setText(generalFunc.retrieveLangLBl("Booking Successful", "LBL_SUCCESS_FINISHED"));
        mesasgeTxt.setText(message);


        builder.setPositiveButton(generalFunc.retrieveLangLBl("", "LBL_OK_THANKS"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                // generalFunc.restartApp();
                generalFunc.restartwithGetDataApp();

            }
        });


        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


//        final Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
//        negativeButton.setTextColor(getResources().getColor(R.color.black));

    }

    public void buildTipCollectMessage(String message, String positiveBtn, String negativeBtn) {
//        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
//        generateAlert.setCancelable(false);
//
//        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
//            @Override
//            public void handleBtnClick(int btn_id) {
//                generateAlert.closeAlertBox();
//                if (btn_id == 1) {
//                    generalFunc.restartApp();
//                } else {
//                    showTipBox();
//                }
//            }
//        });
//        generateAlert.setContentMessage(generalFunc.retrieveLangLBl("TIP", "LBL_TIP_TITLE_TXT"), message);
//        generateAlert.setPositiveBtn(positiveBtn);
//        generateAlert.setNegativeBtn(negativeBtn);
//        generateAlert.showAlertBox();

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        //  builder.setTitle(generalFunc.retrieveLangLBl("", "LBL_TIP_AMOUNT_ENTER_TITLE"));

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.desgin_add_tip_layout, null);
        builder.setView(dialogView);

        final MaterialEditText tipAmountEditBox = (MaterialEditText) dialogView.findViewById(R.id.editBox);
        tipAmountEditBox.setVisibility(View.GONE);
        final MTextView giveTipTxtArea = (MTextView) dialogView.findViewById(R.id.giveTipTxtArea);
        final MTextView msgTxt = (MTextView) dialogView.findViewById(R.id.msgTxt);
        msgTxt.setVisibility(View.VISIBLE);
        final MTextView skipTxtArea = (MTextView) dialogView.findViewById(R.id.skipTxtArea);
        final MTextView titileTxt = (MTextView) dialogView.findViewById(R.id.titileTxt);
        titileTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TIP_TITLE_TXT"));
        msgTxt.setText(message);
        giveTipTxtArea.setText(negativeBtn);
        skipTxtArea.setText(positiveBtn);

        skipTxtArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //generalFunc.restartApp();
                giveTipAlertDialog.dismiss();
                //  generalFunc.restartwithGetDataApp();
                tipamount = 0 + "";
                isCollectTip = false;
            }
        });

        giveTipTxtArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                giveTipAlertDialog.dismiss();
                showTipBox();

            }
        });
        giveTipAlertDialog = builder.create();
        giveTipAlertDialog.setCancelable(false);
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(giveTipAlertDialog);
        }
        giveTipAlertDialog.show();
    }

    public void showTipBox() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        //  builder.setTitle(generalFunc.retrieveLangLBl("", "LBL_TIP_AMOUNT_ENTER_TITLE"));

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.desgin_add_tip_layout, null);
        builder.setView(dialogView);

        final MaterialEditText tipAmountEditBox = (MaterialEditText) dialogView.findViewById(R.id.editBox);
        tipAmountEditBox.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        final MTextView giveTipTxtArea = (MTextView) dialogView.findViewById(R.id.giveTipTxtArea);
        final MTextView skipTxtArea = (MTextView) dialogView.findViewById(R.id.skipTxtArea);
        final MTextView titileTxt = (MTextView) dialogView.findViewById(R.id.titileTxt);
        titileTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TIP_AMOUNT_ENTER_TITLE"));
        giveTipTxtArea.setText("" + generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        skipTxtArea.setText("" + generalFunc.retrieveLangLBl("", "LBL_SKIP_TXT"));

        skipTxtArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeyboard(getActContext());
                giveTipAlertDialog.dismiss();
                //generalFunc.restartApp();
                //  generalFunc.restartwithGetDataApp();
            }
        });

        giveTipTxtArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final boolean tipAmountEntered = Utils.checkText(tipAmountEditBox) ? true : Utils.setErrorFields(tipAmountEditBox, generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT"));
                if (tipAmountEntered == false) {
                    return;
                }
                Utils.hideKeyboard(getActContext());
                giveTipAlertDialog.dismiss();
                collectTip(Utils.getText(tipAmountEditBox));

            }
        });
        giveTipAlertDialog = builder.create();
        giveTipAlertDialog.setCancelable(false);
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(giveTipAlertDialog);
        }
        giveTipAlertDialog.show();

    }

    private void collectTip(String tipAmount) {


        tipamount = tipAmount;

//        HashMap<String, String> parameters = new HashMap<String, String>();
//        parameters.put("type", "collectTip");
//        parameters.put("iTripId", iTripId_str);
//        parameters.put("iMemberId", generalFunc.getMemberId());
//        parameters.put("fAmount", "" + tipAmount);
//
//        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
//        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
//        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
//            @Override
//            public void setResponse(String responseString) {
//
//                if (responseString != null && !responseString.equals("")) {
//
//                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);
//
//                    if (isDataAvail == true) {
//
//                        //generalFunc.restartApp();
//                        //    generalFunc.restartwithGetDataApp();
//
//                    } else {
//                        buildTipCollectErrorMessage(responseString);
//
//                    }
//                } else {
//                    generalFunc.showError();
//                }
//            }
//        });
//        exeWebServer.execute();
    }

    public void buildTipCollectErrorMessage(String responseString) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {
                generateAlert.closeAlertBox();
                showTipBox();
            }
        });
        generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)) + "" + generalFunc.getJsonValue("minValue", responseString));
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        generateAlert.showAlertBox();
    }

    public void closeLoader() {
        if (loading.getVisibility() == View.VISIBLE) {
            loading.setVisibility(View.GONE);
        }
    }

    public void generateErrorView() {

        closeLoader();

        generalFunc.generateErrorView(errorView, "LBL_ERROR_TXT", "LBL_NO_INTERNET_TXT");

        if (errorView.getVisibility() != View.VISIBLE) {
            errorView.setVisibility(View.VISIBLE);
        }
        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                getFare();
            }
        });
    }


    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
