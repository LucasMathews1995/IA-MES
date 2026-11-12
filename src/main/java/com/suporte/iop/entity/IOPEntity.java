package com.suporte.iop.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name=  "iop")
public class IOPEntity extends PanacheEntityBase{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String titulo;
    public String equipamento;
    public String chamadoReferencia;
    @Column(columnDefinition = "TEXT")
    public String texto;

    public IOPEntity() {
    }


    public IOPEntity(String titulo, String equipamento, String chamadoReferencia, String texto) {
        this.titulo = titulo;
        this.equipamento = equipamento;
        this.chamadoReferencia = chamadoReferencia;
        this.texto = texto;
    }

}
