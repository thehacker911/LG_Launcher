package com.android.settings.nfc;

import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import com.android.internal.content.PackageMonitor;
import com.android.settings.HelpUtils;
import com.android.settings.SettingsPreferenceFragment;
import java.util.Iterator;
import java.util.List;

public class PaymentSettings extends SettingsPreferenceFragment
  implements View.OnClickListener
{
  private final Handler mHandler = new Handler()
  {
    public void dispatchMessage(Message paramAnonymousMessage)
    {
      PaymentSettings.this.refresh();
    }
  };
  private LayoutInflater mInflater;
  private PaymentBackend mPaymentBackend;
  private final PackageMonitor mSettingsPackageMonitor = new SettingsPackageMonitor(null);

  public void onClick(View paramView)
  {
    if ((paramView.getTag() instanceof PaymentBackend.PaymentAppInfo))
    {
      PaymentBackend.PaymentAppInfo localPaymentAppInfo = (PaymentBackend.PaymentAppInfo)paramView.getTag();
      if (localPaymentAppInfo.componentName != null)
        this.mPaymentBackend.setDefaultPaymentApp(localPaymentAppInfo.componentName);
      refresh();
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mPaymentBackend = new PaymentBackend(getActivity());
    this.mInflater = ((LayoutInflater)getSystemService("layout_inflater"));
    setHasOptionsMenu(true);
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
    String str = Settings.Secure.getString(getContentResolver(), "payment_service_search_uri");
    if (!TextUtils.isEmpty(str))
    {
      MenuItem localMenuItem = paramMenu.add(2131429245);
      localMenuItem.setShowAsActionFlags(1);
      localMenuItem.setIntent(new Intent("android.intent.action.VIEW", Uri.parse(str)));
    }
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
    View localView = this.mInflater.inflate(2130968652, paramViewGroup, false);
    ((TextView)localView.findViewById(2131230917)).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        String str = PaymentSettings.this.getResources().getString(2131429269);
        if (!TextUtils.isEmpty(str))
        {
          Intent localIntent = new Intent("android.intent.action.VIEW", HelpUtils.uriWithAddedParameters(PaymentSettings.this.getActivity(), Uri.parse(str)));
          localIntent.setFlags(276824064);
          PaymentSettings.this.startActivity(localIntent);
          return;
        }
        Log.e("PaymentSettings", "Help url not set.");
      }
    });
    return localView;
  }

  public void onPause()
  {
    this.mSettingsPackageMonitor.unregister();
    super.onPause();
  }

  public void onResume()
  {
    super.onResume();
    this.mSettingsPackageMonitor.register(getActivity(), getActivity().getMainLooper(), false);
    refresh();
  }

  public void refresh()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity());
    List localList = this.mPaymentBackend.getPaymentAppInfos();
    if ((localList != null) && (localList.size() > 0))
    {
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        PaymentBackend.PaymentAppInfo localPaymentAppInfo = (PaymentBackend.PaymentAppInfo)localIterator.next();
        PaymentAppPreference localPaymentAppPreference = new PaymentAppPreference(getActivity(), localPaymentAppInfo, this);
        localPaymentAppPreference.setTitle(localPaymentAppInfo.caption);
        if (localPaymentAppInfo.banner != null)
          localPreferenceScreen.addPreference(localPaymentAppPreference);
        else
          Log.e("PaymentSettings", "Couldn't load banner drawable of service " + localPaymentAppInfo.componentName);
      }
    }
    TextView localTextView1 = (TextView)getView().findViewById(2131230916);
    TextView localTextView2 = (TextView)getView().findViewById(2131230917);
    ImageView localImageView = (ImageView)getView().findViewById(2131230915);
    if (localPreferenceScreen.getPreferenceCount() == 0)
    {
      localTextView1.setVisibility(0);
      localTextView2.setVisibility(0);
      localImageView.setVisibility(0);
      getListView().setVisibility(8);
    }
    while (true)
    {
      setPreferenceScreen(localPreferenceScreen);
      return;
      localTextView1.setVisibility(8);
      localTextView2.setVisibility(8);
      localImageView.setVisibility(8);
      getListView().setVisibility(0);
    }
  }

  public static class PaymentAppPreference extends Preference
  {
    private final PaymentBackend.PaymentAppInfo appInfo;
    private final View.OnClickListener listener;

    public PaymentAppPreference(Context paramContext, PaymentBackend.PaymentAppInfo paramPaymentAppInfo, View.OnClickListener paramOnClickListener)
    {
      super();
      setLayoutResource(2130968653);
      this.appInfo = paramPaymentAppInfo;
      this.listener = paramOnClickListener;
    }

    protected void onBindView(View paramView)
    {
      super.onBindView(paramView);
      paramView.setOnClickListener(this.listener);
      paramView.setTag(this.appInfo);
      ((RadioButton)paramView.findViewById(16908313)).setChecked(this.appInfo.isDefault);
      ((ImageView)paramView.findViewById(2131230919)).setImageDrawable(this.appInfo.banner);
    }
  }

  private class SettingsPackageMonitor extends PackageMonitor
  {
    private SettingsPackageMonitor()
    {
    }

    public void onPackageAdded(String paramString, int paramInt)
    {
      PaymentSettings.this.mHandler.obtainMessage().sendToTarget();
    }

    public void onPackageAppeared(String paramString, int paramInt)
    {
      PaymentSettings.this.mHandler.obtainMessage().sendToTarget();
    }

    public void onPackageDisappeared(String paramString, int paramInt)
    {
      PaymentSettings.this.mHandler.obtainMessage().sendToTarget();
    }

    public void onPackageRemoved(String paramString, int paramInt)
    {
      PaymentSettings.this.mHandler.obtainMessage().sendToTarget();
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.nfc.PaymentSettings
 * JD-Core Version:    0.6.2
 */