package lisp;

import java.util.Objects;

public final class MyString extends Atom {
    public String string;

    public MyString() {
    }

    public MyString(String s) {
        this.string = s;
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MyString myString)) {
            return false;
        }

        return Objects.equals(string, myString.string);
    }

    @Override
    public int hashCode() {
        return string != null ? string.hashCode() : 0;
    }
}
