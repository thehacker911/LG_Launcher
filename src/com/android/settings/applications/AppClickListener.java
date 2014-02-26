package com.android.settings.applications;

import android.view.View;
import android.widget.AdapterView;

abstract interface AppClickListener
{
  public abstract void onItemClick(ManageApplications.TabInfo paramTabInfo, AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong);
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.applications.AppClickListener
 * JD-Core Version:    0.6.2
 */