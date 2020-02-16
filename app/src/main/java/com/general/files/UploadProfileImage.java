package com.general.files;

import android.os.AsyncTask;

import com.app85taxi.passenger.MyProfileActivity;
import com.utils.Utils;
import com.view.MyProgressDialog;

import java.util.ArrayList;

/**
 * Created by Admin on 08-07-2016.
 */
public class UploadProfileImage extends AsyncTask<String, String, String> {

    String selectedImagePath;
    String responseString = "";

    String temp_File_Name = "";
    ArrayList<String[]> paramsList;

    MyProfileActivity myProfileAct;
    MyProgressDialog myPDialog;

    public UploadProfileImage(MyProfileActivity myProfileAct, String selectedImagePath, String temp_File_Name, ArrayList<String[]> paramsList) {
        this.selectedImagePath = selectedImagePath;
        this.temp_File_Name = temp_File_Name;
        this.paramsList = paramsList;
        this.myProfileAct=myProfileAct;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        myPDialog = new MyProgressDialog(myProfileAct.getActContext(), false, myProfileAct.generalFunc.retrieveLangLBl("Loading", "LBL_LOADING_TXT"));
        myPDialog.show();
    }


    @Override
    protected String doInBackground(String... strings) {

        String filePath = myProfileAct.generalFunc.decodeFile(selectedImagePath, Utils.ImageUpload_DESIREDWIDTH,
                Utils.ImageUpload_DESIREDHEIGHT, temp_File_Name);
        responseString = new ExecuteResponse().uploadImageAsFile(filePath, temp_File_Name, "vImage", paramsList);

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        myPDialog.close();
        myProfileAct.handleImgUploadResponse(responseString);
    }
}
