package com.android.settings.deviceinfo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.preference.Preference;

public class StorageItemPreference extends Preference
{
  public final int color;
  public final int userHandle;

  public StorageItemPreference(Context paramContext, int paramInt1, int paramInt2)
  {
    this(paramContext, paramContext.getText(paramInt1), paramInt2, -10000);
  }

  public StorageItemPreference(Context paramContext, CharSequence paramCharSequence, int paramInt1, int paramInt2)
  {
    super(paramContext);
    if (paramInt1 != 0)
    {
      this.color = paramContext.getResources().getColor(paramInt1);
      Resources localResources = paramContext.getResources();
      setIcon(createRectShape(localResources.getDimensionPixelSize(2131558400), localResources.getDimensionPixelSize(2131558401), this.color));
    }
    while (true)
    {
      setTitle(paramCharSequence);
      setSummary(2131428132);
      this.userHandle = paramInt2;
      return;
      this.color = -65281;
    }
  }

  private static ShapeDrawable createRectShape(int paramInt1, int paramInt2, int paramInt3)
  {
    ShapeDrawable localShapeDrawable = new ShapeDrawable(new RectShape());
    localShapeDrawable.setIntrinsicHeight(paramInt2);
    localShapeDrawable.setIntrinsicWidth(paramInt1);
    localShapeDrawable.getPaint().setColor(paramInt3);
    return localShapeDrawable;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.deviceinfo.StorageItemPreference
 * JD-Core Version:    0.6.2
 */