package lisp;

import java.util.ArrayList;

/**
 * @author cyb
 */
public class Eval {

    public Exp eval(Exp x, Env env) {
        if (x instanceof MyString) {
            for (Exp exp : env.find(x.toString()).keySet()) {
                return exp;
            }
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
                    var test = args.get(0);
                    var consequence = args.get(1);
                    var alt = args.get(2);
                    if (((MyBoolean) eval(test, env)).bool) {
                        return eval(consequence, env);
                    } else {
                        return eval(alt, env);
                    }
                case "define":
                    env.put(args.get(0).toString(), eval(args.get(1), env));
                    break;
                case "set!":
                    for (Env env1 : env.find(args.get(0).toString()).values()) {
                        env1.put(args.get(0).toString(), eval(args.get(1), env));
                    }
                    break;
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
