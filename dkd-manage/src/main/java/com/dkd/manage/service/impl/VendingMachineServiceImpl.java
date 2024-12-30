package com.dkd.manage.service.impl;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.io.unit.DataUnit;
import com.dkd.common.constant.DkdContants;
import com.dkd.common.utils.DateUtils;
import com.dkd.common.utils.bean.BeanUtils;
import com.dkd.common.utils.uuid.UUIDUtils;
import com.dkd.manage.domain.Channel;
import com.dkd.manage.domain.Node;
import com.dkd.manage.domain.VmType;
import com.dkd.manage.service.IChannelService;
import com.dkd.manage.service.INodeService;
import com.dkd.manage.service.IVmTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dkd.manage.mapper.VendingMachineMapper;
import com.dkd.manage.domain.VendingMachine;
import com.dkd.manage.service.IVendingMachineService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 设备管理Service业务层处理
 *
 * @author zetian
 * @date 2024-12-09
 */
@Service
public class VendingMachineServiceImpl implements IVendingMachineService {
    @Autowired
    private VendingMachineMapper vendingMachineMapper;
    @Autowired
    private IVmTypeService iVmTypeService;
    @Autowired
    private INodeService iNodeService;
    @Autowired
    private IChannelService iChannelService;

    /**
     * 查询设备管理
     *
     * @param id 设备管理主键
     * @return 设备管理
     */
    @Override
    public VendingMachine selectVendingMachineById(Long id) {
        return vendingMachineMapper.selectVendingMachineById(id);
    }

    /**
     * 查询设备管理列表
     *
     * @param vendingMachine 设备管理
     * @return 设备管理
     */
    @Override
    public List<VendingMachine> selectVendingMachineList(VendingMachine vendingMachine) {
        return vendingMachineMapper.selectVendingMachineList(vendingMachine);
    }

    /**
     * 此方法用于：根据设备编号查询设备信息
     *
     * @param innerCode 设备编号
     * @return 设备信息
     */
    @Override
    public VendingMachine selectVendingMachineByInnerCode(String innerCode) {
        return vendingMachineMapper.selectVendingMachineByInnerCode(innerCode);
    }

    /**
     * 新增设备管理
     *
     * @param vendingMachine 设备管理
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertVendingMachine(VendingMachine vendingMachine) {
        // 1.新增设备
        // 生成 8 位的唯一标识，补充设备编码
        String innerCode = UUIDUtils.getUUID();
        vendingMachine.setInnerCode(innerCode);

        // 查询售货机类型表，补充设备容量
        VmType vmType = iVmTypeService.selectVmTypeById(vendingMachine.getVmTypeId());
        vendingMachine.setChannelMaxCapacity(vmType.getChannelMaxCapacity());

        // 查询点位表，补充区域、合作商、点位等信息
        Node node = iNodeService.selectNodeById(vendingMachine.getNodeId());
        BeanUtils.copyProperties(node, vendingMachine, "id"); // 合作商 id、区域 id
        vendingMachine.setBusinessType(node.getBusinessDistrictType()); // 商圈类型
        vendingMachine.setAddr(node.getAddress()); // 详细地址

        // 补充其他字段
        vendingMachine.setVmStatus(DkdContants.VM_STATUS_NODEPLOY); // 设置设备状态
        vendingMachine.setCreateTime(DateUtils.getNowDate()); // 创建时间
        vendingMachine.setUpdateTime(DateUtils.getNowDate()); // 更新时间
        int result = vendingMachineMapper.insertVendingMachine(vendingMachine);

        // 2.新增货道
        ArrayList<Channel> channels = new ArrayList<>();
        for (int i = 1; i <= vmType.getVmRow(); i++) {
            for (int j = 1; j <= vmType.getVmCol(); j++) {
                Channel channel = new Channel();
                channel.setChannelCode(i + "-" + j); // 货道编号
                channel.setVmId(vendingMachine.getId()); // 售货机 id
                channel.setInnerCode(vendingMachine.getInnerCode()); // 设备编码
                channel.setMaxCapacity(vendingMachine.getChannelMaxCapacity()); // 货道最大容量
                channel.setCreateTime(DateUtils.getNowDate());
                channel.setUpdateTime(DateUtils.getNowDate());
                channels.add(channel);
            }
        }
        iChannelService.batchInsertChannel(channels);

        return result;
    }

    /**
     * 修改设备管理
     *
     * @param vendingMachine 设备管理
     * @return 结果
     */
    @Override
    public int updateVendingMachine(VendingMachine vendingMachine) {
        // 冗余字段，查询点位表
        Long nodeId = vendingMachine.getNodeId();
        if (nodeId != null) {
            Node node = iNodeService.selectNodeById(nodeId);
            BeanUtils.copyProperties(node, vendingMachine, "id"); // 合作商 id、区域 id
            vendingMachine.setBusinessType(node.getBusinessDistrictType()); // 商圈类型
            vendingMachine.setAddr(node.getAddress()); // 设备地址
            vendingMachine.setUpdateTime(DateUtils.getNowDate()); // 更新时间
        }

        return vendingMachineMapper.updateVendingMachine(vendingMachine);
    }

    /**
     * 批量删除设备管理
     *
     * @param ids 需要删除的设备管理主键
     * @return 结果
     */
    @Override
    public int deleteVendingMachineByIds(Long[] ids) {
        return vendingMachineMapper.deleteVendingMachineByIds(ids);
    }

    /**
     * 删除设备管理信息
     *
     * @param id 设备管理主键
     * @return 结果
     */
    @Override
    public int deleteVendingMachineById(Long id) {
        return vendingMachineMapper.deleteVendingMachineById(id);
    }
}
