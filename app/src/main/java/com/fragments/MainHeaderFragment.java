package com.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app85taxi.passenger.MainActivity;
import com.app85taxi.passenger.R;
import com.general.files.GeneralFunctions;
import com.general.files.GetAddressFromLocation;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.MTextView;

public class MainHeaderFragment extends Fragment implements GetAddressFromLocation.AddressFound {

    MainActivity mainAct;
    GeneralFunctions generalFunc;
    GoogleMap gMap;

    public ImageView menuImgView;
    public ImageView backImgView;

    ImageView headerLogo;
    View view;

    GetAddressFromLocation getAddressFromLocation;
    public MTextView pickUpLocTxt;
    LinearLayout pickupLocArea1;
    //LinearLayout searchPickUpLocArea;
    public MTextView sourceLocSelectTxt;
    public MTextView destLocSelectTxt;
    MTextView destLocTxt;

    String pickUpAddress = "";
    String destAddress = "";

    MainHeaderFragment mainHeaderFrag;
    String userProfileJson = "";
    String uberXSelectedServiceData = "";

    LinearLayout destarea;

    //ImageView rmDestLocImgView;
    // boolean isPickUpDrag = false;

    boolean isDestinationMode = false;
    public boolean uberXHeaderHide = false;

    public View area_source;
    public View area2;

    public MTextView mapTxt;
    public MTextView listTxt;
    public MTextView uberXTitleTxtView;
    public ImageView uberXbackImgView;
    LinearLayout switchArea;
    public LinearLayout uberXMainHeaderLayout, MainHeaderLayout;
    android.support.v7.widget.Toolbar toolbar;

    boolean isfirst = false;

    MTextView pickUpLocHTxt, destLocHTxt;
    String app_type = "";
    boolean isUfx = false;
    boolean isclickabledest = false;
    boolean isclickablesource = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.fragment_main_header, container, false);
        menuImgView = (ImageView) view.findViewById(R.id.menuImgView);
        backImgView = (ImageView) view.findViewById(R.id.backImgView);
        pickUpLocTxt = (MTextView) view.findViewById(R.id.pickUpLocTxt);
        sourceLocSelectTxt = (MTextView) view.findViewById(R.id.sourceLocSelectTxt);
        destLocSelectTxt = (MTextView) view.findViewById(R.id.destLocSelectTxt);
        destLocTxt = (MTextView) view.findViewById(R.id.destLocTxt);
        pickUpLocHTxt = (MTextView) view.findViewById(R.id.pickUpLocHTxt);
        destLocHTxt = (MTextView) view.findViewById(R.id.destLocHTxt);
        pickupLocArea1 = (LinearLayout) view.findViewById(R.id.pickupLocArea1);
        pickupLocArea1.setOnClickListener(new setOnClickList());
        destarea = (LinearLayout) view.findViewById(R.id.destarea);
        destarea.setOnClickListener(new setOnClickList());

        // searchPickUpLocArea = (LinearLayout) view.findViewById(R.id.searchPickUpLocArea);

        uberXMainHeaderLayout = (LinearLayout) view.findViewById(R.id.uberXMainHeaderLayout);
        MainHeaderLayout = (LinearLayout) view.findViewById(R.id.MainHeaderLayout);

        switchArea = (LinearLayout) view.findViewById(R.id.switchArea);
        headerLogo = (ImageView) view.findViewById(R.id.headerLogo);
        mapTxt = (MTextView) view.findViewById(R.id.mapTxt);
        listTxt = (MTextView) view.findViewById(R.id.listTxt);
        uberXTitleTxtView = (MTextView) view.findViewById(R.id.titleTxt);
        uberXbackImgView = (ImageView) view.findViewById(R.id.backImgViewuberx);
//        rmDestLocImgView = (ImageView) view.findViewById(R.id.rmDestLocImgView);
        area_source = view.findViewById(R.id.area_source);
        area2 = view.findViewById(R.id.area2);
        //  toolbar=(android.support.v7.widget.Toolbar)view.findViewById(R.id.toolbar);
        //  toolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mainAct = (MainActivity) getActivity();
        generalFunc = mainAct.generalFunc;

        if (!mainAct.isFrompickupaddress) {
            area_source.setVisibility(View.GONE);

        } else {
            Utils.printLog("mainheader", "visible");
            area_source.setVisibility(View.VISIBLE);
        }

        isUfx = getArguments().getBoolean("isUfx", false);

        pickUpLocHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PICK_UP_FROM"));
        destLocHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DROP_AT"));

        uberXTitleTxtView.setText("Menu");
        mainHeaderFrag = mainAct.getMainHeaderFrag();
        userProfileJson = mainAct.userProfileJson;

        getAddressFromLocation = new GetAddressFromLocation(mainAct.getActContext(), generalFunc);
        getAddressFromLocation.setAddressList(this);

        ((MTextView) view.findViewById(R.id.pickUpLocTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_PICKUP_LOCATION_HEADER_TXT"));

        Utils.printLog("Api", "menuImgView Click set");
        app_type = generalFunc.getJsonValue("APP_TYPE", userProfileJson);

        if (app_type.equals("UberX")) {
            area_source.setVisibility(View.GONE);
            area2.setVisibility(View.GONE);
        }

        if (isUfx) {
            if (app_type.equals(Utils.CabGeneralTypeRide_Delivery_UberX)) {
                area_source.setVisibility(View.GONE);
                area2.setVisibility(View.GONE);

            }
        }
        //searchPickUpLocArea.setOnClickListener(new setOnClickList());
        menuImgView.setOnClickListener(new setOnClickList());
        backImgView.setOnClickListener(new setOnClickList());

        /*menuImgView.setId(Utils.generateViewId());
        menuImgView.setOnClickListener(new setOnClickList());*/

        listTxt.setOnClickListener(new setOnClickList());
        mapTxt.setOnClickListener(new setOnClickList());
        uberXbackImgView.setOnClickListener(new setOnClickList());

        // pickUpLocTxt.setOnClickListener(new setOnClickList());
        //destLocTxt.setOnClickListener(new setOnClickList());
        sourceLocSelectTxt.setOnClickListener(new setOnClickList());
        destLocSelectTxt.setOnClickListener(new setOnClickList());

        destLocTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_DESTINATION_BTN_TXT"));
        destLocSelectTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_DESTINATION_BTN_TXT"));

        if (isUfx) {
            if (generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX)) {
                uberXMainHeaderLayout.setVisibility(View.VISIBLE);
                MainHeaderLayout.setVisibility(View.GONE);
                switchArea.setVisibility(View.VISIBLE);
                mainAct.redirectToMapOrList(Utils.Cab_UberX_Type_List, false);
            } else if (generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("UberX")) {
                uberXMainHeaderLayout.setVisibility(View.VISIBLE);
                MainHeaderLayout.setVisibility(View.GONE);
                switchArea.setVisibility(View.VISIBLE);
                mainAct.redirectToMapOrList(Utils.Cab_UberX_Type_List, false);
            } else {
                MainHeaderLayout.setVisibility(View.VISIBLE);
                uberXMainHeaderLayout.setVisibility(View.GONE);
                switchArea.setVisibility(View.GONE);
            }
        } else {

            if (generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("UberX")) {
                uberXMainHeaderLayout.setVisibility(View.VISIBLE);
                MainHeaderLayout.setVisibility(View.GONE);
                switchArea.setVisibility(View.VISIBLE);
                mainAct.redirectToMapOrList(Utils.Cab_UberX_Type_List, false);
            } else {
                MainHeaderLayout.setVisibility(View.VISIBLE);
                uberXMainHeaderLayout.setVisibility(View.GONE);
                switchArea.setVisibility(View.GONE);
            }
        }
        if (mainAct != null) {
            mainAct.addDrawer.setMenuImgClick(view, false);
        }

        new CreateRoundedView(getResources().getColor(R.color.pickup_req_now_btn), Utils.dipToPixels(mainAct, 25), 0,
                Color.parseColor("#00000000"), view.findViewById(R.id.imgsourcearea1));
        new CreateRoundedView(getResources().getColor(R.color.pickup_req_later_btn), Utils.dipToPixels(mainAct, 25), 0,
                Color.parseColor("#00000000"), view.findViewById(R.id.imagemarkerdest1));
        new CreateRoundedView(getResources().getColor(R.color.pickup_req_now_btn), Utils.dipToPixels(mainAct, 25), 0,
                Color.parseColor("#00000000"), view.findViewById(R.id.imgsourcearea2));
        new CreateRoundedView(getResources().getColor(R.color.pickup_req_later_btn), Utils.dipToPixels(mainAct, 25), 0,
                Color.parseColor("#00000000"), view.findViewById(R.id.imagemarkerdest2));

        CameraPosition cameraPosition = mainAct.cameraForUserPosition();

        if (mainAct.getMap() != null && mainAct.getIntent().getStringExtra("latitude") != null && mainAct.getIntent().getStringExtra("longitude") != null
                && !mainAct.getIntent().getStringExtra("latitude").equals("0.0") && !mainAct.getIntent().getStringExtra("longitude").equals("0.0")) {
            mainAct.animateToLocation(generalFunc.parseDoubleValue(0.0, mainAct.getIntent().getStringExtra("latitude")),
                    generalFunc.parseDoubleValue(0.0, mainAct.getIntent().getStringExtra("longitude")), Utils.defaultZomLevel);
        } else if (mainAct.getMap() != null && mainAct.userLocation != null) {
            if (mainAct.userLocation.getLatitude() != 0.0 && mainAct.userLocation.getLongitude() != 0.0) {
                mainAct.animateToLocation(mainAct.userLocation.getLatitude(),
                        mainAct.userLocation.getLongitude(), Utils.defaultZomLevel);
            } else {
                mainAct.getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        } else if (cameraPosition != null) {
            mainAct.getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        return view;
    }

    public void refreshFragment() {
        view = null;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    public void setGoogleMapInstance(GoogleMap gMap) {
//        gMap = mainAct.getMap();
        this.gMap = gMap;
        this.gMap.setOnCameraChangeListener(new onGoogleMapCameraChangeList());
    }

    public void setDefaultView() {

        if (!app_type.equals("UberX")) {
            area_source.setVisibility(View.VISIBLE);
            area2.setVisibility(View.GONE);
        }

        destLocTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_DESTINATION_BTN_TXT"));
        destLocSelectTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_DESTINATION_BTN_TXT"));
        mainAct.setDestinationPoint("", "", "", false);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(mainAct.pickUpLocation.getLatitude(), mainAct.pickUpLocation.getLongitude()))
                .zoom(gMap.getCameraPosition().zoom).build();
        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void setPickUpAddress(String pickUpAddress) {
        if (sourceLocSelectTxt != null) {
            sourceLocSelectTxt.setText(pickUpAddress);
        }
        this.pickUpAddress = pickUpAddress;
        if (pickUpLocTxt != null) {
            Utils.printLog("pickUpLocTxt", "" + pickUpLocTxt);
            pickUpLocTxt.setText(pickUpAddress);
        } else {
            Utils.printLog("pickUpLocTxt", "0 " + pickUpLocTxt);
            this.pickUpAddress = pickUpAddress;
        }

        LatLng center = gMap.getCameraPosition().target;
        mainAct.pickUpLocationAddress = this.pickUpAddress;
        Location pickupLocation = new Location("");
        pickupLocation.setLongitude(center.longitude);
        pickupLocation.setLatitude(center.latitude);
        mainAct.pickUpLocation = pickupLocation;

    }

    public void setDestinationAddress(String destAddress) {
        if (destLocTxt != null) {
            destLocTxt.setText(destAddress);
        } else {
            this.destAddress = destAddress;
        }

        LatLng center = gMap.getCameraPosition().target;
        mainAct.setDestinationPoint("" + center.latitude, "" + center.longitude, destAddress, true);
//        rmDestLocImgView.setVisibility(View.GONE);
    }

    public String getPickUpAddress() {
        return pickUpLocTxt.getText().toString();
    }

    public void configDestinationMode(boolean isDestinationMode) {
        this.isDestinationMode = isDestinationMode;
    }

    @Override
    public void onAddressFound(String address, double latitude, double longitude, String geocodeobject) {

        Utils.printLog("address", ":" + address);

        //  if (mainAct.cabSelectionFrag != null) {

        geocodeobject = Utils.removeWithSpace(geocodeobject);

        if (isDestinationMode == false) {
            mainAct.tempDestGeoCode = geocodeobject;
            pickUpLocTxt.setText(address);
            sourceLocSelectTxt.setText(address);
        } else {
            mainAct.tempPickupGeoCode = geocodeobject;
        }
        mainAct.onAddressFound(address);

        //  }

        if (isDestinationMode == false) {
            mainAct.pickUpLocationAddress = address;
            mainAct.currentGeoCodeObject = geocodeobject;
            if (mainAct.loadAvailCabs != null) {
                mainAct.loadAvailCabs.pickUpAddress = mainAct.pickUpLocationAddress;
            }
        }

        if (mainAct.loadAvailCabs == null) {
            mainAct.initializeLoadCab();
        }
        if (mainAct.cabSelectionFrag != null) {
            //else

            if (isDestinationMode == false) {
                if (isPickUpAddressStateChanged(address) == true) {
                    mainAct.loadAvailCabs.changeCabs();
                } else {
                    mainAct.loadAvailCabs.changeCabs();
                }
            }
        }

        if (!isfirst) {
            isfirst = true;

            mainAct.uberXAddress = address;
            mainAct.uberXlat = latitude;
            mainAct.uberXlong = longitude;

            if (isDestinationMode == false) {
                pickUpLocTxt.setText(address);
                sourceLocSelectTxt.setText(address);
                Location pickUpLoc = new Location("");
                pickUpLoc.setLatitude(latitude);
                pickUpLoc.setLongitude(longitude);
                mainAct.pickUpLocation = pickUpLoc;

                if (!app_type.equals("UberX")) {
                    area2.setVisibility(View.VISIBLE);
                    area_source.setVisibility(View.GONE);
                }
                if (isUfx) {
                    if (app_type.equals(Utils.CabGeneralTypeRide_Delivery_UberX)) {
                        area_source.setVisibility(View.GONE);
                        area2.setVisibility(View.GONE);
                    }
                }
            }
            if (!app_type.equals("UberX")) {
                area2.setVisibility(View.VISIBLE);
                area_source.setVisibility(View.GONE);
            }
            if (isUfx) {
                if (app_type.equals(Utils.CabGeneralTypeRide_Delivery_UberX)) {
                    area_source.setVisibility(View.GONE);
                    area2.setVisibility(View.GONE);
                }
            }

            isDestinationMode = true;
            mainAct.configDestinationMode(isDestinationMode);
            mainAct.onAddressFound(address);
        }
        mainAct.currentGeoCodeObject = geocodeobject;

        if (mainAct.noloactionview.getVisibility() == View.VISIBLE) {
            area2.setVisibility(View.GONE);
        }
    }

    public boolean isPickUpAddressStateChanged(String address) {

        String[] addressArr = address.split(",");

        boolean isCountryMatched = false;
        boolean isCityMatched = false;

        for (int i = 0; i < addressArr.length; i++) {
            String addComponent = addressArr[i].trim();
            addComponent = addComponent.trim();
            if (addComponent.equals(mainAct.loadAvailCabs.currentPickUpCountry)) {
                isCountryMatched = true;
            }
            if (addComponent.equals(mainAct.loadAvailCabs.currentPickUpCity)) {
                isCityMatched = true;
            }
        }

        if (isCityMatched == false || isCountryMatched == false) {
            return true;
        }
        return false;
    }

    /*public void configPickUpDrag(boolean isPickUpDrag, boolean isChangeToUserLoc) {
        this.isPickUpDrag = isPickUpDrag;

        if (this.isPickUpDrag == false) {
            mainAct.locationMarker.setVisibility(View.INVISIBLE);
        } else {
            mainAct.locationMarker.setVisibility(View.VISIBLE);

            if (isChangeToUserLoc == true) {
                mainAct.userLocBtnImgView.performClick();
            }

        }
    }*/

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == destarea.getId()) {


//                Bundle bn = new Bundle();
//                bn.putString("isPickUpLoc", "false");
//                if (mainAct.getPickUpLocation() != null) {
//                    bn.putString("PickUpLatitude", "" + mainAct.getPickUpLocation().getLatitude());
//                    bn.putString("PickUpLongitude", "" + mainAct.getPickUpLocation().getLongitude());
//
//                }
//                new StartActProcess(mainAct.getActContext()).startActForResult(mainHeaderFrag, SearchPickupLocationActivity.class,
//                        Utils.SEARCH_DEST_LOC_REQ_CODE, bn);

                try {
                    Utils.printLog("pickUpLocationAddress", mainAct.pickUpLocationAddress);
                    if (mainAct.pickUpLocationAddress != null && !mainAct.pickUpLocationAddress.equals("")) {
                        if (!isclickabledest) {
                            isclickabledest = true;
                            LatLngBounds bounds = null;
                            if (mainAct.pickUpLocation != null) {

                                LatLng pickupPlaceLocation = new LatLng(mainAct.pickUpLocation.getLatitude(),
                                        mainAct.pickUpLocation.getLongitude());
                                bounds = new LatLngBounds(pickupPlaceLocation, pickupPlaceLocation);
                            }

                            if (!mainAct.destLocLatitude.equals("")) {
                                LatLng pickupPlaceLocation = new LatLng(generalFunc.parseDoubleValue(0.0, mainAct.destLocLatitude),
                                        generalFunc.parseDoubleValue(0.0, mainAct.destLocLongitude));
                                bounds = new LatLngBounds(pickupPlaceLocation, pickupPlaceLocation);

                            }

                            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .setBoundsBias(bounds)
                                    .build(mainAct);
                            startActivityForResult(intent, Utils.PLACE_AUTOCOMPLETE_REQUEST_CODE);
                        }
                    }

                } catch (Exception e) {

                }


            }
//            else if (id == rmDestLocImgView.getId()) {
//                mainAct.setDestinationPoint("", "", "", false);
////                destLocArea.setVisibility(View.GONE);
////                addDestLocImgView.setVisibility(View.VISIBLE);
////                mainAct.configPickUpDrag(true, true, true);
////                gMap.setPadding(0, Utils.dipToPixels(getActContext(), 60), 0, 0);
//                //destAddress = "";
//                destLocTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_DESTINATION_BTN_TXT"));
//                sourceLocSelectTxt.performClick();
////                rmDestLocImgView.setVisibility(View.GONE);
//            }
            else if (view.getId() == pickupLocArea1.getId()) {

//                Bundle bn = new Bundle();
//                bn.putString("isPickUpLoc", "true");
//                if (mainAct.getPickUpLocation() != null) {
//                    bn.putString("PickUpLatitude", "" + mainAct.getPickUpLocation().getLatitude());
//                    bn.putString("PickUpLongitude", "" + mainAct.getPickUpLocation().getLongitude());
//                    bn.putString("pickUpAddress", mainAct.getPickUpLocationAddress());
//                }
//                new StartActProcess(mainAct.getActContext()).startActForResult(mainHeaderFrag, SearchPickupLocationActivity.class,
//                        Utils.SEARCH_PICKUP_LOC_REQ_CODE, bn);
                try {
                    if (!isclickablesource) {
                        isclickablesource = true;
                        LatLngBounds bounds = null;
                        disableDestMode();

                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                .setBoundsBias(bounds)
                                .build(mainAct);
                        startActivityForResult(intent, Utils.SEARCH_PICKUP_LOC_REQ_CODE);
                    }
                } catch (Exception e) {

                }
            } else if (view.getId() == R.id.sourceLocSelectTxt) {

                area_source.setVisibility(View.VISIBLE);
                area2.setVisibility(View.GONE);
                disableDestMode();

                if (mainAct.getDestinationStatus() == true) {
                    destLocSelectTxt.setText(mainAct.getDestAddress());
                } else {
                    destLocSelectTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_DESTINATION_BTN_TXT"));
                }
            } else if (view.getId() == R.id.destLocSelectTxt) {

                if (mainAct.pickUpLocation != null) {
                    area2.setVisibility(View.VISIBLE);
                    area_source.setVisibility(View.GONE);

                    isDestinationMode = true;
                    mainAct.configDestinationMode(isDestinationMode);

                    if (mainAct.getDestinationStatus() == false) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                destarea.performClick();
                            }
                        }, 250);
                    }
                }

            } else if (view.getId() == backImgView.getId()) {
                menuImgView.setVisibility(View.VISIBLE);
                backImgView.setVisibility(View.GONE);

                mainAct.onBackPressed();

            } else if (view.getId() == menuImgView.getId()) {
                mainAct.addDrawer.checkDrawerState(true);
            }
//            else if (view.getId() == searchPickUpLocArea.getId()) {
//
//                Bundle bn = new Bundle();
//                bn.putString("isPickUpLoc", "true");
//                if (mainAct.getPickUpLocation() != null) {
//                    bn.putString("PickUpLatitude", "" + mainAct.getPickUpLocation().getLatitude());
//                    bn.putString("PickUpLongitude", "" + mainAct.getPickUpLocation().getLongitude());
//                }
//                new StartActProcess(mainAct.getActContext()).startActForResult(mainHeaderFrag, SearchPickupLocationActivity.class,
//                        Utils.SEARCH_PICKUP_LOC_REQ_CODE, bn);
//            }
            else if (view.getId() == listTxt.getId()) {
                Utils.printLog("list", "list");
                mainAct.redirectToMapOrList(Utils.Cab_UberX_Type_List, false);
            } else if (view.getId() == mapTxt.getId()) {
                Utils.printLog("list", "Map");

                mainAct.redirectToMapOrList(Utils.Cab_UberX_Type_Map, false);
            } else if (view.getId() == uberXbackImgView.getId()) {

                mainAct.onBackPressed();
            }
        }
    }

    public void disableDestMode() {
        isDestinationMode = false;
        mainAct.configDestinationMode(isDestinationMode);
    }

    public class onGoogleMapCameraChangeList implements GoogleMap.OnCameraChangeListener {

        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
//            if (isPickUpDrag == false) {
//                return;
//            }
            Utils.printLog("onCamerachnagecalled", "maiheaderfragment");

            if (getAddressFromLocation == null) {
                return;
            }
            // if(getAddressFromLocation!=null) {

            Utils.printLog("Camera", "changed");
            LatLng center = gMap.getCameraPosition().target;
            getAddressFromLocation.setLocation(center.latitude, center.longitude);
            getAddressFromLocation.execute();

//            if (isDestinationMode == false) {
//                if (!isfirst) {
//                    pickUpLocTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SELECTING_LOCATION_TXT"));
//
//                    Location pickUpLoc = new Location("CameraChange");
//                    pickUpLoc.setLatitude(center.latitude);
//                    pickUpLoc.setLongitude(center.longitude);
//
//                    mainAct.onPickUpLocChanged(pickUpLoc);
//                }
//
//            }
            mainAct.onMapCameraChanged();
            // }
//           else {
//                LatLng center = gMap.getCameraPosition().target;
//                Location pickUpLoc = new Location("CameraChange");
//                pickUpLoc.setLatitude(center.latitude);
//                pickUpLoc.setLongitude(center.longitude);
//
//                mainAct.pickUpLocation=pickUpLoc;
//                mainAct.onMapCameraChanged();
//
//            }
        }
    }

    public void releaseResources() {
        this.gMap.setOnCameraChangeListener(null);
        this.gMap = null;
        getAddressFromLocation.setAddressList(null);
        getAddressFromLocation = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.SEARCH_PICKUP_LOC_REQ_CODE) {
            isclickablesource = false;
        }

        if (requestCode == Utils.SEARCH_PICKUP_LOC_REQ_CODE && resultCode == mainAct.RESULT_OK && data != null && gMap != null) {

//            mainAct.configPickUpDrag(true, false, false);

            if (resultCode == mainAct.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);

                LatLng placeLocation = place.getLatLng();

                pickUpLocTxt.setText(place.getAddress().toString());
                sourceLocSelectTxt.setText(place.getAddress().toString());

                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(placeLocation.latitude, placeLocation.longitude))
                        .zoom(gMap.getCameraPosition().zoom).build();
                gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                mainAct.pickUpLocationAddress = place.getAddress().toString();
                if (mainAct.loadAvailCabs != null) {
                    mainAct.loadAvailCabs.pickUpAddress = mainAct.pickUpLocationAddress;
                }
                mainAct.pickUpLocation.setLatitude(placeLocation.latitude);
                mainAct.pickUpLocation.setLongitude(placeLocation.longitude);

                if (mainAct.cabSelectionFrag != null) {
                    mainAct.cabSelectionFrag.findRoute();
                }

                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(placeLocation, 14.0f);

                if (gMap != null) {
                    gMap.clear();
                    gMap.moveCamera(cu);
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);

                generalFunc.showMessage(generalFunc.getCurrentView(getActivity()),
                        status.getStatusMessage());
            } else if (requestCode == mainAct.RESULT_CANCELED) {

            }

        } else if (requestCode == Utils.SEARCH_DEST_LOC_REQ_CODE && resultCode == mainAct.RESULT_OK && data != null && gMap != null) {


//            destLocArea.setVisibility(View.VISIBLE);
            destLocTxt.setText(data.getStringExtra("Address"));
            //   destLocSelectTxt.setText(data.getStringExtra("Address"));


            mainAct.setDestinationPoint(data.getStringExtra("Latitude"), data.getStringExtra("Longitude"), data.getStringExtra("Address"), true);

//            mainAct.configPickUpDrag(false, false, false);
//            addDestLocImgView.setVisibility(View.GONE);

//            gMap.setPadding(0, Utils.dipToPixels(getActContext(), 100), 0, 0);
//            destinationPointMarker_temp = gMap.addMarker(
//                    new MarkerOptions().position(new LatLng(generalFunc.parseDoubleValue(0.0, data.getStringExtra("Latitude")),
//                            generalFunc.parseDoubleValue(0.0, data.getStringExtra("Longitude"))))
//                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_dest_marker)));
            if (mainAct.cabSelectionFrag != null) {
                mainAct.cabSelectionFrag.findRoute();
            }

            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(generalFunc.parseDoubleValue(0.0, data.getStringExtra("Latitude")),
                            generalFunc.parseDoubleValue(0.0, data.getStringExtra("Longitude"))))
                    .zoom(gMap.getCameraPosition().zoom).build();
            gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            mainAct.addcabselectionfragment();

            menuImgView.setVisibility(View.GONE);
            backImgView.setVisibility(View.VISIBLE);


        } else if (requestCode == Utils.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            isclickabledest = false;
            if (resultCode == mainAct.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);

                LatLng placeLocation = place.getLatLng();

                mainAct.setDestinationPoint(placeLocation.latitude + "", placeLocation.longitude + "", place.getAddress().toString(), true);

                //mainHeaderFrag.setDestinationAddress(place.getAddress().toString());

                destLocTxt.setText(place.getAddress().toString());
                destLocSelectTxt.setText(place.getAddress().toString());


                if (mainAct.cabSelectionFrag != null) {
                    mainAct.cabSelectionFrag.findRoute();
                }

                mainAct.addcabselectionfragment();

                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(placeLocation, 14.0f);


                if (gMap != null) {
                    gMap.clear();
                    gMap.moveCamera(cu);
                }
                destLocTxt.setText(place.getAddress().toString());
                destLocSelectTxt.setText(place.getAddress().toString());


                menuImgView.setVisibility(View.GONE);
                backImgView.setVisibility(View.VISIBLE);


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);


                generalFunc.showMessage(generalFunc.getCurrentView(getActivity()),
                        status.getStatusMessage());
            } else if (requestCode == mainAct.RESULT_CANCELED) {

            }
        }
    }


}
