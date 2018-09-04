package net.vantagesystems.VantageLicenseScanner;

import android.os.AsyncTask;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class ParserWebTask extends AsyncTask<ParserServerRequest, Void, ServerResponse>
{

    /* https://stackoverflow.com/a/25647882 */

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param strings The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */

    private WeakReference<LicenseScanActivity> mAcvitity;

    public ParserWebTask(LicenseScanActivity lsaActivity)
    {
        mAcvitity = new WeakReference<LicenseScanActivity>(lsaActivity);
    }

    @Override
    protected void onPreExecute()
    {

    }

    @Override
    protected ServerResponse doInBackground(ParserServerRequest... params)
    {
        String message = "";
        String strServerRespContent = "";
        ServerResponse sp = new ServerResponse();
        URL serverURL;
        HttpURLConnection connection;

        try
        {
            serverURL = params[0].GetServerURL();
            message = params[0].GetData();

            connection = (HttpURLConnection) serverURL.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(message.getBytes().length);
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            //connect to service, get response
            connection.connect();

            //setup msg send
            OutputStream os = new BufferedOutputStream(connection.getOutputStream());
            os.write(message.getBytes());
            os.flush();

            //read resp
            InputStream is = connection.getInputStream();
            strServerRespContent = ConvertISToString(is, 750);

            //add to ServerResponse obj
            sp.SetData(strServerRespContent);
            sp.SetResponseStatus(ServerTaskResultStatus.Success);
        }
        catch(Exception ex)
        {
            sp.SetResponseStatus(ServerTaskResultStatus.Failure);
        }

        return sp;
    }

    @Override
    protected void onPostExecute(ServerResponse resp)
    {
        LicenseScanActivity lsa = mAcvitity.get();

        if(resp.getServerResponseStatus() == ServerTaskResultStatus.Success)
        {
            Person p = PersonParser.ParsePersonInformation(resp.getResponseData());

            if(p.GetParseResult() == PersonInformationParseResult.ParseSuccess)
            {
                lsa.SetUIResult(p);
            }
            else
            {
                lsa.SendNotificationPopup("There was an issue with reading the license, please try again.");
            }
        }
        else
        {
            lsa.SendNotificationPopup("Issue connecting to server, please check your settings of contact your administrator");
        }
    }

    // Reads an InputStream and converts its contents to a String.
    private String ConvertISToString(InputStream stream, int len) throws IOException
    {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

}
