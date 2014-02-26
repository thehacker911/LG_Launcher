package com.android.settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.android.internal.os.storage.ExternalStorageFormatter;

public class MasterClearConfirm extends Fragment
{
  private View mContentView;
  private boolean mEraseSdCard;
  private Button mFinalButton;
  private View.OnClickListener mFinalClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (Utils.isMonkeyRunning())
        return;
      if (MasterClearConfirm.this.mEraseSdCard)
      {
        Intent localIntent = new Intent("com.android.internal.os.storage.FORMAT_AND_FACTORY_RESET");
        localIntent.setComponent(ExternalStorageFormatter.COMPONENT_NAME);
        MasterClearConfirm.this.getActivity().startService(localIntent);
        return;
      }
      MasterClearConfirm.this.getActivity().sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
    }
  };

  private void establishFinalConfirmationState()
  {
    this.mFinalButton = ((Button)this.mContentView.findViewById(2131230912));
    this.mFinalButton.setOnClickListener(this.mFinalClickListener);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Bundle localBundle = getArguments();
    if (localBundle != null);
    for (boolean bool = localBundle.getBoolean("erase_sd"); ; bool = false)
    {
      this.mEraseSdCard = bool;
      return;
    }
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mContentView = paramLayoutInflater.inflate(2130968649, null);
    establishFinalConfirmationState();
    return this.mContentView;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.MasterClearConfirm
 * JD-Core Version:    0.6.2
 */