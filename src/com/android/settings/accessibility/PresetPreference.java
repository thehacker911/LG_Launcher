package com.android.settings.accessibility;

import android.content.Context;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.CaptioningManager;
import android.widget.TextView;
import com.android.internal.widget.SubtitleView;

public class PresetPreference extends ListDialogPreference
{
  private final CaptioningManager mCaptioningManager;

  public PresetPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setDialogLayoutResource(2130968634);
    setListItemLayoutResource(2130968691);
    this.mCaptioningManager = ((CaptioningManager)paramContext.getSystemService("captioning"));
  }

  protected void onBindListItem(View paramView, int paramInt)
  {
    SubtitleView localSubtitleView = (SubtitleView)paramView.findViewById(2131230842);
    int i = getValueAt(paramInt);
    ToggleCaptioningPreferenceFragment.applyCaptionProperties(this.mCaptioningManager, localSubtitleView, i);
    localSubtitleView.setTextSize(96.0F);
    CharSequence localCharSequence = getTitleAt(paramInt);
    if (localCharSequence != null)
      ((TextView)paramView.findViewById(2131230772)).setText(localCharSequence);
  }

  public boolean shouldDisableDependents()
  {
    return (getValue() != -1) || (super.shouldDisableDependents());
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accessibility.PresetPreference
 * JD-Core Version:    0.6.2
 */