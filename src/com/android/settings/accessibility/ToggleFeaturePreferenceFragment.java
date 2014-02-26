package com.android.settings.accessibility;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import com.android.settings.SettingsPreferenceFragment;

public abstract class ToggleFeaturePreferenceFragment extends SettingsPreferenceFragment
{
  protected String mPreferenceKey;
  protected Intent mSettingsIntent;
  protected CharSequence mSettingsTitle;
  protected Preference mSummaryPreference;
  protected ToggleSwitch mToggleSwitch;

  private ToggleSwitch createAndAddActionBarToggleSwitch(Activity paramActivity)
  {
    ToggleSwitch localToggleSwitch = new ToggleSwitch(paramActivity);
    localToggleSwitch.setPaddingRelative(0, 0, paramActivity.getResources().getDimensionPixelSize(2131558402), 0);
    paramActivity.getActionBar().setDisplayOptions(16, 16);
    paramActivity.getActionBar().setCustomView(localToggleSwitch, new ActionBar.LayoutParams(-2, -2, 8388629));
    return localToggleSwitch;
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    PreferenceScreen localPreferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity());
    setPreferenceScreen(localPreferenceScreen);
    this.mSummaryPreference = new Preference(getActivity())
    {
      private void sendAccessibilityEvent(View paramAnonymousView)
      {
        AccessibilityManager localAccessibilityManager = AccessibilityManager.getInstance(ToggleFeaturePreferenceFragment.this.getActivity());
        if (localAccessibilityManager.isEnabled())
        {
          AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain();
          localAccessibilityEvent.setEventType(8);
          paramAnonymousView.onInitializeAccessibilityEvent(localAccessibilityEvent);
          paramAnonymousView.dispatchPopulateAccessibilityEvent(localAccessibilityEvent);
          localAccessibilityManager.sendAccessibilityEvent(localAccessibilityEvent);
        }
      }

      protected void onBindView(View paramAnonymousView)
      {
        super.onBindView(paramAnonymousView);
        TextView localTextView = (TextView)paramAnonymousView.findViewById(2131230772);
        localTextView.setText(getSummary());
        sendAccessibilityEvent(localTextView);
      }
    };
    this.mSummaryPreference.setPersistent(false);
    this.mSummaryPreference.setLayoutResource(2130968713);
    localPreferenceScreen.addPreference(this.mSummaryPreference);
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
    MenuItem localMenuItem = paramMenu.add(this.mSettingsTitle);
    localMenuItem.setShowAsAction(1);
    localMenuItem.setIntent(this.mSettingsIntent);
  }

  public void onDestroyView()
  {
    getActivity().getActionBar().setCustomView(null);
    this.mToggleSwitch.setOnBeforeCheckedChangeListener(null);
    super.onDestroyView();
  }

  protected void onInstallActionBarToggleSwitch()
  {
    this.mToggleSwitch = createAndAddActionBarToggleSwitch(getActivity());
  }

  protected void onProcessArguments(Bundle paramBundle)
  {
    this.mPreferenceKey = paramBundle.getString("preference_key");
    boolean bool = paramBundle.getBoolean("checked");
    this.mToggleSwitch.setCheckedInternal(bool);
    PreferenceActivity localPreferenceActivity = (PreferenceActivity)getActivity();
    if ((!localPreferenceActivity.onIsMultiPane()) || (localPreferenceActivity.onIsHidingHeaders()))
    {
      String str = paramBundle.getString("title");
      getActivity().setTitle(str);
    }
    CharSequence localCharSequence = paramBundle.getCharSequence("summary");
    this.mSummaryPreference.setSummary(localCharSequence);
  }

  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    onInstallActionBarToggleSwitch();
    onProcessArguments(getArguments());
    getListView().setSelector(new ColorDrawable(0));
    getListView().setDivider(null);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accessibility.ToggleFeaturePreferenceFragment
 * JD-Core Version:    0.6.2
 */