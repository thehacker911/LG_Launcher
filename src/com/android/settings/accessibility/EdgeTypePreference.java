package com.android.settings.accessibility;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.android.internal.widget.SubtitleView;

public class EdgeTypePreference extends ListDialogPreference
{
  public EdgeTypePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    Resources localResources = paramContext.getResources();
    setValues(localResources.getIntArray(2131165245));
    setTitles(localResources.getStringArray(2131165244));
    setDialogLayoutResource(2130968634);
    setListItemLayoutResource(2130968691);
  }

  protected void onBindListItem(View paramView, int paramInt)
  {
    SubtitleView localSubtitleView = (SubtitleView)paramView.findViewById(2131230842);
    localSubtitleView.setForegroundColor(-1);
    localSubtitleView.setBackgroundColor(0);
    localSubtitleView.setTextSize(96.0F);
    localSubtitleView.setEdgeType(getValueAt(paramInt));
    localSubtitleView.setEdgeColor(-16777216);
    CharSequence localCharSequence = getTitleAt(paramInt);
    if (localCharSequence != null)
      ((TextView)paramView.findViewById(2131230772)).setText(localCharSequence);
  }

  public boolean shouldDisableDependents()
  {
    return (getValue() == 0) || (super.shouldDisableDependents());
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accessibility.EdgeTypePreference
 * JD-Core Version:    0.6.2
 */