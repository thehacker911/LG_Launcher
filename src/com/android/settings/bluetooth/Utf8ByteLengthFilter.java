package com.android.settings.bluetooth;

import android.text.InputFilter;
import android.text.Spanned;

class Utf8ByteLengthFilter
  implements InputFilter
{
  private final int mMaxBytes;

  Utf8ByteLengthFilter(int paramInt)
  {
    this.mMaxBytes = paramInt;
  }

  public CharSequence filter(CharSequence paramCharSequence, int paramInt1, int paramInt2, Spanned paramSpanned, int paramInt3, int paramInt4)
  {
    int i = 0;
    int j = paramInt1;
    if (j < paramInt2)
    {
      int i7 = paramCharSequence.charAt(j);
      int i8;
      if (i7 < 128)
        i8 = 1;
      while (true)
      {
        i += i8;
        j++;
        break;
        if (i7 < 2048)
          i8 = 2;
        else
          i8 = 3;
      }
    }
    int k = paramSpanned.length();
    int m = 0;
    int n = 0;
    if (n < k)
    {
      int i5;
      int i6;
      if ((n < paramInt3) || (n >= paramInt4))
      {
        i5 = paramSpanned.charAt(n);
        if (i5 >= 128)
          break label137;
        i6 = 1;
      }
      while (true)
      {
        m += i6;
        n++;
        break;
        label137: if (i5 < 2048)
          i6 = 2;
        else
          i6 = 3;
      }
    }
    int i1 = this.mMaxBytes - m;
    if (i1 <= 0)
      return "";
    if (i1 >= i)
      return null;
    for (int i2 = paramInt1; i2 < paramInt2; i2++)
    {
      int i3 = paramCharSequence.charAt(i2);
      int i4;
      if (i3 < 128)
        i4 = 1;
      while (true)
      {
        i1 -= i4;
        if (i1 >= 0)
          break;
        return paramCharSequence.subSequence(paramInt1, i2);
        if (i3 < 2048)
          i4 = 2;
        else
          i4 = 3;
      }
    }
    return null;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.Utf8ByteLengthFilter
 * JD-Core Version:    0.6.2
 */