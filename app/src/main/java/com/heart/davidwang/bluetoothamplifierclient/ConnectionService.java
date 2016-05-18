package com.heart.davidwang.bluetoothamplifierclient;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class ConnectionService extends Service {
    //For Connection
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private DataInputStream dataInputStream;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //For Audio Streaming
    private String serverAddress = null;
    private AudioTrack audioTrack = null;
    private int bufferSize;
    private byte[] buffer;
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        bufferSize = intent.getIntExtra(getString(R.string.key_bufferSize), 1024);
        String json = intent.getStringExtra(getString(R.string.key_audioTrack));
        Gson gson = new Gson();
        audioTrack = gson.fromJson(json, new TypeToken<AudioTrack>(){}.getType());
        serverAddress = intent.getStringExtra(getString(R.string.key_serverAddress));
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter==null){
            // TODO: 2016/5/15 Bluetooth not support (later)
        }else{
            if(btAdapter.isEnabled()){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // TODO: 2016/5/15 put work here (now)
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
                            stopSelf();
                        }
                    }).start();
            }else{
                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                //Intent enableBTIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
                //startActivity(enableBTIntent);
                //original code;
                // TODO: 2016/5/15 hint user to open bluetooth (later)
            }
        }
        // TODO: 2016/5/15 try start not sticky for now, change to sticky and try to kill the service (later)

        // TODO: 2016/5/15  is notification necessary? (now)
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{intent}, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Bluetooth Amplifier")
                .setContentText("Streaming Audio...")
                .setContentIntent(pendingIntent).build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        startForeground(1, notification);
        return Service.START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
