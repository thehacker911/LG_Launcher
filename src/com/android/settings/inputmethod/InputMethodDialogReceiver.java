package com.android.settings.inputmethod;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.InputMethodManager;

public class InputMethodDialogReceiver extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if ("android.settings.SHOW_INPUT_METHOD_PICKER".equals(paramIntent.getAction()))
      ((InputMethodManager)paramContext.getSystemService("input_method")).showInputMethodPicker();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.inputmethod.InputMethodDialogReceiver
 * JD-Core Version:    0.6.2
 */