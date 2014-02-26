package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.net.Uri.Builder;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import java.util.Locale;

public class HelpUtils
{
  private static final String TAG = HelpUtils.class.getName();
  private static String sCachedVersionCode = null;

  public static boolean prepareHelpMenuItem(Context paramContext, MenuItem paramMenuItem, String paramString)
  {
    if (TextUtils.isEmpty(paramString))
    {
      paramMenuItem.setVisible(false);
      return false;
    }
    Intent localIntent = new Intent("android.intent.action.VIEW", uriWithAddedParameters(paramContext, Uri.parse(paramString)));
    localIntent.setFlags(276824064);
    if (localIntent.resolveActivity(paramContext.getPackageManager()) != null)
    {
      paramMenuItem.setIntent(localIntent);
      paramMenuItem.setShowAsAction(0);
      paramMenuItem.setVisible(true);
      return true;
    }
    paramMenuItem.setVisible(false);
    return false;
  }

  public static Uri uriWithAddedParameters(Context paramContext, Uri paramUri)
  {
    Uri.Builder localBuilder = paramUri.buildUpon();
    localBuilder.appendQueryParameter("hl", Locale.getDefault().toString());
    if (sCachedVersionCode == null);
    while (true)
    {
      try
      {
        sCachedVersionCode = Integer.toString(paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0).versionCode);
        localBuilder.appendQueryParameter("version", sCachedVersionCode);
        return localBuilder.build();
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        Log.wtf(TAG, "Invalid package name for context", localNameNotFoundException);
        continue;
      }
      localBuilder.appendQueryParameter("version", sCachedVersionCode);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.HelpUtils
 * JD-Core Version:    0.6.2
 */