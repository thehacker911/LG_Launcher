package com.android.settings;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.Telephony.Carriers;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import com.android.internal.telephony.PhoneConstants.DataState;
import java.util.ArrayList;
import java.util.Iterator;

public class ApnSettings extends PreferenceActivity
  implements Preference.OnPreferenceChangeListener
{
  private static final Uri DEFAULTAPN_URI = Uri.parse("content://telephony/carriers/restore");
  private static final Uri PREFERAPN_URI = Uri.parse("content://telephony/carriers/preferapn");
  private static boolean mRestoreDefaultApnMode;
  private IntentFilter mMobileStateFilter;
  private final BroadcastReceiver mMobileStateReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      PhoneConstants.DataState localDataState;
      if (paramAnonymousIntent.getAction().equals("android.intent.action.ANY_DATA_STATE"))
        localDataState = ApnSettings.getMobileDataState(paramAnonymousIntent);
      switch (ApnSettings.2.$SwitchMap$com$android$internal$telephony$PhoneConstants$DataState[localDataState.ordinal()])
      {
      default:
        return;
      case 1:
      }
      if (!ApnSettings.mRestoreDefaultApnMode)
      {
        ApnSettings.this.fillList();
        return;
      }
      ApnSettings.this.showDialog(1001);
    }
  };
  private RestoreApnProcessHandler mRestoreApnProcessHandler;
  private RestoreApnUiHandler mRestoreApnUiHandler;
  private HandlerThread mRestoreDefaultApnThread;
  private String mSelectedKey;

  private void addNewApn()
  {
    startActivity(new Intent("android.intent.action.INSERT", Telephony.Carriers.CONTENT_URI));
  }

  private void fillList()
  {
    String str1 = "numeric=\"" + SystemProperties.get("gsm.sim.operator.numeric", "") + "\"";
    Cursor localCursor = getContentResolver().query(Telephony.Carriers.CONTENT_URI, new String[] { "_id", "name", "apn", "type" }, str1, null, "name ASC");
    if (localCursor != null)
    {
      PreferenceGroup localPreferenceGroup = (PreferenceGroup)findPreference("apn_list");
      localPreferenceGroup.removeAll();
      ArrayList localArrayList = new ArrayList();
      this.mSelectedKey = getSelectedApnKey();
      localCursor.moveToFirst();
      if (!localCursor.isAfterLast())
      {
        String str2 = localCursor.getString(1);
        String str3 = localCursor.getString(2);
        String str4 = localCursor.getString(0);
        String str5 = localCursor.getString(3);
        ApnPreference localApnPreference = new ApnPreference(this);
        localApnPreference.setKey(str4);
        localApnPreference.setTitle(str2);
        localApnPreference.setSummary(str3);
        localApnPreference.setPersistent(false);
        localApnPreference.setOnPreferenceChangeListener(this);
        boolean bool;
        if ((str5 == null) || (!str5.equals("mms")))
        {
          bool = true;
          label218: localApnPreference.setSelectable(bool);
          if (!bool)
            break label277;
          if ((this.mSelectedKey != null) && (this.mSelectedKey.equals(str4)))
            localApnPreference.setChecked();
          localPreferenceGroup.addPreference(localApnPreference);
        }
        while (true)
        {
          localCursor.moveToNext();
          break;
          bool = false;
          break label218;
          label277: localArrayList.add(localApnPreference);
        }
      }
      localCursor.close();
      Iterator localIterator = localArrayList.iterator();
      while (localIterator.hasNext())
        localPreferenceGroup.addPreference((Preference)localIterator.next());
    }
  }

  private static PhoneConstants.DataState getMobileDataState(Intent paramIntent)
  {
    String str = paramIntent.getStringExtra("state");
    if (str != null)
      return (PhoneConstants.DataState)Enum.valueOf(PhoneConstants.DataState.class, str);
    return PhoneConstants.DataState.DISCONNECTED;
  }

  private String getSelectedApnKey()
  {
    Cursor localCursor = getContentResolver().query(PREFERAPN_URI, new String[] { "_id" }, null, null, "name ASC");
    int i = localCursor.getCount();
    String str = null;
    if (i > 0)
    {
      localCursor.moveToFirst();
      str = localCursor.getString(0);
    }
    localCursor.close();
    return str;
  }

  private boolean restoreDefaultApn()
  {
    showDialog(1001);
    mRestoreDefaultApnMode = true;
    if (this.mRestoreApnUiHandler == null)
      this.mRestoreApnUiHandler = new RestoreApnUiHandler(null);
    if ((this.mRestoreApnProcessHandler == null) || (this.mRestoreDefaultApnThread == null))
    {
      this.mRestoreDefaultApnThread = new HandlerThread("Restore default APN Handler: Process Thread");
      this.mRestoreDefaultApnThread.start();
      this.mRestoreApnProcessHandler = new RestoreApnProcessHandler(this.mRestoreDefaultApnThread.getLooper(), this.mRestoreApnUiHandler);
    }
    this.mRestoreApnProcessHandler.sendEmptyMessage(1);
    return true;
  }

  private void setSelectedApnKey(String paramString)
  {
    this.mSelectedKey = paramString;
    ContentResolver localContentResolver = getContentResolver();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("apn_id", this.mSelectedKey);
    localContentResolver.update(PREFERAPN_URI, localContentValues, null, null);
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131034116);
    getListView().setItemsCanFocus(true);
    this.mMobileStateFilter = new IntentFilter("android.intent.action.ANY_DATA_STATE");
  }

  protected Dialog onCreateDialog(int paramInt)
  {
    if (paramInt == 1001)
    {
      ProgressDialog localProgressDialog = new ProgressDialog(this);
      localProgressDialog.setMessage(getResources().getString(2131428208));
      localProgressDialog.setCancelable(false);
      return localProgressDialog;
    }
    return null;
  }

  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    super.onCreateOptionsMenu(paramMenu);
    paramMenu.add(0, 1, 0, getResources().getString(2131428200)).setIcon(17301555).setShowAsAction(1);
    paramMenu.add(0, 2, 0, getResources().getString(2131428209)).setIcon(17301589);
    return true;
  }

  protected void onDestroy()
  {
    super.onDestroy();
    if (this.mRestoreDefaultApnThread != null)
      this.mRestoreDefaultApnThread.quit();
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default:
      return super.onOptionsItemSelected(paramMenuItem);
    case 1:
      addNewApn();
      return true;
    case 2:
    }
    restoreDefaultApn();
    return true;
  }

  protected void onPause()
  {
    super.onPause();
    unregisterReceiver(this.mMobileStateReceiver);
  }

  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    Log.d("ApnSettings", "onPreferenceChange(): Preference - " + paramPreference + ", newValue - " + paramObject + ", newValue type - " + paramObject.getClass());
    if ((paramObject instanceof String))
      setSelectedApnKey((String)paramObject);
    return true;
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    int i = Integer.parseInt(paramPreference.getKey());
    startActivity(new Intent("android.intent.action.EDIT", ContentUris.withAppendedId(Telephony.Carriers.CONTENT_URI, i)));
    return true;
  }

  protected void onPrepareDialog(int paramInt, Dialog paramDialog)
  {
    if (paramInt == 1001)
      getPreferenceScreen().setEnabled(false);
  }

  protected void onResume()
  {
    super.onResume();
    registerReceiver(this.mMobileStateReceiver, this.mMobileStateFilter);
    if (!mRestoreDefaultApnMode)
    {
      fillList();
      return;
    }
    showDialog(1001);
  }

  private class RestoreApnProcessHandler extends Handler
  {
    private Handler mRestoreApnUiHandler;

    public RestoreApnProcessHandler(Looper paramHandler, Handler arg3)
    {
      super();
      Object localObject;
      this.mRestoreApnUiHandler = localObject;
    }

    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default:
        return;
      case 1:
      }
      ApnSettings.this.getContentResolver().delete(ApnSettings.DEFAULTAPN_URI, null, null);
      this.mRestoreApnUiHandler.sendEmptyMessage(2);
    }
  }

  private class RestoreApnUiHandler extends Handler
  {
    private RestoreApnUiHandler()
    {
    }

    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default:
        return;
      case 2:
      }
      ApnSettings.this.fillList();
      ApnSettings.this.getPreferenceScreen().setEnabled(true);
      ApnSettings.access$102(false);
      ApnSettings.this.dismissDialog(1001);
      Toast.makeText(ApnSettings.this, ApnSettings.this.getResources().getString(2131428210), 1).show();
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.ApnSettings
 * JD-Core Version:    0.6.2
 */