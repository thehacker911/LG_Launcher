package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Fragment;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.android.internal.widget.LockPatternUtils;

public class CryptKeeperSettings extends Fragment
{
  private View mBatteryWarning;
  private View mContentView;
  private Button mInitiateButton;
  private View.OnClickListener mInitiateListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (!CryptKeeperSettings.this.runKeyguardConfirmation(55))
        new AlertDialog.Builder(CryptKeeperSettings.this.getActivity()).setTitle(2131427631).setIconAttribute(16843605).setMessage(2131427632).setPositiveButton(17039370, null).create().show();
    }
  };
  private IntentFilter mIntentFilter;
  private BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      int i = 8;
      boolean bool1 = true;
      boolean bool2;
      boolean bool3;
      label73: label92: int n;
      label116: View localView2;
      if (paramAnonymousIntent.getAction().equals("android.intent.action.BATTERY_CHANGED"))
      {
        int j = paramAnonymousIntent.getIntExtra("level", 0);
        int k = paramAnonymousIntent.getIntExtra("plugged", 0);
        int m = paramAnonymousIntent.getIntExtra("invalid_charger", 0);
        if (j < 80)
          break label144;
        bool2 = bool1;
        if (((k & 0x7) == 0) || (m != 0))
          break label150;
        bool3 = bool1;
        Button localButton = CryptKeeperSettings.this.mInitiateButton;
        if ((!bool2) || (!bool3))
          break label156;
        localButton.setEnabled(bool1);
        View localView1 = CryptKeeperSettings.this.mPowerWarning;
        if (!bool3)
          break label162;
        n = i;
        localView1.setVisibility(n);
        localView2 = CryptKeeperSettings.this.mBatteryWarning;
        if (!bool2)
          break label168;
      }
      while (true)
      {
        localView2.setVisibility(i);
        return;
        label144: bool2 = false;
        break;
        label150: bool3 = false;
        break label73;
        label156: bool1 = false;
        break label92;
        label162: n = 0;
        break label116;
        label168: i = 0;
      }
    }
  };
  private View mPowerWarning;

  private boolean runKeyguardConfirmation(int paramInt)
  {
    LockPatternUtils localLockPatternUtils = new LockPatternUtils(getActivity());
    int i = localLockPatternUtils.getActivePasswordQuality();
    if ((i == 32768) && (localLockPatternUtils.isLockPasswordEnabled()))
      i = localLockPatternUtils.getKeyguardStoredPasswordQuality();
    if (i < 131072)
      return false;
    Resources localResources = getActivity().getResources();
    return new ChooseLockSettingsHelper(getActivity(), this).launchConfirmationActivity(paramInt, localResources.getText(2131428222), localResources.getText(2131428223));
  }

  private void showFinalConfirmation(String paramString)
  {
    Preference localPreference = new Preference(getActivity());
    localPreference.setFragment(CryptKeeperConfirm.class.getName());
    localPreference.setTitle(2131427633);
    localPreference.getExtras().putString("password", paramString);
    ((PreferenceActivity)getActivity()).onPreferenceStartFragment(null, localPreference);
  }

  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    Activity localActivity = getActivity();
    if ("android.app.action.START_ENCRYPTION".equals(localActivity.getIntent().getAction()))
    {
      DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)localActivity.getSystemService("device_policy");
      if ((localDevicePolicyManager != null) && (localDevicePolicyManager.getStorageEncryptionStatus() != 1))
        localActivity.finish();
    }
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (paramInt1 != 55);
    String str;
    do
    {
      do
        return;
      while ((paramInt2 != -1) || (paramIntent == null));
      str = paramIntent.getStringExtra("password");
    }
    while (TextUtils.isEmpty(str));
    showFinalConfirmation(str);
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mContentView = paramLayoutInflater.inflate(2130968608, null);
    this.mIntentFilter = new IntentFilter();
    this.mIntentFilter.addAction("android.intent.action.BATTERY_CHANGED");
    this.mInitiateButton = ((Button)this.mContentView.findViewById(2131230789));
    this.mInitiateButton.setOnClickListener(this.mInitiateListener);
    this.mInitiateButton.setEnabled(false);
    this.mPowerWarning = this.mContentView.findViewById(2131230788);
    this.mBatteryWarning = this.mContentView.findViewById(2131230787);
    return this.mContentView;
  }

  public void onPause()
  {
    super.onPause();
    getActivity().unregisterReceiver(this.mIntentReceiver);
  }

  public void onResume()
  {
    super.onResume();
    getActivity().registerReceiver(this.mIntentReceiver, this.mIntentFilter);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.CryptKeeperSettings
 * JD-Core Version:    0.6.2
 */