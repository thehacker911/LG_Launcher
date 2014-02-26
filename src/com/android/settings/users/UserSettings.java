package com.android.settings.users;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.IActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Profile;
import android.provider.Settings.Secure;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SimpleAdapter;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.OwnerInfoSettings;
import com.android.settings.RestrictedSettingsFragment;
import com.android.settings.SelectableEditTextPreference;
import com.android.settings.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class UserSettings extends RestrictedSettingsFragment
  implements DialogInterface.OnDismissListener, Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener, View.OnClickListener
{
  static final int[] USER_DRAWABLES = { 2130837527, 2130837528, 2130837529, 2130837530, 2130837531, 2130837532, 2130837533, 2130837534 };
  private Preference mAddUser;
  private int mAddedUserId = 0;
  private boolean mAddingUser;
  private Handler mHandler;
  private boolean mIsOwner;
  private Preference mMePreference;
  private SelectableEditTextPreference mNicknamePreference;
  private boolean mProfileExists;
  private int mRemovingUserId = -1;
  private BroadcastReceiver mUserChangeReceiver;
  private SparseArray<Bitmap> mUserIcons = new SparseArray();
  private PreferenceGroup mUserListCategory;
  private final Object mUserLock = new Object();
  private UserManager mUserManager;

  public UserSettings()
  {
    super("restrictions_pin_set");
    int i = UserHandle.myUserId();
    boolean bool = false;
    if (i == 0)
      bool = true;
    this.mIsOwner = bool;
    this.mHandler = new Handler()
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        switch (paramAnonymousMessage.what)
        {
        default:
          return;
        case 1:
          UserSettings.this.updateUserList();
          return;
        case 2:
          UserSettings.this.onUserCreated(paramAnonymousMessage.arg1);
          return;
        case 3:
        }
        UserSettings.this.onManageUserClicked(paramAnonymousMessage.arg1, true);
      }
    };
    this.mUserChangeReceiver = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        if (paramAnonymousIntent.getAction().equals("android.intent.action.USER_REMOVED"))
          UserSettings.access$302(UserSettings.this, -1);
        while (true)
        {
          UserSettings.this.mHandler.sendEmptyMessage(1);
          return;
          if (paramAnonymousIntent.getAction().equals("android.intent.action.USER_INFO_CHANGED"))
          {
            int i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", -1);
            if (i != -1)
              UserSettings.this.mUserIcons.remove(i);
          }
        }
      }
    };
  }

  private void addUserNow(final int paramInt)
  {
    synchronized (this.mUserLock)
    {
      this.mAddingUser = true;
      new Thread()
      {
        public void run()
        {
          UserInfo localUserInfo;
          if (paramInt == 1)
            localUserInfo = UserSettings.this.createTrustedUser();
          synchronized (UserSettings.this.mUserLock)
          {
            while (true)
            {
              UserSettings.access$1902(UserSettings.this, false);
              if (paramInt != 1)
                break;
              UserSettings.this.mHandler.sendEmptyMessage(1);
              UserSettings.this.mHandler.sendMessage(UserSettings.this.mHandler.obtainMessage(2, localUserInfo.id, localUserInfo.serialNumber));
              return;
              localUserInfo = UserSettings.this.createLimitedUser();
            }
            UserSettings.this.mHandler.sendMessage(UserSettings.this.mHandler.obtainMessage(3, localUserInfo.id, localUserInfo.serialNumber));
          }
        }
      }
      .start();
      return;
    }
  }

  private void assignDefaultPhoto(UserInfo paramUserInfo)
  {
    Bitmap localBitmap = BitmapFactory.decodeResource(getResources(), USER_DRAWABLES[(paramUserInfo.id % USER_DRAWABLES.length)]);
    this.mUserManager.setUserIcon(paramUserInfo.id, localBitmap);
  }

  private void assignProfilePhoto(UserInfo paramUserInfo)
  {
    if (!Utils.copyMeProfilePhoto(getActivity(), paramUserInfo))
      assignDefaultPhoto(paramUserInfo);
  }

  private UserInfo createLimitedUser()
  {
    UserInfo localUserInfo = this.mUserManager.createUser(getResources().getString(2131429224), 8);
    int i = localUserInfo.id;
    UserHandle localUserHandle = new UserHandle(i);
    this.mUserManager.setUserRestriction("no_modify_accounts", true, localUserHandle);
    this.mUserManager.setUserRestriction("no_share_location", true, localUserHandle);
    Settings.Secure.putStringForUser(getContentResolver(), "location_providers_allowed", "", i);
    Bitmap localBitmap = BitmapFactory.decodeResource(getResources(), USER_DRAWABLES[(i % USER_DRAWABLES.length)]);
    this.mUserManager.setUserIcon(i, localBitmap);
    AccountManager localAccountManager = AccountManager.get(getActivity());
    Account[] arrayOfAccount = localAccountManager.getAccounts();
    if (arrayOfAccount != null)
    {
      int j = arrayOfAccount.length;
      for (int k = 0; k < j; k++)
        localAccountManager.addSharedAccount(arrayOfAccount[k], localUserHandle);
    }
    return localUserInfo;
  }

  private UserInfo createTrustedUser()
  {
    UserInfo localUserInfo = this.mUserManager.createUser(getResources().getString(2131429223), 0);
    if (localUserInfo != null)
      assignDefaultPhoto(localUserInfo);
    return localUserInfo;
  }

  private Drawable encircle(int paramInt)
  {
    return encircle(BitmapFactory.decodeResource(getResources(), paramInt));
  }

  private Drawable encircle(Bitmap paramBitmap)
  {
    return CircleFramedDrawable.getInstance(getActivity(), paramBitmap);
  }

  private void finishLoadProfile(String paramString)
  {
    if (getActivity() == null);
    int i;
    Bitmap localBitmap;
    do
    {
      return;
      this.mMePreference.setTitle(getString(2131429205, new Object[] { paramString }));
      i = UserHandle.myUserId();
      localBitmap = this.mUserManager.getUserIcon(i);
    }
    while (localBitmap == null);
    this.mMePreference.setIcon(encircle(localBitmap));
    this.mUserIcons.put(i, localBitmap);
  }

  private String getProfileName()
  {
    String str = Utils.getMeProfileName(getActivity(), true);
    if (str != null)
      this.mProfileExists = true;
    return str;
  }

  private boolean hasLockscreenSecurity()
  {
    LockPatternUtils localLockPatternUtils = new LockPatternUtils(getActivity());
    return (localLockPatternUtils.isLockPasswordEnabled()) || (localLockPatternUtils.isLockPatternEnabled());
  }

  private boolean isInitialized(UserInfo paramUserInfo)
  {
    return (0x10 & paramUserInfo.flags) != 0;
  }

  private void launchChooseLockscreen()
  {
    Intent localIntent = new Intent("android.app.action.SET_NEW_PASSWORD");
    localIntent.putExtra("minimum_quality", 65536);
    startActivityForResult(localIntent, 10);
  }

  private void loadIconsAsync(List<Integer> paramList)
  {
    getResources();
    new AsyncTask()
    {
      protected Void doInBackground(List<Integer>[] paramAnonymousArrayOfList)
      {
        Iterator localIterator = paramAnonymousArrayOfList[0].iterator();
        while (localIterator.hasNext())
        {
          int i = ((Integer)localIterator.next()).intValue();
          Bitmap localBitmap = UserSettings.this.mUserManager.getUserIcon(i);
          UserSettings.this.mUserIcons.append(i, localBitmap);
        }
        return null;
      }

      protected void onPostExecute(Void paramAnonymousVoid)
      {
        UserSettings.this.updateUserList();
      }
    }
    .execute(new List[] { paramList });
  }

  private void loadProfile()
  {
    this.mProfileExists = false;
    new AsyncTask()
    {
      protected String doInBackground(Void[] paramAnonymousArrayOfVoid)
      {
        UserInfo localUserInfo = UserSettings.this.mUserManager.getUserInfo(UserHandle.myUserId());
        if ((localUserInfo.iconPath == null) || (localUserInfo.iconPath.equals("")))
          UserSettings.this.assignProfilePhoto(localUserInfo);
        String str = UserSettings.this.getProfileName();
        if (str == null)
          str = localUserInfo.name;
        return str;
      }

      protected void onPostExecute(String paramAnonymousString)
      {
        UserSettings.this.finishLoadProfile(paramAnonymousString);
      }
    }
    .execute(new Void[0]);
  }

  private void onAddUserClicked(int paramInt)
  {
    while (true)
    {
      synchronized (this.mUserLock)
      {
        if ((this.mRemovingUserId == -1) && (!this.mAddingUser));
        switch (paramInt)
        {
        default:
          return;
        case 1:
          showDialog(2);
        case 2:
        }
      }
      if (hasLockscreenSecurity())
        addUserNow(2);
      else
        showDialog(7);
    }
  }

  private void onManageUserClicked(int paramInt, boolean paramBoolean)
  {
    UserInfo localUserInfo = this.mUserManager.getUserInfo(paramInt);
    if ((localUserInfo.isRestricted()) && (this.mIsOwner))
    {
      localBundle2 = new Bundle();
      localBundle2.putInt("user_id", paramInt);
      localBundle2.putBoolean("new_user", paramBoolean);
      ((PreferenceActivity)getActivity()).startPreferencePanel(RestrictedProfileSettings.class.getName(), localBundle2, 2131429274, null, null, 0);
    }
    while (localUserInfo.id != UserHandle.myUserId())
    {
      Bundle localBundle2;
      return;
    }
    Bundle localBundle1 = new Bundle();
    if (!localUserInfo.isRestricted())
      localBundle1.putBoolean("show_nickname", true);
    int i;
    if (localUserInfo.id == 0)
      i = 2131427609;
    while (true)
    {
      ((PreferenceActivity)getActivity()).startPreferencePanel(OwnerInfoSettings.class.getName(), localBundle1, i, null, null, 0);
      return;
      if (localUserInfo.isRestricted())
        i = 2131427617;
      else
        i = 2131427615;
    }
  }

  private void onRemoveUserClicked(int paramInt)
  {
    synchronized (this.mUserLock)
    {
      if ((this.mRemovingUserId == -1) && (!this.mAddingUser))
      {
        this.mRemovingUserId = paramInt;
        showDialog(1);
      }
      return;
    }
  }

  private void onUserCreated(int paramInt)
  {
    this.mAddedUserId = paramInt;
    if (this.mUserManager.getUserInfo(paramInt).isRestricted())
    {
      showDialog(4);
      return;
    }
    showDialog(3);
  }

  private void removeThisUser()
  {
    try
    {
      ActivityManagerNative.getDefault().switchUser(0);
      ((UserManager)getActivity().getSystemService("user")).removeUser(UserHandle.myUserId());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("UserSettings", "Unable to remove self user");
    }
  }

  private void removeUserNow()
  {
    if (this.mRemovingUserId == UserHandle.myUserId())
    {
      removeThisUser();
      return;
    }
    new Thread()
    {
      public void run()
      {
        synchronized (UserSettings.this.mUserLock)
        {
          UserSettings.this.mUserManager.removeUser(UserSettings.this.mRemovingUserId);
          UserSettings.this.mHandler.sendEmptyMessage(1);
          return;
        }
      }
    }
    .start();
  }

  private void setPhotoId(Preference paramPreference, UserInfo paramUserInfo)
  {
    Bitmap localBitmap = (Bitmap)this.mUserIcons.get(paramUserInfo.id);
    if (localBitmap != null)
      paramPreference.setIcon(encircle(localBitmap));
  }

  private void setUserName(String paramString)
  {
    this.mUserManager.setUserName(UserHandle.myUserId(), paramString);
    this.mNicknamePreference.setSummary(paramString);
    getActivity().invalidateOptionsMenu();
  }

  private void switchUserNow(int paramInt)
  {
    try
    {
      ActivityManagerNative.getDefault().switchUser(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
    }
  }

  private void updateUserList()
  {
    boolean bool = true;
    if (getActivity() == null)
      return;
    List localList = this.mUserManager.getUsers(bool);
    this.mUserListCategory.removeAll();
    this.mUserListCategory.setOrderingAsAdded(false);
    this.mUserListCategory.addPreference(this.mMePreference);
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      UserInfo localUserInfo = (UserInfo)localIterator.next();
      Object localObject;
      int j;
      if (localUserInfo.id == UserHandle.myUserId())
      {
        localObject = this.mMePreference;
        if (isInitialized(localUserInfo))
          break label335;
        if (!localUserInfo.isRestricted())
          break label327;
        j = 2131429203;
        label124: ((Preference)localObject).setSummary(j);
      }
      while (true)
      {
        if (localUserInfo.iconPath == null)
          break label352;
        if (this.mUserIcons.get(localUserInfo.id) != null)
          break label354;
        localArrayList.add(Integer.valueOf(localUserInfo.id));
        ((Preference)localObject).setIcon(encircle(2130837527));
        break;
        Activity localActivity = getActivity();
        int i = localUserInfo.id;
        UserSettings localUserSettings1;
        if ((this.mIsOwner) && (localUserInfo.isRestricted()))
        {
          localUserSettings1 = this;
          label213: if (!this.mIsOwner)
            break label321;
        }
        label321: for (UserSettings localUserSettings2 = this; ; localUserSettings2 = null)
        {
          localObject = new UserPreference(localActivity, null, i, localUserSettings1, localUserSettings2);
          ((Preference)localObject).setOnPreferenceClickListener(this);
          ((Preference)localObject).setKey("id=" + localUserInfo.id);
          this.mUserListCategory.addPreference((Preference)localObject);
          if (localUserInfo.id == 0)
            ((Preference)localObject).setSummary(2131429204);
          ((Preference)localObject).setTitle(localUserInfo.name);
          break;
          localUserSettings1 = null;
          break label213;
        }
        label327: j = 2131429202;
        break label124;
        label335: if (localUserInfo.isRestricted())
          ((Preference)localObject).setSummary(2131429199);
      }
      label352: continue;
      label354: setPhotoId((Preference)localObject, localUserInfo);
    }
    if (this.mAddingUser)
    {
      UserPreference localUserPreference = new UserPreference(getActivity(), null, -10, null, null);
      localUserPreference.setEnabled(false);
      localUserPreference.setTitle(2131429223);
      localUserPreference.setIcon(encircle(2130837527));
      this.mUserListCategory.addPreference(localUserPreference);
    }
    getActivity().invalidateOptionsMenu();
    if (localArrayList.size() > 0)
      loadIconsAsync(localArrayList);
    if (UserManager.getMaxSupportedUsers() > localList.size());
    while (true)
    {
      this.mAddUser.setEnabled(bool);
      return;
      bool = false;
    }
  }

  public int getHelpResource()
  {
    return 2131429266;
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (paramInt1 == 10)
    {
      if ((paramInt2 != 0) && (hasLockscreenSecurity()))
        addUserNow(2);
    }
    else
      return;
    showDialog(7);
  }

  public void onClick(View paramView)
  {
    int i;
    if ((paramView.getTag() instanceof UserPreference))
      i = ((UserPreference)paramView.getTag()).getUserId();
    switch (paramView.getId())
    {
    case 2131230977:
    default:
      return;
    case 2131230978:
      onRemoveUserClicked(i);
      return;
    case 2131230976:
    }
    onManageUserClicked(i, false);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle != null)
    {
      if (paramBundle.containsKey("adding_user"))
        this.mAddedUserId = paramBundle.getInt("adding_user");
      if (paramBundle.containsKey("removing_user"))
        this.mRemovingUserId = paramBundle.getInt("removing_user");
    }
    this.mUserManager = ((UserManager)getActivity().getSystemService("user"));
    addPreferencesFromResource(2131034168);
    this.mUserListCategory = ((PreferenceGroup)findPreference("user_list"));
    Activity localActivity = getActivity();
    int i = UserHandle.myUserId();
    if (this.mUserManager.isLinkedUser());
    for (Object localObject = null; ; localObject = this)
    {
      this.mMePreference = new UserPreference(localActivity, null, i, (View.OnClickListener)localObject, null);
      this.mMePreference.setKey("user_me");
      this.mMePreference.setOnPreferenceClickListener(this);
      if (this.mIsOwner)
        this.mMePreference.setSummary(2131429204);
      this.mAddUser = findPreference("user_add");
      this.mAddUser.setOnPreferenceClickListener(this);
      if ((!this.mIsOwner) || (UserManager.getMaxSupportedUsers() < 2))
        removePreference("user_add");
      loadProfile();
      setHasOptionsMenu(true);
      IntentFilter localIntentFilter = new IntentFilter("android.intent.action.USER_REMOVED");
      localIntentFilter.addAction("android.intent.action.USER_INFO_CHANGED");
      getActivity().registerReceiverAsUser(this.mUserChangeReceiver, UserHandle.ALL, localIntentFilter, null, this.mHandler);
      return;
    }
  }

  public Dialog onCreateDialog(int paramInt)
  {
    Activity localActivity = getActivity();
    if (localActivity == null)
      return null;
    switch (paramInt)
    {
    default:
      return null;
    case 1:
      AlertDialog.Builder localBuilder1 = new AlertDialog.Builder(getActivity());
      int k;
      AlertDialog.Builder localBuilder2;
      int m;
      if (UserHandle.myUserId() == this.mRemovingUserId)
      {
        k = 2131429225;
        localBuilder2 = localBuilder1.setTitle(k);
        if (UserHandle.myUserId() != this.mRemovingUserId)
          break label175;
        m = 2131429228;
      }
      while (true)
      {
        return localBuilder2.setMessage(m).setPositiveButton(2131429233, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            UserSettings.this.removeUserNow();
          }
        }).setNegativeButton(17039360, null).create();
        if (this.mUserManager.getUserInfo(this.mRemovingUserId).isRestricted())
        {
          k = 2131429227;
          break;
        }
        k = 2131429226;
        break;
        if (this.mUserManager.getUserInfo(this.mRemovingUserId).isRestricted())
          m = 2131429230;
        else
          m = 2131429229;
      }
    case 5:
      return new AlertDialog.Builder(localActivity).setMessage(2131429220).setPositiveButton(17039370, null).create();
    case 2:
      final SharedPreferences localSharedPreferences = getActivity().getPreferences(0);
      final boolean bool = localSharedPreferences.getBoolean("key_add_user_long_message_displayed", false);
      int i;
      if (bool)
      {
        i = 2131429214;
        if (paramInt != 2)
          break label332;
      }
      for (final int j = 1; ; j = 2)
      {
        return new AlertDialog.Builder(localActivity).setTitle(2131429212).setMessage(i).setPositiveButton(17039370, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            UserSettings.this.addUserNow(j);
            if (!bool)
              localSharedPreferences.edit().putBoolean("key_add_user_long_message_displayed", true).apply();
          }
        }).setNegativeButton(17039360, null).create();
        i = 2131429213;
        break;
      }
    case 3:
      return new AlertDialog.Builder(localActivity).setTitle(2131429215).setMessage(2131429216).setPositiveButton(2131429218, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          UserSettings.this.switchUserNow(UserSettings.this.mAddedUserId);
        }
      }).setNegativeButton(2131429219, null).create();
    case 4:
      return new AlertDialog.Builder(localActivity).setMessage(2131429217).setPositiveButton(17039370, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          UserSettings.this.switchUserNow(UserSettings.this.mAddedUserId);
        }
      }).setNegativeButton(17039360, null).create();
    case 6:
      label175: label332: ArrayList localArrayList = new ArrayList();
      HashMap localHashMap1 = new HashMap();
      localHashMap1.put("title", getString(2131429210));
      localHashMap1.put("summary", getString(2131429208));
      HashMap localHashMap2 = new HashMap();
      localHashMap2.put("title", getString(2131429211));
      localHashMap2.put("summary", getString(2131429209));
      localArrayList.add(localHashMap1);
      localArrayList.add(localHashMap2);
      return new AlertDialog.Builder(localActivity).setTitle(2131429207).setAdapter(new SimpleAdapter(localActivity, localArrayList, 2130968720, new String[] { "title", "summary" }, new int[] { 2131230756, 2131230772 }), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          UserSettings localUserSettings = UserSettings.this;
          if (paramAnonymousInt == 0);
          for (int i = 1; ; i = 2)
          {
            localUserSettings.onAddUserClicked(i);
            return;
          }
        }
      }).create();
    case 7:
    }
    return new AlertDialog.Builder(localActivity).setMessage(2131429200).setPositiveButton(2131429201, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        UserSettings.this.launchChooseLockscreen();
      }
    }).setNegativeButton(17039360, null).create();
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    UserManager localUserManager = (UserManager)getActivity().getSystemService("user");
    if ((!this.mIsOwner) && (!localUserManager.hasUserRestriction("no_remove_user")))
    {
      String str = this.mUserManager.getUserName();
      paramMenu.add(0, 1, 0, getResources().getString(2131429222, new Object[] { str })).setShowAsAction(0);
    }
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
  }

  public void onDestroy()
  {
    super.onDestroy();
    getActivity().unregisterReceiver(this.mUserChangeReceiver);
  }

  public void onDialogShowing()
  {
    super.onDialogShowing();
    setOnDismissListener(this);
  }

  public void onDismiss(DialogInterface paramDialogInterface)
  {
    synchronized (this.mUserLock)
    {
      this.mAddingUser = false;
      this.mRemovingUserId = -1;
      updateUserList();
      return;
    }
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (paramMenuItem.getItemId() == 1)
    {
      onRemoveUserClicked(UserHandle.myUserId());
      return true;
    }
    return super.onOptionsItemSelected(paramMenuItem);
  }

  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if (paramPreference == this.mNicknamePreference)
    {
      String str = (String)paramObject;
      if ((paramPreference == this.mNicknamePreference) && (str != null) && (str.length() > 0))
        setUserName(str);
      return true;
    }
    return false;
  }

  public boolean onPreferenceClick(Preference paramPreference)
  {
    Intent localIntent;
    if (paramPreference == this.mMePreference)
      if (!this.mProfileExists)
      {
        localIntent = new Intent("android.intent.action.INSERT", ContactsContract.Contacts.CONTENT_URI);
        localIntent.putExtra("newLocalProfile", true);
        localIntent.putExtra("finishActivityOnSaveCompleted", true);
        if (!this.mUserManager.isLinkedUser())
          break label88;
        onManageUserClicked(UserHandle.myUserId(), false);
      }
    label88: 
    do
    {
      UserInfo localUserInfo;
      do
      {
        return false;
        localIntent = new Intent("android.intent.action.EDIT", ContactsContract.Profile.CONTENT_URI);
        break;
        startActivity(localIntent);
        return false;
        if (!(paramPreference instanceof UserPreference))
          break label185;
        int i = ((UserPreference)paramPreference).getUserId();
        localUserInfo = this.mUserManager.getUserInfo(i);
        if (UserHandle.myUserId() != 0)
        {
          showDialog(5);
          return false;
        }
        if (!isInitialized(localUserInfo))
        {
          this.mHandler.sendMessage(this.mHandler.obtainMessage(2, localUserInfo.id, localUserInfo.serialNumber));
          return false;
        }
      }
      while (!localUserInfo.isRestricted());
      onManageUserClicked(localUserInfo.id, false);
      return false;
    }
    while (paramPreference != this.mAddUser);
    label185: showDialog(6);
    return false;
  }

  public void onResume()
  {
    super.onResume();
    loadProfile();
    updateUserList();
  }

  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putInt("adding_user", this.mAddedUserId);
    paramBundle.putInt("removing_user", this.mRemovingUserId);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.users.UserSettings
 * JD-Core Version:    0.6.2
 */