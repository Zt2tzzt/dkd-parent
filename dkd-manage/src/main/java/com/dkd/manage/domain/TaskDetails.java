package com.dkd.manage.domain;

import com.dkd.common.annotation.Excel;
import com.dkd.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 工单详情对象 task_details
 *
 * @author zetian
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDetails extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "工单详情Id")
    private Long detailsId;

    @Excel(name = "工单Id")
    @ApiModelProperty(value = "工单Id")
    private Long taskId;

    @Excel(name = "货道编号")
    @ApiModelProperty(value = "货道编号")
    private String channelCode;

    @Excel(name = "补货期望容量")
    @ApiModelProperty(value = "补货期望容量")
    private Long expectCapacity;

    @Excel(name = "商品Id")
    @ApiModelProperty(value = "商品Id")
    private Long skuId;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String skuName;

    @Excel(name = "商品图片")
    @ApiModelProperty(value = "商品图片")
    private String skuImage;
}