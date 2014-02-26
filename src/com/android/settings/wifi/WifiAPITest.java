package com.android.settings.wifi;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.text.Editable;
import android.widget.EditText;

public class WifiAPITest extends PreferenceActivity
  implements Preference.OnPreferenceClickListener
{
  private Preference mWifiDisableNetwork;
  private Preference mWifiDisconnect;
  private Preference mWifiEnableNetwork;
  private WifiManager mWifiManager;
  private int netid;

  private void onCreatePreferences()
  {
    addPreferencesFromResource(2130968733);
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    this.mWifiDisconnect = localPreferenceScreen.findPreference("disconnect");
    this.mWifiDisconnect.setOnPreferenceClickListener(this);
    this.mWifiDisableNetwork = localPreferenceScreen.findPreference("disable_network");
    this.mWifiDisableNetwork.setOnPreferenceClickListener(this);
    this.mWifiEnableNetwork = localPreferenceScreen.findPreference("enable_network");
    this.mWifiEnableNetwork.setOnPreferenceClickListener(this);
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    onCreatePreferences();
    this.mWifiManager = ((WifiManager)getSystemService("wifi"));
  }

  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (paramPreference == this.mWifiDisconnect)
      this.mWifiManager.disconnect();
    while (true)
    {
      return true;
      if (paramPreference == this.mWifiDisableNetwork)
      {
        AlertDialog.Builder localBuilder1 = new AlertDialog.Builder(this);
        localBuilder1.setTitle("Input");
        localBuilder1.setMessage("Enter Network ID");
        final EditText localEditText1 = new EditText(this);
        localBuilder1.setView(localEditText1);
        localBuilder1.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            Editable localEditable = localEditText1.getText();
            WifiAPITest.access$002(WifiAPITest.this, Integer.parseInt(localEditable.toString()));
            WifiAPITest.this.mWifiManager.disableNetwork(WifiAPITest.this.netid);
          }
        });
        localBuilder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
          }
        });
        localBuilder1.show();
      }
      else if (paramPreference == this.mWifiEnableNetwork)
      {
        AlertDialog.Builder localBuilder2 = new AlertDialog.Builder(this);
        localBuilder2.setTitle("Input");
        localBuilder2.setMessage("Enter Network ID");
        final EditText localEditText2 = new EditText(this);
        localBuilder2.setView(localEditText2);
        localBuilder2.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            Editable localEditable = localEditText2.getText();
            WifiAPITest.access$002(WifiAPITest.this, Integer.parseInt(localEditable.toString()));
            WifiAPITest.this.mWifiManager.enableNetwork(WifiAPITest.this.netid, false);
          }
        });
        localBuilder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
          }
        });
        localBuilder2.show();
      }
    }
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
    return false;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.wifi.WifiAPITest
 * JD-Core Version:    0.6.2
 */