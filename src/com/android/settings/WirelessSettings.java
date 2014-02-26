package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Fragment;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.provider.Settings.Global;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import com.android.internal.telephony.SmsApplication;
import com.android.internal.telephony.SmsApplication.SmsApplicationData;
import com.android.settings.nfc.NfcEnabler;
import java.util.Collection;
import java.util.Iterator;

public class WirelessSettings extends RestrictedSettingsFragment
  implements Preference.OnPreferenceChangeListener
{
  private AirplaneModeEnabler mAirplaneModeEnabler;
  private CheckBoxPreference mAirplaneModePreference;
  private ConnectivityManager mCm;
  private String mManageMobilePlanMessage;
  private NfcAdapter mNfcAdapter;
  private NfcEnabler mNfcEnabler;
  private NsdEnabler mNsdEnabler;
  private SmsListPreference mSmsApplicationPreference;
  private TelephonyManager mTm;

  public WirelessSettings()
  {
    super(null);
  }

  private void initSmsApplicationSetting()
  {
    log("initSmsApplicationSetting:");
    Collection localCollection = SmsApplication.getApplicationCollection(getActivity());
    int i = localCollection.size();
    CharSequence[] arrayOfCharSequence1 = new CharSequence[i];
    CharSequence[] arrayOfCharSequence2 = new CharSequence[i];
    Drawable[] arrayOfDrawable = new Drawable[i];
    PackageManager localPackageManager = getPackageManager();
    int j = 0;
    Iterator localIterator = localCollection.iterator();
    while (true)
      if (localIterator.hasNext())
      {
        SmsApplication.SmsApplicationData localSmsApplicationData = (SmsApplication.SmsApplicationData)localIterator.next();
        arrayOfCharSequence1[j] = localSmsApplicationData.mApplicationName;
        arrayOfCharSequence2[j] = localSmsApplicationData.mPackageName;
        try
        {
          arrayOfDrawable[j] = localPackageManager.getApplicationIcon(localSmsApplicationData.mPackageName);
          j++;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          while (true)
            arrayOfDrawable[j] = localPackageManager.getDefaultActivityIcon();
        }
      }
    this.mSmsApplicationPreference.setEntries(arrayOfCharSequence1);
    this.mSmsApplicationPreference.setEntryValues(arrayOfCharSequence2);
    this.mSmsApplicationPreference.setEntryDrawables(arrayOfDrawable);
    updateSmsApplicationSetting();
  }

  public static boolean isRadioAllowed(Context paramContext, String paramString)
  {
    if (!AirplaneModeEnabler.isAirplaneModeOn(paramContext));
    String str;
    do
    {
      return true;
      str = Settings.Global.getString(paramContext.getContentResolver(), "airplane_mode_toggleable_radios");
    }
    while ((str != null) && (str.contains(paramString)));
    return false;
  }

  private boolean isSmsSupported()
  {
    return this.mTm.getPhoneType() != 0;
  }

  private void log(String paramString)
  {
    Log.d("WirelessSettings", paramString);
  }

  private void updateSmsApplicationSetting()
  {
    log("updateSmsApplicationSetting:");
    ComponentName localComponentName = SmsApplication.getDefaultSmsApplication(getActivity(), true);
    String str;
    CharSequence[] arrayOfCharSequence;
    if (localComponentName != null)
    {
      str = localComponentName.getPackageName();
      arrayOfCharSequence = this.mSmsApplicationPreference.getEntryValues();
    }
    for (int i = 0; ; i++)
      if (i < arrayOfCharSequence.length)
      {
        if (str.contentEquals(arrayOfCharSequence[i]))
        {
          this.mSmsApplicationPreference.setValueIndex(i);
          this.mSmsApplicationPreference.setSummary(this.mSmsApplicationPreference.getEntries()[i]);
        }
      }
      else
        return;
  }

  protected int getHelpResource()
  {
    return 2131429257;
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt1 == 1)
    {
      Boolean localBoolean = Boolean.valueOf(paramIntent.getBooleanExtra("exit_ecm_result", false));
      this.mAirplaneModeEnabler.setAirplaneModeInECM(localBoolean.booleanValue(), this.mAirplaneModePreference.isChecked());
    }
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle != null)
      this.mManageMobilePlanMessage = paramBundle.getString("mManageMobilePlanMessage");
    log("onCreate: mManageMobilePlanMessage=" + this.mManageMobilePlanMessage);
    this.mCm = ((ConnectivityManager)getSystemService("connectivity"));
    this.mTm = ((TelephonyManager)getSystemService("phone"));
    addPreferencesFromResource(2131034177);
    int i;
    if (UserHandle.myUserId() != 0)
      i = 1;
    while (true)
    {
      Activity localActivity = getActivity();
      this.mAirplaneModePreference = ((CheckBoxPreference)findPreference("toggle_airplane"));
      CheckBoxPreference localCheckBoxPreference1 = (CheckBoxPreference)findPreference("toggle_nfc");
      PreferenceScreen localPreferenceScreen1 = (PreferenceScreen)findPreference("android_beam_settings");
      CheckBoxPreference localCheckBoxPreference2 = (CheckBoxPreference)findPreference("toggle_nsd");
      AirplaneModeEnabler localAirplaneModeEnabler = new AirplaneModeEnabler(localActivity, this.mAirplaneModePreference);
      this.mAirplaneModeEnabler = localAirplaneModeEnabler;
      NfcEnabler localNfcEnabler = new NfcEnabler(localActivity, localCheckBoxPreference1, localPreferenceScreen1);
      this.mNfcEnabler = localNfcEnabler;
      this.mSmsApplicationPreference = ((SmsListPreference)findPreference("sms_application"));
      this.mSmsApplicationPreference.setOnPreferenceChangeListener(this);
      initSmsApplicationSetting();
      getPreferenceScreen().removePreference(localCheckBoxPreference2);
      String str = Settings.Global.getString(localActivity.getContentResolver(), "airplane_mode_toggleable_radios");
      int j;
      label250: boolean bool1;
      label283: label586: ConnectivityManager localConnectivityManager;
      boolean bool2;
      if ((i == 0) && (getResources().getBoolean(17891400)))
      {
        j = 1;
        if (j != 0)
          break label733;
        PreferenceScreen localPreferenceScreen3 = getPreferenceScreen();
        Preference localPreference3 = findPreference("wimax_settings");
        if (localPreference3 != null)
          localPreferenceScreen3.removePreference(localPreference3);
        protectByRestrictions("wimax_settings");
        if ((str == null) || (!str.contains("wifi")))
          findPreference("vpn_settings").setDependency("toggle_airplane");
        if (i != 0)
          removePreference("vpn_settings");
        protectByRestrictions("vpn_settings");
        if (((str == null) || (str.contains("bluetooth"))) || ((str == null) || (!str.contains("nfc"))))
        {
          findPreference("toggle_nfc").setDependency("toggle_airplane");
          findPreference("android_beam_settings").setDependency("toggle_airplane");
        }
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(localActivity);
        if (this.mNfcAdapter == null)
        {
          getPreferenceScreen().removePreference(localCheckBoxPreference1);
          getPreferenceScreen().removePreference(localPreferenceScreen1);
          this.mNfcEnabler = null;
        }
        if ((i != 0) || (Utils.isWifiOnly(getActivity())))
        {
          removePreference("mobile_network_settings");
          removePreference("manage_mobile_plan");
        }
        if ((!getResources().getBoolean(2131296262)) && (findPreference("manage_mobile_plan") != null))
          removePreference("manage_mobile_plan");
        protectByRestrictions("mobile_network_settings");
        protectByRestrictions("manage_mobile_plan");
        if (!isSmsSupported())
          removePreference("sms_application");
        if (getActivity().getPackageManager().hasSystemFeature("android.hardware.type.television"))
          removePreference("toggle_airplane");
        Preference localPreference1 = findPreference("proxy_settings");
        DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)localActivity.getSystemService("device_policy");
        getPreferenceScreen().removePreference(localPreference1);
        if (localDevicePolicyManager.getGlobalProxyAdmin() != null)
          break label770;
        bool1 = true;
        localPreference1.setEnabled(bool1);
        localConnectivityManager = (ConnectivityManager)localActivity.getSystemService("connectivity");
        if ((i == 0) && (localConnectivityManager.isTetheringSupported()))
          break label776;
        getPreferenceScreen().removePreference(findPreference("tether_settings"));
        protectByRestrictions("tether_settings");
        bool2 = getResources().getBoolean(17891405);
        if (!bool2);
      }
      try
      {
        int k = getPackageManager().getApplicationEnabledSetting("com.android.cellbroadcastreceiver");
        if (k == 2)
          bool2 = false;
        if ((i != 0) || (!bool2))
        {
          PreferenceScreen localPreferenceScreen2 = getPreferenceScreen();
          Preference localPreference2 = findPreference("cell_broadcast_settings");
          if (localPreference2 != null)
            localPreferenceScreen2.removePreference(localPreference2);
        }
        protectByRestrictions("cell_broadcast_settings");
        return;
        i = 0;
        continue;
        j = 0;
        break label250;
        label733: if ((str != null) && ((str.contains("wimax")) || (j == 0)))
          break label283;
        findPreference("wimax_settings").setDependency("toggle_airplane");
        break label283;
        label770: bool1 = false;
        break label586;
        label776: findPreference("tether_settings").setTitle(Utils.getTetheringLabel(localConnectivityManager));
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        while (true)
          bool2 = false;
      }
    }
  }

  public Dialog onCreateDialog(int paramInt)
  {
    log("onCreateDialog: dialogId=" + paramInt);
    switch (paramInt)
    {
    default:
      return super.onCreateDialog(paramInt);
    case 1:
    }
    return new AlertDialog.Builder(getActivity()).setMessage(this.mManageMobilePlanMessage).setCancelable(false).setPositiveButton(17039370, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        WirelessSettings.this.log("MANAGE_MOBILE_PLAN_DIALOG.onClickListener id=" + paramAnonymousInt);
        WirelessSettings.access$102(WirelessSettings.this, null);
      }
    }).create();
  }

  public void onManageMobilePlanClick()
  {
    log("onManageMobilePlanClick:");
    this.mManageMobilePlanMessage = null;
    Resources localResources = getActivity().getResources();
    NetworkInfo localNetworkInfo = this.mCm.getProvisioningOrActiveNetworkInfo();
    if ((this.mTm.hasIccCard()) && (localNetworkInfo != null))
    {
      String str1 = this.mCm.getMobileProvisioningUrl();
      if (!TextUtils.isEmpty(str1))
      {
        Intent localIntent = new Intent("com.android.server.connectivityservice.CONNECTED_TO_PROVISIONING_NETWORK_ACTION");
        localIntent.putExtra("EXTRA_URL", str1);
        getActivity().getBaseContext().sendBroadcast(localIntent);
        this.mManageMobilePlanMessage = null;
      }
    }
    while (true)
    {
      if (!TextUtils.isEmpty(this.mManageMobilePlanMessage))
      {
        log("onManageMobilePlanClick: message=" + this.mManageMobilePlanMessage);
        showDialog(1);
      }
      return;
      String str2 = this.mTm.getSimOperatorName();
      if (TextUtils.isEmpty(str2))
      {
        String str3 = this.mTm.getNetworkOperatorName();
        if (TextUtils.isEmpty(str3))
          this.mManageMobilePlanMessage = localResources.getString(2131428263);
        else
          this.mManageMobilePlanMessage = localResources.getString(2131428264, new Object[] { str3 });
      }
      else
      {
        this.mManageMobilePlanMessage = localResources.getString(2131428264, new Object[] { str2 });
        continue;
        if (!this.mTm.hasIccCard())
          this.mManageMobilePlanMessage = localResources.getString(2131428265);
        else
          this.mManageMobilePlanMessage = localResources.getString(2131428266);
      }
    }
  }

  public void onPause()
  {
    super.onPause();
    this.mAirplaneModeEnabler.pause();
    if (this.mNfcEnabler != null)
      this.mNfcEnabler.pause();
    if (this.mNsdEnabler != null)
      this.mNsdEnabler.pause();
  }

  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if ((paramPreference == this.mSmsApplicationPreference) && (paramObject != null))
    {
      SmsApplication.setDefaultApplication(paramObject.toString(), getActivity());
      updateSmsApplicationSetting();
      return true;
    }
    return false;
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    if (ensurePinRestrictedPreference(paramPreference))
      return true;
    log("onPreferenceTreeClick: preference=" + paramPreference);
    if ((paramPreference == this.mAirplaneModePreference) && (Boolean.parseBoolean(SystemProperties.get("ril.cdma.inecmmode"))))
    {
      startActivityForResult(new Intent("android.intent.action.ACTION_SHOW_NOTICE_ECM_BLOCK_OTHERS", null), 1);
      return true;
    }
    if (paramPreference == findPreference("manage_mobile_plan"))
      onManageMobilePlanClick();
    return super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
  }

  public void onResume()
  {
    super.onResume();
    this.mAirplaneModeEnabler.resume();
    if (this.mNfcEnabler != null)
      this.mNfcEnabler.resume();
    if (this.mNsdEnabler != null)
      this.mNsdEnabler.resume();
  }

  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if (!TextUtils.isEmpty(this.mManageMobilePlanMessage))
      paramBundle.putString("mManageMobilePlanMessage", this.mManageMobilePlanMessage);
  }

  public void onStart()
  {
    super.onStart();
    initSmsApplicationSetting();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.WirelessSettings
 * JD-Core Version:    0.6.2
 */