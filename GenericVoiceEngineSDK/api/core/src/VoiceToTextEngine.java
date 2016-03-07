/** 
 * 	VoiceToTextEngine.java
 *
 * 
 */
package GenericVoiceSDK.voice;

import GenericVoiceSDK.core.Core;


public interface VoiceToTextEngine
{

    public void initialize(Core core);

    public void startVoiceToText(VoiceToTextListener listener);

    public void releaseResources();
}
