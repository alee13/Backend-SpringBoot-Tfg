package controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import models.Producto;

@RestController
@RequestMapping("api/producto")
public class ProductoController {

	// PARA DEVOLVER UNA LISTA DE PRODUCTOS
	@GetMapping
	public List<Producto> getProducto(){
		List<Producto> listaProductos = new ArrayList<>();
		Producto producto = new Producto(1L, "Hamburguesa", "Con queso", 10.5f, true);
		listaProductos.add(producto);
		return listaProductos;
	}
	
	// PARA CREAR PRODUCTOS
	@PostMapping
	public Producto createProducto(@RequestBody Producto newProducto) {
		return newProducto;
	}
	
	// PARA MODIFICAR PRODUCTO A TRAVES DE ID
	@PutMapping("{idProducto}")
	public Producto updateProducto(@PathVariable Long idProducto, @RequestBody Producto updatedProducto) {
		updatedProducto.setId(idProducto);
		return updatedProducto;
	}
	
	// PARA ELIMINAR PRODUCTO A TRAVES DE ID
	@DeleteMapping("{idProducto}")
	public String deleteProducto(@PathVariable Long idProducto) {
		return "Producto eliminado";
	}
	
	
}
