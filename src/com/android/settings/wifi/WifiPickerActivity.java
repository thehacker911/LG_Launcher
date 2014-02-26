package com.android.settings.wifi;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.Button;
import com.android.settings.ButtonBarHandler;
import com.android.settings.wifi.p2p.WifiP2pSettings;

public class WifiPickerActivity extends PreferenceActivity
  implements ButtonBarHandler
{
  public Intent getIntent()
  {
    Intent localIntent = new Intent(super.getIntent());
    if (!localIntent.hasExtra(":android:show_fragment"))
      localIntent.putExtra(":android:show_fragment", WifiSettings.class.getName());
    localIntent.putExtra(":android:no_headers", true);
    return localIntent;
  }

  public Button getNextButton()
  {
    return super.getNextButton();
  }

  public boolean hasNextButton()
  {
    return super.hasNextButton();
  }

  protected boolean isValidFragment(String paramString)
  {
    return (WifiSettings.class.getName().equals(paramString)) || (WifiP2pSettings.class.getName().equals(paramString)) || (AdvancedWifiSettings.class.getName().equals(paramString));
  }

  public void startWithFragment(String paramString, Bundle paramBundle, Fragment paramFragment, int paramInt)
  {
    Intent localIntent1 = new Intent("android.intent.action.MAIN");
    localIntent1.setClass(this, getClass());
    localIntent1.putExtra(":android:show_fragment", paramString);
    localIntent1.putExtra(":android:show_fragment_args", paramBundle);
    localIntent1.putExtra(":android:no_headers", true);
    Intent localIntent2 = getIntent();
    if (localIntent2.hasExtra("extra_prefs_show_button_bar"))
      localIntent1.putExtra("extra_prefs_show_button_bar", localIntent2.getBooleanExtra("extra_prefs_show_button_bar", false));
    if (localIntent2.hasExtra("extra_prefs_set_next_text"))
      localIntent1.putExtra("extra_prefs_set_next_text", localIntent2.getStringExtra("extra_prefs_set_next_text"));
    if (localIntent2.hasExtra("extra_prefs_set_back_text"))
      localIntent1.putExtra("extra_prefs_set_back_text", localIntent2.getStringExtra("extra_prefs_set_back_text"));
    if (localIntent2.hasExtra("wifi_show_action_bar"))
      localIntent1.putExtra("wifi_show_action_bar", localIntent2.getBooleanExtra("wifi_show_action_bar", true));
    if (localIntent2.hasExtra("wifi_show_menus"))
      localIntent1.putExtra("wifi_show_menus", localIntent2.getBooleanExtra("wifi_show_menus", true));
    if (paramFragment == null)
    {
      startActivity(localIntent1);
      return;
    }
    paramFragment.startActivityForResult(localIntent1, paramInt);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.wifi.WifiPickerActivity
 * JD-Core Version:    0.6.2
 */