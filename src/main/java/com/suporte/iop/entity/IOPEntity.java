package com.suporte.iop.entity;
import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name=  "iop")
public class IOPEntity extends PanacheEntityBase{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String titulo;

    @Enumerated(EnumType.STRING)
    public EquipmentType equipamento;

    public String chamadoReferencia;

    @Column(columnDefinition = "TEXT")
    public String texto;

    public IOPEntity() {
    }
    public IOPEntity(Long id ,String titulo, EquipmentType equipamento, String chamadoReferencia, String texto) {
        this. id = id;
        this.titulo = titulo;
        this.equipamento = equipamento;
        this.chamadoReferencia = chamadoReferencia;
        this.texto = texto;
    }


    public IOPEntity(String titulo, EquipmentType equipamento, String chamadoReferencia, String texto) {
        this.titulo = titulo;
        this.equipamento = equipamento;
        this.chamadoReferencia = chamadoReferencia;
        this.texto = texto;
    }

}
