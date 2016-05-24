package com.acrcloud.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.acrcloud.utils.ACRCloudRecognizer;
import com.acrcloud.utils.JsonUtils;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView mVolume, mResult, tv_time;

    private boolean mProcessing = false;
    private boolean initState = false;

    private String path = "";
    public AssetManager mgr;

    //JSON Node Names
    private static final String TAG_USER = "custom_files";
    private static final String TAG_Brand = "Brand";
    private static final String TAG_About = "About";

    JSONArray user = null;

    String position = "1";
    String city = "";
    String weather = "";
    String temperature = "";
    String windSpeed = "";
    String iconfile = "";

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String res = (String) msg.obj;
                    mResult.setText(res);
                    break;

                default:
                    break;
            }
        }

        ;
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get position to display
        Intent i = getIntent();

        this.position = i.getStringExtra("position");
        this.city = i.getStringExtra("city");
        this.weather = i.getStringExtra("weather");
        //this.temperature =  i.getStringExtra("temperature");
        this.windSpeed = i.getStringExtra("windspeed");
        this.iconfile = i.getStringExtra("icon");


        mgr = getAssets();
        String mediafile = new String("");
        if (iconfile.equalsIgnoreCase("coke")) {
            mediafile = "https://dl.dropboxusercontent.com/s/fuzgbuoprwl15h3/coke.mp4";
        } else if (iconfile.equalsIgnoreCase("jaguar")) {
            mediafile = "https://dl.dropboxusercontent.com/s/u5xoa5vi3bkr6hz/jaguar.mp4";
        } else if (iconfile.equalsIgnoreCase("lego")) {
            mediafile = "https://dl.dropboxusercontent.com/s/n0cgtlrghavsm06/lego.mp4";
        } else if (iconfile.equalsIgnoreCase("raymond")) {
            mediafile = "https://dl.dropboxusercontent.com/s/e6vwzxa3ph87iod/raymond.mp4";
        } else if (iconfile.equalsIgnoreCase("amazon")) {
            mediafile = "https://dl.dropboxusercontent.com/s/glqdlxaien2dj6e/amazon.mp4";
        }else if (iconfile.equalsIgnoreCase("mercedez")) {
            mediafile = "https://dl.dropboxusercontent.com/s/br2izw5miztbn81/mercedez.mp4";
        }


        VideoView myVideoView = (VideoView) findViewById(R.id.videoview);
        myVideoView.setVideoURI(Uri.parse(mediafile));
        myVideoView.setMediaController(new MediaController(this));
        myVideoView.requestFocus();
        myVideoView.start();


        mResult = (TextView) findViewById(R.id.result);

        Button recBtn = (Button) findViewById(R.id.rec);
        recBtn.setText(getResources().getString(R.string.rec));

        findViewById(R.id.rec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                rec();
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.acrcloud.demo/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.acrcloud.demo/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    class RecThread extends Thread {

        public void run() {
            Map<String, Object> config = new HashMap<String, Object>();
            // Replace "xxxxxxxx" below with your project's access_key and access_secret.
            config.put("access_key", "08e4517a56f73380b5f8e03766e23b90");
            config.put("access_secret", "oDRakcljIsnwCSJTRnKv46jzXYwtrbTJYfhXE4Ps");
            config.put("host", "ap-southeast-1.api.acrcloud.com");
            config.put("debug", false);
            config.put("timeout", 5);

            ACRCloudRecognizer re = new ACRCloudRecognizer(config);

            FileInputStream fin = null;
            String result = new String("");
            String mediafile = new String("");
            if (iconfile.equalsIgnoreCase("mercedez")) {
                mediafile = "mercedez.mp4";
            } else if (iconfile.equalsIgnoreCase("coke")) {
                mediafile = "coke.mp4";
            } else if (iconfile.equalsIgnoreCase("jaguar")) {
                mediafile = "jaguar.mp4";
            } else if (iconfile.equalsIgnoreCase("lego")) {
                mediafile = "lego.mp4";
            } else if (iconfile.equalsIgnoreCase("raymond")) {
                mediafile = "raymond.mp4";
            } else if (iconfile.equalsIgnoreCase("amazon")) {
                mediafile = "amazon.mp4";
            }
            try {
                InputStream is = mgr.open(mediafile);
                int size = is.available();
                byte[] buffer = new byte[size]; //declare the size of the byte array with size of the file
                is.read(buffer); //read file
                result = re.recognizeByFileBuffer(buffer, size, 5);
                is.close(); //close file
            } catch (Exception e) {
                e.printStackTrace();
            }


            JSONObject jObj = null;
            Map<String, Object> retMap = new HashMap<String, Object>();
            // try parse the string to a JSON object
            try {
                jObj = new JSONObject(result);
                retMap = JsonUtils.jsonToMap(jObj);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }

            HashMap<String, Object> ret = ((HashMap<String, Object>) retMap.get("metadata"));
            ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) ret.get("custom_files");

            for (int i = 0; i < list.size();  i++){
                HashMap<String, Object> data = list.get(i);
                result = data.get("About").toString();
            }

//            try {
//                // Getting JSON Array
//                user = jObj.getJSONArray(TAG_USER);
//                JSONObject c = user.getJSONObject(0);
//
//                // Storing  JSON item in a Variable
//                String id = c.getString(TAG_Brand);
//                String name = c.getString(TAG_About);

            //Set JSON Data in TextView
            //  uid.setText(id);
            //  name1.setText(name);
            // email1.setText(email);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

            try {
                Message msg = new Message();
                msg.obj = result;

                msg.what = 1;
                mHandler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void rec() {
        new RecThread().start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
