package com.android.settings.applications;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceFrameLayout;
import android.preference.PreferenceFrameLayout.LayoutParams;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AppOpsSummary extends Fragment
{
  static AppOpsState.OpsTemplate[] sPageTemplates = arrayOfOpsTemplate;
  private ViewGroup mContentContainer;
  int mCurPos;
  private LayoutInflater mInflater;
  CharSequence[] mPageNames;
  private View mRootView;
  private ViewPager mViewPager;

  static
  {
    AppOpsState.OpsTemplate[] arrayOfOpsTemplate = new AppOpsState.OpsTemplate[5];
    arrayOfOpsTemplate[0] = AppOpsState.LOCATION_TEMPLATE;
    arrayOfOpsTemplate[1] = AppOpsState.PERSONAL_TEMPLATE;
    arrayOfOpsTemplate[2] = AppOpsState.MESSAGING_TEMPLATE;
    arrayOfOpsTemplate[3] = AppOpsState.MEDIA_TEMPLATE;
    arrayOfOpsTemplate[4] = AppOpsState.DEVICE_TEMPLATE;
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mInflater = paramLayoutInflater;
    View localView = this.mInflater.inflate(2130968585, paramViewGroup, false);
    this.mContentContainer = paramViewGroup;
    this.mRootView = localView;
    this.mPageNames = getResources().getTextArray(2131165235);
    this.mViewPager = ((ViewPager)localView.findViewById(2131230737));
    MyPagerAdapter localMyPagerAdapter = new MyPagerAdapter(getChildFragmentManager());
    this.mViewPager.setAdapter(localMyPagerAdapter);
    this.mViewPager.setOnPageChangeListener(localMyPagerAdapter);
    ((PagerTabStrip)localView.findViewById(2131230738)).setTabIndicatorColorResource(17170450);
    if ((paramViewGroup instanceof PreferenceFrameLayout))
      ((PreferenceFrameLayout.LayoutParams)localView.getLayoutParams()).removeBorders = true;
    return localView;
  }

  class MyPagerAdapter extends FragmentPagerAdapter
    implements ViewPager.OnPageChangeListener
  {
    public MyPagerAdapter(FragmentManager arg2)
    {
      super();
    }

    public int getCount()
    {
      return AppOpsSummary.sPageTemplates.length;
    }

    public Fragment getItem(int paramInt)
    {
      return new AppOpsCategory(AppOpsSummary.sPageTemplates[paramInt]);
    }

    public CharSequence getPageTitle(int paramInt)
    {
      return AppOpsSummary.this.mPageNames[paramInt];
    }

    public void onPageScrollStateChanged(int paramInt)
    {
      if (paramInt == 0);
    }

    public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2)
    {
    }

    public void onPageSelected(int paramInt)
    {
      AppOpsSummary.this.mCurPos = paramInt;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.applications.AppOpsSummary
 * JD-Core Version:    0.6.2
 */