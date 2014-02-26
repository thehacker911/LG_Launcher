package com.android.settings.quicklaunch;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ResolveInfo.DisplayNameComparator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BookmarkPicker extends ListActivity
  implements SimpleAdapter.ViewBinder
{
  private static final String[] sKeys = { "TITLE", "RESOLVE_INFO" };
  private static Intent sLaunchIntent;
  private static final int[] sResourceIds = { 2131230756, 2131230755 };
  private static Intent sShortcutIntent;
  private int mDisplayMode = 0;
  private SimpleAdapter mMyAdapter;
  private List<ResolveInfo> mResolveList;
  private Handler mUiHandler = new Handler();

  private SimpleAdapter createResolveAdapter(List<Map<String, ?>> paramList)
  {
    SimpleAdapter localSimpleAdapter = new SimpleAdapter(this, paramList, 2130968595, sKeys, sResourceIds);
    localSimpleAdapter.setViewBinder(this);
    return localSimpleAdapter;
  }

  private void ensureIntents()
  {
    if (sLaunchIntent == null)
    {
      sLaunchIntent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER");
      sShortcutIntent = new Intent("android.intent.action.CREATE_SHORTCUT");
    }
  }

  private void fillAdapterList(List<Map<String, ?>> paramList, List<ResolveInfo> paramList1)
  {
    paramList.clear();
    int i = paramList1.size();
    for (int j = 0; j < i; j++)
    {
      ResolveInfo localResolveInfo = (ResolveInfo)paramList1.get(j);
      TreeMap localTreeMap = new TreeMap();
      localTreeMap.put("TITLE", getResolveInfoTitle(localResolveInfo));
      localTreeMap.put("RESOLVE_INFO", localResolveInfo);
      paramList.add(localTreeMap);
    }
  }

  private void fillResolveList(List<ResolveInfo> paramList)
  {
    ensureIntents();
    PackageManager localPackageManager = getPackageManager();
    paramList.clear();
    if (this.mDisplayMode == 0)
      paramList.addAll(localPackageManager.queryIntentActivities(sLaunchIntent, 0));
    while (this.mDisplayMode != 1)
      return;
    paramList.addAll(localPackageManager.queryIntentActivities(sShortcutIntent, 0));
  }

  private void finish(Intent paramIntent, String paramString)
  {
    paramIntent.putExtras(getIntent());
    paramIntent.putExtra("com.android.settings.quicklaunch.TITLE", paramString);
    setResult(-1, paramIntent);
    finish();
  }

  private static Intent getIntentForResolveInfo(ResolveInfo paramResolveInfo, String paramString)
  {
    Intent localIntent = new Intent(paramString);
    ActivityInfo localActivityInfo = paramResolveInfo.activityInfo;
    localIntent.setClassName(localActivityInfo.packageName, localActivityInfo.name);
    return localIntent;
  }

  private String getResolveInfoTitle(ResolveInfo paramResolveInfo)
  {
    Object localObject = paramResolveInfo.loadLabel(getPackageManager());
    if (localObject == null)
      localObject = paramResolveInfo.activityInfo.name;
    if (localObject != null)
      return localObject.toString();
    return null;
  }

  private void startShortcutActivity(ResolveInfo paramResolveInfo)
  {
    startActivityForResult(getIntentForResolveInfo(paramResolveInfo, "android.intent.action.CREATE_SHORTCUT"), 1);
  }

  private void updateAdapterToUseNewLists(final ArrayList<Map<String, ?>> paramArrayList, final ArrayList<ResolveInfo> paramArrayList1)
  {
    this.mUiHandler.post(new Runnable()
    {
      public void run()
      {
        BookmarkPicker.access$302(BookmarkPicker.this, BookmarkPicker.this.createResolveAdapter(paramArrayList));
        BookmarkPicker.access$502(BookmarkPicker.this, paramArrayList1);
        BookmarkPicker.this.setListAdapter(BookmarkPicker.this.mMyAdapter);
      }
    });
  }

  private void updateListAndAdapter()
  {
    new Thread("data updater")
    {
      public void run()
      {
        synchronized (BookmarkPicker.this)
        {
          ArrayList localArrayList1 = new ArrayList();
          ArrayList localArrayList2 = new ArrayList();
          BookmarkPicker.this.fillResolveList(localArrayList1);
          Collections.sort(localArrayList1, new ResolveInfo.DisplayNameComparator(BookmarkPicker.this.getPackageManager()));
          BookmarkPicker.this.fillAdapterList(localArrayList2, localArrayList1);
          BookmarkPicker.this.updateAdapterToUseNewLists(localArrayList2, localArrayList1);
          return;
        }
      }
    }
    .start();
  }

  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt2 != -1);
    do
    {
      return;
      switch (paramInt1)
      {
      default:
        super.onActivityResult(paramInt1, paramInt2, paramIntent);
        return;
      case 1:
      }
    }
    while (paramIntent == null);
    finish((Intent)paramIntent.getParcelableExtra("android.intent.extra.shortcut.INTENT"), paramIntent.getStringExtra("android.intent.extra.shortcut.NAME"));
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    updateListAndAdapter();
  }

  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    paramMenu.add(0, 0, 0, 2131428560).setIcon(17302322);
    paramMenu.add(0, 1, 0, 2131428561).setIcon(17302346);
    return true;
  }

  protected void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
  {
    if (paramInt >= this.mResolveList.size())
      return;
    ResolveInfo localResolveInfo = (ResolveInfo)this.mResolveList.get(paramInt);
    switch (this.mDisplayMode)
    {
    default:
      return;
    case 0:
      Intent localIntent = getIntentForResolveInfo(localResolveInfo, "android.intent.action.MAIN");
      localIntent.addCategory("android.intent.category.LAUNCHER");
      finish(localIntent, getResolveInfoTitle(localResolveInfo));
      return;
    case 1:
    }
    startShortcutActivity(localResolveInfo);
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default:
      return false;
    case 0:
    case 1:
    }
    for (this.mDisplayMode = 0; ; this.mDisplayMode = 1)
    {
      updateListAndAdapter();
      return true;
    }
  }

  public boolean onPrepareOptionsMenu(Menu paramMenu)
  {
    MenuItem localMenuItem1 = paramMenu.findItem(0);
    if (this.mDisplayMode != 0);
    for (boolean bool1 = true; ; bool1 = false)
    {
      localMenuItem1.setVisible(bool1);
      MenuItem localMenuItem2 = paramMenu.findItem(1);
      int i = this.mDisplayMode;
      boolean bool2 = false;
      if (i != 1)
        bool2 = true;
      localMenuItem2.setVisible(bool2);
      return true;
    }
  }

  public boolean setViewValue(View paramView, Object paramObject, String paramString)
  {
    if (paramView.getId() == 2131230755)
    {
      Drawable localDrawable = ((ResolveInfo)paramObject).loadIcon(getPackageManager());
      if (localDrawable != null)
        ((ImageView)paramView).setImageDrawable(localDrawable);
      return true;
    }
    return false;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.quicklaunch.BookmarkPicker
 * JD-Core Version:    0.6.2
 */