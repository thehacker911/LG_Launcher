package com.android.settings.bluetooth;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public final class BluetoothVisibilityTimeoutFragment extends DialogFragment
  implements DialogInterface.OnClickListener
{
  private final BluetoothDiscoverableEnabler mDiscoverableEnabler = LocalBluetoothManager.getInstance(getActivity()).getDiscoverableEnabler();

  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    this.mDiscoverableEnabler.setDiscoverableTimeout(paramInt);
    dismiss();
  }

  public Dialog onCreateDialog(Bundle paramBundle)
  {
    return new AlertDialog.Builder(getActivity()).setTitle(2131427426).setSingleChoiceItems(2131165208, this.mDiscoverableEnabler.getDiscoverableTimeoutIndex(), this).setNegativeButton(17039360, null).create();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.BluetoothVisibilityTimeoutFragment
 * JD-Core Version:    0.6.2
 */