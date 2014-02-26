package com.android.settings.wfd;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources.Theme;
import android.database.ContentObserver;
import android.hardware.display.DisplayManager;
import android.hardware.display.WifiDisplay;
import android.hardware.display.WifiDisplaySessionInfo;
import android.hardware.display.WifiDisplayStatus;
import android.media.MediaRouter;
import android.media.MediaRouter.Callback;
import android.media.MediaRouter.RouteInfo;
import android.media.MediaRouter.SimpleCallback;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.Settings.Global;
import android.util.Slog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.android.internal.app.MediaRouteDialogPresenter;
import com.android.settings.SettingsPreferenceFragment;

public final class WifiDisplaySettings extends SettingsPreferenceFragment
{
  private boolean mAutoGO;
  private PreferenceGroup mCertCategory;
  private DisplayManager mDisplayManager;
  private TextView mEmptyView;
  private final Handler mHandler = new Handler();
  private boolean mListen;
  private int mListenChannel;
  private int mOperatingChannel;
  private int mPendingChanges;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (paramAnonymousIntent.getAction().equals("android.hardware.display.action.WIFI_DISPLAY_STATUS_CHANGED"))
        WifiDisplaySettings.this.scheduleUpdate(4);
    }
  };
  private MediaRouter mRouter;
  private final MediaRouter.Callback mRouterCallback = new MediaRouter.SimpleCallback()
  {
    public void onRouteAdded(MediaRouter paramAnonymousMediaRouter, MediaRouter.RouteInfo paramAnonymousRouteInfo)
    {
      WifiDisplaySettings.this.scheduleUpdate(2);
    }

    public void onRouteChanged(MediaRouter paramAnonymousMediaRouter, MediaRouter.RouteInfo paramAnonymousRouteInfo)
    {
      WifiDisplaySettings.this.scheduleUpdate(2);
    }

    public void onRouteRemoved(MediaRouter paramAnonymousMediaRouter, MediaRouter.RouteInfo paramAnonymousRouteInfo)
    {
      WifiDisplaySettings.this.scheduleUpdate(2);
    }

    public void onRouteSelected(MediaRouter paramAnonymousMediaRouter, int paramAnonymousInt, MediaRouter.RouteInfo paramAnonymousRouteInfo)
    {
      WifiDisplaySettings.this.scheduleUpdate(2);
    }

    public void onRouteUnselected(MediaRouter paramAnonymousMediaRouter, int paramAnonymousInt, MediaRouter.RouteInfo paramAnonymousRouteInfo)
    {
      WifiDisplaySettings.this.scheduleUpdate(2);
    }
  };
  private final ContentObserver mSettingsObserver = new ContentObserver(new Handler())
  {
    public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
    {
      WifiDisplaySettings.this.scheduleUpdate(1);
    }
  };
  private boolean mStarted;
  private final Runnable mUpdateRunnable = new Runnable()
  {
    public void run()
    {
      int i = WifiDisplaySettings.this.mPendingChanges;
      WifiDisplaySettings.access$1002(WifiDisplaySettings.this, 0);
      WifiDisplaySettings.this.update(i);
    }
  };
  private boolean mWifiDisplayCertificationOn;
  private boolean mWifiDisplayOnSetting;
  private WifiDisplayStatus mWifiDisplayStatus;
  private WifiP2pManager.Channel mWifiP2pChannel;
  private WifiP2pManager mWifiP2pManager;
  private int mWpsConfig = 4;

  private void buildCertificationMenu(PreferenceScreen paramPreferenceScreen)
  {
    if (this.mCertCategory == null)
    {
      this.mCertCategory = new PreferenceCategory(getActivity());
      this.mCertCategory.setTitle(2131427789);
      this.mCertCategory.setOrder(1);
    }
    while (true)
    {
      paramPreferenceScreen.addPreference(this.mCertCategory);
      if (!this.mWifiDisplayStatus.getSessionInfo().getGroupId().isEmpty())
      {
        Preference localPreference = new Preference(getActivity());
        localPreference.setTitle(2131427790);
        localPreference.setSummary(this.mWifiDisplayStatus.getSessionInfo().toString());
        this.mCertCategory.addPreference(localPreference);
        if (this.mWifiDisplayStatus.getSessionInfo().getSessionId() != 0)
          this.mCertCategory.addPreference(new Preference(getActivity())
          {
            public View getView(View paramAnonymousView, ViewGroup paramAnonymousViewGroup)
            {
              if (paramAnonymousView == null);
              for (View localView = ((LayoutInflater)WifiDisplaySettings.this.getActivity().getSystemService("layout_inflater")).inflate(2130968719, null); ; localView = paramAnonymousView)
              {
                Button localButton1 = (Button)localView.findViewById(2131231089);
                localButton1.setText(2131427793);
                localButton1.setOnClickListener(new View.OnClickListener()
                {
                  public void onClick(View paramAnonymous2View)
                  {
                    WifiDisplaySettings.this.mDisplayManager.pauseWifiDisplay();
                  }
                });
                Button localButton2 = (Button)localView.findViewById(2131231090);
                localButton2.setText(2131427794);
                localButton2.setOnClickListener(new View.OnClickListener()
                {
                  public void onClick(View paramAnonymous2View)
                  {
                    WifiDisplaySettings.this.mDisplayManager.resumeWifiDisplay();
                  }
                });
                return localView;
              }
            }
          });
      }
      CheckBoxPreference local2 = new CheckBoxPreference(getActivity())
      {
        protected void onClick()
        {
          WifiDisplaySettings localWifiDisplaySettings = WifiDisplaySettings.this;
          if (!WifiDisplaySettings.this.mListen);
          for (boolean bool = true; ; bool = false)
          {
            WifiDisplaySettings.access$102(localWifiDisplaySettings, bool);
            WifiDisplaySettings.this.setListenMode(WifiDisplaySettings.this.mListen);
            setChecked(WifiDisplaySettings.this.mListen);
            return;
          }
        }
      };
      local2.setTitle(2131427791);
      local2.setChecked(this.mListen);
      this.mCertCategory.addPreference(local2);
      CheckBoxPreference local3 = new CheckBoxPreference(getActivity())
      {
        protected void onClick()
        {
          WifiDisplaySettings localWifiDisplaySettings = WifiDisplaySettings.this;
          boolean bool;
          if (!WifiDisplaySettings.this.mAutoGO)
          {
            bool = true;
            WifiDisplaySettings.access$302(localWifiDisplaySettings, bool);
            if (!WifiDisplaySettings.this.mAutoGO)
              break label57;
            WifiDisplaySettings.this.startAutoGO();
          }
          while (true)
          {
            setChecked(WifiDisplaySettings.this.mAutoGO);
            return;
            bool = false;
            break;
            label57: WifiDisplaySettings.this.stopAutoGO();
          }
        }
      };
      local3.setTitle(2131427792);
      local3.setChecked(this.mAutoGO);
      this.mCertCategory.addPreference(local3);
      ListPreference local4 = new ListPreference(getActivity())
      {
        protected void onDialogClosed(boolean paramAnonymousBoolean)
        {
          super.onDialogClosed(paramAnonymousBoolean);
          if (paramAnonymousBoolean)
          {
            WifiDisplaySettings.access$602(WifiDisplaySettings.this, Integer.parseInt(getValue()));
            setSummary("%1$s");
            WifiDisplaySettings.this.getActivity().invalidateOptionsMenu();
            Settings.Global.putInt(WifiDisplaySettings.this.getActivity().getContentResolver(), "wifi_display_wps_config", WifiDisplaySettings.this.mWpsConfig);
          }
        }
      };
      this.mWpsConfig = Settings.Global.getInt(getActivity().getContentResolver(), "wifi_display_wps_config", 4);
      String[] arrayOfString1 = { "Default", "PBC", "KEYPAD", "DISPLAY" };
      String[] arrayOfString2 = { "4", "0", "2", "1" };
      local4.setTitle(2131427795);
      local4.setEntries(arrayOfString1);
      local4.setEntryValues(arrayOfString2);
      local4.setValue("" + this.mWpsConfig);
      local4.setSummary("%1$s");
      this.mCertCategory.addPreference(local4);
      ListPreference local5 = new ListPreference(getActivity())
      {
        protected void onDialogClosed(boolean paramAnonymousBoolean)
        {
          super.onDialogClosed(paramAnonymousBoolean);
          if (paramAnonymousBoolean)
          {
            WifiDisplaySettings.access$702(WifiDisplaySettings.this, Integer.parseInt(getValue()));
            setSummary("%1$s");
            WifiDisplaySettings.this.getActivity().invalidateOptionsMenu();
            WifiDisplaySettings.this.setWifiP2pChannels(WifiDisplaySettings.this.mListenChannel, WifiDisplaySettings.this.mOperatingChannel);
          }
        }
      };
      String[] arrayOfString3 = { "Auto", "1", "6", "11" };
      String[] arrayOfString4 = { "0", "1", "6", "11" };
      local5.setTitle(2131427796);
      local5.setEntries(arrayOfString3);
      local5.setEntryValues(arrayOfString4);
      local5.setValue("" + this.mListenChannel);
      local5.setSummary("%1$s");
      this.mCertCategory.addPreference(local5);
      ListPreference local6 = new ListPreference(getActivity())
      {
        protected void onDialogClosed(boolean paramAnonymousBoolean)
        {
          super.onDialogClosed(paramAnonymousBoolean);
          if (paramAnonymousBoolean)
          {
            WifiDisplaySettings.access$802(WifiDisplaySettings.this, Integer.parseInt(getValue()));
            setSummary("%1$s");
            WifiDisplaySettings.this.getActivity().invalidateOptionsMenu();
            WifiDisplaySettings.this.setWifiP2pChannels(WifiDisplaySettings.this.mListenChannel, WifiDisplaySettings.this.mOperatingChannel);
          }
        }
      };
      String[] arrayOfString5 = { "Auto", "1", "6", "11", "36" };
      String[] arrayOfString6 = { "0", "1", "6", "11", "36" };
      local6.setTitle(2131427797);
      local6.setEntries(arrayOfString5);
      local6.setEntryValues(arrayOfString6);
      local6.setValue("" + this.mOperatingChannel);
      local6.setSummary("%1$s");
      this.mCertCategory.addPreference(local6);
      return;
      this.mCertCategory.removeAll();
    }
  }

  private RoutePreference createRoutePreference(MediaRouter.RouteInfo paramRouteInfo)
  {
    WifiDisplay localWifiDisplay = findWifiDisplay(paramRouteInfo.getDeviceAddress());
    if (localWifiDisplay != null)
      return new WifiDisplayRoutePreference(getActivity(), paramRouteInfo, localWifiDisplay);
    return new RoutePreference(getActivity(), paramRouteInfo);
  }

  private WifiDisplay findWifiDisplay(String paramString)
  {
    if ((this.mWifiDisplayStatus != null) && (paramString != null))
      for (WifiDisplay localWifiDisplay : this.mWifiDisplayStatus.getDisplays())
        if (localWifiDisplay.getDeviceAddress().equals(paramString))
          return localWifiDisplay;
    return null;
  }

  private void pairWifiDisplay(WifiDisplay paramWifiDisplay)
  {
    if (paramWifiDisplay.canConnect())
      this.mDisplayManager.connectWifiDisplay(paramWifiDisplay.getDeviceAddress());
  }

  private void scheduleUpdate(int paramInt)
  {
    if (this.mStarted)
    {
      if (this.mPendingChanges == 0)
        this.mHandler.post(this.mUpdateRunnable);
      this.mPendingChanges = (paramInt | this.mPendingChanges);
    }
  }

  private void setListenMode(final boolean paramBoolean)
  {
    this.mWifiP2pManager.listen(this.mWifiP2pChannel, paramBoolean, new WifiP2pManager.ActionListener()
    {
      public void onFailure(int paramAnonymousInt)
      {
        StringBuilder localStringBuilder = new StringBuilder().append("Failed to ");
        if (paramBoolean);
        for (String str = "entered"; ; str = "exited")
        {
          Slog.e("WifiDisplaySettings", str + " listen mode with reason " + paramAnonymousInt + ".");
          return;
        }
      }

      public void onSuccess()
      {
      }
    });
  }

  private void setWifiP2pChannels(int paramInt1, int paramInt2)
  {
    this.mWifiP2pManager.setWifiP2pChannels(this.mWifiP2pChannel, paramInt1, paramInt2, new WifiP2pManager.ActionListener()
    {
      public void onFailure(int paramAnonymousInt)
      {
        Slog.e("WifiDisplaySettings", "Failed to set wifi p2p channels with reason " + paramAnonymousInt + ".");
      }

      public void onSuccess()
      {
      }
    });
  }

  private void showWifiDisplayOptionsDialog(final WifiDisplay paramWifiDisplay)
  {
    View localView = getActivity().getLayoutInflater().inflate(2130968738, null);
    final EditText localEditText = (EditText)localView.findViewById(2131230837);
    localEditText.setText(paramWifiDisplay.getFriendlyDisplayName());
    DialogInterface.OnClickListener local11 = new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        String str = localEditText.getText().toString().trim();
        if ((str.isEmpty()) || (str.equals(paramWifiDisplay.getDeviceName())))
          str = null;
        WifiDisplaySettings.this.mDisplayManager.renameWifiDisplay(paramWifiDisplay.getDeviceAddress(), str);
      }
    };
    DialogInterface.OnClickListener local12 = new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        WifiDisplaySettings.this.mDisplayManager.forgetWifiDisplay(paramWifiDisplay.getDeviceAddress());
      }
    };
    new AlertDialog.Builder(getActivity()).setCancelable(true).setTitle(2131427785).setView(localView).setPositiveButton(2131427787, local11).setNegativeButton(2131427786, local12).create().show();
  }

  private void startAutoGO()
  {
    this.mWifiP2pManager.createGroup(this.mWifiP2pChannel, new WifiP2pManager.ActionListener()
    {
      public void onFailure(int paramAnonymousInt)
      {
        Slog.e("WifiDisplaySettings", "Failed to start AutoGO with reason " + paramAnonymousInt + ".");
      }

      public void onSuccess()
      {
      }
    });
  }

  private void stopAutoGO()
  {
    this.mWifiP2pManager.removeGroup(this.mWifiP2pChannel, new WifiP2pManager.ActionListener()
    {
      public void onFailure(int paramAnonymousInt)
      {
        Slog.e("WifiDisplaySettings", "Failed to stop AutoGO with reason " + paramAnonymousInt + ".");
      }

      public void onSuccess()
      {
      }
    });
  }

  private void toggleRoute(MediaRouter.RouteInfo paramRouteInfo)
  {
    if (paramRouteInfo.isSelected())
    {
      MediaRouteDialogPresenter.showDialogFragment(getActivity(), 4, null);
      return;
    }
    paramRouteInfo.select();
  }

  private void unscheduleUpdate()
  {
    if (this.mPendingChanges != 0)
    {
      this.mPendingChanges = 0;
      this.mHandler.removeCallbacks(this.mUpdateRunnable);
    }
  }

  private void update(int paramInt)
  {
    boolean bool1 = true;
    int i = paramInt & 0x1;
    int j = 0;
    boolean bool2;
    if (i != 0)
    {
      if (Settings.Global.getInt(getContentResolver(), "wifi_display_on", 0) == 0)
        break label160;
      bool2 = bool1;
      this.mWifiDisplayOnSetting = bool2;
      if (Settings.Global.getInt(getContentResolver(), "wifi_display_certification_on", 0) == 0)
        break label166;
    }
    PreferenceScreen localPreferenceScreen;
    while (true)
    {
      this.mWifiDisplayCertificationOn = bool1;
      this.mWpsConfig = Settings.Global.getInt(getContentResolver(), "wifi_display_wps_config", 4);
      j = 1;
      if ((paramInt & 0x4) != 0)
      {
        this.mWifiDisplayStatus = this.mDisplayManager.getWifiDisplayStatus();
        j = 1;
      }
      localPreferenceScreen = getPreferenceScreen();
      localPreferenceScreen.removeAll();
      int k = this.mRouter.getRouteCount();
      for (int m = 0; m < k; m++)
      {
        MediaRouter.RouteInfo localRouteInfo = this.mRouter.getRouteAt(m);
        if (localRouteInfo.matchesTypes(4))
          localPreferenceScreen.addPreference(createRoutePreference(localRouteInfo));
      }
      label160: bool2 = false;
      break;
      label166: bool1 = false;
    }
    if ((this.mWifiDisplayStatus != null) && (this.mWifiDisplayStatus.getFeatureState() == 3))
    {
      for (WifiDisplay localWifiDisplay : this.mWifiDisplayStatus.getDisplays())
        if ((!localWifiDisplay.isRemembered()) && (localWifiDisplay.isAvailable()) && (!localWifiDisplay.equals(this.mWifiDisplayStatus.getActiveDisplay())))
          localPreferenceScreen.addPreference(new UnpairedWifiDisplayPreference(getActivity(), localWifiDisplay));
      if (this.mWifiDisplayCertificationOn)
        buildCertificationMenu(localPreferenceScreen);
    }
    if (j != 0)
      getActivity().invalidateOptionsMenu();
  }

  protected int getHelpResource()
  {
    return 2131429270;
  }

  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    this.mEmptyView = ((TextView)getView().findViewById(16908292));
    this.mEmptyView.setText(2131427779);
    getListView().setEmptyView(this.mEmptyView);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Activity localActivity = getActivity();
    this.mRouter = ((MediaRouter)localActivity.getSystemService("media_router"));
    this.mDisplayManager = ((DisplayManager)localActivity.getSystemService("display"));
    this.mWifiP2pManager = ((WifiP2pManager)localActivity.getSystemService("wifip2p"));
    this.mWifiP2pChannel = this.mWifiP2pManager.initialize(localActivity, Looper.getMainLooper(), null);
    addPreferencesFromResource(2131034174);
    setHasOptionsMenu(true);
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    if ((this.mWifiDisplayStatus != null) && (this.mWifiDisplayStatus.getFeatureState() != 0))
    {
      MenuItem localMenuItem = paramMenu.add(0, 1, 0, 2131427778);
      localMenuItem.setCheckable(true);
      localMenuItem.setChecked(this.mWifiDisplayOnSetting);
    }
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default:
      return super.onOptionsItemSelected(paramMenuItem);
    case 1:
    }
    if (!paramMenuItem.isChecked());
    for (boolean bool1 = true; ; bool1 = false)
    {
      this.mWifiDisplayOnSetting = bool1;
      paramMenuItem.setChecked(this.mWifiDisplayOnSetting);
      ContentResolver localContentResolver = getContentResolver();
      boolean bool2 = this.mWifiDisplayOnSetting;
      int i = 0;
      if (bool2)
        i = 1;
      Settings.Global.putInt(localContentResolver, "wifi_display_on", i);
      return true;
    }
  }

  public void onStart()
  {
    super.onStart();
    this.mStarted = true;
    Activity localActivity = getActivity();
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.hardware.display.action.WIFI_DISPLAY_STATUS_CHANGED");
    localActivity.registerReceiver(this.mReceiver, localIntentFilter);
    getContentResolver().registerContentObserver(Settings.Global.getUriFor("wifi_display_on"), false, this.mSettingsObserver);
    getContentResolver().registerContentObserver(Settings.Global.getUriFor("wifi_display_certification_on"), false, this.mSettingsObserver);
    getContentResolver().registerContentObserver(Settings.Global.getUriFor("wifi_display_wps_config"), false, this.mSettingsObserver);
    this.mRouter.addCallback(4, this.mRouterCallback, 1);
    update(-1);
  }

  public void onStop()
  {
    super.onStop();
    this.mStarted = false;
    getActivity().unregisterReceiver(this.mReceiver);
    getContentResolver().unregisterContentObserver(this.mSettingsObserver);
    this.mRouter.removeCallback(this.mRouterCallback);
    unscheduleUpdate();
  }

  private class RoutePreference extends Preference
    implements Preference.OnPreferenceClickListener
  {
    private final MediaRouter.RouteInfo mRoute;

    public RoutePreference(Context paramRouteInfo, MediaRouter.RouteInfo arg3)
    {
      super();
      Object localObject;
      this.mRoute = localObject;
      setTitle(localObject.getName());
      setSummary(localObject.getDescription());
      setEnabled(localObject.isEnabled());
      if (localObject.isSelected())
      {
        setOrder(2);
        if (localObject.isConnecting())
          setSummary(2131427780);
      }
      while (true)
      {
        setOnPreferenceClickListener(this);
        return;
        setSummary(2131427781);
        continue;
        if (isEnabled())
        {
          setOrder(3);
        }
        else
        {
          setOrder(4);
          if (localObject.getStatusCode() == 5)
            setSummary(2131427782);
          else
            setSummary(2131427783);
        }
      }
    }

    public boolean onPreferenceClick(Preference paramPreference)
    {
      WifiDisplaySettings.this.toggleRoute(this.mRoute);
      return true;
    }
  }

  private class UnpairedWifiDisplayPreference extends Preference
    implements Preference.OnPreferenceClickListener
  {
    private final WifiDisplay mDisplay;

    public UnpairedWifiDisplayPreference(Context paramWifiDisplay, WifiDisplay arg3)
    {
      super();
      Object localObject;
      this.mDisplay = localObject;
      setTitle(localObject.getFriendlyDisplayName());
      setSummary(17040709);
      setEnabled(localObject.canConnect());
      if (isEnabled())
        setOrder(3);
      while (true)
      {
        setOnPreferenceClickListener(this);
        return;
        setOrder(4);
        setSummary(2131427782);
      }
    }

    public boolean onPreferenceClick(Preference paramPreference)
    {
      WifiDisplaySettings.this.pairWifiDisplay(this.mDisplay);
      return true;
    }
  }

  private class WifiDisplayRoutePreference extends WifiDisplaySettings.RoutePreference
    implements View.OnClickListener
  {
    private final WifiDisplay mDisplay;

    public WifiDisplayRoutePreference(Context paramRouteInfo, MediaRouter.RouteInfo paramWifiDisplay, WifiDisplay arg4)
    {
      super(paramRouteInfo, paramWifiDisplay);
      Object localObject;
      this.mDisplay = localObject;
      setWidgetLayoutResource(2130968739);
    }

    protected void onBindView(View paramView)
    {
      super.onBindView(paramView);
      ImageView localImageView = (ImageView)paramView.findViewById(2131230938);
      if (localImageView != null)
      {
        localImageView.setOnClickListener(this);
        if (!isEnabled())
        {
          TypedValue localTypedValue = new TypedValue();
          getContext().getTheme().resolveAttribute(16842803, localTypedValue, true);
          localImageView.setImageAlpha((int)(255.0F * localTypedValue.getFloat()));
          localImageView.setEnabled(true);
        }
      }
    }

    public void onClick(View paramView)
    {
      WifiDisplaySettings.this.showWifiDisplayOptionsDialog(this.mDisplay);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.wfd.WifiDisplaySettings
 * JD-Core Version:    0.6.2
 */