package com.example.google.playservices.placepicker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.android.common.logger.Log;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

import java.net.MalformedURLException;
//import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
//
//import com.microsoft.windowsazure.mobileservices.MobileServiceUser;
//import com.microsoft.windowsazure.mobileservices.UserAuthenticationCallback;
//import com.microsoft.windowsazure.mobileservices.MobileServiceAuthenticationProvider;

public class AuthenticationActivity extends Activity
{
    private static final String TAG = "PlacePickerSample";
    private String password,password1,password2,username,email;
    private MobileServiceClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        try {
            mClient = new MobileServiceClient(
                    "https://codefundoapp.azure-mobile.net/",
                    "YGCJknIwaJFEPuUmOLzfQWAkeLGgzL32",
                    this
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public void login(View view) throws ClassCastException
    {
        EditText editText = (EditText) findViewById(R.id.emailinput);
        email = editText.getText().toString();
        EditText editText2 = (EditText) findViewById(R.id.passwordinput);
        password = editText.getText().toString();

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key),0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(email, 1);
        editor.commit();

        final MobileServiceTable<SignUpDetails> mToDoTable = mClient.getTable(SignUpDetails.class);

        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                try
                {
                    final MobileServiceList<SignUpDetails> result =
                            mToDoTable.where().field("email").eq(email).execute().get();
                    for (SignUpDetails item : result) {
                        Log.i(TAG, "Read object with ID " + item.Id);
                        if(password.equals(item.password))
                        {

                        }
                    }
                }
                catch (Exception exception)
                {
                    //createAndShowDialog(exception, "Error");
                    exception.printStackTrace();
                }
                return null;
            }
        }.execute();


    }
    public void signup(View view) throws ClassCastException
    {
        EditText nametext = (EditText) findViewById(R.id.name);
        username = nametext.getText().toString();
        EditText emailtext = (EditText) findViewById(R.id.email2input);
        email = emailtext.getText().toString();
        EditText passwordtext1 = (EditText) findViewById(R.id.password1input);
        password1 = passwordtext1.getText().toString();
        EditText passwordtext2 = (EditText) findViewById(R.id.password2input);
        password2 = passwordtext2.getText().toString();
        if(password2.equals(password1))
        {
            SignUpDetails sd=new SignUpDetails();
            sd.username=username;
            sd.email=email;
            sd.password=password1;


            mClient.getTable(SignUpDetails.class).insert(sd, new TableOperationCallback<SignUpDetails>() {
                public void onCompleted(SignUpDetails entity, Exception exception, ServiceFilterResponse response) {
                    if (exception == null) {
                        // Insert succeeded
                    } else {
                        // Insert failed
                        exception.printStackTrace();
                    }
                }
            });
        }
        else
        {

        }
      }
}