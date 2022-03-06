package extend;

import java.util.HashMap;

public class Env extends HashMap<String, Exp> {
    public Env outer;

    public Env() {
    }

    public Env(MyList params, MyList args, Env outer) {
        this.clear();
        for (int i = 0; i < params.list.size(); i++) {
            this.put(String.valueOf(params.list.get(i)), args.list.get(i));
        }
        this.outer = outer;
    }

    public Env find(String key) {
        var res = this.get(key);
        if (res != null) {
            return this;
        } else {
            try {
                return outer.find(key);
            } catch (NullPointerException e) {
                throw new RuntimeException("can`t find " + key);
            }
        }
    }
}