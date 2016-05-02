package com.acrcloud.demo;

import android.Manifest;
import android.app.Activity;
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
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.acrcloud.utils.ACRCloudRecognizer;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView mVolume, mResult, tv_time;

    private boolean mProcessing = false;
    private boolean initState = false;

    private String path = "";
    public AssetManager mgr;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    String res = (String) msg.obj;
                    mResult.setText(res);
                    break;

                default:
                    break;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        path = Environment.getExternalStorageDirectory().toString()
               + "/acrcloud/model";

      //  path = getExternalFilesDir(null).toString()+ "/acrcloud/model";

        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }

        String dirPath ="/Android/data/com.acrcloud.demo/acrcloud/model";
        File projDir = new File(dirPath);
        if (!projDir.exists())
            projDir.mkdirs();

        MediaPlayer player;
        mgr = getAssets();

        player = new MediaPlayer();
        try {
            AssetFileDescriptor afd = mgr.openFd("test.mp3");
            player.setDataSource(afd.getFileDescriptor());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.start();

//        MediaPlayer mp;
//        mp = MediaPlayer.create(this,R.raw.test);
//        mp.start();

//        MediaPlayer mpintro;
//       // mpintro = MediaPlayer.create(this, Uri.parse(getFilesDir().getAbsolutePath()+ "/acrcloud/model/test.mp3"));
//        mpintro = MediaPlayer.create(this, Uri.parse(getExternalFilesDir(null).getPath()+ "/acrcloud/model/test.mp3"));
//        mpintro.setLooping(true);
//        mpintro.start();

//        MediaPlayer mediaPlayer;
//        String filePath = Environment.getExternalStorageDirectory()+"/acrcloud/model/test.mp3";
//        mediaPlayer = new  MediaPlayer();
//
//        FileDescriptor fd = null;
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream(Environment.getExternalStorageDirectory()+"/acrcloud/model/test.mp3");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        try {
//            fd = fis.getFD();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            //mediaPlayer.setDataSource("/android/data/com.acrcloud.demo/acrcloud/model/test.mp3");
//            mediaPlayer.setDataSource(fd);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            mediaPlayer.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mediaPlayer.start();

        mResult = (TextView) findViewById(R.id.result);

        Button recBtn = (Button) findViewById(R.id.rec);
        recBtn.setText(getResources().getString(R.string.rec));

        findViewById(R.id.rec).setOnClickListener(new View.OnClickListener() {
           // verifyStoragePermissions(this);

            @Override
            public void onClick(View arg0) {
                rec();
            }
        });


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
            String result1 = re.recognizeByFile(path + "/test.mp3", 50);
            System.out.println(result1);



//            File file = new File(path + "/test.mp3");
//            byte[] buffer = new byte[10 * 1024 * 1024];
//            if (!file.exists()) {
//                return;
//            }
            FileInputStream fin = null;
            String result = new String("");
            try {
                InputStream is = mgr.open("test.mp3");
                int size = is.available();
                byte[] buffer = new byte[size]; //declare the size of the byte array with size of the file
                is.read(buffer); //read file
                result = re.recognizeByFileBuffer(buffer, size, 80);
                is.close(); //close file
            } catch(Exception e){
                e.printStackTrace();
            }
//            int bufferLen = 0;
//            try {
//                fin = new FileInputStream(file);
//                bufferLen = fin.read(buffer, 0, buffer.length);
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (fin != null) {
//                        fin.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
            //System.out.println("bufferLen=" + bufferLen);

//            if (bufferLen <= 0)
//                return;
//
//            String result = re.recognizeByFileBuffer(buffer, bufferLen, 80);

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
    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
