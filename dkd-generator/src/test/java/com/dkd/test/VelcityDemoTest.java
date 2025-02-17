package com.dkd.test;

import com.dkd.generator.util.VelocityInitializer;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class VelcityDemoTest {
    public static void main(String[] args) throws IOException {
        // 1.初始化模板引擎
        VelocityInitializer.initVelocity();

        // 2.准备模板数据模型
        VelocityContext vc = new VelocityContext();
        vc.put("message", "加油少年！！");

        // 创建区域对象
        Region region1 = new Region(1L, "北京北五环");
        Region region2 = new Region(2L, "上海南四环");
        List<Region> list = List.of(region1, region2);
        vc.put("regionList", list);

        // 3.读取模版
        Template template = Velocity.getTemplate("vm/index.html.vm", "UTF-8");

        // 4.渲染模板（合并输出）
        FileWriter fw = new FileWriter("/Users/zetian/Downloads/index.html");
        template.merge(vc, fw);

        // 5.关闭流
        fw.close();
    }
}
