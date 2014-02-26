package com.android.settings.tts;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.EngineInfo;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TtsEngines;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.Checkable;
import com.android.settings.SettingsPreferenceFragment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TextToSpeechSettings extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener, TtsEnginePreference.RadioButtonGroupState
{
  private List<String> mAvailableStrLocals;
  private Checkable mCurrentChecked;
  private Locale mCurrentDefaultLocale;
  private String mCurrentEngine;
  private int mDefaultRate = 100;
  private ListPreference mDefaultRatePref;
  private PreferenceCategory mEnginePreferenceCategory;
  private Preference mEngineStatus;
  private TtsEngines mEnginesHelper = null;
  private final TextToSpeech.OnInitListener mInitListener = new TextToSpeech.OnInitListener()
  {
    public void onInit(int paramAnonymousInt)
    {
      TextToSpeechSettings.this.onInitEngine(paramAnonymousInt);
    }
  };
  private Preference mPlayExample;
  private String mPreviousEngine;
  private String mSampleText = "";
  private TextToSpeech mTts = null;
  private final TextToSpeech.OnInitListener mUpdateListener = new TextToSpeech.OnInitListener()
  {
    public void onInit(int paramAnonymousInt)
    {
      TextToSpeechSettings.this.onUpdateEngine(paramAnonymousInt);
    }
  };

  private void checkDefaultLocale()
  {
    Locale localLocale = this.mTts.getDefaultLanguage();
    if (localLocale == null)
    {
      Log.e("TextToSpeechSettings", "Failed to get default language from engine " + this.mCurrentEngine);
      updateWidgetState(false);
      updateEngineStatus(2131428845);
    }
    do
    {
      return;
      this.mCurrentDefaultLocale = localLocale;
      this.mTts.setLanguage(localLocale);
    }
    while (!evaluateDefaultLocale());
    getSampleText();
  }

  private void checkVoiceData(String paramString)
  {
    Intent localIntent = new Intent("android.speech.tts.engine.CHECK_TTS_DATA");
    localIntent.setPackage(paramString);
    try
    {
      startActivityForResult(localIntent, 1977);
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Log.e("TextToSpeechSettings", "Failed to check TTS data, no activity found for " + localIntent + ")");
    }
  }

  private void displayNetworkAlert()
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getActivity());
    localBuilder.setTitle(17039380);
    localBuilder.setIconAttribute(16843605);
    localBuilder.setMessage(getActivity().getString(2131428840));
    localBuilder.setCancelable(false);
    localBuilder.setPositiveButton(17039370, null);
    localBuilder.create().show();
  }

  private boolean evaluateDefaultLocale()
  {
    if ((this.mCurrentDefaultLocale == null) || (this.mAvailableStrLocals == null))
      return false;
    int i = this.mTts.setLanguage(this.mCurrentDefaultLocale);
    String str = this.mCurrentDefaultLocale.getISO3Language();
    int j = 1;
    if (!TextUtils.isEmpty(this.mCurrentDefaultLocale.getISO3Country()))
      str = str + "-" + this.mCurrentDefaultLocale.getISO3Country();
    if (!TextUtils.isEmpty(this.mCurrentDefaultLocale.getVariant()))
      str = str + "-" + this.mCurrentDefaultLocale.getVariant();
    Iterator localIterator = this.mAvailableStrLocals.iterator();
    while (localIterator.hasNext())
      if (((String)localIterator.next()).equalsIgnoreCase(str))
        j = 0;
    if ((i == -2) || (i == -1) || (j != 0))
    {
      updateEngineStatus(2131428845);
      updateWidgetState(false);
      return false;
    }
    if (isNetworkRequiredForSynthesis())
      updateEngineStatus(2131428844);
    while (true)
    {
      updateWidgetState(true);
      return true;
      updateEngineStatus(2131428843);
    }
  }

  private String getDefaultSampleString()
  {
    if ((this.mTts != null) && (this.mTts.getLanguage() != null))
    {
      String str = this.mTts.getLanguage().getISO3Language();
      String[] arrayOfString1 = getActivity().getResources().getStringArray(2131165198);
      String[] arrayOfString2 = getActivity().getResources().getStringArray(2131165199);
      for (int i = 0; i < arrayOfString1.length; i++)
        if (arrayOfString2[i].equals(str))
          return arrayOfString1[i];
    }
    return getString(2131428841);
  }

  private void getSampleText()
  {
    String str = this.mTts.getCurrentEngine();
    if (TextUtils.isEmpty(str))
      str = this.mTts.getDefaultEngine();
    Intent localIntent = new Intent("android.speech.tts.engine.GET_SAMPLE_TEXT");
    localIntent.putExtra("language", this.mCurrentDefaultLocale.getLanguage());
    localIntent.putExtra("country", this.mCurrentDefaultLocale.getCountry());
    localIntent.putExtra("variant", this.mCurrentDefaultLocale.getVariant());
    localIntent.setPackage(str);
    try
    {
      startActivityForResult(localIntent, 1983);
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Log.e("TextToSpeechSettings", "Failed to get sample text, no activity found for " + localIntent + ")");
    }
  }

  private void initSettings()
  {
    ContentResolver localContentResolver = getContentResolver();
    try
    {
      this.mDefaultRate = Settings.Secure.getInt(localContentResolver, "tts_default_rate");
      this.mDefaultRatePref.setValue(String.valueOf(this.mDefaultRate));
      this.mDefaultRatePref.setOnPreferenceChangeListener(this);
      this.mCurrentEngine = this.mTts.getCurrentEngine();
      if ((getActivity() instanceof PreferenceActivity))
      {
        PreferenceActivity localPreferenceActivity = (PreferenceActivity)getActivity();
        this.mEnginePreferenceCategory.removeAll();
        Iterator localIterator = this.mEnginesHelper.getEngines().iterator();
        while (localIterator.hasNext())
        {
          TextToSpeech.EngineInfo localEngineInfo = (TextToSpeech.EngineInfo)localIterator.next();
          TtsEnginePreference localTtsEnginePreference = new TtsEnginePreference(getActivity(), localEngineInfo, this, localPreferenceActivity);
          this.mEnginePreferenceCategory.addPreference(localTtsEnginePreference);
        }
      }
    }
    catch (Settings.SettingNotFoundException localSettingNotFoundException)
    {
      while (true)
        this.mDefaultRate = 100;
    }
    throw new IllegalStateException("TextToSpeechSettings used outside a PreferenceActivity");
    checkVoiceData(this.mCurrentEngine);
  }

  private boolean isNetworkRequiredForSynthesis()
  {
    Set localSet = this.mTts.getFeatures(this.mCurrentDefaultLocale);
    if (localSet == null);
    while ((!localSet.contains("networkTts")) || (localSet.contains("embeddedTts")))
      return false;
    return true;
  }

  private void onSampleTextReceived(int paramInt, Intent paramIntent)
  {
    String str = getDefaultSampleString();
    if ((paramInt == 0) && (paramIntent != null) && (paramIntent != null) && (paramIntent.getStringExtra("sampleText") != null))
      str = paramIntent.getStringExtra("sampleText");
    this.mSampleText = str;
    if (this.mSampleText != null)
    {
      updateWidgetState(true);
      return;
    }
    Log.e("TextToSpeechSettings", "Did not have a sample string for the requested language. Using default");
  }

  private void onVoiceDataIntegrityCheckDone(Intent paramIntent)
  {
    String str = this.mTts.getCurrentEngine();
    if (str == null)
      Log.e("TextToSpeechSettings", "Voice data check complete, but no engine bound");
    while (true)
    {
      return;
      if (paramIntent == null)
      {
        Log.e("TextToSpeechSettings", "Engine failed voice data integrity check (null return)" + this.mTts.getCurrentEngine());
        return;
      }
      Settings.Secure.putString(getContentResolver(), "tts_default_synth", str);
      this.mAvailableStrLocals = paramIntent.getStringArrayListExtra("availableVoices");
      if (this.mAvailableStrLocals == null)
      {
        Log.e("TextToSpeechSettings", "Voice data check complete, but no available voices found");
        this.mAvailableStrLocals = new ArrayList();
      }
      if (evaluateDefaultLocale())
        getSampleText();
      int i = this.mEnginePreferenceCategory.getPreferenceCount();
      for (int j = 0; j < i; j++)
      {
        Preference localPreference = this.mEnginePreferenceCategory.getPreference(j);
        if ((localPreference instanceof TtsEnginePreference))
        {
          TtsEnginePreference localTtsEnginePreference = (TtsEnginePreference)localPreference;
          if (localTtsEnginePreference.getKey().equals(str))
          {
            localTtsEnginePreference.setVoiceDataDetails(paramIntent);
            return;
          }
        }
      }
    }
  }

  private void setTtsUtteranceProgressListener()
  {
    if (this.mTts == null)
      return;
    this.mTts.setOnUtteranceProgressListener(new UtteranceProgressListener()
    {
      public void onDone(String paramAnonymousString)
      {
      }

      public void onError(String paramAnonymousString)
      {
        Log.e("TextToSpeechSettings", "Error while trying to synthesize sample text");
      }

      public void onStart(String paramAnonymousString)
      {
      }
    });
  }

  private void speakSampleText()
  {
    boolean bool = isNetworkRequiredForSynthesis();
    if ((!bool) || ((bool) && (this.mTts.isLanguageAvailable(this.mCurrentDefaultLocale) >= 0)))
    {
      HashMap localHashMap = new HashMap();
      localHashMap.put("utteranceId", "Sample");
      this.mTts.speak(this.mSampleText, 0, localHashMap);
      return;
    }
    Log.w("TextToSpeechSettings", "Network required for sample synthesis for requested language");
    displayNetworkAlert();
  }

  private void updateDefaultEngine(String paramString)
  {
    updateWidgetState(false);
    updateEngineStatus(2131428846);
    this.mPreviousEngine = this.mTts.getCurrentEngine();
    if (this.mTts != null);
    try
    {
      this.mTts.shutdown();
      this.mTts = null;
      this.mTts = new TextToSpeech(getActivity().getApplicationContext(), this.mUpdateListener, paramString);
      setTtsUtteranceProgressListener();
      return;
    }
    catch (Exception localException)
    {
      while (true)
        Log.e("TextToSpeechSettings", "Error shutting down TTS engine" + localException);
    }
  }

  private void updateEngineStatus(int paramInt)
  {
    Locale localLocale = this.mCurrentDefaultLocale;
    if (localLocale == null)
      localLocale = Locale.getDefault();
    Preference localPreference = this.mEngineStatus;
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = localLocale.getDisplayName();
    localPreference.setSummary(getString(paramInt, arrayOfObject));
  }

  private void updateWidgetState(boolean paramBoolean)
  {
    this.mPlayExample.setEnabled(paramBoolean);
    this.mDefaultRatePref.setEnabled(paramBoolean);
    this.mEngineStatus.setEnabled(paramBoolean);
  }

  public Checkable getCurrentChecked()
  {
    return this.mCurrentChecked;
  }

  public String getCurrentKey()
  {
    return this.mCurrentEngine;
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt1 == 1983)
      onSampleTextReceived(paramInt2, paramIntent);
    while (paramInt1 != 1977)
      return;
    onVoiceDataIntegrityCheckDone(paramIntent);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131034166);
    getActivity().setVolumeControlStream(3);
    this.mPlayExample = findPreference("tts_play_example");
    this.mPlayExample.setOnPreferenceClickListener(this);
    this.mPlayExample.setEnabled(false);
    this.mEnginePreferenceCategory = ((PreferenceCategory)findPreference("tts_engine_preference_section"));
    this.mDefaultRatePref = ((ListPreference)findPreference("tts_default_rate"));
    this.mEngineStatus = findPreference("tts_status");
    updateEngineStatus(2131428846);
    this.mTts = new TextToSpeech(getActivity().getApplicationContext(), this.mInitListener);
    this.mEnginesHelper = new TtsEngines(getActivity().getApplicationContext());
    setTtsUtteranceProgressListener();
    initSettings();
  }

  public void onDestroy()
  {
    super.onDestroy();
    if (this.mTts != null)
    {
      this.mTts.shutdown();
      this.mTts = null;
    }
  }

  public void onInitEngine(int paramInt)
  {
    if (paramInt == 0)
    {
      checkDefaultLocale();
      return;
    }
    updateWidgetState(false);
  }

  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if ("tts_default_rate".equals(paramPreference.getKey()))
      this.mDefaultRate = Integer.parseInt((String)paramObject);
    try
    {
      Settings.Secure.putInt(getContentResolver(), "tts_default_rate", this.mDefaultRate);
      if (this.mTts != null)
        this.mTts.setSpeechRate(this.mDefaultRate / 100.0F);
      return true;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      while (true)
        Log.e("TextToSpeechSettings", "could not persist default TTS rate setting", localNumberFormatException);
    }
  }

  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (paramPreference == this.mPlayExample)
    {
      speakSampleText();
      return true;
    }
    return false;
  }

  public void onResume()
  {
    super.onResume();
    if ((this.mTts == null) || (this.mCurrentDefaultLocale == null));
    Locale localLocale;
    do
    {
      return;
      localLocale = this.mTts.getDefaultLanguage();
    }
    while ((this.mCurrentDefaultLocale == null) || (this.mCurrentDefaultLocale.equals(localLocale)));
    updateWidgetState(false);
    checkDefaultLocale();
  }

  public void onUpdateEngine(int paramInt)
  {
    if (paramInt == 0)
    {
      checkVoiceData(this.mTts.getCurrentEngine());
      return;
    }
    if (this.mPreviousEngine != null)
    {
      this.mTts = new TextToSpeech(getActivity().getApplicationContext(), this.mInitListener, this.mPreviousEngine);
      setTtsUtteranceProgressListener();
    }
    this.mPreviousEngine = null;
  }

  public void setCurrentChecked(Checkable paramCheckable)
  {
    this.mCurrentChecked = paramCheckable;
  }

  public void setCurrentKey(String paramString)
  {
    this.mCurrentEngine = paramString;
    updateDefaultEngine(this.mCurrentEngine);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.tts.TextToSpeechSettings
 * JD-Core Version:    0.6.2
 */