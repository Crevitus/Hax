package com.crevitus.hax;

import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;

public class CollusionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collusion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText editDataToSend = (EditText)findViewById(R.id.editDataToSend);

        Button btnCovertShare = (Button)findViewById(R.id.btnCovertShare);
        btnCovertShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Collusion().execute(editDataToSend.getText().toString());
                // Check if no view has focus:
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
    }

    private class Collusion extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String binaryToSend = "";
            try {
                byte[] messageToSend = params[0].getBytes("UTF-8");
                for (byte b : messageToSend)
                {
                    int val = b;
                    for (int i = 0; i < 8; i++)
                    {
                        binaryToSend+= ((val & 128) == 0 ? 0 : 1);
                        val <<= 1;
                    }
                }
                Log.e("sent", "" + binaryToSend);
            }
            catch(UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            int previousBit = AudioManager.RINGER_MODE_NORMAL;
            //send initiation sequence
            AudioManager audioManager =
                    (AudioManager) getSystemService(AUDIO_SERVICE);
            if(audioManager.getRingerMode() ==
                    AudioManager.RINGER_MODE_SILENT)
            {
                sendData(AudioManager.RINGER_MODE_VIBRATE, false);
                sendData(AudioManager.RINGER_MODE_SILENT, false);
                sendData(AudioManager.RINGER_MODE_VIBRATE, false);

            }
            else if(audioManager.getRingerMode() ==
                    AudioManager.RINGER_MODE_VIBRATE)
            {
                sendData(AudioManager.RINGER_MODE_SILENT, false);
                sendData(AudioManager.RINGER_MODE_VIBRATE, false);
                sendData(AudioManager.RINGER_MODE_SILENT, false);

            }
            else if(audioManager.getRingerMode() ==
                    AudioManager.RINGER_MODE_NORMAL)
            {
                sendData(AudioManager.RINGER_MODE_VIBRATE, false);
                sendData(AudioManager.RINGER_MODE_SILENT, false);
                sendData(AudioManager.RINGER_MODE_VIBRATE, false);

            }

            //transmit data
            for(char abit : binaryToSend.toCharArray())
            {
                    sendData((abit - '0'), true);
                    previousBit = abit - '0';
            }

            if(previousBit == AudioManager.RINGER_MODE_SILENT) {
                //send hand off procedure
                sendData(AudioManager.RINGER_MODE_VIBRATE, false);
                sendData(AudioManager.RINGER_MODE_SILENT, false);
                sendData(AudioManager.RINGER_MODE_VIBRATE, false);
            }
            else if(previousBit == AudioManager.RINGER_MODE_VIBRATE){
                //send hand off procedure
                sendData(AudioManager.RINGER_MODE_SILENT, false);
                sendData(AudioManager.RINGER_MODE_VIBRATE, false);
                sendData(AudioManager.RINGER_MODE_SILENT, false);
            }
            return "Executed";
        }
        
        private void sendData(int dataBit, boolean controlNeeded) {
            AudioManager audioManager = (AudioManager)
                    getSystemService(AUDIO_SERVICE);
            try
            {
                if (controlNeeded) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
                Thread.sleep(30);
                audioManager.setRingerMode(dataBit);
                Thread.sleep(30);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
