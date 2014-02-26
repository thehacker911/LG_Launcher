package com.android.settings.applications;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

class InterestingConfigChanges
{
  final Configuration mLastConfiguration = new Configuration();
  int mLastDensity;

  boolean applyNewConfig(Resources paramResources)
  {
    int i = this.mLastConfiguration.updateFrom(paramResources.getConfiguration());
    if (this.mLastDensity != paramResources.getDisplayMetrics().densityDpi);
    for (int j = 1; ; j = 0)
    {
      boolean bool;
      if (j == 0)
      {
        int k = i & 0x304;
        bool = false;
        if (k == 0);
      }
      else
      {
        this.mLastDensity = paramResources.getDisplayMetrics().densityDpi;
        bool = true;
      }
      return bool;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.applications.InterestingConfigChanges
 * JD-Core Version:    0.6.2
 */