package com.dkd.manage.controller;

import com.dkd.common.annotation.Log;
import com.dkd.common.core.controller.BaseController;
import com.dkd.common.core.domain.AjaxResult;
import com.dkd.common.core.page.TableDataInfo;
import com.dkd.common.enums.BusinessType;
import com.dkd.common.utils.poi.ExcelUtil;
import com.dkd.manage.domain.SkuClass;
import com.dkd.manage.service.ISkuClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 商品类型Controller
 *
 * @author zetian
 * @date 2024-12-15
 */
@RestController
@RequestMapping("/manage/skuClass")
public class SkuClassController extends BaseController {
    private final ISkuClassService skuClassService;

    @Autowired
    public SkuClassController(ISkuClassService skuClassService) {
        this.skuClassService = skuClassService;
    }

    /**
     * 查询商品类型列表
     */
    @PreAuthorize("@ss.hasPermi('manage:skuClass:list')")
    @GetMapping("/list")
    public TableDataInfo list(SkuClass skuClass) {
        startPage();
        List<SkuClass> list = skuClassService.selectSkuClassList(skuClass);
        return getDataTable(list);
    }

    /**
     * 导出商品类型列表
     */
    @PreAuthorize("@ss.hasPermi('manage:skuClass:export')")
    @Log(title = "商品类型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SkuClass skuClass) {
        List<SkuClass> list = skuClassService.selectSkuClassList(skuClass);
        ExcelUtil<SkuClass> util = new ExcelUtil<SkuClass>(SkuClass.class);
        util.exportExcel(response, list, "商品类型数据");
    }

    /**
     * 获取商品类型详细信息
     */
    @PreAuthorize("@ss.hasPermi('manage:skuClass:query')")
    @GetMapping(value = "/{classId}")
    public AjaxResult getInfo(@PathVariable("classId") Long classId) {
        return success(skuClassService.selectSkuClassByClassId(classId));
    }

    /**
     * 新增商品类型
     */
    @PreAuthorize("@ss.hasPermi('manage:skuClass:add')")
    @Log(title = "商品类型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SkuClass skuClass) {
        return toAjax(skuClassService.insertSkuClass(skuClass));
    }

    /**
     * 修改商品类型
     */
    @PreAuthorize("@ss.hasPermi('manage:skuClass:edit')")
    @Log(title = "商品类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SkuClass skuClass) {
        return toAjax(skuClassService.updateSkuClass(skuClass));
    }

    /**
     * 删除商品类型
     */
    @PreAuthorize("@ss.hasPermi('manage:skuClass:remove')")
    @Log(title = "商品类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{classIds}")
    public AjaxResult remove(@PathVariable Long[] classIds) {
        return toAjax(skuClassService.deleteSkuClassByClassIds(classIds));
    }
}
