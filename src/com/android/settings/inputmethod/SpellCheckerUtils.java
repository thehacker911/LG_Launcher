package com.android.settings.inputmethod;

import android.view.textservice.SpellCheckerInfo;
import android.view.textservice.TextServicesManager;

public class SpellCheckerUtils
{
  private static final String TAG = SpellCheckerUtils.class.getSimpleName();

  public static SpellCheckerInfo getCurrentSpellChecker(TextServicesManager paramTextServicesManager)
  {
    return paramTextServicesManager.getCurrentSpellChecker();
  }

  public static SpellCheckerInfo[] getEnabledSpellCheckers(TextServicesManager paramTextServicesManager)
  {
    return paramTextServicesManager.getEnabledSpellCheckers();
  }

  public static void setCurrentSpellChecker(TextServicesManager paramTextServicesManager, SpellCheckerInfo paramSpellCheckerInfo)
  {
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.inputmethod.SpellCheckerUtils
 * JD-Core Version:    0.6.2
 */