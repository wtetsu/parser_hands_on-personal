package my;

import com.github.kmizu.parser_hands_on.ParseFailure;
import com.github.kmizu.parser_hands_on.digit.AbstractDigitParser;

import java.text.ParseException;

public class MyDigitParser extends AbstractDigitParser {
    @Override
    public Integer parse(String input) {
        if (input.length() != 1) {
            throw new ParseFailure("");
        }
        char ch = input.charAt(0);
        boolean isNumber = ('0' <= ch && ch <= '9');

        if (!isNumber) {
            throw new ParseFailure("");
        }

        return (ch - '0');
    }
}
