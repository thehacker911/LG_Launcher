package com.android.settings;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.ListFragment;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.xmlpull.v1.XmlPullParserException;

public class ZonePicker extends ListFragment
{
  private SimpleAdapter mAlphabeticalAdapter;
  private ZoneSelectionListener mListener;
  private boolean mSortedByTimezone;
  private SimpleAdapter mTimezoneSortedAdapter;

  private static void addItem(List<HashMap<String, Object>> paramList, String paramString1, String paramString2, long paramLong)
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("id", paramString1);
    localHashMap.put("name", paramString2);
    int i = TimeZone.getTimeZone(paramString1).getOffset(paramLong);
    int j = Math.abs(i);
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("GMT");
    if (i < 0)
      localStringBuilder.append('-');
    while (true)
    {
      localStringBuilder.append(j / 3600000);
      localStringBuilder.append(':');
      int k = j / 60000 % 60;
      if (k < 10)
        localStringBuilder.append('0');
      localStringBuilder.append(k);
      localHashMap.put("gmt", localStringBuilder.toString());
      localHashMap.put("offset", Integer.valueOf(i));
      paramList.add(localHashMap);
      return;
      localStringBuilder.append('+');
    }
  }

  public static SimpleAdapter constructTimezoneAdapter(Context paramContext, boolean paramBoolean)
  {
    return constructTimezoneAdapter(paramContext, paramBoolean, 2130968621);
  }

  public static SimpleAdapter constructTimezoneAdapter(Context paramContext, boolean paramBoolean, int paramInt)
  {
    String[] arrayOfString = { "name", "gmt" };
    int[] arrayOfInt = { 16908308, 16908309 };
    if (paramBoolean);
    for (String str = "name"; ; str = "offset")
    {
      MyComparator localMyComparator = new MyComparator(str);
      List localList = getZones(paramContext);
      Collections.sort(localList, localMyComparator);
      return new SimpleAdapter(paramContext, localList, paramInt, arrayOfString, arrayOfInt);
    }
  }

  public static int getTimeZoneIndex(SimpleAdapter paramSimpleAdapter, TimeZone paramTimeZone)
  {
    String str = paramTimeZone.getID();
    int i = paramSimpleAdapter.getCount();
    for (int j = 0; j < i; j++)
      if (str.equals((String)((HashMap)paramSimpleAdapter.getItem(j)).get("id")))
        return j;
    return -1;
  }

  private static List<HashMap<String, Object>> getZones(Context paramContext)
  {
    ArrayList localArrayList = new ArrayList();
    long l = Calendar.getInstance().getTimeInMillis();
    XmlResourceParser localXmlResourceParser;
    while (true)
    {
      try
      {
        localXmlResourceParser = paramContext.getResources().getXml(2131034164);
        if (localXmlResourceParser.next() != 2)
          continue;
        localXmlResourceParser.next();
        if (localXmlResourceParser.getEventType() == 3)
          break;
        if (localXmlResourceParser.getEventType() != 2)
        {
          if (localXmlResourceParser.getEventType() == 1)
            return localArrayList;
          localXmlResourceParser.next();
          continue;
        }
      }
      catch (XmlPullParserException localXmlPullParserException)
      {
        Log.e("ZonePicker", "Ill-formatted timezones.xml file");
        return localArrayList;
        if (localXmlResourceParser.getName().equals("timezone"))
          addItem(localArrayList, localXmlResourceParser.getAttributeValue(0), localXmlResourceParser.nextText(), l);
        if (localXmlResourceParser.getEventType() != 3)
        {
          localXmlResourceParser.next();
          continue;
        }
      }
      catch (IOException localIOException)
      {
        Log.e("ZonePicker", "Unable to read timezones.xml file");
        return localArrayList;
      }
      localXmlResourceParser.next();
    }
    localXmlResourceParser.close();
    return localArrayList;
  }

  public static TimeZone obtainTimeZoneFromItem(Object paramObject)
  {
    return TimeZone.getTimeZone((String)((Map)paramObject).get("id"));
  }

  private void setSorting(boolean paramBoolean)
  {
    if (paramBoolean);
    for (SimpleAdapter localSimpleAdapter = this.mTimezoneSortedAdapter; ; localSimpleAdapter = this.mAlphabeticalAdapter)
    {
      setListAdapter(localSimpleAdapter);
      this.mSortedByTimezone = paramBoolean;
      int i = getTimeZoneIndex(localSimpleAdapter, TimeZone.getDefault());
      if (i >= 0)
        setSelection(i);
      return;
    }
  }

  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    Activity localActivity = getActivity();
    this.mTimezoneSortedAdapter = constructTimezoneAdapter(localActivity, false);
    this.mAlphabeticalAdapter = constructTimezoneAdapter(localActivity, true);
    setSorting(true);
    setHasOptionsMenu(true);
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    paramMenu.add(0, 1, 0, 2131427599).setIcon(17301660);
    paramMenu.add(0, 2, 0, 2131427600).setIcon(2130837588);
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    View localView = super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
    Utils.forcePrepareCustomPreferencesList(paramViewGroup, localView, (ListView)localView.findViewById(16908298), false);
    return localView;
  }

  public void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
  {
    if (!isResumed())
      return;
    String str = (String)((Map)paramListView.getItemAtPosition(paramInt)).get("id");
    ((AlarmManager)getActivity().getSystemService("alarm")).setTimeZone(str);
    TimeZone localTimeZone = TimeZone.getTimeZone(str);
    if (this.mListener != null)
    {
      this.mListener.onZoneSelected(localTimeZone);
      return;
    }
    getActivity().onBackPressed();
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default:
      return false;
    case 2:
      setSorting(true);
      return true;
    case 1:
    }
    setSorting(false);
    return true;
  }

  public void onPrepareOptionsMenu(Menu paramMenu)
  {
    if (this.mSortedByTimezone)
    {
      paramMenu.findItem(2).setVisible(false);
      paramMenu.findItem(1).setVisible(true);
      return;
    }
    paramMenu.findItem(2).setVisible(true);
    paramMenu.findItem(1).setVisible(false);
  }

  private static class MyComparator
    implements Comparator<HashMap<?, ?>>
  {
    private String mSortingKey;

    public MyComparator(String paramString)
    {
      this.mSortingKey = paramString;
    }

    private boolean isComparable(Object paramObject)
    {
      return (paramObject != null) && ((paramObject instanceof Comparable));
    }

    public int compare(HashMap<?, ?> paramHashMap1, HashMap<?, ?> paramHashMap2)
    {
      Object localObject1 = paramHashMap1.get(this.mSortingKey);
      Object localObject2 = paramHashMap2.get(this.mSortingKey);
      if (!isComparable(localObject1))
      {
        if (isComparable(localObject2))
          return 1;
        return 0;
      }
      if (!isComparable(localObject2))
        return -1;
      return ((Comparable)localObject1).compareTo(localObject2);
    }
  }

  public static abstract interface ZoneSelectionListener
  {
    public abstract void onZoneSelected(TimeZone paramTimeZone);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.ZonePicker
 * JD-Core Version:    0.6.2
 */