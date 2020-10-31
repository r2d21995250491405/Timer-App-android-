package com.example.timer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener /* Следить за всеми изменениями в sharedPreference */,
        Preference.OnPreferenceChangeListener /* Следить за конкретным изменением */ {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.timer_pref);
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount();

        for (int i = 0; i < count; i++) {
            Preference preference = preferenceScreen.getPreference(i);
            if (!(preference instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferencelabel(preference, value);

            }
        }
        Preference preference = findPreference("default_interval");
        preference.setOnPreferenceChangeListener(this);
    }

    private void setPreferencelabel(Preference pref, String value) {
        if (pref instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) pref;
            int index = listPreference.findIndexOfValue(value);
            if (index >= 0) {
                listPreference.setSummary(listPreference.getEntries()[index]);
            }

        } else if (pref instanceof EditTextPreference) {
            pref.setSummary(value);

        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (!(preference instanceof CheckBoxPreference)) {
            String value = sharedPreferences.getString(preference.getKey(), "");
            setPreferencelabel(preference, value);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        Toast toast = Toast.makeText(getContext(), "Please enter an integer number", Toast.LENGTH_LONG);

        if (preference.getKey().equals("default_interval")) {
            String defaultIntervalString = (String) newValue;
            try {
                int defaultInterval = Integer.parseInt(defaultIntervalString);
            } catch (NumberFormatException e) {
                toast.show();
                return false;
            }
        }
        return true;
    }
}
