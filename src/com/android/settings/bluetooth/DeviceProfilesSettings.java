package com.android.settings.bluetooth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import com.android.settings.SettingsPreferenceFragment;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public final class DeviceProfilesSettings extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, CachedBluetoothDevice.Callback
{
  private final HashMap<LocalBluetoothProfile, CheckBoxPreference> mAutoConnectPrefs = new HashMap();
  private CachedBluetoothDevice mCachedDevice;
  private EditTextPreference mDeviceNamePref;
  private AlertDialog mDisconnectDialog;
  private LocalBluetoothManager mManager;
  private PreferenceGroup mProfileContainer;
  private boolean mProfileGroupIsRemoved;
  private LocalBluetoothProfileManager mProfileManager;
  private RenameEditTextPreference mRenameDeviceNamePref;

  private void addPreferencesForProfiles()
  {
    Iterator localIterator = this.mCachedDevice.getConnectableProfiles().iterator();
    while (localIterator.hasNext())
    {
      CheckBoxPreference localCheckBoxPreference = createProfilePreference((LocalBluetoothProfile)localIterator.next());
      this.mProfileContainer.addPreference(localCheckBoxPreference);
    }
    showOrHideProfileGroup();
  }

  private void askDisconnect(Context paramContext, final LocalBluetoothProfile paramLocalBluetoothProfile)
  {
    final CachedBluetoothDevice localCachedBluetoothDevice = this.mCachedDevice;
    String str1 = localCachedBluetoothDevice.getName();
    if (TextUtils.isEmpty(str1))
      str1 = paramContext.getString(2131427451);
    String str2 = paramContext.getString(paramLocalBluetoothProfile.getNameResource(localCachedBluetoothDevice.getDevice()));
    String str3 = paramContext.getString(2131427439);
    String str4 = paramContext.getString(2131427440, new Object[] { str2, str1 });
    DialogInterface.OnClickListener local1 = new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        localCachedBluetoothDevice.disconnect(paramLocalBluetoothProfile);
        paramLocalBluetoothProfile.setPreferred(localCachedBluetoothDevice.getDevice(), false);
      }
    };
    this.mDisconnectDialog = Utils.showDisconnectDialog(paramContext, this.mDisconnectDialog, local1, str3, Html.fromHtml(str4));
  }

  private CheckBoxPreference createProfilePreference(LocalBluetoothProfile paramLocalBluetoothProfile)
  {
    CheckBoxPreference localCheckBoxPreference = new CheckBoxPreference(getActivity());
    localCheckBoxPreference.setKey(paramLocalBluetoothProfile.toString());
    localCheckBoxPreference.setTitle(paramLocalBluetoothProfile.getNameResource(this.mCachedDevice.getDevice()));
    localCheckBoxPreference.setPersistent(false);
    localCheckBoxPreference.setOrder(getProfilePreferenceIndex(paramLocalBluetoothProfile.getOrdinal()));
    localCheckBoxPreference.setOnPreferenceChangeListener(this);
    int i = paramLocalBluetoothProfile.getDrawableResource(this.mCachedDevice.getBtClass());
    if (i != 0)
      localCheckBoxPreference.setIcon(getResources().getDrawable(i));
    boolean bool1 = this.mCachedDevice.isBusy();
    boolean bool2 = false;
    if (!bool1)
      bool2 = true;
    localCheckBoxPreference.setEnabled(bool2);
    refreshProfilePreference(localCheckBoxPreference, paramLocalBluetoothProfile);
    return localCheckBoxPreference;
  }

  private LocalBluetoothProfile getProfileOf(Preference paramPreference)
  {
    if (!(paramPreference instanceof CheckBoxPreference));
    while (TextUtils.isEmpty(paramPreference.getKey()))
      return null;
    try
    {
      LocalBluetoothProfile localLocalBluetoothProfile = this.mProfileManager.getProfileByName(paramPreference.getKey());
      return localLocalBluetoothProfile;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
    }
    return null;
  }

  private int getProfilePreferenceIndex(int paramInt)
  {
    return this.mProfileContainer.getOrder() + paramInt * 10;
  }

  private void onProfileClicked(LocalBluetoothProfile paramLocalBluetoothProfile, CheckBoxPreference paramCheckBoxPreference)
  {
    BluetoothDevice localBluetoothDevice = this.mCachedDevice.getDevice();
    if (paramLocalBluetoothProfile.getConnectionStatus(localBluetoothDevice) == 2);
    for (int i = 1; i != 0; i = 0)
    {
      askDisconnect(getActivity(), paramLocalBluetoothProfile);
      return;
    }
    if (paramLocalBluetoothProfile.isPreferred(localBluetoothDevice))
    {
      paramLocalBluetoothProfile.setPreferred(localBluetoothDevice, false);
      refreshProfilePreference(paramCheckBoxPreference, paramLocalBluetoothProfile);
      return;
    }
    paramLocalBluetoothProfile.setPreferred(localBluetoothDevice, true);
    this.mCachedDevice.connectProfile(paramLocalBluetoothProfile);
  }

  private void refresh()
  {
    String str = this.mCachedDevice.getName();
    this.mDeviceNamePref.setSummary(str);
    this.mDeviceNamePref.setText(str);
    refreshProfiles();
  }

  private void refreshProfilePreference(CheckBoxPreference paramCheckBoxPreference, LocalBluetoothProfile paramLocalBluetoothProfile)
  {
    BluetoothDevice localBluetoothDevice = this.mCachedDevice.getDevice();
    if (!this.mCachedDevice.isBusy());
    for (boolean bool = true; ; bool = false)
    {
      paramCheckBoxPreference.setEnabled(bool);
      paramCheckBoxPreference.setChecked(paramLocalBluetoothProfile.isPreferred(localBluetoothDevice));
      paramCheckBoxPreference.setSummary(paramLocalBluetoothProfile.getSummaryResourceForDevice(localBluetoothDevice));
      return;
    }
  }

  private void refreshProfiles()
  {
    Iterator localIterator1 = this.mCachedDevice.getConnectableProfiles().iterator();
    while (localIterator1.hasNext())
    {
      LocalBluetoothProfile localLocalBluetoothProfile2 = (LocalBluetoothProfile)localIterator1.next();
      CheckBoxPreference localCheckBoxPreference1 = (CheckBoxPreference)findPreference(localLocalBluetoothProfile2.toString());
      if (localCheckBoxPreference1 == null)
      {
        CheckBoxPreference localCheckBoxPreference2 = createProfilePreference(localLocalBluetoothProfile2);
        this.mProfileContainer.addPreference(localCheckBoxPreference2);
      }
      else
      {
        refreshProfilePreference(localCheckBoxPreference1, localLocalBluetoothProfile2);
      }
    }
    Iterator localIterator2 = this.mCachedDevice.getRemovedProfiles().iterator();
    while (localIterator2.hasNext())
    {
      LocalBluetoothProfile localLocalBluetoothProfile1 = (LocalBluetoothProfile)localIterator2.next();
      Preference localPreference = findPreference(localLocalBluetoothProfile1.toString());
      if (localPreference != null)
      {
        Log.d("DeviceProfilesSettings", "Removing " + localLocalBluetoothProfile1.toString() + " from profile list");
        this.mProfileContainer.removePreference(localPreference);
      }
    }
    showOrHideProfileGroup();
  }

  private void showOrHideProfileGroup()
  {
    int i = this.mProfileContainer.getPreferenceCount();
    if ((!this.mProfileGroupIsRemoved) && (i == 0))
    {
      getPreferenceScreen().removePreference(this.mProfileContainer);
      this.mProfileGroupIsRemoved = true;
    }
    while ((!this.mProfileGroupIsRemoved) || (i == 0))
      return;
    getPreferenceScreen().addPreference(this.mProfileContainer);
    this.mProfileGroupIsRemoved = false;
  }

  private void unpairDevice()
  {
    this.mCachedDevice.unpair();
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle != null);
    for (BluetoothDevice localBluetoothDevice = (BluetoothDevice)paramBundle.getParcelable("device"); ; localBluetoothDevice = (BluetoothDevice)getArguments().getParcelable("device"))
    {
      addPreferencesFromResource(2131034120);
      getPreferenceScreen().setOrderingAsAdded(false);
      this.mProfileContainer = ((PreferenceGroup)findPreference("profile_container"));
      this.mDeviceNamePref = ((EditTextPreference)findPreference("rename_device"));
      if (localBluetoothDevice != null)
        break;
      Log.w("DeviceProfilesSettings", "Activity started without a remote Bluetooth device");
      finish();
      return;
    }
    this.mRenameDeviceNamePref = new RenameEditTextPreference(null);
    this.mManager = LocalBluetoothManager.getInstance(getActivity());
    CachedBluetoothDeviceManager localCachedBluetoothDeviceManager = this.mManager.getCachedDeviceManager();
    this.mProfileManager = this.mManager.getProfileManager();
    this.mCachedDevice = localCachedBluetoothDeviceManager.findDevice(localBluetoothDevice);
    if (this.mCachedDevice == null)
    {
      Log.w("DeviceProfilesSettings", "Device not found, cannot connect to it");
      finish();
      return;
    }
    String str = this.mCachedDevice.getName();
    this.mDeviceNamePref.setSummary(str);
    this.mDeviceNamePref.setText(str);
    this.mDeviceNamePref.setOnPreferenceChangeListener(this);
    addPreferencesForProfiles();
  }

  public void onDestroy()
  {
    super.onDestroy();
    if (this.mDisconnectDialog != null)
    {
      this.mDisconnectDialog.dismiss();
      this.mDisconnectDialog = null;
    }
  }

  public void onDeviceAttributesChanged()
  {
    refresh();
  }

  public void onPause()
  {
    super.onPause();
    this.mCachedDevice.unregisterCallback(this);
    this.mManager.setForegroundActivity(null);
  }

  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    boolean bool2;
    if (paramPreference == this.mDeviceNamePref)
    {
      this.mCachedDevice.setName((String)paramObject);
      bool2 = true;
    }
    boolean bool1;
    do
    {
      return bool2;
      bool1 = paramPreference instanceof CheckBoxPreference;
      bool2 = false;
    }
    while (!bool1);
    onProfileClicked(getProfileOf(paramPreference), (CheckBoxPreference)paramPreference);
    return false;
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    if (paramPreference.getKey().equals("unpair"))
    {
      unpairDevice();
      finish();
      return true;
    }
    return super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
  }

  public void onResume()
  {
    super.onResume();
    this.mManager.setForegroundActivity(getActivity());
    this.mCachedDevice.registerCallback(this);
    if (this.mCachedDevice.getBondState() == 10)
      finish();
    refresh();
    EditText localEditText = this.mDeviceNamePref.getEditText();
    Button localButton;
    if (localEditText != null)
    {
      localEditText.addTextChangedListener(this.mRenameDeviceNamePref);
      Dialog localDialog = this.mDeviceNamePref.getDialog();
      if ((localDialog instanceof AlertDialog))
      {
        localButton = ((AlertDialog)localDialog).getButton(-1);
        if (localEditText.getText().length() <= 0)
          break label109;
      }
    }
    label109: for (boolean bool = true; ; bool = false)
    {
      localButton.setEnabled(bool);
      return;
    }
  }

  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putParcelable("device", this.mCachedDevice.getDevice());
  }

  private class RenameEditTextPreference
    implements TextWatcher
  {
    private RenameEditTextPreference()
    {
    }

    public void afterTextChanged(Editable paramEditable)
    {
      Dialog localDialog = DeviceProfilesSettings.this.mDeviceNamePref.getDialog();
      Button localButton;
      if ((localDialog instanceof AlertDialog))
      {
        localButton = ((AlertDialog)localDialog).getButton(-1);
        if (paramEditable.length() <= 0)
          break label46;
      }
      label46: for (boolean bool = true; ; bool = false)
      {
        localButton.setEnabled(bool);
        return;
      }
    }

    public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
    {
    }

    public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
    {
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.DeviceProfilesSettings
 * JD-Core Version:    0.6.2
 */