package com.dkd.manage.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Long createType;// 创建类型
    private String innerCode;// 设备编号
    private Long userId;// 执行人id
    private Long assignorId;// 指派人id
    private Long productTypeId;// 工单类型
    private String desc;// 描述信息
    private List<TaskDetailDTO> details;// 工单详情（只有补货工单才涉及）
}
