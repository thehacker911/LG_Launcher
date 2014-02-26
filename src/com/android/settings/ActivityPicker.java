package com.android.settings;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ResolveInfo.DisplayNameComparator;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController.AlertParams;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivityPicker extends AlertActivity
  implements DialogInterface.OnCancelListener, DialogInterface.OnClickListener
{
  private PickAdapter mAdapter;
  private Intent mBaseIntent;

  protected Intent getIntentForPosition(int paramInt)
  {
    return ((ActivityPicker.PickAdapter.Item)this.mAdapter.getItem(paramInt)).getIntent(this.mBaseIntent);
  }

  protected List<ActivityPicker.PickAdapter.Item> getItems()
  {
    PackageManager localPackageManager = getPackageManager();
    ArrayList localArrayList1 = new ArrayList();
    Intent localIntent = getIntent();
    ArrayList localArrayList2 = localIntent.getStringArrayListExtra("android.intent.extra.shortcut.NAME");
    ArrayList localArrayList3 = localIntent.getParcelableArrayListExtra("android.intent.extra.shortcut.ICON_RESOURCE");
    int i;
    if ((localArrayList2 != null) && (localArrayList3 != null) && (localArrayList2.size() == localArrayList3.size()))
      i = 0;
    while (true)
    {
      String str;
      if (i < localArrayList2.size())
        str = (String)localArrayList2.get(i);
      try
      {
        Intent.ShortcutIconResource localShortcutIconResource = (Intent.ShortcutIconResource)localArrayList3.get(i);
        Resources localResources = localPackageManager.getResourcesForApplication(localShortcutIconResource.packageName);
        Drawable localDrawable2 = localResources.getDrawable(localResources.getIdentifier(localShortcutIconResource.resourceName, null, null));
        localDrawable1 = localDrawable2;
        localArrayList1.add(new ActivityPicker.PickAdapter.Item(this, str, localDrawable1));
        i++;
        continue;
        if (this.mBaseIntent != null)
          putIntentItems(this.mBaseIntent, localArrayList1);
        return localArrayList1;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        while (true)
          Drawable localDrawable1 = null;
      }
    }
  }

  public void onCancel(DialogInterface paramDialogInterface)
  {
    setResult(0);
    finish();
  }

  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    setResult(-1, getIntentForPosition(paramInt));
    finish();
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Intent localIntent = getIntent();
    Parcelable localParcelable = localIntent.getParcelableExtra("android.intent.extra.INTENT");
    AlertController.AlertParams localAlertParams;
    if ((localParcelable instanceof Intent))
    {
      this.mBaseIntent = ((Intent)localParcelable);
      localAlertParams = this.mAlertParams;
      localAlertParams.mOnClickListener = this;
      localAlertParams.mOnCancelListener = this;
      if (!localIntent.hasExtra("android.intent.extra.TITLE"))
        break label127;
    }
    label127: for (localAlertParams.mTitle = localIntent.getStringExtra("android.intent.extra.TITLE"); ; localAlertParams.mTitle = getTitle())
    {
      this.mAdapter = new PickAdapter(this, getItems());
      localAlertParams.mAdapter = this.mAdapter;
      setupAlert();
      return;
      this.mBaseIntent = new Intent("android.intent.action.MAIN", null);
      this.mBaseIntent.addCategory("android.intent.category.DEFAULT");
      break;
    }
  }

  protected void putIntentItems(Intent paramIntent, List<ActivityPicker.PickAdapter.Item> paramList)
  {
    PackageManager localPackageManager = getPackageManager();
    List localList = localPackageManager.queryIntentActivities(paramIntent, 0);
    Collections.sort(localList, new ResolveInfo.DisplayNameComparator(localPackageManager));
    int i = localList.size();
    for (int j = 0; j < i; j++)
      paramList.add(new ActivityPicker.PickAdapter.Item(this, localPackageManager, (ResolveInfo)localList.get(j)));
  }

  private static class EmptyDrawable extends Drawable
  {
    private final int mHeight;
    private final int mWidth;

    EmptyDrawable(int paramInt1, int paramInt2)
    {
      this.mWidth = paramInt1;
      this.mHeight = paramInt2;
    }

    public void draw(Canvas paramCanvas)
    {
    }

    public int getIntrinsicHeight()
    {
      return this.mHeight;
    }

    public int getIntrinsicWidth()
    {
      return this.mWidth;
    }

    public int getMinimumHeight()
    {
      return this.mHeight;
    }

    public int getMinimumWidth()
    {
      return this.mWidth;
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

  private static class IconResizer
  {
    private final Canvas mCanvas = new Canvas();
    private final int mIconHeight;
    private final int mIconWidth;
    private final DisplayMetrics mMetrics;
    private final Rect mOldBounds = new Rect();

    public IconResizer(int paramInt1, int paramInt2, DisplayMetrics paramDisplayMetrics)
    {
      this.mCanvas.setDrawFilter(new PaintFlagsDrawFilter(4, 2));
      this.mMetrics = paramDisplayMetrics;
      this.mIconWidth = paramInt1;
      this.mIconHeight = paramInt2;
    }

    // ERROR //
    public Drawable createIconThumbnail(Drawable paramDrawable)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 41	com/android/settings/ActivityPicker$IconResizer:mIconWidth	I
      //   4: istore_2
      //   5: aload_0
      //   6: getfield 43	com/android/settings/ActivityPicker$IconResizer:mIconHeight	I
      //   9: istore_3
      //   10: aload_1
      //   11: ifnonnull +17 -> 28
      //   14: new 49	com/android/settings/ActivityPicker$EmptyDrawable
      //   17: dup
      //   18: iload_2
      //   19: iload_3
      //   20: invokespecial 50	com/android/settings/ActivityPicker$EmptyDrawable:<init>	(II)V
      //   23: astore 4
      //   25: aload 4
      //   27: areturn
      //   28: aload_1
      //   29: instanceof 52
      //   32: ifeq +226 -> 258
      //   35: aload_1
      //   36: checkcast 52	android/graphics/drawable/PaintDrawable
      //   39: astore 29
      //   41: aload 29
      //   43: iload_2
      //   44: invokevirtual 58	android/graphics/drawable/ShapeDrawable:setIntrinsicWidth	(I)V
      //   47: aload 29
      //   49: iload_3
      //   50: invokevirtual 61	android/graphics/drawable/ShapeDrawable:setIntrinsicHeight	(I)V
      //   53: aload_1
      //   54: invokevirtual 67	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
      //   57: istore 7
      //   59: aload_1
      //   60: invokevirtual 70	android/graphics/drawable/Drawable:getIntrinsicHeight	()I
      //   63: istore 8
      //   65: iload 7
      //   67: ifle +189 -> 256
      //   70: iload 8
      //   72: ifle +184 -> 256
      //   75: iload_2
      //   76: iload 7
      //   78: if_icmplt +9 -> 87
      //   81: iload_3
      //   82: iload 8
      //   84: if_icmpge +250 -> 334
      //   87: iload 7
      //   89: i2f
      //   90: iload 8
      //   92: i2f
      //   93: fdiv
      //   94: fstore 9
      //   96: iload 7
      //   98: iload 8
      //   100: if_icmple +209 -> 309
      //   103: iload_2
      //   104: i2f
      //   105: fload 9
      //   107: fdiv
      //   108: f2i
      //   109: istore_3
      //   110: aload_1
      //   111: invokevirtual 73	android/graphics/drawable/Drawable:getOpacity	()I
      //   114: iconst_m1
      //   115: if_icmpeq +211 -> 326
      //   118: getstatic 79	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   121: astore 10
      //   123: aload_0
      //   124: getfield 41	com/android/settings/ActivityPicker$IconResizer:mIconWidth	I
      //   127: aload_0
      //   128: getfield 43	com/android/settings/ActivityPicker$IconResizer:mIconHeight	I
      //   131: aload 10
      //   133: invokestatic 85	android/graphics/Bitmap:createBitmap	(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
      //   136: astore 11
      //   138: aload_0
      //   139: getfield 28	com/android/settings/ActivityPicker$IconResizer:mCanvas	Landroid/graphics/Canvas;
      //   142: astore 12
      //   144: aload 12
      //   146: aload 11
      //   148: invokevirtual 89	android/graphics/Canvas:setBitmap	(Landroid/graphics/Bitmap;)V
      //   151: aload_0
      //   152: getfield 23	com/android/settings/ActivityPicker$IconResizer:mOldBounds	Landroid/graphics/Rect;
      //   155: aload_1
      //   156: invokevirtual 93	android/graphics/drawable/Drawable:getBounds	()Landroid/graphics/Rect;
      //   159: invokevirtual 97	android/graphics/Rect:set	(Landroid/graphics/Rect;)V
      //   162: aload_0
      //   163: getfield 41	com/android/settings/ActivityPicker$IconResizer:mIconWidth	I
      //   166: iload_2
      //   167: isub
      //   168: iconst_2
      //   169: idiv
      //   170: istore 13
      //   172: aload_0
      //   173: getfield 43	com/android/settings/ActivityPicker$IconResizer:mIconHeight	I
      //   176: iload_3
      //   177: isub
      //   178: iconst_2
      //   179: idiv
      //   180: istore 14
      //   182: iload 13
      //   184: iload_2
      //   185: iadd
      //   186: istore 15
      //   188: iload 14
      //   190: iload_3
      //   191: iadd
      //   192: istore 16
      //   194: aload_1
      //   195: iload 13
      //   197: iload 14
      //   199: iload 15
      //   201: iload 16
      //   203: invokevirtual 101	android/graphics/drawable/Drawable:setBounds	(IIII)V
      //   206: aload_1
      //   207: aload 12
      //   209: invokevirtual 105	android/graphics/drawable/Drawable:draw	(Landroid/graphics/Canvas;)V
      //   212: aload_0
      //   213: getfield 23	com/android/settings/ActivityPicker$IconResizer:mOldBounds	Landroid/graphics/Rect;
      //   216: astore 17
      //   218: aload_1
      //   219: aload 17
      //   221: invokevirtual 107	android/graphics/drawable/Drawable:setBounds	(Landroid/graphics/Rect;)V
      //   224: new 109	android/graphics/drawable/BitmapDrawable
      //   227: dup
      //   228: aload 11
      //   230: invokespecial 111	android/graphics/drawable/BitmapDrawable:<init>	(Landroid/graphics/Bitmap;)V
      //   233: astore 18
      //   235: aload 18
      //   237: checkcast 109	android/graphics/drawable/BitmapDrawable
      //   240: aload_0
      //   241: getfield 39	com/android/settings/ActivityPicker$IconResizer:mMetrics	Landroid/util/DisplayMetrics;
      //   244: invokevirtual 115	android/graphics/drawable/BitmapDrawable:setTargetDensity	(Landroid/util/DisplayMetrics;)V
      //   247: aload 12
      //   249: aconst_null
      //   250: invokevirtual 89	android/graphics/Canvas:setBitmap	(Landroid/graphics/Bitmap;)V
      //   253: aload 18
      //   255: astore_1
      //   256: aload_1
      //   257: areturn
      //   258: aload_1
      //   259: instanceof 109
      //   262: ifeq -209 -> 53
      //   265: aload_1
      //   266: checkcast 109	android/graphics/drawable/BitmapDrawable
      //   269: astore 6
      //   271: aload 6
      //   273: invokevirtual 119	android/graphics/drawable/BitmapDrawable:getBitmap	()Landroid/graphics/Bitmap;
      //   276: invokevirtual 122	android/graphics/Bitmap:getDensity	()I
      //   279: ifne -226 -> 53
      //   282: aload 6
      //   284: aload_0
      //   285: getfield 39	com/android/settings/ActivityPicker$IconResizer:mMetrics	Landroid/util/DisplayMetrics;
      //   288: invokevirtual 115	android/graphics/drawable/BitmapDrawable:setTargetDensity	(Landroid/util/DisplayMetrics;)V
      //   291: goto -238 -> 53
      //   294: astore 5
      //   296: new 49	com/android/settings/ActivityPicker$EmptyDrawable
      //   299: dup
      //   300: iload_2
      //   301: iload_3
      //   302: invokespecial 50	com/android/settings/ActivityPicker$EmptyDrawable:<init>	(II)V
      //   305: astore_1
      //   306: goto -50 -> 256
      //   309: iload 8
      //   311: iload 7
      //   313: if_icmple -203 -> 110
      //   316: fload 9
      //   318: iload_3
      //   319: i2f
      //   320: fmul
      //   321: f2i
      //   322: istore_2
      //   323: goto -213 -> 110
      //   326: getstatic 125	android/graphics/Bitmap$Config:RGB_565	Landroid/graphics/Bitmap$Config;
      //   329: astore 10
      //   331: goto -208 -> 123
      //   334: iload 7
      //   336: iload_2
      //   337: if_icmpge -81 -> 256
      //   340: iload 8
      //   342: iload_3
      //   343: if_icmpge -87 -> 256
      //   346: getstatic 79	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   349: astore 21
      //   351: aload_0
      //   352: getfield 41	com/android/settings/ActivityPicker$IconResizer:mIconWidth	I
      //   355: aload_0
      //   356: getfield 43	com/android/settings/ActivityPicker$IconResizer:mIconHeight	I
      //   359: aload 21
      //   361: invokestatic 85	android/graphics/Bitmap:createBitmap	(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
      //   364: astore 22
      //   366: aload_0
      //   367: getfield 28	com/android/settings/ActivityPicker$IconResizer:mCanvas	Landroid/graphics/Canvas;
      //   370: astore 23
      //   372: aload 23
      //   374: aload 22
      //   376: invokevirtual 89	android/graphics/Canvas:setBitmap	(Landroid/graphics/Bitmap;)V
      //   379: aload_0
      //   380: getfield 23	com/android/settings/ActivityPicker$IconResizer:mOldBounds	Landroid/graphics/Rect;
      //   383: aload_1
      //   384: invokevirtual 93	android/graphics/drawable/Drawable:getBounds	()Landroid/graphics/Rect;
      //   387: invokevirtual 97	android/graphics/Rect:set	(Landroid/graphics/Rect;)V
      //   390: iload_2
      //   391: iload 7
      //   393: isub
      //   394: iconst_2
      //   395: idiv
      //   396: istore 24
      //   398: iload_3
      //   399: iload 8
      //   401: isub
      //   402: iconst_2
      //   403: idiv
      //   404: istore 25
      //   406: iload 24
      //   408: iload 7
      //   410: iadd
      //   411: istore 26
      //   413: iload 25
      //   415: iload 8
      //   417: iadd
      //   418: istore 27
      //   420: aload_1
      //   421: iload 24
      //   423: iload 25
      //   425: iload 26
      //   427: iload 27
      //   429: invokevirtual 101	android/graphics/drawable/Drawable:setBounds	(IIII)V
      //   432: aload_1
      //   433: aload 23
      //   435: invokevirtual 105	android/graphics/drawable/Drawable:draw	(Landroid/graphics/Canvas;)V
      //   438: aload_0
      //   439: getfield 23	com/android/settings/ActivityPicker$IconResizer:mOldBounds	Landroid/graphics/Rect;
      //   442: astore 28
      //   444: aload_1
      //   445: aload 28
      //   447: invokevirtual 107	android/graphics/drawable/Drawable:setBounds	(Landroid/graphics/Rect;)V
      //   450: new 109	android/graphics/drawable/BitmapDrawable
      //   453: dup
      //   454: aload 22
      //   456: invokespecial 111	android/graphics/drawable/BitmapDrawable:<init>	(Landroid/graphics/Bitmap;)V
      //   459: astore 18
      //   461: aload 18
      //   463: checkcast 109	android/graphics/drawable/BitmapDrawable
      //   466: aload_0
      //   467: getfield 39	com/android/settings/ActivityPicker$IconResizer:mMetrics	Landroid/util/DisplayMetrics;
      //   470: invokevirtual 115	android/graphics/drawable/BitmapDrawable:setTargetDensity	(Landroid/util/DisplayMetrics;)V
      //   473: aload 23
      //   475: aconst_null
      //   476: invokevirtual 89	android/graphics/Canvas:setBitmap	(Landroid/graphics/Bitmap;)V
      //   479: aload 18
      //   481: astore_1
      //   482: goto -226 -> 256
      //   485: astore 19
      //   487: aload 18
      //   489: pop
      //   490: goto -194 -> 296
      //
      // Exception table:
      //   from	to	target	type
      //   28	53	294	java/lang/Throwable
      //   53	65	294	java/lang/Throwable
      //   87	96	294	java/lang/Throwable
      //   103	110	294	java/lang/Throwable
      //   110	123	294	java/lang/Throwable
      //   123	182	294	java/lang/Throwable
      //   194	235	294	java/lang/Throwable
      //   258	291	294	java/lang/Throwable
      //   326	331	294	java/lang/Throwable
      //   346	406	294	java/lang/Throwable
      //   420	461	294	java/lang/Throwable
      //   235	253	485	java/lang/Throwable
      //   461	479	485	java/lang/Throwable
    }
  }

  protected static class PickAdapter extends BaseAdapter
  {
    private final LayoutInflater mInflater;
    private final List<Item> mItems;

    public PickAdapter(Context paramContext, List<Item> paramList)
    {
      this.mInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
      this.mItems = paramList;
    }

    public int getCount()
    {
      return this.mItems.size();
    }

    public Object getItem(int paramInt)
    {
      return this.mItems.get(paramInt);
    }

    public long getItemId(int paramInt)
    {
      return paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView == null)
        paramView = this.mInflater.inflate(2130968659, paramViewGroup, false);
      Item localItem = (Item)getItem(paramInt);
      TextView localTextView = (TextView)paramView;
      localTextView.setText(localItem.label);
      localTextView.setCompoundDrawablesWithIntrinsicBounds(localItem.icon, null, null, null);
      return paramView;
    }

    public static class Item
      implements AppWidgetLoader.LabelledItem
    {
      protected static ActivityPicker.IconResizer sResizer;
      String className;
      Bundle extras;
      Drawable icon;
      CharSequence label;
      String packageName;

      Item(Context paramContext, PackageManager paramPackageManager, ResolveInfo paramResolveInfo)
      {
        this.label = paramResolveInfo.loadLabel(paramPackageManager);
        if ((this.label == null) && (paramResolveInfo.activityInfo != null))
          this.label = paramResolveInfo.activityInfo.name;
        this.icon = getResizer(paramContext).createIconThumbnail(paramResolveInfo.loadIcon(paramPackageManager));
        this.packageName = paramResolveInfo.activityInfo.applicationInfo.packageName;
        this.className = paramResolveInfo.activityInfo.name;
      }

      Item(Context paramContext, CharSequence paramCharSequence, Drawable paramDrawable)
      {
        this.label = paramCharSequence;
        this.icon = getResizer(paramContext).createIconThumbnail(paramDrawable);
      }

      Intent getIntent(Intent paramIntent)
      {
        Intent localIntent = new Intent(paramIntent);
        if ((this.packageName != null) && (this.className != null))
        {
          localIntent.setClassName(this.packageName, this.className);
          if (this.extras != null)
            localIntent.putExtras(this.extras);
          return localIntent;
        }
        localIntent.setAction("android.intent.action.CREATE_SHORTCUT");
        localIntent.putExtra("android.intent.extra.shortcut.NAME", this.label);
        return localIntent;
      }

      public CharSequence getLabel()
      {
        return this.label;
      }

      protected ActivityPicker.IconResizer getResizer(Context paramContext)
      {
        if (sResizer == null)
        {
          Resources localResources = paramContext.getResources();
          int i = (int)localResources.getDimension(17104896);
          sResizer = new ActivityPicker.IconResizer(i, i, localResources.getDisplayMetrics());
        }
        return sResizer;
      }
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.ActivityPicker
 * JD-Core Version:    0.6.2
 */