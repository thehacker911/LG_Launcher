package com.android.settings;

import android.content.Context;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

public class SelectableEditTextPreference extends EditTextPreference
{
  private int mSelectionMode;

  public SelectableEditTextPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  protected void onBindDialogView(View paramView)
  {
    super.onBindDialogView(paramView);
    EditText localEditText = getEditText();
    if (localEditText.getText() != null);
    for (int i = localEditText.getText().length(); ; i = 0)
    {
      if (!TextUtils.isEmpty(localEditText.getText()));
      switch (this.mSelectionMode)
      {
      default:
        return;
      case 0:
      case 1:
      case 2:
      }
    }
    localEditText.setSelection(i);
    return;
    localEditText.setSelection(0);
    return;
    localEditText.setSelection(0, i);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.SelectableEditTextPreference
 * JD-Core Version:    0.6.2
 */