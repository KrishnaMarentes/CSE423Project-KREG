import javafx.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Optimizations {

     /* Custom class for a Block - basically Pair but for only primitive int
     * Each Block holds the start and end indices of the starting and ending
     * lines for the basic block in the IR. (Inclusive)
     * e.g. If basic blocks exist from lines 10 to 16 in the IR, it would be
     * represented as a Block with Block.start = 10 and Block.end = 16
     */
    static class Block {
        final int start;
        final int end;
        Block(final int s, final int e) {
            this.start = s;
            this.end = e;
        }
        int getStart() {
            return this.start;
        }
        int getEnd() {
            return this.end;
        }
        public String toString() {
            return "<" + start + "," + end + ">";
        }
    }

    public static String optimizeIR(String ir) {
        ArrayList<Block> basicblocks = new ArrayList<Block>();
        basicblocks = findBasicBlocks(ir);
        basicblocks = cleanBlocks(basicblocks);
        
        /* For each basic block, perform optimizations */

        return basicblocks.toString(); // print this in SCC to check if it's right
    }

    /* Return a list of all the basic blocks in the IR
     * **basic blocks have exactly one entry pt and one exit pt**
     * @param ir The IR in one continuous string, with lines
     * separated by EOL
     * @return ArrayList of basic blocks
     */
    static ArrayList<Block> findBasicBlocks(String ir) {
        final String EOL = System.lineSeparator();
        String[] lines = ir.split(EOL);

        /* to be used as regexes */
        //final Pattern entry = Pattern.compile(".*function.*|.*KREG\\d*:.*");
        //final Pattern exit = Pattern.compile(".*goto.*|.*return.*");
        final String entry = ".*function.*|<KREG.\\d*>:.*";
        final String exit = ".*goto.*|.*return.*";

        ArrayList<Block> blocks = new ArrayList<Block>();

        int start = 0;
        int end = 1;
        String line;

        /* Looking at the IR line by line, mark the first found entry point
        * with "start." From there, look for either an entry or exit point,
        * and mark it with "end" */
        while (start < (lines.length-1)) {
            line = lines[start];
            if (Pattern.matches(entry, line)) {
                start++; // Don't include function or label lines in basic block
                end = start;
                while (end < (lines.length-1)) {
                    if (Pattern.matches(entry, lines[end])||Pattern.matches(exit, lines[end])) {
                        /* Special case: "If" with no "else" is a fall through, and thus an entry point */
                        // I don't think our IR currently has this case;  whiles use fall through but there's
                        // immediately a label after loops. But good to cover our bases if we add more features -becca
                        if (Pattern.matches("if ", lines[end]) && !Pattern.matches(" else ", lines[end])) {
                            /* Record the last block, mark the fall through start, and go directly to end search */
                            blocks.add(new Block(start, end-1));
                            start = end+1;
                            end = end+2;
                            continue;
                        }
                        break;
                    }
                    end++;
                }
                /* Case: adjacent entry/exit points. ex. "goto <KREG.5> /n <KREG.6>" */
                if (end-1 < start) {
                    start = end+1;
                    continue;
                }
                blocks.add(new Block(start, end-1)); // Don't include exit lines in block
                start = end+1; // Start new search after last exit point
                continue;
            }
            start++;
        }
        return blocks;
    }

    /* Remove leading, trailing, and standalone "empty" lines (e.g. "","{", etc) */
    static ArrayList<Block> cleanBlocks(ArrayList<Block> blocks) {
        /* For each block
        *  if a one-line block, check if it's empty and if so, remove it
        *  if a multi-line block
        *   check if first and/or last line is empty, if so remove */
        return blocks;
    }
}
