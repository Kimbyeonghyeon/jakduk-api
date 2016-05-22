package com.jakduk.restcontroller;


import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.jakduk.common.CommonConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import com.jakduk.model.db.Encyclopedia;
import com.jakduk.model.db.HomeDescription;
import com.jakduk.service.AdminService;
import com.jakduk.service.CommonService;
import com.jakduk.vo.HomeDescriptionRequest;


/**
 * Created by pyohwan on 16. 5. 8.
 */

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    @Resource
    private LocaleResolver localeResolver;

    @Autowired
    private CommonService commonService;

    @Autowired
    private AdminService adminService;

    // 알림판 목록.
    @RequestMapping(value = "/home/descriptions", method = RequestMethod.GET)
    public Map<String, Object> getHomeDescriptions() {

        List<HomeDescription> homeDescriptions = adminService.findHomeDescriptions();

        Map<String, Object> response = new HashMap<>();

        response.put("homeDescriptions", homeDescriptions);

        return response;
    }

    // 알림판 하나.
    @RequestMapping(value = "/home/description/{id}", method = RequestMethod.GET)
    public Map<String, Object> gethomeDescription(@PathVariable String id,
                                                  HttpServletRequest request) {

        HomeDescription homeDescription = adminService.findHomeDescriptionById(id);

        Locale locale = localeResolver.resolveLocale(request);

        if (Objects.isNull(homeDescription)) {
            throw new IllegalArgumentException(commonService.getResourceBundleMessage(locale, "messages.common", "common.exception.invalid.parameter"));
        }

        Map<String, Object> response = new HashMap<>();

        response.put("homeDescription", homeDescription);

        return response;
    }

    // 새 알림판 저장.
    @RequestMapping(value = "/home/description", method = RequestMethod.POST)
    public Map<String, Object> addHomeDescription(@RequestBody HomeDescriptionRequest homeDescriptionRequest) {

        if (Objects.isNull(homeDescriptionRequest.getDesc()) || homeDescriptionRequest.getDesc().isEmpty())
            throw new IllegalArgumentException("desc는 필수값입니다.");

        if (Objects.isNull(homeDescriptionRequest.getPriority()))
            throw new IllegalArgumentException("priority는 필수값입니다.");

        HomeDescription homeDescription = HomeDescription.builder()
                .desc(homeDescriptionRequest.getDesc())
                .priority(homeDescriptionRequest.getPriority())
                .build();

        adminService.saveHomeDescription(homeDescription);

        Map<String, Object> response = new HashMap();

        response.put("homeDescription", homeDescription);

        return response;
    }

    // 알림판 편집.
    @RequestMapping(value = "/home/description/{id}", method = RequestMethod.PUT)
    public Map<String, Object> EditHomeDescription(@PathVariable String id,
                                                   @RequestBody HomeDescriptionRequest homeDescriptionRequest) {

        if (Objects.isNull(homeDescriptionRequest.getDesc()) || homeDescriptionRequest.getDesc().isEmpty())
            throw new IllegalArgumentException("desc는 필수값입니다.");

        if (Objects.isNull(homeDescriptionRequest.getPriority()))
            throw new IllegalArgumentException("priority는 필수값입니다.");

        HomeDescription existHomeDescription = adminService.findHomeDescriptionById(id);

        if (Objects.isNull(existHomeDescription))
            throw new IllegalArgumentException("id가 " + id + "에 해당하는 알림판이 존재하지 않습니다.");

        HomeDescription homeDescription = HomeDescription.builder()
                .id(id)
                .desc(homeDescriptionRequest.getDesc())
                .priority(homeDescriptionRequest.getPriority())
                .build();

        adminService.saveHomeDescription(homeDescription);

        Map<String, Object> response = new HashMap<>();

        response.put("homeDescription", homeDescription);

        return response;
    }

    // 알림판 지움.
    @RequestMapping(value = "/home/description/{id}", method = RequestMethod.DELETE)
    public Map<String, Object> DeleteHomeDescription(@PathVariable String id) {

        HomeDescription existHomeDescription = adminService.findHomeDescriptionById(id);

        if (Objects.isNull(existHomeDescription))
            throw new IllegalArgumentException("id가 " + id + "에 해당하는 알림판이 존재하지 않습니다.");

        adminService.deleteHomeDescriptionById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("result", true);

        return response;
    }

    // 백과사전 목록.
    @RequestMapping(value = "/encyclopedias", method = RequestMethod.GET)
    public Map<String, Object> getEncyclopedias() {

        List<Encyclopedia> encyclopedias = adminService.findEncyclopedias();

        Map<String, Object> response = new HashMap<>();

        response.put("encyclopedias", encyclopedias);

        return response;
    }

    // 새 백과사전 저장.
    @RequestMapping(value = "/encyclopedia", method = RequestMethod.POST)
    public Map<String, Object> addEncyclopedia(@RequestBody Encyclopedia encyclopedia) {

        // 신규로 만들기때문에 null로 설정.
        encyclopedia.setId(null);

        if (encyclopedia.getLanguage().equals(Locale.ENGLISH.getLanguage())) {
            encyclopedia.setSeq(commonService.getNextSequence(CommonConst.ENCYCLOPEDIA_EN));
        } else if (encyclopedia.getLanguage().equals(Locale.KOREAN.getLanguage())) {
            encyclopedia.setSeq(commonService.getNextSequence(CommonConst.ENCYCLOPEDIA_KO));
        }

        adminService.saveEncyclopedia(encyclopedia);

        Map<String, Object> response = new HashMap<>();
        response.put("encyclopedia", encyclopedia);

        return response;
    }

    @RequestMapping(value = "/board/category/init", method = RequestMethod.POST)
    public Map<String, Object> initBoardCategory() {
        return adminService.initBoardCategory();
    }

    @RequestMapping(value = "/search/index/init", method = RequestMethod.POST)
    public Map<String, Object> initSearchIndex() {
        return adminService.initSearchIndex();
    }

    @RequestMapping(value = "/search/type/init", method = RequestMethod.POST)
    public Map<String, Object> initSearchType() {
        return adminService.initSearchType();
    }

    @RequestMapping(value = "/search/data/init", method = RequestMethod.POST)
    public Map<String, Object> initSearchData() {
        return adminService.initSearchData();
    }

}
