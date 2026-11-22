package es.iesjandula.credenciales.rest;

import es.iesjandula.credenciales.dtos.CredencialDTO;
import es.iesjandula.credenciales.dtos.CredencialPassDTO;
import es.iesjandula.credenciales.services.CredencialService;
import es.iesjandula.credenciales.utils.Constants;
import es.iesjandula.credenciales.utils.CredencialException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * Controlador REST que expone los endpoints de /credentials.
 * Delega la lógica en CredencialService y traduce las CredencialException
 * a los códigos HTTP definidos en el swagger
 */
@RequestMapping(value = "/credentials")
@RestController
@Slf4j
public class CredencialController
{
    /** Servicio con la lógica de negocio de las credenciales */
    @Autowired
    private CredencialService credencialService;

    /**
     * POST /credentials: crea una nueva credencial con su contraseña
     * @param credencialPassDTO datos de la credencial y contraseña
     * @return 200 si se crea, 400-406 si falla una validación o 500 si hay un error inesperado
     */
    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<?> crearCredencial(@RequestBody CredencialPassDTO credencialPassDTO)
    {
        try
        {
            this.credencialService.crearCredencial(credencialPassDTO);
            return ResponseEntity.status(200).build();
        }
        catch (CredencialException credencialException)
        {
            // Error de validación controlado: se traduce a su código HTTP
            return ResponseEntity.status(this.obtenerStatusPost(credencialException.getCode())).body(credencialException.getBodyExceptionMessage());
        }
        catch (Exception exception)
        {
            // Error no controlado: se envuelve en una CredencialException genérica
            String errorString = "Error mientras se registraba una credencial";
            CredencialException credencialException = new CredencialException(Constants.ERROR_GENERIC, errorString, exception);

            log.error(errorString, credencialException);
            return ResponseEntity.status(500).body(credencialException.getBodyExceptionMessage());
        }
    }

    /**
     * GET /credentials: lista todas las credenciales sin la contraseña
     * @return 200 con la lista (vacía si no hay) o 500 si hay un error inesperado
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> listarCredenciales()
    {
        try
        {
            List<CredencialDTO> listaCredenciales = this.credencialService.listarCredenciales();
            return ResponseEntity.status(200).body(listaCredenciales);
        }
        catch (Exception exception)
        {
            String errorString = "Error mientras se listaban las credenciales";
            CredencialException credencialException = new CredencialException(Constants.ERROR_GENERIC, errorString, exception);

            log.error(errorString, credencialException);
            return ResponseEntity.status(500).body(credencialException.getBodyExceptionMessage());
        }
    }

    /**
     * DELETE /credentials: elimina las credenciales del usuario que coinciden
     * con la ubicación y/o la url indicadas
     * @param credencialDTO filtro con usuario, ubicación y url
     * @return 200 si se eliminan, 400-402 si falla el filtro o 500 si hay un error inesperado
     */
    @RequestMapping(method = RequestMethod.DELETE, consumes = "application/json")
    public ResponseEntity<?> eliminarCredenciales(@RequestBody CredencialDTO credencialDTO)
    {
        try
        {
            this.credencialService.eliminarCredenciales(credencialDTO);
            return ResponseEntity.status(200).build();
        }
        catch (CredencialException credencialException)
        {
            return ResponseEntity.status(this.obtenerStatusFiltro(credencialException.getCode())).body(credencialException.getBodyExceptionMessage());
        }
        catch (Exception exception)
        {
            String errorString = "Error mientras se eliminaban las credenciales";
            CredencialException credencialException = new CredencialException(Constants.ERROR_GENERIC, errorString, exception);

            log.error(errorString, credencialException);
            return ResponseEntity.status(500).body(credencialException.getBodyExceptionMessage());
        }
    }

    /**
     * PUT /credentials: marca si funcionan o no las credenciales que coinciden con el filtro
     * @param funciona cabecera con el nuevo valor del campo funciona
     * @param credencialDTO filtro con usuario, ubicación y url
     * @return 200 si se actualizan, 400-402 si falla el filtro o 500 si hay un error inesperado
     */
    @RequestMapping(method = RequestMethod.PUT, consumes = "application/json")
    public ResponseEntity<?> actualizarFuncionamiento(@RequestHeader(value = "funciona") Boolean funciona,
                                                      @RequestBody CredencialDTO credencialDTO)
    {
        try
        {
            this.credencialService.actualizarFuncionamiento(credencialDTO, funciona);
            return ResponseEntity.status(200).build();
        }
        catch (CredencialException credencialException)
        {
            return ResponseEntity.status(this.obtenerStatusFiltro(credencialException.getCode())).body(credencialException.getBodyExceptionMessage());
        }
        catch (Exception exception)
        {
            String errorString = "Error mientras se actualizaba el funcionamiento de las credenciales";
            CredencialException credencialException = new CredencialException(Constants.ERROR_GENERIC, errorString, exception);

            log.error(errorString, credencialException);
            return ResponseEntity.status(500).body(credencialException.getBodyExceptionMessage());
        }
    }

    /**
     * GET /credentials/password: devuelve la contraseña de la única credencial
     * que coincide con el filtro
     * @param credencialDTO filtro con usuario, ubicación y url
     * @return 200 con la contraseña, 400-403 si falla el filtro o hay varias coincidencias,
     *         o 500 si hay un error inesperado
     */
    @RequestMapping(method = RequestMethod.GET, value = "/password", consumes = "application/json")
    public ResponseEntity<?> obtenerPassword(@RequestBody CredencialDTO credencialDTO)
    {
        try
        {
            String password = this.credencialService.obtenerPassword(credencialDTO);
            return ResponseEntity.status(200).body(password);
        }
        catch (CredencialException credencialException)
        {
            return ResponseEntity.status(this.obtenerStatusFiltro(credencialException.getCode())).body(credencialException.getBodyExceptionMessage());
        }
        catch (Exception exception)
        {
            String errorString = "Error mientras se obtenía la contraseña";
            CredencialException credencialException = new CredencialException(Constants.ERROR_GENERIC, errorString, exception);

            log.error(errorString, credencialException);
            return ResponseEntity.status(500).body(credencialException.getBodyExceptionMessage());
        }
    }

    /**
     * Códigos HTTP del POST según el swagger
     * @param code código de error de la CredencialException
     * @return código HTTP correspondiente
     */
    private int obtenerStatusPost(String code)
    {
        if (code.equals(Constants.ERROR_USUARIO_NULO_O_VACIO))
        {
            return 401;
        }
        if (code.equals(Constants.ERROR_DESCRIPCION_NULA_O_VACIA))
        {
            return 402;
        }
        if (code.equals(Constants.ERROR_UBICACION_Y_URL_NULAS_O_VACIAS))
        {
            return 403;
        }
        if (code.equals(Constants.ERROR_UBICACION_NO_EXISTE))
        {
            return 404;
        }
        if (code.equals(Constants.ERROR_PASSWORD_NULA_O_VACIA))
        {
            return 405;
        }
        if (code.equals(Constants.ERROR_PASSWORD_INCORRECTA))
        {
            return 406;
        }

        // ERROR_OBJETO_VACIO
        return 400;
    }

    /**
     * Códigos HTTP de DELETE, PUT y GET /password según el swagger
     * @param code código de error de la CredencialException
     * @return código HTTP correspondiente
     */
    private int obtenerStatusFiltro(String code)
    {
        if (code.equals(Constants.ERROR_UBICACION_Y_URL_NULAS_O_VACIAS))
        {
            return 401;
        }
        if (code.equals(Constants.ERROR_UBICACION_NO_EXISTE))
        {
            return 402;
        }
        if (code.equals(Constants.ERROR_CREDENCIAL_DUPLICADA))
        {
            return 403;
        }

        // ERROR_OBJETO_VACIO o ERROR_USUARIO_NULO_O_VACIO
        return 400;
    }
}