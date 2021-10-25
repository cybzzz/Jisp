package lisp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author cyb
 */
public class Parse {

    public Parse() {}

    public MyList tokenize(String input) {
        String[] strings = input.replace("(", " ( ").replace(")", " ) ").split(" ");
        return new MyList(Arrays.stream(strings).filter(s -> !"".equals(s)).map(MyString::new).collect(Collectors.toCollection(ArrayList::new)));
    }

    public Exp parse(String input) {
        return read_from_token(tokenize(input));
    }

    public Exp read_from_token(MyList myList) {
        var tokens = myList.list;
        if (tokens.size() == 0) {
            throw new RuntimeException("unexpected EOF");
        }
        String token = tokens.get(0).toString();
        tokens.remove(0);
        if ("(".equals(token)) {
            ArrayList<Exp> L = new ArrayList<>();
            while (!")".equals(tokens.get(0).toString())) {
                Exp res = read_from_token(myList);
                L.add(res);
            }
            tokens.remove(0);
            return new MyList(L);
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
