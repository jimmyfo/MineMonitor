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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;


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
    private static final String notifyFoundParameter = "param4";
    private static final String notifyFoundIntervalParameter = "param5";
    private static final String notifyMaturedParameter = "param6";
    private static final String notifyMaturedIntervalParameter = "param7";
    private static final String notifyPaymentParameter = "param8";
    private static final String notifyPaymentIntervalParameter = "param9";

    private String mUserAddress;
    private String mSiteStatsURL;
    private String mUserStatsURL;

    private boolean mNotifyFound;
    private String mNotifyFoundInterval;
    private boolean mNotifyMatured;
    private String mNotifyMaturedInterval;
    private boolean mNotifyPayment;
    private String mNotifyPaymentInterval;

    View rootView;

    private OnFragmentInteractionListener mListener;

    public UserPreferencesFragment() {
        // Required empty public constructor
    }

    public static UserPreferencesFragment newInstance(String userStatsURL, String siteStatsURL, String userAddress,
                                                      boolean notifyFound, String notifyFoundInterval, boolean notifyMatured, String notifyMaturedInterval,
                                                      boolean notifyPayment, String notifyPaymentInterval) {
        UserPreferencesFragment fragment = new UserPreferencesFragment();
        Bundle args = new Bundle();

        args.putString(userStatsParameter, userStatsURL);
        args.putString(siteStatsParameter, siteStatsURL);
        args.putString(userAddressParameter, userAddress);

        args.putBoolean(notifyFoundParameter, notifyFound);
        args.putString(notifyFoundIntervalParameter, notifyFoundInterval);
        args.putBoolean(notifyMaturedParameter, notifyMatured);
        args.putString(notifyMaturedIntervalParameter, notifyMaturedInterval);
        args.putBoolean(notifyPaymentParameter, notifyPayment);
        args.putString(notifyPaymentIntervalParameter, notifyPaymentInterval);

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
            mUserStatsURL = getArguments().getString(userStatsParameter);
            mSiteStatsURL = getArguments().getString(siteStatsParameter);
            mUserAddress = getArguments().getString(userAddressParameter);

            mNotifyFound = getArguments().getBoolean(notifyFoundParameter);
            mNotifyFoundInterval = getArguments().getString(notifyFoundIntervalParameter);
            mNotifyMatured = getArguments().getBoolean(notifyMaturedParameter);
            mNotifyMaturedInterval = getArguments().getString(notifyMaturedIntervalParameter);
            mNotifyPayment = getArguments().getBoolean(notifyPaymentParameter);
            mNotifyPaymentInterval = getArguments().getString(notifyPaymentIntervalParameter);
        }
    }

    private void BindSpinners(int id, String selected){
        Spinner staticSpinner = (Spinner)rootView.findViewById(id);

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(rootView.getContext(), R.array.notification_periods,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);

        if (staticAdapter.getPosition(selected) > -1) {
            ((Spinner) rootView.findViewById(id)).setSelection(staticAdapter.getPosition(selected));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Hide the header/hide the title
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_user_preferences, container, false);

        BindSpinners(R.id.notificationBlockFoundSpinner, mNotifyFoundInterval);
        BindSpinners(R.id.notificationBlockMaturedSpinner, mNotifyMaturedInterval);
        BindSpinners(R.id.notificationPaymentReceivedSpinner, mNotifyPaymentInterval);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((Button)rootView.findViewById(R.id.saveAddressButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onFragmentInteraction(
                        ((EditText)rootView.findViewById(R.id.userStatsText)).getText().toString(),
                        ((EditText)rootView.findViewById(R.id.siteStatsText)).getText().toString(),
                        ((EditText)rootView.findViewById(R.id.userAddressText)).getText().toString(),
                        ((CheckBox)rootView.findViewById(R.id.notificationBlockFoundCheckBox)).isChecked(),
                        ((Spinner)rootView.findViewById(R.id.notificationBlockFoundSpinner)).getSelectedItem().toString(),
                        ((CheckBox)rootView.findViewById(R.id.notificationBlockMaturedCheckBox)).isChecked(),
                        ((Spinner)rootView.findViewById(R.id.notificationBlockMaturedSpinner)).getSelectedItem().toString(),
                        ((CheckBox)rootView.findViewById(R.id.notificationPaymentReceivedCheckBox)).isChecked(),
                        ((Spinner)rootView.findViewById(R.id.notificationPaymentReceivedSpinner)).getSelectedItem().toString());

                dismiss();
            }
        });

        ((EditText)rootView.findViewById(R.id.userStatsText)).setText(mUserStatsURL);
        ((EditText)rootView.findViewById(R.id.siteStatsText)).setText(mSiteStatsURL);
        ((EditText)rootView.findViewById(R.id.userAddressText)).setText(mUserAddress);

        ((CheckBox)rootView.findViewById(R.id.notificationBlockFoundCheckBox)).setChecked(mNotifyFound);
        ((CheckBox)rootView.findViewById(R.id.notificationBlockMaturedCheckBox)).setChecked(mNotifyMatured);
        ((CheckBox)rootView.findViewById(R.id.notificationPaymentReceivedCheckBox)).setChecked(mNotifyPayment);
    }

    public void onButtonPressed(String userStatsURL, String siteStatsURL, String userAddress,
                                boolean notifyFound, String notifyFoundInterval, boolean notifyMatured, String notifyMaturedInterval,
                                boolean notifyPayment, String notifyPaymentInterval) {
        if (mListener != null) {
            mListener.onFragmentInteraction(userStatsURL, siteStatsURL, userAddress, notifyFound, notifyFoundInterval, notifyMatured, notifyMaturedInterval, notifyPayment, notifyPaymentInterval);
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
        void onFragmentInteraction(String userStatsURL, String siteStatsURL, String userAddress,
                                   boolean notifyFound, String notifyFoundInterval, boolean notifyMatured, String notifyMaturedInterval,
                                   boolean notifyPayment, String notifyPaymentInterval);
    }
}
