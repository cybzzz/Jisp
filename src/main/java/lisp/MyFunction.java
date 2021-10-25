package lisp;

import java.util.function.Function;

/**
 * @author cyb
 */
public final class MyFunction extends Atom {
    public Function<MyList, Exp> function;

    public MyFunction() {}

    public MyFunction(Function<MyList, Exp> function) {
        this.function = function;
    }
}
