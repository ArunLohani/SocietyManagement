package com.project.societyManagement.graphql;

import com.project.societyManagement.annotations.RequiresPermission;
import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.Notice.NoticeCreationRequest;
import com.project.societyManagement.entity.Notice;
import com.project.societyManagement.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class NoticeGraphQLController {

    private final NoticeService noticeService;

    @QueryMapping
    public Notice getNoticeById(@Argument Long id){
        return noticeService.getNoticeById(id);
    }

    @MutationMapping
    public Notice createNotice(
            @Argument NoticeCreationRequest input
    ) {
        return noticeService.createNotice(input);

    }

}
