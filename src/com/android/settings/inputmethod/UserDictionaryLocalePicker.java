package com.android.settings.inputmethod;

import com.android.internal.app.LocalePicker;

public class UserDictionaryLocalePicker extends LocalePicker
{
  public UserDictionaryLocalePicker(UserDictionaryAddWordFragment paramUserDictionaryAddWordFragment)
  {
    setLocaleSelectionListener(paramUserDictionaryAddWordFragment);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.inputmethod.UserDictionaryLocalePicker
 * JD-Core Version:    0.6.2
 */