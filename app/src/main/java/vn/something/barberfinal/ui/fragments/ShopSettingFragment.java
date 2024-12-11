package vn.something.barberfinal.ui.fragments;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import vn.something.barberfinal.FacebookLoginActivity;
import vn.something.barberfinal.MainActivity;
import vn.something.barberfinal.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShopSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopSettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShopSettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShopSettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShopSettingFragment newInstance(String param1, String param2) {
        ShopSettingFragment fragment = new ShopSettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_shop_setting, container, false);
        // Inflate the layout for this fragment
        Button logoutButton = view.findViewById(R.id.fb_logout_btn);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutHandling();
            }
        });
        return view;
    }
    private void logOutHandling(){
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), FacebookLoginActivity.class);
        startActivity(intent);
    }
}