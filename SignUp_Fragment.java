package com.lucky13.wali.qeysari;

/**
 * Created by WALI on 2/20/2018.
 */
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.lucky13.wali.qeysari.R;


import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUp_Fragment extends Fragment implements OnClickListener {
    private static View view;
    private static EditText fullName, emailId, mobileNumber,password, confirmPassword, shopnename;
    private TextView location;
    private static TextView login;
    private static Button signUpButton;
    private static Button shopLocater;
    private static CheckBox terms_conditions;
    private static String defLocationText;
    private static String defLocationText_ku;

    private static String shop_Location="";

    public SignUp_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signup_layout, container, false);
        defLocationText="";
        defLocationText_ku="";
        initViews();
        setListeners();
        return view;
    }

    // Initialize all views
    private void initViews() {
        fullName = (EditText) view.findViewById(R.id.fullName);
        emailId = (EditText) view.findViewById(R.id.userEmailId);
        mobileNumber = (EditText) view.findViewById(R.id.mobileNumber);
        location = (TextView) view.findViewById(R.id.location);
        password = (EditText) view.findViewById(R.id.password);
        confirmPassword = (EditText) view.findViewById(R.id.confirmPassword);
        shopnename = (EditText) view.findViewById(R.id.shopeId);
        signUpButton = (Button) view.findViewById(R.id.signUpBtn);
        shopLocater = (Button) view.findViewById(R.id.setLocationBtn);
        login = (TextView) view.findViewById(R.id.already_user);
        terms_conditions = (CheckBox) view.findViewById(R.id.terms_conditions);


    }

    // Set Listeners
    private void setListeners() {
        signUpButton.setOnClickListener(this);
        login.setOnClickListener(this);
        shopLocater.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setLocationBtn:




                // ActivityCompat.requestPermissions(getActivity().getApplicationContext(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 123);

                GpsTracker gt = new GpsTracker(getActivity().getApplicationContext());
                Location l = gt.getLocation();
                if( l == null){
                    Toast.makeText(getActivity().getApplicationContext(),"Unable to get Position , Check if gps is turned",Toast.LENGTH_SHORT).show();
                }else {
                    double lat = l.getLatitude();
                    double lon = l.getLongitude();
                    Toast.makeText(getActivity().getApplicationContext(),"GPS Lat = "+lat+"\n lon = "+lon,Toast.LENGTH_SHORT).show();
                    shop_Location=lat+"\n"+lon;
                    location.setText("Shop Location has been Set");
                }



                break;

            case R.id.signUpBtn:

                // Call checkValidation method
                checkValidation();
                if(CheckEditText){

                    // If EditText is not empty and CheckEditText = True then this block will execute.

                    StudentRegistration(getFullName, getEmailId, getMobileNumber,shop_Location,getPassword, getShope);

                }
                else {

                    // If EditText is empty then this block will execute .
                    Toast.makeText(getActivity(), "Please fill all form fields.", Toast.LENGTH_LONG).show();

                }


                break;

            case R.id.already_user:

                // Replace login fragment
                new LoginActivity().replaceLoginFragment();
                break;
        }

    }

    // Check Validation Method
    String getFullName, getEmailId, getMobileNumber,getLocation,getPassword,getConfirmPassword, getShope;
    private void checkValidation() {

        // Get all edittext texts
        getFullName = fullName.getText().toString();
        getEmailId = emailId.getText().toString();
        getMobileNumber = mobileNumber.getText().toString();
        getLocation = location.getText().toString();
        getPassword = password.getText().toString();
        getConfirmPassword = confirmPassword.getText().toString();
        getShope=shopnename.getText().toString();
        // Pattern match for email id
        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);

        // Check if all strings are null or not
        if (getFullName.equals("") || getFullName.length() == 0
                || getEmailId.equals("") || getEmailId.length() == 0
                || getMobileNumber.equals("") || getMobileNumber.length() == 0
                || getLocation.equals("Shop Location is not set yet!")
                || getLocation.equals("شوێنی فرۆشگا هێشتا دیاری نەکراوە")
                || getPassword.equals("") || getPassword.length() == 0
                || getShope.equals("") || getShope.length() == 0
                || getConfirmPassword.equals("")
                || getConfirmPassword.length() == 0) {
            CheckEditText = false;
            new CustomToast().Show_Toast(getActivity(), view,
                    "All fields are required.");
        }
        else if(getLocation.equals("Shop Location is not set yet!")
                || getLocation.equals("شوێنی فرۆشگا هێشتا دیاری نەکراوە")){
            new CustomToast().Show_Toast(getActivity(), view,
                    "Please set the Location!");
        }
            // Check if email id valid or not
        else if (!m.find()) {
            CheckEditText = false;
            new CustomToast().Show_Toast(getActivity(), view,
                    "Your Email Id is Invalid.");
        }

            // Check if both password should be equal
        else if (!getConfirmPassword.equals(getPassword))
        {
            CheckEditText = false;
            new CustomToast().Show_Toast(getActivity(), view,
                    "Both password doesn't match.");
        }

            // Make sure user should check Terms and Conditions checkbox
        else if (!terms_conditions.isChecked()) {
            CheckEditText = false;
            new CustomToast().Show_Toast(getActivity(), view,
                    "Please select Terms and Conditions.");
        }

            // Else do signup or do your stuff
        else {
            CheckEditText = true;
            Toast.makeText(getActivity(), "Do SignUp.", Toast.LENGTH_SHORT)
                    .show();
        }

    }
    ProgressDialog progressDialog;
    HashMap<String,String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    String HttpURL = "http://koyapics.info/offlineapp/userRegisteration.php";
    String finalResult ;
    Boolean CheckEditText ;

    public void StudentRegistration(final String Name, final String EmailId, final String MobileNumber, final String Location, final String Password, final String Shope ){

        class StudentRegistrationClass extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(getActivity(),"Loading Data",null,true,true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();

                Toast.makeText(getActivity(),httpResponseMsg.toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("fullname",params[0]);
                hashMap.put("email",params[1]);
                hashMap.put("phonenumber",params[2]);
                hashMap.put("location",params[3]);
                hashMap.put("password",params[4]);
                hashMap.put("shope",params[5]);

                finalResult = httpParse.postRequest(hashMap, HttpURL);

                return finalResult;
            }
        }

        StudentRegistrationClass studentRegistrationClass = new StudentRegistrationClass();

        studentRegistrationClass.execute(Name,EmailId, MobileNumber,Location,Password, Shope);
    }

}