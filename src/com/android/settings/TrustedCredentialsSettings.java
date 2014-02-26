package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.http.SslCertificate;
import android.net.http.SslCertificate.DName;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserManager;
import android.security.IKeyChainService;
import android.security.KeyChain;
import android.security.KeyChain.KeyChainConnection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import com.android.org.conscrypt.TrustedCertificateStore;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TrustedCredentialsSettings extends Fragment
{
  private boolean mChallengeRequested;
  private boolean mChallengeSucceeded;
  private final TrustedCertificateStore mStore = new TrustedCertificateStore();
  private TabHost mTabHost;
  private UserManager mUserManager;

  private void addTab(Tab paramTab)
  {
    TabHost.TabSpec localTabSpec = this.mTabHost.newTabSpec(paramTab.mTag).setIndicator(getActivity().getString(paramTab.mLabel)).setContent(paramTab.mView);
    this.mTabHost.addTab(localTabSpec);
    ListView localListView = (ListView)this.mTabHost.findViewById(paramTab.mList);
    final TrustedCertificateAdapter localTrustedCertificateAdapter = new TrustedCertificateAdapter(paramTab, null);
    localListView.setAdapter(localTrustedCertificateAdapter);
    localListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        TrustedCredentialsSettings.this.showCertDialog(localTrustedCertificateAdapter.getItem(paramAnonymousInt));
      }
    });
  }

  private void ensurePin()
  {
    if (!this.mChallengeSucceeded)
    {
      UserManager localUserManager = UserManager.get(getActivity());
      if ((!this.mChallengeRequested) && (localUserManager.hasRestrictionsChallenge()))
      {
        startActivityForResult(new Intent("android.intent.action.RESTRICTIONS_CHALLENGE"), 12309);
        this.mChallengeRequested = true;
      }
    }
    this.mChallengeSucceeded = false;
  }

  private void showCertDialog(final CertHolder paramCertHolder)
  {
    View localView = paramCertHolder.mSslCert.inflateCertificateView(getActivity());
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getActivity());
    localBuilder.setTitle(17040680);
    localBuilder.setView(localView);
    localBuilder.setPositiveButton(17039370, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface.dismiss();
      }
    });
    final AlertDialog localAlertDialog = localBuilder.create();
    ViewGroup localViewGroup = (ViewGroup)localView.findViewById(16909052);
    Button localButton = (Button)LayoutInflater.from(getActivity()).inflate(2130968716, localViewGroup, false);
    localViewGroup.addView(localButton);
    localButton.setText(paramCertHolder.mTab.getButtonLabel(paramCertHolder));
    localButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if ((TrustedCredentialsSettings.this.mUserManager.hasRestrictionsChallenge()) && (!TrustedCredentialsSettings.this.mChallengeSucceeded))
        {
          TrustedCredentialsSettings.this.ensurePin();
          return;
        }
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(TrustedCredentialsSettings.this.getActivity());
        localBuilder.setMessage(TrustedCredentialsSettings.Tab.access$3100(TrustedCredentialsSettings.CertHolder.access$100(paramCertHolder), paramCertHolder));
        localBuilder.setPositiveButton(17039379, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
          {
            new TrustedCredentialsSettings.AliasOperation(TrustedCredentialsSettings.this, TrustedCredentialsSettings.3.this.val$certHolder, null).execute(new Void[0]);
            paramAnonymous2DialogInterface.dismiss();
            TrustedCredentialsSettings.3.this.val$certDialog.dismiss();
          }
        });
        localBuilder.setNegativeButton(17039369, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
          {
            paramAnonymous2DialogInterface.cancel();
          }
        });
        localBuilder.create().show();
      }
    });
    localAlertDialog.show();
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt1 == 12309)
    {
      this.mChallengeRequested = false;
      if (paramInt2 == -1)
        this.mChallengeSucceeded = true;
      return;
    }
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mUserManager = ((UserManager)getActivity().getSystemService("user"));
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mTabHost = ((TabHost)paramLayoutInflater.inflate(2130968717, paramViewGroup, false));
    this.mTabHost.setup();
    addTab(Tab.SYSTEM);
    addTab(Tab.USER);
    if ((getActivity().getIntent() != null) && ("com.android.settings.TRUSTED_CREDENTIALS_USER".equals(getActivity().getIntent().getAction())))
      this.mTabHost.setCurrentTabByTag(Tab.USER.mTag);
    return this.mTabHost;
  }

  private class AliasOperation extends AsyncTask<Void, Void, Boolean>
  {
    private final TrustedCredentialsSettings.CertHolder mCertHolder;

    private AliasOperation(TrustedCredentialsSettings.CertHolder arg2)
    {
      Object localObject;
      this.mCertHolder = localObject;
    }

    protected Boolean doInBackground(Void[] paramArrayOfVoid)
    {
      try
      {
        localKeyChainConnection = KeyChain.bind(TrustedCredentialsSettings.this.getActivity());
        localIKeyChainService = localKeyChainConnection.getService();
      }
      catch (CertificateEncodingException localCertificateEncodingException)
      {
        try
        {
          IKeyChainService localIKeyChainService;
          if (TrustedCredentialsSettings.CertHolder.access$000(this.mCertHolder))
          {
            localIKeyChainService.installCaCertificate(TrustedCredentialsSettings.CertHolder.access$3300(this.mCertHolder).getEncoded());
            Boolean localBoolean2 = Boolean.valueOf(true);
            return localBoolean2;
          }
          Boolean localBoolean1 = Boolean.valueOf(localIKeyChainService.deleteCaCertificate(TrustedCredentialsSettings.CertHolder.access$3400(this.mCertHolder)));
          return localBoolean1;
          localCertificateEncodingException = localCertificateEncodingException;
          return Boolean.valueOf(false);
        }
        finally
        {
          KeyChain.KeyChainConnection localKeyChainConnection;
          localKeyChainConnection.close();
        }
      }
      catch (IllegalStateException localIllegalStateException)
      {
        return Boolean.valueOf(false);
      }
      catch (RemoteException localRemoteException)
      {
        return Boolean.valueOf(false);
      }
      catch (InterruptedException localInterruptedException)
      {
        Thread.currentThread().interrupt();
      }
      return Boolean.valueOf(false);
    }

    protected void onPostExecute(Boolean paramBoolean)
    {
      TrustedCredentialsSettings.Tab.access$3500(TrustedCredentialsSettings.CertHolder.access$100(this.mCertHolder), paramBoolean.booleanValue(), this.mCertHolder);
    }
  }

  private static class CertHolder
    implements Comparable<CertHolder>
  {
    private final TrustedCredentialsSettings.TrustedCertificateAdapter mAdapter;
    private final String mAlias;
    private boolean mDeleted;
    private final SslCertificate mSslCert;
    private final TrustedCertificateStore mStore;
    private final String mSubjectPrimary;
    private final String mSubjectSecondary;
    private final TrustedCredentialsSettings.Tab mTab;
    private final X509Certificate mX509Cert;

    private CertHolder(TrustedCertificateStore paramTrustedCertificateStore, TrustedCredentialsSettings.TrustedCertificateAdapter paramTrustedCertificateAdapter, TrustedCredentialsSettings.Tab paramTab, String paramString, X509Certificate paramX509Certificate)
    {
      this.mStore = paramTrustedCertificateStore;
      this.mAdapter = paramTrustedCertificateAdapter;
      this.mTab = paramTab;
      this.mAlias = paramString;
      this.mX509Cert = paramX509Certificate;
      this.mSslCert = new SslCertificate(paramX509Certificate);
      String str1 = this.mSslCert.getIssuedTo().getCName();
      String str2 = this.mSslCert.getIssuedTo().getOName();
      String str3 = this.mSslCert.getIssuedTo().getUName();
      if (!str2.isEmpty())
        if (!str1.isEmpty())
        {
          this.mSubjectPrimary = str2;
          this.mSubjectSecondary = str1;
        }
      while (true)
      {
        this.mDeleted = TrustedCredentialsSettings.Tab.access$2500(this.mTab, this.mStore, this.mAlias);
        return;
        this.mSubjectPrimary = str2;
        this.mSubjectSecondary = str3;
        continue;
        if (!str1.isEmpty())
        {
          this.mSubjectPrimary = str1;
          this.mSubjectSecondary = "";
        }
        else
        {
          this.mSubjectPrimary = this.mSslCert.getIssuedTo().getDName();
          this.mSubjectSecondary = "";
        }
      }
    }

    public int compareTo(CertHolder paramCertHolder)
    {
      int i = this.mSubjectPrimary.compareToIgnoreCase(paramCertHolder.mSubjectPrimary);
      if (i != 0)
        return i;
      return this.mSubjectSecondary.compareToIgnoreCase(paramCertHolder.mSubjectSecondary);
    }

    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof CertHolder))
        return false;
      CertHolder localCertHolder = (CertHolder)paramObject;
      return this.mAlias.equals(localCertHolder.mAlias);
    }

    public int hashCode()
    {
      return this.mAlias.hashCode();
    }
  }

  private static enum Tab
  {
    private final boolean mCheckbox;
    private final int mLabel;
    private final int mList;
    private final int mProgress;
    private final String mTag;
    private final int mView;

    static
    {
      Tab[] arrayOfTab = new Tab[2];
      arrayOfTab[0] = SYSTEM;
      arrayOfTab[1] = USER;
    }

    private Tab(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
    {
      this.mTag = paramString;
      this.mLabel = paramInt1;
      this.mView = paramInt2;
      this.mProgress = paramInt3;
      this.mList = paramInt4;
      this.mCheckbox = paramBoolean;
    }

    private boolean deleted(TrustedCertificateStore paramTrustedCertificateStore, String paramString)
    {
      int i = TrustedCredentialsSettings.4.$SwitchMap$com$android$settings$TrustedCredentialsSettings$Tab[ordinal()];
      boolean bool1 = false;
      switch (i)
      {
      default:
        throw new AssertionError();
      case 1:
        boolean bool2 = paramTrustedCertificateStore.containsAlias(paramString);
        bool1 = false;
        if (!bool2)
          bool1 = true;
        break;
      case 2:
      }
      return bool1;
    }

    private Set<String> getAliases(TrustedCertificateStore paramTrustedCertificateStore)
    {
      switch (TrustedCredentialsSettings.4.$SwitchMap$com$android$settings$TrustedCredentialsSettings$Tab[ordinal()])
      {
      default:
        throw new AssertionError();
      case 1:
        return paramTrustedCertificateStore.allSystemAliases();
      case 2:
      }
      return paramTrustedCertificateStore.userAliases();
    }

    private int getButtonConfirmation(TrustedCredentialsSettings.CertHolder paramCertHolder)
    {
      switch (TrustedCredentialsSettings.4.$SwitchMap$com$android$settings$TrustedCredentialsSettings$Tab[ordinal()])
      {
      default:
        throw new AssertionError();
      case 1:
        if (paramCertHolder.mDeleted)
          return 2131429176;
        return 2131429177;
      case 2:
      }
      return 2131429178;
    }

    private int getButtonLabel(TrustedCredentialsSettings.CertHolder paramCertHolder)
    {
      switch (TrustedCredentialsSettings.4.$SwitchMap$com$android$settings$TrustedCredentialsSettings$Tab[ordinal()])
      {
      default:
        throw new AssertionError();
      case 1:
        if (paramCertHolder.mDeleted)
          return 2131429174;
        return 2131429173;
      case 2:
      }
      return 2131429175;
    }

    private void postOperationUpdate(boolean paramBoolean, TrustedCredentialsSettings.CertHolder paramCertHolder)
    {
      if (paramBoolean)
      {
        boolean bool;
        if (paramCertHolder.mTab.mCheckbox)
          if (!paramCertHolder.mDeleted)
          {
            bool = true;
            TrustedCredentialsSettings.CertHolder.access$002(paramCertHolder, bool);
          }
        while (true)
        {
          paramCertHolder.mAdapter.notifyDataSetChanged();
          return;
          bool = false;
          break;
          TrustedCredentialsSettings.TrustedCertificateAdapter.access$300(paramCertHolder.mAdapter).remove(paramCertHolder);
        }
      }
      TrustedCredentialsSettings.TrustedCertificateAdapter.access$400(paramCertHolder.mAdapter);
    }
  }

  private class TrustedCertificateAdapter extends BaseAdapter
  {
    private final List<TrustedCredentialsSettings.CertHolder> mCertHolders = new ArrayList();
    private final TrustedCredentialsSettings.Tab mTab;

    private TrustedCertificateAdapter(TrustedCredentialsSettings.Tab arg2)
    {
      Object localObject;
      this.mTab = localObject;
      load();
    }

    private void load()
    {
      new AliasLoader(null).execute(new Void[0]);
    }

    public int getCount()
    {
      return this.mCertHolders.size();
    }

    public TrustedCredentialsSettings.CertHolder getItem(int paramInt)
    {
      return (TrustedCredentialsSettings.CertHolder)this.mCertHolders.get(paramInt);
    }

    public long getItemId(int paramInt)
    {
      return paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      TrustedCredentialsSettings.ViewHolder localViewHolder;
      CheckBox localCheckBox;
      if (paramView == null)
      {
        paramView = LayoutInflater.from(TrustedCredentialsSettings.this.getActivity()).inflate(2130968715, paramViewGroup, false);
        localViewHolder = new TrustedCredentialsSettings.ViewHolder(null);
        TrustedCredentialsSettings.ViewHolder.access$1302(localViewHolder, (TextView)paramView.findViewById(2131231067));
        TrustedCredentialsSettings.ViewHolder.access$1402(localViewHolder, (TextView)paramView.findViewById(2131231068));
        TrustedCredentialsSettings.ViewHolder.access$1502(localViewHolder, (CheckBox)paramView.findViewById(2131231069));
        paramView.setTag(localViewHolder);
        TrustedCredentialsSettings.CertHolder localCertHolder = (TrustedCredentialsSettings.CertHolder)this.mCertHolders.get(paramInt);
        TrustedCredentialsSettings.ViewHolder.access$1300(localViewHolder).setText(localCertHolder.mSubjectPrimary);
        TrustedCredentialsSettings.ViewHolder.access$1400(localViewHolder).setText(localCertHolder.mSubjectSecondary);
        if (this.mTab.mCheckbox)
        {
          localCheckBox = TrustedCredentialsSettings.ViewHolder.access$1500(localViewHolder);
          if (localCertHolder.mDeleted)
            break label182;
        }
      }
      label182: for (boolean bool = true; ; bool = false)
      {
        localCheckBox.setChecked(bool);
        TrustedCredentialsSettings.ViewHolder.access$1500(localViewHolder).setVisibility(0);
        return paramView;
        localViewHolder = (TrustedCredentialsSettings.ViewHolder)paramView.getTag();
        break;
      }
    }

    private class AliasLoader extends AsyncTask<Void, Integer, List<TrustedCredentialsSettings.CertHolder>>
    {
      View mList;
      ProgressBar mProgressBar;

      private AliasLoader()
      {
      }

      protected List<TrustedCredentialsSettings.CertHolder> doInBackground(Void[] paramArrayOfVoid)
      {
        Set localSet = TrustedCredentialsSettings.TrustedCertificateAdapter.this.mTab.getAliases(TrustedCredentialsSettings.this.mStore);
        int i = localSet.size();
        int j = 0;
        ArrayList localArrayList = new ArrayList(i);
        Iterator localIterator = localSet.iterator();
        while (localIterator.hasNext())
        {
          String str = (String)localIterator.next();
          X509Certificate localX509Certificate = (X509Certificate)TrustedCredentialsSettings.this.mStore.getCertificate(str, true);
          localArrayList.add(new TrustedCredentialsSettings.CertHolder(TrustedCredentialsSettings.this.mStore, TrustedCredentialsSettings.TrustedCertificateAdapter.this, TrustedCredentialsSettings.TrustedCertificateAdapter.this.mTab, str, localX509Certificate, null));
          Integer[] arrayOfInteger = new Integer[2];
          j++;
          arrayOfInteger[0] = Integer.valueOf(j);
          arrayOfInteger[1] = Integer.valueOf(i);
          publishProgress(arrayOfInteger);
        }
        Collections.sort(localArrayList);
        return localArrayList;
      }

      protected void onPostExecute(List<TrustedCredentialsSettings.CertHolder> paramList)
      {
        TrustedCredentialsSettings.TrustedCertificateAdapter.this.mCertHolders.clear();
        TrustedCredentialsSettings.TrustedCertificateAdapter.this.mCertHolders.addAll(paramList);
        TrustedCredentialsSettings.TrustedCertificateAdapter.this.notifyDataSetChanged();
        TrustedCredentialsSettings.this.mTabHost.getTabContentView();
        this.mProgressBar.setVisibility(8);
        this.mList.setVisibility(0);
        this.mProgressBar.setProgress(0);
      }

      protected void onPreExecute()
      {
        FrameLayout localFrameLayout = TrustedCredentialsSettings.this.mTabHost.getTabContentView();
        this.mProgressBar = ((ProgressBar)localFrameLayout.findViewById(TrustedCredentialsSettings.TrustedCertificateAdapter.access$2000(TrustedCredentialsSettings.TrustedCertificateAdapter.this).mProgress));
        this.mList = localFrameLayout.findViewById(TrustedCredentialsSettings.TrustedCertificateAdapter.access$2000(TrustedCredentialsSettings.TrustedCertificateAdapter.this).mList);
        this.mProgressBar.setVisibility(0);
        this.mList.setVisibility(8);
      }

      protected void onProgressUpdate(Integer[] paramArrayOfInteger)
      {
        int i = paramArrayOfInteger[0].intValue();
        int j = paramArrayOfInteger[1].intValue();
        if (j != this.mProgressBar.getMax())
          this.mProgressBar.setMax(j);
        this.mProgressBar.setProgress(i);
      }
    }
  }

  private static class ViewHolder
  {
    private CheckBox mCheckBox;
    private TextView mSubjectPrimaryView;
    private TextView mSubjectSecondaryView;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.TrustedCredentialsSettings
 * JD-Core Version:    0.6.2
 */