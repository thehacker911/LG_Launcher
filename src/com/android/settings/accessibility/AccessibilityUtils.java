package com.android.settings.accessibility;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.provider.Settings.Secure;
import android.text.TextUtils.SimpleStringSplitter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

class AccessibilityUtils
{
  static Set<ComponentName> getEnabledServicesFromSettings(Context paramContext)
  {
    String str = Settings.Secure.getString(paramContext.getContentResolver(), "enabled_accessibility_services");
    Object localObject;
    if (str == null)
      localObject = Collections.emptySet();
    while (true)
    {
      return localObject;
      localObject = new HashSet();
      TextUtils.SimpleStringSplitter localSimpleStringSplitter = AccessibilitySettings.sStringColonSplitter;
      localSimpleStringSplitter.setString(str);
      while (localSimpleStringSplitter.hasNext())
      {
        ComponentName localComponentName = ComponentName.unflattenFromString(localSimpleStringSplitter.next());
        if (localComponentName != null)
          ((Set)localObject).add(localComponentName);
      }
    }
  }

  static CharSequence getTextForLocale(Context paramContext, Locale paramLocale, int paramInt)
  {
    Resources localResources = paramContext.getResources();
    Configuration localConfiguration = localResources.getConfiguration();
    Locale localLocale = localConfiguration.locale;
    try
    {
      localConfiguration.locale = paramLocale;
      localResources.updateConfiguration(localConfiguration, null);
      CharSequence localCharSequence = localResources.getText(paramInt);
      return localCharSequence;
    }
    finally
    {
      localConfiguration.locale = localLocale;
      localResources.updateConfiguration(localConfiguration, null);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accessibility.AccessibilityUtils
 * JD-Core Version:    0.6.2
 */