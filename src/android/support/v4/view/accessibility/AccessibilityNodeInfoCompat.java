package android.support.v4.view.accessibility;

import android.graphics.Rect;
import android.os.Build.VERSION;

public class AccessibilityNodeInfoCompat
{
  private static final AccessibilityNodeInfoImpl IMPL = new AccessibilityNodeInfoStubImpl();
  private final Object mInfo;

  static
  {
    if (Build.VERSION.SDK_INT >= 19)
    {
      IMPL = new AccessibilityNodeInfoKitKatImpl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 18)
    {
      IMPL = new AccessibilityNodeInfoJellybeanMr2Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 16)
    {
      IMPL = new AccessibilityNodeInfoJellybeanImpl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 14)
    {
      IMPL = new AccessibilityNodeInfoIcsImpl();
      return;
    }
  }

  public AccessibilityNodeInfoCompat(Object paramObject)
  {
    this.mInfo = paramObject;
  }

  private static String getActionSymbolicName(int paramInt)
  {
    switch (paramInt)
    {
    default:
      return "ACTION_UNKNOWN";
    case 1:
      return "ACTION_FOCUS";
    case 2:
      return "ACTION_CLEAR_FOCUS";
    case 4:
      return "ACTION_SELECT";
    case 8:
      return "ACTION_CLEAR_SELECTION";
    case 16:
      return "ACTION_CLICK";
    case 32:
      return "ACTION_LONG_CLICK";
    case 64:
      return "ACTION_ACCESSIBILITY_FOCUS";
    case 128:
      return "ACTION_CLEAR_ACCESSIBILITY_FOCUS";
    case 256:
      return "ACTION_NEXT_AT_MOVEMENT_GRANULARITY";
    case 512:
      return "ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY";
    case 1024:
      return "ACTION_NEXT_HTML_ELEMENT";
    case 2048:
      return "ACTION_PREVIOUS_HTML_ELEMENT";
    case 4096:
      return "ACTION_SCROLL_FORWARD";
    case 8192:
      return "ACTION_SCROLL_BACKWARD";
    case 65536:
      return "ACTION_CUT";
    case 16384:
      return "ACTION_COPY";
    case 32768:
      return "ACTION_PASTE";
    case 131072:
    }
    return "ACTION_SET_SELECTION";
  }

  public void addAction(int paramInt)
  {
    IMPL.addAction(this.mInfo, paramInt);
  }

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    AccessibilityNodeInfoCompat localAccessibilityNodeInfoCompat;
    do
    {
      do
      {
        return true;
        if (paramObject == null)
          return false;
        if (getClass() != paramObject.getClass())
          return false;
        localAccessibilityNodeInfoCompat = (AccessibilityNodeInfoCompat)paramObject;
        if (this.mInfo != null)
          break;
      }
      while (localAccessibilityNodeInfoCompat.mInfo == null);
      return false;
    }
    while (this.mInfo.equals(localAccessibilityNodeInfoCompat.mInfo));
    return false;
  }

  public int getActions()
  {
    return IMPL.getActions(this.mInfo);
  }

  public void getBoundsInParent(Rect paramRect)
  {
    IMPL.getBoundsInParent(this.mInfo, paramRect);
  }

  public void getBoundsInScreen(Rect paramRect)
  {
    IMPL.getBoundsInScreen(this.mInfo, paramRect);
  }

  public CharSequence getClassName()
  {
    return IMPL.getClassName(this.mInfo);
  }

  public CharSequence getContentDescription()
  {
    return IMPL.getContentDescription(this.mInfo);
  }

  public Object getInfo()
  {
    return this.mInfo;
  }

  public CharSequence getPackageName()
  {
    return IMPL.getPackageName(this.mInfo);
  }

  public CharSequence getText()
  {
    return IMPL.getText(this.mInfo);
  }

  public String getViewIdResourceName()
  {
    return IMPL.getViewIdResourceName(this.mInfo);
  }

  public int hashCode()
  {
    if (this.mInfo == null)
      return 0;
    return this.mInfo.hashCode();
  }

  public boolean isCheckable()
  {
    return IMPL.isCheckable(this.mInfo);
  }

  public boolean isChecked()
  {
    return IMPL.isChecked(this.mInfo);
  }

  public boolean isClickable()
  {
    return IMPL.isClickable(this.mInfo);
  }

  public boolean isEnabled()
  {
    return IMPL.isEnabled(this.mInfo);
  }

  public boolean isFocusable()
  {
    return IMPL.isFocusable(this.mInfo);
  }

  public boolean isFocused()
  {
    return IMPL.isFocused(this.mInfo);
  }

  public boolean isLongClickable()
  {
    return IMPL.isLongClickable(this.mInfo);
  }

  public boolean isPassword()
  {
    return IMPL.isPassword(this.mInfo);
  }

  public boolean isScrollable()
  {
    return IMPL.isScrollable(this.mInfo);
  }

  public boolean isSelected()
  {
    return IMPL.isSelected(this.mInfo);
  }

  public void setClassName(CharSequence paramCharSequence)
  {
    IMPL.setClassName(this.mInfo, paramCharSequence);
  }

  public void setScrollable(boolean paramBoolean)
  {
    IMPL.setScrollable(this.mInfo, paramBoolean);
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(super.toString());
    Rect localRect = new Rect();
    getBoundsInParent(localRect);
    localStringBuilder.append("; boundsInParent: " + localRect);
    getBoundsInScreen(localRect);
    localStringBuilder.append("; boundsInScreen: " + localRect);
    localStringBuilder.append("; packageName: ").append(getPackageName());
    localStringBuilder.append("; className: ").append(getClassName());
    localStringBuilder.append("; text: ").append(getText());
    localStringBuilder.append("; contentDescription: ").append(getContentDescription());
    localStringBuilder.append("; viewId: ").append(getViewIdResourceName());
    localStringBuilder.append("; checkable: ").append(isCheckable());
    localStringBuilder.append("; checked: ").append(isChecked());
    localStringBuilder.append("; focusable: ").append(isFocusable());
    localStringBuilder.append("; focused: ").append(isFocused());
    localStringBuilder.append("; selected: ").append(isSelected());
    localStringBuilder.append("; clickable: ").append(isClickable());
    localStringBuilder.append("; longClickable: ").append(isLongClickable());
    localStringBuilder.append("; enabled: ").append(isEnabled());
    localStringBuilder.append("; password: ").append(isPassword());
    localStringBuilder.append("; scrollable: " + isScrollable());
    localStringBuilder.append("; [");
    int i = getActions();
    while (i != 0)
    {
      int j = 1 << Integer.numberOfTrailingZeros(i);
      i &= (j ^ 0xFFFFFFFF);
      localStringBuilder.append(getActionSymbolicName(j));
      if (i != 0)
        localStringBuilder.append(", ");
    }
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }

  static class AccessibilityNodeInfoIcsImpl extends AccessibilityNodeInfoCompat.AccessibilityNodeInfoStubImpl
  {
    public void addAction(Object paramObject, int paramInt)
    {
      AccessibilityNodeInfoCompatIcs.addAction(paramObject, paramInt);
    }

    public int getActions(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.getActions(paramObject);
    }

    public void getBoundsInParent(Object paramObject, Rect paramRect)
    {
      AccessibilityNodeInfoCompatIcs.getBoundsInParent(paramObject, paramRect);
    }

    public void getBoundsInScreen(Object paramObject, Rect paramRect)
    {
      AccessibilityNodeInfoCompatIcs.getBoundsInScreen(paramObject, paramRect);
    }

    public CharSequence getClassName(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.getClassName(paramObject);
    }

    public CharSequence getContentDescription(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.getContentDescription(paramObject);
    }

    public CharSequence getPackageName(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.getPackageName(paramObject);
    }

    public CharSequence getText(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.getText(paramObject);
    }

    public boolean isCheckable(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.isCheckable(paramObject);
    }

    public boolean isChecked(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.isChecked(paramObject);
    }

    public boolean isClickable(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.isClickable(paramObject);
    }

    public boolean isEnabled(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.isEnabled(paramObject);
    }

    public boolean isFocusable(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.isFocusable(paramObject);
    }

    public boolean isFocused(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.isFocused(paramObject);
    }

    public boolean isLongClickable(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.isLongClickable(paramObject);
    }

    public boolean isPassword(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.isPassword(paramObject);
    }

    public boolean isScrollable(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.isScrollable(paramObject);
    }

    public boolean isSelected(Object paramObject)
    {
      return AccessibilityNodeInfoCompatIcs.isSelected(paramObject);
    }

    public void setClassName(Object paramObject, CharSequence paramCharSequence)
    {
      AccessibilityNodeInfoCompatIcs.setClassName(paramObject, paramCharSequence);
    }

    public void setScrollable(Object paramObject, boolean paramBoolean)
    {
      AccessibilityNodeInfoCompatIcs.setScrollable(paramObject, paramBoolean);
    }
  }

  static abstract interface AccessibilityNodeInfoImpl
  {
    public abstract void addAction(Object paramObject, int paramInt);

    public abstract int getActions(Object paramObject);

    public abstract void getBoundsInParent(Object paramObject, Rect paramRect);

    public abstract void getBoundsInScreen(Object paramObject, Rect paramRect);

    public abstract CharSequence getClassName(Object paramObject);

    public abstract CharSequence getContentDescription(Object paramObject);

    public abstract CharSequence getPackageName(Object paramObject);

    public abstract CharSequence getText(Object paramObject);

    public abstract String getViewIdResourceName(Object paramObject);

    public abstract boolean isCheckable(Object paramObject);

    public abstract boolean isChecked(Object paramObject);

    public abstract boolean isClickable(Object paramObject);

    public abstract boolean isEnabled(Object paramObject);

    public abstract boolean isFocusable(Object paramObject);

    public abstract boolean isFocused(Object paramObject);

    public abstract boolean isLongClickable(Object paramObject);

    public abstract boolean isPassword(Object paramObject);

    public abstract boolean isScrollable(Object paramObject);

    public abstract boolean isSelected(Object paramObject);

    public abstract void setClassName(Object paramObject, CharSequence paramCharSequence);

    public abstract void setScrollable(Object paramObject, boolean paramBoolean);
  }

  static class AccessibilityNodeInfoJellybeanImpl extends AccessibilityNodeInfoCompat.AccessibilityNodeInfoIcsImpl
  {
  }

  static class AccessibilityNodeInfoJellybeanMr2Impl extends AccessibilityNodeInfoCompat.AccessibilityNodeInfoJellybeanImpl
  {
    public String getViewIdResourceName(Object paramObject)
    {
      return AccessibilityNodeInfoCompatJellybeanMr2.getViewIdResourceName(paramObject);
    }
  }

  static class AccessibilityNodeInfoKitKatImpl extends AccessibilityNodeInfoCompat.AccessibilityNodeInfoJellybeanMr2Impl
  {
  }

  static class AccessibilityNodeInfoStubImpl
    implements AccessibilityNodeInfoCompat.AccessibilityNodeInfoImpl
  {
    public void addAction(Object paramObject, int paramInt)
    {
    }

    public int getActions(Object paramObject)
    {
      return 0;
    }

    public void getBoundsInParent(Object paramObject, Rect paramRect)
    {
    }

    public void getBoundsInScreen(Object paramObject, Rect paramRect)
    {
    }

    public CharSequence getClassName(Object paramObject)
    {
      return null;
    }

    public CharSequence getContentDescription(Object paramObject)
    {
      return null;
    }

    public CharSequence getPackageName(Object paramObject)
    {
      return null;
    }

    public CharSequence getText(Object paramObject)
    {
      return null;
    }

    public String getViewIdResourceName(Object paramObject)
    {
      return null;
    }

    public boolean isCheckable(Object paramObject)
    {
      return false;
    }

    public boolean isChecked(Object paramObject)
    {
      return false;
    }

    public boolean isClickable(Object paramObject)
    {
      return false;
    }

    public boolean isEnabled(Object paramObject)
    {
      return false;
    }

    public boolean isFocusable(Object paramObject)
    {
      return false;
    }

    public boolean isFocused(Object paramObject)
    {
      return false;
    }

    public boolean isLongClickable(Object paramObject)
    {
      return false;
    }

    public boolean isPassword(Object paramObject)
    {
      return false;
    }

    public boolean isScrollable(Object paramObject)
    {
      return false;
    }

    public boolean isSelected(Object paramObject)
    {
      return false;
    }

    public void setClassName(Object paramObject, CharSequence paramCharSequence)
    {
    }

    public void setScrollable(Object paramObject, boolean paramBoolean)
    {
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     android.support.v4.view.accessibility.AccessibilityNodeInfoCompat
 * JD-Core Version:    0.6.2
 */