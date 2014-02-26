package com.android.settings.wifi;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WpsListener;
import android.net.wifi.WpsInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;

public class WpsDialog extends AlertDialog
{
  private Button mButton;
  private Context mContext;
  DialogState mDialogState = DialogState.WPS_INIT;
  private final IntentFilter mFilter;
  private Handler mHandler = new Handler();
  private ProgressBar mProgressBar;
  private BroadcastReceiver mReceiver;
  private TextView mTextView;
  private ProgressBar mTimeoutBar;
  private Timer mTimer;
  private View mView;
  private WifiManager mWifiManager;
  private WifiManager.WpsListener mWpsListener;
  private int mWpsSetup;

  public WpsDialog(Context paramContext, int paramInt)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mWpsSetup = paramInt;
    this.mWpsListener = new WifiManager.WpsListener()
    {
      public void onCompletion()
      {
        WpsDialog.this.updateDialog(WpsDialog.DialogState.WPS_COMPLETE, WpsDialog.this.mContext.getString(2131427857));
      }

      public void onFailure(int paramAnonymousInt)
      {
        String str;
        switch (paramAnonymousInt)
        {
        case 2:
        default:
          str = WpsDialog.this.mContext.getString(2131427860);
        case 3:
        case 4:
        case 5:
        case 1:
        }
        while (true)
        {
          WpsDialog.this.updateDialog(WpsDialog.DialogState.WPS_FAILED, str);
          return;
          str = WpsDialog.this.mContext.getString(2131427864);
          continue;
          str = WpsDialog.this.mContext.getString(2131427861);
          continue;
          str = WpsDialog.this.mContext.getString(2131427862);
          continue;
          str = WpsDialog.this.mContext.getString(2131427859);
        }
      }

      public void onStartSuccess(String paramAnonymousString)
      {
        if (paramAnonymousString != null)
        {
          WpsDialog.this.updateDialog(WpsDialog.DialogState.WPS_START, String.format(WpsDialog.this.mContext.getString(2131427856), new Object[] { paramAnonymousString }));
          return;
        }
        WpsDialog.this.updateDialog(WpsDialog.DialogState.WPS_START, WpsDialog.this.mContext.getString(2131427855));
      }
    };
    this.mFilter = new IntentFilter();
    this.mFilter.addAction("android.net.wifi.STATE_CHANGE");
    this.mReceiver = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        WpsDialog.this.handleEvent(paramAnonymousContext, paramAnonymousIntent);
      }
    };
  }

  private void handleEvent(Context paramContext, Intent paramIntent)
  {
    if (("android.net.wifi.STATE_CHANGE".equals(paramIntent.getAction())) && (((NetworkInfo)paramIntent.getParcelableExtra("networkInfo")).getDetailedState() == NetworkInfo.DetailedState.CONNECTED) && (this.mDialogState == DialogState.WPS_COMPLETE))
    {
      WifiInfo localWifiInfo = this.mWifiManager.getConnectionInfo();
      if (localWifiInfo != null)
      {
        String str1 = this.mContext.getString(2131427858);
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = localWifiInfo.getSSID();
        String str2 = String.format(str1, arrayOfObject);
        updateDialog(DialogState.CONNECTED, str2);
      }
    }
  }

  private void updateDialog(final DialogState paramDialogState, final String paramString)
  {
    if (this.mDialogState.ordinal() >= paramDialogState.ordinal())
      return;
    this.mDialogState = paramDialogState;
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        switch (WpsDialog.5.$SwitchMap$com$android$settings$wifi$WpsDialog$DialogState[paramDialogState.ordinal()])
        {
        default:
        case 1:
        case 2:
        case 3:
        }
        while (true)
        {
          WpsDialog.this.mTextView.setText(paramString);
          return;
          WpsDialog.this.mTimeoutBar.setVisibility(8);
          WpsDialog.this.mProgressBar.setVisibility(0);
          continue;
          WpsDialog.this.mButton.setText(WpsDialog.this.mContext.getString(2131428420));
          WpsDialog.this.mTimeoutBar.setVisibility(8);
          WpsDialog.this.mProgressBar.setVisibility(8);
          if (WpsDialog.this.mReceiver != null)
          {
            WpsDialog.this.mContext.unregisterReceiver(WpsDialog.this.mReceiver);
            WpsDialog.access$702(WpsDialog.this, null);
          }
        }
      }
    });
  }

  protected void onCreate(Bundle paramBundle)
  {
    this.mView = getLayoutInflater().inflate(2130968744, null);
    this.mTextView = ((TextView)this.mView.findViewById(2131231224));
    this.mTextView.setText(2131427854);
    this.mTimeoutBar = ((ProgressBar)this.mView.findViewById(2131231225));
    this.mTimeoutBar.setMax(120);
    this.mTimeoutBar.setProgress(0);
    this.mProgressBar = ((ProgressBar)this.mView.findViewById(2131231226));
    this.mProgressBar.setVisibility(8);
    this.mButton = ((Button)this.mView.findViewById(2131231227));
    this.mButton.setText(2131427915);
    this.mButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        WpsDialog.this.dismiss();
      }
    });
    this.mWifiManager = ((WifiManager)this.mContext.getSystemService("wifi"));
    setView(this.mView);
    super.onCreate(paramBundle);
  }

  protected void onStart()
  {
    this.mTimer = new Timer(false);
    this.mTimer.schedule(new TimerTask()
    {
      public void run()
      {
        WpsDialog.this.mHandler.post(new Runnable()
        {
          public void run()
          {
            WpsDialog.this.mTimeoutBar.incrementProgressBy(1);
          }
        });
      }
    }
    , 1000L, 1000L);
    this.mContext.registerReceiver(this.mReceiver, this.mFilter);
    WpsInfo localWpsInfo = new WpsInfo();
    localWpsInfo.setup = this.mWpsSetup;
    this.mWifiManager.startWps(localWpsInfo, this.mWpsListener);
  }

  protected void onStop()
  {
    if (this.mDialogState != DialogState.WPS_COMPLETE)
      this.mWifiManager.cancelWps(null);
    if (this.mReceiver != null)
    {
      this.mContext.unregisterReceiver(this.mReceiver);
      this.mReceiver = null;
    }
    if (this.mTimer != null)
      this.mTimer.cancel();
  }

  private static enum DialogState
  {
    static
    {
      WPS_COMPLETE = new DialogState("WPS_COMPLETE", 2);
      CONNECTED = new DialogState("CONNECTED", 3);
      WPS_FAILED = new DialogState("WPS_FAILED", 4);
      DialogState[] arrayOfDialogState = new DialogState[5];
      arrayOfDialogState[0] = WPS_INIT;
      arrayOfDialogState[1] = WPS_START;
      arrayOfDialogState[2] = WPS_COMPLETE;
      arrayOfDialogState[3] = CONNECTED;
      arrayOfDialogState[4] = WPS_FAILED;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.wifi.WpsDialog
 * JD-Core Version:    0.6.2
 */