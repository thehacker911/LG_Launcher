package com.android.settings.wifi;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import com.android.settings.ButtonBarHandler;

public class WifiSetupActivity extends WifiPickerActivity
  implements ButtonBarHandler
{
  protected void onApplyThemeResource(Resources.Theme paramTheme, int paramInt, boolean paramBoolean)
  {
    String str = getIntent().getStringExtra("theme");
    if ((str != null) && (str.equalsIgnoreCase("holo_light")))
      paramInt = getResources().getIdentifier("SetupWizardWifiTheme.Light", "style", getPackageName());
    super.onApplyThemeResource(paramTheme, paramInt, paramBoolean);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.wifi.WifiSetupActivity
 * JD-Core Version:    0.6.2
 */