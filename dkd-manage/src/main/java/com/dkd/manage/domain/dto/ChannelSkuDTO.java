package com.dkd.manage.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelSkuDTO {
    private String innerCode; // 售货机编号
    private String channelCode; // 货道编号
    private Long skuId; // 商品ID
}
