package com.android.settings.fuelgauge;

import android.content.Context;

public class Utils
{
  public static String formatElapsedTime(Context paramContext, double paramDouble, boolean paramBoolean)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = (int)Math.floor(paramDouble / 1000.0D);
    if (!paramBoolean)
      i += 30;
    int j = 0;
    if (i > 86400)
    {
      j = i / 86400;
      i -= j * 86400;
    }
    int k = 0;
    if (i > 3600)
    {
      k = i / 3600;
      i -= k * 3600;
    }
    int m = 0;
    if (i > 60)
    {
      m = i / 60;
      i -= m * 60;
    }
    if (paramBoolean)
      if (j > 0)
      {
        Object[] arrayOfObject7 = new Object[4];
        arrayOfObject7[0] = Integer.valueOf(j);
        arrayOfObject7[1] = Integer.valueOf(k);
        arrayOfObject7[2] = Integer.valueOf(m);
        arrayOfObject7[3] = Integer.valueOf(i);
        localStringBuilder.append(paramContext.getString(2131428616, arrayOfObject7));
      }
    while (true)
    {
      return localStringBuilder.toString();
      if (k > 0)
      {
        Object[] arrayOfObject6 = new Object[3];
        arrayOfObject6[0] = Integer.valueOf(k);
        arrayOfObject6[1] = Integer.valueOf(m);
        arrayOfObject6[2] = Integer.valueOf(i);
        localStringBuilder.append(paramContext.getString(2131428617, arrayOfObject6));
      }
      else if (m > 0)
      {
        Object[] arrayOfObject5 = new Object[2];
        arrayOfObject5[0] = Integer.valueOf(m);
        arrayOfObject5[1] = Integer.valueOf(i);
        localStringBuilder.append(paramContext.getString(2131428618, arrayOfObject5));
      }
      else
      {
        Object[] arrayOfObject4 = new Object[1];
        arrayOfObject4[0] = Integer.valueOf(i);
        localStringBuilder.append(paramContext.getString(2131428619, arrayOfObject4));
        continue;
        if (j > 0)
        {
          Object[] arrayOfObject3 = new Object[3];
          arrayOfObject3[0] = Integer.valueOf(j);
          arrayOfObject3[1] = Integer.valueOf(k);
          arrayOfObject3[2] = Integer.valueOf(m);
          localStringBuilder.append(paramContext.getString(2131428620, arrayOfObject3));
        }
        else if (k > 0)
        {
          Object[] arrayOfObject2 = new Object[2];
          arrayOfObject2[0] = Integer.valueOf(k);
          arrayOfObject2[1] = Integer.valueOf(m);
          localStringBuilder.append(paramContext.getString(2131428621, arrayOfObject2));
        }
        else
        {
          Object[] arrayOfObject1 = new Object[1];
          arrayOfObject1[0] = Integer.valueOf(m);
          localStringBuilder.append(paramContext.getString(2131428622, arrayOfObject1));
        }
      }
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.fuelgauge.Utils
 * JD-Core Version:    0.6.2
 */