package com.android.settings;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.provider.UserDictionary.Words;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import com.android.settings.inputmethod.UserDictionaryAddWordFragment;
import com.android.settings.inputmethod.UserDictionarySettingsUtils;
import java.util.Locale;

public class UserDictionarySettings extends ListFragment
{
  private static final String[] QUERY_PROJECTION = { "_id", "word", "shortcut" };
  private Cursor mCursor;
  protected String mLocale;

  private ListAdapter createAdapter()
  {
    return new MyAdapter(getActivity(), 2130968725, this.mCursor, new String[] { "word", "shortcut" }, new int[] { 16908308, 16908309 }, this);
  }

  private Cursor createCursor(String paramString)
  {
    if ("".equals(paramString))
      return getActivity().managedQuery(UserDictionary.Words.CONTENT_URI, QUERY_PROJECTION, "locale is null", null, "UPPER(word)");
    if (paramString != null);
    for (String str = paramString; ; str = Locale.getDefault().toString())
      return getActivity().managedQuery(UserDictionary.Words.CONTENT_URI, QUERY_PROJECTION, "locale=?", new String[] { str }, "UPPER(word)");
  }

  public static void deleteWord(String paramString1, String paramString2, ContentResolver paramContentResolver)
  {
    if (TextUtils.isEmpty(paramString2))
    {
      paramContentResolver.delete(UserDictionary.Words.CONTENT_URI, "word=? AND shortcut is null OR shortcut=''", new String[] { paramString1 });
      return;
    }
    paramContentResolver.delete(UserDictionary.Words.CONTENT_URI, "word=? AND shortcut=?", new String[] { paramString1, paramString2 });
  }

  private String getShortcut(int paramInt)
  {
    if (this.mCursor == null);
    do
    {
      return null;
      this.mCursor.moveToPosition(paramInt);
    }
    while (this.mCursor.isAfterLast());
    return this.mCursor.getString(this.mCursor.getColumnIndexOrThrow("shortcut"));
  }

  private String getWord(int paramInt)
  {
    if (this.mCursor == null);
    do
    {
      return null;
      this.mCursor.moveToPosition(paramInt);
    }
    while (this.mCursor.isAfterLast());
    return this.mCursor.getString(this.mCursor.getColumnIndexOrThrow("word"));
  }

  private void showAddOrEditDialog(String paramString1, String paramString2)
  {
    Bundle localBundle = new Bundle();
    if (paramString1 == null);
    for (int i = 1; ; i = 0)
    {
      localBundle.putInt("mode", i);
      localBundle.putString("word", paramString1);
      localBundle.putString("shortcut", paramString2);
      localBundle.putString("locale", this.mLocale);
      ((PreferenceActivity)getActivity()).startPreferencePanel(UserDictionaryAddWordFragment.class.getName(), localBundle, 2131428531, null, null, 0);
      return;
    }
  }

  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    Intent localIntent = getActivity().getIntent();
    String str1;
    Bundle localBundle;
    String str2;
    label33: String str3;
    if (localIntent == null)
    {
      str1 = null;
      localBundle = getArguments();
      if (localBundle != null)
        break label144;
      str2 = null;
      if (str2 == null)
        break label156;
      str3 = str2;
    }
    while (true)
    {
      this.mLocale = str3;
      this.mCursor = createCursor(str3);
      TextView localTextView = (TextView)getView().findViewById(16908292);
      localTextView.setText(2131428544);
      ListView localListView = getListView();
      localListView.setAdapter(createAdapter());
      localListView.setFastScrollEnabled(true);
      localListView.setEmptyView(localTextView);
      setHasOptionsMenu(true);
      getActivity().getActionBar().setSubtitle(UserDictionarySettingsUtils.getLocaleDisplayName(getActivity(), this.mLocale));
      return;
      str1 = localIntent.getStringExtra("locale");
      break;
      label144: str2 = localBundle.getString("locale");
      break label33;
      label156: if (str1 != null)
        str3 = str1;
      else
        str3 = null;
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    getActivity().getActionBar().setTitle(2131428528);
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    paramMenu.add(0, 1, 0, 2131428530).setIcon(2130837589).setShowAsAction(5);
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    return paramLayoutInflater.inflate(17367165, paramViewGroup, false);
  }

  public void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
  {
    String str1 = getWord(paramInt);
    String str2 = getShortcut(paramInt);
    if (str1 != null)
      showAddOrEditDialog(str1, str2);
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (paramMenuItem.getItemId() == 1)
    {
      showAddOrEditDialog(null, null);
      return true;
    }
    return false;
  }

  private static class MyAdapter extends SimpleCursorAdapter
    implements SectionIndexer
  {
    private AlphabetIndexer mIndexer;
    private final SimpleCursorAdapter.ViewBinder mViewBinder = new SimpleCursorAdapter.ViewBinder()
    {
      public boolean setViewValue(View paramAnonymousView, Cursor paramAnonymousCursor, int paramAnonymousInt)
      {
        if (paramAnonymousInt == 2)
        {
          String str = paramAnonymousCursor.getString(2);
          if (TextUtils.isEmpty(str))
            paramAnonymousView.setVisibility(8);
          while (true)
          {
            paramAnonymousView.invalidate();
            return true;
            ((TextView)paramAnonymousView).setText(str);
            paramAnonymousView.setVisibility(0);
          }
        }
        return false;
      }
    };

    public MyAdapter(Context paramContext, int paramInt, Cursor paramCursor, String[] paramArrayOfString, int[] paramArrayOfInt, UserDictionarySettings paramUserDictionarySettings)
    {
      super(paramInt, paramCursor, paramArrayOfString, paramArrayOfInt);
      if (paramCursor != null)
      {
        String str = paramContext.getString(17040514);
        this.mIndexer = new AlphabetIndexer(paramCursor, paramCursor.getColumnIndexOrThrow("word"), str);
      }
      setViewBinder(this.mViewBinder);
    }

    public int getPositionForSection(int paramInt)
    {
      if (this.mIndexer == null)
        return 0;
      return this.mIndexer.getPositionForSection(paramInt);
    }

    public int getSectionForPosition(int paramInt)
    {
      if (this.mIndexer == null)
        return 0;
      return this.mIndexer.getSectionForPosition(paramInt);
    }

    public Object[] getSections()
    {
      if (this.mIndexer == null)
        return null;
      return this.mIndexer.getSections();
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.UserDictionarySettings
 * JD-Core Version:    0.6.2
 */