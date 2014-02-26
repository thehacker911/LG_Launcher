package com.android.settings.accessibility;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceFrameLayout;
import android.preference.PreferenceFrameLayout.LayoutParams;
import android.provider.Settings.Secure;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.CaptioningManager;
import android.widget.TextView;
import com.android.internal.widget.SubtitleView;
import java.util.Locale;

public class ToggleCaptioningPreferenceFragment extends Fragment
{
  private CaptioningManager mCaptioningManager;
  private SubtitleView mPreviewText;
  private CaptionPropertiesFragment mPropsFragment;

  public static void applyCaptionProperties(CaptioningManager paramCaptioningManager, SubtitleView paramSubtitleView, int paramInt)
  {
    paramSubtitleView.setStyle(paramInt);
    Context localContext = paramSubtitleView.getContext();
    localContext.getContentResolver();
    paramSubtitleView.setTextSize(48.0F * paramCaptioningManager.getFontScale());
    Locale localLocale = paramCaptioningManager.getLocale();
    if (localLocale != null)
    {
      paramSubtitleView.setText(AccessibilityUtils.getTextForLocale(localContext, localLocale, 2131428663));
      return;
    }
    paramSubtitleView.setText(2131428663);
  }

  private void installActionBarToggleSwitch()
  {
    Activity localActivity = getActivity();
    ToggleSwitch localToggleSwitch = new ToggleSwitch(localActivity);
    localToggleSwitch.setPaddingRelative(0, 0, getResources().getDimensionPixelSize(2131558402), 0);
    ActionBar localActionBar = localActivity.getActionBar();
    localActionBar.setDisplayOptions(16, 16);
    localActionBar.setCustomView(localToggleSwitch, new ActionBar.LayoutParams(-2, -2, 8388629));
    boolean bool = this.mCaptioningManager.isEnabled();
    this.mPropsFragment.getPreferenceScreen().setEnabled(bool);
    SubtitleView localSubtitleView = this.mPreviewText;
    int i = 0;
    if (bool);
    while (true)
    {
      localSubtitleView.setVisibility(i);
      localToggleSwitch.setCheckedInternal(bool);
      localToggleSwitch.setOnBeforeCheckedChangeListener(new ToggleSwitch.OnBeforeCheckedChangeListener()
      {
        public boolean onBeforeCheckedChanged(ToggleSwitch paramAnonymousToggleSwitch, boolean paramAnonymousBoolean)
        {
          paramAnonymousToggleSwitch.setCheckedInternal(paramAnonymousBoolean);
          ContentResolver localContentResolver = ToggleCaptioningPreferenceFragment.this.getActivity().getContentResolver();
          int i;
          SubtitleView localSubtitleView;
          if (paramAnonymousBoolean)
          {
            i = 1;
            Settings.Secure.putInt(localContentResolver, "accessibility_captioning_enabled", i);
            ToggleCaptioningPreferenceFragment.this.mPropsFragment.getPreferenceScreen().setEnabled(paramAnonymousBoolean);
            localSubtitleView = ToggleCaptioningPreferenceFragment.this.mPreviewText;
            if (!paramAnonymousBoolean)
              break label77;
          }
          label77: for (int j = 0; ; j = 4)
          {
            localSubtitleView.setVisibility(j);
            return false;
            i = 0;
            break;
          }
        }
      });
      return;
      i = 4;
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mCaptioningManager = ((CaptioningManager)getActivity().getSystemService("captioning"));
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    View localView = paramLayoutInflater.inflate(2130968596, paramViewGroup, false);
    if ((paramViewGroup instanceof PreferenceFrameLayout))
      ((PreferenceFrameLayout.LayoutParams)localView.getLayoutParams()).removeBorders = true;
    return localView;
  }

  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    this.mPropsFragment = ((CaptionPropertiesFragment)getFragmentManager().findFragmentById(2131230758));
    this.mPropsFragment.setParent(this);
    this.mPreviewText = ((SubtitleView)paramView.findViewById(2131230757));
    installActionBarToggleSwitch();
    refreshPreviewText();
  }

  public void refreshPreviewText()
  {
    SubtitleView localSubtitleView = this.mPreviewText;
    if (localSubtitleView != null)
    {
      Activity localActivity = getActivity();
      localActivity.getContentResolver();
      int i = this.mCaptioningManager.getRawUserStyle();
      applyCaptionProperties(this.mCaptioningManager, localSubtitleView, i);
      Locale localLocale = this.mCaptioningManager.getLocale();
      if (localLocale != null)
        localSubtitleView.setText(AccessibilityUtils.getTextForLocale(localActivity, localLocale, 2131428662));
    }
    else
    {
      return;
    }
    localSubtitleView.setText(2131428662);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accessibility.ToggleCaptioningPreferenceFragment
 * JD-Core Version:    0.6.2
 */