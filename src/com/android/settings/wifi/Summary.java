package com.android.settings.wifi;

import android.content.Context;
import android.content.res.Resources;
import android.net.NetworkInfo.DetailedState;

class Summary
{
  static String get(Context paramContext, NetworkInfo.DetailedState paramDetailedState)
  {
    return get(paramContext, null, paramDetailedState);
  }

  static String get(Context paramContext, String paramString, NetworkInfo.DetailedState paramDetailedState)
  {
    Resources localResources = paramContext.getResources();
    if (paramString == null);
    String[] arrayOfString;
    int j;
    for (int i = 2131165200; ; i = 2131165201)
    {
      arrayOfString = localResources.getStringArray(i);
      j = paramDetailedState.ordinal();
      if ((j < arrayOfString.length) && (arrayOfString[j].length() != 0))
        break;
      return null;
    }
    return String.format(arrayOfString[j], new Object[] { paramString });
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.wifi.Summary
 * JD-Core Version:    0.6.2
 */