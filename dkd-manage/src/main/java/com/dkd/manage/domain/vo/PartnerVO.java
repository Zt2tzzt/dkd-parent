package com.dkd.manage.domain.vo;

import com.dkd.manage.domain.Partner;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartnerVO extends Partner {
    // 点位数量
    private Integer nodeCount;
}
