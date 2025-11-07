package exception;

public class NicknameYaExisteException extends RuntimeException {
    public NicknameYaExisteException() {
        super();
    }
}
// Aquí no se utiliza "extends Exception" como en las otras excepciones porque no es un error que dependa del estado del
// sistema o de la BBDD, sino del usuario.  No es como si se hace una petición perfectamente válida para recuperar un usuario,
// (GET /usuarios/5), pero el sistema no puede recuperarla porque no existe.
// En este caso usamos Runtime Exception porque el error se debe a un dato incorrecto o a una acción no válida del usuario.

