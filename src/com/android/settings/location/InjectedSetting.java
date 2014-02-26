package com.android.settings.location;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.annotations.Immutable;
import com.android.internal.util.Preconditions;

@Immutable
class InjectedSetting
{
  public final String className;
  public final int iconId;
  public final String packageName;
  public final String settingsActivity;
  public final String title;

  private InjectedSetting(String paramString1, String paramString2, String paramString3, int paramInt, String paramString4)
  {
    this.packageName = ((String)Preconditions.checkNotNull(paramString1, "packageName"));
    this.className = ((String)Preconditions.checkNotNull(paramString2, "className"));
    this.title = ((String)Preconditions.checkNotNull(paramString3, "title"));
    this.iconId = paramInt;
    this.settingsActivity = ((String)Preconditions.checkNotNull(paramString4));
  }

  public static InjectedSetting newInstance(String paramString1, String paramString2, String paramString3, int paramInt, String paramString4)
  {
    if ((paramString1 == null) || (paramString2 == null) || (TextUtils.isEmpty(paramString3)) || (TextUtils.isEmpty(paramString4)))
    {
      if (Log.isLoggable("SettingsInjector", 5))
        Log.w("SettingsInjector", "Illegal setting specification: package=" + paramString1 + ", class=" + paramString2 + ", title=" + paramString3 + ", settingsActivity=" + paramString4);
      return null;
    }
    return new InjectedSetting(paramString1, paramString2, paramString3, paramInt, paramString4);
  }

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    InjectedSetting localInjectedSetting;
    do
    {
      return true;
      if (!(paramObject instanceof InjectedSetting))
        return false;
      localInjectedSetting = (InjectedSetting)paramObject;
    }
    while ((this.packageName.equals(localInjectedSetting.packageName)) && (this.className.equals(localInjectedSetting.className)) && (this.title.equals(localInjectedSetting.title)) && (this.iconId == localInjectedSetting.iconId) && (this.settingsActivity.equals(localInjectedSetting.settingsActivity)));
    return false;
  }

  public Intent getServiceIntent()
  {
    Intent localIntent = new Intent();
    localIntent.setClassName(this.packageName, this.className);
    return localIntent;
  }

  public int hashCode()
  {
    return 31 * (31 * (31 * (31 * this.packageName.hashCode() + this.className.hashCode()) + this.title.hashCode()) + this.iconId) + this.settingsActivity.hashCode();
  }

  public String toString()
  {
    return "InjectedSetting{mPackageName='" + this.packageName + '\'' + ", mClassName='" + this.className + '\'' + ", label=" + this.title + ", iconId=" + this.iconId + ", settingsActivity='" + this.settingsActivity + '\'' + '}';
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.location.InjectedSetting
 * JD-Core Version:    0.6.2
 */