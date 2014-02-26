package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.security.KeyChain;
import android.security.KeyStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.internal.widget.LockPatternUtils;
import com.android.org.bouncycastle.asn1.ASN1InputStream;
import com.android.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import com.android.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.harmony.security.utils.AlgNameMapper;

public final class CredentialStorage extends Activity
{
  private Bundle mInstallBundle;
  private final KeyStore mKeyStore = KeyStore.getInstance();
  private int mRetriesRemaining = -1;

  private boolean checkKeyGuardQuality()
  {
    return new LockPatternUtils(this).getActivePasswordQuality() >= 65536;
  }

  private boolean confirmKeyGuard()
  {
    Resources localResources = getResources();
    return new ChooseLockSettingsHelper(this).launchConfirmationActivity(1, localResources.getText(2131428891), localResources.getText(2131428892));
  }

  private void ensureKeyGuard()
  {
    if (!checkKeyGuardQuality())
      new ConfigureKeyGuardDialog(null);
    while (confirmKeyGuard())
      return;
    finish();
  }

  private void handleUnlockOrInstall()
  {
    if (isFinishing())
      return;
    switch (1.$SwitchMap$android$security$KeyStore$State[this.mKeyStore.state().ordinal()])
    {
    default:
      return;
    case 1:
      ensureKeyGuard();
      return;
    case 2:
      new UnlockDialog(null);
      return;
    case 3:
    }
    if (!checkKeyGuardQuality())
    {
      new ConfigureKeyGuardDialog(null);
      return;
    }
    installIfAvailable();
    finish();
  }

  private void installIfAvailable()
  {
    Bundle localBundle;
    int i;
    if ((this.mInstallBundle != null) && (!this.mInstallBundle.isEmpty()))
    {
      localBundle = this.mInstallBundle;
      this.mInstallBundle = null;
      i = localBundle.getInt("install_as_uid", -1);
      if (localBundle.containsKey("user_private_key_name"))
      {
        String str3 = localBundle.getString("user_private_key_name");
        byte[] arrayOfByte3 = localBundle.getByteArray("user_private_key_data");
        int k = 1;
        if ((i == 1010) && (isHardwareBackedKey(arrayOfByte3)))
        {
          Log.d("CredentialStorage", "Saving private key with FLAG_NONE for WIFI_UID");
          k = 0;
        }
        if (!this.mKeyStore.importKey(str3, arrayOfByte3, i, k))
          Log.e("CredentialStorage", "Failed to install " + str3 + " as user " + i);
      }
    }
    else
    {
      return;
    }
    if (i == 1010);
    for (int j = 0; localBundle.containsKey("user_certificate_name"); j = 1)
    {
      String str2 = localBundle.getString("user_certificate_name");
      byte[] arrayOfByte2 = localBundle.getByteArray("user_certificate_data");
      if (this.mKeyStore.put(str2, arrayOfByte2, i, j))
        break;
      Log.e("CredentialStorage", "Failed to install " + str2 + " as user " + i);
      return;
    }
    if (localBundle.containsKey("ca_certificates_name"))
    {
      String str1 = localBundle.getString("ca_certificates_name");
      byte[] arrayOfByte1 = localBundle.getByteArray("ca_certificates_data");
      if (!this.mKeyStore.put(str1, arrayOfByte1, i, j))
      {
        Log.e("CredentialStorage", "Failed to install " + str1 + " as user " + i);
        return;
      }
    }
    setResult(-1);
  }

  private boolean isHardwareBackedKey(byte[] paramArrayOfByte)
  {
    try
    {
      boolean bool = KeyChain.isBoundKeyAlgorithm(AlgNameMapper.map2AlgName(PrivateKeyInfo.getInstance(new ASN1InputStream(new ByteArrayInputStream(paramArrayOfByte)).readObject()).getAlgorithmId().getAlgorithm().getId()));
      return bool;
    }
    catch (IOException localIOException)
    {
      Log.e("CredentialStorage", "Failed to parse key data");
    }
    return false;
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (paramInt1 == 1)
    {
      if (paramInt2 == -1)
      {
        String str = paramIntent.getStringExtra("password");
        if (!TextUtils.isEmpty(str))
          this.mKeyStore.password(str);
      }
    }
    else
      return;
    finish();
  }

  protected void onResume()
  {
    super.onResume();
    Intent localIntent = getIntent();
    String str = localIntent.getAction();
    if ("com.android.credentials.RESET".equals(str))
    {
      new ResetDialog(null);
      return;
    }
    if (("com.android.credentials.INSTALL".equals(str)) && ("com.android.certinstaller".equals(getCallingPackage())))
      this.mInstallBundle = localIntent.getExtras();
    handleUnlockOrInstall();
  }

  private class ConfigureKeyGuardDialog
    implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener
  {
    private boolean mConfigureConfirmed;

    private ConfigureKeyGuardDialog()
    {
      AlertDialog localAlertDialog = new AlertDialog.Builder(CredentialStorage.this).setTitle(17039380).setIconAttribute(16843605).setMessage(2131428904).setPositiveButton(17039370, this).setNegativeButton(17039360, this).create();
      localAlertDialog.setOnDismissListener(this);
      localAlertDialog.show();
    }

    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      if (paramInt == -1);
      for (boolean bool = true; ; bool = false)
      {
        this.mConfigureConfirmed = bool;
        return;
      }
    }

    public void onDismiss(DialogInterface paramDialogInterface)
    {
      if (this.mConfigureConfirmed)
      {
        this.mConfigureConfirmed = false;
        Intent localIntent = new Intent("android.app.action.SET_NEW_PASSWORD");
        localIntent.putExtra("minimum_quality", 65536);
        CredentialStorage.this.startActivity(localIntent);
        return;
      }
      CredentialStorage.this.finish();
    }
  }

  private class ResetDialog
    implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener
  {
    private boolean mResetConfirmed;

    private ResetDialog()
    {
      AlertDialog localAlertDialog = new AlertDialog.Builder(CredentialStorage.this).setTitle(17039380).setIconAttribute(16843605).setMessage(2131428896).setPositiveButton(17039370, this).setNegativeButton(17039360, this).create();
      localAlertDialog.setOnDismissListener(this);
      localAlertDialog.show();
    }

    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      if (paramInt == -1);
      for (boolean bool = true; ; bool = false)
      {
        this.mResetConfirmed = bool;
        return;
      }
    }

    public void onDismiss(DialogInterface paramDialogInterface)
    {
      if (this.mResetConfirmed)
      {
        this.mResetConfirmed = false;
        new CredentialStorage.ResetKeyStoreAndKeyChain(CredentialStorage.this, null).execute(new Void[0]);
        return;
      }
      CredentialStorage.this.finish();
    }
  }

  private class ResetKeyStoreAndKeyChain extends AsyncTask<Void, Void, Boolean>
  {
    private ResetKeyStoreAndKeyChain()
    {
    }

    // ERROR //
    protected Boolean doInBackground(Void[] paramArrayOfVoid)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 11	com/android/settings/CredentialStorage$ResetKeyStoreAndKeyChain:this$0	Lcom/android/settings/CredentialStorage;
      //   4: invokestatic 29	com/android/settings/CredentialStorage:access$400	(Lcom/android/settings/CredentialStorage;)Landroid/security/KeyStore;
      //   7: invokevirtual 35	android/security/KeyStore:reset	()Z
      //   10: pop
      //   11: aload_0
      //   12: getfield 11	com/android/settings/CredentialStorage$ResetKeyStoreAndKeyChain:this$0	Lcom/android/settings/CredentialStorage;
      //   15: invokestatic 41	android/security/KeyChain:bind	(Landroid/content/Context;)Landroid/security/KeyChain$KeyChainConnection;
      //   18: astore 4
      //   20: aload 4
      //   22: invokevirtual 47	android/security/KeyChain$KeyChainConnection:getService	()Landroid/security/IKeyChainService;
      //   25: invokeinterface 50 1 0
      //   30: invokestatic 56	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
      //   33: astore 8
      //   35: aload 4
      //   37: invokevirtual 59	android/security/KeyChain$KeyChainConnection:close	()V
      //   40: aload 8
      //   42: areturn
      //   43: astore 6
      //   45: iconst_0
      //   46: invokestatic 56	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
      //   49: astore 7
      //   51: aload 4
      //   53: invokevirtual 59	android/security/KeyChain$KeyChainConnection:close	()V
      //   56: aload 7
      //   58: areturn
      //   59: astore_3
      //   60: invokestatic 65	java/lang/Thread:currentThread	()Ljava/lang/Thread;
      //   63: invokevirtual 68	java/lang/Thread:interrupt	()V
      //   66: iconst_0
      //   67: invokestatic 56	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
      //   70: areturn
      //   71: astore 5
      //   73: aload 4
      //   75: invokevirtual 59	android/security/KeyChain$KeyChainConnection:close	()V
      //   78: aload 5
      //   80: athrow
      //
      // Exception table:
      //   from	to	target	type
      //   20	35	43	android/os/RemoteException
      //   11	20	59	java/lang/InterruptedException
      //   35	40	59	java/lang/InterruptedException
      //   51	56	59	java/lang/InterruptedException
      //   73	81	59	java/lang/InterruptedException
      //   20	35	71	finally
      //   45	51	71	finally
    }

    protected void onPostExecute(Boolean paramBoolean)
    {
      if (paramBoolean.booleanValue())
        Toast.makeText(CredentialStorage.this, 2131428901, 0).show();
      while (true)
      {
        CredentialStorage.this.finish();
        return;
        Toast.makeText(CredentialStorage.this, 2131428902, 0).show();
      }
    }
  }

  private class UnlockDialog
    implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener, TextWatcher
  {
    private final Button mButton;
    private final TextView mError;
    private final TextView mOldPassword;
    private boolean mUnlockConfirmed;

    private UnlockDialog()
    {
      View localView = View.inflate(CredentialStorage.this, 2130968602, null);
      Object localObject;
      if (CredentialStorage.this.mRetriesRemaining == -1)
        localObject = CredentialStorage.this.getResources().getText(2131428894);
      while (true)
      {
        ((TextView)localView.findViewById(2131230773)).setText((CharSequence)localObject);
        this.mOldPassword = ((TextView)localView.findViewById(2131230776));
        this.mOldPassword.setVisibility(0);
        this.mOldPassword.addTextChangedListener(this);
        this.mError = ((TextView)localView.findViewById(2131230774));
        AlertDialog localAlertDialog = new AlertDialog.Builder(CredentialStorage.this).setView(localView).setTitle(2131428893).setPositiveButton(17039370, this).setNegativeButton(17039360, this).create();
        localAlertDialog.setOnDismissListener(this);
        localAlertDialog.show();
        this.mButton = localAlertDialog.getButton(-1);
        this.mButton.setEnabled(false);
        return;
        if (CredentialStorage.this.mRetriesRemaining > 3)
        {
          localObject = CredentialStorage.this.getResources().getText(2131428898);
        }
        else if (CredentialStorage.this.mRetriesRemaining == 1)
        {
          localObject = CredentialStorage.this.getResources().getText(2131428899);
        }
        else
        {
          Object[] arrayOfObject = new Object[1];
          arrayOfObject[0] = Integer.valueOf(CredentialStorage.this.mRetriesRemaining);
          localObject = CredentialStorage.this.getString(2131428900, arrayOfObject);
        }
      }
    }

    public void afterTextChanged(Editable paramEditable)
    {
      Button localButton = this.mButton;
      if ((this.mOldPassword == null) || (this.mOldPassword.getText().length() > 0));
      for (boolean bool = true; ; bool = false)
      {
        localButton.setEnabled(bool);
        return;
      }
    }

    public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
    {
    }

    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      if (paramInt == -1);
      for (boolean bool = true; ; bool = false)
      {
        this.mUnlockConfirmed = bool;
        return;
      }
    }

    public void onDismiss(DialogInterface paramDialogInterface)
    {
      if (this.mUnlockConfirmed)
      {
        this.mUnlockConfirmed = false;
        this.mError.setVisibility(0);
        CredentialStorage.this.mKeyStore.unlock(this.mOldPassword.getText().toString());
        int i = CredentialStorage.this.mKeyStore.getLastError();
        if (i == 1)
        {
          CredentialStorage.access$502(CredentialStorage.this, -1);
          Toast.makeText(CredentialStorage.this, 2131428903, 0).show();
          CredentialStorage.this.ensureKeyGuard();
        }
        do
        {
          return;
          if (i == 3)
          {
            CredentialStorage.access$502(CredentialStorage.this, -1);
            Toast.makeText(CredentialStorage.this, 2131428901, 0).show();
            CredentialStorage.this.handleUnlockOrInstall();
            return;
          }
        }
        while (i < 10);
        CredentialStorage.access$502(CredentialStorage.this, 1 + (i - 10));
        CredentialStorage.this.handleUnlockOrInstall();
        return;
      }
      CredentialStorage.this.finish();
    }

    public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
    {
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.CredentialStorage
 * JD-Core Version:    0.6.2
 */