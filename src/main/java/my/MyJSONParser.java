package my;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.github.kmizu.parser_hands_on.ParseFailure;
import com.github.kmizu.parser_hands_on.json.AbstractJSONParser;
import com.github.kmizu.parser_hands_on.json.JSONNode;

public class MyJSONParser extends AbstractJSONParser{
    private String input;
    @Override
    public JSONNode parse(String input) {
        this.input = input;
        this.position = 0;
        return jvalue();
    }

    //    jobject = '{'
    //            [jstring ':' jvalue {',' jstring ':' jvalue}]
    //            '}';
    private JSONNode jobject() {
        Map<String, JSONNode> pairs = new HashMap<>();

        try {
            save();
            accept('{');

            for (;;) {
                try {
                    save();
                    accept('}');

                    return new JSONNode.JSONObject(pairs);

                } catch (ParseFailure e) {
                    restore();
                }

                JSONNode.JSONString key = (JSONNode.JSONString)jstring();
                accept(':');
                JSONNode jvalue = jvalue();
                pairs.put(key.value, jvalue);
            }
        } catch (ParseFailure e) {
            restore();
        }
        throw new ParseFailure("");
    }

    //    jarray = '[' [jvalue {',' jvalue}] ']';
    private JSONNode jarray() {
        List<JSONNode> nodes = new ArrayList<JSONNode>();
        try {
            save();
            accept('[');

            for (;;) {
                if (position >= input.length()) {
                    throw new ParseFailure("");
                }

                char ch = input.charAt(position);

                if (ch == ']') {
                    position += 1;
                    return JSONNode.jarray(nodes.toArray(new JSONNode[nodes.size()]));
                } else if (ch == ',') {
                    position += 1;
                }

                JSONNode newNode = jvalue();
                nodes.add(newNode);
            }
        } catch (ParseFailure e) {
            restore();
            throw new ParseFailure("");
        }
    }

    //    jvalue = jobject | jarray | jboolean |
    //            | jnull | jstring | jnumber;
    private JSONNode jvalue() {
        try {
            save();
            JSONNode node = jobject();
            return node;
        } catch (ParseFailure e) {
            restore();
        }
        try {
            save();
            JSONNode node = jarray();
            return node;
        } catch (ParseFailure e) {
            restore();
        }
        try {
            save();
            JSONNode node = jboolean();
            return node;
        } catch (ParseFailure e) {
            restore();
        }
        try {
            save();
            JSONNode node = jnull();
            return node;
        } catch (ParseFailure e) {
            restore();
        }
        try {
            save();
            JSONNode node = jstring();
            return node;
        } catch (ParseFailure e) {
            restore();
        }
        try {
            save();
            JSONNode node = jnumber();
            return node;
        } catch (ParseFailure e) {
            restore();
        }

        throw new ParseFailure("");
    }

    //    jboolean = 'true' | 'false';
    private JSONNode jboolean() {
        try {
            save();
            acceptString("true");
            return JSONNode.jboolean(true);
        } catch (ParseFailure e) {
            restore();
            try {
                save();
                acceptString("false");
                return JSONNode.jboolean(false);
            } catch (ParseFailure e2) {
                restore();
                throw new ParseFailure("");
            }
        }
    }

    //    jnull = 'null';
    private JSONNode jnull() {
        try {
            save();
            acceptString("null");
            return JSONNode.jnull();
        } catch (ParseFailure e) {
            restore();
            throw new ParseFailure("");
        }
    }

    //    jnumber = integer;
    private JSONNode jnumber() {
        int n = integer();
        return JSONNode.jnumber(n);
    }

    //    jstring = '"' ... '"';
    private JSONNode jstring() {
        try {
            save();

            accept('"');

            StringBuilder content = new StringBuilder();
            for (;;) {
                if (position >= input.length()) {
                    throw new ParseFailure("");
                }

                char ch = accept();

                switch (ch) {
                    case '"':
                        return JSONNode.jstring(content.toString());
                    case '\\':
                        content.append(specialCharacter());
                        break;
                    default:
                        content.append(ch);
                }
            }

        } catch (ParseFailure e) {
            restore();
        }
        throw new ParseFailure("");
    }

    private char specialCharacter() {
        char chAfterBackslash = accept();
        switch (chAfterBackslash) {
            case 'r':
                return '\r';
            case 'n':
                return '\n';
            case 'b':
                return '\b';
            case 'f':
                return '\f';
            case '\\':
                return '\\';
            case '"':
                return '"';
        }
        throw new ParseFailure("");
    }

    private void acceptString(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (position >= input.length()) {
                throw new ParseFailure("");
            }

            if (input.charAt(position) != str.charAt(i)) {
                throw new ParseFailure("");
            }

            position += 1;
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

    public char accept() {
        if (position >= input.length()) {
            throw new ParseFailure("");
        }
        char ch = input.charAt(position);
        position += 1;
        return ch;
    }

    private void accept(char ch) {
        if (position >= input.length() ||  input.charAt(position) != ch) {
            throw new ParseFailure("");
        }
        position += 1;
    }
}

