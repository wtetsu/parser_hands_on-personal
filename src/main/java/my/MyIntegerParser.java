package my;

import com.github.kmizu.parser_hands_on.ParseFailure;
import com.github.kmizu.parser_hands_on.integer.AbstractIntegerParser;

public class MyIntegerParser extends AbstractIntegerParser {

    @Override
    public Integer parse(String input) {

        if (input.length() < 0) {
            throw new ParseFailure("");
        }

        if (input.equals("0")) {
            return 0;
        }

        int result = 0;

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            if (!isNumber(ch)) {
                throw new ParseFailure("");
            }

            int n = toInt(ch);

            boolean isFirst = (i==0);
            if (isFirst) {
                if (n ==0) {
                    throw new ParseFailure("");
                }
            }

            result += n * Math.pow(10, input.length()-i-1);
        }

        return result;
    }

    private int toInt(char ch) {
        return ch - '0';
    }

    private boolean isNumber(char ch) {
        return ('0' <= ch && ch <= '9');
    }
}
