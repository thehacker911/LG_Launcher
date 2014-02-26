package com.android.settings;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

public class SettingsPreferenceFragment extends PreferenceFragment
  implements DialogCreatable
{
  private ContentResolver mContentResolver;
  private SettingsDialogFragment mDialogFragment;
  private String mHelpUrl;

  public void finish()
  {
    getActivity().onBackPressed();
  }

  public final void finishFragment()
  {
    getActivity().onBackPressed();
  }

  protected ContentResolver getContentResolver()
  {
    Activity localActivity = getActivity();
    if (localActivity != null)
      this.mContentResolver = localActivity.getContentResolver();
    return this.mContentResolver;
  }

  protected int getHelpResource()
  {
    return 0;
  }

  protected Button getNextButton()
  {
    return ((ButtonBarHandler)getActivity()).getNextButton();
  }

  protected PackageManager getPackageManager()
  {
    return getActivity().getPackageManager();
  }

  protected Object getSystemService(String paramString)
  {
    return getActivity().getSystemService(paramString);
  }

  protected boolean hasNextButton()
  {
    return ((ButtonBarHandler)getActivity()).hasNextButton();
  }

  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    if (!TextUtils.isEmpty(this.mHelpUrl))
      setHasOptionsMenu(true);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    int i = getHelpResource();
    if (i != 0)
      this.mHelpUrl = getResources().getString(i);
  }

  public Dialog onCreateDialog(int paramInt)
  {
    return null;
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    if ((this.mHelpUrl != null) && (getActivity() != null))
    {
      MenuItem localMenuItem = paramMenu.add(0, 101, 0, 2131429253);
      HelpUtils.prepareHelpMenuItem(getActivity(), localMenuItem, this.mHelpUrl);
    }
  }

  public void onDetach()
  {
    if ((isRemoving()) && (this.mDialogFragment != null))
    {
      this.mDialogFragment.dismiss();
      this.mDialogFragment = null;
    }
    super.onDetach();
  }

  public void onDialogShowing()
  {
  }

  protected void removeDialog(int paramInt)
  {
    if ((this.mDialogFragment != null) && (this.mDialogFragment.getDialogId() == paramInt))
      this.mDialogFragment.dismiss();
    this.mDialogFragment = null;
  }

  protected void removePreference(String paramString)
  {
    Preference localPreference = findPreference(paramString);
    if (localPreference != null)
      getPreferenceScreen().removePreference(localPreference);
  }

  protected void setOnDismissListener(DialogInterface.OnDismissListener paramOnDismissListener)
  {
    if (this.mDialogFragment != null)
      SettingsDialogFragment.access$102(this.mDialogFragment, paramOnDismissListener);
  }

  protected void showDialog(int paramInt)
  {
    if (this.mDialogFragment != null)
      Log.e("SettingsPreferenceFragment", "Old dialog fragment not null!");
    this.mDialogFragment = new SettingsDialogFragment(this, paramInt);
    this.mDialogFragment.show(getActivity().getFragmentManager(), Integer.toString(paramInt));
  }

  public boolean startFragment(Fragment paramFragment, String paramString, int paramInt, Bundle paramBundle)
  {
    if ((getActivity() instanceof PreferenceActivity))
    {
      ((PreferenceActivity)getActivity()).startPreferencePanel(paramString, paramBundle, 2131427642, null, paramFragment, paramInt);
      return true;
    }
    Log.w("SettingsPreferenceFragment", "Parent isn't PreferenceActivity, thus there's no way to launch the given Fragment (name: " + paramString + ", requestCode: " + paramInt + ")");
    return false;
  }

  public static class SettingsDialogFragment extends DialogFragment
  {
    private int mDialogId;
    private DialogInterface.OnCancelListener mOnCancelListener;
    private DialogInterface.OnDismissListener mOnDismissListener;
    private Fragment mParentFragment;

    public SettingsDialogFragment()
    {
    }

    public SettingsDialogFragment(DialogCreatable paramDialogCreatable, int paramInt)
    {
      this.mDialogId = paramInt;
      if (!(paramDialogCreatable instanceof Fragment))
        throw new IllegalArgumentException("fragment argument must be an instance of " + Fragment.class.getName());
      this.mParentFragment = ((Fragment)paramDialogCreatable);
    }

    public int getDialogId()
    {
      return this.mDialogId;
    }

    public void onCancel(DialogInterface paramDialogInterface)
    {
      super.onCancel(paramDialogInterface);
      if (this.mOnCancelListener != null)
        this.mOnCancelListener.onCancel(paramDialogInterface);
    }

    public Dialog onCreateDialog(Bundle paramBundle)
    {
      if (paramBundle != null)
      {
        this.mDialogId = paramBundle.getInt("key_dialog_id", 0);
        int i = paramBundle.getInt("key_parent_fragment_id", -1);
        if (i > -1)
        {
          this.mParentFragment = getFragmentManager().findFragmentById(i);
          if (!(this.mParentFragment instanceof DialogCreatable))
          {
            StringBuilder localStringBuilder = new StringBuilder();
            if (this.mParentFragment != null);
            for (Object localObject = this.mParentFragment.getClass().getName(); ; localObject = Integer.valueOf(i))
              throw new IllegalArgumentException(localObject + " must implement " + DialogCreatable.class.getName());
          }
        }
        if ((this.mParentFragment instanceof SettingsPreferenceFragment))
          SettingsPreferenceFragment.access$202((SettingsPreferenceFragment)this.mParentFragment, this);
      }
      return ((DialogCreatable)this.mParentFragment).onCreateDialog(this.mDialogId);
    }

    public void onDetach()
    {
      super.onDetach();
      if (((this.mParentFragment instanceof SettingsPreferenceFragment)) && (((SettingsPreferenceFragment)this.mParentFragment).mDialogFragment == this))
        SettingsPreferenceFragment.access$202((SettingsPreferenceFragment)this.mParentFragment, null);
    }

    public void onDismiss(DialogInterface paramDialogInterface)
    {
      super.onDismiss(paramDialogInterface);
      if (this.mOnDismissListener != null)
        this.mOnDismissListener.onDismiss(paramDialogInterface);
    }

    public void onSaveInstanceState(Bundle paramBundle)
    {
      super.onSaveInstanceState(paramBundle);
      if (this.mParentFragment != null)
      {
        paramBundle.putInt("key_dialog_id", this.mDialogId);
        paramBundle.putInt("key_parent_fragment_id", this.mParentFragment.getId());
      }
    }

    public void onStart()
    {
      super.onStart();
      if ((this.mParentFragment != null) && ((this.mParentFragment instanceof SettingsPreferenceFragment)))
        ((SettingsPreferenceFragment)this.mParentFragment).onDialogShowing();
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.SettingsPreferenceFragment
 * JD-Core Version:    0.6.2
 */