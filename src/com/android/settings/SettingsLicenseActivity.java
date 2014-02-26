package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class SettingsLicenseActivity extends Activity
{
  private Handler mHandler = null;
  private ProgressDialog mSpinnerDlg = null;
  private AlertDialog mTextDlg = null;
  private WebView mWebView = null;

  private void showErrorAndFinish()
  {
    this.mSpinnerDlg.dismiss();
    this.mSpinnerDlg = null;
    Toast.makeText(this, 2131428305, 1).show();
    finish();
  }

  private void showPageOfText(String paramString)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
    localBuilder.setCancelable(true).setView(this.mWebView).setTitle(2131428304);
    this.mTextDlg = localBuilder.create();
    this.mTextDlg.setOnDismissListener(new DialogInterface.OnDismissListener()
    {
      public void onDismiss(DialogInterface paramAnonymousDialogInterface)
      {
        SettingsLicenseActivity.this.finish();
      }
    });
    this.mWebView.loadDataWithBaseURL(null, paramString, "text/html", "utf-8", null);
    this.mWebView.setWebViewClient(new WebViewClient()
    {
      public void onPageFinished(WebView paramAnonymousWebView, String paramAnonymousString)
      {
        SettingsLicenseActivity.this.mSpinnerDlg.dismiss();
        if (SettingsLicenseActivity.this.isResumed())
          SettingsLicenseActivity.this.mTextDlg.show();
      }
    });
    this.mWebView = null;
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    String str = SystemProperties.get("ro.config.license_path", "/system/etc/NOTICE.html.gz");
    if (TextUtils.isEmpty(str))
    {
      Log.e("SettingsLicenseActivity", "The system property for the license file is empty.");
      showErrorAndFinish();
      return;
    }
    setVisible(false);
    this.mWebView = new WebView(this);
    this.mHandler = new Handler()
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        super.handleMessage(paramAnonymousMessage);
        if (paramAnonymousMessage.what == 0)
        {
          String str = (String)paramAnonymousMessage.obj;
          SettingsLicenseActivity.this.showPageOfText(str);
          return;
        }
        SettingsLicenseActivity.this.showErrorAndFinish();
      }
    };
    ProgressDialog localProgressDialog = ProgressDialog.show(this, getText(2131428304), getText(2131428306), true, false);
    localProgressDialog.setProgressStyle(0);
    this.mSpinnerDlg = localProgressDialog;
    new Thread(new LicenseFileLoader(str, this.mHandler)).start();
  }

  protected void onDestroy()
  {
    if ((this.mTextDlg != null) && (this.mTextDlg.isShowing()))
      this.mTextDlg.dismiss();
    if ((this.mSpinnerDlg != null) && (this.mSpinnerDlg.isShowing()))
      this.mSpinnerDlg.dismiss();
    super.onDestroy();
  }

  private class LicenseFileLoader
    implements Runnable
  {
    private String mFileName;
    private Handler mHandler;

    public LicenseFileLoader(String paramHandler, Handler arg3)
    {
      this.mFileName = paramHandler;
      Object localObject;
      this.mHandler = localObject;
    }

    // ERROR //
    public void run()
    {
      // Byte code:
      //   0: aconst_null
      //   1: astore_1
      //   2: new 30	java/lang/StringBuilder
      //   5: dup
      //   6: sipush 2048
      //   9: invokespecial 33	java/lang/StringBuilder:<init>	(I)V
      //   12: astore_2
      //   13: sipush 2048
      //   16: newarray char
      //   18: astore 15
      //   20: aload_0
      //   21: getfield 21	com/android/settings/SettingsLicenseActivity$LicenseFileLoader:mFileName	Ljava/lang/String;
      //   24: ldc 35
      //   26: invokevirtual 41	java/lang/String:endsWith	(Ljava/lang/String;)Z
      //   29: istore 16
      //   31: aconst_null
      //   32: astore_1
      //   33: iload 16
      //   35: ifeq +183 -> 218
      //   38: new 43	java/io/InputStreamReader
      //   41: dup
      //   42: new 45	java/util/zip/GZIPInputStream
      //   45: dup
      //   46: new 47	java/io/FileInputStream
      //   49: dup
      //   50: aload_0
      //   51: getfield 21	com/android/settings/SettingsLicenseActivity$LicenseFileLoader:mFileName	Ljava/lang/String;
      //   54: invokespecial 50	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
      //   57: invokespecial 53	java/util/zip/GZIPInputStream:<init>	(Ljava/io/InputStream;)V
      //   60: invokespecial 54	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
      //   63: astore_1
      //   64: aload_1
      //   65: aload 15
      //   67: invokevirtual 60	java/io/Reader:read	([C)I
      //   70: istore 17
      //   72: iload 17
      //   74: iflt +163 -> 237
      //   77: aload_2
      //   78: aload 15
      //   80: iconst_0
      //   81: iload 17
      //   83: invokevirtual 64	java/lang/StringBuilder:append	([CII)Ljava/lang/StringBuilder;
      //   86: pop
      //   87: goto -23 -> 64
      //   90: astore 12
      //   92: ldc 66
      //   94: new 30	java/lang/StringBuilder
      //   97: dup
      //   98: invokespecial 67	java/lang/StringBuilder:<init>	()V
      //   101: ldc 69
      //   103: invokevirtual 72	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   106: aload_0
      //   107: getfield 21	com/android/settings/SettingsLicenseActivity$LicenseFileLoader:mFileName	Ljava/lang/String;
      //   110: invokevirtual 72	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   113: invokevirtual 76	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   116: aload 12
      //   118: invokestatic 82	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   121: pop
      //   122: iconst_1
      //   123: istore 7
      //   125: aload_1
      //   126: ifnull +7 -> 133
      //   129: aload_1
      //   130: invokevirtual 85	java/io/InputStreamReader:close	()V
      //   133: iload 7
      //   135: ifne +46 -> 181
      //   138: aload_2
      //   139: invokestatic 91	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
      //   142: ifeq +39 -> 181
      //   145: ldc 66
      //   147: new 30	java/lang/StringBuilder
      //   150: dup
      //   151: invokespecial 67	java/lang/StringBuilder:<init>	()V
      //   154: ldc 93
      //   156: invokevirtual 72	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   159: aload_0
      //   160: getfield 21	com/android/settings/SettingsLicenseActivity$LicenseFileLoader:mFileName	Ljava/lang/String;
      //   163: invokevirtual 72	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   166: ldc 95
      //   168: invokevirtual 72	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   171: invokevirtual 76	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   174: invokestatic 98	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   177: pop
      //   178: iconst_3
      //   179: istore 7
      //   181: aload_0
      //   182: getfield 23	com/android/settings/SettingsLicenseActivity$LicenseFileLoader:mHandler	Landroid/os/Handler;
      //   185: iload 7
      //   187: aconst_null
      //   188: invokevirtual 104	android/os/Handler:obtainMessage	(ILjava/lang/Object;)Landroid/os/Message;
      //   191: astore 9
      //   193: iload 7
      //   195: ifne +12 -> 207
      //   198: aload 9
      //   200: aload_2
      //   201: invokevirtual 76	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   204: putfield 110	android/os/Message:obj	Ljava/lang/Object;
      //   207: aload_0
      //   208: getfield 23	com/android/settings/SettingsLicenseActivity$LicenseFileLoader:mHandler	Landroid/os/Handler;
      //   211: aload 9
      //   213: invokevirtual 114	android/os/Handler:sendMessage	(Landroid/os/Message;)Z
      //   216: pop
      //   217: return
      //   218: new 116	java/io/FileReader
      //   221: dup
      //   222: aload_0
      //   223: getfield 21	com/android/settings/SettingsLicenseActivity$LicenseFileLoader:mFileName	Ljava/lang/String;
      //   226: invokespecial 117	java/io/FileReader:<init>	(Ljava/lang/String;)V
      //   229: astore 20
      //   231: aload 20
      //   233: astore_1
      //   234: goto -170 -> 64
      //   237: iconst_0
      //   238: istore 7
      //   240: aload_1
      //   241: ifnull -108 -> 133
      //   244: aload_1
      //   245: invokevirtual 85	java/io/InputStreamReader:close	()V
      //   248: iconst_0
      //   249: istore 7
      //   251: goto -118 -> 133
      //   254: astore 19
      //   256: iconst_0
      //   257: istore 7
      //   259: goto -126 -> 133
      //   262: astore 5
      //   264: ldc 66
      //   266: new 30	java/lang/StringBuilder
      //   269: dup
      //   270: invokespecial 67	java/lang/StringBuilder:<init>	()V
      //   273: ldc 119
      //   275: invokevirtual 72	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   278: aload_0
      //   279: getfield 21	com/android/settings/SettingsLicenseActivity$LicenseFileLoader:mFileName	Ljava/lang/String;
      //   282: invokevirtual 72	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   285: invokevirtual 76	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   288: aload 5
      //   290: invokestatic 82	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   293: pop
      //   294: iconst_2
      //   295: istore 7
      //   297: aload_1
      //   298: ifnull -165 -> 133
      //   301: aload_1
      //   302: invokevirtual 85	java/io/InputStreamReader:close	()V
      //   305: goto -172 -> 133
      //   308: astore 8
      //   310: goto -177 -> 133
      //   313: astore_3
      //   314: aload_1
      //   315: ifnull +7 -> 322
      //   318: aload_1
      //   319: invokevirtual 85	java/io/InputStreamReader:close	()V
      //   322: aload_3
      //   323: athrow
      //   324: astore 14
      //   326: goto -193 -> 133
      //   329: astore 4
      //   331: goto -9 -> 322
      //
      // Exception table:
      //   from	to	target	type
      //   13	31	90	java/io/FileNotFoundException
      //   38	64	90	java/io/FileNotFoundException
      //   64	72	90	java/io/FileNotFoundException
      //   77	87	90	java/io/FileNotFoundException
      //   218	231	90	java/io/FileNotFoundException
      //   244	248	254	java/io/IOException
      //   13	31	262	java/io/IOException
      //   38	64	262	java/io/IOException
      //   64	72	262	java/io/IOException
      //   77	87	262	java/io/IOException
      //   218	231	262	java/io/IOException
      //   301	305	308	java/io/IOException
      //   13	31	313	finally
      //   38	64	313	finally
      //   64	72	313	finally
      //   77	87	313	finally
      //   92	122	313	finally
      //   218	231	313	finally
      //   264	294	313	finally
      //   129	133	324	java/io/IOException
      //   318	322	329	java/io/IOException
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.SettingsLicenseActivity
 * JD-Core Version:    0.6.2
 */