package com.heart.davidwang.bluetoothamplifierclient;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class AndroidDeviceSetupFragment extends Fragment {
    public AndroidDeviceSetupFragment() {
        // Required empty public constructor
    }
    public static AndroidDeviceSetupFragment newInstance() {
        AndroidDeviceSetupFragment fragment = new AndroidDeviceSetupFragment();
        return fragment;
    }
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String key;
    private String[] stringArray;
    //MAC Address
    private EditText editText_serverAddress;
    private Button button_serverAddressOK;
    //Signed
    private Button button_signed;
    //Bits Per Sample
    private Button button_bitsPerSample;
    //Channel Type
    private Button button_channelType;
    //Bytes Per Frame
    private TextView textView_bytesPerFrame;
    //Endian
    private Button button_endian;
    //Sample Rate
    private TextView textView_sampleRate;

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_android_device_setup, container, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        editor = sharedPreferences.edit();

        //MAC Address EditText and Button
        button_serverAddressOK = (Button)view.findViewById(R.id.button_serverAddressOK);
        button_serverAddressOK.setVisibility(View.INVISIBLE);
        editText_serverAddress = (EditText)view.findViewById(R.id.editText_serverAddress);
        editText_serverAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==12){
                    button_serverAddressOK.setEnabled(true);
                    button_serverAddressOK.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        key = getString(R.string.key_serverAddress);
        String previousServerAddress = sharedPreferences.getString(key, "");
        editText_serverAddress.setText(previousServerAddress);
        button_serverAddressOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key = getString(R.string.key_serverAddress);
                editor.putString(key, editText_serverAddress.getText().toString());
                editor.commit();
                button_serverAddressOK.setEnabled(false);
                button_serverAddressOK.setVisibility(View.INVISIBLE);
                // TODO: 2016/5/16 show a snack bar to show that the address been saved (later)
                // TODO: 2016/5/16 auto hide keyboard (later)
            }
        });

        //Signed Button
        // TODO: 2016/5/15 save to shared preference once not focused, but check, snack bar? (later)
        button_signed = (Button)view.findViewById(R.id.button_signed);
        button_signed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringArray = getResources().getStringArray(R.array.signedArray);
                key = getString(R.string.key_signed_index);
                int signed_index = sharedPreferences.getInt(key, 0);
                int next_index = (signed_index + 1)%(stringArray.length);
                button_signed.setText(stringArray[next_index]);
                editor.putInt(key, next_index);
                editor.commit();
            }
        });
        // TODO: 2016/5/18 why setting return default after I restart? (now)
        for(int i=0;i< (sharedPreferences.getInt(  getString(R.string.key_signed_index)  , 0)) ;i++){
            button_signed.performClick();
        }

        //Bits Per Sample Spinner
        button_bitsPerSample = (Button)view.findViewById(R.id.button_bitsPerSample);
        button_bitsPerSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringArray = getResources().getStringArray(R.array.bitsPerSampleArray);
                key = getString(R.string.key_bitsPerSample_index);
                int bitsPerSample_index = sharedPreferences.getInt(key, 0);
                int next_index = (bitsPerSample_index + 1)%(stringArray.length);
                button_bitsPerSample.setText(stringArray[next_index]);
                editor.putInt(key, next_index);
                editor.commit();
                updateBytesPerFrameTextView(view);
            }
        });
        for(int i=0;i< (sharedPreferences.getInt(  getString(R.string.key_bitsPerSample_index)  , 0)) ;i++){
            button_bitsPerSample.performClick();
        }

        //Channel Type Button
        button_channelType = (Button)view.findViewById(R.id.button_channelType);
        button_channelType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringArray = getResources().getStringArray(R.array.channelTypeArray);
                key = getString(R.string.key_channelType_index);
                int channelType_index = sharedPreferences.getInt(key, 0);
                int next_index = (channelType_index + 1)%(stringArray.length);
                button_channelType.setText(stringArray[next_index]);
                editor.putInt(key, next_index);
                editor.commit();
                updateBytesPerFrameTextView(view);
            }
        });
        for(int i=0;i< (sharedPreferences.getInt( getString(R.string.key_channelType_index), 0 )) ;i++){
            button_channelType.performClick();
        }

        //Bytes Per Frame TextView
        updateBytesPerFrameTextView(view);

        //Endian Button
        button_endian = (Button)view.findViewById(R.id.button_endian);
        button_endian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringArray = getResources().getStringArray(R.array.endianArray);
                key = getString(R.string.key_endian_index);
                int endian_index = sharedPreferences.getInt(key, 0);
                int next_index = (endian_index + 1)%(stringArray.length);
                button_endian.setText(stringArray[next_index]);
                editor.putInt(key, next_index);
                editor.commit();
            }
        });
        for(int i=0;i< (sharedPreferences.getInt( getString(R.string.key_endian_index), 0 )) ;i++){
            button_endian.performClick();
        }

        //Sample Rate TextView
        textView_sampleRate = (TextView) view.findViewById(R.id.textView_sampleRate);
        textView_sampleRate.setText( getString(R.string.sampleRateArrayFirstElement));
        return view;
    }
    private void updateBytesPerFrameTextView(View view){
        textView_bytesPerFrame = (TextView)view.findViewById(R.id.textView_bytesPerFrame);
        int bitsPerSample_index = sharedPreferences.getInt( getString(R.string.key_bitsPerSample_index), 0 );
        int bitsPerSample = Integer.parseInt(  getResources().getStringArray(R.array.bitsPerSampleArray)[bitsPerSample_index]  );
        int channelType_index = sharedPreferences.getInt( getString(R.string.key_channelType_index), 0 );
        String channelType = getResources().getStringArray(R.array.channelTypeArray)[channelType_index];
        int channels;
        if( channelType.equals("mono")){
            // TODO: 2016/5/16 may have more channels, not just mono or stereo. (much later)
            channels = 1;
        }else{
            channels = 2;
        }
        textView_bytesPerFrame.setText(bitsPerSample*channels/8+"");//Since Bytes Per Frame = bitsPerSample * channels, calculate it.
        // TODO: 2016/5/16 minWidth (later)
    }
}
