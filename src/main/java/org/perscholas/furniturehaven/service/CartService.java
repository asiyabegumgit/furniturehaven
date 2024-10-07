package org.perscholas.furniturehaven.service;

import org.perscholas.furniturehaven.model.Cart;
import org.perscholas.furniturehaven.model.CartItem;
import org.perscholas.furniturehaven.model.Customer;
import org.perscholas.furniturehaven.model.Product;
import org.perscholas.furniturehaven.repository.CartRepository;
import org.perscholas.furniturehaven.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;

    public void addProductToCart(Customer customer, Long productId, int quantity) {
        Cart cart=cartRepository.findByCustomer(customer);
        if(cart==null) {
            throw new RuntimeException("Cart is empty"+customer.getId());
        }
        //Fetch the product
        Product product=productRepository.findById(productId).orElseThrow(()->new RuntimeException("product not found with id"+productId));
        //Create a new cart item
        CartItem cartItem=new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);

        //Add the cart item to the cart
        cart.getCartItems().add(cartItem);
        //save the cart
        cartRepository.save(cart);
    }
}
