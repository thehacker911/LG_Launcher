package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;

public class BandMode extends Activity
{
  private static final String[] BAND_NAMES = { "Automatic", "EURO Band", "USA Band", "JAPAN Band", "AUS Band", "AUS2 Band" };
  private ListView mBandList;
  private ArrayAdapter mBandListAdapter;
  private AdapterView.OnItemClickListener mBandSelectionHandler = new AdapterView.OnItemClickListener()
  {
    public void onItemClick(AdapterView paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      BandMode.this.getWindow().setFeatureInt(5, -1);
      BandMode.access$002(BandMode.this, (BandMode.BandListItem)paramAnonymousAdapterView.getAdapter().getItem(paramAnonymousInt));
      Message localMessage = BandMode.this.mHandler.obtainMessage(200);
      BandMode.this.mPhone.setBandMode(BandMode.this.mTargetBand.getBand(), localMessage);
    }
  };
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default:
      case 100:
      case 200:
      }
      AsyncResult localAsyncResult1;
      do
      {
        return;
        AsyncResult localAsyncResult2 = (AsyncResult)paramAnonymousMessage.obj;
        BandMode.this.bandListLoaded(localAsyncResult2);
        return;
        localAsyncResult1 = (AsyncResult)paramAnonymousMessage.obj;
        BandMode.this.getWindow().setFeatureInt(5, -2);
      }
      while (BandMode.this.isFinishing());
      BandMode.this.displayBandSelectionResult(localAsyncResult1.exception);
    }
  };
  private Phone mPhone = null;
  private DialogInterface mProgressPanel;
  private BandListItem mTargetBand = null;

  private void bandListLoaded(AsyncResult paramAsyncResult)
  {
    if (this.mProgressPanel != null)
      this.mProgressPanel.dismiss();
    clearList();
    Object localObject = paramAsyncResult.result;
    int i = 0;
    if (localObject != null)
    {
      int[] arrayOfInt = (int[])paramAsyncResult.result;
      int k = arrayOfInt[0];
      i = 0;
      if (k > 0)
      {
        for (int m = 1; m < k; m++)
        {
          BandListItem localBandListItem2 = new BandListItem(arrayOfInt[m]);
          this.mBandListAdapter.add(localBandListItem2);
        }
        i = 1;
      }
    }
    if (i == 0)
      for (int j = 0; j < 6; j++)
      {
        BandListItem localBandListItem1 = new BandListItem(j);
        this.mBandListAdapter.add(localBandListItem1);
      }
    this.mBandList.requestFocus();
  }

  private void clearList()
  {
    while (this.mBandListAdapter.getCount() > 0)
      this.mBandListAdapter.remove(this.mBandListAdapter.getItem(0));
  }

  private void displayBandSelectionResult(Throwable paramThrowable)
  {
    String str1 = getString(2131427542) + " [" + this.mTargetBand.toString() + "] ";
    if (paramThrowable != null);
    for (String str2 = str1 + getString(2131427543); ; str2 = str1 + getString(2131427544))
    {
      this.mProgressPanel = new AlertDialog.Builder(this).setMessage(str2).setPositiveButton(17039370, null).show();
      return;
    }
  }

  private void loadBandList()
  {
    String str = getString(2131427541);
    this.mProgressPanel = new AlertDialog.Builder(this).setMessage(str).show();
    Message localMessage = this.mHandler.obtainMessage(100);
    this.mPhone.queryAvailableBandMode(localMessage);
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    requestWindowFeature(5);
    setContentView(2130968589);
    setTitle(getString(2131427540));
    getWindow().setLayout(-1, -2);
    this.mPhone = PhoneFactory.getDefaultPhone();
    this.mBandList = ((ListView)findViewById(2131230739));
    this.mBandListAdapter = new ArrayAdapter(this, 17367043);
    this.mBandList.setAdapter(this.mBandListAdapter);
    this.mBandList.setOnItemClickListener(this.mBandSelectionHandler);
    loadBandList();
  }

  private static class BandListItem
  {
    private int mBandMode = 0;

    public BandListItem(int paramInt)
    {
      this.mBandMode = paramInt;
    }

    public int getBand()
    {
      return this.mBandMode;
    }

    public String toString()
    {
      return BandMode.BAND_NAMES[this.mBandMode];
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.BandMode
 * JD-Core Version:    0.6.2
 */