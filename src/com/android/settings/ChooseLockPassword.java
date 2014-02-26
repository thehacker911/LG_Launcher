package com.android.settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.PasswordEntryKeyboardHelper;
import com.android.internal.widget.PasswordEntryKeyboardView;

public class ChooseLockPassword extends PreferenceActivity
{
  public Intent getIntent()
  {
    Intent localIntent = new Intent(super.getIntent());
    localIntent.putExtra(":android:show_fragment", ChooseLockPasswordFragment.class.getName());
    localIntent.putExtra(":android:no_headers", true);
    return localIntent;
  }

  protected boolean isValidFragment(String paramString)
  {
    return ChooseLockPasswordFragment.class.getName().equals(paramString);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    CharSequence localCharSequence = getText(2131428311);
    showBreadCrumbs(localCharSequence, localCharSequence);
  }

  public static class ChooseLockPasswordFragment extends Fragment
    implements TextWatcher, View.OnClickListener, TextView.OnEditorActionListener
  {
    private Button mCancelButton;
    private ChooseLockSettingsHelper mChooseLockSettingsHelper;
    private String mFirstPin;
    private Handler mHandler = new Handler()
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        if (paramAnonymousMessage.what == 1)
          ChooseLockPassword.ChooseLockPasswordFragment.this.updateStage((ChooseLockPassword.ChooseLockPasswordFragment.Stage)paramAnonymousMessage.obj);
      }
    };
    private TextView mHeaderText;
    private boolean mIsAlphaMode;
    private PasswordEntryKeyboardHelper mKeyboardHelper;
    private KeyboardView mKeyboardView;
    private LockPatternUtils mLockPatternUtils;
    private Button mNextButton;
    private TextView mPasswordEntry;
    private int mPasswordMaxLength = 16;
    private int mPasswordMinLength = 4;
    private int mPasswordMinLetters = 0;
    private int mPasswordMinLowerCase = 0;
    private int mPasswordMinNonLetter = 0;
    private int mPasswordMinNumeric = 0;
    private int mPasswordMinSymbols = 0;
    private int mPasswordMinUpperCase = 0;
    private int mRequestedQuality = 131072;
    private Stage mUiStage = Stage.Introduction;

    private void handleNext()
    {
      String str1 = this.mPasswordEntry.getText().toString();
      if (TextUtils.isEmpty(str1));
      while (true)
      {
        return;
        String str2;
        if (this.mUiStage == Stage.Introduction)
        {
          str2 = validatePassword(str1);
          if (str2 == null)
          {
            this.mFirstPin = str1;
            this.mPasswordEntry.setText("");
            updateStage(Stage.NeedToConfirm);
          }
        }
        while (str2 != null)
        {
          showError(str2, this.mUiStage);
          return;
          Stage localStage1 = this.mUiStage;
          Stage localStage2 = Stage.NeedToConfirm;
          str2 = null;
          if (localStage1 == localStage2)
            if (this.mFirstPin.equals(str1))
            {
              boolean bool = getActivity().getIntent().getBooleanExtra("lockscreen.biometric_weak_fallback", false);
              this.mLockPatternUtils.clearLock(bool);
              this.mLockPatternUtils.saveLockPassword(str1, this.mRequestedQuality, bool);
              getActivity().setResult(1);
              getActivity().finish();
              str2 = null;
            }
            else
            {
              CharSequence localCharSequence = this.mPasswordEntry.getText();
              if (localCharSequence != null)
                Selection.setSelection((Spannable)localCharSequence, 0, localCharSequence.length());
              updateStage(Stage.ConfirmWrong);
              str2 = null;
            }
        }
      }
    }

    private void showError(String paramString, Stage paramStage)
    {
      this.mHeaderText.setText(paramString);
      this.mHeaderText.announceForAccessibility(this.mHeaderText.getText());
      Message localMessage = this.mHandler.obtainMessage(1, paramStage);
      this.mHandler.removeMessages(1);
      this.mHandler.sendMessageDelayed(localMessage, 3000L);
    }

    private void updateUi()
    {
      String str1 = this.mPasswordEntry.getText().toString();
      int i = str1.length();
      if ((this.mUiStage == Stage.Introduction) && (i > 0))
      {
        int k;
        if (i < this.mPasswordMinLength)
          if (this.mIsAlphaMode)
          {
            k = 2131427675;
            Object[] arrayOfObject = new Object[1];
            arrayOfObject[0] = Integer.valueOf(this.mPasswordMinLength);
            String str3 = getString(k, arrayOfObject);
            this.mHeaderText.setText(str3);
            this.mNextButton.setEnabled(false);
          }
        while (true)
        {
          this.mNextButton.setText(this.mUiStage.buttonText);
          return;
          k = 2131427676;
          break;
          String str2 = validatePassword(str1);
          if (str2 != null)
          {
            this.mHeaderText.setText(str2);
            this.mNextButton.setEnabled(false);
          }
          else
          {
            this.mHeaderText.setText(2131427677);
            this.mNextButton.setEnabled(true);
          }
        }
      }
      TextView localTextView = this.mHeaderText;
      int j;
      label188: Button localButton;
      if (this.mIsAlphaMode)
      {
        j = this.mUiStage.alphaHint;
        localTextView.setText(j);
        localButton = this.mNextButton;
        if (i <= 0)
          break label229;
      }
      label229: for (boolean bool = true; ; bool = false)
      {
        localButton.setEnabled(bool);
        break;
        j = this.mUiStage.numericHint;
        break label188;
      }
    }

    private String validatePassword(String paramString)
    {
      if (paramString.length() < this.mPasswordMinLength)
      {
        if (this.mIsAlphaMode);
        for (int i8 = 2131427675; ; i8 = 2131427676)
        {
          Object[] arrayOfObject8 = new Object[1];
          arrayOfObject8[0] = Integer.valueOf(this.mPasswordMinLength);
          return getString(i8, arrayOfObject8);
        }
      }
      if (paramString.length() > this.mPasswordMaxLength)
      {
        if (this.mIsAlphaMode);
        for (int i7 = 2131427679; ; i7 = 2131427680)
        {
          Object[] arrayOfObject7 = new Object[1];
          arrayOfObject7[0] = Integer.valueOf(1 + this.mPasswordMaxLength);
          return getString(i7, arrayOfObject7);
        }
      }
      int i = 0;
      int j = 0;
      int k = 0;
      int m = 0;
      int n = 0;
      int i1 = 0;
      int i2 = 0;
      if (i2 < paramString.length())
      {
        int i6 = paramString.charAt(i2);
        if ((i6 < 32) || (i6 > 127))
          return getString(2131427683);
        if ((i6 >= 48) && (i6 <= 57))
        {
          j++;
          i1++;
        }
        while (true)
        {
          i2++;
          break;
          if ((i6 >= 65) && (i6 <= 90))
          {
            i++;
            n++;
          }
          else if ((i6 >= 97) && (i6 <= 122))
          {
            i++;
            k++;
          }
          else
          {
            m++;
            i1++;
          }
        }
      }
      if ((131072 == this.mRequestedQuality) && ((i > 0) || (m > 0)))
        return getString(2131427681);
      if (393216 == this.mRequestedQuality)
      {
        if (i < this.mPasswordMinLetters)
        {
          String str6 = getResources().getQuantityString(2131623937, this.mPasswordMinLetters);
          Object[] arrayOfObject6 = new Object[1];
          arrayOfObject6[0] = Integer.valueOf(this.mPasswordMinLetters);
          return String.format(str6, arrayOfObject6);
        }
        if (j < this.mPasswordMinNumeric)
        {
          String str5 = getResources().getQuantityString(2131623940, this.mPasswordMinNumeric);
          Object[] arrayOfObject5 = new Object[1];
          arrayOfObject5[0] = Integer.valueOf(this.mPasswordMinNumeric);
          return String.format(str5, arrayOfObject5);
        }
        if (k < this.mPasswordMinLowerCase)
        {
          String str4 = getResources().getQuantityString(2131623938, this.mPasswordMinLowerCase);
          Object[] arrayOfObject4 = new Object[1];
          arrayOfObject4[0] = Integer.valueOf(this.mPasswordMinLowerCase);
          return String.format(str4, arrayOfObject4);
        }
        if (n < this.mPasswordMinUpperCase)
        {
          String str3 = getResources().getQuantityString(2131623939, this.mPasswordMinUpperCase);
          Object[] arrayOfObject3 = new Object[1];
          arrayOfObject3[0] = Integer.valueOf(this.mPasswordMinUpperCase);
          return String.format(str3, arrayOfObject3);
        }
        if (m < this.mPasswordMinSymbols)
        {
          String str2 = getResources().getQuantityString(2131623941, this.mPasswordMinSymbols);
          Object[] arrayOfObject2 = new Object[1];
          arrayOfObject2[0] = Integer.valueOf(this.mPasswordMinSymbols);
          return String.format(str2, arrayOfObject2);
        }
        if (i1 < this.mPasswordMinNonLetter)
        {
          String str1 = getResources().getQuantityString(2131623942, this.mPasswordMinNonLetter);
          Object[] arrayOfObject1 = new Object[1];
          arrayOfObject1[0] = Integer.valueOf(this.mPasswordMinNonLetter);
          return String.format(str1, arrayOfObject1);
        }
      }
      else
      {
        int i3;
        if (262144 == this.mRequestedQuality)
        {
          i3 = 1;
          if (327680 != this.mRequestedQuality)
            break label636;
        }
        label636: for (int i4 = 1; ; i4 = 0)
        {
          if (((i3 == 0) && (i4 == 0)) || (i != 0))
            break label642;
          return getString(2131427684);
          i3 = 0;
          break;
        }
        label642: if ((i4 != 0) && (j == 0))
          return getString(2131427685);
      }
      if (this.mLockPatternUtils.checkPasswordHistory(paramString))
      {
        if (this.mIsAlphaMode);
        for (int i5 = 2131427687; ; i5 = 2131427682)
          return getString(i5);
      }
      return null;
    }

    public void afterTextChanged(Editable paramEditable)
    {
      if (this.mUiStage == Stage.ConfirmWrong)
        this.mUiStage = Stage.NeedToConfirm;
      updateUi();
    }

    public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
    {
    }

    public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
    {
      super.onActivityResult(paramInt1, paramInt2, paramIntent);
      switch (paramInt1)
      {
      default:
      case 58:
      }
      do
        return;
      while (paramInt2 == -1);
      getActivity().setResult(1);
      getActivity().finish();
    }

    public void onClick(View paramView)
    {
      switch (paramView.getId())
      {
      default:
        return;
      case 2131230763:
        handleNext();
        return;
      case 2131230762:
      }
      getActivity().finish();
    }

    public void onCreate(Bundle paramBundle)
    {
      super.onCreate(paramBundle);
      this.mLockPatternUtils = new LockPatternUtils(getActivity());
      Intent localIntent = getActivity().getIntent();
      if (!(getActivity() instanceof ChooseLockPassword))
        throw new SecurityException("Fragment contained in wrong activity");
      this.mRequestedQuality = Math.max(localIntent.getIntExtra("lockscreen.password_type", this.mRequestedQuality), this.mLockPatternUtils.getRequestedPasswordQuality());
      this.mPasswordMinLength = Math.max(localIntent.getIntExtra("lockscreen.password_min", this.mPasswordMinLength), this.mLockPatternUtils.getRequestedMinimumPasswordLength());
      this.mPasswordMaxLength = localIntent.getIntExtra("lockscreen.password_max", this.mPasswordMaxLength);
      this.mPasswordMinLetters = Math.max(localIntent.getIntExtra("lockscreen.password_min_letters", this.mPasswordMinLetters), this.mLockPatternUtils.getRequestedPasswordMinimumLetters());
      this.mPasswordMinUpperCase = Math.max(localIntent.getIntExtra("lockscreen.password_min_uppercase", this.mPasswordMinUpperCase), this.mLockPatternUtils.getRequestedPasswordMinimumUpperCase());
      this.mPasswordMinLowerCase = Math.max(localIntent.getIntExtra("lockscreen.password_min_lowercase", this.mPasswordMinLowerCase), this.mLockPatternUtils.getRequestedPasswordMinimumLowerCase());
      this.mPasswordMinNumeric = Math.max(localIntent.getIntExtra("lockscreen.password_min_numeric", this.mPasswordMinNumeric), this.mLockPatternUtils.getRequestedPasswordMinimumNumeric());
      this.mPasswordMinSymbols = Math.max(localIntent.getIntExtra("lockscreen.password_min_symbols", this.mPasswordMinSymbols), this.mLockPatternUtils.getRequestedPasswordMinimumSymbols());
      this.mPasswordMinNonLetter = Math.max(localIntent.getIntExtra("lockscreen.password_min_nonletter", this.mPasswordMinNonLetter), this.mLockPatternUtils.getRequestedPasswordMinimumNonLetter());
      this.mChooseLockSettingsHelper = new ChooseLockSettingsHelper(getActivity());
    }

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      View localView = paramLayoutInflater.inflate(2130968597, null);
      this.mCancelButton = ((Button)localView.findViewById(2131230762));
      this.mCancelButton.setOnClickListener(this);
      this.mNextButton = ((Button)localView.findViewById(2131230763));
      this.mNextButton.setOnClickListener(this);
      boolean bool1;
      int i;
      label183: int j;
      label235: PreferenceActivity localPreferenceActivity;
      if ((262144 == this.mRequestedQuality) || (327680 == this.mRequestedQuality) || (393216 == this.mRequestedQuality))
      {
        bool1 = true;
        this.mIsAlphaMode = bool1;
        this.mKeyboardView = ((PasswordEntryKeyboardView)localView.findViewById(2131230764));
        this.mPasswordEntry = ((TextView)localView.findViewById(2131230761));
        this.mPasswordEntry.setOnEditorActionListener(this);
        this.mPasswordEntry.addTextChangedListener(this);
        Activity localActivity = getActivity();
        this.mKeyboardHelper = new PasswordEntryKeyboardHelper(localActivity, this.mKeyboardView, this.mPasswordEntry);
        PasswordEntryKeyboardHelper localPasswordEntryKeyboardHelper = this.mKeyboardHelper;
        if (!this.mIsAlphaMode)
          break label339;
        i = 0;
        localPasswordEntryKeyboardHelper.setKeyboardMode(i);
        this.mHeaderText = ((TextView)localView.findViewById(2131230759));
        this.mKeyboardView.requestFocus();
        j = this.mPasswordEntry.getInputType();
        TextView localTextView = this.mPasswordEntry;
        if (!this.mIsAlphaMode)
          break label345;
        localTextView.setInputType(j);
        boolean bool2 = getActivity().getIntent().getBooleanExtra("confirm_credentials", true);
        if (paramBundle != null)
          break label352;
        updateStage(Stage.Introduction);
        if (bool2)
          this.mChooseLockSettingsHelper.launchConfirmationActivity(58, null, null);
        label286: if ((localActivity instanceof PreferenceActivity))
        {
          localPreferenceActivity = (PreferenceActivity)localActivity;
          if (!this.mIsAlphaMode)
            break label397;
        }
      }
      label397: for (int k = 2131428311; ; k = 2131428313)
      {
        CharSequence localCharSequence = getText(k);
        localPreferenceActivity.showBreadCrumbs(localCharSequence, localCharSequence);
        return localView;
        bool1 = false;
        break;
        label339: i = 1;
        break label183;
        label345: j = 18;
        break label235;
        label352: this.mFirstPin = paramBundle.getString("first_pin");
        String str = paramBundle.getString("ui_stage");
        if (str == null)
          break label286;
        this.mUiStage = Stage.valueOf(str);
        updateStage(this.mUiStage);
        break label286;
      }
    }

    public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
    {
      if ((paramInt == 0) || (paramInt == 6) || (paramInt == 5))
      {
        handleNext();
        return true;
      }
      return false;
    }

    public void onPause()
    {
      this.mHandler.removeMessages(1);
      super.onPause();
    }

    public void onResume()
    {
      super.onResume();
      updateStage(this.mUiStage);
      this.mKeyboardView.requestFocus();
    }

    public void onSaveInstanceState(Bundle paramBundle)
    {
      super.onSaveInstanceState(paramBundle);
      paramBundle.putString("ui_stage", this.mUiStage.name());
      paramBundle.putString("first_pin", this.mFirstPin);
    }

    public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
    {
    }

    protected void updateStage(Stage paramStage)
    {
      Stage localStage = this.mUiStage;
      this.mUiStage = paramStage;
      updateUi();
      if (localStage != paramStage)
        this.mHeaderText.announceForAccessibility(this.mHeaderText.getText());
    }

    protected static enum Stage
    {
      public final int alphaHint;
      public final int buttonText;
      public final int numericHint;

      static
      {
        ConfirmWrong = new Stage("ConfirmWrong", 2, 2131428317, 2131428318, 2131427678);
        Stage[] arrayOfStage = new Stage[3];
        arrayOfStage[0] = Introduction;
        arrayOfStage[1] = NeedToConfirm;
        arrayOfStage[2] = ConfirmWrong;
      }

      private Stage(int paramInt1, int paramInt2, int paramInt3)
      {
        this.alphaHint = paramInt1;
        this.numericHint = paramInt2;
        this.buttonText = paramInt3;
      }
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.ChooseLockPassword
 * JD-Core Version:    0.6.2
 */