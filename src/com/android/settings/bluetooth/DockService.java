package com.android.settings.bluetooth;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings.Global;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class DockService extends Service
  implements LocalBluetoothProfileManager.ServiceListener
{
  private CheckBox mAudioMediaCheckbox;
  private final CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener()
  {
    public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
    {
      if (DockService.this.mDevice != null)
      {
        LocalBluetoothPreferences.saveDockAutoConnectSetting(DockService.this, DockService.this.mDevice.getAddress(), paramAnonymousBoolean);
        return;
      }
      ContentResolver localContentResolver = DockService.this.getContentResolver();
      if (paramAnonymousBoolean);
      for (int i = 1; ; i = 0)
      {
        Settings.Global.putInt(localContentResolver, "dock_audio_media_enabled", i);
        return;
      }
    }
  };
  private boolean[] mCheckedItems;
  private final DialogInterface.OnClickListener mClickListener = new DialogInterface.OnClickListener()
  {
    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
    {
      int i = 1;
      if (paramAnonymousInt == -1)
      {
        if (DockService.this.mDevice == null)
          break label77;
        if (!LocalBluetoothPreferences.hasDockAutoConnectSetting(DockService.this, DockService.this.mDevice.getAddress()))
          LocalBluetoothPreferences.saveDockAutoConnectSetting(DockService.this, DockService.this.mDevice.getAddress(), i);
        DockService.this.applyBtSettings(DockService.this.mDevice, DockService.this.mStartIdAssociatedWithDialog);
      }
      label77: 
      while (DockService.this.mAudioMediaCheckbox == null)
        return;
      ContentResolver localContentResolver = DockService.this.getContentResolver();
      if (DockService.this.mAudioMediaCheckbox.isChecked());
      while (true)
      {
        Settings.Global.putInt(localContentResolver, "dock_audio_media_enabled", i);
        return;
        int j = 0;
      }
    }
  };
  private BluetoothDevice mDevice;
  private CachedBluetoothDeviceManager mDeviceManager;
  private AlertDialog mDialog;
  private final DialogInterface.OnDismissListener mDismissListener = new DialogInterface.OnDismissListener()
  {
    public void onDismiss(DialogInterface paramAnonymousDialogInterface)
    {
      if (DockService.this.mPendingDevice == null)
        DockEventReceiver.finishStartingService(DockService.this, DockService.this.mStartIdAssociatedWithDialog);
      DockService.this.stopForeground(true);
    }
  };
  private LocalBluetoothAdapter mLocalAdapter;
  private final DialogInterface.OnMultiChoiceClickListener mMultiClickListener = new DialogInterface.OnMultiChoiceClickListener()
  {
    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt, boolean paramAnonymousBoolean)
    {
      DockService.this.mCheckedItems[paramAnonymousInt] = paramAnonymousBoolean;
    }
  };
  private BluetoothDevice mPendingDevice;
  private int mPendingStartId;
  private int mPendingTurnOffStartId = -100;
  private int mPendingTurnOnStartId = -100;
  private LocalBluetoothProfileManager mProfileManager;
  private LocalBluetoothProfile[] mProfiles;
  private Runnable mRunnable;
  private volatile ServiceHandler mServiceHandler;
  private volatile Looper mServiceLooper;
  private int mStartIdAssociatedWithDialog;

  private void applyBtSettings(BluetoothDevice paramBluetoothDevice, int paramInt)
  {
    if (paramBluetoothDevice != null);
    while (true)
    {
      int j;
      try
      {
        if ((this.mProfiles != null) && (this.mCheckedItems != null))
        {
          LocalBluetoothAdapter localLocalBluetoothAdapter = this.mLocalAdapter;
          if (localLocalBluetoothAdapter != null);
        }
        else
        {
          return;
        }
        boolean[] arrayOfBoolean = this.mCheckedItems;
        int i = arrayOfBoolean.length;
        j = 0;
        if (j >= i)
          break label161;
        if (arrayOfBoolean[j] != 0)
        {
          int k = this.mLocalAdapter.getBluetoothState();
          this.mLocalAdapter.enable();
          if (k != 12)
          {
            if ((this.mPendingDevice != null) && (this.mPendingDevice.equals(this.mDevice)))
              continue;
            this.mPendingDevice = paramBluetoothDevice;
            this.mPendingStartId = paramInt;
            if (k == 11)
              continue;
            getPrefs().edit().putBoolean("disable_bt_when_undock", true).apply();
            continue;
          }
        }
      }
      finally
      {
      }
      j++;
      continue;
      label161: this.mPendingDevice = null;
      int m = 0;
      CachedBluetoothDevice localCachedBluetoothDevice = getCachedBluetoothDevice(paramBluetoothDevice);
      int n = 0;
      if (n < this.mProfiles.length)
      {
        LocalBluetoothProfile localLocalBluetoothProfile = this.mProfiles[n];
        if (this.mCheckedItems[n] != 0)
          m = 1;
        while (true)
        {
          localLocalBluetoothProfile.setPreferred(paramBluetoothDevice, this.mCheckedItems[n]);
          n++;
          break;
          if ((this.mCheckedItems[n] == 0) && (localLocalBluetoothProfile.getConnectionStatus(localCachedBluetoothDevice.getDevice()) == 2))
            localCachedBluetoothDevice.disconnect(this.mProfiles[n]);
        }
      }
      if (m != 0)
        localCachedBluetoothDevice.connect(false);
    }
  }

  private void connectIfEnabled(BluetoothDevice paramBluetoothDevice)
  {
    try
    {
      CachedBluetoothDevice localCachedBluetoothDevice = getCachedBluetoothDevice(paramBluetoothDevice);
      Iterator localIterator = localCachedBluetoothDevice.getConnectableProfiles().iterator();
      while (localIterator.hasNext())
        if (((LocalBluetoothProfile)localIterator.next()).getPreferred(paramBluetoothDevice) == 1000)
          localCachedBluetoothDevice.connect(false);
      return;
    }
    finally
    {
    }
  }

  private void createDialog(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2)
  {
    if (this.mDialog != null)
    {
      this.mDialog.dismiss();
      this.mDialog = null;
    }
    this.mDevice = paramBluetoothDevice;
    switch (paramInt1)
    {
    default:
      return;
    case 1:
    case 2:
    case 3:
    case 4:
    }
    startForeground(0, new Notification());
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
    LayoutInflater localLayoutInflater = (LayoutInflater)getSystemService("layout_inflater");
    this.mAudioMediaCheckbox = null;
    if (paramBluetoothDevice != null)
    {
      boolean bool2;
      CheckBox localCheckBox;
      if (!LocalBluetoothPreferences.hasDockAutoConnectSetting(this, paramBluetoothDevice.getAddress()))
      {
        bool2 = true;
        CharSequence[] arrayOfCharSequence = initBtSettings(paramBluetoothDevice, paramInt1, bool2);
        localBuilder.setTitle(getString(2131427773));
        localBuilder.setMultiChoiceItems(arrayOfCharSequence, this.mCheckedItems, this.mMultiClickListener);
        localView = localLayoutInflater.inflate(2130968699, null);
        localCheckBox = (CheckBox)localView.findViewById(2131231030);
        if ((!bool2) && (!LocalBluetoothPreferences.getDockAutoConnectSetting(this, paramBluetoothDevice.getAddress())))
          break label315;
      }
      label315: for (boolean bool3 = true; ; bool3 = false)
      {
        localCheckBox.setChecked(bool3);
        localCheckBox.setOnCheckedChangeListener(this.mCheckedChangeListener);
        float f = getResources().getDisplayMetrics().density;
        localBuilder.setView(localView, (int)(14.0F * f), 0, (int)(14.0F * f), 0);
        localBuilder.setPositiveButton(getString(17039370), this.mClickListener);
        this.mStartIdAssociatedWithDialog = paramInt2;
        this.mDialog = localBuilder.create();
        this.mDialog.getWindow().setType(2009);
        this.mDialog.setOnDismissListener(this.mDismissListener);
        this.mDialog.show();
        return;
        bool2 = false;
        break;
      }
    }
    localBuilder.setTitle(getString(2131427773));
    View localView = localLayoutInflater.inflate(2130968627, null);
    this.mAudioMediaCheckbox = ((CheckBox)localView.findViewById(2131230844));
    if (Settings.Global.getInt(getContentResolver(), "dock_audio_media_enabled", 0) == 1);
    for (boolean bool1 = true; ; bool1 = false)
    {
      this.mAudioMediaCheckbox.setChecked(bool1);
      this.mAudioMediaCheckbox.setOnCheckedChangeListener(this.mCheckedChangeListener);
      break;
    }
  }

  private CachedBluetoothDevice getCachedBluetoothDevice(BluetoothDevice paramBluetoothDevice)
  {
    CachedBluetoothDevice localCachedBluetoothDevice = this.mDeviceManager.findDevice(paramBluetoothDevice);
    if (localCachedBluetoothDevice == null)
      localCachedBluetoothDevice = this.mDeviceManager.addDevice(this.mLocalAdapter, this.mProfileManager, paramBluetoothDevice);
    return localCachedBluetoothDevice;
  }

  private SharedPreferences getPrefs()
  {
    return getSharedPreferences("dock_settings", 0);
  }

  private void handleBluetoothStateOn(int paramInt)
  {
    if (this.mPendingDevice != null)
    {
      if (this.mPendingDevice.equals(this.mDevice))
        applyBtSettings(this.mPendingDevice, this.mPendingStartId);
      this.mPendingDevice = null;
      DockEventReceiver.finishStartingService(this, this.mPendingStartId);
    }
    SharedPreferences localSharedPreferences;
    do
      while (true)
      {
        if (this.mPendingTurnOnStartId != -100)
        {
          DockEventReceiver.finishStartingService(this, this.mPendingTurnOnStartId);
          this.mPendingTurnOnStartId = -100;
        }
        DockEventReceiver.finishStartingService(this, paramInt);
        return;
        localSharedPreferences = getPrefs();
        Intent localIntent = registerReceiver(null, new IntentFilter("android.intent.action.DOCK_EVENT"));
        if (localIntent != null)
        {
          if (localIntent.getIntExtra("android.intent.extra.DOCK_STATE", 0) == 0)
            break;
          BluetoothDevice localBluetoothDevice = (BluetoothDevice)localIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
          if (localBluetoothDevice != null)
            connectIfEnabled(localBluetoothDevice);
        }
      }
    while ((!localSharedPreferences.getBoolean("disable_bt", false)) || (!this.mLocalAdapter.disable()));
    this.mPendingTurnOffStartId = paramInt;
    localSharedPreferences.edit().remove("disable_bt").apply();
  }

  private void handleBtStateChange(Intent paramIntent, int paramInt)
  {
    int i = paramIntent.getIntExtra("android.bluetooth.adapter.extra.STATE", -2147483648);
    if (i == 12);
    while (true)
    {
      try
      {
        handleBluetoothStateOn(paramInt);
        return;
        if (i == 13)
        {
          getPrefs().edit().remove("disable_bt_when_undock").apply();
          DockEventReceiver.finishStartingService(this, paramInt);
          continue;
        }
      }
      finally
      {
      }
      if (i == 10)
      {
        if (this.mPendingTurnOffStartId != -100)
        {
          DockEventReceiver.finishStartingService(this, this.mPendingTurnOffStartId);
          getPrefs().edit().remove("disable_bt").apply();
          this.mPendingTurnOffStartId = -100;
        }
        if (this.mPendingDevice != null)
        {
          this.mLocalAdapter.enable();
          this.mPendingTurnOnStartId = paramInt;
        }
        else
        {
          DockEventReceiver.finishStartingService(this, paramInt);
        }
      }
    }
  }

  private void handleDocked(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2)
  {
    if (paramBluetoothDevice != null);
    try
    {
      if (LocalBluetoothPreferences.getDockAutoConnectSetting(this, paramBluetoothDevice.getAddress()))
      {
        initBtSettings(paramBluetoothDevice, paramInt1, false);
        applyBtSettings(this.mDevice, paramInt2);
      }
      while (true)
      {
        return;
        createDialog(paramBluetoothDevice, paramInt1, paramInt2);
      }
    }
    finally
    {
    }
  }

  private void handleUndocked(BluetoothDevice paramBluetoothDevice)
  {
    try
    {
      this.mRunnable = null;
      this.mProfileManager.removeServiceListener(this);
      if (this.mDialog != null)
      {
        this.mDialog.dismiss();
        this.mDialog = null;
      }
      this.mDevice = null;
      this.mPendingDevice = null;
      if (paramBluetoothDevice != null)
        getCachedBluetoothDevice(paramBluetoothDevice).disconnect();
      return;
    }
    finally
    {
    }
  }

  private void handleUnexpectedDisconnect(BluetoothDevice paramBluetoothDevice, LocalBluetoothProfile paramLocalBluetoothProfile, int paramInt)
  {
    if (paramBluetoothDevice != null);
    try
    {
      Intent localIntent = registerReceiver(null, new IntentFilter("android.intent.action.DOCK_EVENT"));
      if ((localIntent != null) && (localIntent.getIntExtra("android.intent.extra.DOCK_STATE", 0) != 0))
      {
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)localIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        if ((localBluetoothDevice != null) && (localBluetoothDevice.equals(paramBluetoothDevice)))
          getCachedBluetoothDevice(localBluetoothDevice).connectProfile(paramLocalBluetoothProfile);
      }
      DockEventReceiver.finishStartingService(this, paramInt);
      return;
    }
    finally
    {
    }
  }

  private CharSequence[] initBtSettings(BluetoothDevice paramBluetoothDevice, int paramInt, boolean paramBoolean)
  {
    switch (paramInt)
    {
    default:
      return null;
    case 1:
    case 3:
    case 4:
    case 2:
    }
    CharSequence[] arrayOfCharSequence;
    for (int i = 1; ; i = 2)
    {
      this.mProfiles = new LocalBluetoothProfile[i];
      this.mCheckedItems = new boolean[i];
      arrayOfCharSequence = new CharSequence[i];
      switch (paramInt)
      {
      default:
        return arrayOfCharSequence;
      case 1:
      case 3:
      case 4:
        arrayOfCharSequence[0] = getString(2131427775);
        this.mProfiles[0] = this.mProfileManager.getA2dpProfile();
        if (!paramBoolean)
          break label247;
        this.mCheckedItems[0] = false;
        return arrayOfCharSequence;
      case 2:
      }
    }
    arrayOfCharSequence[0] = getString(2131427774);
    arrayOfCharSequence[1] = getString(2131427775);
    this.mProfiles[0] = this.mProfileManager.getHeadsetProfile();
    this.mProfiles[1] = this.mProfileManager.getA2dpProfile();
    if (paramBoolean)
    {
      this.mCheckedItems[0] = true;
      this.mCheckedItems[1] = true;
      return arrayOfCharSequence;
    }
    this.mCheckedItems[0] = this.mProfiles[0].isPreferred(paramBluetoothDevice);
    this.mCheckedItems[1] = this.mProfiles[1].isPreferred(paramBluetoothDevice);
    return arrayOfCharSequence;
    label247: this.mCheckedItems[0] = this.mProfiles[0].isPreferred(paramBluetoothDevice);
    return arrayOfCharSequence;
  }

  private boolean msgTypeDisableBluetooth(int paramInt)
  {
    SharedPreferences localSharedPreferences = getPrefs();
    if (this.mLocalAdapter.disable())
    {
      localSharedPreferences.edit().remove("disable_bt_when_undock").apply();
      return false;
    }
    localSharedPreferences.edit().putBoolean("disable_bt", true).apply();
    this.mPendingTurnOffStartId = paramInt;
    return true;
  }

  private boolean msgTypeDocked(final BluetoothDevice paramBluetoothDevice, final int paramInt1, final int paramInt2)
  {
    this.mServiceHandler.removeMessages(444);
    this.mServiceHandler.removeMessages(555);
    getPrefs().edit().remove("disable_bt").apply();
    if (paramBluetoothDevice != null)
      if (!paramBluetoothDevice.equals(this.mDevice))
      {
        if (this.mDevice != null)
          handleUndocked(this.mDevice);
        this.mDevice = paramBluetoothDevice;
        this.mProfileManager.addServiceListener(this);
        if (!this.mProfileManager.isManagerReady())
          break label112;
        handleDocked(paramBluetoothDevice, paramInt1, paramInt2);
        this.mProfileManager.removeServiceListener(this);
      }
    label112: 
    while ((Settings.Global.getInt(getContentResolver(), "dock_audio_media_enabled", -1) != -1) || (paramInt1 != 3))
    {
      return false;
      this.mRunnable = new Runnable()
      {
        public void run()
        {
          DockService.this.handleDocked(paramBluetoothDevice, paramInt1, paramInt2);
        }
      };
      return true;
    }
    handleDocked(null, paramInt1, paramInt2);
    return true;
  }

  private boolean msgTypeUndockedPermanent(BluetoothDevice paramBluetoothDevice, int paramInt)
  {
    handleUndocked(paramBluetoothDevice);
    if (paramBluetoothDevice != null)
    {
      SharedPreferences localSharedPreferences = getPrefs();
      if (localSharedPreferences.getBoolean("disable_bt_when_undock", false))
      {
        if (!hasOtherConnectedDevices(paramBluetoothDevice))
          break label54;
        localSharedPreferences.edit().remove("disable_bt_when_undock").apply();
      }
    }
    return false;
    label54: Message localMessage = this.mServiceHandler.obtainMessage(555, 0, paramInt, null);
    this.mServiceHandler.sendMessageDelayed(localMessage, 2000L);
    return true;
  }

  private void msgTypeUndockedTemporary(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2)
  {
    Message localMessage = this.mServiceHandler.obtainMessage(444, paramInt1, paramInt2, paramBluetoothDevice);
    this.mServiceHandler.sendMessageDelayed(localMessage, 1000L);
  }

  private Message parseIntent(Intent paramIntent)
  {
    BluetoothDevice localBluetoothDevice = (BluetoothDevice)paramIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
    int i = paramIntent.getIntExtra("android.intent.extra.DOCK_STATE", -1234);
    int j;
    switch (i)
    {
    default:
      return null;
    case 0:
      j = 333;
    case 1:
    case 2:
    case 4:
    case 3:
    }
    while (true)
    {
      return this.mServiceHandler.obtainMessage(j, i, 0, localBluetoothDevice);
      if (localBluetoothDevice == null)
      {
        Log.w("DockService", "device is null");
        return null;
      }
      if ("com.android.settings.bluetooth.action.DOCK_SHOW_UI".equals(paramIntent.getAction()))
      {
        if (localBluetoothDevice == null)
        {
          Log.w("DockService", "device is null");
          return null;
        }
        j = 111;
      }
      else
      {
        j = 222;
      }
    }
  }

  private void processMessage(Message paramMessage)
  {
    int i;
    while (true)
    {
      int j;
      int k;
      BluetoothDevice localBluetoothDevice;
      try
      {
        i = paramMessage.what;
        j = paramMessage.arg1;
        k = paramMessage.arg2;
        Object localObject2 = paramMessage.obj;
        localBluetoothDevice = null;
        if (localObject2 == null)
          break;
        localBluetoothDevice = (BluetoothDevice)paramMessage.obj;
        break;
        if ((this.mDialog == null) && (this.mPendingDevice == null) && (i != 333) && (!bool1))
          DockEventReceiver.finishStartingService(this, k);
        return;
        bool1 = false;
        if (localBluetoothDevice == null)
          continue;
        createDialog(localBluetoothDevice, j, k);
        bool1 = false;
        continue;
      }
      finally
      {
      }
      bool1 = msgTypeDocked(localBluetoothDevice, j, k);
      continue;
      bool1 = msgTypeUndockedPermanent(localBluetoothDevice, k);
      continue;
      msgTypeUndockedTemporary(localBluetoothDevice, j, k);
      bool1 = false;
      continue;
      boolean bool2 = msgTypeDisableBluetooth(k);
      bool1 = bool2;
    }
    boolean bool1 = false;
    switch (i)
    {
    default:
    case 111:
    case 222:
    case 444:
    case 333:
    case 555:
    }
  }

  boolean hasOtherConnectedDevices(BluetoothDevice paramBluetoothDevice)
  {
    try
    {
      Collection localCollection = this.mDeviceManager.getCachedDevicesCopy();
      Set localSet = this.mLocalAdapter.getBondedDevices();
      boolean bool1 = false;
      if (localSet != null)
      {
        bool1 = false;
        if (localCollection != null)
        {
          boolean bool2 = localSet.isEmpty();
          bool1 = false;
          if (!bool2)
            break label56;
        }
      }
      while (true)
      {
        return bool1;
        label56: Iterator localIterator = localCollection.iterator();
        boolean bool4;
        do
        {
          CachedBluetoothDevice localCachedBluetoothDevice;
          BluetoothDevice localBluetoothDevice;
          do
          {
            boolean bool3 = localIterator.hasNext();
            bool1 = false;
            if (!bool3)
              break;
            localCachedBluetoothDevice = (CachedBluetoothDevice)localIterator.next();
            localBluetoothDevice = localCachedBluetoothDevice.getDevice();
          }
          while ((localBluetoothDevice.equals(paramBluetoothDevice)) || (!localSet.contains(localBluetoothDevice)));
          bool4 = localCachedBluetoothDevice.isConnected();
        }
        while (!bool4);
        bool1 = true;
      }
    }
    finally
    {
    }
  }

  public IBinder onBind(Intent paramIntent)
  {
    return null;
  }

  public void onCreate()
  {
    LocalBluetoothManager localLocalBluetoothManager = LocalBluetoothManager.getInstance(this);
    if (localLocalBluetoothManager == null)
    {
      Log.e("DockService", "Can't get LocalBluetoothManager: exiting");
      return;
    }
    this.mLocalAdapter = localLocalBluetoothManager.getBluetoothAdapter();
    this.mDeviceManager = localLocalBluetoothManager.getCachedDeviceManager();
    this.mProfileManager = localLocalBluetoothManager.getProfileManager();
    if (this.mProfileManager == null)
    {
      Log.e("DockService", "Can't get LocalBluetoothProfileManager: exiting");
      return;
    }
    HandlerThread localHandlerThread = new HandlerThread("DockService");
    localHandlerThread.start();
    this.mServiceLooper = localHandlerThread.getLooper();
    this.mServiceHandler = new ServiceHandler(this.mServiceLooper, null);
  }

  public void onDestroy()
  {
    this.mRunnable = null;
    if (this.mDialog != null)
    {
      this.mDialog.dismiss();
      this.mDialog = null;
    }
    if (this.mProfileManager != null)
      this.mProfileManager.removeServiceListener(this);
    if (this.mServiceLooper != null)
      this.mServiceLooper.quit();
    this.mLocalAdapter = null;
    this.mDeviceManager = null;
    this.mProfileManager = null;
    this.mServiceLooper = null;
    this.mServiceHandler = null;
  }

  public void onServiceConnected()
  {
    try
    {
      if (this.mRunnable != null)
      {
        this.mRunnable.run();
        this.mRunnable = null;
        this.mProfileManager.removeServiceListener(this);
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }

  public void onServiceDisconnected()
  {
  }

  public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    if (paramIntent == null)
      DockEventReceiver.finishStartingService(this, paramInt2);
    SharedPreferences localSharedPreferences;
    BluetoothDevice localBluetoothDevice1;
    int i;
    do
    {
      BluetoothDevice localBluetoothDevice2;
      int j;
      do
      {
        return 2;
        if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(paramIntent.getAction()))
        {
          handleBtStateChange(paramIntent, paramInt2);
          return 2;
        }
        localSharedPreferences = getPrefs();
        if (!"android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED".equals(paramIntent.getAction()))
          break;
        localBluetoothDevice2 = (BluetoothDevice)paramIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        j = localSharedPreferences.getInt("connect_retry_count", 0);
      }
      while (j >= 6);
      localSharedPreferences.edit().putInt("connect_retry_count", j + 1).apply();
      handleUnexpectedDisconnect(localBluetoothDevice2, this.mProfileManager.getHeadsetProfile(), paramInt2);
      return 2;
      if (!"android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED".equals(paramIntent.getAction()))
        break;
      localBluetoothDevice1 = (BluetoothDevice)paramIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
      i = localSharedPreferences.getInt("connect_retry_count", 0);
    }
    while (i >= 6);
    localSharedPreferences.edit().putInt("connect_retry_count", i + 1).apply();
    handleUnexpectedDisconnect(localBluetoothDevice1, this.mProfileManager.getA2dpProfile(), paramInt2);
    return 2;
    Message localMessage = parseIntent(paramIntent);
    if (localMessage == null)
    {
      DockEventReceiver.finishStartingService(this, paramInt2);
      return 2;
    }
    if (localMessage.what == 222)
      localSharedPreferences.edit().remove("connect_retry_count").apply();
    localMessage.arg2 = paramInt2;
    processMessage(localMessage);
    return 2;
  }

  private final class ServiceHandler extends Handler
  {
    private ServiceHandler(Looper arg2)
    {
      super();
    }

    public void handleMessage(Message paramMessage)
    {
      DockService.this.processMessage(paramMessage);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.DockService
 * JD-Core Version:    0.6.2
 */