package com.android.settings.print;

import android.content.ComponentName;
import android.content.Context;
import android.provider.Settings.Secure;
import android.text.TextUtils.SimpleStringSplitter;
import java.util.ArrayList;
import java.util.List;

public class SettingsUtils
{
  public static List<ComponentName> readEnabledPrintServices(Context paramContext)
  {
    ArrayList localArrayList = new ArrayList();
    String str = Settings.Secure.getString(paramContext.getContentResolver(), "enabled_print_services");
    if (str == null);
    while (true)
    {
      return localArrayList;
      TextUtils.SimpleStringSplitter localSimpleStringSplitter = new TextUtils.SimpleStringSplitter(':');
      localSimpleStringSplitter.setString(str);
      while (localSimpleStringSplitter.hasNext())
        localArrayList.add(ComponentName.unflattenFromString(localSimpleStringSplitter.next()));
    }
  }

  public static void writeEnabledPrintServices(Context paramContext, List<ComponentName> paramList)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = paramList.size();
    for (int j = 0; j < i; j++)
    {
      ComponentName localComponentName = (ComponentName)paramList.get(j);
      if (localStringBuilder.length() > 0)
        localStringBuilder.append(':');
      localStringBuilder.append(localComponentName.flattenToString());
    }
    Settings.Secure.putString(paramContext.getContentResolver(), "enabled_print_services", localStringBuilder.toString());
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.print.SettingsUtils
 * JD-Core Version:    0.6.2
 */