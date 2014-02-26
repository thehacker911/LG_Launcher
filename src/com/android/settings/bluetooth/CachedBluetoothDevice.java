package com.android.settings.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

final class CachedBluetoothDevice
  implements Comparable<CachedBluetoothDevice>
{
  private BluetoothClass mBtClass;
  private final Collection<Callback> mCallbacks = new ArrayList();
  private boolean mConnectAfterPairing;
  private long mConnectAttempted;
  private final Context mContext;
  private final BluetoothDevice mDevice;
  private boolean mIsConnectingErrorPossible;
  private final LocalBluetoothAdapter mLocalAdapter;
  private boolean mLocalNapRoleConnected;
  private int mMessagePermissionChoice;
  private int mMessageRejectedTimes;
  private String mName;
  private int mPhonebookPermissionChoice;
  private int mPhonebookRejectedTimes;
  private HashMap<LocalBluetoothProfile, Integer> mProfileConnectionState;
  private final LocalBluetoothProfileManager mProfileManager;
  private final List<LocalBluetoothProfile> mProfiles = new ArrayList();
  private final List<LocalBluetoothProfile> mRemovedProfiles = new ArrayList();
  private short mRssi;
  private boolean mVisible;

  CachedBluetoothDevice(Context paramContext, LocalBluetoothAdapter paramLocalBluetoothAdapter, LocalBluetoothProfileManager paramLocalBluetoothProfileManager, BluetoothDevice paramBluetoothDevice)
  {
    this.mContext = paramContext;
    this.mLocalAdapter = paramLocalBluetoothAdapter;
    this.mProfileManager = paramLocalBluetoothProfileManager;
    this.mDevice = paramBluetoothDevice;
    this.mProfileConnectionState = new HashMap();
    fillData();
  }

  private void connectAutoConnectableProfiles()
  {
    if (!ensurePaired());
    while (true)
    {
      return;
      this.mIsConnectingErrorPossible = true;
      Iterator localIterator = this.mProfiles.iterator();
      while (localIterator.hasNext())
      {
        LocalBluetoothProfile localLocalBluetoothProfile = (LocalBluetoothProfile)localIterator.next();
        if (localLocalBluetoothProfile.isAutoConnectable())
        {
          localLocalBluetoothProfile.setPreferred(this.mDevice, true);
          connectInt(localLocalBluetoothProfile);
        }
      }
    }
  }

  private void connectWithoutResettingTimer(boolean paramBoolean)
  {
    if (this.mProfiles.isEmpty())
      Log.d("CachedBluetoothDevice", "No profiles. Maybe we will connect later");
    int i;
    label109: 
    do
    {
      return;
      this.mIsConnectingErrorPossible = true;
      i = 0;
      Iterator localIterator = this.mProfiles.iterator();
      while (localIterator.hasNext())
      {
        LocalBluetoothProfile localLocalBluetoothProfile = (LocalBluetoothProfile)localIterator.next();
        if (paramBoolean)
        {
          if (!localLocalBluetoothProfile.isConnectable());
        }
        else
          while (true)
          {
            if (!localLocalBluetoothProfile.isPreferred(this.mDevice))
              break label109;
            i++;
            connectInt(localLocalBluetoothProfile);
            break;
            if (!localLocalBluetoothProfile.isAutoConnectable())
              break;
          }
      }
    }
    while (i != 0);
    connectAutoConnectableProfiles();
  }

  private String describe(LocalBluetoothProfile paramLocalBluetoothProfile)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Address:").append(this.mDevice);
    if (paramLocalBluetoothProfile != null)
      localStringBuilder.append(" Profile:").append(paramLocalBluetoothProfile);
    return localStringBuilder.toString();
  }

  private void dispatchAttributesChanged()
  {
    synchronized (this.mCallbacks)
    {
      Iterator localIterator = this.mCallbacks.iterator();
      if (localIterator.hasNext())
        ((Callback)localIterator.next()).onDeviceAttributesChanged();
    }
  }

  private boolean ensurePaired()
  {
    if (getBondState() == 10)
    {
      startPairing();
      return false;
    }
    return true;
  }

  private void fetchBtClass()
  {
    this.mBtClass = this.mDevice.getBluetoothClass();
  }

  private void fetchMessagePermissionChoice()
  {
    this.mMessagePermissionChoice = this.mContext.getSharedPreferences("bluetooth_message_permission", 0).getInt(this.mDevice.getAddress(), 0);
  }

  private void fetchMessageRejectTimes()
  {
    this.mMessageRejectedTimes = this.mContext.getSharedPreferences("bluetooth_message_reject", 0).getInt(this.mDevice.getAddress(), 0);
  }

  private void fetchName()
  {
    this.mName = this.mDevice.getAliasName();
    if (TextUtils.isEmpty(this.mName))
      this.mName = this.mDevice.getAddress();
  }

  private void fetchPhonebookPermissionChoice()
  {
    this.mPhonebookPermissionChoice = this.mContext.getSharedPreferences("bluetooth_phonebook_permission", 0).getInt(this.mDevice.getAddress(), 0);
  }

  private void fetchPhonebookRejectTimes()
  {
    this.mPhonebookRejectedTimes = this.mContext.getSharedPreferences("bluetooth_phonebook_reject", 0).getInt(this.mDevice.getAddress(), 0);
  }

  private void fillData()
  {
    fetchName();
    fetchBtClass();
    updateProfiles();
    fetchPhonebookPermissionChoice();
    fetchMessagePermissionChoice();
    fetchPhonebookRejectTimes();
    fetchMessageRejectTimes();
    this.mVisible = false;
    dispatchAttributesChanged();
  }

  private void saveMessageRejectTimes()
  {
    SharedPreferences.Editor localEditor = this.mContext.getSharedPreferences("bluetooth_message_reject", 0).edit();
    if (this.mMessageRejectedTimes == 0)
      localEditor.remove(this.mDevice.getAddress());
    while (true)
    {
      localEditor.commit();
      return;
      localEditor.putInt(this.mDevice.getAddress(), this.mMessageRejectedTimes);
    }
  }

  private void savePhonebookRejectTimes()
  {
    SharedPreferences.Editor localEditor = this.mContext.getSharedPreferences("bluetooth_phonebook_reject", 0).edit();
    if (this.mPhonebookRejectedTimes == 0)
      localEditor.remove(this.mDevice.getAddress());
    while (true)
    {
      localEditor.commit();
      return;
      localEditor.putInt(this.mDevice.getAddress(), this.mPhonebookRejectedTimes);
    }
  }

  private boolean updateProfiles()
  {
    ParcelUuid[] arrayOfParcelUuid1 = this.mDevice.getUuids();
    if (arrayOfParcelUuid1 == null);
    ParcelUuid[] arrayOfParcelUuid2;
    do
    {
      return false;
      arrayOfParcelUuid2 = this.mLocalAdapter.getUuids();
    }
    while (arrayOfParcelUuid2 == null);
    this.mProfileManager.updateProfiles(arrayOfParcelUuid1, arrayOfParcelUuid2, this.mProfiles, this.mRemovedProfiles, this.mLocalNapRoleConnected, this.mDevice);
    return true;
  }

  public void clearProfileConnectionState()
  {
    Log.d("CachedBluetoothDevice", " Clearing all connection state for dev:" + this.mDevice.getName());
    Iterator localIterator = getProfiles().iterator();
    while (localIterator.hasNext())
    {
      LocalBluetoothProfile localLocalBluetoothProfile = (LocalBluetoothProfile)localIterator.next();
      this.mProfileConnectionState.put(localLocalBluetoothProfile, Integer.valueOf(0));
    }
  }

  public int compareTo(CachedBluetoothDevice paramCachedBluetoothDevice)
  {
    int i = 1;
    int j;
    if (paramCachedBluetoothDevice.isConnected())
    {
      j = i;
      if (!isConnected())
        break label40;
    }
    label40: for (int k = i; ; k = 0)
    {
      int m = j - k;
      if (m == 0)
        break label46;
      return m;
      j = 0;
      break;
    }
    label46: int n;
    if (paramCachedBluetoothDevice.getBondState() == 12)
    {
      n = i;
      if (getBondState() != 12)
        break label91;
    }
    label91: for (int i1 = i; ; i1 = 0)
    {
      int i2 = n - i1;
      if (i2 == 0)
        break label97;
      return i2;
      n = 0;
      break;
    }
    label97: int i3;
    if (paramCachedBluetoothDevice.mVisible)
    {
      i3 = i;
      if (!this.mVisible)
        break label134;
    }
    while (true)
    {
      int i4 = i3 - i;
      if (i4 == 0)
        break label139;
      return i4;
      i3 = 0;
      break;
      label134: i = 0;
    }
    label139: int i5 = paramCachedBluetoothDevice.mRssi - this.mRssi;
    if (i5 != 0)
      return i5;
    return this.mName.compareTo(paramCachedBluetoothDevice.mName);
  }

  void connect(boolean paramBoolean)
  {
    if (!ensurePaired())
      return;
    this.mConnectAttempted = SystemClock.elapsedRealtime();
    connectWithoutResettingTimer(paramBoolean);
  }

  void connectInt(LocalBluetoothProfile paramLocalBluetoothProfile)
  {
    while (true)
    {
      try
      {
        boolean bool = ensurePaired();
        if (!bool)
          return;
        if (paramLocalBluetoothProfile.connect(this.mDevice))
        {
          Log.d("CachedBluetoothDevice", "Command sent successfully:CONNECT " + describe(paramLocalBluetoothProfile));
          continue;
        }
      }
      finally
      {
      }
      Log.i("CachedBluetoothDevice", "Failed to connect " + paramLocalBluetoothProfile.toString() + " to " + this.mName);
    }
  }

  void connectProfile(LocalBluetoothProfile paramLocalBluetoothProfile)
  {
    this.mConnectAttempted = SystemClock.elapsedRealtime();
    this.mIsConnectingErrorPossible = true;
    connectInt(paramLocalBluetoothProfile);
    refresh();
  }

  void disconnect()
  {
    Iterator localIterator = this.mProfiles.iterator();
    while (localIterator.hasNext())
      disconnect((LocalBluetoothProfile)localIterator.next());
    PbapServerProfile localPbapServerProfile = this.mProfileManager.getPbapProfile();
    if (localPbapServerProfile.getConnectionStatus(this.mDevice) == 2)
      localPbapServerProfile.disconnect(this.mDevice);
  }

  void disconnect(LocalBluetoothProfile paramLocalBluetoothProfile)
  {
    if (paramLocalBluetoothProfile.disconnect(this.mDevice))
      Log.d("CachedBluetoothDevice", "Command sent successfully:DISCONNECT " + describe(paramLocalBluetoothProfile));
  }

  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof CachedBluetoothDevice)))
      return false;
    return this.mDevice.equals(((CachedBluetoothDevice)paramObject).mDevice);
  }

  int getBondState()
  {
    return this.mDevice.getBondState();
  }

  BluetoothClass getBtClass()
  {
    return this.mBtClass;
  }

  List<LocalBluetoothProfile> getConnectableProfiles()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.mProfiles.iterator();
    while (localIterator.hasNext())
    {
      LocalBluetoothProfile localLocalBluetoothProfile = (LocalBluetoothProfile)localIterator.next();
      if (localLocalBluetoothProfile.isConnectable())
        localArrayList.add(localLocalBluetoothProfile);
    }
    return localArrayList;
  }

  BluetoothDevice getDevice()
  {
    return this.mDevice;
  }

  int getMessagePermissionChoice()
  {
    return this.mMessagePermissionChoice;
  }

  String getName()
  {
    return this.mName;
  }

  int getPhonebookPermissionChoice()
  {
    return this.mPhonebookPermissionChoice;
  }

  int getProfileConnectionState(LocalBluetoothProfile paramLocalBluetoothProfile)
  {
    if ((this.mProfileConnectionState == null) || (this.mProfileConnectionState.get(paramLocalBluetoothProfile) == null))
    {
      int i = paramLocalBluetoothProfile.getConnectionStatus(this.mDevice);
      this.mProfileConnectionState.put(paramLocalBluetoothProfile, Integer.valueOf(i));
    }
    return ((Integer)this.mProfileConnectionState.get(paramLocalBluetoothProfile)).intValue();
  }

  List<LocalBluetoothProfile> getProfiles()
  {
    return Collections.unmodifiableList(this.mProfiles);
  }

  List<LocalBluetoothProfile> getRemovedProfiles()
  {
    return this.mRemovedProfiles;
  }

  public int hashCode()
  {
    return this.mDevice.getAddress().hashCode();
  }

  boolean isBusy()
  {
    Iterator localIterator = this.mProfiles.iterator();
    do
    {
      if (!localIterator.hasNext())
        break;
      i = getProfileConnectionState((LocalBluetoothProfile)localIterator.next());
    }
    while ((i != 1) && (i != 3));
    while (getBondState() == 11)
    {
      int i;
      return true;
    }
    return false;
  }

  boolean isConnected()
  {
    Iterator localIterator = this.mProfiles.iterator();
    while (localIterator.hasNext())
      if (getProfileConnectionState((LocalBluetoothProfile)localIterator.next()) == 2)
        return true;
    return false;
  }

  void onBondingDockConnect()
  {
    connect(false);
  }

  void onBondingStateChanged(int paramInt)
  {
    if (paramInt == 10)
    {
      this.mProfiles.clear();
      this.mConnectAfterPairing = false;
      setPhonebookPermissionChoice(0);
      setMessagePermissionChoice(0);
      this.mPhonebookRejectedTimes = 0;
      savePhonebookRejectTimes();
      this.mMessageRejectedTimes = 0;
      saveMessageRejectTimes();
    }
    refresh();
    if (paramInt == 12)
    {
      if (!this.mDevice.isBluetoothDock())
        break label78;
      onBondingDockConnect();
    }
    while (true)
    {
      this.mConnectAfterPairing = false;
      return;
      label78: if (this.mConnectAfterPairing)
        connect(false);
    }
  }

  void onProfileStateChanged(LocalBluetoothProfile paramLocalBluetoothProfile, int paramInt)
  {
    Log.d("CachedBluetoothDevice", "onProfileStateChanged: profile " + paramLocalBluetoothProfile + " newProfileState " + paramInt);
    if (this.mLocalAdapter.getBluetoothState() == 13)
      Log.d("CachedBluetoothDevice", " BT Turninig Off...Profile conn state change ignored...");
    do
    {
      do
      {
        return;
        this.mProfileConnectionState.put(paramLocalBluetoothProfile, Integer.valueOf(paramInt));
        if (paramInt != 2)
          break;
        if (!this.mProfiles.contains(paramLocalBluetoothProfile))
        {
          this.mRemovedProfiles.remove(paramLocalBluetoothProfile);
          this.mProfiles.add(paramLocalBluetoothProfile);
          if (((paramLocalBluetoothProfile instanceof PanProfile)) && (((PanProfile)paramLocalBluetoothProfile).isLocalRoleNap(this.mDevice)))
            this.mLocalNapRoleConnected = true;
        }
      }
      while (!(paramLocalBluetoothProfile instanceof MapProfile));
      paramLocalBluetoothProfile.setPreferred(this.mDevice, true);
      return;
      if (((paramLocalBluetoothProfile instanceof MapProfile)) && (paramInt == 0))
      {
        if (this.mProfiles.contains(paramLocalBluetoothProfile))
        {
          this.mRemovedProfiles.add(paramLocalBluetoothProfile);
          this.mProfiles.remove(paramLocalBluetoothProfile);
        }
        paramLocalBluetoothProfile.setPreferred(this.mDevice, false);
        return;
      }
    }
    while ((!this.mLocalNapRoleConnected) || (!(paramLocalBluetoothProfile instanceof PanProfile)) || (!((PanProfile)paramLocalBluetoothProfile).isLocalRoleNap(this.mDevice)) || (paramInt != 0));
    Log.d("CachedBluetoothDevice", "Removing PanProfile from device after NAP disconnect");
    this.mProfiles.remove(paramLocalBluetoothProfile);
    this.mRemovedProfiles.add(paramLocalBluetoothProfile);
    this.mLocalNapRoleConnected = false;
  }

  void onUuidChanged()
  {
    updateProfiles();
    if ((!this.mProfiles.isEmpty()) && (5000L + this.mConnectAttempted > SystemClock.elapsedRealtime()))
      connectWithoutResettingTimer(false);
    dispatchAttributesChanged();
  }

  void refresh()
  {
    dispatchAttributesChanged();
  }

  void refreshBtClass()
  {
    fetchBtClass();
    dispatchAttributesChanged();
  }

  void refreshName()
  {
    fetchName();
    dispatchAttributesChanged();
  }

  void registerCallback(Callback paramCallback)
  {
    synchronized (this.mCallbacks)
    {
      this.mCallbacks.add(paramCallback);
      return;
    }
  }

  void setBtClass(BluetoothClass paramBluetoothClass)
  {
    if ((paramBluetoothClass != null) && (this.mBtClass != paramBluetoothClass))
    {
      this.mBtClass = paramBluetoothClass;
      dispatchAttributesChanged();
    }
  }

  void setMessagePermissionChoice(int paramInt)
  {
    if (paramInt == 2)
    {
      this.mMessageRejectedTimes = (1 + this.mMessageRejectedTimes);
      saveMessageRejectTimes();
      if (this.mMessageRejectedTimes < 2)
        return;
    }
    this.mMessagePermissionChoice = paramInt;
    SharedPreferences.Editor localEditor = this.mContext.getSharedPreferences("bluetooth_message_permission", 0).edit();
    if (paramInt == 0)
      localEditor.remove(this.mDevice.getAddress());
    while (true)
    {
      localEditor.commit();
      return;
      localEditor.putInt(this.mDevice.getAddress(), paramInt);
    }
  }

  void setName(String paramString)
  {
    if (!this.mName.equals(paramString))
    {
      if (!TextUtils.isEmpty(paramString))
        break label34;
      this.mName = this.mDevice.getAddress();
    }
    while (true)
    {
      dispatchAttributesChanged();
      return;
      label34: this.mName = paramString;
      this.mDevice.setAlias(paramString);
    }
  }

  void setPhonebookPermissionChoice(int paramInt)
  {
    if (paramInt == 2)
    {
      this.mPhonebookRejectedTimes = (1 + this.mPhonebookRejectedTimes);
      savePhonebookRejectTimes();
      if (this.mPhonebookRejectedTimes < 2)
        return;
    }
    this.mPhonebookPermissionChoice = paramInt;
    SharedPreferences.Editor localEditor = this.mContext.getSharedPreferences("bluetooth_phonebook_permission", 0).edit();
    if (paramInt == 0)
      localEditor.remove(this.mDevice.getAddress());
    while (true)
    {
      localEditor.commit();
      return;
      localEditor.putInt(this.mDevice.getAddress(), paramInt);
    }
  }

  void setRssi(short paramShort)
  {
    if (this.mRssi != paramShort)
    {
      this.mRssi = paramShort;
      dispatchAttributesChanged();
    }
  }

  void setVisible(boolean paramBoolean)
  {
    if (this.mVisible != paramBoolean)
    {
      this.mVisible = paramBoolean;
      dispatchAttributesChanged();
    }
  }

  boolean startPairing()
  {
    if (this.mLocalAdapter.isDiscovering())
      this.mLocalAdapter.cancelDiscovery();
    if (!this.mDevice.createBond())
      return false;
    this.mConnectAfterPairing = true;
    return true;
  }

  public String toString()
  {
    return this.mDevice.toString();
  }

  void unpair()
  {
    int i = getBondState();
    if (i == 11)
      this.mDevice.cancelBondProcess();
    if (i != 10)
    {
      BluetoothDevice localBluetoothDevice = this.mDevice;
      if ((localBluetoothDevice != null) && (localBluetoothDevice.removeBond()))
        Log.d("CachedBluetoothDevice", "Command sent successfully:REMOVE_BOND " + describe(null));
    }
  }

  void unregisterCallback(Callback paramCallback)
  {
    synchronized (this.mCallbacks)
    {
      this.mCallbacks.remove(paramCallback);
      return;
    }
  }

  public static abstract interface Callback
  {
    public abstract void onDeviceAttributesChanged();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.CachedBluetoothDevice
 * JD-Core Version:    0.6.2
 */