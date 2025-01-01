package com.dkd.manage.mapper;

import com.dkd.manage.domain.VendingMachine;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 设备管理Mapper接口
 *
 * @author zetian
 * @date 2024-12-09
 */
public interface VendingMachineMapper {
    /**
     * 查询设备管理
     *
     * @param id 设备管理主键
     * @return 设备管理
     */
    VendingMachine selectVendingMachineById(Long id);

    /**
     * 查询设备管理列表
     *
     * @param vendingMachine 设备管理
     * @return 设备管理集合
     */
    List<VendingMachine> selectVendingMachineList(VendingMachine vendingMachine);

    /**
     * 此方法用于：根据设备编号查询设备信息
     *
     * @param innerCode 设备编号
     * @return 设备信息
     */
    @Select("SELECT id, inner_code, channel_max_capacity, node_id, addr, last_supply_time, business_type, region_id, partner_id, vm_type_id, vm_status, running_status, longitudes, latitude, client_id, policy_id, create_time, update_time FROM vending_machine WHERE inner_code = #{innerCode}")
    VendingMachine selectVendingMachineByInnerCode(String innerCode);

    /**
     * 新增设备管理
     *
     * @param vendingMachine 设备管理
     * @return 结果
     */
    int insertVendingMachine(VendingMachine vendingMachine);

    /**
     * 修改设备管理
     *
     * @param vendingMachine 设备管理
     * @return 结果
     */
    int updateVendingMachine(VendingMachine vendingMachine);

    /**
     * 删除设备管理
     *
     * @param id 设备管理主键
     * @return 结果
     */
    int deleteVendingMachineById(Long id);

    /**
     * 批量删除设备管理
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteVendingMachineByIds(Long[] ids);
}
