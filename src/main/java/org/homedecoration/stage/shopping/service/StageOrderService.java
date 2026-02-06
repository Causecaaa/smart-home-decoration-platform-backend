package org.homedecoration.stage.shopping.service;

import lombok.RequiredArgsConstructor;
import org.homedecoration.common.exception.BusinessException;
import org.homedecoration.stage.shopping.dto.response.StageOrderItemResponse;
import org.homedecoration.stage.shopping.dto.response.StageOrderResponse;
import org.homedecoration.stage.shopping.dto.response.StagePurchasedMaterialsResponse;
import org.homedecoration.stage.shopping.entity.Product;
import org.homedecoration.stage.shopping.entity.StageCart;
import org.homedecoration.stage.shopping.entity.StageCartItem;
import org.homedecoration.stage.shopping.entity.StageOrder;
import org.homedecoration.stage.shopping.entity.StageOrderItem;
import org.homedecoration.stage.shopping.repository.ProductRepository;
import org.homedecoration.stage.shopping.repository.StageOrderItemRepository;
import org.homedecoration.stage.shopping.repository.StageOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StageOrderService {

    private final StageOrderRepository stageOrderRepository;
    private final StageOrderItemRepository stageOrderItemRepository;
    private final ProductRepository productRepository;
    private final StageCartService stageCartService;

    @Transactional
    public StageOrderResponse checkout(Long stageId, Long userId) {
        StageCart cart = stageCartService.getUserCart(stageId, userId);
        List<StageCartItem> cartItems = stageCartService.getCartItems(cart.getId());

        if (cartItems.isEmpty()) {
            throw new BusinessException("购物车为空，无法下单");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<StageOrderItem> orderItems = new ArrayList<>();

        for (StageCartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new BusinessException("商品不存在，ID=" + cartItem.getProductId()));

            if (product.getStock() != null && product.getStock() < cartItem.getQuantity().intValue()) {
                throw new BusinessException("商品库存不足：" + product.getName());
            }

            BigDecimal subtotal = product.getPrice().multiply(cartItem.getQuantity());
            totalAmount = totalAmount.add(subtotal);

            StageOrderItem orderItem = new StageOrderItem();
            orderItem.setProductId(product.getProductId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItems.add(orderItem);

            if (product.getStock() != null) {
                product.setStock(product.getStock() - cartItem.getQuantity().intValue());
                productRepository.save(product);
            }
        }

        StageOrder order = new StageOrder();
        order.setStageId(stageId);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(StageOrder.OrderStatus.CREATED);
        StageOrder savedOrder = stageOrderRepository.save(order);

        for (StageOrderItem orderItem : orderItems) {
            orderItem.setStageOrderId(savedOrder.getId());
            stageOrderItemRepository.save(orderItem);
        }

        stageCartService.clearCart(cart.getId());
        return buildOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<StageOrderResponse> myOrders(Long userId) {
        return stageOrderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::buildOrderResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StageOrderResponse getById(Long orderId, Long userId) {
        StageOrder order = stageOrderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("订单不存在"));

        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权限访问该订单");
        }

        return buildOrderResponse(order);
    }

    @Transactional
    public StageOrderResponse updateStatus(Long orderId, Long userId, StageOrder.OrderStatus status) {
        StageOrder order = stageOrderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("订单不存在"));

        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权限更新该订单");
        }

        order.setStatus(status);
        return buildOrderResponse(stageOrderRepository.save(order));
    }

    private StageOrderResponse buildOrderResponse(StageOrder order) {
        List<StageOrderItem> items = stageOrderItemRepository.findByStageOrderId(order.getId());
        List<StageOrderItemResponse> itemResponses = new ArrayList<>();

        for (StageOrderItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new BusinessException("商品不存在，ID=" + item.getProductId()));

            StageOrderItemResponse response = new StageOrderItemResponse();
            response.setId(item.getId());
            response.setProductId(item.getProductId());
            response.setProductName(product.getName());
            response.setQuantity(item.getQuantity());
            response.setPrice(item.getPrice());
            response.setSubtotal(item.getPrice().multiply(item.getQuantity()));
            itemResponses.add(response);
        }

        StageOrderResponse response = new StageOrderResponse();
        response.setId(order.getId());
        response.setStageId(order.getStageId());
        response.setUserId(order.getUserId());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setItems(itemResponses);
        return response;
    }



    // Service
    @Transactional(readOnly = true)
    public StagePurchasedMaterialsResponse getPurchasedMaterials(Long stageId, Long userId) {
        System.out.println(">>> getPurchasedMaterials called, stageId=" + stageId + ", userId=" + userId);

        // 1️⃣ 查出该阶段所有 PAID 的订单
        List<StageOrder> orders = stageOrderRepository
                .findByStageIdAndUserIdAndStatus(stageId, userId, StageOrder.OrderStatus.PAID);

        System.out.println("Found " + orders.size() + " PAID orders for stageId=" + stageId + ", userId=" + userId);
        for (StageOrder o : orders) {
            System.out.println("Order: id=" + o.getId() + ", total=" + o.getTotalAmount());
        }

        Map<Long, StagePurchasedMaterialsResponse.MainMaterial> mainMap = new HashMap<>();
        Map<Long, StagePurchasedMaterialsResponse.AuxMaterial> auxMap = new HashMap<>();

        for (StageOrder order : orders) {
            List<StageOrderItem> items = stageOrderItemRepository.findByStageOrderId(order.getId());
            System.out.println("Order id=" + order.getId() + " has " + items.size() + " items");

            for (StageOrderItem item : items) {
                System.out.println("  Item: productId=" + item.getProductId() + ", qty=" + item.getQuantity() + ", price=" + item.getPrice());
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new BusinessException("商品不存在，ID=" + item.getProductId()));

                BigDecimal subtotal = item.getPrice().multiply(item.getQuantity());

                // 主材
                if (product.getMaterialType() == 1) {
                    StagePurchasedMaterialsResponse.MainMaterial mm = mainMap.computeIfAbsent(product.getProductId(), k -> {
                        StagePurchasedMaterialsResponse.MainMaterial m = new StagePurchasedMaterialsResponse.MainMaterial();
                        m.setType(product.getMainCategory());
                        m.setDisplayName(product.getName());
                        m.setUnit(product.getUnit());
                        m.setBrand(product.getBrand());
                        m.setQuantity(BigDecimal.ZERO);
                        m.setSubtotal(BigDecimal.ZERO);
                        m.setUnitPrice(product.getPrice());
                        m.setRemark("");
                        return m;
                    });
                    mm.setQuantity(mm.getQuantity().add(item.getQuantity()));
                    mm.setSubtotal(mm.getSubtotal().add(subtotal));
                } else { // 辅材
                    StagePurchasedMaterialsResponse.AuxMaterial am = auxMap.computeIfAbsent(product.getProductId(), k -> {
                        StagePurchasedMaterialsResponse.AuxMaterial a = new StagePurchasedMaterialsResponse.AuxMaterial();
                        a.setName(product.getName());
                        a.setCategory(product.getSubCategory());
                        a.setUnit(product.getUnit());
                        a.setBrand(product.getBrand());
                        a.setQuantity(BigDecimal.ZERO);
                        a.setUnitPrice(product.getPrice());
                        a.setSubtotal(BigDecimal.ZERO);
                        return a;
                    });
                    am.setQuantity(am.getQuantity().add(item.getQuantity()));
                    am.setSubtotal(am.getSubtotal().add(subtotal));
                }
            }
        }

        StagePurchasedMaterialsResponse response = new StagePurchasedMaterialsResponse();
        response.getMainMaterials().addAll(mainMap.values());
        response.getAuxiliaryMaterials().addAll(auxMap.values());

        System.out.println("Returning purchasedMaterials: main=" + response.getMainMaterials().size() +
                ", aux=" + response.getAuxiliaryMaterials().size());

        return response;
    }


}
