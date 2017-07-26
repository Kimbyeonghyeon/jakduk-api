package com.jakduk.api.vo.board;


import com.jakduk.api.common.JakdukConst;
import com.jakduk.api.model.embedded.BoardCommentStatus;
import com.jakduk.api.model.embedded.BoardItem;
import com.jakduk.api.model.embedded.CommonWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by pyohwanjang on 2017. 3. 6..
 */

@ApiModel(description = "특정 글의 댓글")
@Getter
@Setter
public class FreePostDetailComment {

    @ApiModelProperty(example = "58bcfabce1948902af90acc4", value = "댓글 ID")
    private String id;

    @ApiModelProperty(value = "댓글 ID")
    private BoardItem boardItem;

    @ApiModelProperty(value = "댓글 상태")
    private BoardCommentStatus status;

    @ApiModelProperty(value = "글쓴이")
    private CommonWriter writer;

    @ApiModelProperty(example = "댓글입니다.", value = "댓글 내용")
    private String content;

    @ApiModelProperty(example = "5", value = "좋아요 수")
    private Integer numberOfLike;

    @ApiModelProperty(example = "5", value = "싫어요 수")
    private Integer numberOfDislike;

    @ApiModelProperty(example = "LIKE", value = "나의 감정 표현 종류")
    private JakdukConst.FEELING_TYPE myFeeling;

    @ApiModelProperty(value = "그림 목록")
    private List<BoardGallery> galleries;

    @ApiModelProperty(example = "2017-07-18T00:25:45", value = "마지막으로 수정한 날 (ISO 8601)")
    private LocalDateTime lastUpdated;

}
