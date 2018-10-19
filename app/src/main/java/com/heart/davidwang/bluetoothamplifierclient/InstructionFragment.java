package com.heart.davidwang.bluetoothamplifierclient;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
public class InstructionFragment extends Fragment {
    public InstructionFragment() {
        // Required empty public constructor
    }
    public static InstructionFragment newInstance(){
        InstructionFragment fragment = new InstructionFragment();
        return  fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_instruction, container, false);
    }

}
