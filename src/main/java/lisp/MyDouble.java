package lisp;

import java.util.Objects;

public final class MyDouble extends Atom {
    public Double num;

    public MyDouble() {}

    public MyDouble(double num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return num.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MyDouble myDouble)) {
            return false;
        }

        return Objects.equals(num, myDouble.num);
    }

    @Override
    public int hashCode() {
        return num != null ? num.hashCode() : 0;
    }
}
