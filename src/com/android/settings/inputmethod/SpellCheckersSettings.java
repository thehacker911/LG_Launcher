package com.android.settings.inputmethod;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;
import android.view.textservice.SpellCheckerInfo;
import android.view.textservice.TextServicesManager;
import com.android.settings.SettingsPreferenceFragment;
import java.util.ArrayList;
import java.util.Iterator;

public class SpellCheckersSettings extends SettingsPreferenceFragment
  implements Preference.OnPreferenceClickListener
{
  private static final String TAG = SpellCheckersSettings.class.getSimpleName();
  private SpellCheckerInfo mCurrentSci;
  private AlertDialog mDialog = null;
  private SpellCheckerInfo[] mEnabledScis;
  private final ArrayList<SingleSpellCheckerPreference> mSpellCheckers = new ArrayList();
  private TextServicesManager mTsm;

  private void changeCurrentSpellChecker(SingleSpellCheckerPreference paramSingleSpellCheckerPreference)
  {
    this.mTsm.setCurrentSpellChecker(paramSingleSpellCheckerPreference.getSpellCheckerInfo());
    updateScreen();
  }

  private static boolean isSystemApp(SpellCheckerInfo paramSpellCheckerInfo)
  {
    return (0x1 & paramSpellCheckerInfo.getServiceInfo().applicationInfo.flags) != 0;
  }

  private void saveState()
  {
    SpellCheckerUtils.setCurrentSpellChecker(this.mTsm, this.mCurrentSci);
  }

  private void showSecurityWarnDialog(final SingleSpellCheckerPreference paramSingleSpellCheckerPreference)
  {
    if ((this.mDialog != null) && (this.mDialog.isShowing()))
      this.mDialog.dismiss();
    this.mDialog = new AlertDialog.Builder(getActivity()).setTitle(17039380).setIconAttribute(16843605).setCancelable(true).setPositiveButton(17039370, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        SpellCheckersSettings.this.changeCurrentSpellChecker(paramSingleSpellCheckerPreference);
      }
    }).setNegativeButton(17039360, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
      }
    }).create();
    AlertDialog localAlertDialog = this.mDialog;
    Resources localResources = getResources();
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = paramSingleSpellCheckerPreference.getSpellCheckerInfo().getServiceInfo().applicationInfo.loadLabel(getActivity().getPackageManager());
    localAlertDialog.setMessage(localResources.getString(2131428514, arrayOfObject));
    this.mDialog.show();
  }

  private void updateEnabledSpellCheckers()
  {
    PackageManager localPackageManager = getPackageManager();
    this.mCurrentSci = SpellCheckerUtils.getCurrentSpellChecker(this.mTsm);
    this.mEnabledScis = SpellCheckerUtils.getEnabledSpellCheckers(this.mTsm);
    if ((this.mCurrentSci == null) || (this.mEnabledScis == null))
      return;
    this.mSpellCheckers.clear();
    int i = 0;
    label51: SingleSpellCheckerPreference localSingleSpellCheckerPreference;
    if (i < this.mEnabledScis.length)
    {
      SpellCheckerInfo localSpellCheckerInfo = this.mEnabledScis[i];
      localSingleSpellCheckerPreference = new SingleSpellCheckerPreference(this, null, localSpellCheckerInfo, this.mTsm);
      this.mSpellCheckers.add(localSingleSpellCheckerPreference);
      localSingleSpellCheckerPreference.setTitle(localSpellCheckerInfo.loadLabel(localPackageManager));
      if ((this.mCurrentSci == null) || (!this.mCurrentSci.getId().equals(localSpellCheckerInfo.getId())))
        break label153;
    }
    label153: for (boolean bool = true; ; bool = false)
    {
      localSingleSpellCheckerPreference.setSelected(bool);
      getPreferenceScreen().addPreference(localSingleSpellCheckerPreference);
      i++;
      break label51;
      break;
    }
  }

  private void updateScreen()
  {
    getPreferenceScreen().removeAll();
    updateEnabledSpellCheckers();
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mTsm = ((TextServicesManager)getSystemService("textservices"));
    addPreferencesFromResource(2131034159);
    updateScreen();
  }

  public void onPause()
  {
    super.onPause();
    saveState();
  }

  public boolean onPreferenceClick(Preference paramPreference)
  {
    Object localObject = null;
    Iterator localIterator = this.mSpellCheckers.iterator();
    while (localIterator.hasNext())
    {
      SingleSpellCheckerPreference localSingleSpellCheckerPreference = (SingleSpellCheckerPreference)localIterator.next();
      if (paramPreference.equals(localSingleSpellCheckerPreference))
        localObject = localSingleSpellCheckerPreference;
    }
    if (localObject != null)
    {
      if (isSystemApp(localObject.getSpellCheckerInfo()))
        break label66;
      showSecurityWarnDialog(localObject);
    }
    while (true)
    {
      return true;
      label66: changeCurrentSpellChecker(localObject);
    }
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    return false;
  }

  public void onResume()
  {
    super.onResume();
    updateScreen();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.inputmethod.SpellCheckersSettings
 * JD-Core Version:    0.6.2
 */