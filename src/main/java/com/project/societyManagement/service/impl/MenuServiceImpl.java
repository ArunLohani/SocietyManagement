package com.project.societyManagement.service.impl;


import com.project.societyManagement.entity.Menu;
import com.project.societyManagement.queryBuilder.menu.MenuFilter;
import com.project.societyManagement.queryBuilder.menu.MenuQueryBuilder;
import com.project.societyManagement.repository.MenuRepo;
import com.project.societyManagement.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuQueryBuilder menuQueryBuilder;
    @Autowired
    private MenuRepo menuRepo;

    public Menu findMenuById(Long id){
        MenuFilter menuFilter = new MenuFilter();
        menuFilter.setId(id);
        Menu menu = menuQueryBuilder.findById(menuFilter);
        return menu;
    }

    public Menu findMenuByName(String menuName){
        MenuFilter menuFilter = new MenuFilter();
        menuFilter.setName(menuName);
        Menu menu = menuQueryBuilder.findById(menuFilter);
        return menu;
    }

    @Override
    public List<Menu> searchMenu() {
        MenuFilter menuFilter = new MenuFilter();
        return menuQueryBuilder.search(menuFilter);
    }

    @Override
    public Page<Menu> searchMenuPaginated(Pageable pageable) {
        MenuFilter menuFilter = new MenuFilter();
        return menuQueryBuilder.searchPaginated(menuFilter,pageable);
    }

    @Override
    public Menu createMenu(Menu menu){
        return menuRepo.save(menu);
    }

}
