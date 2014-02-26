package com.android.settings;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import java.util.List;

public class AppWidgetPickActivity extends ActivityPicker
  implements AppWidgetLoader.ItemConstructor<ActivityPicker.PickAdapter.Item>
{
  private int mAppWidgetId;
  private AppWidgetLoader<ActivityPicker.PickAdapter.Item> mAppWidgetLoader;
  private AppWidgetManager mAppWidgetManager;
  List<ActivityPicker.PickAdapter.Item> mItems;
  private PackageManager mPackageManager;

  public ActivityPicker.PickAdapter.Item createItem(Context paramContext, AppWidgetProviderInfo paramAppWidgetProviderInfo, Bundle paramBundle)
  {
    String str = paramAppWidgetProviderInfo.label;
    int i = paramAppWidgetProviderInfo.icon;
    Object localObject = null;
    if (i != 0);
    try
    {
      int j = paramContext.getResources().getDisplayMetrics().densityDpi;
      switch (j)
      {
      default:
      case 160:
      case 213:
      case 240:
      case 320:
      case 480:
      }
      while (true)
      {
        int k = (int)(0.5F + 0.75F * j);
        Drawable localDrawable = this.mPackageManager.getResourcesForApplication(paramAppWidgetProviderInfo.provider.getPackageName()).getDrawableForDensity(paramAppWidgetProviderInfo.icon, k);
        localObject = localDrawable;
        if (localObject == null)
          Log.w("AppWidgetPickActivity", "Can't load icon drawable 0x" + Integer.toHexString(paramAppWidgetProviderInfo.icon) + " for provider: " + paramAppWidgetProviderInfo.provider);
        ActivityPicker.PickAdapter.Item localItem = new ActivityPicker.PickAdapter.Item(paramContext, str, localObject);
        localItem.packageName = paramAppWidgetProviderInfo.provider.getPackageName();
        localItem.className = paramAppWidgetProviderInfo.provider.getClassName();
        localItem.extras = paramBundle;
        return localItem;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      while (true)
      {
        Log.w("AppWidgetPickActivity", "Can't load icon drawable 0x" + Integer.toHexString(paramAppWidgetProviderInfo.icon) + " for provider: " + paramAppWidgetProviderInfo.provider);
        localObject = null;
      }
    }
  }

  protected List<ActivityPicker.PickAdapter.Item> getItems()
  {
    this.mItems = this.mAppWidgetLoader.getItems(getIntent());
    return this.mItems;
  }

  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    Intent localIntent = getIntentForPosition(paramInt);
    if (((ActivityPicker.PickAdapter.Item)this.mItems.get(paramInt)).extras != null)
      setResultData(-1, localIntent);
    while (true)
    {
      finish();
      return;
      try
      {
        Bundle localBundle1 = localIntent.getExtras();
        Bundle localBundle2 = null;
        if (localBundle1 != null)
          localBundle2 = localIntent.getExtras().getBundle("appWidgetOptions");
        this.mAppWidgetManager.bindAppWidgetId(this.mAppWidgetId, localIntent.getComponent(), localBundle2);
        i = -1;
        setResultData(i, null);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        while (true)
          int i = 0;
      }
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    this.mPackageManager = getPackageManager();
    this.mAppWidgetManager = AppWidgetManager.getInstance(this);
    this.mAppWidgetLoader = new AppWidgetLoader(this, this.mAppWidgetManager, this);
    super.onCreate(paramBundle);
    setResultData(0, null);
    Intent localIntent = getIntent();
    if (localIntent.hasExtra("appWidgetId"))
    {
      this.mAppWidgetId = localIntent.getIntExtra("appWidgetId", 0);
      return;
    }
    finish();
  }

  void setResultData(int paramInt, Intent paramIntent)
  {
    if (paramIntent != null);
    for (Intent localIntent = paramIntent; ; localIntent = new Intent())
    {
      localIntent.putExtra("appWidgetId", this.mAppWidgetId);
      setResult(paramInt, localIntent);
      return;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.AppWidgetPickActivity
 * JD-Core Version:    0.6.2
 */