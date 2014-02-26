package com.android.settings.drawable;

import android.graphics.drawable.Drawable;

public class InsetBoundsDrawable extends DrawableWrapper
{
  private final int mInsetBoundsSides;

  public InsetBoundsDrawable(Drawable paramDrawable, int paramInt)
  {
    super(paramDrawable);
    this.mInsetBoundsSides = paramInt;
  }

  public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.setBounds(paramInt1 + this.mInsetBoundsSides, paramInt2, paramInt3 - this.mInsetBoundsSides, paramInt4);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.drawable.InsetBoundsDrawable
 * JD-Core Version:    0.6.2
 */