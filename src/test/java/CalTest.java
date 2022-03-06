import extend.*;
import methodHandle.Jisp;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class CalTest {
    @Test
    public void extendTest() {
        Parse parse = new Parse();
        Eval eval = new Eval();
        Env env = Eval.initEnv();

        eval.eval(parse.parse("(define fact (lambda (n) (if (= n 0) 1 (* n (fact (- n 1))))))"), env);
        Exp exp = eval.eval(parse.parse("(fact 10)"), env);
        int res = ((MyDouble) exp).num.intValue();
        assertEquals(res, 3628800);

        eval.eval(parse.parse("(define twice (lambda (x) (* 2 x)))"), env);
        exp = eval.eval(parse.parse("(twice 5)"), env);
        res = ((MyDouble) exp).num.intValue();
        assertEquals(res, 10);
    }

    @Test
    public void methodHandleTest() {
        Jisp.Env env = Jisp.globalEnv();
        Jisp.eval(Jisp.parse("(define eq?toNum (lambda (x y) (if (equal? x y) 1 0)))"), env);
        Jisp.eval(Jisp.parse("(define count (lambda (item L) (if (not (null? L)) (+ (eq?toNum item (car L)) (count item (cdr L))) 0)))"), env);
        Object o = Jisp.eval(Jisp.parse("(count 0 (list 0 1 2 3 0 0))"), env);
        assertEquals(o, new BigInteger("3"));

        Jisp.eval(Jisp.parse("(define fib (lambda (n) (if (< n 2) 1 (+ (fib (- n 1)) (fib (- n 2))))))"), env);
        o = Jisp.eval(Jisp.parse("(fib 10)"), env);
        assertEquals(o, new BigInteger("89"));

        Jisp.eval(Jisp.parse("(define range (lambda (a b) (if (= a b) (quote ()) (cons a (range (+ a 1) b)))))"), env);
        o = Jisp.eval(Jisp.parse("(range 0 10)"), env);
        assertEquals(o.toString(), "[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]");
    }
}
