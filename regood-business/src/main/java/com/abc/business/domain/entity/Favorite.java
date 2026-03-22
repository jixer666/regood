package com.abc.business.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@TableName("tb_favorite")
@AllArgsConstructor
@NoArgsConstructor
public class Favorite implements Serializable {

    @TableId
    private Long favoriteId;

    private Long userId;

    private Long productId;

    private Date createTime;
}
