package com.android.settings.deviceinfo;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import com.google.android.collect.Lists;
import java.util.Collections;
import java.util.List;

public class UsageBarPreference extends Preference
{
  private PercentageBarChart mChart = null;
  private final List<PercentageBarChart.Entry> mEntries = Lists.newArrayList();

  public UsageBarPreference(Context paramContext)
  {
    super(paramContext);
    setLayoutResource(2130968680);
  }

  public UsageBarPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setLayoutResource(2130968680);
  }

  public UsageBarPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setLayoutResource(2130968680);
  }

  public void addEntry(int paramInt1, float paramFloat, int paramInt2)
  {
    this.mEntries.add(PercentageBarChart.createEntry(paramInt1, paramFloat, paramInt2));
    Collections.sort(this.mEntries);
  }

  public void clear()
  {
    this.mEntries.clear();
  }

  public void commit()
  {
    if (this.mChart != null)
      this.mChart.invalidate();
  }

  protected void onBindView(View paramView)
  {
    super.onBindView(paramView);
    this.mChart = ((PercentageBarChart)paramView.findViewById(2131230962));
    this.mChart.setEntries(this.mEntries);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.deviceinfo.UsageBarPreference
 * JD-Core Version:    0.6.2
 */