package com.atharva.nlp;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import java.util.List;
import java.util.Properties;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

public class NLPApp {
    private JFrame frame;
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private StanfordCoreNLP stanfordCoreNLP;

    private Map<String, String> posTagMeanings;

    public NLPApp() {
        initialize();
        createStanfordCoreNLP();
        initializePOSTagMeanings();
    }

    private void initialize() {
        frame = new JFrame("NLP Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(100, 100, 800, 600);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        JLabel inputLabel = new JLabel("Input Text:");
        inputPanel.add(inputLabel, BorderLayout.NORTH);

        inputTextArea = new JTextArea();
        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton analyzeButton = new JButton("Analyze Text");
        analyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performNLPAnalysis();
            }
        });
        buttonPanel.add(analyzeButton);

        JButton speakButton = new JButton("Speak Text");
        speakButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performTextToSpeech();
            }
        });
        buttonPanel.add(speakButton);

        inputPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(inputPanel, BorderLayout.WEST);

        outputTextArea = new JTextArea();
        JScrollPane outputScrollPane = new JScrollPane(outputTextArea);
        frame.add(outputScrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void createStanfordCoreNLP() {
        Properties properties = new Properties();
        properties.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,sentiment");
        stanfordCoreNLP = new StanfordCoreNLP(properties);
    }

    private void initializePOSTagMeanings() {
        posTagMeanings = new HashMap<>();
        posTagMeanings.put("CC", "Coordinating conjunction");
        posTagMeanings.put("CD", "Cardinal number");
        posTagMeanings.put("DT", "Determiner");
        posTagMeanings.put("EX", "Existential there");
        posTagMeanings.put("FW", "Foreign word");
        posTagMeanings.put("IN", "Preposition or subordinating conjunction");
        posTagMeanings.put("JJ", "Adjective");
        posTagMeanings.put("JJR", "Adjective, comparative");
        posTagMeanings.put("JJS", "Adjective, superlative");
        posTagMeanings.put("LS", "List item marker");
        posTagMeanings.put("MD", "Modal");
        posTagMeanings.put("NN", "Noun, singular or mass");
        posTagMeanings.put("NNS", "Noun, plural");
        posTagMeanings.put("NNP", "Proper noun, singular");
        posTagMeanings.put("NNPS", "Proper noun, plural");
        posTagMeanings.put("PDT", "Predeterminer");
        posTagMeanings.put("POS", "Possessive ending");
        posTagMeanings.put("PRP", "Personal pronoun");
        posTagMeanings.put("PRP$", "Possessive pronoun");
        posTagMeanings.put("RB", "Adverb");
        posTagMeanings.put("RBR", "Adverb, comparative");
        posTagMeanings.put("RBS", "Adverb, superlative");
        posTagMeanings.put("RP", "Particle");
        posTagMeanings.put("SYM", "Symbol");
        posTagMeanings.put("TO", "to");
        posTagMeanings.put("UH", "Interjection");
        posTagMeanings.put("VB", "Verb, base form");
        posTagMeanings.put("VBD", "Verb, past tense");
        posTagMeanings.put("VBG", "Verb, gerund or present participle");
        posTagMeanings.put("VBN", "Verb, past participle");
        posTagMeanings.put("VBP", "Verb, non-3rd person singular present");
        posTagMeanings.put("VBZ", "Verb, 3rd person singular present");
        posTagMeanings.put("WDT", "Wh-determiner");
        posTagMeanings.put("WP", "Wh-pronoun");
        posTagMeanings.put("WP$", "Possessive wh-pronoun");
        posTagMeanings.put("WRB", "Wh-adverb");
    }

// ... (Previous code remains the same)

    private void performNLPAnalysis() {
        String inputText = inputTextArea.getText();
        CoreDocument coreDocument = new CoreDocument(inputText);
        stanfordCoreNLP.annotate(coreDocument);

        List<CoreSentence> sentences = coreDocument.sentences();

        outputTextArea.setText("");

        for (CoreSentence sentence : sentences) {
            StringBuilder result = new StringBuilder();
            result.append("Sentiment: ").append(sentence.sentiment()).append("\n");
            result.append("Tokens and Part of Speech Tags:\n");

            for (CoreLabel token : sentence.tokens()) {
                String word = token.originalText();
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

                String posMeaning = posTagMeanings.get(pos);
                if (posMeaning == null) {
                    posMeaning = "Unknown";
                }

                result.append("Word: ").append(word).append("\n");
                result.append("POS Tag: ").append(pos).append(" - ").append(posMeaning).append("\n");
                result.append("Lemma: ").append(token.lemma()).append("\n");
                result.append("Named Entity: ").append(token.ner()).append("\n");
                result.append("--------\n");
            }

            // Add a comment to explain the Dependency Parse section
            result.append("Dependency Parse (How words relate to each other in the sentence):\n");

            // Get the dependency parse tree
            SemanticGraph dependencies = sentence.dependencyParse();

            // Append the dependency parse tree to the result
            result.append(dependencies.toString(SemanticGraph.OutputFormat.LIST));

            result.append("\n\n");
            outputTextArea.append(result.toString());
        }
    }


    private void performTextToSpeech() {
        String text = inputTextArea.getText();
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        try {
            Central.registerEngineCentral("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");
            Synthesizer synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(Locale.US));
            synthesizer.allocate();
            synthesizer.resume();

            synthesizer.speakPlainText(text, null);
            synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
            synthesizer.deallocate();
        } catch (EngineException | AudioException | EngineStateError | IllegalArgumentException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new NLPApp();
            }
        });
    }
}
