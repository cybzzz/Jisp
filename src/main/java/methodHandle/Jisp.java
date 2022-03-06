package methodHandle;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.range;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

public class Jisp {
    public interface Fun {
        Object apply(Object a);
    }

    public interface Fun2 {
        Object apply(Object a, Object b);
    }

    public interface FunAll {
        Object apply(Object[] args);
    }

    static MethodHandle mhRef(Class<?> type, String name) {
        try {
            return MethodHandles.publicLookup().unreflect(stream(type.getMethods()).filter(m -> m.getName().equals(name)).findFirst().get());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static <F> MethodHandle mh(Class<F> type, F fun) {
        return mhRef(type, "apply").bindTo(fun);
    }

    @SuppressWarnings("unchecked")
    static int compare(Object a, Object b) {
        return ((Comparable<Object>) a).compareTo(b);
    }

    static List<?> list(Object o) {
        return (List<?>) o;
    }

    static String string(Object o) {
        return (String) o;
    }

    static double dbl(Object o) {
        return ((Number) o).doubleValue();
    }

    static BigInteger bigint(Object o) {
        return ((BigInteger) o);
    }

    static boolean isDouble(Object o) {
        return o instanceof Double;
    }

    public static class Env {
        final HashMap<String, Object> dict = new HashMap<>();
        private final Env outer;

        Env(Env outer) {
            this.outer = outer;
        }

        Env find(String var) {
            return dict.containsKey(var) ? this : outer.find(var);
        }

        Env add(String var, Object value) {
            dict.put(var, value);
            return this;
        }

        Env addAll(List<?> vars, List<?> values) {
            range(0, vars.size()).forEach(i -> add(string(vars.get(i)), values.get(i)));
            return this;
        }
    }

    public static Env globalEnv() {
        return new Env(null)
                .add("+", mh(FunAll.class, args -> Arrays.stream(args).reduce(BigInteger.ZERO, (a, b) -> (isDouble(a) || isDouble(b)) ? dbl(a) + dbl(b) : bigint(a).add(bigint(b))))
                        .asVarargsCollector(Object[].class))
                .add("-", mh(Fun2.class, (a, b) -> (isDouble(a) || isDouble(b)) ? dbl(a) - dbl(b) : bigint(a).subtract(bigint(b))))
                .add("*", mh(FunAll.class, args -> Arrays.stream(args).reduce(BigInteger.ONE, (a, b) -> (isDouble(a) || isDouble(b)) ? dbl(a) * dbl(b) : bigint(a).multiply(bigint(b))))
                        .asVarargsCollector(Object[].class))
                .add("/", mh(Fun2.class, (a, b) -> (isDouble(a) || isDouble(b)) ? dbl(a) / dbl(b) : bigint(a).divide(bigint(b))))
                .add("<", mh(Fun2.class, (a, b) -> compare(a, b) < 0))
                .add("<=", mh(Fun2.class, (a, b) -> compare(a, b) <= 0))
                .add(">", mh(Fun2.class, (a, b) -> compare(a, b) > 0))
                .add(">=", mh(Fun2.class, (a, b) -> compare(a, b) >= 0))
                .add("=", mhRef(Object.class, "equals"))
                .add("equal?", mhRef(Object.class, "equals"))
                .add("eq?", mh(Fun2.class, (o, c) -> (((Class<?>) c).isInstance(o))))
                .add("length", mhRef(List.class, "size"))
                .add("cons", mh(Fun2.class, (a, l) -> concat(of(a), list(l).stream()).collect(toList())))
                .add("car", mh(Fun.class, l -> list(l).get(0)))
                .add("cdr", mh(Fun.class, l -> list(l).subList(1, list(l).size())))
                .add("append", mh(Fun2.class, (l, m) -> concat(list(l).stream(), list(m).stream()).collect(toList())))
                .add("list", mh(FunAll.class, Arrays::asList).asVarargsCollector(Object[].class))
                .add("list?", mh(Fun.class, l -> l instanceof List))
                .add("null?", mhRef(List.class, "isEmpty"))
                .add("symbol?", mh(Fun.class, a -> a instanceof String))
                .add("not", mh(Fun.class, a -> !(boolean) a));
    }

    @SuppressWarnings("unchecked")
    public static Object eval(Object x, Env env) {
        if (x instanceof String) {             // variable reference
            return env.find(string(x)).dict.get(x);
        }
        if (!(x instanceof List)) {            // constant
            return x;
        }
        List<Object> l = (List<Object>) x;
        String var;
        Object exp, cmd = l.get(0);
        if (cmd instanceof String) {
            switch (string(l.get(0))) {
                // (quote exp)
                case "quote":
                    return l.get(1);
                // (if test conseq alt)
                case "if":
                    return eval(((Boolean) eval(l.get(1), env)) ? l.get(2) : l.get(3), env);
                // (set! var exp)
                case "set!":
                    var = string(l.get(1));
                    env.find(var).add(var, eval(l.get(2), env));
                    return null;
                // (define var exp)
                case "define":
                    var = string(l.get(1));
                    env.add(var, eval(l.get(2), env));
                    return null;
                // (lambda (vars) exp)
                case "lambda":
                    List<?> vars = list(l.get(1));
                    exp = l.get(2);
                    return mh(FunAll.class, args -> eval(exp, new Env(env).addAll(vars, asList(args)))).asCollector(Object[].class, vars.size());
                // (begin exp*)
                case "begin":
                    return l.stream().skip(1).reduce(null, (val, e) -> eval(e, env));
                default:
            }
        }
        List<?> exprs = l.stream().map(expr -> eval(expr, env)).collect(toList());
        MethodHandle proc = (MethodHandle) exprs.get(0);
        try {
            return proc.invokeWithArguments(exprs.subList(1, exprs.size()));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Object parse(String s) {
        return readFrom(tokenize(s));
    }

    static Queue<String> tokenize(String text) {
        return stream(text.replace("(", "( ").replace(")", " )").split(" ")).filter(s -> !s.isEmpty()).collect(toCollection(ArrayDeque::new));
    }

    static Object readFrom(Queue<String> tokens) {
        if (tokens.isEmpty()) {
            throw new Error("unexpected EOF while reading");
        }
        String token = tokens.poll();
        if ("(".equals(token)) {
            ArrayList<Object> l = new ArrayList<>();
            while (!")".equals(tokens.peek())) {
                l.add(readFrom(tokens));
            }
            tokens.poll();   // pop of ")"
            return l;
        }
        if (")".equals(token)) {
            return new Error("unexpected ')'");
        }
        return atom(token);
    }

    static Object atom(String token) {
        try {
            return new BigInteger(token);
        } catch (NumberFormatException __) {
            try {
                return Double.parseDouble(token);
            } catch (NumberFormatException ___) {
                return token;
            }
        }
    }

    static String toString(Object val) {
        return (val instanceof List) ? list(val).stream().map(Jisp::toString).collect(joining(" ", "(", ")")) : String.valueOf(val);
    }

    static void repl() {
        Scanner scanner = new Scanner(System.in);
        Env env = globalEnv();
        while (true) {
            System.out.print("Jisp > ");
            String s = scanner.nextLine();
            if ("exit".equals(s)) {
                System.out.println("bye");
                break;
            }
            Object val = eval(parse(s), env);
            if (val != null) {
                System.out.println(toString(val));
            }
        }
    }

    public static void main(String[] args) {
        repl();
    }
}