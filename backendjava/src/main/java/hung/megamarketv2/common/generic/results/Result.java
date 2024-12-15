package hung.megamarketv2.common.generic.results;

public class Result<V, E> {
    public final V value;
    public final E error;
    public final boolean isSuccessful;

    private Result(V value, E error, boolean isSuccessful) {
        this.value = value;
        this.error = error;
        this.isSuccessful = isSuccessful;
    }

    public static <V, E> Result<V, E> ofValue(V value) {
        return new Result<>(value, null, true);
    }

    public static <V, E> Result<V, E> ofError(E error) {
        return new Result<>(null, error, false);
    }
}