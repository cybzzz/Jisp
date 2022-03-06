package extend;

import java.util.function.Function;

public final class MyFunction extends Atom {
    public Function<MyList, Exp> function;

    public MyFunction() {
    }

    public MyFunction(Function<MyList, Exp> function) {
        this.function = function;
    }
}
