package com.project.societyManagement.service.impl;

import com.project.societyManagement.dto.User.UserDetails;
import com.project.societyManagement.entity.Menu;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.queryBuilder.menu.MenuFilter;
import com.project.societyManagement.queryBuilder.menu.MenuQueryBuilder;
import com.project.societyManagement.queryBuilder.user.UserFilter;
import com.project.societyManagement.queryBuilder.user.UserQueryBuilder;
import com.project.societyManagement.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;


@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuQueryBuilder menuQueryBuilder;


    public Menu findMenuById(Long id){

        MenuFilter menuFilter = new MenuFilter();
        menuFilter.setId(id);
        Menu menu = menuQueryBuilder.findById(menuFilter);

        return menu;

    }

    @Override
    public Page<Menu> searchUser(Pageable pageable) {
        MenuFilter menuFilter = new MenuFilter();
        return menuQueryBuilder.searchPaginated(menuFilter,pageable);
    }


}
