package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TestingSettingsBroadcastReceiver extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if (paramIntent.getAction().equals("android.provider.Telephony.SECRET_CODE"))
    {
      Intent localIntent = new Intent("android.intent.action.MAIN");
      localIntent.setClass(paramContext, TestingSettings.class);
      localIntent.setFlags(268435456);
      paramContext.startActivity(localIntent);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.TestingSettingsBroadcastReceiver
 * JD-Core Version:    0.6.2
 */