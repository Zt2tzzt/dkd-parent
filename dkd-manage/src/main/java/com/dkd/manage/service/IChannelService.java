package com.dkd.manage.service;

import java.util.List;

import com.dkd.manage.domain.Channel;
import com.dkd.manage.domain.dto.ChannelConfigDTO;
import com.dkd.manage.domain.vo.ChannelVO;

/**
 * 售货机货道Service接口
 *
 * @author zetian
 * @date 2024-12-09
 */
public interface IChannelService {
    /**
     * 查询售货机货道
     *
     * @param id 售货机货道主键
     * @return 售货机货道
     */
    public Channel selectChannelById(Long id);

    /**
     * 查询售货机货道列表
     *
     * @param channel 售货机货道
     * @return 售货机货道集合
     */
    public List<Channel> selectChannelList(Channel channel);

    /**
     * 根据售货机编号，查询货道列表
     *
     * @param innerCode 售货机编号
     * @return List<ChannelVO>
     */
    List<ChannelVO> selectChannelVOListByInnerCode(String innerCode);

    /**
     * 新增售货机货道
     *
     * @param channel 售货机货道
     * @return 结果
     */
    public int insertChannel(Channel channel);

    /**
     * 修改售货机货道
     *
     * @param channel 售货机货道
     * @return 结果
     */
    public int updateChannel(Channel channel);

    /**
     * 设置货道关联商品
     *
     * @param channelConfigDTO 货道配置信息
     * @return int
     */
    int setChannels(ChannelConfigDTO channelConfigDTO);

    /**
     * 批量删除售货机货道
     *
     * @param ids 需要删除的售货机货道主键集合
     * @return 结果
     */
    public int deleteChannelByIds(Long[] ids);

    /**
     * 删除售货机货道信息
     *
     * @param id 售货机货道主键
     * @return 结果
     */
    public int deleteChannelById(Long id);

    /**
     * 此方法用于：批量插入货道记录
     *
     * @param channels 货道集合
     */
    void batchInsertChannel(List<Channel> channels);
}
