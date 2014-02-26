package com.android.settings.fuelgauge;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.BatteryStats.Uid;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.android.internal.os.BatteryStatsImpl;
import com.android.internal.os.PowerProfile;
import com.android.settings.HelpUtils;
import com.android.settings.Utils;
import java.util.Iterator;
import java.util.List;

public class PowerUsageSummary extends PreferenceFragment
{
  private PreferenceGroup mAppListGroup;
  private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.intent.action.BATTERY_CHANGED".equals(paramAnonymousIntent.getAction()))
      {
        String str1 = Utils.getBatteryPercentage(paramAnonymousIntent);
        String str2 = Utils.getBatteryStatus(PowerUsageSummary.this.getResources(), paramAnonymousIntent);
        String str3 = paramAnonymousContext.getResources().getString(2131428716, new Object[] { str1, str2 });
        PowerUsageSummary.this.mBatteryStatusPref.setTitle(str3);
        PowerUsageSummary.this.mStatsHelper.clearStats();
        PowerUsageSummary.this.refreshStats();
      }
    }
  };
  private Preference mBatteryStatusPref;
  Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default:
      case 1:
      case 2:
      }
      while (true)
      {
        super.handleMessage(paramAnonymousMessage);
        return;
        BatterySipper localBatterySipper = (BatterySipper)paramAnonymousMessage.obj;
        PowerGaugePreference localPowerGaugePreference = (PowerGaugePreference)PowerUsageSummary.this.findPreference(Integer.toString(localBatterySipper.uidObj.getUid()));
        if (localPowerGaugePreference != null)
        {
          localPowerGaugePreference.setIcon(localBatterySipper.icon);
          localPowerGaugePreference.setTitle(localBatterySipper.name);
          continue;
          Activity localActivity = PowerUsageSummary.this.getActivity();
          if (localActivity != null)
            localActivity.reportFullyDrawn();
        }
      }
    }
  };
  private BatteryStatsHelper mStatsHelper;
  private int mStatsType = 0;

  private void addNotAvailableMessage()
  {
    Preference localPreference = new Preference(getActivity());
    localPreference.setTitle(2131428715);
    this.mAppListGroup.addPreference(localPreference);
  }

  private void refreshStats()
  {
    this.mAppListGroup.removeAll();
    this.mAppListGroup.setOrderingAsAdded(false);
    this.mBatteryStatusPref.setOrder(-2);
    this.mAppListGroup.addPreference(this.mBatteryStatusPref);
    BatteryHistoryPreference localBatteryHistoryPreference = new BatteryHistoryPreference(getActivity(), this.mStatsHelper.getStats());
    localBatteryHistoryPreference.setOrder(-1);
    this.mAppListGroup.addPreference(localBatteryHistoryPreference);
    if (this.mStatsHelper.getPowerProfile().getAveragePower("screen.full") < 10.0D)
      addNotAvailableMessage();
    label115: 
    do
    {
      BatterySipper localBatterySipper;
      double d1;
      do
      {
        do
        {
          return;
          break label115;
          Iterator localIterator;
          while (!localIterator.hasNext())
          {
            this.mStatsHelper.refreshStats(false);
            localIterator = this.mStatsHelper.getUsageList().iterator();
          }
          localBatterySipper = (BatterySipper)localIterator.next();
        }
        while (localBatterySipper.getSortValue() < 5.0D);
        d1 = 100.0D * (localBatterySipper.getSortValue() / this.mStatsHelper.getTotalPower());
      }
      while (d1 < 1.0D);
      PowerGaugePreference localPowerGaugePreference = new PowerGaugePreference(getActivity(), localBatterySipper.getIcon(), localBatterySipper);
      double d2 = 100.0D * localBatterySipper.getSortValue() / this.mStatsHelper.getMaxPower();
      localBatterySipper.percent = d1;
      localPowerGaugePreference.setTitle(localBatterySipper.name);
      localPowerGaugePreference.setOrder(2147483647 - (int)localBatterySipper.getSortValue());
      localPowerGaugePreference.setPercent(d2, d1);
      if (localBatterySipper.uidObj != null)
        localPowerGaugePreference.setKey(Integer.toString(localBatterySipper.uidObj.getUid()));
      this.mAppListGroup.addPreference(localPowerGaugePreference);
    }
    while (this.mAppListGroup.getPreferenceCount() <= 11);
  }

  public void onAttach(Activity paramActivity)
  {
    super.onAttach(paramActivity);
    this.mStatsHelper = new BatteryStatsHelper(paramActivity, this.mHandler);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mStatsHelper.create(paramBundle);
    addPreferencesFromResource(2131034139);
    this.mAppListGroup = ((PreferenceGroup)findPreference("app_list"));
    this.mBatteryStatusPref = this.mAppListGroup.findPreference("battery_status");
    setHasOptionsMenu(true);
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    paramMenu.add(0, 2, 0, 2131428782).setIcon(2130837592).setAlphabeticShortcut('r').setShowAsAction(5);
    String str = getResources().getString(2131429260);
    if (!TextUtils.isEmpty(str))
    {
      MenuItem localMenuItem = paramMenu.add(0, 3, 0, 2131429253);
      HelpUtils.prepareHelpMenuItem(getActivity(), localMenuItem, str);
    }
  }

  public void onDestroy()
  {
    super.onDestroy();
    this.mStatsHelper.destroy();
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default:
      return false;
    case 1:
      if (this.mStatsType == 0);
      for (this.mStatsType = 3; ; this.mStatsType = 0)
      {
        refreshStats();
        return true;
      }
    case 2:
    }
    this.mStatsHelper.clearStats();
    refreshStats();
    return true;
  }

  public void onPause()
  {
    this.mStatsHelper.pause();
    this.mHandler.removeMessages(1);
    getActivity().unregisterReceiver(this.mBatteryInfoReceiver);
    super.onPause();
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    boolean bool2;
    if ((paramPreference instanceof BatteryHistoryPreference))
    {
      Parcel localParcel = Parcel.obtain();
      this.mStatsHelper.getStats().writeToParcelWithoutUids(localParcel, 0);
      byte[] arrayOfByte = localParcel.marshall();
      Bundle localBundle = new Bundle();
      localBundle.putByteArray("stats", arrayOfByte);
      ((PreferenceActivity)getActivity()).startPreferencePanel(BatteryHistoryDetail.class.getName(), localBundle, 2131428732, null, null, 0);
      bool2 = super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
    }
    boolean bool1;
    do
    {
      return bool2;
      bool1 = paramPreference instanceof PowerGaugePreference;
      bool2 = false;
    }
    while (!bool1);
    BatterySipper localBatterySipper = ((PowerGaugePreference)paramPreference).getInfo();
    this.mStatsHelper.startBatteryDetailPage((PreferenceActivity)getActivity(), localBatterySipper, true);
    return super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
  }

  public void onResume()
  {
    super.onResume();
    getActivity().registerReceiver(this.mBatteryInfoReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
    refreshStats();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.fuelgauge.PowerUsageSummary
 * JD-Core Version:    0.6.2
 */