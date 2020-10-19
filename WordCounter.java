
import java.util.Comparator;

import components.map.Map;
import components.map.Map1L;
import components.queue.Queue;
import components.queue.Queue1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * A word counter class that allows user to counter the number of words in a
 * file and convert it to a HTML page words and its count.
 *
 * @author Jimmy Yuan
 */
public final class WordCounter {

    /**
     * Default constructor--private to prevent instantiation.
     */
    private WordCounter() {
        // no code needed here
    }

    /**
     * Compare {@code String}s in lexicographic order.
     */
    private static class StringLT implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareToIgnoreCase(o2);
        }
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments; unused here
     */
    public static void main(String[] args) {
        SimpleReader input = new SimpleReader1L();
        SimpleWriter output = new SimpleWriter1L();
        //Asking for names of the files.
        output.println("Please enter the name of the input file: ");
        String inFile = input.nextLine();
        output.println("Please enter the name of the input file: ");
        String outFile = input.nextLine();
        SimpleReader in = new SimpleReader1L(inFile);
        SimpleWriter out = new SimpleWriter1L(outFile);
        //Store the words into the queue, and words and count
        //into map.
        Map<String, Integer> pair = new Map1L<>();
        Comparator<String> order = new StringLT();
        Queue<String> sortedTerm = new Queue1L<>();
        Queue<String> copy = new Queue1L<>();
        //Generate separators.
        Set<Character> separators = generateSeparator();
        //Start at every line from the input file.
        while (!in.atEOS()) {
            String next = in.nextLine();
            //Separate every word in this line and store them.
            int position = 0;
            while (position < next.length()) {
                //Separate.
                String word = nextWordOrSeparator(next, position, separators);
                //Check whether it is a separator or not.
                if (!separators.contains(word.charAt(0))) {
                    //Store.
                    inputTerm(pair, sortedTerm, copy, word);
                }
                position += word.length();
            }
        }
        //Sort the queue.
        sort(order, sortedTerm);
        sort(order, copy);
        //Write the HTML file using the data in Map
        outputHTML(sortedTerm, pair, out, inFile);
        in.close();
        input.close();
        output.close();
    }

    /**
     * Generate separators, which is not English characters.
     *
     * @return separators that is not a word.
     */
    public static Set<Character> generateSeparator() {
        Set<Character> separators = new Set1L<>();
        separators.add(' ');
        separators.add(',');
        separators.add('.');
        separators.add('?');
        separators.add('/');
        separators.add(';');
        separators.add(':');
        separators.add('"');
        separators.add('[');
        separators.add(']');
        separators.add('{');
        separators.add('}');
        separators.add('!');
        separators.add('@');
        separators.add('#');
        separators.add('$');
        separators.add('%');
        separators.add('^');
        separators.add('&');
        separators.add('*');
        separators.add('(');
        separators.add(')');
        separators.add('_');
        separators.add('+');
        separators.add('-');
        separators.add('=');
        separators.add('0');
        separators.add('1');
        separators.add('2');
        separators.add('3');
        separators.add('4');
        separators.add('5');
        separators.add('6');
        separators.add('7');
        separators.add('8');
        separators.add('9');
        return separators;
    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) in the given {@code text} starting at the given
     * {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        assert text != null : "Violation of: text is not null";
        assert separators != null : "Violation of: separators is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        String result = "";
        char c = text.charAt(position);
        boolean check = true;
        boolean check2 = true;

        //if the character at the position of text doesn't exist in separators
        if (!separators.contains(c)) {
            int count = 1;
            for (int i = position + 1; i < text.length(); i++) {
                //find another char in the string and compare it to the
                //one in the separator set.
                if (check) {
                    if (!separators.contains(text.charAt(i))) {
                        count++;
                    } else {
                        //terminate the loop
                        check = false;
                    }
                }
            }
            result = text.substring(position, position + count);
        } else {
            int count = 1;
            for (int i = position + 1; i < text.length(); i++) {
                //find another char in the string and compare it to the
                //one in the separator set.
                if (check2) {
                    if (separators.contains(text.charAt(i))) {
                        count++;
                    } else {
                        //terminate the loop
                        check2 = false;
                    }
                }
            }
            result = text.substring(position, position + count);
        }
        return result;
    }

    /**
     * Extract terms from the input file and store them into queue, and store
     * terms and definitions into the Map.
     *
     * @param q
     *            queue that stores terms
     *
     * @param copy
     *            a queue copy
     *
     * @param pair
     *            map that stores terms and definitions
     * @param s
     *            string that is to be stored
     * @Ensures every term in the file is stored into the queue.
     */
    private static void inputTerm(Map<String, Integer> pair, Queue<String> copy,
            Queue<String> q, String s) {
        //If the word already exists in the Map.
        if (pair.hasKey(s)) {
            //Increment the count.
            pair.replaceValue(s, pair.value(s) + 1);
        } else {
            pair.add(s, 1);
            q.enqueue(s);
            copy.enqueue(s);
        }
    }

    /**
     * Write the content of index.html to the output file in order to generate a
     * glossary page as specified by the client.
     *
     * @param pair
     *            map value that stores the terms and definitions
     * @param q
     *            sorted queue that contains the terms
     *
     * @param name
     *            name of directory where the file is located
     * @param out
     *            output stream that output the html file
     */
    private static void outputHTML(Queue<String> q, Map<String, Integer> pair,
            SimpleWriter out, String name) {
        //Header and formatting.
        out.println("<html> <head>");
        out.println("<title>Words Counted in " + name + "</title>");
        out.println("</head>\r\n" + "<body>");
        out.println("<h2>Words Counted in " + name + "</h2>");

        //Table.
        out.println("<table border = \"1\"");
        out.println("<tr>\n<th>Words</th>\n<th>Counts</th>\n</tr>");
        int len = q.length();
        for (int i = 0; i < len; i++) {
            String target = q.dequeue();
            out.println("<tr>\n<th>" + target + "</th>\n<th>"
                    + pair.value(target) + "</th>\n</tr>");
        }

        //Footer.
        out.println("</table>\n</body>\r\n</html>");
        out.close();
    }

    /**
     * Removes and returns the minimum value from {@code q} according to the
     * ordering provided by the {@code compare} method from {@code order}.
     *
     * @param q
     *            the queue
     * @param order
     *            ordering by which to compare entries
     * @return the minimum value from {@code q}
     * @updates q
     * @requires <pre>
     * q /= empty_string  and
     *  [the relation computed by order.compare is a total preorder]
     * </pre>
     * @ensures <pre>
     * (q * <removeMin>) is permutation of #q  and
     *  for all x: string of character
     *      where (x is in entries (q))
     *    ([relation computed by order.compare method](removeMin, x))
     * </pre>
     */
    private static String removeMin(Queue<String> q, Comparator<String> order) {
        assert q != null : "Violation of: q is not null";
        assert order != null : "Violation of: order is not null";
        Queue<String> tem = q.newInstance();
        int len = q.length();
        String min = q.dequeue();
        String temString;
        tem.enqueue(min);
        len--;
        //Find the minimum.
        for (int i = 0; i < len; i++) {
            temString = q.dequeue();
            tem.enqueue(temString);
            if (order.compare(min, temString) > 0) {
                min = temString;
            }
        }
        //Restore the queue.
        for (int i = 0; i < len + 1; i++) {
            String tem2 = tem.dequeue();
            if (!tem2.equals(min)) {
                q.enqueue(tem2);
            }
        }
        return min;
    }

    /**
     * Sort the queue in alphabetic order.
     *
     * @param order
     *            lexicographic order
     *
     * @param q
     *            queue that is being sorted
     */
    private static void sort(Comparator<String> order, Queue<String> q) {
        assert order != null : "Violation of: order is not null";
        Queue<String> tem = q.newInstance();
        int len = q.length();
        while (q.length() != 0) {
            String smallest = removeMin(q, order);
            tem.enqueue(smallest);
        }
        for (int i = 0; i < len; i++) {
            q.enqueue(tem.dequeue());
        }
    }
}
