package extend;

import java.util.ArrayList;

public final class MyList extends Exp {
    public ArrayList<Exp> list;

    public MyList() {
    }

    public MyList(ArrayList<Exp> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "MyList-" + list.toString() + " " + list.size();
    }
}
