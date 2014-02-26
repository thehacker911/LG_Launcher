package com.android.settings.inputmethod;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.view.Window;

public class UserDictionaryAddWordActivity extends Activity
{
  private UserDictionaryAddWordContents mContents;

  private void reportBackToCaller(int paramInt, Bundle paramBundle)
  {
    Object localObject = getIntent().getExtras().get("listener");
    if (!(localObject instanceof Messenger))
      return;
    Messenger localMessenger = (Messenger)localObject;
    Message localMessage = Message.obtain();
    localMessage.obj = paramBundle;
    localMessage.what = paramInt;
    try
    {
      localMessenger.send(localMessage);
      return;
    }
    catch (RemoteException localRemoteException)
    {
    }
  }

  public void onClickCancel(View paramView)
  {
    reportBackToCaller(1, null);
    finish();
  }

  public void onClickConfirm(View paramView)
  {
    Bundle localBundle = new Bundle();
    reportBackToCaller(this.mContents.apply(this, localBundle), localBundle);
    finish();
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968723);
    Intent localIntent = getIntent();
    String str = localIntent.getAction();
    if ("com.android.settings.USER_DICTIONARY_EDIT".equals(str));
    for (int i = 0; ; i = 1)
    {
      Bundle localBundle = localIntent.getExtras();
      localBundle.putInt("mode", i);
      if (paramBundle != null)
        localBundle.putAll(paramBundle);
      this.mContents = new UserDictionaryAddWordContents(getWindow().getDecorView(), localBundle);
      return;
      if (!"com.android.settings.USER_DICTIONARY_INSERT".equals(str))
        break;
    }
    throw new RuntimeException("Unsupported action: " + str);
  }

  public void onSaveInstanceState(Bundle paramBundle)
  {
    this.mContents.saveStateIntoBundle(paramBundle);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.inputmethod.UserDictionaryAddWordActivity
 * JD-Core Version:    0.6.2
 */