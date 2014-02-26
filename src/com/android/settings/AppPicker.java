package com.android.settings;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.android.settings.applications.AppViewHolder;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppPicker extends ListActivity
{
  private static final Comparator<MyApplicationInfo> sDisplayNameComparator = new Comparator()
  {
    private final Collator collator = Collator.getInstance();

    public final int compare(AppPicker.MyApplicationInfo paramAnonymousMyApplicationInfo1, AppPicker.MyApplicationInfo paramAnonymousMyApplicationInfo2)
    {
      return this.collator.compare(paramAnonymousMyApplicationInfo1.label, paramAnonymousMyApplicationInfo2.label);
    }
  };
  private AppListAdapter mAdapter;

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mAdapter = new AppListAdapter(this);
    if (this.mAdapter.getCount() <= 0)
    {
      finish();
      return;
    }
    setListAdapter(this.mAdapter);
  }

  protected void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
  {
    MyApplicationInfo localMyApplicationInfo = (MyApplicationInfo)this.mAdapter.getItem(paramInt);
    Intent localIntent = new Intent();
    if (localMyApplicationInfo.info != null)
      localIntent.setAction(localMyApplicationInfo.info.packageName);
    setResult(-1, localIntent);
    finish();
  }

  protected void onResume()
  {
    super.onResume();
  }

  protected void onStop()
  {
    super.onStop();
  }

  public class AppListAdapter extends ArrayAdapter<AppPicker.MyApplicationInfo>
  {
    private final LayoutInflater mInflater;
    private final List<AppPicker.MyApplicationInfo> mPackageInfoList = new ArrayList();

    public AppListAdapter(Context arg2)
    {
      super(0);
      this.mInflater = ((LayoutInflater)localContext.getSystemService("layout_inflater"));
      List localList = localContext.getPackageManager().getInstalledApplications(0);
      int i = 0;
      if (i < localList.size())
      {
        ApplicationInfo localApplicationInfo = (ApplicationInfo)localList.get(i);
        if (localApplicationInfo.uid == 1000);
        while (true)
        {
          i++;
          break;
          if (((0x2 & localApplicationInfo.flags) != 0) || (!"user".equals(Build.TYPE)))
          {
            AppPicker.MyApplicationInfo localMyApplicationInfo2 = new AppPicker.MyApplicationInfo(AppPicker.this);
            localMyApplicationInfo2.info = localApplicationInfo;
            localMyApplicationInfo2.label = localMyApplicationInfo2.info.loadLabel(AppPicker.this.getPackageManager()).toString();
            this.mPackageInfoList.add(localMyApplicationInfo2);
          }
        }
      }
      Collections.sort(this.mPackageInfoList, AppPicker.sDisplayNameComparator);
      AppPicker.MyApplicationInfo localMyApplicationInfo1 = new AppPicker.MyApplicationInfo(AppPicker.this);
      localMyApplicationInfo1.label = localContext.getText(2131429026);
      this.mPackageInfoList.add(0, localMyApplicationInfo1);
      addAll(this.mPackageInfoList);
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      AppViewHolder localAppViewHolder = AppViewHolder.createOrRecycle(this.mInflater, paramView);
      View localView = localAppViewHolder.rootView;
      AppPicker.MyApplicationInfo localMyApplicationInfo = (AppPicker.MyApplicationInfo)getItem(paramInt);
      localAppViewHolder.appName.setText(localMyApplicationInfo.label);
      if (localMyApplicationInfo.info != null)
      {
        localAppViewHolder.appIcon.setImageDrawable(localMyApplicationInfo.info.loadIcon(AppPicker.this.getPackageManager()));
        localAppViewHolder.appSize.setText(localMyApplicationInfo.info.packageName);
      }
      while (true)
      {
        localAppViewHolder.disabled.setVisibility(8);
        localAppViewHolder.checkBox.setVisibility(8);
        return localView;
        localAppViewHolder.appIcon.setImageDrawable(null);
        localAppViewHolder.appSize.setText("");
      }
    }
  }

  class MyApplicationInfo
  {
    ApplicationInfo info;
    CharSequence label;

    MyApplicationInfo()
    {
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.AppPicker
 * JD-Core Version:    0.6.2
 */