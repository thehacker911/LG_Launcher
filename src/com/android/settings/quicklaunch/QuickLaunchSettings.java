package com.android.settings.quicklaunch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.Settings.Bookmarks;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import java.net.URISyntaxException;

public class QuickLaunchSettings extends PreferenceActivity
  implements DialogInterface.OnClickListener, AdapterView.OnItemLongClickListener
{
  private static final String[] sProjection = { "shortcut", "title", "intent" };
  private SparseBooleanArray mBookmarkedShortcuts;
  private Cursor mBookmarksCursor;
  private BookmarksObserver mBookmarksObserver;
  private CharSequence mClearDialogBookmarkTitle;
  private char mClearDialogShortcut;
  private PreferenceGroup mShortcutGroup;
  private SparseArray<ShortcutPreference> mShortcutToPreference;
  private Handler mUiHandler = new Handler();

  private void clearShortcut(char paramChar)
  {
    ContentResolver localContentResolver = getContentResolver();
    Uri localUri = Settings.Bookmarks.CONTENT_URI;
    String[] arrayOfString = new String[1];
    arrayOfString[0] = String.valueOf(paramChar);
    localContentResolver.delete(localUri, "shortcut=?", arrayOfString);
  }

  private ShortcutPreference createPreference(char paramChar)
  {
    ShortcutPreference localShortcutPreference = new ShortcutPreference(this, paramChar);
    this.mShortcutGroup.addPreference(localShortcutPreference);
    this.mShortcutToPreference.put(paramChar, localShortcutPreference);
    return localShortcutPreference;
  }

  private ShortcutPreference getOrCreatePreference(char paramChar)
  {
    ShortcutPreference localShortcutPreference = (ShortcutPreference)this.mShortcutToPreference.get(paramChar);
    if (localShortcutPreference != null)
      return localShortcutPreference;
    Log.w("QuickLaunchSettings", "Unknown shortcut '" + paramChar + "', creating preference anyway");
    return createPreference(paramChar);
  }

  private void initShortcutPreferences()
  {
    SparseBooleanArray localSparseBooleanArray = new SparseBooleanArray();
    KeyCharacterMap localKeyCharacterMap = KeyCharacterMap.load(-1);
    int i = -1 + KeyEvent.getMaxKeyCode();
    if (i >= 0)
    {
      char c = Character.toLowerCase(localKeyCharacterMap.getDisplayLabel(i));
      if ((c == 0) || (localSparseBooleanArray.get(c, false)));
      while (true)
      {
        i--;
        break;
        if (Character.isLetterOrDigit(c))
        {
          localSparseBooleanArray.put(c, true);
          createPreference(c);
        }
      }
    }
  }

  private void refreshShortcuts()
  {
    while (true)
    {
      Cursor localCursor;
      try
      {
        localCursor = this.mBookmarksCursor;
        if (localCursor == null)
          return;
        if (!localCursor.requery())
        {
          Log.e("QuickLaunchSettings", "Could not requery cursor when refreshing shortcuts.");
          continue;
        }
      }
      finally
      {
      }
      SparseBooleanArray localSparseBooleanArray1 = this.mBookmarkedShortcuts;
      SparseBooleanArray localSparseBooleanArray2 = new SparseBooleanArray();
      label53: char c;
      ShortcutPreference localShortcutPreference2;
      Object localObject2;
      String str;
      PackageManager localPackageManager;
      while (localCursor.moveToNext())
      {
        c = Character.toLowerCase((char)localCursor.getInt(0));
        if (c != 0)
        {
          localShortcutPreference2 = getOrCreatePreference(c);
          localObject2 = Settings.Bookmarks.getTitle(this, localCursor);
          str = localCursor.getString(localCursor.getColumnIndex("intent"));
          localPackageManager = getPackageManager();
        }
      }
      try
      {
        ResolveInfo localResolveInfo = localPackageManager.resolveActivity(Intent.parseUri(str, 0), 0);
        if (localResolveInfo != null)
        {
          CharSequence localCharSequence = localResolveInfo.loadLabel(localPackageManager);
          localObject2 = localCharSequence;
        }
        localShortcutPreference2.setTitle((CharSequence)localObject2);
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = String.valueOf(c);
        localShortcutPreference2.setSummary(getString(2131428555, arrayOfObject));
        localShortcutPreference2.setHasBookmark(true);
        localSparseBooleanArray2.put(c, true);
        if (localSparseBooleanArray1 == null)
          break label53;
        localSparseBooleanArray1.put(c, false);
        break label53;
        if (localSparseBooleanArray1 != null)
        {
          i = -1 + localSparseBooleanArray1.size();
          if (i >= 0)
          {
            if (!localSparseBooleanArray1.valueAt(i))
              break label295;
            int j = (char)localSparseBooleanArray1.keyAt(i);
            ShortcutPreference localShortcutPreference1 = (ShortcutPreference)this.mShortcutToPreference.get(j);
            if (localShortcutPreference1 == null)
              break label295;
            localShortcutPreference1.setHasBookmark(false);
            break label295;
          }
        }
        this.mBookmarkedShortcuts = localSparseBooleanArray2;
        localCursor.deactivate();
      }
      catch (URISyntaxException localURISyntaxException)
      {
        while (true)
        {
          int i;
          continue;
          label295: i--;
        }
      }
    }
  }

  private void showClearDialog(ShortcutPreference paramShortcutPreference)
  {
    if (!paramShortcutPreference.hasBookmark())
      return;
    this.mClearDialogBookmarkTitle = paramShortcutPreference.getTitle();
    this.mClearDialogShortcut = paramShortcutPreference.getShortcut();
    showDialog(0);
  }

  private void updateShortcut(char paramChar, Intent paramIntent)
  {
    Settings.Bookmarks.add(getContentResolver(), paramIntent, "", "@quicklaunch", paramChar, 0);
  }

  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt2 != -1)
      return;
    if (paramInt1 == 1)
    {
      if (paramIntent == null)
      {
        Log.w("QuickLaunchSettings", "Result from bookmark picker does not have an intent.");
        return;
      }
      updateShortcut(paramIntent.getCharExtra("com.android.settings.quicklaunch.SHORTCUT", '\000'), paramIntent);
      return;
    }
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
  }

  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if ((this.mClearDialogShortcut > 0) && (paramInt == -1))
      clearShortcut(this.mClearDialogShortcut);
    this.mClearDialogBookmarkTitle = null;
    this.mClearDialogShortcut = '\000';
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131034144);
    this.mShortcutGroup = ((PreferenceGroup)findPreference("shortcut_category"));
    this.mShortcutToPreference = new SparseArray();
    this.mBookmarksObserver = new BookmarksObserver(this.mUiHandler);
    initShortcutPreferences();
    this.mBookmarksCursor = managedQuery(Settings.Bookmarks.CONTENT_URI, sProjection, null, null);
    getListView().setOnItemLongClickListener(this);
  }

  protected Dialog onCreateDialog(int paramInt)
  {
    switch (paramInt)
    {
    default:
      return super.onCreateDialog(paramInt);
    case 0:
    }
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this).setTitle(getString(2131428556)).setIconAttribute(16843605);
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = Character.valueOf(this.mClearDialogShortcut);
    arrayOfObject[1] = this.mClearDialogBookmarkTitle;
    return localBuilder.setMessage(getString(2131428557, arrayOfObject)).setPositiveButton(2131428558, this).setNegativeButton(2131428559, this).create();
  }

  public boolean onItemLongClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    Preference localPreference = (Preference)getPreferenceScreen().getRootAdapter().getItem(paramInt);
    if (!(localPreference instanceof ShortcutPreference))
      return false;
    showClearDialog((ShortcutPreference)localPreference);
    return true;
  }

  protected void onPause()
  {
    super.onPause();
    getContentResolver().unregisterContentObserver(this.mBookmarksObserver);
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    if (!(paramPreference instanceof ShortcutPreference))
      return false;
    ShortcutPreference localShortcutPreference = (ShortcutPreference)paramPreference;
    Intent localIntent = new Intent(this, BookmarkPicker.class);
    localIntent.putExtra("com.android.settings.quicklaunch.SHORTCUT", localShortcutPreference.getShortcut());
    startActivityForResult(localIntent, 1);
    return true;
  }

  protected void onPrepareDialog(int paramInt, Dialog paramDialog)
  {
    switch (paramInt)
    {
    default:
      return;
    case 0:
    }
    AlertDialog localAlertDialog = (AlertDialog)paramDialog;
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = Character.valueOf(this.mClearDialogShortcut);
    arrayOfObject[1] = this.mClearDialogBookmarkTitle;
    localAlertDialog.setMessage(getString(2131428557, arrayOfObject));
  }

  protected void onRestoreInstanceState(Bundle paramBundle)
  {
    super.onRestoreInstanceState(paramBundle);
    this.mClearDialogBookmarkTitle = paramBundle.getString("CLEAR_DIALOG_BOOKMARK_TITLE");
    this.mClearDialogShortcut = ((char)paramBundle.getInt("CLEAR_DIALOG_SHORTCUT", 0));
  }

  protected void onResume()
  {
    super.onResume();
    getContentResolver().registerContentObserver(Settings.Bookmarks.CONTENT_URI, true, this.mBookmarksObserver);
    refreshShortcuts();
  }

  protected void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putCharSequence("CLEAR_DIALOG_BOOKMARK_TITLE", this.mClearDialogBookmarkTitle);
    paramBundle.putInt("CLEAR_DIALOG_SHORTCUT", this.mClearDialogShortcut);
  }

  private class BookmarksObserver extends ContentObserver
  {
    public BookmarksObserver(Handler arg2)
    {
      super();
    }

    public void onChange(boolean paramBoolean)
    {
      super.onChange(paramBoolean);
      QuickLaunchSettings.this.refreshShortcuts();
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.quicklaunch.QuickLaunchSettings
 * JD-Core Version:    0.6.2
 */