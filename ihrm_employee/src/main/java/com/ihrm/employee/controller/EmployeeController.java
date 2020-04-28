package com.ihrm.employee.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entiy.PageResult;
import com.ihrm.common.entiy.Result;
import com.ihrm.common.entiy.ResultCode;
import com.ihrm.common.util.BeanMapUtils;
import com.ihrm.common.util.DownloadUtils;
import com.ihrm.common.util.ExcelExportUtil;
import com.ihrm.common.util.ExcelImportUtil;
import com.ihrm.domain.employee.*;
import com.ihrm.domain.employee.response.EmployeeReportResult;
import com.ihrm.domain.system.User;
import com.ihrm.employee.service.*;
import net.sf.jasperreports.engine.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import java.io.*;
import java.sql.RowId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping("/employees")
@CrossOrigin //解决跨域问题
public class EmployeeController extends BaseController {
    @Autowired
    private UserCompanyPersonalService userCompanyPersonalService;
    @Autowired
    private UserCompanyJobsService userCompanyJobsService;
    @Autowired
    private ResignationService resignationService;
    @Autowired
    private TransferPositionService transferPositionService;
    @Autowired
    private PositiveService positiveService;
    @Autowired
    private ArchiveService archiveService;


    /**
     * 员工个人信息保存
     */
    @RequestMapping(value = "/{id}/personalInfo", method = RequestMethod.PUT)
    public Result savePersonalInfo(@PathVariable(name = "id") String uid, @RequestBody Map map) throws Exception {
        UserCompanyPersonal sourceInfo = BeanMapUtils.mapToBean(map, UserCompanyPersonal.class);
        if (sourceInfo == null) {
            sourceInfo = new UserCompanyPersonal();
        }
        sourceInfo.setUserId(uid);
        sourceInfo.setCompanyId(super.companyId);
        userCompanyPersonalService.save(sourceInfo);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 员工个人信息读取
     */
    @RequestMapping(value = "/{id}/personalInfo", method = RequestMethod.GET)
    public Result findPersonalInfo(@PathVariable(name = "id") String uid) throws Exception {
        UserCompanyPersonal info = userCompanyPersonalService.findById(uid);
        if(info == null) {
            info = new UserCompanyPersonal();
            info.setUserId(uid);
        }
        return new Result(ResultCode.SUCCESS,info);
    }

    /**
     * 员工岗位信息保存
     */
    @RequestMapping(value = "/{id}/jobs", method = RequestMethod.PUT)
    public Result saveJobsInfo(@PathVariable(name = "id") String uid, @RequestBody UserCompanyJobs sourceInfo) throws Exception {
        //更新员工岗位信息
        if (sourceInfo == null) {
            sourceInfo = new UserCompanyJobs();
            sourceInfo.setUserId(uid);
            sourceInfo.setCompanyId(super.companyId);
        }
        userCompanyJobsService.save(sourceInfo);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 员工岗位信息读取
     */
    @RequestMapping(value = "/{id}/jobs", method = RequestMethod.GET)
    public Result findJobsInfo(@PathVariable(name = "id") String uid) throws Exception {
        UserCompanyJobs info = userCompanyJobsService.findById(super.userId);
        if(info == null) {
            info = new UserCompanyJobs();
            info.setUserId(uid);
            info.setCompanyId(companyId);
        }
        return new Result(ResultCode.SUCCESS,info);
    }

    /**
     * 离职表单保存
     */
    @RequestMapping(value = "/{id}/leave", method = RequestMethod.PUT)
    public Result saveLeave(@PathVariable(name = "id") String uid, @RequestBody EmployeeResignation resignation) throws Exception {
        resignation.setUserId(uid);
        resignationService.save(resignation);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 离职表单读取
     */
    @RequestMapping(value = "/{id}/leave", method = RequestMethod.GET)
    public Result findLeave(@PathVariable(name = "id") String uid) throws Exception {
        EmployeeResignation resignation = resignationService.findById(uid);
        if(resignation == null) {
            resignation = new EmployeeResignation();
            resignation.setUserId(uid);
        }
        return new Result(ResultCode.SUCCESS,resignation);
    }

    /**
     * 导入员工
     */
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public Result importDatas(@RequestParam(name = "file") MultipartFile file) throws Exception {

        List<User> list = new ExcelImportUtil<User>(User.class).readExcel(file.getInputStream(), 1, 1);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 调岗表单保存
     */
    @RequestMapping(value = "/{id}/transferPosition", method = RequestMethod.PUT)
    public Result saveTransferPosition(@PathVariable(name = "id") String uid, @RequestBody EmployeeTransferPosition transferPosition) throws Exception {
        transferPosition.setUserId(uid);
        transferPositionService.save(transferPosition);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 调岗表单读取
     */
    @RequestMapping(value = "/{id}/transferPosition", method = RequestMethod.GET)
    public Result findTransferPosition(@PathVariable(name = "id") String uid) throws Exception {
        UserCompanyJobs jobsInfo = userCompanyJobsService.findById(uid);
        if(jobsInfo == null) {
            jobsInfo = new UserCompanyJobs();
            jobsInfo.setUserId(uid);
        }
        return new Result(ResultCode.SUCCESS,jobsInfo);
    }

    /**
     * 转正表单保存
     */
    @RequestMapping(value = "/{id}/positive", method = RequestMethod.PUT)
    public Result savePositive(@PathVariable(name = "id") String uid, @RequestBody EmployeePositive positive) throws Exception {
        positiveService.save(positive);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 转正表单读取
     */
    @RequestMapping(value = "/{id}/positive", method = RequestMethod.GET)
    public Result findPositive(@PathVariable(name = "id") String uid) throws Exception {
        EmployeePositive positive = positiveService.findById(uid);
        if(positive == null) {
            positive = new EmployeePositive();
            positive.setUserId(uid);
        }
        return new Result(ResultCode.SUCCESS,positive);
    }

    /**
     * 历史归档详情列表
     */
    @RequestMapping(value = "/archives/{year}", method = RequestMethod.GET)
    public Result archives(@PathVariable(name = "month") String year, @RequestParam(name = "type") Integer type,@RequestParam(name = "month") String month) throws Exception {
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 归档更新
     */
    @RequestMapping(value = "/archives/{month}", method = RequestMethod.PUT)
    public Result saveArchives(@PathVariable(name = "month") String month) throws Exception {
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 历史归档列表
     */
    @RequestMapping(value = "/archives", method = RequestMethod.GET)
    public Result findArchives(@RequestParam(name = "pagesize") Integer pagesize, @RequestParam(name = "page") Integer page, @RequestParam(name = "year") String year) throws Exception {
        Map map = new HashMap();
        map.put("year",year);
        map.put("companyId",companyId);
        Page<EmployeeArchive> searchPage = archiveService.findSearch(map, page, pagesize);
        PageResult<EmployeeArchive> pr = new PageResult(searchPage.getTotalElements(),searchPage.getContent());
        return new Result(ResultCode.SUCCESS,pr);
    }

//    /**
//     * 模拟海量数据 导出
//    **/
//    @GetMapping("/export/{month}")
//    public void export(
//            @PathVariable(name = "month") String month
////            @RequestParam(name = "type") Integer typ,
////            @RequestParam(name = "month") String month
//    ) throws IOException {
//        //获取数据
//        List<EmployeeReportResult> list =  userCompanyPersonalService.findByRepost(companyId,month);
//
//        //构造excel
//        //创建工作簿
//        Workbook workbook = new XSSFWorkbook();
//        //创建sheet
//        Sheet sheet = workbook.createSheet();
//
//        //构造标题
//        String[] titles = {"编号", "姓名", "手机","最高学历", "国家地区", "护照号", "籍贯", "生日", "属相","入职时间","离职类型","离职原因","离职时间"};
//        //写入标题
//        //创建行
//        Row row = sheet.createRow(0);
//        int cellNum = 0;
//        for (String title : titles) {
//            //创建单元格
//            Cell cell = row.createCell(cellNum);
//            cell.setCellValue(title);
//            cellNum ++;
//        }
//
//        //写入内容
//        AtomicInteger headersAi = new AtomicInteger();
//
//        for(int i = 0; i<1000000; i ++){
//            Cell cell = null;
//            for (EmployeeReportResult result : list) {
//                //创建单元格
//                Row dataRow = sheet.createRow(headersAi.getAndIncrement());
//                //编号
//                cell = dataRow.createCell(0);
//                cell.setCellValue(result.getUserId());
//                //姓名
//                cell = dataRow.createCell(1);
//                cell.setCellValue(result.getUsername());
//                //手机
//                cell = dataRow.createCell(2);
//                cell.setCellValue(result.getMobile());
//                //最高学历
//                cell = dataRow.createCell(3);
//                cell.setCellValue(result.getTheHighestDegreeOfEducation());
//                //国家地区
//                cell = dataRow.createCell(4);
//                cell.setCellValue(result.getNationalArea());
//                //护照号
//                cell = dataRow.createCell(5);
//                cell.setCellValue(result.getPassportNo());
//                //籍贯
//                cell = dataRow.createCell(6);
//                cell.setCellValue(result.getNativePlace());
//                //生日
//                cell = dataRow.createCell(7);
//                cell.setCellValue(result.getBirthday());
//                //属相
//                cell = dataRow.createCell(8);
//                cell.setCellValue(result.getZodiac());
//                //入职时间
//                cell = dataRow.createCell(9);
//                cell.setCellValue(result.getTimeOfEntry());
//                //离职类型
//                cell = dataRow.createCell(10);
//                cell.setCellValue(result.getTypeOfTurnover());
//                //离职原因
//                cell = dataRow.createCell(11);
//                cell.setCellValue(result.getReasonsForLeaving());
//                //离职时间
//                cell = dataRow.createCell(12);
//                cell.setCellValue(result.getResignationTime());
//            }
//
//            //通过输出流进行文件下载
////        ServletOutputStream out = response.getOutputStream();
////        //指定格式
////        response.setContentType("application/vnd.ms-excel");
////        response.setHeader("content-Disposition", "attachment;filename=report.xlsx");
////
////        workbook.write(out);
////
////        out.flush();
////        out.close();
//
//            //完成下载
//            ByteArrayOutputStream os = new ByteArrayOutputStream();
//            workbook.write(os);
//
//            new DownloadUtils().download(os,response,month+"_人事报表.xlsx");
//        }
//
//        System.out.println("循环执行完成");
//
//    }


    /**
     * 采用模板导出poi
    **/
//    @GetMapping("/export/{month}")
//    public void export(@PathVariable(name = "month") String month) throws Exception {
//        //获取数据
//        List<EmployeeReportResult> list =  userCompanyPersonalService.findByRepost(companyId,month);
//
//        //加载模板
//        Resource resource = new ClassPathResource("excel-template/hr-demo.xlsx");
//        FileInputStream fis = new FileInputStream(resource.getFile());
//
////        //根据文件流加载工作簿
////        Workbook workbook = new XSSFWorkbook(fis);
////        //读取工作表
////        Sheet sheet = workbook.getSheetAt(0);
////
////        //抽取公共样式
////        Row styleRow = sheet.getRow(2);
////
////        CellStyle[] cellStyles = new CellStyle[styleRow.getLastCellNum()];
////
////        for(int i = 0; i<styleRow.getLastCellNum();i++){
////            Cell cell = styleRow.getCell(i);
////            cellStyles[i] = cell.getCellStyle();
////        }
//
//        //导出内容
//        new ExcelExportUtil(EmployeeReportResult.class,2,2).export(response,fis,list,month+"_人事报表.xlsx");
//
//    }

    /**
     * 测试下载
     */
//    @GetMapping("/download")
//    public Result downloadTest() throws Exception {
//
//        String fileName = "download.xlsx";
////        response.setContentType("application/force-download");
////        response.addHeader("Content-Disposition","attachment;fileName="+fileName);
//
//        response.setContentType("application/octet-stream");
//        response.setHeader("content-disposition", "attachment;filename=" + new String(fileName.getBytes("ISO8859-1")));
//
//        FileInputStream fileInputStream = new FileInputStream("D:\\test.xlsx");
//        ServletOutputStream outputStream = response.getOutputStream();
//
//        response.setContentType("application/octet-stream");
//        response.setHeader("content-disposition", "attachment;filename=" + new String(fileName.getBytes("ISO8859-1")));
//
//        byte[] buffer = new byte[1024];
//
//
//        try {
//            int read = fileInputStream.read(buffer);
//            while (read != -1) {
//                outputStream.write(buffer,0,read);
//                read = fileInputStream.read(buffer);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            fileInputStream.close();
//            outputStream.close();
//        }
//
//
//
//        return Result.SUCCESS();
//    }
    /**
     * 测试下载
     */
    @GetMapping("/download")
    public Result downloadTest() throws Exception {
        String month = "2020-1";

        //获取数据
        List<EmployeeReportResult> list =  userCompanyPersonalService.findByRepost(companyId,month);

        //构造excel
        //创建工作簿
//        Workbook workbook = new XSSFWorkbook();
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        //创建sheet
        Sheet sheet = workbook.createSheet();

        //构造标题
        String[] titles = {"编号", "姓名", "手机","最高学历", "国家地区", "护照号", "籍贯", "生日", "属相","入职时间","离职类型","离职原因","离职时间"};
        //写入标题
        //创建行
        Row row = sheet.createRow(0);
        int cellNum = 0;
        for (String title : titles) {
            //创建单元格
            Cell cell = row.createCell(cellNum);
            cell.setCellValue(title);
            cellNum ++;
        }

        //写入内容
        AtomicInteger headersAi = new AtomicInteger();

        for(int i = 0; i<100000; i ++){
            Cell cell = null;
            for (EmployeeReportResult result : list) {
                //创建单元格
                Row dataRow = sheet.createRow(headersAi.getAndIncrement());
                //编号
                cell = dataRow.createCell(0);
                cell.setCellValue(result.getUserId());
                //姓名
                cell = dataRow.createCell(1);
                cell.setCellValue(result.getUsername());
                //手机
                cell = dataRow.createCell(2);
                cell.setCellValue(result.getMobile());
                //最高学历
                cell = dataRow.createCell(3);
                cell.setCellValue(result.getTheHighestDegreeOfEducation());
                //国家地区
                cell = dataRow.createCell(4);
                cell.setCellValue(result.getNationalArea());
                //护照号
                cell = dataRow.createCell(5);
                cell.setCellValue(result.getPassportNo());
                //籍贯
                cell = dataRow.createCell(6);
                cell.setCellValue(result.getNativePlace());
                //生日
                cell = dataRow.createCell(7);
                cell.setCellValue(result.getBirthday());
                //属相
                cell = dataRow.createCell(8);
                cell.setCellValue(result.getZodiac());
                //入职时间
                cell = dataRow.createCell(9);
                cell.setCellValue(result.getTimeOfEntry());
                //离职类型
                cell = dataRow.createCell(10);
                cell.setCellValue(result.getTypeOfTurnover());
                //离职原因
                cell = dataRow.createCell(11);
                cell.setCellValue(result.getReasonsForLeaving());
                //离职时间
                cell = dataRow.createCell(12);
                cell.setCellValue(result.getResignationTime());
            }

        }

        //完成下载
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        workbook.write(os);

        new DownloadUtils().download(os,response,month+"_人事报表.xlsx");

        System.out.println("循环执行完成");

        return Result.SUCCESS();
    }

    /**
     * 解决 海量数据 导出 出现的问题
    **/
    @GetMapping("/export/{month}")
    public void export(
            @PathVariable(name = "month") String month
    ) throws IOException {
        //获取数据
        List<EmployeeReportResult> list =  userCompanyPersonalService.findByRepost(companyId,month);

        //构造excel
        //创建工作簿
//        Workbook workbook = new XSSFWorkbook();
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        //创建sheet
        Sheet sheet = workbook.createSheet();

        //构造标题
        String[] titles = {"编号", "姓名", "手机","最高学历", "国家地区", "护照号", "籍贯", "生日", "属相","入职时间","离职类型","离职原因","离职时间"};
        //写入标题
        //创建行
        Row row = sheet.createRow(0);
        int cellNum = 0;
        for (String title : titles) {
            //创建单元格
            Cell cell = row.createCell(cellNum);
            cell.setCellValue(title);
            cellNum ++;
        }

        //写入内容
        AtomicInteger headersAi = new AtomicInteger();

        for(int i = 0; i<500000; i ++){
            Cell cell = null;
            for (EmployeeReportResult result : list) {
                //创建单元格
                Row dataRow = sheet.createRow(headersAi.getAndIncrement());
                //编号
                cell = dataRow.createCell(0);
                cell.setCellValue(result.getUserId());
                //姓名
                cell = dataRow.createCell(1);
                cell.setCellValue(result.getUsername());
                //手机
                cell = dataRow.createCell(2);
                cell.setCellValue(result.getMobile());
                //最高学历
                cell = dataRow.createCell(3);
                cell.setCellValue(result.getTheHighestDegreeOfEducation());
                //国家地区
                cell = dataRow.createCell(4);
                cell.setCellValue(result.getNationalArea());
                //护照号
                cell = dataRow.createCell(5);
                cell.setCellValue(result.getPassportNo());
                //籍贯
                cell = dataRow.createCell(6);
                cell.setCellValue(result.getNativePlace());
                //生日
                cell = dataRow.createCell(7);
                cell.setCellValue(result.getBirthday());
                //属相
                cell = dataRow.createCell(8);
                cell.setCellValue(result.getZodiac());
                //入职时间
                cell = dataRow.createCell(9);
                cell.setCellValue(result.getTimeOfEntry());
                //离职类型
                cell = dataRow.createCell(10);
                cell.setCellValue(result.getTypeOfTurnover());
                //离职原因
                cell = dataRow.createCell(11);
                cell.setCellValue(result.getReasonsForLeaving());
                //离职时间
                cell = dataRow.createCell(12);
                cell.setCellValue(result.getResignationTime());
            }

            //通过输出流进行文件下载
//        ServletOutputStream out = response.getOutputStream();
//        //指定格式
//        response.setContentType("application/vnd.ms-excel");
//        response.setHeader("content-Disposition", "attachment;filename=report.xlsx");
//
//        workbook.write(out);
//
//        out.flush();
//        out.close();

        }

        //完成下载
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        workbook.write(os);

        new DownloadUtils().download(os,response,month+"_人事报表.xlsx");

        System.out.println("循环执行完成");

    }

    /**
     * 打印员工pdf报表x
     */
    @RequestMapping(value="/{id}/pdf",method = RequestMethod.GET)
    public void pdf(@PathVariable String id) throws IOException {
        //1.引入jasper文件
        Resource resource = new ClassPathResource("templates/profile.jasper");
        FileInputStream fis = new FileInputStream(resource.getFile());

        //2.构造数据
        //a.用户详情数据
        UserCompanyPersonal personal = userCompanyPersonalService.findById(id);
        //b.用户岗位信息数据
        UserCompanyJobs jobs = userCompanyJobsService.findById(id);
        //c.用户头像        域名 / id
        String staffPhoto = "http://q9fg6lii3.bkt.clouddn.com/"+id;

        System.out.println(staffPhoto);

        //3.填充pdf模板数据，并输出pdf
        Map params = new HashMap();

        Map<String, Object> map1 = BeanMapUtils.beanToMap(personal);
        Map<String, Object> map2 = BeanMapUtils.beanToMap(jobs);

        params.putAll(map1);
        params.putAll(map2);
        params.put("staffPhoto",staffPhoto);

        ServletOutputStream os = response.getOutputStream();
        try {
            JasperPrint print = JasperFillManager.fillReport(fis, params,new JREmptyDataSource());
            JasperExportManager.exportReportToPdfStream(print,os);
        } catch (JRException e) {
            e.printStackTrace();
        }finally {
            os.flush();
        }
    }


}
