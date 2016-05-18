package com.heart.davidwang.bluetoothamplifierclient;
import android.app.Notification;
import android.app.PendingIntent;
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
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectAndStartServiceFragment extends Fragment {
    public ConnectAndStartServiceFragment() {
        // Required empty public constructor
    }
    public static ConnectAndStartServiceFragment newInstance() {
        ConnectAndStartServiceFragment fragment = new ConnectAndStartServiceFragment();
        return fragment;
    }
    private ConnectAndStartServiceFragment myself;
    SharedPreferences sharedPreferences;
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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myself = this;
        View view = inflater.inflate(R.layout.fragment_connect_and_start_service, container, false);
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
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        btAdapter = BluetoothAdapter.getDefaultAdapter();
                        if(btAdapter==null){
                            // TODO: 2016/5/15 Bluetooth not support (later)
                        }else{
                            if(btAdapter.isEnabled()){
                                try {
                                    // TODO: 2016/5/15 address is not a valid Bluetooth address (now)
                                    serverAddress = "00:1A:7D:DA:71:0D";
                                    BluetoothDevice device = btAdapter.getRemoteDevice(serverAddress);
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
        return view;
    }
    // TODO: 2016/5/15 if mac address is not correct format show hint, diable start button. the word in client and server must be consistent. (later)
    // TODO: 2016/5/15 start use service (how to stop? use trial and error method, and I can tell others) (right now!)
    // TODO: 2016/5/15 Image (later)
    // TODO: 2016/5/15 show the possible reason if no sound (much later)
    // TODO: 2016/5/15 show how to fix the noise sound, run too many programs on android device? (much later)
    // TODO: 2016/5/15 upload jar to git website. (much later)
    // TODO: 2016/5/15 console output on server side, service output on android (much later)
}
