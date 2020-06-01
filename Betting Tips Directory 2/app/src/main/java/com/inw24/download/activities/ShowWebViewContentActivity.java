package com.inw24.download.activities;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Build;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.inw24.download.Config;
import com.inw24.download.R;

public class ShowWebViewContentActivity extends AppCompatActivity {
    private String contentTitle;
    private String contentUrl;
    private String contentOrientation;
    private WebView showContentWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.activity_show_webview_content);
        // Makes Progress bar Visible
        getWindow().setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);

        //Set to RTL if true
        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("Log", "Working in Normal Mode, RTL Mode is Disabled");
        }

        //Get item url
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("contentUrl")) {
                contentTitle = extras.getString("contentTitle");
                contentUrl = extras.getString("contentUrl");
                contentOrientation = extras.getString("contentOrientation");
            }
        }

        //Set Activity Orientation
        if(contentOrientation.equals("1")) { // It does not matter
            //Not change
        }else if(contentOrientation.equals("2")) { // Portrait
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else if(contentOrientation.equals("3")) {  // Landscape
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        showContentWebView = (WebView) findViewById(R.id.wv_show_video);
        WebSettings settingWebView = showContentWebView.getSettings();
        settingWebView.setJavaScriptEnabled(true);
        settingWebView.setAllowFileAccess(true);
        settingWebView.setDomStorageEnabled(true);

        showContentWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {
                //Make the bar disappear after URL is loaded, and changes string to Loading...
                setTitle(R.string.txt_loading);
                setProgress(progress * 100); //Make the bar disappear after URL is loaded

                // Return the app name after finish loading
                if(progress == 100)
                    setTitle(contentTitle);
            }
        });
        showContentWebView.loadUrl(contentUrl);
    }

    //==========================================================================//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.common, menu);
        return true;
    }


    //==========================================================================//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_back) {
            showContentWebView.stopLoading();
            showContentWebView.loadUrl("");
            showContentWebView.reload();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.txt_exit));
        builder.setMessage(getString(R.string.txt_confirm_exit));

        builder.setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                showContentWebView.stopLoading();
                showContentWebView.loadUrl("");
                showContentWebView.reload();
                finish();
            }
        });

        builder.setNegativeButton(getString(R.string.txt_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
