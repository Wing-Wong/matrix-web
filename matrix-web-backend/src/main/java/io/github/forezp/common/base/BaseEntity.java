package io.github.forezp.common.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class BaseEntity {


    /*
     * java中得long能表示的范围比js中number大,也就意味着部分数值在js中存不下(变成不准确的值)
     * @JsonSerialize 用在属性上,让系统序列化时，保留相关精度
     */
    @JsonSerialize(using= ToStringSerializer.class)
    /*
     * //@TableId 主键注解;type:指定主键类型
     * ID_WORKER:分布式全局唯一 ID 长整型类型(please use ASSIGN_ID)
     */
    @TableId(value = "id", type = IdType.ID_WORKER)
    protected Long id;

    /*
     * 创建时间
     * @TableField:字段注解;fill:字段自动填充策略;
     * INSERT:插入时填充字段;INSERT_UPDATE:插入更新时填充字段
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    protected Date createTime;
    /**
     * 创建人id
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    protected String createBy;
    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    protected Date updateTime;
    /**
     * 更新人
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    protected String updateBy;

}
