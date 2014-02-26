package com.android.settings.accessibility;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.preference.ListPreference;
import android.util.AttributeSet;
import java.text.Collator;
import java.util.Arrays;
import java.util.Locale;

public class LocalePreference extends ListPreference
{
  public LocalePreference(Context paramContext)
  {
    super(paramContext);
    init(paramContext);
  }

  public LocalePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramContext);
  }

  private static String getDisplayName(Locale paramLocale, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    String str = paramLocale.toString();
    for (int i = 0; i < paramArrayOfString1.length; i++)
      if (paramArrayOfString1[i].equals(str))
        return paramArrayOfString2[i];
    return paramLocale.getDisplayName(paramLocale);
  }

  public void init(Context paramContext)
  {
    String[] arrayOfString1 = Resources.getSystem().getAssets().getLocales();
    Arrays.sort(arrayOfString1);
    Resources localResources = paramContext.getResources();
    String[] arrayOfString2 = localResources.getStringArray(17235975);
    String[] arrayOfString3 = localResources.getStringArray(17235976);
    int i = arrayOfString1.length;
    LocaleInfo[] arrayOfLocaleInfo = new LocaleInfo[i];
    int j = 0;
    int k = 0;
    if (j < i)
    {
      String str1 = arrayOfString1[j];
      int n;
      if (str1.length() != 5)
        n = k;
      Locale localLocale;
      while (true)
      {
        j++;
        k = n;
        break;
        String str2 = str1.substring(0, 2);
        localLocale = new Locale(str2, str1.substring(3, 5));
        if (k == 0)
        {
          n = k + 1;
          LocaleInfo localLocaleInfo5 = new LocaleInfo(localLocale.getDisplayLanguage(localLocale), localLocale);
          arrayOfLocaleInfo[k] = localLocaleInfo5;
        }
        else
        {
          LocaleInfo localLocaleInfo2 = arrayOfLocaleInfo[(k - 1)];
          if ((!localLocaleInfo2.locale.getLanguage().equals(str2)) || (localLocaleInfo2.locale.getLanguage().equals("zz")))
            break label253;
          localLocaleInfo2.label = getDisplayName(arrayOfLocaleInfo[(k - 1)].locale, arrayOfString2, arrayOfString3);
          n = k + 1;
          LocaleInfo localLocaleInfo4 = new LocaleInfo(getDisplayName(localLocale, arrayOfString2, arrayOfString3), localLocale);
          arrayOfLocaleInfo[k] = localLocaleInfo4;
        }
      }
      label253: String str3;
      if (str1.equals("zz_ZZ"))
        str3 = "[Developer] Accented English";
      while (true)
      {
        n = k + 1;
        LocaleInfo localLocaleInfo3 = new LocaleInfo(str3, localLocale);
        arrayOfLocaleInfo[k] = localLocaleInfo3;
        break;
        if (str1.equals("zz_ZY"))
          str3 = "[Developer] Fake Bi-Directional";
        else
          str3 = localLocale.getDisplayLanguage(localLocale);
      }
    }
    CharSequence[] arrayOfCharSequence1 = new CharSequence[k + 1];
    CharSequence[] arrayOfCharSequence2 = new CharSequence[k + 1];
    Arrays.sort(arrayOfLocaleInfo, 0, k);
    arrayOfCharSequence1[0] = localResources.getString(2131428664);
    arrayOfCharSequence2[0] = "";
    for (int m = 0; m < k; m++)
    {
      LocaleInfo localLocaleInfo1 = arrayOfLocaleInfo[m];
      arrayOfCharSequence1[(m + 1)] = localLocaleInfo1.toString();
      arrayOfCharSequence2[(m + 1)] = localLocaleInfo1.locale.toString();
    }
    setEntries(arrayOfCharSequence1);
    setEntryValues(arrayOfCharSequence2);
  }

  private static class LocaleInfo
    implements Comparable<LocaleInfo>
  {
    private static final Collator sCollator = Collator.getInstance();
    public String label;
    public Locale locale;

    public LocaleInfo(String paramString, Locale paramLocale)
    {
      this.label = paramString;
      this.locale = paramLocale;
    }

    public int compareTo(LocaleInfo paramLocaleInfo)
    {
      return sCollator.compare(this.label, paramLocaleInfo.label);
    }

    public String toString()
    {
      return this.label;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accessibility.LocalePreference
 * JD-Core Version:    0.6.2
 */