package com.android.settings;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import com.android.internal.widget.LockPatternUtils;

public class OwnerInfoSettings extends Fragment
{
  private CheckBox mCheckbox;
  private LockPatternUtils mLockPatternUtils;
  private EditText mNickname;
  private EditText mOwnerInfo;
  private boolean mShowNickname;
  private int mUserId;
  private View mView;

  private void initView(View paramView)
  {
    getActivity().getContentResolver();
    String str = this.mLockPatternUtils.getOwnerInfo(this.mUserId);
    boolean bool = this.mLockPatternUtils.isOwnerInfoEnabled();
    this.mCheckbox = ((CheckBox)this.mView.findViewById(2131230925));
    this.mOwnerInfo = ((EditText)this.mView.findViewById(2131230926));
    this.mOwnerInfo.setText(str);
    this.mOwnerInfo.setEnabled(bool);
    this.mNickname = ((EditText)this.mView.findViewById(2131230924));
    if (!this.mShowNickname)
    {
      this.mNickname.setVisibility(8);
      this.mCheckbox.setChecked(bool);
      if (UserHandle.myUserId() != 0)
      {
        if (!UserManager.get(getActivity()).isLinkedUser())
          break label191;
        this.mCheckbox.setText(2131427616);
      }
    }
    while (true)
    {
      this.mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
      {
        public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
        {
          OwnerInfoSettings.this.mLockPatternUtils.setOwnerInfoEnabled(paramAnonymousBoolean);
          OwnerInfoSettings.this.mOwnerInfo.setEnabled(paramAnonymousBoolean);
        }
      });
      return;
      this.mNickname.setText(UserManager.get(getActivity()).getUserName());
      this.mNickname.setSelected(true);
      break;
      label191: this.mCheckbox.setText(2131427614);
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Bundle localBundle = getArguments();
    if ((localBundle != null) && (localBundle.containsKey("show_nickname")))
      this.mShowNickname = localBundle.getBoolean("show_nickname");
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mView = paramLayoutInflater.inflate(2130968658, paramViewGroup, false);
    this.mUserId = UserHandle.myUserId();
    this.mLockPatternUtils = new LockPatternUtils(getActivity());
    initView(this.mView);
    return this.mView;
  }

  public void onPause()
  {
    super.onPause();
    saveChanges();
  }

  void saveChanges()
  {
    getActivity().getContentResolver();
    String str1 = this.mOwnerInfo.getText().toString();
    this.mLockPatternUtils.setOwnerInfo(str1, this.mUserId);
    if (this.mShowNickname)
    {
      String str2 = UserManager.get(getActivity()).getUserName();
      Editable localEditable = this.mNickname.getText();
      if ((!TextUtils.isEmpty(localEditable)) && (!localEditable.equals(str2)))
        UserManager.get(getActivity()).setUserName(UserHandle.myUserId(), localEditable.toString());
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.OwnerInfoSettings
 * JD-Core Version:    0.6.2
 */