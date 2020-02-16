package com.app85taxi.passenger;

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.utils.Utils;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
              String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Intent intent = new Intent();
        intent.setAction("com.rider.GCM.TokenUpdate");
        sendBroadcast(intent);


    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Utils.printLog(TAG, "sendRegistrationToServer: " + token);
    }


}

