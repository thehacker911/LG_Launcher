package com.android.settings.print;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.print.PrintJob;
import android.print.PrintJobId;
import android.print.PrintJobInfo;
import android.print.PrintManager;
import android.print.PrintManager.PrintJobStateChangeListener;
import android.printservice.PrintServiceInfo;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import com.android.internal.content.PackageMonitor;
import com.android.settings.DialogCreatable;
import com.android.settings.SettingsPreferenceFragment;
import java.util.ArrayList;
import java.util.List;

public class PrintSettingsFragment extends SettingsPreferenceFragment
  implements DialogCreatable
{
  private PreferenceCategory mActivePrintJobsCategory;
  private final Handler mHandler = new Handler()
  {
    public void dispatchMessage(Message paramAnonymousMessage)
    {
      PrintSettingsFragment.this.updateServicesPreferences();
    }
  };
  private PrintJobsController mPrintJobsController;
  private PreferenceCategory mPrintServicesCategory;
  private final SettingsContentObserver mSettingsContentObserver = new SettingsContentObserver(this.mHandler)
  {
    public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
    {
      PrintSettingsFragment.this.updateServicesPreferences();
    }
  };
  private final PackageMonitor mSettingsPackageMonitor = new SettingsPackageMonitor(null);

  private void startSubSettingsIfNeeded()
  {
    if (getArguments() == null);
    Preference localPreference;
    do
    {
      String str;
      do
      {
        return;
        str = getArguments().getString("EXTRA_PRINT_SERVICE_COMPONENT_NAME");
      }
      while (str == null);
      getArguments().remove("EXTRA_PRINT_SERVICE_COMPONENT_NAME");
      localPreference = findPreference(str);
    }
    while (localPreference == null);
    localPreference.performClick(getPreferenceScreen());
  }

  private void updateServicesPreferences()
  {
    int j;
    label65: ResolveInfo localResolveInfo;
    PreferenceScreen localPreferenceScreen;
    String str1;
    ComponentName localComponentName;
    boolean bool;
    if (getPreferenceScreen().findPreference("print_services_category") == null)
    {
      getPreferenceScreen().addPreference(this.mPrintServicesCategory);
      List localList1 = SettingsUtils.readEnabledPrintServices(getActivity());
      List localList2 = getActivity().getPackageManager().queryIntentServices(new Intent("android.printservice.PrintService"), 132);
      int i = localList2.size();
      j = 0;
      if (j >= i)
        break label475;
      localResolveInfo = (ResolveInfo)localList2.get(j);
      localPreferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity());
      str1 = localResolveInfo.loadLabel(getPackageManager()).toString();
      localPreferenceScreen.setTitle(str1);
      localComponentName = new ComponentName(localResolveInfo.serviceInfo.packageName, localResolveInfo.serviceInfo.name);
      localPreferenceScreen.setKey(localComponentName.flattenToString());
      localPreferenceScreen.setOrder(j);
      localPreferenceScreen.setFragment(PrintServiceSettingsFragment.class.getName());
      localPreferenceScreen.setPersistent(false);
      bool = localList1.contains(localComponentName);
      if (!bool)
        break label460;
      localPreferenceScreen.setSummary(getString(2131428695));
    }
    while (true)
    {
      Bundle localBundle = localPreferenceScreen.getExtras();
      localBundle.putString("EXTRA_PREFERENCE_KEY", localPreferenceScreen.getKey());
      localBundle.putBoolean("EXTRA_CHECKED", bool);
      localBundle.putString("EXTRA_TITLE", str1);
      PrintServiceInfo localPrintServiceInfo = PrintServiceInfo.create(localResolveInfo, getActivity());
      CharSequence localCharSequence = localResolveInfo.loadLabel(getPackageManager());
      localBundle.putString("EXTRA_ENABLE_WARNING_TITLE", getString(2131428689, new Object[] { localCharSequence }));
      localBundle.putString("EXTRA_ENABLE_WARNING_MESSAGE", getString(2131428690, new Object[] { localCharSequence }));
      String str2 = localPrintServiceInfo.getSettingsActivityName();
      if (!TextUtils.isEmpty(str2))
      {
        localBundle.putString("EXTRA_SETTINGS_TITLE", getString(2131428693));
        localBundle.putString("EXTRA_SETTINGS_COMPONENT_NAME", new ComponentName(localResolveInfo.serviceInfo.packageName, str2).flattenToString());
      }
      String str3 = localPrintServiceInfo.getAddPrintersActivityName();
      if (!TextUtils.isEmpty(str3))
      {
        localBundle.putString("EXTRA_ADD_PRINTERS_TITLE", getString(2131428694));
        localBundle.putString("EXTRA_ADD_PRINTERS_COMPONENT_NAME", new ComponentName(localResolveInfo.serviceInfo.packageName, str3).flattenToString());
      }
      localBundle.putString("EXTRA_SERVICE_COMPONENT_NAME", localComponentName.flattenToString());
      this.mPrintServicesCategory.addPreference(localPreferenceScreen);
      j++;
      break label65;
      this.mPrintServicesCategory.removeAll();
      break;
      label460: localPreferenceScreen.setSummary(getString(2131428696));
    }
    label475: if (this.mPrintServicesCategory.getPreferenceCount() == 0)
      getPreferenceScreen().removePreference(this.mPrintServicesCategory);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131034141);
    this.mActivePrintJobsCategory = ((PreferenceCategory)findPreference("print_jobs_category"));
    this.mPrintServicesCategory = ((PreferenceCategory)findPreference("print_services_category"));
    getPreferenceScreen().removePreference(this.mActivePrintJobsCategory);
    this.mPrintJobsController = new PrintJobsController(null);
    getActivity().getLoaderManager().initLoader(1, null, this.mPrintJobsController);
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
    String str = Settings.Secure.getString(getContentResolver(), "print_service_search_uri");
    if (!TextUtils.isEmpty(str))
    {
      MenuItem localMenuItem = paramMenu.add(2131428697);
      localMenuItem.setShowAsActionFlags(1);
      localMenuItem.setIntent(new Intent("android.intent.action.VIEW", Uri.parse(str)));
    }
  }

  public void onPause()
  {
    this.mSettingsPackageMonitor.unregister();
    this.mSettingsContentObserver.unregister(getContentResolver());
    super.onPause();
  }

  public void onResume()
  {
    super.onResume();
    this.mSettingsPackageMonitor.register(getActivity(), getActivity().getMainLooper(), false);
    this.mSettingsContentObserver.register(getContentResolver());
    updateServicesPreferences();
    setHasOptionsMenu(true);
    startSubSettingsIfNeeded();
  }

  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    ViewGroup localViewGroup = (ViewGroup)getListView().getParent();
    View localView = getActivity().getLayoutInflater().inflate(2130968631, localViewGroup, false);
    ((TextView)localView.findViewById(2131230749)).setText(2131428691);
    localViewGroup.addView(localView);
    getListView().setEmptyView(localView);
  }

  private final class PrintJobsController
    implements LoaderManager.LoaderCallbacks<List<PrintJobInfo>>
  {
    private PrintJobsController()
    {
    }

    public Loader<List<PrintJobInfo>> onCreateLoader(int paramInt, Bundle paramBundle)
    {
      if (paramInt == 1)
        return new PrintSettingsFragment.PrintJobsLoader(PrintSettingsFragment.this.getActivity());
      return null;
    }

    public void onLoadFinished(Loader<List<PrintJobInfo>> paramLoader, List<PrintJobInfo> paramList)
    {
      if ((paramList == null) || (paramList.isEmpty()))
        PrintSettingsFragment.this.getPreferenceScreen().removePreference(PrintSettingsFragment.this.mActivePrintJobsCategory);
      int i;
      int j;
      do
      {
        return;
        if (PrintSettingsFragment.this.getPreferenceScreen().findPreference("print_jobs_category") == null)
          PrintSettingsFragment.this.getPreferenceScreen().addPreference(PrintSettingsFragment.this.mActivePrintJobsCategory);
        PrintSettingsFragment.this.mActivePrintJobsCategory.removeAll();
        i = paramList.size();
        j = 0;
      }
      while (j >= i);
      PrintJobInfo localPrintJobInfo = (PrintJobInfo)paramList.get(j);
      PreferenceScreen localPreferenceScreen = PrintSettingsFragment.this.getPreferenceManager().createPreferenceScreen(PrintSettingsFragment.this.getActivity());
      localPreferenceScreen.setPersistent(false);
      localPreferenceScreen.setFragment(PrintJobSettingsFragment.class.getName());
      localPreferenceScreen.setKey(localPrintJobInfo.getId().flattenToString());
      switch (localPrintJobInfo.getState())
      {
      case 5:
      default:
        label192: PrintSettingsFragment localPrintSettingsFragment2 = PrintSettingsFragment.this;
        Object[] arrayOfObject2 = new Object[2];
        arrayOfObject2[0] = localPrintJobInfo.getPrinterName();
        arrayOfObject2[1] = DateUtils.formatSameDayTime(localPrintJobInfo.getCreationTime(), localPrintJobInfo.getCreationTime(), 3, 3);
        localPreferenceScreen.setSummary(localPrintSettingsFragment2.getString(2131428706, arrayOfObject2));
        switch (localPrintJobInfo.getState())
        {
        case 5:
        default:
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
        localPreferenceScreen.getExtras().putString("EXTRA_PRINT_JOB_ID", localPrintJobInfo.getId().flattenToString());
        PrintSettingsFragment.this.mActivePrintJobsCategory.addPreference(localPreferenceScreen);
        j++;
        break;
        if (!localPrintJobInfo.isCancelling())
        {
          PrintSettingsFragment localPrintSettingsFragment6 = PrintSettingsFragment.this;
          Object[] arrayOfObject6 = new Object[1];
          arrayOfObject6[0] = localPrintJobInfo.getLabel();
          localPreferenceScreen.setTitle(localPrintSettingsFragment6.getString(2131428707, arrayOfObject6));
          break label192;
        }
        PrintSettingsFragment localPrintSettingsFragment5 = PrintSettingsFragment.this;
        Object[] arrayOfObject5 = new Object[1];
        arrayOfObject5[0] = localPrintJobInfo.getLabel();
        localPreferenceScreen.setTitle(localPrintSettingsFragment5.getString(2131428708, arrayOfObject5));
        break label192;
        PrintSettingsFragment localPrintSettingsFragment4 = PrintSettingsFragment.this;
        Object[] arrayOfObject4 = new Object[1];
        arrayOfObject4[0] = localPrintJobInfo.getLabel();
        localPreferenceScreen.setTitle(localPrintSettingsFragment4.getString(2131428709, arrayOfObject4));
        break label192;
        if (!localPrintJobInfo.isCancelling())
        {
          PrintSettingsFragment localPrintSettingsFragment3 = PrintSettingsFragment.this;
          Object[] arrayOfObject3 = new Object[1];
          arrayOfObject3[0] = localPrintJobInfo.getLabel();
          localPreferenceScreen.setTitle(localPrintSettingsFragment3.getString(2131428710, arrayOfObject3));
          break label192;
        }
        PrintSettingsFragment localPrintSettingsFragment1 = PrintSettingsFragment.this;
        Object[] arrayOfObject1 = new Object[1];
        arrayOfObject1[0] = localPrintJobInfo.getLabel();
        localPreferenceScreen.setTitle(localPrintSettingsFragment1.getString(2131428708, arrayOfObject1));
        break label192;
        localPreferenceScreen.setIcon(17302416);
        continue;
        localPreferenceScreen.setIcon(17302417);
      }
    }

    public void onLoaderReset(Loader<List<PrintJobInfo>> paramLoader)
    {
      PrintSettingsFragment.this.getPreferenceScreen().removePreference(PrintSettingsFragment.this.mActivePrintJobsCategory);
    }
  }

  private static final class PrintJobsLoader extends AsyncTaskLoader<List<PrintJobInfo>>
  {
    private PrintManager.PrintJobStateChangeListener mPrintJobStateChangeListener;
    private List<PrintJobInfo> mPrintJobs = new ArrayList();
    private final PrintManager mPrintManager;

    public PrintJobsLoader(Context paramContext)
    {
      super();
      this.mPrintManager = ((PrintManager)paramContext.getSystemService("print")).getGlobalPrintManagerForUser(ActivityManager.getCurrentUser());
    }

    private static boolean shouldShowToUser(PrintJobInfo paramPrintJobInfo)
    {
      switch (paramPrintJobInfo.getState())
      {
      case 5:
      default:
        return false;
      case 2:
      case 3:
      case 4:
      case 6:
      }
      return true;
    }

    public void deliverResult(List<PrintJobInfo> paramList)
    {
      if (isStarted())
        super.deliverResult(paramList);
    }

    public List<PrintJobInfo> loadInBackground()
    {
      ArrayList localArrayList = null;
      List localList = this.mPrintManager.getPrintJobs();
      int i = localList.size();
      for (int j = 0; j < i; j++)
      {
        PrintJobInfo localPrintJobInfo = ((PrintJob)localList.get(j)).getInfo();
        if (shouldShowToUser(localPrintJobInfo))
        {
          if (localArrayList == null)
            localArrayList = new ArrayList();
          localArrayList.add(localPrintJobInfo);
        }
      }
      return localArrayList;
    }

    protected void onReset()
    {
      onStopLoading();
      this.mPrintJobs.clear();
      if (this.mPrintJobStateChangeListener != null)
      {
        this.mPrintManager.removePrintJobStateChangeListener(this.mPrintJobStateChangeListener);
        this.mPrintJobStateChangeListener = null;
      }
    }

    protected void onStartLoading()
    {
      if (!this.mPrintJobs.isEmpty())
        deliverResult(new ArrayList(this.mPrintJobs));
      if (this.mPrintJobStateChangeListener == null)
      {
        this.mPrintJobStateChangeListener = new PrintManager.PrintJobStateChangeListener()
        {
          public void onPrintJobStateChanged(PrintJobId paramAnonymousPrintJobId)
          {
            PrintSettingsFragment.PrintJobsLoader.this.onForceLoad();
          }
        };
        this.mPrintManager.addPrintJobStateChangeListener(this.mPrintJobStateChangeListener);
      }
      if (this.mPrintJobs.isEmpty())
        onForceLoad();
    }

    protected void onStopLoading()
    {
      onCancelLoad();
    }
  }

  private static abstract class SettingsContentObserver extends ContentObserver
  {
    public SettingsContentObserver(Handler paramHandler)
    {
      super();
    }

    public void register(ContentResolver paramContentResolver)
    {
      paramContentResolver.registerContentObserver(Settings.Secure.getUriFor("enabled_print_services"), false, this);
    }

    public void unregister(ContentResolver paramContentResolver)
    {
      paramContentResolver.unregisterContentObserver(this);
    }
  }

  private class SettingsPackageMonitor extends PackageMonitor
  {
    private SettingsPackageMonitor()
    {
    }

    public void onPackageAdded(String paramString, int paramInt)
    {
      PrintSettingsFragment.this.mHandler.obtainMessage().sendToTarget();
    }

    public void onPackageAppeared(String paramString, int paramInt)
    {
      PrintSettingsFragment.this.mHandler.obtainMessage().sendToTarget();
    }

    public void onPackageDisappeared(String paramString, int paramInt)
    {
      PrintSettingsFragment.this.mHandler.obtainMessage().sendToTarget();
    }

    public void onPackageRemoved(String paramString, int paramInt)
    {
      PrintSettingsFragment.this.mHandler.obtainMessage().sendToTarget();
    }
  }

  public static class ToggleSwitch extends Switch
  {
    private OnBeforeCheckedChangeListener mOnBeforeListener;

    public ToggleSwitch(Context paramContext)
    {
      super();
    }

    public void setChecked(boolean paramBoolean)
    {
      if ((this.mOnBeforeListener != null) && (this.mOnBeforeListener.onBeforeCheckedChanged(this, paramBoolean)))
        return;
      super.setChecked(paramBoolean);
    }

    public void setCheckedInternal(boolean paramBoolean)
    {
      super.setChecked(paramBoolean);
    }

    public void setOnBeforeCheckedChangeListener(OnBeforeCheckedChangeListener paramOnBeforeCheckedChangeListener)
    {
      this.mOnBeforeListener = paramOnBeforeCheckedChangeListener;
    }

    public static abstract interface OnBeforeCheckedChangeListener
    {
      public abstract boolean onBeforeCheckedChanged(PrintSettingsFragment.ToggleSwitch paramToggleSwitch, boolean paramBoolean);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.print.PrintSettingsFragment
 * JD-Core Version:    0.6.2
 */