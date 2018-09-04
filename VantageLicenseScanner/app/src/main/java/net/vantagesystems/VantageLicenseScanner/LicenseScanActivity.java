package net.vantagesystems.VantageLicenseScanner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.joda.time.*; //joda-time-2.9.3.jar
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class LicenseScanActivity extends Activity
{
	// laser&msr begin
	// Tag used for logging errors
	private static final String TAG = LicenseScanActivity.class.getSimpleName();

	// Let's define some intent strings
	// This intent string contains the source of the data as a string
	private static final String SOURCE_TAG = "com.motorolasolutions.emdk.datawedge.source";
	// This intent string contains the barcode symbology as a string
	private static final String LABEL_TYPE_TAG = "com.motorolasolutions.emdk.datawedge.label_type";
	// This intent string contains the barcode data as a byte array list
	private static final String DECODE_DATA_TAG = "com.motorolasolutions.emdk.datawedge.decode_data";

	// This intent string contains the captured data as a string
	// (in the case of MSR this data string contains a concatenation of the
	// track data)
	private static final String DATA_STRING_TAG = "com.motorolasolutions.emdk.datawedge.data_string";

	public final static String EXTRA_MESSAGE = "com.motorolasolutions.emdk.sample.dwdemosample.MESSAGE";

	// Let's define the API intent strings for the soft scan trigger
	private static final String ACTION_SOFTSCANTRIGGER = "com.motorolasolutions.emdk.datawedge.api.ACTION_SOFTSCANTRIGGER";
	private static final String EXTRA_PARAM = "com.motorolasolutions.emdk.datawedge.api.EXTRA_PARAMETER";
	private static final String DWAPI_START_SCANNING = "START_SCANNING";
	private static final String DWAPI_STOP_SCANNING = "STOP_SCANNING";
	private static final String DWAPI_TOGGLE_SCANNING = "TOGGLE_SCANNING";
	private static String ourIntentAction = "";
	// laser&msr end

	ImageView imApprove, imDeny;
	TextView tvNameResult;
	EditText etName, etBirth, etAge, etExpDate;
	CheckBox cbUnderage, cbExpired, cbBanned;

	Button bDetails;
	Intent dlIntent;
	SharedPreferences appPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tourn_assign_players);

		appPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		dlIntent = new Intent(getApplicationContext(),dlDetailsActivity.class);
		getWindow().getDecorView().clearFocus();

		ourIntentAction = getString(R.string.intentAction);

		initializeUIObjects();
	}

	private void initializeUIObjects()
	{
		imApprove = (ImageView)findViewById(R.id.imViewCheck);
		imDeny = (ImageView)findViewById(R.id.imViewXMark);
		tvNameResult = (TextView)findViewById(R.id.tvNameResp);
		imApprove.setVisibility(View.INVISIBLE);
		imDeny.setVisibility(View.INVISIBLE);

		etName = (EditText)findViewById(R.id.etName);
		etAge = (EditText)findViewById(R.id.etAge);
		etBirth = (EditText)findViewById(R.id.etBirth);
		etExpDate = (EditText)findViewById(R.id.etExpir);

		cbUnderage = (CheckBox)findViewById(R.id.cbUnderage);
		cbBanned = (CheckBox)findViewById(R.id.cbBanned);
		cbExpired = (CheckBox)findViewById(R.id.cbExpired);

		bDetails = (Button)findViewById(R.id.bDetails);
		bDetails.setVisibility(View.INVISIBLE);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.preferences:
			{
				Intent intent = new Intent();
				intent.setClassName(this, "net.vantagesystems.VantageLicenseScanner.AppPreferenceActivity");
				startActivity(intent);

				return true;
			}
		}

		return super.onOptionsItemSelected(item);
	}

	public void TransistToDetailsActivity(View view)
	{
		startActivity(dlIntent);
	}

	// We need to handle any incoming intents, so let override the onNewIntent
	// method
	@Override
	public void onNewIntent(Intent i)
	{
		handleDecodeData(i);
	}

	// This function is responsible for getting the data from the intent
	private void handleDecodeData(Intent i)
	{
		ResetScan(); //reset UI fields,

		if (i.getAction().contentEquals(ourIntentAction))
		{
			// define a string that will hold our output
			String out = "";
			// get the source of the data
			String source = i.getStringExtra(SOURCE_TAG);
			// save it to use later
			if (source == null)
				source = "scanner";

			// get the data from the intent
			String data = i.getStringExtra(DATA_STRING_TAG);
			// let's define a variable for the data length
			Integer data_len = 0;
			// and set it to the length of the data
			if (data != null)
				data_len = data.length();

			// check if the data has come from the barcode scanner
			if (source.equalsIgnoreCase("scanner"))
			{
				// check if there is anything in the data
				if (data != null && data.length() > 0)
				{
					// we have some data, so let's get it's symbology
					String sLabelType = i.getStringExtra(LABEL_TYPE_TAG);
					// check if the string is empty
					if (sLabelType != null && sLabelType.length() > 0)
					{
						// format of the label type string is
						// LABEL-TYPE-SYMBOLOGY
						// so let's skip the LABEL-TYPE- portion to get just the
						// symbology
						sLabelType = sLabelType.substring(11);
					}
					else
					{
						// the string was empty so let's set it to "Unknown"
						sLabelType = "Unknown";
					}
					// let's construct the beginning of our output string
					out = "";
				}
			}

			SpannableStringBuilder stringbuilder = new SpannableStringBuilder();

			// add the output string we constructed earlier
			stringbuilder.append(out);
			// now let's highlight our output string in bold type
			stringbuilder.setSpan(new StyleSpan(Typeface.BOLD), 0,
					stringbuilder.length(),
					SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
			stringbuilder.append(data);

			String strDL = stringbuilder.toString();

			if (source.equalsIgnoreCase("scanner"))
			{
				strDL = strDL.replace("\r", "|r|");
				strDL = strDL.replace("\n", "|n|");
				strDL = strDL.replace("\u001E", "#");

				ParserServerRequest psr = new ParserServerRequest();
				ParserWebTask pwt = new ParserWebTask(this);

				if(psr.SetURL("http://" + appPrefs.getString("lpsURL","63.142.245.33") + "/VantageMobile/GetDLElementsPOST"))
				{
					psr.SetData(strDL);
					pwt.execute(psr);
				}
				else
				{
					SendNotificationPopup("Please check your server configuration in app settings");
				}
			}
		}
	}

	//Ensure there's no residual data during various states
	@Override
	public void onResume()
	{
		super.onResume();
		ResetScan();
	}

	@Override
	public void onStop()
	{
		super.onStop();
		ResetScan();
	}

	@Override
    public void onPause()
    {
        super.onPause();
        ResetScan();
    }

	public void ResetScan()
	{
		etName.setText("");
		etAge.setText("");
		etBirth.setText("");
		etExpDate.setText("");

		imApprove.setVisibility(View.INVISIBLE);
		imDeny.setVisibility(View.INVISIBLE);
		bDetails.setVisibility(View.INVISIBLE);

		cbUnderage.setChecked(false);
		cbExpired.setChecked(false);
		cbBanned.setChecked(false);
	}

	public void SetUIResult(Person p)
	{
		etName.setText(p.GetFirstName() + " " + p.GetFamName());
		etAge.setText(p.GetAge().toString());
		etBirth.setText(p.GetBirthdate().toString());
		etExpDate.setText(p.GetExpiredate().toString());

		if(!p.isLegalAge())
			cbUnderage.setChecked(true);
		if(p.isLicenseExpired())
			cbExpired.setChecked(true);
		if(p.isBanned())
			cbBanned.setChecked(true);

		if(cbBanned.isChecked() || cbUnderage.isChecked() || cbExpired.isChecked())
			imDeny.setVisibility(View.VISIBLE);
		else
			imApprove.setVisibility(View.VISIBLE);

		//bundle Person object values to be transported to details Activity
		dlIntent.putExtra("firstName", p.GetFirstName());
		dlIntent.putExtra("famName", p.GetFamName());
		dlIntent.putExtra("address", p.GetAddress());
		dlIntent.putExtra("city", p.GetCity());
		dlIntent.putExtra("post", p.GetPostCode());
		dlIntent.putExtra("birthDate", p.GetBirthdate());
		dlIntent.putExtra("expDate", p.GetExpiredate());
		dlIntent.putExtra("licenseNumber", p.GetLicenseNumber());
		dlIntent.putExtra("SEPNum", p.GetSEP());

		bDetails.setVisibility(View.VISIBLE);
	}

	public void SendNotificationPopup(String Message)
	{
		Toast.makeText(getApplicationContext(), Message, Toast.LENGTH_SHORT).show();
	}
}