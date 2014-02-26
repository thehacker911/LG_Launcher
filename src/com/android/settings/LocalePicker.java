package com.android.settings;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.android.internal.app.LocalePicker.LocaleSelectionListener;
import java.util.Locale;

public class LocalePicker extends com.android.internal.app.LocalePicker
  implements LocalePicker.LocaleSelectionListener, DialogCreatable
{
  private SettingsPreferenceFragment.SettingsDialogFragment mDialogFragment;
  private Locale mTargetLocale;

  public LocalePicker()
  {
    setLocaleSelectionListener(this);
  }

  protected boolean isInDeveloperMode()
  {
    return getActivity().getSharedPreferences("development", 0).getBoolean("show", Build.TYPE.equals("eng"));
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if ((paramBundle != null) && (paramBundle.containsKey("locale")))
      this.mTargetLocale = new Locale(paramBundle.getString("locale"));
  }

  public Dialog onCreateDialog(final int paramInt)
  {
    return Utils.buildGlobalChangeWarningDialog(getActivity(), 2131429240, new Runnable()
    {
      public void run()
      {
        LocalePicker.this.removeDialog(paramInt);
        LocalePicker.this.getActivity().onBackPressed();
        LocalePicker.updateLocale(LocalePicker.this.mTargetLocale);
      }
    });
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    View localView = super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
    Utils.forcePrepareCustomPreferencesList(paramViewGroup, localView, (ListView)localView.findViewById(16908298), false);
    return localView;
  }

  public void onLocaleSelected(Locale paramLocale)
  {
    if (Utils.hasMultipleUsers(getActivity()))
    {
      this.mTargetLocale = paramLocale;
      showDialog(1);
      return;
    }
    getActivity().onBackPressed();
    updateLocale(paramLocale);
  }

  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if (this.mTargetLocale != null)
      paramBundle.putString("locale", this.mTargetLocale.toString());
  }

  protected void removeDialog(int paramInt)
  {
    if ((this.mDialogFragment != null) && (this.mDialogFragment.getDialogId() == paramInt))
      this.mDialogFragment.dismiss();
    this.mDialogFragment = null;
  }

  protected void showDialog(int paramInt)
  {
    if (this.mDialogFragment != null)
      Log.e("LocalePicker", "Old dialog fragment not null!");
    this.mDialogFragment = new SettingsPreferenceFragment.SettingsDialogFragment(this, paramInt);
    this.mDialogFragment.show(getActivity().getFragmentManager(), Integer.toString(paramInt));
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.LocalePicker
 * JD-Core Version:    0.6.2
 */