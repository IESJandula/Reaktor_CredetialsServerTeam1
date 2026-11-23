package es.iesjandula.credenciales.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CredencialPassDTO extends CredencialDTO
{
    private String password;
}