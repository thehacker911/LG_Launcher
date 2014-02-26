package com.android.settings;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.os.storage.StorageVolume;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.android.internal.os.storage.ExternalStorageFormatter;

public class MediaFormat extends Activity
{
  private Button mFinalButton;
  private View.OnClickListener mFinalClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (Utils.isMonkeyRunning())
        return;
      Intent localIntent = new Intent("com.android.internal.os.storage.FORMAT_ONLY");
      localIntent.setComponent(ExternalStorageFormatter.COMPONENT_NAME);
      localIntent.putExtra("storage_volume", (StorageVolume)MediaFormat.this.getIntent().getParcelableExtra("storage_volume"));
      MediaFormat.this.startService(localIntent);
      MediaFormat.this.finish();
    }
  };
  private View mFinalView;
  private LayoutInflater mInflater;
  private View mInitialView;
  private Button mInitiateButton;
  private View.OnClickListener mInitiateListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (!MediaFormat.this.runKeyguardConfirmation(55))
        MediaFormat.this.establishFinalConfirmationState();
    }
  };

  private void establishFinalConfirmationState()
  {
    if (this.mFinalView == null)
    {
      this.mFinalView = this.mInflater.inflate(2130968650, null);
      this.mFinalButton = ((Button)this.mFinalView.findViewById(2131230913));
      this.mFinalButton.setOnClickListener(this.mFinalClickListener);
    }
    setContentView(this.mFinalView);
  }

  private void establishInitialState()
  {
    if (this.mInitialView == null)
    {
      this.mInitialView = this.mInflater.inflate(2130968651, null);
      this.mInitiateButton = ((Button)this.mInitialView.findViewById(2131230914));
      this.mInitiateButton.setOnClickListener(this.mInitiateListener);
    }
    setContentView(this.mInitialView);
  }

  private boolean runKeyguardConfirmation(int paramInt)
  {
    return new ChooseLockSettingsHelper(this).launchConfirmationActivity(paramInt, getText(2131428232), getText(2131428233));
  }

  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (paramInt1 != 55)
      return;
    if (paramInt2 == -1)
    {
      establishFinalConfirmationState();
      return;
    }
    if (paramInt2 == 0)
    {
      finish();
      return;
    }
    establishInitialState();
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mInitialView = null;
    this.mFinalView = null;
    this.mInflater = LayoutInflater.from(this);
    establishInitialState();
  }

  public void onPause()
  {
    super.onPause();
    if (!isFinishing())
      establishInitialState();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.MediaFormat
 * JD-Core Version:    0.6.2
 */