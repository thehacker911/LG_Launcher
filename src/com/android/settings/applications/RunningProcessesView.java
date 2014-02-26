package com.android.settings.applications;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.UserHandle;
import android.preference.PreferenceActivity;
import android.text.BidiFormatter;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.RecyclerListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.android.internal.util.MemInfoReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class RunningProcessesView extends FrameLayout
  implements AbsListView.RecyclerListener, AdapterView.OnItemClickListener, RunningState.OnRefreshUiListener
{
  long SECONDARY_SERVER_MEM;
  final HashMap<View, ActiveItem> mActiveItems = new HashMap();
  ServiceListAdapter mAdapter;
  ActivityManager mAm;
  TextView mBackgroundProcessText;
  StringBuilder mBuilder = new StringBuilder(128);
  LinearColorBar mColorBar;
  RunningState.BaseItem mCurSelected;
  Runnable mDataAvail;
  TextView mForegroundProcessText;
  long mLastAvailMemory = -1L;
  long mLastBackgroundProcessMemory = -1L;
  long mLastForegroundProcessMemory = -1L;
  int mLastNumBackgroundProcesses = -1;
  int mLastNumForegroundProcesses = -1;
  int mLastNumServiceProcesses = -1;
  long mLastServiceProcessMemory = -1L;
  ListView mListView;
  MemInfoReader mMemInfoReader = new MemInfoReader();
  final int mMyUserId = UserHandle.myUserId();
  Fragment mOwner;
  RunningState mState;

  public RunningProcessesView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  private void startServiceDetailsActivity(RunningState.MergedItem paramMergedItem)
  {
    if (this.mOwner != null)
    {
      Bundle localBundle = new Bundle();
      if (paramMergedItem.mProcess != null)
      {
        localBundle.putInt("uid", paramMergedItem.mProcess.mUid);
        localBundle.putString("process", paramMergedItem.mProcess.mProcessName);
      }
      localBundle.putInt("user_id", paramMergedItem.mUserId);
      localBundle.putBoolean("background", this.mAdapter.mShowBackground);
      ((PreferenceActivity)this.mOwner.getActivity()).startPreferencePanel(RunningServiceDetails.class.getName(), localBundle, 2131428484, null, null, 0);
    }
  }

  public void doCreate(Bundle paramBundle)
  {
    this.mAm = ((ActivityManager)getContext().getSystemService("activity"));
    this.mState = RunningState.getInstance(getContext());
    ((LayoutInflater)getContext().getSystemService("layout_inflater")).inflate(2130968701, this);
    this.mListView = ((ListView)findViewById(16908298));
    View localView = findViewById(16908292);
    if (localView != null)
      this.mListView.setEmptyView(localView);
    this.mListView.setOnItemClickListener(this);
    this.mListView.setRecyclerListener(this);
    this.mAdapter = new ServiceListAdapter(this.mState);
    this.mListView.setAdapter(this.mAdapter);
    this.mColorBar = ((LinearColorBar)findViewById(2131231032));
    this.mBackgroundProcessText = ((TextView)findViewById(2131231034));
    this.mBackgroundProcessText.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        RunningProcessesView.this.mAdapter.setShowBackground(true);
      }
    });
    this.mForegroundProcessText = ((TextView)findViewById(2131231033));
    this.mForegroundProcessText.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        RunningProcessesView.this.mAdapter.setShowBackground(false);
      }
    });
    ActivityManager.MemoryInfo localMemoryInfo = new ActivityManager.MemoryInfo();
    this.mAm.getMemoryInfo(localMemoryInfo);
    this.SECONDARY_SERVER_MEM = localMemoryInfo.secondaryServerThreshold;
  }

  public void doPause()
  {
    this.mState.pause();
    this.mDataAvail = null;
    this.mOwner = null;
  }

  public boolean doResume(Fragment paramFragment, Runnable paramRunnable)
  {
    this.mOwner = paramFragment;
    this.mState.resume(this);
    if (this.mState.hasData())
    {
      refreshUi(true);
      return true;
    }
    this.mDataAvail = paramRunnable;
    return false;
  }

  public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    RunningState.MergedItem localMergedItem = (RunningState.MergedItem)((ListView)paramAdapterView).getAdapter().getItem(paramInt);
    this.mCurSelected = localMergedItem;
    startServiceDetailsActivity(localMergedItem);
  }

  public void onMovedToScrapHeap(View paramView)
  {
    this.mActiveItems.remove(paramView);
  }

  public void onRefreshUi(int paramInt)
  {
    switch (paramInt)
    {
    default:
      return;
    case 0:
      updateTimes();
      return;
    case 1:
      refreshUi(false);
      updateTimes();
      return;
    case 2:
    }
    refreshUi(true);
    updateTimes();
  }

  void refreshUi(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      ServiceListAdapter localServiceListAdapter = (ServiceListAdapter)this.mListView.getAdapter();
      localServiceListAdapter.refreshItems();
      localServiceListAdapter.notifyDataSetChanged();
    }
    if (this.mDataAvail != null)
    {
      this.mDataAvail.run();
      this.mDataAvail = null;
    }
    this.mMemInfoReader.readMemInfo();
    long l1 = this.mMemInfoReader.getFreeSize() + this.mMemInfoReader.getCachedSize() - this.SECONDARY_SERVER_MEM;
    if (l1 < 0L)
      l1 = 0L;
    synchronized (this.mState.mLock)
    {
      if ((this.mLastNumBackgroundProcesses != this.mState.mNumBackgroundProcesses) || (this.mLastBackgroundProcessMemory != this.mState.mBackgroundProcessMemory) || (this.mLastAvailMemory != l1))
      {
        this.mLastNumBackgroundProcesses = this.mState.mNumBackgroundProcesses;
        this.mLastBackgroundProcessMemory = this.mState.mBackgroundProcessMemory;
        this.mLastAvailMemory = l1;
        long l2 = this.mLastAvailMemory + this.mLastBackgroundProcessMemory;
        BidiFormatter localBidiFormatter = BidiFormatter.getInstance();
        String str1 = localBidiFormatter.unicodeWrap(Formatter.formatShortFileSize(getContext(), l2));
        this.mBackgroundProcessText.setText(getResources().getString(2131428474, new Object[] { str1 }));
        String str2 = localBidiFormatter.unicodeWrap(Formatter.formatShortFileSize(getContext(), this.mMemInfoReader.getTotalSize() - l2));
        this.mForegroundProcessText.setText(getResources().getString(2131428475, new Object[] { str2 }));
      }
      if ((this.mLastNumForegroundProcesses != this.mState.mNumForegroundProcesses) || (this.mLastForegroundProcessMemory != this.mState.mForegroundProcessMemory) || (this.mLastNumServiceProcesses != this.mState.mNumServiceProcesses) || (this.mLastServiceProcessMemory != this.mState.mServiceProcessMemory))
      {
        this.mLastNumForegroundProcesses = this.mState.mNumForegroundProcesses;
        this.mLastForegroundProcessMemory = this.mState.mForegroundProcessMemory;
        this.mLastNumServiceProcesses = this.mState.mNumServiceProcesses;
        this.mLastServiceProcessMemory = this.mState.mServiceProcessMemory;
      }
      float f1 = (float)this.mMemInfoReader.getTotalSize();
      float f2 = (float)(l1 + this.mLastBackgroundProcessMemory + this.mLastServiceProcessMemory);
      this.mColorBar.setRatios((f1 - f2) / f1, (float)this.mLastServiceProcessMemory / f1, (float)this.mLastBackgroundProcessMemory / f1);
      return;
    }
  }

  void updateTimes()
  {
    Iterator localIterator = this.mActiveItems.values().iterator();
    while (localIterator.hasNext())
    {
      ActiveItem localActiveItem = (ActiveItem)localIterator.next();
      if (localActiveItem.mRootView.getWindowToken() == null)
        localIterator.remove();
      else
        localActiveItem.updateTime(getContext(), this.mBuilder);
    }
  }

  public static class ActiveItem
  {
    long mFirstRunTime;
    RunningProcessesView.ViewHolder mHolder;
    RunningState.BaseItem mItem;
    View mRootView;
    boolean mSetBackground;

    void updateTime(Context paramContext, StringBuilder paramStringBuilder)
    {
      TextView localTextView;
      if ((this.mItem instanceof RunningState.ServiceItem))
        localTextView = this.mHolder.size;
      while (true)
      {
        if (localTextView != null)
        {
          this.mSetBackground = false;
          if (this.mFirstRunTime < 0L)
            break;
          localTextView.setText(DateUtils.formatElapsedTime(paramStringBuilder, (SystemClock.elapsedRealtime() - this.mFirstRunTime) / 1000L));
        }
        return;
        if (this.mItem.mSizeStr != null);
        for (String str = this.mItem.mSizeStr; ; str = "")
        {
          if (!str.equals(this.mItem.mCurSizeStr))
          {
            this.mItem.mCurSizeStr = str;
            this.mHolder.size.setText(str);
          }
          if (!this.mItem.mBackground)
            break label164;
          boolean bool3 = this.mSetBackground;
          localTextView = null;
          if (bool3)
            break;
          this.mSetBackground = true;
          this.mHolder.uptime.setText("");
          localTextView = null;
          break;
        }
        label164: boolean bool1 = this.mItem instanceof RunningState.MergedItem;
        localTextView = null;
        if (bool1)
          localTextView = this.mHolder.uptime;
      }
      boolean bool2 = this.mItem instanceof RunningState.MergedItem;
      int i = 0;
      if (bool2)
        if (((RunningState.MergedItem)this.mItem).mServices.size() <= 0)
          break label249;
      label249: for (i = 1; i != 0; i = 0)
      {
        localTextView.setText(paramContext.getResources().getText(2131428469));
        return;
      }
      localTextView.setText("");
    }
  }

  class ServiceListAdapter extends BaseAdapter
  {
    final LayoutInflater mInflater;
    final ArrayList<RunningState.MergedItem> mItems = new ArrayList();
    ArrayList<RunningState.MergedItem> mOrigItems;
    boolean mShowBackground;
    final RunningState mState;

    ServiceListAdapter(RunningState arg2)
    {
      Object localObject;
      this.mState = localObject;
      this.mInflater = ((LayoutInflater)RunningProcessesView.this.getContext().getSystemService("layout_inflater"));
      refreshItems();
    }

    public boolean areAllItemsEnabled()
    {
      return false;
    }

    public void bindView(View paramView, int paramInt)
    {
      synchronized (this.mState.mLock)
      {
        if (paramInt >= this.mItems.size())
          return;
        RunningProcessesView.ViewHolder localViewHolder = (RunningProcessesView.ViewHolder)paramView.getTag();
        RunningState.MergedItem localMergedItem = (RunningState.MergedItem)this.mItems.get(paramInt);
        RunningProcessesView.ActiveItem localActiveItem = localViewHolder.bind(this.mState, localMergedItem, RunningProcessesView.this.mBuilder);
        RunningProcessesView.this.mActiveItems.put(paramView, localActiveItem);
        return;
      }
    }

    public int getCount()
    {
      return this.mItems.size();
    }

    public Object getItem(int paramInt)
    {
      return this.mItems.get(paramInt);
    }

    public long getItemId(int paramInt)
    {
      return ((RunningState.MergedItem)this.mItems.get(paramInt)).hashCode();
    }

    boolean getShowBackground()
    {
      return this.mShowBackground;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView == null);
      for (View localView = newView(paramViewGroup); ; localView = paramView)
      {
        bindView(localView, paramInt);
        return localView;
      }
    }

    public boolean hasStableIds()
    {
      return true;
    }

    public boolean isEmpty()
    {
      return (this.mState.hasData()) && (this.mItems.size() == 0);
    }

    public boolean isEnabled(int paramInt)
    {
      return !((RunningState.MergedItem)this.mItems.get(paramInt)).mIsProcess;
    }

    public View newView(ViewGroup paramViewGroup)
    {
      View localView = this.mInflater.inflate(2130968700, paramViewGroup, false);
      new RunningProcessesView.ViewHolder(localView);
      return localView;
    }

    void refreshItems()
    {
      ArrayList localArrayList;
      if (this.mShowBackground)
      {
        localArrayList = this.mState.getCurrentBackgroundItems();
        if (this.mOrigItems != localArrayList)
        {
          this.mOrigItems = localArrayList;
          if (localArrayList != null)
            break label51;
          this.mItems.clear();
        }
      }
      label51: 
      do
      {
        return;
        localArrayList = this.mState.getCurrentMergedItems();
        break;
        this.mItems.clear();
        this.mItems.addAll(localArrayList);
      }
      while (!this.mShowBackground);
      Collections.sort(this.mItems, this.mState.mBackgroundComparator);
    }

    void setShowBackground(boolean paramBoolean)
    {
      if (this.mShowBackground != paramBoolean)
      {
        this.mShowBackground = paramBoolean;
        this.mState.setWatchingBackgroundItems(paramBoolean);
        refreshItems();
        notifyDataSetChanged();
        RunningProcessesView.this.mColorBar.setShowingGreen(this.mShowBackground);
      }
    }
  }

  static class TimeTicker extends TextView
  {
    public TimeTicker(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
  }

  public static class ViewHolder
  {
    public TextView description;
    public ImageView icon;
    public TextView name;
    public View rootView;
    public TextView size;
    public TextView uptime;

    public ViewHolder(View paramView)
    {
      this.rootView = paramView;
      this.icon = ((ImageView)paramView.findViewById(2131230755));
      this.name = ((TextView)paramView.findViewById(2131230837));
      this.description = ((TextView)paramView.findViewById(2131230838));
      this.size = ((TextView)paramView.findViewById(2131231031));
      this.uptime = ((TextView)paramView.findViewById(2131230748));
      paramView.setTag(this);
    }

    public RunningProcessesView.ActiveItem bind(RunningState paramRunningState, RunningState.BaseItem paramBaseItem, StringBuilder paramStringBuilder)
    {
      synchronized (paramRunningState.mLock)
      {
        PackageManager localPackageManager = this.rootView.getContext().getPackageManager();
        if ((paramBaseItem.mPackageInfo == null) && ((paramBaseItem instanceof RunningState.MergedItem)) && (((RunningState.MergedItem)paramBaseItem).mProcess != null))
        {
          ((RunningState.MergedItem)paramBaseItem).mProcess.ensureLabel(localPackageManager);
          paramBaseItem.mPackageInfo = ((RunningState.MergedItem)paramBaseItem).mProcess.mPackageInfo;
          paramBaseItem.mDisplayLabel = ((RunningState.MergedItem)paramBaseItem).mProcess.mDisplayLabel;
        }
        this.name.setText(paramBaseItem.mDisplayLabel);
        RunningProcessesView.ActiveItem localActiveItem = new RunningProcessesView.ActiveItem();
        localActiveItem.mRootView = this.rootView;
        localActiveItem.mItem = paramBaseItem;
        localActiveItem.mHolder = this;
        localActiveItem.mFirstRunTime = paramBaseItem.mActiveSince;
        if (paramBaseItem.mBackground)
        {
          this.description.setText(this.rootView.getContext().getText(2131428470));
          paramBaseItem.mCurSizeStr = null;
          this.icon.setImageDrawable(paramBaseItem.loadIcon(this.rootView.getContext(), paramRunningState));
          this.icon.setVisibility(0);
          localActiveItem.updateTime(this.rootView.getContext(), paramStringBuilder);
          return localActiveItem;
        }
        this.description.setText(paramBaseItem.mDescription);
      }
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.applications.RunningProcessesView
 * JD-Core Version:    0.6.2
 */