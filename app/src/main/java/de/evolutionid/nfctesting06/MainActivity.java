package de.evolutionid.nfctesting06;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    NfcAdapter nfcAdapter;
    EditText edtWriteToTag;
    TextView txtTagContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Initialize the NFC component
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        //Initialize GUI Elements
        edtWriteToTag = (EditText) findViewById(R.id.edtWriteToTag);
        txtTagContent = (TextView) findViewById(R.id.txtTagContent);

        //Some more GUI functionality


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Gets called when app is active (in foreground).
    @Override
    protected void onResume() {
        super.onResume();
        enableForegroundDispatchSystem();
    }

    //Gets called when focus is lost (e.g. user returns to the home screen).
    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatchSystem();
    }

    /**
     * Sets the app to an intent-listening 'mode', so all of the tag discoveries get dispatched to it.
     */
    private void enableForegroundDispatchSystem() {
        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    /**
     * Stops the app from listening for NFC intents.
     * This saves battery and CPU usage.
     */
    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //If intent is a NFC tag
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            //Feedback for the user on Tag discovery
            Toast.makeText(this, "Tag discovered", Toast.LENGTH_SHORT).show();

            //get the message
            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            //if there are messages
            if (parcelables != null && parcelables.length > 0) {
                readTextFromMessage((NdefMessage) parcelables[0]);
            } else {
                //Feedback if there are no messages
                edtWriteToTag.setText("[No Text on tag!]");
            }
        }
    }

    /**
     * Reads all the Records in a NdefMessage and prints them out to a TextView.
     *
     * @param ndefMessage the message to be read
     */
    private void readTextFromMessage(NdefMessage ndefMessage) {
        //Read out all the Records contained in the ndefMessage
        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        //If there are records
        if (ndefRecords != null && ndefRecords.length > 0) {
            NdefRecord ndefRecord = ndefRecords[0];
            String tagContent = getTextFromNdefRecord(ndefRecord);
            txtTagContent.setText(tagContent);
        } else {
            //Feedback if there are no records
            txtTagContent.setText("[No records on Tag]");
        }
    }

    /**
     * Reads one NdefRecord and returns it.
     *
     * @param ndefRecord the record to be read
     * @return the text of a record as a String
     */
    private String getTextFromNdefRecord(NdefRecord ndefRecord) {
        byte[] payload = ndefRecord.getPayload();

        String textEncoding;
        if ((payload[0] & 128) == 0) {
            textEncoding = "UTF-8";
        } else {
            textEncoding = "UTF-16";
        }

        //There is a warning here. I have not tried any solutions, since the code works perfectly fine.
        int languageCodeLength = payload[0] & 0063;

        try {
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(this, "Error: getTextFromNdefRecord()", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return null;
    }


}
