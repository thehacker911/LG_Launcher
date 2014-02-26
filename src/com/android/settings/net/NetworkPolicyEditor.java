package com.android.settings.net;

import android.net.NetworkPolicy;
import android.net.NetworkPolicyManager;
import android.net.NetworkTemplate;
import android.net.wifi.WifiInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.text.format.Time;
import com.android.internal.util.Objects;
import com.android.internal.util.Preconditions;
import com.google.android.collect.Lists;
import com.google.android.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class NetworkPolicyEditor
{
  private ArrayList<NetworkPolicy> mPolicies = Lists.newArrayList();
  private NetworkPolicyManager mPolicyManager;

  public NetworkPolicyEditor(NetworkPolicyManager paramNetworkPolicyManager)
  {
    this.mPolicyManager = ((NetworkPolicyManager)Preconditions.checkNotNull(paramNetworkPolicyManager));
  }

  @Deprecated
  private static NetworkPolicy buildDefaultPolicy(NetworkTemplate paramNetworkTemplate)
  {
    int i;
    String str;
    if (paramNetworkTemplate.getMatchRule() == 4)
    {
      i = -1;
      str = "UTC";
    }
    for (boolean bool = false; ; bool = true)
    {
      return new NetworkPolicy(paramNetworkTemplate, i, str, -1L, -1L, -1L, -1L, bool, true);
      Time localTime = new Time();
      localTime.setToNow();
      i = localTime.monthDay;
      str = localTime.timezone;
    }
  }

  private static NetworkTemplate buildUnquotedNetworkTemplate(NetworkTemplate paramNetworkTemplate)
  {
    if (paramNetworkTemplate == null);
    String str1;
    String str2;
    do
    {
      return null;
      str1 = paramNetworkTemplate.getNetworkId();
      str2 = WifiInfo.removeDoubleQuotes(str1);
    }
    while (TextUtils.equals(str2, str1));
    return new NetworkTemplate(paramNetworkTemplate.getMatchRule(), paramNetworkTemplate.getSubscriberId(), str2);
  }

  private boolean forceMobilePolicyCombined()
  {
    HashSet localHashSet = Sets.newHashSet();
    Iterator localIterator1 = this.mPolicies.iterator();
    while (localIterator1.hasNext())
      localHashSet.add(((NetworkPolicy)localIterator1.next()).template.getSubscriberId());
    boolean bool = false;
    Iterator localIterator2 = localHashSet.iterator();
    while (localIterator2.hasNext())
      bool |= setMobilePolicySplitInternal((String)localIterator2.next(), false);
    return bool;
  }

  @Deprecated
  private boolean setMobilePolicySplitInternal(String paramString, boolean paramBoolean)
  {
    boolean bool = isMobilePolicySplit(paramString);
    NetworkTemplate localNetworkTemplate1 = NetworkTemplate.buildTemplateMobile3gLower(paramString);
    NetworkTemplate localNetworkTemplate2 = NetworkTemplate.buildTemplateMobile4g(paramString);
    NetworkTemplate localNetworkTemplate3 = NetworkTemplate.buildTemplateMobileAll(paramString);
    if (paramBoolean == bool)
      return false;
    if ((bool) && (!paramBoolean))
    {
      NetworkPolicy localNetworkPolicy2 = getPolicy(localNetworkTemplate1);
      NetworkPolicy localNetworkPolicy3 = getPolicy(localNetworkTemplate2);
      if (localNetworkPolicy2.compareTo(localNetworkPolicy3) < 0);
      for (NetworkPolicy localNetworkPolicy4 = localNetworkPolicy2; ; localNetworkPolicy4 = localNetworkPolicy3)
      {
        this.mPolicies.remove(localNetworkPolicy2);
        this.mPolicies.remove(localNetworkPolicy3);
        this.mPolicies.add(new NetworkPolicy(localNetworkTemplate3, localNetworkPolicy4.cycleDay, localNetworkPolicy4.cycleTimezone, localNetworkPolicy4.warningBytes, localNetworkPolicy4.limitBytes, -1L, -1L, localNetworkPolicy4.metered, localNetworkPolicy4.inferred));
        return true;
      }
    }
    if ((!bool) && (paramBoolean))
    {
      NetworkPolicy localNetworkPolicy1 = getPolicy(localNetworkTemplate3);
      this.mPolicies.remove(localNetworkPolicy1);
      this.mPolicies.add(new NetworkPolicy(localNetworkTemplate1, localNetworkPolicy1.cycleDay, localNetworkPolicy1.cycleTimezone, localNetworkPolicy1.warningBytes, localNetworkPolicy1.limitBytes, -1L, -1L, localNetworkPolicy1.metered, localNetworkPolicy1.inferred));
      this.mPolicies.add(new NetworkPolicy(localNetworkTemplate2, localNetworkPolicy1.cycleDay, localNetworkPolicy1.cycleTimezone, localNetworkPolicy1.warningBytes, localNetworkPolicy1.limitBytes, -1L, -1L, localNetworkPolicy1.metered, localNetworkPolicy1.inferred));
      return true;
    }
    return false;
  }

  public NetworkPolicy getOrCreatePolicy(NetworkTemplate paramNetworkTemplate)
  {
    NetworkPolicy localNetworkPolicy = getPolicy(paramNetworkTemplate);
    if (localNetworkPolicy == null)
    {
      localNetworkPolicy = buildDefaultPolicy(paramNetworkTemplate);
      this.mPolicies.add(localNetworkPolicy);
    }
    return localNetworkPolicy;
  }

  public NetworkPolicy getPolicy(NetworkTemplate paramNetworkTemplate)
  {
    Iterator localIterator = this.mPolicies.iterator();
    while (localIterator.hasNext())
    {
      NetworkPolicy localNetworkPolicy = (NetworkPolicy)localIterator.next();
      if (localNetworkPolicy.template.equals(paramNetworkTemplate))
        return localNetworkPolicy;
    }
    return null;
  }

  public int getPolicyCycleDay(NetworkTemplate paramNetworkTemplate)
  {
    return getPolicy(paramNetworkTemplate).cycleDay;
  }

  public long getPolicyLimitBytes(NetworkTemplate paramNetworkTemplate)
  {
    return getPolicy(paramNetworkTemplate).limitBytes;
  }

  public NetworkPolicy getPolicyMaybeUnquoted(NetworkTemplate paramNetworkTemplate)
  {
    NetworkPolicy localNetworkPolicy = getPolicy(paramNetworkTemplate);
    if (localNetworkPolicy != null)
      return localNetworkPolicy;
    return getPolicy(buildUnquotedNetworkTemplate(paramNetworkTemplate));
  }

  public long getPolicyWarningBytes(NetworkTemplate paramNetworkTemplate)
  {
    return getPolicy(paramNetworkTemplate).warningBytes;
  }

  @Deprecated
  public boolean isMobilePolicySplit(String paramString)
  {
    int i = 0;
    int j = 0;
    Iterator localIterator = this.mPolicies.iterator();
    while (localIterator.hasNext())
    {
      NetworkTemplate localNetworkTemplate = ((NetworkPolicy)localIterator.next()).template;
      if (Objects.equal(paramString, localNetworkTemplate.getSubscriberId()))
        switch (localNetworkTemplate.getMatchRule())
        {
        default:
          break;
        case 2:
          i = 1;
          break;
        case 3:
          j = 1;
        }
    }
    return (i != 0) && (j != 0);
  }

  public void read()
  {
    NetworkPolicy[] arrayOfNetworkPolicy = this.mPolicyManager.getNetworkPolicies();
    int i = 0;
    this.mPolicies.clear();
    int j = arrayOfNetworkPolicy.length;
    for (int k = 0; k < j; k++)
    {
      NetworkPolicy localNetworkPolicy = arrayOfNetworkPolicy[k];
      if (localNetworkPolicy.limitBytes < -1L)
      {
        localNetworkPolicy.limitBytes = -1L;
        i = 1;
      }
      if (localNetworkPolicy.warningBytes < -1L)
      {
        localNetworkPolicy.warningBytes = -1L;
        i = 1;
      }
      this.mPolicies.add(localNetworkPolicy);
    }
    if ((i | forceMobilePolicyCombined()) != 0)
      writeAsync();
  }

  @Deprecated
  public void setMobilePolicySplit(String paramString, boolean paramBoolean)
  {
    if (setMobilePolicySplitInternal(paramString, paramBoolean))
      writeAsync();
  }

  public void setPolicyCycleDay(NetworkTemplate paramNetworkTemplate, int paramInt, String paramString)
  {
    NetworkPolicy localNetworkPolicy = getOrCreatePolicy(paramNetworkTemplate);
    localNetworkPolicy.cycleDay = paramInt;
    localNetworkPolicy.cycleTimezone = paramString;
    localNetworkPolicy.inferred = false;
    localNetworkPolicy.clearSnooze();
    writeAsync();
  }

  public void setPolicyLimitBytes(NetworkTemplate paramNetworkTemplate, long paramLong)
  {
    NetworkPolicy localNetworkPolicy = getOrCreatePolicy(paramNetworkTemplate);
    localNetworkPolicy.limitBytes = paramLong;
    localNetworkPolicy.inferred = false;
    localNetworkPolicy.clearSnooze();
    writeAsync();
  }

  public void setPolicyMetered(NetworkTemplate paramNetworkTemplate, boolean paramBoolean)
  {
    NetworkPolicy localNetworkPolicy1 = getPolicy(paramNetworkTemplate);
    int i;
    if (paramBoolean)
      if (localNetworkPolicy1 == null)
      {
        NetworkPolicy localNetworkPolicy3 = buildDefaultPolicy(paramNetworkTemplate);
        localNetworkPolicy3.metered = true;
        localNetworkPolicy3.inferred = false;
        this.mPolicies.add(localNetworkPolicy3);
        i = 1;
      }
    while (true)
    {
      NetworkPolicy localNetworkPolicy2 = getPolicy(buildUnquotedNetworkTemplate(paramNetworkTemplate));
      if (localNetworkPolicy2 != null)
      {
        this.mPolicies.remove(localNetworkPolicy2);
        i = 1;
      }
      if (i != 0)
        writeAsync();
      return;
      boolean bool2 = localNetworkPolicy1.metered;
      i = 0;
      if (!bool2)
      {
        localNetworkPolicy1.metered = true;
        localNetworkPolicy1.inferred = false;
        i = 1;
        continue;
        i = 0;
        if (localNetworkPolicy1 != null)
        {
          boolean bool1 = localNetworkPolicy1.metered;
          i = 0;
          if (bool1)
          {
            localNetworkPolicy1.metered = false;
            localNetworkPolicy1.inferred = false;
            i = 1;
          }
        }
      }
    }
  }

  public void setPolicyWarningBytes(NetworkTemplate paramNetworkTemplate, long paramLong)
  {
    NetworkPolicy localNetworkPolicy = getOrCreatePolicy(paramNetworkTemplate);
    localNetworkPolicy.warningBytes = paramLong;
    localNetworkPolicy.inferred = false;
    localNetworkPolicy.clearSnooze();
    writeAsync();
  }

  public void write(NetworkPolicy[] paramArrayOfNetworkPolicy)
  {
    this.mPolicyManager.setNetworkPolicies(paramArrayOfNetworkPolicy);
  }

  public void writeAsync()
  {
    new AsyncTask()
    {
      protected Void doInBackground(Void[] paramAnonymousArrayOfVoid)
      {
        NetworkPolicyEditor.this.write(this.val$policies);
        return null;
      }
    }
    .execute(new Void[0]);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.net.NetworkPolicyEditor
 * JD-Core Version:    0.6.2
 */