package com.android.settings.fuelgauge;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.os.BatteryStatsImpl;

public class BatteryHistoryDetail extends Fragment
{
  private BatteryStatsImpl mStats;

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    byte[] arrayOfByte = getArguments().getByteArray("stats");
    Parcel localParcel = Parcel.obtain();
    localParcel.unmarshall(arrayOfByte, 0, arrayOfByte.length);
    localParcel.setDataPosition(0);
    this.mStats = ((BatteryStatsImpl)BatteryStatsImpl.CREATOR.createFromParcel(localParcel));
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    View localView = paramLayoutInflater.inflate(2130968666, null);
    ((BatteryHistoryChart)localView.findViewById(16842754)).setStats(this.mStats);
    return localView;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.fuelgauge.BatteryHistoryDetail
 * JD-Core Version:    0.6.2
 */