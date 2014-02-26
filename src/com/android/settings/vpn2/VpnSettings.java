package com.android.settings.vpn2;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.IConnectivityManager;
import android.net.IConnectivityManager.Stub;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.security.Credentials;
import android.security.KeyStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnProfile;
import com.android.internal.util.ArrayUtils;
import com.android.settings.SettingsPreferenceFragment;
import com.google.android.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class VpnSettings extends SettingsPreferenceFragment
  implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener, Handler.Callback, Preference.OnPreferenceClickListener
{
  private VpnDialog mDialog;
  private LegacyVpnInfo mInfo;
  private final KeyStore mKeyStore = KeyStore.getInstance();
  private HashMap<String, VpnPreference> mPreferences = new HashMap();
  private String mSelectedKey;
  private final IConnectivityManager mService = IConnectivityManager.Stub.asInterface(ServiceManager.getService("connectivity"));
  private boolean mUnlocking = false;
  private Handler mUpdater;

  private void connect(VpnProfile paramVpnProfile)
    throws Exception
  {
    try
    {
      this.mService.startLegacyVpn(paramVpnProfile);
      return;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      Toast.makeText(getActivity(), 2131429169, 1).show();
    }
  }

  private void disconnect(String paramString)
  {
    if ((this.mInfo != null) && (paramString.equals(this.mInfo.key)));
    try
    {
      this.mService.prepareVpn("[Legacy VPN]", "[Legacy VPN]");
      return;
    }
    catch (Exception localException)
    {
    }
  }

  private static List<VpnProfile> loadVpnProfiles(KeyStore paramKeyStore, int[] paramArrayOfInt)
  {
    ArrayList localArrayList = Lists.newArrayList();
    String[] arrayOfString = paramKeyStore.saw("VPN_");
    if (arrayOfString != null)
    {
      int i = arrayOfString.length;
      for (int j = 0; j < i; j++)
      {
        String str = arrayOfString[j];
        VpnProfile localVpnProfile = VpnProfile.decode(str, paramKeyStore.get("VPN_" + str));
        if ((localVpnProfile != null) && (!ArrayUtils.contains(paramArrayOfInt, localVpnProfile.type)))
          localArrayList.add(localVpnProfile);
      }
    }
    return localArrayList;
  }

  protected int getHelpResource()
  {
    return 2131429258;
  }

  public boolean handleMessage(Message paramMessage)
  {
    this.mUpdater.removeMessages(0);
    if (isResumed());
    try
    {
      LegacyVpnInfo localLegacyVpnInfo = this.mService.getLegacyVpnInfo();
      if (this.mInfo != null)
      {
        VpnPreference localVpnPreference2 = (VpnPreference)this.mPreferences.get(this.mInfo.key);
        if (localVpnPreference2 != null)
          localVpnPreference2.update(-1);
        this.mInfo = null;
      }
      if (localLegacyVpnInfo != null)
      {
        VpnPreference localVpnPreference1 = (VpnPreference)this.mPreferences.get(localLegacyVpnInfo.key);
        if (localVpnPreference1 != null)
        {
          localVpnPreference1.update(localLegacyVpnInfo.state);
          this.mInfo = localLegacyVpnInfo;
        }
      }
      label111: this.mUpdater.sendEmptyMessageDelayed(0, 1000L);
      return true;
    }
    catch (Exception localException)
    {
      break label111;
    }
  }

  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    VpnProfile localVpnProfile;
    if (paramInt == -1)
    {
      localVpnProfile = this.mDialog.getProfile();
      this.mKeyStore.put("VPN_" + localVpnProfile.key, localVpnProfile.encode(), -1, 1);
      VpnPreference localVpnPreference1 = (VpnPreference)this.mPreferences.get(localVpnProfile.key);
      if (localVpnPreference1 == null)
        break label100;
      disconnect(localVpnProfile.key);
      localVpnPreference1.update(localVpnProfile);
    }
    while (true)
    {
      if (!this.mDialog.isEditing());
      try
      {
        connect(localVpnProfile);
        return;
        label100: VpnPreference localVpnPreference2 = new VpnPreference(getActivity(), localVpnProfile);
        localVpnPreference2.setOnPreferenceClickListener(this);
        this.mPreferences.put(localVpnProfile.key, localVpnPreference2);
        getPreferenceScreen().addPreference(localVpnPreference2);
      }
      catch (Exception localException)
      {
        Log.e("VpnSettings", "connect", localException);
      }
    }
  }

  public boolean onContextItemSelected(MenuItem paramMenuItem)
  {
    if (this.mDialog != null)
    {
      Log.v("VpnSettings", "onContextItemSelected() is called when mDialog != null");
      return false;
    }
    VpnPreference localVpnPreference = (VpnPreference)this.mPreferences.get(this.mSelectedKey);
    if (localVpnPreference == null)
    {
      Log.v("VpnSettings", "onContextItemSelected() is called but no preference is found");
      return false;
    }
    switch (paramMenuItem.getItemId())
    {
    default:
      return false;
    case 2131429163:
      this.mDialog = new VpnDialog(getActivity(), this, localVpnPreference.getProfile(), true);
      this.mDialog.setOnDismissListener(this);
      this.mDialog.show();
      return true;
    case 2131429164:
    }
    disconnect(this.mSelectedKey);
    getPreferenceScreen().removePreference(localVpnPreference);
    this.mPreferences.remove(this.mSelectedKey);
    this.mKeyStore.delete("VPN_" + this.mSelectedKey);
    return true;
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setHasOptionsMenu(true);
    addPreferencesFromResource(2131034169);
    if (paramBundle != null)
    {
      VpnProfile localVpnProfile = VpnProfile.decode(paramBundle.getString("VpnKey"), paramBundle.getByteArray("VpnProfile"));
      if (localVpnProfile != null)
        this.mDialog = new VpnDialog(getActivity(), this, localVpnProfile, paramBundle.getBoolean("VpnEditing"));
    }
  }

  public void onCreateContextMenu(ContextMenu paramContextMenu, View paramView, ContextMenu.ContextMenuInfo paramContextMenuInfo)
  {
    if (this.mDialog != null);
    Preference localPreference;
    do
    {
      Log.v("VpnSettings", "onCreateContextMenu() is called when mDialog != null");
      do
        return;
      while (!(paramContextMenuInfo instanceof AdapterView.AdapterContextMenuInfo));
      localPreference = (Preference)getListView().getItemAtPosition(((AdapterView.AdapterContextMenuInfo)paramContextMenuInfo).position);
    }
    while (!(localPreference instanceof VpnPreference));
    VpnProfile localVpnProfile = ((VpnPreference)localPreference).getProfile();
    this.mSelectedKey = localVpnProfile.key;
    paramContextMenu.setHeaderTitle(localVpnProfile.name);
    paramContextMenu.add(0, 2131429163, 0, 2131429163);
    paramContextMenu.add(0, 2131429164, 0, 2131429164);
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
    paramMenuInflater.inflate(2131755012, paramMenu);
  }

  public void onDismiss(DialogInterface paramDialogInterface)
  {
    this.mDialog = null;
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default:
      return super.onOptionsItemSelected(paramMenuItem);
    case 2131231271:
      for (long l = System.currentTimeMillis(); this.mPreferences.containsKey(Long.toHexString(l)); l += 1L);
      this.mDialog = new VpnDialog(getActivity(), this, new VpnProfile(Long.toHexString(l)), true);
      this.mDialog.setOnDismissListener(this);
      this.mDialog.show();
      return true;
    case 2131231272:
    }
    LockdownConfigFragment.show(this);
    return true;
  }

  public void onPause()
  {
    super.onPause();
    if (this.mDialog != null)
    {
      this.mDialog.setOnDismissListener(null);
      this.mDialog.dismiss();
    }
    if (getView() != null)
      unregisterForContextMenu(getListView());
  }

  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (this.mDialog != null)
    {
      Log.v("VpnSettings", "onPreferenceClick() is called when mDialog != null");
      return true;
    }
    VpnProfile localVpnProfile;
    if ((paramPreference instanceof VpnPreference))
    {
      localVpnProfile = ((VpnPreference)paramPreference).getProfile();
      if ((this.mInfo != null) && (localVpnProfile.key.equals(this.mInfo.key)) && (this.mInfo.state == 3))
        try
        {
          this.mInfo.intent.send();
          return true;
        }
        catch (Exception localException)
        {
        }
    }
    long l;
    for (this.mDialog = new VpnDialog(getActivity(), this, localVpnProfile, false); ; this.mDialog = new VpnDialog(getActivity(), this, new VpnProfile(Long.toHexString(l)), true))
    {
      this.mDialog.setOnDismissListener(this);
      this.mDialog.show();
      return true;
      for (l = System.currentTimeMillis(); this.mPreferences.containsKey(Long.toHexString(l)); l += 1L);
    }
  }

  public void onPrepareOptionsMenu(Menu paramMenu)
  {
    super.onPrepareOptionsMenu(paramMenu);
    if (SystemProperties.getBoolean("persist.radio.imsregrequired", false))
      paramMenu.findItem(2131231272).setVisible(false);
  }

  public void onResume()
  {
    super.onResume();
    if (getActivity().getIntent().getBooleanExtra("android.net.vpn.PICK_LOCKDOWN", false))
      LockdownConfigFragment.show(this);
    if (!this.mKeyStore.isUnlocked())
    {
      if (!this.mUnlocking)
        Credentials.getInstance().unlock(getActivity());
      while (true)
      {
        boolean bool1 = this.mUnlocking;
        boolean bool2 = false;
        if (!bool1)
          bool2 = true;
        this.mUnlocking = bool2;
        return;
        finishFragment();
      }
    }
    this.mUnlocking = false;
    if (this.mPreferences.size() == 0)
    {
      PreferenceScreen localPreferenceScreen = getPreferenceScreen();
      Activity localActivity = getActivity();
      Iterator localIterator = loadVpnProfiles(this.mKeyStore, new int[0]).iterator();
      while (localIterator.hasNext())
      {
        VpnProfile localVpnProfile = (VpnProfile)localIterator.next();
        VpnPreference localVpnPreference = new VpnPreference(localActivity, localVpnProfile);
        localVpnPreference.setOnPreferenceClickListener(this);
        this.mPreferences.put(localVpnProfile.key, localVpnPreference);
        localPreferenceScreen.addPreference(localVpnPreference);
      }
    }
    if (this.mDialog != null)
    {
      this.mDialog.setOnDismissListener(this);
      this.mDialog.show();
    }
    if (this.mUpdater == null)
      this.mUpdater = new Handler(this);
    this.mUpdater.sendEmptyMessage(0);
    registerForContextMenu(getListView());
  }

  public void onSaveInstanceState(Bundle paramBundle)
  {
    if (this.mDialog != null)
    {
      VpnProfile localVpnProfile = this.mDialog.getProfile();
      paramBundle.putString("VpnKey", localVpnProfile.key);
      paramBundle.putByteArray("VpnProfile", localVpnProfile.encode());
      paramBundle.putBoolean("VpnEditing", this.mDialog.isEditing());
    }
  }

  public static class LockdownConfigFragment extends DialogFragment
  {
    private int mCurrentIndex;
    private List<VpnProfile> mProfiles;
    private List<CharSequence> mTitles;

    private static String getStringOrNull(KeyStore paramKeyStore, String paramString)
    {
      byte[] arrayOfByte = paramKeyStore.get("LOCKDOWN_VPN");
      if (arrayOfByte == null)
        return null;
      return new String(arrayOfByte);
    }

    private void initProfiles(KeyStore paramKeyStore, Resources paramResources)
    {
      String str = getStringOrNull(paramKeyStore, "LOCKDOWN_VPN");
      this.mProfiles = VpnSettings.loadVpnProfiles(paramKeyStore, new int[] { 0 });
      this.mTitles = Lists.newArrayList();
      this.mTitles.add(paramResources.getText(2131429167));
      this.mCurrentIndex = 0;
      Iterator localIterator = this.mProfiles.iterator();
      while (localIterator.hasNext())
      {
        VpnProfile localVpnProfile = (VpnProfile)localIterator.next();
        if (TextUtils.equals(localVpnProfile.key, str))
          this.mCurrentIndex = this.mTitles.size();
        this.mTitles.add(localVpnProfile.name);
      }
    }

    public static void show(VpnSettings paramVpnSettings)
    {
      if (!paramVpnSettings.isAdded())
        return;
      new LockdownConfigFragment().show(paramVpnSettings.getFragmentManager(), "lockdown");
    }

    public Dialog onCreateDialog(Bundle paramBundle)
    {
      final Activity localActivity = getActivity();
      final KeyStore localKeyStore = KeyStore.getInstance();
      initProfiles(localKeyStore, localActivity.getResources());
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(localActivity);
      LayoutInflater localLayoutInflater = LayoutInflater.from(localBuilder.getContext());
      localBuilder.setTitle(2131429165);
      View localView = localLayoutInflater.inflate(2130968729, null, false);
      final ListView localListView = (ListView)localView.findViewById(16908298);
      localListView.setChoiceMode(1);
      localListView.setAdapter(new TitleAdapter(localActivity, this.mTitles));
      localListView.setItemChecked(this.mCurrentIndex, true);
      localBuilder.setView(localView);
      localBuilder.setPositiveButton(17039370, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          int i = localListView.getCheckedItemPosition();
          if (VpnSettings.LockdownConfigFragment.this.mCurrentIndex == i)
            return;
          if (i == 0)
            localKeyStore.delete("LOCKDOWN_VPN");
          while (true)
          {
            ConnectivityManager.from(VpnSettings.LockdownConfigFragment.this.getActivity()).updateLockdownVpn();
            return;
            VpnProfile localVpnProfile = (VpnProfile)VpnSettings.LockdownConfigFragment.this.mProfiles.get(i - 1);
            if (!localVpnProfile.isValidLockdownProfile())
            {
              Toast.makeText(localActivity, 2131429168, 1).show();
              return;
            }
            localKeyStore.put("LOCKDOWN_VPN", localVpnProfile.key.getBytes(), -1, 1);
          }
        }
      });
      return localBuilder.create();
    }

    private static class TitleAdapter extends ArrayAdapter<CharSequence>
    {
      public TitleAdapter(Context paramContext, List<CharSequence> paramList)
      {
        super(17367197, 16908308, paramList);
      }
    }
  }

  private static class VpnPreference extends Preference
  {
    private VpnProfile mProfile;
    private int mState = -1;

    VpnPreference(Context paramContext, VpnProfile paramVpnProfile)
    {
      super();
      setPersistent(false);
      setOrder(0);
      this.mProfile = paramVpnProfile;
      update();
    }

    public int compareTo(Preference paramPreference)
    {
      int i = -1;
      if ((paramPreference instanceof VpnPreference))
      {
        VpnPreference localVpnPreference = (VpnPreference)paramPreference;
        i = localVpnPreference.mState - this.mState;
        if (i == 0)
        {
          i = this.mProfile.name.compareTo(localVpnPreference.mProfile.name);
          if (i == 0)
          {
            i = this.mProfile.type - localVpnPreference.mProfile.type;
            if (i == 0)
              i = this.mProfile.key.compareTo(localVpnPreference.mProfile.key);
          }
        }
      }
      return i;
    }

    VpnProfile getProfile()
    {
      return this.mProfile;
    }

    void update()
    {
      if (this.mState < 0)
        setSummary(getContext().getResources().getStringArray(2131165277)[this.mProfile.type]);
      while (true)
      {
        setTitle(this.mProfile.name);
        notifyHierarchyChanged();
        return;
        setSummary(getContext().getResources().getStringArray(2131165278)[this.mState]);
      }
    }

    void update(int paramInt)
    {
      this.mState = paramInt;
      update();
    }

    void update(VpnProfile paramVpnProfile)
    {
      this.mProfile = paramVpnProfile;
      update();
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.vpn2.VpnSettings
 * JD-Core Version:    0.6.2
 */