package com.android.settings;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import java.util.List;

public class DreamSettings extends SettingsPreferenceFragment
{
  private static final String TAG = DreamSettings.class.getSimpleName();
  private DreamInfoAdapter mAdapter;
  private DreamBackend mBackend;
  private Context mContext;
  private MenuItem[] mMenuItemsWhenEnabled;
  private final PackageReceiver mPackageReceiver = new PackageReceiver(null);
  private boolean mRefreshing;
  private Switch mSwitch;

  private MenuItem createMenuItem(Menu paramMenu, int paramInt1, int paramInt2, boolean paramBoolean, final Runnable paramRunnable)
  {
    MenuItem localMenuItem = paramMenu.add(paramInt1);
    localMenuItem.setShowAsAction(paramInt2);
    localMenuItem.setEnabled(paramBoolean);
    localMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
    {
      public boolean onMenuItemClick(MenuItem paramAnonymousMenuItem)
      {
        paramRunnable.run();
        return true;
      }
    });
    return localMenuItem;
  }

  private Dialog createWhenToDreamDialog()
  {
    int i = 2;
    CharSequence[] arrayOfCharSequence = new CharSequence[3];
    arrayOfCharSequence[0] = this.mContext.getString(2131428056);
    arrayOfCharSequence[1] = this.mContext.getString(2131428055);
    arrayOfCharSequence[i] = this.mContext.getString(2131428054);
    if ((this.mBackend.isActivatedOnDock()) && (this.mBackend.isActivatedOnSleep()));
    while (true)
    {
      return new AlertDialog.Builder(this.mContext).setTitle(2131428059).setSingleChoiceItems(arrayOfCharSequence, i, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          DreamBackend localDreamBackend1 = DreamSettings.this.mBackend;
          if ((paramAnonymousInt == 0) || (paramAnonymousInt == 2));
          for (boolean bool1 = true; ; bool1 = false)
          {
            localDreamBackend1.setActivatedOnDock(bool1);
            DreamBackend localDreamBackend2 = DreamSettings.this.mBackend;
            boolean bool2;
            if (paramAnonymousInt != 1)
            {
              bool2 = false;
              if (paramAnonymousInt != 2);
            }
            else
            {
              bool2 = true;
            }
            localDreamBackend2.setActivatedOnSleep(bool2);
            return;
          }
        }
      }).create();
      if (this.mBackend.isActivatedOnDock())
        i = 0;
      else if (this.mBackend.isActivatedOnSleep())
        i = 1;
      else
        i = -1;
    }
  }

  public static CharSequence getSummaryTextWithDreamName(Context paramContext)
  {
    DreamBackend localDreamBackend = new DreamBackend(paramContext);
    if (!localDreamBackend.isEnabled())
      return paramContext.getString(2131428057);
    return localDreamBackend.getActiveDreamName();
  }

  private static void logd(String paramString, Object[] paramArrayOfObject)
  {
  }

  private void refreshFromBackend()
  {
    logd("refreshFromBackend()", new Object[0]);
    this.mRefreshing = true;
    boolean bool = this.mBackend.isEnabled();
    if (this.mSwitch.isChecked() != bool)
      this.mSwitch.setChecked(bool);
    this.mAdapter.clear();
    if (bool)
    {
      List localList = this.mBackend.getDreamInfos();
      this.mAdapter.addAll(localList);
    }
    if (this.mMenuItemsWhenEnabled != null)
    {
      MenuItem[] arrayOfMenuItem = this.mMenuItemsWhenEnabled;
      int i = arrayOfMenuItem.length;
      for (int j = 0; j < i; j++)
        arrayOfMenuItem[j].setEnabled(bool);
    }
    this.mRefreshing = false;
  }

  public int getHelpResource()
  {
    return 2131429265;
  }

  public void onActivityCreated(Bundle paramBundle)
  {
    logd("onActivityCreated(%s)", new Object[] { paramBundle });
    super.onActivityCreated(paramBundle);
    ListView localListView = getListView();
    localListView.setItemsCanFocus(true);
    TextView localTextView = (TextView)getView().findViewById(16908292);
    localTextView.setText(2131428058);
    localListView.setEmptyView(localTextView);
    this.mAdapter = new DreamInfoAdapter(this.mContext);
    localListView.setAdapter(this.mAdapter);
  }

  public void onAttach(Activity paramActivity)
  {
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = paramActivity.getClass().getSimpleName();
    logd("onAttach(%s)", arrayOfObject);
    super.onAttach(paramActivity);
    this.mContext = paramActivity;
  }

  public void onCreate(Bundle paramBundle)
  {
    logd("onCreate(%s)", new Object[] { paramBundle });
    super.onCreate(paramBundle);
    Activity localActivity = getActivity();
    this.mBackend = new DreamBackend(localActivity);
    this.mSwitch = new Switch(localActivity);
    this.mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
    {
      public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
      {
        if (!DreamSettings.this.mRefreshing)
        {
          DreamSettings.this.mBackend.setEnabled(paramAnonymousBoolean);
          DreamSettings.this.refreshFromBackend();
        }
      }
    });
    int i = localActivity.getResources().getDimensionPixelSize(2131558402);
    this.mSwitch.setPaddingRelative(0, 0, i, 0);
    localActivity.getActionBar().setDisplayOptions(16, 16);
    localActivity.getActionBar().setCustomView(this.mSwitch, new ActionBar.LayoutParams(-2, -2, 8388629));
    setHasOptionsMenu(true);
  }

  public Dialog onCreateDialog(int paramInt)
  {
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = Integer.valueOf(paramInt);
    logd("onCreateDialog(%s)", arrayOfObject);
    if (paramInt == 1)
      return createWhenToDreamDialog();
    return super.onCreateDialog(paramInt);
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    logd("onCreateOptionsMenu()", new Object[0]);
    boolean bool = this.mBackend.isEnabled();
    MenuItem localMenuItem1 = createMenuItem(paramMenu, 2131428060, 2, bool, new Runnable()
    {
      public void run()
      {
        DreamSettings.this.mBackend.startDreaming();
      }
    });
    MenuItem localMenuItem2 = createMenuItem(paramMenu, 2131428059, 1, bool, new Runnable()
    {
      public void run()
      {
        DreamSettings.this.showDialog(1);
      }
    });
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
    this.mMenuItemsWhenEnabled = new MenuItem[] { localMenuItem1, localMenuItem2 };
  }

  public void onDestroyView()
  {
    getActivity().getActionBar().setCustomView(null);
    super.onDestroyView();
  }

  public void onPause()
  {
    logd("onPause()", new Object[0]);
    super.onPause();
    this.mContext.unregisterReceiver(this.mPackageReceiver);
  }

  public void onResume()
  {
    logd("onResume()", new Object[0]);
    super.onResume();
    refreshFromBackend();
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.PACKAGE_ADDED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
    localIntentFilter.addDataScheme("package");
    this.mContext.registerReceiver(this.mPackageReceiver, localIntentFilter);
  }

  private class DreamInfoAdapter extends ArrayAdapter<DreamBackend.DreamInfo>
  {
    private final LayoutInflater mInflater;

    public DreamInfoAdapter(Context arg2)
    {
      super(0);
      this.mInflater = ((LayoutInflater)localContext.getSystemService("layout_inflater"));
    }

    private void activate(DreamBackend.DreamInfo paramDreamInfo)
    {
      if (paramDreamInfo.equals(getCurrentSelection()))
        return;
      for (int i = 0; i < getCount(); i++)
        ((DreamBackend.DreamInfo)getItem(i)).isActive = false;
      paramDreamInfo.isActive = true;
      DreamSettings.this.mBackend.setActiveDream(paramDreamInfo.componentName);
      notifyDataSetChanged();
    }

    private View createDreamInfoRow(ViewGroup paramViewGroup)
    {
      final View localView = this.mInflater.inflate(2130968628, paramViewGroup, false);
      localView.findViewById(16908312).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView.setPressed(true);
          DreamSettings.DreamInfoAdapter.this.activate((DreamBackend.DreamInfo)localView.getTag());
        }
      });
      return localView;
    }

    private DreamBackend.DreamInfo getCurrentSelection()
    {
      for (int i = 0; i < getCount(); i++)
      {
        DreamBackend.DreamInfo localDreamInfo = (DreamBackend.DreamInfo)getItem(i);
        if (localDreamInfo.isActive)
          return localDreamInfo;
      }
      return null;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      int i = 1;
      DreamBackend.DreamInfo localDreamInfo = (DreamBackend.DreamInfo)getItem(paramInt);
      Object[] arrayOfObject = new Object[i];
      arrayOfObject[0] = localDreamInfo.caption;
      DreamSettings.logd("getView(%s)", arrayOfObject);
      final View localView1;
      label131: int j;
      label148: ImageView localImageView;
      int k;
      if (paramView != null)
      {
        localView1 = paramView;
        localView1.setTag(localDreamInfo);
        ((ImageView)localView1.findViewById(16908294)).setImageDrawable(localDreamInfo.icon);
        ((TextView)localView1.findViewById(16908310)).setText(localDreamInfo.caption);
        RadioButton localRadioButton = (RadioButton)localView1.findViewById(16908313);
        localRadioButton.setChecked(localDreamInfo.isActive);
        localRadioButton.setOnTouchListener(new View.OnTouchListener()
        {
          public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
          {
            localView1.onTouchEvent(paramAnonymousMotionEvent);
            return false;
          }
        });
        if (localDreamInfo.settingsComponentName == null)
          break label248;
        View localView2 = localView1.findViewById(2131230845);
        if (i == 0)
          break label254;
        j = 0;
        localView2.setVisibility(j);
        localImageView = (ImageView)localView1.findViewById(16908314);
        k = 0;
        if (i == 0)
          break label260;
        label175: localImageView.setVisibility(k);
        if (!localDreamInfo.isActive)
          break label266;
      }
      label260: label266: for (float f = 1.0F; ; f = 0.4F)
      {
        localImageView.setAlpha(f);
        localImageView.setEnabled(localDreamInfo.isActive);
        localImageView.setFocusable(localDreamInfo.isActive);
        localImageView.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            DreamSettings.this.mBackend.launchSettings((DreamBackend.DreamInfo)localView1.getTag());
          }
        });
        return localView1;
        localView1 = createDreamInfoRow(paramViewGroup);
        break;
        label248: i = 0;
        break label131;
        label254: j = 4;
        break label148;
        k = 4;
        break label175;
      }
    }
  }

  private class PackageReceiver extends BroadcastReceiver
  {
    private PackageReceiver()
    {
    }

    public void onReceive(Context paramContext, Intent paramIntent)
    {
      DreamSettings.logd("PackageReceiver.onReceive", new Object[0]);
      DreamSettings.this.refreshFromBackend();
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.DreamSettings
 * JD-Core Version:    0.6.2
 */