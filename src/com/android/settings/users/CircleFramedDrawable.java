package com.android.settings.users;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

class CircleFramedDrawable extends Drawable
{
  private final Bitmap mBitmap;
  private RectF mDstRect;
  private final int mFrameColor;
  private Path mFramePath;
  private RectF mFrameRect;
  private final int mFrameShadowColor;
  private final int mHighlightColor;
  private final Paint mPaint;
  private boolean mPressed;
  private float mScale;
  private final float mShadowRadius;
  private final int mSize;
  private Rect mSrcRect;
  private final float mStrokeWidth;

  public CircleFramedDrawable(Bitmap paramBitmap, int paramInt1, int paramInt2, float paramFloat1, int paramInt3, float paramFloat2, int paramInt4)
  {
    this.mSize = paramInt1;
    this.mShadowRadius = paramFloat2;
    this.mFrameColor = paramInt2;
    this.mFrameShadowColor = paramInt3;
    this.mStrokeWidth = paramFloat1;
    this.mHighlightColor = paramInt4;
    this.mBitmap = Bitmap.createBitmap(this.mSize, this.mSize, Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas(this.mBitmap);
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    int k = Math.min(i, j);
    Rect localRect = new Rect((i - k) / 2, (j - k) / 2, k, k);
    RectF localRectF = new RectF(0.0F, 0.0F, this.mSize, this.mSize);
    localRectF.inset(this.mStrokeWidth / 2.0F, this.mStrokeWidth / 2.0F);
    localRectF.inset(this.mShadowRadius, this.mShadowRadius);
    Path localPath = new Path();
    localPath.addArc(localRectF, 0.0F, 360.0F);
    localCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
    this.mPaint = new Paint();
    this.mPaint.setAntiAlias(true);
    this.mPaint.setColor(-16777216);
    this.mPaint.setStyle(Paint.Style.FILL);
    localCanvas.drawPath(localPath, this.mPaint);
    this.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
    localCanvas.drawBitmap(paramBitmap, localRect, localRectF, this.mPaint);
    this.mPaint.setXfermode(null);
    this.mScale = 1.0F;
    this.mSrcRect = new Rect(0, 0, this.mSize, this.mSize);
    this.mDstRect = new RectF(0.0F, 0.0F, this.mSize, this.mSize);
    this.mFrameRect = new RectF(this.mDstRect);
    this.mFramePath = new Path();
  }

  public static CircleFramedDrawable getInstance(Context paramContext, Bitmap paramBitmap)
  {
    Resources localResources = paramContext.getResources();
    float f1 = localResources.getDimension(2131558435);
    float f2 = localResources.getDimension(2131558436);
    float f3 = localResources.getDimension(2131558437);
    int i = localResources.getColor(2131361811);
    int j = localResources.getColor(2131361812);
    int k = localResources.getColor(2131361813);
    return new CircleFramedDrawable(paramBitmap, (int)f1, i, f2, j, f3, k);
  }

  public void draw(Canvas paramCanvas)
  {
    float f1 = this.mScale * this.mSize;
    float f2 = (this.mSize - f1) / 2.0F;
    this.mDstRect.set(f2, f2, this.mSize - f2, this.mSize - f2);
    paramCanvas.drawBitmap(this.mBitmap, this.mSrcRect, this.mDstRect, null);
    this.mFrameRect.set(this.mDstRect);
    this.mFrameRect.inset(this.mStrokeWidth / 2.0F, this.mStrokeWidth / 2.0F);
    this.mFrameRect.inset(this.mShadowRadius, this.mShadowRadius);
    this.mFramePath.reset();
    this.mFramePath.addArc(this.mFrameRect, 0.0F, 360.0F);
    if (this.mPressed)
    {
      this.mPaint.setStyle(Paint.Style.FILL);
      this.mPaint.setColor(Color.argb(84, Color.red(this.mHighlightColor), Color.green(this.mHighlightColor), Color.blue(this.mHighlightColor)));
      paramCanvas.drawPath(this.mFramePath, this.mPaint);
    }
    this.mPaint.setStrokeWidth(this.mStrokeWidth);
    this.mPaint.setStyle(Paint.Style.STROKE);
    Paint localPaint = this.mPaint;
    if (this.mPressed);
    for (int i = this.mHighlightColor; ; i = this.mFrameColor)
    {
      localPaint.setColor(i);
      this.mPaint.setShadowLayer(this.mShadowRadius, 0.0F, 0.0F, this.mFrameShadowColor);
      paramCanvas.drawPath(this.mFramePath, this.mPaint);
      return;
    }
  }

  public int getIntrinsicHeight()
  {
    return this.mSize;
  }

  public int getIntrinsicWidth()
  {
    return this.mSize;
  }

  public int getOpacity()
  {
    return -3;
  }

  public void setAlpha(int paramInt)
  {
  }

  public void setColorFilter(ColorFilter paramColorFilter)
  {
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.users.CircleFramedDrawable
 * JD-Core Version:    0.6.2
 */