package com.android.settings;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppWidgetLoader<Item extends LabelledItem>
{
  private AppWidgetManager mAppWidgetManager;
  private Context mContext;
  ItemConstructor<Item> mItemConstructor;

  public AppWidgetLoader(Context paramContext, AppWidgetManager paramAppWidgetManager, ItemConstructor<Item> paramItemConstructor)
  {
    this.mContext = paramContext;
    this.mAppWidgetManager = paramAppWidgetManager;
    this.mItemConstructor = paramItemConstructor;
  }

  protected List<Item> getItems(Intent paramIntent)
  {
    boolean bool = paramIntent.getBooleanExtra("customSort", true);
    ArrayList localArrayList1 = new ArrayList();
    putInstalledAppWidgets(localArrayList1, paramIntent.getIntExtra("categoryFilter", 1));
    if (bool)
      putCustomAppWidgets(localArrayList1, paramIntent);
    Collections.sort(localArrayList1, new Comparator()
    {
      Collator mCollator = Collator.getInstance();

      public int compare(Item paramAnonymousItem1, Item paramAnonymousItem2)
      {
        return this.mCollator.compare(paramAnonymousItem1.getLabel(), paramAnonymousItem2.getLabel());
      }
    });
    if (!bool)
    {
      ArrayList localArrayList2 = new ArrayList();
      putCustomAppWidgets(localArrayList2, paramIntent);
      localArrayList1.addAll(localArrayList2);
    }
    return localArrayList1;
  }

  void putAppWidgetItems(List<AppWidgetProviderInfo> paramList, List<Bundle> paramList1, List<Item> paramList2, int paramInt, boolean paramBoolean)
  {
    if (paramList == null);
    int j;
    AppWidgetProviderInfo localAppWidgetProviderInfo;
    while (true)
    {
      return;
      int i = paramList.size();
      for (j = 0; j < i; j++)
      {
        localAppWidgetProviderInfo = (AppWidgetProviderInfo)paramList.get(j);
        if ((paramBoolean) || ((paramInt & localAppWidgetProviderInfo.widgetCategory) != 0))
          break label58;
      }
    }
    label58: ItemConstructor localItemConstructor = this.mItemConstructor;
    Context localContext = this.mContext;
    if (paramList1 != null);
    for (Bundle localBundle = (Bundle)paramList1.get(j); ; localBundle = null)
    {
      paramList2.add((LabelledItem)localItemConstructor.createItem(localContext, localAppWidgetProviderInfo, localBundle));
      break;
    }
  }

  void putCustomAppWidgets(List<Item> paramList, Intent paramIntent)
  {
    Object localObject = null;
    ArrayList localArrayList = paramIntent.getParcelableArrayListExtra("customInfo");
    if ((localArrayList == null) || (localArrayList.size() == 0))
      Log.i("AppWidgetAdapter", "EXTRA_CUSTOM_INFO not present.");
    label278: 
    while (true)
    {
      putAppWidgetItems(localArrayList, (List)localObject, paramList, 0, true);
      return;
      int i = localArrayList.size();
      for (int j = 0; ; j++)
      {
        if (j >= i)
          break label124;
        Parcelable localParcelable2 = (Parcelable)localArrayList.get(j);
        if ((localParcelable2 == null) || (!(localParcelable2 instanceof AppWidgetProviderInfo)))
        {
          Log.e("AppWidgetAdapter", "error using EXTRA_CUSTOM_INFO index=" + j);
          localArrayList = null;
          localObject = null;
          break;
        }
      }
      label124: localObject = paramIntent.getParcelableArrayListExtra("customExtras");
      if (localObject == null)
      {
        Log.e("AppWidgetAdapter", "EXTRA_CUSTOM_INFO without EXTRA_CUSTOM_EXTRAS");
        localArrayList = null;
      }
      else
      {
        int k = ((ArrayList)localObject).size();
        if (i != k)
        {
          Log.e("AppWidgetAdapter", "list size mismatch: EXTRA_CUSTOM_INFO: " + i + " EXTRA_CUSTOM_EXTRAS: " + k);
          localArrayList = null;
          localObject = null;
        }
        else
        {
          for (int m = 0; ; m++)
          {
            if (m >= k)
              break label278;
            Parcelable localParcelable1 = (Parcelable)((ArrayList)localObject).get(m);
            if ((localParcelable1 == null) || (!(localParcelable1 instanceof Bundle)))
            {
              Log.e("AppWidgetAdapter", "error using EXTRA_CUSTOM_EXTRAS index=" + m);
              localArrayList = null;
              localObject = null;
              break;
            }
          }
        }
      }
    }
  }

  void putInstalledAppWidgets(List<Item> paramList, int paramInt)
  {
    putAppWidgetItems(this.mAppWidgetManager.getInstalledProviders(paramInt), null, paramList, paramInt, false);
  }

  public static abstract interface ItemConstructor<Item>
  {
    public abstract Item createItem(Context paramContext, AppWidgetProviderInfo paramAppWidgetProviderInfo, Bundle paramBundle);
  }

  static abstract interface LabelledItem
  {
    public abstract CharSequence getLabel();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.AppWidgetLoader
 * JD-Core Version:    0.6.2
 */