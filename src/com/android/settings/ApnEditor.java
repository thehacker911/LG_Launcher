package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.TwoStatePreference;
import android.provider.Telephony.Carriers;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

public class ApnEditor extends PreferenceActivity
  implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener
{
  private static final String TAG = ApnEditor.class.getSimpleName();
  private static String sNotSet;
  private static final String[] sProjection = { "_id", "name", "apn", "proxy", "port", "user", "server", "password", "mmsc", "mcc", "mnc", "numeric", "mmsproxy", "mmsport", "authtype", "type", "protocol", "carrier_enabled", "bearer", "roaming_protocol", "mvno_type", "mvno_match_data" };
  private EditTextPreference mApn;
  private EditTextPreference mApnType;
  private ListPreference mAuthType;
  private ListPreference mBearer;
  private CheckBoxPreference mCarrierEnabled;
  private String mCurMcc;
  private String mCurMnc;
  private Cursor mCursor;
  private boolean mFirstTime;
  private EditTextPreference mMcc;
  private EditTextPreference mMmsPort;
  private EditTextPreference mMmsProxy;
  private EditTextPreference mMmsc;
  private EditTextPreference mMnc;
  private EditTextPreference mMvnoMatchData;
  private ListPreference mMvnoType;
  private EditTextPreference mName;
  private boolean mNewApn;
  private EditTextPreference mPassword;
  private EditTextPreference mPort;
  private ListPreference mProtocol;
  private EditTextPreference mProxy;
  private Resources mRes;
  private ListPreference mRoamingProtocol;
  private EditTextPreference mServer;
  private TelephonyManager mTelephonyManager;
  private Uri mUri;
  private EditTextPreference mUser;

  private String bearerDescription(String paramString)
  {
    int i = this.mBearer.findIndexOfValue(paramString);
    if (i == -1)
      return null;
    String[] arrayOfString = this.mRes.getStringArray(2131165229);
    try
    {
      String str = arrayOfString[i];
      return str;
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
    }
    return null;
  }

  private String checkNotSet(String paramString)
  {
    if ((paramString == null) || (paramString.equals(sNotSet)))
      paramString = "";
    return paramString;
  }

  private String checkNull(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0))
      paramString = sNotSet;
    return paramString;
  }

  private void deleteApn()
  {
    getContentResolver().delete(this.mUri, null, null);
    finish();
  }

  private void fillUi()
  {
    int i = 1;
    if (this.mFirstTime)
    {
      this.mFirstTime = false;
      this.mName.setText(this.mCursor.getString(i));
      this.mApn.setText(this.mCursor.getString(2));
      this.mProxy.setText(this.mCursor.getString(3));
      this.mPort.setText(this.mCursor.getString(4));
      this.mUser.setText(this.mCursor.getString(5));
      this.mServer.setText(this.mCursor.getString(6));
      this.mPassword.setText(this.mCursor.getString(7));
      this.mMmsProxy.setText(this.mCursor.getString(12));
      this.mMmsPort.setText(this.mCursor.getString(13));
      this.mMmsc.setText(this.mCursor.getString(8));
      this.mMcc.setText(this.mCursor.getString(9));
      this.mMnc.setText(this.mCursor.getString(10));
      this.mApnType.setText(this.mCursor.getString(15));
      if (this.mNewApn)
      {
        String str2 = SystemProperties.get("gsm.sim.operator.numeric");
        if ((str2 != null) && (str2.length() > 4))
        {
          String str3 = str2.substring(0, 3);
          String str4 = str2.substring(3);
          this.mMcc.setText(str3);
          this.mMnc.setText(str4);
          this.mCurMnc = str4;
          this.mCurMcc = str3;
        }
      }
      int m = this.mCursor.getInt(14);
      if (m != -1)
      {
        this.mAuthType.setValueIndex(m);
        this.mProtocol.setValue(this.mCursor.getString(16));
        this.mRoamingProtocol.setValue(this.mCursor.getString(19));
        CheckBoxPreference localCheckBoxPreference = this.mCarrierEnabled;
        if (this.mCursor.getInt(17) != i)
          break label879;
        label403: localCheckBoxPreference.setChecked(i);
        this.mBearer.setValue(this.mCursor.getString(18));
        this.mMvnoType.setValue(this.mCursor.getString(20));
        this.mMvnoMatchData.setEnabled(false);
        this.mMvnoMatchData.setText(this.mCursor.getString(21));
      }
    }
    else
    {
      this.mName.setSummary(checkNull(this.mName.getText()));
      this.mApn.setSummary(checkNull(this.mApn.getText()));
      this.mProxy.setSummary(checkNull(this.mProxy.getText()));
      this.mPort.setSummary(checkNull(this.mPort.getText()));
      this.mUser.setSummary(checkNull(this.mUser.getText()));
      this.mServer.setSummary(checkNull(this.mServer.getText()));
      this.mPassword.setSummary(starify(this.mPassword.getText()));
      this.mMmsProxy.setSummary(checkNull(this.mMmsProxy.getText()));
      this.mMmsPort.setSummary(checkNull(this.mMmsPort.getText()));
      this.mMmsc.setSummary(checkNull(this.mMmsc.getText()));
      this.mMcc.setSummary(checkNull(this.mMcc.getText()));
      this.mMnc.setSummary(checkNull(this.mMnc.getText()));
      this.mApnType.setSummary(checkNull(this.mApnType.getText()));
      String str1 = this.mAuthType.getValue();
      if (str1 == null)
        break label884;
      int k = Integer.parseInt(str1);
      this.mAuthType.setValueIndex(k);
      String[] arrayOfString = this.mRes.getStringArray(2131165225);
      this.mAuthType.setSummary(arrayOfString[k]);
    }
    while (true)
    {
      this.mProtocol.setSummary(checkNull(protocolDescription(this.mProtocol.getValue(), this.mProtocol)));
      this.mRoamingProtocol.setSummary(checkNull(protocolDescription(this.mRoamingProtocol.getValue(), this.mRoamingProtocol)));
      this.mBearer.setSummary(checkNull(bearerDescription(this.mBearer.getValue())));
      this.mMvnoType.setSummary(checkNull(mvnoDescription(this.mMvnoType.getValue())));
      this.mMvnoMatchData.setSummary(checkNull(this.mMvnoMatchData.getText()));
      return;
      this.mAuthType.setValue(null);
      break;
      label879: int j = 0;
      break label403;
      label884: this.mAuthType.setSummary(sNotSet);
    }
  }

  private String getErrorMsg()
  {
    String str1 = checkNotSet(this.mName.getText());
    String str2 = checkNotSet(this.mApn.getText());
    String str3 = checkNotSet(this.mMcc.getText());
    String str4 = checkNotSet(this.mMnc.getText());
    String str5;
    if (str1.length() < 1)
      str5 = this.mRes.getString(2131428204);
    int i;
    do
    {
      return str5;
      if (str2.length() < 1)
        return this.mRes.getString(2131428205);
      if (str3.length() != 3)
        return this.mRes.getString(2131428206);
      i = 0xFFFE & str4.length();
      str5 = null;
    }
    while (i == 2);
    return this.mRes.getString(2131428207);
  }

  private String mvnoDescription(String paramString)
  {
    int i = this.mMvnoType.findIndexOfValue(paramString);
    String str1 = this.mMvnoType.getValue();
    if (i == -1)
      return null;
    String[] arrayOfString = this.mRes.getStringArray(2131165231);
    if (arrayOfString[i].equals("None"))
      this.mMvnoMatchData.setEnabled(false);
    while (true)
    {
      if ((paramString != null) && (!paramString.equals(str1)))
      {
        if (!arrayOfString[i].equals("SPN"))
          break label116;
        this.mMvnoMatchData.setText(this.mTelephonyManager.getSimOperatorName());
      }
      try
      {
        while (true)
        {
          String str2 = arrayOfString[i];
          return str2;
          this.mMvnoMatchData.setEnabled(true);
          break;
          label116: if (arrayOfString[i].equals("IMSI"))
          {
            String str3 = SystemProperties.get("gsm.sim.operator.numeric");
            this.mMvnoMatchData.setText(str3 + "x");
          }
          else if (arrayOfString[i].equals("GID"))
          {
            this.mMvnoMatchData.setText(this.mTelephonyManager.getGroupIdLevel1());
          }
        }
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
      {
      }
    }
    return null;
  }

  private String protocolDescription(String paramString, ListPreference paramListPreference)
  {
    int i = paramListPreference.findIndexOfValue(paramString);
    if (i == -1)
      return null;
    String[] arrayOfString = this.mRes.getStringArray(2131165227);
    try
    {
      String str = arrayOfString[i];
      return str;
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
    }
    return null;
  }

  private String starify(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0))
      return sNotSet;
    char[] arrayOfChar = new char[paramString.length()];
    for (int i = 0; i < arrayOfChar.length; i++)
      arrayOfChar[i] = '*';
    return new String(arrayOfChar);
  }

  private boolean validateAndSave(boolean paramBoolean)
  {
    String str1 = checkNotSet(this.mName.getText());
    String str2 = checkNotSet(this.mApn.getText());
    String str3 = checkNotSet(this.mMcc.getText());
    String str4 = checkNotSet(this.mMnc.getText());
    if ((getErrorMsg() != null) && (!paramBoolean))
    {
      showDialog(0);
      return false;
    }
    if (!this.mCursor.moveToFirst())
    {
      Log.w(TAG, "Could not go to the first row in the Cursor when saving data.");
      return false;
    }
    if ((paramBoolean) && (this.mNewApn) && (str1.length() < 1) && (str2.length() < 1))
    {
      getContentResolver().delete(this.mUri, null, null);
      return false;
    }
    ContentValues localContentValues = new ContentValues();
    if (str1.length() < 1)
      str1 = getResources().getString(2131428935);
    localContentValues.put("name", str1);
    localContentValues.put("apn", str2);
    localContentValues.put("proxy", checkNotSet(this.mProxy.getText()));
    localContentValues.put("port", checkNotSet(this.mPort.getText()));
    localContentValues.put("mmsproxy", checkNotSet(this.mMmsProxy.getText()));
    localContentValues.put("mmsport", checkNotSet(this.mMmsPort.getText()));
    localContentValues.put("user", checkNotSet(this.mUser.getText()));
    localContentValues.put("server", checkNotSet(this.mServer.getText()));
    localContentValues.put("password", checkNotSet(this.mPassword.getText()));
    localContentValues.put("mmsc", checkNotSet(this.mMmsc.getText()));
    String str5 = this.mAuthType.getValue();
    if (str5 != null)
      localContentValues.put("authtype", Integer.valueOf(Integer.parseInt(str5)));
    localContentValues.put("protocol", checkNotSet(this.mProtocol.getValue()));
    localContentValues.put("roaming_protocol", checkNotSet(this.mRoamingProtocol.getValue()));
    localContentValues.put("type", checkNotSet(this.mApnType.getText()));
    localContentValues.put("mcc", str3);
    localContentValues.put("mnc", str4);
    localContentValues.put("numeric", str3 + str4);
    if ((this.mCurMnc != null) && (this.mCurMcc != null) && (this.mCurMnc.equals(str4)) && (this.mCurMcc.equals(str3)))
      localContentValues.put("current", Integer.valueOf(1));
    String str6 = this.mBearer.getValue();
    if (str6 != null)
      localContentValues.put("bearer", Integer.valueOf(Integer.parseInt(str6)));
    localContentValues.put("mvno_type", checkNotSet(this.mMvnoType.getValue()));
    localContentValues.put("mvno_match_data", checkNotSet(this.mMvnoMatchData.getText()));
    getContentResolver().update(this.mUri, localContentValues, null, null);
    return true;
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131034115);
    sNotSet = getResources().getString(2131428172);
    this.mName = ((EditTextPreference)findPreference("apn_name"));
    this.mApn = ((EditTextPreference)findPreference("apn_apn"));
    this.mProxy = ((EditTextPreference)findPreference("apn_http_proxy"));
    this.mPort = ((EditTextPreference)findPreference("apn_http_port"));
    this.mUser = ((EditTextPreference)findPreference("apn_user"));
    this.mServer = ((EditTextPreference)findPreference("apn_server"));
    this.mPassword = ((EditTextPreference)findPreference("apn_password"));
    this.mMmsProxy = ((EditTextPreference)findPreference("apn_mms_proxy"));
    this.mMmsPort = ((EditTextPreference)findPreference("apn_mms_port"));
    this.mMmsc = ((EditTextPreference)findPreference("apn_mmsc"));
    this.mMcc = ((EditTextPreference)findPreference("apn_mcc"));
    this.mMnc = ((EditTextPreference)findPreference("apn_mnc"));
    this.mApnType = ((EditTextPreference)findPreference("apn_type"));
    this.mAuthType = ((ListPreference)findPreference("auth_type"));
    this.mAuthType.setOnPreferenceChangeListener(this);
    this.mProtocol = ((ListPreference)findPreference("apn_protocol"));
    this.mProtocol.setOnPreferenceChangeListener(this);
    this.mRoamingProtocol = ((ListPreference)findPreference("apn_roaming_protocol"));
    this.mRoamingProtocol.setOnPreferenceChangeListener(this);
    this.mCarrierEnabled = ((CheckBoxPreference)findPreference("carrier_enabled"));
    this.mBearer = ((ListPreference)findPreference("bearer"));
    this.mBearer.setOnPreferenceChangeListener(this);
    this.mMvnoType = ((ListPreference)findPreference("mvno_type"));
    this.mMvnoType.setOnPreferenceChangeListener(this);
    this.mMvnoMatchData = ((EditTextPreference)findPreference("mvno_match_data"));
    this.mRes = getResources();
    Intent localIntent = getIntent();
    String str = localIntent.getAction();
    boolean bool;
    if (paramBundle == null)
    {
      bool = true;
      this.mFirstTime = bool;
      if (!str.equals("android.intent.action.EDIT"))
        break label442;
      this.mUri = localIntent.getData();
    }
    while (true)
    {
      this.mCursor = managedQuery(this.mUri, sProjection, null, null);
      this.mCursor.moveToFirst();
      this.mTelephonyManager = ((TelephonyManager)getSystemService("phone"));
      fillUi();
      return;
      bool = false;
      break;
      label442: if (!str.equals("android.intent.action.INSERT"))
        break label587;
      if ((this.mFirstTime) || (paramBundle.getInt("pos") == 0));
      for (this.mUri = getContentResolver().insert(localIntent.getData(), new ContentValues()); ; this.mUri = ContentUris.withAppendedId(Telephony.Carriers.CONTENT_URI, paramBundle.getInt("pos")))
      {
        this.mNewApn = true;
        if (this.mUri != null)
          break;
        Log.w(TAG, "Failed to insert new telephony provider into " + getIntent().getData());
        finish();
        return;
      }
      setResult(-1, new Intent().setAction(this.mUri.toString()));
    }
    label587: finish();
  }

  protected Dialog onCreateDialog(int paramInt)
  {
    if (paramInt == 0)
    {
      String str = getErrorMsg();
      return new AlertDialog.Builder(this).setTitle(2131428203).setPositiveButton(17039370, null).setMessage(str).create();
    }
    return super.onCreateDialog(paramInt);
  }

  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    super.onCreateOptionsMenu(paramMenu);
    if (!this.mNewApn)
      paramMenu.add(0, 1, 0, 2131428199).setIcon(2130837591);
    paramMenu.add(0, 2, 0, 2131428201).setIcon(17301582);
    paramMenu.add(0, 3, 0, 2131428202).setIcon(17301560);
    return true;
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    switch (paramInt)
    {
    default:
      return super.onKeyDown(paramInt, paramKeyEvent);
    case 4:
    }
    if (validateAndSave(false))
      finish();
    return true;
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    boolean bool = true;
    switch (paramMenuItem.getItemId())
    {
    default:
      bool = super.onOptionsItemSelected(paramMenuItem);
    case 1:
    case 2:
      do
      {
        return bool;
        deleteApn();
        return bool;
      }
      while (!validateAndSave(false));
      finish();
      return bool;
    case 3:
    }
    if (this.mNewApn)
      getContentResolver().delete(this.mUri, null, null);
    finish();
    return bool;
  }

  public void onPause()
  {
    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    super.onPause();
  }

  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    String str1 = paramPreference.getKey();
    if ("auth_type".equals(str1));
    while (true)
    {
      try
      {
        int i = Integer.parseInt((String)paramObject);
        this.mAuthType.setValueIndex(i);
        String[] arrayOfString = this.mRes.getStringArray(2131165225);
        this.mAuthType.setSummary(arrayOfString[i]);
        return true;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        return false;
      }
      if ("apn_protocol".equals(str1))
      {
        String str5 = protocolDescription((String)paramObject, this.mProtocol);
        if (str5 == null)
          return false;
        this.mProtocol.setSummary(str5);
        this.mProtocol.setValue((String)paramObject);
      }
      else if ("apn_roaming_protocol".equals(str1))
      {
        String str4 = protocolDescription((String)paramObject, this.mRoamingProtocol);
        if (str4 == null)
          return false;
        this.mRoamingProtocol.setSummary(str4);
        this.mRoamingProtocol.setValue((String)paramObject);
      }
      else if ("bearer".equals(str1))
      {
        String str3 = bearerDescription((String)paramObject);
        if (str3 == null)
          return false;
        this.mBearer.setValue((String)paramObject);
        this.mBearer.setSummary(str3);
      }
      else if ("mvno_type".equals(str1))
      {
        String str2 = mvnoDescription((String)paramObject);
        if (str2 == null)
          return false;
        this.mMvnoType.setValue((String)paramObject);
        this.mMvnoType.setSummary(str2);
      }
    }
  }

  protected void onPrepareDialog(int paramInt, Dialog paramDialog)
  {
    super.onPrepareDialog(paramInt, paramDialog);
    if (paramInt == 0)
    {
      String str = getErrorMsg();
      if (str != null)
        ((AlertDialog)paramDialog).setMessage(str);
    }
  }

  public void onResume()
  {
    super.onResume();
    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
  }

  protected void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if (validateAndSave(true))
      paramBundle.putInt("pos", this.mCursor.getInt(0));
  }

  public void onSharedPreferenceChanged(SharedPreferences paramSharedPreferences, String paramString)
  {
    Preference localPreference = findPreference(paramString);
    if (localPreference != null)
    {
      if (localPreference.equals(this.mPassword))
        localPreference.setSummary(starify(paramSharedPreferences.getString(paramString, "")));
    }
    else
      return;
    localPreference.setSummary(checkNull(paramSharedPreferences.getString(paramString, "")));
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.ApnEditor
 * JD-Core Version:    0.6.2
 */