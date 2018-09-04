package net.vantagesystems.VantageLicenseScanner;

import android.preference.PreferenceActivity;

import java.util.List;

public class AppPreferenceActivity extends PreferenceActivity
{
    @Override
    public void onBuildHeaders(List<android.preference.PreferenceActivity.Header> target)
    {
        loadHeadersFromResource(R.xml.headers_preference, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName)
    {
        return AppPreferenceFrag.class.getName().equals(fragmentName);
    }
}