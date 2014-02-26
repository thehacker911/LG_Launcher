package com.android.settings;

import android.app.Activity;
import android.app.StatusBarManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.storage.IMountService;
import android.os.storage.IMountService.Stub;
import android.provider.Settings.Global;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.android.internal.telephony.ITelephony;
import com.android.internal.telephony.ITelephony.Stub;
import java.util.Iterator;
import java.util.List;

public class CryptKeeper extends Activity
  implements TextWatcher, View.OnKeyListener, View.OnTouchListener, TextView.OnEditorActionListener
{
  private AudioManager mAudioManager;
  private int mCooldown;
  private boolean mEncryptionGoneBad;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default:
        return;
      case 1:
        CryptKeeper.this.updateProgress();
        return;
      case 2:
        CryptKeeper.this.cooldown();
        return;
      case 3:
      }
      CryptKeeper.this.notifyUser();
    }
  };
  private boolean mIgnoreBack = false;
  private int mNotificationCountdown = 0;
  private EditText mPasswordEntry;
  private StatusBarManager mStatusBar;
  private boolean mValidationComplete;
  private boolean mValidationRequested;
  PowerManager.WakeLock mWakeLock;

  private void cooldown()
  {
    TextView localTextView = (TextView)findViewById(2131230740);
    if (this.mCooldown <= 0)
    {
      this.mPasswordEntry.setEnabled(true);
      setBackFunctionality(true);
      localTextView.setText(2131429012);
      return;
    }
    CharSequence localCharSequence = getText(2131427637);
    CharSequence[] arrayOfCharSequence = new CharSequence[1];
    arrayOfCharSequence[0] = Integer.toString(this.mCooldown);
    localTextView.setText(TextUtils.expandTemplate(localCharSequence, arrayOfCharSequence));
    this.mCooldown = (-1 + this.mCooldown);
    this.mHandler.removeMessages(2);
    this.mHandler.sendEmptyMessageDelayed(2, 1000L);
  }

  private void delayAudioNotification()
  {
    this.mNotificationCountdown = 20;
  }

  private void encryptionProgressInit()
  {
    Log.d("CryptKeeper", "Encryption progress screen initializing.");
    if (this.mWakeLock == null)
    {
      Log.d("CryptKeeper", "Acquiring wakelock.");
      this.mWakeLock = ((PowerManager)getSystemService("power")).newWakeLock(26, "CryptKeeper");
      this.mWakeLock.acquire();
    }
    ((ProgressBar)findViewById(2131230784)).setIndeterminate(true);
    setBackFunctionality(false);
    updateProgress();
  }

  private IMountService getMountService()
  {
    IBinder localIBinder = ServiceManager.getService("mount");
    if (localIBinder != null)
      return IMountService.Stub.asInterface(localIBinder);
    return null;
  }

  private boolean hasMultipleEnabledIMEsOrSubtypes(InputMethodManager paramInputMethodManager, boolean paramBoolean)
  {
    List localList1 = paramInputMethodManager.getEnabledInputMethodList();
    int i = 0;
    Iterator localIterator1 = localList1.iterator();
    while (localIterator1.hasNext())
    {
      InputMethodInfo localInputMethodInfo = (InputMethodInfo)localIterator1.next();
      if (i > 1)
        return true;
      List localList2 = paramInputMethodManager.getEnabledInputMethodSubtypeList(localInputMethodInfo, true);
      if (localList2.isEmpty())
      {
        i++;
      }
      else
      {
        int k = 0;
        Iterator localIterator2 = localList2.iterator();
        while (localIterator2.hasNext())
          if (((InputMethodSubtype)localIterator2.next()).isAuxiliary())
            k++;
        if ((localList2.size() - k > 0) || ((paramBoolean) && (k > 1)))
          i++;
      }
    }
    boolean bool;
    if (i <= 1)
    {
      int j = paramInputMethodManager.getEnabledInputMethodSubtypeList(null, false).size();
      bool = false;
      if (j <= 1);
    }
    else
    {
      bool = true;
    }
    return bool;
  }

  private boolean isDebugView()
  {
    return getIntent().hasExtra("com.android.settings.CryptKeeper.DEBUG_FORCE_VIEW");
  }

  private boolean isDebugView(String paramString)
  {
    return paramString.equals(getIntent().getStringExtra("com.android.settings.CryptKeeper.DEBUG_FORCE_VIEW"));
  }

  private boolean isEmergencyCallCapable()
  {
    return getResources().getBoolean(17891385);
  }

  private void launchEmergencyDialer()
  {
    Intent localIntent = new Intent("com.android.phone.EmergencyDialer.DIAL");
    localIntent.setFlags(276824064);
    startActivity(localIntent);
  }

  private void notifyUser()
  {
    if (this.mNotificationCountdown > 0)
      this.mNotificationCountdown = (-1 + this.mNotificationCountdown);
    while (true)
    {
      this.mHandler.removeMessages(3);
      this.mHandler.sendEmptyMessageDelayed(3, 5000L);
      return;
      if (this.mAudioManager != null)
        try
        {
          this.mAudioManager.playSoundEffect(5, 100.0F);
        }
        catch (Exception localException)
        {
          Log.w("CryptKeeper", "notifyUser: Exception while playing sound: " + localException);
        }
    }
  }

  private void passwordEntryInit()
  {
    this.mPasswordEntry = ((EditText)findViewById(2131230781));
    this.mPasswordEntry.setOnEditorActionListener(this);
    this.mPasswordEntry.requestFocus();
    this.mPasswordEntry.setOnKeyListener(this);
    this.mPasswordEntry.setOnTouchListener(this);
    this.mPasswordEntry.addTextChangedListener(this);
    if (!((TelephonyManager)getSystemService("phone")).isVoiceCapable())
    {
      View localView2 = findViewById(2131230778);
      if (localView2 != null)
      {
        Log.d("CryptKeeper", "Removing the emergency Call button");
        localView2.setVisibility(8);
      }
    }
    View localView1 = findViewById(2131230782);
    final InputMethodManager localInputMethodManager = (InputMethodManager)getSystemService("input_method");
    if ((localView1 != null) && (hasMultipleEnabledIMEsOrSubtypes(localInputMethodManager, false)))
    {
      localView1.setVisibility(0);
      localView1.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          localInputMethodManager.showInputMethodPicker();
        }
      });
    }
    if (this.mWakeLock == null)
    {
      Log.d("CryptKeeper", "Acquiring wakelock.");
      PowerManager localPowerManager = (PowerManager)getSystemService("power");
      if (localPowerManager != null)
      {
        this.mWakeLock = localPowerManager.newWakeLock(26, "CryptKeeper");
        this.mWakeLock.acquire();
      }
    }
    this.mHandler.postDelayed(new Runnable()
    {
      public void run()
      {
        localInputMethodManager.showSoftInputUnchecked(0, null);
      }
    }
    , 0L);
    updateEmergencyCallButtonState();
    this.mHandler.removeMessages(3);
    this.mHandler.sendEmptyMessageDelayed(3, 120000L);
    getWindow().addFlags(4194304);
  }

  private void resumeCall()
  {
    ITelephony localITelephony = ITelephony.Stub.asInterface(ServiceManager.checkService("phone"));
    if (localITelephony != null);
    try
    {
      localITelephony.showCallScreen();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("CryptKeeper", "Error calling ITelephony service: " + localRemoteException);
    }
  }

  private final void setAirplaneModeIfNecessary()
  {
    if (TelephonyManager.getDefault().getLteOnCdmaMode() == 1);
    for (int i = 1; ; i = 0)
    {
      if (i == 0)
      {
        Log.d("CryptKeeper", "Going into airplane mode.");
        Settings.Global.putInt(getContentResolver(), "airplane_mode_on", 1);
        Intent localIntent = new Intent("android.intent.action.AIRPLANE_MODE");
        localIntent.putExtra("state", true);
        sendBroadcastAsUser(localIntent, UserHandle.ALL);
      }
      return;
    }
  }

  private final void setBackFunctionality(boolean paramBoolean)
  {
    if (!paramBoolean);
    for (boolean bool = true; ; bool = false)
    {
      this.mIgnoreBack = bool;
      if (!paramBoolean)
        break;
      this.mStatusBar.disable(53936128);
      return;
    }
    this.mStatusBar.disable(58130432);
  }

  private void setupUi()
  {
    if ((this.mEncryptionGoneBad) || (isDebugView("error")))
    {
      setContentView(2130968607);
      showFactoryReset();
    }
    do
    {
      return;
      if ((!"".equals(SystemProperties.get("vold.encrypt_progress"))) || (isDebugView("progress")))
      {
        setContentView(2130968607);
        encryptionProgressInit();
        return;
      }
      if ((this.mValidationComplete) || (isDebugView("password")))
      {
        setContentView(2130968605);
        passwordEntryInit();
        return;
      }
    }
    while (this.mValidationRequested);
    new ValidationTask(null).execute((Void[])null);
    this.mValidationRequested = true;
  }

  private void showFactoryReset()
  {
    findViewById(2131230779).setVisibility(8);
    Button localButton = (Button)findViewById(2131230785);
    localButton.setVisibility(0);
    localButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        CryptKeeper.this.sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
      }
    });
    ((TextView)findViewById(2131230756)).setText(2131427639);
    ((TextView)findViewById(2131230740)).setText(2131427640);
    View localView = findViewById(2131230786);
    if (localView != null)
      localView.setVisibility(0);
  }

  private void takeEmergencyCallAction()
  {
    if (TelephonyManager.getDefault().getCallState() == 2)
    {
      resumeCall();
      return;
    }
    launchEmergencyDialer();
  }

  private void updateEmergencyCallButtonState()
  {
    Button localButton = (Button)findViewById(2131230778);
    if (localButton == null)
      return;
    int i;
    if (isEmergencyCallCapable())
    {
      localButton.setVisibility(0);
      localButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          CryptKeeper.this.takeEmergencyCallAction();
        }
      });
      if (TelephonyManager.getDefault().getCallState() != 2)
        break label77;
      i = 2131429135;
      localButton.setCompoundDrawablesWithIntrinsicBounds(2130837690, 0, 0, 0);
    }
    while (true)
    {
      localButton.setText(i);
      return;
      localButton.setVisibility(8);
      return;
      label77: i = 2131429134;
      localButton.setCompoundDrawablesWithIntrinsicBounds(2130837583, 0, 0, 0);
    }
  }

  private void updateProgress()
  {
    String str = SystemProperties.get("vold.encrypt_progress");
    if ("error_partially_encrypted".equals(str))
    {
      showFactoryReset();
      return;
    }
    try
    {
      boolean bool = isDebugView();
      if (bool);
      int j;
      for (i = 50; ; i = j)
      {
        CharSequence localCharSequence = getText(2131427636);
        Log.v("CryptKeeper", "Encryption progress: " + i);
        TextView localTextView = (TextView)findViewById(2131230740);
        if (localTextView != null)
        {
          CharSequence[] arrayOfCharSequence = new CharSequence[1];
          arrayOfCharSequence[0] = Integer.toString(i);
          localTextView.setText(TextUtils.expandTemplate(localCharSequence, arrayOfCharSequence));
        }
        this.mHandler.removeMessages(1);
        this.mHandler.sendEmptyMessageDelayed(1, 5000L);
        return;
        j = Integer.parseInt(str);
      }
    }
    catch (Exception localException)
    {
      while (true)
      {
        Log.w("CryptKeeper", "Error parsing progress: " + localException.toString());
        int i = 0;
      }
    }
  }

  public void afterTextChanged(Editable paramEditable)
  {
  }

  public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
  {
  }

  public void onBackPressed()
  {
    if (this.mIgnoreBack)
      return;
    super.onBackPressed();
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    String str = SystemProperties.get("vold.decrypt");
    if ((!isDebugView()) && (("".equals(str)) || ("trigger_restart_framework".equals(str))))
    {
      getPackageManager().setComponentEnabledSetting(new ComponentName(this, CryptKeeper.class), 2, 1);
      finish();
    }
    Object localObject;
    do
    {
      return;
      this.mStatusBar = ((StatusBarManager)getSystemService("statusbar"));
      this.mStatusBar.disable(53936128);
      setAirplaneModeIfNecessary();
      this.mAudioManager = ((AudioManager)getSystemService("audio"));
      localObject = getLastNonConfigurationInstance();
    }
    while (!(localObject instanceof NonConfigurationInstanceState));
    this.mWakeLock = ((NonConfigurationInstanceState)localObject).wakelock;
    Log.d("CryptKeeper", "Restoring wakelock from NonConfigurationInstanceState");
  }

  public void onDestroy()
  {
    super.onDestroy();
    if (this.mWakeLock != null)
    {
      Log.d("CryptKeeper", "Releasing and destroying wakelock");
      this.mWakeLock.release();
      this.mWakeLock = null;
    }
  }

  public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
  {
    boolean bool;
    String str;
    if (paramInt != 0)
    {
      bool = false;
      if (paramInt != 6);
    }
    else
    {
      str = paramTextView.getText().toString();
      if (!TextUtils.isEmpty(str))
        break label36;
      bool = true;
    }
    return bool;
    label36: paramTextView.setText(null);
    this.mPasswordEntry.setEnabled(false);
    setBackFunctionality(false);
    Log.d("CryptKeeper", "Attempting to send command to decrypt");
    new DecryptTask(null).execute(new String[] { str });
    return true;
  }

  public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
  {
    delayAudioNotification();
    return false;
  }

  public Object onRetainNonConfigurationInstance()
  {
    NonConfigurationInstanceState localNonConfigurationInstanceState = new NonConfigurationInstanceState(this.mWakeLock);
    Log.d("CryptKeeper", "Handing wakelock off to NonConfigurationInstanceState");
    this.mWakeLock = null;
    return localNonConfigurationInstanceState;
  }

  public void onStart()
  {
    super.onStart();
    setupUi();
  }

  public void onStop()
  {
    super.onStop();
    this.mHandler.removeMessages(2);
    this.mHandler.removeMessages(1);
    this.mHandler.removeMessages(3);
  }

  public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
  {
    delayAudioNotification();
  }

  public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
  {
    delayAudioNotification();
    return false;
  }

  private class DecryptTask extends AsyncTask<String, Void, Integer>
  {
    private DecryptTask()
    {
    }

    protected Integer doInBackground(String[] paramArrayOfString)
    {
      IMountService localIMountService = CryptKeeper.this.getMountService();
      try
      {
        Integer localInteger = Integer.valueOf(localIMountService.decryptStorage(paramArrayOfString[0]));
        return localInteger;
      }
      catch (Exception localException)
      {
        Log.e("CryptKeeper", "Error while decrypting...", localException);
      }
      return Integer.valueOf(-1);
    }

    protected void onPostExecute(Integer paramInteger)
    {
      if (paramInteger.intValue() == 0)
      {
        Intent localIntent = new Intent(CryptKeeper.this, CryptKeeper.FadeToBlack.class);
        CryptKeeper.this.finish();
        CryptKeeper.this.startActivity(localIntent);
        return;
      }
      if (paramInteger.intValue() == 30)
      {
        CryptKeeper.this.sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
        return;
      }
      if (paramInteger.intValue() % 10 == 0)
      {
        CryptKeeper.access$102(CryptKeeper.this, 30);
        CryptKeeper.this.cooldown();
        return;
      }
      ((TextView)CryptKeeper.this.findViewById(2131230740)).setText(2131429013);
      CryptKeeper.this.mPasswordEntry.setEnabled(true);
    }
  }

  public static class FadeToBlack extends Activity
  {
    public void onBackPressed()
    {
    }

    public void onCreate(Bundle paramBundle)
    {
      super.onCreate(paramBundle);
      setContentView(2130968603);
    }
  }

  private static class NonConfigurationInstanceState
  {
    final PowerManager.WakeLock wakelock;

    NonConfigurationInstanceState(PowerManager.WakeLock paramWakeLock)
    {
      this.wakelock = paramWakeLock;
    }
  }

  private class ValidationTask extends AsyncTask<Void, Void, Boolean>
  {
    private ValidationTask()
    {
    }

    protected Boolean doInBackground(Void[] paramArrayOfVoid)
    {
      IMountService localIMountService = CryptKeeper.this.getMountService();
      while (true)
      {
        int i;
        boolean bool;
        try
        {
          Log.d("CryptKeeper", "Validating encryption state.");
          i = localIMountService.getEncryptionState();
          if (i == 1)
          {
            Log.w("CryptKeeper", "Unexpectedly in CryptKeeper even though there is no encryption.");
            return Boolean.valueOf(true);
            Boolean localBoolean = Boolean.valueOf(bool);
            return localBoolean;
            bool = false;
            continue;
          }
        }
        catch (RemoteException localRemoteException)
        {
          Log.w("CryptKeeper", "Unable to get encryption state properly");
          return Boolean.valueOf(true);
        }
        if (i == 0)
          bool = true;
      }
    }

    protected void onPostExecute(Boolean paramBoolean)
    {
      CryptKeeper.access$402(CryptKeeper.this, true);
      if (Boolean.FALSE.equals(paramBoolean))
      {
        Log.w("CryptKeeper", "Incomplete, or corrupted encryption detected. Prompting user to wipe.");
        CryptKeeper.access$502(CryptKeeper.this, true);
      }
      while (true)
      {
        CryptKeeper.this.setupUi();
        return;
        Log.d("CryptKeeper", "Encryption state validated. Proceeding to configure UI");
      }
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.CryptKeeper
 * JD-Core Version:    0.6.2
 */