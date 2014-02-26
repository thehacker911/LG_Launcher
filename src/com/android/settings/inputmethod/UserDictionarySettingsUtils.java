package com.android.settings.inputmethod;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import com.android.settings.Utils;
import java.util.Locale;

public class UserDictionarySettingsUtils
{
  public static String getLocaleDisplayName(Context paramContext, String paramString)
  {
    if (TextUtils.isEmpty(paramString))
      return paramContext.getResources().getString(2131428545);
    return Utils.createLocaleFromString(paramString).getDisplayName(paramContext.getResources().getConfiguration().locale);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.inputmethod.UserDictionarySettingsUtils
 * JD-Core Version:    0.6.2
 */