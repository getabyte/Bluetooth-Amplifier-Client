package com.heart.davidwang.bluetoothamplifierclient;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
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

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionFragment extends Fragment {
    public ConnectionFragment() {
        // Required empty public constructor
    }
    public static ConnectionFragment newInstance() {
        ConnectionFragment fragment = new ConnectionFragment();
        return fragment;
    }
    /*************************Setup Part*************************/
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
        view = inflater.inflate(R.layout.fragment_connection, container, false);
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

        //Connection Button
        setupConnectionButton(view);
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

    /*************************Connection Part*************************/
    ConnectionFragment myself;
    //Construct buffer size, Audio Track (transform into gson), serverAddress, and put into intent
    int bufferSize;
    AudioTrack audioTrack;
    String serverAddress;
    String audioTrack_gson;
    //get necessary data from shared preference
    String signedString;
    String bitsPerSampleString;
    String channelTypeString;
    int bytesPerFrame;
    String endianString;
    String sampleRateString;

    private Button button_startAudioStreaming;
    private Intent intent;
    private void setupConnectionButton(View view){
        myself = this;
        button_startAudioStreaming = (Button)view.findViewById(R.id.button_startStreamingAudio);
        button_startAudioStreaming.setOnClickListener(new View.OnClickListener() {
            //For Connection
            BluetoothAdapter btAdapter;
            BluetoothSocket btSocket;
            DataInputStream dataInputStream;
            UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            //For Audio Streaming
            String serverAddress = null;
            AudioTrack audioTrack = null;
            int bufferSize;
            byte[] buffer;
            @Override
            public void onClick(View v) {
                button_serverAddressOK.setEnabled(false);
                button_signed.setEnabled(false);
                button_bitsPerSample.setEnabled(false);
                button_channelType.setEnabled(false);
                button_endian.setEnabled(false);
                button_startAudioStreaming.setEnabled(false);
                /*
                button_startAudioStreaming.setEnabled(false);
                intent.putExtra( getString(R.string.key_bufferSize), bufferSize);
                Gson gson = new Gson();
                Type type = new TypeToken<AudioTrack>(){}.getType();
                audioTrack_gson = gson.toJson(audioTrack, type);
                intent.putExtra( getString(R.string.key_audioTrack), audioTrack_gson);
                intent.putExtra( getString(R.string.key_serverAddress), serverAddress);
                myself.getContext().startService(intent);
*/

                //give it a try
                // TODO: 2016/5/17 change these to service (now)
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(myself.getActivity());
                //Signed
                int signedIndex = sharedPreferences.getInt( getString(R.string.key_signed_index), 0);
                signedString = getResources().getStringArray(R.array.signedArray)[signedIndex];
                //Bits Per Sample
                int bitsPerSampleIndex = sharedPreferences.getInt( getString(R.string.key_bitsPerSample_index), 0);
                bitsPerSampleString = getResources().getStringArray(R.array.bitsPerSampleArray)[bitsPerSampleIndex];
                //Channel Type
                int channelTypeIndex = sharedPreferences.getInt( getString(R.string.key_channelType_index), 0);
                channelTypeString = getResources().getStringArray(R.array.channelTypeArray)[channelTypeIndex];
                //Bytes Per Frame
                //later
                //Endian
                int endianIndex = sharedPreferences.getInt( getString(R.string.key_endian_index), 0);
                endianString = getResources().getStringArray(R.array.endianArray)[endianIndex];
                //SampleRate
                int sampleRateIndex = sharedPreferences.getInt( getString(R.string.key_sampleRate_index), 0);
                sampleRateString = getResources().getStringArray(R.array.sampleRateArray)[sampleRateIndex];

                //Construct bufferSize, AudioTrack, serverAddress
                int streamType = AudioManager.STREAM_MUSIC;
                int sampleRate = Integer.parseInt(sampleRateString);
                int channelConfig;
                if(channelTypeString.equals("mono")){// TODO: 2016/5/16 there may be more in the future (much later)
                    channelConfig = AudioFormat.CHANNEL_OUT_MONO;
                }else if(channelTypeString.equals("stereo")){
                    channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
                }else{
                    channelConfig = AudioFormat.CHANNEL_OUT_MONO;
                }
                final int audioFormat;
                if(bitsPerSampleString.equals("8")){
                    audioFormat = AudioFormat.ENCODING_PCM_8BIT;
                }else if(bitsPerSampleString.equals("16")){
                    audioFormat = AudioFormat.ENCODING_PCM_16BIT;
                }else{
                    audioFormat = AudioFormat.ENCODING_PCM_8BIT;
                }
                bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);
                audioTrack = new AudioTrack(streamType, sampleRate, channelConfig, audioFormat, bufferSize, AudioTrack.MODE_STREAM);
                serverAddress = sharedPreferences.getString( getString(R.string.key_serverAddress), "");
                // TODO: 2016/5/16 start foreground (now)
                // TODO: 2016/5/20 how to stop properly? (now)
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        btAdapter = BluetoothAdapter.getDefaultAdapter();
                        if(btAdapter==null){
                            // TODO: 2016/5/15 Show "Bluetooth not support" message. (later)
                        }else{
                            if(btAdapter.isEnabled()){
                                try {
                                    String serverAddressWithColon = getServerAddressWithColon(serverAddress);
                                    BluetoothDevice device = btAdapter.getRemoteDevice(serverAddressWithColon);
                                    btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                                    btAdapter.cancelDiscovery();
                                    btSocket.connect();
                                    dataInputStream = new DataInputStream(btSocket.getInputStream());
                                    buffer = new byte[bufferSize];
                                    audioTrack.play();

                                    while(true){
                                        dataInputStream.read(buffer, 0, bufferSize);
                                        audioTrack.write(buffer, 0, buffer.length);
                                    }
                                } catch (IOException e) {
                                    try {
                                        btSocket.close();
                                        button_serverAddressOK.setEnabled(true);
                                        button_signed.setEnabled(true);
                                        button_bitsPerSample.setEnabled(true);
                                        button_channelType.setEnabled(true);
                                        button_endian.setEnabled(true);
                                        button_startAudioStreaming.setEnabled(false);                                        button_startAudioStreaming.setEnabled(true);
                                        // TODO: 2016/5/15 where to close connection? (much later)
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }else{
                                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                                //Intent enableBTIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
                                //startActivity(enableBTIntent);
                                //original code;
                                // TODO: 2016/5/15 hint user to open bluetooth (later)
                            }
                        }
                    }
                });

                //give it a try
            }
        });
    }
    private String getServerAddressWithColon(String serverAddress){
        String serverAddressWithColon = "";
        for(int i=0;i<serverAddress.length();i++){
            if( (i>0)&&(i%2==0) ){
                serverAddressWithColon+=":";
            }
            serverAddressWithColon+=serverAddress.charAt(i);
        }
        return serverAddressWithColon;
    }
    // TODO: 2016/5/15 if mac address is not correct format show hint, diable start button. the word in client and server must be consistent. (later)
    // TODO: 2016/5/15 start use service (how to stop? use trial and error method, and I can tell others) (right now!)
    // TODO: 2016/5/15 show the possible reason if no sound (much later)
    // TODO: 2016/5/15 show how to fix the noise sound, run too many programs on android device? (much later)
    // TODO: 2016/5/15 console output on server side, service output on android (much later)
}
