package net.vantagesystems.VantageLicenseScanner;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class AppPreferenceFrag extends PreferenceFragment
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_preference);
    }
}
