package com.dkd.manage.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelConfigDTO {
    private String innerCode;
    private List<ChannelSkuDTO> channelList;
}
