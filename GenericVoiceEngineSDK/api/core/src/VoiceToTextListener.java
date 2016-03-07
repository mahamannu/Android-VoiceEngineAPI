/** 
 * 	VoiceToTextListener.java
 *
 * 
 */
package GenericVoiceSDK.voice;

public interface VoiceToTextListener
{

	public void onRecordingStarted(); // Called when we recording starts

	public void onRecordingStopped(); // Called when recording is done

	public void onResult(String text); // Called when we have a result

	public void onError(int errorCode); // Called when recording has error'd, canceled, etc.
}
