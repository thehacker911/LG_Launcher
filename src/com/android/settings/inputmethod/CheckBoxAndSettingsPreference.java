package com.android.settings.inputmethod;

import android.content.Context;
import android.content.Intent;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.SettingsPreferenceFragment;

public class CheckBoxAndSettingsPreference extends CheckBoxPreference
{
  private SettingsPreferenceFragment mFragment;
  private ImageView mSettingsButton;
  private Intent mSettingsIntent;
  private TextView mSummaryText;
  private TextView mTitleText;

  public CheckBoxAndSettingsPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setLayoutResource(2130968677);
    setWidgetLayoutResource(2130968678);
  }

  private void enableSettingsButton()
  {
    if (this.mSettingsButton != null)
    {
      if (this.mSettingsIntent != null)
        break label54;
      this.mSettingsButton.setVisibility(8);
    }
    while (true)
    {
      if (this.mTitleText != null)
        this.mTitleText.setEnabled(true);
      if (this.mSummaryText != null)
        this.mSummaryText.setEnabled(true);
      return;
      label54: boolean bool = isChecked();
      this.mSettingsButton.setEnabled(bool);
      this.mSettingsButton.setClickable(bool);
      this.mSettingsButton.setFocusable(bool);
      if (!bool)
        this.mSettingsButton.setAlpha(0.4F);
    }
  }

  protected void onBindView(View paramView)
  {
    super.onBindView(paramView);
    paramView.findViewById(2131230960).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        CheckBoxAndSettingsPreference.this.onCheckBoxClicked();
      }
    });
    this.mSettingsButton = ((ImageView)paramView.findViewById(2131230961));
    this.mTitleText = ((TextView)paramView.findViewById(16908310));
    this.mSummaryText = ((TextView)paramView.findViewById(16908304));
    this.mSettingsButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        CheckBoxAndSettingsPreference.this.onSettingsButtonClicked();
      }
    });
    enableSettingsButton();
  }

  protected void onCheckBoxClicked()
  {
    if (isChecked())
    {
      setChecked(false);
      return;
    }
    setChecked(true);
  }

  protected void onSettingsButtonClicked()
  {
    if ((this.mFragment != null) && (this.mSettingsIntent != null))
      this.mFragment.startActivity(this.mSettingsIntent);
  }

  public void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
    enableSettingsButton();
  }

  public void setFragmentIntent(SettingsPreferenceFragment paramSettingsPreferenceFragment, Intent paramIntent)
  {
    this.mFragment = paramSettingsPreferenceFragment;
    this.mSettingsIntent = paramIntent;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.inputmethod.CheckBoxAndSettingsPreference
 * JD-Core Version:    0.6.2
 */