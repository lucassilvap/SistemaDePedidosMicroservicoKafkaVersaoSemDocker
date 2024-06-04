package com.orderservice.orderservice.core.dto;

import com.orderservice.orderservice.core.document.OrderProduct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    private List<OrderProduct> orderProductList;


}
