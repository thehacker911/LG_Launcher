package com.android.settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import com.android.internal.widget.LockPatternUtils;

public final class ChooseLockSettingsHelper
{
  private Activity mActivity;
  private Fragment mFragment;
  private LockPatternUtils mLockPatternUtils;

  public ChooseLockSettingsHelper(Activity paramActivity)
  {
    this.mActivity = paramActivity;
    this.mLockPatternUtils = new LockPatternUtils(paramActivity);
  }

  public ChooseLockSettingsHelper(Activity paramActivity, Fragment paramFragment)
  {
    this(paramActivity);
    this.mFragment = paramFragment;
  }

  private boolean confirmPassword(int paramInt)
  {
    if (!this.mLockPatternUtils.isLockPasswordEnabled())
      return false;
    Intent localIntent = new Intent();
    localIntent.setClassName("com.android.settings", "com.android.settings.ConfirmLockPassword");
    if (this.mFragment != null)
      this.mFragment.startActivityForResult(localIntent, paramInt);
    while (true)
    {
      return true;
      this.mActivity.startActivityForResult(localIntent, paramInt);
    }
  }

  private boolean confirmPattern(int paramInt, CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    if ((!this.mLockPatternUtils.isLockPatternEnabled()) || (!this.mLockPatternUtils.savedPatternExists()))
      return false;
    Intent localIntent = new Intent();
    localIntent.putExtra("com.android.settings.ConfirmLockPattern.header", paramCharSequence1);
    localIntent.putExtra("com.android.settings.ConfirmLockPattern.footer", paramCharSequence2);
    localIntent.setClassName("com.android.settings", "com.android.settings.ConfirmLockPattern");
    if (this.mFragment != null)
      this.mFragment.startActivityForResult(localIntent, paramInt);
    while (true)
    {
      return true;
      this.mActivity.startActivityForResult(localIntent, paramInt);
    }
  }

  boolean launchConfirmationActivity(int paramInt, CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    switch (this.mLockPatternUtils.getKeyguardStoredPasswordQuality())
    {
    default:
      return false;
    case 65536:
      return confirmPattern(paramInt, paramCharSequence1, paramCharSequence2);
    case 131072:
    case 262144:
    case 327680:
    case 393216:
    }
    return confirmPassword(paramInt);
  }

  public LockPatternUtils utils()
  {
    return this.mLockPatternUtils;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.ChooseLockSettingsHelper
 * JD-Core Version:    0.6.2
 */