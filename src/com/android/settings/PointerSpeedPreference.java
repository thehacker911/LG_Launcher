package com.android.settings;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.preference.Preference.BaseSavedState;
import android.preference.SeekBarDialogPreference;
import android.provider.Settings.System;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class PointerSpeedPreference extends SeekBarDialogPreference
  implements SeekBar.OnSeekBarChangeListener
{
  private final InputManager mIm = (InputManager)getContext().getSystemService("input");
  private int mOldSpeed;
  private boolean mRestoredOldState;
  private SeekBar mSeekBar;
  private ContentObserver mSpeedObserver = new ContentObserver(new Handler())
  {
    public void onChange(boolean paramAnonymousBoolean)
    {
      PointerSpeedPreference.this.onSpeedChanged();
    }
  };
  private boolean mTouchInProgress;

  public PointerSpeedPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  private void onSpeedChanged()
  {
    int i = this.mIm.getPointerSpeed(getContext());
    this.mSeekBar.setProgress(i + 7);
  }

  private void restoreOldState()
  {
    if (this.mRestoredOldState)
      return;
    this.mIm.tryPointerSpeed(this.mOldSpeed);
    this.mRestoredOldState = true;
  }

  protected void onBindDialogView(View paramView)
  {
    super.onBindDialogView(paramView);
    this.mSeekBar = getSeekBar(paramView);
    this.mSeekBar.setMax(14);
    this.mOldSpeed = this.mIm.getPointerSpeed(getContext());
    this.mSeekBar.setProgress(7 + this.mOldSpeed);
    this.mSeekBar.setOnSeekBarChangeListener(this);
  }

  protected void onDialogClosed(boolean paramBoolean)
  {
    super.onDialogClosed(paramBoolean);
    ContentResolver localContentResolver = getContext().getContentResolver();
    if (paramBoolean)
      this.mIm.setPointerSpeed(getContext(), -7 + this.mSeekBar.getProgress());
    while (true)
    {
      localContentResolver.unregisterContentObserver(this.mSpeedObserver);
      return;
      restoreOldState();
    }
  }

  public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean)
  {
    if (!this.mTouchInProgress)
      this.mIm.tryPointerSpeed(paramInt - 7);
  }

  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if ((paramParcelable == null) || (!paramParcelable.getClass().equals(SavedState.class)))
    {
      super.onRestoreInstanceState(paramParcelable);
      return;
    }
    SavedState localSavedState = (SavedState)paramParcelable;
    super.onRestoreInstanceState(localSavedState.getSuperState());
    this.mOldSpeed = localSavedState.oldSpeed;
    this.mSeekBar.setProgress(localSavedState.progress);
    this.mIm.tryPointerSpeed(-7 + localSavedState.progress);
  }

  protected Parcelable onSaveInstanceState()
  {
    Parcelable localParcelable = super.onSaveInstanceState();
    if ((getDialog() == null) || (!getDialog().isShowing()))
      return localParcelable;
    SavedState localSavedState = new SavedState(localParcelable);
    localSavedState.progress = this.mSeekBar.getProgress();
    localSavedState.oldSpeed = this.mOldSpeed;
    restoreOldState();
    return localSavedState;
  }

  public void onStartTrackingTouch(SeekBar paramSeekBar)
  {
    this.mTouchInProgress = true;
  }

  public void onStopTrackingTouch(SeekBar paramSeekBar)
  {
    this.mTouchInProgress = false;
    this.mIm.tryPointerSpeed(-7 + paramSeekBar.getProgress());
  }

  protected void showDialog(Bundle paramBundle)
  {
    super.showDialog(paramBundle);
    getContext().getContentResolver().registerContentObserver(Settings.System.getUriFor("pointer_speed"), true, this.mSpeedObserver);
    this.mRestoredOldState = false;
  }

  private static class SavedState extends Preference.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public PointerSpeedPreference.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new PointerSpeedPreference.SavedState(paramAnonymousParcel);
      }

      public PointerSpeedPreference.SavedState[] newArray(int paramAnonymousInt)
      {
        return new PointerSpeedPreference.SavedState[paramAnonymousInt];
      }
    };
    int oldSpeed;
    int progress;

    public SavedState(Parcel paramParcel)
    {
      super();
      this.progress = paramParcel.readInt();
      this.oldSpeed = paramParcel.readInt();
    }

    public SavedState(Parcelable paramParcelable)
    {
      super();
    }

    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.progress);
      paramParcel.writeInt(this.oldSpeed);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.PointerSpeedPreference
 * JD-Core Version:    0.6.2
 */