package com.heart.davidwang.bluetoothamplifierclient;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.plus.PlusOneButton;
public class PairDeviceAndStartServerFragment extends Fragment {
    public PairDeviceAndStartServerFragment() {
        // Required empty public constructor
    }
    public static PairDeviceAndStartServerFragment newInstance() {
        PairDeviceAndStartServerFragment fragment = new PairDeviceAndStartServerFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pair_device_and_start_server, container, false);
        return view;
    }
}
