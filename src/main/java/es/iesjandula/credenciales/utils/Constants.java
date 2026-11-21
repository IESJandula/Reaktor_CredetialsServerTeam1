package es.iesjandula.credenciales.utils;

/**
 * Clase que contiene los códigos de error de las excepciones
 * y los valores usados en las validaciones
 */
public class Constants
{
    // Genéricos

    /** Error no controlado durante la petición (HTTP 500) */
    public static final String ERROR_GENERIC = "ERROR_GENERIC";

    /** El objeto recibido en el body es nulo */
    public static final String ERROR_OBJETO_VACIO = "ERROR_OBJETO_VACIO";

    // Credencial

    /** El usuario es nulo o vacío */
    public static final String ERROR_USUARIO_NULO_O_VACIO = "ERROR_USUARIO_NULO_O_VACIO";

    /** La descripción es nula o vacía */
    public static final String ERROR_DESCRIPCION_NULA_O_VACIA = "ERROR_DESCRIPCION_NULA_O_VACIA";

    /** La ubicación y la url son nulas o vacías, al menos una debe estar rellena */
    public static final String ERROR_UBICACION_Y_URL_NULAS_O_VACIAS = "ERROR_UBICACION_Y_URL_NULAS_O_VACIAS";

    /** No existe ninguna credencial que coincida con el filtro */
    public static final String ERROR_UBICACION_NO_EXISTE = "ERROR_UBICACION_NO_EXISTE";

    /** El filtro coincide con más de una credencial */
    public static final String ERROR_CREDENCIAL_DUPLICADA = "ERROR_CREDENCIAL_DUPLICADA";

    // Password

    /** La contraseña es nula o vacía */
    public static final String ERROR_PASSWORD_NULA_O_VACIA = "ERROR_PASSWORD_NULA_O_VACIA";

    /** La contraseña no cumple los requisitos de seguridad */
    public static final String ERROR_PASSWORD_INCORRECTA = "ERROR_PASSWORD_INCORRECTA";

    // Validaciones

    /** Número mínimo de caracteres que debe tener la contraseña */
    public static final int PASSWORD_LONGITUD_MINIMA = 8;
}
