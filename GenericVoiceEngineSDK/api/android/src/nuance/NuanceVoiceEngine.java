/**
 * 	NuanceVoiceEngine.java
 *
 *
 */

package GenericVoiceSDK.android.voice.nuance;

import android.util.Log;
import GenericVoiceSDK.android.core.CoreAndroid;
import GenericVoiceSDK.core.Core;
import GenericVoiceSDK.voice.VoiceToTextEngine;
import GenericVoiceSDK.voice.VoiceToTextListener;
import com.nuance.nmdp.speechkit.Prompt;
import com.nuance.nmdp.speechkit.Recognition;
import com.nuance.nmdp.speechkit.Recognizer;
import com.nuance.nmdp.speechkit.SpeechError;
import com.nuance.nmdp.speechkit.SpeechKit;

import android.content.Context;

import java.util.LinkedList;

public class NuanceVoiceEngine implements VoiceToTextEngine
{
    private static final boolean DEBUG = true;
    private static final String TAG = "NuanceVoiceEngine";

    private Context mContext;
    private SpeechKit mSpeechKit;
    private NuanceRecognizerListener mRecognizerListener;
    private Recognizer mRecognizer;
    private boolean mDestroyed;
    private boolean mIsRecording = false;

    private VoiceToTextListener mListener;
    private LinkedList<VoiceToTextListener> mVoiceQueue = new LinkedList<VoiceToTextListener>();

    public void initialize(Core core)
    {
        mContext = ((CoreAndroid) core).getContext();
        mSpeechKit = SpeechKit.initialize(mContext.getApplicationContext(),
                AppInfo.SpeechKitAppId, AppInfo.SpeechKitServer,
                AppInfo.SpeechKitPort, AppInfo.SpeechKitSsl,
                AppInfo.SpeechKitApplicationKey);
        mSpeechKit.connect();
        // Prompt beep = mSpeechKit.defineAudioPrompt(R.raw.beep);
        mSpeechKit.setDefaultRecognizerPrompts(null, Prompt.vibration(100),
                null, null);
        mRecognizerListener = new NuanceRecognizerListener();

    }


    @Override
    public void startVoiceToText(VoiceToTextListener voiceToTextListener)
    {
        if (mListener == null)
        {
            mListener = voiceToTextListener;
            startRecognition();
        } else
        {
            mVoiceQueue.add(voiceToTextListener);
        }
    }


    @Override
    public void releaseResources()
    {
        if (mSpeechKit != null)
        {
            mSpeechKit.release();
            mSpeechKit = null;
        }
        mDestroyed = true;
        if (mRecognizer != null)
        {
            mRecognizer.cancel();
            mRecognizer = null;
        }

    }

    private void startRecognition()
    {
        mRecognizer = mSpeechKit.createRecognizer(
                Recognizer.RecognizerType.Dictation,
                Recognizer.EndOfSpeechDetection.Long, "en_US", mRecognizerListener, null);
        mRecognizer.start();

    }

    private class NuanceRecognizerListener implements Recognizer.Listener
    {

        @Override
        public void onRecordingBegin(Recognizer recognizer)
        {

            Log.d(TAG, "onRecordingBegin");
            mIsRecording = true;
            mListener.onRecordingStarted();

        }

        @Override
        public void onRecordingDone(Recognizer recognizer)
        {

            mIsRecording = false;
            // _listeningDialog.setStoppable(false);
            mListener.onRecordingStopped();

            mListener = null;
            if(!mVoiceQueue.isEmpty())
            {
                mListener = mVoiceQueue.remove();
                startRecognition();
            }
        }

        @Override
        public void onError(Recognizer recognizer, SpeechError error)
        {

            if (mRecognizer != null)
            {
                mRecognizer.cancel();
                mRecognizer = null;
            }

            mIsRecording = false;

            // Display the error + suggestion in the edit box
            String detail = error.getErrorDetail();
            String suggestion = error.getSuggestion();

            if (suggestion == null)
                suggestion = "";
            setResult(detail + "\n" + suggestion);

            mListener.onError(0); // TODO change from NuanceVoiceEngine error code to VoiceToText error code.

        }

        @Override
        public void onResults(Recognizer recognizer, Recognition results)
        {

            if (mRecognizer != null)
            {
                mRecognizer.cancel();
                mRecognizer = null;
            }

            mIsRecording = false;
            int count = results.getResultCount();
            Recognition.Result[] rs = new Recognition.Result[count];
            for (int i = 0; i < count; i++)
            {
                rs[i] = results.getResult(i);
            }
            setResult(rs[0].getText());

            mListener.onResult(rs[0].getText());
        }
    }


    private void setResult(String result)
    {
        Log.d(TAG, "Voice result = " + result);
    }


    private static class AppInfo
    {
        /**
         * The login parameters should be specified in the following manner:
         * <p/>
         * public static final String SpeechKitServer = "ndev.server.name";
         * <p/>
         * public static final int SpeechKitPort = 1000;
         * <p/>
         * public static final String SpeechKitAppId =
         * "ExampleSpeechKitSampleID";
         * <p/>
         * public static final byte[] SpeechKitApplicationKey = { (byte)0x38,
         * (byte)0x32, (byte)0x0e, (byte)0x46, (byte)0x4e, (byte)0x46,
         * (byte)0x12, (byte)0x5c, (byte)0x50, (byte)0x1d, (byte)0x4a,
         * (byte)0x39, (byte)0x4f, (byte)0x12, (byte)0x48, (byte)0x53,
         * (byte)0x3e, (byte)0x5b, (byte)0x31, (byte)0x22, (byte)0x5d,
         * (byte)0x4b, (byte)0x22, (byte)0x09, (byte)0x13, (byte)0x46,
         * (byte)0x61, (byte)0x19, (byte)0x1f, (byte)0x2d, (byte)0x13,
         * (byte)0x47, (byte)0x3d, (byte)0x58, (byte)0x30, (byte)0x29,
         * (byte)0x56, (byte)0x04, (byte)0x20, (byte)0x33, (byte)0x27,
         * (byte)0x0f, (byte)0x57, (byte)0x45, (byte)0x61, (byte)0x5f,
         * (byte)0x25, (byte)0x0d, (byte)0x48, (byte)0x21, (byte)0x2a,
         * (byte)0x62, (byte)0x46, (byte)0x64, (byte)0x54, (byte)0x4a,
         * (byte)0x10, (byte)0x36, (byte)0x4f, (byte)0x64 };
         * <p/>
         * Please note that all the specified values are non-functional and are
         * provided solely as an illustrative example.
         */

		/*
         * Please contact Nuance to receive the necessary connection and login
		 * parameters
		 */
        public static final String SpeechKitServer = "sandbox.nmdp.nuancemobility.net" /*
                                                                                         * Enter
																						 * your
																						 * server
																						 * here
																						 */;

        public static final int SpeechKitPort = 443 /* Enter your port here */;

        public static final boolean SpeechKitSsl = false;

        public static final String SpeechKitAppId = "NMDPTRIAL_goldeneye20130507215536"/*
                                                                                         * Enter
																						 * your
																						 * ID
																						 * here
																						 */;

        public static final byte[] SpeechKitApplicationKey = {
		/*
		 * Enter your application key here: (byte)0x00, (byte)0x01, ...
		 * (byte)0x00
		 */
                (byte) 0xef, (byte) 0x4f, (byte) 0xb5, (byte) 0x7a, (byte) 0x38,
                (byte) 0x74, (byte) 0x44, (byte) 0x04, (byte) 0x0b,
                (byte) 0x08, (byte) 0x92, (byte) 0x3a, (byte) 0xc2,
                (byte) 0xf8, (byte) 0x68, (byte) 0xcb, (byte) 0x77,
                (byte) 0x33, (byte) 0xab, (byte) 0xb1, (byte) 0x58,
                (byte) 0x44, (byte) 0x77, (byte) 0x7f, (byte) 0x94,
                (byte) 0xc9, (byte) 0xc6, (byte) 0xa9, (byte) 0x17,
                (byte) 0xd8, (byte) 0x8e, (byte) 0x06, (byte) 0x27,
                (byte) 0x43, (byte) 0x81, (byte) 0x60, (byte) 0xb7,
                (byte) 0xc5, (byte) 0x64, (byte) 0xc2, (byte) 0xcd,
                (byte) 0xf6, (byte) 0xfd, (byte) 0x8f, (byte) 0xef,
                (byte) 0x65, (byte) 0x5a, (byte) 0x66, (byte) 0x98,
                (byte) 0x06, (byte) 0x17, (byte) 0xf0, (byte) 0x4a,
                (byte) 0x8e, (byte) 0x70, (byte) 0xde, (byte) 0x57,
                (byte) 0x36, (byte) 0x87, (byte) 0x6e, (byte) 0x0f,
                (byte) 0x15, (byte) 0x75, (byte) 0x36};
    }

}
