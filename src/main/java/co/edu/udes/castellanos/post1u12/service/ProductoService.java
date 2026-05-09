package co.edu.udes.castellanos.post1u12.service;

import java.util.List;

import co.edu.udes.castellanos.post1u12.web.dto.ProductoRequest;
import co.edu.udes.castellanos.post1u12.web.dto.ProductoResponse;

public interface ProductoService {

    List<ProductoResponse> listar();

    ProductoResponse obtenerPorId(Long id);

    ProductoResponse crear(ProductoRequest request);

    ProductoResponse actualizar(Long id, ProductoRequest request);

    void eliminar(Long id);
}