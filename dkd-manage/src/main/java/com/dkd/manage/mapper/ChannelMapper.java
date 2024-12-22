package com.dkd.manage.mapper;

import java.util.List;

import com.dkd.manage.domain.Channel;
import com.dkd.manage.domain.vo.ChannelVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
     * 根据售货机编号，查询货道列表
     *
     * @param innerCode 售货机编号
     * @return List<ChannelVO>
     */
    List<ChannelVO> selectChannelVOListByInnerCode(String innerCode);

    /**
     * 根据售货机编号和货道编号查询货道信息
     *
     * @param innerCode 售货机编号
     * @param channelCode 货道编号
     * @return Channel
     */
    @Select("SELECT id, channel_code, sku_id, vm_id, inner_code, max_capacity, current_capacity, last_supply_time, create_time, update_time FROM channel WHERE inner_code = #{innerCode} AND channel_code = #{channelCode}")
    Channel selectChannelByInnerCodeAndChannelCode(@Param("innerCode") String innerCode, @Param("channelCode") String channelCode);

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
     * 批量修改货道信息
     *
     * @param channelList 货道集合
     * @return int
     */
    int updateChannelsBatch(List<Channel> channelList);

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
