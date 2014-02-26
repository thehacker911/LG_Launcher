package com.android.settings.accessibility;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.preference.DialogPreference;
import android.preference.Preference.BaseSavedState;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

public abstract class ListDialogPreference extends DialogPreference
{
  private CharSequence[] mEntryTitles;
  private int[] mEntryValues;
  private int mListItemLayout;
  private OnValueChangedListener mOnValueChangedListener;
  private int mValue;
  private int mValueIndex;
  private boolean mValueSet;

  public ListDialogPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  protected int getIndexForValue(int paramInt)
  {
    int[] arrayOfInt = this.mEntryValues;
    int i = arrayOfInt.length;
    for (int j = 0; j < i; j++)
      if (arrayOfInt[j] == paramInt)
        return j;
    return -1;
  }

  public CharSequence getSummary()
  {
    if (this.mValueIndex >= 0)
      return getTitleAt(this.mValueIndex);
    return null;
  }

  protected CharSequence getTitleAt(int paramInt)
  {
    if ((this.mEntryTitles == null) || (this.mEntryTitles.length <= paramInt))
      return null;
    return this.mEntryTitles[paramInt];
  }

  public int getValue()
  {
    return this.mValue;
  }

  protected int getValueAt(int paramInt)
  {
    return this.mEntryValues[paramInt];
  }

  protected abstract void onBindListItem(View paramView, int paramInt);

  protected Object onGetDefaultValue(TypedArray paramTypedArray, int paramInt)
  {
    return Integer.valueOf(paramTypedArray.getInt(paramInt, 0));
  }

  protected void onPrepareDialogBuilder(AlertDialog.Builder paramBuilder)
  {
    super.onPrepareDialogBuilder(paramBuilder);
    Context localContext = getContext();
    int i = getDialogLayoutResource();
    View localView = LayoutInflater.from(localContext).inflate(i, null);
    ListPreferenceAdapter localListPreferenceAdapter = new ListPreferenceAdapter(null);
    AbsListView localAbsListView = (AbsListView)localView.findViewById(16908298);
    localAbsListView.setAdapter(localListPreferenceAdapter);
    localAbsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        if (ListDialogPreference.this.callChangeListener(Integer.valueOf((int)paramAnonymousLong)))
          ListDialogPreference.this.setValue((int)paramAnonymousLong);
        Dialog localDialog = ListDialogPreference.this.getDialog();
        if (localDialog != null)
          localDialog.dismiss();
      }
    });
    int j = getIndexForValue(this.mValue);
    if (j != -1)
      localAbsListView.setSelection(j);
    paramBuilder.setView(localView);
    paramBuilder.setPositiveButton(null, null);
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
    setValue(localSavedState.value);
  }

  protected Parcelable onSaveInstanceState()
  {
    Parcelable localParcelable = super.onSaveInstanceState();
    if (isPersistent())
      return localParcelable;
    SavedState localSavedState = new SavedState(localParcelable);
    localSavedState.value = getValue();
    return localSavedState;
  }

  protected void onSetInitialValue(boolean paramBoolean, Object paramObject)
  {
    if (paramBoolean);
    for (int i = getPersistedInt(this.mValue); ; i = ((Integer)paramObject).intValue())
    {
      setValue(i);
      return;
    }
  }

  public void setListItemLayoutResource(int paramInt)
  {
    this.mListItemLayout = paramInt;
  }

  public void setOnValueChangedListener(OnValueChangedListener paramOnValueChangedListener)
  {
    this.mOnValueChangedListener = paramOnValueChangedListener;
  }

  public void setTitles(CharSequence[] paramArrayOfCharSequence)
  {
    this.mEntryTitles = paramArrayOfCharSequence;
  }

  public void setValue(int paramInt)
  {
    if (this.mValue != paramInt);
    for (int i = 1; ; i = 0)
    {
      if ((i != 0) || (!this.mValueSet))
      {
        this.mValue = paramInt;
        this.mValueIndex = getIndexForValue(paramInt);
        this.mValueSet = true;
        persistInt(paramInt);
        if (i != 0)
        {
          notifyDependencyChange(shouldDisableDependents());
          notifyChanged();
        }
        if (this.mOnValueChangedListener != null)
          this.mOnValueChangedListener.onValueChanged(this, paramInt);
      }
      return;
    }
  }

  public void setValues(int[] paramArrayOfInt)
  {
    this.mEntryValues = paramArrayOfInt;
  }

  private class ListPreferenceAdapter extends BaseAdapter
  {
    private LayoutInflater mInflater;

    private ListPreferenceAdapter()
    {
    }

    public int getCount()
    {
      return ListDialogPreference.this.mEntryValues.length;
    }

    public Integer getItem(int paramInt)
    {
      return Integer.valueOf(ListDialogPreference.this.mEntryValues[paramInt]);
    }

    public long getItemId(int paramInt)
    {
      return ListDialogPreference.this.mEntryValues[paramInt];
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView == null)
      {
        if (this.mInflater == null)
          this.mInflater = LayoutInflater.from(paramViewGroup.getContext());
        paramView = this.mInflater.inflate(ListDialogPreference.this.mListItemLayout, paramViewGroup, false);
      }
      ListDialogPreference.this.onBindListItem(paramView, paramInt);
      return paramView;
    }

    public boolean hasStableIds()
    {
      return true;
    }
  }

  public static abstract interface OnValueChangedListener
  {
    public abstract void onValueChanged(ListDialogPreference paramListDialogPreference, int paramInt);
  }

  private static class SavedState extends Preference.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public ListDialogPreference.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ListDialogPreference.SavedState(paramAnonymousParcel);
      }

      public ListDialogPreference.SavedState[] newArray(int paramAnonymousInt)
      {
        return new ListDialogPreference.SavedState[paramAnonymousInt];
      }
    };
    public int value;

    public SavedState(Parcel paramParcel)
    {
      super();
      this.value = paramParcel.readInt();
    }

    public SavedState(Parcelable paramParcelable)
    {
      super();
    }

    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.value);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accessibility.ListDialogPreference
 * JD-Core Version:    0.6.2
 */