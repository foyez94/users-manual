package com.gabnitesolutions.bettingtipsdirectory.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;


import com.gabnitesolutions.bettingtipsdirectory.utils.AppController;

import com.gabnitesolutions.bettingtipsdirectory.R;

import com.gabnitesolutions.bettingtipsdirectory.utils.Tools;

import static com.gabnitesolutions.bettingtipsdirectory.Config.WEB_LOGIN_URL;


public class ProfileFragment extends Fragment {
    ScrollView userProfileScrollview;
    String firstname;
    String lastname;
    String email;
    String username;
    String roleTitle;
    String credit;
    public ProfileFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        //Set ActionBar Title
        getActivity().setTitle(R.string.txt_my_profile);

        userProfileScrollview = (ScrollView) view.findViewById(R.id.userProfileScrollview);
        //Check internet connection start
        if (!Tools.isNetworkAvailable(getActivity())) {
            Snackbar snackbar = Snackbar.make(userProfileScrollview, R.string.txt_no_internet, Snackbar.LENGTH_LONG)
                    .setAction(R.string.txt_retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Refresh fragment
                            /*getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frmMain, new MainFragment())
                                    .commit();*/
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    });
            snackbar.setActionTextColor(getResources().getColor(R.color.colorYellow));
            snackbar.show();
        }

        //Get local variable from AppController
        firstname = ((AppController) getActivity().getApplication()).getUserFirstName();
        lastname = ((AppController) getActivity().getApplication()).getUserLastName();
        email = ((AppController) getActivity().getApplication()).getUserEmail();
        username = ((AppController) getActivity().getApplication()).getUserUserName();
        roleTitle = ((AppController) getActivity().getApplication()).getUserRoleTitle();
        credit = ((AppController) getActivity().getApplication()).getUserCredit();


        TextView userProfileFullname = (TextView) view.findViewById(R.id.tv_user_profile_fullname);
        userProfileFullname.setText(firstname + " " + lastname);

        TextView userProfileEmail = (TextView) view.findViewById(R.id.tv_user_profile_email);
        userProfileEmail.setText(email);

        TextView profileUsername = (TextView) view.findViewById(R.id.tv_profile_username);
        profileUsername.setText(getResources().getString(R.string.txt_username) + ": " + username);

        TextView userProfileRole = (TextView) view.findViewById(R.id.tv_profile_role);
        userProfileRole.setText(getResources().getString(R.string.txt_profile_role) + ": " + roleTitle);

        TextView profileCredit = (TextView) view.findViewById(R.id.tv_profile_credit);
        profileCredit.setText(getResources().getString(R.string.txt_credit) + ": " + credit + " " + getResources().getString(R.string.txt_currency));

        TextView userProfileWebLoginBTN = (TextView) view.findViewById(R.id.tv_profile_web_login_btn);

        userProfileWebLoginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WEB_LOGIN_URL));
                startActivity(browserIntent);
            }
        });


        return view;
    }

}
