package tlang.core;

import tlang.core.func.ApplyVoidFunc;
import tlang.core.func.MapFunc;

public class Either<T extends Value<T>, U extends Value<U>> {

    private final T left;
    private final U right;

    private Either(T left, U right) {
        this.left = left;
        this.right = right;
    }

    public static <T extends Value<T>, U extends Value<U>> Either<T, U> left(T value) {
        return new Either<>(value, null);
    }

    public static <T extends Value<T>, U extends Value<U>> Either<T, U> right(U value) {
        return new Either<>(null, value);
    }

    public boolean isLeft() {
        return left != null;
    }

    public boolean isRight() {
        return right != null;
    }

    public T getLeft() {
        return left;
    }

    public U getRight() {
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

    public void applyLeft(ApplyVoidFunc<T> func) {
        if (isLeft()) {
            func.apply(getLeft());
        }
    }

    public void applyRight(ApplyVoidFunc<U> func) {
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
