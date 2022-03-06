package extend;

/**
 * @author cyb
 * 原子类
 */
public abstract sealed class Atom extends Exp
        permits MyDouble, MyString, MyFunction {
}
