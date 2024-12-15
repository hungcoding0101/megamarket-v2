package hung.megamarketv2.common.generic.outcomes;

public class Outcome<E> {
    public final E error;
    public final boolean isSuccessful;

    private Outcome(E error, boolean isSuccessful) {
        this.error = error;
        this.isSuccessful = isSuccessful;
    }

    public static <E> Outcome<E> ofSuccess() {
        return new Outcome<>(null, true);
    }

    public static <E> Outcome<E> ofError(E error) {
        return new Outcome<>(error, false);
    }
}