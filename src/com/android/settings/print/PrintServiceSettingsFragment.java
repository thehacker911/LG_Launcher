package com.android.settings.print;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.DataSetObserver;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.print.PrintManager;
import android.print.PrinterDiscoverySession;
import android.print.PrinterDiscoverySession.OnPrintersChangeListener;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Filter;
import android.widget.Filter.FilterResults;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import com.android.settings.SettingsPreferenceFragment;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PrintServiceSettingsFragment extends SettingsPreferenceFragment
  implements DialogInterface.OnClickListener
{
  private Intent mAddPrintersIntent;
  private CharSequence mAddPrintersTitle;
  private AnnounceFilterResult mAnnounceFilterResult;
  private ComponentName mComponentName;
  private final DataSetObserver mDataObserver = new DataSetObserver()
  {
    private void invalidateOptionsMenuIfNeeded()
    {
      int i = PrintServiceSettingsFragment.this.mPrintersAdapter.getUnfilteredCount();
      if (((PrintServiceSettingsFragment.this.mLastUnfilteredItemCount <= 0) && (i > 0)) || ((PrintServiceSettingsFragment.this.mLastUnfilteredItemCount > 0) && (i <= 0)))
        PrintServiceSettingsFragment.this.getActivity().invalidateOptionsMenu();
      PrintServiceSettingsFragment.access$302(PrintServiceSettingsFragment.this, i);
    }

    public void onChanged()
    {
      invalidateOptionsMenuIfNeeded();
      PrintServiceSettingsFragment.this.updateEmptyView();
    }

    public void onInvalidated()
    {
      invalidateOptionsMenuIfNeeded();
    }
  };
  private CharSequence mEnableWarningMessage;
  private CharSequence mEnableWarningTitle;
  private int mLastUnfilteredItemCount;
  private String mPreferenceKey;
  private PrintersAdapter mPrintersAdapter;
  private boolean mServiceEnabled;
  private final SettingsContentObserver mSettingsContentObserver = new SettingsContentObserver(new Handler())
  {
    public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
    {
      PrintServiceSettingsFragment.this.updateUiForServiceState();
    }
  };
  private Intent mSettingsIntent;
  private CharSequence mSettingsTitle;
  private PrintSettingsFragment.ToggleSwitch mToggleSwitch;

  private void announceSearchResult()
  {
    if (this.mAnnounceFilterResult == null)
      this.mAnnounceFilterResult = new AnnounceFilterResult(null);
    this.mAnnounceFilterResult.post();
  }

  private PrintSettingsFragment.ToggleSwitch createAndAddActionBarToggleSwitch(Activity paramActivity)
  {
    PrintSettingsFragment.ToggleSwitch localToggleSwitch = new PrintSettingsFragment.ToggleSwitch(paramActivity);
    localToggleSwitch.setPaddingRelative(0, 0, paramActivity.getResources().getDimensionPixelSize(2131558402), 0);
    paramActivity.getActionBar().setDisplayOptions(16, 16);
    paramActivity.getActionBar().setCustomView(localToggleSwitch, new ActionBar.LayoutParams(-2, -2, 8388629));
    return localToggleSwitch;
  }

  private void initComponents()
  {
    this.mPrintersAdapter = new PrintersAdapter(null);
    this.mPrintersAdapter.registerDataSetObserver(this.mDataObserver);
    this.mToggleSwitch = createAndAddActionBarToggleSwitch(getActivity());
    this.mToggleSwitch.setOnBeforeCheckedChangeListener(new PrintSettingsFragment.ToggleSwitch.OnBeforeCheckedChangeListener()
    {
      public boolean onBeforeCheckedChanged(PrintSettingsFragment.ToggleSwitch paramAnonymousToggleSwitch, boolean paramAnonymousBoolean)
      {
        if (paramAnonymousBoolean)
        {
          if (!TextUtils.isEmpty(PrintServiceSettingsFragment.this.mEnableWarningMessage))
          {
            paramAnonymousToggleSwitch.setCheckedInternal(false);
            PrintServiceSettingsFragment.this.getArguments().putBoolean("EXTRA_CHECKED", false);
            PrintServiceSettingsFragment.this.showDialog(1);
            return true;
          }
          PrintServiceSettingsFragment.this.onPreferenceToggled(PrintServiceSettingsFragment.this.mPreferenceKey, true);
        }
        while (true)
        {
          return false;
          PrintServiceSettingsFragment.this.onPreferenceToggled(PrintServiceSettingsFragment.this.mPreferenceKey, false);
        }
      }
    });
    this.mToggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
    {
      public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
      {
        PrintServiceSettingsFragment.this.updateEmptyView();
      }
    });
    getListView().setSelector(new ColorDrawable(0));
    getListView().setAdapter(this.mPrintersAdapter);
  }

  private void onPreferenceToggled(String paramString, boolean paramBoolean)
  {
    ComponentName localComponentName = ComponentName.unflattenFromString(paramString);
    List localList = SettingsUtils.readEnabledPrintServices(getActivity());
    if (paramBoolean)
      localList.add(localComponentName);
    while (true)
    {
      SettingsUtils.writeEnabledPrintServices(getActivity(), localList);
      return;
      localList.remove(localComponentName);
    }
  }

  private void updateEmptyView()
  {
    ListView localListView = getListView();
    ViewGroup localViewGroup = (ViewGroup)localListView.getParent();
    View localView1 = localListView.getEmptyView();
    if (!this.mToggleSwitch.isChecked())
    {
      if ((localView1 != null) && (localView1.getId() != 2131230848))
      {
        localViewGroup.removeView(localView1);
        localView1 = null;
      }
      if (localView1 == null)
      {
        View localView4 = getActivity().getLayoutInflater().inflate(2130968631, localViewGroup, false);
        localView4.setContentDescription(getString(2131428701));
        ((TextView)localView4.findViewById(2131230749)).setText(2131428701);
        localViewGroup.addView(localView4);
        localListView.setEmptyView(localView4);
      }
    }
    do
    {
      do
      {
        do
        {
          return;
          if (this.mPrintersAdapter.getUnfilteredCount() > 0)
            break;
          if ((localView1 != null) && (localView1.getId() != 2131230849))
          {
            localViewGroup.removeView(localView1);
            localView1 = null;
          }
        }
        while (localView1 != null);
        View localView3 = getActivity().getLayoutInflater().inflate(2130968632, localViewGroup, false);
        localViewGroup.addView(localView3);
        localListView.setEmptyView(localView3);
        return;
      }
      while (this.mPrintersAdapter.getCount() > 0);
      if ((localView1 != null) && (localView1.getId() != 2131230848))
      {
        localViewGroup.removeView(localView1);
        localView1 = null;
      }
    }
    while (localView1 != null);
    View localView2 = getActivity().getLayoutInflater().inflate(2130968631, localViewGroup, false);
    localView2.setContentDescription(getString(2131428692));
    ((TextView)localView2.findViewById(2131230749)).setText(2131428692);
    localViewGroup.addView(localView2);
    localListView.setEmptyView(localView2);
  }

  private void updateUiForArguments()
  {
    Bundle localBundle = getArguments();
    this.mPreferenceKey = localBundle.getString("EXTRA_PREFERENCE_KEY");
    boolean bool = localBundle.getBoolean("EXTRA_CHECKED");
    this.mToggleSwitch.setCheckedInternal(bool);
    PreferenceActivity localPreferenceActivity = (PreferenceActivity)getActivity();
    if ((!localPreferenceActivity.onIsMultiPane()) || (localPreferenceActivity.onIsHidingHeaders()))
    {
      String str1 = localBundle.getString("EXTRA_TITLE");
      getActivity().setTitle(str1);
    }
    String str2 = localBundle.getString("EXTRA_SETTINGS_TITLE");
    String str3 = localBundle.getString("EXTRA_SETTINGS_COMPONENT_NAME");
    if ((!TextUtils.isEmpty(str2)) && (!TextUtils.isEmpty(str3)))
    {
      Intent localIntent2 = new Intent("android.intent.action.MAIN").setComponent(ComponentName.unflattenFromString(str3.toString()));
      List localList2 = getPackageManager().queryIntentActivities(localIntent2, 0);
      if ((!localList2.isEmpty()) && (((ResolveInfo)localList2.get(0)).activityInfo.exported))
      {
        this.mSettingsTitle = str2;
        this.mSettingsIntent = localIntent2;
      }
    }
    String str4 = localBundle.getString("EXTRA_ADD_PRINTERS_TITLE");
    String str5 = localBundle.getString("EXTRA_ADD_PRINTERS_COMPONENT_NAME");
    if ((!TextUtils.isEmpty(str4)) && (!TextUtils.isEmpty(str5)))
    {
      Intent localIntent1 = new Intent("android.intent.action.MAIN").setComponent(ComponentName.unflattenFromString(str5.toString()));
      List localList1 = getPackageManager().queryIntentActivities(localIntent1, 0);
      if ((!localList1.isEmpty()) && (((ResolveInfo)localList1.get(0)).activityInfo.exported))
      {
        this.mAddPrintersTitle = str4;
        this.mAddPrintersIntent = localIntent1;
      }
    }
    this.mEnableWarningTitle = localBundle.getCharSequence("EXTRA_ENABLE_WARNING_TITLE");
    this.mEnableWarningMessage = localBundle.getCharSequence("EXTRA_ENABLE_WARNING_MESSAGE");
    this.mComponentName = ComponentName.unflattenFromString(localBundle.getString("EXTRA_SERVICE_COMPONENT_NAME"));
    setHasOptionsMenu(true);
  }

  private void updateUiForServiceState()
  {
    this.mServiceEnabled = SettingsUtils.readEnabledPrintServices(getActivity()).contains(this.mComponentName);
    if (this.mServiceEnabled)
    {
      this.mToggleSwitch.setCheckedInternal(true);
      this.mPrintersAdapter.enable();
    }
    while (true)
    {
      getActivity().invalidateOptionsMenu();
      return;
      this.mToggleSwitch.setCheckedInternal(false);
      this.mPrintersAdapter.disable();
    }
  }

  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    switch (paramInt)
    {
    default:
      throw new IllegalArgumentException();
    case -1:
      this.mToggleSwitch.setCheckedInternal(true);
      getArguments().putBoolean("EXTRA_CHECKED", true);
      onPreferenceToggled(this.mPreferenceKey, true);
      return;
    case -2:
    }
    this.mToggleSwitch.setCheckedInternal(false);
    getArguments().putBoolean("EXTRA_CHECKED", false);
    onPreferenceToggled(this.mPreferenceKey, false);
  }

  public Dialog onCreateDialog(int paramInt)
  {
    switch (paramInt)
    {
    default:
      throw new IllegalArgumentException();
    case 1:
    }
    CharSequence localCharSequence1 = this.mEnableWarningTitle;
    CharSequence localCharSequence2 = this.mEnableWarningMessage;
    return new AlertDialog.Builder(getActivity()).setTitle(localCharSequence1).setIconAttribute(16843605).setMessage(localCharSequence2).setCancelable(true).setPositiveButton(17039370, this).setNegativeButton(17039360, this).create();
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
    paramMenuInflater.inflate(2131755010, paramMenu);
    MenuItem localMenuItem1 = paramMenu.findItem(2131231268);
    if ((this.mServiceEnabled) && (!TextUtils.isEmpty(this.mAddPrintersTitle)) && (this.mAddPrintersIntent != null))
    {
      localMenuItem1.setIntent(this.mAddPrintersIntent);
      MenuItem localMenuItem2 = paramMenu.findItem(2131231269);
      if ((!this.mServiceEnabled) || (TextUtils.isEmpty(this.mSettingsTitle)) || (this.mSettingsIntent == null))
        break label185;
      localMenuItem2.setIntent(this.mSettingsIntent);
    }
    while (true)
    {
      MenuItem localMenuItem3 = paramMenu.findItem(2131231267);
      if ((!this.mServiceEnabled) || (this.mPrintersAdapter.getUnfilteredCount() <= 0))
        break label197;
      SearchView localSearchView = (SearchView)localMenuItem3.getActionView();
      localSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
      {
        public boolean onQueryTextChange(String paramAnonymousString)
        {
          ((Filterable)PrintServiceSettingsFragment.this.getListView().getAdapter()).getFilter().filter(paramAnonymousString);
          return true;
        }

        public boolean onQueryTextSubmit(String paramAnonymousString)
        {
          return true;
        }
      });
      localSearchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener()
      {
        public void onViewAttachedToWindow(View paramAnonymousView)
        {
          if (AccessibilityManager.getInstance(PrintServiceSettingsFragment.this.getActivity()).isEnabled())
            paramAnonymousView.announceForAccessibility(PrintServiceSettingsFragment.this.getString(2131428711));
        }

        public void onViewDetachedFromWindow(View paramAnonymousView)
        {
          Activity localActivity = PrintServiceSettingsFragment.this.getActivity();
          if ((localActivity != null) && (!localActivity.isFinishing()) && (AccessibilityManager.getInstance(localActivity).isEnabled()))
            paramAnonymousView.announceForAccessibility(PrintServiceSettingsFragment.this.getString(2131428712));
        }
      });
      return;
      paramMenu.removeItem(2131231268);
      break;
      label185: paramMenu.removeItem(2131231269);
    }
    label197: paramMenu.removeItem(2131231267);
  }

  public void onDestroyView()
  {
    getActivity().getActionBar().setCustomView(null);
    this.mToggleSwitch.setOnBeforeCheckedChangeListener(null);
    super.onDestroyView();
  }

  public void onPause()
  {
    this.mSettingsContentObserver.unregister(getContentResolver());
    if (this.mAnnounceFilterResult != null)
      this.mAnnounceFilterResult.remove();
    super.onPause();
  }

  public void onResume()
  {
    super.onResume();
    this.mSettingsContentObserver.register(getContentResolver());
    updateEmptyView();
    updateUiForServiceState();
  }

  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    initComponents();
    updateUiForArguments();
  }

  private final class AnnounceFilterResult
    implements Runnable
  {
    private AnnounceFilterResult()
    {
    }

    public void post()
    {
      remove();
      PrintServiceSettingsFragment.this.getListView().postDelayed(this, 1000L);
    }

    public void remove()
    {
      PrintServiceSettingsFragment.this.getListView().removeCallbacks(this);
    }

    public void run()
    {
      int i = PrintServiceSettingsFragment.this.getListView().getAdapter().getCount();
      if (i <= 0);
      Resources localResources;
      Object[] arrayOfObject;
      for (String str = PrintServiceSettingsFragment.this.getString(2131428692); ; str = localResources.getQuantityString(2131623945, i, arrayOfObject))
      {
        PrintServiceSettingsFragment.this.getListView().announceForAccessibility(str);
        return;
        localResources = PrintServiceSettingsFragment.this.getActivity().getResources();
        arrayOfObject = new Object[1];
        arrayOfObject[0] = Integer.valueOf(i);
      }
    }
  }

  private final class PrintersAdapter extends BaseAdapter
    implements LoaderManager.LoaderCallbacks<List<PrinterInfo>>, Filterable
  {
    private final List<PrinterInfo> mFilteredPrinters = new ArrayList();
    private CharSequence mLastSearchString;
    private final Object mLock = new Object();
    private final List<PrinterInfo> mPrinters = new ArrayList();

    private PrintersAdapter()
    {
    }

    public void disable()
    {
      PrintServiceSettingsFragment.this.getLoaderManager().destroyLoader(1);
      this.mPrinters.clear();
    }

    public void enable()
    {
      PrintServiceSettingsFragment.this.getLoaderManager().initLoader(1, null, this);
    }

    public int getCount()
    {
      synchronized (this.mLock)
      {
        int i = this.mFilteredPrinters.size();
        return i;
      }
    }

    public Filter getFilter()
    {
      return new Filter()
      {
        protected Filter.FilterResults performFiltering(CharSequence paramAnonymousCharSequence)
        {
          while (true)
          {
            int j;
            synchronized (PrintServiceSettingsFragment.PrintersAdapter.this.mLock)
            {
              if (TextUtils.isEmpty(paramAnonymousCharSequence))
                return null;
              Filter.FilterResults localFilterResults = new Filter.FilterResults();
              ArrayList localArrayList = new ArrayList();
              String str = paramAnonymousCharSequence.toString().toLowerCase();
              int i = PrintServiceSettingsFragment.PrintersAdapter.this.mPrinters.size();
              j = 0;
              if (j < i)
              {
                PrinterInfo localPrinterInfo = (PrinterInfo)PrintServiceSettingsFragment.PrintersAdapter.this.mPrinters.get(j);
                if (localPrinterInfo.getName().toLowerCase().contains(str))
                  localArrayList.add(localPrinterInfo);
              }
              else
              {
                localFilterResults.values = localArrayList;
                localFilterResults.count = localArrayList.size();
                return localFilterResults;
              }
            }
            j++;
          }
        }

        protected void publishResults(CharSequence paramAnonymousCharSequence, Filter.FilterResults paramAnonymousFilterResults)
        {
          while (true)
          {
            synchronized (PrintServiceSettingsFragment.PrintersAdapter.this.mLock)
            {
              int i = PrintServiceSettingsFragment.PrintersAdapter.this.mFilteredPrinters.size();
              PrintServiceSettingsFragment.PrintersAdapter.access$1302(PrintServiceSettingsFragment.PrintersAdapter.this, paramAnonymousCharSequence);
              PrintServiceSettingsFragment.PrintersAdapter.this.mFilteredPrinters.clear();
              if (paramAnonymousFilterResults == null)
              {
                PrintServiceSettingsFragment.PrintersAdapter.this.mFilteredPrinters.addAll(PrintServiceSettingsFragment.PrintersAdapter.this.mPrinters);
                if (i != PrintServiceSettingsFragment.PrintersAdapter.this.mFilteredPrinters.size())
                {
                  j = 1;
                  if (j != 0)
                    PrintServiceSettingsFragment.this.announceSearchResult();
                  PrintServiceSettingsFragment.PrintersAdapter.this.notifyDataSetChanged();
                }
              }
              else
              {
                List localList = (List)paramAnonymousFilterResults.values;
                PrintServiceSettingsFragment.PrintersAdapter.this.mFilteredPrinters.addAll(localList);
              }
            }
            int j = 0;
          }
        }
      };
    }

    public Object getItem(int paramInt)
    {
      synchronized (this.mLock)
      {
        Object localObject3 = this.mFilteredPrinters.get(paramInt);
        return localObject3;
      }
    }

    public long getItemId(int paramInt)
    {
      return paramInt;
    }

    public int getUnfilteredCount()
    {
      return this.mPrinters.size();
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView == null)
        paramView = PrintServiceSettingsFragment.this.getActivity().getLayoutInflater().inflate(2130968693, paramViewGroup, false);
      PrinterInfo localPrinterInfo = (PrinterInfo)getItem(paramInt);
      String str = localPrinterInfo.getName();
      CharSequence localCharSequence = null;
      try
      {
        PackageInfo localPackageInfo = PrintServiceSettingsFragment.this.getPackageManager().getPackageInfo(localPrinterInfo.getId().getServiceName().getPackageName(), 0);
        localCharSequence = localPackageInfo.applicationInfo.loadLabel(PrintServiceSettingsFragment.this.getPackageManager());
        Drawable localDrawable2 = localPackageInfo.applicationInfo.loadIcon(PrintServiceSettingsFragment.this.getPackageManager());
        localDrawable1 = localDrawable2;
        ((TextView)paramView.findViewById(2131230756)).setText(str);
        TextView localTextView = (TextView)paramView.findViewById(2131230983);
        if (!TextUtils.isEmpty(localCharSequence))
        {
          localTextView.setText(localCharSequence);
          localTextView.setVisibility(0);
        }
        ImageView localImageView;
        while (true)
        {
          localImageView = (ImageView)paramView.findViewById(2131230755);
          if (localDrawable1 == null)
            break;
          localImageView.setImageDrawable(localDrawable1);
          localImageView.setVisibility(0);
          return paramView;
          localTextView.setText(null);
          localTextView.setVisibility(8);
        }
        localImageView.setVisibility(8);
        return paramView;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        while (true)
          Drawable localDrawable1 = null;
      }
    }

    public boolean isEnabled(int paramInt)
    {
      return false;
    }

    public Loader<List<PrinterInfo>> onCreateLoader(int paramInt, Bundle paramBundle)
    {
      if (paramInt == 1)
        return new PrintServiceSettingsFragment.PrintersLoader(PrintServiceSettingsFragment.this.getActivity());
      return null;
    }

    public void onLoadFinished(Loader<List<PrinterInfo>> paramLoader, List<PrinterInfo> paramList)
    {
      while (true)
      {
        int j;
        synchronized (this.mLock)
        {
          this.mPrinters.clear();
          int i = paramList.size();
          j = 0;
          if (j < i)
          {
            PrinterInfo localPrinterInfo = (PrinterInfo)paramList.get(j);
            if (localPrinterInfo.getId().getServiceName().equals(PrintServiceSettingsFragment.this.mComponentName))
              this.mPrinters.add(localPrinterInfo);
          }
          else
          {
            this.mFilteredPrinters.clear();
            this.mFilteredPrinters.addAll(this.mPrinters);
            if (!TextUtils.isEmpty(this.mLastSearchString))
              getFilter().filter(this.mLastSearchString);
            notifyDataSetChanged();
            return;
          }
        }
        j++;
      }
    }

    public void onLoaderReset(Loader<List<PrinterInfo>> paramLoader)
    {
      synchronized (this.mLock)
      {
        this.mPrinters.clear();
        this.mFilteredPrinters.clear();
        this.mLastSearchString = null;
        notifyDataSetInvalidated();
        return;
      }
    }
  }

  private static class PrintersLoader extends Loader<List<PrinterInfo>>
  {
    private PrinterDiscoverySession mDiscoverySession;
    private final Map<PrinterId, PrinterInfo> mPrinters = new LinkedHashMap();

    public PrintersLoader(Context paramContext)
    {
      super();
    }

    private boolean cancelInternal()
    {
      if ((this.mDiscoverySession != null) && (this.mDiscoverySession.isPrinterDiscoveryStarted()))
      {
        this.mDiscoverySession.stopPrinterDiscovery();
        return true;
      }
      return false;
    }

    private void loadInternal()
    {
      if (this.mDiscoverySession == null)
      {
        this.mDiscoverySession = ((PrintManager)getContext().getSystemService("print")).createPrinterDiscoverySession();
        this.mDiscoverySession.setOnPrintersChangeListener(new PrinterDiscoverySession.OnPrintersChangeListener()
        {
          public void onPrintersChanged()
          {
            PrintServiceSettingsFragment.PrintersLoader.this.deliverResult(new ArrayList(PrintServiceSettingsFragment.PrintersLoader.this.mDiscoverySession.getPrinters()));
          }
        });
      }
      this.mDiscoverySession.startPrinterDisovery(null);
    }

    public void deliverResult(List<PrinterInfo> paramList)
    {
      if (isStarted())
        super.deliverResult(paramList);
    }

    protected void onAbandon()
    {
      onStopLoading();
    }

    protected boolean onCancelLoad()
    {
      return cancelInternal();
    }

    protected void onForceLoad()
    {
      loadInternal();
    }

    protected void onReset()
    {
      onStopLoading();
      this.mPrinters.clear();
      if (this.mDiscoverySession != null)
      {
        this.mDiscoverySession.destroy();
        this.mDiscoverySession = null;
      }
    }

    protected void onStartLoading()
    {
      if (!this.mPrinters.isEmpty())
        deliverResult(new ArrayList(this.mPrinters.values()));
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
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.print.PrintServiceSettingsFragment
 * JD-Core Version:    0.6.2
 */