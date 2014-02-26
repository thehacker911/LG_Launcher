package com.android.settings;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.Settings.Secure;
import android.speech.tts.TtsEngines;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.android.internal.R.styleable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class VoiceInputOutputSettings
  implements Preference.OnPreferenceChangeListener
{
  private HashMap<String, ResolveInfo> mAvailableRecognizersMap;
  private final SettingsPreferenceFragment mFragment;
  private PreferenceGroup mParent;
  private ListPreference mRecognizerPref;
  private Preference mRecognizerSettingsPref;
  private PreferenceScreen mSettingsPref;
  private final TtsEngines mTtsEngines;
  private Preference mTtsSettingsPref;
  private PreferenceCategory mVoiceCategory;

  public VoiceInputOutputSettings(SettingsPreferenceFragment paramSettingsPreferenceFragment)
  {
    this.mFragment = paramSettingsPreferenceFragment;
    this.mTtsEngines = new TtsEngines(paramSettingsPreferenceFragment.getPreferenceScreen().getContext());
  }

  private void populateOrRemovePreferences()
  {
    boolean bool1 = populateOrRemoveRecognizerPrefs();
    boolean bool2 = populateOrRemoveTtsPrefs();
    if ((!bool1) && (!bool2))
      this.mFragment.getPreferenceScreen().removePreference(this.mVoiceCategory);
  }

  private boolean populateOrRemoveRecognizerPrefs()
  {
    List localList = this.mFragment.getPackageManager().queryIntentServices(new Intent("android.speech.RecognitionService"), 128);
    int i = localList.size();
    if (i == 0)
    {
      this.mVoiceCategory.removePreference(this.mRecognizerPref);
      this.mVoiceCategory.removePreference(this.mRecognizerSettingsPref);
      return false;
    }
    if (i == 1)
    {
      this.mVoiceCategory.removePreference(this.mRecognizerPref);
      ResolveInfo localResolveInfo = (ResolveInfo)localList.get(0);
      String str = new ComponentName(localResolveInfo.serviceInfo.packageName, localResolveInfo.serviceInfo.name).flattenToShortString();
      this.mAvailableRecognizersMap.put(str, localResolveInfo);
      updateSettingsLink(Settings.Secure.getString(this.mFragment.getContentResolver(), "voice_recognition_service"));
    }
    while (true)
    {
      return true;
      populateRecognizerPreference(localList);
    }
  }

  private boolean populateOrRemoveTtsPrefs()
  {
    if (this.mTtsEngines.getEngines().isEmpty())
    {
      this.mVoiceCategory.removePreference(this.mTtsSettingsPref);
      return false;
    }
    return true;
  }

  private void populateRecognizerPreference(List<ResolveInfo> paramList)
  {
    int i = paramList.size();
    CharSequence[] arrayOfCharSequence1 = new CharSequence[i];
    CharSequence[] arrayOfCharSequence2 = new CharSequence[i];
    String str1 = Settings.Secure.getString(this.mFragment.getContentResolver(), "voice_recognition_service");
    for (int j = 0; j < i; j++)
    {
      ResolveInfo localResolveInfo = (ResolveInfo)paramList.get(j);
      String str2 = new ComponentName(localResolveInfo.serviceInfo.packageName, localResolveInfo.serviceInfo.name).flattenToShortString();
      this.mAvailableRecognizersMap.put(str2, localResolveInfo);
      arrayOfCharSequence1[j] = localResolveInfo.loadLabel(this.mFragment.getPackageManager());
      arrayOfCharSequence2[j] = str2;
    }
    this.mRecognizerPref.setEntries(arrayOfCharSequence1);
    this.mRecognizerPref.setEntryValues(arrayOfCharSequence2);
    this.mRecognizerPref.setDefaultValue(str1);
    this.mRecognizerPref.setValue(str1);
    updateSettingsLink(str1);
  }

  private void updateSettingsLink(String paramString)
  {
    ResolveInfo localResolveInfo = (ResolveInfo)this.mAvailableRecognizersMap.get(paramString);
    if (localResolveInfo == null)
      return;
    ServiceInfo localServiceInfo = localResolveInfo.serviceInfo;
    XmlResourceParser localXmlResourceParser = null;
    String str = null;
    try
    {
      localXmlResourceParser = localServiceInfo.loadXmlMetaData(this.mFragment.getPackageManager(), "android.speech");
      str = null;
      if (localXmlResourceParser == null)
        throw new XmlPullParserException("No android.speech meta-data for " + localServiceInfo.packageName);
    }
    catch (XmlPullParserException localXmlPullParserException)
    {
      Log.e("VoiceInputOutputSettings", "error parsing recognition service meta-data", localXmlPullParserException);
      if (localXmlResourceParser != null)
        localXmlResourceParser.close();
      if (str == null)
      {
        Log.w("VoiceInputOutputSettings", "no recognizer settings available for " + localServiceInfo.packageName);
        this.mSettingsPref.setIntent(null);
        this.mVoiceCategory.removePreference(this.mSettingsPref);
        return;
        localResources = this.mFragment.getPackageManager().getResourcesForApplication(localServiceInfo.applicationInfo);
        localAttributeSet = Xml.asAttributeSet(localXmlResourceParser);
        int i;
        do
          i = localXmlResourceParser.next();
        while ((i != 1) && (i != 2));
        boolean bool = "recognition-service".equals(localXmlResourceParser.getName());
        str = null;
        if (!bool)
          throw new XmlPullParserException("Meta-data does not start with recognition-service tag");
      }
    }
    catch (IOException localIOException)
    {
      while (true)
      {
        Resources localResources;
        AttributeSet localAttributeSet;
        Log.e("VoiceInputOutputSettings", "error parsing recognition service meta-data", localIOException);
        if (localXmlResourceParser != null)
        {
          localXmlResourceParser.close();
          continue;
          TypedArray localTypedArray = localResources.obtainAttributes(localAttributeSet, R.styleable.RecognitionService);
          str = localTypedArray.getString(0);
          localTypedArray.recycle();
          if (localXmlResourceParser != null)
            localXmlResourceParser.close();
        }
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      while (true)
      {
        Log.e("VoiceInputOutputSettings", "error parsing recognition service meta-data", localNameNotFoundException);
        if (localXmlResourceParser != null)
          localXmlResourceParser.close();
      }
    }
    finally
    {
      if (localXmlResourceParser != null)
        localXmlResourceParser.close();
    }
    Intent localIntent = new Intent("android.intent.action.MAIN");
    localIntent.setComponent(new ComponentName(localServiceInfo.packageName, str));
    this.mSettingsPref.setIntent(localIntent);
    this.mRecognizerPref.setSummary(localResolveInfo.loadLabel(this.mFragment.getPackageManager()));
  }

  public void onCreate()
  {
    this.mParent = this.mFragment.getPreferenceScreen();
    this.mVoiceCategory = ((PreferenceCategory)this.mParent.findPreference("voice_category"));
    this.mRecognizerPref = ((ListPreference)this.mVoiceCategory.findPreference("recognizer"));
    this.mRecognizerSettingsPref = this.mVoiceCategory.findPreference("recognizer_settings");
    this.mTtsSettingsPref = this.mVoiceCategory.findPreference("tts_settings");
    this.mRecognizerPref.setOnPreferenceChangeListener(this);
    this.mSettingsPref = ((PreferenceScreen)this.mVoiceCategory.findPreference("recognizer_settings"));
    this.mAvailableRecognizersMap = new HashMap();
    populateOrRemovePreferences();
  }

  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if (paramPreference == this.mRecognizerPref)
    {
      String str = (String)paramObject;
      Settings.Secure.putString(this.mFragment.getContentResolver(), "voice_recognition_service", str);
      updateSettingsLink(str);
    }
    return true;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.VoiceInputOutputSettings
 * JD-Core Version:    0.6.2
 */