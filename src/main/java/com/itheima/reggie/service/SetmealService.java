package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {
    void updateWithFlavor(SetmealDto setmealDto);

    void saveWithdish(SetmealDto setmealDto);

    SetmealDto getWithFlavor(Long id);

    public void del(Long id);
}
