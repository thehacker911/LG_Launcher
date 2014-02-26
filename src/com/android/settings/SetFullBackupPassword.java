package com.android.settings;

import android.app.Activity;
import android.app.backup.IBackupManager;
import android.app.backup.IBackupManager.Stub;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SetFullBackupPassword extends Activity
{
  IBackupManager mBackupManager;
  View.OnClickListener mButtonListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (paramAnonymousView == SetFullBackupPassword.this.mSet)
      {
        String str1 = SetFullBackupPassword.this.mCurrentPw.getText().toString();
        String str2 = SetFullBackupPassword.this.mNewPw.getText().toString();
        if (!str2.equals(SetFullBackupPassword.this.mConfirmNewPw.getText().toString()))
        {
          Log.i("SetFullBackupPassword", "password mismatch");
          Toast.makeText(SetFullBackupPassword.this, 2131428921, 1).show();
          return;
        }
        if (SetFullBackupPassword.this.setBackupPassword(str1, str2))
        {
          Log.i("SetFullBackupPassword", "password set successfully");
          Toast.makeText(SetFullBackupPassword.this, 2131428920, 1).show();
          SetFullBackupPassword.this.finish();
          return;
        }
        Log.i("SetFullBackupPassword", "failure; password mismatch?");
        Toast.makeText(SetFullBackupPassword.this, 2131428922, 1).show();
        return;
      }
      if (paramAnonymousView == SetFullBackupPassword.this.mCancel)
      {
        SetFullBackupPassword.this.finish();
        return;
      }
      Log.w("SetFullBackupPassword", "Click on unknown view");
    }
  };
  Button mCancel;
  TextView mConfirmNewPw;
  TextView mCurrentPw;
  TextView mNewPw;
  Button mSet;

  private boolean setBackupPassword(String paramString1, String paramString2)
  {
    try
    {
      boolean bool = this.mBackupManager.setBackupPassword(paramString1, paramString2);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("SetFullBackupPassword", "Unable to communicate with backup manager");
    }
    return false;
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mBackupManager = IBackupManager.Stub.asInterface(ServiceManager.getService("backup"));
    setContentView(2130968706);
    this.mCurrentPw = ((TextView)findViewById(2131231039));
    this.mNewPw = ((TextView)findViewById(2131231041));
    this.mConfirmNewPw = ((TextView)findViewById(2131231043));
    this.mCancel = ((Button)findViewById(2131231044));
    this.mSet = ((Button)findViewById(2131231045));
    this.mCancel.setOnClickListener(this.mButtonListener);
    this.mSet.setOnClickListener(this.mButtonListener);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.SetFullBackupPassword
 * JD-Core Version:    0.6.2
 */