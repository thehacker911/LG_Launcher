package com.android.settings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemProperties;
import android.os.UserManager;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MasterClear extends Fragment
{
  private View mContentView;
  private CheckBox mExternalStorage;
  private View mExternalStorageContainer;
  private Button mInitiateButton;
  private final View.OnClickListener mInitiateListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      MasterClear.access$002(MasterClear.this, false);
      if (MasterClear.this.runRestrictionsChallenge());
      while (MasterClear.this.runKeyguardConfirmation(55))
        return;
      MasterClear.this.showFinalConfirmation();
    }
  };
  private boolean mPinConfirmed;

  private void establishInitialState()
  {
    this.mInitiateButton = ((Button)this.mContentView.findViewById(2131230911));
    this.mInitiateButton.setOnClickListener(this.mInitiateListener);
    this.mExternalStorageContainer = this.mContentView.findViewById(2131230909);
    this.mExternalStorage = ((CheckBox)this.mContentView.findViewById(2131230910));
    boolean bool1 = Environment.isExternalStorageEmulated();
    boolean bool2;
    if ((bool1) || ((!Environment.isExternalStorageRemovable()) && (isExtStorageEncrypted())))
    {
      this.mExternalStorageContainer.setVisibility(8);
      this.mContentView.findViewById(2131230908).setVisibility(8);
      this.mContentView.findViewById(2131230905).setVisibility(0);
      CheckBox localCheckBox = this.mExternalStorage;
      if (!bool1)
      {
        bool2 = true;
        localCheckBox.setChecked(bool2);
      }
    }
    while (true)
    {
      loadAccountList();
      return;
      bool2 = false;
      break;
      this.mExternalStorageContainer.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          MasterClear.this.mExternalStorage.toggle();
        }
      });
    }
  }

  private boolean isExtStorageEncrypted()
  {
    return !"".equals(SystemProperties.get("vold.decrypt"));
  }

  private void loadAccountList()
  {
    View localView = this.mContentView.findViewById(2131230906);
    LinearLayout localLinearLayout = (LinearLayout)this.mContentView.findViewById(2131230907);
    localLinearLayout.removeAllViews();
    Activity localActivity = getActivity();
    Account[] arrayOfAccount = AccountManager.get(localActivity).getAccounts();
    int i = arrayOfAccount.length;
    if (i == 0)
    {
      localView.setVisibility(8);
      localLinearLayout.setVisibility(8);
      return;
    }
    LayoutInflater localLayoutInflater = (LayoutInflater)localActivity.getSystemService("layout_inflater");
    AuthenticatorDescription[] arrayOfAuthenticatorDescription = AccountManager.get(localActivity).getAuthenticatorTypes();
    int j = arrayOfAuthenticatorDescription.length;
    int k = 0;
    if (k < i)
    {
      Account localAccount = arrayOfAccount[k];
      int m = 0;
      label109: int n = m;
      AuthenticatorDescription localAuthenticatorDescription = null;
      if (n < j)
      {
        if (localAccount.type.equals(arrayOfAuthenticatorDescription[m].type))
          localAuthenticatorDescription = arrayOfAuthenticatorDescription[m];
      }
      else
      {
        if (localAuthenticatorDescription != null)
          break label208;
        Log.w("MasterClear", "No descriptor for account name=" + localAccount.name + " type=" + localAccount.type);
      }
      while (true)
      {
        k++;
        break;
        m++;
        break label109;
        try
        {
          label208: int i1 = localAuthenticatorDescription.iconId;
          localObject = null;
          if (i1 != 0)
          {
            Drawable localDrawable = localActivity.createPackageContext(localAuthenticatorDescription.packageName, 0).getResources().getDrawable(localAuthenticatorDescription.iconId);
            localObject = localDrawable;
          }
          TextView localTextView = (TextView)localLayoutInflater.inflate(2130968648, localLinearLayout, false);
          localTextView.setText(localAccount.name);
          if (localObject != null)
            localTextView.setCompoundDrawablesWithIntrinsicBounds(localObject, null, null, null);
          localLinearLayout.addView(localTextView);
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          while (true)
          {
            Log.w("MasterClear", "No icon for account type " + localAuthenticatorDescription.type);
            Object localObject = null;
          }
        }
      }
    }
    localView.setVisibility(0);
    localLinearLayout.setVisibility(0);
  }

  private boolean runKeyguardConfirmation(int paramInt)
  {
    Resources localResources = getActivity().getResources();
    return new ChooseLockSettingsHelper(getActivity(), this).launchConfirmationActivity(paramInt, localResources.getText(2131428222), localResources.getText(2131428223));
  }

  private boolean runRestrictionsChallenge()
  {
    if (UserManager.get(getActivity()).hasRestrictionsChallenge())
    {
      startActivityForResult(new Intent("android.intent.action.RESTRICTIONS_CHALLENGE"), 56);
      return true;
    }
    return false;
  }

  private void showFinalConfirmation()
  {
    Preference localPreference = new Preference(getActivity());
    localPreference.setFragment(MasterClearConfirm.class.getName());
    localPreference.setTitle(2131428225);
    localPreference.getExtras().putBoolean("erase_sd", this.mExternalStorage.isChecked());
    ((PreferenceActivity)getActivity()).onPreferenceStartFragment(null, localPreference);
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (paramInt1 == 56)
      if (paramInt2 == -1)
        this.mPinConfirmed = true;
    while (paramInt1 != 55)
      return;
    if (paramInt2 == -1)
    {
      showFinalConfirmation();
      return;
    }
    establishInitialState();
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mContentView = paramLayoutInflater.inflate(2130968647, null);
    establishInitialState();
    return this.mContentView;
  }

  public void onResume()
  {
    super.onResume();
    if (this.mPinConfirmed)
    {
      this.mPinConfirmed = false;
      if (!runKeyguardConfirmation(55))
        showFinalConfirmation();
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.MasterClear
 * JD-Core Version:    0.6.2
 */