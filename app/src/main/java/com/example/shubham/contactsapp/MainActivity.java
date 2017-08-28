package com.example.shubham.contactsapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private Activity context;
    private ProgressDialog pd;
    private static final String TAG = "MainActivity";

    String text = "";
    String num = "";
    String num1 = "", data1 = "";
    String num2="",num3="";

    String[] contactNames = new String[5];
    String[] latitudes    = new String[5];
    String[] longitudes   = new String[5];



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;




        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BackTask bt = new BackTask();
                EditText text = (EditText) findViewById(R.id.editext);
                bt.execute(text.getText().toString());
            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }


    //background process to download the file from internet
    private class BackTask extends AsyncTask<String, Integer, Void> {

        protected void onPreExecute() {
            super.onPreExecute();
            //display progress dialog
            pd = new ProgressDialog(context);
            pd.setTitle("Reading contacts");
            pd.setMessage("Please wait...");
            pd.setCancelable(true);
            pd.setIndeterminate(false);
            pd.show();
        }

        protected Void doInBackground(String... params) {
            URL url;
            int lines, j, k = 0;
            String name = "";


            try {
                //Log.d(TAG,"123*********12212****");
                //create url object to point to the file location on internet
                url = new URL(params[0]);
                //make a request to server
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                //get InputStream instance
                InputStream is = con.getInputStream();
                //create BufferedReader object
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                int z = 0,z1=0;
                int a1 = 0,a2= 0,a3= 0 ,a4= 0,a5= 0,a6= 0,a7= 0,a8= 0;
                //read content of the file line by line
                while ((line = br.readLine()) != null) {
                    //Log.d(TAG,line);
                    text += line + "\n";
                    name = line + "  \n";

                    j = 0;

                    for (int i = 0; i < 4; i++) {
                        k = name.indexOf(" ", j);

                        if(i==0){
                            a1=j;
                            a2=k;
                        }
                        else if(i==1){
                            a3=j;
                            a4=k;
                        }

                        else if(i==2){
                            a5=j;
                            a6=k;
                        }

                        else if(i==3){
                            a7=j;
                            a8=k;
                        }

                        //tempdata[i] = name.substring(j,k);
                        //Log.d(TAG,name.substring(j,k));
                        //Log.d(TAG,String.valueOf(k));
                        num += j;
                        num += k;
                        j = k + 1;

                    }


                    //savecode(line.substring(a1,a2),line.substring(a3,a4),line.substring(a5,a6),line.substring(a7,a8));


                    contactNames[z1] = line.substring(a1,a2);
                    latitudes[z1]=line.substring(a5,a6);
                    longitudes[z1]=line.substring(a7,a8);
                    z1+=1;
                }

                br.close();

            } catch (Exception e) {
                Log.e(TAG, "error");
                e.printStackTrace();
                //close dialog if error occurs
                if (pd != null) pd.dismiss();
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            //close dialog
            if (pd != null)
                pd.dismiss();

            TextView txtview = (TextView) findViewById(R.id.text_view);
            txtview.setMovementMethod(ScrollingMovementMethod.getInstance());
            //display read text in TextView
            txtview.setText(text);

            TextView txtview1 = (TextView) findViewById(R.id.textView);
            txtview1.setMovementMethod(ScrollingMovementMethod.getInstance());
            //display read text in TextView
            txtview1.setText("Above contacts saved");

/*
            int num_contact = 5;
            int j = 0, k = 0;
            String a,b,c,d;

            for (int i = 0; i < num_contact; i++) {
                num1 = num.substring(i * 12 + i, 12 * (i + 1) + i + 1);
                k = Integer.valueOf(num1.substring(11, 13)) + j;
                data1 = text.substring(j, k);

                Log.d(TAG, data1);
                j = k + 1;
                Log.d(TAG, num1);
                //save_contact(data1,num1);
            }
*/

        }
    }




    public void savecode(String name, String email, String ph1, String ph2){
        ArrayList<ContentProviderOperation> ops = new ArrayList < ContentProviderOperation > ();
        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        if (name != null) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            name).build());
        }
        if (ph1 != null) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, ph1)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        //------------------------------------------------------ Home Numbers
        if (ph2 != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, ph2)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                    .build());
        }


        //------------------------------------------------------ Email
        if (email != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }
        try
        {
            //ContentProviderResult[] res = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        }
        catch (RemoteException e)
        {
            Log.e(TAG,"errrrrrrrrrrrrrrrrrrrrrrrooooooooooooooooooooooooorrrrrrrrrrrrrrrr11111111111111");
            // error
        }
        catch (OperationApplicationException e)
        {
            Log.e(TAG,"errrrrrrrrrrrrrrrrrrrrrrrooooooooooooooooooooooooorrrrrrrrrrrrrrrr2222222222222");
            // error
        }

    }



    public void showOnMap(String contactNames, String latitudes,String longitudes)
    {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("com.contactdownload.Names", contactNames);
        intent.putExtra("com.contactdownload.Latitudes", latitudes);
        intent.putExtra("com.contactdownload.Longitudes", longitudes);
        startActivity(intent);
    }
}