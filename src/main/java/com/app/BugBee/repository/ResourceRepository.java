package com.app.BugBee.repository;

import com.app.BugBee.entity.Resource;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ResourceRepository extends R2dbcRepository<Resource, Long> {
    @Modifying
    @Query("INSERT INTO bugbee.resources(post_pid, nsfw_flag, file_format, secret_key, iv) VALUES (:#{#resource.postId}, :#{#resource.nsfwFlag}, :#{#resource.fileFormat}, :#{#resource.secretKey}, :#{#resource.iv}) ON CONFLICT (post_pid) DO UPDATE SET nsfw_flag=:#{#resource.nsfwFlag}, file_format=:#{#resource.fileFormat}, secret_key=:#{#resource.secretKey}, iv=:#{#resource.iv}")
    Mono<Long> save(final Resource resource);


}
