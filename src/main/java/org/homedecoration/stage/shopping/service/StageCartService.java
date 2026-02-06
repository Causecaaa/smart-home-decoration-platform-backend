package org.homedecoration.stage.shopping.service;

import lombok.RequiredArgsConstructor;
import org.homedecoration.common.exception.BusinessException;
import org.homedecoration.stage.shopping.dto.request.AddCartItemRequest;
import org.homedecoration.stage.shopping.dto.request.UpdateCartItemRequest;
import org.homedecoration.stage.shopping.dto.response.StageCartItemResponse;
import org.homedecoration.stage.shopping.dto.response.StageCartResponse;
import org.homedecoration.stage.shopping.entity.Product;
import org.homedecoration.stage.shopping.entity.StageCart;
import org.homedecoration.stage.shopping.entity.StageCartItem;
import org.homedecoration.stage.shopping.repository.ProductRepository;
import org.homedecoration.stage.shopping.repository.StageCartItemRepository;
import org.homedecoration.stage.shopping.repository.StageCartRepository;
import org.homedecoration.stage.stage.repository.StageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StageCartService {

    private final StageCartRepository stageCartRepository;
    private final StageCartItemRepository stageCartItemRepository;
    private final ProductRepository productRepository;
    private final StageRepository stageRepository;

    @Transactional
    public StageCartResponse addItem(Long stageId, Long userId, AddCartItemRequest request) {
        validateStage(stageId);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException("商品不存在"));

        if (product.getStatus() == null || product.getStatus() == 0) {
            throw new BusinessException("商品已下架，无法加入购物车");
        }

        StageCart cart = stageCartRepository.findByStageIdAndUserId(stageId, userId)
                .orElseGet(() -> {
                    StageCart newCart = new StageCart();
                    newCart.setStageId(stageId);
                    newCart.setUserId(userId);
                    return stageCartRepository.save(newCart);
                });

        StageCartItem item = stageCartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
                .orElseGet(() -> {
                    StageCartItem newItem = new StageCartItem();
                    newItem.setCartId(cart.getId());
                    newItem.setProductId(request.getProductId());
                    newItem.setQuantity(BigDecimal.ZERO);
                    return newItem;
                });

        item.setQuantity(item.getQuantity().add(request.getQuantity()));
        stageCartItemRepository.save(item);
        return buildCartResponse(cart);
    }

    @Transactional
    public StageCartResponse updateItem(Long stageId, Long userId, Long itemId, UpdateCartItemRequest request) {
        StageCart cart = getUserCart(stageId, userId);
        StageCartItem item = stageCartItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException("购物车项不存在"));

        if (!item.getCartId().equals(cart.getId())) {
            throw new BusinessException("购物车项不属于当前阶段");
        }

        if (request.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
            stageCartItemRepository.delete(item);
            return buildCartResponse(cart);
        }

        item.setQuantity(request.getQuantity());
        stageCartItemRepository.save(item);
        return buildCartResponse(cart);
    }

    @Transactional
    public StageCartResponse removeItem(Long stageId, Long userId, Long itemId) {
        StageCart cart = getUserCart(stageId, userId);
        StageCartItem item = stageCartItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException("购物车项不存在"));

        if (!item.getCartId().equals(cart.getId())) {
            throw new BusinessException("购物车项不属于当前阶段");
        }

        stageCartItemRepository.delete(item);
        return buildCartResponse(cart);
    }

    @Transactional(readOnly = true)
    public StageCartResponse getCart(Long stageId, Long userId) {
        return stageCartRepository.findByStageIdAndUserId(stageId, userId)
                .map(this::buildCartResponse)
                .orElseGet(() -> {
                    StageCartResponse response = new StageCartResponse();
                    response.setStageId(stageId);
                    response.setUserId(userId);
                    response.setItems(List.of());
                    response.setTotalAmount(BigDecimal.ZERO);
                    return response;
                });
    }

    @Transactional
    public void clearCart(Long cartId) {
        stageCartItemRepository.deleteByCartId(cartId);
    }

    @Transactional(readOnly = true)
    public StageCart getUserCart(Long stageId, Long userId) {
        validateStage(stageId);
        return stageCartRepository.findByStageIdAndUserId(stageId, userId)
                .orElseThrow(() -> new BusinessException("当前阶段购物车为空"));
    }

    @Transactional(readOnly = true)
    public List<StageCartItem> getCartItems(Long cartId) {
        return stageCartItemRepository.findByCartId(cartId);
    }

    private StageCartResponse buildCartResponse(StageCart cart) {
        List<StageCartItem> items = stageCartItemRepository.findByCartId(cart.getId());
        List<StageCartItemResponse> itemResponses = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (StageCartItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new BusinessException("商品不存在，ID=" + item.getProductId()));

            BigDecimal subtotal = product.getPrice().multiply(item.getQuantity());
            totalAmount = totalAmount.add(subtotal);

            StageCartItemResponse response = new StageCartItemResponse();
            response.setId(item.getId());
            response.setProductId(item.getProductId());
            response.setProductName(product.getName());
            response.setPrice(product.getPrice());
            response.setQuantity(item.getQuantity());
            response.setSubtotal(subtotal);
            itemResponses.add(response);
        }

        StageCartResponse response = new StageCartResponse();
        response.setCartId(cart.getId());
        response.setStageId(cart.getStageId());
        response.setUserId(cart.getUserId());
        response.setItems(itemResponses);
        response.setTotalAmount(totalAmount);
        return response;
    }

    private void validateStage(Long stageId) {
        if (!stageRepository.existsById(stageId)) {
            throw new BusinessException("装修阶段不存在");
        }
    }
}
