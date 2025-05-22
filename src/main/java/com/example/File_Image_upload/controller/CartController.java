package com.example.File_Image_upload.controller;

import com.example.File_Image_upload.entity.AddToCartRequest;
import com.example.File_Image_upload.entity.CartResponse;
import com.example.File_Image_upload.entity.UpdateCartRequest;
import com.example.File_Image_upload.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(@RequestBody AddToCartRequest request) {
        try {
            CartResponse response = cartService.addToCart(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CartResponse errorResponse = new CartResponse();
            errorResponse.setMessage("Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable String userId) {
        try {
            CartResponse response = cartService.getCart(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CartResponse errorResponse = new CartResponse();
            errorResponse.setMessage("Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<CartResponse> updateCartItem(@RequestBody UpdateCartRequest request) {
        try {
            CartResponse response = cartService.updateCartItem(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CartResponse errorResponse = new CartResponse();
            errorResponse.setMessage("Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<CartResponse> removeFromCart(@PathVariable Long cartItemId) {
        try {
            CartResponse response = cartService.removeFromCart(cartItemId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CartResponse errorResponse = new CartResponse();
            errorResponse.setMessage("Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<CartResponse> clearCart(@PathVariable String userId) {
        try {
            CartResponse response = cartService.clearCart(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CartResponse errorResponse = new CartResponse();
            errorResponse.setMessage("Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
