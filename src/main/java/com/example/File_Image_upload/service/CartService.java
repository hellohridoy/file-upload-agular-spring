package com.example.File_Image_upload.service;

import com.example.File_Image_upload.entity.*;
import com.example.File_Image_upload.repository.CartItemRepository;
import com.example.File_Image_upload.repository.CartRepository;
import com.example.File_Image_upload.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    public CartResponse addToCart(AddToCartRequest request) {
        // Validate product exists and has stock
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock available");
        }

        // Get or create cart for user
        Cart cart = cartRepository.findByUserId(request.getUserId())
            .orElse(new Cart());
        cart.setUserId(request.getUserId());

        if (cart.getId() == null) {
            cart = cartRepository.save(cart);
        }

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository
            .findByCartIdAndProductId(cart.getId(), product.getId());

        CartItem cartItem;
        if (existingItem.isPresent()) {
            // Update existing item
            cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + request.getQuantity();

            if (newQuantity > product.getStock()) {
                throw new RuntimeException("Cannot add more items. Stock limit exceeded");
            }

            cartItem.setQuantity(newQuantity);
        } else {
            // Create new cart item
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(product.getPrice());
            cart.getItems().add(cartItem);
        }

        cartItemRepository.save(cartItem);

        // Recalculate cart totals
        cart.calculateTotals();
        cart = cartRepository.save(cart);

        return buildCartResponse(cart, "Item added to cart successfully");
    }

    public CartResponse getCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElse(new Cart());
        cart.setUserId(userId);

        if (cart.getId() == null) {
            cart = cartRepository.save(cart);
        }

        return buildCartResponse(cart, "Cart retrieved successfully");
    }

    public CartResponse updateCartItem(UpdateCartRequest request) {
        CartItem cartItem = cartItemRepository.findById(request.getCartItemId())
            .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (request.getQuantity() <= 0) {
            return removeFromCart(request.getCartItemId());
        }

        // Check stock availability
        if (request.getQuantity() > cartItem.getProduct().getStock()) {
            throw new RuntimeException("Insufficient stock available");
        }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        Cart cart = cartItem.getCart();
        cart.calculateTotals();
        cart = cartRepository.save(cart);

        return buildCartResponse(cart, "Cart updated successfully");
    }

    public CartResponse removeFromCart(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));

        Cart cart = cartItem.getCart();
        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        cart.calculateTotals();
        cart = cartRepository.save(cart);

        return buildCartResponse(cart, "Item removed from cart");
    }

    public CartResponse clearCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Cart not found"));

        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cart.calculateTotals();
        cart = cartRepository.save(cart);

        return buildCartResponse(cart, "Cart cleared successfully");
    }

    private CartResponse buildCartResponse(Cart cart, String message) {
        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setUserId(cart.getUserId());
        response.setTotalAmount(cart.getTotalAmount());
        response.setTotalItems(cart.getTotalItems());
        response.setMessage(message);

        List<CartItemResponse> items = cart.getItems().stream()
            .map(this::buildCartItemResponse)
            .collect(Collectors.toList());
        response.setItems(items);

        return response;
    }

    private CartItemResponse buildCartItemResponse(CartItem cartItem) {
        CartItemResponse response = new CartItemResponse();
        response.setId(cartItem.getId());
        response.setProductId(cartItem.getProduct().getId());
        response.setProductName(cartItem.getProduct().getName());
        response.setPrice(cartItem.getPrice());
        response.setQuantity(cartItem.getQuantity());
        response.setSubtotal(cartItem.getPrice().multiply(
            java.math.BigDecimal.valueOf(cartItem.getQuantity())));
        response.setImageUrl(cartItem.getProduct().getImageUrl());
        return response;
    }
}
