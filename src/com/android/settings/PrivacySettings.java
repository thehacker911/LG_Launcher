package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.backup.IBackupManager;
import android.app.backup.IBackupManager.Stub;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings.Secure;

public class PrivacySettings extends SettingsPreferenceFragment
  implements DialogInterface.OnClickListener
{
  private CheckBoxPreference mAutoRestore;
  private CheckBoxPreference mBackup;
  private IBackupManager mBackupManager;
  private PreferenceScreen mConfigure;
  private Dialog mConfirmDialog;
  private int mDialogType;

  private void setBackupEnabled(boolean paramBoolean)
  {
    boolean bool1 = true;
    if (this.mBackupManager != null);
    CheckBoxPreference localCheckBoxPreference1;
    try
    {
      this.mBackupManager.setBackupEnabled(paramBoolean);
      this.mBackup.setChecked(paramBoolean);
      this.mAutoRestore.setEnabled(paramBoolean);
      this.mConfigure.setEnabled(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      localCheckBoxPreference1 = this.mBackup;
      if (paramBoolean)
        break label82;
    }
    boolean bool2 = bool1;
    localCheckBoxPreference1.setChecked(bool2);
    CheckBoxPreference localCheckBoxPreference2 = this.mAutoRestore;
    if (!paramBoolean);
    while (true)
    {
      localCheckBoxPreference2.setEnabled(bool1);
      return;
      label82: bool2 = false;
      break;
      bool1 = false;
    }
  }

  private void setConfigureSummary(String paramString)
  {
    if (paramString != null)
    {
      this.mConfigure.setSummary(paramString);
      return;
    }
    this.mConfigure.setSummary(2131428914);
  }

  private void showEraseBackupDialog()
  {
    this.mBackup.setChecked(true);
    this.mDialogType = 2;
    CharSequence localCharSequence = getResources().getText(2131428924);
    this.mConfirmDialog = new AlertDialog.Builder(getActivity()).setMessage(localCharSequence).setTitle(2131428923).setIconAttribute(16843605).setPositiveButton(17039370, this).setNegativeButton(17039360, this).show();
  }

  private void updateConfigureSummary()
  {
    try
    {
      String str = this.mBackupManager.getCurrentTransport();
      setConfigureSummary(this.mBackupManager.getDestinationString(str));
      return;
    }
    catch (RemoteException localRemoteException)
    {
    }
  }

  private void updateToggles()
  {
    ContentResolver localContentResolver = getContentResolver();
    boolean bool1 = false;
    Intent localIntent = null;
    try
    {
      bool1 = this.mBackupManager.isBackupEnabled();
      String str2 = this.mBackupManager.getCurrentTransport();
      localIntent = this.mBackupManager.getConfigurationIntent(str2);
      String str3 = this.mBackupManager.getDestinationString(str2);
      str1 = str3;
      this.mBackup.setChecked(bool1);
      CheckBoxPreference localCheckBoxPreference = this.mAutoRestore;
      if (Settings.Secure.getInt(localContentResolver, "backup_auto_restore", 1) == 1)
      {
        bool2 = true;
        localCheckBoxPreference.setChecked(bool2);
        this.mAutoRestore.setEnabled(bool1);
        if ((localIntent == null) || (!bool1))
          break label159;
        bool3 = true;
        this.mConfigure.setEnabled(bool3);
        this.mConfigure.setIntent(localIntent);
        setConfigureSummary(str1);
      }
    }
    catch (RemoteException localRemoteException)
    {
      while (true)
      {
        this.mBackup.setEnabled(false);
        String str1 = null;
        continue;
        boolean bool2 = false;
        continue;
        label159: boolean bool3 = false;
      }
    }
  }

  protected int getHelpResource()
  {
    return 2131429263;
  }

  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if ((paramInt == -1) && (this.mDialogType == 2))
    {
      setBackupEnabled(false);
      updateConfigureSummary();
    }
    this.mDialogType = 0;
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131034142);
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    this.mBackupManager = IBackupManager.Stub.asInterface(ServiceManager.getService("backup"));
    this.mBackup = ((CheckBoxPreference)localPreferenceScreen.findPreference("backup_data"));
    this.mAutoRestore = ((CheckBoxPreference)localPreferenceScreen.findPreference("auto_restore"));
    this.mConfigure = ((PreferenceScreen)localPreferenceScreen.findPreference("configure_account"));
    if (getActivity().getPackageManager().resolveContentProvider("com.google.settings", 0) == null)
      localPreferenceScreen.removePreference(findPreference("backup_category"));
    updateToggles();
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    boolean bool1 = true;
    if (paramPreference == this.mBackup)
      if (!this.mBackup.isChecked())
        showEraseBackupDialog();
    CheckBoxPreference localCheckBoxPreference;
    while (true)
    {
      return super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
      setBackupEnabled(bool1);
      continue;
      if (paramPreference == this.mAutoRestore)
      {
        boolean bool2 = this.mAutoRestore.isChecked();
        try
        {
          this.mBackupManager.setAutoRestore(bool2);
        }
        catch (RemoteException localRemoteException)
        {
          localCheckBoxPreference = this.mAutoRestore;
          if (bool2);
        }
      }
    }
    while (true)
    {
      localCheckBoxPreference.setChecked(bool1);
      break;
      bool1 = false;
    }
  }

  public void onResume()
  {
    super.onResume();
    updateToggles();
  }

  public void onStop()
  {
    if ((this.mConfirmDialog != null) && (this.mConfirmDialog.isShowing()))
      this.mConfirmDialog.dismiss();
    this.mConfirmDialog = null;
    this.mDialogType = 0;
    super.onStop();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.PrivacySettings
 * JD-Core Version:    0.6.2
 */