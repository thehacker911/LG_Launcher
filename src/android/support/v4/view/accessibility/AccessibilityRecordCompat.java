package android.support.v4.view.accessibility;

import android.os.Build.VERSION;

public class AccessibilityRecordCompat
{
  private static final AccessibilityRecordImpl IMPL = new AccessibilityRecordStubImpl();
  private final Object mRecord;

  static
  {
    if (Build.VERSION.SDK_INT >= 16)
    {
      IMPL = new AccessibilityRecordJellyBeanImpl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 15)
    {
      IMPL = new AccessibilityRecordIcsMr1Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 14)
    {
      IMPL = new AccessibilityRecordIcsImpl();
      return;
    }
  }

  public AccessibilityRecordCompat(Object paramObject)
  {
    this.mRecord = paramObject;
  }

  public static AccessibilityRecordCompat obtain()
  {
    return new AccessibilityRecordCompat(IMPL.obtain());
  }

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    AccessibilityRecordCompat localAccessibilityRecordCompat;
    do
    {
      do
      {
        return true;
        if (paramObject == null)
          return false;
        if (getClass() != paramObject.getClass())
          return false;
        localAccessibilityRecordCompat = (AccessibilityRecordCompat)paramObject;
        if (this.mRecord != null)
          break;
      }
      while (localAccessibilityRecordCompat.mRecord == null);
      return false;
    }
    while (this.mRecord.equals(localAccessibilityRecordCompat.mRecord));
    return false;
  }

  public int hashCode()
  {
    if (this.mRecord == null)
      return 0;
    return this.mRecord.hashCode();
  }

  public void setFromIndex(int paramInt)
  {
    IMPL.setFromIndex(this.mRecord, paramInt);
  }

  public void setItemCount(int paramInt)
  {
    IMPL.setItemCount(this.mRecord, paramInt);
  }

  public void setScrollable(boolean paramBoolean)
  {
    IMPL.setScrollable(this.mRecord, paramBoolean);
  }

  public void setToIndex(int paramInt)
  {
    IMPL.setToIndex(this.mRecord, paramInt);
  }

  static class AccessibilityRecordIcsImpl extends AccessibilityRecordCompat.AccessibilityRecordStubImpl
  {
    public Object obtain()
    {
      return AccessibilityRecordCompatIcs.obtain();
    }

    public void setFromIndex(Object paramObject, int paramInt)
    {
      AccessibilityRecordCompatIcs.setFromIndex(paramObject, paramInt);
    }

    public void setItemCount(Object paramObject, int paramInt)
    {
      AccessibilityRecordCompatIcs.setItemCount(paramObject, paramInt);
    }

    public void setScrollable(Object paramObject, boolean paramBoolean)
    {
      AccessibilityRecordCompatIcs.setScrollable(paramObject, paramBoolean);
    }

    public void setToIndex(Object paramObject, int paramInt)
    {
      AccessibilityRecordCompatIcs.setToIndex(paramObject, paramInt);
    }
  }

  static class AccessibilityRecordIcsMr1Impl extends AccessibilityRecordCompat.AccessibilityRecordIcsImpl
  {
  }

  static abstract interface AccessibilityRecordImpl
  {
    public abstract Object obtain();

    public abstract void setFromIndex(Object paramObject, int paramInt);

    public abstract void setItemCount(Object paramObject, int paramInt);

    public abstract void setScrollable(Object paramObject, boolean paramBoolean);

    public abstract void setToIndex(Object paramObject, int paramInt);
  }

  static class AccessibilityRecordJellyBeanImpl extends AccessibilityRecordCompat.AccessibilityRecordIcsMr1Impl
  {
  }

  static class AccessibilityRecordStubImpl
    implements AccessibilityRecordCompat.AccessibilityRecordImpl
  {
    public Object obtain()
    {
      return null;
    }

    public void setFromIndex(Object paramObject, int paramInt)
    {
    }

    public void setItemCount(Object paramObject, int paramInt)
    {
    }

    public void setScrollable(Object paramObject, boolean paramBoolean)
    {
    }

    public void setToIndex(Object paramObject, int paramInt)
    {
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     android.support.v4.view.accessibility.AccessibilityRecordCompat
 * JD-Core Version:    0.6.2
 */