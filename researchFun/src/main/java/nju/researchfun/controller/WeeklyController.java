package nju.researchfun.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import nju.researchfun.service.WeeklyService;
import nju.researchfun.vo.response.ResponseVO;
import nju.researchfun.vo.weeklywriting.WeeklySaveVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/weekly")
@Api(tags = "周报相关接口")
public class WeeklyController {

    @Autowired
    private WeeklyService weeklyService;


    @PostMapping("/save")
    @ApiOperation(value = "保存周报接口", notes = "成功后返回success")
    public ResponseVO<String> save(@RequestBody WeeklySaveVO vo) {
        weeklyService.save(vo);
        return new ResponseVO<>("success");
    }

    @GetMapping("/get")
    @ApiOperation(value = "获取该用户已保存的周报接口", notes = "成功后返回该上次保存的周报的内容，如果该用户从未写过周报，则自动创建一个空的")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户id", defaultValue = "6", required = true),
            @ApiImplicitParam(name = "gid", value = "研究组id", defaultValue = "3", required = true)
    })
    public ResponseVO<WeeklySaveVO> get(@RequestParam Long uid, @RequestParam Long gid) {
        return new ResponseVO<>(weeklyService.get(uid, gid));
    }

    @GetMapping("/download")
    @ApiOperation(value = "下载生成好的周报", notes = "调用后会强制下载，前端不需要做任何操作")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户id", defaultValue = "1", required = true),
            @ApiImplicitParam(name = "gid", value = "研究组id", defaultValue = "1", required = true),
            @ApiImplicitParam(name = "startDate", value = "开始时间", defaultValue = "2021-02-01", required = true),
            @ApiImplicitParam(name = "endDate", value = "结束时间", defaultValue = "2021-02-01", required = true),
            @ApiImplicitParam(name = "docs", value = "阅读过的文献名", defaultValue = "[西瓜书, 摸鱼导论, 为什么今天什么都没做]", required = true),
            @ApiImplicitParam(name = "done", value = "已完成", defaultValue = "写完了周报数据结构", required = true),
            @ApiImplicitParam(name = "todo", value = "待完成", defaultValue = "模式识别作业还没做", required = true)
    })
    public void download(@RequestParam Long uid, @RequestParam Long gid,
                         @RequestParam String startDate, @RequestParam String endDate,
                         @RequestParam String docs,
                         @RequestParam String done, @RequestParam String todo, HttpServletResponse response) {

        WeeklySaveVO vo = WeeklySaveVO.param2VO(uid, gid, startDate, endDate, docs, done, todo);
        weeklyService.download(vo, response);
    }

}
