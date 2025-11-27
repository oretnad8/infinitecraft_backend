package com.microservice.product.controllers;

import com.microservice.product.entities.Product;
import com.microservice.product.services.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private IProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> save(@RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(product));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return productService.findById(id).map(existingProduct -> {
            existingProduct.setNombre(product.getNombre());
            existingProduct.setPrecio(product.getPrecio());
            existingProduct.setImagen(product.getImagen());
            existingProduct.setCategoria(product.getCategoria());
            existingProduct.setDescripcion(product.getDescripcion());
            return ResponseEntity.ok(productService.save(existingProduct));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (productService.findById(id).isPresent()) {
            productService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/images")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        try {
            // Save file to a directory (e.g., "uploads")
            // For simplicity, we just return a simulated path or the original filename
            // In a real scenario, you'd save it to S3 or a specific folder
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            // Path path = Paths.get("uploads/" + fileName);
            // Files.createDirectories(path.getParent());
            // Files.write(path, file.getBytes());
            
            // Return the relative path that would be stored in the database
            return ResponseEntity.ok("/assets/img/" + fileName); 
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file");
        }
    }
}
