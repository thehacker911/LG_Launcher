package com.android.settings.deviceinfo;

import android.content.Context;
import android.os.Environment.UserEnvironment;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.File;

public class FileItemInfoLayout extends RelativeLayout
  implements Checkable
{
  private static final int sLengthExternalStorageDirPrefix = 1 + new Environment.UserEnvironment(UserHandle.myUserId()).getExternalStorageDirectory().getAbsolutePath().length();
  private CheckBox mCheckbox;
  private TextView mFileNameView;
  private TextView mFileSizeView;

  public FileItemInfoLayout(Context paramContext)
  {
    this(paramContext, null);
  }

  public FileItemInfoLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }

  public FileItemInfoLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }

  public CheckBox getCheckBox()
  {
    return this.mCheckbox;
  }

  @ViewDebug.ExportedProperty
  public boolean isChecked()
  {
    return this.mCheckbox.isChecked();
  }

  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mFileNameView = ((TextView)findViewById(2131231047));
    this.mFileSizeView = ((TextView)findViewById(2131231048));
    this.mCheckbox = ((CheckBox)findViewById(2131231046));
  }

  public void setChecked(boolean paramBoolean)
  {
    this.mCheckbox.setChecked(paramBoolean);
  }

  public void setFileName(String paramString)
  {
    this.mFileNameView.setText(paramString.substring(sLengthExternalStorageDirPrefix));
  }

  public void setFileSize(String paramString)
  {
    this.mFileSizeView.setText(paramString);
  }

  public void toggle()
  {
    if (!this.mCheckbox.isChecked());
    for (boolean bool = true; ; bool = false)
    {
      setChecked(bool);
      return;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.deviceinfo.FileItemInfoLayout
 * JD-Core Version:    0.6.2
 */