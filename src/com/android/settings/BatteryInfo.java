package com.android.settings;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IPowerManager;
import android.os.IPowerManager.Stub;
import android.os.Message;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.widget.TextView;
import com.android.internal.app.IBatteryStats;
import com.android.internal.app.IBatteryStats.Stub;

public class BatteryInfo extends Activity
{
  private IBatteryStats mBatteryStats;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default:
        return;
      case 1:
      }
      BatteryInfo.this.updateBatteryStats();
      sendEmptyMessageDelayed(1, 1000L);
    }
  };
  private TextView mHealth;
  private IntentFilter mIntentFilter;
  private BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      int j;
      String str;
      if (paramAnonymousIntent.getAction().equals("android.intent.action.BATTERY_CHANGED"))
      {
        int i = paramAnonymousIntent.getIntExtra("plugged", 0);
        BatteryInfo.this.mLevel.setText("" + paramAnonymousIntent.getIntExtra("level", 0));
        BatteryInfo.this.mScale.setText("" + paramAnonymousIntent.getIntExtra("scale", 0));
        BatteryInfo.this.mVoltage.setText("" + paramAnonymousIntent.getIntExtra("voltage", 0) + " " + BatteryInfo.this.getString(2131427393));
        BatteryInfo.this.mTemperature.setText("" + BatteryInfo.this.tenthsToFixedString(paramAnonymousIntent.getIntExtra("temperature", 0)) + BatteryInfo.this.getString(2131427395));
        BatteryInfo.this.mTechnology.setText("" + paramAnonymousIntent.getStringExtra("technology"));
        BatteryInfo.this.mStatus.setText(Utils.getBatteryStatus(BatteryInfo.this.getResources(), paramAnonymousIntent));
        switch (i)
        {
        default:
          BatteryInfo.this.mPower.setText(BatteryInfo.this.getString(2131427413));
          j = paramAnonymousIntent.getIntExtra("health", 1);
          if (j == 2)
            str = BatteryInfo.this.getString(2131427415);
          break;
        case 0:
        case 1:
        case 2:
        case 4:
        case 3:
        }
      }
      while (true)
      {
        BatteryInfo.this.mHealth.setText(str);
        return;
        BatteryInfo.this.mPower.setText(BatteryInfo.this.getString(2131427408));
        break;
        BatteryInfo.this.mPower.setText(BatteryInfo.this.getString(2131427409));
        break;
        BatteryInfo.this.mPower.setText(BatteryInfo.this.getString(2131427410));
        break;
        BatteryInfo.this.mPower.setText(BatteryInfo.this.getString(2131427411));
        break;
        BatteryInfo.this.mPower.setText(BatteryInfo.this.getString(2131427412));
        break;
        if (j == 3)
          str = BatteryInfo.this.getString(2131427416);
        else if (j == 4)
          str = BatteryInfo.this.getString(2131427417);
        else if (j == 5)
          str = BatteryInfo.this.getString(2131427418);
        else if (j == 6)
          str = BatteryInfo.this.getString(2131427419);
        else if (j == 7)
          str = BatteryInfo.this.getString(2131427420);
        else
          str = BatteryInfo.this.getString(2131427414);
      }
    }
  };
  private TextView mLevel;
  private TextView mPower;
  private TextView mScale;
  private IPowerManager mScreenStats;
  private TextView mStatus;
  private TextView mTechnology;
  private TextView mTemperature;
  private TextView mUptime;
  private TextView mVoltage;

  private final String tenthsToFixedString(int paramInt)
  {
    int i = paramInt / 10;
    return Integer.toString(i) + "." + Math.abs(paramInt - i * 10);
  }

  private void updateBatteryStats()
  {
    long l = SystemClock.elapsedRealtime();
    this.mUptime.setText(DateUtils.formatElapsedTime(l / 1000L));
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968590);
    this.mIntentFilter = new IntentFilter();
    this.mIntentFilter.addAction("android.intent.action.BATTERY_CHANGED");
  }

  public void onPause()
  {
    super.onPause();
    this.mHandler.removeMessages(1);
    unregisterReceiver(this.mIntentReceiver);
  }

  public void onResume()
  {
    super.onResume();
    this.mStatus = ((TextView)findViewById(2131230740));
    this.mPower = ((TextView)findViewById(2131230741));
    this.mLevel = ((TextView)findViewById(2131230742));
    this.mScale = ((TextView)findViewById(2131230743));
    this.mHealth = ((TextView)findViewById(2131230744));
    this.mTechnology = ((TextView)findViewById(2131230747));
    this.mVoltage = ((TextView)findViewById(2131230745));
    this.mTemperature = ((TextView)findViewById(2131230746));
    this.mUptime = ((TextView)findViewById(2131230748));
    this.mBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
    this.mScreenStats = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
    this.mHandler.sendEmptyMessageDelayed(1, 1000L);
    registerReceiver(this.mIntentReceiver, this.mIntentFilter);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.BatteryInfo
 * JD-Core Version:    0.6.2
 */