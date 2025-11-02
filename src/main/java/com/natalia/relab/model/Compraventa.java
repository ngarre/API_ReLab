package com.natalia.relab.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "compraventas")
public class Compraventa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha; // Cojo la fecha del sistema
    private boolean devuelto;
    private String comentario;
    private float precioFinal;

    // RELACIÓN CON LA TABLA PRODUCTO
    @OneToOne // Cada producto puede aparecer en una única operación de compraventa
    @JoinColumn(name = "producto_id") // FK
    private Producto producto;

    // RELACIÓN CON LA TABLA USUARIOS - COMPRADOR
    @ManyToOne // Un usuario puede hacer varias compras, pero una compra solo es hecha por un usuario.
    @JoinColumn(name = "comprador_id")
    private Usuario comprador;

    // RELACIÓN CON LA TABLA USUARIOS - VENDEDOR
    @ManyToOne // Un usuario puede hacer varias ventas, pero una venta solo es hecha por un usuario
    @JoinColumn(name = "vendedor_id")
    private Usuario vendedor;
}
