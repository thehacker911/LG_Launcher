package com.android.settings;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageItemInfo.DisplayNameComparator;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class NotificationAccessSettings extends ListFragment
{
  static final String TAG = NotificationAccessSettings.class.getSimpleName();
  private final Uri ENABLED_NOTIFICATION_LISTENERS_URI = Settings.Secure.getUriFor("enabled_notification_listeners");
  private ContentResolver mCR;
  private final HashSet<ComponentName> mEnabledListeners = new HashSet();
  private ListenerListAdapter mList;
  private PackageManager mPM;
  private final BroadcastReceiver mPackageReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      NotificationAccessSettings.this.updateList();
    }
  };
  private final ContentObserver mSettingsObserver = new ContentObserver(new Handler())
  {
    public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
    {
      NotificationAccessSettings.this.updateList();
    }
  };

  private static int getListeners(ArrayAdapter<ServiceInfo> paramArrayAdapter, PackageManager paramPackageManager)
  {
    int i = 0;
    if (paramArrayAdapter != null)
      paramArrayAdapter.clear();
    int j = ActivityManager.getCurrentUser();
    List localList = paramPackageManager.queryIntentServicesAsUser(new Intent("android.service.notification.NotificationListenerService"), 132, j);
    int k = 0;
    int m = localList.size();
    if (k < m)
    {
      ServiceInfo localServiceInfo = ((ResolveInfo)localList.get(k)).serviceInfo;
      if (!"android.permission.BIND_NOTIFICATION_LISTENER_SERVICE".equals(localServiceInfo.permission))
        Slog.w(TAG, "Skipping notification listener service " + localServiceInfo.packageName + "/" + localServiceInfo.name + ": it does not require the permission " + "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE");
      while (true)
      {
        k++;
        break;
        if (paramArrayAdapter != null)
          paramArrayAdapter.add(localServiceInfo);
        i++;
      }
    }
    return i;
  }

  static int getListenersCount(PackageManager paramPackageManager)
  {
    return getListeners(null, paramPackageManager);
  }

  boolean isListenerEnabled(ServiceInfo paramServiceInfo)
  {
    ComponentName localComponentName = new ComponentName(paramServiceInfo.packageName, paramServiceInfo.name);
    return this.mEnabledListeners.contains(localComponentName);
  }

  void loadEnabledListeners()
  {
    this.mEnabledListeners.clear();
    String str = Settings.Secure.getString(this.mCR, "enabled_notification_listeners");
    if ((str != null) && (!"".equals(str)))
    {
      String[] arrayOfString = str.split(":");
      for (int i = 0; i < arrayOfString.length; i++)
      {
        ComponentName localComponentName = ComponentName.unflattenFromString(arrayOfString[i]);
        if (localComponentName != null)
          this.mEnabledListeners.add(localComponentName);
      }
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mPM = getActivity().getPackageManager();
    this.mCR = getActivity().getContentResolver();
    this.mList = new ListenerListAdapter(getActivity());
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    return paramLayoutInflater.inflate(2130968654, paramViewGroup, false);
  }

  public void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
  {
    ServiceInfo localServiceInfo = (ServiceInfo)this.mList.getItem(paramInt);
    ComponentName localComponentName = new ComponentName(localServiceInfo.packageName, localServiceInfo.name);
    if (this.mEnabledListeners.contains(localComponentName))
    {
      this.mEnabledListeners.remove(localComponentName);
      saveEnabledListeners();
      return;
    }
    new ListenerWarningDialogFragment().setListenerInfo(localComponentName, localServiceInfo.loadLabel(this.mPM).toString()).show(getFragmentManager(), "dialog");
  }

  public void onPause()
  {
    super.onPause();
    getActivity().unregisterReceiver(this.mPackageReceiver);
    this.mCR.unregisterContentObserver(this.mSettingsObserver);
  }

  public void onResume()
  {
    super.onResume();
    updateList();
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.PACKAGE_ADDED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
    localIntentFilter.addDataScheme("package");
    getActivity().registerReceiver(this.mPackageReceiver, localIntentFilter);
    this.mCR.registerContentObserver(this.ENABLED_NOTIFICATION_LISTENERS_URI, false, this.mSettingsObserver);
  }

  void saveEnabledListeners()
  {
    StringBuilder localStringBuilder = null;
    Iterator localIterator = this.mEnabledListeners.iterator();
    if (localIterator.hasNext())
    {
      ComponentName localComponentName = (ComponentName)localIterator.next();
      if (localStringBuilder == null)
        localStringBuilder = new StringBuilder();
      while (true)
      {
        localStringBuilder.append(localComponentName.flattenToString());
        break;
        localStringBuilder.append(':');
      }
    }
    ContentResolver localContentResolver = this.mCR;
    if (localStringBuilder != null);
    for (String str = localStringBuilder.toString(); ; str = "")
    {
      Settings.Secure.putString(localContentResolver, "enabled_notification_listeners", str);
      return;
    }
  }

  void updateList()
  {
    loadEnabledListeners();
    getListeners(this.mList, this.mPM);
    this.mList.sort(new PackageItemInfo.DisplayNameComparator(this.mPM));
    getListView().setAdapter(this.mList);
  }

  class ListenerListAdapter extends ArrayAdapter<ServiceInfo>
  {
    final LayoutInflater mInflater = (LayoutInflater)NotificationAccessSettings.this.getActivity().getSystemService("layout_inflater");

    ListenerListAdapter(Context arg2)
    {
      super(0, 0);
    }

    public void bindView(View paramView, int paramInt)
    {
      NotificationAccessSettings.ViewHolder localViewHolder = (NotificationAccessSettings.ViewHolder)paramView.getTag();
      ServiceInfo localServiceInfo = (ServiceInfo)getItem(paramInt);
      localViewHolder.icon.setImageDrawable(localServiceInfo.loadIcon(NotificationAccessSettings.this.mPM));
      localViewHolder.name.setText(localServiceInfo.loadLabel(NotificationAccessSettings.this.mPM));
      localViewHolder.description.setVisibility(8);
      localViewHolder.checkbox.setChecked(NotificationAccessSettings.this.isListenerEnabled(localServiceInfo));
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

    public View newView(ViewGroup paramViewGroup)
    {
      View localView = this.mInflater.inflate(2130968656, paramViewGroup, false);
      NotificationAccessSettings.ViewHolder localViewHolder = new NotificationAccessSettings.ViewHolder();
      localViewHolder.icon = ((ImageView)localView.findViewById(2131230755));
      localViewHolder.name = ((TextView)localView.findViewById(2131230837));
      localViewHolder.checkbox = ((CheckBox)localView.findViewById(2131230839));
      localViewHolder.description = ((TextView)localView.findViewById(2131230838));
      localView.setTag(localViewHolder);
      return localView;
    }
  }

  public class ListenerWarningDialogFragment extends DialogFragment
  {
    public ListenerWarningDialogFragment()
    {
    }

    public Dialog onCreateDialog(Bundle paramBundle)
    {
      super.onCreate(paramBundle);
      Bundle localBundle = getArguments();
      String str1 = localBundle.getString("l");
      final ComponentName localComponentName = ComponentName.unflattenFromString(localBundle.getString("c"));
      String str2 = getResources().getString(2131427699, new Object[] { str1 });
      String str3 = getResources().getString(2131427700, new Object[] { str1 });
      return new AlertDialog.Builder(getActivity()).setMessage(str3).setTitle(str2).setIconAttribute(16843605).setCancelable(true).setPositiveButton(17039370, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          NotificationAccessSettings.this.mEnabledListeners.add(localComponentName);
          NotificationAccessSettings.this.saveEnabledListeners();
        }
      }).setNegativeButton(17039360, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
        }
      }).create();
    }

    public ListenerWarningDialogFragment setListenerInfo(ComponentName paramComponentName, String paramString)
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("c", paramComponentName.flattenToString());
      localBundle.putString("l", paramString);
      setArguments(localBundle);
      return this;
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
 * Qualified Name:     com.android.settings.NotificationAccessSettings
 * JD-Core Version:    0.6.2
 */