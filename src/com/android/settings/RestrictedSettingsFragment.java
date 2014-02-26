package com.android.settings;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.UserManager;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import java.util.HashSet;

public class RestrictedSettingsFragment extends SettingsPreferenceFragment
{
  private boolean mChallengeRequested;
  private boolean mChallengeSucceeded;
  private final HashSet<Preference> mProtectedByRestictionsPrefs = new HashSet();
  private final String mRestrictionKey;
  private Bundle mResumeActionBundle;
  private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      RestrictedSettingsFragment.access$002(RestrictedSettingsFragment.this, false);
      if (RestrictedSettingsFragment.this.shouldBePinProtected(RestrictedSettingsFragment.this.mRestrictionKey))
        RestrictedSettingsFragment.this.ensurePin(null);
    }
  };
  private UserManager mUserManager;

  public RestrictedSettingsFragment(String paramString)
  {
    this.mRestrictionKey = paramString;
  }

  private void ensurePin(Preference paramPreference)
  {
    if (!this.mChallengeSucceeded)
    {
      UserManager localUserManager = UserManager.get(getActivity());
      if ((!this.mChallengeRequested) && (localUserManager.hasRestrictionsChallenge()))
      {
        this.mResumeActionBundle = new Bundle();
        if (paramPreference != null)
        {
          this.mResumeActionBundle.putString("pref", paramPreference.getKey());
          if ((paramPreference instanceof CheckBoxPreference))
            this.mResumeActionBundle.putBoolean("isChecked", ((CheckBoxPreference)paramPreference).isChecked());
        }
        startActivityForResult(new Intent("android.intent.action.RESTRICTIONS_CHALLENGE"), 12309);
        this.mChallengeRequested = true;
      }
    }
    this.mChallengeSucceeded = false;
  }

  boolean ensurePinRestrictedPreference(Preference paramPreference)
  {
    return (this.mProtectedByRestictionsPrefs.contains(paramPreference)) && (!restrictionsPinCheck("restrictions_pin_set", paramPreference));
  }

  protected boolean isRestrictedAndNotPinProtected()
  {
    if ((this.mRestrictionKey == null) || ("restrictions_pin_set".equals(this.mRestrictionKey)));
    while ((!this.mUserManager.hasUserRestriction(this.mRestrictionKey)) || (this.mUserManager.hasRestrictionsChallenge()))
      return false;
    return true;
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt1 == 12309)
    {
      Bundle localBundle = this.mResumeActionBundle;
      this.mResumeActionBundle = null;
      this.mChallengeRequested = false;
      if (paramInt2 == -1)
      {
        this.mChallengeSucceeded = true;
        localObject = null;
        if (localBundle == null)
          if (localObject != null)
          {
            localPreference = findPreference((CharSequence)localObject);
            if (localPreference != null)
            {
              if (((localPreference instanceof CheckBoxPreference)) && (localBundle.containsKey("isChecked")))
              {
                bool = localBundle.getBoolean("isChecked", false);
                ((CheckBoxPreference)localPreference).setChecked(bool);
              }
              if (!onPreferenceTreeClick(getPreferenceScreen(), localPreference))
              {
                localIntent = localPreference.getIntent();
                if (localIntent != null)
                  localPreference.getContext().startActivity(localIntent);
              }
            }
          }
      }
      while (isDetached())
        while (true)
        {
          Preference localPreference;
          boolean bool;
          Intent localIntent;
          return;
          Object localObject = localBundle.getString("pref");
        }
      finishFragment();
      return;
    }
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mUserManager = ((UserManager)getSystemService("user"));
    if (paramBundle != null)
    {
      this.mChallengeSucceeded = paramBundle.getBoolean("chsc", false);
      this.mChallengeRequested = paramBundle.getBoolean("chrq", false);
      this.mResumeActionBundle = paramBundle.getBundle("rsmb");
    }
  }

  public void onPause()
  {
    super.onPause();
    getActivity().unregisterReceiver(this.mScreenOffReceiver);
  }

  public void onResume()
  {
    super.onResume();
    if (shouldBePinProtected(this.mRestrictionKey))
      ensurePin(null);
    while (true)
    {
      IntentFilter localIntentFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
      localIntentFilter.addAction("android.intent.action.USER_PRESENT");
      getActivity().registerReceiver(this.mScreenOffReceiver, localIntentFilter);
      return;
      this.mChallengeSucceeded = false;
    }
  }

  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putBoolean("chrq", this.mChallengeRequested);
    if (this.mResumeActionBundle != null)
      paramBundle.putBundle("rsmb", this.mResumeActionBundle);
    if (getActivity().isChangingConfigurations())
      paramBundle.putBoolean("chsc", this.mChallengeSucceeded);
  }

  protected void protectByRestrictions(Preference paramPreference)
  {
    if (paramPreference != null)
      this.mProtectedByRestictionsPrefs.add(paramPreference);
  }

  protected void protectByRestrictions(String paramString)
  {
    protectByRestrictions(findPreference(paramString));
  }

  protected boolean restrictionsPinCheck(String paramString, Preference paramPreference)
  {
    if ((shouldBePinProtected(paramString)) && (!this.mChallengeSucceeded))
    {
      ensurePin(paramPreference);
      return false;
    }
    return true;
  }

  protected boolean shouldBePinProtected(String paramString)
  {
    boolean bool1 = true;
    if (paramString == null)
      return false;
    boolean bool2;
    if (("restrictions_pin_set".equals(paramString)) || (this.mUserManager.hasUserRestriction(paramString)))
    {
      bool2 = bool1;
      if ((!bool2) || (!this.mUserManager.hasRestrictionsChallenge()))
        break label51;
    }
    while (true)
    {
      return bool1;
      bool2 = false;
      break;
      label51: bool1 = false;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.RestrictedSettingsFragment
 * JD-Core Version:    0.6.2
 */