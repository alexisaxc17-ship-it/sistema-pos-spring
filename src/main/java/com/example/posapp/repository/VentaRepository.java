package com.example.posapp.repository;

import com.example.posapp.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    @Query("SELECT v FROM Venta v WHERE v.fecha BETWEEN :inicio AND :fin ORDER BY v.fecha ASC")
    List<Venta> findByRangoFechas(@Param("inicio") OffsetDateTime inicio,
                                  @Param("fin") OffsetDateTime fin);

    // Consulta JPQL para sumar el total de hoy
    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.fecha >= CURRENT_DATE")
    Double sumTotalVentasHoy();
}




