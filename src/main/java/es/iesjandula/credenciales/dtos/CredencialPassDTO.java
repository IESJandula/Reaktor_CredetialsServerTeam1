package es.iesjandula.credenciales.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CredencialPassDTO extends CredencialDTO
{
    private String password;

    public CredencialPassDTO(String usuario, String descripcion, String ubicacion, String url, String password)
    {
        super(usuario, descripcion, ubicacion, url);
        this.password = password;
    }
}