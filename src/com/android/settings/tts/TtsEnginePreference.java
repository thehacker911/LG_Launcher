package com.android.settings.tts;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.speech.tts.TextToSpeech.EngineInfo;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;

public class TtsEnginePreference extends Preference
{
  private final TextToSpeech.EngineInfo mEngineInfo;
  private final PreferenceActivity mPreferenceActivity;
  private volatile boolean mPreventRadioButtonCallbacks;
  private RadioButton mRadioButton;
  private final CompoundButton.OnCheckedChangeListener mRadioChangeListener = new CompoundButton.OnCheckedChangeListener()
  {
    public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
    {
      TtsEnginePreference.this.onRadioButtonClicked(paramAnonymousCompoundButton, paramAnonymousBoolean);
    }
  };
  private View mSettingsIcon;
  private final RadioButtonGroupState mSharedState;
  private Intent mVoiceCheckData;

  public TtsEnginePreference(Context paramContext, TextToSpeech.EngineInfo paramEngineInfo, RadioButtonGroupState paramRadioButtonGroupState, PreferenceActivity paramPreferenceActivity)
  {
    super(paramContext);
    setLayoutResource(2130968685);
    this.mSharedState = paramRadioButtonGroupState;
    this.mPreferenceActivity = paramPreferenceActivity;
    this.mEngineInfo = paramEngineInfo;
    this.mPreventRadioButtonCallbacks = false;
    setKey(this.mEngineInfo.name);
    setTitle(this.mEngineInfo.label);
  }

  private void displayDataAlert(DialogInterface.OnClickListener paramOnClickListener1, DialogInterface.OnClickListener paramOnClickListener2)
  {
    Log.i("TtsEnginePreference", "Displaying data alert for :" + this.mEngineInfo.name);
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getContext());
    localBuilder.setTitle(17039380);
    localBuilder.setIconAttribute(16843605);
    Context localContext = getContext();
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = this.mEngineInfo.label;
    localBuilder.setMessage(localContext.getString(2131428839, arrayOfObject));
    localBuilder.setCancelable(true);
    localBuilder.setPositiveButton(17039370, paramOnClickListener1);
    localBuilder.setNegativeButton(17039360, paramOnClickListener2);
    localBuilder.create().show();
  }

  private void makeCurrentEngine(Checkable paramCheckable)
  {
    if (this.mSharedState.getCurrentChecked() != null)
      this.mSharedState.getCurrentChecked().setChecked(false);
    this.mSharedState.setCurrentChecked(paramCheckable);
    this.mSharedState.setCurrentKey(getKey());
    callChangeListener(this.mSharedState.getCurrentKey());
    this.mSettingsIcon.setEnabled(true);
  }

  private void onRadioButtonClicked(final CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    if ((this.mPreventRadioButtonCallbacks) || (this.mSharedState.getCurrentChecked() == paramCompoundButton))
      return;
    if (paramBoolean)
    {
      if (shouldDisplayDataAlert())
      {
        displayDataAlert(new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            TtsEnginePreference.this.makeCurrentEngine(paramCompoundButton);
          }
        }
        , new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            paramCompoundButton.setChecked(false);
          }
        });
        return;
      }
      makeCurrentEngine(paramCompoundButton);
      return;
    }
    this.mSettingsIcon.setEnabled(false);
  }

  private boolean shouldDisplayDataAlert()
  {
    return !this.mEngineInfo.system;
  }

  public View getView(View paramView, ViewGroup paramViewGroup)
  {
    boolean bool1 = true;
    if (this.mSharedState == null)
      throw new IllegalStateException("Call to getView() before a call tosetSharedState()");
    View localView1 = super.getView(paramView, paramViewGroup);
    final RadioButton localRadioButton = (RadioButton)localView1.findViewById(2131230972);
    localRadioButton.setOnCheckedChangeListener(this.mRadioChangeListener);
    boolean bool2 = getKey().equals(this.mSharedState.getCurrentKey());
    if (bool2)
      this.mSharedState.setCurrentChecked(localRadioButton);
    this.mPreventRadioButtonCallbacks = bool1;
    localRadioButton.setChecked(bool2);
    this.mPreventRadioButtonCallbacks = false;
    this.mRadioButton = localRadioButton;
    localView1.findViewById(2131230973).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        TtsEnginePreference localTtsEnginePreference = TtsEnginePreference.this;
        RadioButton localRadioButton = localRadioButton;
        if (!localRadioButton.isChecked());
        for (boolean bool = true; ; bool = false)
        {
          localTtsEnginePreference.onRadioButtonClicked(localRadioButton, bool);
          return;
        }
      }
    });
    this.mSettingsIcon = localView1.findViewById(2131230974);
    View localView2 = this.mSettingsIcon;
    if ((bool2) && (this.mVoiceCheckData != null));
    while (true)
    {
      localView2.setEnabled(bool1);
      if (!bool2)
        this.mSettingsIcon.setAlpha(0.4F);
      this.mSettingsIcon.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          Bundle localBundle = new Bundle();
          localBundle.putString("name", TtsEnginePreference.this.mEngineInfo.name);
          localBundle.putString("label", TtsEnginePreference.this.mEngineInfo.label);
          if (TtsEnginePreference.this.mVoiceCheckData != null)
            localBundle.putParcelable("voices", TtsEnginePreference.this.mVoiceCheckData);
          TtsEnginePreference.this.mPreferenceActivity.startPreferencePanel(TtsEngineSettingsFragment.class.getName(), localBundle, 0, TtsEnginePreference.this.mEngineInfo.label, null, 0);
        }
      });
      if (this.mVoiceCheckData != null)
        this.mSettingsIcon.setEnabled(this.mRadioButton.isChecked());
      return localView1;
      bool1 = false;
    }
  }

  public void setVoiceDataDetails(Intent paramIntent)
  {
    this.mVoiceCheckData = paramIntent;
    if ((this.mSettingsIcon != null) && (this.mRadioButton != null))
    {
      if (this.mRadioButton.isChecked())
        this.mSettingsIcon.setEnabled(true);
    }
    else
      return;
    this.mSettingsIcon.setEnabled(false);
    this.mSettingsIcon.setAlpha(0.4F);
  }

  public static abstract interface RadioButtonGroupState
  {
    public abstract Checkable getCurrentChecked();

    public abstract String getCurrentKey();

    public abstract void setCurrentChecked(Checkable paramCheckable);

    public abstract void setCurrentKey(String paramString);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.tts.TtsEnginePreference
 * JD-Core Version:    0.6.2
 */