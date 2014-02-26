package com.android.settings;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.INotificationManager;
import android.app.INotificationManager.Stub;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.PreferenceFragment;
import android.service.notification.INotificationListener.Stub;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DateTimeView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NotificationStation extends SettingsPreferenceFragment
{
  private static final String TAG = NotificationStation.class.getSimpleName();
  private NotificationHistoryAdapter mAdapter;
  private Context mContext;
  private INotificationListener.Stub mListener = new INotificationListener.Stub()
  {
    public void onNotificationPosted(android.service.notification.StatusBarNotification paramAnonymousStatusBarNotification)
      throws RemoteException
    {
      Log.v(NotificationStation.TAG, "onNotificationPosted: " + paramAnonymousStatusBarNotification);
      Handler localHandler = NotificationStation.this.getListView().getHandler();
      localHandler.removeCallbacks(NotificationStation.this.mRefreshListRunnable);
      localHandler.postDelayed(NotificationStation.this.mRefreshListRunnable, 100L);
    }

    public void onNotificationRemoved(android.service.notification.StatusBarNotification paramAnonymousStatusBarNotification)
      throws RemoteException
    {
      Handler localHandler = NotificationStation.this.getListView().getHandler();
      localHandler.removeCallbacks(NotificationStation.this.mRefreshListRunnable);
      localHandler.postDelayed(NotificationStation.this.mRefreshListRunnable, 100L);
    }
  };
  private INotificationManager mNoMan;
  private final Comparator<HistoricalNotificationInfo> mNotificationSorter = new Comparator()
  {
    public int compare(NotificationStation.HistoricalNotificationInfo paramAnonymousHistoricalNotificationInfo1, NotificationStation.HistoricalNotificationInfo paramAnonymousHistoricalNotificationInfo2)
    {
      return (int)(paramAnonymousHistoricalNotificationInfo2.timestamp - paramAnonymousHistoricalNotificationInfo1.timestamp);
    }
  };
  private final PackageReceiver mPackageReceiver = new PackageReceiver(null);
  private PackageManager mPm;
  private Runnable mRefreshListRunnable = new Runnable()
  {
    public void run()
    {
      NotificationStation.this.refreshList();
    }
  };

  private Resources getResourcesForUserPackage(String paramString, int paramInt)
  {
    if (paramString != null)
      if (paramInt == -1)
        paramInt = 0;
    while (true)
    {
      try
      {
        Resources localResources2 = this.mPm.getResourcesForApplicationAsUser(paramString, paramInt);
        localResources1 = localResources2;
        return localResources1;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        Log.e(TAG, "Icon package not found: " + paramString);
        return null;
      }
      Resources localResources1 = this.mContext.getResources();
    }
  }

  private Drawable loadIconDrawable(String paramString, int paramInt1, int paramInt2)
  {
    Resources localResources = getResourcesForUserPackage(paramString, paramInt1);
    if (paramInt2 == 0)
      return null;
    String str;
    StringBuilder localStringBuilder;
    try
    {
      Drawable localDrawable = localResources.getDrawable(paramInt2);
      return localDrawable;
    }
    catch (RuntimeException localRuntimeException)
    {
      str = TAG;
      localStringBuilder = new StringBuilder().append("Icon not found in ");
      if (paramString == null);
    }
    for (Object localObject = Integer.valueOf(paramInt2); ; localObject = "<system>")
    {
      Log.w(str, localObject + ": " + Integer.toHexString(paramInt2));
      return null;
    }
  }

  private List<HistoricalNotificationInfo> loadNotifications()
  {
    int i = ActivityManager.getCurrentUser();
    ArrayList localArrayList;
    try
    {
      android.service.notification.StatusBarNotification[] arrayOfStatusBarNotification1 = this.mNoMan.getActiveNotifications(this.mContext.getPackageName());
      android.service.notification.StatusBarNotification[] arrayOfStatusBarNotification2 = this.mNoMan.getHistoricalNotifications(this.mContext.getPackageName(), 50);
      localArrayList = new ArrayList(arrayOfStatusBarNotification1.length + arrayOfStatusBarNotification2.length);
      for ([Landroid.service.notification.StatusBarNotification localStatusBarNotification; : new android.service.notification.StatusBarNotification[][] { arrayOfStatusBarNotification1, arrayOfStatusBarNotification2 })
      {
        int m = localStatusBarNotification;.length;
        int n = 0;
        if (n < m)
        {
          Object localObject = localStatusBarNotification;[n];
          HistoricalNotificationInfo localHistoricalNotificationInfo = new HistoricalNotificationInfo(null);
          localHistoricalNotificationInfo.pkg = localObject.getPackageName();
          localHistoricalNotificationInfo.user = localObject.getUserId();
          localHistoricalNotificationInfo.icon = loadIconDrawable(localHistoricalNotificationInfo.pkg, localHistoricalNotificationInfo.user, localObject.getNotification().icon);
          localHistoricalNotificationInfo.pkgicon = loadPackageIconDrawable(localHistoricalNotificationInfo.pkg, localHistoricalNotificationInfo.user);
          localHistoricalNotificationInfo.pkgname = loadPackageName(localHistoricalNotificationInfo.pkg);
          if (localObject.getNotification().extras != null)
          {
            localHistoricalNotificationInfo.title = localObject.getNotification().extras.getString("android.title");
            if ((localHistoricalNotificationInfo.title == null) || ("".equals(localHistoricalNotificationInfo.title)))
              localHistoricalNotificationInfo.title = localObject.getNotification().extras.getString("android.text");
          }
          if ((localHistoricalNotificationInfo.title == null) || ("".equals(localHistoricalNotificationInfo.title)))
            localHistoricalNotificationInfo.title = localObject.getNotification().tickerText;
          if ((localHistoricalNotificationInfo.title == null) || ("".equals(localHistoricalNotificationInfo.title)))
            localHistoricalNotificationInfo.title = localHistoricalNotificationInfo.pkgname;
          localHistoricalNotificationInfo.timestamp = localObject.getPostTime();
          localHistoricalNotificationInfo.priority = localObject.getNotification().priority;
          Object[] arrayOfObject = new Object[3];
          arrayOfObject[0] = Long.valueOf(localHistoricalNotificationInfo.timestamp);
          arrayOfObject[1] = localHistoricalNotificationInfo.pkg;
          arrayOfObject[2] = localHistoricalNotificationInfo.title;
          logd("   [%d] %s: %s", arrayOfObject);
          if (localStatusBarNotification; == arrayOfStatusBarNotification1);
          for (boolean bool = true; ; bool = false)
          {
            localHistoricalNotificationInfo.active = bool;
            if ((localHistoricalNotificationInfo.user == -1) || (localHistoricalNotificationInfo.user == i))
              localArrayList.add(localHistoricalNotificationInfo);
            n++;
            break;
          }
        }
      }
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.printStackTrace();
      localArrayList = null;
    }
    return localArrayList;
  }

  private Drawable loadPackageIconDrawable(String paramString, int paramInt)
  {
    try
    {
      Drawable localDrawable = this.mPm.getApplicationIcon(paramString);
      return localDrawable;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
    }
    return null;
  }

  private CharSequence loadPackageName(String paramString)
  {
    try
    {
      ApplicationInfo localApplicationInfo = this.mPm.getApplicationInfo(paramString, 8192);
      if (localApplicationInfo != null)
      {
        CharSequence localCharSequence = this.mPm.getApplicationLabel(localApplicationInfo);
        paramString = localCharSequence;
      }
      return paramString;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
    }
    return paramString;
  }

  private static void logd(String paramString, Object[] paramArrayOfObject)
  {
    String str = TAG;
    if ((paramArrayOfObject == null) || (paramArrayOfObject.length == 0));
    while (true)
    {
      Log.d(str, paramString);
      return;
      paramString = String.format(paramString, paramArrayOfObject);
    }
  }

  private void refreshList()
  {
    List localList = loadNotifications();
    if (localList != null)
    {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = Integer.valueOf(localList.size());
      logd("adding %d infos", arrayOfObject);
      this.mAdapter.clear();
      this.mAdapter.addAll(localList);
      this.mAdapter.sort(this.mNotificationSorter);
    }
  }

  private void startApplicationDetailsActivity(String paramString)
  {
    Intent localIntent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.fromParts("package", paramString, null));
    localIntent.setComponent(localIntent.resolveActivity(this.mPm));
    startActivity(localIntent);
  }

  public void onActivityCreated(Bundle paramBundle)
  {
    logd("onActivityCreated(%s)", new Object[] { paramBundle });
    super.onActivityCreated(paramBundle);
    ListView localListView = getListView();
    this.mAdapter = new NotificationHistoryAdapter(this.mContext);
    localListView.setAdapter(this.mAdapter);
  }

  public void onAttach(Activity paramActivity)
  {
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = paramActivity.getClass().getSimpleName();
    logd("onAttach(%s)", arrayOfObject);
    super.onAttach(paramActivity);
    this.mContext = paramActivity;
    this.mPm = this.mContext.getPackageManager();
    this.mNoMan = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
    try
    {
      this.mNoMan.registerListener(this.mListener, new ComponentName(this.mContext.getPackageName(), getClass().getCanonicalName()), ActivityManager.getCurrentUser());
      return;
    }
    catch (RemoteException localRemoteException)
    {
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    logd("onCreate(%s)", new Object[] { paramBundle });
    super.onCreate(paramBundle);
    getActivity();
  }

  public void onDestroyView()
  {
    super.onDestroyView();
  }

  public void onPause()
  {
    logd("onPause()", new Object[0]);
    super.onPause();
    this.mContext.unregisterReceiver(this.mPackageReceiver);
  }

  public void onResume()
  {
    logd("onResume()", new Object[0]);
    super.onResume();
    refreshList();
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.PACKAGE_ADDED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
    localIntentFilter.addDataScheme("package");
    this.mContext.registerReceiver(this.mPackageReceiver, localIntentFilter);
  }

  private static class HistoricalNotificationInfo
  {
    public boolean active;
    public Drawable icon;
    public String pkg;
    public Drawable pkgicon;
    public CharSequence pkgname;
    public int priority;
    public long timestamp;
    public CharSequence title;
    public int user;
  }

  private class NotificationHistoryAdapter extends ArrayAdapter<NotificationStation.HistoricalNotificationInfo>
  {
    private final LayoutInflater mInflater;

    public NotificationHistoryAdapter(Context arg2)
    {
      super(0);
      this.mInflater = ((LayoutInflater)localContext.getSystemService("layout_inflater"));
    }

    private View createRow(ViewGroup paramViewGroup)
    {
      return this.mInflater.inflate(2130968657, paramViewGroup, false);
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      final NotificationStation.HistoricalNotificationInfo localHistoricalNotificationInfo = (NotificationStation.HistoricalNotificationInfo)getItem(paramInt);
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = localHistoricalNotificationInfo.pkg;
      arrayOfObject[1] = localHistoricalNotificationInfo.title;
      NotificationStation.logd("getView(%s/%s)", arrayOfObject);
      View localView;
      if (paramView != null)
      {
        localView = paramView;
        localView.setTag(localHistoricalNotificationInfo);
        if (localHistoricalNotificationInfo.icon != null)
          ((ImageView)localView.findViewById(16908294)).setImageDrawable(localHistoricalNotificationInfo.icon);
        if (localHistoricalNotificationInfo.pkgicon != null)
          ((ImageView)localView.findViewById(2131230920)).setImageDrawable(localHistoricalNotificationInfo.pkgicon);
        ((DateTimeView)localView.findViewById(2131230921)).setTime(localHistoricalNotificationInfo.timestamp);
        ((TextView)localView.findViewById(16908310)).setText(localHistoricalNotificationInfo.title);
        ((TextView)localView.findViewById(2131230923)).setText(localHistoricalNotificationInfo.pkgname);
        localView.findViewById(2131230922).setVisibility(8);
        if (!localHistoricalNotificationInfo.active)
          break label219;
      }
      label219: for (float f = 1.0F; ; f = 0.5F)
      {
        localView.setAlpha(f);
        localView.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView.setPressed(true);
            NotificationStation.this.startApplicationDetailsActivity(localHistoricalNotificationInfo.pkg);
          }
        });
        return localView;
        localView = createRow(paramViewGroup);
        break;
      }
    }
  }

  private class PackageReceiver extends BroadcastReceiver
  {
    private PackageReceiver()
    {
    }

    public void onReceive(Context paramContext, Intent paramIntent)
    {
      NotificationStation.logd("PackageReceiver.onReceive", new Object[0]);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.NotificationStation
 * JD-Core Version:    0.6.2
 */