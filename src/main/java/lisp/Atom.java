package lisp;

public abstract sealed class Atom extends Exp
        permits MyDouble, MyString, MyFunction {
}
