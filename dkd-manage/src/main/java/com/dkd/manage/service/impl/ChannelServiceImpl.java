package com.dkd.manage.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.dkd.common.utils.DateUtils;
import com.dkd.manage.domain.dto.ChannelConfigDTO;
import com.dkd.manage.domain.dto.ChannelSkuDTO;
import com.dkd.manage.domain.vo.ChannelVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dkd.manage.mapper.ChannelMapper;
import com.dkd.manage.domain.Channel;
import com.dkd.manage.service.IChannelService;

/**
 * 售货机货道Service业务层处理
 *
 * @author zetian
 * @date 2024-12-09
 */
@Service
public class ChannelServiceImpl implements IChannelService {
    @Autowired
    private ChannelMapper channelMapper;

    /**
     * 查询售货机货道
     *
     * @param id 售货机货道主键
     * @return 售货机货道
     */
    @Override
    public Channel selectChannelById(Long id) {
        return channelMapper.selectChannelById(id);
    }

    /**
     * 查询售货机货道列表
     *
     * @param channel 售货机货道
     * @return 售货机货道
     */
    @Override
    public List<Channel> selectChannelList(Channel channel) {
        return channelMapper.selectChannelList(channel);
    }

    /**
     * 新增售货机货道
     *
     * @param channel 售货机货道
     * @return 结果
     */
    @Override
    public int insertChannel(Channel channel) {
        channel.setCreateTime(DateUtils.getNowDate());
        return channelMapper.insertChannel(channel);
    }

    /**
     * 修改售货机货道
     *
     * @param channel 售货机货道
     * @return 结果
     */
    @Override
    public int updateChannel(Channel channel) {
        channel.setUpdateTime(DateUtils.getNowDate());
        return channelMapper.updateChannel(channel);
    }

    /**
     * 设置货道关联商品
     *
     * @param channelConfigDTO 货道配置信息
     * @return int
     */
    @Override
    public int setChannels(ChannelConfigDTO channelConfigDTO) {
        // 将 dto 转为 po 对象
        List<ChannelSkuDTO> channeVOlList = channelConfigDTO.getChannelList();

        List<Channel> channelList = channeVOlList.stream().map(dto -> {
            // 根据售货机编号，货道编号查询货道信息
            Channel channel = channelMapper.selectChannelByInnerCodeAndChannelCode(dto.getInnerCode(), dto.getChannelCode());
            if (channel == null) return null;
            channel.setSkuId(dto.getSkuId()); // 关联最新商品 id
            channel.setUpdateTime(DateUtils.getNowDate()); // 更新修改时间
            return channel;
        }).collect(Collectors.toList());

        // 修改货道
        return channelMapper.updateChannelsBatch(channelList);
    }

    /**
     * 批量删除售货机货道
     *
     * @param ids 需要删除的售货机货道主键
     * @return 结果
     */
    @Override
    public int deleteChannelByIds(Long[] ids) {
        return channelMapper.deleteChannelByIds(ids);
    }

    /**
     * 删除售货机货道信息
     *
     * @param id 售货机货道主键
     * @return 结果
     */
    @Override
    public int deleteChannelById(Long id) {
        return channelMapper.deleteChannelById(id);
    }

    @Override
    public void batchInsertChannel(List<Channel> channels) {
        channelMapper.batchInsertChannel(channels);
    }

    /**
     * 根据售货机编号，查询货道列表
     *
     * @param innerCode 售货机编号
     * @return List<ChannelVO>
     */
    @Override
    public List<ChannelVO> selectChannelVOListByInnerCode(String innerCode) {
        return channelMapper.selectChannelVOListByInnerCode(innerCode);
    }
}
