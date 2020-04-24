package com.ihrm.system.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entiy.PageResult;
import com.ihrm.common.entiy.Result;
import com.ihrm.common.entiy.ResultCode;
import com.ihrm.domain.system.Role;
import com.ihrm.domain.system.response.RoleResult;
import com.ihrm.system.service.RoleService;
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
@RestController
@RequestMapping("/sys")
@Api(tags = "角色管理Api", description= "角色的 增删改查")
public class RoleController extends BaseController{
    
    @Autowired
    private RoleService  roleService;

    @ApiOperation("分配权限")
    @PutMapping("/role/assignPrem")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map",value = "角色数据",required = true,dataType = "map",paramType = "body")
    })
    public Result save(@RequestBody Map<String,Object> map) {
        //1.获取被分配的角色的id
        String roleId = (String) map.get("id");
        //2.获取到权限的id列表
        List<String> permIds = (List<String>) map.get("permIds");
        //3.调用service完成权限分配
        roleService.assignPerms(roleId,permIds);
        return new Result(ResultCode.SUCCESS);
    }


    @ApiOperation("添加角色")
    @PostMapping("/role")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "role",value = "角色数据",required = true,dataType = "Role",paramType = "body")
    })
    public Result add(@RequestBody Role role) throws Exception {
        role.setCompanyId(companyId);
        roleService.save(role);
        return Result.SUCCESS();
    }

    @ApiOperation("更新角色")
    @PutMapping("/role/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "角色id",required = true,dataType = "string",paramType = "path"),
            @ApiImplicitParam(name = "role",value = "角色数据",required = true,dataType = "Role",paramType = "body")
    })
    public Result update(@PathVariable(name = "id") String id, @RequestBody Role role) throws Exception {
        roleService.update(role);
        return Result.SUCCESS();
    }

    @ApiOperation("删除角色")
    @DeleteMapping("/role/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "角色id",required = true,dataType = "string",paramType = "path")
    })
    public Result delete(@PathVariable(name = "id") String id) throws Exception {
        roleService.delete(id);
        return Result.SUCCESS();
    }

    @ApiOperation("根据ID获取角色信息")
    @GetMapping("/role/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "角色id",required = true,dataType = "string",paramType = "path")
    })
    public Result findById(@PathVariable(name = "id") String id) throws Exception {
        Role role = roleService.findById(id);
        RoleResult roleResult = new RoleResult(role);
        return new Result(ResultCode.SUCCESS,roleResult);
    }

    @ApiOperation("分页查询角色")
    @GetMapping("/role")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "第几页",required = true,dataType = "int",paramType = "param"),
            @ApiImplicitParam(name = "size",value = "每页条数",required = true,dataType = "int",paramType = "param"),
            @ApiImplicitParam(name = "role",value = "查询条件",dataType = "Role",paramType = "param"),
    })
    public Result findByPage(int page,int pagesize,Role role) throws Exception {
        Page<Role> searchPage = roleService.findByPage(companyId, page, pagesize);
        PageResult<Role> pr = new PageResult(searchPage.getTotalElements(),searchPage.getContent());
        return new Result(ResultCode.SUCCESS,pr);
    }

    @ApiOperation("查询所有角色")
    @GetMapping("/role/list")
    public Result findAll() throws Exception {
        List<Role> roleList = roleService.findAll(companyId);
        return new Result(ResultCode.SUCCESS,roleList);
    }
}
