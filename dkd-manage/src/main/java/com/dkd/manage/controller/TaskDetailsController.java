package com.dkd.manage.controller;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;

import com.dkd.common.core.domain.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dkd.common.annotation.Log;
import com.dkd.common.core.controller.BaseController;
import com.dkd.common.core.domain.AjaxResult;
import com.dkd.common.enums.BusinessType;
import com.dkd.manage.domain.TaskDetails;
import com.dkd.manage.service.ITaskDetailsService;
import com.dkd.common.utils.poi.ExcelUtil;
import com.dkd.common.core.page.TableDataInfo;

/**
 * 工单详情Controller
 *
 * @author zetian
 */
@Api(tags = "工单详情管理")
@RestController
@RequestMapping("/manage/taskDetails")
public class TaskDetailsController extends BaseController {
    @Autowired
    private ITaskDetailsService taskDetailsService;

    /**
     * 查询工单详情列表
     */
    @ApiOperation(value = "查询工单详情列表", notes = "返回工单详情列表数据")
    @PreAuthorize("@ss.hasPermi('manage:taskDetails:list')")
    @GetMapping("/list")
    public TableDataInfo list(@ApiParam(value = "工单详情信息", required = true) TaskDetails taskDetails) {
        startPage();
        List<TaskDetails> list = taskDetailsService.selectTaskDetailsList(taskDetails);
        return getDataTable(list);
    }

    /**
     * 根据工单Id查询工单详情列表
     */
    @ApiOperation(value = "根据工单Id查询工单详情列表", notes = "根据工单Id返回工单详情列表数据")
    @PreAuthorize("@ss.hasPermi('manage:taskDetails:list')")
    @GetMapping("/byTaskId/{taskId}")
    public R<List<TaskDetails>> listByTaskId(@ApiParam(value = "工单Id", required = true) @PathVariable Long taskId) {
        TaskDetails taskDetails = new TaskDetails();
        taskDetails.setTaskId(taskId);
        return R.ok(taskDetailsService.selectTaskDetailsList(taskDetails));
    }

    /**
     * 导出工单详情列表
     */
    @ApiOperation(value = "导出工单详情列表", notes = "导出工单详情列表数据到Excel")
    @PreAuthorize("@ss.hasPermi('manage:taskDetails:export')")
    @Log(title = "工单详情", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, @ApiParam(value = "工单详情信息", required = true) TaskDetails taskDetails) {
        List<TaskDetails> list = taskDetailsService.selectTaskDetailsList(taskDetails);
        ExcelUtil<TaskDetails> util = new ExcelUtil<TaskDetails>(TaskDetails.class);
        util.exportExcel(response, list, "工单详情数据");
    }

    /**
     * 获取工单详情详细信息
     */
    @ApiOperation(value = "获取工单详情详细信息", notes = "根据工单详情Id返回工单详情详细信息")
    @PreAuthorize("@ss.hasPermi('manage:taskDetails:query')")
    @GetMapping(value = "/{detailsId}")
    public R<TaskDetails> getInfo(@ApiParam(value = "工单详情Id", required = true) @PathVariable("detailsId") Long detailsId) {
        return R.ok(taskDetailsService.selectTaskDetailsByDetailsId(detailsId));
    }

    /**
     * 新增工单详情
     */
    @ApiOperation(value = "新增工单详情", notes = "新增一条工单详情记录")
    @PreAuthorize("@ss.hasPermi('manage:taskDetails:add')")
    @Log(title = "工单详情", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Object> add(@ApiParam(value = "工单详情信息", required = true) @RequestBody TaskDetails taskDetails) {
        int result = taskDetailsService.insertTaskDetails(taskDetails);
        return result > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改工单详情
     */
    @ApiOperation(value = "修改工单详情", notes = "修改一条工单详情记录")
    @PreAuthorize("@ss.hasPermi('manage:taskDetails:edit')")
    @Log(title = "工单详情", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Object> edit(@ApiParam(value = "工单详情信息", required = true) @RequestBody TaskDetails taskDetails) {
        int result = taskDetailsService.updateTaskDetails(taskDetails);
        return result > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除工单详情
     */
    @ApiOperation(value = "删除工单详情", notes = "根据工单详情Ids删除工单详情记录")
    @PreAuthorize("@ss.hasPermi('manage:taskDetails:remove')")
    @Log(title = "工单详情", businessType = BusinessType.DELETE)
    @DeleteMapping("/{detailsIds}")
    public R<Object> remove(@ApiParam(value = "工单详情Ids", required = true) @PathVariable Long[] detailsIds) {
        int result = taskDetailsService.deleteTaskDetailsByDetailsIds(detailsIds);
        return result > 0 ? R.ok() : R.fail();
    }
}