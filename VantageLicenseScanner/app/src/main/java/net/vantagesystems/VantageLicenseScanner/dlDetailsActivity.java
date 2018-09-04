package net.vantagesystems.VantageLicenseScanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import org.xml.sax.Parser;

public class dlDetailsActivity extends Activity
{
    Bundle scannedExtras;
    EditText etName, etAddressOne, etCity, etBirthdate, etExpir, etLicense;
    Intent intent;
    String strSepNo = "";
    Long lSepNumber = 0l;
    ImageView ivBanned;
    SharedPreferences connectPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dl_details);

        getWindow().getDecorView().clearFocus();

        scannedExtras = getIntent().getExtras();
        etName = (EditText)findViewById(R.id.etName);
        etAddressOne = (EditText)findViewById(R.id.etAddressOne);
        etCity = (EditText)findViewById(R.id.etCity);
        etBirthdate = (EditText)findViewById(R.id.etBirthdate);
        etExpir = (EditText)findViewById(R.id.etExpir);
        etLicense = (EditText)findViewById(R.id.etLicense);
        ivBanned = (ImageView)findViewById(R.id.ivBanned);
        intent = getIntent();
        scannedExtras = intent.getExtras();

        getWindow().getDecorView().clearFocus();

        connectPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        PopulateFields();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        ClearFields();
    }

    public void PopulateFields()
    {
        String name = scannedExtras.getString("firstName");
        name = name + " " + scannedExtras.getString("famName");
        String addressOne = scannedExtras.getString("address");
        String addressSpecific = scannedExtras.get("city") + "," + scannedExtras.getString("postCode");
        String birthdate = scannedExtras.getString("birthDate");
        String expirDate = scannedExtras.getString("expDate");
        String license = scannedExtras.getString("licenseNumber");
        lSepNumber = scannedExtras.getLong("SEPNum");
        strSepNo = Long.toString(lSepNumber);

        etName.setText(name);
        etAddressOne.setText(addressOne);
        etBirthdate.setText(birthdate);
        etExpir.setText(expirDate);
        etLicense.setText(license);
        etCity.setText(addressSpecific);

        if(lSepNumber != 0)
        {
            Toast.makeText(getApplicationContext(),"Attempting to download banned patron's image.",Toast.LENGTH_SHORT).show();

            ParserServerRequest psr = new ParserServerRequest();
            if(psr.SetURL("http://" + connectPrefs.getString("lpsURL","63.142.245.33") + "/VantageMobile/DownloadBanImagePOST"))
            {
                psr.SetData(strSepNo);

                WebImageDownloader widr = new WebImageDownloader();
                widr.execute(psr);
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Cannot connect to server, please contact your administrator.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void ClearFields()
    {
        etName.setText("");
        etAddressOne.setText("");
        etCity.setText("");
        etExpir.setText("");
        etLicense.setText("");
        strSepNo = "";
        ivBanned.setImageBitmap(null);
    }

    private class WebImageDownloader extends AsyncTask<ParserServerRequest, Integer, Bitmap>
    {
        Bitmap imageBan = null;
        String message = "";

        StringWriter sw = new StringWriter();

        @Override
        protected Bitmap doInBackground(ParserServerRequest... params)
        {
            try
            {

                URL url = params[0].GetServerURL();
                message = params[0].GetData();

                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setReadTimeout(10000 /* milliseconds */);
                connection.setConnectTimeout(15000 /* milliseconds */);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setFixedLengthStreamingMode(message.getBytes().length);
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");// ???

                connection.connect();

                OutputStream os = new BufferedOutputStream(
                        connection.getOutputStream());

                os.write(message.getBytes());
                // clean up
                os.flush();

                InputStream is = connection.getInputStream();
                IOUtils.copy(is,sw,"UTF-8");
                String encodedImageString = sw.toString();

                byte[] baDecodedString = Base64.decode(encodedImageString,Base64.NO_WRAP); //emit line terminators, they obscure the image data

                try
                {
                    imageBan = BitmapFactory.decodeByteArray(baDecodedString,0,baDecodedString.length);
                }
                catch(Exception imageException)
                {
                    DebugLogger.LogException(imageException);
                }
            }
            catch(Exception ex)
            {
                imageBan = null;
            }

            return imageBan;
        }

        @Override
        protected void onPostExecute(Bitmap image)
        {
            if (!(image == null))
            {
                int maxHeight = 2000;
                int maxWidth = 2000;

                float scale = Math.min(((float)maxHeight / image.getWidth()),((float)maxWidth / image.getHeight()));

                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);

                image = Bitmap.createBitmap(image,0,0,image.getWidth(),image.getHeight(),matrix,true);

                ivBanned.setImageBitmap(image);
                ivBanned.setScaleType(ImageView.ScaleType.FIT_XY);
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Banned patron's image not found.",Toast.LENGTH_SHORT).show();
            }
        }

    }
}