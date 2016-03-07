
/**
 * 	GoogleVoiceEngine.java
 *

 *
 */
package GenericVoiceSDK.android.voice.google;

import android.os.AsyncTask;
import android.util.Log;
import android.speech.SpeechRecognizer;
import android.speech.RecognizerIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.speech.RecognitionListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

import GenericVoiceSDK.android.core.CoreAndroid;
import GenericVoiceSDK.core.Core;
import GenericVoiceSDK.voice.VoiceToText;
import GenericVoiceSDK.voice.VoiceToTextEngine;
import GenericVoiceSDK.voice.VoiceToTextListener;


public class GoogleVoiceEngine implements VoiceToTextEngine
{
    private static boolean DEBUG = true;
    private static String TAG = "GenericVoiceSDK.GoogleVoiceEngine";

    private SpeechRecognizer mSpeechRecognizer;
    private VoiceToTextListener mListener;
    private Context mContext;
    private boolean mPrepared = false;
    LinkedList<VoiceToTextListener> mVoiceQueue = new LinkedList<VoiceToTextListener>();

    public void initialize(Core core)
    {
    }


    private void prepare()
    {
        mPrepared = true;
        mContext = ((CoreAndroid) Core.getInstance()).getContext();
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
        mSpeechRecognizer.setRecognitionListener(new GoogleRecognitionListener());
    }

    @Override
    public void releaseResources()
    {

    }



    @Override
    public void startVoiceToText(VoiceToTextListener voiceToTextListener)
    {
        if(mListener == null)
        {
            mListener = voiceToTextListener;
            startRecognition();
        }
        else
        {
            mVoiceQueue.add(voiceToTextListener);
        }
    }


    private void startRecognition()
    {
        AsyncTask startListeningTask = new AsyncTask()
        {

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            {
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "voice.recognition.test");
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            }

            @Override
            protected Object doInBackground(Object... params)
            {
                return null;
            }

            @Override
            protected void onPostExecute(Object obj)
            {
                if(!mPrepared)
                {
                    prepare();
                }

                if(DEBUG)Log.d(TAG, "startListening");
                mSpeechRecognizer.startListening(intent);
            }
        };

        startListeningTask.execute();
    }

    class GoogleRecognitionListener implements RecognitionListener
    {
        public void onReadyForSpeech(Bundle params)
        {
            if(DEBUG)Log.d(TAG, "onReadyForSpeech");
        }

        public void onBeginningOfSpeech()
        {
            if(DEBUG)Log.d(TAG, "onBeginningOfSpeech");
            mListener.onRecordingStarted();
        }

        public void onRmsChanged(float rmsdB)
        {
            //  Log.d(TAG, "onRmsChanged");
        }

        public void onBufferReceived(byte[] buffer)
        {
            if(DEBUG) Log.d(TAG, "onBufferReceived");
        }

        public void onEndOfSpeech()
        {
            if(DEBUG) Log.d(TAG, "onEndofSpeech");
        }

        public void onError(int error)
        {
            if(DEBUG)Log.d(TAG, "error " + error);

            //TODO turn error from GoogleVoiceEngine error code to VoiceToText error codes.
            int GenericVoiceSDKError;
            if(error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS)
            {
                GenericVoiceSDKError = VoiceToText.ERROR_NO_PERMISSIONS;
            }
            else if(error == SpeechRecognizer.ERROR_NETWORK || error == SpeechRecognizer.ERROR_NETWORK_TIMEOUT)
            {
                GenericVoiceSDKError = VoiceToText.ERROR_NO_CONNECTION;
            }
            else if (error == SpeechRecognizer.ERROR_NO_MATCH)
            {
                GenericVoiceSDKError = VoiceToText.ERROR_NO_VOICE_HEARD;
            }
            else
            {
                GenericVoiceSDKError = -1;
            }

            // Since there was an error, stop!

            if(mListener != null) mListener.onError(GenericVoiceSDKError);
        }

        public void onResults(Bundle results)
        {
            if(DEBUG)Log.d(TAG, "onResults " + results);

            String str = new String();
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < 1; i++)
            {
                Log.d(TAG, "result " + data.get(i));
                str += data.get(i);
            }
            mListener.onResult(str);

            // TODO only stop listening if the queue is empty

            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.cancel();


            mListener = null;
            // TODO maybe do this elsewhere
            if(!mVoiceQueue.isEmpty())
            {
                mListener = mVoiceQueue.remove();
                startRecognition();
            }
        }

        public void onPartialResults(Bundle partialResults)
        {
            if(DEBUG)Log.d(TAG, "onPartialResults");
        }

        public void onEvent(int eventType, Bundle params)
        {
            if(DEBUG)Log.d(TAG, "onEvent " + eventType);
        }
    }
}
