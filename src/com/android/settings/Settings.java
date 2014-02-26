package com.android.settings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ListActivity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.INetworkManagementService;
import android.os.INetworkManagementService.Stub;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceActivity.Header;
import android.preference.PreferenceFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import com.android.internal.util.ArrayUtils;
import com.android.settings.accessibility.AccessibilitySettings;
import com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment;
import com.android.settings.accessibility.ToggleCaptioningPreferenceFragment;
import com.android.settings.accounts.AccountSyncSettings;
import com.android.settings.accounts.AuthenticatorHelper;
import com.android.settings.accounts.ManageAccountsSettings;
import com.android.settings.applications.ManageApplications;
import com.android.settings.applications.ProcessStatsUi;
import com.android.settings.bluetooth.BluetoothEnabler;
import com.android.settings.bluetooth.BluetoothSettings;
import com.android.settings.deviceinfo.Memory;
import com.android.settings.deviceinfo.UsbSettings;
import com.android.settings.fuelgauge.PowerUsageSummary;
import com.android.settings.inputmethod.InputMethodAndLanguageSettings;
import com.android.settings.inputmethod.KeyboardLayoutPickerFragment;
import com.android.settings.inputmethod.SpellCheckersSettings;
import com.android.settings.inputmethod.UserDictionaryList;
import com.android.settings.location.LocationSettings;
import com.android.settings.nfc.AndroidBeam;
import com.android.settings.nfc.PaymentSettings;
import com.android.settings.print.PrintJobSettingsFragment;
import com.android.settings.print.PrintServiceSettingsFragment;
import com.android.settings.print.PrintSettingsFragment;
import com.android.settings.tts.TextToSpeechSettings;
import com.android.settings.users.UserSettings;
import com.android.settings.vpn2.VpnSettings;
import com.android.settings.wfd.WifiDisplaySettings;
import com.android.settings.wifi.AdvancedWifiSettings;
import com.android.settings.wifi.WifiEnabler;
import com.android.settings.wifi.WifiSettings;
import com.android.settings.wifi.p2p.WifiP2pSettings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Settings extends PreferenceActivity
  implements OnAccountsUpdateListener, ButtonBarHandler
{
  private static final String[] ENTRY_FRAGMENTS = arrayOfString;
  private static boolean sShowNoHomeNotice = false;
  private int[] SETTINGS_FOR_RESTRICTED = { 2131231228, 2131231229, 2131231230, 2131231231, 2131231233, 2131231234, 2131231236, 2131231237, 2131231238, 2131231240, 2131231239, 2131231244, 2131231245, 2131231246, 2131231247, 2131231241, 2131231249, 2131231250, 2131231251, 2131231252, 2131231256, 2131231253, 2131231254, 2131231242, 2131231235 };
  private AuthenticatorHelper mAuthenticatorHelper;
  private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.intent.action.BATTERY_CHANGED".equals(paramAnonymousIntent.getAction()))
      {
        boolean bool = Utils.isBatteryPresent(paramAnonymousIntent);
        if (Settings.this.mBatteryPresent != bool)
        {
          Settings.access$002(Settings.this, bool);
          Settings.this.invalidateHeaders();
        }
      }
    }
  };
  private boolean mBatteryPresent = true;
  private PreferenceActivity.Header mCurrentHeader;
  private SharedPreferences mDevelopmentPreferences;
  private SharedPreferences.OnSharedPreferenceChangeListener mDevelopmentPreferencesListener;
  private PreferenceActivity.Header mFirstHeader;
  private String mFragmentClass;
  protected HashMap<Integer, Integer> mHeaderIndexMap = new HashMap();
  private boolean mInLocalHeaderSwitch;
  private PreferenceActivity.Header mLastHeader;
  private boolean mListeningToAccountUpdates;
  private PreferenceActivity.Header mParentHeader;
  private int mTopLevelHeaderId;

  static
  {
    String[] arrayOfString = new String[44];
    arrayOfString[0] = WirelessSettings.class.getName();
    arrayOfString[1] = WifiSettings.class.getName();
    arrayOfString[2] = AdvancedWifiSettings.class.getName();
    arrayOfString[3] = BluetoothSettings.class.getName();
    arrayOfString[4] = TetherSettings.class.getName();
    arrayOfString[5] = WifiP2pSettings.class.getName();
    arrayOfString[6] = VpnSettings.class.getName();
    arrayOfString[7] = DateTimeSettings.class.getName();
    arrayOfString[8] = LocalePicker.class.getName();
    arrayOfString[9] = InputMethodAndLanguageSettings.class.getName();
    arrayOfString[10] = SpellCheckersSettings.class.getName();
    arrayOfString[11] = UserDictionaryList.class.getName();
    arrayOfString[12] = UserDictionarySettings.class.getName();
    arrayOfString[13] = SoundSettings.class.getName();
    arrayOfString[14] = DisplaySettings.class.getName();
    arrayOfString[15] = DeviceInfoSettings.class.getName();
    arrayOfString[16] = ManageApplications.class.getName();
    arrayOfString[17] = ProcessStatsUi.class.getName();
    arrayOfString[18] = NotificationStation.class.getName();
    arrayOfString[19] = LocationSettings.class.getName();
    arrayOfString[20] = SecuritySettings.class.getName();
    arrayOfString[21] = PrivacySettings.class.getName();
    arrayOfString[22] = DeviceAdminSettings.class.getName();
    arrayOfString[23] = AccessibilitySettings.class.getName();
    arrayOfString[24] = ToggleCaptioningPreferenceFragment.class.getName();
    arrayOfString[25] = TextToSpeechSettings.class.getName();
    arrayOfString[26] = Memory.class.getName();
    arrayOfString[27] = DevelopmentSettings.class.getName();
    arrayOfString[28] = UsbSettings.class.getName();
    arrayOfString[29] = AndroidBeam.class.getName();
    arrayOfString[30] = WifiDisplaySettings.class.getName();
    arrayOfString[31] = PowerUsageSummary.class.getName();
    arrayOfString[32] = AccountSyncSettings.class.getName();
    arrayOfString[33] = CryptKeeperSettings.class.getName();
    arrayOfString[34] = DataUsageSummary.class.getName();
    arrayOfString[35] = DreamSettings.class.getName();
    arrayOfString[36] = UserSettings.class.getName();
    arrayOfString[37] = NotificationAccessSettings.class.getName();
    arrayOfString[38] = ManageAccountsSettings.class.getName();
    arrayOfString[39] = PrintSettingsFragment.class.getName();
    arrayOfString[40] = PrintJobSettingsFragment.class.getName();
    arrayOfString[41] = TrustedCredentialsSettings.class.getName();
    arrayOfString[42] = PaymentSettings.class.getName();
    arrayOfString[43] = KeyboardLayoutPickerFragment.class.getName();
  }

  private void getMetaData()
  {
    try
    {
      ActivityInfo localActivityInfo = getPackageManager().getActivityInfo(getComponentName(), 128);
      if (localActivityInfo != null)
      {
        if (localActivityInfo.metaData == null)
          return;
        this.mTopLevelHeaderId = localActivityInfo.metaData.getInt("com.android.settings.TOP_LEVEL_HEADER_ID");
        this.mFragmentClass = localActivityInfo.metaData.getString("com.android.settings.FRAGMENT_CLASS");
        int i = localActivityInfo.metaData.getInt("com.android.settings.PARENT_FRAGMENT_TITLE");
        String str = localActivityInfo.metaData.getString("com.android.settings.PARENT_FRAGMENT_CLASS");
        if (str != null)
        {
          this.mParentHeader = new PreferenceActivity.Header();
          this.mParentHeader.fragment = str;
          if (i != 0)
          {
            this.mParentHeader.title = getResources().getString(i);
            return;
          }
        }
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
    }
  }

  private void highlightHeader(int paramInt)
  {
    if (paramInt != 0)
    {
      Integer localInteger = (Integer)this.mHeaderIndexMap.get(Integer.valueOf(paramInt));
      if (localInteger != null)
      {
        getListView().setItemChecked(localInteger.intValue(), true);
        if (isMultiPane())
          getListView().smoothScrollToPosition(localInteger.intValue());
      }
    }
  }

  private int insertAccountsHeaders(List<PreferenceActivity.Header> paramList, int paramInt)
  {
    String[] arrayOfString = this.mAuthenticatorHelper.getEnabledAccountTypes();
    ArrayList localArrayList = new ArrayList(arrayOfString.length);
    int i = arrayOfString.length;
    int j = 0;
    while (j < i)
    {
      String str = arrayOfString[j];
      CharSequence localCharSequence = this.mAuthenticatorHelper.getLabelForType(this, str);
      if (localCharSequence == null)
      {
        j++;
      }
      else
      {
        Account[] arrayOfAccount = AccountManager.get(this).getAccountsByType(str);
        int m;
        label95: PreferenceActivity.Header localHeader2;
        if ((arrayOfAccount.length == 1) && (!this.mAuthenticatorHelper.hasAccountPreferences(str)))
        {
          m = 1;
          localHeader2 = new PreferenceActivity.Header();
          localHeader2.title = localCharSequence;
          if (localHeader2.extras == null)
            localHeader2.extras = new Bundle();
          if (m == 0)
            break label246;
          localHeader2.breadCrumbTitleRes = 2131428974;
          localHeader2.breadCrumbShortTitleRes = 2131428974;
          localHeader2.fragment = AccountSyncSettings.class.getName();
          localHeader2.fragmentArguments = new Bundle();
          localHeader2.extras.putString("account_type", str);
          localHeader2.extras.putParcelable("account", arrayOfAccount[0]);
          localHeader2.fragmentArguments.putParcelable("account", arrayOfAccount[0]);
        }
        while (true)
        {
          localArrayList.add(localHeader2);
          this.mAuthenticatorHelper.preloadDrawableForType(this, str);
          break;
          m = 0;
          break label95;
          label246: localHeader2.breadCrumbTitle = localCharSequence;
          localHeader2.breadCrumbShortTitle = localCharSequence;
          localHeader2.fragment = ManageAccountsSettings.class.getName();
          localHeader2.fragmentArguments = new Bundle();
          localHeader2.extras.putString("account_type", str);
          localHeader2.fragmentArguments.putString("account_type", str);
          if (!isMultiPane())
            localHeader2.fragmentArguments.putString("account_label", localCharSequence.toString());
        }
      }
    }
    Collections.sort(localArrayList, new Comparator()
    {
      public int compare(PreferenceActivity.Header paramAnonymousHeader1, PreferenceActivity.Header paramAnonymousHeader2)
      {
        return paramAnonymousHeader1.title.toString().compareTo(paramAnonymousHeader2.title.toString());
      }
    });
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      PreferenceActivity.Header localHeader1 = (PreferenceActivity.Header)localIterator.next();
      int k = paramInt + 1;
      paramList.add(paramInt, localHeader1);
      paramInt = k;
    }
    if (!this.mListeningToAccountUpdates)
    {
      AccountManager.get(this).addOnAccountsUpdatedListener(this, null, true);
      this.mListeningToAccountUpdates = true;
    }
    return paramInt;
  }

  public static void requestHomeNotice()
  {
    sShowNoHomeNotice = true;
  }

  private void switchToHeaderLocal(PreferenceActivity.Header paramHeader)
  {
    this.mInLocalHeaderSwitch = true;
    switchToHeader(paramHeader);
    this.mInLocalHeaderSwitch = false;
  }

  private void switchToParent(String paramString)
  {
    ComponentName localComponentName = new ComponentName(this, paramString);
    try
    {
      PackageManager localPackageManager = getPackageManager();
      ActivityInfo localActivityInfo = localPackageManager.getActivityInfo(localComponentName, 128);
      if ((localActivityInfo != null) && (localActivityInfo.metaData != null))
      {
        String str = localActivityInfo.metaData.getString("com.android.settings.FRAGMENT_CLASS");
        CharSequence localCharSequence = localActivityInfo.loadLabel(localPackageManager);
        PreferenceActivity.Header localHeader = new PreferenceActivity.Header();
        localHeader.fragment = str;
        localHeader.title = localCharSequence;
        this.mCurrentHeader = localHeader;
        switchToHeaderLocal(localHeader);
        highlightHeader(this.mTopLevelHeaderId);
        this.mParentHeader = new PreferenceActivity.Header();
        this.mParentHeader.fragment = localActivityInfo.metaData.getString("com.android.settings.PARENT_FRAGMENT_CLASS");
        this.mParentHeader.title = localActivityInfo.metaData.getString("com.android.settings.PARENT_FRAGMENT_TITLE");
      }
      return;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.w("Settings", "Could not find parent activity : " + paramString);
    }
  }

  private void updateHeaderList(List<PreferenceActivity.Header> paramList)
  {
    boolean bool = this.mDevelopmentPreferences.getBoolean("show", Build.TYPE.equals("eng"));
    int i = 0;
    UserManager localUserManager = (UserManager)getSystemService("user");
    this.mHeaderIndexMap.clear();
    if (i < paramList.size())
    {
      PreferenceActivity.Header localHeader = (PreferenceActivity.Header)paramList.get(i);
      int j = (int)localHeader.id;
      if ((j == 2131231232) || (j == 2131231243))
        Utils.updateHeaderToSpecificActivityFromMetaDataOrRemove(this, paramList, localHeader);
      while (true)
      {
        if ((i < paramList.size()) && (paramList.get(i) == localHeader) && (UserHandle.myUserId() != 0) && (!ArrayUtils.contains(this.SETTINGS_FOR_RESTRICTED, j)))
          paramList.remove(i);
        if ((i >= paramList.size()) || (paramList.get(i) != localHeader))
          break;
        if ((this.mFirstHeader == null) && (HeaderAdapter.getHeaderType(localHeader) != 0))
          this.mFirstHeader = localHeader;
        this.mHeaderIndexMap.put(Integer.valueOf(j), Integer.valueOf(i));
        i++;
        break;
        if (j == 2131231229)
        {
          if (!getPackageManager().hasSystemFeature("android.hardware.wifi"))
            paramList.remove(i);
        }
        else if (j == 2131231230)
        {
          if (!getPackageManager().hasSystemFeature("android.hardware.bluetooth"))
            paramList.remove(i);
        }
        else if (j == 2131231231)
        {
          INetworkManagementService localINetworkManagementService = INetworkManagementService.Stub.asInterface(ServiceManager.getService("network_management"));
          try
          {
            if (localINetworkManagementService.isBandwidthControlEnabled())
              continue;
            paramList.remove(i);
          }
          catch (RemoteException localRemoteException)
          {
          }
        }
        else if (j == 2131231239)
        {
          if (!this.mBatteryPresent)
            paramList.remove(i);
        }
        else if (j == 2131231249)
        {
          i = insertAccountsHeaders(paramList, i + 1);
        }
        else if (j == 2131231235)
        {
          if (!updateHomeSettingHeaders(localHeader))
            paramList.remove(i);
        }
        else if (j == 2131231241)
        {
          if ((!UserManager.supportsMultipleUsers()) || (Utils.isMonkeyRunning()))
            paramList.remove(i);
        }
        else if (j == 2131231242)
        {
          if (!getPackageManager().hasSystemFeature("android.hardware.nfc"))
            paramList.remove(i);
          else if ((!NfcAdapter.getDefaultAdapter(this).isEnabled()) || (!getPackageManager().hasSystemFeature("android.hardware.nfc.hce")))
            paramList.remove(i);
        }
        else if (j == 2131231255)
        {
          if (!bool)
            paramList.remove(i);
        }
        else if ((j == 2131231250) && (localUserManager.hasUserRestriction("no_modify_accounts")))
        {
          paramList.remove(i);
        }
      }
    }
  }

  private boolean updateHomeSettingHeaders(PreferenceActivity.Header paramHeader)
  {
    SharedPreferences localSharedPreferences = getSharedPreferences("home_prefs", 0);
    if (localSharedPreferences.getBoolean("do_show", false))
      return true;
    try
    {
      ArrayList localArrayList = new ArrayList();
      getPackageManager().getHomeActivities(localArrayList);
      if (localArrayList.size() < 2)
      {
        if (sShowNoHomeNotice)
        {
          sShowNoHomeNotice = false;
          NoHomeDialogFragment.show(this);
        }
      }
      else
      {
        if (paramHeader.fragmentArguments == null)
          paramHeader.fragmentArguments = new Bundle();
        paramHeader.fragmentArguments.putBoolean("show", true);
        localSharedPreferences.edit().putBoolean("do_show", true).apply();
        return true;
      }
    }
    catch (Exception localException)
    {
      while (true)
        Log.w("Settings", "Problem looking up home activity!", localException);
    }
    return false;
  }

  public Intent getIntent()
  {
    Intent localIntent1 = super.getIntent();
    String str = getStartingFragmentClass(localIntent1);
    if ((str != null) && (!onIsMultiPane()))
    {
      Intent localIntent2 = new Intent(localIntent1);
      localIntent2.putExtra(":android:show_fragment", str);
      Bundle localBundle1 = localIntent1.getExtras();
      if (localBundle1 != null);
      for (Bundle localBundle2 = new Bundle(localBundle1); ; localBundle2 = new Bundle())
      {
        localBundle2.putParcelable("intent", localIntent1);
        localIntent2.putExtra(":android:show_fragment_args", localIntent1.getExtras());
        return localIntent2;
      }
    }
    return localIntent1;
  }

  public Button getNextButton()
  {
    return super.getNextButton();
  }

  protected String getStartingFragmentClass(Intent paramIntent)
  {
    String str;
    if (this.mFragmentClass != null)
      str = this.mFragmentClass;
    do
    {
      return str;
      str = paramIntent.getComponent().getClassName();
      if (str.equals(getClass().getName()))
        return null;
    }
    while ((!"com.android.settings.ManageApplications".equals(str)) && (!"com.android.settings.RunningServices".equals(str)) && (!"com.android.settings.applications.StorageUse".equals(str)));
    return ManageApplications.class.getName();
  }

  public boolean hasNextButton()
  {
    return super.hasNextButton();
  }

  protected boolean isValidFragment(String paramString)
  {
    for (int i = 0; i < ENTRY_FRAGMENTS.length; i++)
      if (ENTRY_FRAGMENTS[i].equals(paramString))
        return true;
    return false;
  }

  public void onAccountsUpdated(Account[] paramArrayOfAccount)
  {
    this.mAuthenticatorHelper.updateAuthDescriptions(this);
    this.mAuthenticatorHelper.onAccountsUpdated(this, paramArrayOfAccount);
    invalidateHeaders();
  }

  public void onBuildHeaders(List<PreferenceActivity.Header> paramList)
  {
    if (!onIsHidingHeaders())
    {
      loadHeadersFromResource(2131034156, paramList);
      updateHeaderList(paramList);
    }
  }

  public Intent onBuildStartFragmentIntent(String paramString, Bundle paramBundle, int paramInt1, int paramInt2)
  {
    Intent localIntent = super.onBuildStartFragmentIntent(paramString, paramBundle, paramInt1, paramInt2);
    if ((WifiSettings.class.getName().equals(paramString)) || (WifiP2pSettings.class.getName().equals(paramString)) || (BluetoothSettings.class.getName().equals(paramString)) || (DreamSettings.class.getName().equals(paramString)) || (LocationSettings.class.getName().equals(paramString)) || (ToggleAccessibilityServicePreferenceFragment.class.getName().equals(paramString)) || (PrintSettingsFragment.class.getName().equals(paramString)) || (PrintServiceSettingsFragment.class.getName().equals(paramString)))
      localIntent.putExtra("settings:ui_options", 1);
    localIntent.setClass(this, SubSettings.class);
    return localIntent;
  }

  protected void onCreate(Bundle paramBundle)
  {
    if (getIntent().hasExtra("settings:ui_options"))
      getWindow().setUiOptions(getIntent().getIntExtra("settings:ui_options", 0));
    this.mAuthenticatorHelper = new AuthenticatorHelper();
    this.mAuthenticatorHelper.updateAuthDescriptions(this);
    this.mAuthenticatorHelper.onAccountsUpdated(this, null);
    this.mDevelopmentPreferences = getSharedPreferences("development", 0);
    getMetaData();
    this.mInLocalHeaderSwitch = true;
    super.onCreate(paramBundle);
    this.mInLocalHeaderSwitch = false;
    if ((!onIsHidingHeaders()) && (onIsMultiPane()))
    {
      highlightHeader(this.mTopLevelHeaderId);
      setTitle(2131427568);
    }
    if (paramBundle != null)
    {
      this.mCurrentHeader = ((PreferenceActivity.Header)paramBundle.getParcelable("com.android.settings.CURRENT_HEADER"));
      this.mParentHeader = ((PreferenceActivity.Header)paramBundle.getParcelable("com.android.settings.PARENT_HEADER"));
    }
    if ((paramBundle != null) && (this.mCurrentHeader != null))
      showBreadCrumbs(this.mCurrentHeader.title, null);
    if (this.mParentHeader != null)
      setParentTitle(this.mParentHeader.title, null, new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          Settings.this.switchToParent(Settings.this.mParentHeader.fragment);
        }
      });
    if (onIsMultiPane())
    {
      getActionBar().setDisplayHomeAsUpEnabled(false);
      getActionBar().setHomeButtonEnabled(false);
    }
  }

  public void onDestroy()
  {
    super.onDestroy();
    if (this.mListeningToAccountUpdates)
      AccountManager.get(this).removeOnAccountsUpdatedListener(this);
  }

  public PreferenceActivity.Header onGetInitialHeader()
  {
    String str = getStartingFragmentClass(super.getIntent());
    if (str != null)
    {
      PreferenceActivity.Header localHeader = new PreferenceActivity.Header();
      localHeader.fragment = str;
      localHeader.title = getTitle();
      localHeader.fragmentArguments = getIntent().getExtras();
      this.mCurrentHeader = localHeader;
      return localHeader;
    }
    return this.mFirstHeader;
  }

  public void onHeaderClick(PreferenceActivity.Header paramHeader, int paramInt)
  {
    boolean bool = paramHeader.id < 2131231250L;
    int i = 0;
    if (!bool)
      i = 1;
    super.onHeaderClick(paramHeader, paramInt);
    if ((i != 0) && (this.mLastHeader != null))
    {
      highlightHeader((int)this.mLastHeader.id);
      return;
    }
    this.mLastHeader = paramHeader;
  }

  public boolean onIsMultiPane()
  {
    return false;
  }

  public void onNewIntent(Intent paramIntent)
  {
    super.onNewIntent(paramIntent);
    if ((0x100000 & paramIntent.getFlags()) == 0)
    {
      if ((this.mFirstHeader != null) && (!onIsHidingHeaders()) && (onIsMultiPane()))
        switchToHeaderLocal(this.mFirstHeader);
      getListView().setSelectionFromTop(0, 0);
    }
  }

  public void onPause()
  {
    super.onPause();
    unregisterReceiver(this.mBatteryInfoReceiver);
    ListAdapter localListAdapter = getListAdapter();
    if ((localListAdapter instanceof HeaderAdapter))
      ((HeaderAdapter)localListAdapter).pause();
    this.mDevelopmentPreferences.unregisterOnSharedPreferenceChangeListener(this.mDevelopmentPreferencesListener);
    this.mDevelopmentPreferencesListener = null;
  }

  public boolean onPreferenceStartFragment(PreferenceFragment paramPreferenceFragment, Preference paramPreference)
  {
    int i = paramPreference.getTitleRes();
    if (paramPreference.getFragment().equals(WallpaperTypeSettings.class.getName()))
      i = 2131428051;
    while (true)
    {
      startPreferencePanel(paramPreference.getFragment(), paramPreference.getExtras(), i, paramPreference.getTitle(), null, 0);
      return true;
      if ((paramPreference.getFragment().equals(OwnerInfoSettings.class.getName())) && (UserHandle.myUserId() != 0))
        if (UserManager.get(this).isLinkedUser())
          i = 2131427617;
        else
          i = 2131427615;
    }
  }

  public void onResume()
  {
    super.onResume();
    this.mDevelopmentPreferencesListener = new SharedPreferences.OnSharedPreferenceChangeListener()
    {
      public void onSharedPreferenceChanged(SharedPreferences paramAnonymousSharedPreferences, String paramAnonymousString)
      {
        Settings.this.invalidateHeaders();
      }
    };
    this.mDevelopmentPreferences.registerOnSharedPreferenceChangeListener(this.mDevelopmentPreferencesListener);
    ListAdapter localListAdapter = getListAdapter();
    if ((localListAdapter instanceof HeaderAdapter))
      ((HeaderAdapter)localListAdapter).resume();
    invalidateHeaders();
    registerReceiver(this.mBatteryInfoReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
  }

  protected void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if (this.mCurrentHeader != null)
      paramBundle.putParcelable("com.android.settings.CURRENT_HEADER", this.mCurrentHeader);
    if (this.mParentHeader != null)
      paramBundle.putParcelable("com.android.settings.PARENT_HEADER", this.mParentHeader);
  }

  public void setListAdapter(ListAdapter paramListAdapter)
  {
    if (paramListAdapter == null)
    {
      super.setListAdapter(null);
      return;
    }
    DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)getSystemService("device_policy");
    super.setListAdapter(new HeaderAdapter(this, getHeaders(), this.mAuthenticatorHelper, localDevicePolicyManager));
  }

  public boolean shouldUpRecreateTask(Intent paramIntent)
  {
    return super.shouldUpRecreateTask(new Intent(this, Settings.class));
  }

  public void switchToHeader(PreferenceActivity.Header paramHeader)
  {
    if (!this.mInLocalHeaderSwitch)
    {
      this.mCurrentHeader = null;
      this.mParentHeader = null;
    }
    super.switchToHeader(paramHeader);
  }

  public static class AccessibilitySettingsActivity extends Settings
  {
  }

  public static class AccountSyncSettingsActivity extends Settings
  {
  }

  public static class AdvancedWifiSettingsActivity extends Settings
  {
  }

  public static class AndroidBeamSettingsActivity extends Settings
  {
  }

  public static class BluetoothSettingsActivity extends Settings
  {
  }

  public static class CaptioningSettingsActivity extends Settings
  {
  }

  public static class CryptKeeperSettingsActivity extends Settings
  {
  }

  public static class DataUsageSummaryActivity extends Settings
  {
  }

  public static class DateTimeSettingsActivity extends Settings
  {
  }

  public static class DevelopmentSettingsActivity extends Settings
  {
  }

  public static class DeviceAdminSettingsActivity extends Settings
  {
  }

  public static class DeviceInfoSettingsActivity extends Settings
  {
  }

  public static class DisplaySettingsActivity extends Settings
  {
  }

  public static class DreamSettingsActivity extends Settings
  {
  }

  private static class HeaderAdapter extends ArrayAdapter<PreferenceActivity.Header>
  {
    private AuthenticatorHelper mAuthHelper;
    private final BluetoothEnabler mBluetoothEnabler;
    private DevicePolicyManager mDevicePolicyManager;
    private LayoutInflater mInflater;
    private final WifiEnabler mWifiEnabler;

    public HeaderAdapter(Context paramContext, List<PreferenceActivity.Header> paramList, AuthenticatorHelper paramAuthenticatorHelper, DevicePolicyManager paramDevicePolicyManager)
    {
      super(0, paramList);
      this.mAuthHelper = paramAuthenticatorHelper;
      this.mInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
      this.mWifiEnabler = new WifiEnabler(paramContext, new Switch(paramContext));
      this.mBluetoothEnabler = new BluetoothEnabler(paramContext, new Switch(paramContext));
      this.mDevicePolicyManager = paramDevicePolicyManager;
    }

    static int getHeaderType(PreferenceActivity.Header paramHeader)
    {
      if ((paramHeader.fragment == null) && (paramHeader.intent == null))
        return 0;
      if ((paramHeader.id == 2131231229L) || (paramHeader.id == 2131231230L))
        return 2;
      if (paramHeader.id == 2131231246L)
        return 3;
      return 1;
    }

    private void setHeaderIcon(HeaderViewHolder paramHeaderViewHolder, Drawable paramDrawable)
    {
      ViewGroup.LayoutParams localLayoutParams = paramHeaderViewHolder.icon.getLayoutParams();
      localLayoutParams.width = getContext().getResources().getDimensionPixelSize(2131558426);
      localLayoutParams.height = localLayoutParams.width;
      paramHeaderViewHolder.icon.setLayoutParams(localLayoutParams);
      paramHeaderViewHolder.icon.setImageDrawable(paramDrawable);
    }

    private void updateCommonHeaderView(PreferenceActivity.Header paramHeader, HeaderViewHolder paramHeaderViewHolder)
    {
      if ((paramHeader.extras != null) && (paramHeader.extras.containsKey("account_type")))
      {
        String str = paramHeader.extras.getString("account_type");
        setHeaderIcon(paramHeaderViewHolder, this.mAuthHelper.getDrawableForType(getContext(), str));
      }
      while (true)
      {
        paramHeaderViewHolder.title.setText(paramHeader.getTitle(getContext().getResources()));
        CharSequence localCharSequence = paramHeader.getSummary(getContext().getResources());
        if (TextUtils.isEmpty(localCharSequence))
          break;
        paramHeaderViewHolder.summary.setVisibility(0);
        paramHeaderViewHolder.summary.setText(localCharSequence);
        return;
        paramHeaderViewHolder.icon.setImageResource(paramHeader.iconRes);
      }
      paramHeaderViewHolder.summary.setVisibility(8);
    }

    public boolean areAllItemsEnabled()
    {
      return false;
    }

    public int getItemViewType(int paramInt)
    {
      return getHeaderType((PreferenceActivity.Header)getItem(paramInt));
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      PreferenceActivity.Header localHeader = (PreferenceActivity.Header)getItem(paramInt);
      int i = getHeaderType(localHeader);
      HeaderViewHolder localHeaderViewHolder;
      Object localObject;
      if (paramView == null)
      {
        localHeaderViewHolder = new HeaderViewHolder(null);
        localObject = null;
        switch (i)
        {
        default:
          label68: ((View)localObject).setTag(localHeaderViewHolder);
        case 0:
        case 2:
        case 3:
        case 1:
        }
      }
      while (true)
        switch (i)
        {
        default:
          return localObject;
          localObject = new TextView(getContext(), null, 16843272);
          localHeaderViewHolder.title = ((TextView)localObject);
          break label68;
          localObject = this.mInflater.inflate(2130968674, paramViewGroup, false);
          localHeaderViewHolder.icon = ((ImageView)((View)localObject).findViewById(2131230755));
          localHeaderViewHolder.title = ((TextView)((View)localObject).findViewById(16908310));
          localHeaderViewHolder.summary = ((TextView)((View)localObject).findViewById(16908304));
          localHeaderViewHolder.switch_ = ((Switch)((View)localObject).findViewById(2131230734));
          break label68;
          localObject = this.mInflater.inflate(2130968672, paramViewGroup, false);
          localHeaderViewHolder.icon = ((ImageView)((View)localObject).findViewById(2131230755));
          localHeaderViewHolder.title = ((TextView)((View)localObject).findViewById(16908310));
          localHeaderViewHolder.summary = ((TextView)((View)localObject).findViewById(16908304));
          localHeaderViewHolder.button_ = ((ImageButton)((View)localObject).findViewById(2131230955));
          localHeaderViewHolder.divider_ = ((View)localObject).findViewById(2131230845);
          break label68;
          localObject = this.mInflater.inflate(2130968673, paramViewGroup, false);
          localHeaderViewHolder.icon = ((ImageView)((View)localObject).findViewById(2131230755));
          localHeaderViewHolder.title = ((TextView)((View)localObject).findViewById(16908310));
          localHeaderViewHolder.summary = ((TextView)((View)localObject).findViewById(16908304));
          break label68;
          localObject = paramView;
          localHeaderViewHolder = (HeaderViewHolder)((View)localObject).getTag();
        case 0:
        case 2:
        case 3:
        case 1:
        }
      localHeaderViewHolder.title.setText(localHeader.getTitle(getContext().getResources()));
      return localObject;
      if (localHeader.id == 2131231229L)
        this.mWifiEnabler.setSwitch(localHeaderViewHolder.switch_);
      while (true)
      {
        updateCommonHeaderView(localHeader, localHeaderViewHolder);
        return localObject;
        this.mBluetoothEnabler.setSwitch(localHeaderViewHolder.switch_);
      }
      if (localHeader.id == 2131231246L)
      {
        if (!DevicePolicyManager.hasAnyCaCertsInstalled())
          break label567;
        localHeaderViewHolder.button_.setVisibility(0);
        localHeaderViewHolder.divider_.setVisibility(0);
        String str = this.mDevicePolicyManager.getDeviceOwner();
        int j = 0;
        if (str != null)
          j = 1;
        if (j == 0)
          break label553;
        localHeaderViewHolder.button_.setImageResource(2130837600);
        localHeaderViewHolder.button_.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            Intent localIntent = new Intent("com.android.settings.MONITORING_CERT_INFO");
            Settings.HeaderAdapter.this.getContext().startActivity(localIntent);
          }
        });
      }
      while (true)
      {
        updateCommonHeaderView(localHeader, localHeaderViewHolder);
        return localObject;
        label553: localHeaderViewHolder.button_.setImageResource(17301624);
        break;
        label567: localHeaderViewHolder.button_.setVisibility(8);
        localHeaderViewHolder.divider_.setVisibility(8);
      }
      updateCommonHeaderView(localHeader, localHeaderViewHolder);
      return localObject;
    }

    public int getViewTypeCount()
    {
      return 4;
    }

    public boolean hasStableIds()
    {
      return true;
    }

    public boolean isEnabled(int paramInt)
    {
      return getItemViewType(paramInt) != 0;
    }

    public void pause()
    {
      this.mWifiEnabler.pause();
      this.mBluetoothEnabler.pause();
    }

    public void resume()
    {
      this.mWifiEnabler.resume();
      this.mBluetoothEnabler.resume();
    }

    private static class HeaderViewHolder
    {
      ImageButton button_;
      View divider_;
      ImageView icon;
      TextView summary;
      Switch switch_;
      TextView title;
    }
  }

  public static class InputMethodAndLanguageSettingsActivity extends Settings
  {
  }

  public static class KeyboardLayoutPickerActivity extends Settings
  {
  }

  public static class LocalePickerActivity extends Settings
  {
  }

  public static class LocationSettingsActivity extends Settings
  {
  }

  public static class ManageApplicationsActivity extends Settings
  {
  }

  public static class NoHomeDialogFragment extends DialogFragment
  {
    public static void show(Activity paramActivity)
    {
      new NoHomeDialogFragment().show(paramActivity.getFragmentManager(), null);
    }

    public Dialog onCreateDialog(Bundle paramBundle)
    {
      return new AlertDialog.Builder(getActivity()).setMessage(2131429238).setPositiveButton(17039370, null).create();
    }
  }

  public static class NotificationAccessSettingsActivity extends Settings
  {
  }

  public static class NotificationStationActivity extends Settings
  {
  }

  public static class PaymentSettingsActivity extends Settings
  {
  }

  public static class PowerUsageSummaryActivity extends Settings
  {
  }

  public static class PrintJobSettingsActivity extends Settings
  {
  }

  public static class PrintSettingsActivity extends Settings
  {
  }

  public static class PrivacySettingsActivity extends Settings
  {
  }

  public static class RunningServicesActivity extends Settings
  {
  }

  public static class SecuritySettingsActivity extends Settings
  {
  }

  public static class SoundSettingsActivity extends Settings
  {
  }

  public static class SpellCheckersSettingsActivity extends Settings
  {
  }

  public static class StorageSettingsActivity extends Settings
  {
  }

  public static class StorageUseActivity extends Settings
  {
  }

  public static class TetherSettingsActivity extends Settings
  {
  }

  public static class TextToSpeechSettingsActivity extends Settings
  {
  }

  public static class TrustedCredentialsSettingsActivity extends Settings
  {
  }

  public static class UsbSettingsActivity extends Settings
  {
  }

  public static class UserDictionarySettingsActivity extends Settings
  {
  }

  public static class UserSettingsActivity extends Settings
  {
  }

  public static class VpnSettingsActivity extends Settings
  {
  }

  public static class WifiDisplaySettingsActivity extends Settings
  {
  }

  public static class WifiP2pSettingsActivity extends Settings
  {
  }

  public static class WifiSettingsActivity extends Settings
  {
  }

  public static class WirelessSettingsActivity extends Settings
  {
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.Settings
 * JD-Core Version:    0.6.2
 */