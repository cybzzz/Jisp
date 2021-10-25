package lisp;

import java.util.HashMap;

/**
 * @author cyb
 */
public class Env extends HashMap<String, Exp> {
    public Env outer;

    public Env() {}

    public Env(MyList params, MyList args, Env outer) {
        this.clear();
        for (int i = 0; i < params.list.size(); i++) {
            this.put(String.valueOf(params.list.get(i)), args.list.get(i));
        }
        this.outer = outer;
    }

    public HashMap<Exp, Env> find(String key) {
        var res = this.get(key);
        if (res != null) {
            HashMap<Exp, Env> map = new HashMap<>(1);
            map.put(res, this);
            return map;
        } else {
            try {
                return outer.find(key);
            } catch (NullPointerException e) {
                throw new RuntimeException("can`t find " + key);
            }
        }
    }
}