package com.android.settings.applications;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class AppViewHolder
{
  public ImageView appIcon;
  public TextView appName;
  public TextView appSize;
  public CheckBox checkBox;
  public TextView disabled;
  public ApplicationsState.AppEntry entry;
  public View rootView;

  public static AppViewHolder createOrRecycle(LayoutInflater paramLayoutInflater, View paramView)
  {
    if (paramView == null)
    {
      View localView = paramLayoutInflater.inflate(2130968645, null);
      AppViewHolder localAppViewHolder = new AppViewHolder();
      localAppViewHolder.rootView = localView;
      localAppViewHolder.appName = ((TextView)localView.findViewById(2131230736));
      localAppViewHolder.appIcon = ((ImageView)localView.findViewById(2131230735));
      localAppViewHolder.appSize = ((TextView)localView.findViewById(2131230902));
      localAppViewHolder.disabled = ((TextView)localView.findViewById(2131230903));
      localAppViewHolder.checkBox = ((CheckBox)localView.findViewById(2131230901));
      localView.setTag(localAppViewHolder);
      return localAppViewHolder;
    }
    return (AppViewHolder)paramView.getTag();
  }

  void updateSizeText(CharSequence paramCharSequence, int paramInt)
  {
    if (this.entry.sizeStr != null)
      switch (paramInt)
      {
      default:
        this.appSize.setText(this.entry.sizeStr);
      case 1:
      case 2:
      }
    while (this.entry.size != -2L)
    {
      return;
      this.appSize.setText(this.entry.internalSizeStr);
      return;
      this.appSize.setText(this.entry.externalSizeStr);
      return;
    }
    this.appSize.setText(paramCharSequence);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.applications.AppViewHolder
 * JD-Core Version:    0.6.2
 */