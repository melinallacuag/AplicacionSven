package com.anggastudio.sample;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;

public class NFCUtil {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFilters;
    private String[][] techLists;
    private Activity activity;

    public NFCUtil(Activity activity) {

        this.activity = activity;

        nfcAdapter = NfcAdapter.getDefaultAdapter(activity);

        if (nfcAdapter != null) {
            Intent intent = new Intent(activity, activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            IntentFilter tagIntentFilter = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
            intentFilters = new IntentFilter[]{tagIntentFilter};

            techLists = new String[][]{
                    new String[]{NfcA.class.getName(), NfcB.class.getName(),
                            NfcF.class.getName(), NfcV.class.getName(), IsoDep.class.getName(),
                            MifareClassic.class.getName(), MifareUltralight.class.getName(),
                            Ndef.class.getName()}
            };
        }

    }

    public void onResume() {
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(activity, pendingIntent, intentFilters, techLists);
        }
    }

    public void onPause() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(activity);
        }
    }

}
