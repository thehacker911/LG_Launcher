package com.android.settings.users;

import android.content.Context;
import android.content.RestrictionEntry;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import java.util.ArrayList;
import java.util.Iterator;

public class RestrictionUtils
{
  public static final int[] sRestrictionDescriptions = { 2131429287 };
  public static final String[] sRestrictionKeys = { "no_share_location" };
  public static final int[] sRestrictionTitles = { 2131429286 };

  public static ArrayList<RestrictionEntry> getRestrictions(Context paramContext, UserHandle paramUserHandle)
  {
    Resources localResources = paramContext.getResources();
    ArrayList localArrayList = new ArrayList();
    Bundle localBundle = UserManager.get(paramContext).getUserRestrictions(paramUserHandle);
    int i = 0;
    if (i < sRestrictionKeys.length)
    {
      String str = sRestrictionKeys[i];
      if (!localBundle.getBoolean(sRestrictionKeys[i], false));
      for (boolean bool = true; ; bool = false)
      {
        RestrictionEntry localRestrictionEntry = new RestrictionEntry(str, bool);
        localRestrictionEntry.setTitle(localResources.getString(sRestrictionTitles[i]));
        localRestrictionEntry.setDescription(localResources.getString(sRestrictionDescriptions[i]));
        localRestrictionEntry.setType(1);
        localArrayList.add(localRestrictionEntry);
        i++;
        break;
      }
    }
    return localArrayList;
  }

  public static Bundle restrictionsToBundle(ArrayList<RestrictionEntry> paramArrayList)
  {
    Bundle localBundle = new Bundle();
    Iterator localIterator = paramArrayList.iterator();
    while (localIterator.hasNext())
    {
      RestrictionEntry localRestrictionEntry = (RestrictionEntry)localIterator.next();
      if (localRestrictionEntry.getType() == 1)
        localBundle.putBoolean(localRestrictionEntry.getKey(), localRestrictionEntry.getSelectedState());
      else if (localRestrictionEntry.getType() == 4)
        localBundle.putStringArray(localRestrictionEntry.getKey(), localRestrictionEntry.getAllSelectedStrings());
      else
        localBundle.putString(localRestrictionEntry.getKey(), localRestrictionEntry.getSelectedString());
    }
    return localBundle;
  }

  public static void setRestrictions(Context paramContext, ArrayList<RestrictionEntry> paramArrayList, UserHandle paramUserHandle)
  {
    UserManager localUserManager = UserManager.get(paramContext);
    Bundle localBundle = localUserManager.getUserRestrictions(paramUserHandle);
    Iterator localIterator = paramArrayList.iterator();
    if (localIterator.hasNext())
    {
      RestrictionEntry localRestrictionEntry = (RestrictionEntry)localIterator.next();
      String str = localRestrictionEntry.getKey();
      if (!localRestrictionEntry.getSelectedState());
      for (boolean bool = true; ; bool = false)
      {
        localBundle.putBoolean(str, bool);
        if ((!localRestrictionEntry.getKey().equals("no_share_location")) || (localRestrictionEntry.getSelectedState()))
          break;
        Settings.Secure.putStringForUser(paramContext.getContentResolver(), "location_providers_allowed", "", paramUserHandle.getIdentifier());
        break;
      }
    }
    localUserManager.setUserRestrictions(localBundle, paramUserHandle);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.users.RestrictionUtils
 * JD-Core Version:    0.6.2
 */