package android.support.v4.view.accessibility;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

class AccessibilityNodeInfoCompatIcs
{
  public static void addAction(Object paramObject, int paramInt)
  {
    ((AccessibilityNodeInfo)paramObject).addAction(paramInt);
  }

  public static int getActions(Object paramObject)
  {
    return ((AccessibilityNodeInfo)paramObject).getActions();
  }

  public static void getBoundsInParent(Object paramObject, Rect paramRect)
  {
    ((AccessibilityNodeInfo)paramObject).getBoundsInParent(paramRect);
  }

  public static void getBoundsInScreen(Object paramObject, Rect paramRect)
  {
    ((AccessibilityNodeInfo)paramObject).getBoundsInScreen(paramRect);
  }

  public static CharSequence getClassName(Object paramObject)
  {
    return ((AccessibilityNodeInfo)paramObject).getClassName();
  }

  public static CharSequence getContentDescription(Object paramObject)
  {
    return ((AccessibilityNodeInfo)paramObject).getContentDescription();
  }

  public static CharSequence getPackageName(Object paramObject)
  {
    return ((AccessibilityNodeInfo)paramObject).getPackageName();
  }

  public static CharSequence getText(Object paramObject)
  {
    return ((AccessibilityNodeInfo)paramObject).getText();
  }

  public static boolean isCheckable(Object paramObject)
  {
    return ((AccessibilityNodeInfo)paramObject).isCheckable();
  }

  public static boolean isChecked(Object paramObject)
  {
    return ((AccessibilityNodeInfo)paramObject).isChecked();
  }

  public static boolean isClickable(Object paramObject)
  {
    return ((AccessibilityNodeInfo)paramObject).isClickable();
  }

  public static boolean isEnabled(Object paramObject)
  {
    return ((AccessibilityNodeInfo)paramObject).isEnabled();
  }

  public static boolean isFocusable(Object paramObject)
  {
    return ((AccessibilityNodeInfo)paramObject).isFocusable();
  }

  public static boolean isFocused(Object paramObject)
  {
    return ((AccessibilityNodeInfo)paramObject).isFocused();
  }

  public static boolean isLongClickable(Object paramObject)
  {
    return ((AccessibilityNodeInfo)paramObject).isLongClickable();
  }

  public static boolean isPassword(Object paramObject)
  {
    return ((AccessibilityNodeInfo)paramObject).isPassword();
  }

  public static boolean isScrollable(Object paramObject)
  {
    return ((AccessibilityNodeInfo)paramObject).isScrollable();
  }

  public static boolean isSelected(Object paramObject)
  {
    return ((AccessibilityNodeInfo)paramObject).isSelected();
  }

  public static void setClassName(Object paramObject, CharSequence paramCharSequence)
  {
    ((AccessibilityNodeInfo)paramObject).setClassName(paramCharSequence);
  }

  public static void setScrollable(Object paramObject, boolean paramBoolean)
  {
    ((AccessibilityNodeInfo)paramObject).setScrollable(paramBoolean);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     android.support.v4.view.accessibility.AccessibilityNodeInfoCompatIcs
 * JD-Core Version:    0.6.2
 */