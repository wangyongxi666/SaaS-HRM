package com.ihrm.system.service;

import com.ihrm.common.service.BaseService;
import com.ihrm.common.util.IdWorker;
import com.ihrm.common.util.PermissionConstants;
import com.ihrm.domain.system.Permission;
import com.ihrm.domain.system.Role;
import com.ihrm.system.dao.PermissionDao;
import com.ihrm.system.dao.RoleResitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * 角色操作业务逻辑层
 */
@Service
public class RoleService extends BaseService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RoleResitory roleResitory;

    @Autowired
    private PermissionDao permissionDao;

    /**
     * 分配权限
     */
    public void assignPerms(String roleId,List<String> permIds) {
        //1.获取分配的角色对象
        Role role = roleResitory.findById(roleId).get();
        //2.构造角色的权限集合
        Set<Permission> perms = new HashSet<>();
        for (String permId : permIds) {
            Permission permission = permissionDao.findById(permId).get();
            //需要根据父id和类型查询API权限列表
            List<Permission> apiList = permissionDao.findByTypeAndPid(PermissionConstants.PY_API, permission.getId());
            perms.addAll(apiList);//自定赋予API权限
            perms.add(permission);//当前菜单或按钮的权限
        }
        System.out.println(perms.size());
        //3.设置角色和权限的关系
        role.setPermission(perms);
        //4.更新角色
        roleResitory.save(role);
    }

    /**
     * 添加角色
     */
    public void save(Role role) {
        //填充其他参数
        role.setId(idWorker.nextId() + "");
        roleResitory.save(role);
    }

    /**
     * 更新角色
     */
    public void update(Role role) {
        Role targer = roleResitory.getOne(role.getId());
        targer.setDescription(role.getDescription());
        targer.setName(role.getName());
        roleResitory.save(targer);
    }

    /**
     * 根据ID查询角色
     */
    public Role findById(String id) {
        return roleResitory.findById(id).get();
    }

    public List<Role> findAll(String companyId) {
        return roleResitory.findAll(getSpec(companyId));
    }

    /**
     * 删除角色
     */
    public void delete(String id) {
        roleResitory.deleteById(id);
    }

    public Page<Role> findByPage(String companyId, int page, int size) {
        return roleResitory.findAll(getSpec(companyId), PageRequest.of(page-1, size));
    }



}
