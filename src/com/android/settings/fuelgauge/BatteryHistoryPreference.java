package com.android.settings.fuelgauge;

import android.content.Context;
import android.os.BatteryStats;
import android.preference.Preference;
import android.view.View;

public class BatteryHistoryPreference extends Preference
{
  private BatteryStats mStats;

  public BatteryHistoryPreference(Context paramContext, BatteryStats paramBatteryStats)
  {
    super(paramContext);
    setLayoutResource(2130968666);
    this.mStats = paramBatteryStats;
  }

  protected void onBindView(View paramView)
  {
    super.onBindView(paramView);
    ((BatteryHistoryChart)paramView.findViewById(16842754)).setStats(this.mStats);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.fuelgauge.BatteryHistoryPreference
 * JD-Core Version:    0.6.2
 */