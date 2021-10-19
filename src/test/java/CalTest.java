import lisp.*;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CalTest {
    public static void main(String[] args) {
        Parse parse = new Parse();
        Eval eval = new Eval();
        var global_env = initEnv();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("lisp > ");
            String s = scanner.nextLine();
            if (s.equals("exit")) {
                break;
            }
            Exp exp = eval.eval(parse.parse(s), global_env);
            if (exp != null) {
                System.out.println(exp);
            }
        }
    }

    private static Env initEnv() {
        Env env = new Env();
        env.put("x", new MyDouble(5));
        env.put("pi", new MyDouble(Math.PI));

        env.put("*", new MyFunction(list -> new MyDouble(list.list.stream().mapToDouble(e -> ((MyDouble) e).num).reduce(1, (a, b) -> a * b))));
        env.put("+", new MyFunction(list -> new MyDouble(list.list.stream().mapToDouble(e -> ((MyDouble) e).num).reduce(0, Double::sum))));
        env.put("-", new MyFunction(list -> new MyDouble(((MyDouble)list.list.get(0)).num - list.list.stream().mapToDouble(e -> ((MyDouble) e).num).skip(1).reduce(0, Double::sum))));
        env.put("/", new MyFunction(list -> new MyDouble(((MyDouble)list.list.get(0)).num / list.list.stream().mapToDouble(e -> ((MyDouble) e).num).skip(1).reduce(1, (a, b) -> a * b))));

        env.put("=", new MyFunction(list -> new MyBoolean((list.list.get(0)).equals((list.list.get(1))))));
        env.put("<", new MyFunction(list -> new MyBoolean(((MyDouble) list.list.get(0)).num < ((MyDouble) list.list.get(1)).num)));
        env.put(">", new MyFunction(list -> new MyBoolean(((MyDouble) list.list.get(0)).num > ((MyDouble) list.list.get(1)).num)));

        env.put("first", new MyFunction(list -> ((MyList)list.list.get(0)).list.get(0)));
        env.put("rest", new MyFunction(list -> new MyList(((MyList)list.list.get(0)).list.stream().skip(1).collect(Collectors.toCollection(ArrayList::new)))));
        env.put("list", new MyFunction(list -> list));
        env.put("exist?", new MyFunction(list -> new MyBoolean(((MyList)list.list.get(0)).list.size() != 0)));

        return env;
    }
}
