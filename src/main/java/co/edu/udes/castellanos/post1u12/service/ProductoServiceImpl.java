package co.edu.udes.castellanos.post1u12.service;

import java.util.List;

import co.edu.udes.castellanos.post1u12.domain.Producto;
import co.edu.udes.castellanos.post1u12.repository.ProductoRepository;
import co.edu.udes.castellanos.post1u12.web.dto.ProductoRequest;
import co.edu.udes.castellanos.post1u12.web.dto.ProductoResponse;
import co.edu.udes.castellanos.post1u12.web.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {
        return productoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado con id " + id));
    }

    @Override
    public ProductoResponse crear(ProductoRequest request) {
        Producto producto = new Producto(
                request.nombre(),
                request.descripcion(),
                request.precio(),
                request.stock());
        return toResponse(productoRepository.save(producto));
    }

    @Override
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado con id " + id));

        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());

        return toResponse(productoRepository.save(producto));
    }

    @Override
    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado con id " + id));
        productoRepository.delete(producto);
    }

    private ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock());
    }
}