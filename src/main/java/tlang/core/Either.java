package tlang.core;

import tlang.core.func.ApplyFunc;
import tlang.core.func.MapFunc;

public class Either<T, U> {

    private final T left;
    private final U right;

    private Either(T left, U right) {
        this.left = left;
        this.right = right;
    }

    public static <T, U> Either<T, U> left(T value) {
        return new Either<>(value, null);
    }

    public static <T, U> Either<T, U> right(U value) {
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

    public <B> Either<B, U> mapLeft(MapFunc<T, B> func) {
        if (isLeft()) {
            return Either.left(func.apply(getLeft()));
        }
        return Either.right(getRight());
    }

    public <B> Either<T, B> mapRight(MapFunc<U, B> func) {
        if (isRight()) {
            return Either.right(func.apply(getRight()));
        }
        return Either.left(getLeft());
    }

    public void applyLeft(ApplyFunc<T> func) {
        if (isLeft()) {
            func.apply(getLeft());
        }
    }

    public void applyRight(ApplyFunc<U> func) {
        if (isRight()) {
            func.apply(getRight());
        }
    }

    public <B> B fold(MapFunc<T, B> leftFunc, MapFunc<U, B> rightFunc) {
        if (isLeft()) {
            return leftFunc.apply(getLeft());
        }
        return rightFunc.apply(getRight());
    }
}
