package com.android.settings.tts;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TtsEngines;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.android.settings.SettingsPreferenceFragment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

public class TtsEngineSettingsFragment extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener
{
  private Intent mEngineSettingsIntent;
  private Preference mEngineSettingsPreference;
  private TtsEngines mEnginesHelper;
  private Preference mInstallVoicesPreference;
  private final BroadcastReceiver mLanguagesChangedReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.speech.tts.engine.TTS_DATA_INSTALLED".equals(paramAnonymousIntent.getAction()))
        TtsEngineSettingsFragment.this.checkTtsData();
    }
  };
  private ListPreference mLocalePreference;
  private int mSelectedLocaleIndex = -1;
  private int mSystemLocaleIndex = -1;
  private TextToSpeech mTts;
  private final TextToSpeech.OnInitListener mTtsInitListener = new TextToSpeech.OnInitListener()
  {
    public void onInit(int paramAnonymousInt)
    {
      if (paramAnonymousInt != 0)
      {
        TtsEngineSettingsFragment.this.finishFragment();
        return;
      }
      TtsEngineSettingsFragment.this.getActivity().runOnUiThread(new Runnable()
      {
        public void run()
        {
          TtsEngineSettingsFragment.this.mLocalePreference.setEnabled(true);
        }
      });
    }
  };
  private Intent mVoiceDataDetails;

  private final void checkTtsData()
  {
    Intent localIntent = new Intent("android.speech.tts.engine.CHECK_TTS_DATA");
    localIntent.setPackage(getEngineName());
    try
    {
      startActivityForResult(localIntent, 1977);
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Log.e("TtsEngineSettings", "Failed to check TTS data, no activity found for " + localIntent + ")");
    }
  }

  private String getEngineLabel()
  {
    return getArguments().getString("label");
  }

  private String getEngineName()
  {
    return getArguments().getString("name");
  }

  private void installVoiceData()
  {
    if (TextUtils.isEmpty(getEngineName()))
      return;
    Intent localIntent = new Intent("android.speech.tts.engine.INSTALL_TTS_DATA");
    localIntent.addFlags(268435456);
    localIntent.setPackage(getEngineName());
    try
    {
      Log.v("TtsEngineSettings", "Installing voice data: " + localIntent.toUri(0));
      startActivity(localIntent);
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Log.e("TtsEngineSettings", "Failed to install TTS data, no acitivty found for " + localIntent + ")");
    }
  }

  private void setLocalePreference(int paramInt)
  {
    if (paramInt < 0)
    {
      this.mLocalePreference.setValue("");
      this.mLocalePreference.setSummary(2131428828);
      return;
    }
    this.mLocalePreference.setValueIndex(paramInt);
    this.mLocalePreference.setSummary(this.mLocalePreference.getEntries()[paramInt]);
  }

  private void updateDefaultLocalePref(ArrayList<String> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.size() == 0))
    {
      this.mLocalePreference.setEnabled(false);
      return;
    }
    String str1 = this.mEnginesHelper.getLocalePrefForEngine(getEngineName());
    ArrayList localArrayList = new ArrayList(paramArrayList.size());
    int i = 0;
    if (i < paramArrayList.size())
    {
      String[] arrayOfString = ((String)paramArrayList.get(i)).split("-");
      Locale localLocale;
      if (arrayOfString.length == 1)
        localLocale = new Locale(arrayOfString[0]);
      while (true)
      {
        if (localLocale != null)
          localArrayList.add(new Pair(localLocale.getDisplayName(), paramArrayList.get(i)));
        i++;
        break;
        if (arrayOfString.length == 2)
        {
          localLocale = new Locale(arrayOfString[0], arrayOfString[1]);
        }
        else
        {
          int m = arrayOfString.length;
          localLocale = null;
          if (m == 3)
            localLocale = new Locale(arrayOfString[0], arrayOfString[1], arrayOfString[2]);
        }
      }
    }
    Collections.sort(localArrayList, new Comparator()
    {
      public int compare(Pair<String, String> paramAnonymousPair1, Pair<String, String> paramAnonymousPair2)
      {
        return ((String)paramAnonymousPair1.first).compareToIgnoreCase((String)paramAnonymousPair2.first);
      }
    });
    String str2 = this.mEnginesHelper.getDefaultLocale();
    this.mSelectedLocaleIndex = -1;
    this.mSystemLocaleIndex = -1;
    CharSequence[] arrayOfCharSequence1 = new CharSequence[paramArrayList.size()];
    CharSequence[] arrayOfCharSequence2 = new CharSequence[paramArrayList.size()];
    int j = 0;
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      Pair localPair = (Pair)localIterator.next();
      if (((String)localPair.second).equalsIgnoreCase(str1))
        this.mSelectedLocaleIndex = j;
      if (((String)localPair.second).equalsIgnoreCase(str2))
        this.mSystemLocaleIndex = j;
      arrayOfCharSequence1[j] = ((CharSequence)localPair.first);
      int k = j + 1;
      arrayOfCharSequence2[j] = ((CharSequence)localPair.second);
      j = k;
    }
    this.mLocalePreference.setEntries(arrayOfCharSequence1);
    this.mLocalePreference.setEntryValues(arrayOfCharSequence2);
    this.mLocalePreference.setEnabled(true);
    setLocalePreference(this.mSelectedLocaleIndex);
  }

  private void updateLanguageTo(String paramString)
  {
    int i = -1;
    int j = 0;
    if (j < this.mLocalePreference.getEntryValues().length)
    {
      if (paramString.equalsIgnoreCase(this.mLocalePreference.getEntryValues()[j].toString()))
        i = j;
    }
    else
    {
      if (i != -1)
        break label58;
      Log.w("TtsEngineSettings", "updateLanguageTo called with unknown locale argument");
    }
    label172: 
    while (true)
    {
      return;
      j++;
      break;
      label58: this.mLocalePreference.setSummary(this.mLocalePreference.getEntries()[i]);
      this.mSelectedLocaleIndex = i;
      if (this.mSelectedLocaleIndex == this.mSystemLocaleIndex)
        this.mEnginesHelper.updateLocalePrefForEngine(getEngineName(), "");
      while (true)
      {
        if (!getEngineName().equals(this.mTts.getCurrentEngine()))
          break label172;
        String[] arrayOfString = TtsEngines.parseLocalePref(paramString);
        if (arrayOfString == null)
          break;
        this.mTts.setLanguage(new Locale(arrayOfString[0], arrayOfString[1], arrayOfString[2]));
        return;
        this.mEnginesHelper.updateLocalePrefForEngine(getEngineName(), paramString);
      }
    }
  }

  private void updateVoiceDetails()
  {
    ArrayList localArrayList1 = this.mVoiceDataDetails.getStringArrayListExtra("availableVoices");
    ArrayList localArrayList2 = this.mVoiceDataDetails.getStringArrayListExtra("unavailableVoices");
    if ((localArrayList2 != null) && (localArrayList2.size() > 0))
      this.mInstallVoicesPreference.setEnabled(true);
    while (localArrayList1 == null)
    {
      Log.e("TtsEngineSettings", "TTS data check failed (available == null).");
      this.mLocalePreference.setEnabled(false);
      return;
      this.mInstallVoicesPreference.setEnabled(false);
    }
    updateDefaultLocalePref(localArrayList1);
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt1 == 1977)
    {
      this.mVoiceDataDetails = paramIntent;
      updateVoiceDetails();
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131034165);
    this.mEnginesHelper = new TtsEngines(getActivity());
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    this.mLocalePreference = ((ListPreference)localPreferenceScreen.findPreference("tts_default_lang"));
    this.mLocalePreference.setOnPreferenceChangeListener(this);
    this.mEngineSettingsPreference = localPreferenceScreen.findPreference("tts_engine_settings");
    this.mEngineSettingsPreference.setOnPreferenceClickListener(this);
    this.mInstallVoicesPreference = localPreferenceScreen.findPreference("tts_install_data");
    this.mInstallVoicesPreference.setOnPreferenceClickListener(this);
    localPreferenceScreen.setTitle(getEngineLabel());
    localPreferenceScreen.setKey(getEngineName());
    Preference localPreference = this.mEngineSettingsPreference;
    Resources localResources = getResources();
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = getEngineLabel();
    localPreference.setTitle(localResources.getString(2131428852, arrayOfObject));
    this.mEngineSettingsIntent = this.mEnginesHelper.getSettingsIntent(getEngineName());
    if (this.mEngineSettingsIntent == null)
      this.mEngineSettingsPreference.setEnabled(false);
    this.mInstallVoicesPreference.setEnabled(false);
    this.mLocalePreference.setEnabled(false);
    this.mLocalePreference.setEntries(new CharSequence[0]);
    this.mLocalePreference.setEntryValues(new CharSequence[0]);
    this.mVoiceDataDetails = ((Intent)getArguments().getParcelable("voices"));
    this.mTts = new TextToSpeech(getActivity().getApplicationContext(), this.mTtsInitListener, getEngineName());
    checkTtsData();
    getActivity().registerReceiver(this.mLanguagesChangedReceiver, new IntentFilter("android.speech.tts.engine.TTS_DATA_INSTALLED"));
  }

  public void onDestroy()
  {
    getActivity().unregisterReceiver(this.mLanguagesChangedReceiver);
    this.mTts.shutdown();
    super.onDestroy();
  }

  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if (paramPreference == this.mLocalePreference)
    {
      updateLanguageTo((String)paramObject);
      return true;
    }
    return false;
  }

  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (paramPreference == this.mInstallVoicesPreference)
    {
      installVoiceData();
      return true;
    }
    if (paramPreference == this.mEngineSettingsPreference)
    {
      startActivity(this.mEngineSettingsIntent);
      return true;
    }
    return false;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.tts.TtsEngineSettingsFragment
 * JD-Core Version:    0.6.2
 */