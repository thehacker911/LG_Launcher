package com.android.settings.print;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.print.PrintJob;
import android.print.PrintJobId;
import android.print.PrintJobInfo;
import android.print.PrintManager;
import android.print.PrintManager.PrintJobStateChangeListener;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.android.settings.SettingsPreferenceFragment;

public class PrintJobSettingsFragment extends SettingsPreferenceFragment
{
  private Drawable mListDivider;
  private Preference mMessagePreference;
  private PrintJob mPrintJob;
  private PrintJobId mPrintJobId;
  private Preference mPrintJobPreference;
  private final PrintManager.PrintJobStateChangeListener mPrintJobStateChangeListener = new PrintManager.PrintJobStateChangeListener()
  {
    public void onPrintJobStateChanged(PrintJobId paramAnonymousPrintJobId)
    {
      PrintJobSettingsFragment.this.updateUi();
    }
  };
  private PrintManager mPrintManager;

  private void processArguments()
  {
    this.mPrintJobId = PrintJobId.unflattenFromString(getArguments().getString("EXTRA_PRINT_JOB_ID"));
    if (this.mPrintJobId == null)
      finish();
  }

  private void updateUi()
  {
    this.mPrintJob = this.mPrintManager.getPrintJob(this.mPrintJobId);
    if (this.mPrintJob == null)
    {
      finish();
      return;
    }
    if ((this.mPrintJob.isCancelled()) || (this.mPrintJob.isCompleted()))
    {
      finish();
      return;
    }
    PrintJobInfo localPrintJobInfo = this.mPrintJob.getInfo();
    switch (localPrintJobInfo.getState())
    {
    case 5:
    default:
      Preference localPreference2 = this.mPrintJobPreference;
      Object[] arrayOfObject2 = new Object[2];
      arrayOfObject2[0] = localPrintJobInfo.getPrinterName();
      arrayOfObject2[1] = DateUtils.formatSameDayTime(localPrintJobInfo.getCreationTime(), localPrintJobInfo.getCreationTime(), 3, 3);
      localPreference2.setSummary(getString(2131428706, arrayOfObject2));
      switch (localPrintJobInfo.getState())
      {
      case 5:
      default:
        label188: String str = localPrintJobInfo.getStateReason();
        if (!TextUtils.isEmpty(str))
        {
          if (getPreferenceScreen().findPreference("print_job_message_preference") == null)
            getPreferenceScreen().addPreference(this.mMessagePreference);
          this.mMessagePreference.setSummary(str);
          getListView().setDivider(null);
        }
        break;
      case 2:
      case 3:
      case 4:
      case 6:
      }
      break;
    case 2:
    case 3:
    case 6:
    case 4:
    }
    while (true)
    {
      getActivity().invalidateOptionsMenu();
      return;
      if (!this.mPrintJob.getInfo().isCancelling())
      {
        Preference localPreference6 = this.mPrintJobPreference;
        Object[] arrayOfObject6 = new Object[1];
        arrayOfObject6[0] = localPrintJobInfo.getLabel();
        localPreference6.setTitle(getString(2131428707, arrayOfObject6));
        break;
      }
      Preference localPreference5 = this.mPrintJobPreference;
      Object[] arrayOfObject5 = new Object[1];
      arrayOfObject5[0] = localPrintJobInfo.getLabel();
      localPreference5.setTitle(getString(2131428708, arrayOfObject5));
      break;
      Preference localPreference4 = this.mPrintJobPreference;
      Object[] arrayOfObject4 = new Object[1];
      arrayOfObject4[0] = localPrintJobInfo.getLabel();
      localPreference4.setTitle(getString(2131428709, arrayOfObject4));
      break;
      if (!this.mPrintJob.getInfo().isCancelling())
      {
        Preference localPreference3 = this.mPrintJobPreference;
        Object[] arrayOfObject3 = new Object[1];
        arrayOfObject3[0] = localPrintJobInfo.getLabel();
        localPreference3.setTitle(getString(2131428710, arrayOfObject3));
        break;
      }
      Preference localPreference1 = this.mPrintJobPreference;
      Object[] arrayOfObject1 = new Object[1];
      arrayOfObject1[0] = localPrintJobInfo.getLabel();
      localPreference1.setTitle(getString(2131428708, arrayOfObject1));
      break;
      this.mPrintJobPreference.setIcon(17302416);
      break label188;
      this.mPrintJobPreference.setIcon(17302417);
      break label188;
      getPreferenceScreen().removePreference(this.mMessagePreference);
      getListView().setDivider(this.mListDivider);
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131034140);
    this.mPrintJobPreference = findPreference("print_job_preference");
    this.mMessagePreference = findPreference("print_job_message_preference");
    this.mPrintManager = ((PrintManager)getActivity().getSystemService("print")).getGlobalPrintManagerForUser(ActivityManager.getCurrentUser());
    getActivity().setTitle(2131428703);
    processArguments();
    setHasOptionsMenu(true);
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
    if (!this.mPrintJob.getInfo().isCancelling())
      paramMenu.add(0, 1, 0, getString(2131428705)).setShowAsAction(1);
    if (this.mPrintJob.isFailed())
      paramMenu.add(0, 2, 0, getString(2131428704)).setShowAsAction(1);
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default:
      return super.onOptionsItemSelected(paramMenuItem);
    case 1:
      this.mPrintJob.cancel();
      finish();
      return true;
    case 2:
    }
    this.mPrintJob.restart();
    finish();
    return true;
  }

  public void onPause()
  {
    super.onPause();
    this.mPrintManager.removePrintJobStateChangeListener(this.mPrintJobStateChangeListener);
  }

  public void onResume()
  {
    super.onResume();
    this.mPrintManager.addPrintJobStateChangeListener(this.mPrintJobStateChangeListener);
    updateUi();
  }

  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    getListView().setEnabled(false);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.print.PrintJobSettingsFragment
 * JD-Core Version:    0.6.2
 */