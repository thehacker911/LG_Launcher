package com.android.settings.applications;

import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageManager.Stub;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

final class CanBeOnSdCardChecker
{
  int mInstallLocation;
  final IPackageManager mPm = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));

  boolean check(ApplicationInfo paramApplicationInfo)
  {
    boolean bool;
    if ((0x40000 & paramApplicationInfo.flags) != 0)
      bool = true;
    int k;
    do
    {
      int j;
      do
      {
        int i;
        do
        {
          return bool;
          i = 0x1 & paramApplicationInfo.flags;
          bool = false;
        }
        while (i != 0);
        if ((paramApplicationInfo.installLocation == 2) || (paramApplicationInfo.installLocation == 0))
          return true;
        j = paramApplicationInfo.installLocation;
        bool = false;
      }
      while (j != -1);
      k = this.mInstallLocation;
      bool = false;
    }
    while (k != 2);
    return true;
  }

  void init()
  {
    try
    {
      this.mInstallLocation = this.mPm.getInstallLocation();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("CanBeOnSdCardChecker", "Is Package Manager running?");
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.applications.CanBeOnSdCardChecker
 * JD-Core Version:    0.6.2
 */