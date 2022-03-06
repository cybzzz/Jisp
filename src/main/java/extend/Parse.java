package extend;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.stream.Collectors;

/**
 * @author cyb
 */
public class Parse {

    public Parse() {
    }

    public Deque<MyString> tokenize(String input) {
        String[] strings = input.replace("(", " ( ").replace(")", " ) ").split(" ");
        return Arrays.stream(strings).filter(s -> !"".equals(s)).map(MyString::new).collect(Collectors.toCollection(ArrayDeque<MyString>::new));
    }

    public Exp parse(String input) {
        return readFromToken(tokenize(input));
    }

    public Exp readFromToken(Deque<MyString> tokens) {
        if (tokens.size() == 0) {
            throw new RuntimeException("unexpected EOF");
        }
        String token = tokens.poll().string;
        if ("(".equals(token)) {
            ArrayList<Exp> l = new ArrayList<>();
            while (!")".equals(tokens.peek().string)) {
                Exp res = readFromToken(tokens);
                l.add(res);
            }
            // 删除 ")"
            tokens.poll();
            return new MyList(l);
        } else if (")".equals(token)) {
            throw new RuntimeException("unexpected )");
        } else {
            return atom(token);
        }
    }

    public Atom atom(String token) {
        try {
            double parseDouble = Double.parseDouble(token);
            var resD = new MyDouble();
            resD.num = parseDouble;
            return resD;
        } catch (NumberFormatException exception) {
            var resS = new MyString();
            resS.string = token;
            return resS;
        }
    }
}
