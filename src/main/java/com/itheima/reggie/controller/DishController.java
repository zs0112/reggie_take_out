package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name){
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dtoPage = new Page<>();
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getSort);
        dishService.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list=records.stream().map((item)->{
            DishDto dishDto= new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category byId = categoryService.getById(categoryId);
            if (byId!=null){
                String name1 = byId.getName();
                dishDto.setCategoryName(name1);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
}

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto withFlavor = dishService.getWithFlavor(id);
        return R.success(withFlavor);
    }
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }
    /**
     * 套餐批量删除和单个删除
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") List<Long> ids){
        for (Long id : ids) {
            dishService.del(id);
            dishService.removeById(id);
        }
        return R.success("菜品删除成功");
    }
    /**
     * 对菜品批量或者是单个 进行停售或者是起售
     * @return
     */
    @PostMapping("/status/{status}")
//这个参数这里一定记得加注解才能获取到参数，否则这里非常容易出问题
    public R<String> status(@PathVariable("status") Integer status,@RequestParam List<Long> ids){
        //log.info("status:{}",status);
        //log.info("ids:{}",ids);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids !=null,Dish::getId,ids);
        //根据数据进行批量查询
        List<Dish> list = dishService.list(queryWrapper);

        for (Dish dish : list) {
            if (dish != null){
                dish.setStatus(status);
                dishService.updateById(dish);
            }
        }
        return R.success("售卖状态修改成功");
    }

    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        lambdaQueryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lambdaQueryWrapper);
        return R.success(list);

    }
}
