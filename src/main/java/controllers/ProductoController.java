package controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import db.ProductRepository;
import models.Producto;

@RestController
@RequestMapping("api/producto")
public class ProductoController {
	@Autowired
	private ProductRepository productRepository;

	// PARA DEVOLVER UNA LISTA DE PRODUCTOS
	@GetMapping
	public List<Producto> getProducto() {
		return productRepository.findAll();
	}

	// PARA CREAR PRODUCTOS
	@PostMapping
	public Producto createProducto(@RequestBody Producto newProducto) {
		return productRepository.save(newProducto);
	}

	// PARA MODIFICAR PRODUCTO A TRAVES DE ID
	@PutMapping("/{idProducto}")
	public Producto updateProducto(@PathVariable("idProducto") Long idProducto, @RequestBody Producto updatedProducto) {
		if (productRepository.existsById(idProducto)) {
			updatedProducto.setId(idProducto);
			return productRepository.save(updatedProducto);
		} else {
			throw new RuntimeException("Error: El producto con ID " + idProducto + " no existe en la base de datos.");
		}
	}

	// PARA ELIMINAR PRODUCTO A TRAVES DE ID
	@DeleteMapping("{idProducto}")
	public String deleteProducto(@PathVariable("idProducto") Long idProducto) {
		if (productRepository.existsById(idProducto)) {
			productRepository.deleteById(idProducto);
			return "Producto con ID " + idProducto + " eliminado con éxito.";
		} else {
			return "Error: No se pudo eliminar porque el producto con ID " + idProducto + " no existe.";
		}
	}

}
