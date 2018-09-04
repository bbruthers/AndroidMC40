package net.vantagesystems.VantageLicenseScanner;


import android.util.Log;

public class DebugLogger
{
    public static void LogException(Exception ex)
    {
        Log.getStackTraceString(ex);
    }

    public static void LogErrorMessage(String message)
    {
        Log.d("AppError", message);
    }
}
