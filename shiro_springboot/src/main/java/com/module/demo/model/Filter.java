package com.module.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("shiro_filter")
public class Filter extends Model<Filter> {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer filterSort;

    private String filterUrl;

    private String filterPerm;

}
