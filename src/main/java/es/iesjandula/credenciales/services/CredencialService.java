package es.iesjandula.credenciales.services;

import es.iesjandula.credenciales.dtos.CredencialDTO;
import es.iesjandula.credenciales.dtos.CredencialPassDTO;
import es.iesjandula.credenciales.models.CredencialPass;
import es.iesjandula.credenciales.repositories.CredencialRepository;
import es.iesjandula.credenciales.utils.Constants;
import es.iesjandula.credenciales.utils.CredencialException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Servicio con la lógica de negocio de las credenciales.
 * Valida los datos recibidos, accede a la base de datos a través de CredencialRepository
 * y lanza CredencialException con el código de error correspondiente cuando algo falla
 */
@Service
@Slf4j
public class CredencialService
{
    /** Repositorio para acceder a las credenciales de la base de datos */
    @Autowired
    private CredencialRepository credencialRepository;

    /**
     * Guarda una nueva credencial con funciona a true
     * @param credencialPassDTO datos de la credencial y su contraseña
     * @throws CredencialException si algún campo es nulo o vacío o la contraseña no es válida
     */
    public void crearCredencial(CredencialPassDTO credencialPassDTO) throws CredencialException
    {
        this.validarCredencial(credencialPassDTO);
        this.validarPassword(credencialPassDTO.getPassword());

        // El id va a null para que lo genere la base de datos
        CredencialPass credencialPass = new CredencialPass(null,
                credencialPassDTO.getUsuario(),
                credencialPassDTO.getDescripcion(),
                credencialPassDTO.getUbicacion(),
                credencialPassDTO.getUrl(),
                true,
                credencialPassDTO.getPassword());

        this.credencialRepository.saveAndFlush(credencialPass);
    }

    /**
     * Devuelve todas las credenciales sin la contraseña. Si no hay, la lista va vacía
     * @return lista de credenciales sin la contraseña
     */
    public List<CredencialDTO> listarCredenciales()
    {
        return this.credencialRepository.listarCredenciales();
    }

    /**
     * Elimina todas las credenciales que coinciden con el filtro
     * @param credencialDTO filtro con usuario, ubicación y url
     * @throws CredencialException si el filtro no es válido o no coincide con ninguna credencial
     */
    public void eliminarCredenciales(CredencialDTO credencialDTO) throws CredencialException
    {
        List<CredencialPass> listaCredenciales = this.buscarCredenciales(credencialDTO);

        this.credencialRepository.deleteAll(listaCredenciales);
        this.credencialRepository.flush();
    }

    /**
     * Actualiza el campo funciona de todas las credenciales que coinciden con el filtro
     * @param credencialDTO filtro con usuario, ubicación y url
     * @param funciona nuevo valor del campo funciona
     * @throws CredencialException si el filtro no es válido o no coincide con ninguna credencial
     */
    public void actualizarFuncionamiento(CredencialDTO credencialDTO, Boolean funciona) throws CredencialException
    {
        List<CredencialPass> listaCredenciales = this.buscarCredenciales(credencialDTO);

        for (CredencialPass credencialPass : listaCredenciales)
        {
            credencialPass.setFunciona(funciona);
        }

        this.credencialRepository.saveAllAndFlush(listaCredenciales);
    }

    /**
     * Devuelve la contraseña de la credencial que coincide con el filtro.
     * Si coincide con más de una, lanza error
     * @param credencialDTO filtro con usuario, ubicación y url
     * @return contraseña de la credencial encontrada
     * @throws CredencialException si el filtro no es válido, no coincide con ninguna credencial
     *                             o coincide con más de una
     */
    public String obtenerPassword(CredencialDTO credencialDTO) throws CredencialException
    {
        List<CredencialPass> listaCredenciales = this.buscarCredenciales(credencialDTO);

        // Solo se puede devolver la contraseña si el filtro identifica una única credencial
        if (listaCredenciales.size() > 1)
        {
            String errorString = "La búsqueda coincide con " + listaCredenciales.size() + " credenciales";

            log.error(errorString);
            throw new CredencialException(Constants.ERROR_CREDENCIAL_DUPLICADA, errorString);
        }

        return listaCredenciales.getFirst().getPassword();
    }

    /**
     * Valida el filtro y devuelve las credenciales del usuario que coinciden
     * con la ubicación y/o la url indicadas. Si no hay ninguna, lanza error
     * @param credencialDTO filtro con usuario, ubicación y url
     * @return lista con las credenciales encontradas (nunca vacía)
     * @throws CredencialException si el filtro no es válido o no coincide con ninguna credencial
     */
    private List<CredencialPass> buscarCredenciales(CredencialDTO credencialDTO) throws CredencialException
    {
        this.validarFiltro(credencialDTO);

        // Si llegan vacíos se pasan como null para que la @Query no filtre por ese campo
        String ubicacion = credencialDTO.getUbicacion();
        if (ubicacion != null && ubicacion.isEmpty())
        {
            ubicacion = null;
        }

        String url = credencialDTO.getUrl();
        if (url != null && url.isEmpty())
        {
            url = null;
        }

        List<CredencialPass> listaCredenciales = this.credencialRepository.buscarCredenciales(credencialDTO.getUsuario(), ubicacion, url);

        if (listaCredenciales.isEmpty())
        {
            String errorString = "No existe ninguna credencial del usuario " + credencialDTO.getUsuario()
                    + " con ubicación " + credencialDTO.getUbicacion() + " y url " + credencialDTO.getUrl();

            log.error(errorString);
            throw new CredencialException(Constants.ERROR_UBICACION_NO_EXISTE, errorString);
        }

        return listaCredenciales;
    }

    /**
     * Valida en orden: objeto, usuario, descripción, ubicación/url y password.
     * Lanza la excepción del primer campo que no sea válido
     * @param credencialPassDTO datos de la credencial a validar
     * @throws CredencialException si el objeto es nulo o algún campo obligatorio es nulo o vacío
     */
    private void validarCredencial(CredencialPassDTO credencialPassDTO) throws CredencialException
    {
        // Objeto
        if (credencialPassDTO == null)
        {
            String errorString = "El objeto CredencialPassDTO viene vacío";

            log.error(errorString);
            throw new CredencialException(Constants.ERROR_OBJETO_VACIO, errorString);
        }

        // Usuario
        if (credencialPassDTO.getUsuario() == null || credencialPassDTO.getUsuario().isEmpty())
        {
            String errorString = "El usuario es nulo o vacío";

            log.error(errorString);
            throw new CredencialException(Constants.ERROR_USUARIO_NULO_O_VACIO, errorString);
        }

        // Descripción
        if (credencialPassDTO.getDescripcion() ==  null || credencialPassDTO.getDescripcion().isEmpty())
        {
            String errorString = "La descripción es nula o vacía";

            log.error(errorString);
            throw new CredencialException(Constants.ERROR_DESCRIPCION_NULA_O_VACIA, errorString);
        }

        // Ubicación y url: basta con que una de las dos esté rellena
        if (
                (credencialPassDTO.getUbicacion() == null || credencialPassDTO.getUbicacion().isEmpty()) &&
                (credencialPassDTO.getUrl() == null || credencialPassDTO.getUrl().isEmpty())
        )
        {
            String errorString = "La ubicación y la url son nulas o vacías. Una de las dos debe estar rellena";

            log.error(errorString);
            throw new CredencialException(Constants.ERROR_UBICACION_Y_URL_NULAS_O_VACIAS, errorString);
        }

        // Password (su formato se comprueba después en validarPassword)
        if (credencialPassDTO.getPassword() == null || credencialPassDTO.getPassword().isEmpty())
        {
            String errorString = "La contraseña es nula o vacía";

            log.error(errorString);
            throw new CredencialException(Constants.ERROR_PASSWORD_NULA_O_VACIA, errorString);
        }
    }

    /**
     * Valida el filtro de DELETE, PUT y GET /password: usuario y ubicación o url
     * @param credencialDTO filtro a validar
     * @throws CredencialException si el objeto es nulo, el usuario es nulo o vacío,
     *                             o la ubicación y la url son nulas o vacías a la vez
     */
    private void validarFiltro(CredencialDTO credencialDTO) throws CredencialException
    {
        // Objeto
        if (credencialDTO == null)
        {
            String errorString = "El objeto CredencialPassDTO viene vacío";

            log.error(errorString);
            throw new CredencialException(Constants.ERROR_OBJETO_VACIO, errorString);
        }

        // Usuario
        if (credencialDTO.getUsuario() == null || credencialDTO.getUsuario().isEmpty())
        {
            String errorString = "El usuario es nulo o vacío";

            log.error(errorString);
            throw new CredencialException(Constants.ERROR_USUARIO_NULO_O_VACIO, errorString);
        }

        // Ubicación y url: basta con que una de las dos esté rellena
        if (
                (credencialDTO.getUbicacion() == null || credencialDTO.getUbicacion().isEmpty()) &&
                        (credencialDTO.getUrl() == null || credencialDTO.getUrl().isEmpty())
        )
        {
            String errorString = "La ubicación y la url son nulas o vacías. Una de las dos debe estar rellena";

            log.error(errorString);
            throw new CredencialException(Constants.ERROR_UBICACION_Y_URL_NULAS_O_VACIAS, errorString);
        }
    }

    /**
     * Comprueba en orden: longitud, mayúscula, minúscula, dígito y carácter especial.
     * Lanza la excepción del primer requisito que no se cumpla
     * @param password contraseña a validar (ya comprobado que no es nula ni vacía)
     * @throws CredencialException si la contraseña no cumple alguno de los requisitos
     */
    private void validarPassword(String password) throws CredencialException
    {
        // Longitud mínima
        if (password.length() < Constants.PASSWORD_LONGITUD_MINIMA)
        {
            String errorString = "La contraseña debe tener al menos " + Constants.PASSWORD_LONGITUD_MINIMA + " caracteres";

            log.error(errorString);
            throw new CredencialException(Constants.ERROR_PASSWORD_INCORRECTA, errorString);
        }

        boolean tieneMayuscula = false;
        boolean tieneMinuscula = false;
        boolean tieneDigito = false;
        boolean tieneEspecial = false;

        // Se recorre la contraseña marcando qué tipos de carácter contiene.
        // Cualquier carácter que no sea letra, dígito ni espacio cuenta como especial
        for (char caracter : password.toCharArray())
        {
            if (Character.isUpperCase(caracter))
            {
                tieneMayuscula = true;
            }
            else
            {
                if (Character.isLowerCase(caracter))
                {
                    tieneMinuscula = true;
                }
                else
                {
                    if (Character.isDigit(caracter))
                    {
                        tieneDigito = true;
                    }
                    else
                    {
                        if (!Character.isWhitespace(caracter))
                        {
                            tieneEspecial = true;
                        }
                    }
                }
            }
        }

        // Mayúscula
        if (!tieneMayuscula)
        {
            String errorString = "La contraseña debe contener al menos una letra mayúscula";

            log.error(errorString);
            throw new CredencialException(Constants.ERROR_PASSWORD_INCORRECTA, errorString);
        }

        // Minúscula
        if (!tieneMinuscula)
        {
            String errorString = "La contraseña debe contener al menos una letra minúscula";

            log.error(errorString);
            throw new CredencialException(Constants.ERROR_PASSWORD_INCORRECTA, errorString);
        }

        // Dígito
        if (!tieneDigito)
        {
            String errorString = "La contraseña debe contener al menos un dígito";

            log.error(errorString);
            throw new CredencialException(Constants.ERROR_PASSWORD_INCORRECTA, errorString);
        }

        // Carácter especial
        if (!tieneEspecial)
        {
            String errorString = "La contraseña debe contener al menos un signo de puntuación o símbolo";

            log.error(errorString);
            throw new CredencialException(Constants.ERROR_PASSWORD_INCORRECTA, errorString);
        }
    }
}