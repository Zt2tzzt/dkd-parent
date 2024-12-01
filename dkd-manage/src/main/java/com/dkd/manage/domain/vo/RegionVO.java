package com.dkd.manage.domain.vo;

import com.dkd.manage.domain.Region;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionVO extends Region {
    // 点位数量
    private Integer nodeCount;
}
