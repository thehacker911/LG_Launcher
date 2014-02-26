package com.android.settings;

import android.app.Dialog;
import android.content.Context;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

class EditPinPreference extends EditTextPreference
{
  private OnPinEnteredListener mPinListener;

  public EditPinPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public EditPinPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }

  public boolean isDialogOpen()
  {
    Dialog localDialog = getDialog();
    return (localDialog != null) && (localDialog.isShowing());
  }

  protected void onBindDialogView(View paramView)
  {
    super.onBindDialogView(paramView);
    EditText localEditText = getEditText();
    if (localEditText != null)
      localEditText.setInputType(18);
  }

  protected void onDialogClosed(boolean paramBoolean)
  {
    super.onDialogClosed(paramBoolean);
    if (this.mPinListener != null)
      this.mPinListener.onPinEntered(this, paramBoolean);
  }

  public void setOnPinEnteredListener(OnPinEnteredListener paramOnPinEnteredListener)
  {
    this.mPinListener = paramOnPinEnteredListener;
  }

  public void showPinDialog()
  {
    Dialog localDialog = getDialog();
    if ((localDialog == null) || (!localDialog.isShowing()))
      showDialog(null);
  }

  static abstract interface OnPinEnteredListener
  {
    public abstract void onPinEntered(EditPinPreference paramEditPinPreference, boolean paramBoolean);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.EditPinPreference
 * JD-Core Version:    0.6.2
 */