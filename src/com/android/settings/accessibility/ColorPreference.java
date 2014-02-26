package com.android.settings.accessibility;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ColorPreference extends ListDialogPreference
{
  private ColorDrawable mPreviewColor;
  private boolean mPreviewEnabled;

  public ColorPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setDialogLayoutResource(2130968634);
    setListItemLayoutResource(2130968599);
  }

  protected CharSequence getTitleAt(int paramInt)
  {
    CharSequence localCharSequence = super.getTitleAt(paramInt);
    if (localCharSequence != null)
      return localCharSequence;
    int i = getValueAt(paramInt);
    int j = Color.red(i);
    int k = Color.green(i);
    int m = Color.blue(i);
    Context localContext = getContext();
    Object[] arrayOfObject = new Object[3];
    arrayOfObject[0] = Integer.valueOf(j);
    arrayOfObject[1] = Integer.valueOf(k);
    arrayOfObject[2] = Integer.valueOf(m);
    return localContext.getString(2131428675, arrayOfObject);
  }

  protected void onBindListItem(View paramView, int paramInt)
  {
    int i = getValueAt(paramInt);
    int j = Color.alpha(i);
    ImageView localImageView = (ImageView)paramView.findViewById(2131230771);
    if (j < 255)
    {
      localImageView.setBackgroundResource(2130837692);
      Drawable localDrawable = localImageView.getDrawable();
      if (!(localDrawable instanceof ColorDrawable))
        break label98;
      ((ColorDrawable)localDrawable).setColor(i);
    }
    while (true)
    {
      CharSequence localCharSequence = getTitleAt(paramInt);
      if (localCharSequence != null)
        ((TextView)paramView.findViewById(2131230772)).setText(localCharSequence);
      return;
      localImageView.setBackground(null);
      break;
      label98: localImageView.setImageDrawable(new ColorDrawable(i));
    }
  }

  protected void onBindView(View paramView)
  {
    super.onBindView(paramView);
    ImageView localImageView;
    int i;
    if (this.mPreviewEnabled)
    {
      localImageView = (ImageView)paramView.findViewById(2131230940);
      i = getValue();
      if (Color.alpha(i) >= 255)
        break label107;
      localImageView.setBackgroundResource(2130837692);
      if (this.mPreviewColor != null)
        break label115;
      this.mPreviewColor = new ColorDrawable(i);
      localImageView.setImageDrawable(this.mPreviewColor);
      label70: CharSequence localCharSequence = getSummary();
      if (TextUtils.isEmpty(localCharSequence))
        break label126;
      localImageView.setContentDescription(localCharSequence);
      label90: if (!isEnabled())
        break label134;
    }
    label134: for (float f = 1.0F; ; f = 0.2F)
    {
      localImageView.setAlpha(f);
      return;
      label107: localImageView.setBackground(null);
      break;
      label115: this.mPreviewColor.setColor(i);
      break label70;
      label126: localImageView.setContentDescription(null);
      break label90;
    }
  }

  public boolean shouldDisableDependents()
  {
    return (getValue() == 0) || (super.shouldDisableDependents());
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accessibility.ColorPreference
 * JD-Core Version:    0.6.2
 */