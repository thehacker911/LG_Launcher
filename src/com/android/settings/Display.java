package com.android.settings;

import android.R.styleable;
import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class Display extends Activity
  implements View.OnClickListener
{
  private Configuration mCurConfig = new Configuration();
  private DisplayMetrics mDisplayMetrics;
  private Spinner mFontSize;
  private AdapterView.OnItemSelectedListener mFontSizeChanged = new AdapterView.OnItemSelectedListener()
  {
    public void onItemSelected(AdapterView paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      if (paramAnonymousInt == 0)
        Display.this.mCurConfig.fontScale = 0.75F;
      while (true)
      {
        Display.this.updateFontScale();
        return;
        if (paramAnonymousInt == 2)
          Display.this.mCurConfig.fontScale = 1.25F;
        else
          Display.this.mCurConfig.fontScale = 1.0F;
      }
    }

    public void onNothingSelected(AdapterView paramAnonymousAdapterView)
    {
    }
  };
  private TextView mPreview;
  private TypedValue mTextSizeTyped;

  private void updateFontScale()
  {
    this.mDisplayMetrics.scaledDensity = (this.mDisplayMetrics.density * this.mCurConfig.fontScale);
    float f = this.mTextSizeTyped.getDimension(this.mDisplayMetrics);
    this.mPreview.setTextSize(0, f);
  }

  public void onClick(View paramView)
  {
    try
    {
      ActivityManagerNative.getDefault().updatePersistentConfiguration(this.mCurConfig);
      label12: finish();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      break label12;
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968626);
    this.mFontSize = ((Spinner)findViewById(2131230841));
    this.mFontSize.setOnItemSelectedListener(this.mFontSizeChanged);
    String[] arrayOfString = new String[3];
    Resources localResources = getResources();
    arrayOfString[0] = localResources.getString(2131427380);
    arrayOfString[1] = localResources.getString(2131427381);
    arrayOfString[2] = localResources.getString(2131427382);
    ArrayAdapter localArrayAdapter = new ArrayAdapter(this, 17367048, arrayOfString);
    localArrayAdapter.setDropDownViewResource(17367049);
    this.mFontSize.setAdapter(localArrayAdapter);
    this.mPreview = ((TextView)findViewById(2131230842));
    this.mPreview.setText(localResources.getText(2131427383));
    Button localButton = (Button)findViewById(2131230843);
    localButton.setText(localResources.getText(2131427384));
    localButton.setOnClickListener(this);
    this.mTextSizeTyped = new TypedValue();
    TypedArray localTypedArray = obtainStyledAttributes(R.styleable.TextView);
    localTypedArray.getValue(2, this.mTextSizeTyped);
    DisplayMetrics localDisplayMetrics = getResources().getDisplayMetrics();
    this.mDisplayMetrics = new DisplayMetrics();
    this.mDisplayMetrics.density = localDisplayMetrics.density;
    this.mDisplayMetrics.heightPixels = localDisplayMetrics.heightPixels;
    this.mDisplayMetrics.scaledDensity = localDisplayMetrics.scaledDensity;
    this.mDisplayMetrics.widthPixels = localDisplayMetrics.widthPixels;
    this.mDisplayMetrics.xdpi = localDisplayMetrics.xdpi;
    this.mDisplayMetrics.ydpi = localDisplayMetrics.ydpi;
    localTypedArray.recycle();
  }

  public void onResume()
  {
    super.onResume();
    try
    {
      this.mCurConfig.updateFrom(ActivityManagerNative.getDefault().getConfiguration());
      label20: if (this.mCurConfig.fontScale < 1.0F)
        this.mFontSize.setSelection(0);
      while (true)
      {
        updateFontScale();
        return;
        if (this.mCurConfig.fontScale > 1.0F)
          this.mFontSize.setSelection(2);
        else
          this.mFontSize.setSelection(1);
      }
    }
    catch (RemoteException localRemoteException)
    {
      break label20;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.Display
 * JD-Core Version:    0.6.2
 */