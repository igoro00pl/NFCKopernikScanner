package com.example.nfckopernikscanner;

import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.tech.NfcF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.widget.Toast;

//ToDo: Miasta w ktorych mieszkal kopernik: Torun, Krakow, Padwa, Olsztyn, Lidzbark Warminski, Frombork

public class MainActivity extends AppCompatActivity {
    //BT stuff
    private MainViewModel viewModel;

    //nfc stuff
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;

    //debugging stuff
    public byte[] kartaMiejska = {91, 66, 64, 99, 54, 101, 101, 48, 51, 57};
    private String TAG = "NFCKS";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        // Create a generic PendingIntent that will be deliver to this activity. The NFC stack
        // will fill in the intent with the details of the discovered tag before delivering to
        // this activity.
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // Setup an intent filter for all MIME based dispatches
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        mFilters = new IntentFilter[] {
                ndef,
        };
        // Setup a tech list for all NfcF tags
        mTechLists = new String[][] { new String[] { NfcF.class.getName() } };


        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // This method return false if there is an error, so if it does, we should close.
        if (!viewModel.setupViewModel(getIntent().getStringExtra("device_name"), getIntent().getStringExtra("device_mac"))) {
            finish();
            return;
        }
        //sendData("loading");
        Log.d(TAG, "LOADING");
        viewModel.connect();

    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "resumed");

        if (mAdapter != null) mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                mTechLists);

    }

    @Override
    public void onNewIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                byte[] tagId = tag.getId();
                if (tagId == kartaMiejska) {
                    toast("dziala!");
                } else {
                    toast(tagId.toString());
                    sendData("torun");
                    move("torun");
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) mAdapter.disableForegroundDispatch(this);
    }

    public void btnClicked(View view) {
        sendData("torun");
        move("torun");
    }

    public void sendData(String data) {
        viewModel.sendMessage(data + "\n");
    }

    public void move(String city) {
        Intent myIntent;
        Log.d(TAG, "Going to " + city);
        if(city.equals("torun")){
            myIntent = new Intent(this, cTorun.class);
            this.startActivity(myIntent);
        }
        else if(city.equals("krakow")){
            myIntent = new Intent(this, cKrakow.class);
            this.startActivity(myIntent);
        }
        else if(city.equals("padwa")){
            myIntent = new Intent(this, cPadwa.class);
            this.startActivity(myIntent);
        }
        else if(city.equals("olsztyn")){
            myIntent = new Intent(this, cOlsztyn.class);
            this.startActivity(myIntent);
        }
        else if(city.equals("lidzbark")){
            myIntent = new Intent(this, cLidzbark.class);
            this.startActivity(myIntent);
        }
        else if(city.equals("frombork")){
            myIntent = new Intent(this, cFrombork.class);
            this.startActivity(myIntent);
        }
        else {
            Log.d(TAG, city + " is not correct city");
        }
    }

    private void toast(String messageResource) { Toast.makeText(getApplication(), messageResource, Toast.LENGTH_LONG).show(); }
}



