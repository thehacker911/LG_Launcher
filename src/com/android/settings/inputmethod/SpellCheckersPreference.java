package com.android.settings.inputmethod;

import android.content.Context;
import android.util.AttributeSet;
import android.view.textservice.TextServicesManager;

public class SpellCheckersPreference extends CheckBoxAndSettingsPreference
{
  private final TextServicesManager mTsm;

  public SpellCheckersPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mTsm = ((TextServicesManager)paramContext.getSystemService("textservices"));
    setChecked(this.mTsm.isSpellCheckerEnabled());
  }

  protected void onCheckBoxClicked()
  {
    super.onCheckBoxClicked();
    boolean bool = isChecked();
    this.mTsm.setSpellCheckerEnabled(bool);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.inputmethod.SpellCheckersPreference
 * JD-Core Version:    0.6.2
 */