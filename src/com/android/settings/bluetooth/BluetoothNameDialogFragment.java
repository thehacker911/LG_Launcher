package com.android.settings.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public final class BluetoothNameDialogFragment extends DialogFragment
  implements TextWatcher
{
  private AlertDialog mAlertDialog;
  private boolean mDeviceNameEdited;
  private boolean mDeviceNameUpdated;
  EditText mDeviceNameView;
  final LocalBluetoothAdapter mLocalAdapter = LocalBluetoothManager.getInstance(getActivity()).getBluetoothAdapter();
  private Button mOkButton;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      String str = paramAnonymousIntent.getAction();
      if (str.equals("android.bluetooth.adapter.action.LOCAL_NAME_CHANGED"))
        BluetoothNameDialogFragment.this.updateDeviceName();
      while ((!str.equals("android.bluetooth.adapter.action.STATE_CHANGED")) || (paramAnonymousIntent.getIntExtra("android.bluetooth.adapter.extra.STATE", -2147483648) != 12))
        return;
      BluetoothNameDialogFragment.this.updateDeviceName();
    }
  };

  private View createDialogView(String paramString)
  {
    View localView = ((LayoutInflater)getActivity().getSystemService("layout_inflater")).inflate(2130968625, null);
    this.mDeviceNameView = ((EditText)localView.findViewById(2131230840));
    EditText localEditText = this.mDeviceNameView;
    InputFilter[] arrayOfInputFilter = new InputFilter[1];
    arrayOfInputFilter[0] = new Utf8ByteLengthFilter(248);
    localEditText.setFilters(arrayOfInputFilter);
    this.mDeviceNameView.setText(paramString);
    this.mDeviceNameView.addTextChangedListener(this);
    this.mDeviceNameView.setOnEditorActionListener(new TextView.OnEditorActionListener()
    {
      public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
      {
        if (paramAnonymousInt == 6)
        {
          BluetoothNameDialogFragment.this.setDeviceName(paramAnonymousTextView.getText().toString());
          BluetoothNameDialogFragment.this.mAlertDialog.dismiss();
          return true;
        }
        return false;
      }
    });
    return localView;
  }

  private void setDeviceName(String paramString)
  {
    Log.d("BluetoothNameDialogFragment", "Setting device name to " + paramString);
    this.mLocalAdapter.setName(paramString);
  }

  public void afterTextChanged(Editable paramEditable)
  {
    boolean bool = true;
    if (this.mDeviceNameUpdated)
    {
      this.mDeviceNameUpdated = false;
      this.mOkButton.setEnabled(false);
    }
    do
    {
      return;
      this.mDeviceNameEdited = bool;
    }
    while (this.mOkButton == null);
    Button localButton = this.mOkButton;
    if (paramEditable.length() != 0);
    while (true)
    {
      localButton.setEnabled(bool);
      return;
      bool = false;
    }
  }

  public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
  {
  }

  public Dialog onCreateDialog(Bundle paramBundle)
  {
    String str = this.mLocalAdapter.getName();
    if (paramBundle != null)
    {
      str = paramBundle.getString("device_name", str);
      this.mDeviceNameEdited = paramBundle.getBoolean("device_name_edited", false);
    }
    this.mAlertDialog = new AlertDialog.Builder(getActivity()).setIcon(17301659).setTitle(2131427435).setView(createDialogView(str)).setPositiveButton(2131427436, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        String str = BluetoothNameDialogFragment.this.mDeviceNameView.getText().toString();
        BluetoothNameDialogFragment.this.setDeviceName(str);
      }
    }).setNegativeButton(17039360, null).create();
    this.mAlertDialog.getWindow().setSoftInputMode(5);
    return this.mAlertDialog;
  }

  public void onDestroy()
  {
    super.onDestroy();
    this.mAlertDialog = null;
    this.mDeviceNameView = null;
    this.mOkButton = null;
  }

  public void onPause()
  {
    super.onPause();
    getActivity().unregisterReceiver(this.mReceiver);
  }

  public void onResume()
  {
    super.onResume();
    if (this.mOkButton == null)
    {
      this.mOkButton = this.mAlertDialog.getButton(-1);
      this.mOkButton.setEnabled(this.mDeviceNameEdited);
    }
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
    localIntentFilter.addAction("android.bluetooth.adapter.action.LOCAL_NAME_CHANGED");
    getActivity().registerReceiver(this.mReceiver, localIntentFilter);
  }

  public void onSaveInstanceState(Bundle paramBundle)
  {
    paramBundle.putString("device_name", this.mDeviceNameView.getText().toString());
    paramBundle.putBoolean("device_name_edited", this.mDeviceNameEdited);
  }

  public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
  {
  }

  void updateDeviceName()
  {
    if ((this.mLocalAdapter != null) && (this.mLocalAdapter.isEnabled()))
    {
      this.mDeviceNameUpdated = true;
      this.mDeviceNameEdited = false;
      this.mDeviceNameView.setText(this.mLocalAdapter.getName());
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.BluetoothNameDialogFragment
 * JD-Core Version:    0.6.2
 */