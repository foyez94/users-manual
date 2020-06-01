package com.inw24.download.utils;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import com.inw24.download.R;


public class AppController extends Application
{
	public static final String TAG = AppController.class.getSimpleName();
	private RequestQueue mRequestQueue;
	private static AppController mInstance;

	private String userId;
	private String userUserName;
	private String userFirstName;
	private String userLastName;
	private String userMobile;
	private String userEmail;
	private String userCoin;
	private String userCredit;
	private String userReferral;
	private String userEmailVerified;
	private String userMobileVerified;
	private String userRoleID;
	private String userRoleTitle;
	private String settingAppName;
	private String settingEmail;
	private String settingVersionCode;
	private String settingAndroidMaintenance;
	private String settingTextMaintenance;

	public String getUserRoleID() {
		return userRoleID;
	}

	public void setUserRoleID(String userRoleID) {
		this.userRoleID = userRoleID;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserRoleTitle() {
		return userRoleTitle;
	}

	public void setUserRoleTitle(String userRoleTitle) {
		this.userRoleTitle = userRoleTitle;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public static String getTAG() {
		return TAG;
	}

	public String getUserUserName() {
		return userUserName;
	}

	public void setUserUserName(String userUserName) {
		this.userUserName = userUserName;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public String getUserCoin() {
		return userCoin;
	}

	public void setUserCoin(String userCoin) {
		this.userCoin = userCoin;
	}

	public String getUserCredit() {
		return userCredit;
	}

	public void setUserCredit(String userCredit) {
		this.userCredit = userCredit;
	}

	public String getUserReferral() {
		return userReferral;
	}

	public void setUserReferral(String userReferral) {
		this.userReferral = userReferral;
	}

	public String getUserEmailVerified() {
		return userEmailVerified;
	}

	public void setUserEmailVerified(String userEmailVerified) {
		this.userEmailVerified = userEmailVerified;
	}

	public String getUserMobileVerified() {
		return userMobileVerified;
	}

	public void setUserMobileVerified(String userMobileVerified) {
		this.userMobileVerified = userMobileVerified;
	}

	public String getSettingAppName() {
		return settingAppName;
	}

	public void setSettingAppName(String settingAppName) {
		this.settingAppName = settingAppName;
	}

	public String getSettingEmail() {
		return settingEmail;
	}

	public void setSettingEmail(String settingEmail) {
		this.settingEmail = settingEmail;
	}

	public String getSettingVersionCode() {
		return settingVersionCode;
	}

	public void setSettingVersionCode(String settingVersionCode) {
		this.settingVersionCode = settingVersionCode;
	}

	public String getSettingAndroidMaintenance() {
		return settingAndroidMaintenance;
	}

	public void setSettingAndroidMaintenance(String settingAndroidMaintenance) {
		this.settingAndroidMaintenance = settingAndroidMaintenance;
	}

	public String getSettingTextMaintenance() {
		return settingTextMaintenance;
	}

	public void setSettingTextMaintenance(String settingTextMaintenance) {
		this.settingTextMaintenance = settingTextMaintenance;
	}

	//Set and Get local variable
	// To Set
	//((AppController) this.getApplication()).setSomeVariable("foo");
	//((AppController) MainActivity.this.getApplication()).setUserId(jsonObject.getString("user_id"));

	// To Get
	//String s = ((AppController) this.getApplication()).getSomeVariable();

	@Override
	public void onCreate()
	{
		super.onCreate();
		mInstance = this;
		//For Custom Font
		ViewPump.init(ViewPump.builder()
				.addInterceptor(new CalligraphyInterceptor(
						new CalligraphyConfig.Builder()
								.setDefaultFontPath("fonts/custom.ttf")
								.setFontAttrId(R.attr.fontPath)
								.build()))
				.build());

	}

	public static synchronized AppController getInstance()
	{
		return mInstance;
	}

	public RequestQueue getRequestQueue()
	{
		if (mRequestQueue == null)
		{
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag)
	{
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req)
	{
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag)
	{
		if (mRequestQueue != null)
		{
			mRequestQueue.cancelAll(tag);
		}
	}

	public void cancelPendingRequests()
	{
		if (mRequestQueue != null)
		{
			mRequestQueue.cancelAll(TAG);
		}
	}
}