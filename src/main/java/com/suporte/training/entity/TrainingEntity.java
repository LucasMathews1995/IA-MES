package com.suporte.training.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;


@Entity
@Table(name= "training")
public class TrainingEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public  Long id;
    @Column(length = 100)
    public String titulo;

    @Enumerated(EnumType.STRING)
    public TrainingType tipoMes;

    @Column(length = 100)
    public String chamadoReferencia;

    public String text;


    public TrainingEntity() {
    }


    public TrainingEntity(Long id, String titulo, TrainingType tipoMes,String chamadoReferencia, String text) {
        this.id = id;
        this.titulo = titulo;
        this.tipoMes = tipoMes;
        this.chamadoReferencia = chamadoReferencia;
        this.text = text;
    }

    public TrainingEntity( String titulo, TrainingType tipoMes,String chamadoReferencia, String text) {
        this.titulo = titulo;
        this.tipoMes = tipoMes;
        this.chamadoReferencia = chamadoReferencia;
        this.text = text;
    }





}
