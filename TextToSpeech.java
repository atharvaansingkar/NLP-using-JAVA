package com.atharva.nlp;

import java.util.Locale;
        import java.util.Scanner;

        import javax.speech.AudioException;
        import javax.speech.Central;
        import javax.speech.EngineException;
        import javax.speech.EngineStateError;
        import javax.speech.synthesis.Synthesizer;
        import javax.speech.synthesis.SynthesizerModeDesc;

public class TextToSpeech {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Enter text: ");
        String text = scanner.nextLine();

        System.setProperty("freetts.voices",
                "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        try {
            Central.registerEngineCentral("com.sun.speech.freetts"
                    + ".jsapi.FreeTTSEngineCentral");
            Synthesizer synthesizer = Central.createSynthesizer(new SynthesizerModeDesc
                    (Locale.US));
            synthesizer.allocate();
            synthesizer.resume();

            synthesizer.speakPlainText(text, null);
            synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
            synthesizer.deallocate();

        } catch (EngineException e) {

            e.printStackTrace();
        } catch (AudioException e) {

            e.printStackTrace();
        } catch (EngineStateError e) {

            e.printStackTrace();
        } catch (IllegalArgumentException e) {

            e.printStackTrace();
        } catch (InterruptedException e) {

            e.printStackTrace();

        }
    }
}