package com.app85taxi.passenger;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.dialogs.GetReceiverLocation;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.SetOnTouchList;
import com.general.files.StartActProcess;
import com.google.android.gms.maps.model.LatLng;
import com.app85taxi.passenger.R;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.SelectableRoundedImageView;
import com.view.editBox.MaterialEditText;

import java.util.HashMap;

public class AddRecipientActivity extends AppCompatActivity {

    MaterialEditText recipientAddrBox, recipientNameBox, recipientEmailbox, recipientCountryBox, recipientPhoneBox;
    String addr, name, email, phone, country;
    MButton btn_type2;
    MTextView plusSignTxt, askRecipientTxt,titleTxt;
    ImageView backImgView;
    String vPhoneCode = "";
    boolean isCountrySelected = false;
    boolean isAddressSelected = false;

    ProgressBar loading;
    String required_str = "";
    String error_email_str = "";

    GeneralFunctions generalFunc;
    private SelectableRoundedImageView orImgView;
    private GetReceiverLocation getReceiverLocation;
    LatLng placeLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipient);

        generalFunc = new GeneralFunctions(getActContext());

        recipientAddrBox = (MaterialEditText) findViewById(R.id.recipientAddrBox);
        recipientEmailbox = (MaterialEditText) findViewById(R.id.recipientEmailbox);
        recipientNameBox = (MaterialEditText) findViewById(R.id.recipientNameBox);
        recipientCountryBox = (MaterialEditText) findViewById(R.id.recipientcountryBox);
        recipientPhoneBox = (MaterialEditText) findViewById(R.id.recipientPhoneBox);
        orImgView = (SelectableRoundedImageView) findViewById(R.id.orImgView);
        askRecipientTxt = (MTextView) findViewById(R.id.askRecipientTxt);

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        loading = (ProgressBar) findViewById(R.id.loading);

        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setId(Utils.generateViewId());
        btn_type2.setOnClickListener(new setOnClickList());
        backImgView.setOnClickListener(new setOnClickList());

        new CreateRoundedView(getResources().getColor(R.color.editBox_primary), Utils.dipToPixels(getActContext(), 30), 0,
                Color.parseColor("#00000000"), orImgView);

        new CreateRoundedView(getResources().getColor(R.color.editBox_primary), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1),
                getResources().getColor(R.color.editBox_primary), askRecipientTxt);


        recipientCountryBox.setShowClearButton(false);
        recipientAddrBox.setShowClearButton(false);
        recipientCountryBox.setVisibility(View.GONE);
        recipientEmailbox.setVisibility(View.GONE);
        recipientPhoneBox.setInputType(InputType.TYPE_CLASS_NUMBER);

        removeInput();
        setLabels();

    }

    public void removeInput() {
        Utils.removeInput(recipientCountryBox);
        Utils.removeInput(recipientAddrBox);

        recipientCountryBox.setOnTouchListener(new SetOnTouchList());
        askRecipientTxt.setOnTouchListener(new SetOnTouchList());
        recipientAddrBox.setOnTouchListener(new SetOnTouchList());

        recipientCountryBox.setOnClickListener(new setOnClickList());
        askRecipientTxt.setOnClickListener(new setOnClickList());
        recipientAddrBox.setOnClickListener(new setOnClickList());
    }

    private void setLabels() {

        titleTxt.setText(generalFunc.retrieveLangLBl("Add Receipient", "LBL_ADD_RECIPIENT_TXT"));
        recipientAddrBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_SELECT_RECIPIENT_ADDRESS"));
        recipientCountryBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_COUNTRY_TXT"));
        recipientPhoneBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_MOBILE_NUMBER_HEADER_TXT"));
        recipientNameBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_RECIPIENT_NAME_HEADER_TXT"));
        recipientEmailbox.setBothText(generalFunc.retrieveLangLBl("", "LBL_RECIPIENT_EMAIL_TXT"));
        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT");
        error_email_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_EMAIL_ERROR_TXT");
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_SUBMIT_TXT"));
        askRecipientTxt.setText(generalFunc.retrieveLangLBl("Ask Recipient", "LBL_ASK_RECIPIENT_TXT"));

    }

    public class setOnClickList implements View.OnClickListener, GetReceiverLocation.OnAddressSelected {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == btn_type2.getId()) {
                checkData();
            }
            else if (i==R.id.recipientAddrBox)
            {
                Bundle bn = new Bundle();
                bn.putString("isRecipientLoc", "true");
                new StartActProcess(getActContext()).startActForResult(SearchPickupLocationActivity.class, bn, Utils.SEARCH_PICKUP_LOC_REQ_CODE);

            }
            else if (i == R.id.recipientcountryBox) {
                new StartActProcess(getActContext()).startActForResult(SelectCountryActivity.class, Utils.SELECT_COUNTRY_REQ_CODE);

            } else if (i == R.id.backImgView) {
                AddRecipientActivity.super.onBackPressed();
            }else if (i == R.id.askRecipientTxt) {
                boolean receiverNameEntered = Utils.checkText(recipientNameBox) ? true : Utils.setErrorFields(recipientNameBox, required_str);
                boolean receiverMobileEntered = Utils.checkText(recipientPhoneBox) ? true : Utils.setErrorFields(recipientPhoneBox, required_str);

                if (receiverMobileEntered == false || receiverNameEntered==false ) {
                    return;
                }
                getReceiverLocation = new GetReceiverLocation(getActContext(), generalFunc, Utils.getText(recipientPhoneBox), Utils.getText(recipientNameBox));
                getReceiverLocation.setAddress(this);
                getReceiverLocation.run();
            }

        }

        @Override
        public void getAddress(int iTempReceiverId, String vLatitude, String vLongitude, String address) {
            getReceiverLocation.dismissDialog();
            placeLocation=new LatLng(generalFunc.parseDoubleValue(0.0,vLatitude),generalFunc.parseDoubleValue(0.0,vLongitude));
            showChangeAdddressConfirmationDialog(address);
        }
    }

    public void showChangeAdddressConfirmationDialog(final String address) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("Are you sure you want to change recipient's pickup address ?", "LBL_CHANGE_RECIPIENT_ADDRESS_TXT"));

        generateAlert.resetBtn();
        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {
                generateAlert.closeAlertBox();

                if (btn_id == 1) {
                    recipientAddrBox.setText(""+address);
                    isAddressSelected = true;
                }


            }
        });
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("Confirm", "LBL_BTN_TRIP_CANCEL_CONFIRM_TXT"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"));
        generateAlert.showAlertBox();

    }

    private void checkData() {
        Utils.hideKeyboard(getActContext());

        boolean fNameEntered = Utils.checkText(recipientNameBox) ? true : Utils.setErrorFields(recipientNameBox, required_str);
        boolean lAddrEntered = Utils.checkText(recipientAddrBox) ? true : Utils.setErrorFields(recipientAddrBox, required_str);
      /*  boolean emailEntered = Utils.checkText(recipientEmailbox) ?
                (generalFunc.isEmailValid(Utils.getText(recipientEmailbox)) ? true : Utils.setErrorFields(recipientEmailbox, error_email_str))
                : Utils.setErrorFields(recipientEmailbox, required_str);*/
        boolean mobileEntered = Utils.checkText(recipientPhoneBox) ? true : Utils.setErrorFields(recipientPhoneBox, required_str);
//        boolean countryEntered = isCountrySelected ? true : Utils.setErrorFields(recipientCountryBox, required_str);

        if (fNameEntered == false || lAddrEntered == false || mobileEntered == false) {
            return;
        }

        addRecipient();

    }

    private void addRecipient() {
//        loading.setVisibility(View.VISIBLE);

        addr = recipientAddrBox.getText().toString();
        country = recipientCountryBox.getText().toString();
        phone = recipientPhoneBox.getText().toString();
        email = recipientEmailbox.getText().toString();
        name = recipientNameBox.getText().toString();
        Utils.printLog("register value", "" + generalFunc.getMemberId() + " " + addr + " " + country + " " + phone + " " + email + " " + name);

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "addrecipient");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("vAddress", Utils.getText(recipientAddrBox));
        parameters.put("vName", Utils.getText(recipientNameBox));
        parameters.put("vEmail", Utils.getText(recipientEmailbox));
        parameters.put("vPhone", Utils.getText(recipientPhoneBox));
        parameters.put("PhoneCode", vPhoneCode);
        parameters.put("vLatitude", ""+placeLocation.latitude);
        parameters.put("vLongitude", ""+placeLocation.longitude);

        Utils.printLog("addrecipient_url", "" + CommonUtilities.SERVER_URL_WEBSERVICE + " " + parameters.toString());
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(),parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
//        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken");
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {
                    Utils.printLog("Recipient", responseString);

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    if (isDataAvail == true) {

                        (new StartActProcess(getActContext())).setOkResult();
                        backImgView.performClick();

                    } else {
                        generalFunc.showGeneralMessage("",
                                generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                    }

//                    loading.setVisibility(View.GONE);
//                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

//                    JSONArray data = generalFunc.getJsonArray("recipientsData", responseString);
//                    if (!data.equals("")) {
//
//
//                    } else {
//                        generalFunc.showGeneralMessage("",
//                                generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
//                    }
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

        if (requestCode == Utils.SELECT_COUNTRY_REQ_CODE && resultCode == RESULT_OK && data != null) {
            vPhoneCode = data.getStringExtra("vPhoneCode");
            isCountrySelected = true;
            recipientCountryBox.setText("+" + vPhoneCode);

        }
        else if (requestCode == Utils.SEARCH_PICKUP_LOC_REQ_CODE && resultCode == RESULT_OK && data != null)
        {
            recipientAddrBox.setText(data.getStringExtra("Address"));
            isAddressSelected = true;
            placeLocation = new LatLng(generalFunc.parseDoubleValue(0.0,data.getStringExtra("Latitude")), generalFunc.parseDoubleValue(0.0,data.getStringExtra("Longitude")));

        }
    }

    public Context getActContext() {
        return AddRecipientActivity.this;
    }
}
