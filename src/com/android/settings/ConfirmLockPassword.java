package com.android.settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.text.Editable;
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

public class ConfirmLockPassword extends PreferenceActivity
{
  public Intent getIntent()
  {
    Intent localIntent = new Intent(super.getIntent());
    localIntent.putExtra(":android:show_fragment", ConfirmLockPasswordFragment.class.getName());
    localIntent.putExtra(":android:no_headers", true);
    return localIntent;
  }

  protected boolean isValidFragment(String paramString)
  {
    return ConfirmLockPasswordFragment.class.getName().equals(paramString);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    CharSequence localCharSequence = getText(2131428314);
    showBreadCrumbs(localCharSequence, localCharSequence);
  }

  public static class ConfirmLockPasswordFragment extends Fragment
    implements TextWatcher, View.OnClickListener, TextView.OnEditorActionListener
  {
    private Button mContinueButton;
    private Handler mHandler = new Handler();
    private TextView mHeaderText;
    private PasswordEntryKeyboardHelper mKeyboardHelper;
    private PasswordEntryKeyboardView mKeyboardView;
    private LockPatternUtils mLockPatternUtils;
    private TextView mPasswordEntry;

    private void handleNext()
    {
      String str = this.mPasswordEntry.getText().toString();
      if (this.mLockPatternUtils.checkPassword(str))
      {
        Intent localIntent = new Intent();
        localIntent.putExtra("password", str);
        getActivity().setResult(-1, localIntent);
        getActivity().finish();
        return;
      }
      showError(2131428328);
    }

    private void showError(int paramInt)
    {
      this.mHeaderText.setText(paramInt);
      this.mHeaderText.announceForAccessibility(this.mHeaderText.getText());
      this.mPasswordEntry.setText(null);
      this.mHandler.postDelayed(new Runnable()
      {
        public void run()
        {
          ConfirmLockPassword.ConfirmLockPasswordFragment.this.mHeaderText.setText(2131428314);
        }
      }
      , 3000L);
    }

    public void afterTextChanged(Editable paramEditable)
    {
      Button localButton = this.mContinueButton;
      if (this.mPasswordEntry.getText().length() > 0);
      for (boolean bool = true; ; bool = false)
      {
        localButton.setEnabled(bool);
        return;
      }
    }

    public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
    {
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
      getActivity().setResult(0);
      getActivity().finish();
    }

    public void onCreate(Bundle paramBundle)
    {
      super.onCreate(paramBundle);
      this.mLockPatternUtils = new LockPatternUtils(getActivity());
    }

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      int i = this.mLockPatternUtils.getKeyguardStoredPasswordQuality();
      View localView = paramLayoutInflater.inflate(2130968600, null);
      localView.findViewById(2131230762).setOnClickListener(this);
      this.mContinueButton = ((Button)localView.findViewById(2131230763));
      this.mContinueButton.setOnClickListener(this);
      this.mContinueButton.setEnabled(false);
      this.mPasswordEntry = ((TextView)localView.findViewById(2131230761));
      this.mPasswordEntry.setOnEditorActionListener(this);
      this.mPasswordEntry.addTextChangedListener(this);
      this.mKeyboardView = ((PasswordEntryKeyboardView)localView.findViewById(2131230764));
      this.mHeaderText = ((TextView)localView.findViewById(2131230759));
      int j;
      int k;
      label156: int m;
      label204: int n;
      label239: PreferenceActivity localPreferenceActivity;
      if ((262144 == i) || (327680 == i) || (393216 == i))
      {
        j = 1;
        TextView localTextView1 = this.mHeaderText;
        if (j == 0)
          break label296;
        k = 2131428314;
        localTextView1.setText(k);
        Activity localActivity = getActivity();
        this.mKeyboardHelper = new PasswordEntryKeyboardHelper(localActivity, this.mKeyboardView, this.mPasswordEntry);
        PasswordEntryKeyboardHelper localPasswordEntryKeyboardHelper = this.mKeyboardHelper;
        if (j == 0)
          break label303;
        m = 0;
        localPasswordEntryKeyboardHelper.setKeyboardMode(m);
        this.mKeyboardView.requestFocus();
        n = this.mPasswordEntry.getInputType();
        TextView localTextView2 = this.mPasswordEntry;
        if (j == 0)
          break label309;
        localTextView2.setInputType(n);
        if ((localActivity instanceof PreferenceActivity))
        {
          localPreferenceActivity = (PreferenceActivity)localActivity;
          if (j == 0)
            break label316;
        }
      }
      label296: label303: label309: label316: for (int i1 = 2131428314; ; i1 = 2131428316)
      {
        CharSequence localCharSequence = getText(i1);
        localPreferenceActivity.showBreadCrumbs(localCharSequence, localCharSequence);
        return localView;
        j = 0;
        break;
        k = 2131428316;
        break label156;
        m = 1;
        break label204;
        n = 18;
        break label239;
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
      super.onPause();
      this.mKeyboardView.requestFocus();
    }

    public void onResume()
    {
      super.onResume();
      this.mKeyboardView.requestFocus();
    }

    public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
    {
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.ConfirmLockPassword
 * JD-Core Version:    0.6.2
 */