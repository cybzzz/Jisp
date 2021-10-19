package lisp;

public abstract sealed class Exp
        permits Atom, MyList, MyBoolean, Procedure {
}
