package ma.dentalTech.common.exceptions;

public class DaoException extends RuntimeException {
    public DaoException(String m) {
        super(m);
    }

    public DaoException(String m, Throwable t) {
        super(m, t);
    }
}
