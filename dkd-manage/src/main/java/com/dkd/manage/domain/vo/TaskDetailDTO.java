package com.dkd.manage.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDetailDTO {
    private String channelCode;// 货道编号
    private Long expectCapacity;// 补货数量
    private Long skuld;// 商品id
    private String skuName;// 商品名称
    private String skuImage;// 商品图片
}
