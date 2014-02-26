package com.android.settings.widget;

import android.content.Context;
import android.graphics.drawable.AnimatedRotateDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class AnimatedImageView extends ImageView
{
  private boolean mAnimating;
  private AnimatedRotateDrawable mDrawable;

  public AnimatedImageView(Context paramContext)
  {
    super(paramContext);
  }

  public AnimatedImageView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  private void updateAnimating()
  {
    if (this.mDrawable != null)
    {
      if ((isShown()) && (this.mAnimating))
        this.mDrawable.start();
    }
    else
      return;
    this.mDrawable.stop();
  }

  private void updateDrawable()
  {
    if ((isShown()) && (this.mDrawable != null))
      this.mDrawable.stop();
    Drawable localDrawable = getDrawable();
    if ((localDrawable instanceof AnimatedRotateDrawable))
    {
      this.mDrawable = ((AnimatedRotateDrawable)localDrawable);
      this.mDrawable.setFramesCount(56);
      this.mDrawable.setFramesDuration(32);
      if ((isShown()) && (this.mAnimating))
        this.mDrawable.start();
      return;
    }
    this.mDrawable = null;
  }

  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    updateAnimating();
  }

  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    updateAnimating();
  }

  protected void onVisibilityChanged(View paramView, int paramInt)
  {
    super.onVisibilityChanged(paramView, paramInt);
    updateAnimating();
  }

  public void setAnimating(boolean paramBoolean)
  {
    this.mAnimating = paramBoolean;
    updateAnimating();
  }

  public void setImageDrawable(Drawable paramDrawable)
  {
    super.setImageDrawable(paramDrawable);
    updateDrawable();
  }

  public void setImageResource(int paramInt)
  {
    super.setImageResource(paramInt);
    updateDrawable();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.widget.AnimatedImageView
 * JD-Core Version:    0.6.2
 */