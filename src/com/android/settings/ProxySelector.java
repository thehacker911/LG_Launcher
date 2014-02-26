package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.ProxyProperties;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxySelector extends Fragment
  implements DialogCreatable
{
  private static final Pattern EXCLUSION_PATTERN = Pattern.compile("$|^(\\*)?\\.?[a-zA-Z0-9\\_]+(\\-[a-zA-Z0-9\\_]+)*(\\.[a-zA-Z0-9\\_]+(\\-[a-zA-Z0-9\\_]+)*)*$");
  private static final Pattern HOSTNAME_PATTERN = Pattern.compile("^$|^[a-zA-Z0-9\\_]+(\\-[a-zA-Z0-9\\_]+)*(\\.[a-zA-Z0-9\\_]+(\\-[a-zA-Z0-9\\_]+)*)*$");
  Button mClearButton;
  View.OnClickListener mClearHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      ProxySelector.this.mHostnameField.setText("");
      ProxySelector.this.mPortField.setText("");
      ProxySelector.this.mExclusionListField.setText("");
    }
  };
  Button mDefaultButton;
  View.OnClickListener mDefaultHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      ProxySelector.this.populateFields();
    }
  };
  private SettingsPreferenceFragment.SettingsDialogFragment mDialogFragment;
  EditText mExclusionListField;
  EditText mHostnameField;
  Button mOKButton;
  View.OnClickListener mOKHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (ProxySelector.this.saveToDb())
        ProxySelector.this.getActivity().onBackPressed();
    }
  };
  View.OnFocusChangeListener mOnFocusChangeHandler = new View.OnFocusChangeListener()
  {
    public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
    {
      if (paramAnonymousBoolean)
        Selection.selectAll((Spannable)((TextView)paramAnonymousView).getText());
    }
  };
  EditText mPortField;
  private View mView;

  private void initView(View paramView)
  {
    this.mHostnameField = ((EditText)paramView.findViewById(2131230985));
    this.mHostnameField.setOnFocusChangeListener(this.mOnFocusChangeHandler);
    this.mPortField = ((EditText)paramView.findViewById(2131230986));
    this.mPortField.setOnClickListener(this.mOKHandler);
    this.mPortField.setOnFocusChangeListener(this.mOnFocusChangeHandler);
    this.mExclusionListField = ((EditText)paramView.findViewById(2131230987));
    this.mExclusionListField.setOnFocusChangeListener(this.mOnFocusChangeHandler);
    this.mOKButton = ((Button)paramView.findViewById(2131230988));
    this.mOKButton.setOnClickListener(this.mOKHandler);
    this.mClearButton = ((Button)paramView.findViewById(2131230989));
    this.mClearButton.setOnClickListener(this.mClearHandler);
    this.mDefaultButton = ((Button)paramView.findViewById(2131230990));
    this.mDefaultButton.setOnClickListener(this.mDefaultHandler);
  }

  private void showDialog(int paramInt)
  {
    if (this.mDialogFragment != null)
      Log.e("ProxySelector", "Old dialog fragment not null!");
    this.mDialogFragment = new SettingsPreferenceFragment.SettingsDialogFragment(this, paramInt);
    this.mDialogFragment.show(getActivity().getFragmentManager(), Integer.toString(paramInt));
  }

  public static int validate(String paramString1, String paramString2, String paramString3)
  {
    int i = 2131427505;
    Matcher localMatcher = HOSTNAME_PATTERN.matcher(paramString1);
    String[] arrayOfString = paramString3.split(",");
    if (!localMatcher.matches())
      i = 2131427501;
    while (true)
    {
      return i;
      int j = arrayOfString.length;
      for (int k = 0; k < j; k++)
      {
        String str = arrayOfString[k];
        if (!EXCLUSION_PATTERN.matcher(str).matches())
          return 2131427502;
      }
      if ((paramString1.length() > 0) && (paramString2.length() == 0))
        return 2131427503;
      if (paramString2.length() > 0)
        if (paramString1.length() == 0)
          return 2131427504;
      try
      {
        int m = Integer.parseInt(paramString2);
        if ((m > 0) && (m <= 65535))
          return 0;
      }
      catch (NumberFormatException localNumberFormatException)
      {
      }
    }
    return i;
  }

  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    if (((DevicePolicyManager)getActivity().getSystemService("device_policy")).getGlobalProxyAdmin() == null);
    for (boolean bool = true; ; bool = false)
    {
      this.mHostnameField.setEnabled(bool);
      this.mPortField.setEnabled(bool);
      this.mExclusionListField.setEnabled(bool);
      this.mOKButton.setEnabled(bool);
      this.mClearButton.setEnabled(bool);
      this.mDefaultButton.setEnabled(bool);
      return;
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
  }

  public Dialog onCreateDialog(int paramInt)
  {
    AlertDialog localAlertDialog = null;
    if (paramInt == 0)
    {
      String str1 = this.mHostnameField.getText().toString().trim();
      String str2 = this.mPortField.getText().toString().trim();
      String str3 = this.mExclusionListField.getText().toString().trim();
      String str4 = getActivity().getString(validate(str1, str2, str3));
      localAlertDialog = new AlertDialog.Builder(getActivity()).setTitle(2131427499).setPositiveButton(2131427500, null).setMessage(str4).create();
    }
    return localAlertDialog;
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mView = paramLayoutInflater.inflate(2130968696, paramViewGroup, false);
    initView(this.mView);
    populateFields();
    return this.mView;
  }

  void populateFields()
  {
    Activity localActivity = getActivity();
    String str1 = "";
    int i = -1;
    String str2 = "";
    ProxyProperties localProxyProperties = ((ConnectivityManager)getActivity().getSystemService("connectivity")).getGlobalProxy();
    if (localProxyProperties != null)
    {
      str1 = localProxyProperties.getHost();
      i = localProxyProperties.getPort();
      str2 = localProxyProperties.getExclusionList();
    }
    if (str1 == null)
      str1 = "";
    this.mHostnameField.setText(str1);
    if (i == -1);
    for (String str3 = ""; ; str3 = Integer.toString(i))
    {
      this.mPortField.setText(str3);
      this.mExclusionListField.setText(str2);
      Intent localIntent = localActivity.getIntent();
      String str4 = localIntent.getStringExtra("button-label");
      if (!TextUtils.isEmpty(str4))
        this.mOKButton.setText(str4);
      String str5 = localIntent.getStringExtra("title");
      if (!TextUtils.isEmpty(str5))
        localActivity.setTitle(str5);
      return;
    }
  }

  boolean saveToDb()
  {
    String str1 = this.mHostnameField.getText().toString().trim();
    String str2 = this.mPortField.getText().toString().trim();
    String str3 = this.mExclusionListField.getText().toString().trim();
    if (validate(str1, str2, str3) > 0)
    {
      showDialog(0);
      return false;
    }
    int i = str2.length();
    int j = 0;
    if (i > 0);
    try
    {
      int k = Integer.parseInt(str2);
      j = k;
      ProxyProperties localProxyProperties = new ProxyProperties(str1, j, str3);
      ((ConnectivityManager)getActivity().getSystemService("connectivity")).setGlobalProxy(localProxyProperties);
      return true;
    }
    catch (NumberFormatException localNumberFormatException)
    {
    }
    return false;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.ProxySelector
 * JD-Core Version:    0.6.2
 */