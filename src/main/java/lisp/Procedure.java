package lisp;

/**
 * @author cyb
 */
public final class Procedure extends Exp {
    public MyList params;
    public Exp body;
    public Env env;

    public Procedure() {
    }

    public Procedure(MyList params, Exp body, Env env) {
        this.params = params;
        this.body = body;
        this.env = env;
    }

    public Exp call(MyList args) {
        Eval eval = new Eval();
        return eval.eval(body, new Env(this.params, args, this.env));
    }
}
