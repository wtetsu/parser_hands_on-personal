package my;

import com.github.kmizu.parser_hands_on.ParseFailure;
import com.github.kmizu.parser_hands_on.expression.AbstractExpressionParser;
import com.github.kmizu.parser_hands_on.expression.ExpressionNode;

public class MyExpressionParser extends AbstractExpressionParser {
    private String input;
    private int position;

    @Override
    public ExpressionNode parse(String input) {
        this.input = input;
        this.position = 0;

        ExpressionNode e = expression();

        if (position != input.length()) {
            throw new ParseFailure("");
        }

        return e;
    }

    // expression = additive;
    ExpressionNode expression() {
        return additive();
    }

    // additive = multitive
    //             {'+' multitive | '-' multitive};
    ExpressionNode additive() {
        ExpressionNode result = multitive();

        for (;;) {
            try {
                save();
                accept('+');
                ExpressionNode rhs = multitive();
                result = new ExpressionNode.Addition(result, rhs);
            } catch (ParseFailure e) {
                restore();

                try {
                    save();
                    accept('-');
                    ExpressionNode rhs = multitive();
                    result = new ExpressionNode.Subtraction(result, rhs);
                } catch (ParseFailure e2) {
                    restore();
                    break;
                }
            }
        }

        return result;
    }

    // multitive = primary
    //              {'*' primary | '/' primary};
    ExpressionNode multitive() {
        ExpressionNode result = primary();

        for (;;) {
            try {
                save();
                accept('*');
                ExpressionNode rhs = primary();
                result = new ExpressionNode.Multiplication(result, rhs);
            } catch (ParseFailure e) {
                restore();

                try {
                    save();
                    accept('/');
                    ExpressionNode rhs = primary();
                    result = new ExpressionNode.Division(result, rhs);
                } catch (ParseFailure e2) {
                    restore();
                    break;
                }
            }
        }

        return result;
    }

    // primary = '(' expression ')' | integer;
    ExpressionNode primary() {
        try {
            save();
            accept('(');
            ExpressionNode expression = expression();
            accept(')');
            return expression;
        } catch (ParseFailure e) {
            restore();
            int n = integer();
            return new ExpressionNode.ValueNode(n);
        }
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
