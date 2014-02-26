package com.android.settings.bluetooth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources.Theme;
import android.os.UserManager;
import android.preference.Preference;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import java.util.Iterator;
import java.util.List;

public final class BluetoothDevicePreference extends Preference
  implements View.OnClickListener, CachedBluetoothDevice.Callback
{
  private static int sDimAlpha = -2147483648;
  private final CachedBluetoothDevice mCachedDevice;
  private AlertDialog mDisconnectDialog;
  private View.OnClickListener mOnSettingsClickListener;

  public BluetoothDevicePreference(Context paramContext, CachedBluetoothDevice paramCachedBluetoothDevice)
  {
    super(paramContext);
    if (sDimAlpha == -2147483648)
    {
      TypedValue localTypedValue = new TypedValue();
      paramContext.getTheme().resolveAttribute(16842803, localTypedValue, true);
      sDimAlpha = (int)(255.0F * localTypedValue.getFloat());
    }
    this.mCachedDevice = paramCachedBluetoothDevice;
    if ((paramCachedBluetoothDevice.getBondState() == 12) && (!((UserManager)paramContext.getSystemService("user")).hasUserRestriction("no_config_bluetooth")))
      setWidgetLayoutResource(2130968667);
    this.mCachedDevice.registerCallback(this);
    onDeviceAttributesChanged();
  }

  private void askDisconnect()
  {
    Context localContext = getContext();
    String str1 = this.mCachedDevice.getName();
    if (TextUtils.isEmpty(str1))
      str1 = localContext.getString(2131427451);
    String str2 = localContext.getString(2131427438, new Object[] { str1 });
    String str3 = localContext.getString(2131427437);
    DialogInterface.OnClickListener local1 = new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        BluetoothDevicePreference.this.mCachedDevice.disconnect();
      }
    };
    this.mDisconnectDialog = Utils.showDisconnectDialog(localContext, this.mDisconnectDialog, local1, str3, Html.fromHtml(str2));
  }

  private int getBtClassDrawable()
  {
    BluetoothClass localBluetoothClass = this.mCachedDevice.getBtClass();
    if (localBluetoothClass != null)
      switch (localBluetoothClass.getMajorDeviceClass())
      {
      default:
      case 256:
      case 512:
      case 1280:
      case 1536:
      }
    while (true)
    {
      Iterator localIterator = this.mCachedDevice.getProfiles().iterator();
      int i;
      do
      {
        if (!localIterator.hasNext())
          break;
        i = ((LocalBluetoothProfile)localIterator.next()).getDrawableResource(localBluetoothClass);
      }
      while (i == 0);
      return i;
      return 2130837577;
      return 2130837571;
      return HidProfile.getHidClassDrawable(localBluetoothClass);
      return 2130837575;
      Log.w("BluetoothDevicePreference", "mBtClass is null");
    }
    if (localBluetoothClass != null)
    {
      if (localBluetoothClass.doesClassMatch(1))
        return 2130837573;
      if (localBluetoothClass.doesClassMatch(0))
        return 2130837574;
    }
    return 0;
  }

  private int getConnectionSummary()
  {
    CachedBluetoothDevice localCachedBluetoothDevice = this.mCachedDevice;
    int i = 0;
    int j = 0;
    int k = 0;
    Iterator localIterator = localCachedBluetoothDevice.getProfiles().iterator();
    while (localIterator.hasNext())
    {
      LocalBluetoothProfile localLocalBluetoothProfile = (LocalBluetoothProfile)localIterator.next();
      int m = localCachedBluetoothDevice.getProfileConnectionState(localLocalBluetoothProfile);
      switch (m)
      {
      default:
        break;
      case 0:
        if ((localLocalBluetoothProfile.isProfileReady()) && (localLocalBluetoothProfile.isPreferred(localCachedBluetoothDevice.getDevice())))
          if ((localLocalBluetoothProfile instanceof A2dpProfile))
            j = 1;
        break;
      case 1:
      case 3:
        return Utils.getConnectionStateSummary(m);
      case 2:
        i = 1;
        continue;
        if ((localLocalBluetoothProfile instanceof HeadsetProfile))
          k = 1;
        break;
      }
    }
    if (i != 0)
    {
      if ((j != 0) && (k != 0))
        return 2131427445;
      if (j != 0)
        return 2131427443;
      if (k != 0)
        return 2131427442;
      return 2131427441;
    }
    switch (localCachedBluetoothDevice.getBondState())
    {
    default:
      return 0;
    case 11:
    }
    return 2131427450;
  }

  private void pair()
  {
    if (!this.mCachedDevice.startPairing())
      Utils.showError(getContext(), this.mCachedDevice.getName(), 2131427719);
  }

  public int compareTo(Preference paramPreference)
  {
    if (!(paramPreference instanceof BluetoothDevicePreference))
      return super.compareTo(paramPreference);
    return this.mCachedDevice.compareTo(((BluetoothDevicePreference)paramPreference).mCachedDevice);
  }

  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof BluetoothDevicePreference)))
      return false;
    return this.mCachedDevice.equals(((BluetoothDevicePreference)paramObject).mCachedDevice);
  }

  CachedBluetoothDevice getCachedDevice()
  {
    return this.mCachedDevice;
  }

  public int hashCode()
  {
    return this.mCachedDevice.hashCode();
  }

  protected void onBindView(View paramView)
  {
    if (findPreferenceInHierarchy("bt_checkbox") != null)
      setDependency("bt_checkbox");
    ImageView localImageView;
    if (this.mCachedDevice.getBondState() == 12)
    {
      localImageView = (ImageView)paramView.findViewById(2131230938);
      if (localImageView != null)
      {
        localImageView.setOnClickListener(this);
        localImageView.setTag(this.mCachedDevice);
        if (!isEnabled())
          break label77;
      }
    }
    label77: for (int i = 255; ; i = sDimAlpha)
    {
      localImageView.setAlpha(i);
      super.onBindView(paramView);
      return;
    }
  }

  public void onClick(View paramView)
  {
    if (this.mOnSettingsClickListener != null)
      this.mOnSettingsClickListener.onClick(paramView);
  }

  void onClicked()
  {
    int i = this.mCachedDevice.getBondState();
    if (this.mCachedDevice.isConnected())
      askDisconnect();
    do
    {
      return;
      if (i == 12)
      {
        this.mCachedDevice.connect(true);
        return;
      }
    }
    while (i != 10);
    pair();
  }

  public void onDeviceAttributesChanged()
  {
    setTitle(this.mCachedDevice.getName());
    int i = getConnectionSummary();
    if (i != 0)
    {
      setSummary(i);
      int j = getBtClassDrawable();
      if (j != 0)
        setIcon(j);
      if (this.mCachedDevice.isBusy())
        break label69;
    }
    label69: for (boolean bool = true; ; bool = false)
    {
      setEnabled(bool);
      notifyHierarchyChanged();
      return;
      setSummary(null);
      break;
    }
  }

  protected void onPrepareForRemoval()
  {
    super.onPrepareForRemoval();
    this.mCachedDevice.unregisterCallback(this);
    if (this.mDisconnectDialog != null)
    {
      this.mDisconnectDialog.dismiss();
      this.mDisconnectDialog = null;
    }
  }

  public void setOnSettingsClickListener(View.OnClickListener paramOnClickListener)
  {
    this.mOnSettingsClickListener = paramOnClickListener;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.BluetoothDevicePreference
 * JD-Core Version:    0.6.2
 */