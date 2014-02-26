package com.android.settings.inputmethod;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import com.android.internal.app.LocalePicker.LocaleSelectionListener;
import java.util.ArrayList;
import java.util.Locale;

public class UserDictionaryAddWordFragment extends Fragment
  implements AdapterView.OnItemSelectedListener, LocalePicker.LocaleSelectionListener
{
  private UserDictionaryAddWordContents mContents;
  private boolean mIsDeleting = false;
  private View mRootView;

  private void updateSpinner()
  {
    ArrayList localArrayList = this.mContents.getLocalesList(getActivity());
    new ArrayAdapter(getActivity(), 17367048, localArrayList).setDropDownViewResource(17367049);
  }

  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    setHasOptionsMenu(true);
    getActivity().getActionBar().setTitle(2131428528);
    setRetainInstance(true);
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    paramMenu.add(0, 1, 0, 2131429014).setIcon(17301564).setShowAsAction(5);
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mRootView = paramLayoutInflater.inflate(2130968724, null);
    this.mIsDeleting = false;
    if (this.mContents == null);
    for (this.mContents = new UserDictionaryAddWordContents(this.mRootView, getArguments()); ; this.mContents = new UserDictionaryAddWordContents(this.mRootView, this.mContents))
    {
      getActivity().getActionBar().setSubtitle(UserDictionarySettingsUtils.getLocaleDisplayName(getActivity(), this.mContents.getCurrentUserDictionaryLocale()));
      return this.mRootView;
    }
  }

  public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    UserDictionaryAddWordContents.LocaleRenderer localLocaleRenderer = (UserDictionaryAddWordContents.LocaleRenderer)paramAdapterView.getItemAtPosition(paramInt);
    if (localLocaleRenderer.isMoreLanguages())
    {
      ((PreferenceActivity)getActivity()).startPreferenceFragment(new UserDictionaryLocalePicker(this), true);
      return;
    }
    this.mContents.updateLocale(localLocaleRenderer.getLocaleString());
  }

  public void onLocaleSelected(Locale paramLocale)
  {
    this.mContents.updateLocale(paramLocale.toString());
    getActivity().onBackPressed();
  }

  public void onNothingSelected(AdapterView<?> paramAdapterView)
  {
    Bundle localBundle = getArguments();
    this.mContents.updateLocale(localBundle.getString("locale"));
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (paramMenuItem.getItemId() == 1)
    {
      this.mContents.delete(getActivity());
      this.mIsDeleting = true;
      getActivity().onBackPressed();
      return true;
    }
    return false;
  }

  public void onPause()
  {
    super.onPause();
    if (!this.mIsDeleting)
      this.mContents.apply(getActivity(), null);
  }

  public void onResume()
  {
    super.onResume();
    updateSpinner();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.inputmethod.UserDictionaryAddWordFragment
 * JD-Core Version:    0.6.2
 */