# Knife4j 在若依中的使用

在若依项目中，使用 swagger 前端 UI 的增强解决方案 knife4j，它相比 swagger 有以下优势：

- 友好界面，离线文档，接口排序，安全控制，在线调试，文档清晰，注解增强，容易上手。

## 一、Knife4J 坐标引入

dkd-common/pom.xml

```xml
<!-- knife4j -->
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-spring-boot-starter</artifactId>
    <version>3.0.3</version>
</dependency>
```

重启项目。

## 二、前端改造

在前端工程中，修改跳转访问地址。

src/views/tool/swagger/index.vue

```vue
<template>
  <i-frame v-model:src="url"></i-frame>
</template>

<script setup>
import iFrame from '@/components/iFrame'

const url = ref(import.meta.env.VITE_APP_BASE_API + '/doc.html')
</script>
```

登录系统，访问菜单系统工具 -> 系统接口。

## 三、TaskDetailsController 类生成接口文档

TaskDetailsController.java 控制器类

> 注意：若依框架的 `AjaxResult` 由于继承自 `HashMap`，导致与 Swagger 和 knife4j 不兼容；
>
> 观察 dkd-admin/src/main/java/com/dkd/web/controller/tool/TestController.java 中的做法，可知：
>
> 将返回值类型替换为 `R` 类，可解决 Swagger 解析问题，减少整体改动量。

dkd-manage/src/main/java/com/dkd/manage/controller/TaskDetailsController.java

```java
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
```

在接口文档中，为每个字段添加说明。

在 TaskDetails.java 实体类中，使用 `@ApiModelProperty` 注解。

dkd-manage/src/main/java/com/dkd/manage/domain/TaskDetails.java

```java
package com.dkd.manage.domain;

import com.dkd.common.annotation.Excel;
import com.dkd.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 工单详情对象 task_details
 *
 * @author zetian
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDetails extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "工单详情Id")
    private Long detailsId;

    @Excel(name = "工单Id")
    @ApiModelProperty(value = "工单Id")
    private Long taskId;

    @Excel(name = "货道编号")
    @ApiModelProperty(value = "货道编号")
    private String channelCode;

    @Excel(name = "补货期望容量")
    @ApiModelProperty(value = "补货期望容量")
    private Long expectCapacity;

    @Excel(name = "商品Id")
    @ApiModelProperty(value = "商品Id")
    private Long skuId;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String skuName;

    @Excel(name = "商品图片")
    @ApiModelProperty(value = "商品图片")
    private String skuImage;
}
```

## 四、接口文档相关配置改造

### 4.1.Swagger 配置文件改造

修改标题为：`标题：帝可得管理系统_接口文档`

dkd-admin/src/main/java/com/dkd/web/core/config/SwaggerConfig.java

```java
/**
 * 添加摘要信息
 */
private ApiInfo apiInfo() {
    // 用ApiInfoBuilder进行定制
    return new ApiInfoBuilder()
            // 设置标题
            .title("标题：帝可得管理系统_接口文档")
            // 描述
            .description("描述：用于管理集团旗下公司的人员信息,具体包括XXX,XXX模块...")
            // 作者信息
            .contact(new Contact(ruoyiConfig.getName(), null, null))
            // 版本
            .version("版本号:" + ruoyiConfig.getVersion())
            .build();
}
```

修改作者为：`zetian`

dkd-admin/src/main/resources/application.yml

```yaml
# 项目相关配置
ruoyi:
  # 名称
  name: Zetian
```

> 集成第三方支付框架推荐：[elegent-pay](https://gitee.com/myelegent/elegent-pay)
