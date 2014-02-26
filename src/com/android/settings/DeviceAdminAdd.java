package com.android.settings;

import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.IActivityManager;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DeviceAdminInfo.PolicyInfo;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AppSecurityPermissions;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class DeviceAdminAdd extends Activity
{
  Button mActionButton;
  final ArrayList<View> mActivePolicies = new ArrayList();
  TextView mAddMsg;
  boolean mAddMsgEllipsized = true;
  ImageView mAddMsgExpander;
  CharSequence mAddMsgText;
  boolean mAdding;
  final ArrayList<View> mAddingPolicies = new ArrayList();
  TextView mAdminDescription;
  ImageView mAdminIcon;
  TextView mAdminName;
  ViewGroup mAdminPolicies;
  TextView mAdminWarning;
  Button mCancelButton;
  DevicePolicyManager mDPM;
  DeviceAdminInfo mDeviceAdmin;
  Handler mHandler;
  boolean mRefreshing;

  static void setViewVisibility(ArrayList<View> paramArrayList, int paramInt)
  {
    int i = paramArrayList.size();
    for (int j = 0; j < i; j++)
      ((View)paramArrayList.get(j)).setVisibility(paramInt);
  }

  int getEllipsizedLines()
  {
    Display localDisplay = ((WindowManager)getSystemService("window")).getDefaultDisplay();
    if (localDisplay.getHeight() > localDisplay.getWidth())
      return 5;
    return 2;
  }

  // ERROR //
  protected void onCreate(Bundle paramBundle)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: invokespecial 93	android/app/Activity:onCreate	(Landroid/os/Bundle;)V
    //   5: aload_0
    //   6: new 95	android/os/Handler
    //   9: dup
    //   10: aload_0
    //   11: invokevirtual 99	com/android/settings/DeviceAdminAdd:getMainLooper	()Landroid/os/Looper;
    //   14: invokespecial 102	android/os/Handler:<init>	(Landroid/os/Looper;)V
    //   17: putfield 104	com/android/settings/DeviceAdminAdd:mHandler	Landroid/os/Handler;
    //   20: aload_0
    //   21: aload_0
    //   22: ldc 106
    //   24: invokevirtual 69	com/android/settings/DeviceAdminAdd:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   27: checkcast 108	android/app/admin/DevicePolicyManager
    //   30: putfield 110	com/android/settings/DeviceAdminAdd:mDPM	Landroid/app/admin/DevicePolicyManager;
    //   33: ldc 111
    //   35: aload_0
    //   36: invokevirtual 115	com/android/settings/DeviceAdminAdd:getIntent	()Landroid/content/Intent;
    //   39: invokevirtual 120	android/content/Intent:getFlags	()I
    //   42: iand
    //   43: ifeq +16 -> 59
    //   46: ldc 122
    //   48: ldc 124
    //   50: invokestatic 130	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   53: pop
    //   54: aload_0
    //   55: invokevirtual 133	com/android/settings/DeviceAdminAdd:finish	()V
    //   58: return
    //   59: aload_0
    //   60: invokevirtual 115	com/android/settings/DeviceAdminAdd:getIntent	()Landroid/content/Intent;
    //   63: ldc 135
    //   65: invokevirtual 139	android/content/Intent:getParcelableExtra	(Ljava/lang/String;)Landroid/os/Parcelable;
    //   68: checkcast 141	android/content/ComponentName
    //   71: astore_2
    //   72: aload_2
    //   73: ifnonnull +39 -> 112
    //   76: ldc 122
    //   78: new 143	java/lang/StringBuilder
    //   81: dup
    //   82: invokespecial 144	java/lang/StringBuilder:<init>	()V
    //   85: ldc 146
    //   87: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   90: aload_0
    //   91: invokevirtual 115	com/android/settings/DeviceAdminAdd:getIntent	()Landroid/content/Intent;
    //   94: invokevirtual 154	android/content/Intent:getAction	()Ljava/lang/String;
    //   97: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   100: invokevirtual 157	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   103: invokestatic 130	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   106: pop
    //   107: aload_0
    //   108: invokevirtual 133	com/android/settings/DeviceAdminAdd:finish	()V
    //   111: return
    //   112: aload_0
    //   113: invokevirtual 161	com/android/settings/DeviceAdminAdd:getPackageManager	()Landroid/content/pm/PackageManager;
    //   116: aload_2
    //   117: sipush 128
    //   120: invokevirtual 167	android/content/pm/PackageManager:getReceiverInfo	(Landroid/content/ComponentName;I)Landroid/content/pm/ActivityInfo;
    //   123: astore 5
    //   125: aload_0
    //   126: getfield 110	com/android/settings/DeviceAdminAdd:mDPM	Landroid/app/admin/DevicePolicyManager;
    //   129: aload_2
    //   130: invokevirtual 171	android/app/admin/DevicePolicyManager:isAdminActive	(Landroid/content/ComponentName;)Z
    //   133: ifne +280 -> 413
    //   136: aload_0
    //   137: invokevirtual 161	com/android/settings/DeviceAdminAdd:getPackageManager	()Landroid/content/pm/PackageManager;
    //   140: new 117	android/content/Intent
    //   143: dup
    //   144: ldc 173
    //   146: invokespecial 176	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   149: ldc 177
    //   151: invokevirtual 181	android/content/pm/PackageManager:queryBroadcastReceivers	(Landroid/content/Intent;I)Ljava/util/List;
    //   154: astore 14
    //   156: aload 14
    //   158: ifnonnull +159 -> 317
    //   161: iconst_0
    //   162: istore 15
    //   164: iconst_0
    //   165: istore 16
    //   167: iconst_0
    //   168: istore 17
    //   170: iload 16
    //   172: iload 15
    //   174: if_icmpge +76 -> 250
    //   177: aload 14
    //   179: iload 16
    //   181: invokeinterface 184 2 0
    //   186: checkcast 186	android/content/pm/ResolveInfo
    //   189: astore 19
    //   191: aload 5
    //   193: getfield 192	android/content/pm/ActivityInfo:packageName	Ljava/lang/String;
    //   196: aload 19
    //   198: getfield 196	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   201: getfield 192	android/content/pm/ActivityInfo:packageName	Ljava/lang/String;
    //   204: invokevirtual 202	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   207: ifeq +200 -> 407
    //   210: aload 5
    //   212: getfield 205	android/content/pm/ActivityInfo:name	Ljava/lang/String;
    //   215: aload 19
    //   217: getfield 196	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   220: getfield 205	android/content/pm/ActivityInfo:name	Ljava/lang/String;
    //   223: invokevirtual 202	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   226: ifeq +181 -> 407
    //   229: aload 19
    //   231: aload 5
    //   233: putfield 196	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   236: new 207	android/app/admin/DeviceAdminInfo
    //   239: dup
    //   240: aload_0
    //   241: aload 19
    //   243: invokespecial 210	android/app/admin/DeviceAdminInfo:<init>	(Landroid/content/Context;Landroid/content/pm/ResolveInfo;)V
    //   246: pop
    //   247: iconst_1
    //   248: istore 17
    //   250: iload 17
    //   252: ifne +161 -> 413
    //   255: ldc 122
    //   257: new 143	java/lang/StringBuilder
    //   260: dup
    //   261: invokespecial 144	java/lang/StringBuilder:<init>	()V
    //   264: ldc 212
    //   266: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   269: aload_2
    //   270: invokevirtual 215	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   273: invokevirtual 157	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   276: invokestatic 130	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   279: pop
    //   280: aload_0
    //   281: invokevirtual 133	com/android/settings/DeviceAdminAdd:finish	()V
    //   284: return
    //   285: astore_3
    //   286: ldc 122
    //   288: new 143	java/lang/StringBuilder
    //   291: dup
    //   292: invokespecial 144	java/lang/StringBuilder:<init>	()V
    //   295: ldc 217
    //   297: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   300: aload_2
    //   301: invokevirtual 215	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   304: invokevirtual 157	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   307: aload_3
    //   308: invokestatic 220	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   311: pop
    //   312: aload_0
    //   313: invokevirtual 133	com/android/settings/DeviceAdminAdd:finish	()V
    //   316: return
    //   317: aload 14
    //   319: invokeinterface 221 1 0
    //   324: istore 15
    //   326: goto -162 -> 164
    //   329: astore 22
    //   331: ldc 122
    //   333: new 143	java/lang/StringBuilder
    //   336: dup
    //   337: invokespecial 144	java/lang/StringBuilder:<init>	()V
    //   340: ldc 223
    //   342: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   345: aload 19
    //   347: getfield 196	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   350: invokevirtual 215	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   353: invokevirtual 157	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   356: aload 22
    //   358: invokestatic 220	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   361: pop
    //   362: iconst_0
    //   363: istore 17
    //   365: goto -115 -> 250
    //   368: astore 20
    //   370: ldc 122
    //   372: new 143	java/lang/StringBuilder
    //   375: dup
    //   376: invokespecial 144	java/lang/StringBuilder:<init>	()V
    //   379: ldc 223
    //   381: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   384: aload 19
    //   386: getfield 196	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   389: invokevirtual 215	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   392: invokevirtual 157	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   395: aload 20
    //   397: invokestatic 220	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   400: pop
    //   401: iconst_0
    //   402: istore 17
    //   404: goto -154 -> 250
    //   407: iinc 16 1
    //   410: goto -243 -> 167
    //   413: new 186	android/content/pm/ResolveInfo
    //   416: dup
    //   417: invokespecial 224	android/content/pm/ResolveInfo:<init>	()V
    //   420: astore 6
    //   422: aload 6
    //   424: aload 5
    //   426: putfield 196	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   429: aload_0
    //   430: new 207	android/app/admin/DeviceAdminInfo
    //   433: dup
    //   434: aload_0
    //   435: aload 6
    //   437: invokespecial 210	android/app/admin/DeviceAdminInfo:<init>	(Landroid/content/Context;Landroid/content/pm/ResolveInfo;)V
    //   440: putfield 226	com/android/settings/DeviceAdminAdd:mDeviceAdmin	Landroid/app/admin/DeviceAdminInfo;
    //   443: ldc 228
    //   445: aload_0
    //   446: invokevirtual 115	com/android/settings/DeviceAdminAdd:getIntent	()Landroid/content/Intent;
    //   449: invokevirtual 154	android/content/Intent:getAction	()Ljava/lang/String;
    //   452: invokevirtual 202	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   455: ifeq +165 -> 620
    //   458: aload_0
    //   459: iconst_0
    //   460: putfield 230	com/android/settings/DeviceAdminAdd:mRefreshing	Z
    //   463: aload_0
    //   464: getfield 110	com/android/settings/DeviceAdminAdd:mDPM	Landroid/app/admin/DevicePolicyManager;
    //   467: aload_2
    //   468: invokevirtual 171	android/app/admin/DevicePolicyManager:isAdminActive	(Landroid/content/ComponentName;)Z
    //   471: ifeq +149 -> 620
    //   474: aload_0
    //   475: getfield 226	com/android/settings/DeviceAdminAdd:mDeviceAdmin	Landroid/app/admin/DeviceAdminInfo;
    //   478: invokevirtual 234	android/app/admin/DeviceAdminInfo:getUsedPolicies	()Ljava/util/ArrayList;
    //   481: astore 11
    //   483: iconst_0
    //   484: istore 12
    //   486: iload 12
    //   488: aload 11
    //   490: invokevirtual 52	java/util/ArrayList:size	()I
    //   493: if_icmpge +36 -> 529
    //   496: aload 11
    //   498: iload 12
    //   500: invokevirtual 56	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   503: checkcast 236	android/app/admin/DeviceAdminInfo$PolicyInfo
    //   506: astore 13
    //   508: aload_0
    //   509: getfield 110	com/android/settings/DeviceAdminAdd:mDPM	Landroid/app/admin/DevicePolicyManager;
    //   512: aload_2
    //   513: aload 13
    //   515: getfield 240	android/app/admin/DeviceAdminInfo$PolicyInfo:ident	I
    //   518: invokevirtual 244	android/app/admin/DevicePolicyManager:hasGrantedPolicy	(Landroid/content/ComponentName;I)Z
    //   521: ifne +93 -> 614
    //   524: aload_0
    //   525: iconst_1
    //   526: putfield 230	com/android/settings/DeviceAdminAdd:mRefreshing	Z
    //   529: aload_0
    //   530: getfield 230	com/android/settings/DeviceAdminAdd:mRefreshing	Z
    //   533: ifne +87 -> 620
    //   536: aload_0
    //   537: iconst_m1
    //   538: invokevirtual 247	com/android/settings/DeviceAdminAdd:setResult	(I)V
    //   541: aload_0
    //   542: invokevirtual 133	com/android/settings/DeviceAdminAdd:finish	()V
    //   545: return
    //   546: astore 9
    //   548: ldc 122
    //   550: new 143	java/lang/StringBuilder
    //   553: dup
    //   554: invokespecial 144	java/lang/StringBuilder:<init>	()V
    //   557: ldc 217
    //   559: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   562: aload_2
    //   563: invokevirtual 215	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   566: invokevirtual 157	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   569: aload 9
    //   571: invokestatic 220	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   574: pop
    //   575: aload_0
    //   576: invokevirtual 133	com/android/settings/DeviceAdminAdd:finish	()V
    //   579: return
    //   580: astore 7
    //   582: ldc 122
    //   584: new 143	java/lang/StringBuilder
    //   587: dup
    //   588: invokespecial 144	java/lang/StringBuilder:<init>	()V
    //   591: ldc 217
    //   593: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   596: aload_2
    //   597: invokevirtual 215	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   600: invokevirtual 157	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   603: aload 7
    //   605: invokestatic 220	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   608: pop
    //   609: aload_0
    //   610: invokevirtual 133	com/android/settings/DeviceAdminAdd:finish	()V
    //   613: return
    //   614: iinc 12 1
    //   617: goto -131 -> 486
    //   620: aload_0
    //   621: aload_0
    //   622: invokevirtual 115	com/android/settings/DeviceAdminAdd:getIntent	()Landroid/content/Intent;
    //   625: ldc 249
    //   627: invokevirtual 253	android/content/Intent:getCharSequenceExtra	(Ljava/lang/String;)Ljava/lang/CharSequence;
    //   630: putfield 255	com/android/settings/DeviceAdminAdd:mAddMsgText	Ljava/lang/CharSequence;
    //   633: aload_0
    //   634: ldc_w 256
    //   637: invokevirtual 259	com/android/settings/DeviceAdminAdd:setContentView	(I)V
    //   640: aload_0
    //   641: aload_0
    //   642: ldc_w 260
    //   645: invokevirtual 264	com/android/settings/DeviceAdminAdd:findViewById	(I)Landroid/view/View;
    //   648: checkcast 266	android/widget/ImageView
    //   651: putfield 268	com/android/settings/DeviceAdminAdd:mAdminIcon	Landroid/widget/ImageView;
    //   654: aload_0
    //   655: aload_0
    //   656: ldc_w 269
    //   659: invokevirtual 264	com/android/settings/DeviceAdminAdd:findViewById	(I)Landroid/view/View;
    //   662: checkcast 271	android/widget/TextView
    //   665: putfield 273	com/android/settings/DeviceAdminAdd:mAdminName	Landroid/widget/TextView;
    //   668: aload_0
    //   669: aload_0
    //   670: ldc_w 274
    //   673: invokevirtual 264	com/android/settings/DeviceAdminAdd:findViewById	(I)Landroid/view/View;
    //   676: checkcast 271	android/widget/TextView
    //   679: putfield 276	com/android/settings/DeviceAdminAdd:mAdminDescription	Landroid/widget/TextView;
    //   682: aload_0
    //   683: aload_0
    //   684: ldc_w 277
    //   687: invokevirtual 264	com/android/settings/DeviceAdminAdd:findViewById	(I)Landroid/view/View;
    //   690: checkcast 271	android/widget/TextView
    //   693: putfield 279	com/android/settings/DeviceAdminAdd:mAddMsg	Landroid/widget/TextView;
    //   696: aload_0
    //   697: aload_0
    //   698: ldc_w 280
    //   701: invokevirtual 264	com/android/settings/DeviceAdminAdd:findViewById	(I)Landroid/view/View;
    //   704: checkcast 266	android/widget/ImageView
    //   707: putfield 282	com/android/settings/DeviceAdminAdd:mAddMsgExpander	Landroid/widget/ImageView;
    //   710: aload_0
    //   711: getfield 279	com/android/settings/DeviceAdminAdd:mAddMsg	Landroid/widget/TextView;
    //   714: new 284	com/android/settings/DeviceAdminAdd$1
    //   717: dup
    //   718: aload_0
    //   719: invokespecial 287	com/android/settings/DeviceAdminAdd$1:<init>	(Lcom/android/settings/DeviceAdminAdd;)V
    //   722: invokevirtual 291	android/widget/TextView:setOnClickListener	(Landroid/view/View$OnClickListener;)V
    //   725: aload_0
    //   726: aload_0
    //   727: getfield 279	com/android/settings/DeviceAdminAdd:mAddMsg	Landroid/widget/TextView;
    //   730: invokevirtual 295	com/android/settings/DeviceAdminAdd:toggleMessageEllipsis	(Landroid/view/View;)V
    //   733: aload_0
    //   734: aload_0
    //   735: ldc_w 296
    //   738: invokevirtual 264	com/android/settings/DeviceAdminAdd:findViewById	(I)Landroid/view/View;
    //   741: checkcast 271	android/widget/TextView
    //   744: putfield 298	com/android/settings/DeviceAdminAdd:mAdminWarning	Landroid/widget/TextView;
    //   747: aload_0
    //   748: aload_0
    //   749: ldc_w 299
    //   752: invokevirtual 264	com/android/settings/DeviceAdminAdd:findViewById	(I)Landroid/view/View;
    //   755: checkcast 301	android/view/ViewGroup
    //   758: putfield 303	com/android/settings/DeviceAdminAdd:mAdminPolicies	Landroid/view/ViewGroup;
    //   761: aload_0
    //   762: aload_0
    //   763: ldc_w 304
    //   766: invokevirtual 264	com/android/settings/DeviceAdminAdd:findViewById	(I)Landroid/view/View;
    //   769: checkcast 306	android/widget/Button
    //   772: putfield 308	com/android/settings/DeviceAdminAdd:mCancelButton	Landroid/widget/Button;
    //   775: aload_0
    //   776: getfield 308	com/android/settings/DeviceAdminAdd:mCancelButton	Landroid/widget/Button;
    //   779: new 310	com/android/settings/DeviceAdminAdd$2
    //   782: dup
    //   783: aload_0
    //   784: invokespecial 311	com/android/settings/DeviceAdminAdd$2:<init>	(Lcom/android/settings/DeviceAdminAdd;)V
    //   787: invokevirtual 312	android/widget/Button:setOnClickListener	(Landroid/view/View$OnClickListener;)V
    //   790: aload_0
    //   791: aload_0
    //   792: ldc_w 313
    //   795: invokevirtual 264	com/android/settings/DeviceAdminAdd:findViewById	(I)Landroid/view/View;
    //   798: checkcast 306	android/widget/Button
    //   801: putfield 315	com/android/settings/DeviceAdminAdd:mActionButton	Landroid/widget/Button;
    //   804: aload_0
    //   805: getfield 315	com/android/settings/DeviceAdminAdd:mActionButton	Landroid/widget/Button;
    //   808: new 317	com/android/settings/DeviceAdminAdd$3
    //   811: dup
    //   812: aload_0
    //   813: invokespecial 318	com/android/settings/DeviceAdminAdd$3:<init>	(Lcom/android/settings/DeviceAdminAdd;)V
    //   816: invokevirtual 312	android/widget/Button:setOnClickListener	(Landroid/view/View$OnClickListener;)V
    //   819: return
    //
    // Exception table:
    //   from	to	target	type
    //   112	125	285	android/content/pm/PackageManager$NameNotFoundException
    //   229	247	329	org/xmlpull/v1/XmlPullParserException
    //   229	247	368	java/io/IOException
    //   429	443	546	org/xmlpull/v1/XmlPullParserException
    //   429	443	580	java/io/IOException
  }

  protected Dialog onCreateDialog(int paramInt, Bundle paramBundle)
  {
    switch (paramInt)
    {
    default:
      return super.onCreateDialog(paramInt, paramBundle);
    case 1:
    }
    CharSequence localCharSequence = paramBundle.getCharSequence("android.app.extra.DISABLE_WARNING");
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
    localBuilder.setMessage(localCharSequence);
    localBuilder.setPositiveButton(2131428420, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        DeviceAdminAdd.this.mDPM.removeActiveAdmin(DeviceAdminAdd.this.mDeviceAdmin.getComponent());
        DeviceAdminAdd.this.finish();
      }
    });
    localBuilder.setNegativeButton(2131428421, null);
    return localBuilder.create();
  }

  protected void onResume()
  {
    super.onResume();
    updateInterface();
  }

  void toggleMessageEllipsis(View paramView)
  {
    TextView localTextView = (TextView)paramView;
    boolean bool;
    TextUtils.TruncateAt localTruncateAt;
    label31: int i;
    label50: ImageView localImageView;
    if (!this.mAddMsgEllipsized)
    {
      bool = true;
      this.mAddMsgEllipsized = bool;
      if (!this.mAddMsgEllipsized)
        break label87;
      localTruncateAt = TextUtils.TruncateAt.END;
      localTextView.setEllipsize(localTruncateAt);
      if (!this.mAddMsgEllipsized)
        break label93;
      i = getEllipsizedLines();
      localTextView.setMaxLines(i);
      localImageView = this.mAddMsgExpander;
      if (!this.mAddMsgEllipsized)
        break label100;
    }
    label87: label93: label100: for (int j = 17302131; ; j = 17302130)
    {
      localImageView.setImageResource(j);
      return;
      bool = false;
      break;
      localTruncateAt = null;
      break label31;
      i = 15;
      break label50;
    }
  }

  void updateInterface()
  {
    this.mAdminIcon.setImageDrawable(this.mDeviceAdmin.loadIcon(getPackageManager()));
    this.mAdminName.setText(this.mDeviceAdmin.loadLabel(getPackageManager()));
    try
    {
      this.mAdminDescription.setText(this.mDeviceAdmin.loadDescription(getPackageManager()));
      this.mAdminDescription.setVisibility(0);
      if (this.mAddMsgText != null)
      {
        this.mAddMsg.setText(this.mAddMsgText);
        this.mAddMsg.setVisibility(0);
        if ((this.mRefreshing) || (!this.mDPM.isAdminActive(this.mDeviceAdmin.getComponent())))
          break label325;
        if (this.mActivePolicies.size() != 0)
          break label230;
        ArrayList localArrayList2 = this.mDeviceAdmin.getUsedPolicies();
        for (int j = 0; j < localArrayList2.size(); j++)
        {
          View localView2 = AppSecurityPermissions.getPermissionItemView(this, getText(((DeviceAdminInfo.PolicyInfo)localArrayList2.get(j)).label), "", true);
          this.mActivePolicies.add(localView2);
          this.mAdminPolicies.addView(localView2);
        }
      }
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
      while (true)
      {
        this.mAdminDescription.setVisibility(8);
        continue;
        this.mAddMsg.setVisibility(8);
        this.mAddMsgExpander.setVisibility(8);
      }
      label230: setViewVisibility(this.mActivePolicies, 0);
      setViewVisibility(this.mAddingPolicies, 8);
      TextView localTextView2 = this.mAdminWarning;
      Object[] arrayOfObject2 = new Object[1];
      arrayOfObject2[0] = this.mDeviceAdmin.getActivityInfo().applicationInfo.loadLabel(getPackageManager());
      localTextView2.setText(getString(2131428934, arrayOfObject2));
      setTitle(getText(2131428926));
      this.mActionButton.setText(getText(2131428927));
      this.mAdding = false;
      return;
    }
    label325: if (this.mAddingPolicies.size() == 0)
    {
      ArrayList localArrayList1 = this.mDeviceAdmin.getUsedPolicies();
      for (int i = 0; i < localArrayList1.size(); i++)
      {
        DeviceAdminInfo.PolicyInfo localPolicyInfo = (DeviceAdminInfo.PolicyInfo)localArrayList1.get(i);
        View localView1 = AppSecurityPermissions.getPermissionItemView(this, getText(localPolicyInfo.label), getText(localPolicyInfo.description), true);
        this.mAddingPolicies.add(localView1);
        this.mAdminPolicies.addView(localView1);
      }
    }
    setViewVisibility(this.mAddingPolicies, 0);
    setViewVisibility(this.mActivePolicies, 8);
    TextView localTextView1 = this.mAdminWarning;
    Object[] arrayOfObject1 = new Object[1];
    arrayOfObject1[0] = this.mDeviceAdmin.getActivityInfo().applicationInfo.loadLabel(getPackageManager());
    localTextView1.setText(getString(2131428933, arrayOfObject1));
    setTitle(getText(2131428930));
    this.mActionButton.setText(getText(2131428931));
    this.mAdding = true;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.DeviceAdminAdd
 * JD-Core Version:    0.6.2
 */