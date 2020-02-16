package com.general.files;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.app85taxi.passenger.MainActivity;
import com.app85taxi.passenger.R;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.SelectableRoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Admin on 05-07-2016.
 */
public class LoadAvailableCab implements UpdateFrequentTask.OnTaskRunCalled {
    Context mContext;
    GeneralFunctions generalFunc;
    String selectedCabTypeId = "";
    Location pickUpLocation;
    GoogleMap gMapView;
    View parentView;
    ExecuteWebServerUrl currentWebTask;

    MainActivity mainAct;

    public ArrayList<HashMap<String, String>> listOfDrivers;
    ArrayList<Marker> driverMarkerList;

    String userProfileJson;

    int RESTRICTION_KM_NEAREST_TAXI = 4;
    int ONLINE_DRIVER_LIST_UPDATE_TIME_INTERVAL = 1 * 60 * 1000;
    int DRIVER_ARRIVED_MIN_TIME_PER_MINUTE = 3;

    UpdateFrequentTask updateDriverListTask;
    Dialog dialog = null;
    boolean isTaskKilled = false;

    public String pickUpAddress = "";
    public String currentGeoCodeResult = "";

    public String currentPickUpCity = "";
    public String currentPickUpCountry = "";
    public boolean isAvailableCab = false;

    public LoadAvailableCab(Context mContext, GeneralFunctions generalFunc, String selectedCabTypeId, Location pickUpLocation, GoogleMap gMapView, String userProfileJson) {
        this.mContext = mContext;
        this.generalFunc = generalFunc;
        this.selectedCabTypeId = selectedCabTypeId;
        this.pickUpLocation = pickUpLocation;
        this.gMapView = gMapView;
        this.userProfileJson = userProfileJson;

        if (mContext instanceof MainActivity) {
            mainAct = (MainActivity) mContext;
            parentView = generalFunc.getCurrentView(mainAct);
        }

        listOfDrivers = new ArrayList<>();
        driverMarkerList = new ArrayList<>();

        RESTRICTION_KM_NEAREST_TAXI = generalFunc.parseIntegerValue(4, generalFunc.getJsonValue("RESTRICTION_KM_NEAREST_TAXI", userProfileJson));
        ONLINE_DRIVER_LIST_UPDATE_TIME_INTERVAL = (generalFunc.parseIntegerValue(1, generalFunc.getJsonValue("ONLINE_DRIVER_LIST_UPDATE_TIME_INTERVAL", userProfileJson))) * 60 * 1000;
        DRIVER_ARRIVED_MIN_TIME_PER_MINUTE = generalFunc.parseIntegerValue(3, generalFunc.getJsonValue("DRIVER_ARRIVED_MIN_TIME_PER_MINUTE", userProfileJson));
    }

    public void setPickUpLocation(Location pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public void setCabTypeId(String selectedCabTypeId) {
        this.selectedCabTypeId = selectedCabTypeId;
    }

    public void changeCabs() {

        if (driverMarkerList.size() > 0) {
            filterDrivers(true);
        } else {
            checkAvailableCabs();
        }
    }

    private void checkAvailableCabs() {

        if (pickUpLocation == null) {
            pickUpLocation = mainAct.pickUpLocation;
            if (pickUpLocation == null) {
                return;
            }
        }
        if (updateDriverListTask == null) {
            updateDriverListTask = new UpdateFrequentTask(ONLINE_DRIVER_LIST_UPDATE_TIME_INTERVAL);
//            updateDriverListTask.startRepeatingTask();
            onResumeCalled();
            updateDriverListTask.setTaskRunListener(this);
        }
        if (currentWebTask != null) {
            currentWebTask.cancel(true);
            currentWebTask = null;
        }

        if (mainAct != null) {
            mainAct.notifyCarSearching();
        }

        if (listOfDrivers != null) {
            if (listOfDrivers.size() > 0) {
                listOfDrivers.clear();
            }
        }

//        if (self.mainScreenUv.requestPickUpView != nil) {
//            // Show loader onPlace Of cabTypeRecyclerView
//        }

        if (mainAct.cabSelectionFrag != null) {
            mainAct.cabSelectionFrag.showLoader();

        }

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "loadAvailableCab");
        parameters.put("PassengerLat", "" + pickUpLocation.getLatitude());
        parameters.put("PassengerLon", "" + pickUpLocation.getLongitude());
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("PickUpAddress", pickUpAddress);
        //  parameters.put("currentGeoCodeResult", Utils.removeWithSpace(currentGeoCodeResult));

        if (mainAct != null) {
            if (mainAct.cabSelectionFrag != null) {
                if (mainAct.isUfx) {
                    Utils.printLog("iVehicleTypeId", "iVehicleTypeId called");
                    parameters.put("iVehicleTypeId", mainAct.getSelectedCabTypeId());
                }
            } else {
                if (mainAct.app_type.equals("UberX")) {
                    Utils.printLog("iVehicleTypeId", "iVehicleTypeId called");
                    parameters.put("iVehicleTypeId", mainAct.getSelectedCabTypeId());
                }
            }
        }

        Utils.printLog("loadAvailableCab", "" + CommonUtilities.SERVER_URL_WEBSERVICE + parameters.toString());
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(mContext, parameters);
        this.currentWebTask = exeWebServer;
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    if (generalFunc.getJsonValue(CommonUtilities.message_str, responseString).equals("SESSION_OUT")) {
                        generalFunc.notifySessionTimeOut();
                        Utils.runGC();
                        return;
                    }

                    String currentCountry = generalFunc.getJsonValue("CurrentCountry", responseString);
                    String currentCity = generalFunc.getJsonValue("CurrentCity", responseString);

                    currentPickUpCity = currentCity;
                    currentPickUpCountry = currentCountry;
                    JSONArray vehicleTypesArr = generalFunc.getJsonArray("VehicleTypes", responseString);
                    ArrayList<String> tempCabTypesArrList = new ArrayList<>();

                    for (int i = 0; i < vehicleTypesArr.length(); i++) {
                        JSONObject tempObj = generalFunc.getJsonObject(vehicleTypesArr, i);

                        Utils.printLog("getCurrentCabGenralType", mainAct.getCurrentCabGeneralType());
                        Utils.printLog("eType", generalFunc.getJsonValue("eType", tempObj.toString()));
                        if (generalFunc.getJsonValue("eType", tempObj.toString()).equals(mainAct.getCurrentCabGeneralType())) {
                            tempCabTypesArrList.add(tempObj.toString());
                        }
                    }
//                    if (mainAct.cabTypesArrList!=null) {
//                        mainAct.cabTypesArrList.clear();
//                        mainAct.cabTypesArrList.addAll(tempCabTypesArrList);
                    if (mainAct.cabTypesArrList.size() > 0) {
                        mainAct.cabTypesArrList.clear();
                        mainAct.cabTypesArrList.addAll(tempCabTypesArrList);
                    }
//                    }
                    boolean isCarTypeChanged = isCarTypesArrChanged(tempCabTypesArrList);

                    if (mainAct.cabTypesArrList.size() == 0) {
                        mainAct.cabTypesArrList.addAll(tempCabTypesArrList);
                    }

                    if (isCarTypeChanged) {
                        mainAct.cabTypesArrList.clear();

//                        if(self.mainScreenUv.requestPickUpView != nil){
//                            self.mainScreenUv.cabTypeCollectionView.reloadData()
//                        }
                        mainAct.cabTypesArrList.addAll(tempCabTypesArrList);
                        mainAct.selectedCabTypeId = getFirstCarTypeID();
                        Utils.printLog("mainAct.selectedCabTypeId", "selectedCabTypeId" + mainAct.selectedCabTypeId + "");
                        selectedCabTypeId = getFirstCarTypeID();

                        if (mainAct.cabSelectionFrag != null) {
                            Utils.printLog("LoadavilabelCarType", "");
                            mainAct.cabSelectionFrag.isSelcted = -1;
                            mainAct.cabSelectionFrag.generateCarType();
                            mainAct.cabSelectionFrag.findRoute();
                        }

//                        if(self.mainScreenUv.requestPickUpView != nil){
//                            self.mainScreenUv.cabTypeCollectionView.reloadData()
//                        }
                    }
                    JSONArray availCabArr = generalFunc.getJsonArray("AvailableCabList", responseString);

                    for (int i = 0; i < availCabArr.length(); i++) {
                        JSONObject obj_temp = generalFunc.getJsonObject(availCabArr, i);

                        String carDetailsJson = generalFunc.getJsonValue("DriverCarDetails", obj_temp.toString());
                        HashMap<String, String> driverDataMap = new HashMap<String, String>();
                        driverDataMap.put("driver_id", generalFunc.getJsonValue("iDriverId", obj_temp.toString()));
                        driverDataMap.put("Name", generalFunc.getJsonValue("vName", obj_temp.toString()));
                        driverDataMap.put("LastName", generalFunc.getJsonValue("vLastName", obj_temp.toString()));
                        driverDataMap.put("Latitude", generalFunc.getJsonValue("vLatitude", obj_temp.toString()));
                        driverDataMap.put("Longitude", generalFunc.getJsonValue("vLongitude", obj_temp.toString()));
                        driverDataMap.put("GCMID", generalFunc.getJsonValue("iGcmRegId", obj_temp.toString()));
                        driverDataMap.put("iAppVersion", generalFunc.getJsonValue("iAppVersion", obj_temp.toString()));
                        driverDataMap.put("driver_img", generalFunc.getJsonValue("vImage", obj_temp.toString()));
                        driverDataMap.put("average_rating", generalFunc.getJsonValue("vAvgRating", obj_temp.toString()));
                        driverDataMap.put("vPhone_driver", generalFunc.getJsonValue("vPhone", obj_temp.toString()));
                        driverDataMap.put("tProfileDescription", generalFunc.getJsonValue("tProfileDescription", obj_temp.toString()));


                        driverDataMap.put("DriverGender", generalFunc.getJsonValue("eGender", obj_temp.toString()));
                        driverDataMap.put("eFemaleOnlyReqAccept", generalFunc.getJsonValue("eFemaleOnlyReqAccept", obj_temp.toString()));

                        driverDataMap.put("eHandiCapAccessibility", generalFunc.getJsonValue("eHandiCapAccessibility", carDetailsJson));
                        driverDataMap.put("vCarType", generalFunc.getJsonValue("vCarType", carDetailsJson));
                        driverDataMap.put("vColour", generalFunc.getJsonValue("vColour", carDetailsJson));
                        driverDataMap.put("vLicencePlate", generalFunc.getJsonValue("vLicencePlate", carDetailsJson));
                        driverDataMap.put("make_title", generalFunc.getJsonValue("make_title", carDetailsJson));
                        driverDataMap.put("model_title", generalFunc.getJsonValue("model_title", carDetailsJson));
                        driverDataMap.put("fAmount", generalFunc.getJsonValue("fAmount", carDetailsJson));

                        Utils.printLog("driverDataMap", driverDataMap.toString());
                        listOfDrivers.add(driverDataMap);
                    }
                    Utils.printLog("app_type", "app_type"+" "+generalFunc.getJsonValue("APP_TYPE", userProfileJson));
                    Utils.printLog("listdriversize", listOfDrivers.size() + "");
                    Utils.printLog("availCabArr.length()", availCabArr.length() + "");

                    if (availCabArr.length() == 0) {
                        removeDriversFromMap(true);
                        if (mainAct != null) {
                            mainAct.notifyNoCabs();
                        }
                    } else {
                        Utils.printLog("filter driver", "called");
                        filterDrivers(false);
                    }

                } else {
                    removeDriversFromMap(true);
                    if (parentView != null) {
                        generalFunc.showMessage(parentView, generalFunc.retrieveLangLBl("", "LBL_NO_INTERNET_TXT"));
                    }

                    if (mainAct != null) {
                        mainAct.notifyNoCabs();
                    }
                }
//        if (self.mainScreenUv.requestPickUpView != nil) {
//            // hide loader onPlace Of cabTypeRecyclerView

                if (mainAct.cabSelectionFrag != null) {
                    mainAct.cabSelectionFrag.closeLoader();

                }
//        }
            }
        });
        exeWebServer.execute();
    }

    public boolean isCarTypesArrChanged(ArrayList<String> carTypeList) {
        if (mainAct.cabTypesArrList.size() != carTypeList.size()) {
            return true;
        }

        for (int i = 0; i < carTypeList.size(); i++) {
            String iVehicleTypeId = generalFunc.getJsonValue("iVehicleTypeId", mainAct.cabTypesArrList.get(i));
            String newVehicleTypeId = generalFunc.getJsonValue("iVehicleTypeId", carTypeList.get(i));

            if (!iVehicleTypeId.equals(newVehicleTypeId)) {
                return true;
            }
        }
        return false;
    }

    public String getFirstCarTypeID() {
        for (int i = 0; i < mainAct.cabTypesArrList.size(); i++) {
            String iVehicleTypeId = generalFunc.getJsonValue("iVehicleTypeId", mainAct.cabTypesArrList.get(i));

            return iVehicleTypeId;
        }
        return "";
    }

    public void setTaskKilledValue(boolean isTaskKilled) {
        this.isTaskKilled = isTaskKilled;

        if (isTaskKilled == true) {
            onPauseCalled();
        }
    }

    public void removeDriversFromMap(boolean isUnSubscribeAll) {
        if (driverMarkerList.size() > 0) {
            for (int i = 0; i < driverMarkerList.size(); i++) {
                Marker marker_temp = driverMarkerList.get(0);
                marker_temp.remove();
                driverMarkerList.remove(0);
            }
        }

        // Remove listener of channels (unsuscribe) of drivers from pubnub

        if (mainAct != null && mainAct.configPubNub != null && isUnSubscribeAll == true) {
            mainAct.configPubNub.unSubscribeToChannels(mainAct.getDriverLocationChannelList());
        }
    }

    public ArrayList<Marker> getDriverMarkerList() {
        return this.driverMarkerList;
    }

    public void filterDrivers(boolean isCheckAgain) {

        if (pickUpLocation == null) {
            generalFunc.restartApp();
            return;
        }

        double lowestKM = 0.0;
        boolean isFirst_lowestKM = true;

        ArrayList<HashMap<String, String>> currentLoadedDrivers = new ArrayList<>();
//        removeDriversFromMap(false);

        ArrayList<Marker> driverMarkerList_temp = new ArrayList<>();
        Utils.printLog("app_type", "app_type"+" "+generalFunc.getJsonValue("APP_TYPE", userProfileJson));
        Utils.printLog("listdriversize", listOfDrivers.size() + "");
        for (int i = 0; i < listOfDrivers.size(); i++) {
            HashMap<String, String> driverData = listOfDrivers.get(i);

            String driverName = driverData.get("Name");
            String[] vCarType = driverData.get("vCarType").split(",");

            boolean isCarSelected = Arrays.asList(vCarType).contains(selectedCabTypeId);

            Utils.printLog("isCarSelected", isCarSelected + "");
            String eHandiCapAccessibility = driverData.get("eHandiCapAccessibility");
            String eFemaleOnlyReqAccept = driverData.get("eFemaleOnlyReqAccept");
            String DriverGender = driverData.get("DriverGender");

//            if ((mainAct.ishandicap && !eHandiCapAccessibility.equalsIgnoreCase("yes"))) {
//                Utils.printLog("ishandicap", "called");
//                continue;
//            }
//
//            if ((eFemaleOnlyReqAccept.equalsIgnoreCase("yes") && generalFunc.getJsonValue("eGender", mainAct.userProfileJson).equalsIgnoreCase("Male"))) {
//                Utils.printLog("eFemaleOnlyReqAccept", "eFemaleOnlyReqAccept");
//                continue;
//            }
//
//            if ((mainAct.isfemale && DriverGender.equalsIgnoreCase("Male"))) {
//                Utils.printLog("isfemale", "called");
//                continue;
//
//            }
//
//            if (isCarSelected == false) {
//                Utils.printLog("isCarSelected", "called");
//                continue;
//            }

            if ((isCarSelected == false) ||
                    (mainAct.ishandicap == true && !eHandiCapAccessibility.equalsIgnoreCase("yes")) ||
                    (eFemaleOnlyReqAccept.equalsIgnoreCase("yes") && generalFunc.getJsonValue("eGender", mainAct.userProfileJson).equalsIgnoreCase("Male")) ||
                    (mainAct.isfemale == true && DriverGender.equalsIgnoreCase("Male"))) {
                Utils.printLog("countinue", "called");
                continue;
            }

            double driverLocLatitude = generalFunc.parseDoubleValue(0.0, driverData.get("Latitude"));
            double driverLocLongitude = generalFunc.parseDoubleValue(0.0, driverData.get("Longitude"));

            if (mainAct.pickUpLocation == null) {
                return;
            }
            double distance = Utils.CalculationByLocation(mainAct.pickUpLocation.getLatitude(), mainAct.pickUpLocation.getLongitude(), driverLocLatitude, driverLocLongitude, "");

            if (isFirst_lowestKM == true) {
                lowestKM = distance;
                isFirst_lowestKM = false;
            } else {
                if (distance < lowestKM) {
                    lowestKM = distance;
                }
            }

            if (distance < RESTRICTION_KM_NEAREST_TAXI) {
                driverData.put("DIST_TO_PICKUP", "" + distance);
                driverData.put("DIST_TO_PICKUP_INT", "" + ((int) distance));
                driverData.put("LBL_BTN_REQUEST_PICKUP_TXT", "" + generalFunc.retrieveLangLBl("", "LBL_BTN_REQUEST_PICKUP_TXT"));
                driverData.put("LBL_SEND_REQUEST", "" + generalFunc.retrieveLangLBl("", "LBL_SEND_REQ"));
                driverData.put("LBL_MORE_INFO_TXT", "" + generalFunc.retrieveLangLBl("More info", "LBL_MORE_INFO"));
                driverData.put("LBL_AWAY", "" + generalFunc.retrieveLangLBl("away", "LBL_AWAY"));
                driverData.put("LBL_KM_DISTANCE_TXT", "" + generalFunc.retrieveLangLBl("", "LBL_KM_DISTANCE_TXT"));
                currentLoadedDrivers.add(driverData);

                Marker driverMarker_temp = mainAct.getDriverMarkerOnPubNubMsg(driverData.get("driver_id"), true);

                if (driverMarker_temp == null) {
                    Marker driverMarker = drawMarker(new LatLng(driverLocLatitude, driverLocLongitude), driverName, driverData);
                    driverMarkerList_temp.add(driverMarker);
                } else {

                    if (!generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("UberX")) {
                        Utils.printLog("app_type", "app_type");
                        String eIconType = generalFunc.getSelectedCarTypeData(selectedCabTypeId, "VehicleTypes", "eIconType", userProfileJson);

                        int iconId = R.drawable.car_driver;
                        if (eIconType.equalsIgnoreCase("Bike")) {
                            iconId = R.mipmap.car_driver_1;
                        } else if (eIconType.equalsIgnoreCase("Cycle")) {
                            iconId = R.mipmap.car_driver_2;
                        }

                        driverMarker_temp.remove();
                        Marker driverMarker = drawMarker(new LatLng(driverMarker_temp.getPosition().latitude, driverMarker_temp.getPosition().longitude),
                                driverName, driverData);
                        driverMarker.setRotation(driverMarker_temp.getRotation());
                        driverMarker.setIcon(BitmapDescriptorFactory.fromResource(iconId));

//                        try {
//
//                            driverMarker_temp.setIcon(BitmapDescriptorFactory.fromResource(iconId));
//                        } catch (Exception e) {
//                            Utils.printLog("driverMarker_temp Ex", e.toString());
//                            driverMarker_temp.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                        }
                        driverMarkerList_temp.add(driverMarker);

                    } else {
                        driverMarkerList_temp.add(driverMarker_temp);

                    }
                    if (mainAct != null) {
                        if (mainAct.isUfx) {
                            driverMarkerList_temp.add(driverMarker_temp);

                        }
                    }

                }

            } else {
                gMapView.clear();
            }
        }
        Utils.printLog("driverMarkerList", driverMarkerList.size() + "");
        removeDriversFromMap(false);
        driverMarkerList.addAll(driverMarkerList_temp);

        if (mainAct != null) {
            int lowestTime = ((int) (lowestKM * DRIVER_ARRIVED_MIN_TIME_PER_MINUTE));
            if (lowestTime < DRIVER_ARRIVED_MIN_TIME_PER_MINUTE) {
                isAvailableCab = true;
                mainAct.setETA("" + DRIVER_ARRIVED_MIN_TIME_PER_MINUTE + "\n" + generalFunc.retrieveLangLBl("", "LBL_MIN_SMALL_TXT"));
            } else {
                isAvailableCab = false;
                mainAct.setETA("" + lowestTime + "\n" + generalFunc.retrieveLangLBl("", "LBL_MIN_SMALL_TXT"));
            }

        }
        if (mainAct != null) {

            ArrayList<String> unSubscribeChannelList = new ArrayList<>();
            ArrayList<String> subscribeChannelList = new ArrayList<>();

            ArrayList<String> currentDriverChannelsList = mainAct.getDriverLocationChannelList();
            ArrayList<String> newDriverChannelsList = mainAct.getDriverLocationChannelList(currentLoadedDrivers);

            for (int i = 0; i < currentDriverChannelsList.size(); i++) {
                String channel_name = currentDriverChannelsList.get(i);
                if (!newDriverChannelsList.contains(channel_name)) {
                    unSubscribeChannelList.add(channel_name);
                }
            }

            for (int i = 0; i < newDriverChannelsList.size(); i++) {
                String channel_name = newDriverChannelsList.get(i);
                if (!currentDriverChannelsList.contains(channel_name)) {
                    subscribeChannelList.add(channel_name);
                }
            }

            mainAct.setCurrentLoadedDriverList(currentLoadedDrivers);

            if (mainAct.configPubNub != null) {
                mainAct.configPubNub.subscribeToChannels(subscribeChannelList);
            }
            if (mainAct.configPubNub != null) {
                mainAct.configPubNub.unSubscribeToChannels(unSubscribeChannelList);
            }
        }
        if (currentLoadedDrivers.size() == 0) {
            if (mainAct != null) {
                mainAct.notifyNoCabs();
            }

            if (isCheckAgain == true) {
                checkAvailableCabs();
            }
        } else {

            if (mainAct != null) {
                mainAct.notifyCabsAvailable();
            }
        }
    }

    public Marker drawMarker(LatLng point, String Name, HashMap<String, String> driverData) {

        try {
            finalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        MarkerOptions markerOptions = new MarkerOptions();
        String eIconType = generalFunc.getSelectedCarTypeData(selectedCabTypeId, "VehicleTypes", "eIconType", userProfileJson);

        int iconId = R.drawable.car_driver;
        if (eIconType.equalsIgnoreCase("Bike")) {
            iconId = R.mipmap.car_driver_1;
        } else if (eIconType.equalsIgnoreCase("Cycle")) {
            iconId = R.mipmap.car_driver_2;
        }

        SelectableRoundedImageView providerImgView = null;
        View marker_view = null;

        if (generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("UberX")) {
            marker_view = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.uberx_provider_maker_design, null);
            providerImgView = (SelectableRoundedImageView) marker_view
                    .findViewById(R.id.providerImgView);
//            marker_img_escort

            providerImgView.setImageResource(R.mipmap.ic_no_pic_user);

//                    driverData.get("driver_img");
            markerOptions.position(point).title("DriverId" + driverData.get("driver_id")).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(mContext, marker_view)))
                    .anchor(0.5f, 0.5f).flat(true);

        } else {
            // Setting latitude and longitude for the marker
            if (mainAct != null) {
                if (mainAct.isUfx) {
                    marker_view = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                            .inflate(R.layout.uberx_provider_maker_design, null);
                    providerImgView = (SelectableRoundedImageView) marker_view
                            .findViewById(R.id.providerImgView);
//            marker_img_escort

                    providerImgView.setImageResource(R.mipmap.ic_no_pic_user);

//                    driverData.get("driver_img");
                    markerOptions.position(point).title("DriverId" + driverData.get("driver_id")).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(mContext, marker_view)))
                            .anchor(0.5f, 0.5f).flat(true);
                } else {
                    markerOptions.position(point).title("DriverId" + driverData.get("driver_id")).icon(BitmapDescriptorFactory.fromResource(iconId))
                            .anchor(0.5f, 0.5f).flat(true);
                }
            } else {
                markerOptions.position(point).title("DriverId" + driverData.get("driver_id")).icon(BitmapDescriptorFactory.fromResource(iconId))
                        .anchor(0.5f, 0.5f).flat(true);
            }
        }
        // Adding marker on the Google Map
        final Marker marker = gMapView.addMarker(markerOptions);
        marker.setRotation(0);
        marker.setVisible(true);
//        marker.setTag(driverData.get("driver_id"));
//        driverMarkerList.add(marker);

        if (generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("UberX") &&
                !driverData.get("driver_img").equals("") && !driverData.get("driver_img").equals("NONE") && providerImgView != null && marker_view != null) {

            String image_url = CommonUtilities.SERVER_URL_PHOTOS + "upload/Driver/" + driverData.get("driver_id") + "/"
                    + driverData.get("driver_img");

            final View finalMarker_view = marker_view;
            Picasso.with(mContext)
                    .load(image_url/*"http://www.hellocle.com/wp-content/themes/hello/images/hello-logo-stone.png"*/)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(providerImgView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(mContext, finalMarker_view)));
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }

        if (mainAct != null) {
            if (mainAct.isUfx) {
                if (generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX) &&
                        !driverData.get("driver_img").equals("") && !driverData.get("driver_img").equals("NONE") && providerImgView != null && marker_view != null) {


                    String image_url = CommonUtilities.SERVER_URL_PHOTOS + "upload/Driver/" + driverData.get("driver_id") + "/"
                            + driverData.get("driver_img");

                    final View finalMarker_view = marker_view;
                    Picasso.with(mContext)
                            .load(image_url/*"http://www.hellocle.com/wp-content/themes/hello/images/hello-logo-stone.png"*/)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .into(providerImgView, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(mContext, finalMarker_view)));
                                }

                                @Override
                                public void onError() {

                                }
                            });
                }
            }
        }

        return marker;
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public void onPauseCalled() {

        if (updateDriverListTask != null) {
            updateDriverListTask.stopRepeatingTask();
        }
    }

    public void onResumeCalled() {
        if (updateDriverListTask != null && isTaskKilled == false) {
            updateDriverListTask.startRepeatingTask();
        }
    }

    @Override
    public void onTaskRun() {
        checkAvailableCabs();
    }

    //    ========================================================================================================
//    UBER X DRIVERS MARKERS AND DRIVER DETAILS
    public HashMap<String, String> getMarkerDetails(Marker marker) {
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < listOfDrivers.size(); i++) {
            if (marker.getTitle().replace("DriverId", "").trim().equalsIgnoreCase(listOfDrivers.get(i).get("driver_id"))) {
//                loadDriverDetails(listOfDrivers.get(i));
                map = listOfDrivers.get(i);
            }
        }
        return map;
    }

    public void getMarkerDetails(String driverId) {
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < listOfDrivers.size(); i++) {
            if (driverId.trim().equalsIgnoreCase(listOfDrivers.get(i).get("driver_id"))) {
                loadDriverDetails(listOfDrivers.get(i));
            }
        }
    }

    public void loadDriverDetails(final HashMap<String, String> map) {

        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.uber_x_driver_detail_dialog);
        ((RatingBar) dialog.findViewById(R.id.ratingBar)).setRating(generalFunc.parseFloatValue(0, map.get("average_rating")));
        ((MTextView) dialog.findViewById(R.id.nameTxt)).setText(map.get("Name") + " " + map.get("LastName"));

        if (map.get("fAmount") != null && !map.get("fAmount").trim().equals("")) {
            ((MTextView) dialog.findViewById(R.id.priceTxt)).setText(map.get("fAmount"));
        } else {
            (dialog.findViewById(R.id.priceTxt)).setVisibility(View.GONE);
        }


        ((MTextView) dialog.findViewById(R.id.descTxt)).setText(map.get("tProfileDescription"));
        MButton btn_type2 = ((MaterialRippleLayout) dialog.findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setText(map.get("LBL_SEND_REQUEST"));
        ((MTextView) dialog.findViewById(R.id.driverDTxt)).setText(generalFunc.retrieveLangLBl("Washer Detail", "LBL_DRIVER_DETAIL"));

        String image_url = CommonUtilities.SERVER_URL_PHOTOS + "upload/Driver/" + map.get("driver_id") + "/" + map.get("driver_img");

        Picasso.with(mContext)
                .load(image_url)
                .placeholder(R.mipmap.ic_no_pic_user)
                .error(R.mipmap.ic_no_pic_user)
                .into(((SelectableRoundedImageView) dialog.findViewById(R.id.driverImgView)));

        btn_type2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                closeDialog();
                mainAct.redirectToMapOrList(Utils.Cab_UberX_Type_Map, false);
                mainAct.setSelectedDriverId(map.get("driver_id"));
//                mainAct.selectSourceLocArea.performClick();
                mainAct.continuePickUpProcess();
            }
        });

        (dialog.findViewById(R.id.closeImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                closeDialog();
            }
        });


        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(dialog);
        }
        dialog.show();
    }

    public void closeDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

//                              OVER MARKERS AND DRIVER DETAILS
//    ======================================================================================================

}
