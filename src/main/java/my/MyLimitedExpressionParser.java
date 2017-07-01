package my;

import com.github.kmizu.parser_hands_on.ParseFailure;
import com.github.kmizu.parser_hands_on.limited_expression.LimitedExpressionNode;
import com.github.kmizu.parser_hands_on.limited_expression.AbstractLimitedExpressionParser;

public class MyLimitedExpressionParser extends AbstractLimitedExpressionParser{
    private String input;

    @Override
    public LimitedExpressionNode parse(String input) {
        this.input = input;
        this.position = 0;

        int lhs = integer();

        try {
            save();
            accept('+');
            int rhs = integer();
            return LimitedExpressionNode.add(lhs, rhs);
        } catch (ParseFailure e) {
            restore();
        }
        try {
            save();
            accept('-');
            int rhs = integer();
            return LimitedExpressionNode.sub(lhs, rhs);
        } catch (ParseFailure e) {
            restore();
        }
        try {
            save();
            accept('*');
            int rhs = integer();
            return LimitedExpressionNode.mul(lhs, rhs);
        } catch (ParseFailure e) {
            restore();
        }
        try {
            save();
            accept('/');
            int rhs = integer();
            return LimitedExpressionNode.div(lhs, rhs);
        } catch (ParseFailure e) {
            restore();
        }

        if (position != input.length()) {
            throw new ParseFailure("");
        }

        return LimitedExpressionNode.v(lhs);
    }

    private int integer() {
        int result = 0;
        int firstDigit = -1;
        boolean isValidNumber = false;
        for (;;) {
            if (position >= input.length()) {
                break;
            }
            char ch = input.charAt(position);

            if (!isNumber(ch)) {
                if (isValidNumber) {
                    break;
                } else {
                    throw new ParseFailure("");
                }
            }

            if (firstDigit == 0) {
                throw new ParseFailure("");
            }
            int n = toInt(ch);

            if (!isValidNumber) {
                firstDigit = n;
            }

            result = result * 10 + n;

            position += 1;
            isValidNumber = true;
        }
        return result;
    }

    private static int toInt(char ch) {
        return ch - '0';
    }

    private static boolean isNumber(char ch) {
        return ('0' <= ch && ch <= '9');
    }

    private void accept(char ch) {
        if (position >= input.length() ||  input.charAt(position) != ch) {
            throw new ParseFailure("");
        }
        position += 1;
    }
}
