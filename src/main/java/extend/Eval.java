package extend;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * @author cyb
 */
public class Eval {

    public Exp eval(Exp x, Env env) {
        if (x instanceof MyString) {
            return env.find(((MyString) x).string).get(((MyString) x).string);
        } else if (!(x instanceof MyList)) {
            return x;
        } else {
            var list = ((MyList) x).list;
            var op = list.get(0);
            var args = new ArrayList<Exp>();
            if (list.size() > 1) {
                args.addAll(list.subList(1, list.size()));
            }
            switch (op.toString()) {
                // (quote exp)
                case "quote":
                    return args.get(0);
                // (if test conseq alt)
                case "if":
                    if (args.size() != 3) {
                        throw new RuntimeException("unexpected parameters");
                    }
                    var test = args.get(0);
                    var consequence = args.get(1);
                    var alt = args.get(2);
                    if (((MyBoolean) eval(test, env)).bool) {
                        return eval(consequence, env);
                    } else {
                        return eval(alt, env);
                    }
                    // (define var exp)
                case "define":
                    env.put(args.get(0).toString(), eval(args.get(1), env));
                    break;
                // (set! var exp)
                case "set!":
                    String temp = ((MyString) args.get(0)).string;
                    var tempE = env.find(temp);
                    tempE.put(temp, eval(args.get(1), env));
                    break;
                // (lambda (vars) exp)
                case "lambda":
                    return new MyFunction(aList -> eval(args.get(1), new Env((MyList) args.get(0), aList, env)));
//                    return new Procedure((MyList) args.get(0), args.get(1), env);
                // (begin exp*)
                case "begin":
                    return args.stream().reduce(null, (val, e) -> eval(e, env));
                default:
                    Exp proc = eval(op, env);
                    for (int i = 0; i < args.size(); i++) {
                        Exp arg = eval(args.get(i), env);
                        args.set(i, arg);
                    }
                    MyList values = new MyList(args);
                    return ((MyFunction) proc).function.apply(values);
            }
        }
        return null;
    }

    public static void main(String[] args) {
        Parse parse = new Parse();
        Eval eval = new Eval();
        var globalEnv = initEnv();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Jisp > ");
            String s = scanner.nextLine();
            if ("exit".equals(s)) {
                break;
            }
            Exp exp = eval.eval(parse.parse(s), globalEnv);
            if (exp != null) {
                System.out.println(exp);
            }
        }
    }

    public static Env initEnv() {
        Env env = new Env();
        env.put("pi", new MyDouble(Math.PI));

        env.put("*", new MyFunction(list -> new MyDouble(list.list.stream().mapToDouble(e -> ((MyDouble) e).num).reduce(1, (a, b) -> a * b))));
        env.put("+", new MyFunction(list -> new MyDouble(list.list.stream().mapToDouble(e -> ((MyDouble) e).num).reduce(0, Double::sum))));
        env.put("-", new MyFunction(list -> new MyDouble(((MyDouble) list.list.get(0)).num - list.list.stream().mapToDouble(e -> ((MyDouble) e).num).skip(1).reduce(0, Double::sum))));
        env.put("/", new MyFunction(list -> new MyDouble(((MyDouble) list.list.get(0)).num / list.list.stream().mapToDouble(e -> ((MyDouble) e).num).skip(1).reduce(1, (a, b) -> a * b))));

        env.put("=", new MyFunction(list -> new MyBoolean((list.list.get(0)).equals((list.list.get(1))))));
        env.put("<", new MyFunction(list -> new MyBoolean(((MyDouble) list.list.get(0)).num < ((MyDouble) list.list.get(1)).num)));
        env.put(">", new MyFunction(list -> new MyBoolean(((MyDouble) list.list.get(0)).num > ((MyDouble) list.list.get(1)).num)));

        env.put("car", new MyFunction(list -> ((MyList) list.list.get(0)).list.get(0)));
        env.put("cdr", new MyFunction(list -> new MyList(((MyList) list.list.get(0)).list.stream().skip(1).collect(Collectors.toCollection(ArrayList::new)))));
        env.put("list", new MyFunction(list -> list));
        env.put("exist?", new MyFunction(list -> new MyBoolean(!((MyList) list.list.get(0)).list.isEmpty())));
        env.put("equal?", new MyFunction(list -> new MyBoolean(list.list.get(0).equals(list.list.get(1)))));
        env.put("not", new MyFunction(list -> new MyBoolean(!((MyBoolean) list.list.get(0)).bool)));

        return env;
    }
}
