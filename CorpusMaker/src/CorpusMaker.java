

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.util.Version;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.standard.StandardTokenizer;


/*
 * This program is going to make use of Apache Lucene libraries to parse through the source 
 * code of projects and take note of the lexicon. 
 */
public class CorpusMaker {

    private boolean isReservedWord(final String s) {
        final List<String> RESERVED_WORD = Arrays.asList(
                "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
                "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
                "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
                "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super",
                "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while", "a", "about", "above", "according", "across", "after", "afterwards", "again", "against", "albeit", "all", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anywhere", "apart", "are", "around", "as", "at", "av", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "both", "but", "by", "can", "cannot", "canst", "certain", "cf", "choose", "contrariwise", "cos", "could", "cu", "day", "do", "does", "doesn't", "doing", "dost", "doth", "double", "down", "dual", "during", "each", "either", "else", "elsewhere", "enough", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "except", "excepted", "excepting", "exception", "exclude", "excluding", "exclusive", "far", "farther", "farthest", "few", "ff", "first", "for", "formerly", "forth", "forward", "from", "front", "further", "furthermore", "furthest", "get", "go", "had", "halves", "hardly", "has", "hast", "hath", "have", "he", "hence", "henceforth", "her", "here", "hereabouts", "hereafter", "hereby", "herein", "hereto", "hereupon", "hers", "herself", "him", "himself", "hindmost", "his", "hither", "hitherto", "how", "however", "howsoever", "i", "ie", "if", "in", "inasmuch", "inc", "include", "included", "including", "indeed", "indoors", "inside", "insomuch", "instead", "into", "inward", "inwards", "is", "it", "its", "itself", "just", "kind", "kg", "km", "last", "latter", "latterly", "less", "lest", "let", "like", "little", "ltd", "many", "may", "maybe", "me", "meantime", "meanwhile", "might", "moreover", "most", "mostly", "more", "mr", "mrs", "ms", "much", "must", "my", "myself", "namely", "need", "neither", "never", "nevertheless", "next", "no", "nobody", "none", "nonetheless", "noone", "nope", "nor", "not", "nothing", "notwithstanding", "now", "nowadays", "nowhere", "of", "off", "often", "ok", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "own", "per", "perhaps", "plenty", "provide", "quite", "rather", "really", "round", "said", "sake", "same", "sang", "save", "saw", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "seldom", "selves", "sent", "several", "shalt", "she", "should", "shown", "sideways", "since", "slept", "slew", "slung", "slunk", "smote", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "spake", "spat", "spoke", "spoken", "sprang", "sprung", "stave", "staves", "still", "such", "supposing", "than", "that", "the", "thee", "their", "them", "themselves", "then", "thence", "thenceforth", "there", "thereabout", "thereabouts", "thereafter", "thereby", "therefore", "therein", "thereof", "thereon", "thereto", "thereupon", "these", "they", "this", "those", "thou", "though", "thrice", "through", "throughout", "thru", "thus", "thy", "thyself", "till", "to", "together", "too", "toward", "towards", "ugh", "unable", "under", "underneath", "unless", "unlike", "until", "up", "upon", "upward", "upwards", "us", "use", "used", "using", "very", "via", "vs", "want", "was", "we", "week", "well", "were", "what", "whatever", "whatsoever", "when", "whence", "whenever", "whensoever", "where", "whereabouts", "whereafter", "whereas", "whereat", "whereby", "wherefore", "wherefrom", "wherein", "whereinto", "whereof", "whereon", "wheresoever", "whereto", "whereunto", "whereupon", "wherever", "wherewith", "whether", "whew", "which", "whichever", "whichsoever", "while", "whilst", "whither", "who", "whoa", "whoever", "whole", "whom", "whomever", "whomsoever", "whose", "whosoever", "why", "will", "wilt", "with", "within", "without", "worse", "worst", "would", "wow", "ye", "yet", "year", "yippee", "you", "your", "yours", "yourself", "yourselves"
        );
        return RESERVED_WORD.contains(s);
    }

    public Scanner getFile(File f) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return scanner;
    }

    public List<String> parseFile(Scanner s) {
        List<String> wordsInDoc = new ArrayList<>();

        while (s.hasNextLine()) {
            String line = s.nextLine();
            //System.out.println(line);

            //split by space or period
            String[] parsedLine = line.split("[/. ]+");

            for (int i = 0; i < parsedLine.length; i++) {

                //go to next line if begin with import or package
                if (parsedLine[0].equals("import") || parsedLine[0].equals("package")) {
                    break;
                } //if string does not contains only white spaces, and no reserved word found
                else if (parsedLine[i].trim().length() > 0 && !isReservedWord(parsedLine[i])) {
                    //remove non-alphanumerics
                    parsedLine[i] = parsedLine[i].replaceAll("[^a-zA-Z0-9]", "");

                    //if more than 2 characters, store to list
                    if (parsedLine[i].trim().length() >= 2) {

                        String[] temp = parsedLine[i].split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
                        //if there is camel case split
                        if (temp.length > 1) {
                            for (String w : temp) {
                                wordsInDoc.add(w.toLowerCase());
                            }
                        } else {
                            wordsInDoc.add(parsedLine[i].toLowerCase());
                        }

                        //System.out.print(parsedLine[i] +" ");
                    }
                }

                //string contains only white spaces or <=2 chars, continue to next word
                //else{continue;}
            }
            //System.out.println();
        }//end while == end of document

        //System.out.println(wordsInDoc);
        return wordsInDoc;
    }

    private static Version LUCENE_VERSION = Version.LUCENE_48;

    private static String tokenizeStopStem(String input) {

        TokenStream tokenStream = new StandardTokenizer(LUCENE_VERSION, new StringReader(input));
        tokenStream = new StopFilter(LUCENE_VERSION, tokenStream, EnglishAnalyzer.getDefaultStopSet());
        tokenStream = new PorterStemFilter(tokenStream);

        return tokenStream.toString();
    }

    public Map<String, Integer> calculateWordFrequency(List<String> tokenizeStopStem) {

        Map<String, Integer> wordFrequency = new HashMap<String, Integer>();
        for (String w : tokenizeStopStem) {
            wordFrequency.put(w, Collections.frequency(tokenizeStopStem, w));
        }
        System.out.println(wordFrequency);
        return wordFrequency;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public LinkedHashMap<String, Integer> sortByValue(Map unsortMap) {
        List list = new LinkedList(unsortMap.entrySet());

        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedMap.put((String) entry.getKey(), (Integer) entry.getValue());
        }
        return sortedMap;
    }

    public Doc[] docHighestUnique(Doc d, Doc[] docHighestUnique) {
        int uniqueNum = d.wordFrequency.size();
        int curLowest = Integer.MAX_VALUE;
        int curLowestIndex = -1;
        boolean allFilled = true;

        for (int i = 0; i < docHighestUnique.length; i++) {
            if (docHighestUnique[i] != null) {
                int curUnique = docHighestUnique[i].wordFrequency.size();

                if (curUnique < curLowest) {
                    curLowest = curUnique;
                    curLowestIndex = i;
                }

                if (i == docHighestUnique.length - 1) {
                    allFilled = true;
                }
            } else {
                curLowestIndex = i;
                allFilled = false;
                break;
            }
        }

        if (allFilled) {
            if (uniqueNum > docHighestUnique[curLowestIndex].wordFrequency.size()) {
                docHighestUnique[curLowestIndex] = d;
            }
        } else {
            docHighestUnique[curLowestIndex] = d;
        }

        return docHighestUnique;
    }

    public Doc[] docLowestUnique(Doc d, Doc[] docLowestUnique) {
        int uniqueNum = d.wordFrequency.size();
        int curHighest = Integer.MIN_VALUE;
        int curHighestIndex = -1;
        boolean allFilled = true;

        for (int i = 0; i < docLowestUnique.length; i++) {
            if (docLowestUnique[i] != null) {
                int curUnique = docLowestUnique[i].wordFrequency.size();

                if (curUnique > curHighest) {
                    curHighest = curUnique;
                    curHighestIndex = i;
                }

                if (i == docLowestUnique.length - 1) {
                    allFilled = true;
                }
            } else {
                curHighestIndex = i;
                allFilled = false;
                break;
            }
        }

        if (allFilled) {
            if (uniqueNum < docLowestUnique[curHighestIndex].wordFrequency.size()) {
                docLowestUnique[curHighestIndex] = d;
            }
        } else {
            docLowestUnique[curHighestIndex] = d;
        }

        return docLowestUnique;
    }

    public Word[] highestDocFrequency(Map<String, Integer> CorpusWordFreq, Doc[] docs) {
        Word[] wordsDocFreq = new Word[CorpusWordFreq.size()];
        Object[] words = CorpusWordFreq.keySet().toArray();

        //store all words to array
        for (int i = 0; i < wordsDocFreq.length; i++) {
            wordsDocFreq[i] = new Word(words[i].toString());
        }

        //calculate document frequency of word w
        for (int i = 0; i < wordsDocFreq.length; i++) {
            Word w = wordsDocFreq[i];
            for (Doc d : docs) {
                if (d.wordFrequency.containsKey(w.word)) {
                    w.countInDoc++;
                    w.inDocs.add(d);
                }
            }
        }

        //sort based on inDoc size
        Word temp = null;
        for (int i = 0; i < wordsDocFreq.length; i++) {
            for (int v = 1; v < wordsDocFreq.length - i; v++) {
                if (wordsDocFreq[v - 1].inDocs.size() < wordsDocFreq[v].inDocs.size()) {
                    temp = wordsDocFreq[v - 1];
                    wordsDocFreq[v - 1] = wordsDocFreq[v];
                    wordsDocFreq[v] = temp;
                }
            }
        }
        return wordsDocFreq;
    }

    public static void main(String[] args) throws IOException {
        CorpusMaker a = new CorpusMaker();
        Map<String, Integer> CorpusWordFreq = new HashMap<String, Integer>();
        Doc[] docHighestUnique = new Doc[3];
        Doc[] docLowestUnique = new Doc[3];
        File startingDir = new File("D:\\workspace\\CorpusMaker\\ant"); // current directory

        //File startingDir = new File("/Users/1116743/Documents");
        String[] fileType = {"java"};
        boolean searchSubDir = true;

        List<File> files = (List<File>) FileUtils.listFiles(startingDir, fileType, searchSubDir);
        Doc[] docs = new Doc[files.size()];
        for (int i = 0; i < files.size(); i++) {
            File f = files.get(i);
            System.out.println(f.getCanonicalPath());

            List<String> parsedDoc = a.parseFile(a.getFile(f));

            a.tokenizeStopStem(parsedDoc.toString());
            /*
			 * INSERT STEMMING HERE
             */
            Map<String, Integer> docWordFreq = a.calculateWordFrequency(parsedDoc);

            docs[i] = new Doc(f, docWordFreq);
            docHighestUnique = a.docHighestUnique(docs[i], docHighestUnique);
            docLowestUnique = a.docLowestUnique(docs[i], docLowestUnique);

            for (Map.Entry<String, Integer> e : docWordFreq.entrySet()) {
                //update corpus word frequency
                CorpusWordFreq.merge(e.getKey(), e.getValue(), Integer::sum);
            }
            System.out.println();
        }

        System.out.println("\n_____Distribution of all terms in corpus");
        //System.out.println(CorpusWordFreq);
        LinkedHashMap<String, Integer> sortedCorpusWordFreq = a.sortByValue(CorpusWordFreq);
        System.out.println(sortedCorpusWordFreq);

        System.out.println("\n_____10 most frequent term in corpus");
        Object[] o = sortedCorpusWordFreq.entrySet().toArray(); //convert map to Map.Entry<K,V>[] to get by index
        int c = 0;
        int p = sortedCorpusWordFreq.size() - 1;
        while (c < 10 && p >= 0) {
            System.out.println(o[p]);
            c++;
            p--;
        }

        System.out.println("\n_____10 term with highest document frequency");
        Word[] topDocFreq = a.highestDocFrequency(CorpusWordFreq, docs);
        for (int i = 0; i < 10; i++) {
            Word w = topDocFreq[i];
            System.out.println(w.word + " appears in " + w.inDocs.size() + " files: ");
            for (int j = 0; j < w.inDocs.size(); j++) {
                System.out.println("\t" + w.inDocs.get(j).file.getCanonicalPath());
            }
        }

        System.out.println("\n_____3 documents with highest unique terms");
        for (int i = 0; i < docHighestUnique.length; i++) {
            Doc d = docHighestUnique[i];
            System.out.println(d.file.getCanonicalPath() + " has " + d.wordFrequency.size() + " unique terms.");
        }

        System.out.println("\n_____3 documents with lowest unique terms");
        for (int i = 0; i < docLowestUnique.length; i++) {
            Doc d = docLowestUnique[i];
            System.out.println(d.file.getCanonicalPath() + " has " + d.wordFrequency.size() + " unique terms.");
        }

    }
}

class Doc {

    File file;
    Map<String, Integer> wordFrequency = new HashMap<String, Integer>();

    Doc(File file, Map<String, Integer> wordFrequency) {
        this.file = file;
        this.wordFrequency = wordFrequency;
    }
}

class Word {

    String word = "";
    int countInDoc = 0;
    List<Doc> inDocs = new ArrayList<Doc>();

    Word(String word) {
        this.word = word;
    }
}
