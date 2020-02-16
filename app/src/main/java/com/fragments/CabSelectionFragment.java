package com.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.adapter.files.CabTypeAdapter;
import com.app85taxi.passenger.FareBreakDownActivity;
import com.app85taxi.passenger.MainActivity;
import com.app85taxi.passenger.R;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.anim.loader.AVLoadingIndicatorView;
import com.view.editBox.MaterialEditText;
import com.view.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class CabSelectionFragment extends Fragment implements CabTypeAdapter.OnItemClickList {


    // public LinearLayout rideBtnContainer;
    public MButton ride_now_btn;
    public int currentPanelDefaultStateHeight = 100;
    public String currentCabGeneralType;
    View view = null;
    static MainActivity mainAct;
    static GeneralFunctions generalFunc;
    String userProfileJson = "";
    RecyclerView carTypeRecyclerView;
    CabTypeAdapter adapter;
    ArrayList<HashMap<String, String>> cabTypeList;
    ArrayList<HashMap<String, String>> cabCategoryList;
    // MTextView personSizeVTxt;
    // LinearLayout minFareArea;
    String currency_sign = "";
    boolean isKilled = false;
    public String app_type = "Ride";
    LinearLayout paymentArea;
    LinearLayout promoArea;
    View payTypeSelectArea;
    String appliedPromoCode = "";
    public ImageView img_ridelater;
    boolean isCardValidated = true;
    static MTextView payTypeTxt;
    RadioButton cashRadioBtn;
    static RadioButton cardRadioBtn;

    LinearLayout casharea;
    LinearLayout cardarea;
    static ImageView payImgView;
    LinearLayout cashcardarea;
    public int isSelcted = -1;
    String distance = "";
    String time = "";

    AVLoadingIndicatorView loaderView;
    MTextView noServiceTxt;
    boolean isCardnowselcted = false;
    boolean isCardlaterselcted = false;
    boolean dialogShowOnce = true;
    String RideDeliveryType = "";
    public boolean isclickableridebtn = false;
    public boolean isroutefound = false;
    MTextView promoTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null) {
            return view;
        }

        view = inflater.inflate(R.layout.fragment_new_cab_selection, container, false);

        mainAct = (MainActivity) getActivity();
        generalFunc = mainAct.generalFunc;
        findRoute();
        RideDeliveryType = getArguments().getString("RideDeliveryType");

        carTypeRecyclerView = (RecyclerView) view.findViewById(R.id.carTypeRecyclerView);
        loaderView = (AVLoadingIndicatorView) view.findViewById(R.id.loaderView);
        payTypeSelectArea = view.findViewById(R.id.payTypeSelectArea);
        payTypeTxt = (MTextView) view.findViewById(R.id.payTypeTxt);
        promoTxt = (MTextView) view.findViewById(R.id.promoTxt);
        promoTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PROMO_TXT"));

        ride_now_btn = ((MaterialRippleLayout) view.findViewById(R.id.ride_now_btn)).getChildView();
        img_ridelater = (ImageView) view.findViewById(R.id.img_ridelater);
        noServiceTxt = (MTextView) view.findViewById(R.id.noServiceTxt);

        casharea = (LinearLayout) view.findViewById(R.id.casharea);
        cardarea = (LinearLayout) view.findViewById(R.id.cardarea);

        casharea.setOnClickListener(new setOnClickList());
        cardarea.setOnClickListener(new setOnClickList());

        img_ridelater.setOnClickListener(new setOnClickList());

        paymentArea = (LinearLayout) view.findViewById(R.id.paymentArea);
        promoArea = (LinearLayout) view.findViewById(R.id.promoArea);
        promoArea.setOnClickListener(new setOnClickList());
        paymentArea.setOnClickListener(new setOnClickList());
        cashRadioBtn = (RadioButton) view.findViewById(R.id.cashRadioBtn);
        cardRadioBtn = (RadioButton) view.findViewById(R.id.cardRadioBtn);

        payImgView = (ImageView) view.findViewById(R.id.payImgView);

        cashcardarea = (LinearLayout) view.findViewById(R.id.cashcardarea);

//        new CreateRoundedView(Color.parseColor("#FFFFFF"), 0, Utils.dipToPixels(mainAct.getActContext(), 1),
//                Color.parseColor("#DDDDDD"), cashRadioBtn);
//        new CreateRoundedView(Color.parseColor("#FFFFFF"), 0, Utils.dipToPixels(mainAct.getActContext(), 1),
//                Color.parseColor("#DDDDDD"), cardRadioBtn);


        userProfileJson = mainAct.userProfileJson;

        currency_sign = generalFunc.getJsonValue("CurrencySymbol", userProfileJson);
        app_type = generalFunc.getJsonValue("APP_TYPE", userProfileJson);
        if (app_type.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX)) {
            app_type = "Ride";
        }

        if (app_type.equals("UberX")) {
            view.setVisibility(View.GONE);
            return view;
        }

        isKilled = false;

//        ((RadioGroup) view.findViewById(R.id.paymentTypeRadioGroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                Utils.printLog("ID btn", "::" + i);
//                hidePayTypeSelectionArea();
//
//                if (radioGroup.getCheckedRadioButtonId() == R.id.cashRadioBtn) {
//                    setCashSelection();
//                } else if (isCardValidated == false /*&& (!generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("Delivery") &&
//                        !mainAct.getCurrentCabGeneralType().equals(Utils.CabGeneralType_Deliver))*/) {
////                    payTypeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CARD"));
//                    setCashSelection();
//                    checkCardConfig();
//                }
//            }
//        });


        if (generalFunc.getJsonValue("APP_PAYMENT_MODE", userProfileJson).equalsIgnoreCase("Cash")) {
            cashRadioBtn.setVisibility(View.VISIBLE);
            cardRadioBtn.setVisibility(View.GONE);
        } else if (generalFunc.getJsonValue("APP_PAYMENT_MODE", userProfileJson).equalsIgnoreCase("Card")) {
            cashRadioBtn.setVisibility(View.GONE);
            cardRadioBtn.setVisibility(View.VISIBLE);

            isCardValidated = true;
            setCardSelection();
            isCardValidated = false;
        }


        setLabels();

        ride_now_btn.setId(Utils.generateViewId());


        ride_now_btn.setOnClickListener(new setOnClickList());


        configRideLaterBtnArea(false);

        mainAct.sliding_layout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

                if (isKilled) {
                    return;
                }


//                Utils.printLog("Offest", "::" + slideOffset);

//                if (slideOffset < 0.1) {
//                    configRideLaterBtnArea(false);
//                } else {
//                    configRideLaterBtnArea(true);
//                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                if (isKilled) {
                    return;
                }

//                Utils.printLog("panel state", "::" + previousState);
//                Utils.printLog("panel state", "::" + newState);
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    configRideLaterBtnArea(false);
                }
            }
        });


        return view;
    }


    public void showLoader() {
        loaderView.setVisibility(View.VISIBLE);
    }

    public void showNoServiceText() {
        noServiceTxt.setVisibility(View.VISIBLE);
    }

    public void closeNoServiceText() {
        noServiceTxt.setVisibility(View.GONE);
    }


    public void closeLoader() {
        loaderView.setVisibility(View.GONE);
        if (mainAct.cabTypesArrList.size() == 0) {
            showNoServiceText();
        } else {
            closeNoServiceText();
        }
    }

    public void setUserProfileJson() {
        userProfileJson = mainAct.userProfileJson;
    }

    public void checkCardConfig() {
        setUserProfileJson();

        String vStripeCusId = generalFunc.getJsonValue("vStripeCusId", userProfileJson);

        if (vStripeCusId.equals("")) {
            // Open CardPaymentActivity
            mainAct.OpenCardPaymentAct(true);
        } else {
            showPaymentBox();
        }
    }

    //    public void showPaymentBox() {
//        android.support.v7.app.AlertDialog alertDialog;
//        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
//        builder.setTitle("");
//        builder.setCancelable(false);
//        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View dialogView = inflater.inflate(R.layout.input_box_view, null);
//        builder.setView(dialogView);
//
//        final MaterialEditText input = (MaterialEditText) dialogView.findViewById(R.id.editBox);
//        final MTextView subTitleTxt = (MTextView) dialogView.findViewById(R.id.subTitleTxt);
//
//        Utils.removeInput(input);
//
//        subTitleTxt.setVisibility(View.VISIBLE);
//        subTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TITLE_PAYMENT_ALERT"));
//        input.setText(generalFunc.getJsonValue("vCreditCard", userProfileJson));
//
//
//        builder.setPositiveButton(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        builder.setNeutralButton(generalFunc.retrieveLangLBl("Confirm", "LBL_BTN_TRIP_CANCEL_CONFIRM_TXT"), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//                checkPaymentCard();
//            }
//        });
//        builder.setNegativeButton(generalFunc.retrieveLangLBl("Change", "LBL_CHANGE"), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                mainAct.OpenCardPaymentAct(true);
//            }
//        });
//
//
//        alertDialog = builder.create();
//        alertDialog.setCancelable(false);
//        alertDialog.setCanceledOnTouchOutside(false);
//        alertDialog.show();
//
//        final Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
//        positiveButton.setTextColor(getResources().getColor(R.color.gray));
//        final Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//        negativeButton.setTextColor(getResources().getColor(R.color.black));
//        final Button neutraleButton = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
//        neutraleButton.setTextColor(getResources().getColor(R.color.appThemeColor_1));
//    }
    public void showPaymentBox() {
        android.support.v7.app.AlertDialog alertDialog;
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        builder.setTitle("");
        builder.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.input_box_view, null);
        builder.setView(dialogView);

        final MaterialEditText input = (MaterialEditText) dialogView.findViewById(R.id.editBox);
        final MTextView subTitleTxt = (MTextView) dialogView.findViewById(R.id.subTitleTxt);

        Utils.removeInput(input);

        subTitleTxt.setVisibility(View.VISIBLE);
        subTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TITLE_PAYMENT_ALERT"));
        input.setText(generalFunc.getJsonValue("vCreditCard", userProfileJson));

        builder.setPositiveButton(generalFunc.retrieveLangLBl("Confirm", "LBL_BTN_TRIP_CANCEL_CONFIRM_TXT"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                checkPaymentCard();
            }
        });
        builder.setNeutralButton(generalFunc.retrieveLangLBl("Change", "LBL_CHANGE"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                mainAct.OpenCardPaymentAct(true);
            }
        });
        builder.setNegativeButton(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void setCashSelection() {
        payTypeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CASH_TXT"));

        isCardValidated = false;
        mainAct.setCashSelection(true);
        cashRadioBtn.setChecked(true);

        payImgView.setImageResource(R.mipmap.ic_cash_new);
    }

    public static void setCardSelection() {
        if (generalFunc == null) {
            generalFunc = mainAct.generalFunc;
        }
        payTypeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CARD"));


        mainAct.setCashSelection(false);

        cardRadioBtn.setChecked(true);

        payImgView.setImageResource(R.mipmap.ic_card_new);
    }


    public void checkPaymentCard() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "CheckCard");
        parameters.put("iUserId", generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    String action = generalFunc.getJsonValue(CommonUtilities.action_str, responseString);
                    if (action.equals("1")) {

                        isCardValidated = true;
                        setCardSelection();

                        if (isCardnowselcted) {
                            isCardnowselcted = false;


                            if (mainAct.getCurrentCabGeneralType().equals("Deliver")) {
                                if (mainAct.getDestinationStatus() == false) {
                                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Please add your destination location " +
                                            "to deliver your package.", "LBL_ADD_DEST_MSG_DELIVER_ITEM"));
                                    return;
                                }
                                mainAct.setDeliverySchedule();
                                return;
                            } else {
                                if (!mainAct.getCabReqType().equals(Utils.CabReqType_Later)) {
                                    //  mainAct.requestPickUp();
                                    mainAct.continuePickUpProcess();
                                } else {
                                    mainAct.setRideSchedule();
                                }

                            }
                        }

                        if (isCardlaterselcted) {
                            isCardlaterselcted = false;
                            mainAct.chooseDateTime();
                        }
//                        if (mainAct.getCurrentCabGeneralType().equals(Utils.CabGeneralType_Deliver)) {
//                            mainAct.setCashSelection(false);
//                            mainAct.setDeliverySchedule();
//                            return;
//                        }
//                        performClickOnPayment();
                    } else {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }


    public void setLabels() {


        ride_now_btn.setText(generalFunc.retrieveLangLBl("Request Now", "LBL_REQUEST_NOW"));

        noServiceTxt.setText(generalFunc.retrieveLangLBl("service not available in this location", "LBL_NO_SERVICE_AVAILABLE_TXT"));

        if (RideDeliveryType.equals("")) {



            if (app_type.equalsIgnoreCase(Utils.CabGeneralType_Ride)) {
                currentCabGeneralType = Utils.CabGeneralType_Ride;
                //changeCabGeneralType(generalFunc.getJsonValue("APP_TYPE", userProfileJson));

                generateCarType();
            }

            if (app_type.equalsIgnoreCase("Ride-Delivery")) {
                currentCabGeneralType = Utils.CabGeneralType_Ride;
                generateCarType();

            }
        } else {

            if (RideDeliveryType.equals(Utils.CabGeneralType_Deliver)) {
                currentCabGeneralType = "Deliver";
                ride_now_btn.setText(generalFunc.retrieveLangLBl("Deliver Now", "LBL_DELIVER_NOW"));
                generateCarType();

            } else if (RideDeliveryType.equals(Utils.CabGeneralType_Ride)) {
                currentCabGeneralType = Utils.CabGeneralType_Ride;
                generateCarType();

            }
        }
    }

    public void releaseResources() {
        isKilled = true;
    }

    public void changeCabGeneralType(String currentCabGeneralType) {
        if (!this.currentCabGeneralType.equals(currentCabGeneralType)) {
            this.currentCabGeneralType = currentCabGeneralType;

            Utils.printLog("changeCabGenralType", "");
            generateCarType();

        }
    }

    public String getCurrentCabGeneralType() {
        return this.currentCabGeneralType;
    }

    public void configRideLaterBtnArea(boolean isGone) {
        if (isGone == true || app_type.equalsIgnoreCase("Ride-Delivery")) {
            mainAct.setPanelHeight(232);
            if (!app_type.equalsIgnoreCase("Ride-Delivery")) {
                mainAct.setUserLocImgBtnMargin(105);
            }
            return;
        }
        if (!generalFunc.getJsonValue("RIIDE_LATER", userProfileJson).equalsIgnoreCase("YES") && !app_type.equalsIgnoreCase("Ride-Delivery")) {
            mainAct.setUserLocImgBtnMargin(105);
            mainAct.setPanelHeight(232);
        } else {

            mainAct.setPanelHeight(232);
            currentPanelDefaultStateHeight = 232;
            mainAct.setUserLocImgBtnMargin(164);
        }
//        mainAct.sliding_layout.invalidate();
    }

    public void setadpterData() {
        try {
            Utils.printLog("cabtypelist", cabTypeList.size() + "");
            adapter = new CabTypeAdapter(getActContext(), cabTypeList, generalFunc);
            carTypeRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            adapter.setOnItemClickList(this);
        } catch (Exception e) {

        }
    }

    public void generateCarType() {

        Utils.printLog("generateCarTypeCalled", "");

        if (cabTypeList == null) {
            Utils.printLog("cabTypelistCreate", "");
            cabTypeList = new ArrayList<>();
            Utils.printLog("cabtypelist", cabTypeList.size() + "");
            adapter = new CabTypeAdapter(getActContext(), cabTypeList, generalFunc);
            carTypeRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            cabTypeList.clear();
            Utils.printLog("cabtypelistClear", "");
        }

        // JSONArray vehicleTypesArr = generalFunc.getJsonArray("VehicleTypes", userProfileJson);


        for (int i = 0; i < mainAct.cabTypesArrList.size(); i++) {
            //  JSONObject obj_temp = generalFunc.getJsonObject(vehicleTypesArr, i);

            HashMap<String, String> map = new HashMap<>();
//            String iVehicleTypeId = generalFunc.getJsonValue("iVehicleTypeId", obj_temp.toString());
//
//            String vVehicleType = generalFunc.getJsonValue("vVehicleType", obj_temp.toString());
//            String fPricePerKM = generalFunc.getJsonValue("fPricePerKM", obj_temp.toString());
//            String fPricePerMin = generalFunc.getJsonValue("fPricePerMin", obj_temp.toString());
//            String iBaseFare = generalFunc.getJsonValue("iBaseFare", obj_temp.toString());
//            String fCommision = generalFunc.getJsonValue("fCommision", obj_temp.toString());
//            String iPersonSize = generalFunc.getJsonValue("iPersonSize", obj_temp.toString());
//            String vLogo = generalFunc.getJsonValue("vLogo", obj_temp.toString());
//            String eType = generalFunc.getJsonValue("eType", obj_temp.toString());
            String iVehicleTypeId = generalFunc.getJsonValue("iVehicleTypeId", mainAct.cabTypesArrList.get(i));

            String vVehicleType = generalFunc.getJsonValue("vVehicleType", mainAct.cabTypesArrList.get(i));
            String fPricePerKM = generalFunc.getJsonValue("fPricePerKM", mainAct.cabTypesArrList.get(i));
            String fPricePerMin = generalFunc.getJsonValue("fPricePerMin", mainAct.cabTypesArrList.get(i));
            String iBaseFare = generalFunc.getJsonValue("iBaseFare", mainAct.cabTypesArrList.get(i));
            String fCommision = generalFunc.getJsonValue("fCommision", mainAct.cabTypesArrList.get(i));
            String iPersonSize = generalFunc.getJsonValue("iPersonSize", mainAct.cabTypesArrList.get(i));
            String vLogo = generalFunc.getJsonValue("vLogo", mainAct.cabTypesArrList.get(i));
            String eType = generalFunc.getJsonValue("eType", mainAct.cabTypesArrList.get(i));
            Utils.printLog("beforeCountinue", "");
            if (!eType.equalsIgnoreCase(currentCabGeneralType)) {
                continue;
            }
            Utils.printLog("aftercountinue", "");
            map.put("iVehicleTypeId", iVehicleTypeId);
            map.put("vVehicleType", vVehicleType);
            map.put("fPricePerKM", fPricePerKM);
            map.put("fPricePerMin", fPricePerMin);
            map.put("iBaseFare", iBaseFare);
            map.put("fCommision", fCommision);
            map.put("iPersonSize", iPersonSize);
            map.put("vLogo", vLogo);

            if (cabTypeList.size() == 0) {
                map.put("isHover", "true");
            } else {
                map.put("isHover", "false");
            }

            cabTypeList.add(map);

        }

        adapter.notifyDataSetChanged();
//        adapter = new CabTypeAdapter(getActContext(), cabTypeList, generalFunc);
//        carTypeRecyclerView.setAdapter(adapter);
//        mainAct.setCabTypeList(cabTypeList);

        if (cabTypeList.size() == 0) {


        } else {
            adapter.clickOnItem(0);
        }
    }

    public void setShadow() {
        (view.findViewById(R.id.shadowView)).setVisibility(View.VISIBLE);
    }

    public Context getActContext() {
        return mainAct.getActContext();
    }

    @Override
    public void onItemClick(int position) {
        ArrayList<HashMap<String, String>> tempList = new ArrayList<>();
        tempList.addAll(cabTypeList);
        cabTypeList.clear();

        if (isSelcted == position) {
            // mainAct.showLoader();
            if (dialogShowOnce) {
                dialogShowOnce = false;
                Utils.printLog("allready select", "");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // mainAct.hideLoader();
                        openFareDetailsDilaog();
                    }
                }, 300);
            }

        } else {
            Utils.printLog("not select", "");
        }

        for (int i = 0; i < tempList.size(); i++) {
//            CabTypeAdapter.ViewHolder cabTypeViewHolder = (CabTypeAdapter.ViewHolder) carTypeRecyclerView.findViewHolderForAdapterPosition(i);
            HashMap<String, String> map = tempList.get(i);


            if (i != position) {
                map.put("isHover", "false");
            } else if (i == position) {

                map.put("isHover", "true");
                isSelcted = position;
            }
            cabTypeList.add(map);
        }

        if (position > (cabTypeList.size() - 1)) {
            return;
        }

        mainAct.changeCabType(cabTypeList.get(position).get("iVehicleTypeId"));

           /* adapter = new CabTypeAdapter(getActContext(), cabTypeList, generalFunc);
            carTypeRecyclerView.setAdapter(adapter);
            carTypeRecyclerView.smoothScrollToPosition(position);*/
        //  adapter.notifyDataSetChanged();
//            adapter.setOnItemClickList(this);

        //adapter = new CabTypeAdapter(getActContext(), cabTypeList, generalFunc);
        // carTypeRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        mainAct.setCabTypeList(cabTypeList);


    }

    public void openFareEstimateDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        builder.setTitle("");

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.fare_detail_design, null);
        builder.setView(dialogView);

        ((MTextView) dialogView.findViewById(R.id.fareDetailHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_FARE_DETAIL_TXT"));
        ((MTextView) dialogView.findViewById(R.id.baseFareHTxt)).setText(" " + generalFunc.retrieveLangLBl("", "LBL_BASE_FARE_TXT"));
        ((MTextView) dialogView.findViewById(R.id.parMinHTxt)).setText(" / " + generalFunc.retrieveLangLBl("", "LBL_MIN_TXT"));
        ((MTextView) dialogView.findViewById(R.id.parMinHTxt)).setVisibility(View.GONE);
        ((MTextView) dialogView.findViewById(R.id.andTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_AND_TXT"));
        ((MTextView) dialogView.findViewById(R.id.parKmHTxt)).setText(" / " + generalFunc.retrieveLangLBl("", "LBL_KM_TXT"));
        ((MTextView) dialogView.findViewById(R.id.parKmHTxt)).setVisibility(View.GONE);

        ((MTextView) dialogView.findViewById(R.id.baseFareVTxt)).setText(currency_sign + " " +
                generalFunc.getSelectedCarTypeData(mainAct.getSelectedCabTypeId(), "VehicleTypes", "iBaseFare", userProfileJson));

        ((MTextView) dialogView.findViewById(R.id.parMinVTxt)).setText(currency_sign + " " +
                generalFunc.getSelectedCarTypeData(mainAct.getSelectedCabTypeId(), "VehicleTypes", "fPricePerMin", userProfileJson) + " / " + generalFunc.retrieveLangLBl("", "LBL_MIN_TXT"));

        ((MTextView) dialogView.findViewById(R.id.parKmVTxt)).setText(currency_sign + " " +
                generalFunc.getSelectedCarTypeData(mainAct.getSelectedCabTypeId(), "VehicleTypes", "fPricePerKM", userProfileJson) + " / " + generalFunc.retrieveLangLBl("", "LBL_KM_TXT"));

//        generalFunc.getSelectedCarTypeData(getIntent().getStringExtra("SelectedCarId"), "VehicleTypes","vVehicleType", userProfileJson)
        builder.show();
    }

    public void hidePayTypeSelectionArea() {
        payTypeSelectArea.setVisibility(View.GONE);
        cashcardarea.setVisibility(View.VISIBLE);
        mainAct.setPanelHeight(232);
//        new CreateAnimation(payTypeSelectArea, true, getActContext(), R.anim.scale_top_bottom, 500).startAnimation();
    }

    public void checkPromoCode(final String promoCode) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "CheckPromoCode");
        parameters.put("PromoCode", promoCode);
        parameters.put("iUserId", generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    String action = generalFunc.getJsonValue(CommonUtilities.action_str, responseString);
                    if (action.equals("1")) {
                        appliedPromoCode = promoCode;
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMO_APPLIED"));
                    } else if (action.equals("01")) {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMO_USED"));
                    } else {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMO_INVALIED"));
                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

//    public void showPromoBox() {
//        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
//
//
//        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View dialogView = inflater.inflate(R.layout.input_box_view, null);
//        builder.setView(dialogView);
//
//        final MaterialEditText input = (MaterialEditText) dialogView.findViewById(R.id.editBox);
//        final MTextView subTitleTxt = (MTextView) dialogView.findViewById(R.id.subTitleTxt);
//
//        subTitleTxt.setVisibility(View.VISIBLE);
//        subTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PROMO_CODE_ENTER_TITLE"));
//        input.setGravity(Gravity.CENTER_HORIZONTAL);
//        // input.setTextAlignment();
//
//
//        if (!appliedPromoCode.equals("")) {
//            input.setText(appliedPromoCode);
//        }
//        builder.setNegativeButton(generalFunc.retrieveLangLBl("OK", "LBL_BTN_OK_TXT"), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                if (input.getText().toString().trim().equals("") && appliedPromoCode.equals("")) {
//                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_ENTER_PROMO"));
//                } else if (input.getText().toString().trim().equals("") && !appliedPromoCode.equals("")) {
//                    appliedPromoCode = "";
//                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMO_REMOVED"));
//                } else if (input.getText().toString().contains(" ")) {
//                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMO_INVALIED"));
//                } else {
//                    checkPromoCode(input.getText().toString().trim());
//                }
//
//            }
//        });
//        builder.setPositiveButton(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                dialog.cancel();
//
//            }
//        });
//
//        android.support.v7.app.AlertDialog alertDialog = builder.create();
//
//
//        if (generalFunc.isRTLmode() == true) {
//            generalFunc.forceRTLIfSupported(alertDialog);
//        }
//        alertDialog.show();
//        final Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
//        positiveButton.setTextColor(getResources().getColor(R.color.gray));
//        final Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//        negativeButton.setTextColor(getResources().getColor(R.color.black));
//        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialogInterface) {
//                Utils.hideKeyboard(mainAct);
//            }
//        });
//
//    }

    public void showPromoBox() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        builder.setTitle(generalFunc.retrieveLangLBl("", "LBL_PROMO_CODE_ENTER_TITLE"));

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.input_box_view, null);
        builder.setView(dialogView);

        final MaterialEditText input = (MaterialEditText) dialogView.findViewById(R.id.editBox);


        if (!appliedPromoCode.equals("")) {
            input.setText(appliedPromoCode);
        }
        builder.setPositiveButton(generalFunc.retrieveLangLBl("OK", "LBL_BTN_OK_TXT"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().trim().equals("") && appliedPromoCode.equals("")) {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_ENTER_PROMO"));
                } else if (input.getText().toString().trim().equals("") && !appliedPromoCode.equals("")) {
                    appliedPromoCode = "";
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMO_REMOVED"));
                } else if (input.getText().toString().contains(" ")) {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMO_INVALIED"));
                } else {
                    checkPromoCode(input.getText().toString().trim());
                }
            }
        });
        builder.setNegativeButton(generalFunc.retrieveLangLBl("", "LBL_SKIP_TXT"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        android.support.v7.app.AlertDialog alertDialog = builder.create();
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(alertDialog);
        }
        alertDialog.show();
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Utils.hideKeyboard(mainAct);
            }
        });

    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();

            if (i == R.id.minFareArea) {
                openFareEstimateDialog();
            } else if (i == ride_now_btn.getId()) {
                mainAct.setCabReqType(Utils.CabReqType_Now);
                if (mainAct.getDestinationStatus()) {
                    String destLocAdd = mainAct != null ? (mainAct.getDestAddress().equals(
                            generalFunc.retrieveLangLBl("", "LBL_SELECTING_LOCATION_TXT")) ? "" : mainAct.getDestAddress()) : "";
                    if (destLocAdd.equals("")) {
                        return;
                    }
                }

                if (isCardValidated == false && generalFunc.getJsonValue("APP_PAYMENT_MODE", userProfileJson).equalsIgnoreCase("Card")) {
                    isCardnowselcted = true;
                    isCardlaterselcted = false;
                    checkCardConfig();
                    return;
                }


                if (mainAct.getCurrentCabGeneralType().equals("Deliver")) {
                    if (mainAct.getDestinationStatus() == false) {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Please add your destination location " +
                                "to deliver your package.", "LBL_ADD_DEST_MSG_DELIVER_ITEM"));
                        return;
                    }
                    mainAct.setDeliverySchedule();
                    return;
                }


                if (!mainAct.getCabReqType().equals(Utils.CabReqType_Later)) {
                    //  mainAct.requestPickUp();
                    mainAct.continuePickUpProcess();
                } else {
                    mainAct.setRideSchedule();
                }
            } else if (i == img_ridelater.getId()) {
                if (cabTypeList.size() > 0) {
                    if (isCardValidated == false && generalFunc.getJsonValue("APP_PAYMENT_MODE", userProfileJson).equalsIgnoreCase("Card")) {
                        isCardlaterselcted = true;
                        isCardnowselcted = false;
                        checkCardConfig();
                        return;
                    }
                    mainAct.chooseDateTime();
                }
            } else if (i == R.id.paymentArea) {

                if (payTypeSelectArea.getVisibility() == View.VISIBLE) {
                    hidePayTypeSelectionArea();
                } else {


//                    if (generalFunc.getJsonValue("APP_PAYMENT_MODE", userProfileJson).equalsIgnoreCase("Cash") &&
//                            generalFunc.getJsonValue("APP_PAYMENT_MODE", userProfileJson).equalsIgnoreCase("Card")) {
                    if (generalFunc.getJsonValue("APP_PAYMENT_MODE", userProfileJson).equalsIgnoreCase("Cash-Card")) {
                        mainAct.setPanelHeight(283);
                        payTypeSelectArea.setVisibility(View.VISIBLE);
                        cashcardarea.setVisibility(View.GONE);
                    } else {
                        mainAct.setPanelHeight(283 - 48);
                    }
                }

            } else if (i == R.id.promoArea) {
                showPromoBox();
            } else if (i == R.id.cardarea) {
                hidePayTypeSelectionArea();
                //  if (isCardValidated == true /*&& (!generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("Delivery") &&
                //     !mainAct.getCurrentCabGeneralType().equals(Utils.CabGeneralType_Deliver))*/) {
//                    payTypeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CARD"));
                setCashSelection();
                checkCardConfig();
                //   }

            } else if (i == R.id.casharea) {
                hidePayTypeSelectionArea();
                setCashSelection();
            }
        }
    }

    public String getAppliedPromoCode() {
        return this.appliedPromoCode;
    }

    public void findRoute() {
        try {

            String originLoc = mainAct.getPickUpLocation().getLatitude() + "," + mainAct.getPickUpLocation().getLongitude();
            String destLoc = mainAct.getDestLocLatitude() + "," + mainAct.getDestLocLongitude();

            String serverKey = getResources().getString(R.string.google_api_get_address_from_location_serverApi);
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + originLoc + "&destination=" + destLoc + "&sensor=true&key=" + serverKey + "&language=" + generalFunc.retrieveValue(CommonUtilities.GOOGLE_MAP_LANGUAGE_CODE_KEY);

            Utils.printLog("Fareurl", "::" + url);
            ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), url, true);
            exeWebServer.setLoaderConfig(getActContext(), false, generalFunc);
            exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
                @Override
                public void setResponse(String responseString) {


                    if (responseString != null && !responseString.equals("")) {

                        String status = generalFunc.getJsonValue("status", responseString);

                        if (status.equals("OK")) {

                            JSONArray obj_routes = generalFunc.getJsonArray("routes", responseString);
                            if (obj_routes != null && obj_routes.length() > 0) {
                                JSONObject obj_legs = generalFunc.getJsonObject(generalFunc.getJsonArray("legs", generalFunc.getJsonObject(obj_routes, 0).toString()), 0);

                                distance = "" + (generalFunc.parseDoubleValue(0, generalFunc.getJsonValue("value",
                                        generalFunc.getJsonValue("distance", obj_legs.toString()).toString())));

                                time = "" + (generalFunc.parseDoubleValue(0, generalFunc.getJsonValue("value",
                                        generalFunc.getJsonValue("duration", obj_legs.toString()).toString())));

                                LatLng sourceLocation = new LatLng(generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("lat", generalFunc.getJsonValue("start_location", obj_legs.toString()))),
                                        generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("lng", generalFunc.getJsonValue("start_location", obj_legs.toString()))));

                                LatLng destLocation = new LatLng(generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("lat", generalFunc.getJsonValue("end_location", obj_legs.toString()))),
                                        generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("lng", generalFunc.getJsonValue("end_location", obj_legs.toString()))));

                                Utils.printLog("Fareurl", ":Data:" + distance + "::" + time);
                                estimateFare(distance, time);
                            }

                        } else {

                            estimateFare(null, null);
//                            generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl("", "LBL_ERROR_TXT"),
//                                    generalFunc.retrieveLangLBl("", "LBL_GOOGLE_DIR_NO_ROUTE"));
                        }

                    } else {
//                        generalFunc.showError();
                    }
                }
            });
            exeWebServer.execute();
        } catch (Exception e) {

        }
    }

    public void estimateFare(final String distance, String time) {
        //  loaderView.setVisibility(View.VISIBLE);

        if (distance == null && time == null) {
            generalFunc.showMessage(ride_now_btn, generalFunc.retrieveLangLBl("No Route Found", "LBL_NO_ROUTE_FOUND"));
            ride_now_btn.setEnabled(false);
            ride_now_btn.setTextColor(Color.parseColor("#BABABA"));
            img_ridelater.setEnabled(false);
            isroutefound = false;
        } else {
            if (mainAct.loadAvailCabs != null) {
                if (mainAct.loadAvailCabs.isAvailableCab) {
                    isroutefound = true;
                    if (!mainAct.timeval.equalsIgnoreCase("\n" + "--")) {
                        ride_now_btn.setEnabled(true);
                        ride_now_btn.setTextColor(getResources().getColor(R.color.btn_text_color_type2));
                        img_ridelater.setEnabled(true);
                    }
                }
            }
        }
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "estimateFareNew");
        parameters.put("iUserId", generalFunc.getMemberId());
        if (distance != null && time != null) {
            parameters.put("distance", distance);
            parameters.put("time", time);
        }
        parameters.put("SelectedCar", mainAct.getSelectedCabTypeId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    if (isDataAvail == true) {

                        JSONArray vehicleTypesArr = generalFunc.getJsonArray(CommonUtilities.message_str, responseString);
                        for (int i = 0; i < vehicleTypesArr.length(); i++) {

                            JSONObject obj_temp = generalFunc.getJsonObject(vehicleTypesArr, i);
                            if (distance != null) {

                                Utils.printLog("getCurrentCabGenralType", mainAct.getCurrentCabGeneralType());
                                Utils.printLog("eType", generalFunc.getJsonValue("eType", obj_temp.toString()));
                                if (generalFunc.getJsonValue("eType", obj_temp.toString()).equalsIgnoreCase(mainAct.getCurrentCabGeneralType())) {


                                    if (cabTypeList != null) {
                                        for (int k = 0; k < cabTypeList.size(); k++) {
                                            HashMap<String, String> map = cabTypeList.get(k);

                                            if (map.get("vVehicleType").equalsIgnoreCase(generalFunc.getJsonValue("vVehicleType", obj_temp.toString()))
                                                    && map.get("iVehicleTypeId").equalsIgnoreCase(generalFunc.getJsonValue("iVehicleTypeId", obj_temp.toString()))) {

                                                String totalfare = generalFunc.getJsonValue("total_fare", obj_temp.toString());
                                                if (!totalfare.equals("") && totalfare != null) {
                                                    map.put("total_fare", totalfare);
                                                    cabTypeList.set(k, map);
                                                }
                                            }

                                        }
                                    }


                                }
                            } else {
                                Utils.printLog("getCurrentCabGenralType", mainAct.getCurrentCabGeneralType());
                                Utils.printLog("eType", generalFunc.getJsonValue("eType", obj_temp.toString()));
                                if (generalFunc.getJsonValue("eType", obj_temp.toString()).equalsIgnoreCase(mainAct.getCurrentCabGeneralType())) {


                                    for (int k = 0; k < cabTypeList.size(); k++) {
                                        HashMap<String, String> map = cabTypeList.get(k);

                                        if (map.get("vVehicleType").equalsIgnoreCase(generalFunc.getJsonValue("vVehicleType", obj_temp.toString()))
                                                && map.get("iVehicleTypeId").equalsIgnoreCase(generalFunc.getJsonValue("iVehicleTypeId", obj_temp.toString()))) {
                                            map.put("total_fare", "");
                                            cabTypeList.set(k, map);
                                        }

                                    }


                                }
                            }

                        }
                        //adapter.notifyDataSetChanged();
                        setadpterData();


                    } else {

//                        generalFunc.showGeneralMessage("",
//                                generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                    }
                } else {
//                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }


    public void openFareDetailsDilaog() {

        if (cabTypeList.get(isSelcted).get("total_fare") != null && !cabTypeList.get(isSelcted).get("total_fare").equalsIgnoreCase("")) {
            String vehicleIconPath = CommonUtilities.SERVER_URL + "webimages/icons/VehicleType/";
            // final Dialog faredialog = new Dialog(getActContext());
            final BottomSheetDialog faredialog = new BottomSheetDialog(getActContext());

            View contentView = View.inflate(getContext(), R.layout.dailog_faredetails, null);
            faredialog.setContentView(contentView);
            BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) contentView.getParent());
            mBehavior.setPeekHeight(750);
            View bottomSheetView = faredialog.getWindow().getDecorView().findViewById(android.support.design.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheetView).setHideable(false);
            setCancelable(faredialog, false);


//            Window window = faredialog.getWindow();
//            WindowManager.LayoutParams wlp = window.getAttributes();
//            wlp.gravity = Gravity.BOTTOM;
//            window.setAttributes(wlp);
//            faredialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            // faredialog.setCancelable(false);
            //  faredialog.setCanceledOnTouchOutside(false);
            ImageView imagecar;
            final MTextView carTypeTitle, capacityHTxt, capacityVTxt, fareHTxt, fareVTxt, mordetailsTxt, farenoteTxt;
            MButton btn_type2;
            int submitBtnId;
            imagecar = (ImageView) faredialog.findViewById(R.id.imagecar);
            carTypeTitle = (MTextView) faredialog.findViewById(R.id.carTypeTitle);
            capacityHTxt = (MTextView) faredialog.findViewById(R.id.capacityHTxt);
            capacityVTxt = (MTextView) faredialog.findViewById(R.id.capacityVTxt);
            fareHTxt = (MTextView) faredialog.findViewById(R.id.fareHTxt);
            fareVTxt = (MTextView) faredialog.findViewById(R.id.fareVTxt);
            mordetailsTxt = (MTextView) faredialog.findViewById(R.id.mordetailsTxt);
            farenoteTxt = (MTextView) faredialog.findViewById(R.id.farenoteTxt);
            btn_type2 = ((MaterialRippleLayout) faredialog.findViewById(R.id.btn_type2)).getChildView();
            submitBtnId = Utils.generateViewId();
            btn_type2.setId(submitBtnId);


            capacityHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CAPACITY"));
            fareHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_FARE_TXT"));
            mordetailsTxt.setText(generalFunc.retrieveLangLBl("", "LBL_MORE_DETAILS"));
            farenoteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GENERAL_NOTE_FARE_EST"));
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_DONE"));


            carTypeTitle.setText(cabTypeList.get(isSelcted).get("vVehicleType"));
            fareVTxt.setText(generalFunc.convertNumberWithRTL(cabTypeList.get(isSelcted).get("total_fare")));
            if (RideDeliveryType.equals("")) {
                if (app_type.equals(Utils.CabGeneralType_Ride)) {
                    capacityVTxt.setText(generalFunc.convertNumberWithRTL(cabTypeList.get(isSelcted).get("iPersonSize")) + " " + generalFunc.retrieveLangLBl("", "LBL_PEOPLE_TXT"));

                } else {
                    capacityVTxt.setText("---");
                }

            } else {
                if (RideDeliveryType.equals(Utils.CabGeneralType_Ride)) {
                    capacityVTxt.setText(generalFunc.convertNumberWithRTL(cabTypeList.get(isSelcted).get("iPersonSize")) + " " + generalFunc.retrieveLangLBl("", "LBL_PEOPLE_TXT"));

                } else {
                    capacityVTxt.setText("---");
                }

            }

            Picasso.with(getActContext())
                    .load(vehicleIconPath + cabTypeList.get(isSelcted).get("iVehicleTypeId") + "/android/" + "xxxhdpi_hover_" +
                            cabTypeList.get(isSelcted).get("vLogo"))
                    .into(imagecar, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                        }
                    });


            btn_type2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogShowOnce = true;
                    faredialog.dismiss();

                }
            });

            mordetailsTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogShowOnce = true;
                    Bundle bn = new Bundle();
                    bn.putString("SelectedCar", cabTypeList.get(isSelcted).get("iVehicleTypeId"));
                    bn.putString("iUserId", generalFunc.getMemberId());
                    bn.putString("distance", distance);
                    bn.putString("time", time);
                    bn.putString("PromoCode", appliedPromoCode);
                    bn.putString("vVehicleType", cabTypeList.get(isSelcted).get("vVehicleType"));
                    new StartActProcess(getActContext()).startActWithData(FareBreakDownActivity.class, bn);
                    faredialog.dismiss();
                }
            });


            faredialog.show();

//            faredialog.applyStyle(styleId)
//                    .contentView(view)
//                    .heightParam(ViewGroup.LayoutParams.WRAP_CONTENT)
//                    .inDuration(500)
//                    .cancelable(true)
//            .show();
        }


    }


    public void setCancelable(Dialog dialogview, boolean cancelable) {
        final Dialog dialog = dialogview;
        View touchOutsideView = dialog.getWindow().getDecorView().findViewById(android.support.design.R.id.touch_outside);
        View bottomSheetView = dialog.getWindow().getDecorView().findViewById(android.support.design.R.id.design_bottom_sheet);

        if (cancelable) {
            touchOutsideView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog.isShowing()) {
                        dialog.cancel();
                    }
                }
            });
            BottomSheetBehavior.from(bottomSheetView).setHideable(true);
        } else {
            touchOutsideView.setOnClickListener(null);
            BottomSheetBehavior.from(bottomSheetView).setHideable(false);
        }
    }


}
