package com.ihrm.system.controller;

import com.ihrm.common.entiy.Result;
import com.ihrm.common.entiy.ResultCode;
import com.ihrm.domain.system.Permission;
import com.ihrm.domain.system.User;
import com.ihrm.system.service.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//1.解决跨域
@CrossOrigin
//2.声明restContoller
@RestController
//3.设置父路径
@RequestMapping(value="/sys")
@Api(tags = "权限管理Api", description= "权限的 增删改查")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @ApiOperation("保存")
    @PostMapping("/permission")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map",value = "权限数据",required = true,dataType = "map",paramType = "body")
    })
    public Result save(@RequestBody Map<String,Object> map) throws Exception {
        permissionService.save(map);
        return new Result(ResultCode.SUCCESS);
    }

    @ApiOperation("修改")
    @PutMapping("/permission/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "权限id",required = true,dataType = "string",paramType = "path"),
            @ApiImplicitParam(name = "map",value = "权限数据",required = true,dataType = "map",paramType = "body")
    })
    public Result update(@PathVariable(value = "id") String id, @RequestBody Map<String,Object> map) throws Exception {
        //构造id
        map.put("id",id);
        permissionService.update(map);
        return new Result(ResultCode.SUCCESS);
    }

    @ApiOperation("查询列表")
    @GetMapping("/permission")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map",value = "权限数据",required = true,dataType = "map",paramType = "param")
    })
    public Result findAll(@RequestParam Map map) {
        List<Permission> list =  permissionService.findAll(map);
        return new Result(ResultCode.SUCCESS,list);
    }

    @ApiOperation("根据ID查询")
    @GetMapping("/permission/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "权限id",required = true,dataType = "string",paramType = "path")
    })
    public Result findById(@PathVariable(value = "id") String id) throws Exception {
        Map map = permissionService.findById(id);
        return new Result(ResultCode.SUCCESS,map);
    }



    @ApiOperation("根据id删除")
    @DeleteMapping("/permission/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "权限id",required = true,dataType = "string",paramType = "path")
    })
    public Result delete(@PathVariable(value = "id") String id) throws Exception {
        permissionService.deleteById(id);
        return new Result(ResultCode.SUCCESS);
    }
}
