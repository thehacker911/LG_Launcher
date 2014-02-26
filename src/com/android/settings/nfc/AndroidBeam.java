package com.android.settings.nfc;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

public class AndroidBeam extends Fragment
  implements CompoundButton.OnCheckedChangeListener
{
  private Switch mActionBarSwitch;
  private NfcAdapter mNfcAdapter;
  private CharSequence mOldActivityTitle;
  private View mView;

  private void initView(View paramView)
  {
    this.mActionBarSwitch.setOnCheckedChangeListener(this);
    this.mActionBarSwitch.setChecked(this.mNfcAdapter.isNdefPushEnabled());
  }

  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    this.mActionBarSwitch.setEnabled(false);
    if (paramBoolean);
    for (boolean bool = this.mNfcAdapter.enableNdefPush(); ; bool = this.mNfcAdapter.disableNdefPush())
    {
      if (bool)
        this.mActionBarSwitch.setChecked(paramBoolean);
      this.mActionBarSwitch.setEnabled(true);
      return;
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Activity localActivity = getActivity();
    this.mActionBarSwitch = new Switch(localActivity);
    if ((localActivity instanceof PreferenceActivity))
    {
      int i = localActivity.getResources().getDimensionPixelSize(2131558402);
      this.mActionBarSwitch.setPaddingRelative(0, 0, i, 0);
      localActivity.getActionBar().setDisplayOptions(16, 16);
      localActivity.getActionBar().setCustomView(this.mActionBarSwitch, new ActionBar.LayoutParams(-2, -2, 8388629));
      this.mOldActivityTitle = localActivity.getActionBar().getTitle();
      localActivity.getActionBar().setTitle(2131427800);
    }
    this.mActionBarSwitch.setOnCheckedChangeListener(this);
    this.mNfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
    this.mActionBarSwitch.setChecked(this.mNfcAdapter.isNdefPushEnabled());
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mView = paramLayoutInflater.inflate(2130968580, paramViewGroup, false);
    initView(this.mView);
    return this.mView;
  }

  public void onDestroyView()
  {
    super.onDestroyView();
    getActivity().getActionBar().setCustomView(null);
    if (this.mOldActivityTitle != null)
      getActivity().getActionBar().setTitle(this.mOldActivityTitle);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.nfc.AndroidBeam
 * JD-Core Version:    0.6.2
 */