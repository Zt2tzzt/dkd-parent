package com.dkd.manage.mapper;

import java.util.List;

import com.dkd.manage.domain.Channel;

/**
 * 售货机货道Mapper接口
 *
 * @author zetian
 * @date 2024-12-09
 */
public interface ChannelMapper {
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
     * 删除售货机货道
     *
     * @param id 售货机货道主键
     * @return 结果
     */
    public int deleteChannelById(Long id);

    /**
     * 批量删除售货机货道
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteChannelByIds(Long[] ids);

    /**
     * 此方法用于：批量新增售货机货道
     *
     * @param list 货道集合
     * @return int
     */
    int batchInsertChannel(List<Channel> list);

    /**
     * 根据商品 id 查询货道数量
     *
     * @param skuIds 商品 id
     * @return int
     */
    int selectChannelBySKuIds(Long[] skuIds);
}
