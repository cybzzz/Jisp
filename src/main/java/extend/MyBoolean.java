package extend;

import java.util.Objects;

public final class MyBoolean extends Exp {
    public Boolean bool;

    public MyBoolean() {
    }

    public MyBoolean(boolean bool) {
        this.bool = bool;
    }

    @Override
    public String toString() {
        return bool.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MyBoolean myBoolean)) {
            return false;
        }

        return Objects.equals(bool, myBoolean.bool);
    }

    @Override
    public int hashCode() {
        return bool != null ? bool.hashCode() : 0;
    }
}
