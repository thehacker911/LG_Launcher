package com.android.settings.users;

import android.content.Context;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.UserManager;

public class UserUtils
{
  public static Drawable getUserIcon(Context paramContext, UserManager paramUserManager, UserInfo paramUserInfo, Resources paramResources)
  {
    if (paramUserInfo.iconPath == null);
    Bitmap localBitmap;
    do
    {
      return null;
      localBitmap = paramUserManager.getUserIcon(paramUserInfo.id);
    }
    while (localBitmap == null);
    return CircleFramedDrawable.getInstance(paramContext, localBitmap);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.users.UserUtils
 * JD-Core Version:    0.6.2
 */