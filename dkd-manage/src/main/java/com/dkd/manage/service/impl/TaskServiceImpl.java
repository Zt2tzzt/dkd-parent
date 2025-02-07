package com.dkd.manage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.dkd.common.constant.DkdContants;
import com.dkd.common.exception.ServiceException;
import com.dkd.common.utils.DateUtils;
import com.dkd.manage.domain.Emp;
import com.dkd.manage.domain.Task;
import com.dkd.manage.domain.TaskDetails;
import com.dkd.manage.domain.VendingMachine;
import com.dkd.manage.domain.vo.TaskDTO;
import com.dkd.manage.domain.vo.TaskDetailDTO;
import com.dkd.manage.domain.vo.TaskVO;
import com.dkd.manage.mapper.TaskMapper;
import com.dkd.manage.service.IEmpService;
import com.dkd.manage.service.ITaskDetailsService;
import com.dkd.manage.service.ITaskService;
import com.dkd.manage.service.IVendingMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 工单Service业务层处理
 *
 * @author zetian
 */
@Service
public class TaskServiceImpl implements ITaskService {
    private final TaskMapper taskMapper;
    private final IVendingMachineService vendingMachineService;
    private final IEmpService empService;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final ITaskDetailsService taskDetailsService;

    @Autowired
    public TaskServiceImpl(TaskMapper taskMapper, IVendingMachineService vendingMachineService, IEmpService empService, RedisTemplate<Object, Object> redisTemplate, ITaskDetailsService taskDetailsService) {
        this.taskMapper = taskMapper;
        this.vendingMachineService = vendingMachineService;
        this.empService = empService;
        this.redisTemplate = redisTemplate;
        this.taskDetailsService = taskDetailsService;
    }

    /**
     * 查询工单
     *
     * @param taskId 工单主键
     * @return 工单
     */
    @Override
    public Task selectTaskByTaskId(Long taskId) {
        return taskMapper.selectTaskByTaskId(taskId);
    }

    /**
     * 查询工单列表
     *
     * @param task 工单
     * @return 工单
     */
    @Override
    public List<Task> selectTaskList(Task task) {
        return taskMapper.selectTaskList(task);
    }

    /**
     * 查询工单列表
     *
     * @param task 工单
     * @return List<TaskVO>
     */
    @Override
    public List<TaskVO> selectTaskVOList(Task task) {
        return taskMapper.selectTasVOList(task);
    }

    /**
     * 新增工单
     *
     * @param task 工单
     * @return 结果
     */
    @Override
    public int insertTask(Task task) {
        task.setCreateTime(DateUtils.getNowDate());
        return taskMapper.insertTask(task);
    }

    /**
     * 此方法用于，校验售货机状态与工单类型是否相符
     *
     * @param vmStatus      设备状态
     * @param productTypeId 工单类型
     */
    private void checkCreateTask(Long vmStatus, Long productTypeId) {
        // 如果是投放工单，设备在运行中，抛出异常
        if (DkdContants.TASK_TYPE_DEPLOY.equals(productTypeId) && DkdContants.VM_STATUS_RUNNING.equals(vmStatus))
            throw new ServiceException("设备正在运行中，无法进行投放工单");

        // 如果是维修工单，设备不在运行中，抛出异常
        if (DkdContants.TASK_TYPE_REPAIR.equals(productTypeId) && !DkdContants.VM_STATUS_RUNNING.equals(vmStatus))
            throw new ServiceException("设备不在运行中，无法进行维修工单");

        // 如果是补货工单，设备不在运行中，抛出异常
        if (DkdContants.TASK_TYPE_SUPPLY.equals(productTypeId) && !DkdContants.VM_STATUS_RUNNING.equals(vmStatus))
            throw new ServiceException("设备不在运行中，无法进行补货工单");

        // 如果是撤机工单，设备不在运行中，抛出异常
        if (DkdContants.TASK_TYPE_REVOKE.equals(productTypeId) && !DkdContants.VM_STATUS_RUNNING.equals(vmStatus))
            throw new ServiceException("设备不在运行中，无法进行撤机工单");
    }

    /**
     * 此方法用于：检查设备是否有未完成的同类型工单
     *
     * @param taskDTO 工单详情
     */
    private void hasTask(TaskDTO taskDTO) {
        Task taskParam = new Task();
        taskParam.setInnerCode(taskDTO.getInnerCode());
        taskParam.setProductTypeId(taskDTO.getProductTypeId());
        taskParam.setTaskStatus(DkdContants.TASK_STATUS_PROGRESS);
        List<Task> tasks = taskMapper.selectTaskList(taskParam);
        if (tasks != null && !tasks.isEmpty())
            throw new ServiceException("设备存在未完成的工单");
    }

    /**
     * 此方法用于：生成工单编号
     *
     * @return 工单编号
     */
    private String generateTaskCode() {
        // 获取当前日期，并格式化为 yyyyMMdd
        String dateStr = DateUtils.getDate().replace("-", "");

        // 根据日期，生成当前日期的 key，用于 Redis 中的自增。
        String key = "dkd.task.code" + dateStr;

        // 判断 key 是否存在
        if (!redisTemplate.hasKey(key)) {
            redisTemplate.opsForValue().set(key, 1, Duration.ofDays(1)); // 设置初始值 1，并设置过期时间为 1 天
            return dateStr + "0001";
        }
        Long value = redisTemplate.opsForValue().increment(key);
        return dateStr + String.format("%04d", value);
    }

    /**
     * 此方法用于：批量新增工单详情
     *
     * @param taskDTO 工单详情
     * @return int
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertTaskDTO(TaskDTO taskDTO) {
        // 查询售货机是否存在
        VendingMachine vm = vendingMachineService.selectVendingMachineByInnerCode(taskDTO.getInnerCode());
        if (vm == null)
            throw new ServiceException("售货机不存在");

        // 校验售货机状态与工单类型是否相符
        checkCreateTask(vm.getVmStatus(), taskDTO.getProductTypeId());

        // 检查设备是否有未完成的同类型工单
        hasTask(taskDTO);

        // 判断员工是否存在
        Emp emp = empService.selectEmpById(taskDTO.getUserId());
        if (emp == null)
            throw new ServiceException("员工不存在");

        // 校验员工地区是否匹配
        if (!emp.getRegionId().equals(vm.getRegionId()))
            throw new ServiceException("员工地区与售货机地区不匹配");

        // 将 DTO 转为 PO，并补充属性，保存工单
        Task task = BeanUtil.copyProperties(taskDTO, Task.class); // 将 taskDTO 中的 6 个属性复制到 task 上
        task.setTaskStatus(DkdContants.TASK_STATUS_CREATE); // 创建工单
        task.setUserName(emp.getUserName());
        task.setRegionId(vm.getRegionId());
        task.setAddr(vm.getAddr());
        task.setCreateTime(DateUtils.getNowDate());
        task.setTaskCode(generateTaskCode());
        int taskResult = taskMapper.insertTask(task);

        // 判断是否为补货工单
        if (DkdContants.TASK_TYPE_SUPPLY.equals(taskDTO.getProductTypeId())) {
            // 补货工单，需要批量新增工单详情
            List<TaskDetailDTO> detailDTOs = taskDTO.getDetails();
            if (detailDTOs == null || detailDTOs.isEmpty())
                throw new ServiceException("补货工单详情不能为空");

            List<TaskDetails> taskDetais = detailDTOs.stream()
                    .map(dto -> {
                        TaskDetails taskDetails = BeanUtil.copyProperties(dto, TaskDetails.class);
                        taskDetails.setTaskId(task.getTaskId());
                        return taskDetails;
                    })
                    .collect(Collectors.toList());
            taskDetailsService.inserTaskDetailsBatch(taskDetais);
        }

        return taskResult;
    }

    /**
     * 修改工单
     *
     * @param task 工单
     * @return 结果
     */
    @Override
    public int updateTask(Task task) {
        task.setUpdateTime(DateUtils.getNowDate());
        return taskMapper.updateTask(task);
    }

    /**
     * 此方法用于：取消工单
     * @param task 工单
     * @return int
     */
    @Override
    public int cancelTask(Task task) {
        // 判断工单状态是否可以取消
        Task taskDb = taskMapper.selectTaskByTaskId(task.getTaskId());

        // 工单状态，如果已取消，则跑出异常
        if (DkdContants.TASK_STATUS_CANCEL.equals(taskDb.getTaskStatus()))
            throw new ServiceException("该工单已取消了，不能再次取消");

        // 工单状态如果已完成，则抛出异常
        if (DkdContants.TASK_STATUS_FINISH.equals(taskDb.getTaskStatus()))
            throw new ServiceException("该工单已完成，不能取消");

        // 设置更新字段
        task.setTaskStatus(DkdContants.TASK_STATUS_CANCEL); // 工单状态：取消
        task.setUpdateTime(DateUtils.getNowDate());
        return taskMapper.updateTask(task);
    }

    /**
     * 批量删除工单
     *
     * @param taskIds 需要删除的工单主键
     * @return 结果
     */
    @Override
    public int deleteTaskByTaskIds(Long[] taskIds) {
        return taskMapper.deleteTaskByTaskIds(taskIds);
    }

    /**
     * 删除工单信息
     *
     * @param taskId 工单主键
     * @return 结果
     */
    @Override
    public int deleteTaskByTaskId(Long taskId) {
        return taskMapper.deleteTaskByTaskId(taskId);
    }
}
