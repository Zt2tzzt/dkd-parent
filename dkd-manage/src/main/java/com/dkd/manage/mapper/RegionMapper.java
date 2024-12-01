package com.dkd.manage.mapper;

import com.dkd.manage.domain.Region;
import com.dkd.manage.domain.vo.RegionVO;

import java.util.List;

/**
 * 区域管理Mapper接口
 *
 * @author zetian
 * @date 2024-12-01
 */
public interface RegionMapper {
    /**
     * 查询区域管理
     *
     * @param id 区域管理主键
     * @return 区域管理
     */
    Region selectRegionById(Long id);

    /**
     * 查询区域管理列表
     *
     * @param region 区域管理
     * @return 区域管理集合
     */
    List<Region> selectRegionList(Region region);

    /**
     * 新增区域管理
     *
     * @param region 区域管理
     * @return 结果
     */
    int insertRegion(Region region);

    /**
     * 修改区域管理
     *
     * @param region 区域管理
     * @return 结果
     */
    int updateRegion(Region region);

    /**
     * 删除区域管理
     *
     * @param id 区域管理主键
     * @return 结果
     */
    int deleteRegionById(Long id);

    /**
     * 批量删除区域管理
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteRegionByIds(Long[] ids);

    /**
     * 此方法用于：查询区域管理列表
     *
     * @param region 区域管理
     * @return List<RegionVO>
     */
    List<RegionVO> selectRegionVOList(Region region);
}
