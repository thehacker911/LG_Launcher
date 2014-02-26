package com.android.settings.nfc;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import android.nfc.cardemulation.ApduServiceInfo;
import android.nfc.cardemulation.CardEmulation;
import android.provider.Settings.Secure;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PaymentBackend
{
  private final NfcAdapter mAdapter;
  private final CardEmulation mCardEmuManager;
  private final Context mContext;

  public PaymentBackend(Context paramContext)
  {
    this.mContext = paramContext;
    this.mAdapter = NfcAdapter.getDefaultAdapter(paramContext);
    this.mCardEmuManager = CardEmulation.getInstance(this.mAdapter);
  }

  ComponentName getDefaultPaymentApp()
  {
    String str = Settings.Secure.getString(this.mContext.getContentResolver(), "nfc_payment_default_component");
    if (str != null)
      return ComponentName.unflattenFromString(str);
    return null;
  }

  public List<PaymentAppInfo> getPaymentAppInfos()
  {
    PackageManager localPackageManager = this.mContext.getPackageManager();
    List localList = this.mCardEmuManager.getServices("payment");
    ArrayList localArrayList = new ArrayList();
    if (localList == null);
    while (true)
    {
      return localArrayList;
      ComponentName localComponentName = getDefaultPaymentApp();
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        ApduServiceInfo localApduServiceInfo = (ApduServiceInfo)localIterator.next();
        PaymentAppInfo localPaymentAppInfo = new PaymentAppInfo();
        localPaymentAppInfo.banner = localApduServiceInfo.loadBanner(localPackageManager);
        localPaymentAppInfo.caption = localApduServiceInfo.getDescription();
        if (localPaymentAppInfo.caption == null)
          localPaymentAppInfo.caption = localApduServiceInfo.loadLabel(localPackageManager);
        localPaymentAppInfo.isDefault = localApduServiceInfo.getComponent().equals(localComponentName);
        localPaymentAppInfo.componentName = localApduServiceInfo.getComponent();
        localArrayList.add(localPaymentAppInfo);
      }
    }
  }

  public void setDefaultPaymentApp(ComponentName paramComponentName)
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    if (paramComponentName != null);
    for (String str = paramComponentName.flattenToString(); ; str = null)
    {
      Settings.Secure.putString(localContentResolver, "nfc_payment_default_component", str);
      return;
    }
  }

  public static class PaymentAppInfo
  {
    Drawable banner;
    CharSequence caption;
    public ComponentName componentName;
    boolean isDefault;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.nfc.PaymentBackend
 * JD-Core Version:    0.6.2
 */