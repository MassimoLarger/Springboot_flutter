package com.example.springboot_flutter.service;

import com.example.springboot_flutter.dto.ProductoRequestDTO;
import com.example.springboot_flutter.dto.ProductoResponseDTO;
import com.example.springboot_flutter.exception.ProductoDuplicadoException;
import com.example.springboot_flutter.exception.ProductoNotFoundException;
import com.example.springboot_flutter.mapper.ProductoMapper;
import com.example.springboot_flutter.model.Producto;
import com.example.springboot_flutter.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ProductoMapper productoMapper;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;
    private ProductoRequestDTO requestDTO;
    private ProductoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        producto = Producto.builder()
                .id(1L)
                .nombre("Laptop Gamer")
                .descripcion("Laptop con procesador Intel i7")
                .precio(new BigDecimal("1299.99"))
                .stock(10)
                .activo(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        requestDTO = ProductoRequestDTO.builder()
                .nombre("Laptop Gamer")
                .descripcion("Laptop con procesador Intel i7")
                .precio(new BigDecimal("1299.99"))
                .stock(10)
                .build();

        responseDTO = ProductoResponseDTO.builder()
                .id(1L)
                .nombre("Laptop Gamer")
                .descripcion("Laptop con procesador Intel i7")
                .precio(new BigDecimal("1299.99"))
                .stock(10)
                .activo(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void obtenerTodos_ShouldReturnPageOfProducts() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Producto> productosPage = new PageImpl<>(List.of(producto), pageable, 1);
        when(productoRepository.findByActivo(true, pageable)).thenReturn(productosPage);
        when(productoMapper.entityToResponseDto(any(Producto.class))).thenReturn(responseDTO);

        // Act
        Page<ProductoResponseDTO> result = productoService.obtenerTodos(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getNombre()).isEqualTo("Laptop Gamer");
        verify(productoRepository).findByActivo(true, pageable);
    }

    @Test
    void obtenerPorId_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoMapper.entityToResponseDto(producto)).thenReturn(responseDTO);

        // Act
        ProductoResponseDTO result = productoService.obtenerPorId(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Laptop Gamer");
        verify(productoRepository).findById(1L);
    }

    @Test
    void obtenerPorId_WhenProductNotFound_ShouldThrowException() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productoService.obtenerPorId(99L))
                .isInstanceOf(ProductoNotFoundException.class)
                .hasMessageContaining("Producto no encontrado con ID: 99");
        verify(productoRepository).findById(99L);
    }

    @Test
    void buscarPorNombre_ShouldReturnMatchingProducts() {
        // Arrange
        String nombre = "laptop";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Producto> productosPage = new PageImpl<>(List.of(producto), pageable, 1);
        when(productoRepository.findByNombreContainingIgnoreCaseAndActivo(nombre, true, pageable))
                .thenReturn(productosPage);
        when(productoMapper.entityToResponseDto(any(Producto.class))).thenReturn(responseDTO);

        // Act
        Page<ProductoResponseDTO> result = productoService.buscarPorNombre(nombre, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(productoRepository).findByNombreContainingIgnoreCaseAndActivo(nombre, true, pageable);
    }

    @Test
    void crear_WhenProductNameDoesNotExist_ShouldCreateProduct() {
        // Arrange
        when(productoRepository.existsByNombreIgnoreCase(requestDTO.getNombre())).thenReturn(false);
        when(productoMapper.requestDtoToEntity(requestDTO)).thenReturn(producto);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(productoMapper.entityToResponseDto(producto)).thenReturn(responseDTO);

        // Act
        ProductoResponseDTO result = productoService.crear(requestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Laptop Gamer");
        verify(productoRepository).existsByNombreIgnoreCase(requestDTO.getNombre());
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void crear_WhenProductNameAlreadyExists_ShouldThrowException() {
        // Arrange
        when(productoRepository.existsByNombreIgnoreCase(requestDTO.getNombre())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> productoService.crear(requestDTO))
                .isInstanceOf(ProductoDuplicadoException.class)
                .hasMessageContaining("Ya existe un producto con el nombre: Laptop Gamer");
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void actualizar_WhenProductExists_ShouldUpdateProduct() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(productoMapper.entityToResponseDto(any(Producto.class))).thenReturn(responseDTO);

        // Act
        ProductoResponseDTO result = productoService.actualizar(1L, requestDTO);

        // Assert
        assertThat(result).isNotNull();
        verify(productoRepository).findById(1L);
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void actualizar_WhenProductNotFound_ShouldThrowException() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productoService.actualizar(99L, requestDTO))
                .isInstanceOf(ProductoNotFoundException.class)
                .hasMessageContaining("Producto no encontrado con ID: 99");
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void eliminar_WhenProductExists_ShouldSoftDelete() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // Act
        productoService.eliminar(1L);

        // Assert
        assertThat(producto.getActivo()).isFalse();
        verify(productoRepository).findById(1L);
        verify(productoRepository).save(producto);
    }

    @Test
    void eliminar_WhenProductNotFound_ShouldThrowException() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productoService.eliminar(99L))
                .isInstanceOf(ProductoNotFoundException.class);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void restaurar_WhenProductExists_ShouldRestoreProduct() {
        // Arrange
        producto.setActivo(false);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // Act
        productoService.restaurar(1L);

        // Assert
        assertThat(producto.getActivo()).isTrue();
        verify(productoRepository).findById(1L);
        verify(productoRepository).save(producto);
    }
}