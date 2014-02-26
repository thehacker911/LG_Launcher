package com.android.settings;

import android.app.Activity;
import android.app.ActivityManager;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.IWindowManager;
import android.view.IWindowManager.Stub;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.internal.widget.LockPatternUtils;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.List;

public class KeyguardAppWidgetPickActivity extends Activity
  implements AdapterView.OnItemClickListener, AppWidgetLoader.ItemConstructor<Item>
{
  private boolean mAddingToKeyguard = true;
  private AppWidgetAdapter mAppWidgetAdapter;
  private int mAppWidgetId;
  private AppWidgetLoader<Item> mAppWidgetLoader;
  private AppWidgetManager mAppWidgetManager;
  private Bundle mExtraConfigureOptions;
  private GridView mGridView;
  private List<Item> mItems;
  private LockPatternUtils mLockPatternUtils;
  private Intent mResultData;

  private void finishDelayedAndShowLockScreen(int paramInt)
  {
    IWindowManager localIWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
    Bundle localBundle = null;
    if (paramInt != 0)
    {
      localBundle = new Bundle();
      localBundle.putInt("showappwidget", paramInt);
    }
    try
    {
      localIWindowManager.lockNow(localBundle);
      label37: ViewGroup localViewGroup = (ViewGroup)findViewById(2131230813);
      localViewGroup.setBackgroundColor(-16777216);
      int i = localViewGroup.getChildCount();
      for (int j = 0; j < i; j++)
        localViewGroup.getChildAt(j).setVisibility(4);
      this.mGridView.postDelayed(new Runnable()
      {
        public void run()
        {
          KeyguardAppWidgetPickActivity.this.finish();
        }
      }
      , 500L);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      break label37;
    }
  }

  public Item createItem(Context paramContext, AppWidgetProviderInfo paramAppWidgetProviderInfo, Bundle paramBundle)
  {
    Item localItem = new Item(paramContext, paramAppWidgetProviderInfo.label);
    localItem.appWidgetPreviewId = paramAppWidgetProviderInfo.previewImage;
    localItem.iconId = paramAppWidgetProviderInfo.icon;
    localItem.packageName = paramAppWidgetProviderInfo.provider.getPackageName();
    localItem.className = paramAppWidgetProviderInfo.provider.getClassName();
    localItem.extras = paramBundle;
    return localItem;
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    int i;
    if ((paramInt1 == 126) || (paramInt1 == 127))
    {
      if (paramIntent != null)
        break label105;
      i = 0;
    }
    while ((paramInt1 == 126) && (paramInt2 == -1))
    {
      AppWidgetProviderInfo localAppWidgetProviderInfo = AppWidgetManager.getInstance(this).getAppWidgetInfo(i);
      if (localAppWidgetProviderInfo.configure != null)
      {
        Intent localIntent = new Intent("android.appwidget.action.APPWIDGET_CONFIGURE");
        localIntent.setComponent(localAppWidgetProviderInfo.configure);
        localIntent.addFlags(8388608);
        localIntent.putExtra("appWidgetId", i);
        startActivityForResultSafely(localIntent, 127);
        return;
        label105: i = paramIntent.getIntExtra("appWidgetId", 0);
      }
      else
      {
        onActivityResult(127, -1, paramIntent);
        return;
      }
    }
    if ((paramInt1 == 127) && (paramInt2 == -1))
    {
      this.mLockPatternUtils.addAppWidget(i, 0);
      finishDelayedAndShowLockScreen(i);
      return;
    }
    if ((this.mAddingToKeyguard) && (this.mAppWidgetId != 0))
    {
      int j = ActivityManager.getCurrentUser();
      AppWidgetHost.deleteAppWidgetIdForSystem(this.mAppWidgetId, j);
    }
    finishDelayedAndShowLockScreen(0);
  }

  protected void onCreate(Bundle paramBundle)
  {
    getWindow().addPrivateFlags(512);
    setContentView(2130968639);
    super.onCreate(paramBundle);
    setResultData(0, null);
    Intent localIntent = getIntent();
    if (localIntent.hasExtra("appWidgetId"))
      this.mAppWidgetId = localIntent.getIntExtra("appWidgetId", 0);
    while (true)
    {
      this.mExtraConfigureOptions = localIntent.getBundleExtra("appWidgetOptions");
      this.mGridView = ((GridView)findViewById(2131230893));
      DisplayMetrics localDisplayMetrics = new DisplayMetrics();
      getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
      int i = getResources().getDimensionPixelSize(2131558432);
      if (i < localDisplayMetrics.widthPixels)
        this.mGridView.getLayoutParams().width = i;
      this.mAppWidgetManager = AppWidgetManager.getInstance(this);
      this.mAppWidgetLoader = new AppWidgetLoader(this, this.mAppWidgetManager, this);
      this.mItems = this.mAppWidgetLoader.getItems(getIntent());
      this.mAppWidgetAdapter = new AppWidgetAdapter(this, this.mItems);
      this.mGridView.setAdapter(this.mAppWidgetAdapter);
      this.mGridView.setOnItemClickListener(this);
      this.mLockPatternUtils = new LockPatternUtils(this);
      return;
      finish();
    }
  }

  protected void onDestroy()
  {
    if (this.mAppWidgetAdapter != null)
      this.mAppWidgetAdapter.cancelAllWidgetPreviewLoaders();
    super.onDestroy();
  }

  public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    Item localItem = (Item)this.mItems.get(paramInt);
    Intent localIntent = localItem.getIntent();
    int i;
    if (localItem.extras != null)
    {
      i = -1;
      setResultData(i, localIntent);
    }
    while (true)
      if (this.mAddingToKeyguard)
      {
        onActivityResult(126, i, this.mResultData);
        return;
        try
        {
          if ((this.mAddingToKeyguard) && (this.mAppWidgetId == 0))
            this.mAppWidgetId = AppWidgetHost.allocateAppWidgetIdForPackage(1262836039, ActivityManager.getCurrentUser(), "com.android.keyguard");
          this.mAppWidgetManager.bindAppWidgetId(this.mAppWidgetId, localIntent.getComponent(), this.mExtraConfigureOptions);
          i = -1;
          setResultData(i, null);
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          while (true)
            i = 0;
        }
      }
    finish();
  }

  void setResultData(int paramInt, Intent paramIntent)
  {
    if (paramIntent != null);
    for (Intent localIntent = paramIntent; ; localIntent = new Intent())
    {
      localIntent.putExtra("appWidgetId", this.mAppWidgetId);
      this.mResultData = localIntent;
      setResult(paramInt, localIntent);
      return;
    }
  }

  void startActivityForResultSafely(Intent paramIntent, int paramInt)
  {
    try
    {
      startActivityForResult(paramIntent, paramInt);
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Toast.makeText(this, 2131428352, 0).show();
      return;
    }
    catch (SecurityException localSecurityException)
    {
      Toast.makeText(this, 2131428352, 0).show();
      Log.e("KeyguardAppWidgetPickActivity", "Settings does not have the permission to launch " + paramIntent, localSecurityException);
    }
  }

  protected static class AppWidgetAdapter extends BaseAdapter
  {
    private final LayoutInflater mInflater;
    private final List<KeyguardAppWidgetPickActivity.Item> mItems;

    public AppWidgetAdapter(Context paramContext, List<KeyguardAppWidgetPickActivity.Item> paramList)
    {
      this.mInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
      this.mItems = paramList;
    }

    public void cancelAllWidgetPreviewLoaders()
    {
      for (int i = 0; i < this.mItems.size(); i++)
        ((KeyguardAppWidgetPickActivity.Item)this.mItems.get(i)).cancelLoadingWidgetPreview();
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
        paramView = this.mInflater.inflate(2130968638, paramViewGroup, false);
      KeyguardAppWidgetPickActivity.Item localItem = (KeyguardAppWidgetPickActivity.Item)getItem(paramInt);
      ((TextView)paramView.findViewById(2131230892)).setText(localItem.label);
      ImageView localImageView = (ImageView)paramView.findViewById(2131230755);
      localImageView.setImageDrawable(null);
      localItem.loadWidgetPreview(localImageView);
      return paramView;
    }
  }

  public static class Item
    implements AppWidgetLoader.LabelledItem
  {
    int appWidgetPreviewId;
    String className;
    Bundle extras;
    int iconId;
    CharSequence label;
    private Context mContext;
    private WidgetPreviewLoader mWidgetPreviewLoader;
    String packageName;

    Item(Context paramContext, CharSequence paramCharSequence)
    {
      this.label = paramCharSequence;
      this.mContext = paramContext;
    }

    void cancelLoadingWidgetPreview()
    {
      if (this.mWidgetPreviewLoader != null)
      {
        this.mWidgetPreviewLoader.cancel(false);
        this.mWidgetPreviewLoader = null;
      }
    }

    Intent getIntent()
    {
      Intent localIntent = new Intent();
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

    void loadWidgetPreview(ImageView paramImageView)
    {
      this.mWidgetPreviewLoader = new WidgetPreviewLoader(this.mContext, paramImageView);
      this.mWidgetPreviewLoader.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, (Void[])null);
    }

    class WidgetPreviewLoader extends AsyncTask<Void, Bitmap, Void>
    {
      private int mIconDpi;
      private PackageManager mPackageManager;
      private Resources mResources;
      private ImageView mView;
      CanvasCache sCachedAppWidgetPreviewCanvas = new CanvasCache();
      RectCache sCachedAppWidgetPreviewDestRect = new RectCache();
      PaintCache sCachedAppWidgetPreviewPaint = new PaintCache();
      RectCache sCachedAppWidgetPreviewSrcRect = new RectCache();

      public WidgetPreviewLoader(Context paramImageView, ImageView arg3)
      {
        this.mResources = paramImageView.getResources();
        this.mPackageManager = paramImageView.getPackageManager();
        this.mIconDpi = ((ActivityManager)paramImageView.getSystemService("activity")).getLauncherLargeIconDensity();
        Object localObject;
        this.mView = localObject;
      }

      private Bitmap getWidgetPreview(ComponentName paramComponentName, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        String str = paramComponentName.getPackageName();
        if (paramInt3 < 0)
          paramInt3 = 2147483647;
        if (paramInt4 < 0)
          paramInt4 = 2147483647;
        int i = this.mResources.getDimensionPixelSize(2131558403);
        Drawable localDrawable1 = null;
        if (paramInt1 != 0)
        {
          localDrawable1 = this.mPackageManager.getDrawable(str, paramInt1, null);
          if (localDrawable1 == null)
            Log.w("KeyguardAppWidgetPickActivity", "Can't load widget preview drawable 0x" + Integer.toHexString(paramInt1) + " for provider: " + paramComponentName);
        }
        Bitmap localBitmap1 = null;
        int j;
        int k;
        int m;
        if (localDrawable1 != null)
        {
          j = 1;
          if (j == 0)
            break label209;
          k = localDrawable1.getIntrinsicWidth();
          m = localDrawable1.getIntrinsicHeight();
        }
        int n;
        int i1;
        Bitmap localBitmap2;
        while (true)
        {
          float f = 1.0F;
          if (k > paramInt3)
            f = paramInt3 / k;
          n = (int)(f * k);
          i1 = (int)(f * m);
          localBitmap2 = Bitmap.createBitmap(n, Math.min(i1, paramInt4), Bitmap.Config.ARGB_8888);
          if (j == 0)
            break label276;
          renderDrawableToBitmap(localDrawable1, localBitmap2, 0, 0, n, i1);
          return localBitmap2;
          j = 0;
          break;
          label209: k = i;
          m = i;
          Bitmap.Config localConfig = Bitmap.Config.ARGB_8888;
          localBitmap1 = Bitmap.createBitmap(k, m, localConfig);
          Drawable localDrawable2 = null;
          if (paramInt2 > 0);
          try
          {
            localDrawable2 = getFullResIcon(str, paramInt2);
            if (localDrawable2 != null)
              renderDrawableToBitmap(localDrawable2, localBitmap1, 0, 0, i, i);
          }
          catch (Resources.NotFoundException localNotFoundException)
          {
          }
        }
        label276: Canvas localCanvas = (Canvas)this.sCachedAppWidgetPreviewCanvas.get();
        Rect localRect1 = (Rect)this.sCachedAppWidgetPreviewSrcRect.get();
        Rect localRect2 = (Rect)this.sCachedAppWidgetPreviewDestRect.get();
        localCanvas.setBitmap(localBitmap2);
        localRect1.set(0, 0, localBitmap1.getWidth(), localBitmap1.getHeight());
        localRect2.set(0, 0, n, i1);
        Paint localPaint = (Paint)this.sCachedAppWidgetPreviewPaint.get();
        if (localPaint == null)
        {
          localPaint = new Paint();
          localPaint.setFilterBitmap(true);
          this.sCachedAppWidgetPreviewPaint.set(localPaint);
        }
        localCanvas.drawBitmap(localBitmap1, localRect1, localRect2, localPaint);
        localCanvas.setBitmap(null);
        return localBitmap2;
      }

      private void renderDrawableToBitmap(Drawable paramDrawable, Bitmap paramBitmap, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        renderDrawableToBitmap(paramDrawable, paramBitmap, paramInt1, paramInt2, paramInt3, paramInt4, 1.0F);
      }

      private void renderDrawableToBitmap(Drawable paramDrawable, Bitmap paramBitmap, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat)
      {
        if (paramBitmap != null)
        {
          Canvas localCanvas = new Canvas(paramBitmap);
          localCanvas.scale(paramFloat, paramFloat);
          Rect localRect = paramDrawable.copyBounds();
          paramDrawable.setBounds(paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4);
          paramDrawable.draw(localCanvas);
          paramDrawable.setBounds(localRect);
          localCanvas.setBitmap(null);
        }
      }

      public Void doInBackground(Void[] paramArrayOfVoid)
      {
        if (!isCancelled())
        {
          int i = this.mResources.getDimensionPixelSize(2131558430);
          int j = this.mResources.getDimensionPixelSize(2131558431);
          publishProgress(new Bitmap[] { getWidgetPreview(new ComponentName(KeyguardAppWidgetPickActivity.Item.this.packageName, KeyguardAppWidgetPickActivity.Item.this.className), KeyguardAppWidgetPickActivity.Item.this.appWidgetPreviewId, KeyguardAppWidgetPickActivity.Item.this.iconId, i, j) });
        }
        return null;
      }

      public Drawable getFullResDefaultActivityIcon()
      {
        return getFullResIcon(Resources.getSystem(), 17629184);
      }

      public Drawable getFullResIcon(Resources paramResources, int paramInt)
      {
        try
        {
          Drawable localDrawable2 = paramResources.getDrawableForDensity(paramInt, this.mIconDpi);
          localDrawable1 = localDrawable2;
          if (localDrawable1 != null)
            return localDrawable1;
        }
        catch (Resources.NotFoundException localNotFoundException)
        {
          while (true)
            Drawable localDrawable1 = null;
        }
        return getFullResDefaultActivityIcon();
      }

      public Drawable getFullResIcon(String paramString, int paramInt)
      {
        try
        {
          Resources localResources2 = this.mPackageManager.getResourcesForApplication(paramString);
          localResources1 = localResources2;
          if ((localResources1 != null) && (paramInt != 0))
            return getFullResIcon(localResources1, paramInt);
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          while (true)
            Resources localResources1 = null;
        }
        return getFullResDefaultActivityIcon();
      }

      public void onProgressUpdate(Bitmap[] paramArrayOfBitmap)
      {
        if (!isCancelled())
        {
          Bitmap localBitmap = paramArrayOfBitmap[0];
          this.mView.setImageBitmap(localBitmap);
        }
      }

      class CanvasCache extends KeyguardAppWidgetPickActivity.Item.WidgetPreviewLoader.WeakReferenceThreadLocal<Canvas>
      {
        CanvasCache()
        {
          super();
        }

        protected Canvas initialValue()
        {
          return new Canvas();
        }
      }

      class PaintCache extends KeyguardAppWidgetPickActivity.Item.WidgetPreviewLoader.WeakReferenceThreadLocal<Paint>
      {
        PaintCache()
        {
          super();
        }

        protected Paint initialValue()
        {
          return null;
        }
      }

      class RectCache extends KeyguardAppWidgetPickActivity.Item.WidgetPreviewLoader.WeakReferenceThreadLocal<Rect>
      {
        RectCache()
        {
          super();
        }

        protected Rect initialValue()
        {
          return new Rect();
        }
      }

      abstract class WeakReferenceThreadLocal<T>
      {
        private ThreadLocal<WeakReference<T>> mThreadLocal = new ThreadLocal();

        public WeakReferenceThreadLocal()
        {
        }

        public T get()
        {
          WeakReference localWeakReference = (WeakReference)this.mThreadLocal.get();
          if (localWeakReference == null)
          {
            Object localObject2 = initialValue();
            this.mThreadLocal.set(new WeakReference(localObject2));
            return localObject2;
          }
          Object localObject1 = localWeakReference.get();
          if (localObject1 == null)
          {
            localObject1 = initialValue();
            this.mThreadLocal.set(new WeakReference(localObject1));
          }
          return localObject1;
        }

        abstract T initialValue();

        public void set(T paramT)
        {
          this.mThreadLocal.set(new WeakReference(paramT));
        }
      }
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.KeyguardAppWidgetPickActivity
 * JD-Core Version:    0.6.2
 */