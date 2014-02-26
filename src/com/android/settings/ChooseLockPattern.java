package com.android.settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.internal.widget.LinearLayoutWithDefaultTouchRecepient;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockPatternView;
import com.android.internal.widget.LockPatternView.Cell;
import com.android.internal.widget.LockPatternView.DisplayMode;
import com.android.internal.widget.LockPatternView.OnPatternListener;
import com.google.android.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChooseLockPattern extends PreferenceActivity
{
  public Intent getIntent()
  {
    Intent localIntent = new Intent(super.getIntent());
    localIntent.putExtra(":android:show_fragment", ChooseLockPatternFragment.class.getName());
    localIntent.putExtra(":android:no_headers", true);
    return localIntent;
  }

  protected boolean isValidFragment(String paramString)
  {
    return ChooseLockPatternFragment.class.getName().equals(paramString);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    CharSequence localCharSequence = getText(2131428312);
    showBreadCrumbs(localCharSequence, localCharSequence);
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    return super.onKeyDown(paramInt, paramKeyEvent);
  }

  public static class ChooseLockPatternFragment extends Fragment
    implements View.OnClickListener
  {
    private final List<LockPatternView.Cell> mAnimatePattern;
    private ChooseLockSettingsHelper mChooseLockSettingsHelper;
    protected LockPatternView.OnPatternListener mChooseNewLockPatternListener;
    protected List<LockPatternView.Cell> mChosenPattern = null;
    private Runnable mClearPatternRunnable;
    private TextView mFooterLeftButton;
    private TextView mFooterRightButton;
    protected TextView mFooterText;
    protected TextView mHeaderText;
    protected LockPatternView mLockPatternView;
    private Stage mUiStage;

    public ChooseLockPatternFragment()
    {
      LockPatternView.Cell[] arrayOfCell = new LockPatternView.Cell[4];
      arrayOfCell[0] = LockPatternView.Cell.of(0, 0);
      arrayOfCell[1] = LockPatternView.Cell.of(0, 1);
      arrayOfCell[2] = LockPatternView.Cell.of(1, 1);
      arrayOfCell[3] = LockPatternView.Cell.of(2, 1);
      this.mAnimatePattern = Collections.unmodifiableList(Lists.newArrayList(arrayOfCell));
      this.mChooseNewLockPatternListener = new LockPatternView.OnPatternListener()
      {
        private void patternInProgress()
        {
          ChooseLockPattern.ChooseLockPatternFragment.this.mHeaderText.setText(2131428332);
          ChooseLockPattern.ChooseLockPatternFragment.this.mFooterText.setText("");
          ChooseLockPattern.ChooseLockPatternFragment.this.mFooterLeftButton.setEnabled(false);
          ChooseLockPattern.ChooseLockPatternFragment.this.mFooterRightButton.setEnabled(false);
        }

        public void onPatternCellAdded(List<LockPatternView.Cell> paramAnonymousList)
        {
        }

        public void onPatternCleared()
        {
          ChooseLockPattern.ChooseLockPatternFragment.this.mLockPatternView.removeCallbacks(ChooseLockPattern.ChooseLockPatternFragment.this.mClearPatternRunnable);
        }

        public void onPatternDetected(List<LockPatternView.Cell> paramAnonymousList)
        {
          if ((ChooseLockPattern.ChooseLockPatternFragment.this.mUiStage == ChooseLockPattern.ChooseLockPatternFragment.Stage.NeedToConfirm) || (ChooseLockPattern.ChooseLockPatternFragment.this.mUiStage == ChooseLockPattern.ChooseLockPatternFragment.Stage.ConfirmWrong))
          {
            if (ChooseLockPattern.ChooseLockPatternFragment.this.mChosenPattern == null)
              throw new IllegalStateException("null chosen pattern in stage 'need to confirm");
            if (ChooseLockPattern.ChooseLockPatternFragment.this.mChosenPattern.equals(paramAnonymousList))
            {
              ChooseLockPattern.ChooseLockPatternFragment.this.updateStage(ChooseLockPattern.ChooseLockPatternFragment.Stage.ChoiceConfirmed);
              return;
            }
            ChooseLockPattern.ChooseLockPatternFragment.this.updateStage(ChooseLockPattern.ChooseLockPatternFragment.Stage.ConfirmWrong);
            return;
          }
          if ((ChooseLockPattern.ChooseLockPatternFragment.this.mUiStage == ChooseLockPattern.ChooseLockPatternFragment.Stage.Introduction) || (ChooseLockPattern.ChooseLockPatternFragment.this.mUiStage == ChooseLockPattern.ChooseLockPatternFragment.Stage.ChoiceTooShort))
          {
            if (paramAnonymousList.size() < 4)
            {
              ChooseLockPattern.ChooseLockPatternFragment.this.updateStage(ChooseLockPattern.ChooseLockPatternFragment.Stage.ChoiceTooShort);
              return;
            }
            ChooseLockPattern.ChooseLockPatternFragment.this.mChosenPattern = new ArrayList(paramAnonymousList);
            ChooseLockPattern.ChooseLockPatternFragment.this.updateStage(ChooseLockPattern.ChooseLockPatternFragment.Stage.FirstChoiceValid);
            return;
          }
          throw new IllegalStateException("Unexpected stage " + ChooseLockPattern.ChooseLockPatternFragment.this.mUiStage + " when " + "entering the pattern.");
        }

        public void onPatternStart()
        {
          ChooseLockPattern.ChooseLockPatternFragment.this.mLockPatternView.removeCallbacks(ChooseLockPattern.ChooseLockPatternFragment.this.mClearPatternRunnable);
          patternInProgress();
        }
      };
      this.mUiStage = Stage.Introduction;
      this.mClearPatternRunnable = new Runnable()
      {
        public void run()
        {
          ChooseLockPattern.ChooseLockPatternFragment.this.mLockPatternView.clearPattern();
        }
      };
    }

    private void postClearPatternRunnable()
    {
      this.mLockPatternView.removeCallbacks(this.mClearPatternRunnable);
      this.mLockPatternView.postDelayed(this.mClearPatternRunnable, 2000L);
    }

    private void saveChosenPatternAndFinish()
    {
      LockPatternUtils localLockPatternUtils = this.mChooseLockSettingsHelper.utils();
      if (!localLockPatternUtils.isPatternEverChosen());
      for (int i = 1; ; i = 0)
      {
        boolean bool = getActivity().getIntent().getBooleanExtra("lockscreen.biometric_weak_fallback", false);
        localLockPatternUtils.saveLockPattern(this.mChosenPattern, bool);
        localLockPatternUtils.setLockPatternEnabled(true);
        if (i != 0)
          localLockPatternUtils.setVisiblePatternEnabled(true);
        getActivity().setResult(1);
        getActivity().finish();
        return;
      }
    }

    public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
    {
      super.onActivityResult(paramInt1, paramInt2, paramIntent);
      switch (paramInt1)
      {
      default:
        return;
      case 55:
      }
      if (paramInt2 != -1)
      {
        getActivity().setResult(1);
        getActivity().finish();
      }
      updateStage(Stage.Introduction);
    }

    public void onClick(View paramView)
    {
      if (paramView == this.mFooterLeftButton)
        if (this.mUiStage.leftMode == LeftButtonMode.Retry)
        {
          this.mChosenPattern = null;
          this.mLockPatternView.clearPattern();
          updateStage(Stage.Introduction);
        }
      do
      {
        do
        {
          return;
          if (this.mUiStage.leftMode == LeftButtonMode.Cancel)
          {
            getActivity().setResult(1);
            getActivity().finish();
            return;
          }
          throw new IllegalStateException("left footer button pressed, but stage of " + this.mUiStage + " doesn't make sense");
        }
        while (paramView != this.mFooterRightButton);
        if (this.mUiStage.rightMode == RightButtonMode.Continue)
        {
          if (this.mUiStage != Stage.FirstChoiceValid)
            throw new IllegalStateException("expected ui stage " + Stage.FirstChoiceValid + " when button is " + RightButtonMode.Continue);
          updateStage(Stage.NeedToConfirm);
          return;
        }
        if (this.mUiStage.rightMode == RightButtonMode.Confirm)
        {
          if (this.mUiStage != Stage.ChoiceConfirmed)
            throw new IllegalStateException("expected ui stage " + Stage.ChoiceConfirmed + " when button is " + RightButtonMode.Confirm);
          saveChosenPatternAndFinish();
          return;
        }
      }
      while (this.mUiStage.rightMode != RightButtonMode.Ok);
      if (this.mUiStage != Stage.HelpScreen)
        throw new IllegalStateException("Help screen is only mode with ok button, but stage is " + this.mUiStage);
      this.mLockPatternView.clearPattern();
      this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
      updateStage(Stage.Introduction);
    }

    public void onCreate(Bundle paramBundle)
    {
      super.onCreate(paramBundle);
      this.mChooseLockSettingsHelper = new ChooseLockSettingsHelper(getActivity());
      if (!(getActivity() instanceof ChooseLockPattern))
        throw new SecurityException("Fragment contained in wrong activity");
    }

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      View localView = paramLayoutInflater.inflate(2130968598, null);
      this.mHeaderText = ((TextView)localView.findViewById(2131230759));
      this.mLockPatternView = ((LockPatternView)localView.findViewById(2131230766));
      this.mLockPatternView.setOnPatternListener(this.mChooseNewLockPatternListener);
      this.mLockPatternView.setTactileFeedbackEnabled(this.mChooseLockSettingsHelper.utils().isTactileFeedbackEnabled());
      this.mFooterText = ((TextView)localView.findViewById(2131230767));
      this.mFooterLeftButton = ((TextView)localView.findViewById(2131230768));
      this.mFooterRightButton = ((TextView)localView.findViewById(2131230769));
      this.mFooterLeftButton.setOnClickListener(this);
      this.mFooterRightButton.setOnClickListener(this);
      ((LinearLayoutWithDefaultTouchRecepient)localView.findViewById(2131230765)).setDefaultTouchRecepient(this.mLockPatternView);
      boolean bool = getActivity().getIntent().getBooleanExtra("confirm_credentials", true);
      if (paramBundle == null)
      {
        if (bool)
        {
          updateStage(Stage.NeedToConfirm);
          if (!this.mChooseLockSettingsHelper.launchConfirmationActivity(55, null, null))
            updateStage(Stage.Introduction);
          return localView;
        }
        updateStage(Stage.Introduction);
        return localView;
      }
      String str = paramBundle.getString("chosenPattern");
      if (str != null)
        this.mChosenPattern = LockPatternUtils.stringToPattern(str);
      updateStage(Stage.values()[paramBundle.getInt("uiStage")]);
      return localView;
    }

    public void onSaveInstanceState(Bundle paramBundle)
    {
      super.onSaveInstanceState(paramBundle);
      paramBundle.putInt("uiStage", this.mUiStage.ordinal());
      if (this.mChosenPattern != null)
        paramBundle.putString("chosenPattern", LockPatternUtils.patternToString(this.mChosenPattern));
    }

    protected void updateStage(Stage paramStage)
    {
      Stage localStage = this.mUiStage;
      this.mUiStage = paramStage;
      if (paramStage == Stage.ChoiceTooShort)
      {
        TextView localTextView = this.mHeaderText;
        Resources localResources = getResources();
        int i = paramStage.headerMessage;
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = Integer.valueOf(4);
        localTextView.setText(localResources.getString(i, arrayOfObject));
        if (paramStage.footerMessage != -1)
          break label234;
        this.mFooterText.setText("");
        label79: if (paramStage.leftMode != LeftButtonMode.Gone)
          break label248;
        this.mFooterLeftButton.setVisibility(8);
        label98: this.mFooterRightButton.setText(paramStage.rightMode.text);
        this.mFooterRightButton.setEnabled(paramStage.rightMode.enabled);
        if (!paramStage.patternEnabled)
          break label287;
        this.mLockPatternView.enableInput();
        label140: this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
        switch (ChooseLockPattern.1.$SwitchMap$com$android$settings$ChooseLockPattern$ChooseLockPatternFragment$Stage[this.mUiStage.ordinal()])
        {
        case 4:
        default:
        case 1:
        case 2:
        case 3:
        case 5:
        case 6:
        }
      }
      while (true)
      {
        if (localStage != paramStage)
          this.mHeaderText.announceForAccessibility(this.mHeaderText.getText());
        return;
        this.mHeaderText.setText(paramStage.headerMessage);
        break;
        label234: this.mFooterText.setText(paramStage.footerMessage);
        break label79;
        label248: this.mFooterLeftButton.setVisibility(0);
        this.mFooterLeftButton.setText(paramStage.leftMode.text);
        this.mFooterLeftButton.setEnabled(paramStage.leftMode.enabled);
        break label98;
        label287: this.mLockPatternView.disableInput();
        break label140;
        this.mLockPatternView.clearPattern();
        continue;
        this.mLockPatternView.setPattern(LockPatternView.DisplayMode.Animate, this.mAnimatePattern);
        continue;
        this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
        postClearPatternRunnable();
        continue;
        this.mLockPatternView.clearPattern();
        continue;
        this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
        postClearPatternRunnable();
      }
    }

    static enum LeftButtonMode
    {
      final boolean enabled;
      final int text;

      static
      {
        Gone = new LeftButtonMode("Gone", 4, -1, false);
        LeftButtonMode[] arrayOfLeftButtonMode = new LeftButtonMode[5];
        arrayOfLeftButtonMode[0] = Cancel;
        arrayOfLeftButtonMode[1] = CancelDisabled;
        arrayOfLeftButtonMode[2] = Retry;
        arrayOfLeftButtonMode[3] = RetryDisabled;
        arrayOfLeftButtonMode[4] = Gone;
      }

      private LeftButtonMode(int paramInt, boolean paramBoolean)
      {
        this.text = paramInt;
        this.enabled = paramBoolean;
      }
    }

    static enum RightButtonMode
    {
      final boolean enabled;
      final int text;

      static
      {
        Confirm = new RightButtonMode("Confirm", 2, 2131428337, true);
        ConfirmDisabled = new RightButtonMode("ConfirmDisabled", 3, 2131428337, false);
        Ok = new RightButtonMode("Ok", 4, 17039370, true);
        RightButtonMode[] arrayOfRightButtonMode = new RightButtonMode[5];
        arrayOfRightButtonMode[0] = Continue;
        arrayOfRightButtonMode[1] = ContinueDisabled;
        arrayOfRightButtonMode[2] = Confirm;
        arrayOfRightButtonMode[3] = ConfirmDisabled;
        arrayOfRightButtonMode[4] = Ok;
      }

      private RightButtonMode(int paramInt, boolean paramBoolean)
      {
        this.text = paramInt;
        this.enabled = paramBoolean;
      }
    }

    protected static enum Stage
    {
      final int footerMessage;
      final int headerMessage;
      final ChooseLockPattern.ChooseLockPatternFragment.LeftButtonMode leftMode;
      final boolean patternEnabled;
      final ChooseLockPattern.ChooseLockPatternFragment.RightButtonMode rightMode;

      static
      {
        HelpScreen = new Stage("HelpScreen", 1, 2131428349, ChooseLockPattern.ChooseLockPatternFragment.LeftButtonMode.Gone, ChooseLockPattern.ChooseLockPatternFragment.RightButtonMode.Ok, -1, false);
        ChoiceTooShort = new Stage("ChoiceTooShort", 2, 2131428333, ChooseLockPattern.ChooseLockPatternFragment.LeftButtonMode.Retry, ChooseLockPattern.ChooseLockPatternFragment.RightButtonMode.ContinueDisabled, -1, true);
        FirstChoiceValid = new Stage("FirstChoiceValid", 3, 2131428334, ChooseLockPattern.ChooseLockPatternFragment.LeftButtonMode.Retry, ChooseLockPattern.ChooseLockPatternFragment.RightButtonMode.Continue, -1, false);
        NeedToConfirm = new Stage("NeedToConfirm", 4, 2131428335, ChooseLockPattern.ChooseLockPatternFragment.LeftButtonMode.Cancel, ChooseLockPattern.ChooseLockPatternFragment.RightButtonMode.ConfirmDisabled, -1, true);
        ConfirmWrong = new Stage("ConfirmWrong", 5, 2131428328, ChooseLockPattern.ChooseLockPatternFragment.LeftButtonMode.Cancel, ChooseLockPattern.ChooseLockPatternFragment.RightButtonMode.ConfirmDisabled, -1, true);
        ChoiceConfirmed = new Stage("ChoiceConfirmed", 6, 2131428336, ChooseLockPattern.ChooseLockPatternFragment.LeftButtonMode.Cancel, ChooseLockPattern.ChooseLockPatternFragment.RightButtonMode.Confirm, -1, false);
        Stage[] arrayOfStage = new Stage[7];
        arrayOfStage[0] = Introduction;
        arrayOfStage[1] = HelpScreen;
        arrayOfStage[2] = ChoiceTooShort;
        arrayOfStage[3] = FirstChoiceValid;
        arrayOfStage[4] = NeedToConfirm;
        arrayOfStage[5] = ConfirmWrong;
        arrayOfStage[6] = ChoiceConfirmed;
      }

      private Stage(int paramInt1, ChooseLockPattern.ChooseLockPatternFragment.LeftButtonMode paramLeftButtonMode, ChooseLockPattern.ChooseLockPatternFragment.RightButtonMode paramRightButtonMode, int paramInt2, boolean paramBoolean)
      {
        this.headerMessage = paramInt1;
        this.leftMode = paramLeftButtonMode;
        this.rightMode = paramRightButtonMode;
        this.footerMessage = paramInt2;
        this.patternEnabled = paramBoolean;
      }
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.ChooseLockPattern
 * JD-Core Version:    0.6.2
 */