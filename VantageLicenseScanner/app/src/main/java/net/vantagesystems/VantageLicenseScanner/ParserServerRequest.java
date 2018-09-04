package net.vantagesystems.VantageLicenseScanner;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

public class ParserServerRequest
{
    private URL ServerAddress;
    private String Data;

    public ParserServerRequest ()
    {
    }

    public URL GetServerURL()
    {
        return ServerAddress;
    }

    public String GetData()
    {
        return Data;
    }

    public boolean SetURL(String urlRep)
    {
        boolean ret = false;

        try
        {
            ServerAddress = new URL(urlRep);
            ret = true;
        }
        catch(MalformedURLException mure)
        {
            DebugLogger.LogException(mure);
        }

        return ret;
    }

    public void SetData(String param)
    {
        Data = param;
    }
}
