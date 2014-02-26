package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.RingtonePreference;
import android.util.AttributeSet;

public class DefaultRingtonePreference extends RingtonePreference
{
  public DefaultRingtonePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  protected void onPrepareRingtonePickerIntent(Intent paramIntent)
  {
    super.onPrepareRingtonePickerIntent(paramIntent);
    paramIntent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", false);
  }

  protected Uri onRestoreRingtone()
  {
    return RingtoneManager.getActualDefaultRingtoneUri(getContext(), getRingtoneType());
  }

  protected void onSaveRingtone(Uri paramUri)
  {
    RingtoneManager.setActualDefaultRingtoneUri(getContext(), getRingtoneType(), paramUri);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.DefaultRingtonePreference
 * JD-Core Version:    0.6.2
 */