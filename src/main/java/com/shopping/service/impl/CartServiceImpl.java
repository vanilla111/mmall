package com.shopping.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.shopping.common.Const;
import com.shopping.common.ResponseCode;
import com.shopping.common.ServerResponse;
import com.shopping.dao.CartMapper;
import com.shopping.dao.ProductMapper;
import com.shopping.pojo.Cart;
import com.shopping.pojo.Product;
import com.shopping.service.ICartService;
import com.shopping.util.BigDecimalUtil;
import com.shopping.util.PropertiesUtil;
import com.shopping.vo.CartProductVo;
import com.shopping.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by wang on 2017/5/20.
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    public ServerResponse<CartVo> list(Integer userId) {
        CartVo cartVo = getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //todo 判断商品ID是否有对应的存在，状态也要为出售状态
        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart == null) {
            if (count <= 0) {
                return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
            }
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        } else {
            //已经在购物车里了
            //如果已经存在，数量相加
            if (cart.getQuantity() + count <= 0) {
                return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
            }
            cart.setQuantity(cart.getQuantity() + count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }

        return this.list(userId);
    }

    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart == null) {
//            Cart cartItem = new Cart();
//            cartItem.setQuantity(count);
//            cartItem.setChecked(Const.cart.CHECKED);
//            cartItem.setProductId(productId);
//            cartItem.setUserId(userId);
//            cartMapper.insert(cartItem);
        } else {
            //已经在购物车里了
            if (count <= 0) {
                return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
            }
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }

        return this.list(userId);
    }

    public ServerResponse<CartVo> delete_products(Integer userId, String productIds) {
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productList)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        cartMapper.deleteByUseridAndProductIds(userId, productList);

        return this.list(userId);
    }

    public ServerResponse<CartVo> selectOrUnselect(Integer userId, Integer checked, Integer productId) {
        cartMapper.checkedOrUncheckedProduct(userId, checked, productId);
        return this.list(userId);
    }

    public ServerResponse<Integer> getCartProductCount(Integer userid) {
        if (userid == null)
            return ServerResponse.createBySuccess(0);

        int count = cartMapper.selectCartProductCount(userid);

        return ServerResponse.createBySuccess(count);
    }

    private CartVo getCartVoLimit(Integer userId) {
        //购物车
        CartVo cartVo = new CartVo();
        //购物车清单
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        //购物车中商品详细
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");

        if (CollectionUtils.isNotEmpty(cartList)) {
            for (Cart cart : cartList) {
                CartProductVo p = new CartProductVo();
                p.setId(cart.getId());
                p.setProductId(cart.getProductId());
                p.setQuantity(cart.getQuantity());
                p.setUserId(cart.getUserId());
                Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                if (product != null) {
                    p.setProductMainImage(product.getMainImage());
                    p.setProductSubtitle(product.getSubtitle());
                    p.setProductName(product.getName());
                    p.setProductStatus(product.getStatus());
                    p.setProductStock(product.getStock());
                    p.setProductPrice(product.getPrice());
                    //判断库存
                    int buyLimitCount = 0;
                    if (product.getStock() >= cart.getQuantity()) {
                        buyLimitCount = cart.getQuantity();
                        p.setLimitQuantity(Const.cart.CART_LIMIT_SUCCESS);
                    } else {
                        buyLimitCount = product.getStock();
                        p.setLimitQuantity(Const.cart.CART_LIMIT_FAIL);
                        //更新有效库存
                        Cart update_cart = new Cart();
                        update_cart.setId(cart.getId());
                        update_cart.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(update_cart);
                    }
                    p.setQuantity(buyLimitCount);
                    p.setProductTotalPrice(BigDecimalUtil.mul(buyLimitCount, product.getPrice().doubleValue()));
                    p.setProductChecked(cart.getChecked());

                }
                if (cart.getChecked() == Const.cart.CHECKED) {
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), p.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(p);
            }
        }

        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    private boolean getAllCheckedStatus(Integer userId) {
        if (userId == null) {
            return false;
        }

        return cartMapper.selectCartProductCheckedStatus(userId) == 0;
    }
}
