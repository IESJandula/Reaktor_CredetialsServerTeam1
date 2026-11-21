package es.iesjandula.credenciales.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
public class CredencialPass extends Credencial
{
    @Column
    private String password;

    public CredencialPass(Long id, String usuario, String descripcion, String ubicacion, String url, Boolean funciona, String password)
    {
        super(id, usuario, descripcion, ubicacion, url, funciona);
        this.password = password;
    }
}