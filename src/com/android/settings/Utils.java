package com.android.settings;

import android.app.ActivityManager;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.preference.Preference;
import android.preference.PreferenceActivity.Header;
import android.preference.PreferenceFrameLayout;
import android.preference.PreferenceFrameLayout.LayoutParams;
import android.preference.PreferenceGroup;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Profile;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class Utils
{
  public static Dialog buildGlobalChangeWarningDialog(Context paramContext, int paramInt, Runnable paramRunnable)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
    localBuilder.setTitle(paramInt);
    localBuilder.setMessage(2131429239);
    localBuilder.setPositiveButton(17039370, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        this.val$positiveAction.run();
      }
    });
    localBuilder.setNegativeButton(17039360, null);
    return localBuilder.create();
  }

  public static boolean copyMeProfilePhoto(Context paramContext, UserInfo paramUserInfo)
  {
    Uri localUri = ContactsContract.Profile.CONTENT_URI;
    InputStream localInputStream = ContactsContract.Contacts.openContactPhotoInputStream(paramContext.getContentResolver(), localUri, true);
    if (localInputStream == null)
      return false;
    if (paramUserInfo != null);
    for (int i = paramUserInfo.id; ; i = UserHandle.myUserId())
    {
      ((UserManager)paramContext.getSystemService("user")).setUserIcon(i, BitmapFactory.decodeStream(localInputStream));
      try
      {
        localInputStream.close();
        return true;
      }
      catch (IOException localIOException)
      {
        return true;
      }
    }
  }

  public static Locale createLocaleFromString(String paramString)
  {
    if (paramString == null)
      return Locale.getDefault();
    String[] arrayOfString = paramString.split("_", 3);
    if (1 == arrayOfString.length)
      return new Locale(arrayOfString[0]);
    if (2 == arrayOfString.length)
      return new Locale(arrayOfString[0], arrayOfString[1]);
    return new Locale(arrayOfString[0], arrayOfString[1], arrayOfString[2]);
  }

  public static void forcePrepareCustomPreferencesList(ViewGroup paramViewGroup, View paramView, ListView paramListView, boolean paramBoolean)
  {
    paramListView.setScrollBarStyle(33554432);
    paramListView.setClipToPadding(false);
    prepareCustomPreferencesList(paramViewGroup, paramView, paramListView, paramBoolean);
  }

  private static String formatIpAddresses(LinkProperties paramLinkProperties)
  {
    String str = null;
    if (paramLinkProperties == null);
    while (true)
    {
      return str;
      Iterator localIterator = paramLinkProperties.getAllAddresses().iterator();
      boolean bool = localIterator.hasNext();
      str = null;
      if (bool)
      {
        str = "";
        while (localIterator.hasNext())
        {
          str = str + ((InetAddress)localIterator.next()).getHostAddress();
          if (localIterator.hasNext())
            str = str + "\n";
        }
      }
    }
  }

  public static String getBatteryPercentage(Intent paramIntent)
  {
    int i = paramIntent.getIntExtra("level", 0);
    int j = paramIntent.getIntExtra("scale", 100);
    return String.valueOf(i * 100 / j) + "%";
  }

  public static String getBatteryStatus(Resources paramResources, Intent paramIntent)
  {
    int i = paramIntent.getIntExtra("plugged", 0);
    int j = paramIntent.getIntExtra("status", 1);
    if (j == 2)
    {
      String str = paramResources.getString(2131427401);
      int k;
      if (i > 0)
      {
        if (i != 1)
          break label76;
        k = 2131427402;
      }
      while (true)
      {
        str = str + " " + paramResources.getString(k);
        return str;
        label76: if (i == 2)
          k = 2131427403;
        else
          k = 2131427404;
      }
    }
    if (j == 3)
      return paramResources.getString(2131427405);
    if (j == 4)
      return paramResources.getString(2131427406);
    if (j == 5)
      return paramResources.getString(2131427407);
    return paramResources.getString(2131427400);
  }

  public static String getDefaultIpAddresses(Context paramContext)
  {
    return formatIpAddresses(((ConnectivityManager)paramContext.getSystemService("connectivity")).getActiveLinkProperties());
  }

  // ERROR //
  private static String getLocalProfileGivenName(Context paramContext)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 57	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   4: astore_1
    //   5: aload_1
    //   6: getstatic 241	android/provider/ContactsContract$Profile:CONTENT_RAW_CONTACTS_URI	Landroid/net/Uri;
    //   9: iconst_1
    //   10: anewarray 110	java/lang/String
    //   13: dup
    //   14: iconst_0
    //   15: ldc 243
    //   17: aastore
    //   18: ldc 245
    //   20: aconst_null
    //   21: aconst_null
    //   22: invokevirtual 251	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   25: astore_2
    //   26: aload_2
    //   27: ifnonnull +5 -> 32
    //   30: aconst_null
    //   31: areturn
    //   32: aload_2
    //   33: invokeinterface 256 1 0
    //   38: istore 4
    //   40: iload 4
    //   42: ifne +11 -> 53
    //   45: aload_2
    //   46: invokeinterface 257 1 0
    //   51: aconst_null
    //   52: areturn
    //   53: aload_2
    //   54: iconst_0
    //   55: invokeinterface 261 2 0
    //   60: lstore 5
    //   62: aload_2
    //   63: invokeinterface 257 1 0
    //   68: aload_1
    //   69: getstatic 51	android/provider/ContactsContract$Profile:CONTENT_URI	Landroid/net/Uri;
    //   72: invokevirtual 267	android/net/Uri:buildUpon	()Landroid/net/Uri$Builder;
    //   75: ldc_w 269
    //   78: invokevirtual 275	android/net/Uri$Builder:appendPath	(Ljava/lang/String;)Landroid/net/Uri$Builder;
    //   81: invokevirtual 279	android/net/Uri$Builder:build	()Landroid/net/Uri;
    //   84: iconst_2
    //   85: anewarray 110	java/lang/String
    //   88: dup
    //   89: iconst_0
    //   90: ldc_w 281
    //   93: aastore
    //   94: dup
    //   95: iconst_1
    //   96: ldc_w 283
    //   99: aastore
    //   100: new 166	java/lang/StringBuilder
    //   103: dup
    //   104: invokespecial 167	java/lang/StringBuilder:<init>	()V
    //   107: ldc_w 285
    //   110: invokevirtual 171	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   113: lload 5
    //   115: invokevirtual 288	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   118: invokevirtual 184	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   121: aconst_null
    //   122: aconst_null
    //   123: invokevirtual 251	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   126: astore 7
    //   128: aload 7
    //   130: ifnull -100 -> 30
    //   133: aload 7
    //   135: invokeinterface 256 1 0
    //   140: istore 9
    //   142: iload 9
    //   144: ifne +21 -> 165
    //   147: aload 7
    //   149: invokeinterface 257 1 0
    //   154: aconst_null
    //   155: areturn
    //   156: astore_3
    //   157: aload_2
    //   158: invokeinterface 257 1 0
    //   163: aload_3
    //   164: athrow
    //   165: aload 7
    //   167: iconst_0
    //   168: invokeinterface 289 2 0
    //   173: astore 10
    //   175: aload 10
    //   177: invokestatic 295	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   180: ifeq +17 -> 197
    //   183: aload 7
    //   185: iconst_1
    //   186: invokeinterface 289 2 0
    //   191: astore 11
    //   193: aload 11
    //   195: astore 10
    //   197: aload 7
    //   199: invokeinterface 257 1 0
    //   204: aload 10
    //   206: areturn
    //   207: astore 8
    //   209: aload 7
    //   211: invokeinterface 257 1 0
    //   216: aload 8
    //   218: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   32	40	156	finally
    //   53	62	156	finally
    //   133	142	207	finally
    //   165	193	207	finally
  }

  public static String getMeProfileName(Context paramContext, boolean paramBoolean)
  {
    if (paramBoolean)
      return getProfileDisplayName(paramContext);
    return getShorterNameIfPossible(paramContext);
  }

  private static final String getProfileDisplayName(Context paramContext)
  {
    Cursor localCursor = paramContext.getContentResolver().query(ContactsContract.Profile.CONTENT_URI, new String[] { "display_name" }, null, null, null);
    if (localCursor == null)
      return null;
    try
    {
      boolean bool = localCursor.moveToFirst();
      if (!bool)
        return null;
      String str = localCursor.getString(0);
      return str;
    }
    finally
    {
      localCursor.close();
    }
  }

  private static String getShorterNameIfPossible(Context paramContext)
  {
    String str = getLocalProfileGivenName(paramContext);
    if (!TextUtils.isEmpty(str))
      return str;
    return getProfileDisplayName(paramContext);
  }

  public static int getTetheringLabel(ConnectivityManager paramConnectivityManager)
  {
    String[] arrayOfString1 = paramConnectivityManager.getTetherableUsbRegexs();
    String[] arrayOfString2 = paramConnectivityManager.getTetherableWifiRegexs();
    String[] arrayOfString3 = paramConnectivityManager.getTetherableBluetoothRegexs();
    int i;
    int j;
    if (arrayOfString1.length != 0)
    {
      i = 1;
      if (arrayOfString2.length == 0)
        break label64;
      j = 1;
      label31: if (arrayOfString3.length == 0)
        break label70;
    }
    label64: label70: for (int k = 1; ; k = 0)
    {
      if ((j == 0) || (i == 0) || (k == 0))
        break label76;
      return 2131428240;
      i = 0;
      break;
      j = 0;
      break label31;
    }
    label76: if ((j != 0) && (i != 0))
      return 2131428240;
    if ((j != 0) && (k != 0))
      return 2131428240;
    if (j != 0)
      return 2131428237;
    if ((i != 0) && (k != 0))
      return 2131428239;
    if (i != 0)
      return 2131428236;
    return 2131428238;
  }

  public static String getWifiIpAddresses(Context paramContext)
  {
    return formatIpAddresses(((ConnectivityManager)paramContext.getSystemService("connectivity")).getLinkProperties(1));
  }

  public static boolean hasMultipleUsers(Context paramContext)
  {
    return ((UserManager)paramContext.getSystemService("user")).getUsers().size() > 1;
  }

  public static boolean isBatteryPresent(Intent paramIntent)
  {
    return paramIntent.getBooleanExtra("present", true);
  }

  public static boolean isMonkeyRunning()
  {
    return ActivityManager.isUserAMonkey();
  }

  public static boolean isVoiceCapable(Context paramContext)
  {
    TelephonyManager localTelephonyManager = (TelephonyManager)paramContext.getSystemService("phone");
    return (localTelephonyManager != null) && (localTelephonyManager.isVoiceCapable());
  }

  public static boolean isWifiOnly(Context paramContext)
  {
    boolean bool1 = ((ConnectivityManager)paramContext.getSystemService("connectivity")).isNetworkSupported(0);
    boolean bool2 = false;
    if (!bool1)
      bool2 = true;
    return bool2;
  }

  public static void prepareCustomPreferencesList(ViewGroup paramViewGroup, View paramView1, View paramView2, boolean paramBoolean)
  {
    int i;
    int j;
    int k;
    if (paramView2.getScrollBarStyle() == 33554432)
    {
      i = 1;
      if ((i != 0) && ((paramViewGroup instanceof PreferenceFrameLayout)))
      {
        ((PreferenceFrameLayout.LayoutParams)paramView1.getLayoutParams()).removeBorders = true;
        Resources localResources = paramView2.getResources();
        j = localResources.getDimensionPixelSize(2131558440);
        k = localResources.getDimensionPixelSize(17104938);
        if (!paramBoolean)
          break label86;
      }
    }
    label86: for (int m = 0; ; m = j)
    {
      paramView2.setPaddingRelative(m, 0, m, k);
      return;
      i = 0;
      break;
    }
  }

  public static boolean updateHeaderToSpecificActivityFromMetaDataOrRemove(Context paramContext, List<PreferenceActivity.Header> paramList, PreferenceActivity.Header paramHeader)
  {
    Intent localIntent = paramHeader.intent;
    PackageManager localPackageManager;
    List localList;
    int i;
    int j;
    if (localIntent != null)
    {
      localPackageManager = paramContext.getPackageManager();
      localList = localPackageManager.queryIntentActivities(localIntent, 128);
      i = localList.size();
      j = 0;
    }
    while (true)
    {
      ResolveInfo localResolveInfo;
      String str1;
      if (j < i)
      {
        localResolveInfo = (ResolveInfo)localList.get(j);
        if ((0x1 & localResolveInfo.activityInfo.applicationInfo.flags) != 0)
          str1 = null;
      }
      try
      {
        Resources localResources = localPackageManager.getResourcesForApplication(localResolveInfo.activityInfo.packageName);
        Bundle localBundle = localResolveInfo.activityInfo.metaData;
        localObject = null;
        str1 = null;
        if (localResources != null)
        {
          localObject = null;
          str1 = null;
          if (localBundle != null)
          {
            localResources.getDrawable(localBundle.getInt("com.android.settings.icon"));
            str1 = localResources.getString(localBundle.getInt("com.android.settings.title"));
            String str2 = localResources.getString(localBundle.getInt("com.android.settings.summary"));
            localObject = str2;
          }
        }
        if (TextUtils.isEmpty(str1))
          str1 = localResolveInfo.loadLabel(localPackageManager).toString();
        paramHeader.title = str1;
        paramHeader.summary = localObject;
        paramHeader.intent = new Intent().setClassName(localResolveInfo.activityInfo.packageName, localResolveInfo.activityInfo.name);
        return true;
        j++;
        continue;
        paramList.remove(paramHeader);
        return false;
      }
      catch (Resources.NotFoundException localNotFoundException)
      {
        while (true)
          localObject = null;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        while (true)
          Object localObject = null;
      }
    }
  }

  public static boolean updatePreferenceToSpecificActivityOrRemove(Context paramContext, PreferenceGroup paramPreferenceGroup, String paramString, int paramInt)
  {
    Preference localPreference = paramPreferenceGroup.findPreference(paramString);
    if (localPreference == null)
      return false;
    Intent localIntent = localPreference.getIntent();
    if (localIntent != null)
    {
      PackageManager localPackageManager = paramContext.getPackageManager();
      List localList = localPackageManager.queryIntentActivities(localIntent, 0);
      int i = localList.size();
      for (int j = 0; j < i; j++)
      {
        ResolveInfo localResolveInfo = (ResolveInfo)localList.get(j);
        if ((0x1 & localResolveInfo.activityInfo.applicationInfo.flags) != 0)
        {
          localPreference.setIntent(new Intent().setClassName(localResolveInfo.activityInfo.packageName, localResolveInfo.activityInfo.name));
          if ((paramInt & 0x1) != 0)
            localPreference.setTitle(localResolveInfo.loadLabel(localPackageManager));
          return true;
        }
      }
    }
    paramPreferenceGroup.removePreference(localPreference);
    return false;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.Utils
 * JD-Core Version:    0.6.2
 */