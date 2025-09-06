package com.sky.service;

import com.sky.vo.BusinessDataVO;

public interface WorkspaceService {

    /**
     * 查询今日运行数据
     * @return
     */
    BusinessDataVO businessData();
}
