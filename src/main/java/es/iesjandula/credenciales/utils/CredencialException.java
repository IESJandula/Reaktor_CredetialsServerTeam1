package es.iesjandula.credenciales.utils;

import lombok.Data;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.HashMap;
import java.util.Map;

@Data
public class CredencialException extends Exception
{
    private String code;

    /**
     * Constructor con código de error y mensaje
     * @param code
     * @param message
     */
    public CredencialException(String code, String message)
    {
        super(message);
        this.code = code;
    }

    /**
     * Constructor con código de error, mensage y causa original del error
     * @param code
     * @param message
     * @param exception
     */
    public CredencialException(String code, String message, Throwable exception)
    {
        super(message, exception);
        this.code = code;
    }

    /**
     * Devuelve un mapa con los detalles de la excepción con el formato
     * CredencialError del swagger: codigo, mensaje y stackTrace de la causa si existe.
     * @return
     */
    public Object getBodyExceptionMessage()
    {
        Map<String, Object> mapBodyException = new HashMap<>();
        mapBodyException.put("codigo", this.code);
        mapBodyException.put("mensaje", this.getMessage());

        if (this.getCause() != null)
        {
            String stackTrace = ExceptionUtils.getStackTrace(this.getCause());
            mapBodyException.put("stackTrace", stackTrace);
        }

        return mapBodyException;
    }
}