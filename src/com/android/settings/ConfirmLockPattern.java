package com.android.settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.internal.widget.LinearLayoutWithDefaultTouchRecepient;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockPatternView;
import com.android.internal.widget.LockPatternView.Cell;
import com.android.internal.widget.LockPatternView.DisplayMode;
import com.android.internal.widget.LockPatternView.OnPatternListener;
import java.util.List;

public class ConfirmLockPattern extends PreferenceActivity
{
  public Intent getIntent()
  {
    Intent localIntent = new Intent(super.getIntent());
    localIntent.putExtra(":android:show_fragment", ConfirmLockPatternFragment.class.getName());
    localIntent.putExtra(":android:no_headers", true);
    return localIntent;
  }

  protected boolean isValidFragment(String paramString)
  {
    return ConfirmLockPatternFragment.class.getName().equals(paramString);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    CharSequence localCharSequence = getText(2131428315);
    showBreadCrumbs(localCharSequence, localCharSequence);
  }

  public static class ConfirmLockPatternFragment extends Fragment
  {
    private Runnable mClearPatternRunnable = new Runnable()
    {
      public void run()
      {
        ConfirmLockPattern.ConfirmLockPatternFragment.this.mLockPatternView.clearPattern();
      }
    };
    private LockPatternView.OnPatternListener mConfirmExistingLockPatternListener = new LockPatternView.OnPatternListener()
    {
      public void onPatternCellAdded(List<LockPatternView.Cell> paramAnonymousList)
      {
      }

      public void onPatternCleared()
      {
        ConfirmLockPattern.ConfirmLockPatternFragment.this.mLockPatternView.removeCallbacks(ConfirmLockPattern.ConfirmLockPatternFragment.this.mClearPatternRunnable);
      }

      public void onPatternDetected(List<LockPatternView.Cell> paramAnonymousList)
      {
        if (ConfirmLockPattern.ConfirmLockPatternFragment.this.mLockPatternUtils.checkPattern(paramAnonymousList))
        {
          Intent localIntent = new Intent();
          localIntent.putExtra("password", LockPatternUtils.patternToString(paramAnonymousList));
          ConfirmLockPattern.ConfirmLockPatternFragment.this.getActivity().setResult(-1, localIntent);
          ConfirmLockPattern.ConfirmLockPatternFragment.this.getActivity().finish();
          return;
        }
        if ((paramAnonymousList.size() >= 4) && (ConfirmLockPattern.ConfirmLockPatternFragment.access$304(ConfirmLockPattern.ConfirmLockPatternFragment.this) >= 5))
        {
          long l = ConfirmLockPattern.ConfirmLockPatternFragment.this.mLockPatternUtils.setLockoutAttemptDeadline();
          ConfirmLockPattern.ConfirmLockPatternFragment.this.handleAttemptLockout(l);
          return;
        }
        ConfirmLockPattern.ConfirmLockPatternFragment.this.updateStage(ConfirmLockPattern.Stage.NeedToUnlockWrong);
        ConfirmLockPattern.ConfirmLockPatternFragment.this.postClearPatternRunnable();
      }

      public void onPatternStart()
      {
        ConfirmLockPattern.ConfirmLockPatternFragment.this.mLockPatternView.removeCallbacks(ConfirmLockPattern.ConfirmLockPatternFragment.this.mClearPatternRunnable);
      }
    };
    private CountDownTimer mCountdownTimer;
    private CharSequence mFooterText;
    private TextView mFooterTextView;
    private CharSequence mFooterWrongText;
    private CharSequence mHeaderText;
    private TextView mHeaderTextView;
    private CharSequence mHeaderWrongText;
    private LockPatternUtils mLockPatternUtils;
    private LockPatternView mLockPatternView;
    private int mNumWrongConfirmAttempts;

    private void handleAttemptLockout(long paramLong)
    {
      updateStage(ConfirmLockPattern.Stage.LockedOut);
      this.mCountdownTimer = new CountDownTimer(paramLong - SystemClock.elapsedRealtime(), 1000L)
      {
        public void onFinish()
        {
          ConfirmLockPattern.ConfirmLockPatternFragment.access$302(ConfirmLockPattern.ConfirmLockPatternFragment.this, 0);
          ConfirmLockPattern.ConfirmLockPatternFragment.this.updateStage(ConfirmLockPattern.Stage.NeedToUnlock);
        }

        public void onTick(long paramAnonymousLong)
        {
          ConfirmLockPattern.ConfirmLockPatternFragment.this.mHeaderTextView.setText(2131428350);
          int i = (int)(paramAnonymousLong / 1000L);
          TextView localTextView = ConfirmLockPattern.ConfirmLockPatternFragment.this.mFooterTextView;
          ConfirmLockPattern.ConfirmLockPatternFragment localConfirmLockPatternFragment = ConfirmLockPattern.ConfirmLockPatternFragment.this;
          Object[] arrayOfObject = new Object[1];
          arrayOfObject[0] = Integer.valueOf(i);
          localTextView.setText(localConfirmLockPatternFragment.getString(2131428351, arrayOfObject));
        }
      }
      .start();
    }

    private void postClearPatternRunnable()
    {
      this.mLockPatternView.removeCallbacks(this.mClearPatternRunnable);
      this.mLockPatternView.postDelayed(this.mClearPatternRunnable, 2000L);
    }

    private void updateStage(ConfirmLockPattern.Stage paramStage)
    {
      switch (ConfirmLockPattern.1.$SwitchMap$com$android$settings$ConfirmLockPattern$Stage[paramStage.ordinal()])
      {
      default:
      case 1:
      case 2:
      case 3:
      }
      while (true)
      {
        this.mHeaderTextView.announceForAccessibility(this.mHeaderTextView.getText());
        return;
        if (this.mHeaderText != null)
        {
          this.mHeaderTextView.setText(this.mHeaderText);
          label69: if (this.mFooterText == null)
            break label117;
          this.mFooterTextView.setText(this.mFooterText);
        }
        while (true)
        {
          this.mLockPatternView.setEnabled(true);
          this.mLockPatternView.enableInput();
          break;
          this.mHeaderTextView.setText(2131428326);
          break label69;
          label117: this.mFooterTextView.setText(2131428327);
        }
        if (this.mHeaderWrongText != null)
        {
          this.mHeaderTextView.setText(this.mHeaderWrongText);
          label147: if (this.mFooterWrongText == null)
            break label205;
          this.mFooterTextView.setText(this.mFooterWrongText);
        }
        while (true)
        {
          this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
          this.mLockPatternView.setEnabled(true);
          this.mLockPatternView.enableInput();
          break;
          this.mHeaderTextView.setText(2131428328);
          break label147;
          label205: this.mFooterTextView.setText(2131428329);
        }
        this.mLockPatternView.clearPattern();
        this.mLockPatternView.setEnabled(false);
      }
    }

    public void onCreate(Bundle paramBundle)
    {
      super.onCreate(paramBundle);
      this.mLockPatternUtils = new LockPatternUtils(getActivity());
    }

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      View localView = paramLayoutInflater.inflate(2130968601, null);
      this.mHeaderTextView = ((TextView)localView.findViewById(2131230759));
      this.mLockPatternView = ((LockPatternView)localView.findViewById(2131230766));
      this.mFooterTextView = ((TextView)localView.findViewById(2131230767));
      ((LinearLayoutWithDefaultTouchRecepient)localView.findViewById(2131230765)).setDefaultTouchRecepient(this.mLockPatternView);
      Intent localIntent = getActivity().getIntent();
      if (localIntent != null)
      {
        this.mHeaderText = localIntent.getCharSequenceExtra("com.android.settings.ConfirmLockPattern.header");
        this.mFooterText = localIntent.getCharSequenceExtra("com.android.settings.ConfirmLockPattern.footer");
        this.mHeaderWrongText = localIntent.getCharSequenceExtra("com.android.settings.ConfirmLockPattern.header_wrong");
        this.mFooterWrongText = localIntent.getCharSequenceExtra("com.android.settings.ConfirmLockPattern.footer_wrong");
      }
      this.mLockPatternView.setTactileFeedbackEnabled(this.mLockPatternUtils.isTactileFeedbackEnabled());
      this.mLockPatternView.setOnPatternListener(this.mConfirmExistingLockPatternListener);
      updateStage(ConfirmLockPattern.Stage.NeedToUnlock);
      if (paramBundle != null)
        this.mNumWrongConfirmAttempts = paramBundle.getInt("num_wrong_attempts");
      while (this.mLockPatternUtils.savedPatternExists())
        return localView;
      getActivity().setResult(-1);
      getActivity().finish();
      return localView;
    }

    public void onPause()
    {
      super.onPause();
      if (this.mCountdownTimer != null)
        this.mCountdownTimer.cancel();
    }

    public void onResume()
    {
      super.onResume();
      long l = this.mLockPatternUtils.getLockoutAttemptDeadline();
      if (l != 0L)
        handleAttemptLockout(l);
      while (this.mLockPatternView.isEnabled())
        return;
      this.mNumWrongConfirmAttempts = 0;
      updateStage(ConfirmLockPattern.Stage.NeedToUnlock);
    }

    public void onSaveInstanceState(Bundle paramBundle)
    {
      paramBundle.putInt("num_wrong_attempts", this.mNumWrongConfirmAttempts);
    }
  }

  private static enum Stage
  {
    static
    {
      LockedOut = new Stage("LockedOut", 2);
      Stage[] arrayOfStage = new Stage[3];
      arrayOfStage[0] = NeedToUnlock;
      arrayOfStage[1] = NeedToUnlockWrong;
      arrayOfStage[2] = LockedOut;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.ConfirmLockPattern
 * JD-Core Version:    0.6.2
 */