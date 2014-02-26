package com.android.settings;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.security.KeyStore;
import android.util.EventLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.android.internal.widget.LockPatternUtils;
import java.util.List;
import libcore.util.MutableBoolean;

public class ChooseLockGeneric extends PreferenceActivity
{
  public Intent getIntent()
  {
    Intent localIntent = new Intent(super.getIntent());
    localIntent.putExtra(":android:show_fragment", ChooseLockGenericFragment.class.getName());
    localIntent.putExtra(":android:no_headers", true);
    return localIntent;
  }

  protected boolean isValidFragment(String paramString)
  {
    return ChooseLockGenericFragment.class.getName().equals(paramString);
  }

  public static class ChooseLockGenericFragment extends SettingsPreferenceFragment
  {
    private ChooseLockSettingsHelper mChooseLockSettingsHelper;
    private DevicePolicyManager mDPM;
    private boolean mFinishPending = false;
    private KeyStore mKeyStore;
    private boolean mPasswordConfirmed = false;
    private boolean mWaitingForConfirmation = false;

    private boolean allowedForFallback(String paramString)
    {
      return ("unlock_backup_info".equals(paramString)) || ("unlock_set_pattern".equals(paramString)) || ("unlock_set_pin".equals(paramString));
    }

    private void disableUnusablePreferences(int paramInt, MutableBoolean paramMutableBoolean)
    {
      PreferenceScreen localPreferenceScreen = getPreferenceScreen();
      boolean bool1 = getActivity().getIntent().getBooleanExtra("lockscreen.biometric_weak_fallback", false);
      boolean bool2 = this.mChooseLockSettingsHelper.utils().isBiometricWeakInstalled();
      int i;
      int j;
      label65: Preference localPreference;
      String str;
      int k;
      int m;
      if (((UserManager)getSystemService("user")).getUsers(true).size() == 1)
      {
        i = 1;
        j = -1 + localPreferenceScreen.getPreferenceCount();
        if (j < 0)
          return;
        localPreference = localPreferenceScreen.getPreference(j);
        if ((localPreference instanceof PreferenceScreen))
        {
          str = ((PreferenceScreen)localPreference).getKey();
          k = 1;
          m = 1;
          if (!"unlock_set_off".equals(str))
            break label167;
          if (paramInt > 0)
            break label161;
          k = 1;
          label119: m = i;
          label123: if ((m != 0) && ((!bool1) || (allowedForFallback(str))))
            break label316;
          localPreferenceScreen.removePreference(localPreference);
        }
      }
      while (true)
      {
        j--;
        break label65;
        i = 0;
        break;
        label161: k = 0;
        break label119;
        label167: if ("unlock_set_none".equals(str))
        {
          if (paramInt <= 0);
          for (k = 1; ; k = 0)
            break;
        }
        if ("unlock_set_biometric_weak".equals(str))
        {
          if ((paramInt <= 32768) || (paramMutableBoolean.value));
          for (k = 1; ; k = 0)
          {
            m = bool2;
            break;
          }
        }
        if ("unlock_set_pattern".equals(str))
        {
          if (paramInt <= 65536);
          for (k = 1; ; k = 0)
            break;
        }
        if ("unlock_set_pin".equals(str))
        {
          if (paramInt <= 131072);
          for (k = 1; ; k = 0)
            break;
        }
        if (!"unlock_set_password".equals(str))
          break label123;
        if (paramInt <= 393216);
        for (k = 1; ; k = 0)
          break;
        label316: if (k == 0)
        {
          localPreference.setSummary(2131427661);
          localPreference.setEnabled(false);
        }
      }
    }

    private Intent getBiometricSensorIntent()
    {
      Intent localIntent1 = new Intent().setClass(getActivity(), ChooseLockGeneric.InternalActivity.class);
      localIntent1.putExtra("lockscreen.biometric_weak_fallback", true);
      localIntent1.putExtra("confirm_credentials", false);
      localIntent1.putExtra(":android:show_fragment_title", 2131427643);
      Intent localIntent2 = new Intent();
      localIntent2.setClassName("com.android.facelock", "com.android.facelock.SetupIntro");
      localIntent2.putExtra("showTutorial", true);
      localIntent2.putExtra("PendingIntent", PendingIntent.getActivity(getActivity(), 0, localIntent1, 0));
      return localIntent2;
    }

    private void updatePreferencesOrFinish()
    {
      Intent localIntent = getActivity().getIntent();
      int i = localIntent.getIntExtra("lockscreen.password_type", -1);
      if (i == -1)
      {
        int j = localIntent.getIntExtra("minimum_quality", -1);
        MutableBoolean localMutableBoolean = new MutableBoolean(false);
        int k = upgradeQuality(j, localMutableBoolean);
        PreferenceScreen localPreferenceScreen = getPreferenceScreen();
        if (localPreferenceScreen != null)
          localPreferenceScreen.removeAll();
        addPreferencesFromResource(2131034153);
        disableUnusablePreferences(k, localMutableBoolean);
        return;
      }
      updateUnlockMethodAndFinish(i, false);
    }

    private int upgradeQuality(int paramInt, MutableBoolean paramMutableBoolean)
    {
      int i = upgradeQualityForKeyStore(upgradeQualityForDPM(paramInt));
      int j = upgradeQualityForEncryption(i);
      if (j > i)
      {
        if (paramMutableBoolean == null)
          break label51;
        if (i > 32768)
          break label45;
        bool = true;
        paramMutableBoolean.value = bool;
      }
      label45: label51: 
      while (i != 32768)
        while (true)
        {
          return j;
          boolean bool = false;
        }
      return i;
    }

    private int upgradeQualityForDPM(int paramInt)
    {
      int i = this.mDPM.getPasswordQuality(null);
      if (paramInt < i)
        paramInt = i;
      return paramInt;
    }

    private int upgradeQualityForEncryption(int paramInt)
    {
      if (!Process.myUserHandle().equals(UserHandle.OWNER))
        return paramInt;
      int i = this.mDPM.getStorageEncryptionStatus();
      if ((i == 3) || (i == 2));
      for (int j = 1; ; j = 0)
      {
        if ((j != 0) && (paramInt < 131072))
          paramInt = 131072;
        return paramInt;
      }
    }

    private int upgradeQualityForKeyStore(int paramInt)
    {
      if ((!this.mKeyStore.isEmpty()) && (paramInt < 65536))
        paramInt = 65536;
      return paramInt;
    }

    protected int getHelpResource()
    {
      return 2131429262;
    }

    public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
    {
      super.onActivityResult(paramInt1, paramInt2, paramIntent);
      this.mWaitingForConfirmation = false;
      if ((paramInt1 == 100) && (paramInt2 == -1))
      {
        this.mPasswordConfirmed = true;
        updatePreferencesOrFinish();
        return;
      }
      if (paramInt1 == 101)
      {
        this.mChooseLockSettingsHelper.utils().deleteTempGallery();
        getActivity().setResult(paramInt2);
        finish();
        return;
      }
      getActivity().setResult(0);
      finish();
    }

    public void onCreate(Bundle paramBundle)
    {
      super.onCreate(paramBundle);
      this.mDPM = ((DevicePolicyManager)getSystemService("device_policy"));
      this.mKeyStore = KeyStore.getInstance();
      this.mChooseLockSettingsHelper = new ChooseLockSettingsHelper(getActivity());
      boolean bool1 = getActivity().getIntent().getBooleanExtra("confirm_credentials", true);
      boolean bool2;
      if ((getActivity() instanceof ChooseLockGeneric.InternalActivity))
      {
        if (!bool1)
        {
          bool2 = true;
          this.mPasswordConfirmed = bool2;
        }
      }
      else
      {
        if (paramBundle != null)
        {
          this.mPasswordConfirmed = paramBundle.getBoolean("password_confirmed");
          this.mWaitingForConfirmation = paramBundle.getBoolean("waiting_for_confirmation");
          this.mFinishPending = paramBundle.getBoolean("finish_pending");
        }
        if (!this.mPasswordConfirmed)
          break label130;
        updatePreferencesOrFinish();
      }
      label130: 
      while (this.mWaitingForConfirmation)
      {
        return;
        bool2 = false;
        break;
      }
      if (!new ChooseLockSettingsHelper(getActivity(), this).launchConfirmationActivity(100, null, null))
      {
        this.mPasswordConfirmed = true;
        updatePreferencesOrFinish();
        return;
      }
      this.mWaitingForConfirmation = true;
    }

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      View localView1 = super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
      if (getActivity().getIntent().getBooleanExtra("lockscreen.biometric_weak_fallback", false))
      {
        View localView2 = View.inflate(getActivity(), 2130968730, null);
        ((ListView)localView1.findViewById(16908298)).addHeaderView(localView2, null, false);
      }
      return localView1;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
    {
      String str = paramPreference.getKey();
      EventLog.writeEvent(90200, str);
      if ("unlock_set_off".equals(str))
      {
        updateUnlockMethodAndFinish(0, true);
        return true;
      }
      if ("unlock_set_none".equals(str))
      {
        updateUnlockMethodAndFinish(0, false);
        return true;
      }
      if ("unlock_set_biometric_weak".equals(str))
      {
        updateUnlockMethodAndFinish(32768, false);
        return true;
      }
      if ("unlock_set_pattern".equals(str))
      {
        updateUnlockMethodAndFinish(65536, false);
        return true;
      }
      if ("unlock_set_pin".equals(str))
      {
        updateUnlockMethodAndFinish(131072, false);
        return true;
      }
      if ("unlock_set_password".equals(str))
      {
        updateUnlockMethodAndFinish(262144, false);
        return true;
      }
      return false;
    }

    public void onResume()
    {
      super.onResume();
      if (this.mFinishPending)
      {
        this.mFinishPending = false;
        finish();
      }
    }

    public void onSaveInstanceState(Bundle paramBundle)
    {
      super.onSaveInstanceState(paramBundle);
      paramBundle.putBoolean("password_confirmed", this.mPasswordConfirmed);
      paramBundle.putBoolean("waiting_for_confirmation", this.mWaitingForConfirmation);
      paramBundle.putBoolean("finish_pending", this.mFinishPending);
    }

    void updateUnlockMethodAndFinish(int paramInt, boolean paramBoolean)
    {
      int i = 4;
      if (!this.mPasswordConfirmed)
        throw new IllegalStateException("Tried to update password without confirming it");
      boolean bool = getActivity().getIntent().getBooleanExtra("lockscreen.biometric_weak_fallback", false);
      int j = upgradeQuality(paramInt, null);
      int k;
      if (j >= 131072)
      {
        k = this.mDPM.getPasswordMinimumLength(null);
        if (k >= i)
          break label340;
      }
      while (true)
      {
        int m = this.mDPM.getPasswordMaximumLength(j);
        Intent localIntent3 = new Intent().setClass(getActivity(), ChooseLockPassword.class);
        localIntent3.putExtra("lockscreen.password_type", j);
        localIntent3.putExtra("lockscreen.password_min", i);
        localIntent3.putExtra("lockscreen.password_max", m);
        localIntent3.putExtra("confirm_credentials", false);
        localIntent3.putExtra("lockscreen.biometric_weak_fallback", bool);
        if (bool)
        {
          startActivityForResult(localIntent3, 101);
          return;
        }
        this.mFinishPending = true;
        localIntent3.addFlags(33554432);
        startActivity(localIntent3);
        return;
        if (j == 65536)
        {
          Intent localIntent1 = new Intent(getActivity(), ChooseLockPattern.class);
          localIntent1.putExtra("key_lock_method", "pattern");
          localIntent1.putExtra("confirm_credentials", false);
          localIntent1.putExtra("lockscreen.biometric_weak_fallback", bool);
          if (bool)
          {
            startActivityForResult(localIntent1, 101);
            return;
          }
          this.mFinishPending = true;
          localIntent1.addFlags(33554432);
          startActivity(localIntent1);
          return;
        }
        if (j == 32768)
        {
          Intent localIntent2 = getBiometricSensorIntent();
          this.mFinishPending = true;
          startActivity(localIntent2);
          return;
        }
        if (j == 0)
        {
          this.mChooseLockSettingsHelper.utils().clearLock(false);
          this.mChooseLockSettingsHelper.utils().setLockScreenDisabled(paramBoolean);
          getActivity().setResult(-1);
          finish();
          return;
        }
        finish();
        return;
        label340: i = k;
      }
    }
  }

  public static class InternalActivity extends ChooseLockGeneric
  {
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.ChooseLockGeneric
 * JD-Core Version:    0.6.2
 */