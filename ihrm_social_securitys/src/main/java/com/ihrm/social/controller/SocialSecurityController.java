package com.ihrm.social.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entiy.PageResult;
import com.ihrm.common.entiy.Result;
import com.ihrm.common.entiy.ResultCode;
import com.ihrm.common.util.ExcelImportUtil;
import com.ihrm.domain.social_security.*;
import com.ihrm.domain.system.User;
import com.ihrm.social.client.SystemFeignClient;
import com.ihrm.social.service.ArchiveService;
import com.ihrm.social.service.CompanySettingsService;
import com.ihrm.social.service.PaymentItemService;
import com.ihrm.social.service.UserSocialService;
import com.ihrm.social.vo.SearchListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName SocialSecurityController
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年04月29日 10:17
 * @Version 1.0.0
*/
@RestController
@RequestMapping("/social_security")
public class SocialSecurityController extends BaseController{

  @Autowired
  private CompanySettingsService companySettingsService;

  @Autowired
  private UserSocialService userSocialService;

  @Autowired
  private PaymentItemService paymentItemService;
  @Autowired
  private ArchiveService archiveService;

  @Autowired
  private SystemFeignClient systemFeignClient;

  /**
   * 获取企业是否设置社保
   * @return
   */
  @RequestMapping(value = "/settings", method = RequestMethod.GET)
  public Result getSettings() throws Exception {
    CompanySettings companySettings = companySettingsService.findById(companyId);
    return new Result(ResultCode.SUCCESS, companySettings);
  }

  /**
   * 保存企业社保设置
   */
  @RequestMapping(value = "/settings", method = RequestMethod.POST)
  public Result saveSettings(@RequestBody CompanySettings companySettings){
    companySettings.setCompanyId(companyId);
    companySettingsService.save(companySettings);
    return new Result(ResultCode.SUCCESS);
  }

  /**
   * 获取社保列表
   */
  @RequestMapping(value = "/list", method = RequestMethod.POST)
  public Result list(@RequestBody SearchListVo searchListVo, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "1") int pageSize) throws Exception {
//    Page<UserSocialSecurityItem> itemPage = userSocialService.findAll(page, pageSize, companyId, searchListVo.getDepartmentChecks(), searchListVo.getSocialSecurityChecks(), searchListVo.getProvidentFundChecks());
    PageResult pageResult = userSocialService.findAll(page, pageSize, companyId);
//    PageResult pageResult = new PageResult(itemPage.getTotalElements(), itemPage.getContent());
    return new Result(ResultCode.SUCCESS, pageResult);
  }

  /**
   * 根据城市id获取社保缴费项目
   */
  @RequestMapping(value = "/payment_item/{cityId}", method = RequestMethod.GET)
  public Result findPaymentItemByCityId(@PathVariable(value = "cityId") String
                                                cityId) {
    List<CityPaymentItem> cityPaymentItemList =
            paymentItemService.findAllByCityId(cityId);
    return new Result(ResultCode.SUCCESS, cityPaymentItemList);
  }

  /**
   * 获取员工社保信息
   */
  @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
  public Result findById(@PathVariable(value = "userId") String userId) throws
          Exception {
    //获取用户数据
    User user = (User) systemFeignClient.findById(userId).getData();
    //查询用户的社保数据
    UserSocialSecurity userSocialSecurity = userSocialService.findByUserId(userId);
    Map map = new HashMap<>();
    map.put("user",user);
    map.put("userSocialSecurity",userSocialSecurity);
    return new Result(ResultCode.SUCCESS, map);
  }

  /**
   * 保存用户社保信息
   */
  @RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
  public Result save(@RequestBody UserSocialSecurity userSocialSecurity) {
    userSocialService.save(userSocialSecurity);
    return new Result(ResultCode.SUCCESS);
  }

  /**
   * 批量导入员工参保数据
  **/
  @RequestMapping(value = "/import", method = RequestMethod.POST)
  public Result importSocialSecurity(@RequestParam(name = "file") MultipartFile
                                             file) throws Exception {
    List<UserSocialSecurity> list = new
            ExcelImportUtil(UserSocialSecurity.class).readExcel(file.getInputStream(), 1,
            0);
    for (UserSocialSecurity item : list) {
      UserSocialSecurity us =
              userSocialService.findByUserId(item.getUserId(item.getUserId()));
      if (us == null) {
        userSocialService.save(item);
      }
    }
    return new Result(ResultCode.SUCCESS);
  }

  @RequestMapping(value = "/historys/{yearMonth}", method = RequestMethod.GET)
  public Result histories(@PathVariable(value = "yearMonth") String yearMonth, @RequestParam(value =
          "opType") Integer opType) {
    List<ArchiveDetail> reportVoList = new ArrayList<>();
    if (opType == 1) {
      reportVoList.addAll(archiveService.getReports(yearMonth,companyId));
    } else {
      Archive archive = archiveService.findArchive(companyId, yearMonth);
      if (archive != null) {
        reportVoList =
                archiveService.findAllDetailByArchiveId(archive.getId());
      }
    }
    return new Result(ResultCode.SUCCESS, reportVoList);
  }
}
