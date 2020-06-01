package com.gabnitesolutions.bettingtipsdirectory.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.gabnitesolutions.bettingtipsdirectory.BuildConfig;
import com.gabnitesolutions.bettingtipsdirectory.Config;
import com.gabnitesolutions.bettingtipsdirectory.fragments.AboutFragment;
import com.gabnitesolutions.bettingtipsdirectory.fragments.CategoryFragment;
import com.gabnitesolutions.bettingtipsdirectory.fragments.MainFragment;
import com.gabnitesolutions.bettingtipsdirectory.fragments.ProfileFragment;
import com.gabnitesolutions.bettingtipsdirectory.fragments.SearchFragment;
import com.gabnitesolutions.bettingtipsdirectory.utils.AppController;
import com.gabnitesolutions.bettingtipsdirectory.utils.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import com.gabnitesolutions.bettingtipsdirectory.R;

import com.gabnitesolutions.bettingtipsdirectory.fragments.WebViewFragment;
import com.onesignal.OneSignal;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    //Create object for FrameLayout and Fragments
    private FrameLayout frmMain;
    private DrawerLayout drawerLayout;
    private MainFragment mainFragment = new MainFragment();
    private CategoryFragment categoryFragment = new CategoryFragment();
    private SearchFragment searchFragment = new SearchFragment();
    private WebViewFragment webViewFragmentAbout = new WebViewFragment();
    private WebViewFragment webViewFragmentContact = new WebViewFragment();
    private AboutFragment aboutAppFragment = new AboutFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private PrefManager prefManager;
    String mobileUserLogin;
    RequestQueue rqGetAllAfterLogin;
    String settingVersionCode;
    private InterstitialAd mInterstitialAd;


    //For Custom Font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        //Set to RTL if true
        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("Log", "Working in Normal Mode, RTL Mode is Disabled");
        }

        //Access the FrameLayout
        frmMain = (FrameLayout) findViewById(R.id.frmMain);

        //Load Default and Main fragment in MainActivity
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        transaction.replace(R.id.frmMain, mainFragment);
        transaction.commit();

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Hide login nav menu if user logged in
        //Check user login
        SharedPreferences prefs = getSharedPreferences("USER_LOGIN", MODE_PRIVATE);
        mobileUserLogin = prefs.getString("mobile", null);

        Menu nav_Menu = navigationView.getMenu();
        if (mobileUserLogin != null) { //User is Login
            //Set menu
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            nav_Menu.findItem(R.id.nav_register).setVisible(false);
            nav_Menu.findItem(R.id.nav_bookmark).setVisible(true);
            nav_Menu.findItem(R.id.nav_logout).setVisible(true);
            nav_Menu.findItem(R.id.nav_profile).setVisible(true);

            //Get all information after login
            getAllAfterLogin();

        }else { //User Not Login
            nav_Menu.findItem(R.id.nav_login).setVisible(true);
            nav_Menu.findItem(R.id.nav_register).setVisible(true);
            nav_Menu.findItem(R.id.nav_bookmark).setVisible(false);
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
            nav_Menu.findItem(R.id.nav_profile).setVisible(false);

            //Get all information after login
            getAllBeforeLogin();
        }

    }


    //==========================================================================//
    public void getAllAfterLogin() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Config.GET_ALL_AFTER_LOGIN_URL + "?user_username="+mobileUserLogin+"&api_key="+ Config.API_KEY, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        //Set local variable and use it in whole application
                        ((AppController) MainActivity.this.getApplication()).setUserId(jsonObject.getString("user_id"));
                        ((AppController) MainActivity.this.getApplication()).setUserUserName(jsonObject.getString("user_username"));
                        ((AppController) MainActivity.this.getApplication()).setUserFirstName(jsonObject.getString("user_firstname"));
                        ((AppController) MainActivity.this.getApplication()).setUserLastName(jsonObject.getString("user_lastname"));
                        ((AppController) MainActivity.this.getApplication()).setUserMobile(jsonObject.getString("user_mobile"));
                        ((AppController) MainActivity.this.getApplication()).setUserEmail(jsonObject.getString("user_email"));
                        ((AppController) MainActivity.this.getApplication()).setUserCredit(jsonObject.getString("user_credit"));
                        ((AppController) MainActivity.this.getApplication()).setUserCoin(jsonObject.getString("user_coin"));
                        ((AppController) MainActivity.this.getApplication()).setUserReferral(jsonObject.getString("user_referral"));
                        ((AppController) MainActivity.this.getApplication()).setUserEmailVerified(jsonObject.getString("user_email_verified"));
                        ((AppController) MainActivity.this.getApplication()).setUserMobileVerified(jsonObject.getString("user_mobile_verified"));
                        ((AppController) MainActivity.this.getApplication()).setUserRoleID(jsonObject.getString("user_role_id"));
                        ((AppController) MainActivity.this.getApplication()).setUserRoleTitle(jsonObject.getString("user_role_title"));
                        ((AppController) MainActivity.this.getApplication()).setSettingAppName(jsonObject.getString("setting_app_name"));
                        ((AppController) MainActivity.this.getApplication()).setSettingEmail(jsonObject.getString("setting_email"));
                        ((AppController) MainActivity.this.getApplication()).setSettingVersionCode(jsonObject.getString("setting_version_code"));
                        ((AppController) MainActivity.this.getApplication()).setSettingAndroidMaintenance(jsonObject.getString("setting_android_maintenance"));
                        ((AppController) MainActivity.this.getApplication()).setSettingTextMaintenance(jsonObject.getString("setting_text_maintenance"));

                        //Check App Version
                        versionCheck();
                        //Check Maintenance Status
                        maintenanceCheck();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("BlueDev Volley Error: ", error + "");
                //Toast.makeText(MainActivity.this, R.string.txt_error, Toast.LENGTH_SHORT).show();
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
    }


    //==========================================================================//
    public void getAllBeforeLogin() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Config.GET_ALL_BEFORE_LOGIN_URL + "?api_key="+ Config.API_KEY, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        //Set local variable and use it in whole application
                        ((AppController) MainActivity.this.getApplication()).setSettingAppName(jsonObject.getString("setting_app_name"));
                        ((AppController) MainActivity.this.getApplication()).setSettingEmail(jsonObject.getString("setting_email"));
                        ((AppController) MainActivity.this.getApplication()).setSettingVersionCode(jsonObject.getString("setting_version_code"));
                        ((AppController) MainActivity.this.getApplication()).setSettingAndroidMaintenance(jsonObject.getString("setting_android_maintenance"));
                        ((AppController) MainActivity.this.getApplication()).setSettingTextMaintenance(jsonObject.getString("setting_text_maintenance"));

                        //Check App Version
                        versionCheck();
                        //Check Maintenance Status
                        maintenanceCheck();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("BlueDev Volley Error: ", error + "");
                //Toast.makeText(MainActivity.this, R.string.txt_error, Toast.LENGTH_SHORT).show();
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
    }


    //==========================================================================//
    public void versionCheck() {
        //Get versionCode from local variable
        settingVersionCode = ((AppController) this.getApplication()).getSettingVersionCode();
        int appVersionCode = BuildConfig.VERSION_CODE;
        if (appVersionCode < Integer.parseInt(settingVersionCode))
        {
            //Your app is old
            AlertDialog.Builder builderCheckUpdate = new AlertDialog.Builder(MainActivity.this);
            builderCheckUpdate.setTitle(getResources().getString(R.string.txt_check_update_title));
            builderCheckUpdate.setMessage(getResources().getString(R.string.txt_check_update_msg));
            builderCheckUpdate.setCancelable(false);

            builderCheckUpdate.setPositiveButton(
                    getResources().getString(R.string.txt_get_update),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.txt_update_url)));
                            startActivity(browserIntent);
                        }
                    });

            builderCheckUpdate.setNegativeButton(
                    getResources().getString(R.string.txt_later),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert1CheckUpdate = builderCheckUpdate.create();
            alert1CheckUpdate.show();
        }
    }


    //==========================================================================//
    public void maintenanceCheck() {
        //Get versionCode from local variable
        String settingMaintenance = ((AppController) this.getApplication()).getSettingAndroidMaintenance();
        String settingMaintenanceMessage = ((AppController) this.getApplication()).getSettingTextMaintenance();
        if (settingMaintenance.equals("1"))
        {
            //Maintenance mode is enable
            AlertDialog.Builder builderCheckUpdate = new AlertDialog.Builder(MainActivity.this);
            builderCheckUpdate.setTitle(getResources().getString(R.string.txt_maintenance_title));
            builderCheckUpdate.setMessage(settingMaintenanceMessage);
            builderCheckUpdate.setCancelable(false);

            builderCheckUpdate.setPositiveButton(
                    getResources().getString(R.string.txt_ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });

            /*builderCheckUpdate.setNegativeButton(
                    getResources().getString(R.string.txt_later),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });*/

            AlertDialog alert1CheckUpdate = builderCheckUpdate.create();
            alert1CheckUpdate.show();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            super.onBackPressed();
            //To fix title bug.
            if (mainFragment != null && mainFragment.isVisible()) {
                setTitle(R.string.app_name);
            }
        }
    }


    //==========================================================================//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        if (id == R.id.action_settings) {
            return true;

        }if (id == R.id.action_search) {
            Bundle bundle = new Bundle();
            bundle.putString("showWhichContent","");
            bundle.putString("showTitle", getString(R.string.menu_search));
            searchFragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                    .replace(R.id.frmMain, searchFragment, "SEARCH_FRAGMENT")
                    .addToBackStack(null)
                    .commit();


            //To refresh searchFragment if clicked on the search toolbar again to show search form
            if (searchFragment != null && searchFragment.isVisible()) {
                //getSupportFragmentManager().beginTransaction().detach(searchFragment).attach(searchFragment).commit();
                //Bundle bundle = new Bundle();
                bundle.putString("showWhichContent","");
                bundle.putString("showTitle", getString(R.string.menu_search));
                searchFragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                        .detach(searchFragment)
                        .attach(searchFragment)
                        .addToBackStack(null)
                        .commit();
            }

        }

        return super.onOptionsItemSelected(item);
    }


    //==========================================================================//
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //transaction.setCustomAnimations(R.anim.enter, R.anim.exit); //Start Animation
            transaction.replace(R.id.frmMain, mainFragment);
            //To support back to previous fragment
            transaction.addToBackStack(null);
            transaction.commit();

            //To refresh current fragment if clicked
            if (mainFragment != null && mainFragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(mainFragment).attach(mainFragment).commit();
            }

        } else if (id == R.id.nav_category) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit); //Start Animation

            //Pass variable to fragment
            /*Bundle bundle = new Bundle();
            String myMessage = "Armin MSG";
            bundle.putString("message", myMessage);
            categoryFragment.setArguments(bundle);*/

            transaction.replace(R.id.frmMain, categoryFragment);
            //To support back to previous fragment
            transaction.addToBackStack(null);
            transaction.commit();

            //To refresh current fragment if clicked
            if (categoryFragment != null && categoryFragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(categoryFragment).attach(categoryFragment).commit();
            }

        } else if (id == R.id.nav_bookmark) {
            //Pass data from Fragment to Fragment
            Bundle bundle = new Bundle();
            bundle.putString("showWhichContent","BookmarkContent");
            bundle.putString("showTitle", getString(R.string.nav_bookmark));
            searchFragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                    .replace(R.id.frmMain, searchFragment)
                    .addToBackStack(null)
                    .commit();

            //To refresh current fragment if clicked
            if (searchFragment != null && searchFragment.isVisible()) {
                //getSupportFragmentManager().beginTransaction().detach(bookmarkFragment).attach(bookmarkFragment).commit();
                bundle.putString("showWhichContent","BookmarkContent");
                bundle.putString("showTitle", getString(R.string.nav_bookmark));
                searchFragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                        .detach(searchFragment)
                        .attach(searchFragment)
                        .addToBackStack(null)
                        .commit();
            }

        } else if (id == R.id.nav_contact) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit); //Start Animation

            //Pass variable to fragment
            Bundle bundle = new Bundle();
            String theTitle = getString(R.string.nav_contact);
            String theUrl = Config.PAGE_CONTACT_US + "?api_key=" + Config.API_KEY;
            bundle.putString("title", theTitle);
            bundle.putString("url", theUrl);
            webViewFragmentContact.setArguments(bundle);

            transaction.replace(R.id.frmMain, webViewFragmentContact);
            //To support back to previous fragment
            transaction.addToBackStack(null);
            transaction.commit();

            //To refresh current fragment if clicked
            if (webViewFragmentContact != null && webViewFragmentContact.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(webViewFragmentContact).attach(webViewFragmentContact).commit();
            }

        } else if (id == R.id.nav_help) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit); //Start Animation

            //Pass variable to fragment
            Bundle bundle = new Bundle();
            String theTitle = getString(R.string.nav_help);
            String theUrl = Config.PAGE_HELP + "?api_key=" + Config.API_KEY;
            bundle.putString("title", theTitle);
            bundle.putString("url", theUrl);
            webViewFragmentContact.setArguments(bundle);

            transaction.replace(R.id.frmMain, webViewFragmentContact);
            //To support back to previous fragment
            transaction.addToBackStack(null);
            transaction.commit();

            //To refresh current fragment if clicked
            if (webViewFragmentContact != null && webViewFragmentContact.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(webViewFragmentContact).attach(webViewFragmentContact).commit();
            }

        } else if (id == R.id.nav_about_app) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit); //Start Animation
            transaction.replace(R.id.frmMain, aboutAppFragment);
            //To support back to previous fragment
            transaction.addToBackStack(null);
            transaction.commit();

            //To refresh current fragment if clicked
            if (aboutAppFragment != null && aboutAppFragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(aboutAppFragment).attach(aboutAppFragment).commit();
            }

        } else if (id == R.id.nav_share_app) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = getString(R.string.txt_share);
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.app_name);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));

        } else if (id == R.id.nav_login) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));

        } else if (id == R.id.nav_register) {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));

        } else if (id == R.id.nav_profile) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit); //Start Animation

            transaction.replace(R.id.frmMain, profileFragment);
            //To support back to previous fragment
            transaction.addToBackStack(null);
            transaction.commit();

            //To refresh current fragment if clicked
            if (profileFragment != null && profileFragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(profileFragment).attach(profileFragment).commit();
            }

        } else if (id == R.id.nav_logout) {
            SharedPreferences prefs = getSharedPreferences("USER_LOGIN", MODE_PRIVATE);
            prefs.edit().clear().commit();
            //SharedPreferences users = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
            //users.edit().clear().commit();
            Toast.makeText(getApplicationContext(),R.string.txt_logout_successfully,Toast.LENGTH_SHORT).show();

            Intent refresh = new Intent(MainActivity.this, MainActivity.class);
            startActivity(refresh);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
