/**
 *
 * VoiceToText.java
 *
 */

package GenericVoiceSDK.voice;

import GenericVoiceSDK.core.Core;

public class VoiceToText
{

    public static final int ERROR_NO_VOICE_TO_TEXT_ENGINE = 0;
    public static final int ERROR_NO_CONNECTION = 1;
    public static final int ERROR_NO_PERMISSIONS = 2;
    public static final int ERROR_NO_VOICE_HEARD = 3;



    public static void startVoiceToText(VoiceToTextListener listener)
    {
        Core.getInstance().getVoiceEngine().startVoiceToText(listener);
    }

    public static void releaseResources()
    {
        Core.getInstance().getVoiceEngine().releaseResources();
    }


    public static String errorToString(int error)
    {
        switch(error)
        {
            case ERROR_NO_CONNECTION:
                return "No internet connection found";
            case ERROR_NO_VOICE_TO_TEXT_ENGINE:
                return "No VoiceToTextEngine was initialized.";
            case ERROR_NO_PERMISSIONS:
                return "This App does not have enough permissions";
            case ERROR_NO_VOICE_HEARD:
                return "No voice heard";
            default:
                return "Unknown error";
        }

    }

}
