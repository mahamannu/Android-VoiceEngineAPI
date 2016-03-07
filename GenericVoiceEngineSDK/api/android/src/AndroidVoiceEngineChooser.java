/**
 *
 * AndroidVoiceEngineChooser.java
 *
 * Create on 9/3/13
 * Author: <A HREF="mailto:nathan@atheerlabs.com">Nathan Abercrombie</A>
 *
 * Copyright (C) 2013 Atheer Labs, Inc. All rights reserved.
 *     ATHEER PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package atheer.android.voice;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;

import java.util.List;

public class AndroidVoiceEngineChooser
{



    public static Class getOptimumVoiceEngine(Context context)
    {
        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> speechActivities = pm.queryIntentActivities(speechIntent, 0);
        if (speechActivities.size() != 0) {
        	/* Google Voice Engine is available on this device and will be used for SpeechToText. This
        	 * will be true for Emulator environments where developers are using the Atheer API on an
        	 * Android ( Google Certified ) tablet.  */
            return atheer.android.voice.google.GoogleVoiceEngine.class;
        }
        else {
        	/* Developers can use Nuance for Android provided by Atheer, under a Dev License
        	 * Developers would need to manually include .so ( for now )  in the libs directory
        	 * needed by NuanceForAndroid to work
        	 */
            return atheer.android.voice.nuance.NuanceVoiceEngine.class;
        }

    }

}
