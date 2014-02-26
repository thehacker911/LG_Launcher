package com.android.settings.accessibility;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings.Secure;
import android.view.accessibility.CaptioningManager;
import android.view.accessibility.CaptioningManager.CaptionStyle;
import com.android.settings.SettingsPreferenceFragment;

public class CaptionPropertiesFragment extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, ListDialogPreference.OnValueChangedListener
{
  private ColorPreference mBackgroundColor;
  private ColorPreference mBackgroundOpacity;
  private CaptioningManager mCaptioningManager;
  private PreferenceCategory mCustom;
  private ColorPreference mEdgeColor;
  private EdgeTypePreference mEdgeType;
  private ListPreference mFontSize;
  private ColorPreference mForegroundColor;
  private LocalePreference mLocale;
  private ToggleCaptioningPreferenceFragment mParent;
  private PresetPreference mPreset;
  private boolean mShowingCustom;
  private ListPreference mTypeface;

  private void initializeAllPreferences()
  {
    this.mLocale = ((LocalePreference)findPreference("captioning_locale"));
    this.mFontSize = ((ListPreference)findPreference("captioning_font_size"));
    Resources localResources = getResources();
    int[] arrayOfInt1 = localResources.getIntArray(2131165251);
    String[] arrayOfString1 = localResources.getStringArray(2131165250);
    this.mPreset = ((PresetPreference)findPreference("captioning_preset"));
    this.mPreset.setValues(arrayOfInt1);
    this.mPreset.setTitles(arrayOfString1);
    this.mCustom = ((PreferenceCategory)findPreference("custom"));
    this.mShowingCustom = true;
    int[] arrayOfInt2 = localResources.getIntArray(2131165247);
    String[] arrayOfString2 = localResources.getStringArray(2131165246);
    this.mForegroundColor = ((ColorPreference)this.mCustom.findPreference("captioning_foreground_color"));
    this.mForegroundColor.setTitles(arrayOfString2);
    this.mForegroundColor.setValues(arrayOfInt2);
    this.mEdgeColor = ((ColorPreference)this.mCustom.findPreference("captioning_edge_color"));
    this.mEdgeColor.setTitles(arrayOfString2);
    this.mEdgeColor.setValues(arrayOfInt2);
    int[] arrayOfInt3 = new int[1 + arrayOfInt2.length];
    String[] arrayOfString3 = new String[1 + arrayOfString2.length];
    System.arraycopy(arrayOfInt2, 0, arrayOfInt3, 1, arrayOfInt2.length);
    System.arraycopy(arrayOfString2, 0, arrayOfString3, 1, arrayOfString2.length);
    arrayOfInt3[0] = 0;
    arrayOfString3[0] = getString(2131428665);
    this.mBackgroundColor = ((ColorPreference)this.mCustom.findPreference("captioning_background_color"));
    this.mBackgroundColor.setTitles(arrayOfString3);
    this.mBackgroundColor.setValues(arrayOfInt3);
    int[] arrayOfInt4 = localResources.getIntArray(2131165249);
    String[] arrayOfString4 = localResources.getStringArray(2131165248);
    this.mBackgroundOpacity = ((ColorPreference)this.mCustom.findPreference("captioning_background_opacity"));
    this.mBackgroundOpacity.setTitles(arrayOfString4);
    this.mBackgroundOpacity.setValues(arrayOfInt4);
    this.mEdgeType = ((EdgeTypePreference)this.mCustom.findPreference("captioning_edge_type"));
    this.mTypeface = ((ListPreference)this.mCustom.findPreference("captioning_typeface"));
  }

  private void installUpdateListeners()
  {
    this.mPreset.setOnValueChangedListener(this);
    this.mForegroundColor.setOnValueChangedListener(this);
    this.mEdgeColor.setOnValueChangedListener(this);
    this.mBackgroundColor.setOnValueChangedListener(this);
    this.mBackgroundOpacity.setOnValueChangedListener(this);
    this.mEdgeType.setOnValueChangedListener(this);
    this.mTypeface.setOnPreferenceChangeListener(this);
    this.mFontSize.setOnPreferenceChangeListener(this);
    this.mLocale.setOnPreferenceChangeListener(this);
  }

  private void refreshPreviewText()
  {
    if (this.mParent != null)
      this.mParent.refreshPreviewText();
  }

  private void refreshShowingCustom()
  {
    int i;
    if (this.mPreset.getValue() == -1)
    {
      i = 1;
      if ((i != 0) || (!this.mShowingCustom))
        break label47;
      getPreferenceScreen().removePreference(this.mCustom);
      this.mShowingCustom = false;
    }
    label47: 
    while ((i == 0) || (this.mShowingCustom))
    {
      return;
      i = 0;
      break;
    }
    getPreferenceScreen().addPreference(this.mCustom);
    this.mShowingCustom = true;
  }

  private void updateAllPreferences()
  {
    int i = this.mCaptioningManager.getRawUserStyle();
    this.mPreset.setValue(i);
    float f = this.mCaptioningManager.getFontScale();
    this.mFontSize.setValue(Float.toString(f));
    CaptioningManager.CaptionStyle localCaptionStyle = CaptioningManager.CaptionStyle.getCustomStyle(getContentResolver());
    this.mForegroundColor.setValue(localCaptionStyle.foregroundColor);
    this.mEdgeType.setValue(localCaptionStyle.edgeType);
    this.mEdgeColor.setValue(localCaptionStyle.edgeColor);
    int j = localCaptionStyle.backgroundColor;
    int k;
    if (Color.alpha(j) == 0)
      k = 0;
    for (int m = (j & 0xFF) << 24; ; m = j & 0xFF000000)
    {
      this.mBackgroundColor.setValue(k);
      this.mBackgroundOpacity.setValue(0xFFFFFF | m);
      String str1 = localCaptionStyle.mRawTypeface;
      ListPreference localListPreference = this.mTypeface;
      if (str1 == null)
        str1 = "";
      localListPreference.setValue(str1);
      String str2 = this.mCaptioningManager.getRawLocale();
      LocalePreference localLocalePreference = this.mLocale;
      if (str2 == null)
        str2 = "";
      localLocalePreference.setValue(str2);
      return;
      k = j | 0xFF000000;
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mCaptioningManager = ((CaptioningManager)getSystemService("captioning"));
    addPreferencesFromResource(2131034122);
    initializeAllPreferences();
    updateAllPreferences();
    refreshShowingCustom();
    installUpdateListeners();
  }

  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (this.mTypeface == paramPreference)
      Settings.Secure.putString(localContentResolver, "accessibility_captioning_typeface", (String)paramObject);
    while (true)
    {
      refreshPreviewText();
      return true;
      if (this.mFontSize == paramPreference)
        Settings.Secure.putFloat(localContentResolver, "accessibility_captioning_font_scale", Float.parseFloat((String)paramObject));
      else if (this.mLocale == paramPreference)
        Settings.Secure.putString(localContentResolver, "accessibility_captioning_locale", (String)paramObject);
    }
  }

  public void onValueChanged(ListDialogPreference paramListDialogPreference, int paramInt)
  {
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (this.mForegroundColor == paramListDialogPreference)
      Settings.Secure.putInt(localContentResolver, "accessibility_captioning_foreground_color", paramInt);
    while (true)
    {
      refreshPreviewText();
      return;
      if ((this.mBackgroundColor == paramListDialogPreference) || (this.mBackgroundOpacity == paramListDialogPreference))
      {
        int i = this.mBackgroundColor.getValue();
        int j = this.mBackgroundOpacity.getValue();
        if (Color.alpha(i) == 0);
        for (int k = Color.alpha(j); ; k = 0xFFFFFF & i | 0xFF000000 & j)
        {
          Settings.Secure.putInt(localContentResolver, "accessibility_captioning_background_color", k);
          break;
        }
      }
      if (this.mEdgeColor == paramListDialogPreference)
      {
        Settings.Secure.putInt(localContentResolver, "accessibility_captioning_edge_color", paramInt);
      }
      else if (this.mPreset == paramListDialogPreference)
      {
        Settings.Secure.putInt(localContentResolver, "accessibility_captioning_preset", paramInt);
        refreshShowingCustom();
      }
      else if (this.mEdgeType == paramListDialogPreference)
      {
        Settings.Secure.putInt(localContentResolver, "accessibility_captioning_edge_type", paramInt);
      }
    }
  }

  public void setParent(ToggleCaptioningPreferenceFragment paramToggleCaptioningPreferenceFragment)
  {
    this.mParent = paramToggleCaptioningPreferenceFragment;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accessibility.CaptionPropertiesFragment
 * JD-Core Version:    0.6.2
 */