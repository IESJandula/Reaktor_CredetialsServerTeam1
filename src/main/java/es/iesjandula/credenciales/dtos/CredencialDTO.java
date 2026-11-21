package es.iesjandula.credenciales.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CredencialDTO
{
    private String usuario;
    private String descripcion;
    private String ubicacion;
    private String url;
}