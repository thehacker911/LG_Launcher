package com.android.settings.inputmethod;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.internal.inputmethod.InputMethodUtils;
import com.android.settings.SettingsPreferenceFragment;
import java.text.Collator;
import java.util.Iterator;
import java.util.List;

public class InputMethodPreference extends CheckBoxPreference
{
  private static final String TAG = InputMethodPreference.class.getSimpleName();
  private final Collator mCollator;
  private AlertDialog mDialog = null;
  private final SettingsPreferenceFragment mFragment;
  private final InputMethodInfo mImi;
  private final InputMethodManager mImm;
  private View mInputMethodPref;
  private ImageView mInputMethodSettingsButton;
  private final boolean mIsSystemIme;
  private final boolean mIsValidSystemNonAuxAsciiCapableIme;
  private Preference.OnPreferenceChangeListener mOnImePreferenceChangeListener;
  private final View.OnClickListener mPrefOnclickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (!InputMethodPreference.this.isEnabled())
        return;
      if (InputMethodPreference.this.isChecked())
      {
        InputMethodPreference.this.setChecked(false, true);
        return;
      }
      if (InputMethodPreference.this.mIsSystemIme)
      {
        InputMethodPreference.this.setChecked(true, true);
        return;
      }
      InputMethodPreference.this.showSecurityWarnDialog(InputMethodPreference.this.mImi, InputMethodPreference.this);
    }
  };
  private final Intent mSettingsIntent;
  private TextView mSummaryText;
  private TextView mTitleText;

  public InputMethodPreference(SettingsPreferenceFragment paramSettingsPreferenceFragment, Intent paramIntent, InputMethodManager paramInputMethodManager, InputMethodInfo paramInputMethodInfo)
  {
    super(paramSettingsPreferenceFragment.getActivity(), null, 2131689503);
    setLayoutResource(2130968677);
    setWidgetLayoutResource(2130968678);
    this.mFragment = paramSettingsPreferenceFragment;
    this.mSettingsIntent = paramIntent;
    this.mImm = paramInputMethodManager;
    this.mImi = paramInputMethodInfo;
    this.mIsSystemIme = InputMethodUtils.isSystemIme(paramInputMethodInfo);
    this.mCollator = Collator.getInstance(paramSettingsPreferenceFragment.getResources().getConfiguration().locale);
    Activity localActivity = paramSettingsPreferenceFragment.getActivity();
    this.mIsValidSystemNonAuxAsciiCapableIme = InputMethodSettingValuesWrapper.getInstance(localActivity).isValidSystemNonAuxAsciiCapableIme(paramInputMethodInfo, localActivity);
    updatePreferenceViews();
  }

  private String getSummaryString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = this.mImm.getEnabledInputMethodSubtypeList(this.mImi, true).iterator();
    while (localIterator.hasNext())
    {
      InputMethodSubtype localInputMethodSubtype = (InputMethodSubtype)localIterator.next();
      if (localStringBuilder.length() > 0)
        localStringBuilder.append(", ");
      localStringBuilder.append(localInputMethodSubtype.getDisplayName(this.mFragment.getActivity(), this.mImi.getPackageName(), this.mImi.getServiceInfo().applicationInfo));
    }
    return localStringBuilder.toString();
  }

  private void saveImeSettings()
  {
    SettingsPreferenceFragment localSettingsPreferenceFragment = this.mFragment;
    ContentResolver localContentResolver = this.mFragment.getActivity().getContentResolver();
    List localList = this.mImm.getInputMethodList();
    if (this.mFragment.getResources().getConfiguration().keyboard == 2);
    for (boolean bool = true; ; bool = false)
    {
      InputMethodAndSubtypeUtil.saveInputMethodSubtypeList(localSettingsPreferenceFragment, localContentResolver, localList, bool);
      return;
    }
  }

  private void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = isChecked();
    super.setChecked(paramBoolean1);
    if (paramBoolean2)
    {
      saveImeSettings();
      if ((bool != paramBoolean1) && (this.mOnImePreferenceChangeListener != null))
        this.mOnImePreferenceChangeListener.onPreferenceChange(this, Boolean.valueOf(paramBoolean1));
    }
  }

  private void showSecurityWarnDialog(InputMethodInfo paramInputMethodInfo, final InputMethodPreference paramInputMethodPreference)
  {
    if ((this.mDialog != null) && (this.mDialog.isShowing()))
      this.mDialog.dismiss();
    this.mDialog = new AlertDialog.Builder(this.mFragment.getActivity()).setTitle(17039380).setIconAttribute(16843605).setCancelable(true).setPositiveButton(17039370, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramInputMethodPreference.setChecked(true, true);
      }
    }).setNegativeButton(17039360, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
      }
    }).create();
    AlertDialog localAlertDialog = this.mDialog;
    Resources localResources = this.mFragment.getResources();
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = paramInputMethodInfo.getServiceInfo().applicationInfo.loadLabel(this.mFragment.getActivity().getPackageManager());
    localAlertDialog.setMessage(localResources.getString(2131428513, arrayOfObject));
    this.mDialog.show();
  }

  public static boolean startFragment(Fragment paramFragment, String paramString, int paramInt, Bundle paramBundle)
  {
    if ((paramFragment.getActivity() instanceof PreferenceActivity))
    {
      ((PreferenceActivity)paramFragment.getActivity()).startPreferencePanel(paramString, paramBundle, 0, null, paramFragment, paramInt);
      return true;
    }
    Log.w(TAG, "Parent isn't PreferenceActivity, thus there's no way to launch the given Fragment (name: " + paramString + ", requestCode: " + paramInt + ")");
    return false;
  }

  private void updateSummary()
  {
    String str = getSummaryString();
    if (TextUtils.isEmpty(str))
      return;
    setSummary(str);
  }

  public int compareTo(Preference paramPreference)
  {
    int i = 1;
    if (!(paramPreference instanceof InputMethodPreference))
      i = super.compareTo(paramPreference);
    int j;
    label39: label99: 
    do
    {
      return i;
      InputMethodPreference localInputMethodPreference = (InputMethodPreference)paramPreference;
      int k;
      if ((this.mIsSystemIme) && (this.mIsValidSystemNonAuxAsciiCapableIme))
      {
        j = i;
        if ((!localInputMethodPreference.mIsSystemIme) || (!localInputMethodPreference.mIsValidSystemNonAuxAsciiCapableIme))
          break label99;
        k = i;
      }
      while (true)
        if (j == k)
        {
          CharSequence localCharSequence1 = getTitle();
          CharSequence localCharSequence2 = localInputMethodPreference.getTitle();
          if (TextUtils.isEmpty(localCharSequence1))
            break;
          if (TextUtils.isEmpty(localCharSequence2))
          {
            return -1;
            j = 0;
            break label39;
            k = 0;
            continue;
          }
          return this.mCollator.compare(localCharSequence1.toString(), localCharSequence2.toString());
        }
    }
    while (j == 0);
    return -1;
  }

  protected void onBindView(View paramView)
  {
    int i = 1;
    super.onBindView(paramView);
    this.mInputMethodPref = paramView.findViewById(2131230960);
    this.mInputMethodPref.setOnClickListener(this.mPrefOnclickListener);
    this.mInputMethodSettingsButton = ((ImageView)paramView.findViewById(2131230961));
    this.mTitleText = ((TextView)paramView.findViewById(16908310));
    this.mSummaryText = ((TextView)paramView.findViewById(16908304));
    if (this.mImi.getSubtypeCount() > i);
    while (true)
    {
      final String str = this.mImi.getId();
      if (i != 0)
        this.mInputMethodPref.setOnLongClickListener(new View.OnLongClickListener()
        {
          public boolean onLongClick(View paramAnonymousView)
          {
            Bundle localBundle = new Bundle();
            localBundle.putString("input_method_id", str);
            InputMethodPreference.startFragment(InputMethodPreference.this.mFragment, InputMethodAndSubtypeEnabler.class.getName(), 0, localBundle);
            return true;
          }
        });
      if (this.mSettingsIntent != null)
        this.mInputMethodSettingsButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            try
            {
              InputMethodPreference.this.mFragment.startActivity(InputMethodPreference.this.mSettingsIntent);
              return;
            }
            catch (ActivityNotFoundException localActivityNotFoundException)
            {
              Log.d(InputMethodPreference.TAG, "IME's Settings Activity Not Found: " + localActivityNotFoundException);
              SettingsPreferenceFragment localSettingsPreferenceFragment = InputMethodPreference.this.mFragment;
              Object[] arrayOfObject = new Object[1];
              arrayOfObject[0] = InputMethodPreference.this.mImi.loadLabel(InputMethodPreference.this.mFragment.getActivity().getPackageManager());
              String str = localSettingsPreferenceFragment.getString(2131428517, arrayOfObject);
              Toast.makeText(InputMethodPreference.this.mFragment.getActivity(), str, 1).show();
            }
          }
        });
      if (i != 0)
      {
        View.OnLongClickListener local4 = new View.OnLongClickListener()
        {
          public boolean onLongClick(View paramAnonymousView)
          {
            Bundle localBundle = new Bundle();
            localBundle.putString("input_method_id", str);
            InputMethodPreference.startFragment(InputMethodPreference.this.mFragment, InputMethodAndSubtypeEnabler.class.getName(), 0, localBundle);
            return true;
          }
        };
        this.mInputMethodSettingsButton.setOnLongClickListener(local4);
      }
      if (this.mSettingsIntent == null)
        this.mInputMethodSettingsButton.setVisibility(8);
      updatePreferenceViews();
      return;
      i = 0;
    }
  }

  public void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
    updatePreferenceViews();
  }

  public void setOnImePreferenceChangeListener(Preference.OnPreferenceChangeListener paramOnPreferenceChangeListener)
  {
    this.mOnImePreferenceChangeListener = paramOnPreferenceChangeListener;
  }

  public void updatePreferenceViews()
  {
    boolean bool2;
    View localView;
    if (InputMethodSettingValuesWrapper.getInstance(getContext()).isAlwaysCheckedIme(this.mImi, getContext()))
    {
      super.setChecked(true);
      super.setEnabled(false);
      boolean bool1 = isChecked();
      if (this.mInputMethodSettingsButton != null)
      {
        this.mInputMethodSettingsButton.setEnabled(bool1);
        this.mInputMethodSettingsButton.setClickable(bool1);
        this.mInputMethodSettingsButton.setFocusable(bool1);
        if (!bool1)
          this.mInputMethodSettingsButton.setAlpha(0.4F);
      }
      if (this.mTitleText != null)
        this.mTitleText.setEnabled(true);
      if (this.mSummaryText != null)
        this.mSummaryText.setEnabled(bool1);
      if (this.mInputMethodPref != null)
      {
        this.mInputMethodPref.setEnabled(true);
        this.mInputMethodPref.setLongClickable(bool1);
        bool2 = isEnabled();
        localView = this.mInputMethodPref;
        if (!bool2)
          break label185;
      }
    }
    label185: for (View.OnClickListener localOnClickListener = this.mPrefOnclickListener; ; localOnClickListener = null)
    {
      localView.setOnClickListener(localOnClickListener);
      if (!bool2)
        this.mInputMethodPref.setBackgroundColor(0);
      updateSummary();
      return;
      super.setEnabled(true);
      break;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.inputmethod.InputMethodPreference
 * JD-Core Version:    0.6.2
 */