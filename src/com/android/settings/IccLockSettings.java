package com.android.settings;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.EditText;
import android.widget.Toast;
import com.android.internal.telephony.IccCard;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;

public class IccLockSettings extends PreferenceActivity
  implements EditPinPreference.OnPinEnteredListener
{
  private int mDialogState = 0;
  private String mError;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      boolean bool = true;
      AsyncResult localAsyncResult = (AsyncResult)paramAnonymousMessage.obj;
      switch (paramAnonymousMessage.what)
      {
      default:
        return;
      case 100:
        IccLockSettings localIccLockSettings2 = IccLockSettings.this;
        if (localAsyncResult.exception == null);
        while (true)
        {
          localIccLockSettings2.iccLockChanged(bool, paramAnonymousMessage.arg1);
          return;
          bool = false;
        }
      case 101:
        IccLockSettings localIccLockSettings1 = IccLockSettings.this;
        if (localAsyncResult.exception == null);
        while (true)
        {
          localIccLockSettings1.iccPinChanged(bool, paramAnonymousMessage.arg1);
          return;
          bool = false;
        }
      case 102:
      }
      IccLockSettings.this.updatePreferences();
    }
  };
  private String mNewPin;
  private String mOldPin;
  private Phone mPhone;
  private String mPin;
  private EditPinPreference mPinDialog;
  private CheckBoxPreference mPinToggle;
  private Resources mRes;
  private final BroadcastReceiver mSimStateReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.intent.action.SIM_STATE_CHANGED".equals(paramAnonymousIntent.getAction()))
        IccLockSettings.this.mHandler.sendMessage(IccLockSettings.this.mHandler.obtainMessage(102));
    }
  };
  private boolean mToState;

  private String getPinPasswordErrorMessage(int paramInt)
  {
    String str;
    if (paramInt == 0)
      str = this.mRes.getString(2131428087);
    while (true)
    {
      Log.d("IccLockSettings", "getPinPasswordErrorMessage: attemptsRemaining=" + paramInt + " displayMessage=" + str);
      return str;
      if (paramInt > 0)
      {
        Resources localResources = this.mRes;
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = Integer.valueOf(paramInt);
        str = localResources.getQuantityString(2131623944, paramInt, arrayOfObject);
      }
      else
      {
        str = this.mRes.getString(2131428088);
      }
    }
  }

  private void iccLockChanged(boolean paramBoolean, int paramInt)
  {
    if (paramBoolean)
      this.mPinToggle.setChecked(this.mToState);
    while (true)
    {
      this.mPinToggle.setEnabled(true);
      resetDialogState();
      return;
      Toast.makeText(this, getPinPasswordErrorMessage(paramInt), 1).show();
    }
  }

  private void iccPinChanged(boolean paramBoolean, int paramInt)
  {
    if (!paramBoolean)
      Toast.makeText(this, getPinPasswordErrorMessage(paramInt), 1).show();
    while (true)
    {
      resetDialogState();
      return;
      Toast.makeText(this, this.mRes.getString(2131428083), 0).show();
    }
  }

  private boolean reasonablePin(String paramString)
  {
    return (paramString != null) && (paramString.length() >= 4) && (paramString.length() <= 8);
  }

  private void resetDialogState()
  {
    this.mError = null;
    this.mDialogState = 2;
    this.mPin = "";
    setDialogValues();
    this.mDialogState = 0;
  }

  private void setDialogValues()
  {
    this.mPinDialog.setText(this.mPin);
    String str1 = "";
    switch (this.mDialogState)
    {
    default:
    case 1:
    case 2:
    case 3:
    case 4:
    }
    while (true)
    {
      if (this.mError != null)
      {
        str1 = this.mError + "\n" + str1;
        this.mError = null;
      }
      this.mPinDialog.setDialogMessage(str1);
      return;
      str1 = this.mRes.getString(2131428073);
      EditPinPreference localEditPinPreference = this.mPinDialog;
      if (this.mToState);
      for (String str2 = this.mRes.getString(2131428074); ; str2 = this.mRes.getString(2131428075))
      {
        localEditPinPreference.setDialogTitle(str2);
        break;
      }
      str1 = this.mRes.getString(2131428076);
      this.mPinDialog.setDialogTitle(this.mRes.getString(2131428079));
      continue;
      str1 = this.mRes.getString(2131428077);
      this.mPinDialog.setDialogTitle(this.mRes.getString(2131428079));
      continue;
      str1 = this.mRes.getString(2131428078);
      this.mPinDialog.setDialogTitle(this.mRes.getString(2131428079));
    }
  }

  private void showPinDialog()
  {
    if (this.mDialogState == 0)
      return;
    setDialogValues();
    this.mPinDialog.showPinDialog();
  }

  private void tryChangeIccLockState()
  {
    Message localMessage = Message.obtain(this.mHandler, 100);
    this.mPhone.getIccCard().setIccLockEnabled(this.mToState, this.mPin, localMessage);
    this.mPinToggle.setEnabled(false);
  }

  private void tryChangePin()
  {
    Message localMessage = Message.obtain(this.mHandler, 101);
    this.mPhone.getIccCard().changeIccLockPassword(this.mOldPin, this.mNewPin, localMessage);
  }

  private void updatePreferences()
  {
    this.mPinToggle.setChecked(this.mPhone.getIccCard().getIccLockEnabled());
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (Utils.isMonkeyRunning())
    {
      finish();
      return;
    }
    addPreferencesFromResource(2131034157);
    this.mPinDialog = ((EditPinPreference)findPreference("sim_pin"));
    this.mPinToggle = ((CheckBoxPreference)findPreference("sim_toggle"));
    if ((paramBundle != null) && (paramBundle.containsKey("dialogState")))
    {
      this.mDialogState = paramBundle.getInt("dialogState");
      this.mPin = paramBundle.getString("dialogPin");
      this.mError = paramBundle.getString("dialogError");
      this.mToState = paramBundle.getBoolean("enableState");
      switch (this.mDialogState)
      {
      default:
      case 3:
      case 4:
      }
    }
    while (true)
    {
      this.mPinDialog.setOnPinEnteredListener(this);
      getPreferenceScreen().setPersistent(false);
      this.mPhone = PhoneFactory.getDefaultPhone();
      this.mRes = getResources();
      updatePreferences();
      return;
      this.mOldPin = paramBundle.getString("oldPinCode");
      continue;
      this.mOldPin = paramBundle.getString("oldPinCode");
      this.mNewPin = paramBundle.getString("newPinCode");
    }
  }

  protected void onPause()
  {
    super.onPause();
    unregisterReceiver(this.mSimStateReceiver);
  }

  public void onPinEntered(EditPinPreference paramEditPinPreference, boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      resetDialogState();
      return;
    }
    this.mPin = paramEditPinPreference.getText();
    if (!reasonablePin(this.mPin))
    {
      this.mError = this.mRes.getString(2131428080);
      showPinDialog();
      return;
    }
    switch (this.mDialogState)
    {
    default:
      return;
    case 1:
      tryChangeIccLockState();
      return;
    case 2:
      this.mOldPin = this.mPin;
      this.mDialogState = 3;
      this.mError = null;
      this.mPin = null;
      showPinDialog();
      return;
    case 3:
      this.mNewPin = this.mPin;
      this.mDialogState = 4;
      this.mPin = null;
      showPinDialog();
      return;
    case 4:
    }
    if (!this.mPin.equals(this.mNewPin))
    {
      this.mError = this.mRes.getString(2131428081);
      this.mDialogState = 3;
      this.mPin = null;
      showPinDialog();
      return;
    }
    this.mError = null;
    tryChangePin();
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    if (paramPreference == this.mPinToggle)
    {
      this.mToState = this.mPinToggle.isChecked();
      localCheckBoxPreference = this.mPinToggle;
      bool1 = this.mToState;
      bool2 = false;
      if (!bool1)
        bool2 = true;
      localCheckBoxPreference.setChecked(bool2);
      this.mDialogState = 1;
      showPinDialog();
    }
    while (paramPreference != this.mPinDialog)
    {
      CheckBoxPreference localCheckBoxPreference;
      boolean bool1;
      boolean bool2;
      return true;
    }
    this.mDialogState = 2;
    return false;
  }

  protected void onResume()
  {
    super.onResume();
    IntentFilter localIntentFilter = new IntentFilter("android.intent.action.SIM_STATE_CHANGED");
    registerReceiver(this.mSimStateReceiver, localIntentFilter);
    if (this.mDialogState != 0)
    {
      showPinDialog();
      return;
    }
    resetDialogState();
  }

  protected void onSaveInstanceState(Bundle paramBundle)
  {
    if (this.mPinDialog.isDialogOpen())
    {
      paramBundle.putInt("dialogState", this.mDialogState);
      paramBundle.putString("dialogPin", this.mPinDialog.getEditText().getText().toString());
      paramBundle.putString("dialogError", this.mError);
      paramBundle.putBoolean("enableState", this.mToState);
      switch (this.mDialogState)
      {
      default:
        return;
      case 3:
        paramBundle.putString("oldPinCode", this.mOldPin);
        return;
      case 4:
      }
      paramBundle.putString("oldPinCode", this.mOldPin);
      paramBundle.putString("newPinCode", this.mNewPin);
      return;
    }
    super.onSaveInstanceState(paramBundle);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.IccLockSettings
 * JD-Core Version:    0.6.2
 */