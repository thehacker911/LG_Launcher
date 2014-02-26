package com.android.settings.applications;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.AppOpsManager.OpEntry;
import android.app.AppOpsManager.PackageOps;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.android.settings.Utils;
import java.util.Iterator;
import java.util.List;

public class AppOpsDetails extends Fragment
{
  private AppOpsManager mAppOps;
  private TextView mAppVersion;
  private LayoutInflater mInflater;
  private LinearLayout mOperationsSection;
  private PackageInfo mPackageInfo;
  private PackageManager mPm;
  private View mRootView;
  private AppOpsState mState;

  private boolean refreshUi()
  {
    if (this.mPackageInfo == null)
      return false;
    setAppLabelAndIcon(this.mPackageInfo);
    Resources localResources = getActivity().getResources();
    this.mOperationsSection.removeAllViews();
    String str1 = "";
    AppOpsState.OpsTemplate[] arrayOfOpsTemplate = AppOpsState.ALL_TEMPLATES;
    int i = arrayOfOpsTemplate.length;
    int j = 0;
    while (true)
    {
      final AppOpsState.AppOpEntry localAppOpEntry;
      AppOpsManager.OpEntry localOpEntry;
      View localView;
      String str2;
      if (j < i)
      {
        AppOpsState.OpsTemplate localOpsTemplate = arrayOfOpsTemplate[j];
        Iterator localIterator = this.mState.buildState(localOpsTemplate, this.mPackageInfo.applicationInfo.uid, this.mPackageInfo.packageName).iterator();
        if (localIterator.hasNext())
        {
          localAppOpEntry = (AppOpsState.AppOpEntry)localIterator.next();
          localOpEntry = localAppOpEntry.getOpEntry(0);
          localView = this.mInflater.inflate(2130968583, this.mOperationsSection, false);
          this.mOperationsSection.addView(localView);
          str2 = AppOpsManager.opToPermission(localOpEntry.getOp());
          if (str2 == null);
        }
      }
      try
      {
        PermissionInfo localPermissionInfo = this.mPm.getPermissionInfo(str2, 0);
        if ((localPermissionInfo.group != null) && (!str1.equals(localPermissionInfo.group)))
        {
          str1 = localPermissionInfo.group;
          PermissionGroupInfo localPermissionGroupInfo = this.mPm.getPermissionGroupInfo(localPermissionInfo.group, 0);
          if (localPermissionGroupInfo.icon != 0)
            ((ImageView)localView.findViewById(2131230731)).setImageDrawable(localPermissionGroupInfo.loadIcon(this.mPm));
        }
        label245: ((TextView)localView.findViewById(2131230732)).setText(localAppOpEntry.getSwitchText(this.mState));
        ((TextView)localView.findViewById(2131230733)).setText(localAppOpEntry.getTimeText(localResources, true));
        Switch localSwitch = (Switch)localView.findViewById(2131230734);
        final int k = AppOpsManager.opToSwitch(localOpEntry.getOp());
        if (this.mAppOps.checkOp(k, localAppOpEntry.getPackageOps().getUid(), localAppOpEntry.getPackageOps().getPackageName()) == 0);
        for (boolean bool = true; ; bool = false)
        {
          localSwitch.setChecked(bool);
          CompoundButton.OnCheckedChangeListener local1 = new CompoundButton.OnCheckedChangeListener()
          {
            public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
            {
              AppOpsManager localAppOpsManager = AppOpsDetails.this.mAppOps;
              int i = k;
              int j = localAppOpEntry.getPackageOps().getUid();
              String str = localAppOpEntry.getPackageOps().getPackageName();
              if (paramAnonymousBoolean);
              for (int k = 0; ; k = 1)
              {
                localAppOpsManager.setMode(i, j, str, k);
                return;
              }
            }
          };
          localSwitch.setOnCheckedChangeListener(local1);
          break;
        }
        j++;
        continue;
        return true;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        break label245;
      }
    }
  }

  private String retrieveAppEntry()
  {
    Bundle localBundle = getArguments();
    String str;
    if (localBundle != null)
      str = localBundle.getString("package");
    while (true)
    {
      Intent localIntent;
      if (str == null)
      {
        if (localBundle != null)
          break label69;
        localIntent = getActivity().getIntent();
        if (localIntent != null)
          str = localIntent.getData().getSchemeSpecificPart();
      }
      try
      {
        this.mPackageInfo = this.mPm.getPackageInfo(str, 8704);
        return str;
        str = null;
        continue;
        label69: localIntent = (Intent)localBundle.getParcelable("intent");
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        Log.e("AppOpsDetails", "Exception when retrieving package:" + str, localNameNotFoundException);
        this.mPackageInfo = null;
      }
    }
    return str;
  }

  private void setAppLabelAndIcon(PackageInfo paramPackageInfo)
  {
    View localView = this.mRootView.findViewById(2131230729);
    localView.setPaddingRelative(0, localView.getPaddingTop(), 0, localView.getPaddingBottom());
    ((ImageView)localView.findViewById(2131230735)).setImageDrawable(this.mPm.getApplicationIcon(paramPackageInfo.applicationInfo));
    ((TextView)localView.findViewById(2131230736)).setText(this.mPm.getApplicationLabel(paramPackageInfo.applicationInfo));
    this.mAppVersion = ((TextView)localView.findViewById(2131230902));
    if (paramPackageInfo.versionName != null)
    {
      this.mAppVersion.setVisibility(0);
      TextView localTextView = this.mAppVersion;
      Activity localActivity = getActivity();
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = String.valueOf(paramPackageInfo.versionName);
      localTextView.setText(localActivity.getString(2131428440, arrayOfObject));
      return;
    }
    this.mAppVersion.setVisibility(4);
  }

  private void setIntentAndFinish(boolean paramBoolean1, boolean paramBoolean2)
  {
    Intent localIntent = new Intent();
    localIntent.putExtra("chg", paramBoolean2);
    ((PreferenceActivity)getActivity()).finishPreferencePanel(this, -1, localIntent);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mState = new AppOpsState(getActivity());
    this.mPm = getActivity().getPackageManager();
    this.mInflater = ((LayoutInflater)getActivity().getSystemService("layout_inflater"));
    this.mAppOps = ((AppOpsManager)getActivity().getSystemService("appops"));
    retrieveAppEntry();
    setHasOptionsMenu(true);
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    View localView = paramLayoutInflater.inflate(2130968582, paramViewGroup, false);
    Utils.prepareCustomPreferencesList(paramViewGroup, localView, localView, false);
    this.mRootView = localView;
    this.mOperationsSection = ((LinearLayout)localView.findViewById(2131230730));
    return localView;
  }

  public void onResume()
  {
    super.onResume();
    if (!refreshUi())
      setIntentAndFinish(true, true);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.applications.AppOpsDetails
 * JD-Core Version:    0.6.2
 */