package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class RegulatoryInfoDisplayActivity extends Activity
  implements DialogInterface.OnDismissListener
{
  private final String REGULATORY_INFO_RESOURCE = "regulatory_info";

  private int getResourceId()
  {
    int i = getResources().getIdentifier("regulatory_info", "drawable", getPackageName());
    String str1 = SystemProperties.get("ro.boot.hardware.sku", "");
    if (!TextUtils.isEmpty(str1))
    {
      String str2 = "regulatory_info_" + str1.toLowerCase();
      int j = getResources().getIdentifier(str2, "drawable", getPackageName());
      if (j != 0)
        i = j;
    }
    return i;
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Resources localResources = getResources();
    if (!localResources.getBoolean(2131296261))
      finish();
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this).setTitle(2131428300).setOnDismissListener(this);
    int i = getResourceId();
    int j = 0;
    if (i != 0);
    try
    {
      Drawable localDrawable = localResources.getDrawable(i);
      if (localDrawable.getIntrinsicWidth() > 2)
      {
        int k = localDrawable.getIntrinsicHeight();
        if (k <= 2);
      }
      for (j = 1; ; j = 0)
      {
        localCharSequence = localResources.getText(2131429293);
        if (j == 0)
          break;
        View localView = getLayoutInflater().inflate(2130968698, null);
        ((ImageView)localView.findViewById(2131231029)).setImageResource(i);
        localBuilder.setView(localView);
        localBuilder.show();
        return;
      }
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
      CharSequence localCharSequence;
      while (true)
        j = 0;
      if (localCharSequence.length() > 0)
      {
        localBuilder.setMessage(localCharSequence);
        ((TextView)localBuilder.show().findViewById(16908299)).setGravity(17);
        return;
      }
      finish();
    }
  }

  public void onDismiss(DialogInterface paramDialogInterface)
  {
    finish();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.RegulatoryInfoDisplayActivity
 * JD-Core Version:    0.6.2
 */