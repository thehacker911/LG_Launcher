package com.android.settings.net;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.INetworkStatsSession;
import android.net.NetworkStats;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.os.RemoteException;

public class SummaryForAllUidLoader extends AsyncTaskLoader<NetworkStats>
{
  private final Bundle mArgs;
  private final INetworkStatsSession mSession;

  public SummaryForAllUidLoader(Context paramContext, INetworkStatsSession paramINetworkStatsSession, Bundle paramBundle)
  {
    super(paramContext);
    this.mSession = paramINetworkStatsSession;
    this.mArgs = paramBundle;
  }

  public static Bundle buildArgs(NetworkTemplate paramNetworkTemplate, long paramLong1, long paramLong2)
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("template", paramNetworkTemplate);
    localBundle.putLong("start", paramLong1);
    localBundle.putLong("end", paramLong2);
    return localBundle;
  }

  public NetworkStats loadInBackground()
  {
    NetworkTemplate localNetworkTemplate = (NetworkTemplate)this.mArgs.getParcelable("template");
    long l1 = this.mArgs.getLong("start");
    long l2 = this.mArgs.getLong("end");
    try
    {
      NetworkStats localNetworkStats = this.mSession.getSummaryForAllUid(localNetworkTemplate, l1, l2, false);
      return localNetworkStats;
    }
    catch (RemoteException localRemoteException)
    {
    }
    return null;
  }

  protected void onReset()
  {
    super.onReset();
    cancelLoad();
  }

  protected void onStartLoading()
  {
    super.onStartLoading();
    forceLoad();
  }

  protected void onStopLoading()
  {
    super.onStopLoading();
    cancelLoad();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.net.SummaryForAllUidLoader
 * JD-Core Version:    0.6.2
 */