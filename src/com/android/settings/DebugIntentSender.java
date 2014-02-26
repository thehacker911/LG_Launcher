package com.android.settings;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Selection;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class DebugIntentSender extends Activity
{
  private EditText mAccountField;
  private View.OnClickListener mClicked = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      Intent localIntent;
      if ((paramAnonymousView == DebugIntentSender.this.mSendBroadcastButton) || (paramAnonymousView == DebugIntentSender.this.mStartActivityButton))
      {
        String str1 = DebugIntentSender.this.mIntentField.getText().toString();
        String str2 = DebugIntentSender.this.mDataField.getText().toString();
        String str3 = DebugIntentSender.this.mAccountField.getText().toString();
        String str4 = DebugIntentSender.this.mResourceField.getText().toString();
        localIntent = new Intent(str1);
        if (!TextUtils.isEmpty(str2))
          localIntent.setData(Uri.parse(str2));
        localIntent.putExtra("account", str3);
        localIntent.putExtra("resource", str4);
        if (paramAnonymousView != DebugIntentSender.this.mSendBroadcastButton)
          break label163;
        DebugIntentSender.this.sendBroadcast(localIntent);
      }
      while (true)
      {
        DebugIntentSender.this.setResult(-1);
        DebugIntentSender.this.finish();
        return;
        label163: DebugIntentSender.this.startActivity(localIntent);
      }
    }
  };
  private EditText mDataField;
  private EditText mIntentField;
  private EditText mResourceField;
  private Button mSendBroadcastButton;
  private Button mStartActivityButton;

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968636);
    this.mIntentField = ((EditText)findViewById(2131230885));
    this.mIntentField.setText("android.intent.action.SYNC");
    Selection.selectAll(this.mIntentField.getText());
    this.mDataField = ((EditText)findViewById(2131230886));
    this.mDataField.setBackgroundResource(17301528);
    this.mAccountField = ((EditText)findViewById(2131230887));
    this.mResourceField = ((EditText)findViewById(2131230888));
    this.mSendBroadcastButton = ((Button)findViewById(2131230889));
    this.mSendBroadcastButton.setOnClickListener(this.mClicked);
    this.mStartActivityButton = ((Button)findViewById(2131230890));
    this.mStartActivityButton.setOnClickListener(this.mClicked);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.DebugIntentSender
 * JD-Core Version:    0.6.2
 */