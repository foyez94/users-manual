package com.inw24.download.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.inw24.download.Config;
import com.inw24.download.utils.AppController;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import com.inw24.download.R;

import static com.inw24.download.Config.DIRECTION;

public class OneContentLinkActivity extends AppCompatActivity {
    private String buttonText;
    private String contentId;
    private String contentTitle;
    private String categoryTitle;
    private String contentImage;
    private String contentUrl;
    private String contentDuration;
    private String contentViewed;
    private String contentPublishDate;
    private String contentTypeTitle;
    private String contentTypeId;
    private String contentUserRoleId;
    private String contentOrientation;
    private String userUsername;
    private WebView wvOneContent;
    Context context = this;
    String contentBookmark;
    String contentDescription;
    String mobileUserLogin;
    String user_role_id;
    String userId;
    Button showContent;
    FloatingActionButton floatingActionButtonBookmark;
    FloatingActionButton floatingActionButtonShowLink;
    CoordinatorLayout coordinatorLayoutOneContentLink;
    private ProgressWheel progressWheelInterpolated;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    //For Custom Font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_content_link);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayoutOneContentLink = (CoordinatorLayout) findViewById(R.id.coordinatorLayoutOneContentLink);
        LinearLayout linearLayoutAds = (LinearLayout) findViewById(R.id.ll_ads);

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, getString(R.string.config_admob_app_id));
        mAdView = findViewById(R.id.adView);
        if (Config.ENABLE_ADMOB_BANNER_ADS) {
            mAdView.setVisibility(View.VISIBLE);
            linearLayoutAds.setVisibility(View.VISIBLE);
        }
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Interstitial Ad
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, getString(R.string.config_admob_app_id));
        // Create the InterstitialAd and set the adUnitId.
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.config_admob_interstitial_unit_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        //Material ProgressWheel
        progressWheelInterpolated = (ProgressWheel) findViewById(R.id.one_item_progress_wheel);

        //Check user is login
        SharedPreferences prefs = getSharedPreferences("USER_LOGIN", MODE_PRIVATE);
        mobileUserLogin = prefs.getString("mobile", null);


        //Get Local Variable Exampel
        //String fullName = ((AppController) this.getApplication()).getUserFullname();
        //Toast.makeText(OneItemActivity.this, "Name: "+fullName, Toast.LENGTH_SHORT).show();

        //Set to RTL if true
        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("Log", "Working in Normal Mode, RTL Mode is Disabled");
        }

        //Get Intent Data
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("contentId")) {
                buttonText = extras.getString("buttonText");
                contentId = extras.getString("contentId");
                contentTitle = extras.getString("contentTitle");
                categoryTitle = extras.getString("categoryTitle");
                contentImage = extras.getString("contentImage");
                contentUrl = extras.getString("contentUrl");
                contentDuration = extras.getString("contentDuration");
                contentViewed = extras.getString("contentViewed");
                contentPublishDate = extras.getString("contentPublishDate");
                contentTypeTitle = extras.getString("contentTypeTitle");
                contentTypeId = extras.getString("contentTypeId");
                contentUserRoleId = extras.getString("contentUserRoleId");
                contentOrientation = extras.getString("contentOrientation");
            }
        }


        //Set ActionBar Title
        setTitle("");

        //Get userID from local variable
        userId = ((AppController) this.getApplication()).getUserId();

        //Set background image to app bar
        String contentImageURL = Config.CONTENT_IMG_URL+contentImage;
        ImageView contentImageView = (ImageView) findViewById(R.id.iv_one_content_image);
        Glide
            .with(OneContentLinkActivity.this)
            .load(contentImageURL)
            .apply(new RequestOptions()
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.ALL))
            .into(contentImageView);

        TextView oneContentTitle = (TextView)findViewById(R.id.tv_one_content_title);
        oneContentTitle.setText(contentTitle);

        TextView oneContentCategory = (TextView)findViewById(R.id.tv_one_content_category);
        oneContentCategory.setText(categoryTitle);

        TextView oneContentDate = (TextView)findViewById(R.id.tv_one_content_date);
        oneContentDate.setText(Config.TimeAgo(contentPublishDate));

        TextView oneContentViewed = (TextView)findViewById(R.id.tv_one_content_viewed);
        oneContentViewed.setText(contentViewed+" "+getString(R.string.txt_viewed));

        wvOneContent = (WebView) findViewById(R.id.wv_one_content);
        String text = "<html dir='"+DIRECTION+"'><head>"
                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/custom.ttf\")}body{font-family: MyFont;color: #424242; text-align:justify; direction:"+DIRECTION+"; line-height:23px;}"
                + "</style></head>"
                + "<body>"
                + getString(R.string.txt_loading)
                + "</body></html>";
        wvOneContent.loadDataWithBaseURL(null,text, "text/html; charset=UTF-8", "utf-8", null);
        sendJsonArrayRequest();

        //Total View + 1 //
        totalContentView();

        floatingActionButtonBookmark = (FloatingActionButton) findViewById(R.id.fabBookmark);
        //Get bookmark status
        if (mobileUserLogin != null) {
            getBookmarkStatus();

        }else{
            floatingActionButtonBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Please login first then click on the bookmark
                    Snackbar snackbar = Snackbar.make(coordinatorLayoutOneContentLink, R.string.txt_please_login_first, Snackbar.LENGTH_LONG)
                            .setAction(R.string.txt_bookmark_login, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(OneContentLinkActivity.this, LoginActivity.class));
                                }
                            });
                    snackbar.setActionTextColor(getResources().getColor(R.color.colorYellow));
                    snackbar.show();
                }
            });
        }

        floatingActionButtonShowLink = (FloatingActionButton) findViewById(R.id.fabShowVideo);
        floatingActionButtonShowLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Send contentURL to ShowWebViewContentActivity
                Intent intent = new Intent(context, ShowWebViewContentActivity.class);
                intent.putExtra("contentTitle", contentTitle);
                intent.putExtra("contentUrl", contentUrl);
                intent.putExtra("contentOrientation", contentOrientation);
                startActivity(intent);
            }
        });

        showContent = (Button)findViewById(R.id.btn_show_content);
        showContent.setText(buttonText);
        showContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contentTypeId.equals("1") || contentTypeId.equals("2"))
                {
                    //Go to browser
                    Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(contentUrl));
                    startActivity(browser);

                }else{
                    //Send contentURL to ShowWebViewContentActivity
                    Intent intent = new Intent(context, ShowWebViewContentActivity.class);
                    intent.putExtra("contentTitle", contentTitle);
                    intent.putExtra("contentUrl", contentUrl);
                    intent.putExtra("contentOrientation", contentOrientation);
                    startActivity(intent);
                }
            }
        });
    }


    //============================================================================//
    private void getBookmarkStatus()
    {
        StringRequest requestPostResponse = new StringRequest(Request.Method.GET, Config.GET_BOOKMARK_STATUS+"?user_id="+userId+"&content_id="+contentId+"&api_key="+ Config.API_KEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String getAnswer = response.toString();
                        if (getAnswer.equals("ContentIsBookmark")) {
                            //Toast.makeText(getApplicationContext(),"ItemIsBookmark",Toast.LENGTH_LONG).show();
                            floatingActionButtonBookmark.setImageResource(R.drawable.icon_bookmark_white_24);
                            floatingActionButtonBookmark.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //Change status to unBookmark
                                    floatingActionButtonBookmark.setImageResource(R.drawable.icon_bookmark_border_white_24);
                                    removeFromBookmark();
                                }
                            });

                        }else if (getAnswer.equals("ContentIsNotBookmark")) {
                            //Toast.makeText(getApplicationContext(),"ItemIsNotBookmark",Toast.LENGTH_LONG).show();
                            floatingActionButtonBookmark.setImageResource(R.drawable.icon_bookmark_border_white_24);
                            floatingActionButtonBookmark.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //Change status to Bookmark
                                    floatingActionButtonBookmark.setImageResource(R.drawable.icon_bookmark_white_24);
                                    addToBookmark();
                                }
                            });
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                });

        //To avoid send twice when internet speed is slow
        requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(25000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(requestPostResponse);
    }


    //============================================================================//
    private void removeFromBookmark()
    {
        //Remove From Bookmark
        progressWheelInterpolated.setVisibility(View.VISIBLE);

        StringRequest requestPostResponse = new StringRequest(Request.Method.POST, Config.REMOVE_FROM_BOOKMARK_URL + "?api_key=" + Config.API_KEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String getAnswer = response.toString();
                        Toast.makeText(getApplicationContext(), getAnswer ,Toast.LENGTH_LONG).show();
                        progressWheelInterpolated.setVisibility(View.GONE);
                        finish();
                        startActivity(getIntent());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),R.string.txt_error,Toast.LENGTH_LONG).show();
                        progressWheelInterpolated.setVisibility(View.GONE);
                    }
                }
        ){
            //To send our parametrs
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("user_id",userId);
                params.put("content_id",contentId);

                return params;
            }
        };

        //To avoid send twice when internet speed is slow
        requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(25000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(requestPostResponse);
    }

    //============================================================================//
    private void addToBookmark()
    {
        //Remove From Bookmark
        progressWheelInterpolated.setVisibility(View.VISIBLE);

        StringRequest requestPostResponse = new StringRequest(Request.Method.POST, Config.ADD_TO_BOOKMARK_URL + "?api_key=" + Config.API_KEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String getAnswer = response.toString();
                        Toast.makeText(getApplicationContext(), getAnswer ,Toast.LENGTH_LONG).show();
                        progressWheelInterpolated.setVisibility(View.GONE);
                        finish();
                        startActivity(getIntent());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),R.string.txt_error,Toast.LENGTH_LONG).show();
                        progressWheelInterpolated.setVisibility(View.GONE);
                    }
                }
        ){
            //To send our parametrs
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("user_id",userId);
                params.put("content_id",contentId);

                return params;
            }
        };

        //To avoid send twice when internet speed is slow
        requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(25000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(requestPostResponse);

    }


    /*============================================================================*/
    private void sendJsonArrayRequest()
    {
        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>()
        {
            @Override
            public void onResponse(JSONArray response)
            {
                try
                {
                    for (int i = 0; i < response.length(); i++)
                    {
                        JSONObject object = response.getJSONObject(i);
                        contentDescription = object.getString("content_description");
                        contentBookmark = object.getString("content_description");
                        userUsername = object.getString("user_username");
                        //contentUrl = object.getString("content_url");
                        //Publisher
                        //Toast.makeText(getApplicationContext(), "User: "+userUsername, Toast.LENGTH_LONG).show();


                        if(contentUrl.equals(""))
                        {
                            //No URL Found! So this is article
                            floatingActionButtonShowLink.setVisibility(View.GONE);
                        }


                        //To format as HTML
                        String formattedPageContent = android.text.Html.fromHtml(contentDescription).toString(); //Just for ckEditor

                        wvOneContent.getSettings().setJavaScriptEnabled(true);
                        wvOneContent.setFocusableInTouchMode(false);
                        wvOneContent.setFocusable(false);
                        WebSettings webSettings = wvOneContent.getSettings();
                        webSettings.setDefaultFontSize(Config.Font_Size);
                        wvOneContent.getSettings().setDefaultTextEncodingName("UTF-8");
                        String mimeType = "text/html; charset=UTF-8";
                        String encoding = "utf-8";

                        String text = "<html dir='"+DIRECTION+"'><head>"
                                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/custom.ttf\")}body{font-family: MyFont;color: #424242; text-align:justify; direction:"+DIRECTION+"; line-height:23px;}"
                                + "</style></head>"
                                + "<body>"
                                //+ formattedPageContent
                                + contentDescription
                                + "</body></html>";
                        wvOneContent.loadDataWithBaseURL(null,text, mimeType, encoding, null);
                    }
                }
                catch (Exception e)
                {

                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getApplicationContext(), R.string.txt_error, Toast.LENGTH_LONG).show();
            }
        };

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Config.GET_ONE_CONTENT_URL+contentId+"/?api_key=" + Config.API_KEY, null, listener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(25000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }


    //============================================================================//
    private void totalContentView(){
        Response.Listener<String> listener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                //Total itemView + 1
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                //Toast.makeText(getApplicationContext(), "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        StringRequest requestView = new StringRequest(Request.Method.POST, Config.TOTAL_CONTENT_VIEWED_URL + "?api_key=" + Config.API_KEY,listener,errorListener)
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> params = new HashMap<>();
                params.put("content_id",contentId);
                return params;
            }
        };
        requestView.setRetryPolicy(new DefaultRetryPolicy(9000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(requestView);
    }


    //============================================================================//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_back) {
            // Show Interstitial Ad
            if (mInterstitialAd.isLoaded()) {
                if (Config.ENABLE_ADMOB_INTERSTITIAL_ADS) {
                    mInterstitialAd.show();
                }
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.");
            }

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        // Show Interstitial Ad
        if (mInterstitialAd.isLoaded()) {
            if (Config.ENABLE_ADMOB_INTERSTITIAL_ADS) {
                mInterstitialAd.show();
            }
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }

        finish();
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

}