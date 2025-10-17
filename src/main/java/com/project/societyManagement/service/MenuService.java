package com.project.societyManagement.service;

import com.project.societyManagement.entity.Menu;
import com.project.societyManagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MenuService {

    public Menu findMenuById(Long id);

    public Page<Menu> searchUser(Pageable pageable);

}
