package com.android.settings;

import android.app.Activity;
import android.app.Fragment;
import android.app.StatusBarManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.os.storage.IMountService.Stub;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class CryptKeeperConfirm extends Fragment
{
  private View mContentView;
  private Button mFinalButton;
  private View.OnClickListener mFinalClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (Utils.isMonkeyRunning())
        return;
      Intent localIntent = new Intent(CryptKeeperConfirm.this.getActivity(), CryptKeeperConfirm.Blank.class);
      localIntent.putExtras(CryptKeeperConfirm.this.getArguments());
      CryptKeeperConfirm.this.startActivity(localIntent);
    }
  };

  private void establishFinalConfirmationState()
  {
    this.mFinalButton = ((Button)this.mContentView.findViewById(2131230777));
    this.mFinalButton.setOnClickListener(this.mFinalClickListener);
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mContentView = paramLayoutInflater.inflate(2130968604, null);
    establishFinalConfirmationState();
    return this.mContentView;
  }

  public static class Blank extends Activity
  {
    private Handler mHandler = new Handler();

    public void onCreate(Bundle paramBundle)
    {
      super.onCreate(paramBundle);
      setContentView(2130968603);
      if (Utils.isMonkeyRunning())
        finish();
      ((StatusBarManager)getSystemService("statusbar")).disable(58130432);
      this.mHandler.postDelayed(new Runnable()
      {
        public void run()
        {
          IBinder localIBinder = ServiceManager.getService("mount");
          if (localIBinder == null)
          {
            Log.e("CryptKeeper", "Failed to find the mount service");
            CryptKeeperConfirm.Blank.this.finish();
            return;
          }
          IMountService localIMountService = IMountService.Stub.asInterface(localIBinder);
          try
          {
            localIMountService.encryptStorage(CryptKeeperConfirm.Blank.this.getIntent().getExtras().getString("password"));
            return;
          }
          catch (Exception localException)
          {
            Log.e("CryptKeeper", "Error while encrypting...", localException);
          }
        }
      }
      , 700L);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.CryptKeeperConfirm
 * JD-Core Version:    0.6.2
 */