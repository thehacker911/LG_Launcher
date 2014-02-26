package com.android.settings.users;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.settings.Utils;

public class ProfileUpdateReceiver extends BroadcastReceiver
{
  static void copyProfileName(Context paramContext)
  {
    SharedPreferences localSharedPreferences = paramContext.getSharedPreferences("profile", 0);
    if (localSharedPreferences.contains("name_copied_once"));
    int i;
    UserManager localUserManager;
    String str;
    do
    {
      return;
      i = UserHandle.myUserId();
      localUserManager = (UserManager)paramContext.getSystemService("user");
      str = Utils.getMeProfileName(paramContext, false);
    }
    while ((str == null) || (str.length() <= 0));
    localUserManager.setUserName(i, str);
    localSharedPreferences.edit().putBoolean("name_copied_once", true).commit();
  }

  public void onReceive(final Context paramContext, Intent paramIntent)
  {
    new Thread()
    {
      public void run()
      {
        Utils.copyMeProfilePhoto(paramContext, null);
        ProfileUpdateReceiver.copyProfileName(paramContext);
      }
    }
    .start();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.users.ProfileUpdateReceiver
 * JD-Core Version:    0.6.2
 */