package lisp;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * @author cyb
 */
public class Eval {

    public Exp eval(Exp x, Env env) {
        if (x instanceof MyString) {
            return env.find(x.toString());
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
                case "quote":
                    return args.get(0);
                case "if":
                    if (args.size() != 3) {
                        throw new RuntimeException("unexpected parameters");
                    }
                    Function<MyList, Exp> myIf = myList -> {
                        var test = myList.list.get(1);
                        var consequence = myList.list.get(2);
                        var alt = myList.list.get(3);
                        if (((MyBoolean) eval(test, env)).bool) {
                            return (Exp) consequence;
                        } else {
                            return (Exp) alt;
                        }
                    };
                    var res = myIf.apply((MyList) x);
                    return eval(res, env);
                case "define":
                    env.put(args.get(0).toString(), eval(args.get(1), env));
                    break;
                /*case "set!":
                 */
                case "lambda":
                    return new Procedure((MyList) args.get(0), args.get(1), env);
                default:
                    Exp proc = eval(op, env);
                    for (int i = 0; i < args.size(); i++) {
                        Exp arg = eval(args.get(i), env);
                        args.set(i, arg);
                    }
                    MyList values = new MyList(args);
                    if (proc instanceof MyFunction) {
                        return ((MyFunction) proc).function.apply(values);
                    } else {
                        return ((Procedure) proc).call(values);
                    }
            }
        }
        return null;
    }
}
