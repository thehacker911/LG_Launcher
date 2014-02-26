package com.android.settings.inputmethod;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.preference.Preference;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.textservice.SpellCheckerInfo;
import android.view.textservice.SpellCheckerSubtype;
import android.view.textservice.TextServicesManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class SingleSpellCheckerPreference extends Preference
{
  private static final String TAG = SingleSpellCheckerPreference.class.getSimpleName();
  private AlertDialog mDialog = null;
  private final SpellCheckersSettings mFragment;
  private View mPrefAll;
  private View mPrefLeftButton;
  private RadioButton mRadioButton;
  private final Resources mRes;
  private boolean mSelected;
  private View mSettingsButton;
  private Intent mSettingsIntent;
  private final SpellCheckerInfo mSpellCheckerInfo;
  private ImageView mSubtypeButton;
  private TextView mSummaryText;
  private TextView mTitleText;
  private final TextServicesManager mTsm;

  public SingleSpellCheckerPreference(SpellCheckersSettings paramSpellCheckersSettings, Intent paramIntent, SpellCheckerInfo paramSpellCheckerInfo, TextServicesManager paramTextServicesManager)
  {
    super(paramSpellCheckersSettings.getActivity(), null, 0);
    this.mFragment = paramSpellCheckersSettings;
    this.mRes = paramSpellCheckersSettings.getActivity().getResources();
    this.mTsm = paramTextServicesManager;
    setLayoutResource(2130968684);
    this.mSpellCheckerInfo = paramSpellCheckerInfo;
    this.mSelected = false;
    String str = this.mSpellCheckerInfo.getSettingsActivity();
    if (!TextUtils.isEmpty(str))
    {
      this.mSettingsIntent = new Intent("android.intent.action.MAIN");
      this.mSettingsIntent.setClassName(this.mSpellCheckerInfo.getPackageName(), str);
      return;
    }
    this.mSettingsIntent = null;
  }

  private void enableButtons(boolean paramBoolean)
  {
    if (this.mSettingsButton != null)
    {
      if (this.mSettingsIntent == null)
        this.mSettingsButton.setVisibility(8);
    }
    else if (this.mSubtypeButton != null)
    {
      if (this.mSpellCheckerInfo.getSubtypeCount() > 0)
        break label90;
      this.mSubtypeButton.setVisibility(8);
    }
    label90: 
    do
    {
      return;
      this.mSettingsButton.setEnabled(paramBoolean);
      this.mSettingsButton.setClickable(paramBoolean);
      this.mSettingsButton.setFocusable(paramBoolean);
      if (paramBoolean)
        break;
      this.mSettingsButton.setAlpha(0.4F);
      break;
      this.mSubtypeButton.setEnabled(paramBoolean);
      this.mSubtypeButton.setClickable(paramBoolean);
      this.mSubtypeButton.setFocusable(paramBoolean);
    }
    while (paramBoolean);
    this.mSubtypeButton.setAlpha(0.4F);
  }

  private void onLeftButtonClicked(View paramView)
  {
    this.mFragment.onPreferenceClick(this);
  }

  private void onSettingsButtonClicked(View paramView)
  {
    if ((this.mFragment != null) && (this.mSettingsIntent != null));
    try
    {
      this.mFragment.startActivity(this.mSettingsIntent);
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      SpellCheckersSettings localSpellCheckersSettings = this.mFragment;
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = this.mSpellCheckerInfo.loadLabel(this.mFragment.getActivity().getPackageManager());
      String str = localSpellCheckersSettings.getString(2131428517, arrayOfObject);
      Toast.makeText(this.mFragment.getActivity(), str, 1).show();
    }
  }

  private void onSubtypeButtonClicked(View paramView)
  {
    if ((this.mDialog != null) && (this.mDialog.isShowing()))
      this.mDialog.dismiss();
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this.mFragment.getActivity());
    localBuilder.setTitle(2131428503);
    int i = this.mSpellCheckerInfo.getSubtypeCount();
    CharSequence[] arrayOfCharSequence = new CharSequence[i + 1];
    arrayOfCharSequence[0] = this.mRes.getString(2131428573);
    for (int j = 0; j < i; j++)
    {
      CharSequence localCharSequence = this.mSpellCheckerInfo.getSubtypeAt(j).getDisplayName(this.mFragment.getActivity(), this.mSpellCheckerInfo.getPackageName(), this.mSpellCheckerInfo.getServiceInfo().applicationInfo);
      arrayOfCharSequence[(j + 1)] = localCharSequence;
    }
    SpellCheckerSubtype localSpellCheckerSubtype = this.mTsm.getCurrentSpellCheckerSubtype(false);
    int k = 0;
    if (localSpellCheckerSubtype != null);
    for (int m = 0; ; m++)
    {
      k = 0;
      if (m < i)
      {
        if (this.mSpellCheckerInfo.getSubtypeAt(m).equals(localSpellCheckerSubtype))
          k = m + 1;
      }
      else
      {
        localBuilder.setSingleChoiceItems(arrayOfCharSequence, k, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            if (paramAnonymousInt == 0)
              SingleSpellCheckerPreference.this.mTsm.setSpellCheckerSubtype(null);
            while (true)
            {
              paramAnonymousDialogInterface.dismiss();
              return;
              SingleSpellCheckerPreference.this.mTsm.setSpellCheckerSubtype(SingleSpellCheckerPreference.this.mSpellCheckerInfo.getSubtypeAt(paramAnonymousInt - 1));
            }
          }
        });
        this.mDialog = localBuilder.create();
        this.mDialog.show();
        return;
      }
    }
  }

  private void updateSelectedState(boolean paramBoolean)
  {
    if (this.mPrefAll != null)
    {
      this.mRadioButton.setChecked(paramBoolean);
      enableButtons(paramBoolean);
    }
  }

  public SpellCheckerInfo getSpellCheckerInfo()
  {
    return this.mSpellCheckerInfo;
  }

  protected void onBindView(View paramView)
  {
    super.onBindView(paramView);
    this.mPrefAll = paramView.findViewById(2131230965);
    this.mRadioButton = ((RadioButton)paramView.findViewById(2131230967));
    this.mPrefLeftButton = paramView.findViewById(2131230966);
    this.mPrefLeftButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        SingleSpellCheckerPreference.this.onLeftButtonClicked(paramAnonymousView);
      }
    });
    this.mTitleText = ((TextView)paramView.findViewById(16908310));
    this.mSummaryText = ((TextView)paramView.findViewById(16908304));
    this.mSubtypeButton = ((ImageView)paramView.findViewById(2131230970));
    this.mSubtypeButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        SingleSpellCheckerPreference.this.onSubtypeButtonClicked(paramAnonymousView);
      }
    });
    this.mSettingsButton = paramView.findViewById(2131230968);
    this.mSettingsButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        SingleSpellCheckerPreference.this.onSettingsButtonClicked(paramAnonymousView);
      }
    });
    updateSelectedState(this.mSelected);
  }

  public void setSelected(boolean paramBoolean)
  {
    this.mSelected = paramBoolean;
    updateSelectedState(paramBoolean);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.inputmethod.SingleSpellCheckerPreference
 * JD-Core Version:    0.6.2
 */