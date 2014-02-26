package com.android.settings;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.internal.app.AlertController.AlertParams;
import java.util.Locale;

public class SettingsSafetyLegalActivity extends AlertActivity
  implements DialogInterface.OnCancelListener, DialogInterface.OnClickListener
{
  private AlertDialog mErrorDialog = null;
  private WebView mWebView;

  private void showErrorAndFinish(String paramString)
  {
    if (this.mErrorDialog == null)
      this.mErrorDialog = new AlertDialog.Builder(this).setTitle(2131428308).setPositiveButton(17039370, this).setOnCancelListener(this).setCancelable(true).create();
    while (true)
    {
      this.mErrorDialog.setMessage(getResources().getString(2131428309, new Object[] { paramString }));
      this.mErrorDialog.show();
      return;
      if (this.mErrorDialog.isShowing())
        this.mErrorDialog.dismiss();
    }
  }

  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if ((paramKeyEvent.getKeyCode() == 4) && (paramKeyEvent.getAction() == 0) && (this.mWebView.canGoBack()))
    {
      this.mWebView.goBack();
      return true;
    }
    return super.dispatchKeyEvent(paramKeyEvent);
  }

  public void onCancel(DialogInterface paramDialogInterface)
  {
    finish();
  }

  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    finish();
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    String str1 = SystemProperties.get("ro.url.safetylegal");
    Configuration localConfiguration = getResources().getConfiguration();
    String str2 = String.format("%s&%s", new Object[] { str1, String.format("locale=%s-%s", new Object[] { localConfiguration.locale.getLanguage(), localConfiguration.locale.getCountry() }) });
    this.mWebView = new WebView(this);
    this.mWebView.getSettings().setJavaScriptEnabled(true);
    if (paramBundle == null)
      this.mWebView.loadUrl(str2);
    while (true)
    {
      this.mWebView.setWebViewClient(new WebViewClient()
      {
        public void onPageFinished(WebView paramAnonymousWebView, String paramAnonymousString)
        {
          SettingsSafetyLegalActivity.this.mAlert.setTitle(SettingsSafetyLegalActivity.this.getString(2131428308));
        }

        public void onReceivedError(WebView paramAnonymousWebView, int paramAnonymousInt, String paramAnonymousString1, String paramAnonymousString2)
        {
          SettingsSafetyLegalActivity.this.showErrorAndFinish(paramAnonymousString2);
        }
      });
      AlertController.AlertParams localAlertParams = this.mAlertParams;
      localAlertParams.mTitle = getString(2131428310);
      localAlertParams.mView = this.mWebView;
      localAlertParams.mForceInverseBackground = true;
      setupAlert();
      return;
      this.mWebView.restoreState(paramBundle);
    }
  }

  protected void onDestroy()
  {
    super.onDestroy();
    if (this.mErrorDialog != null)
    {
      this.mErrorDialog.dismiss();
      this.mErrorDialog = null;
    }
  }

  public void onSaveInstanceState(Bundle paramBundle)
  {
    this.mWebView.saveState(paramBundle);
    super.onSaveInstanceState(paramBundle);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.SettingsSafetyLegalActivity
 * JD-Core Version:    0.6.2
 */