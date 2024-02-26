package tlang.core;

import tlang.core.func.ApplyVoidFunc;

public class Either {

    private final Value left;
    private final Value right;

    private Either(Value left, Value right) {
        this.left = left;
        this.right = right;
    }

    public static Either left(Value value) {
        return new Either(value, null);
    }

    public static Either right(Value value) {
        return new Either(null, value);
    }

    public boolean isLeft() {
        return left != null;
    }

    public boolean isRight() {
        return right != null;
    }

    public Value getLeft() {
        return left;
    }

    public Value getRight() {
        return right;
    }

//    public <B extends Value<B>> Either<B, U> mapLeft(MapFunc<T, B> func) {
//        if (isLeft()) {
//            return Either.left(func.apply(getLeft()));
//        }
//        return Either.right(getRight());
//    }
//
//    public <B extends Value<B>> Either<T, B> mapRight(MapFunc<U, B> func) {
//        if (isRight()) {
//            return Either.right(func.apply(getRight()));
//        }
//        return Either.left(getLeft());
//    }

    public void applyLeft(ApplyVoidFunc func) {
        if (isLeft()) {
            func.apply(getLeft());
        }
    }

    public void applyRight(ApplyVoidFunc func) {
        if (isRight()) {
            func.apply(getRight());
        }
    }

//    public <B> B fold(MapFunc<T, B> leftFunc, MapFunc<U, B> rightFunc) {
//        if (isLeft()) {
//            return leftFunc.apply(getLeft());
//        }
//        return rightFunc.apply(getRight());
//    }
}
