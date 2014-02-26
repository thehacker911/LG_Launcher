package com.android.settings.net;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.INetworkStatsSession;
import android.net.NetworkStatsHistory;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.SparseBooleanArray;
import com.android.settings.DataUsageSummary.AppItem;

public class ChartDataLoader extends AsyncTaskLoader<ChartData>
{
  private final Bundle mArgs;
  private final INetworkStatsSession mSession;

  public ChartDataLoader(Context paramContext, INetworkStatsSession paramINetworkStatsSession, Bundle paramBundle)
  {
    super(paramContext);
    this.mSession = paramINetworkStatsSession;
    this.mArgs = paramBundle;
  }

  public static Bundle buildArgs(NetworkTemplate paramNetworkTemplate, DataUsageSummary.AppItem paramAppItem)
  {
    return buildArgs(paramNetworkTemplate, paramAppItem, 10);
  }

  public static Bundle buildArgs(NetworkTemplate paramNetworkTemplate, DataUsageSummary.AppItem paramAppItem, int paramInt)
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("template", paramNetworkTemplate);
    localBundle.putParcelable("app", paramAppItem);
    localBundle.putInt("fields", paramInt);
    return localBundle;
  }

  private NetworkStatsHistory collectHistoryForUid(NetworkTemplate paramNetworkTemplate, int paramInt1, int paramInt2, NetworkStatsHistory paramNetworkStatsHistory)
    throws RemoteException
  {
    NetworkStatsHistory localNetworkStatsHistory = this.mSession.getHistoryForUid(paramNetworkTemplate, paramInt1, paramInt2, 0, 10);
    if (paramNetworkStatsHistory != null)
    {
      paramNetworkStatsHistory.recordEntireHistory(localNetworkStatsHistory);
      return paramNetworkStatsHistory;
    }
    return localNetworkStatsHistory;
  }

  private ChartData loadInBackground(NetworkTemplate paramNetworkTemplate, DataUsageSummary.AppItem paramAppItem, int paramInt)
    throws RemoteException
  {
    ChartData localChartData = new ChartData();
    localChartData.network = this.mSession.getHistoryForNetwork(paramNetworkTemplate, paramInt);
    if (paramAppItem != null)
    {
      int i = paramAppItem.uids.size();
      for (int j = 0; j < i; j++)
      {
        int k = paramAppItem.uids.keyAt(j);
        localChartData.detailDefault = collectHistoryForUid(paramNetworkTemplate, k, 0, localChartData.detailDefault);
        localChartData.detailForeground = collectHistoryForUid(paramNetworkTemplate, k, 1, localChartData.detailForeground);
      }
      if (i > 0)
      {
        localChartData.detail = new NetworkStatsHistory(localChartData.detailForeground.getBucketDuration());
        localChartData.detail.recordEntireHistory(localChartData.detailDefault);
        localChartData.detail.recordEntireHistory(localChartData.detailForeground);
      }
    }
    else
    {
      return localChartData;
    }
    localChartData.detailDefault = new NetworkStatsHistory(3600000L);
    localChartData.detailForeground = new NetworkStatsHistory(3600000L);
    localChartData.detail = new NetworkStatsHistory(3600000L);
    return localChartData;
  }

  public ChartData loadInBackground()
  {
    NetworkTemplate localNetworkTemplate = (NetworkTemplate)this.mArgs.getParcelable("template");
    DataUsageSummary.AppItem localAppItem = (DataUsageSummary.AppItem)this.mArgs.getParcelable("app");
    int i = this.mArgs.getInt("fields");
    try
    {
      ChartData localChartData = loadInBackground(localNetworkTemplate, localAppItem, i);
      return localChartData;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("problem reading network stats", localRemoteException);
    }
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
 * Qualified Name:     com.android.settings.net.ChartDataLoader
 * JD-Core Version:    0.6.2
 */