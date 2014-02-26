package com.android.settings.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.AccessibilityServiceInfo.CapabilityInfo;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ToggleAccessibilityServicePreferenceFragment extends ToggleFeaturePreferenceFragment
  implements DialogInterface.OnClickListener
{
  private ComponentName mComponentName;
  private final SettingsContentObserver mSettingsContentObserver = new SettingsContentObserver(new Handler())
  {
    public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
    {
      boolean bool = Settings.Secure.getString(ToggleAccessibilityServicePreferenceFragment.this.getContentResolver(), "enabled_accessibility_services").contains(ToggleAccessibilityServicePreferenceFragment.this.mComponentName.flattenToString());
      ToggleAccessibilityServicePreferenceFragment.this.mToggleSwitch.setCheckedInternal(bool);
    }
  };
  private int mShownDialogId;

  private View createEnableDialogContentView(AccessibilityServiceInfo paramAccessibilityServiceInfo)
  {
    LayoutInflater localLayoutInflater = (LayoutInflater)getSystemService("layout_inflater");
    View localView1 = localLayoutInflater.inflate(2130968633, null);
    TextView localTextView = (TextView)localView1.findViewById(2131230850);
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = paramAccessibilityServiceInfo.getResolveInfo().loadLabel(getPackageManager());
    localTextView.setText(getString(2131428677, arrayOfObject));
    LinearLayout localLinearLayout = (LinearLayout)localView1.findViewById(2131230851);
    View localView2 = localLayoutInflater.inflate(17367085, null);
    ((ImageView)localView2.findViewById(16908930)).setImageDrawable(getResources().getDrawable(17302424));
    ((TextView)localView2.findViewById(16908934)).setText(getString(2131428678));
    ((TextView)localView2.findViewById(16908935)).setText(getString(2131428679));
    List localList = paramAccessibilityServiceInfo.getCapabilityInfos();
    localLinearLayout.addView(localView2);
    int i = localList.size();
    for (int j = 0; j < i; j++)
    {
      AccessibilityServiceInfo.CapabilityInfo localCapabilityInfo = (AccessibilityServiceInfo.CapabilityInfo)localList.get(j);
      View localView3 = localLayoutInflater.inflate(17367085, null);
      ((ImageView)localView3.findViewById(16908930)).setImageDrawable(getResources().getDrawable(17302424));
      ((TextView)localView3.findViewById(16908934)).setText(getString(localCapabilityInfo.titleResId));
      ((TextView)localView3.findViewById(16908935)).setText(getString(localCapabilityInfo.descResId));
      localLinearLayout.addView(localView3);
    }
    return localView1;
  }

  private AccessibilityServiceInfo getAccessibilityServiceInfo()
  {
    List localList = AccessibilityManager.getInstance(getActivity()).getInstalledAccessibilityServiceList();
    int i = localList.size();
    for (int j = 0; j < i; j++)
    {
      AccessibilityServiceInfo localAccessibilityServiceInfo = (AccessibilityServiceInfo)localList.get(j);
      ResolveInfo localResolveInfo = localAccessibilityServiceInfo.getResolveInfo();
      if ((this.mComponentName.getPackageName().equals(localResolveInfo.serviceInfo.packageName)) && (this.mComponentName.getClassName().equals(localResolveInfo.serviceInfo.name)))
        return localAccessibilityServiceInfo;
    }
    return null;
  }

  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    int i = 1;
    boolean bool;
    switch (paramInt)
    {
    default:
      throw new IllegalArgumentException();
    case -1:
      if (this.mShownDialogId == i);
      while (true)
      {
        this.mToggleSwitch.setCheckedInternal(i);
        getArguments().putBoolean("checked", i);
        onPreferenceToggled(this.mPreferenceKey, i);
        return;
        bool = false;
      }
    case -2:
    }
    if (this.mShownDialogId == 2);
    while (true)
    {
      this.mToggleSwitch.setCheckedInternal(bool);
      getArguments().putBoolean("checked", bool);
      onPreferenceToggled(this.mPreferenceKey, bool);
      return;
      bool = false;
    }
  }

  public Dialog onCreateDialog(int paramInt)
  {
    switch (paramInt)
    {
    default:
      throw new IllegalArgumentException();
    case 1:
      this.mShownDialogId = 1;
      AccessibilityServiceInfo localAccessibilityServiceInfo2 = getAccessibilityServiceInfo();
      if (localAccessibilityServiceInfo2 == null)
        return null;
      AlertDialog.Builder localBuilder3 = new AlertDialog.Builder(getActivity());
      Object[] arrayOfObject3 = new Object[1];
      arrayOfObject3[0] = localAccessibilityServiceInfo2.getResolveInfo().loadLabel(getPackageManager());
      return localBuilder3.setTitle(getString(2131428676, arrayOfObject3)).setIconAttribute(16843605).setView(createEnableDialogContentView(localAccessibilityServiceInfo2)).setCancelable(true).setPositiveButton(17039370, this).setNegativeButton(17039360, this).create();
    case 2:
    }
    this.mShownDialogId = 2;
    AccessibilityServiceInfo localAccessibilityServiceInfo1 = getAccessibilityServiceInfo();
    if (localAccessibilityServiceInfo1 == null)
      return null;
    AlertDialog.Builder localBuilder1 = new AlertDialog.Builder(getActivity());
    Object[] arrayOfObject1 = new Object[1];
    arrayOfObject1[0] = localAccessibilityServiceInfo1.getResolveInfo().loadLabel(getPackageManager());
    AlertDialog.Builder localBuilder2 = localBuilder1.setTitle(getString(2131428680, arrayOfObject1)).setIconAttribute(16843605);
    Object[] arrayOfObject2 = new Object[1];
    arrayOfObject2[0] = localAccessibilityServiceInfo1.getResolveInfo().loadLabel(getPackageManager());
    return localBuilder2.setMessage(getString(2131428681, arrayOfObject2)).setCancelable(true).setPositiveButton(17039370, this).setNegativeButton(17039360, this).create();
  }

  protected void onInstallActionBarToggleSwitch()
  {
    super.onInstallActionBarToggleSwitch();
    this.mToggleSwitch.setOnBeforeCheckedChangeListener(new ToggleSwitch.OnBeforeCheckedChangeListener()
    {
      public boolean onBeforeCheckedChanged(ToggleSwitch paramAnonymousToggleSwitch, boolean paramAnonymousBoolean)
      {
        if (paramAnonymousBoolean)
        {
          paramAnonymousToggleSwitch.setCheckedInternal(false);
          ToggleAccessibilityServicePreferenceFragment.this.getArguments().putBoolean("checked", false);
          ToggleAccessibilityServicePreferenceFragment.this.showDialog(1);
          return true;
        }
        paramAnonymousToggleSwitch.setCheckedInternal(true);
        ToggleAccessibilityServicePreferenceFragment.this.getArguments().putBoolean("checked", true);
        ToggleAccessibilityServicePreferenceFragment.this.showDialog(2);
        return true;
      }
    });
  }

  public void onPause()
  {
    this.mSettingsContentObserver.unregister(getContentResolver());
    super.onPause();
  }

  public void onPreferenceToggled(String paramString, boolean paramBoolean)
  {
    Object localObject = AccessibilityUtils.getEnabledServicesFromSettings(getActivity());
    if (localObject == Collections.emptySet())
      localObject = new HashSet();
    ComponentName localComponentName = ComponentName.unflattenFromString(paramString);
    int i;
    if (paramBoolean)
    {
      ((Set)localObject).add(localComponentName);
      i = 1;
    }
    StringBuilder localStringBuilder;
    while (true)
    {
      localStringBuilder = new StringBuilder();
      Iterator localIterator2 = ((Set)localObject).iterator();
      while (localIterator2.hasNext())
      {
        localStringBuilder.append(((ComponentName)localIterator2.next()).flattenToString());
        localStringBuilder.append(':');
      }
      ((Set)localObject).remove(localComponentName);
      Set localSet = AccessibilitySettings.sInstalledServices;
      Iterator localIterator1 = ((Set)localObject).iterator();
      do
      {
        boolean bool = localIterator1.hasNext();
        i = 0;
        if (!bool)
          break;
      }
      while (!localSet.contains((ComponentName)localIterator1.next()));
      i = 1;
    }
    int j = localStringBuilder.length();
    if (j > 0)
      localStringBuilder.deleteCharAt(j - 1);
    Settings.Secure.putString(getContentResolver(), "enabled_accessibility_services", localStringBuilder.toString());
    ContentResolver localContentResolver = getContentResolver();
    if (i != 0);
    for (int k = 1; ; k = 0)
    {
      Settings.Secure.putInt(localContentResolver, "accessibility_enabled", k);
      return;
    }
  }

  protected void onProcessArguments(Bundle paramBundle)
  {
    super.onProcessArguments(paramBundle);
    String str1 = paramBundle.getString("settings_title");
    String str2 = paramBundle.getString("settings_component_name");
    if ((!TextUtils.isEmpty(str1)) && (!TextUtils.isEmpty(str2)))
    {
      Intent localIntent = new Intent("android.intent.action.MAIN").setComponent(ComponentName.unflattenFromString(str2.toString()));
      if (!getPackageManager().queryIntentActivities(localIntent, 0).isEmpty())
      {
        this.mSettingsTitle = str1;
        this.mSettingsIntent = localIntent;
        setHasOptionsMenu(true);
      }
    }
    this.mComponentName = ((ComponentName)paramBundle.getParcelable("component_name"));
  }

  public void onResume()
  {
    this.mSettingsContentObserver.register(getContentResolver());
    super.onResume();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment
 * JD-Core Version:    0.6.2
 */