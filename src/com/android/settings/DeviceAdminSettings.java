package com.android.settings;

import android.app.Activity;
import android.app.ListFragment;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;

public class DeviceAdminSettings extends ListFragment
{
  final HashSet<ComponentName> mActiveAdmins = new HashSet();
  final ArrayList<DeviceAdminInfo> mAvailableAdmins = new ArrayList();
  DevicePolicyManager mDPM;
  String mDeviceOwnerPkg;

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mDPM = ((DevicePolicyManager)getActivity().getSystemService("device_policy"));
    return paramLayoutInflater.inflate(2130968624, paramViewGroup, false);
  }

  public void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
  {
    DeviceAdminInfo localDeviceAdminInfo = (DeviceAdminInfo)paramListView.getAdapter().getItem(paramInt);
    Intent localIntent = new Intent();
    localIntent.setClass(getActivity(), DeviceAdminAdd.class);
    localIntent.putExtra("android.app.extra.DEVICE_ADMIN", localDeviceAdminInfo.getComponent());
    startActivity(localIntent);
  }

  public void onResume()
  {
    super.onResume();
    this.mDeviceOwnerPkg = this.mDPM.getDeviceOwner();
    if ((this.mDeviceOwnerPkg != null) && (!this.mDPM.isDeviceOwner(this.mDeviceOwnerPkg)))
      this.mDeviceOwnerPkg = null;
    updateList();
  }

  void updateList()
  {
    this.mActiveAdmins.clear();
    List localList1 = this.mDPM.getActiveAdmins();
    if (localList1 != null)
      for (int k = 0; k < localList1.size(); k++)
        this.mActiveAdmins.add(localList1.get(k));
    this.mAvailableAdmins.clear();
    Object localObject = getActivity().getPackageManager().queryBroadcastReceivers(new Intent("android.app.action.DEVICE_ADMIN_ENABLED"), 32896);
    if (localObject == null)
      localObject = Collections.emptyList();
    HashSet localHashSet = new HashSet(this.mActiveAdmins);
    Iterator localIterator1 = ((List)localObject).iterator();
    while (localIterator1.hasNext())
    {
      ResolveInfo localResolveInfo2 = (ResolveInfo)localIterator1.next();
      localHashSet.remove(new ComponentName(localResolveInfo2.activityInfo.packageName, localResolveInfo2.activityInfo.name));
    }
    if (!localHashSet.isEmpty())
    {
      ArrayList localArrayList = new ArrayList((Collection)localObject);
      PackageManager localPackageManager = getActivity().getPackageManager();
      Iterator localIterator2 = localHashSet.iterator();
      while (localIterator2.hasNext())
      {
        ComponentName localComponentName = (ComponentName)localIterator2.next();
        List localList2 = localPackageManager.queryBroadcastReceivers(new Intent().setComponent(localComponentName), 32896);
        if (localList2 != null)
          localArrayList.addAll(localList2);
      }
      localObject = localArrayList;
    }
    int i = 0;
    int j = ((List)localObject).size();
    while (true)
      if (i < j)
      {
        ResolveInfo localResolveInfo1 = (ResolveInfo)((List)localObject).get(i);
        try
        {
          DeviceAdminInfo localDeviceAdminInfo = new DeviceAdminInfo(getActivity(), localResolveInfo1);
          if ((localDeviceAdminInfo.isVisible()) || (this.mActiveAdmins.contains(localDeviceAdminInfo.getComponent())))
            this.mAvailableAdmins.add(localDeviceAdminInfo);
          i++;
        }
        catch (XmlPullParserException localXmlPullParserException)
        {
          while (true)
            Log.w("DeviceAdminSettings", "Skipping " + localResolveInfo1.activityInfo, localXmlPullParserException);
        }
        catch (IOException localIOException)
        {
          while (true)
            Log.w("DeviceAdminSettings", "Skipping " + localResolveInfo1.activityInfo, localIOException);
        }
      }
    ListView localListView = getListView();
    PolicyListAdapter localPolicyListAdapter = new PolicyListAdapter();
    localListView.setAdapter(localPolicyListAdapter);
  }

  class PolicyListAdapter extends BaseAdapter
  {
    final LayoutInflater mInflater = (LayoutInflater)DeviceAdminSettings.this.getActivity().getSystemService("layout_inflater");

    PolicyListAdapter()
    {
    }

    public boolean areAllItemsEnabled()
    {
      return false;
    }

    public void bindView(View paramView, int paramInt)
    {
      boolean bool1 = true;
      Activity localActivity = DeviceAdminSettings.this.getActivity();
      DeviceAdminSettings.ViewHolder localViewHolder = (DeviceAdminSettings.ViewHolder)paramView.getTag();
      DeviceAdminInfo localDeviceAdminInfo = (DeviceAdminInfo)DeviceAdminSettings.this.mAvailableAdmins.get(paramInt);
      localViewHolder.icon.setImageDrawable(localDeviceAdminInfo.loadIcon(localActivity.getPackageManager()));
      localViewHolder.name.setText(localDeviceAdminInfo.loadLabel(localActivity.getPackageManager()));
      localViewHolder.checkbox.setChecked(DeviceAdminSettings.this.mActiveAdmins.contains(localDeviceAdminInfo.getComponent()));
      boolean bool2;
      if ((localViewHolder.checkbox.isChecked()) && (localDeviceAdminInfo.getPackageName().equals(DeviceAdminSettings.this.mDeviceOwnerPkg)))
        bool2 = bool1;
      try
      {
        localViewHolder.description.setText(localDeviceAdminInfo.loadDescription(localActivity.getPackageManager()));
        label145: CheckBox localCheckBox = localViewHolder.checkbox;
        boolean bool3;
        label160: boolean bool4;
        label182: boolean bool5;
        label204: ImageView localImageView;
        if (!bool2)
        {
          bool3 = bool1;
          localCheckBox.setEnabled(bool3);
          TextView localTextView1 = localViewHolder.name;
          if (bool2)
            break label242;
          bool4 = bool1;
          localTextView1.setEnabled(bool4);
          TextView localTextView2 = localViewHolder.description;
          if (bool2)
            break label248;
          bool5 = bool1;
          localTextView2.setEnabled(bool5);
          localImageView = localViewHolder.icon;
          if (bool2)
            break label254;
        }
        while (true)
        {
          localImageView.setEnabled(bool1);
          return;
          bool2 = false;
          break;
          bool3 = false;
          break label160;
          label242: bool4 = false;
          break label182;
          label248: bool5 = false;
          break label204;
          label254: bool1 = false;
        }
      }
      catch (Resources.NotFoundException localNotFoundException)
      {
        break label145;
      }
    }

    public int getCount()
    {
      return DeviceAdminSettings.this.mAvailableAdmins.size();
    }

    public Object getItem(int paramInt)
    {
      return DeviceAdminSettings.this.mAvailableAdmins.get(paramInt);
    }

    public long getItemId(int paramInt)
    {
      return paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView == null);
      for (View localView = newView(paramViewGroup); ; localView = paramView)
      {
        bindView(localView, paramInt);
        return localView;
      }
    }

    public boolean hasStableIds()
    {
      return true;
    }

    public boolean isEnabled(int paramInt)
    {
      DeviceAdminInfo localDeviceAdminInfo = (DeviceAdminInfo)DeviceAdminSettings.this.mAvailableAdmins.get(paramInt);
      return (!DeviceAdminSettings.this.mActiveAdmins.contains(localDeviceAdminInfo.getComponent())) || (!localDeviceAdminInfo.getPackageName().equals(DeviceAdminSettings.this.mDeviceOwnerPkg));
    }

    public View newView(ViewGroup paramViewGroup)
    {
      View localView = this.mInflater.inflate(2130968623, paramViewGroup, false);
      DeviceAdminSettings.ViewHolder localViewHolder = new DeviceAdminSettings.ViewHolder();
      localViewHolder.icon = ((ImageView)localView.findViewById(2131230755));
      localViewHolder.name = ((TextView)localView.findViewById(2131230837));
      localViewHolder.checkbox = ((CheckBox)localView.findViewById(2131230839));
      localViewHolder.description = ((TextView)localView.findViewById(2131230838));
      localView.setTag(localViewHolder);
      return localView;
    }
  }

  static class ViewHolder
  {
    CheckBox checkbox;
    TextView description;
    ImageView icon;
    TextView name;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.DeviceAdminSettings
 * JD-Core Version:    0.6.2
 */