package com.android.settings.bluetooth;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

final class Utils
{
  public static int getConnectionStateSummary(int paramInt)
  {
    switch (paramInt)
    {
    default:
      return 0;
    case 2:
      return 2131427441;
    case 1:
      return 2131427448;
    case 0:
      return 2131427446;
    case 3:
    }
    return 2131427447;
  }

  static AlertDialog showDisconnectDialog(Context paramContext, AlertDialog paramAlertDialog, DialogInterface.OnClickListener paramOnClickListener, CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    if (paramAlertDialog == null)
      paramAlertDialog = new AlertDialog.Builder(paramContext).setPositiveButton(17039370, paramOnClickListener).setNegativeButton(17039360, null).create();
    while (true)
    {
      paramAlertDialog.setTitle(paramCharSequence1);
      paramAlertDialog.setMessage(paramCharSequence2);
      paramAlertDialog.show();
      return paramAlertDialog;
      if (paramAlertDialog.isShowing())
        paramAlertDialog.dismiss();
      paramAlertDialog.setButton(-1, paramContext.getText(17039370), paramOnClickListener);
    }
  }

  static void showError(Context paramContext, String paramString, int paramInt)
  {
    String str = paramContext.getString(paramInt, new Object[] { paramString });
    LocalBluetoothManager localLocalBluetoothManager = LocalBluetoothManager.getInstance(paramContext);
    Context localContext = localLocalBluetoothManager.getForegroundActivity();
    if (localLocalBluetoothManager.isForegroundActivity())
    {
      new AlertDialog.Builder(localContext).setIconAttribute(16843605).setTitle(2131427718).setMessage(str).setPositiveButton(17039370, null).show();
      return;
    }
    Toast.makeText(paramContext, str, 0).show();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.Utils
 * JD-Core Version:    0.6.2
 */