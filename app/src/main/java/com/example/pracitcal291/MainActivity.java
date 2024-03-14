package com.example.pracitcal291;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int RECEIVE_SMS_PERMISSION_REQUEST_CODE = 1;
    private static final int SEND_SMS_PERMISSION_REQUEST_CODE = 2;
    private EditText txtPhoneNum, txtMessage;
    private Button btnSendSMS;
    private TextView tvReceivedSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtPhoneNum = findViewById(R.id.txtPhoneNum);
        txtMessage = findViewById(R.id.txtMessage);
        btnSendSMS = findViewById(R.id.btnSendSMS);
        tvReceivedSMS = findViewById(R.id.tvReceivedSMS);

        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS();
            }
        });

        // Register the SMS receiver
        registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

        // Request SMS permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_SMS}, RECEIVE_SMS_PERMISSION_REQUEST_CODE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==RECEIVE_SMS_PERMISSION_REQUEST_CODE || requestCode==SEND_SMS_PERMISSION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_LONG).show();
            }
        }

    }

    // Send SMS method
    private void sendSMS() {
        String phoneNo = txtPhoneNum.getText().toString();
        String msg = txtMessage.getText().toString();

        if (!phoneNo.isEmpty() && !msg.isEmpty()) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("tel:"+ phoneNo, null, msg, null, null);
            Toast.makeText(this, "SMS sent successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enter phone number and Message", Toast.LENGTH_SHORT).show();
        }
    }

    // SMS receiver
    private final BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");

                if (pdus != null && pdus.length > 0) {
                    // Get the first PDU (assuming there's at least one)
                    byte[] pdu = (byte[]) pdus[0];
                    String format = bundle.getString("format");

                    // Create a message from the PDU
                    SmsMessage message = SmsMessage.createFromPdu(pdu, format);

                    // Extract sender's phone number and message body
                    String sender = message.getOriginatingAddress();
                    String messageBody = message.getMessageBody();

                    // Display received SMS
                    String smsMessage =tvReceivedSMS.getText()+ "\nReceived SMS: " + messageBody + " from " + sender;
                    tvReceivedSMS.setText(smsMessage);
                }
            }
        }
    };
}