package com.jakduk.api.restcontroller.board.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author pyohwan
 * 16. 3. 13 오후 11:05
 */

@Getter
@ApiModel(value = "댓글 달기 폼")
public class BoardCommentForm {

    @ApiModelProperty(value = "글 seq")
    @Min(value = 1)
    @NotNull
    private Integer seq;

    @ApiModelProperty(value = "댓글 내용")
    @Size(min = 3, max=800)
    private String content;
}