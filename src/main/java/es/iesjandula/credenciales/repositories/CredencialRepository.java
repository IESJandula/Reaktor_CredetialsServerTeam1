package es.iesjandula.credenciales.repositories;

import es.iesjandula.credenciales.dtos.CredencialDTO;
import es.iesjandula.credenciales.models.Credencial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CredencialRepository extends JpaRepository<Credencial, Long>
{
    @Query("""
        SELECT new es.iesjandula.credenciales.dtos.CredencialDTO(
            c.usuario,
            c.descripcion,
            c.ubicacion,
            c.url)
        FROM Credencial c
    """)
    List<CredencialDTO> listarCredenciales();

    // Si ubicacion o url llegan a null no se filtra por ese campo
    @Query("""
        SELECT c
        FROM Credencial c
        WHERE c.usuario = :usuario
            AND (:ubicacion IS NULL OR c.ubicacion = :ubicacion)
            AND (:url IS NULL OR c.url = :url)
    """)
    List<Credencial> buscarCredenciales(@Param("usuario") String usuario,
                                            @Param("ubicacion") String ubicacion,
                                            @Param("url") String url);
}