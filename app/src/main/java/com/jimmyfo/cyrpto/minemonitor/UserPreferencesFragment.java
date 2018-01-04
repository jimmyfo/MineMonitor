package com.jimmyfo.cyrpto.minemonitor;

import android.content.Context;
import android.os.Bundle;
import android.app.DialogFragment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserPreferencesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserPreferencesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserPreferencesFragment extends DialogFragment {
    private static final String userStatsParameter = "param1";
    private static final String siteStatsParameter = "param2";
    private static final String userAddressParameter = "param3";

    private String userAddress;
    private String siteStatsURL;
    private String userStatsURL;

    View rootView;

    private OnFragmentInteractionListener mListener;

    public UserPreferencesFragment() {
        // Required empty public constructor
    }

    public static UserPreferencesFragment newInstance(String userURL, String siteURL, String address) {
        UserPreferencesFragment fragment = new UserPreferencesFragment();
        Bundle args = new Bundle();

        args.putString(userStatsParameter, userURL);
        args.putString(siteStatsParameter, siteURL);
        args.putString(userAddressParameter, address);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            mListener = (UserPreferencesFragment.OnFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userStatsURL= getArguments().getString(userStatsParameter);
            siteStatsURL= getArguments().getString(siteStatsParameter);
            userAddress = getArguments().getString(userAddressParameter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Hide the header/hide the title
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_user_preferences, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((Button)rootView.findViewById(R.id.saveAddressButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onFragmentInteraction(((EditText)rootView.findViewById(R.id.userStatsText)).getText().toString(),
                        ((EditText)rootView.findViewById(R.id.siteStatsText)).getText().toString(),
                        ((EditText)rootView.findViewById(R.id.userAddressText)).getText().toString());

                dismiss();
            }
        });

        ((EditText)rootView.findViewById(R.id.userStatsText)).setText(userStatsURL);
        ((EditText)rootView.findViewById(R.id.siteStatsText)).setText(siteStatsURL);
        ((EditText)rootView.findViewById(R.id.userAddressText)).setText(userAddress);
    }

    public void onButtonPressed(String userURL, String siteURL, String address) {
        if (mListener != null) {
            mListener.onFragmentInteraction(userURL, siteURL, address);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String userURL, String siteURL, String address);
    }
}
