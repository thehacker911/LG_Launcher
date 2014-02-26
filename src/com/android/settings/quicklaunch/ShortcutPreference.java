package com.android.settings.quicklaunch;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources.Theme;
import android.preference.Preference;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

public class ShortcutPreference extends Preference
  implements Comparable<Preference>
{
  private static String STRING_ASSIGN_APPLICATION;
  private static String STRING_NO_SHORTCUT;
  private static int sDimAlpha;
  private static ColorStateList sDimSummaryColor;
  private static ColorStateList sDimTitleColor;
  private static ColorStateList sRegularSummaryColor;
  private static ColorStateList sRegularTitleColor;
  private static Object sStaticVarsLock = new Object();
  private boolean mHasBookmark;
  private char mShortcut;

  public ShortcutPreference(Context paramContext, char paramChar)
  {
    super(paramContext);
    synchronized (sStaticVarsLock)
    {
      if (STRING_ASSIGN_APPLICATION == null)
      {
        STRING_ASSIGN_APPLICATION = paramContext.getString(2131428553);
        STRING_NO_SHORTCUT = paramContext.getString(2131428554);
        TypedValue localTypedValue = new TypedValue();
        paramContext.getTheme().resolveAttribute(16842803, localTypedValue, true);
        sDimAlpha = (int)(255.0F * localTypedValue.getFloat());
      }
      this.mShortcut = paramChar;
      setWidgetLayoutResource(2130968688);
      return;
    }
  }

  public int compareTo(Preference paramPreference)
  {
    if (!(paramPreference instanceof ShortcutPreference))
      return super.compareTo(paramPreference);
    char c = ((ShortcutPreference)paramPreference).mShortcut;
    if ((Character.isDigit(this.mShortcut)) && (Character.isLetter(c)))
      return 1;
    if ((Character.isDigit(c)) && (Character.isLetter(this.mShortcut)))
      return -1;
    return this.mShortcut - c;
  }

  public char getShortcut()
  {
    return this.mShortcut;
  }

  public CharSequence getSummary()
  {
    if (this.mHasBookmark)
      return super.getSummary();
    return STRING_NO_SHORTCUT;
  }

  public CharSequence getTitle()
  {
    if (this.mHasBookmark)
      return super.getTitle();
    return STRING_ASSIGN_APPLICATION;
  }

  public boolean hasBookmark()
  {
    return this.mHasBookmark;
  }

  protected void onBindView(View paramView)
  {
    super.onBindView(paramView);
    TextView localTextView1 = (TextView)paramView.findViewById(2131230979);
    if (localTextView1 != null)
      localTextView1.setText(String.valueOf(this.mShortcut));
    TextView localTextView2 = (TextView)paramView.findViewById(16908310);
    ColorStateList localColorStateList1;
    TextView localTextView3;
    synchronized (sStaticVarsLock)
    {
      if (sRegularTitleColor == null)
      {
        sRegularTitleColor = localTextView2.getTextColors();
        sDimTitleColor = sRegularTitleColor.withAlpha(sDimAlpha);
      }
      if (this.mHasBookmark)
      {
        localColorStateList1 = sRegularTitleColor;
        if (localColorStateList1 != null)
          localTextView2.setTextColor(localColorStateList1);
        localTextView3 = (TextView)paramView.findViewById(16908304);
      }
    }
    while (true)
    {
      synchronized (sStaticVarsLock)
      {
        if (sRegularSummaryColor == null)
        {
          sRegularSummaryColor = localTextView3.getTextColors();
          sDimSummaryColor = sRegularSummaryColor.withAlpha(sDimAlpha);
        }
        if (this.mHasBookmark)
        {
          localColorStateList2 = sRegularSummaryColor;
          if (localColorStateList2 != null)
            localTextView3.setTextColor(localColorStateList2);
          return;
          localObject2 = finally;
          throw localObject2;
          localColorStateList1 = sDimTitleColor;
        }
      }
      ColorStateList localColorStateList2 = sDimSummaryColor;
    }
  }

  public void setHasBookmark(boolean paramBoolean)
  {
    if (paramBoolean != this.mHasBookmark)
    {
      this.mHasBookmark = paramBoolean;
      notifyChanged();
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.quicklaunch.ShortcutPreference
 * JD-Core Version:    0.6.2
 */