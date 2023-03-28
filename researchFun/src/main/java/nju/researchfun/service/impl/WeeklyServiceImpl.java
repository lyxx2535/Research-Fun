package nju.researchfun.service.impl;

import cn.afterturn.easypoi.word.WordExportUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import nju.researchfun.entity.user.User;
import nju.researchfun.entity.weekly.Weekly;
import nju.researchfun.exception.BadRequestException;
import nju.researchfun.mapper.WeeklyMapper;
import nju.researchfun.service.ResearchGroupService;
import nju.researchfun.service.UserService;
import nju.researchfun.service.WeeklyService;
import nju.researchfun.vo.weeklywriting.WeeklySaveVO;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class WeeklyServiceImpl implements WeeklyService {

    @Value(value = "${research-group.direction-separator}")
    private String didSep;
    @Value(value = "${file.weeklyWritingTemplate}")
    private String templatePath;
    @Value(value = "${file.weeklyName}")
    private String weeklyName;


    @Autowired
    private WeeklyMapper weeklyMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private ResearchGroupService researchGroupService;


    @Override
    public void save(WeeklySaveVO vo) {
        Weekly weekly = getWeeklyByUidAndGid(vo.getUid(), vo.getGid());
        weekly.setDone(vo.getDone());
        weekly.setTodo(vo.getTodo());
        weekly.setStartDate(vo.getStartDate());
        weekly.setEndDate(vo.getEndDate());

        StringBuilder sb = new StringBuilder();
        List<String> dids = vo.getDocs();
        if (dids != null && dids.size() != 0) {
            for (String did : dids) {
                sb.append(did).append(didSep);
            }
            sb.delete(sb.length() - 2, sb.length());
        }
        weekly.setDid(sb.toString());

        weeklyMapper.updateById(weekly);
    }

    @Override
    public Weekly getWeeklyByUidAndGid(Long uid, Long gid) {//如果没有找到，那就新增一个
        Weekly param = Weekly.builder().uid(uid).gid(gid).build();
        Weekly weekly = weeklyMapper.selectOne(new QueryWrapper<>(param));
        if (weekly == null) {
            weeklyMapper.insert(param);
            return param;
        }
        return weekly;
    }

    @Override
    public WeeklySaveVO get(Long uid, Long gid) {
        Weekly weekly = getWeeklyByUidAndGid(uid, gid);
        WeeklySaveVO vo = WeeklySaveVO.Weekly2WeeklySaveVO(weekly);
        String dids = weekly.getDid();
        if (dids != null && dids.length() != 0) {
            vo.setDocs(Arrays.asList(dids.split(didSep)));
        }

        return vo;
    }


    @Override
    public void download(WeeklySaveVO vo, HttpServletResponse response) {
        Map<String, Object> map = VO2Map(vo);
        try {
            XWPFDocument doc = WordExportUtil.exportWord07(templatePath, map);
            // 设置强制下载不打开
            response.setContentType("application/force-download");
            // 设置文件名
            response.addHeader("Content-Disposition", "attachment;fileName=" + weeklyName);
            OutputStream out = response.getOutputStream();
            doc.write(out);
            out.close();
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private Map<String, Object> VO2Map(WeeklySaveVO vo) {
        Map<String, Object> map = new HashMap<>();

        StringBuilder sb = new StringBuilder();
        List<String> dids = vo.getDocs();
        if (dids != null && dids.size() != 0) {
            for (String doc : dids)
                sb.append(doc).append("\r\n");
        }

        User user = userService.getUserById(vo.getUid());
        String name = user.getTrueName() == null ? user.getUsername() : user.getTrueName();
        String groupName = researchGroupService.getName(vo.getGid());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String startDate = sdf.format(vo.getStartDate());
        String endDate = sdf.format(vo.getEndDate());

        map.put("info", "由研坊——学研小管家生成");
        map.put("doc", sb.toString());
        map.put("username", name);
        map.put("group", groupName);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        map.put("done", vo.getDone());
        map.put("todo", vo.getTodo());

        return map;
    }
}
